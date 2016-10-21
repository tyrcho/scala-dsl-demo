import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.process.Inflector
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.model.Resource

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import scala.collection.mutable.Buffer

object ServerDsl {
  implicit def buildServer(s: Server) = s.build

  case class Res(name: String)

  case class Server(scheme: String = "http",
                    host: String = "0.0.0.0",
                    port: Int = 8081,
                    path: String = "/path") {
    val resources = Buffer.empty[Res]

    def resource(name: String) = resources += Res(name)

    def build = {
      val resourceBuilder = Resource.builder("/test")

      for (res <- resources) {
        resourceBuilder.addChildResource(res.name)
          .addMethod("GET")
          .handledBy(new Inflector[ContainerRequestContext, Response] {
            def apply(context: ContainerRequestContext) = {
              val args = context.getUriInfo.getQueryParameters.get("arg")
              Response.ok(s"Hello $args").build
            }
          })
      }

      val resourceConfig = (new ResourceConfig).registerResources(resourceBuilder.build)

      val uri = UriBuilder.fromPath(path).host(host).port(port).scheme(scheme).build()
      GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig, true)
    }
  }

}
