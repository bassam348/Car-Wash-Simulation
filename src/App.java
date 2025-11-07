import java.util.*;

// ==================== Semaphore Class ====================
class Semaphore {
    private int value;
    
    public Semaphore(int value) {
        this.value = value;
    }
    
    public synchronized void waitSem() {
        while (value <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        value--;
    }
    
    public synchronized void signal() {
        value++;
        notify();
    }
}

// ==================== Car Class (Producer) ====================
class Car extends Thread {
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
        
        // Check if waiting area is full
        synchronized (queue) {
            if (queue.size() >= waitingAreaSize) {
                System.out.println(id + " arrived and waiting");
            }
        }
        
        // Wait for space in waiting area
        empty.waitSem();
        
        // Enter critical section
        mutex.waitSem();
        queue.add(this);
        System.out.println(id + " enters the queue. Current queue size: " + queue.size());
        mutex.signal();
        
        // Signal that there's a car available for service
        full.signal();
    }
    
    public String getCarId() {
        return id;
    }
    
    @Override
    public String toString() {
        return id;
    }
}

// ==================== Pump Class (Consumer) ====================
class Pump extends Thread {
    private final int pumpId;
    private final Queue<Car> queue;
    private final Semaphore mutex;
    private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore pumps;
    private volatile boolean running = true;
    
    public Pump(int pumpId, Queue<Car> queue, Semaphore mutex,
                Semaphore empty, Semaphore full, Semaphore pumps) {
        this.pumpId = pumpId;
        this.queue = queue;
        this.mutex = mutex;
        this.empty = empty;
        this.full = full;
        this.pumps = pumps;
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                // Wait for a car to be available
                full.waitSem();
                
                // Acquire service bay (pump)
                pumps.waitSem();
                
                // Enter critical section to get car from queue
                mutex.waitSem();
                Car car = queue.poll();
                mutex.signal();
                
                // Signal that there's space in waiting area
                empty.signal();
                
                if (car != null) {
                    System.out.println("Pump " + pumpId + ": " + car.getCarId() + " Occupied");
                    System.out.println("Pump " + pumpId + ": " + car.getCarId() + " login");
                    System.out.println("Pump " + pumpId + ": " + car.getCarId() + " begins service at Bay " + pumpId);
                    
                    // Simulate service time
                    Thread.sleep((int)(Math.random() * 3000) + 2000);
                    
                    System.out.println("Pump " + pumpId + ": " + car.getCarId() + " finishes service");
                    System.out.println("Pump " + pumpId + ": Bay " + pumpId + " is now free");
                }
                
                // Release service bay
                pumps.signal();
                
            } catch (InterruptedException e) {
                System.out.println("Pump " + pumpId + " interrupted");
                break;
            }
        }
        System.out.println("Pump " + pumpId + " shutting down");
    }
    
    public void stopPump() {
        running = false;
        this.interrupt();
    }
}

// ==================== Main Application ====================
public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Input parameters
        System.out.print("Enter waiting area capacity (1-10): ");
        int waitingAreaSize = sc.nextInt();
        
        System.out.print("Enter number of service bays (pumps): ");
        int numberOfPumps = sc.nextInt();
        
        System.out.print("Enter number of cars: ");
        int numberOfCars = sc.nextInt();
        
        // Validate input
        if (waitingAreaSize < 1 || waitingAreaSize > 10) {
            System.out.println("Error: Waiting area must be between 1 and 10");
            sc.close();
            return;
        }
        
        // Initialize shared resources
        Queue<Car> waitingQueue = new LinkedList<>();
        Semaphore mutex = new Semaphore(1);
        Semaphore empty = new Semaphore(waitingAreaSize);
        Semaphore full = new Semaphore(0);
        Semaphore pumps = new Semaphore(numberOfPumps);
        
        System.out.println("\n=== Car Wash and Gas Station Simulation Started ===\n");
        
        // Create and start pump threads
        List<Pump> pumpList = new ArrayList<>();
        for (int i = 1; i <= numberOfPumps; i++) {
            Pump pump = new Pump(i, waitingQueue, mutex, empty, full, pumps);
            pumpList.add(pump);
            pump.start();
        }
        
        // Create and start car threads with slight delays
        List<Car> carList = new ArrayList<>();
        for (int i = 1; i <= numberOfCars; i++) {
            Car car = new Car("C" + i, waitingQueue, mutex, empty, full, waitingAreaSize);
            carList.add(car);
            car.start();
            
            try {
                Thread.sleep(500); // Delay between car arrivals
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Wait for all cars to finish
        for (Car car : carList) {
            try {
                car.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Wait a bit for pumps to finish processing
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Stop all pumps
        for (Pump pump : pumpList) {
            pump.stopPump();
        }
        
        System.out.println("\n=== All cars processed; simulation ends ===");
        
        sc.close();
    }
}
