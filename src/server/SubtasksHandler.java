package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Subtask;
import managers.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                if ("/subtasks".equals(path)) {
                    handleGetSubtasks(exchange);
                } else {
                    handleGetSubtask(exchange, path);
                }
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtask(exchange, path);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, response, 200);
    }

    private void handleGetSubtask(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int subtaskId = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtask(subtaskId);
            if (subtask == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);

        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        taskManager.create(subtask);
        String response = gson.toJson(subtask);
        sendText(exchange, response, 201);
    }

    private void handleDeleteSubtask(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int subtaskId = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtask(subtaskId);
            if (subtask == null) {
                sendNotFound(exchange);
            } else {
                taskManager.deleteSubtask(subtaskId);
                sendText(exchange, "Deleted subtask: " + subtaskId, 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}