package managers;

import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    public void historyShouldNotBeNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("testTask", "test", TaskStatus.NEW);
        historyManager.addToHistory(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "History is empty");
        assertEquals(1, history.size(), "History is empty");
    }

    @Test
    public void historyShouldBeNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("testTask", "test", TaskStatus.NEW);
        historyManager.addToHistory(task);
        historyManager.remove(task.getId());
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "History is not empty");
    }

    @Test
    public void nodeShouldReturnTheLatest() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("testTask1", "test", TaskStatus.NEW);
        Task task2 = new Task("testTask2", "test", TaskStatus.NEW);
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(task1, history.getLast(), "Node is not changing places for entries");
    }

    @Test
    public void tasksShouldBeDeletedFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("testTask", "test", TaskStatus.NEW);
        historyManager.addToHistory(task);
        historyManager.remove(task.getId());
        assertFalse(historyManager.getHistory().contains(task), "Tasks are not deleted from history");
    }

}