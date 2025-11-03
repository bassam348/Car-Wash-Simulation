package Pump;
class Pump implements Runnable {
    private int pumpId;
    private boolean running = true;
    private Garage  garage;    // Shared area for cars waiting for service (buffer)
    private Semaphore pumps;  // Semaphore to control access to service bays (pumps)
    
    public Pump(int pumpId, Garage garage, Semaphore pumps) {
        this.pumpId = pumpId;
        this.Garage = garage;
        this.pumps = pumps;
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                // checking a pump before serving a car
                pumps.P(); // Wait for an available pump
                System.out.println("Pump " + pumpId + ": Available and waiting for car");
                
                // Consume a car from the GArage
                Car car = Garage.consume();
                
                if (car != null) {
                    System.out.println("Pump " + pumpId + ": " + car.getId() + " Occupied");
                    System.out.println("Pump " + pumpId + ": " + car.getId() + " login");
                    System.out.println("Pump " + pumpId + ": " + car.getId() + " begins service at pump " + pumpId);
                    
                    Thread.sleep((int)(Math.random() * 3000));
                    
                    System.out.println("Pump " + pumpId + ": " + car.getid() + " finishes service");
                    System.out.println("Pump " + pumpId + ": Pump " + pumpId + " is now free");
                }
                // noyify(awake)
                pumps.V();
                
            } catch (InterruptedException e) {
                System.out.println("Pump " + pumpId + " interrupted");
                break;
            }
        }
        System.out.println("Pump " + pumpId + " shutting down");
    } 
      public void stop() {
        running = false;
    }
}
