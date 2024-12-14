package models;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void subtasksShouldBeEqual() {
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        Epic epic = new Epic("testTask","testing", TaskStatus.NEW, subtaskIds);
        int epicId = taskManager.create(epic);
        Subtask subtask = new Subtask("testSubtask", "testing", TaskStatus.NEW, epicId);
        int id = taskManager.create(subtask);

        assertEquals(taskManager.getSubtask(id), subtask, "Subtasks with the same ID are not equal");
    }
}