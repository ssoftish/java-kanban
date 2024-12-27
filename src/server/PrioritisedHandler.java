package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrioritisedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public PrioritisedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handle(exchange, this.taskManager);
    }

    @Override
    public void handle(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        System.out.println("Received request: " + requestMethod + " " + path);
        if ("GET".equals(requestMethod) && "/prioritised".equals(path)) {
            handleGetPrioritized(exchange, taskManager);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String response = gson.toJson(taskManager.getPrioritisedTasks());
        sendText(exchange, response);
    }
}
