package v1.teams

import com.github.scribejava.core.model.{Response, Verb}
import models.DraftSummary
import play.api.libs.json.Json
import play.api.mvc.Action
import services.{RedisService, YahooOauthService}
import v1.JsonUtil._
import v1.YahooRoutes
import v1.controllers.FLHController

class TeamsController extends FLHController {

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
