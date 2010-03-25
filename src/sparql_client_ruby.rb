# Copyright Mark Watson 2008-2010. All Rights Reserved.
# License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
# Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
# http://markwatson.com/commerciallicense.txt

require 'java'
(Dir.glob("lib/*.jar") + Dir.glob("lib/sesame-2.2.4/*.jar")).each do |fname|
  require fname
end
require "knowledgebooks.jar"

class SparqlClientRuby
  def self.query endpoint_UL, sparql
    proxy = com.knowledgebooks.rdf.SparqlClient.new(endpoint_UL, sparql)
    proxy.variableBindings().collect do |var_binding|
      x = {}
      var_binding.key_set.each {|var| x[var] = var_binding[var]}
      x
    end
  end
end