package managers;

import models.Task;

import models.Node;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> history = new LinkedHashMap<>();
    private Node<Task> head;
    private Node<Task> tail;


    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    private void linkLast(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }

        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(task, tail, null);

        tail = newNode;
        history.put(task.getId(), newNode);

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new LinkedList<>();
        Node<Task> currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }

        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.getNext();
            final Node<Task> last = node.getLast();

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node) {
                head = next;
                head.setLast(null);
            } else if (tail == node) {
                tail = last;
                tail.setNext(null);
            } else {
                last.setNext(next);
                next.setLast(last);
            }
        }
    }

}
