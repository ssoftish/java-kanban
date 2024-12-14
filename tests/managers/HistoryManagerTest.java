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

}