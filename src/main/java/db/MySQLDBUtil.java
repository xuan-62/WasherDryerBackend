package db;

public class MySQLDBUtil {
	private static final String INSTANCE = "YOUR_INSTANCE_ADDRESS";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "YOUR_DB_NAME";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "YOUR_PASSWORD";
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";

}
