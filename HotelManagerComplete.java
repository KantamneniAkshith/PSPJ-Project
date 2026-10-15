import java.io.IOException; import java.nio.file.Files;
import java.nio.file.Path; import java.nio.file.Paths;
import java.util.*; import java.util.function.Predicate;
import java.util.stream.Collectors; import java.util.stream.Stream;
interface HotelFeatures {
    void showExtraReport();
}
class RoomBooking implements HotelFeatures {
    private final int room;
    private final String guest;
    private final int nights;
    private final boolean deluxe;
    private boolean active;
    RoomBooking(int room, String guest, int nights, boolean deluxe) {
        this.room = room; this.guest = guest;
        this.nights = nights; this.deluxe = deluxe;
        this.active = true;
    }
    int getRoom() { return room; }
    String getGuest() { return guest; }
    int getNights() { return nights; }
    boolean isActive() { return active; }
    void setActive(boolean active) { this.active = active; }
    double bill() { return nights * (deluxe ? 2500.0 : 1500.0); }
    public String toString() {
        return "Room " + room + " | Guest: " + guest + " | Nights: " + nights
                + " | Bill: Rs." + String.format("%.2f", bill())
                + " | Status: " + (active ? "Booked" : "Cancelled");
    }
    public void showExtraReport() {
        System.out.println("Guest: " + guest + ", Room: " + room + ", Bill: Rs." + bill());
    }
    String toFileLine() {
        return room + "|" + guest.replace("|", "/") + "|" + nights + "|" + deluxe + "|" + active;
    }
}
public class HotelManagerFixed {
    private static final int TOTAL_ROOMS = 10;
    private static final Path DATA_FILE = Paths.get("hotel-bookings.txt");
    private final Scanner sc = new Scanner(System.in);
    private final int[] rooms = new int[TOTAL_ROOMS];
    private final String[][] roomDetails = new String[TOTAL_ROOMS][3];
    private final List<RoomBooking> bookings = new ArrayList<>();
    private final Map<Integer, RoomBooking> bookingMap = new HashMap<>();
    private final Set<String> guestSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    public static void main(String[] args) {
        HotelManagerFixed manager = new HotelManagerFixed(); manager.loadBookings();
        manager.runMenu();
    }
    private int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a number: "); sc.next();
        }
        int value = sc.nextInt(); sc.nextLine();
        return value;
    }
    private void runMenu() {
        int choice;
        while (true) {
            System.out.println("\\n--- HOTEL BOOKING MANAGER ---"); System.out.println("1. View Rooms");
            System.out.println("2. Book Room"); System.out.println("3. Cancel Room");
            System.out.println("4. Show Occupancy"); System.out.println("5. Exit");
            System.out.print("Enter choice: "); choice = readInt();
            if (choice == 1) {
                viewRooms();
            } else if (choice == 2) {
                bookRoom();
            } else if (choice == 3) {
                cancelRoom();
            } else if (choice == 4) {
                showOccupancy();
            } else if (choice == 5) {
                saveBookings(); System.out.println("Thank you!");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }
    private void viewRooms() {
        for (int i = 0; i < TOTAL_ROOMS; i++) {
            if (rooms[i] == 0)
                System.out.println("Room " + (i + 1) + " - Available");
            else
                System.out.println("Room " + (i + 1) + " - Booked");
        }
    }
    private void bookRoom() {
        System.out.print("Enter room number: ");
        int room = readInt();
        if (room >= 1 && room <= TOTAL_ROOMS) {
            if (rooms[room - 1] == 0) {
                RoomBooking booking = new RoomBooking(room, "Guest" + room, 1, false);
                bookings.add(booking);
                bookingMap.put(room, booking);
                guestSet.add(booking.getGuest());
                rooms[room - 1] = 1;
                roomDetails[room - 1] = new String[] {
                    booking.getGuest(), "1", "Standard"
                };
                System.out.println("Room booked!");
            } else {
                System.out.println("Room already booked!");
            }
        } else {
            System.out.println("Invalid room number!");
        }
    }

    private void cancelRoom() {
        System.out.print("Enter room number: "); int room = readInt();
        if (room >= 1 && room <= TOTAL_ROOMS) {
            if (rooms[room - 1] == 1) {
                rooms[room - 1] = 0; RoomBooking booking = bookingMap.remove(room);
                if (booking != null) booking.setActive(false);
                roomDetails[room - 1] = null; System.out.println("Booking cancelled!");
            } else {
                System.out.println("Room is not booked!");
            }
        } else {
            System.out.println("Invalid room number!");
        }
    }
    private void showOccupancy() {
        int booked = 0;
        for (int i = 0; i < TOTAL_ROOMS; i++) {
            if (rooms[i] == 1) booked++;
        }
        int available = TOTAL_ROOMS - booked; System.out.println("Total Rooms: 10");
        System.out.println("Booked Rooms: " + booked); System.out.println("Available Rooms: " + available);
    }
    private int countAvailableRecursively(int index) {
        if (index == TOTAL_ROOMS) return 0;
        return (rooms[index] == 0 ? 1 : 0) + countAvailableRecursively(index + 1);
    }
    private double discount(double amount) { return amount * 0.05; }
    private double discount(double amount, double percent) { return amount * percent / 100.0; }
    private void saveBookings() {
        List<String> lines = new ArrayList<>(); lines.add("room|guest|nights|deluxe|active");
        for (RoomBooking b : bookings) lines.add(b.toFileLine());
        try {
            Files.write(DATA_FILE, lines);
        } catch (IOException e) {
            System.out.println("Could not save booking data.");
        }
    }
    private void loadBookings() {
        if (!Files.exists(DATA_FILE)) return;
        try (Stream<String> lines = Files.lines(DATA_FILE)) {
            lines.skip(1).forEach(line -> {
                try {
                    String[] p = line.split("\\\\|", -1);
                    if (p.length != 5) return;
                    int room = Integer.parseInt(p[0]); String guest = p[1];
                    int nights = Integer.parseInt(p[2]); boolean deluxe = Boolean.parseBoolean(p[3]);
                    boolean active = Boolean.parseBoolean(p[4]);
                    if (room < 1 || room > TOTAL_ROOMS || nights < 1 || guest.isEmpty()) return;
                    RoomBooking b = new RoomBooking(room, guest, nights, deluxe); b.setActive(active);
                    bookings.add(b); guestSet.add(guest);
                    if (active && bookingMap.get(room) == null) {
                        bookingMap.put(room, b); rooms[room - 1] = 1;
                        roomDetails[room - 1] = new String[] {
                            guest, String.valueOf(nights), deluxe ? "Deluxe" : "Standard"
                        };
                    }
                } catch (NumberFormatException ignored) {
                }
            });
        } catch (IOException e) {
            System.out.println("Could not load booking data.");
        }
    }
    private void streamReport() {
        Predicate<RoomBooking> active = RoomBooking::isActive;
        bookings.stream().filter(active)
                .sorted(Comparator.comparing(RoomBooking::getGuest))
                .forEach(RoomBooking::showExtraReport); System.out.println("Unique guests: " + guestSet);
        System.out.println("Available rooms: " + countAvailableRecursively(0));
    }
    private void textAndSortDemo(String searchName) {
        StringBuilder text = new StringBuilder(searchName.trim());
        bookings.stream().filter(b -> b.getGuest().equalsIgnoreCase(text.toString()))
                .forEach(System.out::println); List<Integer> roomNumbers = new ArrayList<>(bookingMap.keySet());
        Collections.sort(roomNumbers); System.out.println(roomNumbers);
    }
}
