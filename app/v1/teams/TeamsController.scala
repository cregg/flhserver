package v1.teams

import com.github.scribejava.core.model.{OAuth1AccessToken, Response, Verb}
import models.DraftSummary
import play.api.libs.json.{Json, _}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import services.RedisService._
import services.{RedisService, YahooOauthService}
import v1.YahooRoutes
import v1.JsonUtil._

class TeamsController extends Controller{

  def getToken(implicit request: Request[AnyContent]): OAuth1AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth1AccessToken(tokenString, redis.get(tokenString).get)
  }

  def get(id: String) = Action { implicit request =>
    val yahooResponse: Response = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.playersFromTeamReplaceId.replaceAll(":id", s"363.l.63462.t.$id"), getToken)
    RedisService.checkDraftResults(getToken)
    RedisService.getPlayerRankings(getToken)
    val teamDraftSummary: DraftSummary = DraftSummary.generateDraftSummary(yahooResponse.getBody, id)
    Ok(toJson(teamDraftSummary))
  }

  def getTeams = Action { implicit request =>
    val yahooRequest = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersTeams, getToken)
    val usersTeamResponse = Json.parse(yahooRequest.getBody)
    val teams = usersTeamResponse \ "team"
    Ok(teams.toString)
  }

}
