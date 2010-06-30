require 'src/rdf_ruby'
require 'pp'

#rdf = RdfRuby.sesame
rdf = RdfRuby.allegrograph
rdf.delete_repository("rtest_repo")
rdf.create_repository("rtest_repo")
rdf.register_freetext_predicate("http://example.org/ontology/name")
rdf.initialize_geo_location(5.0)
rdf.add_triple("<http://kbsportal.com/oak_creek_flooding>", "<http://knowledgebooks.com/ontology/#storyType>", "<http://knowledgebooks.com/ontology/#disaster>")
rdf.add_triple("http://example.org/people/alice", "http://example.org/ontology/name", "Alice")
rdf.add_triple("http://example.org/people/alice", com.knowledgebooks.rdf.Triple.RDF_LOCATION, rdf.latLonToLiteral(+37.783333,-122.433334))
results = rdf.query("SELECT ?subject ?object WHERE { ?subject <http://knowledgebooks.com/ontology/#storyType> ?object . }")
pp results
results = rdf.text_search("alice")
pp results
results = rdf.get_locations(+37.113333,-122.113334, 500)
pp results

