package notify;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import db.MySQLConnection;

public class NewStatus implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MySQLConnection connection = new MySQLConnection();
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String user_id = dataMap.getString("user_id");
		String item_id = dataMap.getString("item_id");
		String new_status = dataMap.getString("new_status");
		String current_status = connection.getCondition(item_id);
		if (new_status.equals("available")) // set reversed item back to empty
		{
			if (current_status.equals("reserve")) {
				connection.updateCondition(item_id, new_status);
				connection.removeReservation(user_id, item_id);
				connection.removeUserfromItem(item_id);
			} else {
				return;
			}

		} else if (new_status.equals("done")) // set working item to done
		{
			connection.updateCondition(item_id, new_status);
		}

	}

}
