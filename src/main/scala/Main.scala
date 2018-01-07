import java.io.File

import org.slf4j.LoggerFactory

import scala.util.Try

object Main {

  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    logger.debug(s"Starting...")
    val dbuser = args(0)
    val dbpassword = args(1)
    val filePath = args(2)
    val dba = new DBAccess(dbuser, dbpassword)
    dba.createTableIfNotExists()

    new LogsParserWorker(new File(filePath), dba).process()
    logger.debug(s"Finished")
  }

}
