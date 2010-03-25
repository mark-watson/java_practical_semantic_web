(use 'sparql_client_clojure)

(def sparql
  "PREFIX foaf: <http://xmlns.com/foaf/0.1/>
  PREFIX dbpedia2: <http://dbpedia.org/property/>
  PREFIX dbpedia: <http://dbpedia.org/>
  SELECT ?name ?person WHERE {
       ?person dbpedia2:birthPlace <http://dbpedia.org/resource/California> .
       ?person foaf:name ?name .
  }
  LIMIT 10
  ")

(def results (sparql-query "http://dbpedia.org/sparql" sparql))

(println results)

(println (first results))

(println (class (first (first results))))
(println (class (first results)))
(println ((first results) "name"))
(println ((first results) "person"))

(doseq [result results]
  (println (str "Result:\n  person URI: " (result "person")
    "\n  person name: " (result "name") ".")))
