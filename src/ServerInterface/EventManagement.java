package ServerInterface;

import DataModel.EventModel;
import Interface.EventManagementInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class EventManagement extends UnicastRemoteObject implements EventManagementInterface {
    public static final int Montreal_Server_Port = 8888;
    public static final int Quebec_Server_Port = 7777;
    public static final int Sherbrooke_Server_Port = 6666;
    private String server;
    private HashMap<String, HashMap<String, EventModel>> allEvents;
    private HashMap<String, HashMap<String, EventModel>> clientEvents;

    public EventManagement(String server) throws RemoteException {
        super();
        this.server = server;
//        addSomeTestData();
        allEvents = new HashMap<>();
        clientEvents = new HashMap<>();
    }

    private void addSomeTestData() {

    }

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException {
        return "false";
    }

    @Override
    public String removeEvent(String EventID, String eventType) throws RemoteException {
        return "false";
    }

    @Override
    public String listEventAvailability(String eventType) throws RemoteException {
        return "false";
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        return "false";
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        return "false";
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
        return "false";
    }
}
