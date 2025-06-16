import java.util.List;
import java.util.ArrayList;

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
     * @param processors list of available processors (must contain exactly one processor)
     * @return a list of ScheduledTasks sorted on start time based on the Priority strategy.
     * @throws IllegalArgumentException if processors.size() != 1
     */
    @Override
    public List<ScheduledTask> schedule(List<Task> tasks, List<Processor> processors) {
        if (processors.size() != 1) {
            throw new IllegalArgumentException("Priority strategy requires exactly one processor");
        }
        
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create task state tracking
        List<TaskState> taskStates = new ArrayList<>();
        for (Task task : tasks) {
            taskStates.add(new TaskState(task));
        }
        
        List<ScheduledTask> result = new ArrayList<>();
        int currentTime = 0;
        
        while (!allTasksCompleted(taskStates)) {
            // Find the highest priority task that has arrived and is not completed
            TaskState currentTask = getHighestPriorityAvailableTask(taskStates, currentTime);
            
            if (currentTask == null) {
                // No tasks available, advance to next arrival time
                currentTime = getNextArrivalTime(taskStates, currentTime);
                continue;
            }
            
            // Find the next event time (either task completion or higher priority task arrival)
            int nextEventTime = findNextEventTime(taskStates, currentTask, currentTime);
            
            // Execute the current task until the next event
            int executionTime = nextEventTime - currentTime;
            int startTime = currentTime;
            int endTime = currentTime + executionTime;
            
            // Create scheduled task entry
            result.add(new ScheduledTask(currentTask.task, 0, startTime, endTime));
            
            // Update task state
            currentTask.remainingTime -= executionTime;
            if (currentTask.remainingTime <= 0) {
                currentTask.completed = true;
            }
            
            currentTime = nextEventTime;
        }
        
        // Merge consecutive executions of same task
        return mergeConsecutiveExecutions(result);
    }
    
    /**
     * Checks if all tasks have been completed.
     * @param taskStates list of task states
     * @return true if all tasks are completed
     */
    private boolean allTasksCompleted(List<TaskState> taskStates) {
        return taskStates.stream().allMatch(ts -> ts.completed);
    }
    
    /**
     * Finds the highest priority task that has arrived and is not completed.
     * @param taskStates list of task states
     * @param currentTime current simulation time
     * @return the highest priority available task, or null if none available
     */
    private TaskState getHighestPriorityAvailableTask(List<TaskState> taskStates, int currentTime) {
        TaskState bestTask = null;
        
        for (TaskState ts : taskStates) {
            if (!ts.completed && ts.task.getArrivalTime() <= currentTime) {
                if (bestTask == null || hasHigherPriority(ts.task, bestTask.task)) {
                    bestTask = ts;
                }
            }
        }
        
        return bestTask;
    }
    
    /**
     * Compares two tasks to determine if the first has higher priority.
     * @param task1 first task to compare
     * @param task2 second task to compare
     * @return true if task1 has higher priority than task2
     */
    private boolean hasHigherPriority(Task task1, Task task2) {
        if (task1.getPriority() > task2.getPriority()) {
            return true;
        } else if (task1.getPriority() < task2.getPriority()) {
            return false;
        } else {
            // Same priority - tie-breaker is arrival time (earlier wins)
            return task1.getArrivalTime() < task2.getArrivalTime();
        }
    }
    
    /**
     * Finds the next arrival time among uncompleted tasks.
     * @param taskStates list of task states
     * @param currentTime current simulation time
     * @return next arrival time
     */
    private int getNextArrivalTime(List<TaskState> taskStates, int currentTime) {
        int nextArrival = Integer.MAX_VALUE;
        for (TaskState ts : taskStates) {
            if (!ts.completed && ts.task.getArrivalTime() > currentTime) {
                nextArrival = Math.min(nextArrival, ts.task.getArrivalTime());
            }
        }
        return nextArrival == Integer.MAX_VALUE ? currentTime + 1 : nextArrival;
    }
    
    /**
     * Finds the next event time (task completion or higher priority arrival).
     * @param taskStates list of task states
     * @param currentTask currently executing task
     * @param currentTime current simulation time
     * @return next event time
     */
    private int findNextEventTime(List<TaskState> taskStates, TaskState currentTask, 
                                  int currentTime) {
        // When current task would complete
        int taskCompletionTime = currentTime + (int) currentTask.remainingTime;
        
        // When next higher priority task arrives
        int nextHigherPriorityArrival = Integer.MAX_VALUE;
        for (TaskState ts : taskStates) {
            if (!ts.completed && ts.task.getArrivalTime() > currentTime) {
                if (hasHigherPriority(ts.task, currentTask.task)) {
                    nextHigherPriorityArrival = Math.min(nextHigherPriorityArrival, 
                                                        ts.task.getArrivalTime());
                }
            }
        }
        
        return Math.min(taskCompletionTime, nextHigherPriorityArrival);
    }
    
    /**
     * Merges consecutive executions of the same task into single entries.
     * @param scheduledTasks list of scheduled tasks
     * @return merged list of scheduled tasks
     */
    private List<ScheduledTask> mergeConsecutiveExecutions(List<ScheduledTask> scheduledTasks) {
        if (scheduledTasks.isEmpty()) {
            return scheduledTasks;
        }
        
        List<ScheduledTask> merged = new ArrayList<>();
        ScheduledTask current = scheduledTasks.get(0);
        
        for (int i = 1; i < scheduledTasks.size(); i++) {
            ScheduledTask next = scheduledTasks.get(i);
            
            // If same task and consecutive execution (no gap), merge
            if (current.getTask().getId() == next.getTask().getId() 
                && current.getEndTime() == next.getStartTime()) {
                current = new ScheduledTask(current.getTask(), current.getProcessorId(), 
                                          current.getStartTime(), next.getEndTime());
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        
        return merged;
    }
    
    /**
     * Internal class to track task execution state.
     */
    private static class TaskState {
        final Task task;
        long remainingTime;
        boolean completed;
        
        TaskState(Task task) {
            this.task = task;
            this.remainingTime = task.getLength();
            this.completed = false;
        }
    }
}
