package client

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, HTMLDivElement, alert, document}
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryXHR, jQuery}
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom
import scalatags.JsDom._
import all._
//import tags._

object ClientScalaViews {


  def bootstrapContainer(node:TypedTag[HTMLElement]) =
    div(cls := "container")(
      div(cls := "navbar navbar-default")(
        div(cls := "container-fluid")(
          div(cls := "navbar-header")(
            a(cls:="navbar-brand", href:="#")("Project Name")
          ),
          div(cls := "navbar-collapse collapse")(
            ul(cls:="nav navbar-nav")(
              li(cls:="active")( a(href:="#")("Link")  ),
              li()( a(href:="#")("Link")  ),
              li()( a(href:="#")("Link")  )
            )
          )
        )
      ),
      div(cls:="jumbotron")(
        h1("Navbar example"),
      node
      )
    ).render

}
