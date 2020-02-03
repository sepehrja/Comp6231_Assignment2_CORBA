package ServerInterface;

import DataModel.EventModel;
import Interface.EventManagementInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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

    private static String sendUDPMessage(int serverPort, String method, String customerID, String eventType, String eventId) {
        DatagramSocket aSocket = null;
        String result = "";
        String dataFromClient = method + ";" + customerID + ";" + eventType + ";" + eventId;
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split(";");
            result = parts[0];
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return result;

    }

    public HashMap<String, HashMap<String, EventModel>> getAllEvents() {
        return allEvents;
    }

    public HashMap<String, HashMap<String, EventModel>> getClientEvents() {
        return clientEvents;
    }
}
