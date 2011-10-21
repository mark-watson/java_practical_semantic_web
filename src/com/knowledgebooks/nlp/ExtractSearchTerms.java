package com.knowledgebooks.nlp;

import com.knowledgebooks.nlp.util.NameValue;
import com.knowledgebooks.nlp.util.NoiseWords;
import com.knowledgebooks.nlp.util.Tokenizer;
import com.knowledgebooks.public_domain.Stemmer;

import java.util.*;

/**
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

/**
 * The purpose of this class is to determine a small set of search terms that are likely to
 * yield the same page using a search engine. Lots of overlapping code with AutoTagger but I
 * decided to keep this class separate because I need this functionality in a few cases
 * where I don't care about tagging text.
 */
public class ExtractSearchTerms {

  public ExtractSearchTerms(String text) {
    // this code is not so efficient since I first need to get the
    // best tags for the input text, then go back and keep track of
    // which words provide the most evidence for selecting these tags.
    List<NameValue<String, Float>> tagResults = new AutoTagger().getTags(text);
    Map<String, Float> tagRelevance = new HashMap<String, Float>();
    for (NameValue<String, Float> nv : tagResults) {
      tagRelevance.put(nv.getName(), nv.getValue());
    }
    List<String> words = Tokenizer.wordsToList(text);
    int number_of_words = words.size();
    Stemmer stemmer = new Stemmer();
    List<String> stems = new ArrayList<String>(number_of_words);
    for (String word : words) stems.add(stemmer.stemOneWord(word));
    int number_of_tag_types = AutoTagger.tagClassNames.length;
    float[] scores = new float[number_of_words];
    for (int w = 0; w < number_of_words; w++) {
      if (NoiseWords.checkFor(stems.get(w)) == false) {
        for (int i = 0; i < number_of_tag_types; i++) {
          Float f = AutoTagger.hashes.get(i).get(stems.get(w));
          if (f != null) {
            Float tag_relevance_factor = tagRelevance.get(AutoTagger.tagClassNames[i]);
            if (tag_relevance_factor != null) {
              scores[w] += f * tag_relevance_factor;
            }
          }
        }
      }
    }
    float max_score = 0.001f;
    for (int i = 0; i < number_of_words; i++) if (max_score < scores[i]) max_score = scores[i];
    float cutoff = 0.2f * max_score;
    for (int i = 0; i < number_of_words; i++) {
      if (NoiseWords.checkFor(stems.get(i)) == false) {
        if (scores[i] > cutoff)
          if (!bestSearchTerms.contains(words.get(i))) bestSearchTerms.add(words.get(i));
      }
    }
  }

  public List<String> getBest() {
    return bestSearchTerms;
  }

  private List<String> bestSearchTerms = new ArrayList<String>();
}
