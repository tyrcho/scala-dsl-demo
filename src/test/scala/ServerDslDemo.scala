import ServerDsl._
import org.glassfish.grizzly.http.server.HttpServer

object ServerDslDemo extends App {
  val s: HttpServer = new Server(port = 8081) {
    resource("/test")
  }
  s.start()
  io.StdIn.readLine()
  s.shutdown()

}

//  val all = servers {
//    server(port = 12002)

    //    {
    //      resource("/v1/executor") {
    //        put(body = readPrintOKBody < ExecutorTaskDTO > ())
    //      }
    //      resource("/v1/broker/sync/user") {
    //        post(body = readPrintOKBody < UserAndProjectWrapper > ())
    //      }
    //      resource("/v1/broker/unsync/user") {
    //        post(body = readPrintOKBody < UnsyncUserTask > ())
    //      }
    //      resource("/v1/broker/sync/resource") {
    //        post(body = readPrintOKBody < ResourceAndProjectWrapper > ())
    //      }
    //      resource("/v1/broker/unsync/resource") {
    //        post(body = readPrintOKBody < ResourceAndProjectWrapper > ())
    //      }
    //      resource("/v1/broker/create/resource") {
    //        post(body = readPrintOKBody < ResourceAndProjectWrapper > ())
    //      }
    //    }
//  }
