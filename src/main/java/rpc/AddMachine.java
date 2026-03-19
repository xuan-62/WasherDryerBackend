package rpc;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Machine;
import jakarta.servlet.http.HttpSession;

public class AddMachine extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || !RpcHelper.isAdmin(session)) {
			RpcHelper.writeError(response, 403, "Admin access required");
			return;
		}
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		Machine item = RpcHelper.buildMachine(input);
		try (MySQLConnection connection = new MySQLConnection()) {
			connection.addMachine(item);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
			return;
		}
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "success"));
	}
}
