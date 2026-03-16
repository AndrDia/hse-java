package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.ArrayList;
import java.util.List;

public class Synchronizer {

    public static final int DEFAULT_TICKS_PER_WRITER = 10;
    private final List<StreamWriter> tasks;
    private final int ticksPerWriter;

    public Synchronizer(List<StreamWriter> tasks) {
        this(tasks, DEFAULT_TICKS_PER_WRITER);
    }

    public Synchronizer(List<StreamWriter> tasks, int ticksPerWriter) {
        this.tasks = tasks;
        this.ticksPerWriter = ticksPerWriter;
    }

    /**
     * Starts infinite writer threads and waits until each writer prints exactly ticksPerWriter ticks
     * in strict ascending id order.
     */
    public void execute() {
        // add monitor and sync
        StreamingMonitor monitor = new StreamingMonitor(tasks, ticksPerWriter);
        for (StreamWriter writer : tasks) {
            writer.attachMonitor(monitor);
        }

        List<Thread> workers = new ArrayList<>();
        for (StreamWriter writer : tasks) {
            Thread worker = new Thread(writer, "stream-writer-" + writer.getId());
            worker.setDaemon(true);
            worker.start();
            workers.add(worker);
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}