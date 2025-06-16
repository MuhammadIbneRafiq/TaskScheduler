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
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be positive, got: " + quantum);
        }
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
    
    /**
     * Validates that exactly one processor is provided.
     * @param processors list of processors
     * @throws IllegalArgumentException if not exactly one processor
     */
    private void validatePreconditions(List<Processor> processors) {
        if (processors.size() != 1) {
            throw new IllegalArgumentException(
                    "Round Robin strategy requires exactly one processor");
        }
    }
    
    /**
     * Executes the main round robin scheduling algorithm.
     * @param sortedTasks tasks sorted by arrival time
     * @param processor the single processor to use
     * @return list of scheduled tasks
     */
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
    
    /**
     * Adds newly arrived tasks to the ready queue.
     * @param sortedTasks tasks sorted by arrival time
     * @param readyQueue queue of ready tasks
     * @param currentTime current simulation time
     * @param taskIndex current task index
     * @return updated task index
     */
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
    
    /**
     * Executes a task from the ready queue for one quantum.
     * @param readyQueue queue of ready tasks
     * @param result list of scheduled tasks
     * @param processor the processor
     * @param currentTime current simulation time
     * @return updated current time
     */
    private int executeTaskFromQueue(Queue<TaskState> readyQueue, List<ScheduledTask> result, 
                                     Processor processor, int currentTime) {
        TaskState currentTask = readyQueue.poll();
        int startTime = currentTime;
        long executionTime = Math.min(quantum, currentTask.remainingTime);
        
        currentTime += (int) executionTime;
        currentTask.remainingTime -= executionTime;
        
        ScheduledTask newScheduledTask = createScheduledTask(currentTask.task, processor, 
                                                             startTime, currentTime);
        addOrMergeScheduledTask(result, newScheduledTask, startTime);
        
        if (currentTask.remainingTime > 0) {
            readyQueue.offer(currentTask);
        }
        
        return currentTime;
    }
    
    /**
     * Creates a scheduled task for the given parameters.
     * @param task the task
     * @param processor the processor
     * @param startTime start time
     * @param endTime end time
     * @return new ScheduledTask
     */
    private ScheduledTask createScheduledTask(Task task, Processor processor, 
                                              long startTime, int endTime) {
        return new ScheduledTask(task, processor.getId(), (int) startTime, endTime);
    }
    
    /**
     * Adds a scheduled task to the result, merging with previous if possible.
     * @param result list of scheduled tasks
     * @param newTask new task to add
     * @param startTime start time of the new task
     */
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
    
    /**
     * Checks if a task can be merged with the previous execution.
     * @param lastScheduled last scheduled task
     * @param newTask new task
     * @param startTime start time of new task
     * @return true if tasks can be merged
     */
    private boolean canMergeWithPrevious(ScheduledTask lastScheduled, ScheduledTask newTask, 
                                         long startTime) {
        return lastScheduled.getTask().equals(newTask.getTask()) 
               && lastScheduled.getEndTime() == startTime;
    }
    
    /**
     * Creates a merged task from two consecutive executions.
     * @param lastScheduled previous execution
     * @param newTask current execution
     * @return merged ScheduledTask
     */
    private ScheduledTask createMergedTask(ScheduledTask lastScheduled, ScheduledTask newTask) {
        return new ScheduledTask(newTask.getTask(), newTask.getProcessorId(),
                                 lastScheduled.getStartTime(), newTask.getEndTime());
    }
    
    /**
     * Advances time to the next task arrival.
     * @param sortedTasks tasks sorted by arrival time
     * @param taskIndex current task index
     * @param currentTime current simulation time
     * @return updated current time
     */
    private int advanceToNextArrival(List<Task> sortedTasks, int taskIndex, int currentTime) {
        if (taskIndex < sortedTasks.size()) {
            return Math.max(currentTime, sortedTasks.get(taskIndex).getArrivalTime());
        }
        return currentTime;
    }
    
    /**
     * Internal class to track task state in the ready queue.
     */
    private static class TaskState {
        final Task task;
        long remainingTime;
        
        TaskState(Task task, long remainingTime) {
            this.task = task;
            this.remainingTime = remainingTime;
        }
    }
}
