package managers;

import models.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idGenerator = 0;

    protected final Set<Task> prioritisedTasks = new TreeSet<>((task1, task2) -> {
        if ((task1.getStartTime() != null) && (task2.getStartTime() != null)) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if ((task1.getStartTime() == null) && (task2.getStartTime() != null)) {
            return -1;
        } else if ((task1.getStartTime() != null) && (task2.getStartTime() == null)) {
            return 1;
        } else {
            return 0;
        }
    });

    private void addPrioritisedTasks(Task task) {
        if (taskValidation(task)) prioritisedTasks.add(task);
    }

    private boolean taskValidation(Task task) {
        Set<Task> tasks = getPrioritisedTasks();
        boolean answer = true;

        for (Task otherTask : tasks) {
            if (task.getStartTime() != null) {
                if (task.getStartTime().isAfter(otherTask.getEndTime()) ||
                        otherTask.getStartTime().isAfter(task.getEndTime())) {
                    answer = true;
                } else {
                    answer = false;
                    break;
                }
            } else {
                answer = false;
            }
        }

        return answer;
    }

    private Integer makeId() {
        return idGenerator++;
    }
    @Override
    public Integer create(Task task) {
        int id = makeId();
        task.setId(id);
        tasks.put(id, task);
        addPrioritisedTasks(task);
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
        addPrioritisedTasks(subtask);
        epic.addSubtaskId(id);
        checkEpicStatus(epic.getId());
        setEpicTimeAndDuration(epic.getId());

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

    protected void setEpicTimeAndDuration(int epicId) {
        LocalDateTime start;
        LocalDateTime finish;
        Duration duration = Duration.ofMillis(0);

        Epic epic = epics.get(epicId);

        ArrayList<Integer> ids = epic.getSubtasksIds();
        start = subtasks.get(ids.getFirst()).getStartTime();
        finish = subtasks.get(ids.getFirst()).getEndTime();

        for (Integer subtaskId : ids) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStartTime().isBefore(start)) {
                    start = subtask.getStartTime();
                }

                if (subtask.getEndTime().isAfter(finish)) {
                    finish = subtask.getEndTime();
                }

                duration = duration.plus(subtask.getDuration());
            }
        }

        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(finish);
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            prioritisedTasks.remove(tasks.get(id));
            tasks.replace(id, task);
            prioritisedTasks.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            prioritisedTasks.remove(subtasks.get(id));
            subtasks.replace(id, subtask);
            prioritisedTasks.add(subtask);
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
        if (tasks.containsKey(id)) {
            prioritisedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.getSubtasksIds().remove(Integer.valueOf(id));
            prioritisedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtasksIds = epic.getSubtasksIds();

            subtasksIds.forEach(ids -> {
                subtasks.remove(ids);
                historyManager.remove(ids);
            });

            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteTasks() {
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritisedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.values().forEach(epic -> {
            ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
            subtasksIds.forEach(historyManager::remove);
            historyManager.remove(epic.getId());
        });

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
            subtaskIds.forEach(subtaskId -> {
                prioritisedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
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

        subtaskIds.forEach(id -> subtasks.values().stream()
                .filter(subtask -> subtask.getId() == id)
                .forEach(subtasks1::add));
        return subtasks1;
    }

    @Override
    public Set<Task> getPrioritisedTasks() {
        return prioritisedTasks;
    }
}