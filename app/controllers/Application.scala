package controllers

import common.models.BrowserSession
import common.models.BrowserSession._
import play.api._
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import play.twirl.api.Html
import scalaz._, Scalaz._
//import argonaut._, Argonaut._

import scalaviews.ScalaView._

import tools.Logging._

import common.events._
import upickle._
import common.events.BrowserEvents._



object Application extends Controller {


  var browserSession:BrowserSession = BrowserSession.initial


  def index = Action {
    Ok(Html(main.toString))
  }

  def getInitialScene= Action{
    implicit request => {
      val res = write(browserSession)
      Ok(res)
    }
  }

  def browserEvent = Action {
    implicit request => {
      request.body.asJson.fold(Ok("a")){json =>
        read[BrowserEvent](json.toString).logDebug(" parsed " ++ _.toString) match {
          case Initial => Ok(write(browserSession).logDebug("writing browserSession " ++ _.toString))
          case _ =>Ok("b")
        }

      }
    }

  }

}