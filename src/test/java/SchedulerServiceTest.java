import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Scheduler Service.
 * This is testing the behaviour of the all the strategies.
 *
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Replace this line</b></p>
 * <!--//# END TODO-->
 */
public class SchedulerServiceTest {

    private final List<Task> sampleTasks = List.of(
            new Task(1, 1, 1000, 0),
            new Task(2, 3, 500, 2),
            new Task(3, 2, 800, 4)
    );

    @Test
    void testFCFS_ArrivalTimeAscending() {
        // Test FCFS - checking if arrival time is ascending for each processor
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 10),
            new Task(2, 1, 200, 5),
            new Task(3, 1, 150, 15),
            new Task(4, 1, 300, 0)
        );
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 2);
        
        assertEquals(4, result.size());
        
        // Check that tasks are processed by arrival time
        // Task 4 arrives first (time 0), then 2 (time 5), then 1 (time 10), then 3 (time 15)
        assertEquals(4, result.get(0).getTask().getId()); // First to start
        assertEquals(2, result.get(1).getTask().getId()); // Second to start
    }

    @Test
    void testSJF_OrderOfFourTasks() {
        // Test SJF - checking the order of four tasks (length, arrivaltime): (10, 0), (5, 0), (2, 2), (1, 4)
        List<Task> tasks = List.of(
            new Task(1, 1, 10, 0),  // length 10, arrival 0
            new Task(2, 1, 5, 0),   // length 5, arrival 0
            new Task(3, 1, 2, 2),   // length 2, arrival 2
            new Task(4, 1, 1, 4)    // length 1, arrival 4
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(4, result.size());
        
        // Should execute in order: task2(5), task3(2), task4(1), task1(10)
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(3, result.get(1).getTask().getId());
        assertEquals(4, result.get(2).getTask().getId());
        assertEquals(1, result.get(3).getTask().getId());
    }

    @Test
    void testPriority_BasicFourTasks() {
        // Test priority - basic test for four tasks, similar to the one above
        List<Task> tasks = List.of(
            new Task(1, 1, 10, 0),  // priority 1, arrival 0
            new Task(2, 3, 5, 0),   // priority 3, arrival 0
            new Task(3, 2, 2, 2),   // priority 2, arrival 2
            new Task(4, 4, 1, 4)    // priority 4, arrival 4
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertTrue(result.size() >= 4);
        
        // Highest priority task (4) should complete first
        assertEquals(2, result.get(0).getTask().getId()); // Highest priority at start
    }

    @Test
    void testPriority_SimultaneousArrival() {
        // Test simultaneous priority - four tasks arriving simultaneously
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),  // priority 1
            new Task(2, 3, 200, 0),  // priority 3
            new Task(3, 2, 150, 0),  // priority 2
            new Task(4, 4, 50, 0)    // priority 4 (highest)
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(4, result.size());
        
        // Should execute in priority order: task4, task2, task3, task1
        assertEquals(4, result.get(0).getTask().getId());
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(3, result.get(2).getTask().getId());
        assertEquals(1, result.get(3).getTask().getId());
    }

    @Test
    void testPriority_TaskInterruption() {
        // Testing interruption of a task in priority scheduler
        List<Task> tasks = List.of(
            new Task(1, 1, 500, 0),  // Low priority, long task
            new Task(2, 3, 200, 100) // High priority, arrives later
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Task 1 runs first, gets interrupted by task 2, then resumes
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(1, result.get(2).getTask().getId());
    }

    @Test
    void testPriority_TaskEndsAsOtherArrives() {
        // Testing that priority properly schedules when a task ends at the same time as another arriving
        List<Task> tasks = List.of(
            new Task(1, 2, 100, 0),   // Medium priority, ends at time 100
            new Task(2, 3, 150, 100)  // High priority, arrives at time 100
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        
        // Task 1 completes, then task 2 starts immediately
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(100, result.get(0).getEndTime());
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(100, result.get(1).getStartTime());
    }

    @Test
    void testPriority_GapInArrivals() {
        // Test that priority works if nothing arrives for a while
        List<Task> tasks = List.of(
            new Task(1, 1, 50, 0),    // Ends at time 50
            new Task(2, 2, 100, 200)  // Arrives at time 200 (gap of 150ms)
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
        
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(200, result.get(1).getStartTime());
        assertEquals(300, result.get(1).getEndTime());
    }

    @Test
    void testRoundRobin_Basic() {
        // Basic round robin test
        List<Task> tasks = List.of(
            new Task(1, 1, 700, 0),  // needs 2 rounds: 500 + 200
            new Task(2, 1, 400, 0)   // needs 1 round: 400
        );
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertTrue(result.size() >= 2);
        
        // Verify correct total execution times
        int task1TotalTime = result.stream()
                .filter(st -> st.getTask().getId() == 1)
                .mapToInt(st -> st.getEndTime() - st.getStartTime())
                .sum();
        assertEquals(700, task1TotalTime);
        
        int task2TotalTime = result.stream()
                .filter(st -> st.getTask().getId() == 2)
                .mapToInt(st -> st.getEndTime() - st.getStartTime())
                .sum();
        assertEquals(400, task2TotalTime);
    }

    @Test
    void testRoundRobin_SimultaneousArrival() {
        // Simultaneous round robin - four tasks arriving at the same time
        List<Task> tasks = List.of(
            new Task(1, 1, 150, 0),  // 3 rounds: 50 + 50 + 50
            new Task(2, 1, 75, 0),   // 2 rounds: 50 + 25
            new Task(3, 1, 25, 0),   // 1 round: 25
            new Task(4, 1, 100, 0)   // 2 rounds: 50 + 50
        );
        Scheduler scheduler = new RoundRobinStrategy(50);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertTrue(result.size() >= 4);
        
        // Verify all tasks execute for correct total time
        int task1TotalTime = result.stream()
                .filter(st -> st.getTask().getId() == 1)
                .mapToInt(st -> st.getEndTime() - st.getStartTime())
                .sum();
        assertEquals(150, task1TotalTime);
        
        int task2TotalTime = result.stream()
                .filter(st -> st.getTask().getId() == 2)
                .mapToInt(st -> st.getEndTime() - st.getStartTime())
                .sum();
        assertEquals(75, task2TotalTime);
    }

    @Test
    void testRoundRobin_QuantumLargerThanTasks() {
        // Testing quantum > task lengths
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 150, 0),
            new Task(3, 1, 80, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(1000); // Very large quantum
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // With large quantum, should behave like FCFS
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
        
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(100, result.get(1).getStartTime());
        assertEquals(250, result.get(1).getEndTime());
        
        assertEquals(3, result.get(2).getTask().getId());
        assertEquals(250, result.get(2).getStartTime());
        assertEquals(330, result.get(2).getEndTime());
    }
}