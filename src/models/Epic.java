package models;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds) {
        super(name, description, status);
        this.subtaskIds = subtaskIds;
        this.type = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return (getId() == epic.getId()) &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return ("ID: " + id + ". " + name + ": " + description + ". Cтатус: " + status + "." +
                " ID подзадач: " + subtaskIds);
    }
}