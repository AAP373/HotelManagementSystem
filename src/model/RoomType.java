package model;

/**
 * Week 2 – Enum with constructor, fields, and methods.
 * Autoboxing/Unboxing demonstrated in getPriceAsWrapper().
 */
public enum RoomType {

    STANDARD(1500, "Standard Room"),
    DELUXE(3500,   "Deluxe Room"),
    SUITE(7000,    "Suite");

    // Enum instance fields
    private final int    pricePerNight;
    private final String displayName;

    // Enum constructor
    RoomType(int pricePerNight, String displayName) {
        this.pricePerNight = pricePerNight;
        this.displayName   = displayName;
    }

    // Enum method
    public int getPricePerNight() {
        return pricePerNight;
    }

    // Week 2 – Autoboxing: return wrapper type
    public Integer getPriceAsWrapper() {
        return pricePerNight;          // int → Integer (autoboxing)
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Calculate total cost for given nights (Unboxing inside). */
    public double calculateCost(int nights) {
        Integer priceWrapper = getPriceAsWrapper(); // autoboxed
        return priceWrapper * nights;               // unboxed during multiplication
    }

    @Override
    public String toString() {
        return displayName + " (₹" + pricePerNight + "/night)";
    }
}
