;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
;; Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
;; http://markwatson.com/commerciallicense.txt

(ns rdf_clojure)

(import '(com.knowledgebooks.rdf Triple) '(com.knowledgebooks.rdf AllegroGraphServerProxy) '(com.knowledgebooks.rdf SesameEmbeddedProxy))
;;(defn rdf-proxy [] (AllegroGraphServerProxy.))
(defn rdf-proxy [] (SesameEmbeddedProxy.))

(defn delete-repository [ag-proxy name] (.deleteRepository ag-proxy name))
(defn create-repository [ag-proxy name] (.createRepository ag-proxy name))
(defn register-freetext-predicate [ag-proxy predicate-name] (.registerFreetextPredicate ag-proxy predicate-name))
(defn initialize-geoLocation [ag-proxy radius] (.initializeGeoLocation ag-proxy (float radius)))
(defn add-triple [ag-proxy s p o] (.addTriple ag-proxy s p o))
(defn query [ag-proxy sparql]
  (for [triple (seq (.query ag-proxy sparql))]
    [(.get triple 0) (.get triple 1) (.get triple 2)]))
(defn text-search [ag-proxy query-string] (.textSearch ag-proxy query-string))
(defn get-locations [ag-proxy lat lon radius]
  (.getLocations ag-proxy lat lon radius))
