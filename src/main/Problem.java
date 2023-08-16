package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Problem {
  int problemNo;
  int MAX_SAME_Problem, MAX_DIFF_PROB;
  int MAX_NODE;
  double alpha;
  double beta;
  int termination;

  public void setSameProbIt(int sameProbIt) {
    this.sameProbIt = sameProbIt;
  }

  int sameProbIt;

  public Problem(int problemNo, int sameProbIt, int MAX_SAME_PROB, int MAX_DIFF_PROB, int MAX_NODE, double alpha,
      double beta, int termination) {
    this.problemNo = problemNo;
    this.sameProbIt = sameProbIt;

    this.MAX_SAME_Problem = MAX_SAME_PROB;
    this.MAX_DIFF_PROB = MAX_DIFF_PROB;

    this.MAX_NODE = MAX_NODE;
    this.alpha = alpha;
    this.beta = beta;
    this.termination = termination;
  }

  public void setProblemNo(int problemNo) {
    this.problemNo = problemNo;
  }

  public void newProblem() throws IOException {
    Parser parser = new Parser();
    Parse_Output output = parser.getGraph();

    RunnableDemo R[] = new RunnableDemo[MAX_NODE];

    Vector<Integer>[] adj = new Vector[MAX_NODE];
    int[][] indexToEdge = new int[MAX_NODE][MAX_NODE];

    ArrayList<Edge> edgelist = output.all_edges.get(problemNo);
    for (int i = 0; i < MAX_NODE; i++)
      adj[i] = new Vector<>();

    int idx = 0;
    for (Edge e : edgelist) {
      int u = e.getNode1();
      int v = e.getNode2();
      indexToEdge[u][v] = idx;
      idx += 1;
      adj[u].addElement(v);
    }

    Graph graph = new Graph(adj, MAX_NODE);
    graph.bfs();

    MailManager mailManager[] = new MailManager[MAX_NODE];

    for (int j = 0; j < MAX_NODE; j++) {

      mailManager[j] = new MailManager(graph.getNeighbors()[j].size(), MAX_NODE);
      mailManager[j].start();
    }
    for (int i = 0; i < MAX_NODE; i++) {
      R[i] = new RunnableDemo(this, "PLSA", mailManager, i, "Agent-" + i, 500, graph.getNeighbors()[i],
          graph.getParent()[i], graph.getChildren()[i], 1000, -50, 50, false, edgelist, indexToEdge,
          output.all_cons.get(problemNo), alpha, beta, termination);
      R[i].start();
    }
  }
}
