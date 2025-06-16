import java.util.ArrayList;
import java.util.List;

/**
 * Main class - used for running the schedulers.
 * 
 * @author Ferit Ismailov
 * @date 20.05.2025
 * 
 * @version 1.1
 * Robustness checks implemented and signature updated.
 * 
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class SchedulerService {

    /**
     * Executes scheduling using provided strategy.
     *
     * @param strategy     the scheduler type
     * @param tasks        the tasks to be scheduled (all must have non‑negative 
     *                     arrivalTime, length, id, priority)
     * @param nrProcessors the number of processors; must be ≥ 1
     * @return a list of ScheduledTasks sorted by start time
     * @throws IllegalArgumentException if {@code nrProcessors < 1}, or if any task 
     *                                   has a negative field
     */

    public static List<ScheduledTask> runScheduler(Scheduler strategy,
            List<Task> tasks, int nrProcessors) {
        
        validateInputs(strategy, tasks, nrProcessors);
        validateTasks(tasks);
        
        List<Processor> processors = createProcessors(nrProcessors);
        return strategy.schedule(tasks, processors);
    }
    
    /**
     * Validates the basic input parameters.
     * @param strategy the scheduling strategy
     * @param tasks the list of tasks
     * @param nrProcessors number of processors
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputs(Scheduler strategy, List<Task> tasks, int nrProcessors) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks list cannot be null");
        }
        if (nrProcessors < 1) {
            throw new IllegalArgumentException("Number of processors must be at least 1, got: " 
                                               + nrProcessors);
        }
    }
    
    /**
     * Validates all tasks in the list.
     * @param tasks list of tasks to validate
     * @throws IllegalArgumentException if any task is invalid
     */
    private static void validateTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            validateSingleTask(task, i);
        }
    }
    
    /**
     * Validates a single task.
     * @param task the task to validate
     * @param index the index of the task in the list
     * @throws IllegalArgumentException if the task is invalid
     */
    private static void validateSingleTask(Task task, int index) {
        if (task == null) {
            throw new IllegalArgumentException("Task at index " + index + " is null");
        }
        validateTaskFields(task);
    }
    
    /**
     * Validates all fields of a task.
     * @param task the task to validate
     * @throws IllegalArgumentException if any field is invalid
     */
    private static void validateTaskFields(Task task) {
        if (task.getArrivalTime() < 0) {
            throw new IllegalArgumentException("Task " + task.getId() 
                                               + " has negative arrival time: " 
                                               + task.getArrivalTime());
        }
        if (task.getLength() < 0) {
            throw new IllegalArgumentException("Task " + task.getId() 
                                               + " has negative length: " + task.getLength());
        }
        if (task.getId() < 0) {
            throw new IllegalArgumentException("Task has negative id: " + task.getId());
        }
        if (task.getPriority() < 0) {
            throw new IllegalArgumentException("Task " + task.getId() 
                                               + " has negative priority: " + task.getPriority());
        }
    }
    
    /**
     * Creates the specified number of processors.
     * @param nrProcessors number of processors to create
     * @return list of processors
     */
    private static List<Processor> createProcessors(int nrProcessors) {
        List<Processor> processors = new ArrayList<>();
        for (int i = 0; i < nrProcessors; i++) {
            processors.add(new Processor(i));
        }
        return processors;
    }
}