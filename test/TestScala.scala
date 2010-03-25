import scala_wrappers.RdfWrapper

object TestScala {
  def main(args: Array[String]) {
    var ag = new RdfWrapper
    ag.delete_repository("scalatest2")
    ag.create_repository("scalatest2")
    ag.register_free_text_predicate("http://example.org/ontology/name")
    ag.initialize_geolocation(3)
    ag.add_triple("http://example.org/people/alice", com.knowledgebooks.rdf.Triple.RDF_TYPE, "http://example.org/people/alice")
    ag.add_triple("http://example.org/people/alice", "http://example.org/ontology/name", "Alice")

    // does not work with Sesame back end yet:
    ag.add_triple("http://example.org/people/alice", com.knowledgebooks.rdf.Triple.RDF_LOCATION, ag.lat_lon_to_literal(+37.783333, -122.433334))

    var results = ag.query("SELECT ?s ?p ?o  WHERE {?s ?p ?o .}")
    for (result <- results) println("All tuple result using class: " + result)
    //results = ag.query("SELECT ?s ?p ?o WHERE { ?s ?p ?o . ?s fti:match 'Ali*' . }")
    var results2 = ag.text_search("Alice");
    for (result <- results2) println("Partial text match: " + result)
    var results3 = ag.get_locations(+37.513333, -122.313334, 500)
    for (result <- results3) println("Geolocation search: " + result)
  }
}