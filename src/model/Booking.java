package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Week 6 – Serializable object stored in bookings.ser.
 */
public class Booking implements Serializable {

    private static final long serialVersionUID = 5L;

    public enum Status { ACTIVE, CHECKED_OUT, CANCELLED }

    private int       bookingId;
    private int       customerId;
    private int       roomNumber;
    private int       nights;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double    roomCost;
    private Status    status;

    public Booking(int bookingId, int customerId, int roomNumber, int nights) {
        this.bookingId  = bookingId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.nights     = nights;
        this.checkIn    = LocalDate.now();
        this.checkOut   = checkIn.plusDays(nights);
        this.status     = Status.ACTIVE;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int       getBookingId()   { return bookingId; }
    public int       getCustomerId()  { return customerId; }
    public int       getRoomNumber()  { return roomNumber; }
    public int       getNights()      { return nights; }
    public LocalDate getCheckIn()     { return checkIn; }
    public LocalDate getCheckOut()    { return checkOut; }
    public double    getRoomCost()    { return roomCost; }
    public Status    getStatus()      { return status; }

    public void setRoomCost(double roomCost) { this.roomCost = roomCost; }
    public void setStatus(Status status)     { this.status  = status; }

    @Override
    public String toString() {
        return String.format("B%-4d | Customer:%-4d | Room:%-4d | %d nights | Check-in:%s | ₹%.2f | %s",
                bookingId, customerId, roomNumber, nights, checkIn, roomCost, status);
    }
}
