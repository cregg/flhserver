package v1.session

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Created by cleclair on 2017-01-08.
  */
class SessionRouter @Inject()(controller: SessionController) extends SimpleRouter {
  val prefix = "/v1/sessions"

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

//    case POST(p"/") =>
//      controller.process
//
//    case GET(p"/$id") =>
//      controller.show(id)
  }

}
