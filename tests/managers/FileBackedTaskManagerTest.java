package managers;
import models.*;
import managers.*;
import org.junit.jupiter.api.Test;

import java.io.*;
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
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            Task task1 = new Task("testTask1", "task1", TaskStatus.NEW);
            Task task2 = new Task("testTask2", "task2", TaskStatus.DONE);

            int id1 = manager.create(task1);
            int id2 = manager.create(task2);

            assertEquals(2, manager.getTasks().size(), "Tasks are not added");
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }
    }

    @Test
    public void managerLoadsTasksFromFile() {
        try {
            File file = File.createTempFile("testFile", ".txt");
            try (Writer fileWriter = new FileWriter(file);) {
                fileWriter.write("1,TASK,Task1,NEW,task1,");
                fileWriter.write("\n");

                fileWriter.write("2,TASK,Task2,NEW,task2,");
                fileWriter.write("\n");

            } catch (IOException e) {
                System.out.println("Error while putting data in file");
            }

            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            manager = manager.loadFromFile(file);

            assertEquals(2, manager.getTasks().size(), "File is not read");
        } catch (IOException e) {
            System.out.println("Test file wasn't created");
        }
    }


}
