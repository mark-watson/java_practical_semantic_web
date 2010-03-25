(use 'rdf_clojure)
(import '(com.knowledgebooks.rdf Triple))

(def agp (rdf-proxy))
(println agp)
(delete-repository agp "testrepo1")
(create-repository agp "testrepo1")
(register-freetext-predicate agp "http://example.org/ontology/name")
(initialize-geoLocation agp 3)
(add-triple agp "http://example.org/people/alice" Triple/RDF_TYPE "http://example.org/people")
(add-triple agp "http://example.org/people/alice" "http://example.org/ontology/name" "Alice")
(add-triple agp "http://example.org/people/alice" Triple/RDF_LOCATION (.latLonToLiteral agp +37.783333 -122.433334))

(println "All triples:\n" (query agp "select ?s ?p ?o where {?s ?p ?o}"))

(println "\nText match results\n" (text-search agp "Ali*"))
(println "\nGeolocation results:\n" (get-locations agp +37.113333 -122.113334 500.0))

