package com.knowledgebooks.nlp.util;


import java.util.*;
import java.util.ArrayList;

/**
 * Utilities finding sentence breaks in documents.
 */

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class Document {
  /**
   * @param words a string containing plain text
   */
  public Document(String words) {
    List<String> tokens = Tokenizer.wordsToList(words);
    init(tokens);
  }

  /**
   * @param words a list of string tokens
   */
  public Document(List<String> words) {
    init(words);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("[Document " + this.hashCode() + ":\n");
    for (int i = 0, size = getNumSentences(); i < size; i++) {
      sb.append("   ").append(i).append(": ").append(getSentence(i)).append("\n");
    }
    sb.append("]\n");
    return sb.toString();
  }

  /**
   * @return a list of string tokens in this document
   */
  public List<String> getTokens() {
    return tokens;
  }

  private void init(List<String> words) {
    this.tokens = words;
    // pre-calculate sentence boundaries:
    List<IPair> sentenceBoundaries = new ArrayList<IPair>();
    int start = 0, end = 0;
    for (int i = 0; i < words.size(); i++) {
      String w = words.get(i);
      // handle special cases like: Procter & Gamble Co. saves $300 million annually
      // (i.e., do not treat Co. as the end of a sentence)        -- handle ABREVIATIONS
      boolean notEnd = false;
      if (i > 0 && i < (words.size() - 1) && w.equals(".")) {
        if (words.get(i - 1).length() < 5 && words.get(i - 1).length() > 0 && words.get(i + 1).length() > 0) {
          if (Character.isUpperCase(words.get(i - 1).charAt(0)) &&
            Character.isLowerCase(words.get(i + 1).charAt(0))) notEnd = true;
          if (Character.isUpperCase(words.get(i - 1).charAt(0)) &&
            words.get(i - 1).length() == 1) notEnd = true;
          if (words.get(i + 1).charAt(0) == ',') notEnd = true;
          if (i < (words.size() - 2)) {
            if (words.get(i + 1).charAt(0) == '.' && words.get(i + 2).charAt(0) == ',') notEnd = true;
          }
          if (words.get(i + 1).charAt(0) == ';') notEnd = true;
        }
      }
      if ((!notEnd && w.equals(".")) || w.equals("!") || w.equals("?")) {
        end = i;
        sentenceBoundaries.add(new IPair(start, end));
        start = i + 1;
      }
    }
    if (end < start) {
      sentenceBoundaries.add(new IPair(start, words.size() - 1));
    }
    int size = sentenceBoundaries.size();
    if (size > 0) {
      startSentenceBoundary = new int[size];
      endSentenceBoundary = new int[size];
      for (int i = 0; i < size; i++) {
        IPair ip = sentenceBoundaries.get(i);
        startSentenceBoundary[i] = ip.getFirst();
        endSentenceBoundary[i] = ip.getSecond();
      }
    }
  }

  /**
   *
   */
  public int[] startSentenceBoundary = new int[0];
  /**
   *
   */
  public int[] endSentenceBoundary = new int[0];

  /**
   * @return
   */
  public int getNumWords() {
    return tokens.size();
  }

  /**
   * @return
   */
  public int getNumSentences() {
    return startSentenceBoundary.length - 1;
  }

  /**
   * @param wordIndex
   * @return
   */
  public String getWord(int wordIndex) {
    if (wordIndex < 0 || wordIndex >= tokens.size()) return "";
    return tokens.get(wordIndex);
  }

  /**
   * @param wordIndex
   * @return
   */
  public IPair getSentenceBoundaryFromWordIndex(int wordIndex) {
    if (startSentenceBoundary == null) return null;
    for (int i = 0, size = startSentenceBoundary.length; i < size; i++) {
      if (wordIndex >= startSentenceBoundary[i] && wordIndex <= endSentenceBoundary[i]) {
        return new IPair(startSentenceBoundary[i], endSentenceBoundary[i]);
      }
    }
    // the following is, really, an error return:
    return new IPair(startSentenceBoundary[0], endSentenceBoundary[0]);
  }

  /**
   * @param sentenceIndex
   * @return
   */
  public IPair getSentenceBoundary(int sentenceIndex) {
    if (startSentenceBoundary == null) return null;
    return new IPair(startSentenceBoundary[sentenceIndex], endSentenceBoundary[sentenceIndex]);
  }

  private List<String> tokens = new ArrayList<String>(0);

  /**
   * @param index sentence index in document
   * @return a string containing the specified sentence
   */
  public String getSentence(int index) {
    if (index < 0 || index >= startSentenceBoundary.length) return ""; // error/bogus return
    StringBuffer sb = new StringBuffer();
    int start = startSentenceBoundary[index];
    int end = endSentenceBoundary[index] + 1;
    for (int i = start; i < end; i++) {
      if (tokens.get(i).equals("nbsp")) tokens.set(i, "nbsp;");
      sb.append(tokens.get(i));
      if ((i < (end - 1)) &&
        (i == (end - 1) || editSpace.get(tokens.get(i + 1)) == null)) sb.append(" ");
    }
    return sb.toString();
  }

  static private Hashtable<String, Boolean> editSpace = new Hashtable<String, Boolean>();

  static {
    editSpace.put("nbsp", true);
    editSpace.put("t", true);
    editSpace.put("s", true);
    editSpace.put("'", true);
    editSpace.put(",", true);
    editSpace.put(".", true);
    editSpace.put("!", true);
    editSpace.put("?", true);
  }

}

