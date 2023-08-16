package main;

public class Edge {
  public Edge(int node1, int node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  @Override
  public String toString() {
    return "Edge{" + "node1=" + node1 + ", node2=" + node2 + '}';
  }

  public int getNode1() {
    return node1;
  }

  public int getNode2() {
    return node2;
  }

  int node1, node2;
}
