package vorm.jdbc

import com.weiglewilczek.slf4s.Logging
import java.sql.{ResultSet, PreparedStatement, Connection, Statement => JStatement}
import org.joda.time.DateTime

class ConnectionAPI(connection: Connection) extends Logging {

  def select(stmt: Statement): ResultSet = {
    throw new NotImplementedError
  }
  /**
   * @return How many rows updated
   */
  def update(stmt: Statement): Int = {
    logger.info("Executing update:\n" + stmt)
    executeUpdate(stmt)
  }
  /**
   * Either throw an exception or perform an insert and return autogenerated values result set for parsing
   * @return Autogenerated values
   */
  def insert(stmt: Statement): ResultSet = {
    logger.info("Executing insert:\n" + stmt)
    executeUpdateAndGetGeneratedKeys(stmt)
  }
  /**
   * @return How many rows deleted
   */
  def delete(stmt: Statement): Int = {
    logger.info("Executing delete:\n" + stmt)
    executeUpdate(stmt)
  }
  private def executeUpdateAndGetGeneratedKeys(stmt: Statement): ResultSet = {
    if (stmt.data.isEmpty) {
      val js = connection.createStatement()
      js.executeUpdate(stmt.sql).ensuring(_ == 1)
      js.getGeneratedKeys
    } else {
      val js = prepareJDBCStatement(stmt, true)
      js.executeUpdate().ensuring(_ == 1)
      js.getGeneratedKeys
    }
  }
  private def executeUpdate(stmt: Statement) = {
    if (stmt.data.isEmpty) connection.createStatement().executeUpdate(stmt.sql)
    else prepareJDBCStatement(stmt).executeUpdate()
  }
  private def prepareJDBCStatement(stmt: Statement, generatedKeys: Boolean = false) = {
    val s =
      connection.prepareStatement(
        stmt.sql,
        if (generatedKeys) JStatement.RETURN_GENERATED_KEYS
        else JStatement.NO_GENERATED_KEYS
      )
    stmt.data.zipWithIndex.foreach {
      case (v, i) => s.set(i, v)
    }

    s
  }


}