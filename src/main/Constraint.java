package main;

public class Constraint {
  public Constraint(int a, int b, int c, int d, int e, int f) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
  }

  @Override
  public String toString() {
    return "Constraint{" + "a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + ", e=" + e + ", f=" + f + '}';
  }

  public int getA() {
    return a;
  }

  public int getB() {
    return b;
  }

  public int getC() {
    return c;
  }

  public int getD() {
    return d;
  }

  public int getE() {
    return e;
  }

  public int getF() {
    return f;
  }

  private int a, b, c, d, e, f;
}