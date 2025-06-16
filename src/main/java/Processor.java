
import java.util.ArrayList;
import java.util.List;

/**
 * Class for a processor.
 * @author Ferit Ismailov
 * @date 20.05.2025
 */
public class Processor {
    private final int id;
    private final List<ScheduledTask> schedule = new ArrayList<>();

    public Processor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<ScheduledTask> getSchedule() {
        return schedule;
    }

    public void assign(ScheduledTask st) {
        schedule.add(st);
    }
}