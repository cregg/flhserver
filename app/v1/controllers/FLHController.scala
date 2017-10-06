package v1.controllers

import com.github.scribejava.core.model.OAuth2AccessToken
import play.api.mvc.{AnyContent, Controller, Request}
import services.RedisService
import v1.users.User
import services.JsonUtil._

/**
  * Created by cleclair on 2017-09-20.
  */
trait FLHController extends Controller {

  def getToken(implicit request: Request[AnyContent]): OAuth2AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth2AccessToken(tokenString)
  }

  def getUser(implicit request: Request[AnyContent]): Option[User] = {
    RedisService.redis.get(getToken.getAccessToken) match {
      case Some(json: String) => Some(fromJson[User](json))
      case _ => None
    }

  }
  def stringSetToKeys(keys: Set[String]): String = keys.map((key) => s"{$key}").mkString(",")

  def replaceKeysInUrl(url: String, keys: String): String = url.replace("{keys}", keys)

  def replaceSingleKeysInUrl(url: String, keys: String): String = url.replace("{key}", keys)

}
