package common.events

import common.models.Session

sealed trait BrowserEvent
case class UpdateSceneX(v:Int) extends BrowserEvent
case class UpdateSceneY(v:Int) extends BrowserEvent



object BrowserEvents {


  def updateX(v:Int): Session => Session  =_.copy(x = v)


}
