package threads;

import model.Booking;
import service.HotelManager;

/**
 * Week 3 – Runnable implementation.
 * Week 4 – Synchronized booking to prevent double-booking race conditions.
 */
public class BookingTask implements Runnable {

    private final int         customerId;
    private final int         roomNumber;
    private final int         nights;
    private final HotelManager manager;

    public BookingTask(int customerId, int roomNumber, int nights, HotelManager manager) {
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.nights     = nights;
        this.manager    = manager;
    }

    @Override
    public void run() {
        String tName = Thread.currentThread().getName();
        System.out.printf("[BookingTask] %s attempting to book Room %d for Customer %d%n",
                tName, roomNumber, customerId);

        // Week 4 – synchronized booking inside HotelManager prevents race conditions
        boolean success = manager.bookRoom(customerId, roomNumber, nights);

        if (success) {
            System.out.printf("[BookingTask] %s – Room %d booked successfully.%n", tName, roomNumber);
        } else {
            System.out.printf("[BookingTask] %s – Room %d unavailable. Booking failed.%n", tName, roomNumber);
        }
    }
}
