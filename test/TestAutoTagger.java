import com.knowledgebooks.nlp.AutoTagger;
import com.knowledgebooks.nlp.util.NameValue;

import java.util.List;

public class TestAutoTagger {
  public static void main(String[] args) {
    AutoTagger test = new AutoTagger();
    List<NameValue<String, Float>> results = test.getTags("The President went to Congress to argue for his tax bill before leaving on a vacation to Las Vegas to see some shows and gamble.");
    for (NameValue<String, Float> result : results) {
      System.out.println(result);
    }
  }
}