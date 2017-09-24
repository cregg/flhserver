package v1.drafts

import com.github.scribejava.core.model.{OAuth1AccessToken, Verb}
import models.DraftPick
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import services.RedisService._
import services.YahooOauthService
import v1.YahooRoutes
import v1.controllers.FLHController

/**
  * Created by cleclair on 2017-02-02.
  */
class DraftsController extends FLHController {

  implicit def draftWrites = Json.writes[DraftPick]

  def get(teamId: String) = Action { implicit request =>
    val yahooResponse = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.draftResults, getToken)
    val draftResponse = Json.parse(yahooResponse.getBody)
    val draftPicksJson = (draftResponse \ "query" \ "results" \ "league" \ "draft_results" \ "draft_result").as[JsArray]
    val picks = draftPicksJson.value.toIndexedSeq.map((draftJson) =>
      DraftPick(
        (draftJson \ "pick").as[String].toInt,
        (draftJson \ "round").as[String],
        (draftJson \ "team_key").as[String],
        (draftJson \ "player_key").as[String]
      )
    )
    redis.set("363.l.63462_draft", Json.toJson(picks))
    Ok(picks.toString)
  }
}
