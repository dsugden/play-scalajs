package client


import scala.scalajs.js


case class Logging[V](v : V)(implicit console:js.Dynamic ) {
  def logInfo(f: V => String) : V = {console.log("INFO "+f(v)); v}
  def logDebug(f: V => String) : V = {console.log("DEBUG "+f(v)); v}
  def logError(f: V => String) : V = {console.log("ERROR "+f(v))  ; v}
}

object Logging {
  implicit def toLogging[V](v: V)(implicit console:js.Dynamic ) : Logging[V] = Logging(v)
}