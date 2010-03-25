package com.knowledgebooks.nlp.util;

import java.util.List;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 * http://markwatson.com/commerciallicense.txt
 */

public class NameValue<K, V> {
  private K name = null;
  private V value = null;

  public NameValue(K k, V v) {
    this.name = k;
    this.value = v;
  }

  public K getName() {
    return this.name;
  }

  public V getValue() {
    return this.value;
  }

  public void setValue(V val) {
    this.value = val;
  }

  public String toString() {
    return "" + name + ":" + value + "";
  }
}