package services

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model._
import com.github.scribejava.core.oauth.OAuth10aService
import models.{DraftPick, Player}
import play.api.libs.json.{JsArray, JsObject, Json}
import v1.YahooRoutes
import v1.JSParsers._
import services.RedisService._

/**
  * Created by cleclair on 2017-03-05.
  */
class YahooOauthService(val token: OAuth1RequestToken, val service: OAuth10aService) {

  def this() = this(null, YahooOauthService.oAuthService)

  def url(): String = service.getAuthorizationUrl(token)

  def accessToken(oauthSecret: String): OAuth1AccessToken = service.getAccessToken(token, oauthSecret)

  def makeRequest(verb: Verb, url: String, auth1AccessToken: OAuth1AccessToken): Response = {
    val request = new OAuthRequest(Verb.GET, url, service)
    service.signRequest(auth1AccessToken, request)
    request.send()
  }

  def getLeagueIDFromTeamID(verb: Verb, url: String, auth1AccessToken: OAuth1AccessToken): Response = {
    val request = new OAuthRequest(Verb.GET, YahooRoutes.draftResults, service)
    service.signRequest(auth1AccessToken, request)
    request.send()
  }

  def updatePlayerRankings(oAuth1AccessToken: OAuth1AccessToken) = {
    val playerCounts = IndexedSeq("0", "25", "50", "75", "100", "125", "150", "175", "200", "225", "250", "275", "300", "325", "350")
    var leagueId = ""
    val players = playerCounts.flatMap{ (startCount) =>
      val playerUrl = YahooRoutes.playersFromLeague.replaceAll("\\{start\\}", startCount)
      val jsonResponse = Json.parse(makeRequest(Verb.GET, playerUrl, oAuth1AccessToken).getBody)
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
    players
  }

  def updateLeagueDraft(oAuth1AccessToken: OAuth1AccessToken): IndexedSeq[DraftPick] = {
    val yahooResponse = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.draftResults, oAuth1AccessToken)
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
    picks
  }
}

object YahooOauthService {

  val key = "dj0yJmk9VjEyMzZleFZCMnAxJmQ9WVdrOVJWRnpWM0IwTlRnbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03ZA--"
  val secret = "5be45ab806ebd392bfd04100ef2c2140ed9afc03"

  val oAuthService: OAuth10aService = new ServiceBuilder()
    .apiKey(key)
    .apiSecret(secret)
    .build(YahooApi.instance())

  def initService(): YahooOauthService = {
    val oAuthService: OAuth10aService = new ServiceBuilder()
      .apiKey(key)
      .apiSecret(secret)
      .build(YahooApi.instance())
    new YahooOauthService(oAuthService.getRequestToken, oAuthService)
  }

  def initService(token: OAuth1RequestToken): YahooOauthService = {
    val oAuthService: OAuth10aService = new ServiceBuilder()
      .apiKey(key)
      .apiSecret(secret)
      .build(YahooApi.instance())
    new YahooOauthService(token, oAuthService)
  }

}
