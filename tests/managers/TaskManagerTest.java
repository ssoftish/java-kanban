package managers;

import managers.Managers;
import managers.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    TaskManager taskManager;
    @BeforeEach
    public void init(){
        taskManager = Managers.getDefault();
    }
    @Test
    public void inMemoryTaskManagerShouldAddTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW);
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());

        int taskId = taskManager.create(task);
        int epicId = taskManager.create(epic);

        Subtask subtask = new Subtask("testSubtask", "test", TaskStatus.NEW, epicId);

        int subtaskId = taskManager.create(subtask);

        assertNotNull(taskManager.getTasks(), "Tasks are not added");
        assertNotNull(taskManager.getEpics(), "Epics are not added");
        assertNotNull(taskManager.getSubtasks(), "Subtasks are not added");

        assertNotNull(taskManager.getTask(taskId), "Tasks cannot be found by ID");
        assertNotNull(taskManager.getEpic(epicId), "Epics cannot be found by ID");
        assertNotNull(taskManager.getSubtask(subtaskId), "Subtasks cannot be found by ID");
    }

    @Test
    public void changesAreDoneInTaskManagerToo() {
        Task task = new Task("testTask", "test", TaskStatus.NEW);
        int id = taskManager.create(task);
        task.setDescription("test1");

        assertEquals("test1", taskManager.getTask(id).getDescription(), "Description haven't changed in Task Manager");
    }

    @Test
    public void removedTasksShouldBeDeletedFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("testTask", "test", TaskStatus.NEW);
        int id = taskManager.create(task);
        Task testTask = taskManager.getTask(id);

        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int epicId = taskManager.create(epic);
        Epic testEpic = taskManager.getEpic(epicId);

        Subtask subtask = new Subtask("testSubtask", "test", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.create(subtask);
        Subtask testSubtask = taskManager.getSubtask(subtaskId);

        taskManager.deleteTask(id);
        taskManager.deleteEpic(epicId);

        assertTrue(historyManager.getHistory().isEmpty(), "Tasks are not removed from history when deleted");
    }

}