/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

import com.knowledgebooks.info_spiders.OpenCalaisClient;
import com.knowledgebooks.info_spiders.WebSpider;
import com.knowledgebooks.nlp.ExtractNames;
import com.knowledgebooks.nlp.ExtractSearchTerms;
import com.knowledgebooks.nlp.util.ScoredList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * This main public class (and the non public classes and interface also in this
 * file) is the example application for the chapter "Generating RDF by Combining
 * Public and Private Data Sources." Note that you should have a D2R server running
 * on localhost with the database created in Appendix A.
 */
public class RdfDataGenerationApplication {

  private RdfDataGenerationApplication() {
  } // illegal constructor call

  public RdfDataGenerationApplication(String web_sites_config_file,
                                      String database_config_file,
                                      PrintWriter out) throws Exception {
    this.out = out;
    this.database_config_file = database_config_file;
    // process web sites:
    List<String> lines = (List<String>) FileUtils.readLines(new File(web_sites_config_file));
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

    // after processing all 4 data sources, add more RDF statements for inter-source properties:
    process_interpage_shared_properties();
    out.close();
  }

  private String database_config_file = "";
  
  private void spider(String starting_url, int spider_depth) throws Exception {
    System.out.println("** spider(" + starting_url + ", " + spider_depth + ")");
    WebSpider ws = new WebSpider(starting_url, spider_depth);
    Map<String, Set<String>> for_shared_properties = new HashMap<String, Set<String>>();
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1);
      System.out.println("\n\n\n----URL:\n" + url + "\n    content:\n" + text);
      process_data_source(url, text, for_shared_properties);
    }
  }
  
  /**
   * This helper method is used for both web sites and database sources.
   * OpenCalais, Freebase, and DBpedia are used to add useful links.
   *
   * @param uri
   * @param text
   * @param for_shared_properties
   * @throws Exception
   */
  private void process_data_source(String uri, String text, Map<String, Set<String>> for_shared_properties) throws Exception {
    // OpenCalais:
    Map<String, List<String>> results = new OpenCalaisClient().getPropertyNamesAndValues(text);
    out.println("<" + uri + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://knowledgebooks.com/rdf/webpage> .");
    out.println("<" + uri + "> <http://knowledgebooks.com/rdf/contents> \"" + text.replaceAll("\"", "'") + "\" .");
    if (results.get("Person") != null) for (String person : results.get("Person")) {
      out.println("<" + uri + "> <http://knowledgebooks.com/rdf/containsPerson> \"" + person.replaceAll("\"", "'") + "\" .");
    }
    for (String key : results.keySet()) {
      System.out.println("  " + key + ": " + results.get(key));
      for (Object val : results.get(key)) {
        if (("" + val).length() > 0) {
          String property = "<http://knowledgebooks.com/rdf/" + key + ">";
          out.println("<" + uri + "> <http://knowledgebooks.com/rdf/" + key + "> \"" + val + "\" .");
          HashSet<String> hs = (HashSet<String>) for_shared_properties.get(property);
          if (hs == null) hs = new HashSet<String>();
          hs.add("\"" + val + "\"");
          for_shared_properties.put("<http://knowledgebooks.com/rdf/" + key + ">", hs);
        }
      }
    }
    // Find search terms in text:
    ExtractSearchTerms extractor = new ExtractSearchTerms(text);
    System.out.println("Best search terms " + extractor.getBest());
    // Get people and place names in this web page's content:
    ScoredList[] ret = new ExtractNames().getProperNames(text);
    List<String> people = ret[0].getStrings();
    List<String> places = ret[1].getStrings();
    System.out.println("Human names: " + people);
    System.out.println("Place names: " + places);

    // Freebase:
    EntityToRdfHelpersFreebase.processPeople(out, uri, text, "person", people, extractor.getBest());
    EntityToRdfHelpersFreebase.processPlaces(out, uri, "place", places);

    // DBpedia:
    EntityToRdfHelpersDbpedia.processEntity(out, uri, "person", people, extractor.getBest(), processed_DBpedia_queries);
    EntityToRdfHelpersDbpedia.processEntity(out, uri, "place", places, extractor.getBest(), processed_DBpedia_queries);

    // process databases with D2R SPARQL endpoint front ends:
    new EntityToD2RHelpers(uri, database_config_file, people, places, out);

    shared_properties_for_all_sources.put(uri, for_shared_properties);
  }

  private void process_interpage_shared_properties() throws Exception {
    Set<String> unique_urls = shared_properties_for_all_sources.keySet();
    for (String url_1 : unique_urls) {
      for (String url_2 : unique_urls) {
        if (url_1.equals(url_2) == false) {
          System.out.println("\n\n^^^^^^^^^ " + url_1 + " : " + url_2 + "\n");
          float url_similarity = score_mapset(shared_properties_for_all_sources.get(url_1), shared_properties_for_all_sources.get(url_2));
          if (url_similarity > 12f) {
            out.println("<" + url_1 + "> <http://knowledgebooks.com/rdf/high_similarity> <" + url_2 + "> .");
          } else if (url_similarity > 8f) {
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
        ret += score_sets(s1, s2);
      }
    }
    System.out.println(" -------------------- score_mapset: " + "  ret = " + ret);
    return ret;
  }

  private float score_sets(Set<String> l_1, Set<String> l_2) {
    float ret = 0f;
    for (String s : l_1) {
      if (l_2.contains(s)) ret += 1f;
    }
    System.out.println(" -- l_1: " + l_1 + ", l_2: " + l_2 + "  ret = " + ret);
    return ret;
  }

  private PrintWriter out = null;
  private Map<String, Map<String, Set<String>>> shared_properties_for_all_sources = new HashMap<String, Map<String, Set<String>>>();
  private Set<String> processed_DBpedia_queries = new HashSet<String>();

  public static void main(String[] args) throws Exception {
    new RdfDataGenerationApplication("testdata/websites.txt", "testdata/databaseinfo.txt", new PrintWriter("testdata/gen_rdf.nt"));
  }
}
