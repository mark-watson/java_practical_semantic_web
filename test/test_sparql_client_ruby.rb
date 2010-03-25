require 'src/sparql_client_ruby'
require 'pp'

sparql =
"""PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX dbpedia2: <http://dbpedia.org/property/>
PREFIX dbpedia: <http://dbpedia.org/>
SELECT ?name ?person WHERE {
     ?person dbpedia2:birthPlace <http://dbpedia.org/resource/California> .
     ?person foaf:name ?name .
}
LIMIT 10
"""

query_results = SparqlClientRuby.query("http://dbpedia.org/sparql", sparql)

pp query_results
