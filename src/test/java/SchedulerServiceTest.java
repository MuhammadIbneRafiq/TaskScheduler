import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    // TODO: Add more tests for the FCS Strategy.
    // TODO: Remove the test below (it will fail after you have implemented the method)
    @Test
    void testFCFSStrategy() {
        Scheduler scheduler = new FCFSStrategy();
        assertThrows(UnsupportedOperationException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 2));
    }

    // TODO: Add more tests for the SJF Strategy
    // TODO: Remove the test below (it will fail after you have implemented the method)
    @Test
    void testSJFStrategy() {
        Scheduler scheduler = new SJFStrategy();
        assertThrows(UnsupportedOperationException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 2));
    }

    // TODO: Add more tests for the Priority Strategy
    // TODO: Remove the test below (it will fail after you have implemented the method)
    @Test
    void testPriorityStrategy() {
        Scheduler scheduler = new PriorityStrategy();
        assertThrows(UnsupportedOperationException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 3));
    }

    // TODO: Add more tests for the Round Robin Strategy
    // TODO: Remove the test below (it will fail after you have implemented the method)
    @Test
    void testRoundRobinStrategy() {
        Scheduler scheduler = new RoundRobinStrategy(500);
        assertThrows(UnsupportedOperationException.class, () -> 
                SchedulerService.runScheduler(scheduler, sampleTasks, 1));
    }
}