package rpc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class RpcHelper {
	// Writes a JSONArray to http response.
		public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
			response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setContentType("application/json");
			response.getWriter().print(array);

		}

	              // Writes a JSONObject to http response.
		public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {	
			response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setContentType("application/json");
			response.getWriter().print(obj);

		}
		
		// Convert a JSON object to Item object
		public static Item AddMachine(JSONObject machine) {
			ItemBuilder builder = new ItemBuilder();
			builder.setItemId(machine.getString("item_id"));
			builder.setType(machine.getString("type"));
			builder.setAddress(machine.getString("address"));
			builder.setCondition(machine.getString("item_condition"));
			builder.setModel(machine.getString("model"));
			builder.setBrand(machine.getString("brand"));
			return builder.build();
		}
}
