package Client;

import DataModel.EventModel;
import Interface.EventManagementInterface;
import Logger.Logger;

import java.io.IOException;
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

    public static void init() throws IOException {
        input = new Scanner(System.in);
        String userID;
        System.out.println("Please Enter your UserID:");
        userID = input.next().trim().toUpperCase();
        Logger.clientLog(userID, " login attempt");
        switch (checkUserType(userID)) {
            case USER_TYPE_CUSTOMER:
                try {
                    System.out.println("Customer Login successful (" + userID + ")");
                    Logger.clientLog(userID, " Customer Login successful");
                    customer(userID, getServerPort(userID.substring(0, 3)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case USER_TYPE_MANAGER:
                try {
                    System.out.println("Manager Login successful (" + userID + ")");
                    Logger.clientLog(userID, " Manager Login successful");
                    manager(userID, getServerPort(userID.substring(0, 3)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("!!UserID is not in correct format");
                Logger.clientLog(userID, " UserID is not in correct format");
                Logger.deleteALogFile(userID);
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
        EventManagementInterface remoteObject = (EventManagementInterface) registry.lookup(EVENT_MANAGEMENT_REGISTERED_NAME);
        boolean repeat = true;
        printMenu(USER_TYPE_CUSTOMER);
        int menuSelection = input.nextInt();
        String eventType;
        String eventID;
        String serverResponse;
        switch (menuSelection) {
            case CUSTOMER_BOOK_EVENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(customerID, " attempting to bookEvent");
                serverResponse = remoteObject.bookEvent(customerID, eventID, eventType);
                System.out.println(serverResponse);
                Logger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case CUSTOMER_GET_BOOKING_SCHEDULE:
                Logger.clientLog(customerID, " attempting to getBookingSchedule");
                serverResponse = remoteObject.getBookingSchedule(customerID);
                System.out.println(serverResponse);
                Logger.clientLog(customerID, " bookEvent", " null ", serverResponse);
                break;
            case CUSTOMER_CANCEL_EVENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(customerID, " attempting to cancelEvent");
                serverResponse = remoteObject.cancelEvent(customerID, eventID, eventType);
                System.out.println(serverResponse);
                Logger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case CUSTOMER_LOGOUT:
                repeat = false;
                Logger.clientLog(customerID, " attempting to Logout");
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
        EventManagementInterface remoteObject = (EventManagementInterface) registry.lookup(EVENT_MANAGEMENT_REGISTERED_NAME);
        boolean repeat = true;
        printMenu(USER_TYPE_MANAGER);
        String customerID;
        String eventType;
        String eventID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case MANAGER_ADD_EVENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                capacity = promptForCapacity();
                Logger.clientLog(eventManagerID, " attempting to addEvent");
                serverResponse = remoteObject.addEvent(eventID, eventType, capacity);
                System.out.println(serverResponse);
                Logger.clientLog(eventManagerID, " addEvent", " eventID: " + eventID + " eventType: " + eventType + " eventCapacity: " + capacity + " ", serverResponse);
                break;
            case MANAGER_REMOVE_EVENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to removeEvent");
                serverResponse = remoteObject.removeEvent(eventID, eventType);
                System.out.println(serverResponse);
                Logger.clientLog(eventManagerID, " removeEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case MANAGER_LIST_EVENT_AVAILABILITY:
                eventType = promptForEventType();
                Logger.clientLog(eventManagerID, " attempting to listEventAvailability");
                serverResponse = remoteObject.listEventAvailability(eventType);
                System.out.println(serverResponse);
                Logger.clientLog(eventManagerID, " listEventAvailability", " eventType: " + eventType + " ", serverResponse);
                break;
            case MANAGER_BOOK_EVENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                eventType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to bookEvent");
                serverResponse = remoteObject.bookEvent(customerID, eventID, eventType);
                System.out.println(serverResponse);
                Logger.clientLog(eventManagerID, " bookEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case MANAGER_GET_BOOKING_SCHEDULE:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                Logger.clientLog(eventManagerID, " attempting to getBookingSchedule");
                serverResponse = remoteObject.getBookingSchedule(customerID);
                System.out.println(serverResponse);
                Logger.clientLog(eventManagerID, " getBookingSchedule", " customerID: " + customerID + " ", serverResponse);
                break;
            case MANAGER_CANCEL_EVENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                eventType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to cancelEvent");
                serverResponse = remoteObject.cancelEvent(customerID, eventType, eventID);
                System.out.println(serverResponse);
                Logger.clientLog(eventManagerID, " cancelEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case MANAGER_LOGOUT:
                repeat = false;
                Logger.clientLog(eventManagerID, "attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            manager(eventManagerID, serverPort);
        }
    }

    private static String askForCustomerIDFromManager(String server) {
        System.out.println("Please enter a customerID(Within " + server + " Server):");
        String userID = input.next().trim().toUpperCase();
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
        System.out.println("*************************************");
        System.out.println("Please choose an eventType below:");
        System.out.println("1.Conferences");
        System.out.println("2.Seminars");
        System.out.println("3.Trade Shows");
        switch (input.nextInt()) {
            case 1:
                return EventModel.CONFERENCES;
            case 2:
                return EventModel.SEMINARS;
            case 3:
                return EventModel.TRADE_SHOWS;
        }
        return promptForEventType();
    }

    private static String promptForEventID() {
        System.out.println("*************************************");
        System.out.println("Please enter the EventID (e.g MTLM190120)");
        String eventID = input.next().trim().toUpperCase();
        if (eventID.length() == 10) {
            if (eventID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    eventID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    eventID.substring(0, 3).equalsIgnoreCase("QUE")) {
                if (eventID.substring(3, 4).equalsIgnoreCase("M") ||
                        eventID.substring(3, 4).equalsIgnoreCase("A") ||
                        eventID.substring(3, 4).equalsIgnoreCase("E")) {
                    return eventID;
                }
            }
        }
        return promptForEventID();
    }

    private static int promptForCapacity() {
        System.out.println("*************************************");
        System.out.println("Please enter the booking capacity:");
        return input.nextInt();
    }
}
