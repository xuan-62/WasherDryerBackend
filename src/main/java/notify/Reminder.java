package notify;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Reminder {
	public static void setReminder(String to, String item_id, String user_id, double time) {

		String subject = "Reminder from Washer & Dryer Management App";
		String text = "Your laundry in machine id: " + item_id + " will complete in 5 minutes.";

		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(SendEmail.class).withIdentity(item_id, user_id).build();

			jobDetail.getJobDataMap().put("to", to);
			jobDetail.getJobDataMap().put("subject", subject);
			jobDetail.getJobDataMap().put("text", text);

			Date startDate = new Date();
			startDate.setTime((long) (startDate.getTime() + (time * 60 * 1000)));

			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(item_id, user_id).startAt(startDate).build();

			scheduler.scheduleJob(jobDetail, trigger);
			System.out.println("--------scheduler start ! ------------");
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}
}
