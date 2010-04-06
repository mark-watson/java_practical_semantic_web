import com.knowledgebooks.info_spiders.WebSpider;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Jan 30, 2010
 * Time: 10:54:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestWebSpider {
  static public void main(String[] args) throws Exception {
    WebSpider ws = new WebSpider("http://www.knowledgebooks.com", 3);
    for (List<String> ls : ws.url_content_lists) {
      String url = ls.get(0);
      String text = ls.get(1);
      System.out.println("\n\n\n----URL:\n" + url + "\n    content:\n" + text);
    }
  }
}
