package event;

import java.util.Comparator;

/**
 * Created by rafal on 14.05.17.
 */
public class EventComparator implements Comparator<Event> {
  @Override
  public int compare(Event o1, Event o2) {
    if (o1.getIncomingTime() < o2.getIncomingTime()) {
      return -1;
    }
    if (o1.getIncomingTime() > o2.getIncomingTime()) {
      return 1;
    }
    return 0;
  }
}
