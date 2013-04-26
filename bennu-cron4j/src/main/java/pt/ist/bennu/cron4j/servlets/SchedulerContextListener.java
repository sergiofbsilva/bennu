package pt.ist.bennu.cron4j.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.bennu.cron4j.Cron;

@WebListener
public class SchedulerContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Cron.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}
