package main;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class RunnableDemo implements Runnable {
  public Thread getT() {
    return t;
  }

  private Thread t;
  private String threadName;
  private int threadNo;
  private Agent agent;
  MailManager[] mailManagers;
  private ConcurrentHashMap<String, Long> timeResults;

  public ConcurrentHashMap<String, Long> getTimeResults() {
    return timeResults;
  }

  public ConcurrentHashMap<Integer, ArrayList<Long>> getIterTimeResults() {
    return iterTimeResults;
  }

  public ArrayList<Long> getSimTimeArray() {
    return simTimeArray;
  }

  private ConcurrentHashMap<Integer, ArrayList<Long>> iterTimeResults;
  private ArrayList<Long> simTimeArray;

  public ArrayList<Long> getRunTimeArray() {
    return runTimeArray;
  }

  private ArrayList<Long> runTimeArray;

  public ArrayList<Double> getIterSol() {
    return iterSol;
  }

  private ArrayList<Double> iterSol;
  static volatile boolean exit = false;

  double[] bp;
  Problem problem;

  RunnableDemo(Problem problem, String agentType, MailManager[] mailManagers, int threadNo, String name,
      int maxIteration, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb,
      double domain_ub, boolean isMax, ArrayList<Edge> edgelist, int[][] indexToEdge,
      HashMap<Edge, Constraint> constraints, double alpha, double beta, int termination) {
    this.problem = problem;
    this.threadNo = threadNo;
    this.threadName = name;
    this.mailManagers = mailManagers;
    this.timeResults = new ConcurrentHashMap<>();
    this.iterTimeResults = new ConcurrentHashMap<>();
    this.simTimeArray = new ArrayList<>();
    this.iterSol = new ArrayList<>();
    this.runTimeArray = new ArrayList<>();
    this.bp = new double[population];
    if (agentType.equals("PLSA"))
      agent = new Agent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub,
          maxIteration, isMax, edgelist, indexToEdge, constraints, alpha, beta, termination);
  }

  public void run() {
    try {
      long start = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
      while (this.agent.getCurrentIter() < this.agent.getMaxIteration()) {
        this.agent.getMailManager()[0].startNewIter();
        if (this.agent.getCurrentIter() == 0) {
          this.agent.initValues();
        } else {
          this.agent.updateValues();
        }
        this.agent.sendValueMessage();
        this.agent.receiveValueMessage();
        this.agent.calculateCost();
        this.agent.setState();
        this.agent.sendBestValueMessage();
        this.agent.receiveBestValueMessage();
        this.agent.calculateCost_BestValue();
        this.agent.receiveBestCostMessage();
        if (threadNo != 0) {
          this.agent.sendBestCostMessage();
        } else {
          this.agent.sumRootChildBestCost();
        }
        if (threadNo == 0) {
          System.out
              .println("Iteration " + this.agent.getCurrentIter() + " total utility is " + this.agent.getGbest_val());
          long endTime = System.currentTimeMillis();
          long diff = endTime - this.agent.starttime;
          long iterEnd = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
          long iterDiffInMillis = (iterEnd - start) / 1000000;
          this.simTimeArray.add(iterDiffInMillis);
          this.runTimeArray.add(diff);
          this.iterTimeResults.put(threadNo, this.simTimeArray);
          this.iterSol.add(this.agent.getGbest_val());
        }
        this.agent.getMailManager()[0].makeTrue(threadNo);
        this.agent.setCurrentIter(this.agent.getCurrentIter() + 1);
        Thread.sleep(50);
      }

    } catch (InterruptedException e) {
      System.out.println("Thread " + threadName + " interrupted.");
    }
    if (threadNo == 0) {
      int same_it = problem.sameProbIt + 1;
      try {
        if (same_it < problem.MAX_SAME_Problem) {
          problem.setSameProbIt(same_it);
          problem.newProblem();
        } else {
          int diff_it = problem.problemNo + 1;
          if (diff_it < problem.MAX_DIFF_PROB) {
            problem.setSameProbIt(0);
            problem.setProblemNo(diff_it);
            problem.newProblem();
          } else {
            System.out.println("Complete!");
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void start() {
    if (t == null) {

      t = new Thread(this, threadName);

      t.start();

    }
  }
}