import ServerDsl._
import org.glassfish.grizzly.http.server.HttpServer

object ServerDslDemo extends App {
  val s = Server(port = 8081)(
    Resource("/hello")(
      GET { hello },
      POST { hello }),
    Resource("/hola")(
      GET { hola })).build
  s.start()
  // http://localhost:8081/path/test/hello?arg=aze&arg=rrr55
  io.StdIn.readLine()
  s.shutdown()
}
