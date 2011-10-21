package com.knowledgebooks.nlp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class RunExternal {

  public static void main(String argv[]) {
    try {
      String line;
      Process p = Runtime.getRuntime().exec
        ("echo \"thhe dogg brked\" | /usr/local/bin/aspell  -a list");
      BufferedReader input =
        new BufferedReader
          (new InputStreamReader(p.getInputStream()));
      while ((line = input.readLine()) != null) {
        System.out.println(line);
      }
      input.close();
    }
    catch (Exception err) {
      err.printStackTrace();
    }
  }
}
