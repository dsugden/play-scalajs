package common.events

import common.models.BrowserSession

import upickle._
import Implicits._

sealed trait BrowserEvent
case object Initial extends BrowserEvent
case class UpdateScene(v:Int) extends  BrowserEvent



object BrowserEvents {

   val x = knotRW{implicit i: RWKnot[BrowserEvent] => sealedRW(
    Case0ReadWriter(Initial),
    Case1ReadWriter(UpdateScene.apply,UpdateScene.unapply),
    i
  )}

  implicit val (browserEventPicker,initialPickler, updateScenePickler) = x


}
