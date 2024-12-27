package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import models.Task;
import managers.TaskManager;
import adapters.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TasksHandler(TaskManager taskManager) {
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
                    case "/tasks" :
                        handleGetTasks(exchange, taskManager);
                        break;
                    case "/tasks/" :
                        handleGetTask(exchange, taskManager);
                        break;
                }
                break;

            case "POST" :
                handlePostTask(exchange, taskManager);
                break;

            case "DELETE" :
                handleDeleteTask(exchange, taskManager);
                break;

            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response);
    }

    private void handleGetTask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
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
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Task task;

        try {
            task = gson.fromJson(requestBody, Task.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        Integer taskId = task.getId();
        if (taskId != null) {
            System.out.println("Updated task: " + taskId);
            taskManager.updateTask(task);
        } else {
            System.out.println("Created new task: " + taskId);
            taskManager.create(task);
        }

        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handleDeleteTask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
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
                taskManager.deleteTask(task.getId());
                sendText(exchange, "Deleted task: " + task.getId());
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}