package services

import java.net.URI

import com.github.scribejava.core.model.OAuth1AccessToken
import com.redis.RedisClient
import models.{DraftPick, Player}
import play.api.libs.json.{JsArray, Json}
import v1.JSParsers._

import scala.util.{Properties, Try}

/**
  * Created by cleclair on 2017-03-05.
  */
object RedisService {

//  val redis = if(System.getenv("heroku") == null) new RedisClient("localhost", 6379) else new RedisClient("redis://h:p9324402001a0dc7aa058a04e55de64c49b2a609afbfa371b93973a94ffd30c65@ec2-34-198-124-158.compute-1.amazonaws.com", 40769)
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
    if(redis.get("363.l.63462_draft").isDefined) Json.parse(redis.get("363.l.63462_draft").get).as[JsArray].value.map(_.as[DraftPick]) else new YahooOauthService().updateLeagueDraft(oAuth1AccessToken)
  }

  def getPlayerRankings(oAuth1AccessToken: OAuth1AccessToken): Seq[Player] = {
    if(redis.get("363.l.63462").isDefined) Json.parse(redis.get("363.l.63462").get).as[JsArray].value.map(_.as[Player]) else new YahooOauthService().updatePlayerRankings(oAuth1AccessToken)
  }

}
