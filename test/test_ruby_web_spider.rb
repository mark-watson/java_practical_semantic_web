require 'java'
(Dir.glob("lib/*.jar")).each do |fname|
  require fname
end
require "knowledgebooks.jar"
require 'pp'

results = com.knowledgebooks.info_spiders.WebSpider.new("http://www.knowledgebooks.com", 2)
pp results.url_content_lists
