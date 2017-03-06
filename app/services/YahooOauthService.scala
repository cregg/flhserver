package services

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model._
import com.github.scribejava.core.oauth.OAuth10aService
import v1.YahooRoutes

/**
  * Created by cleclair on 2017-03-05.
  */
class YahooOauthService(val token: OAuth1RequestToken, val service: OAuth10aService) {

  def this() = this(null, YahooOauthService.oAuthService)

  def url(): String = service.getAuthorizationUrl(token)

  def accessToken(oauthSecret: String): OAuth1AccessToken = service.getAccessToken(token, oauthSecret)

  def makeRequest(verb: Verb, url: String, auth1AccessToken: OAuth1AccessToken): Response = {
    val request = new OAuthRequest(Verb.GET, YahooRoutes.draftResults, service)
    service.signRequest(auth1AccessToken, request)
    request.send()
  }
}

object YahooOauthService {

  val key = System.getProperty("YAHOO_KEY")
  val secret = System.getProperty("YAHOO_SECRET")

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
