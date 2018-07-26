import java.time.{ZoneOffset, ZonedDateTime}

import doobie.imports._
import doobie.util.meta.Meta
import fs2.interop.cats._

class DBAccess(user: String, password: String) {

  import doobie.util.meta.Meta._

  implicit val mm: Meta[java.time.ZonedDateTime] = JavaTimeInstantMeta.xmap(ZonedDateTime.ofInstant(_, ZoneOffset.UTC), _.toInstant)

  ///public
  val xa = DriverManagerTransactor[IOLite](
    "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", user, password
  )

  val program2: ConnectionIO[Int] = sql"select 42".query[Int].unique

  val program3: ConnectionIO[(Int, Double)] =
    for {
      a <- sql"select 42".query[Int].unique
      b <- sql"select random()".query[Double].unique
    } yield (a, b)

  val task2: IOLite[Int] = program2.transact(xa)

  val task = program3.transact(xa)

  def calcConst() = {
    task.unsafePerformIO
  }

  def createTableIfNotExists(): Int = {
    //datetime varchar(50),
    val free = sql"""
         CREATE TABLE IF NOT EXISTS elb_logs (
           id SERIAL PRIMARY KEY NOT NULL,
           datetime timestamptz,
           ELBName varchar(50),
           RequestIP varchar(50),
           BackendIP varchar(50),
           RequestProcessingTime double precision,
           BackendProcessingTime double precision,
           ClientResponseTime double precision,
           ELBResponseCode varchar(50),
           BackendResponseCode varchar(50),
           ReceivedBytes BIGINT,
           SentBytes bigint,
           RequestVerb varchar(50),
           URL varchar(2048),
           protocol VARCHAR(50),
           userAgent varchar(255),
           ssl varchar(200)
         )
    """.update.run
    free.transact(xa).unsafePerformIO
  }

  def insertRow(le: LogEntry) = {
    val url = le.url.substring(0, 2048)
    val agent = le.userAgent.substring(0, 255)
    sql"""INSERT INTO public.elb_logs (
      datetime, elbname, requestip, backendip,
      requestprocessingtime, backendprocessingtime, clientresponsetime, elbresponsecode,
      backendresponsecode, receivedbytes, sentbytes, requestverb, url,
      useragent, ssl)
      VALUES(
        ${le.dateTime}, ${le.name}, ${le.requestIP}, ${le.backendIP},
        ${le.requestProcessingTime}, ${le.backendProcessingTime}, ${le.clientResponseTime}, ${le.elbResponseCode},
        ${le.backendResponseCode}, ${le.receivedBytes}, ${le.sentBytes}, ${le.requestVerb}, ${url},
        ${le.userAgent}, ${le.ssl}
      );
    """.update
  }

  def insertRows(les: List[LogEntry]) = {
    val cropped = les.map(le => le.copy(
      url = le.url.substring(0, math.min(le.url.length, 2048)),
      userAgent = le.userAgent.substring(0, math.min(le.userAgent.length, 255)))
    )
    val sql = s"INSERT INTO public.elb_logs (datetime, elbname, requestip, backendip, " +
      "requestprocessingtime, backendprocessingtime, clientresponsetime, elbresponsecode, backendresponsecode, " +
      "receivedbytes, sentbytes, requestverb, url, protocol, useragent, ssl) " +
      "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
    Update[LogEntry](sql).updateMany(cropped).transact(xa).unsafePerformIO
  }

}
