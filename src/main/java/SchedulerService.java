import java.util.ArrayList;
import java.util.List;

/**
 * Main class - used for running the schedulers.
 * 
 * @author Ferit Ismailov
 * @date 20.05.2025
 * 
 * @version 1.0
 * TODO: Edit the signature and add robustness checks. Increment version.
 * 
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class SchedulerService {

    /**
     * Executes scheduling using provided strategy.
     * 
     * @param strategy     - the provided type of scheduler.
     * @param tasks        - which tasks are to be scheduled.
     * @param nrProcessors - how many cores we have.
     * @pre {@code nrProcessors >= 1 && 
     *      (\forall i; tasks.has(i); tasks[i].arrivalTime >= 0&& tasks[i].length >= 0 &&
     *              tasks[i].id >= 0 && tasks[i].priority >= 0)}
     * @throws IllegalArgumentException - if precondition violated
     * @return - a list of ScheduledTasks sorted on start time based on the given
     *         strategy.
     */
    public static List<ScheduledTask> runScheduler(Scheduler strategy,
            List<Task> tasks, int nrProcessors) {
        List<Processor> processors = new ArrayList<>();
        for (int i = 0; i < nrProcessors; i++) {
            processors.add(new Processor(i));
        }
        return strategy.schedule(tasks, processors);
    }
}