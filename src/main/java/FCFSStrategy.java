import java.util.List;

/**
 * Class for the First Come, First Served Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class FCFSStrategy implements Scheduler {

    /**
     * A method to schedule a list of tasks on a list of tasks based on FCFS.
     * @param tasks incoming tasks (unsorted)
     * @param processors list of available processors
     * @return a list of ScheduledTasks sorted on start time assigned based on the FCFS strategy.
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        // TODO: implement FCFS Strategy for task scheduling
        throw new UnsupportedOperationException("Not implemented");
    }
}