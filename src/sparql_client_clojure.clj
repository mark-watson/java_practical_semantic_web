;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
;; Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
;; http://markwatson.com/commerciallicense.txt

(ns sparql_client_clojure)

(import '(com.knowledgebooks.rdf SparqlClient))

(defn convert-to-map [vb]
  (into {} vb))

(defn sparql-query [endpoint_uri query]
  (seq (map convert-to-map (.variableBindings (new SparqlClient endpoint_uri query)))))
