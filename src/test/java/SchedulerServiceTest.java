import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testFCFSstrategy_BasicOrdering() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 200, 50),
            new Task(3, 1, 150, 100)
        );
        Scheduler scheduler = new FCFSstrategy();
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
    void testFCFSstrategy_MultipleProcessors() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 200, 0),
            new Task(3, 1, 150, 0)
        );
        Scheduler scheduler = new FCFSstrategy();
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
    void testFCFSstrategy_ZeroLengthTask() {
        List<Task> tasks = List.of(
            new Task(1, 1, 0, 0),
            new Task(2, 1, 100, 0)
        );
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(0).getEndTime());
    }
    
    @Test
    void testFCFSstrategy_EmptyTaskList() {
        List<Task> tasks = new ArrayList<>();
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(0, result.size());
    }
    
    @Test
    void testFCFSstrategy_SingleTask() {
        List<Task> tasks = List.of(new Task(1, 1, 500, 100));
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(100, result.get(0).getStartTime());
        assertEquals(600, result.get(0).getEndTime());
    }
    
    @Test
    void testFCFSstrategy_TasksWithGaps() {
        // Test with tasks that arrive with significant gaps
        List<Task> tasks = List.of(
            new Task(1, 1, 50, 0),
            new Task(2, 1, 50, 200),
            new Task(3, 1, 50, 300)
        );
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
        assertEquals(200, result.get(1).getStartTime());
        assertEquals(250, result.get(1).getEndTime());
        assertEquals(300, result.get(2).getStartTime());
        assertEquals(350, result.get(2).getEndTime());
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
    void testSjfStrategy_EqualLengthTasks() {
        // Test tie-breaking by arrival time
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 100, 5),
            new Task(3, 1, 100, 3)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        // Should execute in order of arrival time when lengths are equal
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(3, result.get(1).getTask().getId());
        assertEquals(2, result.get(2).getTask().getId());
    }
    
    @Test
    void testSjfStrategy_MultipleProcessors() {
        List<Task> tasks = List.of(
            new Task(1, 1, 500, 0),
            new Task(2, 1, 200, 0),
            new Task(3, 1, 300, 0),
            new Task(4, 1, 100, 0)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 2);
        
        assertEquals(4, result.size());
        
        // Shortest jobs should be scheduled first
        assertEquals(4, result.get(0).getTask().getId()); // length 100
        assertEquals(2, result.get(1).getTask().getId()); // length 200
    }
    
    @Test
    void testSjfStrategy_ZeroLengthTasks() {
        List<Task> tasks = List.of(
            new Task(1, 1, 200, 0),
            new Task(2, 1, 0, 0),
            new Task(3, 1, 100, 0)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        // Zero length task should execute first
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(0).getEndTime());
    }

    // ===== PRIORITY STRATEGY TESTS =====
    
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
    void testPriorityStrategy_ZeroLengthTask() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 3, 0, 50)    // Zero length, high priority
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Task 1 runs for 50ms
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
        
        // Task 2 preempts immediately and completes (zero length)
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(50, result.get(1).getStartTime());
        assertEquals(50, result.get(1).getEndTime());
        
        // Task 1 resumes
        assertEquals(1, result.get(2).getTask().getId());
        assertEquals(50, result.get(2).getStartTime());
        assertEquals(100, result.get(2).getEndTime());
    }
    
    @Test
    void testPriorityStrategy_MultipleProcessorsError() {
        List<Task> tasks = List.of(new Task(1, 1, 100, 0));
        Scheduler scheduler = new PriorityStrategy();
        
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, tasks, 2));
    }
    
    @Test
    void testPriorityStrategy_EqualPriorityTieBreaker() {
        // Test tie-breaking by arrival time when priorities are equal
        List<Task> tasks = List.of(
            new Task(1, 2, 100, 0),
            new Task(2, 2, 100, 50),  // Same priority, later arrival
            new Task(3, 2, 100, 30)   // Same priority, middle arrival
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        // Should execute in order of arrival time when priorities are equal
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(3, result.get(1).getTask().getId());
        assertEquals(2, result.get(2).getTask().getId());
    }
    
    @Test
    void testPriorityStrategy_ComplexPreemption() {
        List<Task> tasks = List.of(
            new Task(1, 1, 300, 0),   // Low priority, starts first
            new Task(2, 3, 100, 50),  // High priority, preempts
            new Task(3, 2, 150, 100)  // Medium priority, waits
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertTrue(result.size() >= 3);
        
        // Task 1 starts first
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
        
        // Task 2 preempts (highest priority)
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(50, result.get(1).getStartTime());
        assertEquals(150, result.get(1).getEndTime());
    }
    
    @Test
    void testPriorityStrategy_NoPreemptionNeeded() {
        List<Task> tasks = List.of(
            new Task(1, 3, 100, 0),   // High priority, completes first
            new Task(2, 1, 200, 150)  // Low priority, arrives after completion
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
        
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(150, result.get(1).getStartTime());
        assertEquals(350, result.get(1).getEndTime());
    }

    // ===== ROUND ROBIN STRATEGY TESTS =====
    
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
    void testRoundRobinStrategy_ZeroLengthTasks() {
        List<Task> tasks = List.of(
            new Task(1, 1, 0, 0),  // zero length task
            new Task(2, 1, 100, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(50);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        
        // Zero length task should still be scheduled
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(0).getEndTime());
        
        // Task 2 gets merged execution (since no other tasks to interleave)
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(0, result.get(1).getStartTime());
        assertEquals(100, result.get(1).getEndTime());
    }
    
    @Test
    void testRoundRobinStrategy_MultipleProcessorsError() {
        List<Task> tasks = List.of(new Task(1, 1, 100, 0));
        
        assertThrows(IllegalArgumentException.class, () -> 
                new RoundRobinStrategy(50).schedule(tasks, 
                        List.of(new Processor(0), new Processor(1))));
    }
    
    @Test
    void testRoundRobinStrategy_InvalidQuantum() {
        assertThrows(IllegalArgumentException.class, () -> 
                new RoundRobinStrategy(0));
        
        assertThrows(IllegalArgumentException.class, () -> 
                new RoundRobinStrategy(-5));
    }
    
    @Test
    void testRoundRobinStrategy_ThreeTasks_ComplexInterleaving() {
        List<Task> tasks = List.of(
            new Task(1, 1, 150, 0),  // 3 rounds: 50 + 50 + 50
            new Task(2, 1, 75, 0),   // 2 rounds: 50 + 25
            new Task(3, 1, 25, 0)    // 1 round: 25
        );
        Scheduler scheduler = new RoundRobinStrategy(50);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(6, result.size());
        
        // First round: each task gets up to 50ms
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
        
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(50, result.get(1).getStartTime());
        assertEquals(100, result.get(1).getEndTime());
        
        assertEquals(3, result.get(2).getTask().getId());
        assertEquals(100, result.get(2).getStartTime());
        assertEquals(125, result.get(2).getEndTime());
    }
    
    @Test
    void testRoundRobinStrategy_LargeQuantum() {
        // Quantum larger than all task lengths - should behave like FCFS
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 150, 0),
            new Task(3, 1, 80, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(1000);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        
        // Tasks should complete in arrival order without preemption
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

    // ===== ROBUSTNESS AND INPUT VALIDATION TESTS =====
    
    @Test
    void testSchedulerService_NullStrategy() {
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(null, sampleTasks, 1));
    }
    
    @Test
    void testSchedulerService_NullTasksList() {
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, null, 1));
    }
    
    @Test
    void testSchedulerService_ZeroProcessors() {
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 0));
    }
    
    @Test
    void testSchedulerService_NegativeProcessors() {
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, -1));
    }
    
    @Test
    void testSchedulerService_NullTaskInList() {
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(new Task(1, 1, 100, 0));
        tasksWithNull.add(null);
        tasksWithNull.add(new Task(3, 1, 100, 0));
        
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, tasksWithNull, 1));
    }
    
    @Test
    void testSchedulerService_NegativeTaskId() {
        List<Task> tasks = List.of(new Task(-1, 1, 100, 0));
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, tasks, 1));
    }
    
    @Test
    void testSchedulerService_NegativeTaskLength() {
        List<Task> tasks = List.of(new Task(1, 1, -50, 0));
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, tasks, 1));
    }
    
    @Test
    void testSchedulerService_NegativeArrivalTime() {
        List<Task> tasks = List.of(new Task(1, 1, 100, -10));
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, tasks, 1));
    }
    
    @Test
    void testSchedulerService_NegativePriority() {
        List<Task> tasks = List.of(new Task(1, -1, 100, 0));
        Scheduler scheduler = new FCFSstrategy();
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(scheduler, tasks, 1));
    }

    // ===== STRESS AND PERFORMANCE TESTS =====
    
    @Test
    void testAllStrategies_LargeNumberOfTasks() {
        // Test with many tasks to ensure algorithms scale reasonably
        List<Task> largeTasks = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            largeTasks.add(new Task(i, i % 5 + 1, i * 10, i * 5));
        }
        
        // Test non-preemptive strategies first
        Scheduler[] nonPreemptiveStrategies = {
            new FCFSstrategy(),
            new SJFStrategy()
        };
        
        for (Scheduler strategy : nonPreemptiveStrategies) {
            List<ScheduledTask> result = SchedulerService.runScheduler(strategy, largeTasks, 3);
            assertEquals(20, result.size());
            assertNotNull(result);
            
            // Verify tasks are sorted by start time
            for (int i = 1; i < result.size(); i++) {
                assertTrue(result.get(i).getStartTime() >= result.get(i - 1).getStartTime());
            }
        }
        
        // Test preemptive strategies separately (may create more scheduled tasks)
        List<ScheduledTask> priorityResult = 
                SchedulerService.runScheduler(new PriorityStrategy(), largeTasks, 1);
        assertTrue(priorityResult.size() >= 20);
        
        List<ScheduledTask> rrResult = 
                SchedulerService.runScheduler(new RoundRobinStrategy(50), largeTasks, 1);
        assertTrue(rrResult.size() >= 20);
    }
    
    @Test
    void testAllStrategies_AllTasksAtTimeZero() {
        // All tasks arrive simultaneously
        List<Task> tasks = List.of(
            new Task(1, 3, 100, 0),
            new Task(2, 1, 200, 0),
            new Task(3, 2, 150, 0),
            new Task(4, 4, 50, 0)
        );
        
        // Test FCFS
        List<ScheduledTask> fcfsResult = 
                SchedulerService.runScheduler(new FCFSstrategy(), tasks, 1);
        assertEquals(4, fcfsResult.size());
        
        // Test SJF
        List<ScheduledTask> sjfResult = SchedulerService.runScheduler(new SJFStrategy(), tasks, 1);
        assertEquals(4, sjfResult.size());
        // Shortest job should be first
        assertEquals(4, sjfResult.get(0).getTask().getId());
        
        // Test Priority
        List<ScheduledTask> priorityResult = 
                SchedulerService.runScheduler(new PriorityStrategy(), tasks, 1);
        assertEquals(4, priorityResult.size());
        // Highest priority should be first
        assertEquals(4, priorityResult.get(0).getTask().getId());
        
        // Test Round Robin
        List<ScheduledTask> rrResult = 
                SchedulerService.runScheduler(new RoundRobinStrategy(75), tasks, 1);
        assertTrue(rrResult.size() >= 4);
    }

    // ===== BOUNDARY AND EDGE CASES =====
    
    @Test
    void testAllStrategies_SingleTaskZeroLength() {
        Task zeroTask = new Task(1, 1, 0, 0);
        List<Task> tasks = List.of(zeroTask);
        
        Scheduler[] strategies = {
            new FCFSstrategy(),
            new SJFStrategy(),
            new PriorityStrategy(),
            new RoundRobinStrategy(100)
        };
        
        for (Scheduler strategy : strategies) {
            int processors = (strategy instanceof PriorityStrategy
                            || strategy instanceof RoundRobinStrategy) ? 1 : 2;
            List<ScheduledTask> result = 
                    SchedulerService.runScheduler(strategy, tasks, processors);
            
            assertEquals(1, result.size());
            assertEquals(0, result.get(0).getStartTime());
            assertEquals(0, result.get(0).getEndTime());
        }
    }
    
    @Test
    void testAllStrategies_MaximumValues() {
        // Test with large but valid values
        List<Task> tasks = List.of(
            new Task(Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE / 2, Integer.MAX_VALUE)
        );
        
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(Integer.MAX_VALUE, result.get(0).getStartTime());
    }
    
    @Test
    void testSchedulerService_ValidBoundaryValues() {
        // Test with minimum valid values
        List<Task> tasks = List.of(new Task(0, 0, 0, 0));
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getTask().getPriority());
        assertEquals(0, result.get(0).getTask().getLength());
        assertEquals(0, result.get(0).getTask().getArrivalTime());
    }

    // ===== ADDITIONAL ROBUSTNESS TESTS =====
    
    @Test
    void testSchedulerService_DuplicateTaskIds() {
        // Test with duplicate task IDs (should be allowed per Task.equals implementation)
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(1, 2, 200, 50),  // Same ID, different attributes
            new Task(2, 1, 150, 100)
        );
        
        Scheduler scheduler = new FCFSstrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        // Both tasks with ID 1 should be scheduled
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(1, result.get(1).getTask().getId());
        assertEquals(2, result.get(2).getTask().getId());
    }
    
    @Test
    void testPriorityStrategy_EmptyProcessorList() {
        List<Task> tasks = List.of(new Task(1, 1, 100, 0));
        Scheduler scheduler = new PriorityStrategy();
        
        assertThrows(IllegalArgumentException.class, () -> 
                scheduler.schedule(tasks, new ArrayList<>()));
    }
    
    @Test
    void testRoundRobinStrategy_EmptyProcessorList() {
        List<Task> tasks = List.of(new Task(1, 1, 100, 0));
        Scheduler scheduler = new RoundRobinStrategy(50);
        
        assertThrows(IllegalArgumentException.class, () -> 
                scheduler.schedule(tasks, new ArrayList<>()));
    }
    
    @Test
    void testSchedulerService_VeryLargeTaskCounts() {
        // Test with larger numbers to stress test algorithms
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            tasks.add(new Task(i, (i % 10) + 1, (i % 50) + 10, i % 20));
        }
        
        // Test non-preemptive strategies with multiple processors
        Scheduler[] strategies = {new FCFSstrategy(), new SJFStrategy()};
        
        for (Scheduler strategy : strategies) {
            List<ScheduledTask> result = SchedulerService.runScheduler(strategy, tasks, 5);
            assertEquals(50, result.size());
            
            // Verify all tasks are scheduled
            for (int i = 0; i < 50; i++) {
                final int taskId = i;
                assertTrue(result.stream().anyMatch(st -> st.getTask().getId() == taskId));
            }
        }
    }
    
    @Test
    void testSchedulerService_ExtremelyLargePriorities() {
        List<Task> tasks = List.of(
            new Task(1, Integer.MAX_VALUE, 100, 0),
            new Task(2, 1, 200, 0),
            new Task(3, Integer.MAX_VALUE - 1, 150, 0)
        );
        
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertTrue(result.size() >= 3);
        // Highest priority task should be scheduled first
        assertEquals(1, result.get(0).getTask().getId());
    }
    
    @Test
    void testAllStrategies_AllZeroLengthTasks() {
        List<Task> tasks = List.of(
            new Task(1, 1, 0, 0),
            new Task(2, 2, 0, 10),
            new Task(3, 3, 0, 5)
        );
        
        Scheduler[] strategies = {
            new FCFSstrategy(),
            new SJFStrategy(),
            new PriorityStrategy(),
            new RoundRobinStrategy(50)
        };
        
        for (Scheduler strategy : strategies) {
            int processors = (strategy instanceof PriorityStrategy
                            || strategy instanceof RoundRobinStrategy) ? 1 : 2;
            List<ScheduledTask> result = 
                    SchedulerService.runScheduler(strategy, tasks, processors);
            
            assertTrue(result.size() >= 3);
            
            // All tasks should have zero execution time
            for (ScheduledTask st : result) {
                assertEquals(st.getStartTime(), st.getEndTime());
            }
        }
    }
    
    @Test
    void testSchedulerService_TasksWithIdenticalAttributes() {
        // Test with tasks that have identical properties except ID
        List<Task> tasks = List.of(
            new Task(1, 5, 100, 50),
            new Task(2, 5, 100, 50),
            new Task(3, 5, 100, 50)
        );
        
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        // Should execute in order by task ID due to arrival time tie-breaking
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(2, result.get(1).getTask().getId());
        assertEquals(3, result.get(2).getTask().getId());
    }
    
    @Test
    void testRoundRobinStrategy_VerySmallQuantum() {
        List<Task> tasks = List.of(
            new Task(1, 1, 1000, 0),
            new Task(2, 1, 800, 0)
        );
        
        // Very small quantum should create many context switches
        Scheduler scheduler = new RoundRobinStrategy(1);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        // Should have many scheduled task entries due to frequent switching
        assertTrue(result.size() > 10);
        
        // Verify total execution time is correct
        long totalTime1 = result.stream()
                .filter(st -> st.getTask().getId() == 1)
                .mapToLong(st -> st.getEndTime() - st.getStartTime())
                .sum();
        long totalTime2 = result.stream()
                .filter(st -> st.getTask().getId() == 2)
                .mapToLong(st -> st.getEndTime() - st.getStartTime())
                .sum();
                
        assertEquals(1000, totalTime1);
        assertEquals(800, totalTime2);
    }
}