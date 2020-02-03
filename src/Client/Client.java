package Client;

import java.util.Scanner;

public class Client {
    static final int USER_TYPE_CUSTOMER = 1;
    static final int USER_TYPE_MANAGER = 2;
    static final int CUSTOMER_BOOK_EVENT = 1;
    static final int CUSTOMER_GET_BOOKING_SCHEDULE = 2;
    static final int CUSTOMER_CANCEL_EVENT = 3;
    static final int CUSTOMER_LOGOUT = 4;
    static final int MANAGER_ADD_EVENT = 1;
    static final int MANAGER_REMOVE_EVENT = 2;
    static final int MANAGER_LIST_EVENT_AVAILABILITY = 3;
    static final int MANAGER_BOOK_EVENT = 4;
    static final int MANAGER_GET_BOOKING_SCHEDULE = 5;
    static final int MANAGER_CANCEL_EVENT = 6;
    static final int MANAGER_LOGOUT = 7;

    static Scanner input;

    public static void main(String[] args) throws Exception {
//        Registry registry = LocateRegistry.getRegistry(5555);
//        EventManagement remoteObject = (EventManagement) registry.lookup("Addition");
        init();
    }

    public static void init() {
        input = new Scanner(System.in);
        String userID = "";
        System.out.println("Please Enter your UserID:");
        userID = input.next();
        System.out.println("Login successful (" + userID + ")");
        switch (checkUserType(userID)) {
            case USER_TYPE_CUSTOMER:
                customer(userID);
                break;
            case USER_TYPE_MANAGER:
                manager(userID);
                break;
            default:
                System.out.println("!!UserID is not in correct format");
                init();
        }
    }

    private static int checkUserType(String userID) {
        if (userID.length() == 8) {
            if (userID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    userID.substring(0, 3).equalsIgnoreCase("QUE") ||
                    userID.substring(0, 3).equalsIgnoreCase("SHE")) {
                if (userID.substring(3, 4).equalsIgnoreCase("C")) {
                    return USER_TYPE_CUSTOMER;
                } else if (userID.substring(3, 4).equalsIgnoreCase("M")) {
                    return USER_TYPE_MANAGER;
                }
            }
        }
        return 0;
    }

    private static void customer(String customerID) {
        printMenu(USER_TYPE_CUSTOMER);
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case CUSTOMER_BOOK_EVENT:
                break;
            case CUSTOMER_GET_BOOKING_SCHEDULE:
                break;
            case CUSTOMER_CANCEL_EVENT:
                break;
            case CUSTOMER_LOGOUT:
                init();
                break;
        }
        customer(customerID);
    }

    private static void manager(String eventManagerID) {
        printMenu(USER_TYPE_MANAGER);
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case MANAGER_ADD_EVENT:
                break;
            case MANAGER_REMOVE_EVENT:
                break;
            case MANAGER_LIST_EVENT_AVAILABILITY:
                break;
            case MANAGER_BOOK_EVENT:
                break;
            case MANAGER_GET_BOOKING_SCHEDULE:
                break;
            case MANAGER_CANCEL_EVENT:
                break;
            case MANAGER_LOGOUT:
                init();
                break;
        }
        manager(eventManagerID);
    }

    private static void printMenu(int userType) {
        System.out.println("*************************************");
        System.out.println("Please choose an option below:");
        if (userType == USER_TYPE_CUSTOMER) {
            System.out.println("1.Book Event");
            System.out.println("2.Get Booking Schedule");
            System.out.println("3.Cancel Event");
            System.out.println("4.Logout");
        } else if (userType == USER_TYPE_MANAGER) {
            System.out.println("1.Add Event");
            System.out.println("2.Remove Event");
            System.out.println("3.List Event Availability");
            System.out.println("4.Book Event");
            System.out.println("5.Get Booking Schedule");
            System.out.println("6.Cancel Event");
            System.out.println("7.Logout");
        }
    }
}
