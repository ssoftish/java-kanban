package managers;

import models.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idGenerator = 0;

    private Integer makeId() {
        return idGenerator++;
    }
    @Override
    public Integer create(Task task) {
        int id = makeId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer create(Epic epic) {
        int id = makeId();
        epic.setId(id);
        epics.put(id, epic);

        return id;
    }

    @Override
    public Integer create(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }

        int id = makeId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(id);
        checkEpicStatus(epic.getId());

        return id;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    private void checkEpicStatus(int epicId) {
        int newCounter = 0;
        int doneCounter = 0;
        Epic epic = epics.get(epicId);

        ArrayList<Integer> ids = epic.getSubtasksIds();
        for (Integer subtaskId : ids) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                TaskStatus checkStatus = subtask.getStatus();
                if (checkStatus == TaskStatus.NEW) {
                    newCounter++;
                } else if (checkStatus == TaskStatus.DONE) {
                    doneCounter++;
                }
            }
        }

        if (ids.size() == newCounter) {
            epic.setStatus(TaskStatus.NEW);
        } else if (ids.size() == doneCounter) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.replace(id, task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.replace(id, subtask);
            int epicId = subtask.getEpicId();
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic updatingEpic = epics.get(id);
            updatingEpic.setName(epic.getName());
            updatingEpic.setDescription(epic.getDescription());
            epics.replace(id, updatingEpic);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.getSubtasksIds().remove(Integer.valueOf(id));
            subtasks.remove(id);
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtasksIds = epic.getSubtasksIds();

            for (Integer ids : subtasksIds) {
                subtasks.remove(ids);
            }

            epics.remove(id);
        }
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
            epic.setStatus(TaskStatus.NEW);
            epic.getSubtasksIds().clear();
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addToHistory(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> subtasks1 = new ArrayList<>();
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtasksIds();

        for (Integer id : subtaskIds) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getId() == id) {
                    subtasks1.add(subtask);
                }
            }
        }
        return subtasks1;
    }
}