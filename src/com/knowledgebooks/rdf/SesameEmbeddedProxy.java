package com.knowledgebooks.rdf;

import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.knowledgebooks.rdf.implementation.*;
import org.openrdf.model.*;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 * Wrapper class for an embedded Sesame RDF data store. This class was derived
 * from the TripleStoreSesameManager class in the commercial (but free for
 * non commercial use) product KB_bundle as descibed at http://knowledgebooks.com
 * <p/>
 * Copyright 2008 by Mark Watson. All rights reserved.
 * <p/>
 * This software is not public domain. It can be legally
 * used under either the LGPL version 3 or Apache 2 license.
 */
public class SesameEmbeddedProxy implements RdfServiceProxy {

  public SesameEmbeddedProxy() throws Exception {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
  }

  public List<String> getCatalogNames() throws Exception {
    return new ArrayList<String>(0);
  }

  public void deleteRepository(String name) throws Exception {
    FileUtils.deleteDir(new File("tempdata/" + name + ".sesame_repo"));
    FileUtils.deleteDir(new File("tempdata/" + name + ".sesame_aux_db"));
    FileUtils.deleteDir(new File("tempdata/" + name + ".sesame_lucene"));
  }

  public void createRepository(String name) throws Exception {
    rdfStoreRootPath = "tempdata/" + name + ".sesame_lucene";
    File dataDir = new File("tempdata/" + name + ".sesame_repo");
    String indexes = "spoc,posc,cosp";
    myRepository = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(dataDir, indexes)));
    myRepository.initialize();
    con = myRepository.getConnection();
    valueFactory = con.getRepository().getValueFactory();
    String db_url = "jdbc:derby:tempdata/" + name + ".sesame_aux_db;create=true";
    try {
      database_connection = DriverManager.getConnection(db_url);
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    // create table free_text_predicates if it does not already exist:
    try {
      java.sql.Statement stmt = database_connection.createStatement();
      int status = stmt.executeUpdate("create table free_text_predicates (predicate varchar(120))");
      System.out.println("status for creating table free_text_predicates = " + status);
    } catch (SQLException ex) {
      System.out.println("Error trying to create table free_text_predicates: " + ex);
    }
  }

  public void setCurrentRepository(String name) throws Exception {
    createRepository(name); // ?? does this work for an already created repository?
  }

  public void registerFreetextPredicate(String predicate) {  // do this before adding triples
    try {
      predicate = fix_uri_format(predicate);
      java.sql.Statement stmt = database_connection.createStatement();
      ResultSet rs = stmt.executeQuery("select * from free_text_predicates where predicate = '" + predicate + "'");
      if (rs.next() == false) {
        stmt.executeUpdate("insert into free_text_predicates values('" + predicate + "')");
      }
    } catch (SQLException ex) {
      System.out.println("Error trying to write to table free_text_predicates: " + ex + "\n" + predicate);
    }
  }

  public void initializeGeoLocation(Float strip_width) {
    initializeGeoLocation(strip_width.doubleValue());
  }

  public void initializeGeoLocation(Double strip_width) {
    Triple.RDF_LOCATION = valueFactory.createURI("http://knowledgebooks.com/rdf/location");
    System.out.println("Initializing geolocation database...");
    this.strip_width = strip_width.floatValue();
    // create table geoloc if it does not already exist:
    try {
      java.sql.Statement stmt = database_connection.createStatement();
      int status = stmt.executeUpdate("create table geoloc (geohash char(15), subject varchar(120), predicate varchar(120), lat_lon_object varchar(120), lat float, lon float)");
      System.out.println("status for creating table geoloc = " + status);
    } catch (SQLException ex) {
      System.out.println("Error trying to create table geoloc: " + ex);
    }
  }

  public String latLonToLiteralXXX(double lat, double lon) {
    String lat2, lon2;
    lat2 = lat < 0 ? "" + lat : "+" + lat;
    lon2 = lon < 0 ? "" + lon : "+" + lon;
    return "\"" + lat2 + lon2 + "\"^^<http://knowledgebooks.com/rdf/latlon>";
  }

  public Literal latLonToLiteral(double lat, double lon) {
    String slat, slon;
    if (lat < 0) slat = "" + lat;
    else slat = "+" + lat;
    if (lon < 0) slon = "" + lon;
    else slon = "+" + lon;
    return valueFactory.createLiteral(slat + slon, "http://knowledgebooks.com/rdf/latlon");
  }

  final private String fix_uri_format(String uri) {
    if ((uri.indexOf("http://") == -1 && uri.indexOf("https://http") == -1) || uri.indexOf("^^<") > -1) {
      return uri;
    } else {
      if (uri.startsWith("<") && uri.endsWith(">")) return uri;
      return "<" + uri + ">";
    }
  }

  final private String fix_literal_format(String object) {
    if (object.indexOf("<http://") > -1 || object.indexOf("<https://http") > -1) {
      return object;
    } else {
      if (object.startsWith("\"") && object.endsWith("\"")) return object;
      return "\"" + object + "\"";
    }
  }


  public void addTriple(String subject, String predicate, Literal object) throws Exception {
    addTriple(subject, predicate, object.toString());
  }

  public void addTriple(String subject, String predicate, String object) throws Exception {
    subject = fix_uri_format(subject);
    predicate = fix_uri_format(predicate);
    object = fix_uri_format(object);
    object = fix_literal_format(object);
    String ntriple = subject.replaceAll(" />", ">") + " " + predicate + " " + object + " .";
    StringReader sr = new StringReader(ntriple);
    con.add(sr, "", RDFFormat.NTRIPLES);
    if (object.indexOf("^^<http://knowledgebooks.com/rdf/latlon>") > -1) {
      int index1 = object.indexOf("^^");
      String object2 = object.substring(1, index1 - 2);
      String stemp = object2.substring(1);
      int index2 = stemp.indexOf("+");
      if (index2 == -1) index2 = stemp.indexOf("-");
      float lat = Float.parseFloat(object2.substring(0, index2 + 1));
      float lon = Float.parseFloat(object2.substring(index2 + 1));
      java.sql.Statement stmt = database_connection.createStatement();
      int status = stmt.executeUpdate("insert into geoloc values ('" +
        GeoHash.encode(lat, lon) + "', '" + subject + "', '" + predicate + "', '" + object + "', " + lat + ", " + lon + ")");
      JavaDbUtils.debug(database_connection);
    }
    java.sql.Statement stmt = database_connection.createStatement();
    ResultSet rs = stmt.executeQuery("select * from free_text_predicates where predicate = '" + predicate + "'");
    if (rs.next() == true) {
      // This triple is tagged to index the object part of the triple for free text search:
      if (luceneRdfManager == null) luceneRdfManager = new LuceneRdfManager(rdfStoreRootPath);
      luceneRdfManager.addTripleToIndex(subject, predicate, object);
    }

  }

  public void addTriple(String subject, URI predicate2, Literal object) throws Exception {
    //System.out.println("---  addTriple(String subject, URI predicate, Literal object) predicate2="+predicate2+", object.toString()="+object.toString());
    subject = fix_uri_format(subject);
    String predicate = fix_uri_format("" + predicate2);
    //object = fix_uri_format(object);
    //object = fix_literal_format(object);
    String ntriple = subject.replaceAll(" />", ">") + " " + predicate + " " + object + " .";
    StringReader sr = new StringReader(ntriple);
    con.add(sr, "", RDFFormat.NTRIPLES);
    if (object.toString().indexOf("@http://knowledgebooks.com/rdf/latlon") > -1) {
      int index1 = object.toString().indexOf("@");
      String object2 = object.toString().substring(1, index1 - 2);
      String stemp = object2.substring(1);
      int index2 = stemp.indexOf("+");
      if (index2 == -1) index2 = stemp.indexOf("-");
      float lat = Float.parseFloat(object2.substring(0, index2 + 1));
      float lon = Float.parseFloat(object2.substring(index2 + 1));
      java.sql.Statement stmt = database_connection.createStatement();
      stmt.executeUpdate("insert into geoloc values ('" +
        GeoHash.encode(lat, lon) + "', '" + subject + "', '" + predicate + "', '" + object + "', " + lat + ", " + lon + ")");
      //JavaDbUtils.debug(database_connection);
    }
    java.sql.Statement stmt = database_connection.createStatement();
    ResultSet rs = stmt.executeQuery("select * from free_text_predicates where predicate = '" + predicate + "'");
    if (rs.next() == true) {
      // This triple is tagged to index the object part of the triple for free text search:
      if (luceneRdfManager == null) luceneRdfManager = new LuceneRdfManager(rdfStoreRootPath);
      luceneRdfManager.addTripleToIndex(subject, "" + predicate, object.toString());
    }

  }

  public void addTriple(String subject, URI predicate2, String object) throws Exception {
    //System.out.println("---  addTriple(String subject, URI predicate, String object) predicate2="+predicate2);
    subject = fix_uri_format(subject);
    String predicate = fix_uri_format("" + predicate2);
    object = fix_uri_format(object);
    object = fix_literal_format(object);
    String ntriple = subject.replaceAll(" />", ">") + " " + predicate + " " + object + " .";
    StringReader sr = new StringReader(ntriple);
    con.add(sr, "", RDFFormat.NTRIPLES);
    if (object.indexOf("^^<http://knowledgebooks.com/rdf/latlon>") > -1) {
      int index1 = object.indexOf("^^");
      String object2 = object.substring(1, index1 - 2);
      String stemp = object2.substring(1);
      int index2 = stemp.indexOf("+");
      if (index2 == -1) index2 = stemp.indexOf("-");
      float lat = Float.parseFloat(object2.substring(0, index2 + 1));
      float lon = Float.parseFloat(object2.substring(index2 + 1));
      java.sql.Statement stmt = database_connection.createStatement();
      int status = stmt.executeUpdate("insert into geoloc values ('" +
        GeoHash.encode(lat, lon) + "', '" + subject + "', '" + predicate + "', '" + object + "', " + lat + ", " + lon + ")");
      JavaDbUtils.debug(database_connection);
    }
    java.sql.Statement stmt = database_connection.createStatement();
    ResultSet rs = stmt.executeQuery("select * from free_text_predicates where predicate = '" + predicate + "'");
    if (rs.next() == true) {
      // This triple is tagged to index the object part of the triple for free text search:
      if (luceneRdfManager == null) luceneRdfManager = new LuceneRdfManager(rdfStoreRootPath);
      luceneRdfManager.addTripleToIndex(subject, "" + predicate, object);
    }

  }

  public List<List<String>> textSearch(String query) throws Exception {
    if (luceneRdfManager == null) luceneRdfManager = new LuceneRdfManager(rdfStoreRootPath);
    return luceneRdfManager.searchIndex(query);
  }

  public List<String> textSearch_scala(String query) throws Exception {
    List<String> ret = new ArrayList<String>();
    if (luceneRdfManager == null) luceneRdfManager = new LuceneRdfManager(rdfStoreRootPath);
    List<List<String>> ll = luceneRdfManager.searchIndex(query);
    ret.add("3");
    ret.add("s");
    ret.add("p");
    ret.add("o");
    for (List<String> l : ll) ret.addAll(l);
    return ret;
  }

  public List<List<String>> getLocations(Double lat, Double lon, Double distance) throws Exception {
    List<List<String>> ret = new ArrayList<List<String>>();
    String geohash = GeoHash.encode(lat, lon);
    System.out.println("getLocations: geohash for input lat/lon = " + geohash);
    java.sql.Statement stmt = database_connection.createStatement();
    // TBD: I am only using the first character of the geohash - make this more efficient by
    //      using distance input value to make a rough guess at the number of characters to check.
    ResultSet rs = stmt.executeQuery("select geohash, lat, lon, subject, predicate, lat_lon_object from geoloc where geohash like '" +
      geohash.substring(0, 1) + "%'");
    while (rs.next()) {
      Double lat_db = rs.getDouble("lat");
      Double lon_db = rs.getDouble("lon");
      float dist = Haversine.distance(lat.floatValue(), lon.floatValue(), lat_db.floatValue(), lon_db.floatValue());
      System.out.println("Distance: " + dist);
      //System.out.println("geohash lat lon: "+rs.getString("geohash")+" "+rs.getFloat("lat")+" "+rs.getFloat("lon"));
      if (dist <= distance) {
        List<String> sl = new ArrayList<String>(3);
        sl.add(rs.getString("subject"));
        sl.add(rs.getString("predicate"));
        sl.add(rs.getString("lat_lon_object"));
        ret.add(sl);
      }
    }
    return ret;
  }

  public List<String> getLocations_scala(Double latitude, Double longitude, Double radius_in_km) throws Exception {
    List<String> ret = new ArrayList<String>();
    String geohash = GeoHash.encode(latitude, longitude);
    System.out.println("getLocations: geohash for input lat/lon = " + geohash);
    java.sql.Statement stmt = database_connection.createStatement();
    // TBD: I am only using the first character of the geohash - make this more efficient by
    //      using distance input value to make a rough guess at the number of characters to check.
    ResultSet rs = stmt.executeQuery("select geohash, lat, lon, subject, predicate, lat_lon_object from geoloc where geohash like '" +
      geohash.substring(0, 1) + "%'");
    ret.add("3");
    ret.add("s");
    ret.add("p");
    ret.add("o");
    while (rs.next()) {
      float lat_db = rs.getFloat("lat");
      float lon_db = rs.getFloat("lon");
      float dist = Haversine.distance(latitude.floatValue(), longitude.floatValue(), lat_db, lon_db);
      System.out.println("Distance: " + dist);
      //System.out.println("geohash lat lon: "+rs.getString("geohash")+" "+rs.getFloat("lat")+" "+rs.getFloat("lon"));
      if (dist <= radius_in_km) {
        ret.add(rs.getString("subject"));
        ret.add(rs.getString("predicate"));
        ret.add(rs.getString("lat_lon_object"));
      }
    }
    return ret;
  }

  /**
   * Close the RDF repository
   */
  public void close() {
    try {
      con.close();
      database_connection.close();
      if (luceneRdfManager != null) luceneRdfManager.close();
    } catch (Exception ex) {
      Logger.getLogger(SesameEmbeddedProxy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public List<List<String>> query(String sparql_query) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    List<List<String>> ret = new ArrayList<List<String>>();
    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, sparql_query);
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
    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
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

  private Repository myRepository;
  private RepositoryConnection con;
  private String baseURI = "http://knowledgebooks.com/ontology";
  private Connection database_connection;
  private ValueFactory valueFactory;
  private float strip_width = -1;
  private LuceneRdfManager luceneRdfManager = null;
  private String rdfStoreRootPath = ".";
}

