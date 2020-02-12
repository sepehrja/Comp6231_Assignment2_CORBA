package ServerInterface;

import DataModel.ClientModel;
import DataModel.EventModel;
import Interface.EventManagementInterface;
import Logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventManagement extends UnicastRemoteObject implements EventManagementInterface {
    public static final int Montreal_Server_Port = 8888;
    public static final int Quebec_Server_Port = 7777;
    public static final int Sherbrooke_Server_Port = 6666;
    public static final String EVENT_SERVER_SHERBROOK = "SHERBROOK";
    public static final String EVENT_SERVER_QUEBEC = "QUEBEC";
    public static final String EVENT_SERVER_MONTREAL = "MONTREAL";
    private String serverID;
    private String serverName;
    // HashMap<EventType, HashMap <EventID, Event>>
    private Map<String, Map<String, EventModel>> allEvents;
    // HashMap<CustomerID, HashMap <EventType, List<EventID>>>
    private Map<String, Map<String, List<String>>> clientEvents;
    // HashMap<ClientID, Client>
    private Map<String, ClientModel> serverClients;

    public EventManagement(String serverID, String serverName) throws RemoteException {
        super();
        this.serverID = serverID;
        this.serverName = serverName;
        allEvents = new ConcurrentHashMap<>();
        allEvents.put(EventModel.CONFERENCES, new ConcurrentHashMap<>());
        allEvents.put(EventModel.SEMINARS, new ConcurrentHashMap<>());
        allEvents.put(EventModel.TRADE_SHOWS, new ConcurrentHashMap<>());
        clientEvents = new ConcurrentHashMap<>();
        serverClients = new ConcurrentHashMap<>();
        addTestData();
    }

    private void addTestData() {
//        ClientModel testManager = new ClientModel(serverID + "M1111");
        ClientModel testCustomer = new ClientModel(serverID + "C1111");
//        serverClients.put(testManager.getClientID(), testManager);
        serverClients.put(testCustomer.getClientID(), testCustomer);
        clientEvents.put(testCustomer.getClientID(), new ConcurrentHashMap<>());

        EventModel sampleConf = new EventModel(EventModel.CONFERENCES, serverID + "M010120", 5);
        sampleConf.addRegisteredClientID(testCustomer.getClientID());
        clientEvents.get(testCustomer.getClientID()).put(sampleConf.getEventType(), new ArrayList<>());
        clientEvents.get(testCustomer.getClientID()).get(sampleConf.getEventType()).add(sampleConf.getEventID());

        EventModel sampleTrade = new EventModel(EventModel.TRADE_SHOWS, serverID + "A020220", 15);
        sampleTrade.addRegisteredClientID(testCustomer.getClientID());
        clientEvents.get(testCustomer.getClientID()).put(sampleTrade.getEventType(), new ArrayList<>());
        clientEvents.get(testCustomer.getClientID()).get(sampleTrade.getEventType()).add(sampleTrade.getEventID());

        EventModel sampleSemi = new EventModel(EventModel.SEMINARS, serverID + "E030320", 20);
        sampleSemi.addRegisteredClientID(testCustomer.getClientID());
        clientEvents.get(testCustomer.getClientID()).put(sampleSemi.getEventType(), new ArrayList<>());
        clientEvents.get(testCustomer.getClientID()).get(sampleSemi.getEventType()).add(sampleSemi.getEventID());

        allEvents.get(EventModel.CONFERENCES).put(sampleConf.getEventID(), sampleConf);
        allEvents.get(EventModel.TRADE_SHOWS).put(sampleTrade.getEventID(), sampleTrade);
        allEvents.get(EventModel.SEMINARS).put(sampleSemi.getEventID(), sampleSemi);
    }

    private static int getServerPort(String branchAcronym) {
        if (branchAcronym.equalsIgnoreCase("MTL")) {
            return Montreal_Server_Port;
        } else if (branchAcronym.equalsIgnoreCase("SHE")) {
            return Sherbrooke_Server_Port;
        } else if (branchAcronym.equalsIgnoreCase("QUE")) {
            return Quebec_Server_Port;
        }
        return 1;
    }

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) throws RemoteException {
        String response;
        if (allEvents.get(eventType).containsKey(eventID)) {
            if (allEvents.get(eventType).get(eventID).getEventCapacity() <= bookingCapacity) {
                allEvents.get(eventType).get(eventID).setEventCapacity(bookingCapacity);
                response = "Success: Event " + eventID + " Capacity increased to " + bookingCapacity;
                try {
                    Logger.serverLog(serverID, "null", " RMI addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: Event Already Exists, Cannot Decrease Booking Capacity";
                try {
                    Logger.serverLog(serverID, "null", " RMI addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            EventModel event = new EventModel(eventType, eventID, bookingCapacity);
            Map<String, EventModel> eventHashMap = allEvents.get(eventType);
            eventHashMap.put(eventID, event);
            allEvents.put(eventType, eventHashMap);
            response = "Success: Event " + eventID + " added successfully";
            try {
                Logger.serverLog(serverID, "null", " RMI addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } else {
            response = "Failed: Cannot Add Event to servers other than " + serverName;
            try {
                Logger.serverLog(serverID, "null", " RMI addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @Override
    public String removeEvent(String eventID, String eventType) throws RemoteException {
        String response;
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (allEvents.get(eventType).containsKey(eventID)) {
                List<String> registeredClients = allEvents.get(eventType).get(eventID).getRegisteredClientIDs();
                allEvents.get(eventType).remove(eventID);
                addCustomersToNextSameEvent(eventID, eventType, registeredClients);
                response = "Success: Event Removed Successfully";
                try {
                    Logger.serverLog(serverID, "null", " RMI removeEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: Event " + eventID + " Does Not Exist";
                try {
                    Logger.serverLog(serverID, "null", " RMI removeEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        } else {
            response = "Failed: Cannot Remove Event from servers other than " + serverName;
            try {
                Logger.serverLog(serverID, "null", " RMI removeEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @Override
    public String listEventAvailability(String eventType) throws RemoteException {
        String response;
        Map<String, EventModel> events = allEvents.get(eventType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + eventType + ":\n");
        if (events.size() == 0) {
            builder.append("No Events of Type " + eventType);
        } else {
            for (EventModel event :
                    events.values()) {
                builder.append(event.toString() + " || ");
            }
            builder.append("\n=====================================\n");
        }
        String otherServer1, otherServer2;
        if (serverID.equals("MTL")) {
            otherServer1 = sendUDPMessage(Sherbrooke_Server_Port, "listEventAvailability", "null", eventType, "null");
            otherServer2 = sendUDPMessage(Quebec_Server_Port, "listEventAvailability", "null", eventType, "null");
        } else if (serverID.equals("SHE")) {
            otherServer1 = sendUDPMessage(Quebec_Server_Port, "listEventAvailability", "null", eventType, "null");
            otherServer2 = sendUDPMessage(Montreal_Server_Port, "listEventAvailability", "null", eventType, "null");
        } else {
            otherServer1 = sendUDPMessage(Montreal_Server_Port, "listEventAvailability", "null", eventType, "null");
            otherServer2 = sendUDPMessage(Sherbrooke_Server_Port, "listEventAvailability", "null", eventType, "null");
        }
        builder.append(otherServer1).append(otherServer2);
        response = builder.toString();
        try {
            Logger.serverLog(serverID, "null", " RMI listEventAvailability ", " eventType: " + eventType + " ", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        String response;
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (!serverClients.containsKey(customerID)) {
                addNewCustomerToClients(customerID);
            }
            EventModel bookedEvent = allEvents.get(eventType).get(eventID);
            if (!bookedEvent.isFull()) {
                if (clientEvents.containsKey(customerID)) {
                    if (clientEvents.get(customerID).containsKey(eventType)) {
                        if (!clientEvents.get(customerID).get(eventType).contains(eventID)) {
                            clientEvents.get(customerID).get(eventType).add(eventID);
                        } else {
                            response = "Failed: Event " + eventID + " Already Booked";
                            try {
                                Logger.serverLog(serverID, customerID, " RMI bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return response;
                        }
                    } else {
                        List<String> temp = new ArrayList<>();
                        temp.add(eventID);
                        clientEvents.get(customerID).put(eventType, temp);
                    }
                } else {
                    Map<String, List<String>> temp = new ConcurrentHashMap<>();
                    List<String> temp2 = new ArrayList<>();
                    temp2.add(eventID);
                    temp.put(eventType, temp2);
                    clientEvents.put(customerID, temp);
                }
                if (allEvents.get(eventType).get(eventID).addRegisteredClientID(customerID) == EventModel.ADD_SUCCESS) {
                    response = "Success: Event " + eventID + " Booked Successfully";
                } else if (allEvents.get(eventType).get(eventID).addRegisteredClientID(customerID) == EventModel.EVENT_FULL) {
                    response = "Failed: Event " + eventID + " is Full";
                } else {
                    response = "Failed: Cannot Add You To Event " + eventID;
                }
                try {
                    Logger.serverLog(serverID, customerID, " RMI bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: Event " + eventID + " is Full";
                try {
                    Logger.serverLog(serverID, customerID, " RMI bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        } else {
            if (!exceedWeeklyLimit(customerID, eventID.substring(4))) {
                String serverResponse = sendUDPMessage(getServerPort(eventID.substring(0, 3)), "bookEvent", customerID, eventType, eventID);
                if (serverResponse.startsWith("Success:")) {
                    if (clientEvents.get(customerID).containsKey(eventType)) {
                        clientEvents.get(customerID).get(eventType).add(eventID);
                    } else {
                        List<String> temp = new ArrayList<>();
                        temp.add(eventID);
                        clientEvents.get(customerID).put(eventType, temp);
                    }
                }
                try {
                    Logger.serverLog(serverID, customerID, " RMI bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return serverResponse;
            } else {
                response = "Failed: You Cannot Book Event in Other Servers For This Week(Max Weekly Limit = 3)";
                try {
                    Logger.serverLog(serverID, customerID, " RMI bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        String response;
        if (!serverClients.containsKey(customerID)) {
            addNewCustomerToClients(customerID);
            response = "Booking Schedule Empty For " + customerID;
            try {
                Logger.serverLog(serverID, customerID, " RMI getBookingSchedule ", "null", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        Map<String, List<String>> events = clientEvents.get(customerID);
        if (events.size() == 0) {
            response = "Booking Schedule Empty For " + customerID;
            try {
                Logger.serverLog(serverID, customerID, " RMI getBookingSchedule ", "null", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        StringBuilder builder = new StringBuilder();
        for (String eventType :
                events.keySet()) {
            builder.append(eventType + ":\n");
            for (String eventID :
                    events.get(eventType)) {
                builder.append(eventID + " ||");
            }
            builder.append("\n=====================================\n");
        }
        response = builder.toString();
        try {
            Logger.serverLog(serverID, customerID, " RMI getBookingSchedule ", "null", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
        String response;
        if (EventModel.detectEventServer(eventID).equals(serverName)) {
            if (customerID.substring(0, 3).equals(serverID)) {
                if (!serverClients.containsKey(customerID)) {
                    addNewCustomerToClients(customerID);
                    response = "Failed: You " + customerID + " Are Not Registered in " + eventID;
                    try {
                        Logger.serverLog(serverID, customerID, " RMI cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    if (clientEvents.get(customerID).get(eventType).remove(eventID)) {
                        allEvents.get(eventType).get(eventID).removeRegisteredClientID(customerID);
                        response = "Success: Event " + eventID + " Canceled for " + customerID;
                        try {
                            Logger.serverLog(serverID, customerID, " RMI cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    } else {
                        response = "Failed: You " + customerID + " Are Not Registered in " + eventID;
                        try {
                            Logger.serverLog(serverID, customerID, " RMI cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    }
                }
            } else {
                if (allEvents.get(eventType).get(eventID).removeRegisteredClientID(customerID)) {
                    response = "Success: Event " + eventID + " Canceled for " + customerID;
                    try {
                        Logger.serverLog(serverID, customerID, " RMI cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    response = "Failed: You " + customerID + " Are Not Registered in " + eventID;
                    try {
                        Logger.serverLog(serverID, customerID, " RMI cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            }
        } else {
            if (customerID.substring(0, 3).equals(serverID)) {
                if (!serverClients.containsKey(customerID)) {
                    addNewCustomerToClients(customerID);
                } else {
                    if (clientEvents.get(customerID).get(eventType).remove(eventID)) {
                        return sendUDPMessage(getServerPort(eventID.substring(0, 3)), "cancelEvent", customerID, eventType, eventID);
                    }
                }
            }
            return "Failed: You " + customerID + " Are Not Registered in " + eventID;
        }
    }

    /**
     * for udp calls only
     *
     * @param oldEventID
     * @param eventType
     * @param customerID
     * @return
     * @throws RemoteException
     */
    public String removeEventUDP(String oldEventID, String eventType, String customerID) throws RemoteException {
        if (!serverClients.containsKey(customerID)) {
            addNewCustomerToClients(customerID);
            return "Failed: You " + customerID + " Are Not Registered in " + oldEventID;
        } else {
            if (clientEvents.get(customerID).get(eventType).remove(oldEventID)) {
                return "Success: Event " + oldEventID + " Was Removed from " + customerID + " Schedule";
            } else {
                return "Failed: You " + customerID + " Are Not Registered in " + oldEventID;
            }
        }
    }

    /**
     * for UDP calls only
     *
     * @param eventType
     * @return
     * @throws RemoteException
     */
    public String listEventAvailabilityUDP(String eventType) throws RemoteException {
        Map<String, EventModel> events = allEvents.get(eventType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + eventType + ":\n");
        if (events.size() == 0) {
            builder.append("No Events of Type " + eventType);
        } else {
            for (EventModel event :
                    events.values()) {
                builder.append(event.toString() + " || ");
            }
        }
        builder.append("\n=====================================\n");
        return builder.toString();
    }

    private String sendUDPMessage(int serverPort, String method, String customerID, String eventType, String eventId) {
        DatagramSocket aSocket = null;
        String result = "";
        String dataFromClient = method + ";" + customerID + ";" + eventType + ";" + eventId;
        try {
            Logger.serverLog(serverID, customerID, " UDP request sent " + method + " ", " eventID: " + eventId + " eventType: " + eventType + " ", " ... ");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            Logger.serverLog(serverID, customerID, " UDP reply received" + method + " ", " eventID: " + eventId + " eventType: " + eventType + " ", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private void addNewCustomerToClients(String customerID) {
        ClientModel newCustomer = new ClientModel(customerID);
        serverClients.put(newCustomer.getClientID(), newCustomer);
        clientEvents.put(newCustomer.getClientID(), new ConcurrentHashMap<>());
    }

    private String getNextSameEvent(Set<String> keySet, String eventType, String oldEventID) {
        List<String> sortedIDs = new ArrayList<String>(keySet);
        sortedIDs.add(oldEventID);
        Collections.sort(sortedIDs, new Comparator<String>() {
            @Override
            public int compare(String ID1, String ID2) {
                Integer timeSlot1 = 0;
                switch (ID1.substring(3, 4).toUpperCase()) {
                    case "M":
                        timeSlot1 = 1;
                        break;
                    case "A":
                        timeSlot1 = 2;
                        break;
                    case "E":
                        timeSlot1 = 3;
                        break;
                }
                Integer timeSlot2 = 0;
                switch (ID2.substring(3, 4).toUpperCase()) {
                    case "M":
                        timeSlot2 = 1;
                        break;
                    case "A":
                        timeSlot2 = 2;
                        break;
                    case "E":
                        timeSlot2 = 3;
                        break;
                }
                Integer date1 = Integer.parseInt(ID1.substring(8, 10) + ID1.substring(6, 8) + ID1.substring(4, 6));
                Integer date2 = Integer.parseInt(ID2.substring(8, 10) + ID2.substring(6, 8) + ID2.substring(4, 6));
                int dateCompare = date1.compareTo(date2);
                int timeSlotCompare = timeSlot1.compareTo(timeSlot2);
                if (dateCompare == 0) {
                    return ((timeSlotCompare == 0) ? dateCompare : timeSlotCompare);
                } else {
                    return dateCompare;
                }
            }
        });
        int index = sortedIDs.indexOf(oldEventID) + 1;
        for (int i = index; i < sortedIDs.size(); i++) {
            if (!allEvents.get(eventType).get(sortedIDs.get(i)).isFull()) {
                return sortedIDs.get(i);
            }
        }
        return "Failed";
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
                    int week1 = Integer.parseInt(eventID.substring(4, 6)) / 7;
                    int week2 = Integer.parseInt(eventDate.substring(0, 2)) / 7;
//                    int diff = Math.abs(day2 - day1);
                    if (week1 == week2) {
                        limit++;
                    }
                }
                if (limit == 3)
                    return true;
            }
        }
        return false;
    }

    private void addCustomersToNextSameEvent(String oldEventID, String eventType, List<String> registeredClients) throws RemoteException {
        for (String customerID :
                registeredClients) {
            if (customerID.substring(0, 3).equals(serverID)) {
                clientEvents.get(customerID).get(eventType).remove(oldEventID);
                String nextSameEventResult = getNextSameEvent(allEvents.get(eventType).keySet(), eventType, oldEventID);
                if (nextSameEventResult.equals("Failed")) {
                    return;
                } else {
                    bookEvent(customerID, nextSameEventResult, eventType);
                }
            } else {
                sendUDPMessage(getServerPort(customerID.substring(0, 3)), "removeEvent", customerID, eventType, oldEventID);
            }
        }
    }

    public Map<String, Map<String, EventModel>> getAllEvents() {
        return allEvents;
    }

    public Map<String, Map<String, List<String>>> getClientEvents() {
        return clientEvents;
    }

    public Map<String, ClientModel> getServerClients() {
        return serverClients;
    }
}
