package sipproxy;

import message.Message;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by rafal on 14.05.17.
 */
public class SipProxy {
  private Message inOperation;
  private boolean isFree;
  private Queue<Message> fifoQueue = new ArrayDeque<>();
  public int sipProxyId;


  public SipProxy() {
    inOperation = null;
    isFree = true;
  }


  public Message getInOperation() {
    return inOperation;
  }


  public void setInOperation(Message inOperation) {
    this.inOperation = inOperation;
  }


  public boolean isFree() {
    return isFree;
  }


  public void setFree(boolean free) {
    isFree = free;
  }


  public Queue<Message> getFifoQueue() {
    return fifoQueue;
  }


  public void setFifoQueue(Queue<Message> fifoQueue) {
    this.fifoQueue = fifoQueue;
  }
}
