package ServerInterface;

import Interface.EventManagementInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EventManagement extends UnicastRemoteObject implements EventManagementInterface {
    protected EventManagement() throws RemoteException {
        super();
    }

    @Override
    public boolean addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException {
        return false;
    }

    @Override
    public boolean removeEvent(String EventID, String eventType) throws RemoteException {
        return false;
    }

    @Override
    public boolean listEventAvailability(String eventType) throws RemoteException {
        return false;
    }

    @Override
    public boolean bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        return false;
    }

    @Override
    public boolean getBookingSchedule(String customerID) throws RemoteException {
        return false;
    }

    @Override
    public boolean cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
        return false;
    }
}
