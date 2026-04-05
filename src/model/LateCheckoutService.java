package model;

/**
 * Week 1 – Inherits ServiceItem, overrides calculateCharge().
 * Week 2 – Uses wrapper Double for hourly rate.
 */
public class LateCheckoutService extends ServiceItem {

    private static final long serialVersionUID = 8L;

    // Week 2 – Wrapper class instead of primitive
    private Double  hourlyRate;
    private Integer extraHours;

    public LateCheckoutService(int serviceId, int roomNumber, int extraHours) {
        super(serviceId, roomNumber, "Late Checkout – " + extraHours + " extra hours");
        this.hourlyRate  = 300.0;   // double → Double autoboxing
        this.extraHours  = extraHours; // int → Integer autoboxing
    }

    @Override
    public double calculateCharge() {
        return hourlyRate * extraHours;  // Double & Integer both unboxed
    }

    @Override
    public String getServiceName() { return "Late Checkout"; }

    // Getters
    public double getHourlyRate() { return hourlyRate; } // Double → double unboxing
    public int    getExtraHours() { return extraHours; } // Integer → int unboxing

    @Override
    public String toString() {
        return super.toString() + " | Extra Hours:" + extraHours;
    }
}
