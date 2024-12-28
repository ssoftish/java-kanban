package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.HttpTaskServer;
import models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.*;
import adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    static HttpTaskServer httpTaskServer;
    static Gson gson;
    static TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;


    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.start();

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int taskId = taskManager.create(task);
        int epicId = taskManager.create(epic);
        subtask = new Subtask("testSubtask", "test", TaskStatus.NEW, epicId, Duration.ofMinutes(2), LocalDateTime.now().plusMinutes(5));



        int subtaskId = taskManager.create(subtask);

        Task testTask = taskManager.getTask(taskId);
        Epic testEpic = taskManager.getEpic(epicId);
        Subtask testSubtask = taskManager.getSubtask(subtaskId);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    void stopServer() {
        httpTaskServer.stop();
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(tasksList, "Task list is not received");
        assertEquals(taskManager.getTasks(), tasksList, "Received task list is wrong");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();

        List<Epic> epicList = gson.fromJson(response.body(), epicType);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(epicList, "Epic list is not received");
        assertEquals(taskManager.getEpics(), epicList, "Received epic list is wrong");
    }

    @Test
    void getAllSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Subtask> subtaskList = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(subtaskList, "Epic list is not received");
        assertEquals(taskManager.getSubtasks(), subtaskList, "Received epic list is wrong");
    }

    @Test
    void getTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskDeserialized = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(taskDeserialized, "Task is not received");
        assertEquals(taskManager.getTask(task.getId()), taskDeserialized, "Received wrong task");
    }

    @Test
    void getEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicDeserialized = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(epicDeserialized, "Epic is not received");
        assertEquals(taskManager.getEpic(epic.getId()), epicDeserialized, "Received wrong epic");
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskDeserialized = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(subtaskDeserialized, "Subtask is not received");
        assertEquals(taskManager.getSubtask(subtask.getId()), subtaskDeserialized, "Received wrong subtask");
    }
    @Test
    void getSubtasksByOneEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);
        List<Integer> ids = epic.getSubtasksIds();
        List<Subtask> testList = new ArrayList<>();
        for (Subtask subtask : taskManager.getSubtasks()) {
            if (ids.contains(subtask.getId())) testList.add(subtask);
        }

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(subtasksList, "Subtask list is not received");
        assertEquals(testList, subtasksList, "Received wrong subtasks list");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(history, "History is not received");
        assertEquals(3, history.size(), "Received history list is wrong");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> priority = gson.fromJson(response.body(), taskType);
        Set<Task> testList = taskManager.getPrioritisedTasks();

        assertEquals(200, response.statusCode(), "Status code is not 200");
        assertNotNull(priority, "Prioritised list is not received");
        assertEquals(2, priority.size(), "Received wrong prioritised list");
    }


    @Test
    void addNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        Task task2 = new Task("testTask2", "test", TaskStatus.NEW, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(10));
        String json = gson.toJson(task2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 201");
        assertEquals(2, taskManager.getTasks().size(), "New task is not added");
    }

    @Test
    void addNewEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic2 = new Epic("testEpic2", "test", TaskStatus.NEW, new ArrayList<>());
        String json = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 201");
        assertEquals(2, taskManager.getEpics().size(), "New epic is not added");
    }

    @Test
    void addNewSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        Subtask subtask2 = new Subtask("testSubtask", "test", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(2), LocalDateTime.now().plusMinutes(15));
        String json = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 201");
        assertEquals(2, taskManager.getSubtasks().size(), "New subtask is not added");
    }

    @Test
    void updateNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        Task taskUpdate = new Task("newTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(20));
        taskUpdate.setId(task.getId());

        String json = gson.toJson(taskUpdate);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode(), "Status code is not 201");
        assertEquals(taskUpdate, taskManager.getTasks().get(task.getId()), "Task is not updated");
    }
    //
    @Test
    void updateNewEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        Epic epic2 = new Epic("newEpic", "test", TaskStatus.NEW, new ArrayList<>());
        epic2.setId(epic.getId());
        String json = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 201");
        assertEquals(epic2, taskManager.getEpics().get(epic.getId()), "Epic is not updated");
    }

    @Test
    void updateNewSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        Subtask subtask2 = new Subtask("newSubtask", "Test", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofMinutes(2), LocalDateTime.now().plusMinutes(5));
        subtask2.setId(subtask.getId());
        String json = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 201");
        assertEquals(subtask2, taskManager.getSubtasks().get(subtask.getId()), "Subtask is not updated");
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 200");
        Assertions.assertTrue(taskManager.getTasks().isEmpty(), "Task is not removed");
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Status code is not 200");
        Assertions.assertTrue(taskManager.getEpics().isEmpty(), "Epic is not removed");
    }

    @Test
    void removeSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(taskManager.getSubtasks());
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Status code is not 200");
            Assertions.assertTrue(taskManager.getSubtasks().isEmpty(), "Subtask is not removed");
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

}