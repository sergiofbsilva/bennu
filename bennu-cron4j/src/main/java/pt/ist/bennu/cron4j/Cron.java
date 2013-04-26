package pt.ist.bennu.cron4j;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jvstm.TransactionalCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        new Timer(true).scheduleAtFixedRate(new TimerTask() {

            Boolean shouldRun = false;

            @Override
            public void run() {
                Transaction.withTransaction(false, new TransactionalCommand() {

                    @Override
                    public void doIt() {
                        setShouldRun(SchedulerSystem.getInstance().shouldRun());
                    }
                });

                if (shouldRun) {
                    bootstrap();
                }
            }

            public void setShouldRun(Boolean shouldRun) {
                this.shouldRun = shouldRun;
            }

        }, 0, 3 * 60 * 1000);

    }

    private static void bootstrap() {
        if (scheduler == null) {
            scheduler = new Scheduler();
            scheduler.setDaemon(true);
        }
        if (!scheduler.isStarted()) {
            cleanNonExistingSchedules();
            initSchedules();
            scheduler.start();
        }
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

        scheduler.schedule("* * * * *", new Runnable() {

            @Override
            public void run() {
                Transaction.withTransaction(false, new TransactionalCommand() {

                    @Override
                    public void doIt() {
                        SchedulerSystem.getInstance().lease();
                    }
                });
            }
        });
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

    public static void destroy() {

    }

}
