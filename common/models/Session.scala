package common.models



sealed trait Page
case class PageOne() extends Page
case class PageTwo() extends Page
case class PageThree() extends Page


case class Pages( map:Map[String,Page],selected:Option[String]){
  def all:Set[(Boolean,(String,Page))] = {
    val total = map.keys.toSet
    total.flatMap(t => map.get(t).map((t,_)).map(e => selected.fold((false,e))(sel => if(sel == e._1)(true,e) else (false,e))))
  }

  def selectPage(name:String):Pages = map.get(name).fold(this)(_ => copy(selected = Some(name)))

}




case class BrowserSession(pages:Pages)

object BrowserSession{

  def initial:BrowserSession =
    BrowserSession(Pages(Map("Page One" -> PageOne(), "Page Two" -> PageTwo(), "Page Three" -> PageThree()),Some("Page One")))

}



case class Session(x:Int, y:String)

object Session {

}
