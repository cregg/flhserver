package html

import play.api.mvc.Action
import v1.controllers.FLHController

/**
  * Created by cleclair on 2017-10-05.
  */
class HTMLController extends FLHController {

    def index = Action { implicit request =>
      Ok(views.html.users())
    }
}
