import com.knowledgebooks.rdf.SparqlClient;

import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

/**
 * Test finding people born in California
 */
public class TestSparqlClientDbpedia1 {
  public static void main(String[] args) throws Exception {
    String sparql =
      "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
        "PREFIX dbpedia2: <http://dbpedia.org/property/>\n" +
        "PREFIX dbpedia: <http://dbpedia.org/>\n" +
        "SELECT ?name ?person WHERE {\n" +
        "     ?person dbpedia2:birthPlace <http://dbpedia.org/resource/California> .\n" +
        "     ?person foaf:name ?name .\n" +
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
