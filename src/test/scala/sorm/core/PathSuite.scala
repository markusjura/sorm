package sorm.core

import org.scalatest.FunSuite
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class PathSuite extends FunSuite with Matchers {
  
  import Path._
  
  test("pathAndRemainder failure"){
    pending
  }
  test("pathAndRemainder braced parsing"){
    partAndRemainder("(asdf)") should be === (Part.Braced("asdf"), "")
    partAndRemainder("(asdf).sdf") should be === (Part.Braced("asdf"), ".sdf")
    partAndRemainder("(342).sdf") should be === (Part.Braced("342"), ".sdf")
  }
  test("pathAndRemainder dotted parsing"){
    partAndRemainder("sdf") should be === (Part.Dotted("sdf"), "")
    partAndRemainder("sdf.dksfje") should be === (Part.Dotted("sdf"), ".dksfje")
    partAndRemainder(".sdf.dksfje") should be === (Part.Dotted("sdf"), ".dksfje")
  }
}