package notify;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import db.MySQLConnection;

public class HeartbeatMonitor implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatMonitor.class);

	@Override
	public void run() {
		int threshold = Integer.parseInt(AppConfig.get("HEARTBEAT_THRESHOLD_MINUTES", "10"));
		try (MySQLConnection db = new MySQLConnection()) {
			List<String[]> stale = db.getStaleDevices(threshold);
			for (String[] device : stale) {
				String itemId   = device[0];
				String deviceId = device[1];
				String condition = device[2];
				logger.warn("Device {} (machine {}) missed heartbeat — condition: {}", deviceId, itemId, condition);
				if (condition.equals("start")) {
					SendEmail.sendtext(
							AppConfig.get("MANAGER_EMAIL"),
							"Sensor offline — machine " + itemId,
							"Machine " + itemId + " sensor (device " + deviceId + ") has not reported in "
									+ threshold + " minutes. Machine may be stuck in 'start'. Please investigate.");
				}
			}
		} catch (Exception e) {
			logger.error("Heartbeat check failed", e);
		}
	}
}
