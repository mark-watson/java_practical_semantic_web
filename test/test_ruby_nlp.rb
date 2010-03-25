require 'src/nlp_ruby'
require 'pp'

nlp = NlpRuby.new
tags = nlp.get_tags("The President went to Congress to argue for his tax bill before leaving on a vacation to Las Vegas to see some shows and gamble.")
pp tags

names = nlp.get_proper_names("John Smith went to France and Germany with Sam Jones.")
pp names
