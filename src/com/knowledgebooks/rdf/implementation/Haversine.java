package com.knowledgebooks.rdf.implementation;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 23, 2010
 * Time: 1:00:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Haversine {
  public static float distance(float lat1_deg, float lon1_deg, float lat2_deg, float lon2_deg) {
    double earthRadius = 6371; // in kilometers

    double lat1 = Math.toRadians(lat1_deg);
    double lon1 = Math.toRadians(lon1_deg);
    double lat2 = Math.toRadians(lat2_deg);
    double lon2 = Math.toRadians(lon2_deg);

    double dlon = (lon2 - lon1);
    double dlat = (lat2 - lat1);

    double a = (Math.sin(dlat / 2)) * (Math.sin(dlat / 2))
      + (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2)))
      * (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2)));

    double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));
    double km = earthRadius * c;

    return (float) km;

  }
}
