import event.Event;
import event.EventComparator;
import event.EventType;
import message.Message;
import message.MessageType;
import sipclient.SipClient;
import sipproxy.SipProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import static sun.misc.Version.print;

/**
 * Created by rafal on 14.05.17.
 */
public class Simulation {
  SipProxy sipProxy1, sipProxy2;
  double time = 0;
  private PriorityQueue<Event> priorityQueue = new PriorityQueue<>(new EventComparator());
  private Map<Integer, SipClient> sipClients = new HashMap<>();
  int callID = 1;

  ///--------STATISICS-----------------------
  int servicedClients = 0;
  int lostClients = 0;
  int servicedMessages = 0;
  int delayNum1 = 0;
  double overallDelay1 = 0;
  double underQ1 = 0;
  double underB1 = 0;
  double lastEventTime = 0;
  int delayNum2 = 0;
  double overallDelay2 = 0;
  double underQ2 = 0;
  double underB2 = 0;
  int startedConversation = 0;
  double setingConvTime = 0;
//-------------------------------------

  Random rand = new Random();

  private double lambdaIncClient = 30;
  private double lambdaMessage = 300;
  private double lambdaCall = 1;


  private Simulation() {
    sipProxy1 = new SipProxy();
    sipProxy1.sipProxyId = 1;
    sipProxy2 = new SipProxy();
    sipProxy2.sipProxyId = 2;
    lambdaCall /= 3;
  }


  public void initSimuation() {
    createNewClientEvent();
  }


  private void createNewClientEvent() {
    double incTime = time + Math.log(1 - rand.nextDouble()) / (-lambdaIncClient);
    Event newEvent = new Event(EventType.NEW_CLIENT_INCOMING, incTime, callID, true);
    callID++;
    priorityQueue.offer(newEvent);
  }


  private void runSimulation() {
    initSimuation();
    while (servicedClients < 100) {
      Event currEvent = priorityQueue.poll();
      time = currEvent.getIncomingTime();
      calculateCurrentStats(currEvent);
      switch (currEvent.getType()) {
        case NEW_CLIENT_INCOMING:
          newClientIncoming(currEvent);
          break;
        case FINISH_SERVICE:
          finishService(currEvent);
          break;
        case FINISH_CALL:
          finishCall(currEvent);
          break;
        case TIMEOUT:
          timeOut(currEvent);
      }
      lastEventTime = time;
    }
    printStatistics();
  }


  public void newClientIncoming(Event currEvent) {
    createTimeoutEvent(currEvent);
    createNewClientEvent();
    SipClient sipClient = new SipClient(currEvent.getCallID(), true);
    sipClient.setIncTime(currEvent.getIncomingTime());
    sipClients.put(currEvent.getCallID(), sipClient);
    SipProxy currSipProxy = currEvent.isFirstProxy() ? sipProxy1 : sipProxy2;
    Message message = new Message(MessageType.INVITE, currEvent.getIncomingTime(), currEvent.getCallID(), currSipProxy);

    handleIncomingMessage(message);
  }


  private void handleIncomingMessage(Message message) {
    SipProxy currSipProxy = message.getCurrentProxy();
    if (currSipProxy.isFree()) {
      //TODO:add stats
      currSipProxy.setInOperation(message);
      currSipProxy.setFree(false);
      createEndEvent(message);
    } else {
      //TODO:check if full queue
      if (message.getCurrentProxy() == sipProxy1) {
        delayNum1 += sipProxy1.getFifoQueue().size();
      } else {
        delayNum2 += sipProxy2.getFifoQueue().size();
      }
      currSipProxy.getFifoQueue().offer(message);
    }
  }


  private void createEndEvent(Message message) {
    double incTime = time + Math.log(1 - rand.nextDouble()) / (-lambdaMessage);
    Event event = new Event(EventType.FINISH_SERVICE, incTime, message.getCallID(),
        message.getCurrentProxy() == sipProxy1);
    priorityQueue.offer(event);
  }


