package managers;
import models.*;
import managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends ManagerTest<FileBackedTaskManager>{

    @BeforeEach
    void init() {
        try {
            File file = File.createTempFile("testFile", ".txt");
            this.manager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }

    }
    @Test
    public void managerSavesAndReturnsEmptyFile() {
        assertTrue(manager.getTasks().isEmpty(), "Tasks are not null");
        assertTrue(manager.getEpics().isEmpty(), "Epics are not null");
        assertTrue(manager.getSubtasks().isEmpty(), "Subtasks are not null");
    }

    @Test
    public void managerSavesTasksAndLoadsTasksFromFile() {
        try {
            File file = File.createTempFile("testFile", ".txt");
            FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

            int id1 = manager1.create(new Task("testTask","task",TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
            int id2 = manager1.create(new Epic("testEpic","epic", TaskStatus.NEW, new ArrayList<>(), Duration.ofMinutes(1), LocalDateTime.MAX.minusMinutes(1)));
            int id3 = manager1.create(new Subtask("testSubtask","subtask", TaskStatus.NEW, id2, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(8)));

            manager = FileBackedTaskManager.loadFromFile(file);

            assertNotNull(manager1.getTasks(), "Manager didn't save tasks");
            assertNotNull(manager1.getEpics(), "Manager didn't save epics");
            assertNotNull(manager1.getSubtasks(), "Manager didn't save subtasks");

            assertEquals(manager1.getTasks(), manager.getTasks(), "Manager didn't read tasks");
            assertEquals(manager1.getEpics(), manager.getEpics(), "Manager didn't read epics");
            assertEquals(manager1.getSubtasks(), manager.getSubtasks(), "Manager didn't read subtasks");
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }
    }


}
