package v1.players

import play.api.mvc.Action
import services.YahooOauthService
import v1.controllers.FLHController

/**
  * Created by cleclair on 2017-01-19.
  */
class PlayersController extends FLHController {

  def index = Action { implicit request =>
    val yahooService = YahooOauthService.initService()
    Ok(yahooService.updatePlayerRankings(getToken).toString)
  }

}
