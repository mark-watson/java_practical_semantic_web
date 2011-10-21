import com.freebase.api.Freebase;
import com.freebase.json.JSON;
import com.knowledgebooks.info_spiders.DBpediaLookupClient;
import com.knowledgebooks.rdf.RdfServiceProxy;
import com.knowledgebooks.rdf.SesameEmbeddedProxy;

import java.io.PrintWriter;
import java.util.*;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class EntityToRdfHelpersDbpedia {
  public static void processEntity(PrintWriter out, String url,
                                   String name_type, List<String> name_list,
                                   List<String> possible_search_terms,
                                   Set<String> processed_DBpedia_queries)
                     throws Exception {
    System.out.println("\n\n******* EntityToRdfHelpersDbpedia.processEntity "+name_list);
    for (String name : name_list) {
      System.out.println(" *  name: " + name);
      boolean keep_processing = true;
      int num_to_take = Math.min(2, possible_search_terms.size());
      loop:
      while (keep_processing && num_to_take >= 0) {
        for (int cycle = 0; cycle < 2; cycle++) {
          String query = name;
          List<String> search_terms = take(possible_search_terms, num_to_take);
          for (String key_word : search_terms) {
            if (query.indexOf(key_word) == -1)
              query += " " + key_word;
          }
          if (processed_DBpedia_queries.contains(query)) {
            System.out.println("** already processed DBpedia query: " + query);
          } else {
            System.out.println(" * query: " + query + ", num_to_take = " + num_to_take);
            DBpediaLookupClient lookup = new DBpediaLookupClient(query);
            List<Map<String, String>> results = lookup.variableBindings();
            System.out.println("DBpedia search results: " + results);
            int num_results = results.size();
            if (num_results > 0) {
              for (Map<String,String> bindings : results) {
                String uri = bindings.get("URI");
                String label = bindings.get("Label");
                String description = bindings.get("Description");
                out.println("<" + uri + ">  <http://knowledgebooks.com/rdf/datasource> <http://dbpedia.org> .");
                out.println("<" + uri + ">  <http://knowledgebooks.com/rdf/about/"+name_type+"> \"" + name + "\" .");
                out.println("<" + url + ">  <http://knowledgebooks.com/rdf/freebase_uri> <" + uri +"> .");
                out.println("<" + uri + ">  <http://knowledgebooks.com/rdf/description> \"" + description + "\" .");
                out.println("<" + uri + ">  <http://knowledgebooks.com/rdf/dbpedia/label> \"" + label + "\" .");
                break loop;
              }
            }
            try {
              Thread.sleep(1200);
            } catch (Exception ignore) {
            }
          }
        }
        num_to_take--;
      }
    }
  }

  private static List<String> take(List<String> names, int num_to_take) {
    int size = names.size(), index = 0;
    List<String> ret = new ArrayList<String>(size);
    for (int i = 0; i < num_to_take; i++) {
      loop:
      for (int attempt = 0; attempt < 10; attempt++) {
        index = (int) (0.99 * Math.random() * size);
        if (!noise.contains(names.get(index).toLowerCase()) && !ret.contains(names.get(index))) {
          ret.add(names.get(index));
          break loop;
        }
      }
    }
    return ret;
  }

  static Set<String> noise = new HashSet<String>();

  static {
    noise.add("document");
    noise.add("formats");
    noise.add("company");
    noise.add("text");
    noise.add("system");
    noise.add("product");
    noise.add("documents");
    noise.add("services");
    noise.add("technology");
    noise.add("technologies");
    noise.add("implementing");
    noise.add("implement");
    noise.add("language");
    noise.add("manage");
    noise.add("management");
    noise.add("research");
    noise.add("library");
    noise.add("libraries");
    noise.add("language");
    noise.add("languages");
    noise.add("implement");
    noise.add("implements");
    noise.add("standard");
    noise.add("standards");
    noise.add("project");
    noise.add("projects");
  }

  static RdfServiceProxy rdfServiceProxy = null; // this is to get Lat/Lon RDF value

  static {
    try {
      rdfServiceProxy = new SesameEmbeddedProxy();
      rdfServiceProxy.createRepository("test-repo1");   // must have a repository open
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
