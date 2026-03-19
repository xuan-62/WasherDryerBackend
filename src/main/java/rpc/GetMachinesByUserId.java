package rpc;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;

import db.MySQLConnection;
import entity.Machine;

public class GetMachinesByUserId extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			RpcHelper.writeError(response, 403, "Invalid session");
			return;
		}
		String userId = session.getAttribute("user_id").toString();
		Set<Machine> items;
		try (MySQLConnection connection = new MySQLConnection()) {
			items = connection.getReservedMachines(userId);
		} catch (Exception e) {
			RpcHelper.writeError(response, 500, "Internal server error");
			return;
		}
		JSONArray array = new JSONArray();
		for (Machine item : items) {
			array.put(item.toJSONObject());
		}
		RpcHelper.writeJsonArray(response, array);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
