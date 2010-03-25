package com.knowledgebooks.nlp.util;

/**
 * Utility class that holds 2 integers.
 *
 */

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 * http://markwatson.com/commerciallicense.txt
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


