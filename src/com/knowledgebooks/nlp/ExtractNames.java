package com.knowledgebooks.nlp;

import com.knowledgebooks.nlp.util.ScoredList;
import com.knowledgebooks.nlp.util.Tokenizer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Wrapper for code to find both human and place names in input text.
 */

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class ExtractNames {
  /**
   * Facade method: get all place and human names from a text string
   *
   * @param words
   * @return
   */
  public ScoredList[] getProperNames(List<String> words) {
    ScoredList placeNames = new ScoredList();
    ScoredList humanNames = new ScoredList();
    ScoredList[] ret = new ScoredList[2];
    ret[0] = humanNames;
    ret[1] = placeNames;
    if (words == null) return ret;
    for (int i = 0; i < words.size(); i++) {
      // 5 word human names:
      if (isHumanName(words, i, 5)) {
        String s = words.get(i) + " " + words.get(i + 1) + " " + words.get(i + 2) + " " + words.get(i + 3) + " " + words.get(i + 4);
        humanNames.addValue(s);
        i += 4;
        continue;
      }
      // 4 word human names:
      if (isHumanName(words, i, 4)) {
        String s = words.get(i) + " " + words.get(i + 1) + " " + words.get(i + 2) + " " + words.get(i + 3);
        humanNames.addValue(s);
        i += 3;
        continue;
      }
      // 3 word names:
      if (isPlaceName(words, i, 3)) {
        String s = words.get(i) + " " + words.get(i + 1) + " " + words.get(i + 2);
        placeNames.addValue(s);
        i += 2;
        continue;
      }
      if (isHumanName(words, i, 3)) {
        String s = words.get(i) + " " + words.get(i + 1) + " " + words.get(i + 2);
        humanNames.addValue(s);
        i += 2;
        continue;
      }
      // 2 word names:
      if (isPlaceName(words, i, 2)) {
        String s = words.get(i) + " " + words.get(i + 1);
        placeNames.addValue(s);
        i += 1;
        continue;
      }
      if (isHumanName(words, i, 2)) {
        String s = words.get(i) + " " + words.get(i + 1);
        humanNames.addValue(s);
        i += 1;
        continue;
      }
      // 1 word names:
      if (isPlaceName(words, i, 1)) {
        placeNames.addValue(words.get(i));
        continue;
      }
    }
    return ret;
  }

  /**
   * @param s
   * @return
   */
  public ScoredList[] getProperNames(String s) {
    List<String> words = Tokenizer.wordsToList(s);
    return getProperNames(words);
  }

  public List<List<String>> getProperNamesAsStrings(String s) {
    List<List<String>> ret = new ArrayList<List<String>>();
    ScoredList[] sl = getProperNames(s);
    List<String> human_names = new ArrayList<String>();
    for (int i = 0, size = sl[0].size(); i < size; i++) human_names.add(sl[0].getValue(i) + ":" + sl[0].getScore(i));
    List<String> place_names = new ArrayList<String>();
    for (int i = 0, size = sl[1].size(); i < size; i++) place_names.add(sl[1].getValue(i) + ":" + sl[1].getScore(i));
    ret.add(human_names);
    ret.add(place_names);
    System.out.println("** " + place_names);
    return ret;
  }


  /**
   * @param words
   * @param startIndex
   * @param numWords
   * @return
   */
  public boolean isPlaceName(List<String> words, int startIndex, int numWords) {
    if ((startIndex + numWords) > words.size()) return false;
    if (numWords == 1) return isPlaceName(words.get(startIndex));
    String s = "";
    for (int i = startIndex; i < (startIndex + numWords); i++) {
      if (i < (startIndex + numWords - 1)) s = s + words.get(startIndex) + " ";
      else s = s + words.get(startIndex);
    }
    return isPlaceName(s);
  }

  /**
   * @param name
   * @return
   */
  public boolean isPlaceName(String name) {
    //if (placeNameHash.get(name) != null)
    //  System.out.println("* place name: " + name + ", placeNameHash.get(name): " + placeNameHash.get(name));
    return placeNameHash.get(name) != null;
  }

  /**
   * @param s
   * @return
   */
  public boolean isHumanName(String s) {
    List<String> ss = Tokenizer.wordsToList(s);
    //System.out.print("Tokens: "); for (int i=0; i<ss.length; i++) System.out.print(ss[i] + " "); System.out.println();
    if (ss == null) return false;
    return isHumanName(ss);
  }


  /**
   * @param words
   * @param index
   * @param numWords
   * @return
   */
  public boolean isHumanName(List<String> words, int index, int numWords) {
    if ((index + numWords) > words.size()) return false;
    if (numWords == 1) {
      return isHumanName(Arrays.asList(words.get(index)));
    }
    if (numWords == 2) {
      return isHumanName(Arrays.asList(words.get(index), words.get(index + 1)));
    }
    if (numWords == 3) {
      return isHumanName(Arrays.asList(words.get(index), words.get(index + 1), words.get(index + 2)));
    }
    if (numWords == 4) {
      return isHumanName(Arrays.asList(words.get(index), words.get(index + 1), words.get(index + 2), words.get(index + 3)));
    }
    if (numWords == 5) {
      return isHumanName(Arrays.asList(words.get(index), words.get(index + 1), words.get(index + 2), words.get(index + 3), words.get(index + 4)));
    }
    return false;
  }

  /**
   * @param words
   * @return
   */
  public boolean isHumanName(List<String> words) {
    int len = words.size();
    if (len == 1) {
      if (lastNameHash.get(words.get(0)) != null) return true;
    } else if (len == 2) {
      if (firstNameHash.get(words.get(0)) != null && lastNameHash.get(words.get(1)) != null) return true;
      if (prefixHash.get(words.get(0)) != null && lastNameHash.get(words.get(1)) != null) return true;
    } else if (len == 3) {
      if (firstNameHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        lastNameHash.get(words.get(2)) != null) return true;
      if (prefixHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        lastNameHash.get(words.get(2)) != null) return true;
      if (prefixHash.get(words.get(0)) != null &&
        words.get(1).equals(".") &&
        lastNameHash.get(words.get(2)) != null) return true;
    } else if (len == 4) {
      if (firstNameHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        firstNameHash.get(words.get(2)) != null &&
        lastNameHash.get(words.get(3)) != null) return true;
      if (firstNameHash.get(words.get(0)) != null &&
        words.get(1).length() == 1 &&
        words.get(2).equals(".") &&
        lastNameHash.get(words.get(3)) != null) return true;
      if (prefixHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        firstNameHash.get(words.get(2)) != null &&
        lastNameHash.get(words.get(3)) != null) return true;
      if (prefixHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        words.get(2).length() == 1 &&
        lastNameHash.get(words.get(3)) != null) return true;
    } else if (len == 5) {
      if (firstNameHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        words.get(2).length() == 1 &&
        words.get(3).equals(".") &&
        lastNameHash.get(words.get(4)) != null) return true;
      if (prefixHash.get(words.get(0)) != null &&
        firstNameHash.get(words.get(1)) != null &&
        words.get(2).length() == 1 &&
        words.get(3).equals(".") &&
        lastNameHash.get(words.get(4)) != null) return true;
    }
    return false;
  }

  /**
   *
   */
  public ExtractNames() {
    this("data/propername.ser");
  }

  /**
   * @param dataPath
   */
  public ExtractNames(String dataPath) {
    if (lastNameHash != null) return; // static data already loaded
    try {
      InputStream ins =
        this.getClass().getClassLoader().getResourceAsStream(dataPath);
      if (ins == null) {
        ins = this.getClass().getClassLoader().getResourceAsStream(dataPath);
      }
      if (ins == null) {
        ins = new FileInputStream(dataPath);
      }
      if (ins == null) {
        System.out.println("\ncom.knowledgebooks.entity_extraction.Names: failed to open '" + dataPath + "'\n");
        System.exit(1);
      } else {
        ObjectInputStream p = new ObjectInputStream(ins);
        lastNameHash = (Hashtable) p.readObject();
        firstNameHash = (Hashtable) p.readObject();
        placeNameHash = (Hashtable) p.readObject();
        prefixHash = (Hashtable) p.readObject();
        ins.close();
      }
    } catch (Exception ee) {
      ee.printStackTrace();
    }
    System.out.println("# last names=" + lastNameHash.size() + ", # first names=" + firstNameHash.size());
  }

  /**
   * @param args
   */
  static public void main(String[] args) {
    ExtractNames extractNames = new ExtractNames();
    // initialize everything, before printing any output - trying to see what is taking so long!
    if (args.length > 0) {
      ScoredList[] ret = extractNames.getProperNames(args[0]);
      System.out.println("Human names: " + ret[0].getValuesAsString());
      System.out.println("Place names: " + ret[1].getValuesAsString());
    } else {
      extractNames.isPlaceName("Paris");
      extractNames.isHumanName("President Bush");
      extractNames.isHumanName("President George Bush");
      extractNames.isHumanName("President George W. Bush");
      System.out.println("Initialization complete....");
      System.out.println("Paris: " + extractNames.isPlaceName("Paris"));
      System.out.println("Mexico: " + extractNames.isPlaceName("Mexico"));
      System.out.println("Fresno: " + extractNames.isPlaceName("Fresno"));
      System.out.println("Moscow: " + extractNames.isPlaceName("Moscow"));
      System.out.println("France: " + extractNames.isPlaceName("France"));
      System.out.println("Los Angeles: " + extractNames.isPlaceName("Los Angeles"));
      System.out.println("President Bush: " + extractNames.isHumanName("President Bush"));
      System.out.println("President George Bush: " + extractNames.isHumanName("President George Bush"));
      System.out.println("President George W. Bush: " + extractNames.isHumanName("President George W. Bush"));
      System.out.println("George W. Bush: " + extractNames.isHumanName("George W. Bush"));
      System.out.println("Senator Barbara Boxer: " + extractNames.isHumanName("Senator Barbara Boxer"));
      System.out.println("King Smith: " + extractNames.isHumanName("King Smith"));
      ScoredList[] ret = extractNames.getProperNames("George Bush played golf. President George W. Bush went to London England, Paris France and Mexico to see Mary Smith in Moscow. President Bush will return home Monday.");
      System.out.println("Human names: " + ret[0].getValuesAsString());
      System.out.println("Place names: " + ret[1].getValuesAsString());
      System.out.println("\n\n\n");

      // for book example:
      ExtractNames names = new ExtractNames();
      System.out.println("Los Angeles: " +
        names.isPlaceName("Los Angeles"));
      System.out.println("President Bush: " +
        names.isHumanName("President Bush"));
      System.out.println("President George Bush: " +
        names.isHumanName("President George Bush"));
      System.out.println("President George W. Bush: " +
        names.isHumanName("President George W. Bush"));
      ScoredList[] ret1 = names.getProperNames(
        "George Bush played golf. President  George W. Bush went to London England, Paris France and Mexico to see Mary  Smith in Moscow. President Bush will return home Monday.");
      System.out.println("Human names: " +
        ret1[0].getValuesAsString());
      System.out.println("Place names: " +
        ret1[1].getValuesAsString());
    }
  }

  static Hashtable lastNameHash = null;
  static Hashtable firstNameHash = null;
  static Hashtable placeNameHash = null; // cache for database access
  static Hashtable prefixHash = null;

}
