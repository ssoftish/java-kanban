package models;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void tasksShouldBeEqual() {
        Task task = new Task("testTask","testing", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int id = taskManager.create(task);

        assertEquals(taskManager.getTask(id), task, "Tasks with the same ID are not equal");
    }
}