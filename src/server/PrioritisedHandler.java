package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Task;
import managers.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritisedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritisedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        if ("GET".equals(requestMethod) && "/prioritized".equals(path)) {
            handleGetPrioritized(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritisedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, 200);
    }
}