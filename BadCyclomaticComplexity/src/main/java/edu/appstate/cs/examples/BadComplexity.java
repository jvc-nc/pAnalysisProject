/*
 * A sample file for the checker to process.
 */

package edu.appstate.cs.examples;

public class BadComplexity {
  public void bool(String sd1) {
  	System.out.println(sd1);
  }

  public static void main(String[] args) {
    String m = "This is a message";

    BadComplexity b = new BadComplexity();
    b.bool(m);

    for (int l = 0; l < 1; ++l) {
      System.out.println("Loop iteration: " + l);
    }
  }
}
