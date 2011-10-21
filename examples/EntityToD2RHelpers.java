import com.knowledgebooks.rdf.SparqlClient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class EntityToD2RHelpers {
  public EntityToD2RHelpers(String uri, String config_file, List<String> people, List<String> places, PrintWriter out) throws Exception {
    // In this example, I am assuming that the D2R server is running on localhost:2020
    //out.println("PREFIX vocab: <http://localhost:2020/vocab/resource/>");
    List<String> lines = (List<String>) FileUtils.readLines(new File(config_file));
    String d2r_host_and_port = lines.remove(0);
    String [] info = d2r_host_and_port.split(" ");
    System.out.println("D2R host = |"+info[0]+"| and port = |" + info[1]+"|");
    for (String line : lines) {
      Scanner scanner = new Scanner(line);
      scanner.useDelimiter(" ");
      String d2r_type = scanner.next();
      System.out.println("* d2r_type = " + d2r_type);
      while (scanner.hasNext()) {
        String term = scanner.next();
        String [] property_and_entity_type = term.split("/");
        System.out.println("   property: " + property_and_entity_type[0] +
          " entity type: " + property_and_entity_type[1]);

        if (property_and_entity_type[1].equals("person")) {
           for (String person : people) {
             // perform SPARQL queries to D2R server:
             String sparql =
               "PREFIX vocab: <http://localhost:2020/vocab/resource/>\n" +
                 "SELECT ?subject ?name WHERE {\n" +
                 "     ?subject " + property_and_entity_type[0] + " ?name \n" +
                 " FILTER regex(?name, \"" + person + "\") .\n" +
                 "}\n" +
                 "LIMIT 10\n";
             SparqlClient test = new SparqlClient("http://localhost:2020/sparql", sparql);
             for (Map<String, String> bindings : test.variableBindings()) {
               System.out.print("D2R result:" + bindings);
               if (bindings.keySet().size() > 0) {
                 String blank_node = blankNodeURI("person");
                 out.println(blank_node + " <http://knowledgebooks.com/rdf/personName> \"" + person.replaceAll("\"", "'") + "\" .");
                 out.println("<" + uri + "> <http://knowledgebooks.com/rdf/containsPerson> " + blank_node + " .");
                 out.println(blank_node + " <http://knowledgebooks.com/rdf/d2r_uri> \"" + bindings.get("subject") + "\" .");
               }
             }
           }
        } else if (property_and_entity_type[1].equals("place")) {
          for (String place : places) {
            // perform SPARQL queries to D2R server:
            String sparql =
              "PREFIX vocab: <http://localhost:2020/vocab/resource/>\n" +
                "SELECT ?subject ?name WHERE {\n" +
                "     ?subject " + property_and_entity_type[0] + " ?name \n" +
                " FILTER regex(?name, \"" + place + "\") .\n" +
                "}\n" +
                "LIMIT 10\n";
            SparqlClient test = new SparqlClient("http://localhost:2020/sparql", sparql);
            for (Map<String, String> bindings : test.variableBindings()) {
              System.out.print("D2R result:" + bindings);
              if (bindings.keySet().size() > 0) {
                String blank_node = blankNodeURI("place");
                out.println(blank_node + " <http://knowledgebooks.com/rdf/placeName> \"" + place.replaceAll("\"", "'") + "\" .");
                out.println("<" + uri + "> <http://knowledgebooks.com/rdf/containsPlace> " + blank_node + " .");
                out.println(blank_node + " <http://knowledgebooks.com/rdf/d2r_uri> \"" + bindings.get("subject") + "\" .");
              }
            }
          }
        }
      }
    }
    out.close();
  }
  private static String blankNodeURI(String name_type) {
    return "_:dr" + name_type + blank_node_count++;
  }
  static long blank_node_count = 1;

  // for testing only:
  public static void main(String[] args) throws Exception {
    List<String> people = new ArrayList<String>();
    people.add("Mark Watson");
    List<String> places = new ArrayList<String>();
    places.add("Sedona");
    new EntityToD2RHelpers("http://example.com", "testdata/databaseinfo.txt", people, places, new PrintWriter("testdata/gen_rdf.nt"));
    //new EntityToD2RHelpers("http://example.com", "testdata/databaseinfo.txt", people, places, new PrintWriter(System.out));
  }
}
