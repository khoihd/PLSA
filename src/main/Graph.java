package main;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class Graph {
  @Override
  public String toString() {
    return "Graph{" + "children=" + Arrays.toString(children) + ", neighbors=" + Arrays.toString(neighbors)
        + ", parent=" + Arrays.toString(parent) + ", level=" + Arrays.toString(level) + '}';
  }

  private Vector<Integer>[] adj;

  public Vector<Integer>[] getChildren() {
    return children;
  }

  public Vector<Integer>[] getNeighbors() {
    return neighbors;
  }

  public int[] getParent() {
    return parent;
  }

  public int[] getLevel() {
    return level;
  }

  private Vector<Integer>[] children;
  private Vector<Integer>[] neighbors;
  private int MAX_NODE;
  private int[] parent;
  private int[] level;

  Graph(Vector<Integer>[] adj, int MAX_NDOE) {
    this.adj = adj;
    this.MAX_NODE = MAX_NDOE;
    this.parent = new int[MAX_NDOE];
    this.children = new Vector[MAX_NODE];
    this.neighbors = new Vector[MAX_NDOE];
    for (int i = 0; i < MAX_NODE; i++) {
      this.children[i] = new Vector<>();
      this.neighbors[i] = new Vector<>();

    }
    this.level = new int[MAX_NDOE];
  }

  public void bfs() {
    boolean[] visited = new boolean[this.MAX_NODE];
    Arrays.fill(visited, false);
    LinkedList<Integer> queue = new LinkedList<Integer>();

    int source = 0;
    queue.add(source);
    visited[source] = true;
    this.parent[source] = -1;
    while (!queue.isEmpty()) {
      int u = queue.remove();
      for (int v : adj[u]) {
        if (!visited[v]) {
          visited[v] = true;
          this.children[u].addElement(v);
          this.neighbors[u].addElement(v);
          this.parent[v] = u;
          this.level[v] = this.level[u] + 1;
          queue.add(v);

        } else {
          this.neighbors[u].addElement(v);
        }
      }
    }

  }
}
