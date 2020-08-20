package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import notify.SendEmail;
//import org.json.JSONObject;

/**
 * Servlet implementation class SendingMessageToManager
 */
public class SendingMessageToManager extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendingMessageToManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//change the address later
		final String managerEmailAddress = "bruceshenqqeq@gmail.com";  
		
		
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
		String text = "Issue type: " + issueType + "\nmachine ID: "+ item_id +
				"\nIssue: " + issue;
		SendEmail.sendtext(managerEmailAddress, subject, text);		
	}
}
