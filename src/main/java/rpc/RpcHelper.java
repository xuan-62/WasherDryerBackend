package rpc;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import config.AppConfig;
import entity.Machine;

public class RpcHelper {
	private static final String CORS_ORIGIN = AppConfig.get("CORS_ORIGIN", "http://localhost:3000");

	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
		response.setHeader("Access-Control-Allow-Origin", CORS_ORIGIN);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setContentType("application/json");
		response.getWriter().print(array);
	}

	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
		response.setHeader("Access-Control-Allow-Origin", CORS_ORIGIN);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}

	public static void writeError(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		writeJsonObject(response, new JSONObject().put("error", message));
	}

	public static Machine buildMachine(JSONObject machine) {
		return new Machine(
				machine.getString("item_id"),
				machine.getString("type"),
				machine.getString("address"),
				null,
				machine.getString("item_condition"),
				machine.getString("model"),
				machine.getString("brand"),
				null);
	}
}
