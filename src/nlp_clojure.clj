;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)

(ns nlp-clojure
  (:import (com.knowledgebooks.nlp AutoTagger KeyPhraseExtractionAndSummary ExtractNames)
           (com.knowledgebooks.nlp.util NameValue ScoredList)))

(def auto-tagger (AutoTagger.))
(def name-extractor (ExtractNames.))

(defn get-auto-tags [text]
  (map str (seq (.getTags auto-tagger text))))

(defn get-names [text]
  (let [[names places] (.getProperNames name-extractor text)]
    [(seq (.getStrings names))
     (seq (.getStrings places))]))

(defn get-summary [text]
  (.getSummary (KeyPhraseExtractionAndSummary. text)))

