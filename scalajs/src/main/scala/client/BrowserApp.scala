package client

import common.events._
import common.models.{BrowserSession, Session}

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, HTMLDivElement, alert, document}
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryXHR, jQuery}
import scala.scalajs.js.{ThisFunction0, ThisFunction1}
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom
import scalatags.JsDom._
import all._
//import tags._
import scalaz._, Scalaz._
import ClientScalaViews._
import Logging._
import rx._
import Framework._


object BrowserApp extends js.JSApp {

  var currentDocument =  dom.document
  implicit val console = g.console
  val browserSession = Var(BrowserSession.initial)

  def main(): Unit = {
    currentDocument.logDebug("main() currentDocument " ++ _.toString )
    currentDocument.body.appendChild( renderBrowserSession(browserSession) )
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }


  val labelElem = label("Default").render

  val inputElem = input(
    tpe:="text",
    onfocus := { () => {
      labelElem.textContent = ""
      emitBrowserEvent(toAny(UpdateSceneX(44)))
    }}
  ).render


  val updateXButton = button(
    onclick := {  () =>  BrowserEvents.updateX(3) }
  )




  val box:JsDom.TypedTag[HTMLDivElement] = div(
    div(
      inputElem,
      labelElem
      ),
    div(updateXButton)
  )


  def updateLabel(s:String) = labelElem.textContent = s




  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x
}
