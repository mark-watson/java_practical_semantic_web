import com.knowledgebooks.info_spiders.OpenCalaisClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 29, 2010
 * Time: 4:33:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestOpenCalaisClient {
  /**
   * @param args
   * @throws IOException
   * @throws MalformedURLException
   */
  public static void main(String[] args) throws MalformedURLException, IOException {
    String content = "Hillary Clinton likes to remind Texans that she first came here to ask for their votes in 1972 as a young Democratic campaign worker. Doug Hattaway, a Clinton adviser who worked on Al Gore's presidential campaign in 2000 was in Austin Texas. Texas is crucial to Clinton's hopes of staying in the U.S. presidential race. Clinton traveled to France, Spain, and San Francisco. Both political parties blaim the other for the poor economy.";
    Map<String, List<String>> results = new OpenCalaisClient().getPropertyNamesAndValues(content);
    //System.out.println("\n\nresults:\n\n" + results);
    for (String key : results.keySet()) {
      System.out.println("  " + key + ": " + results.get(key));
    }
  }
}
