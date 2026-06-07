package rpc;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			RpcHelper.writeJsonObject(response, new JSONObject().put("status", "Invalid Session"));
			return;
		}
		String userId = session.getAttribute("user_id").toString();
		try (MySQLConnection connection = new MySQLConnection()) {
			JSONObject obj = new JSONObject()
				.put("status", "OK")
				.put("user_id", userId)
				.put("phone_number", connection.getFullname(userId));
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("user_id");
		String password = input.getString("password");
		try (MySQLConnection connection = new MySQLConnection()) {
			JSONObject obj = new JSONObject();
			if (connection.verifyLogin(userId, password)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				session.setMaxInactiveInterval(600);
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} else {
				obj.put("status", "User Doesn't Exist");
				response.setStatus(401);
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
		}
	}
}
