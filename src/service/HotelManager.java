package service;

import model.*;
import threads.CleaningTask;
import threads.LaundryTask;

import java.util.*;

/**
 * Week 4  – synchronized methods preventing race conditions.
 * Week 8  – HashMap, ArrayList, Iterator, Collections.sort().
 */
public class HotelManager {

    private final Map<Integer, Room>     rooms     = new HashMap<>();
    private final Map<Integer, Customer> customers = new HashMap<>();
    private final List<Booking>          bookings  = new ArrayList<>();
    private final List<ServiceItem>      services  = new ArrayList<>();
    private final Set<Integer>           completedLaundry = new HashSet<>();

    private int customerCounter = 1;
    private int bookingCounter  = 1;
    private int serviceCounter  = 1;

    // ════════════════════════════════════════════════════════════════════════
    //  LOAD persisted data (called from HotelManagementApp on startup)
    // ════════════════════════════════════════════════════════════════════════

    public synchronized void loadAll() {
        // Rooms
        FileManager.loadRooms().forEach(r -> rooms.put(r.getRoomNumber(), r));

        // Customers – restore counter so new IDs don't clash
        List<Customer> savedCustomers = FileManager.loadCustomers();
        for (Customer c : savedCustomers) {
            customers.put(c.getCustomerId(), c);
            if (c.getCustomerId() >= customerCounter)
                customerCounter = c.getCustomerId() + 1;
        }

        // Bookings – restore counter + re-mark occupied rooms
        List<Booking> savedBookings = FileManager.loadBookings();
        for (Booking b : savedBookings) {
            bookings.add(b);
            if (b.getBookingId() >= bookingCounter)
                bookingCounter = b.getBookingId() + 1;
            if (b.getStatus() == Booking.Status.ACTIVE) {
                Room r = rooms.get(b.getRoomNumber());
                if (r != null) { r.setAvailable(false); r.setCleaningStatus("Occupied"); }
            }
        }

        // Services
        List<ServiceItem> savedServices = FileManager.loadServices();
        for (ServiceItem s : savedServices) {
            services.add(s);
            if (s.getServiceId() >= serviceCounter)
                serviceCounter = s.getServiceId() + 1;
        }

        // Seed default rooms only if nothing was loaded
        if (rooms.isEmpty()) seedDefaultRooms();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAVE all data (called on app close)
    // ════════════════════════════════════════════════════════════════════════

    public synchronized void saveAll() {
        FileManager.saveRooms(new ArrayList<>(rooms.values()));
        FileManager.saveCustomers(new ArrayList<>(customers.values()));
        FileManager.saveBookings(new ArrayList<>(bookings));
        FileManager.saveServices(new ArrayList<>(services));
        System.out.println("[HotelManager] All data saved.");
    }

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
        Iterator<Room> it = rooms.values().iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    public List<Room> getRoomsSortedByPrice() {
        List<Room> sorted = new ArrayList<>(rooms.values());
        Collections.sort(sorted, Comparator.comparingInt(r -> r.getRoomType().getPricePerNight()));
        return sorted;
    }

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

    public synchronized Customer getCustomer(int id)     { return customers.get(id); }

    public synchronized List<Customer> getAllCustomers()  { return new ArrayList<>(customers.values()); }

    // ════════════════════════════════════════════════════════════════════════
    //  BOOKING MANAGEMENT
    // ════════════════════════════════════════════════════════════════════════

    public synchronized boolean bookRoom(int customerId, int roomNumber, int nights) {
        Room     room     = rooms.get(roomNumber);
        Customer customer = customers.get(customerId);
        if (room == null || customer == null || !room.isAvailable()) return false;

        room.setAvailable(false);
        room.setCleaningStatus("Occupied");

        Booking b = new Booking(bookingCounter++, customerId, roomNumber, nights);
        b.setRoomCost(room.calculateTariff(nights));
        bookings.add(b);
        customer.setAllocatedRoom(roomNumber);
        return true;
    }

    public synchronized boolean checkout(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null || room.isAvailable()) return false;

        Booking active = bookings.stream()
                .filter(b -> b.getRoomNumber() == roomNumber && b.getStatus() == Booking.Status.ACTIVE)
                .findFirst().orElse(null);

        if (active != null) {
            active.setStatus(Booking.Status.CHECKED_OUT);
            Customer c = customers.get(active.getCustomerId());
            if (c != null) c.setAllocatedRoom(-1);
        }

        room.setCleaningStatus("Needs Cleaning");
        new CleaningTask(roomNumber, this).start();
        return true;
    }

    public synchronized List<Booking> getAllBookings()    { return new ArrayList<>(bookings); }

    public synchronized Booking getActiveBookingForRoom(int roomNumber) {
        return bookings.stream()
                .filter(b -> b.getRoomNumber() == roomNumber && b.getStatus() == Booking.Status.ACTIVE)
                .findFirst().orElse(null);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LAUNDRY
    // ════════════════════════════════════════════════════════════════════════

    public synchronized LaundryService requestLaundry(int roomNumber, int itemCount) {
        Room room = rooms.get(roomNumber);
        if (room == null || room.isAvailable()) return null;

        LaundryService ls = new LaundryService(serviceCounter++, roomNumber, itemCount);
        services.add(ls);

        Thread t = new Thread(new LaundryTask(ls, this), "LaundryThread-" + ls.getServiceId());
        t.setDaemon(true);
        t.start();
        return ls;
    }

    public synchronized void notifyLaundryComplete(int serviceId) {
        completedLaundry.add(serviceId);
        notifyAll();
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

    public synchronized List<ServiceItem> getAllServices()         { return new ArrayList<>(services); }

    public synchronized List<ServiceItem> getServicesForRoom(int roomNumber) {
        List<ServiceItem> result = new ArrayList<>();
        for (ServiceItem s : services)
            if (s.getRoomNumber() == roomNumber) result.add(s);
        return result;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SEED DATA
    // ════════════════════════════════════════════════════════════════════════

    private void seedDefaultRooms() {
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
