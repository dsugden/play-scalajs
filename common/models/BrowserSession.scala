package common.models


import upickle._
import Implicits._






sealed trait Page{
  val name:String
}
case class SpreadSheet(name:String,value:Int) extends Page
case class PageTwo(name:String) extends Page
case class PageThree(name:String) extends Page



object BrowserSession{


  def initial:BrowserSession =
    BrowserSession((SpreadSheet("Spread Sheet",100),PageTwo("PageTwo"),PageThree("PageThree")),None)


  implicit val spreadSheetPickler = Case2ReadWriter(SpreadSheet.apply, SpreadSheet.unapply)
  implicit val pageTwoPickler = Case1ReadWriter(PageTwo.apply, PageTwo.unapply)
  implicit val pageThreePickler = Case1ReadWriter(PageThree.apply, PageThree.unapply)

  implicit val browserSessionPickler = Case2ReadWriter(BrowserSession.apply, BrowserSession.unapply)

}


case class BrowserSession(pages:(SpreadSheet,PageTwo,PageThree),selected:Option[String]){

  implicit def tupleToList(t:(SpreadSheet,PageTwo,PageThree)):List[Page] = List(t._1,t._2,t._3)

  def pageNames:List[(Boolean,String)] =pages.map(p => (selected == Some(p.name),p.name) )


  def getPage(name:String):Option[Page] = pages.find(_.name == name)


  def selectPage(name:String):BrowserSession = pageNames.map(_._2).find(_ == name).fold(this)(_ => copy(selected = Some(name)))


}



