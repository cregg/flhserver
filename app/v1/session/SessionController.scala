//
// Copyright © [2010-2016] Visier Solutions Inc. All rights reserved.
//
package v1.session

import com.github.scribejava.core.model.OAuth2AccessToken
import play.api.mvc.{Action, Controller, Cookie}
import services.YahooOauthService

class SessionController extends Controller{

  def index = Action { implicit request =>
    val yahooService = YahooOauthService.initService()
    Redirect(yahooService.url())
  }

  def callback(oauth_code: String, state: String) = Action { implicit request =>
    val yahooService = YahooOauthService.initService()
    val token: OAuth2AccessToken = yahooService.service.getAccessToken(oauth_code)

    Ok(
      views.html.users()).withCookies(Cookie("auth_token", token.getAccessToken, maxAge = Some(20), httpOnly = false)
    )
  }
}
