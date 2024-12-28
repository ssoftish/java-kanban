package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Task;
import managers.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                if ("/tasks".equals(path)) {
                    handleGetTasks(exchange);
                } else {
                    handleGetTask(exchange, path);
                }
                break;
            case "POST":
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange, path);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response, 200);
    }

    private void handleGetTask(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int taskId = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTask(taskId);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(task);
                sendText(exchange, response, 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Task task = gson.fromJson(requestBody, Task.class);

        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        taskManager.create(task);
        String response = gson.toJson(task);
        sendText(exchange, response, 201);
    }

    private void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int taskId = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTask(taskId);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                taskManager.deleteTask(taskId);
                sendText(exchange, "Deleted task: " + taskId, 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}