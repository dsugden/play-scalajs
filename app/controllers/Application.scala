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



object Application extends Controller {


  implicit val udxReads = Json.reads[UpdateSceneX]



  var browserSession:BrowserSession = BrowserSession.initial


  def index = Action {
    Ok(Html(main.toString))
  }

  def getInitialScene= Action{
    implicit request => {
      val res = request.body.logDebug("getInitialScene " ++ _.toString).asJson.fold(Json.toJson("Not a valid request"))(
        _.validate[UpdateSceneX].fold(e => Json.toJson("didn't validate"),
          valid => JsString("s"))
      )
      Ok(res)
    }
  }



  def browserEvent = Action {
    implicit request => {
      val res = request.body.logDebug("browserEvent " ++ _.toString).asJson.fold(Json.toJson("Not a valid request"))(
      _.validate[UpdateSceneX].fold(e => Json.toJson("didn't validate"),
      valid => JsString("s"))
      )
      Ok(res)
    }

  }

}