package service;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static final String DATA_DIR       = "data/";
    private static final String ROOMS_FILE    = DATA_DIR + "rooms.dat";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.dat";
    private static final String CUSTOMERS_FILE= DATA_DIR + "customers.dat";
    private static final String SERVICES_FILE = DATA_DIR + "services.dat";
    private static final String BILLS_DAT     = DATA_DIR + "bills.dat";
    private static final String BILLS_TXT     = DATA_DIR + "bills.txt";
    private static final int    ROOM_RECORD_SIZE = 23;

    static { new File(DATA_DIR).mkdirs(); }

    // ── Generic helpers ──────────────────────────────────────────────────────

    private static <T> void saveList(String path, List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(new ArrayList<>(list));
            System.out.println("[FileManager] Saved " + list.size() + " -> " + path);
        } catch (IOException e) {
            System.err.println("[FileManager] Save error (" + path + "): " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> loadList(String path) {
        // Try the given path, also try .ser variant for backwards compatibility
        File f = new File(path);
        if (!f.exists()) {
            // Try legacy .ser extension
            String legacyPath = path.replace(".dat", ".ser");
            File legacy = new File(legacyPath);
            if (legacy.exists()) {
                System.out.println("[FileManager] Loading from legacy: " + legacyPath);
                f = legacy;
                path = legacyPath;
            } else {
                return new ArrayList<>();
            }
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<T> result = (List<T>) ois.readObject();
            System.out.println("[FileManager] Loaded " + result.size() + " <- " + path);
            return result;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileManager] Load error (" + path + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Delete stale legacy files that conflict with new naming */
    public static void cleanLegacyFiles() {
        String[] legacy = { DATA_DIR + "bookings.ser", DATA_DIR + "rooms.ser" };
        for (String p : legacy) {
            File f = new File(p);
            if (f.exists()) {
                f.delete();
                System.out.println("[FileManager] Deleted legacy file: " + p);
            }
        }
    }

    // ── Rooms ─────────────────────────────────────────────────────────────────
    public static void saveRooms(List<Room> list)         { saveList(ROOMS_FILE, list); }
    public static List<Room> loadRooms()                  { return loadList(ROOMS_FILE); }

    // ── Bookings ──────────────────────────────────────────────────────────────
    public static void saveBookings(List<Booking> list)   { saveList(BOOKINGS_FILE, list); }
    public static List<Booking> loadBookings()            { return loadList(BOOKINGS_FILE); }

    // ── Customers ─────────────────────────────────────────────────────────────
    public static void saveCustomers(List<Customer> list) { saveList(CUSTOMERS_FILE, list); }
    public static List<Customer> loadCustomers()          { return loadList(CUSTOMERS_FILE); }

    // ── Services ──────────────────────────────────────────────────────────────
    public static void saveServices(List<ServiceItem> list){ saveList(SERVICES_FILE, list); }
    public static List<ServiceItem> loadServices()         { return loadList(SERVICES_FILE); }

    // ── Bills ─────────────────────────────────────────────────────────────────
    public static void saveBills(List<Bill> list)         { saveList(BILLS_DAT, list); }
    public static List<Bill> loadBills()                  { return loadList(BILLS_DAT); }

    // ── RAF ───────────────────────────────────────────────────────────────────
    public static void writeRoomRecord(Room room) {
        try (RandomAccessFile raf = new RandomAccessFile(DATA_DIR + "rooms_index.raf", "rw")) {
            long pos = (long)(room.getRoomNumber() % 1000) * ROOM_RECORD_SIZE;
            raf.seek(pos);
            raf.writeInt(room.getRoomNumber());
            raf.writeBytes(String.format("%-10s", room.getRoomType().name()));
            raf.writeDouble(room.getRoomType().getPricePerNight());
            raf.writeBoolean(room.isAvailable());
        } catch (IOException e) { System.err.println("[RAF] " + e.getMessage()); }
    }

    public static void readRoomRecord(int roomNumber) {
        try (RandomAccessFile raf = new RandomAccessFile(DATA_DIR + "rooms_index.raf", "r")) {
            long pos = (long)(roomNumber % 1000) * ROOM_RECORD_SIZE;
            raf.seek(pos);
            int rn = raf.readInt();
            byte[] tb = new byte[10]; raf.read(tb);
            double price = raf.readDouble();
            boolean avail = raf.readBoolean();
            System.out.printf("[RAF] Room %d | %-10s | Rs%.0f | Available:%b%n",
                    rn, new String(tb).trim(), price, avail);
        } catch (IOException e) { System.err.println("[RAF] " + e.getMessage()); }
    }

    // ── FileWriter bill export ────────────────────────────────────────────────
    public static void exportBill(Bill bill) {
        try (FileWriter fw = new FileWriter(BILLS_TXT, true)) {
            fw.write(bill.generateReceipt());
            fw.write("\n");
            System.out.println("[FileManager] Exported bill to " + BILLS_TXT);
        } catch (IOException e) { System.err.println("[FileManager] Export error: " + e.getMessage()); }
    }

    public static String readBillsFile() {
        File f = new File(BILLS_TXT);
        if (!f.exists()) return "No bills exported yet.";
        try (FileReader fr = new FileReader(BILLS_TXT)) {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = fr.read()) != -1) sb.append((char) ch);
            return sb.toString();
        } catch (IOException e) { return "Error: " + e.getMessage(); }
    }
}
