
case class LogEntry(
                     dateTime: String,
                     name: String,
                     requestIP: String,
//                     requestPort: Int,
                     backendIP: String,
//                     backendPort: Int,

                     requestProcessingTime: Double,
                     backendProcessingTime: Double,
                     clientResponseTime: Double,

                     elbResponseCode: String,
                     backendResponseCode: String,
                     receivedBytes: Long,
                     sentBytes: Long,

                     requestVerb: String,
                     url: String,
                     protocol: String,

                     userAgent: String,
                     ssl: String
                   )