import com.knowledgebooks.info_spiders.OpenCalaisClient;
import com.knowledgebooks.info_spiders.WebSpider;
import com.knowledgebooks.rdf.AllegroGraphServerProxy;
import com.knowledgebooks.rdf.RdfServiceProxy;
import com.knowledgebooks.rdf.SesameEmbeddedProxy;
import com.knowledgebooks.rdf.Triple;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class OpenCalaisWebSpiderToRdfDataStore {
  static public void main(String[] args) throws Exception {
    //RdfServiceProxy proxy = new AllegroGraphServerProxy();
    RdfServiceProxy proxy = new SesameEmbeddedProxy();
    proxy.deleteRepository("knowledgebooks_repo");
    proxy.createRepository("knowledgebooks_repo");
    proxy.registerFreetextPredicate("http://knowledgebooks.com/rdf/contents");  // do this before adding triples

    WebSpider ws = new WebSpider("http://www.knowledgebooks.com", 2);
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1);
      System.out.println("\n\n\n----URL:\n" + url + "\n    content:\n" + text);
      Map<String, List<String>> results = new OpenCalaisClient().getPropertyNamesAndValues(text);

      proxy.addTriple(url, Triple.RDF_TYPE, "http://knowledgebooks.com/rdf/webpage");
      proxy.addTriple("<" + url + ">", "http://knowledgebooks.com/rdf/contents", "\"" + text.replaceAll("\"", "'") + "\"");
      for (String key : results.keySet()) {
        System.out.println("  " + key + ": " + results.get(key) + ", class=" + results.get(key).getClass());
        for (Object val : results.get(key)) {
          proxy.addTriple(url, "http://knowledgebooks.com/rdf/" + val, "\"" + text.replaceAll("\"", "'") + "\"");
        }
      }
    }
    System.out.println("\n\nSample queries:\n");
    List<List<String>> results = proxy.query("SELECT ?s ?p ?o  WHERE {?s ?p ?o .}");
    for (List<String> result : results) {
      System.out.println("All triples result: " + result);
    }

    results = proxy.textSearch("Lisp");
    for (List<String> result : results) {
      System.out.println("Wild card text search result: " + result);
    }

  }
}
