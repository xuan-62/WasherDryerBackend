package messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import config.AppConfig;

public class RabbitMQPublisher implements AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQPublisher.class);
	static final String QUEUE_NAME = "machine-events";

	private final Connection connection;
	private final Channel channel;

	public RabbitMQPublisher() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(AppConfig.get("RABBITMQ_HOST", "localhost"));
		factory.setPort(Integer.parseInt(AppConfig.get("RABBITMQ_PORT", "5672")));
		factory.setUsername(AppConfig.get("RABBITMQ_USER", "guest"));
		factory.setPassword(AppConfig.get("RABBITMQ_PASS", "guest"));

		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false, null); // durable
		logger.info("RabbitMQ publisher connected to {}:{}", factory.getHost(), factory.getPort());
	}

	public void publish(String message) throws Exception {
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
	}

	@Override
	public void close() {
		try {
			if (channel != null && channel.isOpen()) channel.close();
			if (connection != null && connection.isOpen()) connection.close();
			logger.info("RabbitMQ publisher closed");
		} catch (Exception e) {
			logger.warn("Error closing RabbitMQ publisher", e);
		}
	}
}
