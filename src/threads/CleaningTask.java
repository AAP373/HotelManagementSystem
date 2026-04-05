package threads;

import model.Room;
import service.HotelManager;

/**
 * Week 3 – Thread creation by extending Thread class.
 * Week 4 – Synchronized update of room cleaning status.
 */
public class CleaningTask extends Thread {

    private final int         roomNumber;
    private final HotelManager manager;

    public CleaningTask(int roomNumber, HotelManager manager) {
        super("CleaningThread-Room-" + roomNumber);
        this.roomNumber = roomNumber;
        this.manager    = manager;
    }

    @Override
    public void run() {
        System.out.println("[Cleaning] Started for Room " + roomNumber);

        manager.updateRoomCleaningStatus(roomNumber, "In Progress");

        String[] steps = { "Changing bed linen", "Vacuuming floor", "Sanitizing bathroom", "Restocking amenities" };

        for (int i = 0; i < steps.length; i++) {
            System.out.printf("[Cleaning] Room %d – Step %d/%d: %s%n",
                    roomNumber, i + 1, steps.length, steps[i]);
            try {
                Thread.sleep(600);  // Week 3 – sleep()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Cleaning] Room " + roomNumber + " interrupted.");
                return;
            }
        }

        // Week 4 – synchronized update via HotelManager
        manager.updateRoomCleaningStatus(roomNumber, "Clean");
        manager.setRoomAvailable(roomNumber, true);
        System.out.println("[Cleaning] Room " + roomNumber + " is clean and available!");
    }
}
