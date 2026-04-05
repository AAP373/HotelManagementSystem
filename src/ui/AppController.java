package ui;

/**
 * Simple controller interface so UI panes can trigger navigation
 * without depending directly on HotelManagementApp.
 */
public interface AppController {
    void showPane(String paneName);
}
