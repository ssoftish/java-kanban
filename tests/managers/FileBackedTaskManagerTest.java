package managers;
import models.*;
import managers.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {


    @Test
    public void managerSavesAndReturnsEmptyFile() {
        try {
            File file = File.createTempFile("testFile", ".txt");
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            assertTrue(manager.getTasks().isEmpty(), "Tasks are not null");
            assertTrue(manager.getEpics().isEmpty(), "Epics are not null");
            assertTrue(manager.getSubtasks().isEmpty(), "Subtasks are not null");
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }
    }

    @Test
    public void managerSavesTasks() {
        try {
            File file = File.createTempFile("testFile", ".txt");
            FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

            int id1 = manager1.create(new Task("testTask1", "task1", TaskStatus.NEW));
            int id2 = manager1.create(new Task("testTask2", "task2", TaskStatus.DONE));

            FileBackedTaskManager manager2 = new FileBackedTaskManager(file);
            manager2 = manager2.loadFromFile(file);

            assertEquals(manager2.getTasks(), manager1.getTasks(), "Tasks are not added");
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }
    }

    @Test
    public void managerLoadsTasksFromFile() {
        try {
            File file = File.createTempFile("testFile", ".txt");
            FileBackedTaskManager manager1 = new FileBackedTaskManager(file);
            try (Writer fileWriter = new FileWriter(file);) {
                int id1 = manager1.create(new Task("testTask","task",TaskStatus.NEW));
                int id2 = manager1.create(new Epic("testEpic","epic", TaskStatus.NEW, new ArrayList<>()));
                int id3 = manager1.create(new Subtask("testSubtask","subtask", TaskStatus.NEW, id2));

            } catch (IOException e) {
                System.out.println("Error while putting data in file");
            }

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

            assertEquals(1, manager.getTasks().size(), "File didn't read tasks");
            assertEquals(1, manager.getEpics().size(), "File didn't read epics");
            assertEquals(1, manager.getSubtasks().size(), "File didn't read subtasks");
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }
    }


}
