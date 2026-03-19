package notify;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class AutoChangeStatus {
	private static final Logger logger = LoggerFactory.getLogger(AutoChangeStatus.class);

	public static void autoChangeStatus(String user_id, String item_id, String new_status, double time) {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(NewStatus.class).withIdentity(item_id, new_status).build();

			jobDetail.getJobDataMap().put("user_id", user_id);
			jobDetail.getJobDataMap().put("item_id", item_id);
			jobDetail.getJobDataMap().put("new_status", new_status);

			Date startDate = Date.from(Instant.now().plusMillis((long) (time * 60 * 1000)));
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(item_id, new_status).startAt(startDate).build();
			scheduler.scheduleJob(jobDetail, trigger);
			logger.info("Status change scheduler started for item {} -> {}", item_id, new_status);
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to schedule status change", e);
		}
	}
}
