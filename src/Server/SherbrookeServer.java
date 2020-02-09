package Server;

import Client.Client;
import ServerInterface.EventManagement;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SherbrookeServer {
    public static void main(String[] args) throws Exception {

        EventManagement remoteObject = new EventManagement("SHE");
        Registry registry = LocateRegistry.createRegistry(Client.SERVER_SHERBROOKE);
        registry.bind(Client.EVENT_MANAGEMENT_REGISTERED_NAME, remoteObject);

        System.out.println("Sherbrooke Server is Up & Running");
        Runnable task = () -> {
            listenForRequest(remoteObject);
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private static void listenForRequest(EventManagement obj) {
        DatagramSocket aSocket = null;
        String sendingResult = "";
        try {
            aSocket = new DatagramSocket(EventManagement.Sherbrooke_Server_Port);
            byte[] buffer = new byte[1000];
            System.out.println("Sherbrooke UDP Server Started at port " + aSocket.getLocalPort() + " ............");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String sentence = new String(request.getData(), 0,
                        request.getLength());
                String[] parts = sentence.split(";");
                String method = parts[0];
                String customerID = parts[1];
                String eventType = parts[2];
                String eventID = parts[3];
                if (method.equalsIgnoreCase("removedEvent")) {
                    String result = obj.removedEvent(eventID, eventType, customerID);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("listEventAvailability")) {
                    String result = obj.listEventAvailability(eventType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("bookEvent")) {
                    String result = obj.bookEvent(customerID, eventID, eventType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("cancelEvent")) {
                    String result = obj.cancelEvent(customerID, eventID, eventType);
                    sendingResult = result + ";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}
