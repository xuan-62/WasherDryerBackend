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
import notify.SendEmail;

public class RemindUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String item_id = input.getString("item_id");
		String to_user_id = input.getString("user_id");
		String from_user_id = session.getAttribute("user_id").toString();
		try (MySQLConnection connection = new MySQLConnection()) {
			String email = connection.getEmail(to_user_id);
			String subject = "Reminder from user: " + from_user_id;
			String text = "Your laundry awaits (machine ID: " + item_id + ")!";
			SendEmail.sendtext(email, subject, text);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
		}
	}
}
