import java.util.*;

package ServiceStation;

public class ServiceStation {

    private static Queue<Car> waitingQueue;
    private static Semaphore mutex;
    private static Semaphore empty;
    private static Semaphore full;

    private static int waitingAreaSize;
    private static int numberOfPumps;
    private static int numberOfCars;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of pumps: ");
        numberOfPumps = sc.nextInt();

        System.out.print("Enter waiting area size: ");
        waitingAreaSize = sc.nextInt();

        System.out.print("Enter number of cars: ");
        numberOfCars = sc.nextInt();

        waitingQueue = new LinkedList<>();
        mutex = new Semaphore(1);
        empty = new Semaphore(waitingAreaSize);
        full = new Semaphore(0);
        
        for (int i = 1; i <= numberOfPumps; i++) {
            Pump pump = new Pump(i, waitingQueue, mutex, empty, full);
            pump.start();
        }
        for (int i = 1; i <= numberOfCars; i++) {
            Car car = new Car("C" + i, waitingQueue, mutex, empty, full);
            car.start();


        }
    sc.close();
    }
}
