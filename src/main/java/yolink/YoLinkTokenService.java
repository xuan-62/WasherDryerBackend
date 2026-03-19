package yolink;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;

public class YoLinkTokenService {
	private static final Logger logger = LoggerFactory.getLogger(YoLinkTokenService.class);
	private static final String TOKEN_URL = "https://api.yosmart.com/open/yolink/token";

	public static String fetchAccessToken() {
		String clientId = AppConfig.get("YOLINK_CLIENT_ID");
		String secretKey = AppConfig.get("YOLINK_SECRET_KEY");

		String body = "grant_type=client_credentials"
				+ "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
				+ "&client_secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8);

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(TOKEN_URL))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JSONObject json = new JSONObject(response.body());
			logger.info("YoLink access token fetched successfully");
			return json.getString("access_token");
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch YoLink access token", e);
		}
	}
}
