package pt.ist.bennu.cron4j.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.Bennu;

public class SchedulerSystem extends SchedulerSystem_Base {

	private static final Logger LOG = LoggerFactory.getLogger(SchedulerSystem.class);

	private SchedulerSystem() {
		super();
	}

	public static SchedulerSystem getInstance() {
		if (!Bennu.getInstance().hasSchedulerSystem()) {
			Bennu.getInstance().setSchedulerSystem(new SchedulerSystem());
		}
		return Bennu.getInstance().getSchedulerSystem();
	}
}
