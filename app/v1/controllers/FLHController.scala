package v1.controllers

import com.github.scribejava.core.model.OAuth2AccessToken
import play.api.mvc.{AnyContent, Controller, Request}

/**
  * Created by cleclair on 2017-09-20.
  */
trait FLHController extends Controller {

  def getToken(implicit request: Request[AnyContent]): OAuth2AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth2AccessToken(tokenString)
  }

}
