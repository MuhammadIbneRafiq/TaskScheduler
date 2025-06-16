import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

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
        List<ScheduledTask> result = new ArrayList<>();
        if (tasks.isEmpty()) {
            return result;
        }
        
        // Sort tasks by arrival time first
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort((t1, t2) -> Integer.compare(t1.getArrivalTime(), t2.getArrivalTime()));
        
        // Track when each processor becomes free
        int[] processorFreeTime = new int[processors.size()];
        
        // Keep track of which tasks have been scheduled
        boolean[] scheduled = new boolean[sortedTasks.size()];
        int scheduledCount = 0;
        
        while (scheduledCount < sortedTasks.size()) {
            // Find the processor that becomes free earliest
            int earliestProcessor = 0;
            for (int i = 1; i < processors.size(); i++) {
                if (processorFreeTime[i] < processorFreeTime[earliestProcessor]) {
                    earliestProcessor = i;
                }
            }
            
            int currentTime = processorFreeTime[earliestProcessor];
            
            // Find all tasks that have arrived by currentTime and are not yet scheduled
            List<Integer> availableTasks = new ArrayList<>();
            for (int i = 0; i < sortedTasks.size(); i++) {
                if (!scheduled[i] && sortedTasks.get(i).getArrivalTime() <= currentTime) {
                    availableTasks.add(i);
                }
            }
            
            // If no tasks are available, advance time to next task arrival
            if (availableTasks.isEmpty()) {
                int nextArrival = Integer.MAX_VALUE;
                for (int i = 0; i < sortedTasks.size(); i++) {
                    if (!scheduled[i]) {
                        nextArrival = Math.min(nextArrival, sortedTasks.get(i).getArrivalTime());
                    }
                }
                processorFreeTime[earliestProcessor] = nextArrival;
                continue;
            }
            
            // Among available tasks, find the shortest one (SJF)
            int shortestTaskIndex = availableTasks.get(0);
            for (int taskIndex : availableTasks) {
                Task currentTask = sortedTasks.get(taskIndex);
                Task shortestTask = sortedTasks.get(shortestTaskIndex);
                if (currentTask.getLength() < shortestTask.getLength()) {
                    shortestTaskIndex = taskIndex;
                } else if (currentTask.getLength() == shortestTask.getLength()) {
                    // Tie-breaker: earlier arrival time (FCFS for same length)
                    if (currentTask.getArrivalTime() < shortestTask.getArrivalTime()) {
                        shortestTaskIndex = taskIndex;
                    }
                }
            }
            
            Task selectedTask = sortedTasks.get(shortestTaskIndex);
            int startTime = Math.max(selectedTask.getArrivalTime(), processorFreeTime[earliestProcessor]);
            int endTime = (int) (startTime + selectedTask.getLength());
            
            ScheduledTask scheduledTask = new ScheduledTask(selectedTask, earliestProcessor, startTime, endTime);
            result.add(scheduledTask);
            
            // Mark task as scheduled and update processor free time
            scheduled[shortestTaskIndex] = true;
            processorFreeTime[earliestProcessor] = endTime;
            scheduledCount++;
        }
        
        // Sort result by start time
        result.sort((st1, st2) -> Integer.compare(st1.getStartTime(), st2.getStartTime()));
        
        return result;
    }
}
