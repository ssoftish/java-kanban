package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Epic;
import managers.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                if ("/epics".equals(path)) {
                    handleGetEpics(exchange);
                } else {
                    handleGetEpic(exchange, path);
                }
                break;
            case "POST":
                handlePostEpic(exchange);
                break;
            case "DELETE":
                handleDeleteEpic(exchange, path);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response, 200);
    }

    private void handleGetEpic(HttpExchange exchange, String path) throws IOException {
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
                sendText(exchange, response, 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Epic epic = gson.fromJson(requestBody, Epic.class);

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        taskManager.create(epic);
        String response = gson.toJson(epic);
        sendText(exchange, response, 201);
    }

    private void handleDeleteEpic(HttpExchange exchange, String path) throws IOException {
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
                taskManager.deleteEpic(epicId);
                sendText(exchange, "Deleted epic: " + epicId, 200);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}