package managers;
import exception.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private final static String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write(HEADER);

            for (Task task : getTasks()) {
                fileWriter.write("\n");
                fileWriter.write(taskToString(task));
            }

            for (Epic epic : getEpics()) {
                fileWriter.write("\n");
                fileWriter.write(taskToString(epic));
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write("\n");
                fileWriter.write(taskToString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error while saving");
        }
    }



    @Override
    public Integer create(Task task) {
        int id = super.create(task);
        save();
        return id;
    }

    @Override
    public Integer create(Epic epic) {
        int id = super.create(epic);
        save();
        return id;
    }

    @Override
    public Integer create(Subtask subtask) {
        int id = super.create(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }


    private String taskToString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName()  + "," +
                task.getStatus() + "," + task.getDescription() + ",";
    }

    private String taskToString(Epic epic) {
        return epic.getId() + "," + epic.getType() + "," + epic.getName()  + "," +
                epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String taskToString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getType() + "," + subtask.getName()  + "," + subtask.getStatus()
                + "," + subtask.getDescription() + "," + subtask.getEpicId() + ",";
    }

    private Task fromString(String value) {

        String[] stringTask = value.split(",");

        int id = Integer.parseInt(stringTask[0]);
        String name = stringTask[2];
        String description = stringTask[4];
        TaskStatus status = TaskStatus.valueOf(stringTask[3]);

        switch(stringTask[1]) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description, status, new ArrayList<>());
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(stringTask[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
        }

        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        int newId = 0;
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String str = Files.readString(file.toPath());
            String[] list = str.split("\n");

            for (String task : list) {
                if (!task.equals(HEADER)) {
                    if (!task.isEmpty()) {
                        Task newTask = manager.fromString(task);

                        if (newTask != null) {
                            switch (newTask.getType()) {
                                case TaskType.TASK:
                                    manager.tasks.put(newTask.getId(), newTask);
                                    break;
                                case TaskType.EPIC:
                                    Epic epic = (Epic) manager.fromString(task);
                                    manager.epics.put(epic.getId(), epic);
                                    break;
                                case TaskType.SUBTASK:
                                    Subtask subtask = (Subtask) manager.fromString(task);
                                    manager.subtasks.put(subtask.getId(), subtask);

                                    Epic newEpic = manager.getEpic(subtask.getEpicId());
                                    newEpic.addSubtaskId(subtask.getId());
                                    manager.epics.put(newEpic.getId(), newEpic);
                                    break;
                            }
                        }

                        if (newTask.getId() > newId) {
                            newId = newTask.getId();
                        }
                    }
                }
                }


            manager.idGenerator = newId;
        } catch (IOException e) {
            throw new ManagerSaveException("Error in loading from file");
        }

        return manager;

    }

}
