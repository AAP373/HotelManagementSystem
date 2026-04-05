package model;

/**
 * Week 1 – Inherits ServiceItem, overrides calculateCharge().
 * Week 2 – Uses wrapper Integer for itemCount.
 */
public class LaundryService extends ServiceItem {

    private static final long serialVersionUID = 7L;

    private static final double RATE_PER_ITEM = 60.0;

    // Week 2 – Wrapper class field instead of primitive
    private Integer itemCount;

    public enum LaundryStatus { PENDING, IN_PROGRESS, COMPLETED, DELIVERED }

    private LaundryStatus laundryStatus;

    public LaundryService(int serviceId, int roomNumber, int itemCount) {
        super(serviceId, roomNumber, "Laundry – " + itemCount + " items");
        this.itemCount     = itemCount;         // int → Integer autoboxing
        this.laundryStatus = LaundryStatus.PENDING;
    }

    @Override
    public double calculateCharge() {
        return itemCount * RATE_PER_ITEM;       // Integer unboxed during multiply
    }

    @Override
    public String getServiceName() { return "Laundry Service"; }

    // Getters / Setters
    public int           getItemCount()     { return itemCount; }  // Integer unboxed
    public LaundryStatus getLaundryStatus() { return laundryStatus; }
    public void setLaundryStatus(LaundryStatus s) { this.laundryStatus = s; }

    @Override
    public String toString() {
        return super.toString() + " | Items:" + itemCount + " | Status:" + laundryStatus;
    }
}
