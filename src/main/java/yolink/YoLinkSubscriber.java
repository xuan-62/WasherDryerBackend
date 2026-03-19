package yolink;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import messaging.RabbitMQPublisher;

public class YoLinkSubscriber implements MqttCallback, AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(YoLinkSubscriber.class);
	private static final String BROKER_URL = "tcp://api.yosmart.com:8003";

	private final MqttClient mqttClient;
	private final RabbitMQPublisher publisher;

	public YoLinkSubscriber(RabbitMQPublisher publisher) throws MqttException {
		this.publisher = publisher;
		String homeId = AppConfig.get("YOLINK_HOME_ID");

		String accessToken = YoLinkTokenService.fetchAccessToken();

		mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());
		mqttClient.setCallback(this);

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(accessToken);
		options.setPassword(accessToken.toCharArray());
		options.setAutomaticReconnect(true);
		options.setCleanSession(false);

		mqttClient.connect(options);
		mqttClient.subscribe("yl-home/" + homeId + "/+/report", 1);
		logger.info("YoLink subscriber connected, listening on home {}", homeId);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) {
		try {
			JSONObject json = new JSONObject(new String(message.getPayload()));
			String deviceId = json.optString("deviceId", null);
			if (deviceId == null) return;

			// every message from a device counts as a heartbeat
			publisher.publish(new JSONObject().put("deviceId", deviceId).put("state", "heartbeat").toString());

			// additionally forward vibration state changes
			String event = json.optString("event", "");
			if (!event.startsWith("VibrationSensor")) return;

			String state = json.getJSONObject("data").getString("state");
			String mqState = state.equals("alert") ? "vibrating" : "stopped";

			publisher.publish(new JSONObject().put("deviceId", deviceId).put("state", mqState).toString());
			logger.debug("YoLink event forwarded: device {} → {}", deviceId, mqState);
		} catch (Exception e) {
			logger.error("Failed to process YoLink message on topic {}", topic, e);
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.warn("YoLink MQTT connection lost: {}", cause.getMessage());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// not used — we only subscribe, not publish via MQTT
	}

	@Override
	public void close() {
		try {
			if (mqttClient != null && mqttClient.isConnected()) {
				mqttClient.disconnect();
				mqttClient.close();
				logger.info("YoLink subscriber disconnected");
			}
		} catch (MqttException e) {
			logger.warn("Error closing YoLink subscriber", e);
		}
	}
}
