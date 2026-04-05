package model;

/**
 * Week 1 – Inheritance + Method Overriding.
 * Extends abstract Room. Overrides calculateTariff().
 */
public class StandardRoom extends Room {

    private static final long serialVersionUID = 2L;

    private boolean hasAC;

    // Constructor uses super keyword (Week 1)
    public StandardRoom(int roomNumber, boolean hasAC) {
        super(roomNumber, RoomType.STANDARD);
        this.hasAC = hasAC;
    }

    /**
     * Week 1 – Runtime Polymorphism.
     * Tariff = base price + AC surcharge if applicable.
     */
    @Override
    public double calculateTariff(int nights) {
        // Week 2 – Unboxing: getRoomType().getPriceAsWrapper() returns Integer, unboxed here
        double base = getRoomType().getPriceAsWrapper() * nights;   // Integer unboxed
        double ac   = hasAC ? 200.0 * nights : 0.0;
        return base + ac;
    }

    @Override
    public String getSummary() {
        return super.getSummary() + " | AC: " + (hasAC ? "Yes" : "No");
    }

    public boolean isHasAC() { return hasAC; }
    public void setHasAC(boolean hasAC) { this.hasAC = hasAC; }
}
