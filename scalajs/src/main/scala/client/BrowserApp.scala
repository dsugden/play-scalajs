package client

import client.ClientScalaViews.console
import common.events._
import common.models.{PageOne, BrowserSession}

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryXHR, jQuery}
import scala.scalajs.js.{ThisFunction0, ThisFunction1}
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom
import scalatags.JsDom._
import all.{div,cls,id,a,href,li,onclick,ul,span,h2,input,tpe,value,onkeydown}
//import tags._
import scalaz._, Scalaz._
import ClientScalaViews._
import Logging._
import rx._
import Framework._


object BrowserApp extends js.JSApp {

  var currentDocument =  dom.document
  implicit val console = g.console
  val browserSession = BrowserSession.initial

  val selectedPage = Var(browserSession.selected)

  val pageOneRx:Var[PageOne] = Var(browserSession.pages._1)

  val pageOneVal2 = Var("0")
  val pageOneVal3 = Var("0")

  val pageOneRes = Rx{ pageOneVal2() + pageOneVal3()}


  type DefTag = JsDom.TypedTag[HTMLElement]



  def main(): Unit = {
    currentDocument.logDebug("main() currentDocument " ++ _.toString )
    currentDocument.body.appendChild( renderBrowserSession )


    def renderBrowserSession = {
      "renderBrowserSession ".logDebug(_.toString)
      div(cls := "container")(
        div(cls := "navbar navbar-default")(
          div(cls := "container-fluid")(
            div(cls := "navbar-header")(
              a(cls := "navbar-brand", href := "#")("Play with Scala.js")
            ),
            div(cls := "navbar-collapse collapse")( Rx { renderHeader } )
          )
        ),
        Rx {
          div(cls := "jumbotron")(renderPage)
        },
        renderNoSessionRx
      ).render
    }

    def renderHeader ={
      "renderHeader ".logDebug(_.toString)
      val lis:Seq[scalatags.JsDom.Node] = browserSession.pageNames.map{p =>
        if(selectedPage().fold(false)(sp => p._2 == sp))
          li(cls := "active")(a(href := "#")(p._2))
        else
          li()(a(href := "#", onclick := { () => {
            selectedPage().logDebug(_.toString)
            selectedPage() = Some(p._2)
          }})(p._2))
      }.toSeq
      ul(cls:="nav navbar-nav")( lis : _* )
    }



    def ke(input:HTMLInputElement)(e:Event) = e match {
      case k: KeyboardEvent =>  pageOneVal2() = input.value
    }


    def renderPage = {
      "renderPage ".logDebug(_.toString)

      div(
        selectedPage().fold[DefTag](span()) { pageName =>
          div(
            h2(pageName),
            browserSession.getPage(pageName).fold[DefTag](span){ page =>
              page match {
                case (PageOne(name,v)) => {
                  div(
                    div(input(tpe:="text", value:= pageOneRx().value)),
                    div( input(tpe:="text", id:="temp", value:= pageOneVal2()), onkeydown:= { () => pageOneVal3() = pageOneVal3() + " t "}),
                    Rx{div( input(tpe:="text", id:="temp", value:= pageOneVal3()))}
                  )
                }
                case _ => span("XXX")
              }
            }
          )
        }

      )

    }

    def renderNoSessionRx = {
      "renderNoSessionRx ".logDebug(_.toString)
      div("this doesn't depend on the session, no Rx{ }")

    }


    def toAny(event:BrowserEvent) = event match {
      case UpdateSceneX(value) => js.Dynamic.literal(v = value)
      case Page(value) => js.Dynamic.literal(name = value)
      case _ => js.Dynamic.literal(v = 0)
    }

    def emitBrowserEvent(event:scala.scalajs.js.Any) = {

      val data =  js.JSON.stringify(event).toString
      //    console.log("data " + data)
      jQuery.ajax(js.Dynamic.literal(
        url = "/browserEvent",
        data = js.JSON.stringify(event).toString,
        contentType = "application/json",
        success = { (data: js.Any, textStatus: js.String, jqXHR: JQueryXHR) =>
          //        console.log(s"data=$data,text=$textStatus,jqXHR=$jqXHR")
          console.log("emitBrowserEvent data: " + data.toString)
          //        var o = jQuery.parseJSON(data.toString)
          //        console.log("emitBrowserEvent data: " + scala.scalajs.js.JSON.parse(data.toString))
          //        updateLabel(data.toString)
        },
        error = { (jqXHR: JQueryXHR, textStatus: js.String, errorThrow: js.String) =>
          //        console.log(s"jqXHR=$jqXHR,text=$textStatus,err=$errorThrow")
        },
        `type` = "POST"
      ).asInstanceOf[JQueryAjaxSettings])

    }







  }



}
