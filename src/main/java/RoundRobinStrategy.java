import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Class for the Round Robin Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class RoundRobinStrategy implements Scheduler {
    /** The time quantum (in milliseconds) allocated to each task per round. */
    private final long quantum;

    /**
     * Constructor for Round Robin Strategy.
     * 
     * @param quantum the time quantum allocated to each task per execution round 
     *                (must be positive)
     * @throws IllegalArgumentException if quantum <= 0
     */
    public RoundRobinStrategy(long quantum) {
        this.quantum = quantum;
    }

    /**
     * Schedules a list of incoming tasks using the Round Robin Strategy.
     * 
     * The algorithm works as follows:
     * 1. Tasks are sorted by arrival time
     * 2. Available tasks are placed in a ready queue (FIFO)
     * 3. Each task executes for at most 'quantum' time units
     * 4. If a task completes within its quantum, the next task starts immediately
     * 5. If a task doesn't complete, it's moved to the back of the ready queue
     * 6. Consecutive executions of the same task are merged into single ScheduledTask entries
     * 
     * @param tasks incoming tasks (unsorted, may contain tasks with different arrival times)
     * @param processors list of available processors (must contain exactly one processor)
     * @return a list of ScheduledTasks sorted by start time, representing the execution schedule
     * @throws IllegalArgumentException if processors.size() != 1 or other 
     *                                   preconditions are violated
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        validatePreconditions(processors);
        
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort((t1, t2) -> Integer.compare(t1.getArrivalTime(), t2.getArrivalTime()));
        
        return executeRoundRobinScheduling(sortedTasks, processors.get(0));
    }
    
    private void validatePreconditions(List<Processor> processors) {
        if (processors.size() != 1) {
            throw new IllegalArgumentException(
                    "Round Robin strategy requires exactly one processor");
        }
    }
    
    private List<ScheduledTask> executeRoundRobinScheduling(List<Task> sortedTasks, 
                                                            Processor processor) {
        List<ScheduledTask> result = new ArrayList<>();
        Queue<TaskState> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int taskIndex = 0;
        
        while (taskIndex < sortedTasks.size() || !readyQueue.isEmpty()) {
            taskIndex = addNewlyArrivedTasks(sortedTasks, readyQueue, currentTime, taskIndex);
            
            if (!readyQueue.isEmpty()) {
                currentTime = executeTaskFromQueue(readyQueue, result, processor, currentTime);
            } else {
                currentTime = advanceToNextArrival(sortedTasks, taskIndex, currentTime);
            }
        }
        
        return result;
    }
    
    private int addNewlyArrivedTasks(List<Task> sortedTasks, Queue<TaskState> readyQueue, 
                                     int currentTime, int taskIndex) {
        while (taskIndex < sortedTasks.size() 
               && sortedTasks.get(taskIndex).getArrivalTime() <= currentTime) {
            Task arrivedTask = sortedTasks.get(taskIndex);
            readyQueue.offer(new TaskState(arrivedTask, arrivedTask.getLength()));
            taskIndex++;
        }
        return taskIndex;
    }
    
    private int executeTaskFromQueue(Queue<TaskState> readyQueue, List<ScheduledTask> result, 
                                     Processor processor, int currentTime) {
        TaskState currentTask = readyQueue.poll();
        long startTime = currentTime;
        long executionTime = Math.min(quantum, currentTask.remainingTime);
        
        currentTime += executionTime;
        currentTask.remainingTime -= executionTime;
        
        ScheduledTask newScheduledTask = createScheduledTask(currentTask.task, processor, 
                                                             startTime, currentTime);
        addOrMergeScheduledTask(result, newScheduledTask, startTime);
        
        if (currentTask.remainingTime > 0) {
            readyQueue.offer(currentTask);
        }
        
        return currentTime;
    }
    
    private ScheduledTask createScheduledTask(Task task, Processor processor, 
                                              long startTime, int endTime) {
        return new ScheduledTask(task, processor.getId(), (int) startTime, endTime);
    }
    
    private void addOrMergeScheduledTask(List<ScheduledTask> result, ScheduledTask newTask, 
                                         long startTime) {
        if (!result.isEmpty()) {
            ScheduledTask lastScheduled = result.get(result.size() - 1);
            if (canMergeWithPrevious(lastScheduled, newTask, startTime)) {
                result.remove(result.size() - 1);
                result.add(createMergedTask(lastScheduled, newTask));
                return;
            }
        }
        result.add(newTask);
    }
    
    private boolean canMergeWithPrevious(ScheduledTask lastScheduled, ScheduledTask newTask, 
                                         long startTime) {
        return lastScheduled.getTask().equals(newTask.getTask()) 
               && lastScheduled.getEndTime() == startTime;
    }
    
    private ScheduledTask createMergedTask(ScheduledTask lastScheduled, ScheduledTask newTask) {
        return new ScheduledTask(newTask.getTask(), newTask.getProcessorId(),
                                 lastScheduled.getStartTime(), newTask.getEndTime());
    }
    
    private int advanceToNextArrival(List<Task> sortedTasks, int taskIndex, int currentTime) {
        if (taskIndex < sortedTasks.size()) {
            return Math.max(currentTime, sortedTasks.get(taskIndex).getArrivalTime());
        }
        return currentTime;
    }
    
    private static class TaskState {
        final Task task;
        long remainingTime;
        
        TaskState(Task task, long remainingTime) {
            this.task = task;
            this.remainingTime = remainingTime;
        }
    }
}
