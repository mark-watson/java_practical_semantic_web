# Copyright Mark Watson 2008-2010. All Rights Reserved.
# License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
# Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
# http://markwatson.com/commerciallicense.txt

require 'java'
(Dir.glob("lib/*.jar") + Dir.glob("lib/sesame-2.2.4/*.jar")).each do |fname|
  require fname
end
require "knowledgebooks.jar"

class NlpRuby
  def initialize
    @auto_tagger = com.knowledgebooks.nlp.AutoTagger.new
    @extract_names = com.knowledgebooks.nlp.ExtractNames.new
  end
  def get_tags text
    @auto_tagger.getTags(text).collect do |name_value|
      [name_value.getName, name_value.getValue]
    end
  end
  def get_proper_names text
    @extract_names.getProperNames(text).collect do |scored_list|
      scored_list.getStrings.zip(scored_list.getScores)
    end
  end
end