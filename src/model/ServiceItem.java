package model;

import java.io.Serializable;

/**
 * Week 1 – Abstract base for add-on services.
 * LaundryService and LateCheckoutService extend this.
 */
public abstract class ServiceItem implements Serializable {

    private static final long serialVersionUID = 6L;

    private int    serviceId;
    private int    roomNumber;
    private String description;

    public ServiceItem(int serviceId, int roomNumber, String description) {
        this.serviceId   = serviceId;
        this.roomNumber  = roomNumber;
        this.description = description;
    }

    // Abstract method – each service defines its own cost
    public abstract double calculateCharge();

    // Abstract method – human-readable label
    public abstract String getServiceName();

    // Getters
    public int    getServiceId()   { return serviceId; }
    public int    getRoomNumber()  { return roomNumber; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("%-20s | Room:%-4d | ₹%.2f", getServiceName(), roomNumber, calculateCharge());
    }
}
