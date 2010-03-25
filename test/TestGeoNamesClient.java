import com.knowledgebooks.info_spiders.GeoNamesClient;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 * http://markwatson.com/commerciallicense.txt
 */
public class TestGeoNamesClient {
  public static void main(String[] args) throws Exception {
    GeoNamesClient test = new GeoNamesClient();
    System.out.println(test.getCityData("Paris"));
    pause();
    System.out.println(test.getCountryData("Canada"));
    pause();
    System.out.println(test.getStateData("California"));
    pause();
    System.out.println(test.getRiverData("Amazon"));
    pause();
    System.out.println(test.getMountainData("Whitney"));
  }

  private static void pause() {
    try {
      Thread.sleep(1000);
    } catch (Exception ignore) {
    }
  }
}
// {CITY, COUNTRY, STATE, RIVER, MOUNTAIN, UNKNOWN}