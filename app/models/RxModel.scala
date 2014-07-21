package models

import rx._

object RxModel {

  def main(args:Array[String]){

    val a = Var(1)

    val b = Var(2)

    val c = Rx{ a() + b() }

    println(c()) // 3

    a() = 4

    println(c()) // 6



    val list = List(1,2,3)

    val listRx = Var(list)


    val d = Rx{ listRx()  }

    println(d())

    listRx() = list :+ 4

    println(d())

/*

    sealed trait Free[F[_], A]
    case class Pure[F[_],A](v: A) extends Free[F[_],A]
    case class Roll[F[_],A](v: F[Free[F,A]]) extends Free[F[_], A]

    // A | F[A | F[A | ....


    sealed trait Console[+A]
    case class Out[+A](v: (String, A)) extends Console[A]
    case class In[+A](v: String => A) extends Console[A]

    case class Country()
    case class Geo[A](v: Country => A)

    val a : Free[Console, Int] = Out(("hello", In(i => Out(i + " world", i.length))))
    val b: Free[Geo, Int] = ???

    case class Either1[F[_], G[_], A](v: Either[F[A], G[A]])
    val c : Free[Either1[Free[Console], Free[Geo], _], Int]


*/










  }




}
