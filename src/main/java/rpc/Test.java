package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


import notify.SendEmail;

/**
 * Servlet implementation class Test
 */
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		//JSONObject obj = new JSONObject();
		//obj.put("username", "abcd2");
		//RpcHelper.writeJsonObject(response, obj);
		
		
		
		String to = "bruceshenqqeq@gmail.com";
		String subject = "test mail";
		String text = "a text mail without html";
		
		
		//SendEmail.sendtext(to, subject, text);
		//request.getParameter("");
		String user_id = "a";
		String item_id = "b";
		
		
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(SendEmail.class)
	                .withIdentity(item_id, user_id).build();
	        
	        jobDetail.getJobDataMap().put("to", to);
	        jobDetail.getJobDataMap().put("subject", subject);
	        jobDetail.getJobDataMap().put("text", text);
			
	        Date startDate = new Date();
	        startDate.setTime((long) (startDate.getTime() + (0.5*60*1000)));
	        
	        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(item_id, user_id)
	                .startAt(startDate)
	        		.build();

	        scheduler.scheduleJob(jobDetail, trigger);
	        System.out.println("--------scheduler start ! ------------");
	        scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
