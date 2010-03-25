package com.knowledgebooks.rdf.implementation;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 25, 2010
 * Time: 10:58:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  }
}
