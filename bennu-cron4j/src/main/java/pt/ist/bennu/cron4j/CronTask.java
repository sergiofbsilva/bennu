package pt.ist.bennu.cron4j;

import jvstm.TransactionalCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.pstm.Transaction;

public abstract class CronTask implements Runnable {
	private Logger logger;

	public String getLocalizedName() {
		return this.getClass().getName();
	}

	public Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(getClass());
		}
		return logger;
	}

	public abstract void runTask();

	@Override
	public void run() {
		Transaction.withTransaction(false, new TransactionalCommand() {

			@Override
			public void doIt() {
				runTask();
			}
		});
	}

}
