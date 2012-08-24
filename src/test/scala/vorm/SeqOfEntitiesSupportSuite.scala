package vorm

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import com.codahale.logula.Logging
import org.apache.log4j.Level

import vorm._
import api._
import extensions._

import samples._

@RunWith(classOf[JUnitRunner])
class SeqOfEntitiesSupportSuite extends FunSuite with ShouldMatchers {
  Logging.configure { log =>
    log.level = Level.TRACE
  }

  import SeqOfEntitiesSupportSuite._

  test("Non matching equals query") {
    fetchEqualingIds( Seq(b5) ) should be === Set()
    fetchEqualingIds( Seq(b1, b2, b4) ) should be === Set()
  }
  test("Partially matching equals query") {
    fetchEqualingIds( Seq(b2) ) should be === Set()
    fetchEqualingIds( Seq(b1, b2) ) should be === Set()
    fetchEqualingIds( Seq(b3) ) should be === Set()
    fetchEqualingIds( Seq(b2, b3) ) should be === Set()
  }
  test("Empty seq equals query") {
    fetchEqualingIds( Seq() ) should be === Set(1l, 3l)
  }
  test("Same seq equals query") {
    fetchEqualingIds( Seq(b1, b2, b3) ) should be === Set(2l)
    fetchEqualingIds( Seq(b4) ) should be === Set(4l)
  }
  test("Differently ordered seq") {
    fetchEqualingIds( Seq(b1, b3, b2) ) should be === Set()
    fetchEqualingIds( Seq(b2, b3, b1) ) should be === Set()
  }
  test("Equals on empty seq does not include non empty seqs") {
    db.query[A]
      .filterEquals("a", Seq())
      .fetchAll().map(_.id.toInt).toSet
      .should( not contain (2) and not contain (4) )
  }

  test("Everything matches not equals on inexistent") {
    db.query[A]
      .filterNotEquals("a", Seq(b5))
      .fetchAll().map(_.id.toInt).toSet
      .should( contain(1) and contain(2) and contain(3) and contain(4) )
  }
  test("A partially matching item does not get excluded from results on not equals"){
    db.query[A]
      .filterNotEquals("a", Seq(b1, b3))
      .fetchAll().map(_.id.toInt).toSet
      .should( contain (2) )
  }
  test("Not equals on empty seq does not return empty seqs") {
    db.query[A]
      .filterNotEquals("a", Seq())
      .fetchAll().map(_.id.toInt).toSet
      .should( not contain (1) and not contain (3) )
  }


}
object SeqOfEntitiesSupportSuite {
  case class A ( a : Seq[B] )
  case class B ( a : Int )

  val db = TestingInstance.h2( Entity[A](), Entity[B]() )

  val b1 = db.save(B(23))
  val b2 = db.save(B(0))
  val b3 = db.save(B(0))
  val b4 = db.save(B(12))
  val b5 = db.save(B(12))

  db.save(A( Seq() ))
  db.save(A( Seq(b1, b2, b3) ))
  db.save(A( Seq() ))
  db.save(A( Seq(b4) ))

  def fetchEqualingIds ( value : Seq[_] ) : Set[Long]
    = db.query[A].filterEquals("a", value).fetchAll().map{_.id}.toSet
  def fetchNotEqualingIds ( value : Seq[_] ) : Set[Long]
    = db.query[A].filterNotEquals("a", value).fetchAll().map{_.id}.toSet

}