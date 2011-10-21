;; Copyright Mark Watson 2008-2010. All Rights Reserved.
;; License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)

(ns rdf-clojure
  (:import (com.knowledgebooks.rdf Triple)
           (com.knowledgebooks.rdf AllegroGraphServerProxy)
           (com.knowledgebooks.rdf SesameEmbeddedProxy)
           ))

;;(defn rdf-proxy [] (AllegroGraphServerProxy.))
(defn rdf-proxy [] (SesameEmbeddedProxy.))

;; TODO: use macros to define bindings from clojure?
(defn delete-repository [ag-proxy name]
  (.deleteRepository ag-proxy name))
(defn create-repository [ag-proxy name]
  (.createRepository ag-proxy name))
(defn register-freetext-predicate [ag-proxy predicate-name]
  (.registerFreetextPredicate ag-proxy predicate-name))
(defn initialize-geoLocation [ag-proxy radius]
  (.initializeGeoLocation ag-proxy (float radius)))
(defn add-triple [ag-proxy s p o]
  (.addTriple ag-proxy s p o))

;; TODO: for returns lazy sequence, is ag-proxy will still available?
(defn query [ag-proxy sparql]
  (for [triple (seq (.query ag-proxy sparql))]
    [(.get triple 0) (.get triple 1) (.get triple 2)]))

(defn text-search [ag-proxy query-string]
  (.textSearch ag-proxy query-string))

(defn get-locations [ag-proxy lat lon radius]
  (.getLocations ag-proxy lat lon radius))
