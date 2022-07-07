import jdk.internal.access.JavaNetHttpCookieAccess;
import java.sql.SQLOutput;

import java.text.*;
import java.util.*;

public class Hotel {
    static Hashtable<Integer, List<String[]>> rooms = new Hashtable<Integer, List<String[]>>();

    public static int validatingOptionInputsForMenu() {
        Scanner sc = new Scanner(System.in);
        int option;
        String input;

        while (true){
            input = sc.nextLine();
            if (input.length() != 1) {
                System.out.println("Invalid input! Please check again the options above!");
            } else {
                if (input.charAt(0) >= '1' && input.charAt(0) <= '7') {
                    option = Integer.parseInt(input);
                    break;
                } else {
                    System.out.println("Invalid input! Please check again the options above!");
                }
            }
        }

        return option;
    }

    public static void printingMenu() throws ParseException {
       do {
           System.out.println("                             Hotel \"Atlantic\"");
           System.out.println();
           System.out.println("Please select one of the following options");
           System.out.println("-----------------------");
           System.out.println("""
                   1 - Make a reservation
                   2 - List free rooms
                   3 - Checkout room
                   4 - Stats
                   5 - Find a room
                   6 - Update a room
                   7 - Exit""");
           System.out.println("-----------------------");

           switch (validatingOptionInputsForMenu()) {
               case 1 -> makingReservation();
               case 2 -> printFreeRooms();
               case 3 -> vacateRoom();
               case 4 -> printRoomsForPeriod();
               case 5 -> findSuitableRoom();
               case 6 -> additionalRequirements();
               case 7 -> {
                   System.out.println("Exit");
                   return;
               }
           }
       }
        while(true);
    }

    public static boolean checkIfAvailableRooms(int roomNumber, String checkInDate, String checkOutDate) throws ParseException {
        Date checkIn = new SimpleDateFormat("dd.MM.yyyy").parse(checkInDate);
        Date checkOut = new SimpleDateFormat("dd.MM.yyyy").parse(checkOutDate);

        if (rooms.containsKey(roomNumber)) {
            List<String[]> current = rooms.get(roomNumber);
            for (String[] element : current) {
                Date resCheckIn = new SimpleDateFormat("dd.MM.yyyy").parse(element[0]);
                Date resCheckOut = new SimpleDateFormat("dd.MM.yyyy").parse(element[1]);
                if ((checkIn.after(resCheckIn) && checkIn.before(resCheckOut)) || checkOut.after(resCheckIn) && checkOut.before(resCheckOut)) {
                    System.out.println("The room is already booked for this time of period." + "\nPlease select a different room or " +
                            "date !");
                    return false;
                }
            }
        } else {
            return true;
        }

        return true;
    }

