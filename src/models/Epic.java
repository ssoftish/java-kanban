package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds) {
        super(name, description, status);
        this.subtaskIds = subtaskIds;
        this.type = TaskType.EPIC;
    }

    public Epic(String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subtaskIds = subtaskIds;
        this.type = TaskType.EPIC;
        this.endTime = startTime.plus(duration);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Epic epic = (Epic) obj;
        return (getId() == epic.getId()) && Objects.equals(getSubtasksIds(), epic.getSubtasksIds());
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