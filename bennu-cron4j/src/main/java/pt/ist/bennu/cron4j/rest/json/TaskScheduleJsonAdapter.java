package pt.ist.bennu.cron4j.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.cron4j.domain.SchedulerSystem;
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
        obj.addProperty("name", taskSchedule.getTaskClassName());
        obj.addProperty("localizedName", taskSchedule.getTask().getLocalizedName());
        obj.addProperty("schedule", taskSchedule.getSchedule());
        return obj;
    }

    @Override
    public TaskSchedule create(JsonElement el, JsonBuilder ctx) {
        final JsonObject jsonObject = el.getAsJsonObject();

        final String taskName = jsonObject.get("taskName").getAsString();
        final String schedule = jsonObject.get("schedule").getAsString();

        for (TaskSchedule taskSchedule : SchedulerSystem.getInstance().getTaskSchedule()) {
            if (taskSchedule.getTaskClassName().equals(taskName)) {
                updateTaskSchedule(taskSchedule, taskName, schedule);
            }
        }

        return new TaskSchedule(taskName, schedule);
    }

    @Override
    public TaskSchedule update(JsonElement el, TaskSchedule taskSchedule, JsonBuilder ctx) {
        final JsonObject jsonObject = el.getAsJsonObject();
        final String taskName = jsonObject.get("taskName").getAsString();
        final String schedule = jsonObject.get("schedule").getAsString();

        updateTaskSchedule(taskSchedule, taskName, schedule);

        return taskSchedule;

    }

    private void updateTaskSchedule(TaskSchedule taskSchedule, final String taskName, final String schedule) {
        if (schedule != null && !schedule.equals(taskSchedule.getSchedule())) {
            taskSchedule.setSchedule(schedule);
        }

        if (taskName != null && !taskName.equals(taskSchedule.getTaskClassName())) {
            taskSchedule.setTaskClassName(taskName);
        }
    }
}
