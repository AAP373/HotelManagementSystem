package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Week 2 – Wrapper classes: uses Double/Integer instead of primitives.
 * Week 8 – ArrayList to hold service items.
 * Week 6 – Serializable for file persistence.
 */
public class Bill implements Serializable {

    private static final long serialVersionUID = 9L;

    private int    bookingId;
    private int    roomNumber;
    private String customerName;

    // Week 2 – Wrapper types
    private Double  roomCharge;
    private Double  serviceCharge;
    private Double  taxRate;       // e.g. 0.18 for 18%
    private Double  totalAmount;

    // Week 8 – ArrayList for services
    private List<ServiceItem> services;

    private boolean isPaid;

    public Bill(int bookingId, int roomNumber, String customerName, double roomCharge) {
        this.bookingId    = bookingId;
        this.roomNumber   = roomNumber;
        this.customerName = customerName;
        this.roomCharge   = roomCharge;   // double → Double autoboxing
        this.serviceCharge = 0.0;
        this.taxRate       = 0.18;        // double → Double autoboxing
        this.services      = new ArrayList<>();
        this.isPaid        = false;
        recalculate();
    }

    public void addService(ServiceItem item) {
        services.add(item);
        serviceCharge = serviceCharge + item.calculateCharge(); // Double unboxed, re-boxed
        recalculate();
    }

    private void recalculate() {
        double subtotal = roomCharge + serviceCharge;       // Double unboxed
        totalAmount = subtotal + (subtotal * taxRate);      // Double unboxed and re-boxed
    }

    // Getters
    public int             getBookingId()     { return bookingId; }
    public int             getRoomNumber()    { return roomNumber; }
    public String          getCustomerName()  { return customerName; }
    public double          getRoomCharge()    { return roomCharge; }      // Double → double
    public double          getServiceCharge() { return serviceCharge; }
    public double          getTaxRate()       { return taxRate; }
    public double          getTotalAmount()   { return totalAmount; }
    public List<ServiceItem> getServices()    { return services; }
    public boolean         isPaid()           { return isPaid; }
    public void            markPaid()         { this.isPaid = true; }

    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("          HOTEL BILL RECEIPT\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Booking ID   : %d%n", bookingId));
        sb.append(String.format("Customer     : %s%n", customerName));
        sb.append(String.format("Room No.     : %d%n", roomNumber));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Room Charge  : ₹%.2f%n", roomCharge));
        if (!services.isEmpty()) {
            sb.append("Services:\n");
            for (ServiceItem s : services) {
                sb.append(String.format("  %-22s ₹%.2f%n", s.getServiceName(), s.calculateCharge()));
            }
        }
        sb.append(String.format("Service Total: ₹%.2f%n", serviceCharge));
        sb.append(String.format("Tax (%.0f%%)     : ₹%.2f%n", taxRate * 100, totalAmount - roomCharge - serviceCharge));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("TOTAL        : ₹%.2f%n", totalAmount));
        sb.append(String.format("Status       : %s%n", isPaid ? "PAID" : "PENDING"));
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Bill[B%d] | %s | Room:%d | ₹%.2f | %s",
                bookingId, customerName, roomNumber, totalAmount, isPaid ? "PAID" : "PENDING");
    }
}
