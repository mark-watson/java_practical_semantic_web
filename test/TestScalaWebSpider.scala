import com.knowledgebooks.info_spiders.WebSpider

/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: AGPL version 3 (http://www.gnu.org/licenses/agpl-3.0.txt)
 * Alternative commercial license used under special arrangement (contact markw <at> markwatson <dot> com):
 *     http://markwatson.com/commerciallicense.txt
 */


object TestScalaWebSpider {
  def main(args: Array[String]) {
    val results = new WebSpider("http://www.knowledgebooks.com", 2)
    println(results.url_content_lists.get(0))
    println(results.url_content_lists.get(1))
  }
}