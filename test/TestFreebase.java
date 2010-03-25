import com.freebase.json.JSON;
import com.knowledgebooks.info_spiders.FreebaseClient;

public class TestFreebase {
  public static void main(String[] args) {
    try {
      FreebaseClient t = new FreebaseClient();
      JSON r = t.query("[{     \"/people/person/date_of_birth\":null,     \"/people/person/gender\":null,     \"/people/person/place_of_birth\":null,     \"/people/person/profession\":\"biologist\", \"name\":null   }] ");
      System.out.println(r);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

