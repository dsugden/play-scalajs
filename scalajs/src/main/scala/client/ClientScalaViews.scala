package client

import common.events.{Page, UpdateSceneX, BrowserEvent}
import common.models.{PageOne, BrowserSession}

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, HTMLDivElement, alert, document}
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryXHR, jQuery}
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom
import scalatags.JsDom._
import all._
import rx._
import Framework._


object ClientScalaViews {

  implicit val console = g.console


  def renderBrowserSession(session:Var[BrowserSession]) =
    div(cls := "container")(
      div(cls := "navbar navbar-default")(
        div(cls := "container-fluid")(
          div(cls := "navbar-header")(
            a(cls:="navbar-brand", href:="#")("Play with Scala.js")
          ),
          Rx{div(cls := "navbar-collapse collapse")( renderHeader(session) )}
        )
      ),
      Rx{div(cls:="jumbotron")(renderPage(session))}
    ).render

  def renderHeader(session:Var[BrowserSession]) ={
    val lis:Seq[scalatags.JsDom.Node] = session().pages.all.map{p =>
    if(p._1)
      li(cls := "active")(a(href := "#")(p._2._1))
    else
      li()(a(href := "#", onclick := { () => {
        session() = session().copy(pages = session().pages.selectPage(p._2._1))
      }})(p._2._1))
    }.toSeq
    ul(cls:="nav navbar-nav")( lis : _* )
  }

  def renderPage(session:Var[BrowserSession]) = session().pages.selected.fold[JsDom.TypedTag[HTMLElement]](span()) { pageName =>
    h2(pageName)
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
