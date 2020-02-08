package ServerInterface;

import DataModel.ClientModel;
import DataModel.EventModel;
import Interface.EventManagementInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManagement extends UnicastRemoteObject implements EventManagementInterface {
    public static final int Montreal_Server_Port = 8888;
    public static final int Quebec_Server_Port = 7777;
    public static final int Sherbrooke_Server_Port = 6666;
    private String serverID;
    private String serverName;
    // HashMap<EventType, HashMap <EventID, Event>>
    private HashMap<String, HashMap<String, EventModel>> allEvents;
    // HashMap<CustomerID, HashMap <EventType, List<EventID>>>
    private HashMap<String, HashMap<String, List<String>>> clientEvents;
    // HashMap<ClientType, Client>
    private HashMap<String, ClientModel> serverClients;

    public EventManagement(String server) throws RemoteException {
        super();
        this.serverID = server;
        switch (server) {
            case "MTL":
                serverName = EventModel.EVENT_SERVER_MONTREAL;
                break;
            case "QUE":
                serverName = EventModel.EVENT_SERVER_QUEBEC;
                break;
            case "SHE":
                serverName = EventModel.EVENT_SERVER_SHERBROOK;
                break;
        }
        allEvents = new HashMap<>();
        allEvents.put(EventModel.CONFERENCES, new HashMap<>());
        allEvents.put(EventModel.SEMINARS, new HashMap<>());
        allEvents.put(EventModel.TRADE_SHOWS, new HashMap<>());
        clientEvents = new HashMap<>();
        serverClients = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
        ClientModel testManager = new ClientModel(serverID + "M1111");
        ClientModel testCustomer = new ClientModel(serverID + "C1111");
        serverClients.put(testManager.getClientID(), testManager);
        serverClients.put(testCustomer.getClientID(), testCustomer);
        clientEvents.put(testCustomer.getClientID(), new HashMap<>());

        EventModel sampleConf = new EventModel(EventModel.CONFERENCES, serverID + "M01012020", 5);
        sampleConf.addRegisteredClientID(testCustomer.getClientID());
        clientEvents.get(testCustomer.getClientID()).put(sampleConf.getEventType(), new ArrayList<>());
        clientEvents.get(testCustomer.getClientID()).get(sampleConf.getEventType()).add(sampleConf.getEventID());

        EventModel sampleTrade = new EventModel(EventModel.TRADE_SHOWS, serverID + "A02022020", 15);
        sampleTrade.addRegisteredClientID(testCustomer.getClientID());
        clientEvents.get(testCustomer.getClientID()).put(sampleTrade.getEventType(), new ArrayList<>());
        clientEvents.get(testCustomer.getClientID()).get(sampleTrade.getEventType()).add(sampleTrade.getEventID());

        EventModel sampleSemi = new EventModel(EventModel.SEMINARS, serverID + "E03032020", 20);
        sampleSemi.addRegisteredClientID(testCustomer.getClientID());
        clientEvents.get(testCustomer.getClientID()).put(sampleSemi.getEventType(), new ArrayList<>());
        clientEvents.get(testCustomer.getClientID()).get(sampleSemi.getEventType()).add(sampleSemi.getEventID());

        allEvents.get(EventModel.CONFERENCES).put(sampleConf.getEventID(), sampleConf);
        allEvents.get(EventModel.TRADE_SHOWS).put(sampleTrade.getEventID(), sampleTrade);
        allEvents.get(EventModel.SEMINARS).put(sampleSemi.getEventID(), sampleSemi);
    }

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException {
        //TODO: check for event existence
        //TODO: check for client existence
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            EventModel event = new EventModel(eventType, eventID, bookingCapacity);
            HashMap<String, EventModel> eventHashMap = new HashMap<>();
            eventHashMap.put(eventID, event);
            allEvents.put(eventType, eventHashMap);
            return "event " + eventID + "added successfully";
        } else {
            //TODO: contact the needed server
            return "server response";
        }
    }

    @Override
    public String removeEvent(String eventID, String eventType) throws RemoteException {
        //TODO: check for client existence
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (allEvents.get(eventType).remove(eventID) != null) {
                //TODO:re-arrange clients that were registered
                return "event removed successfully";
            } else {
                return "event " + eventID + " does not exist";
            }
        } else {
            //TODO: contact the needed server
            return "server response";
        }
    }

    @Override
    public String listEventAvailability(String eventType) throws RemoteException {
        //TODO: check for client existence
        //TODO: it must be from all servers ?!
        //TODO: if yes we must gather from other servers also
        HashMap<String, EventModel> events = allEvents.get(eventType);
        if (events.size() == 0) {
            return "No events of type " + eventType;
        }
        StringBuilder builder = new StringBuilder();
        for (EventModel event :
                events.values()) {
            builder.append(event.toString() + " || ");
        }
        return builder.toString();
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        //TODO: check for client existence
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            EventModel bookedEvent = allEvents.get(eventType).get(eventID);
            if (clientEvents.containsKey(customerID)) {
                if (clientEvents.get(customerID).containsKey(eventType)) {
                    if (!clientEvents.get(customerID).get(eventType).contains(eventID)) {
                        clientEvents.get(customerID).get(eventType).add(eventID);
                    } else {
                        return "Event " + eventID + " Already Booked";
                    }
                } else {
                    List<String> temp = new ArrayList<>();
                    temp.add(eventID);
                    clientEvents.get(customerID).put(eventType, temp);
                }
            } else {
                HashMap<String, List<String>> temp = new HashMap<>();
                List<String> temp2 = new ArrayList<>();
                temp2.add(eventID);
                temp.put(eventType, temp2);
                clientEvents.put(customerID, temp);
            }
            return "Event " + eventID + " Booked Successfully";
        } else {
            //TODO: contact the needed server
            return "server response";
        }
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        //TODO: check for client existence
        HashMap<String, List<String>> events = clientEvents.get(customerID);
        if (events.size() == 0) {
            return "No scheduled event is booked for " + customerID;
        }
        StringBuilder builder = new StringBuilder();
        for (String eventType :
                events.keySet()) {
            builder.append(eventType + ": ");
            for (String eventID :
                    events.get(eventType)) {
                builder.append(eventID + " ||");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
        //TODO
        //TODO: check for client existence
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            return "false";
        } else {
            //TODO: contact the needed server
            return "server response";
        }
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

    public HashMap<String, HashMap<String, List<String>>> getClientEvents() {
        return clientEvents;
    }

    public HashMap<String, ClientModel> getServerClients() {
        return serverClients;
    }

    private void initCustomer(String customerID) {

    }
}
