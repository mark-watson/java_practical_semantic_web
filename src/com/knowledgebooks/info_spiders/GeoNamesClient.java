package com.knowledgebooks.info_spiders;

import com.knowledgebooks.nlp.util.GeoNameData;
import org.geonames.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */
public class GeoNamesClient {
  public GeoNamesClient() {
  }

  private List<GeoNameData> helper(String name, String type) throws Exception {
    List<GeoNameData> ret = new ArrayList<GeoNameData>();
    ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
    searchCriteria.setStyle(Style.LONG);
    searchCriteria.setQ(name);
    ToponymSearchResult searchResult = WebService.search(searchCriteria);
    for (Toponym toponym : searchResult.getToponyms()) {
      //System.out.println("* " + toponym.getName() + " : " +toponym.getFeatureClassName());
      if (toponym.getFeatureClassName() != null &&
        toponym.getFeatureClassName().toString().indexOf(type) > -1 &&
        toponym.getName().indexOf(name) > -1 &&
        valid(toponym.getName())) {
        ret.add(new GeoNameData(toponym));
      }
    }
    return ret;
  }

  private boolean valid(String str) {
    if (str.indexOf("0") > -1) return false;
    if (str.indexOf("1") > -1) return false;
    if (str.indexOf("2") > -1) return false;
    if (str.indexOf("3") > -1) return false;
    if (str.indexOf("4") > -1) return false;
    if (str.indexOf("5") > -1) return false;
    if (str.indexOf("6") > -1) return false;
    if (str.indexOf("7") > -1) return false;
    if (str.indexOf("8") > -1) return false;
    if (str.indexOf("9") > -1) return false;
    return true;
  }

  public List<GeoNameData> getCityData(String city_name) throws Exception {
    return helper(city_name, "city");
  }

  public List<GeoNameData> getCountryData(String country_name) throws Exception {
    return helper(country_name, "country");
  }

  public List<GeoNameData> getStateData(String state_name) throws Exception {
    List<GeoNameData> states = helper(state_name, "state");
    for (GeoNameData state : states) {
      state.geoType = GeoNameData.GeoType.STATE;
    }
    return states;
  }

  public List<GeoNameData> getRiverData(String river_name) throws Exception {
    return helper(river_name, "stream");
  }

  public List<GeoNameData> getMountainData(String mountain_name) throws Exception {
    return helper(mountain_name, "mountain");
  }
}
