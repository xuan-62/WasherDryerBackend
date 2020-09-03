package db;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset the database.
	public static void main(String[] args) {
		try {
			// Step 1 Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);

			if (conn == null) {
				return;
			}
			
			// Step 2 Drop tables in case they exist.
			Statement statement = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS reservation";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS item";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS background";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS user";
			statement.executeUpdate(sql);
			

			sql = "CREATE TABLE user ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "email VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE background ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "about_me VARCHAR(255),"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "email VARCHAR(255),"
					+ "PRIMARY KEY (user_id),"
					+ "FOREIGN KEY (user_id) REFERENCES user(user_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE item ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "type VARCHAR(255),"
					+ "address VARCHAR(255),"
					+ "user_id VARCHAR(255),"
					+ "item_condition VARCHAR(255),"
					+ "model VARCHAR(255),"
					+ "brand VARCHAR(255),"
					+ "PRIMARY KEY (item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES user(user_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE reservation ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "start_time TIMESTAMP NOT NULL,"
					+ "end_time TIMESTAMP NOT NULL,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES user(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES item(item_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			// Step 4: insert fake user 1111/3229c1097c00d497a0fd282d586be050
			sql = "INSERT INTO user VALUES('1112', '98976544', '3229c1097c00d497a0fd282d586be050')";
			
			statement.executeUpdate(sql);
			
			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
