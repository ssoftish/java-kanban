package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHttpHandler implements HttpHandler {


    public void handle(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String response = "BaseHttpHandler response";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Not Found";
        sendText(exchange, response, 404);
    }

    protected void sendConflict(HttpExchange exchange) throws IOException {
        String response = "Conflict with existing task";
        sendText(exchange, response, 409);
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;
}