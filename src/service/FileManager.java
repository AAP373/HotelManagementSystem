package service;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Week 5 – FileWriter (character stream) for bill export.
 * Week 6 – Serialization / Deserialization (bookings.ser, rooms.dat).
 * Week 6 – RandomAccessFile for fixed-length room index.
 */
public class FileManager {

    private static final String DATA_DIR      = "data/";
    private static final String ROOMS_FILE    = DATA_DIR + "rooms.dat";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.ser";
    private static final String BILLS_FILE    = DATA_DIR + "bills.txt";

    // Each room record in RandomAccessFile: 4 (int) + 10 (type) + 8 (double) + 1 (boolean) = 23 bytes
    private static final int ROOM_RECORD_SIZE = 23;

    static {
        new File(DATA_DIR).mkdirs();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Week 6 – Serialization: save Room list
    // ════════════════════════════════════════════════════════════════════════

    public static void saveRooms(List<Room> rooms) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(new ArrayList<>(rooms));
            System.out.println("[FileManager] Rooms saved (" + rooms.size() + " records).");
        } catch (IOException e) {
            System.err.println("[FileManager] Save rooms error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Room> loadRooms() {
        File f = new File(ROOMS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(ROOMS_FILE))) {
            return (List<Room>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileManager] Load rooms error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Week 6 – Serialization: save Booking list
    // ════════════════════════════════════════════════════════════════════════

    public static void saveBookings(List<Booking> bookings) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(new ArrayList<>(bookings));
            System.out.println("[FileManager] Bookings saved (" + bookings.size() + " records).");
        } catch (IOException e) {
            System.err.println("[FileManager] Save bookings error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Booking> loadBookings() {
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(BOOKINGS_FILE))) {
            return (List<Booking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileManager] Load bookings error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Week 6 – RandomAccessFile: write fixed-size room index record
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Writes one Room record at a calculated offset based on roomNumber.
     * Format: [int roomNumber][10-char type][double price][boolean available]
     */
    public static void writeRoomRecord(Room room) {
        try (RandomAccessFile raf = new RandomAccessFile(DATA_DIR + "rooms_index.raf", "rw")) {
            // Use a simple linear position (roomNumber - 100) * recordSize as demo
            long pos = (long)(room.getRoomNumber() % 1000) * ROOM_RECORD_SIZE;
            raf.seek(pos);
            raf.writeInt(room.getRoomNumber());

            // Fixed-length type string (10 chars, padded)
            String typeStr = String.format("%-10s", room.getRoomType().name());
            raf.writeBytes(typeStr);

            raf.writeDouble(room.getRoomType().getPricePerNight());
            raf.writeBoolean(room.isAvailable());

            System.out.printf("[FileManager][RAF] Room %d written at offset %d%n",
                    room.getRoomNumber(), pos);
        } catch (IOException e) {
            System.err.println("[FileManager][RAF] Error: " + e.getMessage());
        }
    }

    /** Reads a Room record using RandomAccessFile seek. */
    public static void readRoomRecord(int roomNumber) {
        try (RandomAccessFile raf = new RandomAccessFile(DATA_DIR + "rooms_index.raf", "r")) {
            long pos = (long)(roomNumber % 1000) * ROOM_RECORD_SIZE;
            raf.seek(pos);

            int    rn        = raf.readInt();
            byte[] typeBytes = new byte[10];
            raf.read(typeBytes);
            String typeName  = new String(typeBytes).trim();
            double price     = raf.readDouble();
            boolean avail    = raf.readBoolean();

            System.out.printf("[FileManager][RAF] Room %d | Type:%-10s | ₹%.0f | Available:%b%n",
                    rn, typeName, price, avail);
        } catch (IOException e) {
            System.err.println("[FileManager][RAF] Read error: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Week 5 – FileWriter (character stream): export bill to bills.txt
    // ════════════════════════════════════════════════════════════════════════

    public static void exportBill(Bill bill) {
        // Append mode – Week 5: FileWriter with true = append
        try (FileWriter fw = new FileWriter(BILLS_FILE, true)) {
            fw.write(bill.generateReceipt());
            fw.write("\n");
            System.out.println("[FileManager] Bill exported to " + BILLS_FILE);
        } catch (IOException e) {
            System.err.println("[FileManager] Bill export error: " + e.getMessage());
        }
    }

    /** Read bills.txt using FileReader (character stream). */
    public static String readBillsFile() {
        File f = new File(BILLS_FILE);
        if (!f.exists()) return "No bills recorded yet.";

        // Week 5 – try-with-resources FileReader
        try (FileReader fr = new FileReader(BILLS_FILE)) {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = fr.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            return "Error reading bills: " + e.getMessage();
        }
    }
}
