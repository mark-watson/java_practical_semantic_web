import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.knowledgebooks.info_spiders.OpenCalaisClient;
import com.knowledgebooks.info_spiders.WebSpider;
import org.apache.commons.io.FileUtils;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class OpenCalaisGenerateRdfPropertiesFromWebPages {
  public OpenCalaisGenerateRdfPropertiesFromWebPages(String config_file_path, PrintWriter out) throws IOException {
    this.out = out;
    List<String> lines = (List<String>) FileUtils.readLines(new File(config_file_path));
    for (String line : lines) {
      Scanner scanner = new Scanner(line);
      scanner.useDelimiter(" ");
      try {
        String starting_url = scanner.next();
        int spider_depth = Integer.parseInt(scanner.next());
        spider(starting_url, spider_depth);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    this.out.close();
  }

  private void spider(String starting_url, int spider_depth) throws Exception {
    System.out.println("** spider(" + starting_url + ", " + spider_depth + ")");
    WebSpider ws = new WebSpider(starting_url, spider_depth);
    Map<String, Set<String>> for_shared_properties = new HashMap<String, Set<String>>();
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1);
      System.out.println("\n\n\n----URL:\n" + url + "\n    content:\n" + text);
      if (text.length() > 120) {
        Map<String, List<String>> results = new OpenCalaisClient().getPropertyNamesAndValues(text);
        out.println("<" + url + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://knowledgebooks.com/rdf/webpage> .");
        out.println("<" + url + "> <http://knowledgebooks.com/rdf/contents> \"" + text.replaceAll("\"", "'") + "\" .");
        if (results.get("Person") != null) for (String person : results.get("Person")) {
          out.println("<" + url + "> <http://knowledgebooks.com/rdf/containsPerson> \"" + person.replaceAll("\"", "'") + "\" .");
        }
        for (String key : results.keySet()) {
          System.out.println("  " + key + ": " + results.get(key));
          for (Object val : results.get(key)) {
            String property = "<http://knowledgebooks.com/rdf/" + key + ">";
            if (("" + val).length() > 0) {
              out.println("<" + url + "> <http://knowledgebooks.com/rdf/" + key + "> \"" + val + "\" .");
              HashSet<String> hs = (HashSet<String>) for_shared_properties.get(property);
              if (hs == null) hs = new HashSet<String>();
              hs.add("\"" + val + "\"");
              for_shared_properties.put("<http://knowledgebooks.com/rdf/" + key + ">", hs);
            }
          }
        }
      }
      interpage_shared_properties.put(url, for_shared_properties);
    }
    process_interpage_shared_properties();
  }

  private void process_interpage_shared_properties() throws Exception {
    Set<String> unique_urls = interpage_shared_properties.keySet();
    for (String url_1 : unique_urls) {
      for (String url_2 : unique_urls) {
        if (url_1.equals(url_2) == false) {
          System.out.println("\n\n^^^^^^^^^ " + url_1 + " : " + url_2 + "\n");
          float url_similarity = score_mapset(interpage_shared_properties.get(url_1), interpage_shared_properties.get(url_2));
          if (url_similarity > 12f) {
            out.println("<" + url_1 + "> <http://knowledgebooks.com/rdf/high_similarity> <" + url_2 + "> .");
          } else if (url_similarity > 5f) {
            out.println("<" + url_1 + "> <http://knowledgebooks.com/rdf/medium_similarity> <" + url_2 + "> .");
          } else if (url_similarity > 5f) {
            out.println("<" + url_1 + "> <http://knowledgebooks.com/rdf/low_similarity> <" + url_2 + "> .");
          }
        }
      }
    }
  }

  private float score_mapset(Map<String, Set<String>> set_1, Map<String, Set<String>> set_2) {
    System.out.println(" set_1: " + set_1);
    System.out.println(" set_2: " + set_2);
    float ret = 0f;
    for (String property_1 : set_1.keySet()) {
      Set<String> s1 = set_1.get(property_1);
      Set<String> s2 = set_2.get(property_1);
      if (s2 != null) {
        ret += score_arraylist(s1, s2);
      }
    }
    System.out.println(" -------------------- score_mapset: " + "  ret = " + ret);
    return ret;
  }

  private float score_arraylist(Set<String> l_1, Set<String> l_2) {
    float ret = 0f;
    for (String s : l_1) {
      if (l_2.contains(s)) ret += 1f;
    }
    System.out.println(" -- l_1: " + l_1 + ", l_2: " + l_2 + "  ret = " + ret);
    return ret;
  }

  private PrintWriter out = null;
  private Map<String, Map<String, Set<String>>> interpage_shared_properties = new HashMap<String, Map<String, Set<String>>>();
  ;

  public static void main(String[] args) throws Exception {
    new OpenCalaisGenerateRdfPropertiesFromWebPages("testdata/websites.txt", new PrintWriter("tempdata/gen_rdf.nt"));
  }
}
