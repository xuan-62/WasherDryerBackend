package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class ChangeMachineStatus
 */
public class ChangeMachineStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeMachineStatus() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String newStatus = request
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		JSONObject obj = new JSONObject();
		String newStatus = input.getString("status");
		String item_id = input.getString("item_id");
		MySQLConnection connection = new MySQLConnection();
		String user_id = session.getAttribute("user_id").toString();
		//String user_id = "1111";
		if(newStatus.equals("reserve")) {
			connection.updateCondition(item_id, newStatus);
			connection.addUsertoItem(item_id, user_id);
			connection.setReservation(user_id, item_id);
			obj.put("status", "OK");
		}else if(newStatus.equals("available")) { 
			connection.updateCondition(item_id, newStatus);
			connection.removeReservation(user_id, item_id);
			obj.put("status", "OK");
		}		
		RpcHelper.writeJsonObject(response, obj);
	}

}