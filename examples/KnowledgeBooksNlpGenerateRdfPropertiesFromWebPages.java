import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.knowledgebooks.nlp.AutoTagger;
import com.knowledgebooks.nlp.util.NameValue;
import com.knowledgebooks.nlp.ExtractNames;
import com.knowledgebooks.nlp.util.ScoredList;
import com.knowledgebooks.info_spiders.WebSpider;
import org.apache.commons.io.FileUtils;


/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class KnowledgeBooksNlpGenerateRdfPropertiesFromWebPages {
  public KnowledgeBooksNlpGenerateRdfPropertiesFromWebPages(String config_file_path, PrintWriter out) throws IOException {
    this.out = out;
    extractNames = new ExtractNames();
    autoTagger = new AutoTagger();
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
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1);
      HashSet<String> hs = new HashSet<String>();
      System.out.println("\n\n\n----URL:\n" + url + "\n    content:\n" + text);

      ScoredList[] names = extractNames.getProperNames(text);
      ScoredList people = names[0];
      ScoredList places = names[1];
      List<NameValue<String, Float>> tags = autoTagger.getTags(text);

      out.println("<" + url + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://knowledgebooks.com/rdf/webpage> .");
      out.println("<" + url + "> <http://knowledgebooks.com/rdf/contents> \"" + text.trim().replaceAll("\"", "'") + "\" .");
      for (String person : people.getStrings()) {
        out.println("<" + url + "> <http://knowledgebooks.com/rdf/containsPerson> \"" + person.replaceAll("\"", "'") + "\" .");
      }
      for (String place : places.getStrings()) {
        out.println("<" + url + "> <http://knowledgebooks.com/rdf/containsPlace> \"" + place.replaceAll("\"", "'") + "\" .");
      }
      for (NameValue nv : tags) {
        out.println("<" + url + "> <http://knowledgebooks.com/rdf/" + nv.getName() + "> \"" + ("" + nv.getValue()) + "\" .");
        hs.add("" + nv.getName());
      }
      inter_webpage_shared_tags.put(url, hs);
    }
    process_interpage_shared_properties();
  }

  private void process_interpage_shared_properties() throws Exception {
    Set<String> unique_urls = inter_webpage_shared_tags.keySet();
    for (String url_1 : unique_urls) {
      for (String url_2 : unique_urls) {
        if (url_1.equals(url_2) == false) {
          System.out.println("\n\n^^^^^^^^^ " + url_1 + " : " + url_2 + "\n");
          float url_similarity = score_mapset(inter_webpage_shared_tags.get(url_1), inter_webpage_shared_tags.get(url_2));
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

  private float score_mapset(Set<String> set_1, Set<String> set_2) {
    set_1.retainAll(set_2);  // replace contents of set_1 with intersection of set_1 and set_2
    return set_1.size();
  }

  private PrintWriter out = null;
  private Map<String, Set<String>> inter_webpage_shared_tags = new HashMap<String, Set<String>>();
  ;
  private ExtractNames extractNames = null;
  private AutoTagger autoTagger = null;

  public static void main(String[] args) throws Exception {
    new KnowledgeBooksNlpGenerateRdfPropertiesFromWebPages("testdata/websites.txt", new PrintWriter("tempdata/gen_rdf.nt"));
  }
}
