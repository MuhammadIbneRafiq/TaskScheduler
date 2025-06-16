import java.util.List;
import java.util.ArrayList;

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
        List<ScheduledTask> result = new ArrayList<>();
        if (tasks.isEmpty()) {
            return result;
        }
        
        // Sort tasks by arrival time (FCFS - first come, first served)
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort((t1, t2) -> Integer.compare(t1.getArrivalTime(), t2.getArrivalTime()));
        
        // Track when each processor becomes free
        int[] processorFreeTime = new int[processors.size()];
        
        for (Task task : sortedTasks) {
            // Find the processor that will be free earliest
            int earliestProcessor = 0;
            for (int i = 1; i < processors.size(); i++) {
                if (processorFreeTime[i] < processorFreeTime[earliestProcessor]) {
                    earliestProcessor = i;
                }
            }
            
            // Task starts when both it has arrived and processor is free
            int startTime = Math.max(task.getArrivalTime(), processorFreeTime[earliestProcessor]);
            int endTime = (int) (startTime + task.getLength());
            
            ScheduledTask scheduledTask = new ScheduledTask(task, earliestProcessor, 
                                                            startTime, endTime);
            result.add(scheduledTask);
            
            // Update when this processor becomes free
            processorFreeTime[earliestProcessor] = endTime;
        }
        
        // Sort result by start time
        result.sort((st1, st2) -> Integer.compare(st1.getStartTime(), st2.getStartTime()));
        
        return result;
    }
}