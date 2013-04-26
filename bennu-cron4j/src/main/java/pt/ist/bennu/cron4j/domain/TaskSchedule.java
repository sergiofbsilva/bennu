package pt.ist.bennu.cron4j.domain;

import jvstm.TransactionalCommand;
import pt.ist.bennu.cron4j.Cron;
import pt.ist.bennu.cron4j.Queue;
import pt.ist.fenixframework.pstm.Transaction;

public class TaskSchedule extends TaskSchedule_Base {

    private transient String taskId;

    private TaskSchedule(final String taskClassName) {
        super();
        setTaskClassName(taskClassName);
        setSchedulerSystem(SchedulerSystem.getInstance());
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

    public Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() {
                Transaction.withTransaction(false, new TransactionalCommand() {

                    @Override
                    public void doIt() {
                        Queue.queue(getTaskClassName());
                    }
                });
            }
        };
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
