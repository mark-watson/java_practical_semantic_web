package com.knowledgebooks.nlp;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class PhraseList {
  public static final int MAX_PHRASES = 10;
  private String[] phrases = new String[MAX_PHRASES];
  private float[] scores = new float[MAX_PHRASES];
  private int numPhrases = 0;

  /**
   * Public methods:
   */

  public int getNumPhrases() {
    return numPhrases;
  }

  public String getPhrase(int index) {
    if (index < 0 || index >= numPhrases) return ""; // really, an error condition
    return phrases[index];
  }

  public float getScore(int index) {
    if (index < 0 || index >= numPhrases) return 0; // really, an error condition
    return scores[index];
  }

  /**
   * Methods with package-only visibility:
   */


  public int size() {
    return numPhrases;
  }

  void addPhrase(String phrase, float score) {
    score *= 100000;
    if (numPhrases >= (MAX_PHRASES - 1)) {
      // remove phrase with lowest score
      int index = 0;
      float minScore = 999999999999999.99f;
      for (int i = 0; i < numPhrases; i++) {
        if (scores[i] < minScore) {
          minScore = scores[i];
          index = i;
        }
      }
      scores[index] = score;
      phrases[index] = phrase;
    } else {
      scores[numPhrases] = score;
      phrases[numPhrases++] = phrase;
    }
  }

  void sortPhrases() {          // sort : best first in resulting list
    for (int i = 0; i < (numPhrases - 1); i++) {
      for (int j = i + 1; j < numPhrases; j++) {
        if (scores[i] < scores[j]) {
          float x = scores[i];
          String s = phrases[i];
          scores[i] = scores[j];
          scores[j] = x;
          phrases[i] = phrases[j];
          phrases[j] = s;
        }
      }
    }
  }
}


