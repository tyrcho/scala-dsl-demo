import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.process.Inflector
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.model.Resource

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.MediaType

object Starter {
  def createServer(scheme: String = "http",
                   host: String = "0.0.0.0",
                   port: Int = 8081,
                   path: String = "/path"): HttpServer = {

    val resourceBuilder = Resource.builder("/test")
    val dataReceived = collection.mutable.Map.empty[String, String]

    resourceBuilder.addChildResource("/helloGet")
      .addMethod("GET")
      .handledBy(new Inflector[ContainerRequestContext, Response] {
        def apply(context: ContainerRequestContext) = {
          val arg = context.getUriInfo.getQueryParameters.get("arg").get(0)
          val data = dataReceived.getOrElse(arg, s"no data for $arg")
          Response.ok(s"Hello $arg \n$data").build
        }
      })

    resourceBuilder.addChildResource("/helloPost")
      .addMethod("POST")
      .consumes(MediaType.APPLICATION_JSON)
      .handledBy(new Inflector[ContainerRequestContext, Response] {
        def apply(context: ContainerRequestContext): Response = {
          val id = context.getUriInfo.getQueryParameters.get("id").get(0)
          val data = io.Source.fromInputStream(context.getEntityStream).getLines.mkString("\n")
          dataReceived += id -> data
          Response.ok("Message received : " + data).build
        }
      })

    val resourceConfig = (new ResourceConfig).registerResources(resourceBuilder.build)

    val uri = UriBuilder.fromPath(path).host(host).port(port).scheme(scheme).build()
    GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig, true)
  }

}