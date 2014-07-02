package models

import rx._

object RxModel {

  def main(args:Array[String]){

//    val a = Var(1)
//
//    val b = Var(2)
//
//    val c = Rx{ a() + b() }
//
//    println(c()) // 3
//
//    a() = 4
//    println(c()) // 6



    val list = List(1,2,3)

    val listRx = Var(list)


    val d = Rx{ listRx()  }

    println(d())

    listRx() = list :+ 4

    println(d())


  }




}
