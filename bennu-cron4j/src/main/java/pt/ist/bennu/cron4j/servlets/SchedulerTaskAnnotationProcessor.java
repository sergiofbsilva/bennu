package pt.ist.bennu.cron4j.servlets;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import pt.ist.bennu.cron4j.Cron;
import pt.ist.bennu.cron4j.annotation.Task;

@HandlesTypes({ Task.class })
public class SchedulerTaskAnnotationProcessor implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
		if (classes != null) {
			for (Class<?> type : classes) {
				final Task annon = type.getAnnotation(Task.class);
				if (annon != null) {
					Cron.addTask(type.getName());
				}
			}
		}
	}
}
