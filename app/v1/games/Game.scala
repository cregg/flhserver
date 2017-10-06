package v1.games

import play.api.libs.json.{JsArray, Json}
import services.JsonUtil.fromJson

/**
  * Created by cleclair on 2017-09-20.
  */

case object Game {

  def getSingleGameFromJson(json: String): Game = {
    val body = Json.parse(json)
    val gameJson =  ((body \ "fantasy_content" \ "users" \ "0" \ "user").get.asInstanceOf[JsArray].value(1) \ "games" \ "0" \ "game").get.asInstanceOf[JsArray].value(0)
    fromJson[Game](gameJson.toString())
  }

}

case class Game(game_key: String,
                game_id: String,
                name: String,
                code: String,
                url: String,
                season: String,
                is_registration_over: String,
                is_game_over: String,
                is_offseason: String
               )
