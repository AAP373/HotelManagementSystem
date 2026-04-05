package model;

import java.io.Serializable;

/**
 * Week 1 – Encapsulation: private fields, public getters/setters with validation.
 * Implements Serializable for Week 6 persistence.
 */
public class Customer implements Serializable {

    private static final long serialVersionUID = 4L;

    private int    customerId;
    private String name;
    private String phone;
    private String email;
    private int    allocatedRoom;   // -1 = no room

    // Constructor (Week 1 – this keyword)
    public Customer(int customerId, String name, String phone, String email) {
        this.customerId    = customerId;
        this.name          = name;
        this.phone         = phone;
        this.email         = email;
        this.allocatedRoom = -1;
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public int    getCustomerId()    { return customerId; }
    public String getName()          { return name; }
    public String getPhone()         { return phone; }
    public String getEmail()         { return email; }
    public int    getAllocatedRoom()  { return allocatedRoom; }
    public boolean isCheckedIn()     { return allocatedRoom != -1; }

    // ── Setters with basic validation ────────────────────────────────────────
    public void setName(String name) {
        if (name != null && !name.isBlank()) this.name = name;
    }

    public void setPhone(String phone) {
        if (phone != null && phone.matches("\\d{10}")) this.phone = phone;
        else throw new IllegalArgumentException("Phone must be 10 digits.");
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) this.email = email;
        else throw new IllegalArgumentException("Invalid email.");
    }

    public void setAllocatedRoom(int roomNumber) {
        this.allocatedRoom = roomNumber;
    }

    @Override
    public String toString() {
        return String.format("C%-4d | %-20s | %-12s | %-25s | Room: %s",
                customerId, name, phone, email,
                allocatedRoom == -1 ? "None" : String.valueOf(allocatedRoom));
    }
}
