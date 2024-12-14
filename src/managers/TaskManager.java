package managers;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    Integer create(Task task);

    Integer create(Epic epic);

    Integer create(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Task> getHistory();

    List<Subtask> getEpicSubtasks(int epicId);
}
