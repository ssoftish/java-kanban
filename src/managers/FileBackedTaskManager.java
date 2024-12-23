package managers;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save(Task task) {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write(task.toString());
            fileWriter.write("\n");
        } catch (IOException e) {
            System.out.println("Error while saving a task");
        }
    }



    @Override
    public Integer create(Task task) {
        save(task);
        return super.create(task);
    }

    @Override
    public Integer create(Epic epic) {
        save(epic);
        return super.create(epic);
    }

    @Override
    public Integer create(Subtask subtask) {
        save(subtask);
        return super.create(subtask);
    }

    private String toString(Task task) {
        return task.getId() + TaskType.TASK.toString() + task.getName()  + "," +
                task.getStatus() + "," + task.getDescription() + ",";
    }

    private String toString(Epic epic) {
        return epic.getId() + TaskType.EPIC.toString() + epic.getName()  + "," +
                epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String toString(Subtask subtask) {
        return subtask.getId() + TaskType.SUBTASK.toString() + subtask.getName()  + "," + subtask.getStatus()
                + "," + subtask.getDescription() + "," + subtask.getEpicId() + ",";
    }

    private Task fromString(String value) {
        int id, epicId = 0;
        String name, description;
        TaskStatus status = null;
        TaskType type;
        Task task = null;

        String[] stringTask = value.split(",");

        id = Integer.parseInt(stringTask[0]);
        name = stringTask[2];
        description = stringTask[4];

        switch(stringTask[3]) {
            case "NEW":
                status = TaskStatus.NEW;
                break;
            case "IN_PROGRESS":
                status = TaskStatus.IN_PROGRESS;
                break;
            case "DONE":
                status = TaskStatus.DONE;
                break;
        }

        switch(stringTask[1]) {
            case "TASK":
                task = new Task(name, description, status);
                break;
            case "EPIC":
                task = new Epic(name, description, status, new ArrayList<>());
                break;
            case "SUBTASK":
                getEpic(epicId).addSubtaskId(id);
                task = new Subtask(name, description, status, epicId);
                break;
        }

        task.setId(id);

        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String str = Files.readString(file.toPath());
            String[] list = str.split("\n");
            for (String task : list) {
                int id = manager.create(manager.fromString(task));
            }
        } catch (IOException e) {
            System.out.println("Error in loading from file");
        }

        return manager;

    }

}
