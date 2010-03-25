import com.knowledgebooks.nlp.ExtractSearchTerms;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Mar 2, 2010
 * Time: 11:18:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestExtractSearchTerms {
  public static void main(String[] args) throws Exception {
    String s = "The President went to Congress to argue for his tax bill passed into law before leaving on a vacation to Las Vegas to see some shows and gamble. However, too many Senators are against this spending bill.";
    ExtractSearchTerms extractor = new ExtractSearchTerms(s);
    System.out.println("Best search terms " + extractor.getBest());
  }
}
