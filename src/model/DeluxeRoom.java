package model;

/**
 * Week 1 – Inheritance + Method Overriding.
 * Extends abstract Room. Overrides calculateTariff() with premium extras.
 */
public class DeluxeRoom extends Room {

    private static final long serialVersionUID = 3L;

    private boolean hasFreeWifi;
    private boolean hasBreakfast;
    private boolean isSuite;

    // Constructor overloading (Week 1)
    public DeluxeRoom(int roomNumber) {
        super(roomNumber, RoomType.DELUXE);
        this.hasFreeWifi  = true;
        this.hasBreakfast = false;
        this.isSuite      = false;
    }

    public DeluxeRoom(int roomNumber, boolean hasFreeWifi, boolean hasBreakfast) {
        super(roomNumber, RoomType.DELUXE);
        this.hasFreeWifi  = hasFreeWifi;
        this.hasBreakfast = hasBreakfast;
        this.isSuite      = false;
    }

    public DeluxeRoom(int roomNumber, boolean hasFreeWifi, boolean hasBreakfast, boolean isSuite) {
        super(roomNumber, isSuite ? RoomType.SUITE : RoomType.DELUXE);
        this.hasFreeWifi  = hasFreeWifi;
        this.hasBreakfast = hasBreakfast;
        this.isSuite      = isSuite;
    }

    /**
     * Week 1 – Overriding.  Week 2 – Unboxing (getPriceAsWrapper()).
     */
    @Override
    public double calculateTariff(int nights) {
        double base      = getRoomType().getPriceAsWrapper() * (double) nights; // unboxing
        double wifiCost  = hasFreeWifi  ? 0 : 300.0 * nights;   // free if included
        double bfCost    = hasBreakfast ? 500.0 * nights : 0;
        return base + wifiCost + bfCost;
    }

    @Override
    public String getSummary() {
        return super.getSummary()
                + " | WiFi: " + (hasFreeWifi ? "Free" : "Paid")
                + " | Breakfast: " + (hasBreakfast ? "Yes" : "No")
                + (isSuite ? " | SUITE" : "");
    }

    // Getters
    public boolean isHasFreeWifi()   { return hasFreeWifi; }
    public boolean isHasBreakfast()  { return hasBreakfast; }
    public boolean isSuite()         { return isSuite; }
}
