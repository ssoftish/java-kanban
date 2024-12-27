package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Реализация метода handle
        String response = "BaseHttpHandler response";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected void sendText(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Not Found";
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Task has interactions";
        exchange.sendResponseHeaders(409, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public abstract void handle(HttpExchange exchange, TaskManager manager) throws IOException;
}