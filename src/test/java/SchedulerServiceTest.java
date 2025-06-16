import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Scheduler Service.
 * This is testing the behaviour of the all the strategies.
 *
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class SchedulerServiceTest {

    private final List<Task> sampleTasks = List.of(
            new Task(1, 1, 1000, 0),
            new Task(2, 3, 500, 2),
            new Task(3, 2, 800, 4)
    );

    // ===== FCFS STRATEGY TESTS =====
    
    @Test
    void testFcfsStrategy_BasicOrdering() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 200, 50),
            new Task(3, 1, 150, 100)
        );
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Tasks should be scheduled in order of arrival
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
        
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(100, result.get(1).getStartTime());
        assertEquals(300, result.get(1).getEndTime());
        
        assertEquals(3, result.get(2).getTask().getId());
        assertEquals(300, result.get(2).getStartTime());
        assertEquals(450, result.get(2).getEndTime());
    }
    
    @Test
    void testFcfsStrategy_MultipleProcessors() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 200, 0),
            new Task(3, 1, 150, 0)
        );
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 2);
        
        assertEquals(3, result.size());
        
        // First two tasks should start immediately on different processors
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(1).getStartTime());
        
        // Third task starts when first processor becomes free
        assertEquals(100, result.get(2).getStartTime());
        assertEquals(250, result.get(2).getEndTime());
    }
    
    @Test
    void testFcfsStrategy_EmptyTasks() {
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, new ArrayList<>(), 1);
        assertTrue(result.isEmpty());
    }
    


    // ===== SJF STRATEGY TESTS =====
    
    @Test
    void testSjfStrategy_BasicShortest() {
        List<Task> tasks = List.of(
            new Task(1, 1, 300, 0),
            new Task(2, 1, 100, 0),
            new Task(3, 1, 200, 0)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Should run in order: task2(100), task3(200), task1(300)
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
        
        assertEquals(3, result.get(1).getTask().getId());
        assertEquals(100, result.get(1).getStartTime());
        assertEquals(300, result.get(1).getEndTime());
        
        assertEquals(1, result.get(2).getTask().getId());
        assertEquals(300, result.get(2).getStartTime());
        assertEquals(600, result.get(2).getEndTime());
    }
    
    @Test
    void testSjfStrategy_WithArrivalTimes() {
        List<Task> tasks = List.of(
            new Task(1, 1, 400, 0),
            new Task(2, 1, 100, 200),
            new Task(3, 1, 300, 100)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Task 1 starts first (only available), then when task 3 arrives, it's shorter
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(400, result.get(0).getEndTime());
        
        // Task 2 is shortest among remaining
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(400, result.get(1).getStartTime());
        assertEquals(500, result.get(1).getEndTime());
    }
    
    @Test
    void testSjfStrategy_TieBreaker() {
        // Same length tasks should use FCFS as tie-breaker
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 10),
            new Task(2, 1, 100, 0),
            new Task(3, 1, 100, 5)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Should run in arrival order: task2(0), task3(5), task1(10)
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(3, result.get(1).getTask().getId());
        assertEquals(1, result.get(2).getTask().getId());
    }

    // ===== PRIORITY STRATEGY TESTS =====
    
    @Test
    void testPriorityStrategy_MultipleProcessors_ThrowsException() {
        Scheduler scheduler = new PriorityStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 2));
    }
    
    @Test
    void testPriorityStrategy_BasicPreemption() {
        List<Task> tasks = List.of(
            new Task(1, 1, 500, 0),  // Low priority, long task
            new Task(2, 3, 200, 100) // High priority, arrives later
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Task 1 runs first for 100ms
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
        
        // Task 2 preempts and completes
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(100, result.get(1).getStartTime());
        assertEquals(300, result.get(1).getEndTime());
        
        // Task 1 resumes and completes
        assertEquals(1, result.get(2).getTask().getId());
        assertEquals(300, result.get(2).getStartTime());
        assertEquals(700, result.get(2).getEndTime());
    }
    
    @Test
    void testPriorityStrategy_NoPreemption() {
        List<Task> tasks = List.of(
            new Task(1, 3, 200, 0),  // High priority
            new Task(2, 1, 100, 50)  // Low priority, arrives later
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        
        // High priority task runs first and completes
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(200, result.get(0).getEndTime());
        
        // Low priority task runs after
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(200, result.get(1).getStartTime());
        assertEquals(300, result.get(1).getEndTime());
    }
    
    @Test
    void testPriorityStrategy_PriorityTieBreaker() {
        // Same priority tasks should use arrival time as tie-breaker
        List<Task> tasks = List.of(
            new Task(1, 2, 300, 10),
            new Task(2, 2, 200, 0),
            new Task(3, 2, 100, 5)
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Should run in arrival order: task2(0), task3(5), task1(10)
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(3, result.get(1).getTask().getId());
        assertEquals(1, result.get(2).getTask().getId());
    }
    


    // ===== ROUND ROBIN STRATEGY TESTS =====
    
    @Test
    void testRoundRobinStrategy_MultipleProcessors_ThrowsException() {
        Scheduler scheduler = new RoundRobinStrategy(500);
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 2));
    }
    
    @Test
    void testRoundRobinStrategy_EmptyTaskList() {
        Scheduler scheduler = new RoundRobinStrategy(100);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, new ArrayList<>(), 1);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testRoundRobinStrategy_SingleTask_WithinQuantum() {
        // Task completes within quantum
        List<Task> tasks = List.of(new Task(1, 1, 300, 0));
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        ScheduledTask scheduled = result.get(0);
        assertEquals(1, scheduled.getTask().getId());
        assertEquals(0, scheduled.getStartTime());
        assertEquals(300, scheduled.getEndTime());
        assertEquals(0, scheduled.getProcessorId());
    }
    
    @Test
    void testRoundRobinStrategy_SingleTask_ExceedsQuantum() {
        // Task needs multiple rounds - but since it's alone, should be merged
        List<Task> tasks = List.of(new Task(1, 1, 700, 0));
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        ScheduledTask scheduled = result.get(0);
        assertEquals(1, scheduled.getTask().getId());
        assertEquals(0, scheduled.getStartTime());
        assertEquals(700, scheduled.getEndTime());
        assertEquals(0, scheduled.getProcessorId());
    }
    
    @Test
    void testRoundRobinStrategy_TwoTasks_BasicRoundRobin() {
        // Test basic round robin behavior with two tasks
        List<Task> tasks = List.of(
            new Task(1, 1, 700, 0),  // needs 2 rounds: 500 + 200
            new Task(2, 1, 400, 0)   // needs 1 round: 400
        );
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // First: Task 1 gets 500ms
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(500, result.get(0).getEndTime());
        
        // Second: Task 2 gets 400ms (completes)
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(500, result.get(1).getStartTime());
        assertEquals(900, result.get(1).getEndTime());
        
        // Third: Task 1 gets remaining 200ms
        assertEquals(1, result.get(2).getTask().getId());
        assertEquals(900, result.get(2).getStartTime());
        assertEquals(1100, result.get(2).getEndTime());
    }
    
    @Test
    void testRoundRobinStrategy_TasksWithDifferentArrivalTimes() {
        // Test from assignment: task1(length=7, arrival=1), task2(length=4, arrival=0), quantum=5
        List<Task> tasks = List.of(
            new Task(1, 1, 7, 1),
            new Task(2, 1, 4, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(5);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        
        // Task 2 runs first (arrives at time 0), completes in 4ms
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(4, result.get(0).getEndTime());
        
        // Task 1 runs after task 2 completes (starts at time 4), runs for 7ms
        assertEquals(1, result.get(1).getTask().getId());
        assertEquals(4, result.get(1).getStartTime());
        assertEquals(11, result.get(1).getEndTime());
    }
    
    @Test
    void testRoundRobinStrategy_ConsecutiveTaskMerging() {
        // Test that consecutive executions of same task are merged
        // Single task that takes longer than quantum should result in single ScheduledTask
        List<Task> tasks = List.of(new Task(1, 1, 1500, 0));
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(1500, result.get(0).getEndTime());
    }
    
    @Test
    void testRoundRobinStrategy_ComplexScenario() {
        // Multiple tasks with different arrival times and lengths
        List<Task> tasks = List.of(
            new Task(1, 1, 800, 0),   // 800ms, arrives at 0
            new Task(2, 1, 300, 100), // 300ms, arrives at 100
            new Task(3, 1, 200, 500)  // 200ms, arrives at 500
        );
        Scheduler scheduler = new RoundRobinStrategy(250);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        // Verify the scheduling follows round robin with proper arrival times
        assertTrue(result.size() >= 4); // Should have multiple execution segments
        assertEquals(1, result.get(0).getTask().getId()); // Task 1 starts first
        assertEquals(0, result.get(0).getStartTime());
    }
    
    @Test
    void testRoundRobinStrategy_LargeQuantum() {
        // When quantum is larger than all task lengths, should behave like FCFS
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 150, 0),
            new Task(3, 1, 80, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(1000);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Should run in arrival order (FCFS behavior)
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

    // ===== ROBUSTNESS TESTS =====
    
    @Test
    void testSchedulerService_NullStrategy() {
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(null, sampleTasks, 1));
    }
    
    @Test
    void testSchedulerService_NullTasks() {
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, null, 1));
    }
    
    @Test
    void testSchedulerService_ZeroProcessors() {
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 0));
    }
    
    @Test
    void testSchedulerService_NegativeProcessors() {
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, -1));
    }
    
    @Test
    void testSchedulerService_TaskWithNegativeArrivalTime() {
        List<Task> invalidTasks = List.of(new Task(1, 1, 100, -5));
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, invalidTasks, 1));
    }
    
    @Test
    void testSchedulerService_TaskWithNegativeLength() {
        List<Task> invalidTasks = List.of(new Task(1, 1, -100, 0));
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, invalidTasks, 1));
    }
    
    @Test
    void testSchedulerService_TaskWithNegativeId() {
        List<Task> invalidTasks = List.of(new Task(-1, 1, 100, 0));
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, invalidTasks, 1));
    }
    
    @Test
    void testSchedulerService_TaskWithNegativePriority() {
        List<Task> invalidTasks = List.of(new Task(1, -1, 100, 0));
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, invalidTasks, 1));
    }
    
    @Test
    void testSchedulerService_EmptyTaskList() {
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, new ArrayList<>(), 1);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testRoundRobinStrategy_InvalidQuantum() {
        assertThrows(IllegalArgumentException.class, () -> new RoundRobinStrategy(0));
        assertThrows(IllegalArgumentException.class, () -> new RoundRobinStrategy(-1));
    }
}