package service;

import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Week 2  – Wrapper class arithmetic (Double/Integer).
 * Week 8  – ArrayList for bill collection.
 */
public class BillingManager {

    private final HotelManager hotelManager;
    // Week 8 – ArrayList of bills
    private final List<Bill>   bills = new ArrayList<>();

    public BillingManager(HotelManager hotelManager) {
        this.hotelManager = hotelManager;
    }

    /**
     * Generate a Bill for the active booking on a room.
     * Pulls all services for the room and appends them.
     */
    public synchronized Bill generateBill(int roomNumber) {
        Booking booking = hotelManager.getActiveBookingForRoom(roomNumber);
        if (booking == null) return null;

        // Find customer name
        Customer customer = hotelManager.getCustomer(booking.getCustomerId());
        String   name     = customer != null ? customer.getName() : "Guest";

        Bill bill = new Bill(booking.getBookingId(), roomNumber, name, booking.getRoomCost());

        // Attach services
        List<ServiceItem> roomServices = hotelManager.getServicesForRoom(roomNumber);
        for (ServiceItem s : roomServices) {
            bill.addService(s);   // Week 2 – Double arithmetic inside Bill.addService()
        }

        bills.add(bill);
        return bill;
    }

    /** Retrieve existing bill by booking ID, or null. */
    public synchronized Bill getBillByBookingId(int bookingId) {
        for (Bill b : bills) {
            if (b.getBookingId() == bookingId) return b;
        }
        return null;
    }

    public synchronized List<Bill> getAllBills() {
        return new ArrayList<>(bills);
    }

    public synchronized boolean markPaid(int bookingId) {
        Bill b = getBillByBookingId(bookingId);
        if (b != null) { b.markPaid(); return true; }
        return false;
    }
}
