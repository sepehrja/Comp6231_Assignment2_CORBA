package ServerInterface;

import Interface.EventManagementInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EventManagement extends UnicastRemoteObject implements EventManagementInterface {
    protected EventManagement() throws RemoteException {
        super();
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
