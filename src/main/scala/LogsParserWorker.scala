import java.io.File
import java.util.UUID

import org.slf4j.LoggerFactory

import scala.io.Source

class LogsParserWorker(file: File, dBAccess: DBAccess, index: Option[Int] = None) {
  val uid = UUID.randomUUID().toString
  val logger = LoggerFactory.getLogger(this.getClass)

//  val regex = """^([^ ]*) ([^ ]*) ([^ ]*):([0-9]*) ([^ ]*):([0-9]*) ([.0-9]*) ([.0-9]*) ([.0-9]*) (-|[0-9]*) (-|[0-9]*) ([-0-9]*) ([-0-9]*) "([^ ]*) ([^ ]*) (- |[^ ]*)" "(.+)" (.*)""".r
  val regex = """^([^ ]*) ([^ ]*) ([^ ]*) ([^ ]*) ([-.0-9]*) ([-.0-9]*) ([-.0-9]*) (-|[0-9]*) (-|[0-9]*) ([-0-9]*) ([-0-9]*) "([^ ]*) ([^ ]*) (- |[^ ]*)" "(.+)" (.*)""".r

  private val fileInfo = {
    index
      .map(i => s"[$i] < ${file.getName} > (${file.getPath})")
      .getOrElse(s"< ${file.getName} > (${file.getPath})")
  }

  private def line2entry(l: String): Option[LogEntry] = {
    val matcherOpt = regex.findAllIn(l).matchData.toList match {
      case h :: Nil => Some(h)
      case _ => logger.warn(s"Cannot parse line < $l >")
        None
    }
    matcherOpt flatMap { matcher =>
      val list = for {
        i <- 1 to matcher.groupCount
      } yield {
        matcher.group(i)
      }

      list.toList match {
        case datetime :: name :: reqIP :: backIp ::
          reqTime :: backTime :: clientTime :: elbCode :: backCode ::
          rcvBytes :: sentBytes :: reqVerb :: url :: protocol :: ua :: ssl :: Nil => Some(
          new LogEntry(datetime.trim, name, reqIP, backIp, reqTime.toDouble, backTime.toDouble, clientTime.toDouble,
            elbCode, backCode, rcvBytes.toLong, sentBytes.toLong, reqVerb, url, protocol, ua, ssl)
        )

        case _ =>
          logger.warn(s"Couldn't parse line < $l >")
          None
      }
    }
  }

  def save(list: List[LogEntry]) = {
    logger.debug(s"Inserting ${list.size} records parsed from file $fileInfo")
    dBAccess.insertRows(list)
  }

  def process(): Unit = {
    if (file.isDirectory) {
      logger.debug(s"${file.getPath} is a directory; parsing files in it")
      val files = file.listFiles().sortBy(_.getName).zipWithIndex
      logger.debug(s"Found ${files.length} in the directory")
      files.foreach {
        case (f, i) => new LogsParserWorker(f, dBAccess, Some(i)).process()
      }
//      save(res)
    } else {
      logger.debug(s"Parsing file $fileInfo...")
      save(Source.fromFile(file).getLines().flatMap(line2entry).toList)
      logger.debug(s"File parsed & saved. $fileInfo")
    }
  }

}
