package db;

import config.AppConfig;

public class MySQLDBUtil {
	private static final String HOST     = AppConfig.get("DB_HOST");
	private static final String PORT     = AppConfig.get("DB_PORT", "3306");
	public  static final String DB_NAME  = AppConfig.get("DB_NAME", "washerproject");
	private static final String USERNAME = AppConfig.get("DB_USER");
	private static final String PASSWORD = AppConfig.get("DB_PASS");

	public static final String URL = "jdbc:mysql://"
			+ HOST + ":" + PORT + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
}
