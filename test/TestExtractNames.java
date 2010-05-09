import com.knowledgebooks.nlp.ExtractNames;
import com.knowledgebooks.nlp.KeyPhraseExtractionAndSummary;
import com.knowledgebooks.nlp.util.ScoredList;

/**
 * Created by IntelliJ IDEA.
 * User: markw
 * Date: Feb 7, 2010
 * Time: 3:11:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestExtractNames {
  /**
   * @param args
   */
  static public void main(String[] args) {
    ExtractNames extractNames = new ExtractNames();
    // initialize everything, before printing any output - trying to see what is taking so long!
    if (args.length > 0) {
      ScoredList[] ret = extractNames.getProperNames(args[0]);
      System.out.println("Human names: " + ret[0].getValuesAsString());
      System.out.println("Place names: " + ret[1].getValuesAsString());
    } else {
      extractNames.isPlaceName("Paris");
      extractNames.isHumanName("President Bush");
      extractNames.isHumanName("President George Bush");
      extractNames.isHumanName("President George W. Bush");
      System.out.println("Initialization complete....");
      System.out.println("Paris: " + extractNames.isPlaceName("Paris"));
      System.out.println("Mexico: " + extractNames.isPlaceName("Mexico"));
      System.out.println("Fresno: " + extractNames.isPlaceName("Fresno"));
      System.out.println("Moscow: " + extractNames.isPlaceName("Moscow"));
      System.out.println("France: " + extractNames.isPlaceName("France"));
      System.out.println("Los Angeles: " + extractNames.isPlaceName("Los Angeles"));
      System.out.println("President Bush: " + extractNames.isHumanName("President Bush"));
      System.out.println("President George Bush: " + extractNames.isHumanName("President George Bush"));
      System.out.println("President George W. Bush: " + extractNames.isHumanName("President George W. Bush"));
      System.out.println("George W. Bush: " + extractNames.isHumanName("George W. Bush"));
      System.out.println("Senator Barbara Boxer: " + extractNames.isHumanName("Senator Barbara Boxer"));
      System.out.println("King Smith: " + extractNames.isHumanName("King Smith"));
      ScoredList[] ret = extractNames.getProperNames("George Bush played golf. President George W. Bush went to London England, Paris France and Mexico to see Mary Smith in Moscow. President Bush will return home Monday.");
      System.out.println("ret = " + ret);
      System.out.println("Human names: " + ret[0].getValuesAsString());
      System.out.println("Place names: " + ret[1].getValuesAsString());
      System.out.println("\n\n\n");

      // for book example:
      ExtractNames names = new ExtractNames();
      System.out.println("Los Angeles: " +
        names.isPlaceName("Los Angeles"));
      System.out.println("President Bush: " +
        names.isHumanName("President Bush"));
      System.out.println("President George Bush: " +
        names.isHumanName("President George Bush"));
      System.out.println("President George W. Bush: " +
        names.isHumanName("President George W. Bush"));
      ScoredList[] ret1 = names.getProperNames(
        "George Bush played golf. President  George W. Bush went to London England, Paris France and Mexico to see Mary  Smith in Moscow. President Bush will return home Monday.");
      System.out.println("Human names: " +
        ret1[0].getValuesAsString());
      System.out.println("Place names: " +
        ret1[1].getValuesAsString());

      // also text summarization:
      KeyPhraseExtractionAndSummary kp = new KeyPhraseExtractionAndSummary("President Jane Smith spoke to Congress about tax and military appropriations. The subject of the economy was key. Then she left for Mexico.");
      System.out.println("\n\nTesting summary:\n" + kp.getSummary());
    }
  }
}
