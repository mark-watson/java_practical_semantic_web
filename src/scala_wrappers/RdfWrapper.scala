/**
 * Copyright Mark Watson 2008-2010. All Rights Reserved.
 * License: LGPL version 3 (http://www.gnu.org/licenses/lgpl-3.0.txt)
 */

package scala_wrappers

import com.knowledgebooks.rdf
import org.openrdf.model.URI
import rdf.{RdfServiceProxy, SesameEmbeddedProxy, Triple, AllegroGraphServerProxy}

class RdfWrapper {
  val proxy: RdfServiceProxy = new AllegroGraphServerProxy()
  //val proxy : RdfServiceProxy = new SesameEmbeddedProxy()

  def listToTriple(sl: List[Object]): List[Triple] = {
    var arr = List[Triple]()
    var (skip, rest) = sl.splitAt(4)
    while (rest.length > 2) {
      val (x, y) = rest.splitAt(3)
      //arr += new Triple(x(0), x(1), x(2))
      arr = arr ::: List(new Triple(x(0), x(1), x(2)))
      rest = y
    }
    arr
  }

  def listToMulLists(sl: List[Object]): List[List[Object]] = {
    var arr = List[List[Object]]()
    var (num, rest) = sl.splitAt(1)
    val size = Integer.parseInt("" + num(0))
    var (variables, rest2) = rest.splitAt(size)
    while (rest2.length >= size) {
      val (x, y) = rest2.splitAt(size)
      arr = arr ::: List(x)
      //arr += x
      rest2 = y
    }
    arr
  }

  def query(q: String): List[List[Object]] = {
    listToMulLists(proxy.query_scala(q).toArray.toList)
    //listToTriple(proxy.query_scala(q).toArray.toList)
  }

  def get_locations(lat: Double, lon: Double, radius_in_km: Double): List[Triple] = {
    listToTriple(proxy.getLocations_scala(lat, lon, radius_in_km).toArray.toList.toArray.toList)
  }

  def delete_repository(name: String) = {proxy.deleteRepository(name)}

  def create_repository(name: String) = {proxy.createRepository(name)}

  def register_free_text_predicate(predicate_name: String) =
    {proxy.registerFreetextPredicate(predicate_name)}

  def initialize_geolocation(strip_width: Double) = {proxy.initializeGeoLocation(strip_width)}

  def add_triple(subject: String, predicate: String, obj: String) =
    {proxy.addTriple(subject, predicate, obj)}

  def add_triple(subject: String, predicate: String, obj: org.openrdf.model.Literal) =
    {proxy.addTriple(subject, predicate, obj)}

  def add_triple(subject: String, predicate: URI, obj: org.openrdf.model.Literal) =
    {proxy.addTriple(subject, predicate, obj)}

  def add_triple(subject: String, predicate: URI, obj: String) =
    {proxy.addTriple(subject, predicate, obj)}

  def lat_lon_to_literal(lat: Double, lon: Double) = {
    proxy.latLonToLiteral(lat, lon)
  }

  def text_search(query: String) = {
    listToTriple(proxy.textSearch_scala(query).toArray.toList)
  }
}
