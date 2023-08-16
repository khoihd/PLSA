package main;

//import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.util.*;

public class Agent {
  protected boolean isMax;
  protected MailManager[] mailManager;
  protected int agentNo;
  protected int population;
  protected double domain_lb, domain_ub;

  protected double[] position;
  protected String state;
  protected double BestLocalPosition;
  protected int ContinuousSame;
  protected double Pre_BestLocalPosition;
  protected HashMap<Integer, double[]> inbox;
  protected HashMap<Integer, Double> inboxToStoreBestLocalPosition;
  protected HashMap<Integer, Double> Constraint_inbox_BestLocal;
  protected double[] SortNo;

  protected double globalCost;
  protected double gbest_val;

  public double getGbest_val() {
    return gbest_val;
  }

  protected Vector<Integer> neighbors;
  private int parent;
  protected Vector<Integer> child;

  protected int currentIter = 0;

  public int getCurrentIter() {
    return currentIter;
  }

  public void setCurrentIter(int currentIter) {
    this.currentIter = currentIter;
  }

  public int getMaxIteration() {
    return maxIteration;
  }

  private int maxIteration;

  protected ArrayList<Edge> edgelist;
  protected int[][] indexToEdge;
  protected HashMap<Edge, Constraint> constraints;

  protected double[] cons_cost;
  protected double cons_cost_BestLocal;
  double alpha;
  double beta;
  int termination;
  long starttime;

  public MailManager[] getMailManager() {
    return mailManager;
  }

  Agent(MailManager[] mailManager, int AgentNo, Vector<Integer> neighbors, int parent, Vector<Integer> child,
      int population, double domain_lb, double domain_ub, int maxIteration, boolean ismax, ArrayList<Edge> edgelist,
      int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double alpha, double beta, int termination) {
    this.mailManager = mailManager;
    this.agentNo = AgentNo;
    this.population = population;
    this.domain_lb = domain_lb;
    this.domain_ub = domain_ub;
    this.position = new double[this.population];
    this.state = "Active";
    this.BestLocalPosition = 0;
    this.ContinuousSame = 0;
    this.Pre_BestLocalPosition = 0;

    this.inbox = new HashMap<>();
    this.inboxToStoreBestLocalPosition = new HashMap<>();
    this.Constraint_inbox_BestLocal = new HashMap<>();

    this.SortNo = new double[population];
    Arrays.fill(this.SortNo, -1);

    this.cons_cost = new double[this.population];
    this.cons_cost_BestLocal = 0;

    this.neighbors = neighbors;
    this.parent = parent;
    this.child = child;

    this.isMax = ismax;
    this.maxIteration = maxIteration;

    this.edgelist = edgelist;
    this.indexToEdge = indexToEdge;
    this.constraints = constraints;

    this.alpha = alpha;
    this.beta = beta;
    this.termination = termination;
    this.starttime = System.currentTimeMillis();
  }

  // Initialization
  public void initValues() {
    for (int i = 0; i < this.population; i++) {
      double randomValue = ((Math.random() * (this.domain_ub - this.domain_lb)) + this.domain_lb);
      assert (randomValue <= this.domain_ub && randomValue >= this.domain_lb);
      this.position[i] = randomValue;
    }
  }

