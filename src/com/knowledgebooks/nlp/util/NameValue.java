package com.knowledgebooks.nlp.util;

import java.util.List;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
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