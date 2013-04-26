package pt.ist.bennu.cron4j.example;

import org.joda.time.DateTime;

import pt.ist.bennu.cron4j.CronTask;
import pt.ist.bennu.cron4j.annotation.Task;

@Task
public class HourTask extends CronTask {

	@Override
	public void runTask() {
		System.out.println("Esta corre todas as horas : " + new DateTime());
	}

	@Override
	public String getLocalizedName() {
		return "Task que corre a todas as horas";
	}

}
