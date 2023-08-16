package main;

import java.util.Arrays;
import java.util.Vector;

public class MailManager extends Thread {
  int MAX_MSG;
  public int current_iter;
  public boolean[] isCurrentIterDone;
  private Vector<Message> messages = new Vector<>();

  private Vector<Message> BestValueMessages = new Vector<>();

  private Vector<Message> BestCostMessages = new Vector<>();

  MailManager(int MAX_MSG, int MAX_NODE) {
    this.MAX_MSG = MAX_MSG;
    this.current_iter = 0;
    this.isCurrentIterDone = new boolean[MAX_NODE];
    Arrays.fill(this.isCurrentIterDone, false);
  }

  @Override
  public void run() {
    try {
      while (true) {
        sleep(1000);
      }
    } catch (InterruptedException e) {
    }
  }

  public synchronized void makeTrue(int node) {
    this.isCurrentIterDone[node] = true;
  }

  public synchronized void makeFalse(int node) {
    this.isCurrentIterDone[node] = false;
  }

  public synchronized void checkAllTrue(int callingNode) throws InterruptedException {
    if (!isAllTrue(isCurrentIterDone)) {
      wait();
    }
    notifyAll();
  }

  public synchronized boolean isAllTrue(boolean[] array) {
    for (boolean b : isCurrentIterDone) {

      if (!b) {
        return false;
      }
    }
    return true;
  }

  public synchronized boolean isAllFalse(boolean[] array) {
    for (boolean b : isCurrentIterDone) {
      if (b) {
        return false;
      }
    }
    return true;
  }

  public synchronized void startNewIter() throws InterruptedException {
    Arrays.fill(isCurrentIterDone, false);
  }

  public synchronized void putMessage(Message msg) throws InterruptedException {
    while (messages.size() == MAX_MSG) {
      wait();
    }
    messages.addElement(msg);
    notifyAll();
  }

  public synchronized Message getMessage() throws InterruptedException {
    while (messages.size() == 0) {
      wait();
    }

    Message message = (Message) messages.firstElement();
    messages.removeElement(message);
    notifyAll();
    return message;
  }

  public synchronized void putBestValueMessage(Message msg) throws InterruptedException {
    while (BestValueMessages.size() == MAX_MSG) {
      wait();
    }
    BestValueMessages.addElement(msg);
    notifyAll();
  }

  public synchronized Message getBestValueMessage() throws InterruptedException {
    while (BestValueMessages.size() == 0) {
      wait();
    }
    Message BestValue_message = (Message) BestValueMessages.firstElement();
    BestValueMessages.removeElement(BestValue_message);
    notifyAll();
    return BestValue_message;
  }

  public synchronized void putBestCostMessage(Message msg) throws InterruptedException {
    while (BestCostMessages.size() == MAX_MSG) {
      wait();
    }
    BestCostMessages.addElement(msg);
    notifyAll();
  }

  public synchronized Message getBestCostMessage() throws InterruptedException {
    while (BestCostMessages.size() == 0) {
      wait();
    }
    Message bestcostmessage = (Message) BestCostMessages.firstElement();
    BestCostMessages.removeElement(bestcostmessage);
    notifyAll();
    return bestcostmessage;
  }
}