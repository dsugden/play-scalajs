package client

import client.ClientScalaViews.console
import common.events._
import common.models.{PageTwo, SpreadSheet, BrowserSession}

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
import scalaz._
import Scalaz._

object BrowserApp extends js.JSApp {


  // for logging
  implicit val console = g.console

  var browserSession = BrowserSession.initial


  /*  Reactive Var for current page -> when this changes, any Rx{} closed over this will be re-eval'd
      and any Obs{} on that wil refire    */
  val selectedPage:Var[Option[String]] = Var(browserSession.selected)

  // Reactive Vars for SpreadSheet example
  case class SpreadSheetVal(value:Var[Int],error:Var[Option[String]])

  // Type for value + maybe error
  val spreadsheetA =  SpreadSheetVal(Var(browserSession.pages._1.value),Var(None))
  val spreadsheetB = SpreadSheetVal(Var(0),Var(None))


  // These are the actual HTMLInputElement "streams": String
  val spreadSheetAInput = Var("")
  val spreadSheetBInput = Var("")

  // First transformation is error checking for Int
  val spreadsheetA_Rx = Rx{ updateSpreadSheetVal( spreadSheetAInput(),spreadsheetA) }
  val spreadsheetB_Rx = Rx{ updateSpreadSheetVal( spreadSheetBInput(),spreadsheetB) }

  // Parrallel DOM transformation to show error
  val spreadsheetAErrorCls = Rx{ cls := spreadsheetA.error().fold("form-group")(_ => "form-group has-error")}
  val spreadsheetBErrorCls = Rx{ cls := spreadsheetB.error().fold("form-group")(_ => "form-group has-error")}


  // declare reactive "relationship"
  val pageOneTotal = Rx{ spreadsheetA.value() + spreadsheetB.value() }





  def updateSpreadSheetVal(input:String, spv: SpreadSheetVal) = {
    Validation.fromTryCatch(input.toInt).fold( error =>{
      if(input == "" ){
        spv.value.update(0)
        spv.error.update(None)
      }else{
        spv.value.update(0)
        spv.error.update(Some("Not a number"))
      }
    }, i => {
      i.logDebug("Updating A " ++ _.toString)
      spv.value.update(i)
      spv.error.update(None)
    })
  }




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


  def getEl(id:String):Option[HTMLInputElement] =  dom.document.getElementById(id) match {
    case in: HTMLInputElement => Some(in)
    case _ => None
  }


  def changeSpreadSheetVal( spVal:SpreadSheetVal, id:String ) =  () => {
    dom.document.getElementById(id) match {
      case in: HTMLInputElement => if(in.value.length == 0 ){
        spVal.error.update(None)
        spVal.value.update(0)
      }else{
        Try(in.value.toInt).toOption.fold[Unit]( {
          spVal.error() = Some("Not a number")
          spVal.value() = 0
        }){i =>
          spVal.error.update(None)
          spVal.value.update(i)
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
          div(cls := "navbar-collapse collapse")
          // Rendering of class will be kicked when selectedPage is mutated
            {
              Rx {
                val lis: Seq[scalatags.JsDom.Modifier] = browserSession.pageNames.map { p =>
                  li(cls := Rx {
                    if (selectedPage().contains(p._2)) "active" else ""
                  })(a(href := "#", all.onclick := { () => {
                    selectedPage.update(Some(p._2)) // this mutation is observed
                  }
                  })(p._2))
                }.toSeq
                ul(cls := "nav navbar-nav")(lis: _*)
              }
            }
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
                   /*
                     This div will have it's class tag changed "reactively"
                    */
                    div( cls := Rx{
                      spreadsheetA.error().cata(_ => "form-group has-error","form-group")
                    })(
                      label(cls:="control-label")(Rx{spreadsheetA.error().fold("")(identity)}),
                      input(tpe := "text",
                            cls := "form-control",
                             width:= "100",
                             all.id := "spreadsheetA",
                             value := browserSession.pages._1.value, // initial value
                             onkeyup := { () => {getEl("spreadsheetA").map(v => spreadSheetAInput() = v.value )}})
                    ),
                    span("+"),
                    div(cls := Rx{spreadsheetB.error().fold("form-group")(_ => "form-group has-error")})(
                      label(cls:="control-label")(Rx{spreadsheetB.error().fold("")(identity)}),
                      input(tpe := "text",
                            cls := "form-control",
                            width:= "100",
                            all.id := "spreadsheetB",
                            onkeyup :=  { () => {getEl("spreadsheetB").map(v => spreadSheetBInput() = v.value )}})
                    ),
                    span("="),
                    Rx{
                      div(cls:="form-group" )(input(tpe := "text", cls := "form-control",width:= "100",value := pageOneTotal()))
                    }
                  )
                }

                case PageTwo(name,value) => renderPageTwoComponent(value)
                case _ => span("XXX")
              }
            )
          }
        )
      },
      div("This Div doesn't depend on the session, no Rx{ }, never gets re-rendered")
    ).render
  }


  def renderPageTwoComponent(initial:Int) = {
    val page2var = Var(initial)
    val page2res =  Rx{ page2var() }
    div(
      div( page2res ),
      div( all.onclick := {() => page2var.update( page2var() + 1 )} )("CLick me to increment Page Two value")
    )

  }






}
