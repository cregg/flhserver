package v1.drafts

import com.github.scribejava.core.model.Verb
import models.{Draft, DraftPick}
import play.api.libs.json.Json
import play.api.mvc.Action
import services.YahooOauthService
import v1.YahooRoutes
import v1.controllers.FLHController
import v1.users.User

/**
  * Created by cleclair on 2017-02-02.
  */
class DraftsController extends FLHController {
  implicit def draftWrites = Json.writes[DraftPick]

  def get(teamID: String) = Action { implicit request =>
    getUser match {
      case Some(user: User) => {
        val specificLeagueDraftUrl = replaceSingleKeysInUrl(YahooRoutes.draftsFromLeague, teamID)
        val yahooResponse = new YahooOauthService().makeRequest(Verb.GET, specificLeagueDraftUrl, getToken)
        yahooResponse.getCode match {
          case code: Int if code == 200 => {
            Ok(Draft.yahooResponseToJson(yahooResponse.getBody).toString())
          }
          case code: Int if code == 401 => Unauthorized("Probably need to refresh Token")
          case code: Int if code == 400 => BadRequest("Possible Bad Team ID.")
        }
      }
      case _ => Unauthorized("Could not retrieve user.")
    }
  }



//  def get(teamId: String) = Action { implicit request =>
//    val yahooResponse = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.draftResults, getToken)
//    val draftResponse = Json.parse(yahooResponse.getBody)
//    val draftPicksJson = (draftResponse \ "query" \ "results" \ "league" \ "draft_results" \ "draft_result").as[JsArray]
//    val picks = draftPicksJson.value.toIndexedSeq.map((draftJson) =>
//      DraftPick(
//        (draftJson \ "pick").as[String].toInt,
//        (draftJson \ "round").as[String],
//        (draftJson \ "team_key").as[String],
//        (draftJson \ "player_key").as[String]
//      )
//    )
//    redis.set("363.l.63462_draft", Json.toJson(picks))
//    Ok(picks.toString)
//  }
}
