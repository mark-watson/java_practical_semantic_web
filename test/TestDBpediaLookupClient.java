import com.knowledgebooks.info_spiders.DBpediaLookupClient;

import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class TestDBpediaLookupClient {
  public static void main(String[] args) throws Exception {
    DBpediaLookupClient lookup = new DBpediaLookupClient("City of Flagstaff Arizona");
    for (Map<String, String> bindings : lookup.variableBindings()) {
      System.out.println("result:");
      for (String variableName : bindings.keySet()) {
        System.out.println("  " + variableName + ":" + bindings.get(variableName));
      }
      System.out.println();
    }
  }
}
