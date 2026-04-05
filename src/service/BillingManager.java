package service;

import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Week 2  – Wrapper class arithmetic inside Bill.
 * Week 8  – ArrayList for bill collection.
 * Week 6  – Bills persisted via FileManager.
 */
public class BillingManager {

    private final HotelManager hotelManager;
    private final List<Bill>   bills = new ArrayList<>();

    public BillingManager(HotelManager hotelManager) {
        this.hotelManager = hotelManager;
    }

    /** Load saved bills from disk (called on startup). */
    public synchronized void loadBills() {
        List<Bill> saved = FileManager.loadBills();
        bills.addAll(saved);
        System.out.println("[BillingManager] Loaded " + saved.size() + " bills.");
    }

    /** Save all bills to disk (called on close). */
    public synchronized void saveBills() {
        FileManager.saveBills(new ArrayList<>(bills));
    }

    /** Generate a Bill for the active booking on a room. */
    public synchronized Bill generateBill(int roomNumber) {
        Booking booking = hotelManager.getActiveBookingForRoom(roomNumber);
        if (booking == null) return null;

        // Check if bill already exists for this booking
        for (Bill existing : bills) {
            if (existing.getBookingId() == booking.getBookingId()) return existing;
        }

        Customer customer = hotelManager.getCustomer(booking.getCustomerId());
        String   name     = customer != null ? customer.getName() : "Guest";

        Bill bill = new Bill(booking.getBookingId(), roomNumber, name, booking.getRoomCost());

        // Attach all services for this room
        for (ServiceItem s : hotelManager.getServicesForRoom(roomNumber)) {
            bill.addService(s);
        }

        bills.add(bill);
        return bill;
    }

    public synchronized Bill getBillByBookingId(int bookingId) {
        for (Bill b : bills)
            if (b.getBookingId() == bookingId) return b;
        return null;
    }

    public synchronized List<Bill> getAllBills()  { return new ArrayList<>(bills); }

    public synchronized boolean markPaid(int bookingId) {
        Bill b = getBillByBookingId(bookingId);
        if (b != null) { b.markPaid(); return true; }
        return false;
    }
}
