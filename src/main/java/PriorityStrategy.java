import java.util.List;

/**
 * Class for the (Preemptive) Priority Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class PriorityStrategy implements Scheduler {

    /**
     * A method to schedule a list of incoming tasks taking priority into account.
     * @param tasks incoming tasks (unsorted)
     * @param processors list of available processors
     * @pre {@code processors.length == 1}
     * @return a list of ScheduledTasks sorted on start time based on the (Preemptive) Priority strategy.
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        // TODO: implement this (tie-breaker FCFS)
        throw new UnsupportedOperationException("Not implemented");
    }
}
