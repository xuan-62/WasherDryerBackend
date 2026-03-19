package db;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import messaging.MachineStatusConsumer;
import messaging.RabbitMQPublisher;
import notify.HeartbeatMonitor;
import yolink.YoLinkSubscriber;

public class AppContextListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

	private RabbitMQPublisher publisher;
	private MachineStatusConsumer consumer;
	private YoLinkSubscriber yoLinkSubscriber;
	private ScheduledExecutorService heartbeatExecutor;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		runMigrations();
		startSensorMode();
	}

	private void runMigrations() {
		String host = AppConfig.get("DB_HOST");
		String port = AppConfig.get("DB_PORT", "3306");
		String dbName = AppConfig.get("DB_NAME", "washerproject");
		String user = AppConfig.get("DB_USER");
		String password = AppConfig.get("DB_PASS");

		logger.info("Running Flyway migrations on {}:{}/{}", host, port, dbName);
		Flyway.configure()
				.dataSource("jdbc:mysql://" + host + ":" + port + "/" + dbName + "?serverTimezone=UTC", user, password)
				.load()
				.migrate();
		logger.info("Flyway migrations complete");
	}

	private void startSensorMode() {
		if (AppConfig.get("YOLINK_CLIENT_ID") == null) {
			logger.info("Sensor mode disabled — YOLINK_CLIENT_ID not configured");
			return;
		}
		try {
			publisher = new RabbitMQPublisher();
			consumer = new MachineStatusConsumer();
			yoLinkSubscriber = new YoLinkSubscriber(publisher);

			int checkInterval = Integer.parseInt(AppConfig.get("HEARTBEAT_CHECK_INTERVAL_MINUTES", "5"));
			int threshold = Integer.parseInt(AppConfig.get("HEARTBEAT_THRESHOLD_MINUTES", "10"));
			heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
				Thread t = new Thread(r, "heartbeat-monitor");
				t.setDaemon(true);
				return t;
			});
			heartbeatExecutor.scheduleAtFixedRate(new HeartbeatMonitor(), checkInterval, checkInterval, TimeUnit.MINUTES);

			logger.info("Sensor mode active — heartbeat check every {} min, alert threshold {} min",
					checkInterval, threshold);
		} catch (Exception e) {
			logger.warn("Sensor mode failed to start — running in manual mode only: {}", e.getMessage());
			closeQuietly(yoLinkSubscriber);
			closeQuietly(consumer);
			closeQuietly(publisher);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (heartbeatExecutor != null) heartbeatExecutor.shutdown();
		closeQuietly(yoLinkSubscriber);
		closeQuietly(consumer);
		closeQuietly(publisher);
	}

	private void closeQuietly(AutoCloseable resource) {
		if (resource == null) return;
		try {
			resource.close();
		} catch (Exception e) {
			logger.warn("Error closing {}: {}", resource.getClass().getSimpleName(), e.getMessage());
		}
	}
}
