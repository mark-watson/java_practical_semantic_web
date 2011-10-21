;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)

(ns sparql-client-clojure
  (:import (com.knowledgebooks.rdf SparqlClient)))

(defn sparql-query [endpoint-uri query]
  (seq (map #(into {} %) (.variableBindings (new SparqlClient endpoint-uri query)))))
