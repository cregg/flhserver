package v1.users

import com.github.scribejava.core.model.{Response, Verb}
import play.api.mvc.Action
import services.{RedisService, YahooOauthService}
import v1.YahooRoutes
import v1.controllers.FLHController
import v1.games.Game
import v1.leagues.Team
import services.JsonUtil._

/**
  * Created by cleclair on 2017-09-20.
  */
class UsersController extends FLHController {

  def get() = Action { implicit request =>
    val gamesResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersGamesResource, getToken)
    val games = Set(Game.getSingleGameFromJson(gamesResponse.getBody))
    val teamsResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersTeamsResource, getToken)
    val teams = Team.getTeamsFromYahooResponse(teamsResponse.getBody)
    val teamsForThisYear = teams.filter(_.team_key.substring(0,3) == games.head.game_key)
    val userJson = toJson(new User(games = games, teams = teamsForThisYear))
    RedisService.redis.set(getToken.getAccessToken, userJson)
//    val gamesResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.teamStatsURL, getToken)
    Ok(userJson)
  }
}
