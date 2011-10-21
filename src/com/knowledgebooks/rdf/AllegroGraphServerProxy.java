package com.knowledgebooks.rdf;

import com.franz.agraph.repository.*;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 12, 2010
 */
public class AllegroGraphServerProxy implements RdfServiceProxy {
  public AllegroGraphServerProxy() throws Exception {
    if (System.getenv("ALLEGROGRAPH_SERVER") == null)
      System.err.println("ERROR: must set environment variable 'ALLEGROGRAPH_SERVER'");
    if (System.getenv("ALLEGROGRAPH_PORT") == null)
      System.err.println("ERROR: must set environment variable 'ALLEGROGRAPH_PORT'");
    if (System.getenv("ALLEGROGRAPH_USERNAME") == null)
      System.err.println("ERROR: must set environment variable 'ALLEGROGRAPH_USERNAME'");
    if (System.getenv("ALLEGROGRAPH_PASSWD") == null)
      System.err.println("ERROR: must set environment variable 'ALLEGROGRAPH_PASSWD'");
    init(System.getenv("ALLEGROGRAPH_USERNAME"), System.getenv("ALLEGROGRAPH_PASSWD"), System.getenv("ALLEGROGRAPH_SERVER"), Integer.parseInt(System.getenv("ALLEGROGRAPH_PORT"))); // "localhost", 10035);  // 4567 for AG 3.2, 10035 for AG 4
  }

  public AllegroGraphServerProxy(String userName, String password, String host, int port) throws Exception {
    init(userName, password, host, port);
  }

  private void init(String userName, String password, String host, int port) throws Exception {
    server = new AGServer("http://" + host + ":" + port, userName, password);

    // throw away code: just to get the libraries right:
    System.out.println("Available catalogs: " + server.listCatalogs());
    rootCatalog = server.getRootCatalog();          // open rootCatalog
    List<String> repos = rootCatalog.listRepositories();
    for (String repo : repos) System.out.println("Existing repository: " + repo);
  }

  private AGRepositoryConnection conn;
  private AGServer server;
  private AGCatalog rootCatalog;
  private AGRepository currentRepository = null;
  AGValueFactory valueFactory;

  public void close() {
    try {
      conn.close();
    } catch (Exception ex) {
    }
  }

  public List<String> getCatalogNames() throws Exception {
    return server.listCatalogs();
  }  // Franz specific

  public void deleteRepository(String name) throws Exception {
    rootCatalog.deleteRepository(name);
  }

  public void createRepository(String name) throws Exception {
    currentRepository = rootCatalog.createRepository(name);
    System.out.println("Created repository: " + currentRepository);
    currentRepository.initialize();
    conn = currentRepository.getConnection();
    valueFactory = conn.getRepository().getValueFactory();
  }

  public void setCurrentRepository(String name) throws Exception {
    currentRepository = rootCatalog.createRepository(name);
    valueFactory = conn.getRepository().getValueFactory();
  }

  public void addTriple(String subject, String predicate, String object) throws Exception {
    System.out.println(" --- addTriple(String subject, String predicate, String object)  object = " + object);
    URI s = valueFactory.createURI(subject);
    URI p = valueFactory.createURI(predicate);
    if (object.startsWith("http://") || object.startsWith("https://")) {
      conn.add(s, p, valueFactory.createURI(object));
    } else {
      conn.add(s, p, valueFactory.createLiteral(object));
    }
  }

  public void addTriple(String subject, URI predicate, String object) throws Exception {
    System.out.println(" --- addTriple(String subject, URI predicate, String object)  object = " + object);
    URI s = valueFactory.createURI(subject);
    if (object.startsWith("http://") || object.startsWith("https://")) {
      conn.add(s, predicate, valueFactory.createURI(object));
    } else {
      conn.add(s, predicate, valueFactory.createLiteral(object));
    }
  }

  public void addTriple(String subject, String predicate, Literal object) throws Exception {
    System.out.println(" --- addTriple(String subject, String predicate, Literal object)  object = " + object);
    URI s = valueFactory.createURI(subject);
    URI p = valueFactory.createURI(predicate);
    conn.add(s, p, object);
  }

  public void addTriple(String subject, URI predicate, Literal object) throws Exception {
    System.out.println(" --- addTriple(String subject, URI predicate, Literal object)  object = " + object);
    URI s = valueFactory.createURI(subject);
    System.out.println("**** " + predicate + "  " + object);
    conn.add(s, predicate, object);
  }

  public List<List<String>> textSearch(String text) throws Exception {
    return query("SELECT ?s ?p ?o WHERE { ?s ?p ?o . ?s fti:match '" + text + "' . }");
  }

  public List<String> textSearch_scala(String text) throws Exception {
    return query_scala("SELECT ?s ?p ?o WHERE { ?s ?p ?o . ?s fti:match '" + text + "' . }");
  }

