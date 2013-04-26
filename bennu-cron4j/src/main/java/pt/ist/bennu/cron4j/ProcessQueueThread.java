package pt.ist.bennu.cron4j;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.Transaction;

public class ProcessQueueThread extends Thread {

    private CronTask task = null;

    @Override
    public void run() {
        Transaction.withTransaction(false, new TransactionalCommand() {

            @Override
            public void doIt() {
                setTask(Queue.pop());
            }
        });
        runTask();
    }

    private void runTask() {
        if (task != null) {
            task.run();
        }
        setTask(null);
    }

    public void setTask(String taskClassName) {
        try {
            if (taskClassName != null) {
                Class<? extends CronTask> taskClass = (Class<? extends CronTask>) Class.forName(taskClassName);
                task = taskClass.newInstance();
            } else {
                task = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
