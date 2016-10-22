import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.process.Inflector
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.model.{ Resource => JerseyResource }

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import scala.collection.mutable.Buffer

object ServerDsl {
  def hello(context: ContainerRequestContext) = {
    val args = context.getUriInfo.getQueryParameters.get("arg")
    s"Hello $args"
  }
  
   def hola(context: ContainerRequestContext) = {
    s"Hola !"
  }

  def GET = Method("GET") _
  def POST = Method("POST") _

  case class Resource(name: String)(val methods: Method*)

  case class Method(name: String)(val handler: ContainerRequestContext => String)

  case class Server(scheme: String = "http",
                    host: String = "0.0.0.0",
                    port: Int = 8081,
                    root: String = "/path",
                    path: String = "/test")(resources: Resource*) {

    def build = {
      val resourceBuilder = JerseyResource.builder(path)

      for {
        res <- resources
        m <- res.methods
      } resourceBuilder.addChildResource(res.name)
        .addMethod(m.name)
        .handledBy(new Inflector[ContainerRequestContext, Response] {
          def apply(context: ContainerRequestContext) = {
            Response.ok(m.handler(context)).build
          }
        })

      val resourceConfig = (new ResourceConfig).registerResources(resourceBuilder.build)

      val uri = UriBuilder.fromPath(root).host(host).port(port).scheme(scheme).build()
      GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig, true)
    }
  }

}
