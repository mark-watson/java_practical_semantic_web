import com.knowledgebooks.rdf.AllegroGraphServerProxy;
import com.knowledgebooks.rdf.RdfServiceProxy;
import com.knowledgebooks.rdf.Triple;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 13, 2010
 * Time: 9:59:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestRemoteAllegroGraphServer {
  public static void main(String[] args) throws Exception {
    System.out.println(System.getenv("ALLEGROGRAPH_SERVER"));
    RdfServiceProxy proxy = new AllegroGraphServerProxy();
    proxy.deleteRepository("testrepo1");
    proxy.createRepository("testrepo1");
    proxy.registerFreetextPredicate("http://example.org/ontology/name");  // do this before adding triples
    proxy.initializeGeoLocation(10d);
    proxy.addTriple("http://example.org/people/alice", Triple.RDF_TYPE, "http://example.org/people/alice");
    proxy.addTriple("http://example.org/people/alice", "http://example.org/ontology/name", "Alice");
    // "+37.783333-122.433334"
    proxy.addTriple("http://example.org/people/alice", Triple.RDF_LOCATION, proxy.latLonToLiteral(+37.86385, -122.3430));
    proxy.addTriple("http://example.org/people/bob", Triple.RDF_LOCATION, proxy.latLonToLiteral(+37.88385, -122.3130));
    proxy.addTriple("http://example.org/people/ted", Triple.RDF_LOCATION, proxy.latLonToLiteral(+37.81385, -122.3230));
    List<List<String>> results = proxy.query("SELECT ?s ?p ?o  WHERE {?s ?p ?o .}");
    for (List<String> result : results) {
      System.out.println("All triples result: " + result);
    }

    //results = proxy.query("SELECT ?s ?p ?o WHERE { ?s ?p ?o . ?s fti:match 'Ali*' . }");
    results = proxy.textSearch("Alice");
    for (List<String> result : results) {
      System.out.println("Wild card text search result: " + result);
    }

    results = proxy.getLocations(+37.88385d, -122.3130d, 500d);
    for (List<String> result : results) {
      System.out.println("Geolocation result: " + result);
    }
  }
}

