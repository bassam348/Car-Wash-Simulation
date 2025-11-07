import java.util.Queue;

public class Car extends Thread {
    private final String id;
    private final Queue<Car> queue;
    private final Semaphore mutex;
    private final Semaphore empty;
    private final Semaphore full;
    private final int waitingAreaSize;

    public Car(String id, Queue<Car> queue, Semaphore mutex,
               Semaphore empty, Semaphore full, int waitingAreaSize) {
        this.id = id;
        this.queue = queue;
        this.mutex = mutex;
        this.empty = empty;
        this.full = full;
        this.waitingAreaSize = waitingAreaSize;
    }

    @Override
    public void run() {
        System.out.println(id + " arrived");

        synchronized (queue) {
            if (queue.size() >= waitingAreaSize) {
                System.out.println(id + " arrived and waiting");
            }
        }

        empty.waitSem();
        mutex.waitSem();
        queue.add(this);
        System.out.println(id + " enters the queue. Current queue size: " + queue.size());
        mutex.signal();
        full.signal();
    }

    public String getCarId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
