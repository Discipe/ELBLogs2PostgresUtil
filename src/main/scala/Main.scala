import java.io.File

import org.slf4j.LoggerFactory

object Main {

  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    logger.debug(s"Starting...")
//    val le = new LogEntry("x","x","x",100,"x",100,15.0,0.15,0.7,"x","x",7L,8L,"x", "x", "x", "x", "x")
    val dba = new DBAccess()
    dba.createTable()

    new LogsParserWorker(new File("H:\\downloads\\s3logs\\25\\"), dba).process()
//    dba.insertRows(les)

    logger.debug(s"Finished")
  }

}
