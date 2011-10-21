import com.freebase.api.Freebase;
import com.freebase.json.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

public class FreebaseToRdf {
  public static void main(String[] args) throws Exception {
    FreebaseToRdf test = new FreebaseToRdf();
    //   test.search("Mark Louis Watson author consultant");
    Map<String, String> options = new HashMap<String, String>();
    options.put("limit", "2");
    System.out.println("Test search for Berkeley with limit=2: " + test.search("Berkeley", options));
    LatLon ll = test.geoSearchGetLatLon("City of Berkeley"); // "Flagstaff");
    System.out.println("Location of the City of Berkeley is " + ll);
    LatLon ll2 = test.geoSearchGetLatLon("TimbuctuCity"); // "Flagstaff");
    System.out.println("Location of TimbuctuCity " + ll2);
  }

  public FreebaseToRdf() {
    this.freebase = Freebase.getFreebase();

  }

  public JSON search(String query) {
    return search(query, new HashMap<String, String>());
  }

  public JSON search(String query, Map<String, String> options) {
    return freebase.search(query, options);
  }

  public LatLon geoSearchGetLatLon(String location) {
    return geoSearchGetLatLon(location, new HashMap<String, String>());
  }

  public LatLon geoSearchGetLatLon(String location, Map<String, String> options) {
    try {
      JSON results = freebase.geosearch(location, new HashMap<String, String>());
      JSON coordinates = results.get("result").get("features").get(0).get("geometry").get("coordinates");
      return new LatLon(Double.parseDouble("" + coordinates.get(0)), Double.parseDouble("" + coordinates.get(1)));
    } catch (Exception ignore) {
      return null;
    }
  }

  public JSON geoSearch(String location) {
    return geoSearch(location, new HashMap<String, String>());
  }

  public JSON geoSearch(String location, Map<String, String> options) {
    return freebase.geosearch(location, options);
  }

  private Freebase freebase;
}

class LatLon {
  public double lat;
  public double lon;

  public LatLon(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public String toString() {
    return "<Lat: " + lat + ", Lon: " + lon + ">";
  }
}