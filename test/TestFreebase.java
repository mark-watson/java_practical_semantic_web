import com.freebase.json.JSON;
import com.knowledgebooks.info_spiders.FreebaseClient;

public class TestFreebase {
  public static void main(String[] args) {
    try {
      FreebaseClient t = new FreebaseClient();
      JSON r = t.query("{\"type\":\"/people/person\",\"id\":\"/en/madonna\",\"children\":[]}");
      System.out.println(r);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

