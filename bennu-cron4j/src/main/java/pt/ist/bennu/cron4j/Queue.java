package pt.ist.bennu.cron4j;

import java.util.EmptyStackException;
import java.util.Stack;

import pt.ist.bennu.cron4j.domain.SchedulerSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Queue {
    private Queue() {
    }

    public static void queue(String value) {
        final Stack<String> queue = getQueue();
        if (!queue.contains(value)) {
            queue.add(value);
            setQueue(queue);
        }
    }

    public static String pop() {
        final Stack<String> queue = getQueue();
        String value = null;
        try {
            value = queue.pop();
        } catch (EmptyStackException ese) {
            value = null;
        } finally {
            setQueue(queue);
        }
        return value;
    }

    public static void unqueue(String value) {
        final Stack<String> queue = getQueue();
        queue.remove(value);
        setQueue(queue);
    }

    public static Stack<String> getQueue() {
        final Stack<String> stack = new Stack<String>();
        final JsonArray queue = new JsonParser().parse(SchedulerSystem.getInstance().getQueue()).getAsJsonArray();
        for (JsonElement el : queue) {
            stack.add(el.getAsString());
        }
        return stack;
    }

    private static void setQueue(Stack<String> queue) {
        GsonBuilder a = new GsonBuilder();
        Gson create = a.create();
        JsonArray jsonQueue = new JsonArray();
        SchedulerSystem.getInstance().setQueue(create.toJson(jsonQueue));
    }

}
