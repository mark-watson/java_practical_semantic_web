/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 20, 2010
 * Time: 11:09:16 AM
 * To change this template use File | Settings | File Templates.
 */

import java.util.List;

import com.knowledgebooks.rdf.RdfServiceProxy;
import com.knowledgebooks.rdf.SesameEmbeddedProxy;
import com.knowledgebooks.rdf.Triple;
import com.knowledgebooks.rdf.implementation.GeoHash;
import com.knowledgebooks.rdf.implementation.Haversine;

public class TestEmbeddedSesame {
  public static void main(String[] args) throws Exception {
    new TestEmbeddedSesame();
    for (int i = 0; i < 0; i++) {                // SKIP FOR NOW
      float lat = 10f + (float) Math.random() * 6;
      float lon = 10f + (float) Math.random() * 6;
      String geohash = GeoHash.encode(lat, lon);
      float lat2 = 10f + (float) Math.random() * 6;
      float lon2 = 10f + (float) Math.random() * 6;
      String geohash2 = GeoHash.encode(lat2, lon2);
      float dist = Haversine.distance(lat, lon, lat2, lon2);
      System.out.println("\n\ndistance=" + dist);
      System.out.println(geohash + "\t" + lat + "\t" + lon);
      System.out.println(geohash2 + "\t" + lat2 + "\t" + lon2);
    }
  }

  public TestEmbeddedSesame() throws Exception {
    RdfServiceProxy ts = new SesameEmbeddedProxy();
    ts.deleteRepository("test-repo1");
    ts.createRepository("test-repo1");
    ts.registerFreetextPredicate("http://example.org/ontology/name");  // do this before adding triples
    ts.registerFreetextPredicate("http://knowledgebooks.com/ontology/summary");  // do this before adding triples
    ts.initializeGeoLocation(3d);

    ts.addTriple("<http://kbsportal.com/oak_creek_flooding>", "<http://knowledgebooks.com/ontology/storyType>", "<http://knowledgebooks.com/ontology/disaster>");
    ts.addTriple("<http://kbsportal.com/oak_creek_flooding>", "<http://knowledgebooks.com/ontology/summary>", "Oak Creek flooded last week affecting 5 businesses");
    ts.addTriple("<http://kbsportal.com/oak_creek_flooding>", "http://example.org/ontology/name", "Beth");
    ts.addTriple("http://example.org/people/alice", Triple.RDF_TYPE, "http://example.org/people/alice");
    ts.addTriple("http://example.org/people/alice", "http://example.org/ontology/name", "Alice");
    ts.addTriple("http://example.org/people/alice", Triple.RDF_LOCATION, ts.latLonToLiteral(+37.783333, -122.433334));
    String sparql_query = "SELECT ?subject ?object WHERE { ?subject <http://knowledgebooks.com/ontology/storyType> ?object . }";
    List<List<String>> results = ts.query(sparql_query);
    for (List<String> result : results) System.out.println("result: " + result);
    results = ts.getLocations(+37.113333d, -122.113334d, 500d);
    for (List<String> result : results) {
      System.out.println("Geolocation result: " + result);
    }
    List<List<String>> test_results;
    test_results = ts.textSearch("flooded");
    for (List<String> result : test_results) {
      System.out.println("test search result (oak creek flooded): " + result);
    }
    test_results = ts.textSearch("beth");
    for (List<String> result : test_results) {
      System.out.println("test search result (beth): " + result);
    }
    test_results = ts.textSearch("alice"); // "last week");
    for (List<String> result : test_results) {
      System.out.println("test search result (alice): " + result);
    }
    ts.close();
  }
}
