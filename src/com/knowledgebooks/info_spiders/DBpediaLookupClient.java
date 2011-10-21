package com.knowledgebooks.info_spiders;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

// Use Georgi Kobilarov's DBpedia lookup web service
//    ref: http://lookup.dbpedia.org/api/search.asmx?op=KeywordSearch
//    example: http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString=Flagstaff&QueryClass=XML&MaxHits=10

/**
 * Searches return results that contain any of the search terms. I am going to filter
 * the results to ignore results that do not contain all search terms.
 */


public class DBpediaLookupClient extends DefaultHandler {
  public DBpediaLookupClient(String query) throws Exception {
    this.query = query;
    //System.out.println("SparqlClient("+endpoint_URL+", "+sparql+")");
    HttpClient client = new HttpClient();
    //client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);

    String query2 = URLEncoder.encode(query, "utf-8");
    HttpMethod method =
      //new GetMethod("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString=" +
      //  query2 + "&QueryClass=JSON&MaxHits=10");
      new GetMethod("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString=Flagstaff&QueryClass=JSON&MaxHits=10");
    //method.setFollowRedirects(true);
    try {
      client.executeMethod(method);
      System.out.println(method);
      //System.out.println(method.getResponseBodyAsString());
      //if (true) return;
      InputStream ins = method.getResponseBodyAsStream();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser sax = factory.newSAXParser();
      sax.parse(ins, this);
    } catch (HttpException he) {
      System.err.println("Http error connecting to lookup.dbpedia.org");
    } catch (IOException ioe) {
      System.err.println("Unable to connect to lookup.dbpedia.org");
    }
    method.releaseConnection();
  }

  private List<Map<String, String>> variableBindings = new ArrayList<Map<String, String>>();
  private Map<String, String> tempBinding = null;
  private String lastElementName = null;

  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    //System.out.println("startElement " + qName);
    if (qName.equalsIgnoreCase("result")) {
      tempBinding = new HashMap<String, String>();
    }
    lastElementName = qName;
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase("result")) {
      if (!variableBindings.contains(tempBinding) && containsSearchTerms(tempBinding))
        variableBindings.add(tempBinding);
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    String s = new String(ch, start, length).trim();
    if (s.length() > 0) {
      if ("Description".equals(lastElementName)) tempBinding.put("Description", s);
      if ("URI".equals(lastElementName)) tempBinding.put("URI", s);
      if ("Label".equals(lastElementName)) tempBinding.put("Label", s);
    }
  }

  public List<Map<String, String>> variableBindings() {
    return variableBindings;
  }
  private boolean containsSearchTerms(Map<String, String> bindings) {
    StringBuilder sb = new StringBuilder();
    for (String value : bindings.values()) sb.append(value);  // do not need white space
    String text = sb.toString().toLowerCase();
    StringTokenizer st = new StringTokenizer(this.query);
    while (st.hasMoreTokens()) {
      if (text.indexOf(st.nextToken().toLowerCase()) == -1) {
        return false;
      }
    }
    return true;
  }
  private String query = "";
}