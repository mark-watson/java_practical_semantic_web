import com.knowledgebooks.info_spiders.WebSpider;
import com.knowledgebooks.nlp.ExtractNames;
import com.knowledgebooks.nlp.ExtractSearchTerms;
import com.knowledgebooks.nlp.util.ScoredList;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class WebScrapingAndFreebaseSearch {
  static public void main(String[] args) throws Exception {
    PrintWriter out = new PrintWriter(new FileWriter("out.nt"));
    WebSpider ws = new WebSpider("http://www.knowledgebooks.com", 2);
    //WebSpider ws = new WebSpider("http://markwatson.com", 2);
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1) + " Flagstaff";
      // Get search terms for this web page's content:
      ExtractSearchTerms extractor = new ExtractSearchTerms(text);
      System.out.println("Best search terms " + extractor.getBest());
      // Get people and place names in this web page's content:
      ScoredList[] ret = new ExtractNames().getProperNames(text);
      List<String> people = ret[0].getStrings();
      List<String> places = ret[1].getStrings();
      System.out.println("Human names: " + ret[0].getValuesAsString());
      System.out.println("Place names: " + ret[1].getValuesAsString());
      // Use Freebase to get more information about these people and places:
      //Freebase freebase = Freebase.getFreebase();
      EntityToRdfHelpersFreebase.processPeople(out, url, text, "person", people, extractor.getBest());
      EntityToRdfHelpersFreebase.processPlaces(out, url, "place", places);
    }
    out.close();
  }
}
