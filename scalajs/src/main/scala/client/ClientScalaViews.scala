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
import Logging._


object ClientScalaViews {

  implicit val console = g.console




}
