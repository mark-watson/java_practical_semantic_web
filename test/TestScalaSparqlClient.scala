import com.knowledgebooks.rdf.SparqlClient

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 *    http://markwatson.com/commerciallicense.txt
 */


object TestScalaSparqlClient {
  def main(args: Array[String]) {
    val sparql =
"""PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX dbpedia2: <http://dbpedia.org/property/>
PREFIX dbpedia: <http://dbpedia.org/>
SELECT ?name ?person WHERE {
     ?person dbpedia2:birthPlace <http://dbpedia.org/resource/California> .
     ?person foaf:name ?name .
}
LIMIT 10
"""
    val results = new SparqlClient("http://dbpedia.org/sparql", sparql)
    println(results.variableBindings)
  }
}