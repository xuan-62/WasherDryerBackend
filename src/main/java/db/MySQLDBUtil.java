package db;

public class MySQLDBUtil {
	private static final String INSTANCE = System.getenv("DB_HOST");
	private static final String PORT_NUM = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
	public static final String DB_NAME   = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "washerproject";
	private static final String USERNAME = System.getenv("DB_USER");
	private static final String PASSWORD = System.getenv("DB_PASS");
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";

}
