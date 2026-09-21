import java.util.Scanner;

public class HotelManager {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int[] rooms = new int[10];
        int choice;
        int room;

        while (true) {

            System.out.println("\n--- HOTEL BOOKING MANAGER ---");
            System.out.println("1. View Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Room");
            System.out.println("4. Show Occupancy");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            if (choice == 1) {

                for (int i = 0; i < 10; i++) {
                    if (rooms[i] == 0) {
                        System.out.println("Room " + (i + 1) + " - Available");
                    } else {
                        System.out.println("Room " + (i + 1) + " - Booked");
                    }
                }

            } else if (choice == 2) {

                System.out.print("Enter room number: ");
                room = sc.nextInt();

                if (room >= 1 && room <= 10) {

                    if (rooms[room - 1] == 0) {
                        rooms[room - 1] = 1;
                        System.out.println("Room booked!");
                    } else {
                        System.out.println("Room already booked!");
                    }

                } else {
                    System.out.println("Invalid room number!");
                }

            } else if (choice == 3) {

                System.out.print("Enter room number: ");
                room = sc.nextInt();

                if (room >= 1 && room <= 10) {

                    if (rooms[room - 1] == 1) {
                        rooms[room - 1] = 0;
                        System.out.println("Booking cancelled!");
                    } else {
                        System.out.println("Room is not booked!");
                    }

                } else {
                    System.out.println("Invalid room number!");
                }

            } else if (choice == 4) {

                int booked = 0;

                for (int i = 0; i < 10; i++) {
                    if (rooms[i] == 1) {
                        booked++;
                    }
                }

                int available = 10 - booked;

                System.out.println("Total Rooms: 10");
                System.out.println("Booked Rooms: " + booked);
                System.out.println("Available Rooms: " + available);

            } else if (choice == 5) {

                System.out.println("Thank you!");
                break;

            } else {

                System.out.println("Invalid choice!");
            }
        }

        sc.close();
    }
}