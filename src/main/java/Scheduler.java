import java.util.List;

/**
 * Interface to be implemented by all strategies.
 * @author Ferit Ismailov
 * @date 20.05.2025
 */
public interface Scheduler {
    /**
     * Assigns tasks to processors based on the scheduling strategy.
     * @param tasks incoming tasks (unsorted)
     * @param processors list of available processors
     * @return list of ScheduledTask ordered by start time
     */
    List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors);
}