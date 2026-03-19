package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLTableCreation {
	private static final Logger logger = LoggerFactory.getLogger(MySQLTableCreation.class);

	// Run this as Java application to reset the database.
	public static void main(String[] args) {
		logger.info("Connecting to {}", MySQLDBUtil.URL);
		try (Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			 Statement statement = conn.createStatement()) {

			statement.executeUpdate("DROP TABLE IF EXISTS reservation");
			statement.executeUpdate("DROP TABLE IF EXISTS item");
			statement.executeUpdate("DROP TABLE IF EXISTS background");
			statement.executeUpdate("DROP TABLE IF EXISTS user");

			statement.executeUpdate("CREATE TABLE user ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "email VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (user_id)"
					+ ")");

			statement.executeUpdate("CREATE TABLE background ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "about_me VARCHAR(255),"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "email VARCHAR(255),"
					+ "PRIMARY KEY (user_id),"
					+ "FOREIGN KEY (user_id) REFERENCES user(user_id)"
					+ ")");

			statement.executeUpdate("CREATE TABLE item ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "type VARCHAR(255),"
					+ "address VARCHAR(255),"
					+ "user_id VARCHAR(255),"
					+ "item_condition VARCHAR(255),"
					+ "model VARCHAR(255),"
					+ "brand VARCHAR(255),"
					+ "PRIMARY KEY (item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES user(user_id)"
					+ ")");

			statement.executeUpdate("CREATE TABLE reservation ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "start_time TIMESTAMP NOT NULL,"
					+ "end_time TIMESTAMP NOT NULL,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES user(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES item(item_id)"
					+ ")");

			statement.executeUpdate("INSERT INTO user VALUES('1112', '98976544', '3229c1097c00d497a0fd282d586be050')");

			logger.info("Import done successfully");
		} catch (SQLException e) {
			throw new RuntimeException("Database setup failed", e);
		}
	}
}
