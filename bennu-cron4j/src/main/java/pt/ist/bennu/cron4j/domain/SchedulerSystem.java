package pt.ist.bennu.cron4j.domain;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.Bennu;

public class SchedulerSystem extends SchedulerSystem_Base {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerSystem.class);
    private static final Integer LEASE_TIME_MIN = 5;

    private SchedulerSystem() {
        super();
    }

    public static SchedulerSystem getInstance() {
        if (!Bennu.getInstance().hasSchedulerSystem()) {
            Bennu.getInstance().setSchedulerSystem(new SchedulerSystem());
        }
        return Bennu.getInstance().getSchedulerSystem();
    }

    public Boolean shouldRun() {
        if (isLeaseExpired()) {
            lease();
            return true;
        }
        return false;
    }

    public void lease() {
        setLease(new DateTime());
    }

    private boolean isLeaseExpired() {
        final DateTime lease = getLease();
        if (lease == null) {
            return true;
        }
        return lease.plusMinutes(LEASE_TIME_MIN).isBeforeNow();
    }
}
