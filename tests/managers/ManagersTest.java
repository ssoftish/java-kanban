package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void managerShouldNotBeNull() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        TaskManager taskManager1 = Managers.getDefault();

        assertNotNull(historyManager1, "History Manager is null");
        assertNotNull(taskManager1, "Task Manager is null");
    }
}
