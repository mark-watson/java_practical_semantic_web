;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
;; Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
;; http://markwatson.com/commerciallicense.txt

(ns nlp_clojure)

(import '(com.knowledgebooks.nlp AutoTagger KeyPhraseExtractionAndSummary ExtractNames)
  '(com.knowledgebooks.nlp.util NameValue ScoredList))

(def auto-tagger (AutoTagger.))
(def name-extractor (ExtractNames.))

;; utility:
(defn to-string [obj] (.toString obj))

(defn get-auto-tags [text] (seq (map to-string (.getTags auto-tagger text))))
(defn get-names [text]
  (let [[names places] (.getProperNames name-extractor text)]
    [(seq (.getStrings names))
     (seq (.getStrings places))]))
(defn get-summary [text]
  (.getSummary (new KeyPhraseExtractionAndSummary text)))

