package Client;

import ServerInterface.EventManagement;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final int USER_TYPE_CUSTOMER = 1;
    public static final int USER_TYPE_MANAGER = 2;
    public static final int CUSTOMER_BOOK_EVENT = 1;
    public static final int CUSTOMER_GET_BOOKING_SCHEDULE = 2;
    public static final int CUSTOMER_CANCEL_EVENT = 3;
    public static final int CUSTOMER_LOGOUT = 4;
    public static final int MANAGER_ADD_EVENT = 1;
    public static final int MANAGER_REMOVE_EVENT = 2;
    public static final int MANAGER_LIST_EVENT_AVAILABILITY = 3;
    public static final int MANAGER_BOOK_EVENT = 4;
    public static final int MANAGER_GET_BOOKING_SCHEDULE = 5;
    public static final int MANAGER_CANCEL_EVENT = 6;
    public static final int MANAGER_LOGOUT = 7;
    public static final int SERVER_MONTREAL = 2964;
    public static final int SERVER_SHERBROOKE = 2965;
    public static final int SERVER_QUEBEC = 2966;
    public static final String EVENT_MANAGEMENT_REGISTERED_NAME = "EVENT_MANAGEMENT";

    static Scanner input;

    public static void main(String[] args) throws Exception {
        init();
    }

    public static void init() {
        input = new Scanner(System.in);
        String userID = "";
        System.out.println("Please Enter your UserID:");
        userID = input.next().toUpperCase();
        System.out.println("Login successful (" + userID + ")");
        switch (checkUserType(userID)) {
            case USER_TYPE_CUSTOMER:
                try {
                    customer(userID, getServerPort(userID.substring(0, 3)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case USER_TYPE_MANAGER:
                try {
                    manager(userID, getServerPort(userID.substring(0, 3)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("!!UserID is not in correct format");
                init();
        }
    }

    private static int getServerPort(String branchAcronym) {
        if (branchAcronym.equalsIgnoreCase("MTL")) {
            return SERVER_MONTREAL;
        } else if (branchAcronym.equalsIgnoreCase("SHE")) {
            return SERVER_SHERBROOKE;
        } else if (branchAcronym.equalsIgnoreCase("QUE")) {
            return SERVER_QUEBEC;
        }
        return 1;
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

    private static void customer(String customerID, int serverPort) throws Exception {
        if (serverPort == 1) {
            return;
        }
        Registry registry = LocateRegistry.getRegistry(serverPort);
        EventManagement remoteObject = (EventManagement) registry.lookup(EVENT_MANAGEMENT_REGISTERED_NAME);
        boolean repeat = true;
        printMenu(USER_TYPE_CUSTOMER);
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case CUSTOMER_BOOK_EVENT:
                System.out.println(remoteObject.bookEvent(customerID, promptForEventID(), promptForEventType()));
                break;
            case CUSTOMER_GET_BOOKING_SCHEDULE:
                System.out.println(remoteObject.getBookingSchedule(customerID));
                break;
            case CUSTOMER_CANCEL_EVENT:
                System.out.println(remoteObject.cancelEvent(customerID, promptForEventID(), promptForEventType()));
                break;
            case CUSTOMER_LOGOUT:
                repeat = false;
                init();
                break;
        }
        if (repeat) {
            customer(customerID, serverPort);
        }
    }

    private static void manager(String eventManagerID, int serverPort) throws Exception {
        if (serverPort == 1) {
            return;
        }
        Registry registry = LocateRegistry.getRegistry(serverPort);
        EventManagement remoteObject = (EventManagement) registry.lookup(EVENT_MANAGEMENT_REGISTERED_NAME);
        boolean repeat = true;
        printMenu(USER_TYPE_MANAGER);
        String customerID = "";
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case MANAGER_ADD_EVENT:
                System.out.println(remoteObject.addEvent(promptForEventID(), promptForEventType(), promptForCapacity()));
                break;
            case MANAGER_REMOVE_EVENT:
                System.out.println(remoteObject.removeEvent(promptForEventID(), promptForEventType()));
                break;
            case MANAGER_LIST_EVENT_AVAILABILITY:
                System.out.println(remoteObject.listEventAvailability(promptForEventType()));
                break;
            case MANAGER_BOOK_EVENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                System.out.println(remoteObject.bookEvent(customerID, promptForEventID(), promptForEventType()));
                break;
            case MANAGER_GET_BOOKING_SCHEDULE:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                System.out.println(remoteObject.getBookingSchedule(customerID));
                break;
            case MANAGER_CANCEL_EVENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                System.out.println(remoteObject.cancelEvent(customerID, promptForEventID(), promptForEventType()));
                break;
            case MANAGER_LOGOUT:
                repeat = false;
                init();
                break;
        }
        if (repeat) {
            manager(eventManagerID, serverPort);
        }
    }

    private static String askForCustomerIDFromManager(String server) {
        System.out.println("Please enter a customerID(Within " + server + " Server):");
        String userID = input.next().toUpperCase();
        if (checkUserType(userID) != USER_TYPE_CUSTOMER) {
            return askForCustomerIDFromManager(server);
        } else {
            return userID;
        }
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

    private static String promptForEventType() {
        return null;
    }

    private static String promptForEventID() {
        return null;
    }

    private static int promptForCapacity() {
        return 0;
    }
}
