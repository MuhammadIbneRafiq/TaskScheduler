import java.util.Objects;

/**
 * Class that defines a task by its id, priority and arrival time.
 *
 * @author Ferit Ismailov
 * @date 20.05.2025
 */
public class Task implements Comparable<Task> {
    private final int id;
    private final int priority;
    private final long length; // in milliseconds
    private final int arrivalTime;

    /**
     * Basic constructor for the Task class.
     * 
     * @param id          - id of the task
     * @param priority    - priority of the task
     * @param length      - length of the task in milliseconds
     * @param arrivalTime - arrival time of the task (in milliseconds since start)
     */
    public Task(int id, int priority, long length, int arrivalTime) {
        this.id = id;
        this.priority = priority;
        this.length = length;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public int compareTo(Task other) {
        // default: compare by arrivalTime
        if (this.arrivalTime < other.arrivalTime) {
            return -1;
        } else if (this.arrivalTime > other.arrivalTime) {
            return 1;
        }
        return 0;
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public long getLength() {
        return length;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}