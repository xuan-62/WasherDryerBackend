package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnection {
	private Connection conn;

	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//TODO
	public boolean verifyLogin() {
		return false;
	}
	
	public boolean addUser() {
		return false;
	}
	//get all machine
	//change machine status
	

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
