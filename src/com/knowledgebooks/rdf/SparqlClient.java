package com.knowledgebooks.rdf;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */
public class SparqlClient extends DefaultHandler {
  public SparqlClient(String endpoint_URL, String sparql) throws Exception {
    //System.out.println("SparqlClient("+endpoint_URL+", "+sparql+")");
    HttpClient client = new HttpClient();
    client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);

    String req = URLEncoder.encode(sparql, "utf-8");
    HttpMethod method = new GetMethod(endpoint_URL + "?query=" + req);
    method.setFollowRedirects(false);
    try {
      client.executeMethod(method);
      //System.out.println(method.getResponseBodyAsString());
      //if (true) return;
      InputStream ins = method.getResponseBodyAsStream();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser sax = factory.newSAXParser();
      sax.parse(ins, this);
    } catch (HttpException he) {
      System.err.println("Http error connecting to '" + endpoint_URL + "'");
    } catch (IOException ioe) {
      System.err.println("Unable to connect to '" + endpoint_URL + "'");
    }
    method.releaseConnection();
  }

  private List<Map<String, String>> variableBindings = new ArrayList<Map<String, String>>();
  private Map<String, String> tempBinding = null;
  private String tempVariableName = null;
  private String lastElementName = null;

  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    //System.out.println("startElement " + qName);
    if (qName.equalsIgnoreCase("result")) {
      tempBinding = new HashMap<String, String>();
    }
    if (qName.equalsIgnoreCase("binding")) {
      tempVariableName = attributes.getValue("name");
    }
    lastElementName = qName;
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase("result")) {
      if (!variableBindings.contains(tempBinding))
        variableBindings.add(tempBinding);
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    String s = new String(ch, start, length).trim();
    if (s.length() > 0) {
      if ("literal".equals(lastElementName)) tempBinding.put(tempVariableName, s);
      if ("uri".equals(lastElementName)) tempBinding.put(tempVariableName, "<" + s + ">");
    }
  }

  public List<Map<String, String>> variableBindings() {
    return variableBindings;
  }
}
