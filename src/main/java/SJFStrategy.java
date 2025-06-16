import java.util.List;
import java.util.ArrayList;

/**
 * Class for the Shortest Job First Strategy.
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025 </b></p>
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
            int earliestProcessor = findEarliestAvailableProcessor(processorFreeTime);
            int currentTime = processorFreeTime[earliestProcessor];
            
            List<Integer> availableTasks = findAvailableTasks(sortedTasks, scheduled, currentTime);
            
            if (availableTasks.isEmpty()) {
                processorFreeTime[earliestProcessor] = findNextArrivalTime(sortedTasks, scheduled);
                continue;
            }
            
            int shortestTaskIndex = selectShortestTask(sortedTasks, availableTasks);
            ScheduledTask scheduledTask = createScheduledTask(sortedTasks.get(shortestTaskIndex), 
                                                            earliestProcessor, 
                                                            processorFreeTime[earliestProcessor]);
            result.add(scheduledTask);
            
            // Mark task as scheduled and update processor free time
            scheduled[shortestTaskIndex] = true;
            processorFreeTime[earliestProcessor] = scheduledTask.getEndTime();
            scheduledCount++;
        }
        
        // Sort result by start time
        result.sort((st1, st2) -> Integer.compare(st1.getStartTime(), st2.getStartTime()));
        return result;
    }
    
    private int findEarliestAvailableProcessor(int[] processorFreeTime) {
        int earliest = 0;
        for (int i = 1; i < processorFreeTime.length; i++) {
            if (processorFreeTime[i] < processorFreeTime[earliest]) {
                earliest = i;
            }
        }
        return earliest;
    }
    
    private List<Integer> findAvailableTasks(List<Task> sortedTasks, boolean[] scheduled, 
                                             int currentTime) {
        List<Integer> availableTasks = new ArrayList<>();
        for (int i = 0; i < sortedTasks.size(); i++) {
            if (!scheduled[i] && sortedTasks.get(i).getArrivalTime() <= currentTime) {
                availableTasks.add(i);
            }
        }
        return availableTasks;
    }
    
    private int findNextArrivalTime(List<Task> sortedTasks, boolean[] scheduled) {
        int nextArrival = Integer.MAX_VALUE;
        for (int i = 0; i < sortedTasks.size(); i++) {
            if (!scheduled[i]) {
                nextArrival = Math.min(nextArrival, sortedTasks.get(i).getArrivalTime());
            }
        }
        return nextArrival;
    }
    
    private int selectShortestTask(List<Task> sortedTasks, List<Integer> availableTasks) {
        int shortestTaskIndex = availableTasks.get(0);
        for (int taskIndex : availableTasks) {
            if (isTaskShorter(sortedTasks.get(taskIndex), sortedTasks.get(shortestTaskIndex))) {
                shortestTaskIndex = taskIndex;
            }
        }
        return shortestTaskIndex;
    }
    
    private boolean isTaskShorter(Task task1, Task task2) {
        if (task1.getLength() < task2.getLength()) {
            return true;
        }
        if (task1.getLength() == task2.getLength()) {
            // Tie-breaker: earlier arrival time (FCFS for same length)
            return task1.getArrivalTime() < task2.getArrivalTime();
        }
        return false;
    }
    
    private ScheduledTask createScheduledTask(Task task, int processorId, int processorFreeTime) {
        int startTime = Math.max(task.getArrivalTime(), processorFreeTime);
        int endTime = (int) (startTime + task.getLength());
        return new ScheduledTask(task, processorId, startTime, endTime);
    }
}
