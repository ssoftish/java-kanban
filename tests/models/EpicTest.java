package models;

import managers.TaskManager;
import managers.Managers;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void epicsShouldNotKeepIrrelevantSubtaskIds() {
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        Epic epic = new Epic("testTask","testing", TaskStatus.NEW, subtaskIds);
        int id = taskManager.create(epic);
        Subtask subtask = new Subtask("testTask", "test", TaskStatus.IN_PROGRESS, id);
        int subtaskId = taskManager.create(subtask);

        taskManager.deleteSubtask(subtaskId);

        assertTrue(epic.getSubtasksIds().isEmpty(), "Irrelevant subtask IDs are kept");
    }
}