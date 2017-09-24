package services

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model._
import com.github.scribejava.core.oauth.{OAuth10aService, OAuth20Service}
import models.{DraftPick, Player}
import play.api.libs.json.{JsArray, JsObject, Json}
import v1.YahooRoutes
import v1.JsonUtil._
import services.RedisService._

/**
  * Created by cleclair on 2017-03-05.
  */
class YahooOauthService(val service: OAuth20Service) {

  def this() = this(YahooOauthService.oAuthService)

  def url() = service.getAuthorizationUrl

  def makeRequest(verb: Verb, url: String, auth2AccessToken: OAuth2AccessToken): Response = {
    val request = new OAuthRequest(Verb.GET, url, service)
    signRequest(auth2AccessToken, request)
    request.send()
  }

  private def signRequest(accessToken: OAuth2AccessToken, request: AbstractRequest): Unit = {
    request.addHeader("Authorization", "Bearer " + accessToken.getAccessToken)
  }

  def updatePlayerRankings(oAuth2AccessToken: OAuth2AccessToken) = {
    val playerCounts = IndexedSeq("0", "25", "50", "75", "100", "125", "150", "175", "200", "225", "250", "275", "300", "325", "350")
    var leagueId = ""
    val players = playerCounts.flatMap{ (startCount) =>
      val playerUrl = YahooRoutes.playersFromLeague.replaceAll("\\{start\\}", startCount)
      val jsonResponse = Json.parse(makeRequest(Verb.GET, playerUrl, oAuth2AccessToken).getBody)
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
    redis.set(leagueId, toJson(players))
    players
  }

  def updateLeagueDraft(oAuth2AccessToken: OAuth2AccessToken): IndexedSeq[DraftPick] = {
    val yahooResponse = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.draftResults, oAuth2AccessToken)
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
    redis.set("363.l.63462_draft", toJson(picks))
    picks
  }
}

object YahooOauthService {

  val key = System.getenv("YAHOO_KEY")
  val secret = System.getenv("YAHOO_SECRET")
  val callbackURL = System.getenv("YAHOO_CALLBACK")
  val RESPONSE_TYPE = "code"

  val oAuthService: OAuth20Service = new ServiceBuilder().callback(callbackURL)
    .apiKey(key)
    .apiSecret(secret)
    .responseType(RESPONSE_TYPE)
    .build(YahooApi20.instance())

  def initService(): YahooOauthService = {
    val oAuthService: OAuth20Service  = new ServiceBuilder().callback(callbackURL)
      .apiKey(key)
      .apiSecret(secret)
      .responseType(RESPONSE_TYPE)
      .build(YahooApi20.instance())
    new YahooOauthService(oAuthService)
  }
}
