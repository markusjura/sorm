package sorm.jdbc

import com.typesafe.scalalogging.LazyLogging
import java.sql.ResultSet

class JdbcConnectionSimulator
  extends JdbcConnection(null)
  with LazyLogging
  {
    override def executeQuery
      [ T ]
      ( s : Statement )
      ( parse : ResultSetView => T = (_ : ResultSetView).indexedRowsTraversable.toList )
      : T
      = {
        println(s.toString)
        ???
      }

    override def executeUpdateAndGetGeneratedKeys
      ( stmt : Statement )
      : List[IndexedSeq[Any]] 
      = {
        println(stmt.toString)
        List(Vector(777l))
      }

    override def executeUpdate
      ( stmt : Statement )
      : Int = {
        println(stmt.toString)
        1
      }

  }