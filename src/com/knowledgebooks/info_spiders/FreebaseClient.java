package com.knowledgebooks.info_spiders;

// From an example by: Pierre Lindenbaum  http://plindenbaum.blogspot.com/2007/05/hello-world-in-mql-freebase.html

import com.freebase.json.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class FreebaseClient {
  static final String URL = "http://www.freebase.com/api/service/mqlread";

  public JSON query(String json) throws Exception {
    String envelope = "{\"qname\":{\"query\":" + json + "}}";
    String urlStr = URL + "?queries=" + URLEncoder.encode(envelope, "UTF-8");
    URL url = new URL(urlStr);
    URLConnection con = url.openConnection();
    con.connect();
    InputStream in = con.getInputStream();

    JSON results = JSON.parse(new InputStreamReader(in));
    in.close();
    return results;
  }

  public static void main(String[] args) {
    try {
      FreebaseClient t = new FreebaseClient();
      JSON r = t.query("[{     \"/people/person/date_of_birth\":null,     \"/people/person/gender\":null,     \"/people/person/place_of_birth\":null,     \"/people/person/profession\":\"biologist\", \"name\":null   }] ");
      System.out.println(r);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}