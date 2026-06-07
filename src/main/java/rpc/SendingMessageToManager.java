package rpc;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import config.AppConfig;
import notify.SendEmail;

public class SendingMessageToManager extends HttpServlet {
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
		String issueType = input.getString("issueType");
		String issue = input.getString("issue");
		String subject = "Error report from user: " + session.getAttribute("user_id");
		String text = "Issue type: " + issueType + "\nmachine ID: " + item_id + "\nIssue: " + issue;
		String managerEmail = AppConfig.get("MANAGER_EMAIL");
		try {
			SendEmail.sendtext(managerEmail, subject, text);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
		}
	}
}
