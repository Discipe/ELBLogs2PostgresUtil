import doobie.imports._
import cats._, cats.data._, cats.implicits._
import fs2.interop.cats._

class DBAccess(user: String, password: String) {

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
    val free = sql"""
         CREATE TABLE IF NOT EXISTS elb_logs (
           id SERIAL PRIMARY KEY NOT NULL,
           datetime varchar(50),
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
    sql"""INSERT INTO public.elb_logs (
      datetime, elbname, requestip, backendip,
      requestprocessingtime, backendprocessingtime, clientresponsetime, elbresponsecode,
      backendresponsecode, receivedbytes, sentbytes, requestverb, url,
      useragent, ssl)
      VALUES(
        ${le.dateTime}, ${le.name}, ${le.requestIP}, ${le.backendIP},
        ${le.requestProcessingTime}, ${le.backendProcessingTime}, ${le.clientResponseTime}, ${le.elbResponseCode},
        ${le.backendResponseCode}, ${le.receivedBytes}, ${le.sentBytes}, ${le.requestVerb}, ${le.url},
        ${le.userAgent}, ${le.ssl}
      );
    """.update
  }

  def insertRows(les: List[LogEntry]) = {
    val sql = "INSERT INTO public.elb_logs (datetime, elbname, requestip, backendip, " +
      "requestprocessingtime, backendprocessingtime, clientresponsetime, elbresponsecode, backendresponsecode, " +
      "receivedbytes, sentbytes, requestverb, url, protocol, useragent, ssl) " +
      "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
    Update[LogEntry](sql).updateMany(les).transact(xa).unsafePerformIO
  }

}
