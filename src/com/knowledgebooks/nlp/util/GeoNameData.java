package com.knowledgebooks.nlp.util;

import org.geonames.Toponym;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */
public class GeoNameData {
  public enum GeoType {
    CITY, COUNTRY, STATE, RIVER, MOUNTAIN, UNKNOWN
  }

  ;
  public int geoNameId = 0;
  public GeoType geoType = GeoType.UNKNOWN;
  public String name = "";
  public double latitude = 0;
  public double longitude = 0;
  public String countryCode = "";

  public GeoNameData(Toponym toponym) {
    geoNameId = toponym.getGeoNameId();
    latitude = toponym.getLatitude();
    longitude = toponym.getLongitude();
    name = toponym.getName();
    countryCode = toponym.getCountryCode();
    if (toponym.getFeatureClassName().startsWith("city")) geoType = GeoType.CITY;
    if (toponym.getFeatureClassName().startsWith("country")) geoType = GeoType.COUNTRY;
    if (toponym.getFeatureClassName().startsWith("state")) geoType = GeoType.STATE;
    if (toponym.getFeatureClassName().startsWith("stream")) geoType = GeoType.RIVER;
    if (toponym.getFeatureClassName().startsWith("mountain")) geoType = GeoType.MOUNTAIN;
  }

  public GeoNameData() {
  }

  public String toString() {
    return "[GeoNameData: " + name + ", type: " + geoType + ", country code: " + countryCode + ", ID: " + geoNameId + ", latitude: " + latitude + ", longitude: " + longitude + "]";
  }
}
