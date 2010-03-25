package nlp_scala

import com.knowledgebooks.nlp.{AutoTagger, KeyPhraseExtractionAndSummary, ExtractNames}
import com.knowledgebooks.nlp.util.{NameValue, ScoredList}

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 *    http://markwatson.com/commerciallicense.txt
 */


class NlpScala {
  val auto_tagger = new AutoTagger
  val name_extractor = new ExtractNames

  def get_auto_tags(s: String) = {
    // return a Scala List containing instances of java.lang.String:
    auto_tagger.getTagsAsStrings(s).toArray.toList
  }

  def get_names(s: String) = {
    val result: java.util.List[java.util.List[String]] = name_extractor.getProperNamesAsStrings(s)
    // return a List 2 elements: first is a list of human name strings, second a list of place name strings:
    List((result.get(0).toArray.toList, result.get(1).toArray.toList))
  }
}
