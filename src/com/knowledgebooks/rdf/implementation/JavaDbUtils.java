package com.knowledgebooks.rdf.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 23, 2010
 * Time: 11:01:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class JavaDbUtils {
  public static void debug(Connection con) throws Exception {
    System.out.println("\nDUMP OF TABLE GEOLOC:");
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT geohash, subject, predicate, lat_lon_object, lat, lon FROM geoloc");
    while (rs.next())
      System.out.println("geoloc row: " + rs.getString("geohash") + " " + rs.getString("subject") + " " + rs.getString("predicate") + " " + rs.getString("lat_lon_object") + " " + rs.getFloat("lat") + " " + rs.getFloat("lon"));
    System.out.println();
    System.out.println("\nDUMP OF TABLE FREE TEXT PREDICATES:");
    stmt = con.createStatement();
    rs = stmt.executeQuery("SELECT predicate FROM free_text_predicates");
    while (rs.next()) System.out.println("free_text_predicates row: " + rs.getString("predicate"));
    System.out.println();
  }
}
