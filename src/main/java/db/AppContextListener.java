package db;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String host = AppConfig.get("DB_HOST");
		String port = AppConfig.get("DB_PORT", "3306");
		String dbName = AppConfig.get("DB_NAME", "washerproject");
		String user = AppConfig.get("DB_USER");
		String password = AppConfig.get("DB_PASS");

		logger.info("Running Flyway migrations on {}:{}/{}", host, port, dbName);
		Flyway flyway = Flyway.configure()
				.dataSource(
						"jdbc:mysql://" + host + ":" + port + "/" + dbName + "?serverTimezone=UTC",
						user, password)
				.load();
		flyway.migrate();
		logger.info("Flyway migrations complete");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
