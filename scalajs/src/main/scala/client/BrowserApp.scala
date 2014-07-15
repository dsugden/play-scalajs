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


object BrowserApp extends js.JSApp {

  var currentDocument =  dom.document
  implicit val console = g.console
  val browserSession = BrowserSession.initial

  val selectedPage = Var(browserSession.selected)


  val pageOneVal2 = Var(0)
  val pageOneVal2Error = Var("")
  val pageOneVal3 = Var(0)
  val pageOneVal3Error = Var("")

  val pageOneTotal = Rx{ pageOneVal2() + pageOneVal3() }





  val onkeyup = "onkeyup".attr





  type DefTag = JsDom.TypedTag[HTMLElement]


//  fromTryCatch(in.value.toInt).fold(error => pageOneVal2Error() = "Not a number",pageOneVal2() = _ )


  val changePage2 =  () => {
    currentDocument.getElementById("pageOneVal2") match {
      case in: HTMLInputElement =>
        Try(in.value.toInt) match {
          case Success(i) => pageOneVal2() = i
          case Failure(t) => {
            pageOneVal2() = 0
            pageOneVal2Error() = "Not a number"
          }
        }
      case _ => {}
    }
  }

  val changePage3 =  () => {
    pageOneVal3() = {
      currentDocument.getElementById("pageOneVal3") match {
        case in: HTMLInputElement => in.value.toInt
      }
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
          div(cls := "navbar-collapse collapse")( Rx {
            val lis:Seq[scalatags.JsDom.Modifier] = browserSession.pageNames.map{p =>
              if(selectedPage().fold(false)(sp => p._2 == sp))
                li(cls := "active")(a(href := "#")(p._2))
              else
                li()(a(href := "#", all.onclick := { () => {
                  selectedPage().logDebug(_.toString)
                  selectedPage() = Some(p._2)
                }})(p._2))
            }.toSeq
            ul(cls:="nav navbar-nav")( lis : _* )
          } )
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
                    span(input(tpe := "text", width:= "100", all.id := "pageOneVal2",onkeyup := changePage2)),
                    span(" + "),
                    span(input(tpe := "text", all.id := "pageOneVal3",onkeyup := changePage3)),
                    span(" = "),
                    Rx{
                      span(input(tpe := "text", all.id := "pageOneTotal", value := pageOneTotal()))
                    }
                  )
                }
                case _ => span("XXX")
              }
            )
          }
        )
      },
      div("this doesn't depend on the session, no Rx{ }")
    ).render
  }

  @JSExport
  def updatedBrowserSession:( js.Any, js.String, JQueryXHR) => {} =
    (data, textStatus, jqXHR) => {
    console.log("updatedBrowserSession data: " + data.toString)
  }

  @JSExport
  def emitBrowserEvent(event:BrowserEvent) = {
    jQuery.ajax(js.Dynamic.literal(
      url = "/browserEvent",
      data = write(event),
      contentType = "application/json",
      success = { updatedBrowserSession },
      error = { (jqXHR: JQueryXHR, textStatus: js.String, errorThrow: js.String) =>
         console.log(s"jqXHR=$jqXHR,text=$textStatus,err=$errorThrow")
      },
      `type` = "POST"
    ).asInstanceOf[JQueryAjaxSettings])

  }



  def main(): Unit = {
    currentDocument.logDebug("main() currentDocument " ++ _.toString )
    currentDocument.body.appendChild( renderBrowserSession )
    jQuery.apply("#content").logDebug("**** " ++ _.toString).append(div("sd").render)
    emitBrowserEvent(Initial)
  }



}
