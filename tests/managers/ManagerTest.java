package managers;

import models.*;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class ManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    void shouldCreateTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        int id = manager.create(task);

        assertTrue(manager.getTasks().contains(task), "Tasks are not created");
    }

    @Test
    void shouldCreateEpics() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int id = manager.create(epic);

        assertTrue(manager.getEpics().contains(epic), "Epics are not created");
    }

    @Test
    void shouldCreateSubtasks() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        int id = manager.create(subtask1);

        assertTrue(manager.getSubtasks().contains(subtask1), "Subtasks are not created");
    }

    @Test
    void shouldReturnList() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(2));
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(6));
        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertNotNull(manager.getTasks(), "List of tasks is null");
        assertNotNull(manager.getEpics(), "List of epics is null");
        assertNotNull(manager.getSubtasks(), "List of subtasks is null");
    }

    @Test
    void shouldUpdateTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Task newTask = new Task("testTask", "newTest", TaskStatus.NEW, Duration.ofMinutes(2), LocalDateTime.now().plusMinutes(2));
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        Epic newEpic = new Epic("testEpic", "newTest", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask = new Subtask("testSubtask1", "test", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(5));
        Subtask newSubtask = new Subtask("testSubtask1", "newTest", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(9));
        int id = manager.create(subtask);
        newEpic.setId(epicId);
        newSubtask.setId(id);
        newTask.setId(taskId);

        manager.updateTask(newTask);
        manager.updateEpic(newEpic);
        manager.updateSubtask(newSubtask);

        assertEquals(newTask, manager.getTask(taskId), "Task is not updated");
        assertEquals(newEpic.getName(), manager.getEpic(epicId).getName(), "Name of epic is not updated");
        assertEquals(newEpic.getDescription(), manager.getEpic(epicId).getDescription(), "Description of epic is not updated");
        assertEquals(newSubtask, manager.getSubtask(id), "Subtask is not updated");
    }

    @Test
    void shouldDeleteTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));

        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(5));
        int id = manager.create(subtask);

        manager.deleteTask(taskId);
        manager.deleteSubtask(id);
        manager.deleteEpic(epicId);

        assertNull(manager.getTask(taskId), "Task is not deleted");
        assertNull(manager.getEpic(epicId), "Epic is not deleted");
        assertNull(manager.getSubtask(id), "Subtask is not deleted");
    }

    @Test
    void shouldClearTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));

        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(6));
        int id = manager.create(subtask);

        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();

        assertEquals(new ArrayList<>(), manager.getTasks(), "Tasks are not deleted");
        assertEquals(new ArrayList<>(), manager.getEpics(), "Epics are not deleted");
        assertEquals(new ArrayList<>(), manager.getSubtasks(), "Subtasks are not deleted");
    }

    @Test
    void shouldReturnTask() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));

        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(5));
        int id = manager.create(subtask);

        Task newTask = manager.getTask(taskId);
        Epic newEpic = manager.getEpic(epicId);
        Subtask newSubtask = manager.getSubtask(id);

        assertNotNull(newTask, "Manager doesn't return task");
        assertNotNull(newEpic, "Manager doesn't return epic");
        assertNotNull(newSubtask, "Manager doesn't return subtask");
    }

    @Test
    void shouldReturnHistory() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        Task newTask = new Task("testTask", "newTest", TaskStatus.NEW, Duration.ofMinutes(2), LocalDateTime.now().plusMinutes(5));
        int taskId = manager.create(task);
        manager.updateTask(newTask);

        assertNotNull(manager.getHistory(), "Manager doesn't return history");
    }

    @Test
    void shouldReturnEpicSubtasks() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(7));
        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        List<Subtask> testList = new ArrayList<>();
        testList.add(subtask1);
        testList.add(subtask2);

        assertEquals(testList, manager.getEpicSubtasks(epicId), "Doesn't return relevant list of epic's subtasks");
    }

    @Test
    void shouldReturnNotEmptyEpicSubtasks() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(6));
        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertNotNull(manager.getEpicSubtasks(epicId), "Returns null instead of list of subtasks");
    }

    @Test
    void shouldReturnPrioritisedTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now().minusMinutes(8));
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(5));
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertNotNull(manager.getPrioritisedTasks(), "Returns null instead of list of prioritised tasks");
    }

    @Test
    void shouldReturnPrioritisedTasksInCorrectOrder() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now().minusMinutes(8));
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>(), Duration.ofMillis(0), LocalDateTime.MAX.minusDays(5));
        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(5));
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        Set<Task> list = manager.getPrioritisedTasks();
        assertEquals(list.stream().findFirst(), Optional.of(task), "Doesn't put tasks in a right prioritised order");
    }

}
