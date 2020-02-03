package DataModel;

public class EventModel {
    public static final String EVENT_TIME_MORNING = "SEMINARS";
    public static final String EVENT_TIME_AFTERNOON = "CONFERENCES";
    public static final String EVENT_TIME_EVENING = "TRADE_SHOWS";
    public static final String EVENT_SERVER_SHERBROOK = "SHERBROOK";
    public static final String EVENT_SERVER_QUEBEC = "QUEBEC";
    public static final String EVENT_SERVER_MONTREAL = "MONTREAL";
    private String eventType;
    private String eventID;
    private String eventServer;
    private int eventCapacity;
    private String eventDate;
    private String eventTimeSlot;

    public EventModel(String eventType, String eventID, int eventCapacity) {
        this.eventID = eventID;
        this.eventType = eventType;
        this.eventCapacity = eventCapacity;
        this.eventTimeSlot = detectEventTimeSlot();
        this.eventServer = detectEventServer();
        this.eventDate = eventID.substring(4, 6) + "/" + eventID.substring(6, 8) + "/20" + eventID.substring(8, 10);
    }

    private String detectEventServer() {
        if (eventID.substring(0, 3).equalsIgnoreCase("MTL")) {
            return EVENT_SERVER_MONTREAL;
        } else if (eventID.substring(0, 3).equalsIgnoreCase("QUE")) {
            return EVENT_SERVER_QUEBEC;
        } else {
            return EVENT_SERVER_SHERBROOK;
        }
    }

    private String detectEventTimeSlot() {
        if (eventID.substring(3, 4).equalsIgnoreCase("M")) {
            return EVENT_TIME_MORNING;
        } else if (eventID.substring(3, 4).equalsIgnoreCase("A")) {
            return EVENT_TIME_AFTERNOON;
        } else {
            return EVENT_TIME_EVENING;
        }
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventServer() {
        return eventServer;
    }

    public void setEventServer(String eventServer) {
        this.eventServer = eventServer;
    }

    public int getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public void incrementEventCapacity() {
        this.eventCapacity++;
    }

    public boolean decrementEventCapacity() {
        if (!isFull()) {
            this.eventCapacity--;
            return true;
        } else {
            return false;
        }
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTimeSlot() {
        return eventTimeSlot;
    }

    public void setEventTimeSlot(String eventTimeSlot) {
        this.eventTimeSlot = eventTimeSlot;
    }

    public boolean isFull() {
        return getEventCapacity() == 0;
    }

    @Override
    public String toString() {
        return getEventType() + "(" + getEventID() + ") in the " + getEventTimeSlot() + " of " + getEventDate();
    }
}
