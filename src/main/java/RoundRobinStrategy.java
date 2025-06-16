import java.util.List;

/**
 *  Class for the Round Robin Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class RoundRobinStrategy implements Scheduler {
    private final long quantum;

    public RoundRobinStrategy(long quantum) {
        this.quantum = quantum;
    }

    /**
     * A method to schedule a list of incoming tasks using the Round Robin Strategy.
     *
     * @param tasks incoming tasks (unsorted)
     * @param processors list of available processors
     * @pre {@code processors.length == 1}
     * @return a list of ScheduledTasks sorted on start time based on the Round Robin Strategy.
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        // TODO: time-slice tasks in round-robin across the processor
        throw new UnsupportedOperationException("Not implemented");
    }
}
