package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;
import rpc.RpcHelper;

/**
 * Servlet implementation class Register
 */
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("user_id");
		//String firstname = input.getString("first_name");
		//String lastname = input.getString("last_name");
		String phonenumber = input.getString("phone_number");
		String password = input.getString("password");

		MySQLConnection connection = new MySQLConnection();
		JSONObject obj = new JSONObject();
		if (connection.addUser(userId, password, phonenumber)) {
			obj.put("status", "OK");
		} else {
			obj.put("status", "User Already Exists");
		}
		connection.close();
		RpcHelper.writeJsonObject(response, obj);
	}
}