  // Index utility
  public static int getIndex(double[] arr, double value) {
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == value) {
        return i;
      }
    }
    return -99999;
  }

  // Update values
  public void updateValues() {
    double[] pos = new double[population];
    System.arraycopy(this.position, 0, pos, 0, population);
    for (int i = 0; i < population; i++) {
      if (this.state.equals("Hold")) {
        pos[i] = BestLocalPosition;
      } else {
        if (Math.random() < this.beta) {
          pos[i] = ((Math.random() * (this.domain_ub - this.domain_lb)) + this.domain_lb);
        } else {
          pos[i] = (1 - this.alpha) * this.position[i]
              + this.alpha * (BestLocalPosition + this.SortNo[1] - this.SortNo[population - 1]);
        }
      }
    }
    for (int i = 0; i < population; i++) {
      position[i] = pos[i];
      if (this.position[i] < this.domain_lb) {
        this.position[i] = this.domain_lb;
      }
      if (this.position[i] > this.domain_ub) {
        this.position[i] = this.domain_ub;
      }
    }
  }

  // Send Messages
  public void sendValueMessage() throws InterruptedException {
    for (int neigh : neighbors) {
      Message valueMsg = new Message(this.agentNo, neigh, 202, this.position);
      this.mailManager[neigh].putMessage(valueMsg);
    }
  }

  // Receive Message
  public void receiveValueMessage() throws InterruptedException {
    int num = 0;
    while (num < neighbors.size()) {
      Message rcvdValueMsg = this.mailManager[this.agentNo].getMessage();
      this.inbox.put(rcvdValueMsg.getSenderId(), rcvdValueMsg.getMsgDoubleContent());
      num += 1;
    }
  }

  // Calculate the sum of local utilities
  public void calculateCost() {
    double[] consCostList = new double[population];
    Arrays.fill(consCostList, 0.0);
    for (int neigh : neighbors) {
      Constraint cons = this.constraints.get(edgelist.get(indexToEdge[this.agentNo][neigh]));
      for (int i = 0; i < population; i++) {
        double cons_calc = cons.getA() * Math.pow(this.position[i], 2) + cons.getB() * this.position[i]
            + cons.getC() * this.position[i] * this.inbox.get(neigh)[i] + cons.getD() * this.inbox.get(neigh)[i]
            + cons.getE() * Math.pow(this.inbox.get(neigh)[i], 2) + cons.getF();
        consCostList[i] += cons_calc;
      }
    }
    double[] cost = new double[consCostList.length];
    System.arraycopy(consCostList, 0, cost, 0, consCostList.length);
    Arrays.sort(cost);
    for (int i = 0; i < consCostList.length; i++) {
      int temp = getIndex(consCostList, cost[i]);
      this.SortNo[i] = this.position[temp];
      if (i == 0) {
        this.BestLocalPosition = SortNo[i];
      }
    }
  }

  // Update State
  public void setState() {
    if (this.BestLocalPosition == this.Pre_BestLocalPosition) {
      this.ContinuousSame += 1;
    } else {
      this.ContinuousSame = 0;
    }
    if (this.ContinuousSame > this.termination) {
      this.state = "Hold";
    }
    this.Pre_BestLocalPosition = this.BestLocalPosition;
  }

  // Send best value
  public void sendBestValueMessage() throws InterruptedException {
    for (int neigh : neighbors) {
      Message BestValueMsg = new Message(this.agentNo, neigh, 210, this.BestLocalPosition);
      this.mailManager[neigh].putBestValueMessage(BestValueMsg);
    }
  }

  // receive best value
  public void receiveBestValueMessage() throws InterruptedException {
    int num = 0;
    while (num < neighbors.size()) {
      Message rBestValueMsg = this.mailManager[this.agentNo].getBestValueMessage();
      this.inboxToStoreBestLocalPosition.put(rBestValueMsg.getSenderId(), rBestValueMsg.getMsgValueContent());
      num += 1;
    }
  }

  // Calculate the sum of local utilities by best value
  public void calculateCost_BestValue() {
    double consCost = 0;
    for (int neigh : neighbors) {
      Constraint cons = this.constraints.get(edgelist.get(indexToEdge[this.agentNo][neigh]));
      double cons_cal = cons.getA() * Math.pow(this.BestLocalPosition, 2) + cons.getB() * this.BestLocalPosition
          + cons.getC() * this.BestLocalPosition * this.inboxToStoreBestLocalPosition.get(neigh)
          + cons.getD() * this.inboxToStoreBestLocalPosition.get(neigh)
          + cons.getE() * Math.pow(this.inboxToStoreBestLocalPosition.get(neigh), 2) + cons.getF();
      consCost += cons_cal;
    }
    this.cons_cost_BestLocal = consCost;
  }

  // Send the utilities
  public void sendBestCostMessage() throws InterruptedException {
    double sumChildCost = 0;
    for (int num : child) {
      sumChildCost += this.Constraint_inbox_BestLocal.get(num);
    }
    double sumTotalCost;
    sumTotalCost = sumChildCost + this.cons_cost_BestLocal;
    int neigh = this.parent;
    Message BestCostMsg = new Message(this.agentNo, neigh, 211, sumTotalCost);
    this.mailManager[neigh].putBestCostMessage(BestCostMsg);
  }

  // Receive the utilities
  public void receiveBestCostMessage() throws InterruptedException {
    int num = 0;
    while (num < child.size()) {
      Message rBestCostValueMsg = this.mailManager[this.agentNo].getBestCostMessage();
      this.Constraint_inbox_BestLocal.put(rBestCostValueMsg.getSenderId(), rBestCostValueMsg.getMsgValueContent());
      num += 1;
    }
  }

  // Root agent receives the all utilities
  public void sumRootChildBestCost() throws InterruptedException {
    double sumChildCost = 0;
    for (int num : child) {
      sumChildCost += this.Constraint_inbox_BestLocal.get(num);
    }
    this.globalCost = sumChildCost + this.cons_cost_BestLocal;
    this.globalCost /= 2;
    this.gbest_val = this.globalCost;
  }
}