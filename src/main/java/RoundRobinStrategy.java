import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

/**
 *  Class for the Round Robin Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class RoundRobinStrategy implements Scheduler {
    /** The time quantum (in milliseconds) allocated to each task per round */
    private final long quantum;

    /**
     * Constructor for Round Robin Strategy.
     * 
     * @param quantum the time quantum allocated to each task per execution round
     * @pre {@code quantum > 0}
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
     * @param processors list of available processors 
     * @pre {@code processors.size() == 1} (Round Robin operates on a single processor)
     * @pre {@code tasks != null && processors != null}
     * @pre {@code quantum > 0}
     * @return a list of ScheduledTasks sorted by start time, representing the execution schedule
     * @throws IllegalArgumentException if preconditions are violated (e.g., multiple processors)
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        // TODO: time-slice tasks in round-robin across the processor
        if (processors.size() != 1) {
            throw new IllegalArgumentException("Round Robin strategy requires exactly one processor");
        }
        
        List<ScheduledTask> result = new ArrayList<>();
        if (tasks.isEmpty()) {
            return result;
        }
        
        // Sort tasks by arrival time
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort((t1, t2) -> Integer.compare(t1.getArrivalTime(), t2.getArrivalTime()));
        
        // Queue for tasks that are ready but not finished
        Queue<TaskState> readyQueue = new LinkedList<>();
        
        int currentTime = 0;
        int taskIndex = 0;
        Processor processor = processors.get(0);
        
        while (taskIndex < sortedTasks.size() || !readyQueue.isEmpty()) {
            // Add newly arrived tasks to ready queue
            while (taskIndex < sortedTasks.size() && sortedTasks.get(taskIndex).getArrivalTime() <= currentTime) {
                Task arrivedTask = sortedTasks.get(taskIndex);
                readyQueue.offer(new TaskState(arrivedTask, arrivedTask.getLength()));
                taskIndex++;
            }
            
            if (!readyQueue.isEmpty()) {
                // Get next task from ready queue
                TaskState currentTask = readyQueue.poll();
                long startTime = currentTime;
                
                // Calculate execution time (min of quantum and remaining time)
                long executionTime = Math.min(quantum, currentTask.remainingTime);
                
                currentTime += executionTime;
                currentTask.remainingTime -= executionTime;
                
                // Create scheduled task for this execution
                ScheduledTask newScheduledTask = new ScheduledTask(currentTask.task, processor.getId(), 
                                                                 (int)startTime, currentTime);
                
                // Try to merge with previous execution of same task
                if (!result.isEmpty()) {
                    ScheduledTask lastScheduled = result.get(result.size() - 1);
                    if (lastScheduled.getTask().equals(currentTask.task) && 
                        lastScheduled.getEndTime() == startTime) {
                        // Merge consecutive executions - remove last and create merged one
                        result.remove(result.size() - 1);
                        result.add(new ScheduledTask(currentTask.task, processor.getId(),
                                                   lastScheduled.getStartTime(), currentTime));
                    } else {
                        result.add(newScheduledTask);
                    }
                } else {
                    result.add(newScheduledTask);
                }
                
                // If task not finished, put it back in ready queue
                if (currentTask.remainingTime > 0) {
                    readyQueue.offer(currentTask);
                }
            } else {
                // No ready tasks - advance time to next arrival
                if (taskIndex < sortedTasks.size()) {
                    currentTime = Math.max(currentTime, sortedTasks.get(taskIndex).getArrivalTime());
                }
            }
        }
        
        return result;
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
