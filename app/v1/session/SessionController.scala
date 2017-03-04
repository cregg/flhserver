//
// Copyright © [2010-2016] Visier Solutions Inc. All rights reserved.
//
package v1.session

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1RequestToken
import com.redis.RedisClient
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class SessionController extends Controller{

  val key = System.getProperty("YAHOO_KEY")
  val secret = System.getProperty("YAHOO_SECRET")

  val oAuthService = new ServiceBuilder()
    .apiKey(key)
    .apiSecret(secret)
    .build(YahooApi.instance())

  val redis = new RedisClient("localhost", 6379)

  def index = Action { implicit request =>
    val token: OAuth1RequestToken = oAuthService.getRequestToken
    redis.set(token.getToken, token.getTokenSecret)
    Redirect(oAuthService.getAuthorizationUrl(token))
  }

  def callback(oauth_token: String, oauth_verifier: String) = Action { implicit request =>
    val token = new OAuth1RequestToken(oauth_token, redis.get(oauth_token).get)
    (token, oauth_verifier) match {
      case (oauthToken: OAuth1RequestToken, oauthSecret: String) => {
        val token = oAuthService.getAccessToken(oauthToken, oauthSecret)
        redis.set(token.getToken, token.getTokenSecret)
        Ok(Json.toJson(Map("token" -> token.getToken)))
      }
      case _ => Ok(Json.toJson(Map("error" -> "Couldn't get token.")))
    }
  }
}
