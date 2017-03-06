package v1

import models.{DraftPick, Player}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}


object JSParsers {

  implicit val playerReads: Reads[Player] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "name").read[String] and
    (JsPath \ "rank").read[Int] and
    (JsPath \ "draftPos").read[Int]
  )(Player)

  implicit val playerWrites: Writes[Player] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "rank").write[Int] and
      (JsPath \ "draftPos").write[Int]
    )(unlift(Player.unapply))

  implicit val draftPickRead: Reads[DraftPick] = (
    (JsPath \ "pick").read[Int] and
    (JsPath \ "round").read[String] and
    (JsPath \ "team_key").read[String] and
    (JsPath \ "player_key").read[String]
  )(DraftPick)

  implicit val draftPickWrites: Writes[DraftPick] = (
    (JsPath \ "pick").write[Int] and
      (JsPath \ "round").write[String] and
      (JsPath \ "team_key").write[String] and
      (JsPath \ "player_key").write[String]
    )(unlift(DraftPick.unapply))

}