  public void createFinishCallEvent(Message message) {
    double incTime = time + Math.log(1 - rand.nextDouble()) / (-lambdaCall);
    // double incTime = time + 3;
    Event event = new Event(EventType.FINISH_CALL, incTime, message.getCallID(),
        message.getCurrentProxy() == sipProxy1);
    priorityQueue.offer(event);
  }


  private void createTimeoutEvent(Event currEvent) {
    Event event = new Event(EventType.TIMEOUT, time + 1, currEvent.getCallID(), true);
    priorityQueue.offer(event);
  }


  private void finishService(Event currEvent) {
    servicedMessages++;
    SipProxy currSipProxy = currEvent.isFirstProxy() ? sipProxy1 : sipProxy2;
    Message message = currSipProxy.getInOperation();
    MessageType messageType = message.getType();
    int id = message.getCallID();
    boolean work = true;
    //TODO:check if works properly
    Message newMessage = null;
    while (currSipProxy.getFifoQueue().size() > 0 && work) {


      newMessage = currSipProxy.getFifoQueue().poll();

      if (isTimeout(newMessage)) {
        continue;
      } else {
        work = false;
      }

      currSipProxy.setInOperation(newMessage);
      createEndEvent(newMessage);
    }
    if (work) {
      currSipProxy.setInOperation(null);
      currSipProxy.setFree(true);
    } else {
      currSipProxy.setInOperation(newMessage);
      currSipProxy.setFree(false);
    }
    message.setCurrentProxy(currSipProxy == sipProxy1 ? sipProxy2 : sipProxy1);
    MessageType type = null;
    switch (message.getType()) {
      case INVITE:
        type = MessageType.OK_INVITE;
        break;
      case OK_INVITE:
        type = MessageType.ACK;
        break;
      case ACK:
        SipClient sipClient = sipClients.get(message.getCallID());
        sipClient.timeOutOn = false;
        setingConvTime += time - sipClient.getIncTime();
        startedConversation++;

        createFinishCallEvent(message);
        break;
      case BYE:
        type = MessageType.OK_BYE;
        break;
      case OK_BYE:
        servicedClients++;
        break;
    }
    if (type != null) {
      Message message1 = new Message(type, time, message.getCallID(), message.getCurrentProxy());
      handleIncomingMessage(message1);
    }


  }


  private boolean isTimeout(Message message) {
    return !sipClients.get(message.getCallID()).isActive();
  }


  private void finishCall(Event currEvent) {
    Message newMessage = new Message(MessageType.BYE, time, currEvent.getCallID(), sipProxy1);
    handleIncomingMessage(newMessage);
  }


  private void timeOut(Event currEvent) {
    if (sipClients.get(currEvent.getCallID()).timeOutOn) {
      sipClients.get(currEvent.getCallID()).setActive(false);
      lostClients++;
    }
  }


  private void calculateCurrentStats(Event currentEvent) {
    overallDelay1 += time - currentEvent.getIncomingTime();
    overallDelay2 += time - currentEvent.getIncomingTime();
    if (sipProxy1.isFree() != true) {
      underB1 += time - lastEventTime;
    }
    if (sipProxy2.isFree() != true) {
      underB2 += time - lastEventTime;
    }
    underQ1 = sipProxy1.getFifoQueue().size() * (time - lastEventTime);
    underQ2 = sipProxy2.getFifoQueue().size() * (time - lastEventTime);
  }


  public void printStatistics() {
    double d = (delayNum1 + delayNum2) / servicedMessages;
    double q = (underQ1 + underQ2) / servicedMessages;
    double b1 = underB1 / time;
    double b2 = underB2 / time;
    System.out.println("sredni czas oczekiwania" + d);
    System.out.println("sredni ilosc wiadomości w kolejce " + q);
    System.out.println("Zajętość serwera 1 : " + b1);
    System.out.println("Zajętośc serwera 2: " + b2);
    System.out.println("Straceni klienci : " + lostClients);
    System.out.println("Średni czas zestawiania połaczenia : " + setingConvTime/startedConversation);
  }


  public static void main(String args[]) {
    Simulation simulation = new Simulation();
    simulation.runSimulation();
  }
}
