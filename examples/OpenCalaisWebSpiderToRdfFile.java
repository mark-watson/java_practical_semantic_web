import com.knowledgebooks.info_spiders.OpenCalaisClient;
import com.knowledgebooks.info_spiders.WebSpider;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class OpenCalaisWebSpiderToRdfFile {
  static public void main(String[] args) throws Exception {
    PrintWriter out = new PrintWriter(new FileWriter("out.nt"));
    WebSpider ws = new WebSpider("http://www.knowledgebooks.com", 2);
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1);
      System.out.println("\n\n\n----URL:\n" + url + "\n    content:\n" + text);
      Map<String, List<String>> results = new OpenCalaisClient().getPropertyNamesAndValues(text);
      out.println("<" + url + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://knowledgebooks.com/rdf/webpage> .");
      out.println("<" + url + "> <http://knowledgebooks.com/rdf/contents> \"" + text.replaceAll("\"", "'") + "\" .");
      if (results.get("Person") != null) for (String person : results.get("Person")) {
        out.println("<" + url + "> <http://knowledgebooks.com/rdf/containsPerson> \"" + person.replaceAll("\"", "'") + "\" .");
      }
      for (String key : results.keySet()) {
        System.out.println("  " + key + ": " + results.get(key) + ", class=" + results.get(key).getClass());
        for (Object val : results.get(key)) {
          out.println("<" + url + "> <http://knowledgebooks.com/rdf/" + key + "> \"" + val + "\" .");
        }
      }
    }
    out.close();
  }
}
