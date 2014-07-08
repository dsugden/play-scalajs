package common.events

import common.models.BrowserSession

sealed trait BrowserEvent
case class Page(name:String) extends BrowserEvent
case class UpdateSceneX(v:Int) extends BrowserEvent
case class UpdateSceneY(v:Int) extends BrowserEvent



object BrowserEvents {


}
