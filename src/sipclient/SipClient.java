package sipclient;


public class SipClient {
  int callID;
  boolean active;
  public boolean timeOutOn = true;
  double incTime;


  public SipClient(int callID, boolean active) {
    this.callID = callID;
    this.active = active;
  }

  public double getIncTime() {
    return incTime;
  }


  public void setIncTime(double incTime) {
    this.incTime = incTime;
  }


  public int getCallID() {
    return callID;
  }


  public void setCallID(int callID) {
    this.callID = callID;
  }


  public boolean isActive() {
    return active;
  }


  public void setActive(boolean active) {
    this.active = active;
  }
}
