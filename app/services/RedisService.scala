package services

import com.github.scribejava.core.model.OAuth1AccessToken
import com.redis.RedisClient
import models.{DraftPick, Player}
import play.api.libs.json.{JsArray, Json}
import v1.JSParsers._

/**
  * Created by cleclair on 2017-03-05.
  */
object RedisService {

  val redis = if(System.getenv("heroku") == null) new RedisClient("localhost", 6379) else new RedisClient("http://url.com", 6379)

  def checkDraftResults(oAuth1AccessToken: OAuth1AccessToken): Seq[DraftPick] = {
    if(redis.get("363.l.63462_draft").isDefined) Json.parse(redis.get("363.l.63462_draft").get).as[JsArray].value.map(_.as[DraftPick]) else new YahooOauthService().updateLeagueDraft(oAuth1AccessToken)
  }

  def getPlayerRankings(oAuth1AccessToken: OAuth1AccessToken): Seq[Player] = {
    if(redis.get("363.l.63462").isDefined) Json.parse(redis.get("363.l.63462").get).as[JsArray].value.map(_.as[Player]) else new YahooOauthService().updatePlayerRankings(oAuth1AccessToken)
  }

}
