package service;

import model.*;
import threads.CleaningTask;
import threads.LaundryTask;

import java.util.*;

/**
 * Central hotel manager.
 * Week 4  – synchronized methods to prevent race conditions.
 * Week 8  – HashMap, ArrayList, Iterator, Collections.sort().
 * Week 4  – wait()/notify() via notifyLaundryComplete().
 */
public class HotelManager {

    // Week 8 – HashMap: roomNumber → Room
    private final Map<Integer, Room>     rooms     = new HashMap<>();
    // Week 8 – HashMap: customerId → Customer
    private final Map<Integer, Customer> customers = new HashMap<>();
    // Week 8 – ArrayList of bookings
    private final List<Booking>          bookings  = new ArrayList<>();
    // Week 8 – ArrayList of service items
    private final List<ServiceItem>      services  = new ArrayList<>();

    // Counters
    private int customerCounter = 1;
    private int bookingCounter  = 1;
    private int serviceCounter  = 1;

    // Set of completed laundry IDs (Week 4 – shared state across threads)
    private final Set<Integer> completedLaundry = new HashSet<>();

    // ════════════════════════════════════════════════════════════════════════
    //  ROOM MANAGEMENT
    // ════════════════════════════════════════════════════════════════════════

    public synchronized void addRoom(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }

    public synchronized Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    public synchronized List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public synchronized List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        // Week 8 – Iterator
        Iterator<Room> it = rooms.values().iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    /** Week 8 – sort rooms by price using Collections.sort() + Comparator. */
    public List<Room> getRoomsSortedByPrice() {
        List<Room> sorted = new ArrayList<>(rooms.values());
        Collections.sort(sorted,
                Comparator.comparingInt(r -> r.getRoomType().getPricePerNight()));
        return sorted;
    }

    /** Week 4 – synchronized room status update used by CleaningTask. */
    public synchronized void updateRoomCleaningStatus(int roomNumber, String status) {
        Room r = rooms.get(roomNumber);
        if (r != null) r.setCleaningStatus(status);
    }

    public synchronized void setRoomAvailable(int roomNumber, boolean available) {
        Room r = rooms.get(roomNumber);
        if (r != null) r.setAvailable(available);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CUSTOMER MANAGEMENT
    // ════════════════════════════════════════════════════════════════════════

    public synchronized Customer addCustomer(String name, String phone, String email) {
        Customer c = new Customer(customerCounter++, name, phone, email);
        customers.put(c.getCustomerId(), c);
        return c;
    }

    public synchronized Customer getCustomer(int id) {
        return customers.get(id);
    }

    public synchronized List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BOOKING MANAGEMENT  (Week 4 – synchronized to prevent double-booking)
    // ════════════════════════════════════════════════════════════════════════

    public synchronized boolean bookRoom(int customerId, int roomNumber, int nights) {
        Room     room     = rooms.get(roomNumber);
        Customer customer = customers.get(customerId);

        if (room == null || customer == null) return false;
        if (!room.isAvailable()) return false;

        // Mark room occupied
        room.setAvailable(false);
        room.setCleaningStatus("Occupied");

        // Create booking
        Booking b = new Booking(bookingCounter++, customerId, roomNumber, nights);
        b.setRoomCost(room.calculateTariff(nights));
        bookings.add(b);

        customer.setAllocatedRoom(roomNumber);
        return true;
    }

    public synchronized boolean checkout(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null || room.isAvailable()) return false;

        // Find active booking
        Booking active = bookings.stream()
                .filter(b -> b.getRoomNumber() == roomNumber && b.getStatus() == Booking.Status.ACTIVE)
                .findFirst().orElse(null);

        if (active != null) {
            active.setStatus(Booking.Status.CHECKED_OUT);
            // Reset customer's room
            Customer c = customers.get(active.getCustomerId());
            if (c != null) c.setAllocatedRoom(-1);
        }

        // Trigger cleaning thread (Week 3)
        room.setCleaningStatus("Needs Cleaning");
        new CleaningTask(roomNumber, this).start();

        return true;
    }

    public synchronized List<Booking> getAllBookings() { return new ArrayList<>(bookings); }

    public synchronized Booking getActiveBookingForRoom(int roomNumber) {
        return bookings.stream()
                .filter(b -> b.getRoomNumber() == roomNumber && b.getStatus() == Booking.Status.ACTIVE)
                .findFirst().orElse(null);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LAUNDRY SERVICE  (Week 3 – spawns LaundryTask thread)
    // ════════════════════════════════════════════════════════════════════════

    public synchronized LaundryService requestLaundry(int roomNumber, int itemCount) {
        Room room = rooms.get(roomNumber);
        if (room == null || room.isAvailable()) return null;

        LaundryService ls = new LaundryService(serviceCounter++, roomNumber, itemCount);
        services.add(ls);

        // Start background thread (Week 3 – Runnable)
        Thread t = new Thread(new LaundryTask(ls, this), "LaundryThread-" + ls.getServiceId());
        t.setDaemon(true);
        t.start();

        return ls;
    }

    /** Week 4 – notify pattern: called by LaundryTask when done. */
    public synchronized void notifyLaundryComplete(int serviceId) {
        completedLaundry.add(serviceId);
        notifyAll();   // Week 4 – notifyAll()
    }

    public synchronized boolean isLaundryComplete(int serviceId) {
        return completedLaundry.contains(serviceId);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LATE CHECKOUT
    // ════════════════════════════════════════════════════════════════════════

    public synchronized LateCheckoutService addLateCheckout(int roomNumber, int extraHours) {
        Room room = rooms.get(roomNumber);
        if (room == null || room.isAvailable()) return null;

        LateCheckoutService lcs = new LateCheckoutService(serviceCounter++, roomNumber, extraHours);
        services.add(lcs);
        return lcs;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SERVICES
    // ════════════════════════════════════════════════════════════════════════

    public synchronized List<ServiceItem> getAllServices() { return new ArrayList<>(services); }

    public synchronized List<ServiceItem> getServicesForRoom(int roomNumber) {
        List<ServiceItem> result = new ArrayList<>();
        for (ServiceItem s : services) {
            if (s.getRoomNumber() == roomNumber) result.add(s);
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SEED DATA (default rooms on first launch)
    // ════════════════════════════════════════════════════════════════════════

    public void seedDefaultRooms() {
        if (!rooms.isEmpty()) return;
        addRoom(new StandardRoom(101, false));
        addRoom(new StandardRoom(102, true));
        addRoom(new StandardRoom(103, true));
        addRoom(new DeluxeRoom(201, true, false));
        addRoom(new DeluxeRoom(202, true, true));
        addRoom(new DeluxeRoom(203, true, true));
        addRoom(new DeluxeRoom(301, true, true, true));
        addRoom(new DeluxeRoom(302, true, true, true));
    }
}
