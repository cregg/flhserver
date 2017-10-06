package models

import play.api.libs.json.{JsArray, JsDefined, JsObject, Json}
import services.JsonUtil.fromJson
import v1.leagues.Team

/**
  * Created by cleclair on 2017-03-05.
  */
case class DraftPick(pick: Int, round: String, team_key: String, player_key: String)

case object Draft {

  def yahooResponseToJson(yahooResponse: String): Seq[DraftPick] = {
    val draftJson =  Json.parse(yahooResponse) \ "fantasy_content" \ "league" \ 1 \ "draft_results"
    val totalDraftPicks = (draftJson \ "count").get.toString().toInt
    IndexedSeq.range(0, totalDraftPicks).map{(currentDraftPick) =>
      fromJson[DraftPick]((draftJson \ s"$currentDraftPick" \ "draft_result").asInstanceOf[JsDefined].value.toString())
    }
  }

//  def yahooResponseToListOfDraftPicks(yahooResponse: String): IndexedSeq[DraftPick] = {
//    fromJson[IndexedSeq[DraftPick]](yahooResponseToJson(yahooResponse))
//  }

}

case class Draft(draftPicks: Set[DraftPick] = Set())

