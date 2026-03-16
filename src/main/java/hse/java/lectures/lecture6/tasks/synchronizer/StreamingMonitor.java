package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.List;

public class StreamingMonitor {
    private int currentWriter;
    private int ticksPerWriter;
    private int totalWriters;
    private int[] ticks;
    private int total;

    StreamingMonitor(List<StreamWriter> tasks, int ticksPerWriter) {
        this.ticksPerWriter = ticksPerWriter;
        this.totalWriters = tasks.size();
        this.ticks = new int[totalWriters + 1];
        this.total = 0;
        this.currentWriter = 1;
    }

    synchronized boolean checkTurn(int writer) throws InterruptedException {
        while (writer != currentWriter && total < totalWriters * ticksPerWriter) {
            wait();
        }
        return total < totalWriters * ticksPerWriter;
    }

    synchronized void TickFinished() {
        ticks[currentWriter]++;
        total++;

        if (total == totalWriters * ticksPerWriter) {
            notifyAll();
            return;
        }

        int next = currentWriter % totalWriters + 1;

        while (ticks[next] >= ticksPerWriter) {
            next = next % totalWriters + 1;
        }
        currentWriter = next;

        notifyAll();
    }
}