  public List<List<String>> query(String sparql) throws Exception {
    List<List<String>> ret = new ArrayList<List<String>>();
    TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
    TupleQueryResult result = tupleQuery.evaluate();
    try {
      List<String> bindingNames = result.getBindingNames();
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();

        int size2 = bindingSet.size();
        ArrayList<String> vals = new ArrayList<String>(size2);
        for (int i = 0; i < size2; i++) vals.add(bindingSet.getValue(bindingNames.get(i)).stringValue());
        ret.add(vals);
      }
    } finally {
      result.close();
    }
    return ret;
  }

  public List<String> query_scala(String sparql) throws Exception {
    List<String> ret = new ArrayList<String>();
    TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
    TupleQueryResult result = tupleQuery.evaluate();
    boolean save_return_size = true;
    try {
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        int size2 = bindingSet.size();
        List<String> bindingNames = result.getBindingNames();
        if (save_return_size) {
          save_return_size = false;
          ret.add("" + size2);
          ret.addAll(bindingNames);
        }
        for (String name : bindingNames) {
          ret.add(bindingSet.getValue(name).stringValue());
        }
      }
    } finally {
      result.close();
    }
    return ret;
  }

  public void registerFreetextPredicate(String predicate) throws Exception {
    //conn.registerFreetextPredicate(valueFactory.createURI(predicate));
    // NOTE: you would want to change this in any sort of production code:
    conn.createFreetextIndex("index"+System.currentTimeMillis(), new URI[]{valueFactory.createURI(predicate)});
    //if (Triple.RDF_LOCATION == null) Triple.RDF_LOCATION = location;
  }

  public void initializeGeoLocation(Double strip_width_in_miles) throws Exception {
    //initializeGeoLocation(strip_width_in_miles.floatValue());
    Triple.RDF_LOCATION = location = valueFactory.createURI("http://knowledgebooks.com/rdf/location");
    sphericalSystemDegree = conn.registerSphericalType(strip_width_in_miles.floatValue(), "degree");
  }

  public void initializeGeoLocation(float strip_width_in_miles) throws Exception {
    //initializeGeoLocation(strip_width_in_miles);
    Triple.RDF_LOCATION = location = valueFactory.createURI("http://knowledgebooks.com/rdf/location");
    sphericalSystemDegree = conn.registerSphericalType(strip_width_in_miles, "degree");
  }

  public List<List<String>> getLocations(double latitude, double longitude, double radius_in_km) throws Exception {
    return getLocations((float) latitude, (float) longitude, (float) radius_in_km);
  }

  public List<List<String>> getLocations(Double latitude, Double longitude, Double radius_in_km) throws Exception {
    return getLocations(latitude.floatValue(), longitude.floatValue(), radius_in_km.floatValue());
  }


  public List<List<String>> getLocations(float latitude, float longitude, float radius_in_km) throws Exception {
    List<List<String>> ret = new ArrayList<List<String>>();
    RepositoryResult<Statement> result = conn.getGeoHaversine(sphericalSystemDegree, location, latitude, longitude, radius_in_km, "km", 0, false);
    System.out.println(" ##  getLocations: result = " + result);
    try {
      while (result.hasNext()) {
        Statement statement = result.next();
        System.out.println("  ##   getLocations: statement = " + statement);
        Value s = statement.getSubject();
        Value p = statement.getPredicate();
        Value o = statement.getObject();
        List<String> sl = new ArrayList<String>(3);
        sl.add(s.stringValue());
        sl.add(p.stringValue());
        sl.add(o.stringValue());
        ret.add(sl);
      }
    } catch (Exception ex) {
      System.out.println("***** error: " + ex);
    } finally {
      result.close();
    }
    return ret;
  }

  public List<String> getLocations_scala(Double latitude, Double longitude, Double radius_in_km) throws Exception {
    List<String> ret = new ArrayList<String>();
    RepositoryResult<Statement> result = conn.getGeoHaversine(sphericalSystemDegree, location, latitude.floatValue(), longitude.floatValue(), radius_in_km.floatValue(), "km", 0, false);
    try {
      ret.add("3");
      ret.add("s");
      ret.add("p");
      ret.add("o");
      while (result.hasNext()) {
        Statement statement = result.next();
        Value s = statement.getSubject();
        Value p = statement.getPredicate();
        Value o = statement.getObject();
        ret.add(s.stringValue());
        ret.add(p.stringValue());
        ret.add(o.stringValue());
      }
    } catch (Exception ex) {
      System.out.println("** ERROR: " + ex);
      ex.printStackTrace();
    } finally {
      result.close();
    }
    return ret;
  }

  public Literal latLonToLiteral(double lat, double lon) {
    return latLonToLiteral((float) lat, (float) lon);
  }

  public Literal latLonToLiteral(float lat, float lon) {
    String slat, slon;
    if (lat < 0) slat = "" + lat;
    else slat = "+" + lat;
    if (lon < 0) slon = "" + lon;
    else slon = "+" + lon;
    System.out.println("******* sphericalSystemDegree = " + sphericalSystemDegree);
    return valueFactory.createLiteral(slat + slon, sphericalSystemDegree);
  }

  private URI sphericalSystemDegree;
  private URI location;
}
