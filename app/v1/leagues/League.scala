package v1.leagues

import play.api.libs.json._
import services.JsonUtil.fromJson

/**
  * Created by cleclair on 2017-09-20.
  */
case class Team_logo(size: String, url: String)
case class Team_logos(team_logo: Team_logo)
case class Roster_adds(coverage_type: String, coverage_value: String, value: String)
case class Manager(manager_id: String, nickname: String, guid: String, is_current_login: String, email: String, image_url: String)
case class Managers(manager: Manager)

case object League {

  def getLeaguesFromYahooResponse(json: String): Set[League] = {
    val leagueJson =  (Json.parse(json) \ "fantasy_content" \ "users" \ "0" \ "user").get.asInstanceOf[JsArray].value(1) \ "leagues"
    val test = leagueJson
    Set(fromJson[League](test.toString))
  }
}

case class League(team_key: String,
                           team_id: String,
                           name: String,
                           is_owned_by_current_login: String,
                           url: String,
                           team_logos: Team_logos,
                           waiver_priority: String,
                           number_of_moves: String,
                           number_of_trades: String,
                           roster_adds: Roster_adds,
                           clinched_playoffs: String,
                           league_scoring_type: String,
                           draft_position: String,
                           has_draft_grade: String,
                           managers: Managers
                         )
case object Team {

  def getTeamsFromYahooResponse(json: String): Set[Team] = {
    val teamJson =  (Json.parse(json) \ "fantasy_content" \ "users" \ "0" \ "user").get.asInstanceOf[JsArray].value(1) \ "teams"
    val rootJson = teamJson.asInstanceOf[JsDefined].value
    val teamCount = (rootJson \ "count").get.toString().toInt
    IndexedSeq.range(0, teamCount).map{(currentTeamNumber) =>
      val listOfJsonObjects = (teamJson.asInstanceOf[JsDefined].value \ s"${currentTeamNumber}" \ "team").asInstanceOf[JsDefined].value.asInstanceOf[JsArray].value(0).asInstanceOf[JsArray].value.toIndexedSeq
      fromJson[Team](listOfJsonObjects.collect{case js: JsObject => js}.foldLeft(Json.obj())(_ ++ _).toString())
    }.toSet
  }

}

case class Team(team_key: String,
                team_id: String,
                name: String,
                is_owned_by_current_login: Double,
                url: String,
                team_logos: List[Team_logos],
                waiver_priority: Double,
                number_of_moves: String,
                number_of_trades: String,
                roster_adds: Roster_adds,
                clinched_playoffs: Double,
                league_scoring_type: String,
                draft_position: Double,
                has_draft_grade: Double,
                managers: List[Managers]
               ){

  def leagueID(): String = team_key.split(".")(2)

}
