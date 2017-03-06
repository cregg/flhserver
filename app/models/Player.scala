package models

import com.github.scribejava.core.model.{OAuth1AccessToken, OAuth1Token, Verb}
import play.api.libs.json.{JsObject, Json}
import services.RedisService._
import services.YahooOauthService
import v1.YahooRoutes

/**
  * Created by cleclair on 2017-03-05.
  */
case class Player(id: String, name: String, rank: Int = -1, draftPos: Int = -1) {

  override def toString: String = s"Name: $name\nCurrent Rank: $rank \nDraft Position: $draftPos\nScore: ${draftPos - rank}"

}