package com.knowledgebooks.nlp.util;

/**
 * Utility class that holds 2 integers.
 *
 */

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class IPair {
  private int first, second;

  public IPair(int o1, int o2) {
    first = o1;
    second = o2;
  }

  public int getFirst() {
    return first;
  }

  public int getSecond() {
    return second;
  }

  public String toString() {
    return "" + first + " / " + second;
  }
}


