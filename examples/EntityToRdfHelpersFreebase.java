import com.freebase.api.Freebase;
import com.freebase.json.JSON;
import com.knowledgebooks.rdf.RdfServiceProxy;
import com.knowledgebooks.rdf.SesameEmbeddedProxy;

import java.io.PrintWriter;
import java.util.*;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class EntityToRdfHelpersFreebase {

  public static void processPeople(PrintWriter out, String url, String text, String name_type, List<String> name_list, List<String> possible_search_terms) {
    Freebase freebase = Freebase.getFreebase();
    for (String name : name_list) {
      boolean keep_processing = true;
      int num_to_take = Math.min(10, possible_search_terms.size());
      loop:
      while (keep_processing && num_to_take > 2) {
        for (int cycle = 0; cycle < 5; cycle++) {
          String query = name;
          List<String> search_terms = take(possible_search_terms, num_to_take);
          for (String key_word : search_terms) {
            if (query.indexOf(key_word) == -1)
              query += " " + key_word;
          }
          System.out.println(" * query: " + query);
          JSON results = null;
          if (name_type.equals("person"))
            results = freebase.search(query, new HashMap<String, String>()).get("result");
          else
            results = freebase.geosearch(query, new HashMap<String, String>()).get("result");
          System.out.println("Freebase search results: " + results);
          int num_results = results.length(); // I added this API
          if (num_results > 0) {
            System.out.println(" * next result: " + results.get(0));
            System.out.println(" * next relevance score: " + results.get(0).get("relevance:score"));

          }
          if (num_results > 0) {
            System.out.println(" * * * result: " + results.get(0));
            String id = "" + results.get(0).get("id");
            System.out.println(" * * * id: " + id);
            Object relevance = results.get(0).get("relevance:score");
            if (relevance != null && Float.parseFloat("" + relevance) > 1.5) {
              String blank_node = blankNodeURI(name_type);
              out.println("<" + url + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://knowledgebooks.com/rdf/webpage> .");
              out.println("<" + url + "> <http://knowledgebooks.com/rdf/contents> \"" + text.replaceAll("\"", "'") + "\" .");
              out.println("<" + url + "> <http://knowledgebooks.com/rdf/discusses/" + name_type + "> " + blank_node + " .");
              out.println("\n\n" + blank_node +
                " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://knowledgebooks.com/rdfs/entity/" + name_type + "> .");
              out.println("\n\n" + blank_node +
                " <http://xmlns.com/foaf/0.97/name> \"" + name + "\" .");
              out.println("\n\n" + blank_node +
                " <http://knowledgebooks.com/rdf/freebase/id> \"" + id.substring(1) + "\" .");
              break loop;
            }
          }
          // Freebase terms of service: no more that 100,000 web service calls per day.
          // This is at a rate of 1.157 calls per second, so:
          try {
            Thread.sleep(864);
          } catch (Exception ignore) {
          }     // wait 0.846 seconds between search requests
        }
        num_to_take--;
      }
    }
  }

  public static void processPlaces(PrintWriter out, String url, String name_type, List<String> name_list) {
    FreebaseToRdf freebase = new FreebaseToRdf();
    for (String name : name_list) {
      LatLon latlon = freebase.geoSearchGetLatLon(name);
      System.out.println("Freebase search " + name + ", lat/lon results: " + latlon);
      if (latlon != null) {
        String blank_node = blankNodeURI(name_type);
        out.println("<" + url + "> <http://knowledgebooks.com/rdf/discusses/" + name_type + "> " + blank_node + " .");
        out.println(blank_node + " <http://knowledgebooks.com/rdf/name/> \"" + name + "\" .");
        String literal = "" + rdfServiceProxy.latLonToLiteral(latlon.lat, latlon.lon);
        out.println(blank_node + " <http://knowledgebooks.com/rdf/location/> \"" + literal + "\" .");
      }
      // This is at a rate of 1.157 calls per second, so:
      try {
        Thread.sleep(864);
      } catch (Exception ignore) {
      }     // wait 0.846 seconds between search requests
    }
  }


  private static String blankNodeURI(String name_type) {
    return "_:fb" + name_type + blank_node_count++;
  }

  static long blank_node_count = 1;

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
