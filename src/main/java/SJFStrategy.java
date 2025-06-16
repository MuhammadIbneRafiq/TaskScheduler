import java.util.List;

/**
 * Class for the Shortest Job First Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class SJFStrategy implements Scheduler {

    /**
     * A method to schedule a list of tasks on a list of tasks based on SJF.
     * @param tasks incoming tasks (unsorted)
     * @param processors list of available processors
     * @return a list of ScheduledTasks sorted on start time assigned based on the SJF Strategy.
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        // TODO: SJF - when processors becomes available, pick shortest not scheduled job
        throw new UnsupportedOperationException("Not implemented");
    }
}
