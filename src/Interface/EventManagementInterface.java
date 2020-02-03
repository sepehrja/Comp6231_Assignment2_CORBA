package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EventManagementInterface extends Remote {
    /**
     * Only manager
     */
    boolean addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException;

    boolean removeEvent(String EventID, String eventType) throws RemoteException;

    boolean listEventAvailability(String eventType) throws RemoteException;

    /**
     * Both manager and Customer
     */
    boolean bookEvent(String customerID, String eventID, String eventType) throws RemoteException;

    boolean getBookingSchedule(String customerID) throws RemoteException;

    boolean cancelEvent(String customerID, String eventID, String eventType) throws RemoteException;
}
