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


}