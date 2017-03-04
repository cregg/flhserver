package v1.teams

import com.github.scribejava.apis.YahooApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.{OAuth1AccessToken, OAuthRequest, Verb}
import com.redis.RedisClient
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller, Request}
import v1.JSParsers._
import v1.YahooRoutes
import v1.drafts.{DraftPick, Player}



case class DraftSummary(corePicks: IndexedSeq[Player],
                        waiverPicks: IndexedSeq[Player] = IndexedSeq(),
                        bestPick: Player,
                        worstPick: Player,
                        mostAccurate: Player,
                        score: Int,
                        teamName: String = "") {
  val newLine = "\n"

  override def toString: String = s"Curent Draft Score: $score\n" +
    s"Total Draftees Still on team: ${corePicks.size}\n" +
    s"Average Draft Score: ${score.toDouble / corePicks.size.toDouble}\n" +
    s"Best Pick: ${bestPick.name}(${bestPick.draftPos - bestPick.rank})\n" +
    s"Worst Pick: ${worstPick.name}(${worstPick.draftPos - worstPick.rank})\n" +
    s"Most Accurate Pick: ${mostAccurate.name}(${(mostAccurate.draftPos - mostAccurate.rank).abs})\n\n" +
    s"-- Draft Summary --\n\n${corePicks.grouped(3).map(prettyPrintMultiPlayers).mkString(newLine + newLine)}"

  def prettyPrintMultiPlayers(initialSeq: IndexedSeq[Player]): String = {
    val players = if(initialSeq.size == 3) initialSeq else if(initialSeq.size == 2) initialSeq ++ IndexedSeq(initialSeq(1)) else initialSeq ++ initialSeq ++ initialSeq
    val result: String = f"Name: ${players(0).name}%-25s" + f"Name: ${players(1).name}%-25s" + f"Name: ${players(2).name}" +
    f"\nCurrent Rank: ${players(0).rank}%-25s" + f"Current Rank: ${players(1).rank}%-25s" + f"Current Rank: ${players(2).rank}" +
    f"\nDraft Position: ${players(0).draftPos}%-25s" + f"Draft Position: ${players(1).draftPos}%-25s" + f"Draft Position: ${players(2).draftPos}" +
    f"\nScore: ${players(0).draftPos - players(0).rank}%-25s" + f"Score: ${players(1).draftPos - players(1).rank}%-25s" + f"Score: ${players(2).draftPos - players(2).rank}%-25s"
    result
  }
}

class TeamsController extends Controller{

  implicit val draftSummaryReads = Json.reads[DraftSummary]
  implicit val jsStringReads = Json.reads[JsString]
  implicit val draftSummaryWrites = Json.writes[DraftSummary]

  val oAuthService = new ServiceBuilder()
    .apiKey("dj0yJmk9VjEyMzZleFZCMnAxJmQ9WVdrOVJWRnpWM0IwTlRnbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03ZA--")
    .apiSecret("5be45ab806ebd392bfd04100ef2c2140ed9afc03")
    .build(YahooApi.instance())

  val redis = new RedisClient("localhost", 6379)

  def getToken(implicit request: Request[AnyContent]): OAuth1AccessToken = {
    val tokenString = request.headers.get("Authentication").getOrElse("")
    new OAuth1AccessToken(tokenString, redis.get(tokenString).get)
  }

  def get(id: String) = Action { implicit request =>
    val yahooRequest = new OAuthRequest(Verb.GET, YahooRoutes.playersFromTeamReplaceId.replaceAll(":id", s"363.l.63462.t.$id"), oAuthService)
    oAuthService.signRequest(getToken, yahooRequest)
    val yahooResponse = yahooRequest.send()
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
    val yahooRequest = new OAuthRequest(Verb.GET, YahooRoutes.usersTeams, oAuthService)
    oAuthService.signRequest(getToken, yahooRequest)
    val usersTeamResponse = Json.parse(yahooRequest.send().getBody)
    val teams = usersTeamResponse \ "team"
    Ok(teams.toString)
  }


}
