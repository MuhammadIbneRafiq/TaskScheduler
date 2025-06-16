import org.junit.jupiter.api.Test;

import java.util.List;
// import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Scheduler Service.
 * This is testing the behaviour of the all the strategies.
 *
 * <!--//# BEGIN TODO: Name, student ID, and date-->
 * <p><b>Muhammad Rafiq, 1924214, 16th June 2025</b></p>
 * <!--//# END TODO-->
 */
public class SchedulerServiceTest {

    // private final List<Task> sampleTasks = List.of(
    //         new Task(1, 1, 1000, 0),
    //         new Task(2, 3, 500, 2),
    //         new Task(3, 2, 800, 4)
    // );

    // ===== SchedulerService.runScheduler() - 2 tests =====
    
    @Test
    void testRunScheduler_ValidInput() {
        List<Task> tasks = List.of(new Task(1, 1, 100, 0));
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getTask().getId());
    }
    
    @Test
    void testRunScheduler_NullStrategy() {
        List<Task> tasks = List.of(new Task(1, 1, 100, 0));
        assertThrows(IllegalArgumentException.class, () -> 
                SchedulerService.runScheduler(null, tasks, 1));
    }

    // ===== FCFSStrategy.schedule() - 2 tests =====
    
    @Test
    void testFcfsStrategy_BasicOrdering() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 200, 50)
        );
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
    }
    
    @Test
    void testFcfsStrategy_MultipleProcessors() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 1, 200, 0)
        );
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 2);
        
        assertEquals(2, result.size());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(1).getStartTime());
    }

    // ===== SJFStrategy.schedule() - 2 tests =====
    
    @Test
    void testSjfStrategy_BasicShortest() {
        List<Task> tasks = List.of(
            new Task(1, 1, 300, 0),
            new Task(2, 1, 100, 0)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
    }
    
    @Test
    void testSjfStrategy_WithArrivalTimes() {
        List<Task> tasks = List.of(
            new Task(1, 1, 400, 0),
            new Task(2, 1, 100, 200)
        );
        Scheduler scheduler = new SJFStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
    }

    // ===== PriorityStrategy.schedule() - 2 tests =====
    
    @Test
    void testPriorityStrategy_BasicPreemption() {
        List<Task> tasks = List.of(
            new Task(1, 1, 500, 0),
            new Task(2, 3, 200, 100)
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(100, result.get(0).getEndTime());
    }
    
    @Test
    void testPriorityStrategy_HigherPriorityFirst() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 5, 100, 0)
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
    }

    // ===== RoundRobinStrategy constructor - 2 tests =====
    
    @Test
    void testRoundRobinConstructor_ValidQuantum() {
        RoundRobinStrategy scheduler = new RoundRobinStrategy(100);
        List<Task> tasks = List.of(new Task(1, 1, 50, 0));
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
    }
    
    @Test
    void testRoundRobinConstructor_InvalidQuantum() {
        assertThrows(IllegalArgumentException.class, () -> 
                new RoundRobinStrategy(0));
    }

    // ===== RoundRobinStrategy.schedule() - 2 tests =====
    
    @Test
    void testRoundRobinStrategy_SingleTask() {
        List<Task> tasks = List.of(new Task(1, 1, 300, 0));
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(300, result.get(0).getEndTime());
    }
    
    @Test
    void testRoundRobinStrategy_TwoTasks() {
        List<Task> tasks = List.of(
            new Task(1, 1, 700, 0),
            new Task(2, 1, 400, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(500);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(500, result.get(0).getEndTime());
    }

    // ===== Zero-length task tests - 3 tests =====
    
    @Test
    void testFcfsStrategy_ZeroLengthTask() {
        List<Task> tasks = List.of(
            new Task(1, 1, 0, 0),
            new Task(2, 1, 100, 0)
        );
        Scheduler scheduler = new FCFSStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(0).getEndTime());
    }

    @Test
    void testPriorityStrategy_ZeroLengthTask() {
        List<Task> tasks = List.of(
            new Task(1, 1, 100, 0),
            new Task(2, 3, 0, 50)
        );
        Scheduler scheduler = new PriorityStrategy();
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(50, result.get(0).getEndTime());
    }

    @Test
    void testRoundRobinStrategy_ZeroLengthTasks() {
        List<Task> tasks = List.of(
            new Task(1, 1, 0, 0),
            new Task(2, 1, 100, 0)
        );
        Scheduler scheduler = new RoundRobinStrategy(50);
        List<ScheduledTask> result = SchedulerService.runScheduler(scheduler, tasks, 1);
        
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTask().getId());
        assertEquals(0, result.get(0).getStartTime());
        assertEquals(0, result.get(0).getEndTime());
    }
}