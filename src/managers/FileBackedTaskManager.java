package managers;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            String header = "id,type,name,status,description,epic";
            fileWriter.write(header);

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
            System.out.println("Error while saving");
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
        return task.getId() + "," + TaskType.TASK.toString() + "," + task.getName()  + "," +
                task.getStatus() + "," + task.getDescription() + ",";
    }

    private String taskToString(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC.toString() + "," + epic.getName()  + "," +
                epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String taskToString(Subtask subtask) {
        return subtask.getId() + "," + TaskType.SUBTASK.toString() + "," + subtask.getName()  + "," + subtask.getStatus()
                + "," + subtask.getDescription() + "," + subtask.getEpicId() + ",";
    }

    private static Task fromString(String value) {
        int id, epicId = 0;
        String name, description;
        TaskStatus status = null;
        TaskType type;
        Task task = null;
        Epic epic = null;
        Subtask subtask = null;

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
                task.setId(id);
                return task;
            case "EPIC":
                epic = new Epic(name, description, status, new ArrayList<>());
                epic.setId(id);
                return epic;
            case "SUBTASK":
                subtask = new Subtask(name, description, status, epicId);
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
                if (!task.equals("id,type,name,status,description,epic")) {
                    if (!task.isEmpty()) {
                        Task newTask = fromString(task);

                        if (newTask != null) {
                            if (newTask.getType() == TaskType.TASK) {
                                manager.tasks.put(newTask.getId(), newTask);
                            } else if (newTask.getType() == TaskType.EPIC) {
                                Epic epic = (Epic) fromString(task);
                                manager.epics.put(epic.getId(), epic);
                            } else if (newTask.getType() == TaskType.SUBTASK) {
                                Subtask subtask = (Subtask) fromString(task);
                                manager.subtasks.put(subtask.getId(), subtask);
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
            System.out.println("Error in loading from file");
        }

        return manager;

    }

}
