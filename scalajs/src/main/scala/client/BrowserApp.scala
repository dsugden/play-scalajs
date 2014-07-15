package client

import client.ClientScalaViews.console
import common.events._
import common.models.{SpreadSheet, BrowserSession}

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryXHR, jQuery}
import scala.scalajs.js.{ThisFunction0, ThisFunction1}
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success, Try}
import scalatags.JsDom
import scalatags.JsDom._
import all._
import Logging._
import rx._
import Framework._
import upickle._
import Implicits._
//import Pickle._


object BrowserApp extends js.JSApp {


  // for logging
  implicit val console = g.console

  var browserSession = BrowserSession.initial


  //  Reactive Var for current page
  val selectedPage = Var(browserSession.selected)

  // Reactive Vars for SpreadSheet example
  case class SpreadSheetVal(value:Var[Int],error:Var[Option[String]])
  val spreadsheetA = SpreadSheetVal(Var(0),Var(None))
  val spreadsheetB = SpreadSheetVal(Var(0),Var(None))

  val spreadsheetAErrorCls = Rx{ cls := spreadsheetA.error().fold("form-group")(_ => "form-group has-error")}
  val spreadsheetBErrorCls = Rx{ cls := spreadsheetB.error().fold("form-group")(_ => "form-group has-error")}

  val pageOneTotal = Rx{ spreadsheetA.value() + spreadsheetB.value() }

  // This was missing from scalatags (?!)
  val onkeyup = "onkeyup".attr


  type DefTag = JsDom.TypedTag[HTMLElement]


  /**
   * App starts here
   * Ask the server for BrowserSession
   */
  def main(): Unit = {
    emitBrowserEvent(Initial,() => dom.document.body.appendChild( renderBrowserSession )  )
  }


  def updatedBrowserSession(thenDo:() => Unit):( js.Any, js.String, JQueryXHR) => {} =
    (data, textStatus, jqXHR) => {
      console.log("updatedBrowserSession data: " + data.toString)
      browserSession = read[BrowserSession](data.toString)
      browserSession.logDebug("******  " ++ _.toString)
      spreadsheetA.value() = browserSession.pages._1.value
      thenDo()
      Unit
    }

  def emitBrowserEvent(event:BrowserEvent,thenDo:() => Unit) = {
    jQuery.ajax(js.Dynamic.literal(
      url = "/browserEvent",
      data = write[BrowserEvent](event),
      contentType = "application/json",
      success = { updatedBrowserSession(thenDo) },
      error = { (jqXHR: JQueryXHR, textStatus: js.String, errorThrow: js.String) =>
        console.log(s"jqXHR=$jqXHR,text=$textStatus,err=$errorThrow")
      },
      `type` = "POST"
    ).asInstanceOf[JQueryAjaxSettings])
  }




  def changeSpreadSheetVal( spVal:SpreadSheetVal, id:String ) =  () => {
    dom.document.getElementById(id) match {
      case in: HTMLInputElement => if(in.value.length == 0 ){
        spVal.error() = None
        spVal.value() = 0
      }else{
        Try(in.value.toInt).toOption.fold[Unit]( {
          spVal.error() = Some("Not a number")
          spVal.value() = 0
        }){i =>
          spVal.error() = None
          spVal.value() = i
        }
      }
      case _ => {}
    }
  }





  def renderBrowserSession = {
    "renderBrowserSession ".logDebug(_.toString)
    div(cls := "container")(
      div(cls := "navbar navbar-default")(
        div(cls := "container-fluid")(
          div(cls := "navbar-header")(
            a(cls := "navbar-brand", href := "#")("Play with Scala.js")
          ),
          div(cls := "navbar-collapse collapse")(
           // Rendering of <ul><li>..</ul> will be kicked when selectedPage is mutated
            Rx {
              val lis:Seq[scalatags.JsDom.Modifier] = browserSession.pageNames.map{ p =>
                li(cls := Rx{ if(selectedPage().contains(p._2)) "active" else "" })(a(href := "#",all.onclick := { () => {
                  selectedPage() = Some(p._2)  // this mutation is observed
                }})(p._2))
            }.toSeq
            ul(cls:="nav navbar-nav")( lis : _* )
          }
          )
        )
      ),
      div(all.id :="content"),
      Rx {
        div(
          selectedPage().fold[DefTag](span()) { pageName =>
            div(
              h2(pageName),
              browserSession.getPage(pageName).fold[DefTag](span) {
                case (SpreadSheet(name, v)) => {
                  div(
                    div( cls := Rx{spreadsheetA.error().fold("form-group")(_ => "form-group has-error")})(
                      label(cls:="control-label")(Rx{spreadsheetA.error().fold("")(identity)}),
                      input(tpe := "text",
                            cls := "form-control",
                             width:= "100",
                             all.id := "spreadsheetA",
                             value := browserSession.pages._1.value, // initial value
                             onkeyup := changeSpreadSheetVal(spreadsheetA,"spreadsheetA"))
                    ),
                    span("+"),
                    div(cls := Rx{spreadsheetB.error().fold("form-group")(_ => "form-group has-error")})(
                      label(cls:="control-label")(Rx{spreadsheetB.error().fold("")(identity)}),
                      input(tpe := "text",
                            cls := "form-control",
                            width:= "100",
                            all.id := "spreadsheetB",
                            onkeyup := changeSpreadSheetVal(spreadsheetB,"spreadsheetB"))
                    ),
                    span("="),
                    Rx{
                      div(cls:="form-group" )(input(tpe := "text", cls := "form-control",width:= "100",value := pageOneTotal()))
                    }
                  )
                }
                case _ => span("XXX")
              }
            )
          }
        )
      },
      div("This Div doesn't depend on the session, no Rx{ }, never gets re-rendered")
    ).render
  }






}
