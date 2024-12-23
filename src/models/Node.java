package models;

public class Node<Task> {
    private Task data;
    private Node<Task> last;
    private Node<Task> next;

    public Node(Task data, Node<Task> last, Node<Task> next) {
        this.data = data;
        this.last = last;
        this.next = next;
    }

    public Task getData() {
        return data;
    }

    public Node<Task> getLast() {
        return last;
    }

    public Node<Task> getNext() {
        return next;
    }

    public void setData(Task data) {
        this.data = data;
    }

    public void setLast(Node<Task> last) {
        this.last = last;
    }

    public void setNext(Node<Task> next) {
        this.next = next;
    }
}