    public static boolean isCorrectDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(false);
        return sdf.parse(date, new ParsePosition(0)) != null;
    }

    public static boolean correctInputForRoomNumber(String s) {
        if (s.length() != 3 || s.contains(" ")) {
            return false;
        }
        if (s.charAt(0) < '1' || s.charAt(0) > '4') {
            return false;
        }
        if (s.charAt(1) < '0' || s.charAt(1) > '1') {
            return false;
        }

        int secondNumber = s.charAt(1) - '0';
        if (secondNumber == 0) {
            if (s.charAt(2) < '1' || s.charAt(2) > '9') {
                return false;
            }
        } else {
            if (s.charAt(2) != '0') {
                return false;
            }
        }

        return true;
    }

    public static void makingReservation() throws ParseException {
        Scanner sc = new Scanner(System.in);
        int roomNumber;
        String checkIn;
        String checkOut;
        String input;

        do {
            do {
                System.out.print("Choose a room: ");
                input = sc.nextLine();


                if (correctInputForRoomNumber(input)) {
                    roomNumber = Integer.parseInt(input);
                    break;
                } else {
                    System.out.println("There is no such a room. Please check all the available rooms " +
                            "in the hotel!");
                    System.out.println();
                    printingMenu();
                }

            } while (true);

            System.out.println();

            System.out.print("Check in date(dd.mm.yyyy): ");
            checkIn = sc.nextLine();


            while (!isCorrectDate(checkIn)) {
                System.out.println("Incorrect date/input!Please enter correctly.");
                System.out.print("Enter starting date(dd.mm.yyyy): ");
                checkIn = sc.nextLine();
            }

            System.out.print("Check out date(dd.mm.yyyy): ");
            checkOut = sc.nextLine();

            while (!isCorrectDate(checkOut)) {
                System.out.println("Incorrect date/input!Please enter correctly.");
                System.out.print("Enter ending date(dd.mm.yyyy): ");
                checkOut = sc.nextLine();
            }

        } while (!checkIfAvailableRooms(roomNumber, checkIn, checkOut));

        System.out.print("Additional comments: ");
        String guestComment = sc.nextLine();
        String[] values = {checkIn, checkOut, guestComment, ""};
        List<String[]> list;

        if (rooms.containsKey(roomNumber)) {
            list = rooms.get(roomNumber);
            list.add(values);
        } else {
            list = new ArrayList<>();
            list.add(values);
            rooms.put(roomNumber, list);
        }

        System.out.println("Successful reservation!");
    }

    public static void printFreeRooms() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        int roomNumber;

        for (int floor = 1; floor <= 4; floor++) {
            System.out.println("Floor " + floor);
            for (int room = 1; room <= 10; room++) {
                boolean notFree = false;
                roomNumber = room + floor * 100;
                if (rooms.containsKey(roomNumber)) {
                    List<String[]> values = rooms.get(roomNumber);
                    for (String[] element : values) {
                        Date resCheckIn = new SimpleDateFormat("dd.MM.yyyy").parse(element[0]);
                        Date resCheckOut = new SimpleDateFormat("dd.MM.yyyy").parse(element[1]);
                        if (currentDate.after(resCheckIn) && currentDate.before(resCheckOut)) {
                            notFree = true;
                            break;
                        }
                    }
                    if (!notFree) {
                        System.out.println("Room number: " + roomNumber);
                    }
                } else {
                    System.out.println("Room number: " + roomNumber);
                }
            }
            System.out.println("-----------------");
        }
    }

    public static void vacateRoom() {
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        String today = formatter.format(new Date());
        Set<Integer> setOfKeys = rooms.keySet();
        Iterator<Integer> itr = setOfKeys.iterator();
        List<String[]> current;

        while (itr.hasNext()) {
            int key = itr.next();
            current = rooms.get(key);
            int size = current.size();
            int index = 0;

            while (size != 0) {
                if (today.equals(current.get(0)[1])) {
                    current.remove(index);
                }
                index++;
                size--;
                if (rooms.get(key).isEmpty()) {
                    itr.remove();
                }
            }
        }
    }

    public static void printRoomsForPeriod() throws ParseException {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter starting date(dd.mm.yyyy): ");
        String startDate = sc.nextLine();

        while (!isCorrectDate(startDate)) {
            System.out.println("Incorrect date/input!Please enter correctly.");
            System.out.print("Enter starting date(dd.mm.yyyy): ");
            startDate = sc.nextLine();
        }

        System.out.print("Enter ending date(dd.mm.yyyy): ");
        String endDate = sc.nextLine();

        while (!isCorrectDate(endDate)) {
            System.out.println("Incorrect date/input!Please enter correctly.");
            System.out.print("Enter ending date(dd.mm.yyyy): ");
            endDate = sc.nextLine();
        }

        Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(startDate);
        Date date2 = new SimpleDateFormat("dd.MM.yyyy").parse(endDate);

        Set<Integer> setOfKeys = rooms.keySet();
        Iterator<Integer> itr = setOfKeys.iterator();
        List<String[]> current;

        while (itr.hasNext()) {
            int key = itr.next();
            current = rooms.get(key);
            for (String[] element : current) {
                Date checkIn = new SimpleDateFormat("dd.MM.yyyy").parse(element[0]);
                Date checkOut = new SimpleDateFormat("dd.MM.yyyy").parse(element[1]);
                if (!(checkIn.after(date1) && checkIn.before(date2)) && !(checkOut.after(date1) && checkOut.before(date2))) {
                    long difference_In_Time
                            = checkOut.getTime() - checkIn.getTime();
                    long difference_In_Days
                            = (difference_In_Time
                            / (1000 * 60 * 60 * 24))
                            % 365;
                    System.out.println(key + " : " + difference_In_Days + " day(s).");
                } else if (checkOut.after(date2) && (checkIn.after(date1) && checkIn.before(date2))) {
                    long difference_In_Time
                            = date2.getTime() - checkIn.getTime();
                    long difference_In_Days
                            = (difference_In_Time
                            / (1000 * 60 * 60 * 24))
                            % 365;
                    System.out.println(key + " : " + difference_In_Days + " day(s).");
                } else if (checkIn.before(date1) && (checkOut.after(date1) && checkOut.before(date2))) {
                    long difference_In_Time
                            = checkOut.getTime() - date1.getTime();
                    long difference_In_Days
                            = (difference_In_Time
                            / (1000 * 60 * 60 * 24))
                            % 365;
                    System.out.println(key + " : " + difference_In_Days + " day(s).");
                }
            }
        }
    }

    public static void findSuitableRoom() throws ParseException {
        Scanner sc = new Scanner(System.in);
        int numberBeds;
        int startRoom = 0;
        int endRoom = 0;
        boolean correct = false;

        while (!correct) {
            System.out.print("Enter number of beds (2, 3 or 4): ");
            numberBeds = sc.nextInt();
            if (numberBeds == 2) {
                startRoom = 1;
                endRoom = 5;
                correct = true;
            } else if (numberBeds == 3) {
                startRoom = 6;
                endRoom = 9;
                correct = true;
            } else if (numberBeds == 4) {
                startRoom = 10;
                endRoom = 10;
                correct = true;
            }
        }
        String fake= sc.nextLine();
        System.out.print("Enter starting date(dd.mm.yyyy): ");
        String startDate = sc.nextLine();

        while (!isCorrectDate(startDate)) {
            System.out.println("Incorrect date/input!Please enter correctly.");
            System.out.print("Enter starting date(dd.mm.yyyy): ");
            startDate = sc.nextLine();
        }

        System.out.print("Enter ending date(dd.mm.yyyy): ");
        String endDate = sc.nextLine();

        while (!isCorrectDate(endDate)) {
            System.out.println("Incorrect date/input!Please enter correctly.");
            System.out.print("Enter ending date(dd.mm.yyyy): ");
            endDate = sc.nextLine();
        }

        Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(startDate);
        Date date2 = new SimpleDateFormat("dd.MM.yyyy").parse(endDate);

        int startRoomConst = startRoom;
        for (int floor = 1; floor <= 4; floor++) {
            startRoom = startRoomConst;
            while (startRoom <= endRoom) {
                int room = 100 * floor + startRoom;
                List<String[]> current;
                if (rooms.containsKey(room)) {
                    current = rooms.get(room);
                } else {
                    System.out.println(room);
                    startRoom++;
                    continue;
                }

                boolean busy = false;
                for (String[] element : current) {
                    Date checkIn = new SimpleDateFormat("dd.MM.yyyy").parse(element[0]);
                    Date checkOut = new SimpleDateFormat("dd.MM.yyyy").parse(element[1]);
                    if ((checkIn.after(date1) && checkIn.before(date2)) || (checkOut.after(date1) && checkOut.before(date2))) {
                        busy = true;
                        break;
                    }
                }
                if (!busy) {
                    System.out.println("Available rooms for the period");
                    System.out.println();
                    System.out.println(room);
                }
                startRoom++;
            }
        }
    }

    public static void additionalRequirements() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter room number: ");
        String roomNumber = sc.nextLine();

        while (!correctInputForRoomNumber(roomNumber)) {
            System.out.print("Enter room number: ");
            roomNumber = sc.nextLine();
        }

        System.out.print("Enter starting date(dd.mm.yyyy): ");
        String startDate = sc.nextLine();
        String fake= sc.nextLine();

        while (!isCorrectDate(startDate)) {
            System.out.println("Incorrect date/input!Please enter correctly.");
            System.out.print("Enter starting date(dd.mm.yyyy): ");
            startDate = sc.nextLine();
        }

        System.out.print("Enter ending date(dd.mm.yyyy): ");
        String endDate = sc.nextLine();

        while (!isCorrectDate(endDate)) {
            System.out.println("Incorrect date/input!Please enter correctly.");
            System.out.print("Enter starting date(dd.mm.yyyy): ");
            endDate = sc.nextLine();
        }

        if (rooms.containsKey(roomNumber)) {
            List<String[]> curr = rooms.get(roomNumber);
            for (String[] element : curr) {
                if (element[0].equals(startDate) && element[1].equals(endDate)) {
                    System.out.print("Enter requirements for your room: ");
                    element[3] = sc.nextLine();
                    System.out.println("Your room reservation was successfully updated!");
                    return;
                }
            }
            System.out.println("There is no such reservation!");
        } else {
            System.out.println("There is no such reservation!");
        }
    }

    public static void main(String[] args) throws ParseException {
        printingMenu();

    }
}
