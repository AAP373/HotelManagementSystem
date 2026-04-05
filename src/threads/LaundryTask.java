package threads;

import model.LaundryService;
import model.LaundryService.LaundryStatus;
import service.HotelManager;

/**
 * Week 3 – Thread creation using Runnable interface.
 * Week 4 – Inter-thread communication via wait()/notify() in HotelManager.
 */
public class LaundryTask implements Runnable {

    private final LaundryService laundry;
    private final HotelManager   manager;

    public LaundryTask(LaundryService laundry, HotelManager manager) {
        this.laundry = laundry;
        this.manager = manager;
    }

    @Override
    public void run() {
        System.out.printf("[Laundry] Request #%d for Room %d started (%d items)%n",
                laundry.getServiceId(), laundry.getRoomNumber(), laundry.getItemCount());

        laundry.setLaundryStatus(LaundryStatus.IN_PROGRESS);
        simulate("Washing",  1500);

        laundry.setLaundryStatus(LaundryStatus.COMPLETED);
        simulate("Drying",   1000);

        laundry.setLaundryStatus(LaundryStatus.DELIVERED);
        System.out.printf("[Laundry] Request #%d delivered to Room %d. Charge: ₹%.2f%n",
                laundry.getServiceId(), laundry.getRoomNumber(), laundry.calculateCharge());

        // Notify manager that laundry is done (Week 4 – notify pattern)
        manager.notifyLaundryComplete(laundry.getServiceId());
    }

    private void simulate(String step, int ms) {
        System.out.printf("[Laundry] Request #%d – %s...%n", laundry.getServiceId(), step);
        try {
            Thread.sleep(ms);   // Week 3 – sleep()
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
