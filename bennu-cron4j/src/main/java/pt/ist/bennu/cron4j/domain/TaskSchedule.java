package pt.ist.bennu.cron4j.domain;

import pt.ist.bennu.cron4j.Cron;
import pt.ist.bennu.cron4j.CronTask;

public class TaskSchedule extends TaskSchedule_Base {

	private transient CronTask task;
	private transient String taskId;

	private TaskSchedule(final String taskClassName) {
		super();
		setTaskClassName(taskClassName);
		setSchedulerSystem(SchedulerSystem.getInstance());
		task = null;
	}

	public TaskSchedule(final String taskClassName, final String schedule) {
		this(taskClassName);
		setSchedule(schedule);
		Cron.schedule(this);
	}

	public void delete() {
		removeSchedulerSystem();
		Cron.unschedule(this);
		super.deleteDomainObject();
	}

	public CronTask getTask() {
		try {
			if (task == null) {
				Class<? extends CronTask> taskClass = (Class<? extends CronTask>) Class.forName(getTaskClassName());
				task = taskClass.newInstance();
			}
			return task;
		} catch (Exception e) {
			return null;
		}
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
