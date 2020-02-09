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
    // HashMap<ClientID, Client>
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
//        ClientModel testManager = new ClientModel(serverID + "M1111");
        ClientModel testCustomer = new ClientModel(serverID + "C1111");
//        serverClients.put(testManager.getClientID(), testManager);
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
        if (allEvents.get(eventType).containsKey(eventID)) {
            if (allEvents.get(eventType).get(eventID).getEventCapacity() <= bookingCapacity) {
                allEvents.get(eventType).get(eventID).setEventCapacity(bookingCapacity);
                return "Event " + eventID + " Capacity increased to " + bookingCapacity;
            } else {
                return "Event Already Exists, Cannot Decrease Booking Capacity";
            }
        }
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            EventModel event = new EventModel(eventType, eventID, bookingCapacity);
            HashMap<String, EventModel> eventHashMap = new HashMap<>();
            eventHashMap.put(eventID, event);
            allEvents.put(eventType, eventHashMap);
            return "Event " + eventID + " added successfully";
        } else {
            return "Cannot Add Event to servers other than " + serverName;
        }
    }

    @Override
    public String removeEvent(String eventID, String eventType) throws RemoteException {
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (allEvents.get(eventType).containsKey(eventID)) {
                List<String> registeredClients = allEvents.get(eventType).get(eventID).getRegisteredClientIDs();
                addCustomersToNextSameEvent(eventID, eventType, registeredClients);
                allEvents.get(eventType).remove(eventID);
                return "Event Removed Successfully";
            } else {
                return "Event " + eventID + " Does Not Exist";
            }
        } else {
            return "Cannot Remove Event from servers other than " + serverName;
        }
    }

    @Override
    public String listEventAvailability(String eventType) throws RemoteException {
        //TODO:we must gather from other servers also
        HashMap<String, EventModel> events = allEvents.get(eventType);
        if (events.size() == 0) {
            return "No events of type " + eventType;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + eventType + ":\n");
        for (EventModel event :
                events.values()) {
            builder.append(event.toString() + " || ");
        }
        builder.append("=====================================\n");
        return builder.toString();
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (!serverClients.containsKey(customerID)) {
                addNewCustomerToClients(customerID);
            }
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
            if (!exceedWeeklyLimit(customerID, eventID.substring(4))) {
                //TODO: contact the needed server
                return "server response";
            } else {
                return "You Cannot Book Event in Other Servers For This Week(Max Weekly Limit = 3)";
            }
        }
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        if (!serverClients.containsKey(customerID)) {
            addNewCustomerToClients(customerID);
            return "Booking Schedule Empty For " + customerID;
        }
        HashMap<String, List<String>> events = clientEvents.get(customerID);
        if (events.size() == 0) {
            return "Booking Schedule Empty For " + customerID;
        }
        StringBuilder builder = new StringBuilder();
        for (String eventType :
                events.keySet()) {
            builder.append(eventType + ":\n");
            for (String eventID :
                    events.get(eventType)) {
                builder.append(eventID + " ||");
            }
            builder.append("=====================================\n");
        }
        return builder.toString();
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (customerID.substring(0, 3).equals(serverID)) {
                if (!serverClients.containsKey(customerID)) {
                    addNewCustomerToClients(customerID);
                    return "You " + customerID + " Are Not Registered in " + eventID;
                } else {
                    if (clientEvents.get(customerID).get(eventType).remove(eventID)) {
                        allEvents.get(eventType).get(eventID).removeRegisteredClientID(customerID);
                        return "Event " + eventID + " Canceled for " + customerID;
                    } else {
                        return "You " + customerID + " Are Not Registered in " + eventID;
                    }
                }
            } else {
                if (allEvents.get(eventType).get(eventID).removeRegisteredClientID(customerID)) {
                    return "Event " + eventID + " Canceled for " + customerID;
                } else {
                    return "You " + customerID + " Are Not Registered in " + eventID;
                }
            }
        } else {
            if (customerID.substring(0, 3).equals(serverID)) {
                if (!serverClients.containsKey(customerID)) {
                    addNewCustomerToClients(customerID);
                    return "You " + customerID + " Are Not Registered in " + eventID;
                } else {
                    clientEvents.get(customerID).get(eventType).remove(eventID);
                }
            }
            //TODO: contact the needed server
            return "server response";
        }
    }

    @Override
    public String removeEvent(String newEventID, String eventType, String customerID) throws RemoteException {
        //TODO: update client events list for the client that was registered in a removed event
        return "Client Events Updated";
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

    private void addNewCustomerToClients(String customerID) {
        ClientModel newCustomer = new ClientModel(customerID);
        serverClients.put(newCustomer.getClientID(), newCustomer);
        clientEvents.put(newCustomer.getClientID(), new HashMap<>());
    }

    private void addCustomersToNextSameEvent(String eventID, String eventType, List<String> registeredClients) {
        //TODO: implement this
        //TODO update clientEvents of this and other servers
    }

    private boolean exceedWeeklyLimit(String customerID, String eventDate) {
        int limit = 0;
        for (int i = 0; i < 3; i++) {
            List<String> registeredIDs = new ArrayList<>();
            switch (i) {
                case 0:
                    registeredIDs = clientEvents.get(customerID).get(EventModel.CONFERENCES);
                    break;
                case 1:
                    registeredIDs = clientEvents.get(customerID).get(EventModel.SEMINARS);
                    break;
                case 2:
                    registeredIDs = clientEvents.get(customerID).get(EventModel.TRADE_SHOWS);
                    break;
            }
            for (String eventID :
                    registeredIDs) {
                if (eventID.substring(6, 8).equals(eventDate.substring(2, 4)) && eventID.substring(8, 10).equals(eventDate.substring(4, 6))) {
                    int day1 = Integer.parseInt(eventID.substring(4, 6));
                    int day2 = Integer.parseInt(eventDate.substring(0, 2));
                    int diff = Math.abs(day2 - day1);
                    if (diff < 6) {
                        limit++;
                    }
                }
                if (limit == 3)
                    return true;
            }
        }
        return false;
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
