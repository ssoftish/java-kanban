package models;

import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType type;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public TaskType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ("ID: " + id + ". " + name + ": " + description + ". Cтатус: " + status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId() && Objects.equals(getName(),
                task.getName()) && Objects.equals(getDescription(),
                task.getDescription()) && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getStatus());
    }
}