package models;

import managers.TaskManager;
import managers.Managers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void epicsShouldBeEqual() {
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        Epic epic = new Epic("testTask","testing", TaskStatus.NEW, subtaskIds);
        int id = taskManager.create(epic);

        assertEquals(taskManager.getEpic(id), epic, "Epics with the same ID are not equal");
    }
}