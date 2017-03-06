package v1.players

import com.github.scribejava.core.model.OAuth1AccessToken
import models.Player
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Request}
import services.RedisService._
import services.YahooOauthService

/**
  * Created by cleclair on 2017-01-19.
  */
class PlayersController extends Controller{

  implicit def playerWrites = Json.writes[Player]

  def getToken(implicit request: Request[AnyContent]): OAuth1AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth1AccessToken(tokenString, redis.get(tokenString).get)
  }

  def index = Action { implicit request =>
    val yahooService = YahooOauthService.initService()
    Ok(yahooService.updatePlayerRankings(getToken).toString)
  }

}
