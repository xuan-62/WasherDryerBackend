package rpc;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("user_id");
		String phonenumber = input.getString("phone_number");
		String password = input.getString("password");
		try (MySQLConnection connection = new MySQLConnection()) {
			JSONObject obj = new JSONObject();
			if (connection.addUser(userId, phonenumber, password)) {
				obj.put("status", "OK");
			} else {
				obj.put("status", "User Already Exists");
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
		}
	}
}
