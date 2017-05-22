package event;

/**
 * Created by rafal on 14.05.17.
 */
public class Event {
    private EventType type;
    private double incomingTime;
    private int callID;
    private boolean firstProxy;

    public Event() {
    }

    public Event(EventType type, double incomingTime, int callID, boolean firstProxy) {
        this.type = type;
        this.incomingTime = incomingTime;
        this.callID = callID;
        this.firstProxy = firstProxy;
    }


    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public double getIncomingTime() {
        return incomingTime;
    }

    public void setIncomingTime(double incomingTime) {
        this.incomingTime = incomingTime;
    }

    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }

    public boolean isFirstProxy() {
        return firstProxy;
    }

    public void setFirstProxy(boolean firstProxy) {
        this.firstProxy = firstProxy;
    }

}
