package com.knowledgebooks.rdf;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import java.util.List;

public interface RdfServiceProxy {
  public void deleteRepository(String name) throws Exception;

  public void createRepository(String name) throws Exception;

  public void addTriple(String subject, String predicate, String object) throws Exception;

  public void addTriple(String subject, URI predicate, String object) throws Exception;

  public void addTriple(String subject, String predicate, Literal object) throws Exception;

  public void addTriple(String subject, URI predicate, Literal object) throws Exception;

  public List<List<String>> textSearch(String text) throws Exception;

  public List<String> textSearch_scala(String text) throws Exception;

  public List<List<String>> query(String sparql) throws Exception;

  public List<String> query_scala(String sparql) throws Exception;

  public void registerFreetextPredicate(String predicate) throws Exception;

  public void initializeGeoLocation(Double strip_width_in_miles) throws Exception;

  public List<List<String>> getLocations(Double latitude, Double longitude, Double radius_in_km) throws Exception;

  public List<String> getLocations_scala(Double latitude, Double longitude, Double radius_in_km) throws Exception;

  public Literal latLonToLiteral(double lat, double lon);

  public void close();
}
