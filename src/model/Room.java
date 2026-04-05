package model;

import java.io.Serializable;

/**
 * Week 1 – Abstract class.
 * Encapsulation: private fields with getters/setters.
 * Abstraction: abstract calculateTariff() forced on subclasses.
 * Implements Serializable for Week 6 file persistence.
 */
public abstract class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    // Private data members (Encapsulation)
    private int      roomNumber;
    private RoomType roomType;
    private boolean  available;
    private String   cleaningStatus; // "Clean" | "Needs Cleaning" | "In Progress"

    // Constructor
    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber     = roomNumber;
        this.roomType       = roomType;
        this.available      = true;
        this.cleaningStatus = "Clean";
    }

    // ── Abstract method (Week 1 – Abstraction) ──────────────────────────────
    public abstract double calculateTariff(int nights);

    // ── Concrete method ──────────────────────────────────────────────────────
    public String getSummary() {
        return String.format("Room %-4d | %-8s | ₹%-6d/night | %-9s | %s",
                roomNumber,
                roomType.name(),
                roomType.getPricePerNight(),
                available ? "Available" : "Occupied",
                cleaningStatus);
    }

    // ── Getters and Setters (Encapsulation) ─────────────────────────────────
    public int getRoomNumber()          { return roomNumber; }
    public RoomType getRoomType()       { return roomType; }
    public boolean isAvailable()        { return available; }
    public String getCleaningStatus()   { return cleaningStatus; }

    public void setAvailable(boolean available)           { this.available = available; }
    public void setCleaningStatus(String cleaningStatus)  { this.cleaningStatus = cleaningStatus; }

    @Override
    public String toString() {
        return getSummary();
    }
}
