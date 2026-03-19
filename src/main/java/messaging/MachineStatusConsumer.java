package messaging;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import config.AppConfig;
import db.MySQLConnection;

public class MachineStatusConsumer implements AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(MachineStatusConsumer.class);

	private final Connection connection;
	private final Channel channel;

	public MachineStatusConsumer() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(AppConfig.get("RABBITMQ_HOST", "localhost"));
		factory.setPort(Integer.parseInt(AppConfig.get("RABBITMQ_PORT", "5672")));
		factory.setUsername(AppConfig.get("RABBITMQ_USER", "guest"));
		factory.setPassword(AppConfig.get("RABBITMQ_PASS", "guest"));

		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.queueDeclare(RabbitMQPublisher.QUEUE_NAME, true, false, false, null);
		channel.basicQos(1);

		DeliverCallback callback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody());
			try {
				processEvent(message);
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			} catch (Exception e) {
				logger.error("Failed to process event: {}", message, e);
				channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
			}
		};

		channel.basicConsume(RabbitMQPublisher.QUEUE_NAME, false, callback, tag -> {});
		logger.info("RabbitMQ consumer started on queue: {}", RabbitMQPublisher.QUEUE_NAME);
	}

	private void processEvent(String message) {
		JSONObject event = new JSONObject(message);
		String deviceId = event.getString("deviceId");
		String state = event.getString("state"); // "vibrating" or "stopped"

		try (MySQLConnection db = new MySQLConnection()) {
			String itemId = db.getItemIdByDeviceId(deviceId);
			if (itemId == null) {
				logger.warn("No machine mapped to device {}", deviceId);
				return;
			}

			String currentStatus = db.getCondition(itemId);

			if (state.equals("heartbeat")) {
				db.updateDeviceHeartbeat(deviceId);
				logger.debug("Heartbeat updated for device {}", deviceId);
				return;
			} else if (state.equals("vibrating") && !currentStatus.equals("start")) {
				db.updateCondition(itemId, "start");
				String reservedUser = db.getReservedUserForItem(itemId);
				if (reservedUser != null) {
					db.addUsertoMachine(itemId, reservedUser);
				}
				logger.info("Machine {} → start via sensor (device {})", itemId, deviceId);

			} else if (state.equals("stopped") && currentStatus.equals("start")) {
				db.updateCondition(itemId, "done");
				logger.info("Machine {} → done via sensor (device {})", itemId, deviceId);
			}
		}
	}

	@Override
	public void close() {
		try {
			if (channel != null && channel.isOpen()) channel.close();
			if (connection != null && connection.isOpen()) connection.close();
			logger.info("RabbitMQ consumer closed");
		} catch (Exception e) {
			logger.warn("Error closing RabbitMQ consumer", e);
		}
	}
}
