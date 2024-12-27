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
import java.util.ArrayList;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicsHandler(TaskManager taskManager) {
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
            case "GET":
                switch (path) {
                    case "/epics" :
                        handleGetEpics(exchange, taskManager);
                        break;
                    case "/epics/" :
                        handleGetEpic(exchange, taskManager);
                        break;
                    case "/epics/subtasks" :
                        handleGetSubtask(exchange, taskManager);
                        break;
                }
                break;

            case "POST" :
                handlePostEpic(exchange, taskManager);
                break;

            case "DELETE" :
                handleDeleteEpic(exchange, taskManager);
                break;

            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response);
    }

    private void handleGetEpic(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int epicId = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpic(epicId);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(epic);
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetSubtask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 4) {
            sendNotFound(exchange);
            return;
        }

        try {
            int epicId = Integer.parseInt(pathParts[2]);
            List<Integer> ids = taskManager.getEpic(epicId).getSubtasksIds();
            List<Subtask> subtasks = new ArrayList<>();
            for (Subtask subtask : taskManager.getSubtasks()) {
                if (ids.contains(subtask.getId())) subtasks.add(subtask);
            }

            if (subtasks.isEmpty()) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(subtasks);
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange, TaskManager taskManager) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Epic epic;

        try {
            epic = gson.fromJson(requestBody, Epic.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        Integer epicId = epic.getId();
        if (epicId != null) {
            taskManager.updateEpic(epic);
        } else {
            taskManager.create(epic);
        }

        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleDeleteEpic(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int epicId = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpic(epicId);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                taskManager.deleteEpic(epic.getId());
                sendText(exchange, "Deleted epic: " + epic.getId());
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}