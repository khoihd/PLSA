package main;

import java.io.IOException;

public class Main {
  public static void main(String args[]) throws IOException, InterruptedException {

    /** Parameters */
    /**
     * MAX_SAME_PROB = No. of runs for a problem MAX_DIFF_PROB = No. of problem
     * MAX_NODE = No. of Agents alpha = The learning rate beta = The mutation
     * probability termination = No. of the iteration when agent becomes "Hold"
     */

    int MAX_SAME_PROB = 1;
    int MAX_DIFF_PROB = 1;
    int MAX_NODE = 100;
    double alpha = 0.9;
    double beta = 0.01;
    int termination = 100;
    Problem p1 = new Problem(0, 0, MAX_SAME_PROB, MAX_DIFF_PROB, MAX_NODE, alpha, beta, termination);
    p1.newProblem();
  }
}