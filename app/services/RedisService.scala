package services

import java.net.URI

import com.github.scribejava.core.model.OAuth1AccessToken
import com.redis.RedisClient
import models.{DraftPick, Player}
import play.api.libs.json.{JsArray, Json}
import v1.JsonUtil._
import scala.util.{Properties, Try}

/**
  * Created by cleclair on 2017-03-05.
  */
object RedisService {

  val redis = Properties.envOrNone("REDIS_URL") match {
    case Some(redisUrl) =>
      val redisUri = new URI(redisUrl)
      val host = redisUri.getHost
      val port = redisUri.getPort
      val secret = Try(redisUri.getUserInfo.split(":",2).last).toOption
      new RedisClient(host, port, secret = secret)
    case _ => new RedisClient("localhost", 6379)
  }

  def checkDraftResults(oAuth1AccessToken: OAuth1AccessToken): Seq[DraftPick] = {
    if(redis.get("363.l.63462_draft").isDefined) fromJson[Seq[DraftPick]](redis.get("363.l.63462_draft").get) else new YahooOauthService().updateLeagueDraft(oAuth1AccessToken)
  }

  def getPlayerRankings(oAuth1AccessToken: OAuth1AccessToken): Seq[Player] = {
    if(redis.get("363.l.63462").isDefined) fromJson[Seq[Player]](redis.get("363.l.63462").get) else new YahooOauthService().updatePlayerRankings(oAuth1AccessToken)
  }

}
