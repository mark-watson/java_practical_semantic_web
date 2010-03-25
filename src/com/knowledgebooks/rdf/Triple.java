package com.knowledgebooks.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 13, 2010
 * Time: 8:32:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class Triple {
  public String subject;
  public String predicate;
  public String obj;

  public Triple(String subject, String predicate, String obj) {
    this.subject = subject;
    this.predicate = predicate;
    this.obj = obj;
  }

  public Triple(Object subject, Object predicate, Object obj) {
    this.subject = "" + subject;
    this.predicate = "" + predicate;
    this.obj = "" + obj;
  }

  public String toString() {
    return "<" + subject + ", " + predicate + ", " + obj + ">";
  }

  public static String RDF_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"; // + RDF.TYPE;
  public static String RDF_PROPERTY = "" + RDF.PROPERTY;
  //public static String RDF_LOCATION = null; // "<http://knowledgebooks.com/rdf/location>";
  public static URI RDF_LOCATION = null; // "<http://knowledgebooks.com/rdf/location>";
}
