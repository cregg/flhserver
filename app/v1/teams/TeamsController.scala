package v1.teams

import com.github.scribejava.core.model.{OAuth1AccessToken, Verb}
import models.{DraftPick, DraftSummary, Player}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Request}
import services.RedisService._
import services.YahooOauthService
import v1.YahooRoutes

class TeamsController extends Controller{

  implicit val draftSummaryReads = Json.reads[DraftSummary]
  implicit val jsStringReads = Json.reads[JsString]
  implicit val draftSummaryWrites = Json.writes[DraftSummary]

  def getToken(implicit request: Request[AnyContent]): OAuth1AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth1AccessToken(tokenString, redis.get(tokenString).get)
  }

  def get(id: String) = Action { implicit request =>
    val yahooResponse = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.playersFromTeamReplaceId.replaceAll(":id", s"363.l.63462.t.$id"), getToken)
    val jsonResponse = Json.parse(yahooResponse.getBody)
    val playersJson = (jsonResponse \ "fantasy_content" \ "team" \ 1 \ "players").as[JsObject].values.toIndexedSeq.filter(_.isInstanceOf[JsObject])
    val leagueId = "363.l.63462"
    val teamName = (jsonResponse \ "fantasy_content" \ "team" \ 0 \ 2 \ "name").asInstanceOf[JsDefined].value.asInstanceOf[JsString].value
    val players = playersJson.map((playerJson) =>
      Player(
        (playerJson \ "player" \ 0 \ 0 \ "player_key").as[String],
        (playerJson \ "player" \ 0 \ 2 \ "name" \ "full").as[String]
      )
    )

    val rankedPlayers = Json.parse(redis.get(leagueId).getOrElse("")).as[JsArray].value.map(_.as[Player])
    val draftPicksByTeam = Json.parse(redis.get(leagueId + "_draft").getOrElse("")).as[JsArray].value.map(_.as[DraftPick]).filter(_.team_key.substring(12).contains(id))
    val draftedPlayersIds = draftPicksByTeam.map(_.player_key)
    val playerIdDraftPickMap: Map[String, DraftPick] = (draftedPlayersIds zip draftPicksByTeam).toMap
    val drafterPlayersOnRosterIds: IndexedSeq[String] = players.filter(player => draftedPlayersIds.contains(player.id)).map(_.id)

    val rankedPlayersByRank: Seq[(Player, Int)] = rankedPlayers.zipWithIndex
    val finalPlayers = rankedPlayersByRank.filter(player => drafterPlayersOnRosterIds.contains(player._1.id))
        .map{
          playerRankTuple =>
          val playerId: Player = playerRankTuple._1
          playerRankTuple._1.copy(rank = playerRankTuple._2 + 1, draftPos = playerIdDraftPickMap(playerId.id).pick)
        }.toIndexedSeq
    val bestPickPlayer = finalPlayers.maxBy(player => player.draftPos - player.rank)
    val worstPickPlayer = finalPlayers.maxBy(player => player.rank - player.draftPos)
    val mostAccuratePlayer: Player = finalPlayers.minBy(player => (player.draftPos - player.rank).abs)
    val draftScore: Int = finalPlayers.foldLeft(0)((draftScore, player) => draftScore + (player.draftPos - player.rank))
    val summary = DraftSummary(
      corePicks = finalPlayers.sortBy(_.draftPos),
      bestPick = bestPickPlayer,
      worstPick = worstPickPlayer,
      mostAccurate = mostAccuratePlayer,
      score = draftScore,
      teamName = teamName
    )
    Ok(Json.toJson(summary))
  }

  def getTeams() = Action { implicit request =>
    val yahooRequest = new YahooOauthService().makeRequest(Verb.GET, YahooRoutes.usersTeams, getToken)
    val usersTeamResponse = Json.parse(yahooRequest.getBody)
    val teams = usersTeamResponse \ "team"
    Ok(teams.toString)
  }


}
