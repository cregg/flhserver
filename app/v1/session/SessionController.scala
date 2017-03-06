//
// Copyright © [2010-2016] Visier Solutions Inc. All rights reserved.
//
package v1.session

import com.github.scribejava.core.model.{OAuth1AccessToken, OAuth1RequestToken}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Cookie}
import services.RedisService._
import services.YahooOauthService

class SessionController extends Controller{

  def index = Action { implicit request =>
    val yahooService = YahooOauthService.initService()
    redis.set(yahooService.token.getToken, yahooService.token.getTokenSecret)
    Redirect(yahooService.url())
  }

  def callback(oauth_token: String, oauth_verifier: String) = Action { implicit request =>
    val token: OAuth1RequestToken = new OAuth1RequestToken(oauth_token, redis.get(oauth_token).get)
    val yahooService = YahooOauthService.initService(token)
    (token, oauth_verifier) match {
      case (oauthToken: OAuth1RequestToken, oauthSecret: String) => {
        val token: OAuth1AccessToken = yahooService.accessToken(oauthSecret)
        redis.set(token.getToken, token.getTokenSecret)
        Ok(views.html.stats()).withCookies(Cookie("auth_token", token.getToken, maxAge = Some(20), httpOnly = false))
      }
      case _ => Ok(Json.toJson(Map("error" -> "Couldn't get token.")))
    }
  }
}
