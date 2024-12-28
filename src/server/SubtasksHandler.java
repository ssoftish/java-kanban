package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import models.*;
import managers.TaskManager;
import adapters.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SubtasksHandler(TaskManager taskManager) {
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

        switch (requestMethod) {
            case "GET" :
                switch (path) {
                    case "/subtasks" :
                        handleGetSubtasks(exchange, taskManager);
                        break;
                    case "/subtasks/" :
                        handleGetSubtask(exchange, taskManager);
                        break;
                }
                break;

            case "POST" :
                handlePostSubtask(exchange, taskManager);
                break;

            case "DELETE" :
                handleDeleteSubtask(exchange, taskManager);
                break;

            default :
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, response);
    }

    private void handleGetSubtask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
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
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Subtask subtask;

        try {
            subtask = gson.fromJson(requestBody, Subtask.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        Integer subtaskId = subtask.getId();
        if (subtaskId != null) {
            System.out.println("Updated subtask: " + subtaskId);
            taskManager.updateSubtask(subtask);
        } else {
            System.out.println("Created subtask: " + subtaskId);
            taskManager.create(subtask);
        }

        String response = gson.toJson(subtask);
        sendText(exchange, response);
    }

    private void handleDeleteSubtask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
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
                taskManager.deleteSubtask(subtask.getId());
                sendText(exchange, "Deleted subtask: " + subtask.getId());
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}