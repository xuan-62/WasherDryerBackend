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

public class AutoChangeStatus {
	public static void autoChangeStatus(String user_id, String item_id, String new_status, double time) {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(NewStatus.class).withIdentity(item_id, new_status).build();

			jobDetail.getJobDataMap().put("user_id", user_id);
			jobDetail.getJobDataMap().put("item_id", item_id);
			jobDetail.getJobDataMap().put("new_status", new_status);

			Date startDate = new Date();
			startDate.setTime((long) (startDate.getTime() + (time * 60 * 1000)));
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(item_id, new_status).startAt(startDate).build();
			scheduler.scheduleJob(jobDetail, trigger);
			System.out.println("--------scheduler start ! ------------");
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
