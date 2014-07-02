package scalaviews

import controllers.routes

import scalatags.Text._
import scalatags.Text.all._

object ScalaView {

  val titleTag = "title".tag

  lazy val main = html(
    head(
      titleTag("Play with ScalaJs, ScalaTags"),
      link(rel := "shortcut", tpe := "icon", href := routes.Assets.at("images/favicon.png").toString),
      link(rel := "stylesheet", href := routes.Assets.at("bootstrap/css/bootstrap.min.css").toString),
      script(src:=routes.Assets.at("lib/jquery/jquery.min.js").toString, tpe:="text/javascript")
    ),
    body(
  //      for production (play dist), use the optimized version instead
//     <script src="@routes.Assets.at("javascripts/browserapp-opt.js")" type="text/javascript"></script>
      script(src:=routes.Assets.at("javascripts/browserapp-fastopt.js").toString, tpe:="text/javascript"),
      script(src:=routes.Assets.at("javascripts/browserapp-launcher.js").toString, tpe:="text/javascript")
    )
  )

}
