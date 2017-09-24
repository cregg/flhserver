package v1.users

import com.github.scribejava.core.model.{Response, Verb}
import play.api.libs.json.Json
import play.api.mvc.Action
import services.YahooOauthService
import v1.YahooRoutes
import v1.controllers.FLHController
import v1.JsonUtil._
import v1.games.Game
import v1.leagues.League
/**
  * Created by cleclair on 2017-09-20.
  */
class UsersController extends FLHController {

  def get() = Action { implicit request =>
//    val gamesResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersGamesResourceYQL, getToken)
//    val games = fromJson[Set[Game]]((Json.parse(gamesResponse.getBody) \ "query" \ "results" \ "game").get.toString())
//    val userGamesString: String = games.map((game) => s"'${game.game_key}'").mkString(",")
//    val leaguesResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersLeaguesResourceYQL.replace("{game_ids}", userGamesString), getToken)
//    println("here")
//    val leagues: Set[League] = fromJson[Set[League]]((Json.parse(leaguesResponse.getBody) \ "query" \ "results" \ "team").get.toString)
//    val user = User(games, leagues)
    val gamesResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersGamesResource, getToken)
    Ok(gamesResponse.getBody)
  }


}
