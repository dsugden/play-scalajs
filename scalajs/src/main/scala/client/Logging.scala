package client


import scala.scalajs.js


case class Logging[V](v : V)(implicit console:js.Dynamic ) {
//  def logInfo(f: V => String) : V = {g.console("INFO "+f(v)); v}
  def logDebug(f: V => String) : V = {console.log("DEBUG "+f(v)); v}
//  def logError(f: V => String) : V = {g.console("ERROR "+f(v))  ; v}
}

object Logging {
  def withValue[V](v : V)(f: V => Unit) = {f(v); v}
  implicit def toLogging[V](v: V)(implicit console:js.Dynamic ) : Logging[V] = Logging(v)

//  def time[A](f: => A) : (A, Long) = {val s = System.currentTimeMillis; val v : A = f; (v, System.currentTimeMillis - s)}
//  def logTime[A](show: Long => String)(f: => A)(implicit g:js.Dynamic ) : A = time(f).logInfo{case (_, t) => show(t)}._1
}