object LaunchDemo extends App {
  // test at http://localhost:8081/path/test/helloGet?arg=aze&arg=rrr
  val server = Starter.createServer()
  io.StdIn.readLine()
  server.shutdownNow()
}
