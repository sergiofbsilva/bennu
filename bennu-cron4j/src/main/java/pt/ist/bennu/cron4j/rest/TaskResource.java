package pt.ist.bennu.cron4j.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.cron4j.domain.SchedulerSystem;
import pt.ist.bennu.cron4j.domain.TaskSchedule;
import pt.ist.bennu.service.Service;

@Path("/tasks")
public class TaskResource extends BennuRestResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get() {
		return view(SchedulerSystem.getInstance().getTaskSchedule(), "tasks");
	}

	@GET
	@Path("{oid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("oid") String taskOid) {
		return view(readDomainObject(taskOid));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addSchedule(@FormParam("model") String configJson) {
		return view(create(configJson, TaskSchedule.class));
	}

	@DELETE
	@Path("{oid}")
	public void delete(@PathParam("oid") String taskOid) {
		clear((TaskSchedule) readDomainObject(taskOid));
	}

	@Service
	public void clear(TaskSchedule task) {
		task.delete();
	}

}
