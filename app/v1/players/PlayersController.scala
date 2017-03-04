package v1.players

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.{OAuth1AccessToken, OAuthRequest, Verb}
import com.redis.RedisClient
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Request}
import v1.YahooRoutes
import v1.JSParsers._
import v1.drafts.Player
/**
  * Created by cleclair on 2017-01-19.
  */
class PlayersController extends Controller{

  val oAuthService = new ServiceBuilder()
    .apiKey("dj0yJmk9VjEyMzZleFZCMnAxJmQ9WVdrOVJWRnpWM0IwTlRnbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03ZA--")
    .apiSecret("5be45ab806ebd392bfd04100ef2c2140ed9afc03")
    .build(YahooApi.instance())

  val redis = new RedisClient("localhost", 6379)

  def getToken(implicit request: Request[AnyContent]): OAuth1AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth1AccessToken(tokenString, redis.get(tokenString).get)
  }

  def index = Action { implicit request =>
    val playerCounts = IndexedSeq("0", "25", "50", "75", "100", "125", "150", "175", "200", "225", "250", "275", "300", "325", "350")
    var leagueId = ""
    val players = playerCounts.flatMap{ (startCount) =>
      val playerUrl = YahooRoutes.playersFromLeague.replaceAll("\\{start\\}", startCount)
      val yahooRequest = new OAuthRequest(Verb.GET, playerUrl, oAuthService)
      oAuthService.signRequest(getToken, yahooRequest)
      val yahooResponse = yahooRequest.send()
      val jsonResponse = Json.parse(yahooResponse.getBody)
      leagueId = (jsonResponse \ "fantasy_content" \ "league" \ 0 \ "league_key").as[String]
      val players = (jsonResponse \ "fantasy_content" \ "league" \ 1 \ "players").as[JsObject].values.toIndexedSeq.filter(_.isInstanceOf[JsObject])
      redis.set(leagueId, players.toString)
      val newPlayers = players.map((playerJson) =>
        Player(
          (playerJson \ "player" \ 0 \ 0 \ "player_key").as[String],
          (playerJson \ "player" \ 0 \ 2 \ "name" \ "full").as[String]
        )
      )
      newPlayers
    }
    redis.set(leagueId, Json.toJson(players))
    Ok(players.toString)
  }


  

}
