package managers;

import managers.Managers;
import managers.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest extends ManagerTest<TaskManager> {
    @BeforeEach
    public void init(){
        manager = Managers.getDefault();
    }
    @Test
    public void inMemoryTaskManagerShouldAddTasks() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(17), LocalDateTime.now());
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());

        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask = new Subtask("testSubtask", "test", TaskStatus.NEW, epicId, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));

        int subtaskId = manager.create(subtask);

        assertNotNull(manager.getTasks(), "Tasks are not added");
        assertNotNull(manager.getEpics(), "Epics are not added");
        assertNotNull(manager.getSubtasks(), "Subtasks are not added");

        assertNotNull(manager.getTask(taskId), "Tasks cannot be found by ID");
        assertNotNull(manager.getEpic(epicId), "Epics cannot be found by ID");
        assertNotNull(manager.getSubtask(subtaskId), "Subtasks cannot be found by ID");
    }

    @Test
    public void changesAreDoneInTaskManagerToo() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int id = manager.create(task);
        task.setDescription("test1");

        assertEquals("test1", manager.getTask(id).getDescription(), "Description haven't changed in Task Manager");
    }

    @Test
    public void removedTasksShouldBeDeletedFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(12), LocalDateTime.now());
        int id = manager.create(task);
        Task testTask = manager.getTask(id);

        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int epicId = manager.create(epic);
        Epic testEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask("testSubtask", "test", TaskStatus.NEW, epicId, Duration.ofMinutes(56), LocalDateTime.now().plusMinutes(23));
        int subtaskId = manager.create(subtask);
        Subtask testSubtask = manager.getSubtask(subtaskId);

        manager.deleteTask(id);
        manager.deleteEpic(epicId);

        assertTrue(historyManager.getHistory().isEmpty(), "Tasks are not removed from history when deleted");
    }

    @Test
    public void epicStatusIsAssignedCorrectly() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(5));

        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status is not assigned correctly when subtasks differ in status");
    }

    @Test
    public void epicStatusIsAssignedCorrectly_NEW() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.NEW, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(5));

        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Epic status is not assigned correctly when all subtasks are new");
    }

    @Test
    public void epicStatusIsAssignedCorrectly_DONE() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.DONE, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now().minusMinutes(13));

        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Epic status is not assigned correctly when all subtasks are done");
    }

    @Test
    public void epicStatusIsAssignedCorrectly_INPROGRESS() {
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());
        int epicId = manager.create(epic);

        Subtask subtask1 = new Subtask("testSubtask1", "test1", TaskStatus.IN_PROGRESS, epicId, Duration.ofMinutes(3), LocalDateTime.now());
        Subtask subtask2 = new Subtask("testSubtask2", "test2", TaskStatus.IN_PROGRESS, epicId, Duration.ofMinutes(1), LocalDateTime.now().plusMinutes(5));

        int id1 = manager.create(subtask1);
        int id2 = manager.create(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status is not assigned correctly when all subtasks are in progress");
    }

    @Test
    public void tasksAreNotIntersecting() {
        Task task = new Task("testTask", "test", TaskStatus.NEW, Duration.ofMinutes(17), LocalDateTime.now());
        Epic epic = new Epic("testEpic", "test", TaskStatus.NEW, new ArrayList<>());

        int taskId = manager.create(task);
        int epicId = manager.create(epic);

        Subtask subtask = new Subtask("testSubtask", "test", TaskStatus.NEW, epicId, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));

        int subtaskId = manager.create(subtask);

        Set<Task> testSet = manager.getPrioritisedTasks();
        boolean test = true;

        if (!((subtask.getStartTime().isAfter(task.getEndTime())) || (subtask.getStartTime().isBefore(task.getStartTime())))) {
            if (!manager.getSubtasks().contains(subtask)) {
                test = false;
            }
        }

        assertTrue(test, "Tasks in prioritised tasks list are intersecting");
    }

}