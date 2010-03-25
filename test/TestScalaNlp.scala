import nlp_scala.NlpScala

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 *     http://markwatson.com/commerciallicense.txt
 */


object TestScalaNlp {
  def main(args: Array[String]) {
    var test = new NlpScala
    val results = test.get_auto_tags("President Obama went to Congress to talk about taxes")
    println(results)
    val names = test.get_names("Bob Jones is in Canada and is then meeting John Smith in London")
    println(names)
  }
}