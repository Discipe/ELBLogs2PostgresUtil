CREATE TABLE elb_logs (
  datetime varchar(50),
  ELBName varchar(50),
  RequestIP varchar(50),
  RequestPort INT,
  BackendIP varchar(50),
  BackendPort INT,
  RequestProcessingTime double precision,
  BackendProcessingTime double precision,
  ClientResponseTime double precision,
  ELBResponseCode varchar(50),
  BackendResponseCode varchar(50),
  ReceivedBytes BIGINT,
  SentBytes bigint,
  RequestVerb varchar(50),
  URL varchar(1024),
  userAgent varchar(255),
  ssl varchar(200)
)