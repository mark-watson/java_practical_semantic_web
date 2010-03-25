# Copyright Mark Watson 2008-2010. All Rights Reserved.
# License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
# Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
# http://markwatson.com/commerciallicense.txt

require 'java'
(Dir.glob("lib/*.jar") + Dir.glob("lib/sesame-2.2.4/*.jar")).each do |fname|
  require fname
end
require "knowledgebooks.jar"

class RdfRuby
  def initialize
    puts "\nWARNING: call either RdfRuby.allegrograph or RdfRuby.sesame to create a new RdfRuby instance.\n"
  end
  def RdfRuby.allegrograph
    @proxy = com.knowledgebooks.rdf.AllegroGraphServerProxy.new
  end
  def RdfRuby.sesame
    @proxy = com.knowledgebooks.rdf.SesameEmbeddedProxy.new
  end
  def delete_repository name
    @proxy.deleteRepository(name)
  end
  def create_repository name
    @proxy.createRepository(name)
  end
  def register_freetext_predicate predicate_name
    @proxy.registerFreetextPredicate(predicate_name)
  end
  def initialize_geo_location resolution_in_miles
    @proxy.initializeGeoLocation(resolution_in_miles)
  end
  def add_triple subject, predicate, object
    @proxy.addTriple(subject, predicate, object)
  end
  def lat_lon_to_literal lat, lon
    @proxy.latLonToLiteral(lat, lon)
  end
  def query sparql
    @proxy.query(sparql)
  end
  def text_search text
    @proxy.textSearch(text)
  end
  def get_ocations lat, lon, radius
    @proxy.getLocations(lat, lon, radius)
  end
end