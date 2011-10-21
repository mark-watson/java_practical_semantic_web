import com.knowledgebooks.rdf.SparqlClient;

import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

/**
 * Test name lookup
 */
public class TestSparqlClientDbpedia2 {
  public static void main(String[] args) throws Exception {
    test_name("Barack Obama");
    test_name("Mark Louis Watson");
    test_name("Mark Watson");
  }

  private static void test_name(String name) throws Exception {
    System.out.println("\n\nFine name:  " + name + "\n");
    String sparql =
      "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
        "SELECT ?person WHERE {\n" +
        "     ?person foaf:name \"" + name + "\" .\n" +
        "}\n" +
        "LIMIT 10\n";
    SparqlClient test = new SparqlClient("http://dbpedia.org/sparql", sparql);
    for (Map<String, String> bindings : test.variableBindings()) {
      System.out.print("result:");
      for (String variableName : bindings.keySet()) {
        System.out.print("  " + variableName + ":" + bindings.get(variableName));
      }
      System.out.println();
    }
  }
}
