;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
;; Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
;; http://markwatson.com/commerciallicense.txt

(ns sparql-client-clojure
  (:import (com.knowledgebooks.rdf SparqlClient)))

(defn sparql-query [endpoint-uri query]
  (seq (map #(into {} %) (.variableBindings (new SparqlClient endpoint-uri query)))))
