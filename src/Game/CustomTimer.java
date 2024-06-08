package Game;

public class CustomTimer implements Runnable {
    private final int delay;
    private final Runnable task;
    private boolean running;

    public CustomTimer(int delay, Runnable task) {
        this.delay = delay;
        this.task = task;
        this.running = true;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(delay);
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

