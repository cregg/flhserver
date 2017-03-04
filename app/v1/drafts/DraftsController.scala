package v1.drafts

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.{OAuth1AccessToken, OAuthRequest, Verb}
import com.redis.RedisClient
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import v1.YahooRoutes
import v1.JSParsers._

/**
  * Created by cleclair on 2017-02-02.
  */
case class DraftPick(pick: Int, round: String, team_key: String, player_key: String)

case class Player(id: String, name: String, rank: Int = -1, draftPos: Int = -1) {

  override def toString: String = s"Name: $name\nCurrent Rank: $rank \nDraft Position: $draftPos\nScore: ${draftPos - rank}"

}

class DraftsController extends Controller{

  val oAuthService = new ServiceBuilder()
    .apiKey("dj0yJmk9VjEyMzZleFZCMnAxJmQ9WVdrOVJWRnpWM0IwTlRnbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03ZA--")
    .apiSecret("5be45ab806ebd392bfd04100ef2c2140ed9afc03")
    .build(YahooApi.instance())

  val redis = new RedisClient("localhost", 6379)

  def getToken(implicit request: Request[AnyContent]): OAuth1AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth1AccessToken(tokenString, redis.get(tokenString).get)
  }

  def get(teamId: String) = Action { implicit request =>
    val yahooRequest = new OAuthRequest(Verb.GET, YahooRoutes.draftResults, oAuthService)
    oAuthService.signRequest(getToken, yahooRequest)
    val draftResponse = Json.parse(yahooRequest.send().getBody)
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
