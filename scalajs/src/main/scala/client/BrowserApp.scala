package client

import common.events._
import common.models.Session

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


  val session = Session(1,"One")

  val x = Var(session);


  def main(): Unit = {

    //Logging

    currentDocument.logDebug("main() currentDocument " ++ _.toString )
    currentDocument.body.appendChild( bootstrapContainer(rxDiv) )
  }


  val rxDiv:TypedTag[HTMLElement] =  div {
    Rx {
      div(onclick := { () => x() = x().copy(x = x().x + 1, y = x().y + " w")})(
        div("Current Scene"),
        div("x = " + x().x),
        div("y = " + x().y),
        Rx{ div("d")  }
      )
    }
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


  def toAny(event:BrowserEvent) = event match {
    case UpdateSceneX(value) => js.Dynamic.literal(v = value)
    case _ => js.Dynamic.literal(v = 0)
  }


  val box:JsDom.TypedTag[HTMLDivElement] = div(
    div(
      inputElem,
      labelElem
      ),
    div(updateXButton)
  )


  def updateLabel(s:String) = labelElem.textContent = s

  def emitBrowserEvent(event:scala.scalajs.js.Any) = {

    val data =  js.JSON.stringify(event).toString
//    console.log("data " + data)
    jQuery.ajax(js.Dynamic.literal(
      url = "/browserEvent",
      data = js.JSON.stringify(event).toString,
      contentType = "application/json",
      success = { (data: js.Any, textStatus: js.String, jqXHR: JQueryXHR) =>
//        console.log(s"data=$data,text=$textStatus,jqXHR=$jqXHR")
        updateLabel(data.toString)
      },
      error = { (jqXHR: JQueryXHR, textStatus: js.String, errorThrow: js.String) =>
//        console.log(s"jqXHR=$jqXHR,text=$textStatus,err=$errorThrow")
      },
      `type` = "POST"
    ).asInstanceOf[JQueryAjaxSettings])

  }



  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x
}
