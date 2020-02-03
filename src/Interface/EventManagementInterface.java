package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EventManagementInterface extends Remote {
    /**
     * Only manager
     */
    String addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException;

    String removeEvent(String EventID, String eventType) throws RemoteException;

    String listEventAvailability(String eventType) throws RemoteException;

    /**
     * Both manager and Customer
     */
    String bookEvent(String customerID, String eventID, String eventType) throws RemoteException;

    String getBookingSchedule(String customerID) throws RemoteException;

    String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException;
}
