/**
 * Class defining a scheduled task.
 * A task is scheduled for a specific processor and has a starting and ending time.
 *
 * @author Ferit Ismailov
 * @date 20.05.2025
 */
public class ScheduledTask {
    private final Task task;
    private final int processorId;
    private final int startTime;
    private final int endTime;
    
    /**
     * Constructor for a Scheduled Task.
     * @param task - the scheduled task.
     * @param processorId - on which processor.
     * @param startTime - from what time.
     * @param endTime - until what time.
     */
    public ScheduledTask(Task task, int processorId, int startTime, int endTime) {
        this.task = task;
        this.processorId = processorId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task getTask() {
        return task;
    }
    public int getProcessorId() {
        return processorId;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getEndTime() {
        return endTime;
    }
}