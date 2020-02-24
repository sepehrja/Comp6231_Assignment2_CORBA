package Server;

import Client.Client;
import DataModel.EventModel;
import Logger.Logger;
import ServerInterface.EventManagement;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerInstance {

    private String serverID;
    private String serverName;
    private int serverRegistryPort;
    private int serverUdpPort;

    public ServerInstance(String serverID) throws Exception {
        this.serverID = serverID;
        switch (serverID) {
            case "MTL":
                serverName = EventManagement.EVENT_SERVER_MONTREAL;
                serverRegistryPort = Client.SERVER_MONTREAL;
                serverUdpPort = EventManagement.Montreal_Server_Port;
                break;
            case "QUE":
                serverName = EventManagement.EVENT_SERVER_QUEBEC;
                serverRegistryPort = Client.SERVER_QUEBEC;
                serverUdpPort = EventManagement.Quebec_Server_Port;
                break;
            case "SHE":
                serverName = EventManagement.EVENT_SERVER_SHERBROOK;
                serverRegistryPort = Client.SERVER_SHERBROOKE;
                serverUdpPort = EventManagement.Sherbrooke_Server_Port;
                break;
        }

        EventManagement remoteObject = new EventManagement(serverID, serverName);
        Registry registry = LocateRegistry.createRegistry(serverRegistryPort);
        registry.bind(Client.EVENT_MANAGEMENT_REGISTERED_NAME, remoteObject);

        System.out.println(serverName + " Server is Up & Running");
        Logger.serverLog(serverID, " Server is Up & Running");
        addTestData(remoteObject);
        Runnable task = () -> {
            listenForRequest(remoteObject, serverUdpPort, serverName, serverID);
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void addTestData(EventManagement remoteObject) {
        switch (serverID) {
            case "MTL":
                remoteObject.addNewEvent("MTLA090620", EventModel.CONFERENCES, 2);
                remoteObject.addNewEvent("MTLA080620", EventModel.TRADE_SHOWS, 2);
                remoteObject.addNewEvent("MTLE230620", EventModel.SEMINARS, 1);
                remoteObject.addNewEvent("MTLA150620", EventModel.TRADE_SHOWS, 12);
                break;
            case "QUE":
                remoteObject.addNewCustomerToClients("QUEC1234");
                remoteObject.addNewCustomerToClients("QUEC4114");
                break;
            case "SHE":
                remoteObject.addNewEvent("SHEE110620", EventModel.CONFERENCES, 1);
                remoteObject.addNewEvent("SHEE080620", EventModel.CONFERENCES, 1);
                break;
        }
    }

    private static void listenForRequest(EventManagement obj, int serverUdpPort, String serverName, String serverID) {
        DatagramSocket aSocket = null;
        String sendingResult = "";
        try {
            aSocket = new DatagramSocket(serverUdpPort);
            byte[] buffer = new byte[1000];
            System.out.println(serverName + " UDP Server Started at port " + aSocket.getLocalPort() + " ............");
            Logger.serverLog(serverID, " UDP Server Started at port " + aSocket.getLocalPort());
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
                if (method.equalsIgnoreCase("removeEvent")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                    String result = obj.removeEventUDP(eventID, eventType, customerID);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("listEventAvailability")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventType: " + eventType + " ", " ...");
                    String result = obj.listEventAvailabilityUDP(eventType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("bookEvent")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                    String result = obj.bookEvent(customerID, eventID, eventType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("cancelEvent")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                    String result = obj.cancelEvent(customerID, eventID, eventType);
                    sendingResult = result + ";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
                Logger.serverLog(serverID, customerID, " UDP reply sent " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", sendingResult);
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
