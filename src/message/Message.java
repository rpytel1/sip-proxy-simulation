package message;

import sipproxy.SipProxy;

/**
 * Created by rafal on 14.05.17.
 */
public class Message {

    private MessageType type;
    private double incomingTime;
    private int callID;
    private SipProxy currentProxy;

    public Message() {
    }

    public Message(MessageType type, double incomingTime, int callID, SipProxy currentProxy) {
        this.type = type;
        this.incomingTime = incomingTime;
        this.callID = callID;
        this.currentProxy = currentProxy;
    }


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
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

    public SipProxy getCurrentProxy() {
        return currentProxy;
    }

    public void setCurrentProxy(SipProxy currentProxy) {
        this.currentProxy = currentProxy;
    }
}
