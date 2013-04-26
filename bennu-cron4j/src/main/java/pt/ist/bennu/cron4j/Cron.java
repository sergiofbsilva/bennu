package pt.ist.bennu.cron4j;

import it.sauronsoftware.cron4j.Scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import jvstm.TransactionalCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.cron4j.domain.SchedulerSystem;
import pt.ist.bennu.cron4j.domain.TaskSchedule;
import pt.ist.bennu.service.Service;
import pt.ist.fenixframework.pstm.Transaction;

public class Cron {

	private static final Logger LOG = LoggerFactory.getLogger(Cron.class);

	private static final Set<String> taskClassNames = new HashSet<String>();

	private static Scheduler scheduler;

	@Service
	public static void init() {
		final String lockVariable = Cron.class.getName() + ConfigurationManager.getFenixFrameworkConfig().getDbName();
		Statement statement = null;
		ResultSet resultSet = null;
		final Connection connection = Transaction.getCurrentJdbcConnection();
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT GET_LOCK('" + lockVariable + "', 10)");
			if (resultSet.next() && (resultSet.getInt(1) == 1)) {
				LOG.info("Initializing Cron with lock {}", lockVariable);
				bootstrap();
			} else {
				LOG.warn("Cron already initialized with lock {}", lockVariable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void bootstrap() {
		if (scheduler == null) {
			scheduler = new Scheduler();
			scheduler.setDaemon(true);
		}
		cleanNonExistingSchedules();
		initSchedules();
		scheduler.start();
	}

	@Service
	private static void cleanNonExistingSchedules() {
		for (TaskSchedule schedule : SchedulerSystem.getInstance().getTaskSchedule()) {
			if (!taskClassNames.contains(schedule.getTaskClassName())) {
				LOG.warn("Class {} is no longer available. schedule {} - {} - {} deleted. ", schedule.getTaskClassName(),
						schedule.getExternalId(), schedule.getTaskClassName(), schedule.getSchedule());
				schedule.delete();
			}
		}

	}

	@Service
	private static void initSchedules() {
		for (TaskSchedule schedule : SchedulerSystem.getInstance().getTaskSchedule()) {
			LOG.info("Schedule Init [{}] {}", schedule.getSchedule(), schedule.getTaskClassName());
			schedule(schedule);
		}
	}

	public static void schedule(TaskSchedule schedule) {
		schedule.setTaskId(scheduler.schedule(schedule.getSchedule(), schedule.getTask()));
	}

	public static void unschedule(TaskSchedule schedule) {
		scheduler.deschedule(schedule.getTaskId());
	}

	public static final void addTask(String className) {
		LOG.info("Register Task : {}", className);
		taskClassNames.add(className);
	}

}
