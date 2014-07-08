package common.models






sealed trait Page{
  val name:String
}
case class PageOne(name:String,value:Int) extends Page
case class PageTwo(name:String) extends Page
case class PageThree(name:String) extends Page



object BrowserSession{


  def initial:BrowserSession =
    BrowserSession((PageOne("PageOne",100),PageTwo("PageTwo"),PageThree("PageThree")),None)

}


case class BrowserSession(pages:(PageOne,PageTwo,PageThree),selected:Option[String]){

  implicit def tupleToList(t:(PageOne,PageTwo,PageThree)):List[Page] = List(t._1,t._2,t._3)

  def pageNames:List[(Boolean,String)] =pages.map(p => (selected == Some(p.name),p.name) )


  def getPage(name:String):Option[Page] = pages.find(_.name == name)


  def selectPage(name:String):BrowserSession = pageNames.map(_._2).find(_ == name).fold(this)(_ => copy(selected = Some(name)))


}



