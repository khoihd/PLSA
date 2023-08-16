package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Parse_Output {
  ArrayList<Integer> all_nodes;
  ArrayList<ArrayList<Edge>> all_edges;
  ArrayList<HashMap<Edge, Constraint>> all_cons;

  public Parse_Output(ArrayList<Integer> all_nodes, ArrayList<ArrayList<Edge>> all_edges,
      ArrayList<HashMap<Edge, Constraint>> all_cons) {
    this.all_nodes = all_nodes;
    this.all_edges = all_edges;
    this.all_cons = all_cons;

  }

  @Override
  public String toString() {
    return "Parse_Output{" + "all_nodes=" + all_nodes + ", all_edges=" + all_edges + ", all_cons=" + all_cons + '}';
  }

}
