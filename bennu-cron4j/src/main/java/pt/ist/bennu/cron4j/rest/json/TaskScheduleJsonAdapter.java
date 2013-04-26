package pt.ist.bennu.cron4j.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.cron4j.domain.TaskSchedule;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(TaskSchedule.class)
public class TaskScheduleJsonAdapter implements JsonAdapter<TaskSchedule> {

	@Override
	public JsonElement view(TaskSchedule taskSchedule, JsonBuilder ctx) {
		JsonObject obj = new JsonObject();
		obj.addProperty("id", taskSchedule.getExternalId());
		obj.addProperty("name", taskSchedule.getTask().getLocalizedName());
		obj.addProperty("schedule", taskSchedule.getSchedule());
		return obj;
	}

	@Override
	public TaskSchedule create(JsonElement el, JsonBuilder ctx) {
		final JsonObject jsonObject = el.getAsJsonObject();
		final String taskName = jsonObject.get("taskName").getAsString();
		final String schedule = jsonObject.get("schedule").getAsString();
		return new TaskSchedule(taskName, schedule);
	}

	@Override
	public TaskSchedule update(JsonElement arg0, TaskSchedule arg1, JsonBuilder arg2) {
		return null;
	}
}
