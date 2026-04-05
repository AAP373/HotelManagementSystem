# Hotel Management System with Laundry
### Java OOP Lab Manual – All Weeks Covered

---

## Project Structure

```
HotelManagementSystem/
├── module-info.java
├── README.md
├── data/                          ← auto-created at runtime
│   ├── rooms.dat                  ← serialized rooms
│   ├── bookings.ser               ← serialized bookings
│   ├── rooms_index.raf            ← RandomAccessFile index
│   └── bills.txt                  ← exported bills (FileWriter)
└── src/
    ├── app/
    │   └── HotelManagementApp.java     ← JavaFX entry point
    ├── model/
    │   ├── Room.java                   ← abstract base class
    │   ├── StandardRoom.java           ← extends Room
    │   ├── DeluxeRoom.java             ← extends Room (overloaded constructors)
    │   ├── Customer.java               ← encapsulation
    │   ├── Booking.java                ← serializable
    │   ├── Bill.java                   ← wrapper classes + ArrayList
    │   ├── RoomType.java               ← enum with constructor & methods
    │   ├── ServiceItem.java            ← abstract service base
    │   ├── LaundryService.java         ← extends ServiceItem
    │   └── LateCheckoutService.java    ← extends ServiceItem
    ├── service/
    │   ├── HotelManager.java           ← HashMap + synchronized methods
    │   ├── BillingManager.java         ← billing logic
    │   └── FileManager.java            ← serialization + RAF + FileWriter
    ├── threads/
    │   ├── CleaningTask.java           ← extends Thread
    │   ├── LaundryTask.java            ← implements Runnable
    │   └── BookingTask.java            ← implements Runnable + synchronized
    ├── ui/
    │   ├── AppController.java          ← navigation interface
    │   ├── MainDashboard.java          ← JavaFX root layout
    │   ├── RoomPane.java               ← room management screen
    │   ├── CustomerPane.java           ← customer management screen
    │   ├── BookingPane.java            ← booking + laundry + late checkout
    │   └── BillingPane.java            ← billing + receipt + export
    └── util/
        └── ValidationUtil.java         ← input validation helpers
```

---

## Lab Manual Concept Coverage

| Week | Concept                                      | File(s)                                         |
|------|----------------------------------------------|-------------------------------------------------|
| 1    | Abstract class, Inheritance, Overriding, super/this | Room, StandardRoom, DeluxeRoom, ServiceItem |
| 2    | Enum + constructor/methods, Wrapper, Auto/Unboxing | RoomType, Bill, LaundryService, LateCheckoutService |
| 3    | Thread (extend), Runnable, sleep(), join()   | CleaningTask, LaundryTask, BookingTask          |
| 4    | synchronized, wait()/notifyAll()             | HotelManager (all methods)                      |
| 5    | FileWriter (append), FileReader, try-with-resources | FileManager.exportBill(), readBillsFile()  |
| 6    | ObjectOutputStream/InputStream, RandomAccessFile, seek() | FileManager                          |
| 8    | HashMap, ArrayList, Iterator, Collections.sort() | HotelManager, BillingManager, Bill          |
| 9    | JavaFX Stage/Scene, controls, event handling | All ui/ files                                   |
| 10   | Complete modular hotel app                   | HotelManagementApp + all panes                  |

---

## Compile & Run

### Prerequisites
- Java 17+ with JavaFX SDK (download from https://openjfx.io)
- Set `JAVAFX_HOME` to your JavaFX SDK folder

### Step 1 – Create output directory
```bash
cd HotelManagementSystem
mkdir -p out
```

### Step 2 – Compile all sources
```bash
javac \
  --module-path $JAVAFX_HOME/lib \
  --add-modules javafx.controls,javafx.graphics \
  -d out \
  src/model/*.java \
  src/util/*.java \
  src/service/*.java \
  src/threads/*.java \
  src/ui/*.java \
  src/app/*.java
```

### Step 3 – Run
```bash
java \
  --module-path $JAVAFX_HOME/lib \
  --add-modules javafx.controls,javafx.graphics \
  -cp out \
  app.HotelManagementApp
```

### Windows (PowerShell)
```powershell
$JAVAFX_HOME = "C:\javafx-sdk-21"
javac --module-path "$JAVAFX_HOME\lib" --add-modules javafx.controls,javafx.graphics -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java --module-path "$JAVAFX_HOME\lib" --add-modules javafx.controls,javafx.graphics -cp out app.HotelManagementApp
```

---

## How to Use

1. **Rooms tab** – Add rooms (Standard/Deluxe/Suite). Click "Save to File" to serialize.
2. **Customers tab** – Add guests with name, phone, email.
3. **Bookings tab**
   - Enter Customer ID + Room Number + Nights → **Book Room**
   - Enter Room Number → **Checkout** (triggers cleaning thread in background)
   - Enter Room Number + Items → **Laundry** (background thread, prints progress to console)
   - Enter Room Number + Hours → **Late Checkout** (adds charge)
4. **Billing tab**
   - Enter Room Number → **Generate Bill** (includes all services + 18% tax)
   - Select a bill → **Mark Paid** or **Export to File** (appends to data/bills.txt)

Data is auto-saved to `data/` on app close and reloaded on next launch.
