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
import notify.SendEmail;

/**
 * Servlet implementation class RemindUser
 */
public class RemindUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemindUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String item_id = input.getString("item_id");
		String to_user_id = input.getString("user_id");
		String from_user_id = session.getAttribute("user_id").toString();
		MySQLConnection connection = new MySQLConnection();
		String Email = connection.getEmail(to_user_id);
		String subject = "Reminder from user: " + from_user_id;
		String text = "Your laundry awaits (machine ID: "+ item_id +")!";
		SendEmail.sendtext(Email, subject, text);	
	}

}
