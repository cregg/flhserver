package models

import play.api.libs.json._
import services.RedisService._
import v1.JsonUtil._

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

object DraftSummary {

  def generateDraftSummary(jsonString: String, id: String): DraftSummary = {
    val jsonResponse = Json.parse(jsonString)
    val playersJson = (jsonResponse \ "fantasy_content" \ "team" \ 1 \ "players").as[JsObject].values.toIndexedSeq.filter(_.isInstanceOf[JsObject])
    val leagueId = "363.l.63462"
    val teamName = (jsonResponse \ "fantasy_content" \ "team" \ 0 \ 2 \ "name").asInstanceOf[JsDefined].value.asInstanceOf[JsString].value
    val players = playersJson.map((playerJson) =>
      Player(
        (playerJson \ "player" \ 0 \ 0 \ "player_key").as[String],
        (playerJson \ "player" \ 0 \ 2 \ "name" \ "full").as[String]
      )
    )
    val rankedPlayers: Seq[Player] = fromJson[Seq[Player]](redis.get(leagueId).getOrElse(""))
    val draftPicksByTeam: Seq[DraftPick] = fromJson[Seq[DraftPick]](redis.get(leagueId + "_draft").getOrElse("")).filter(_.team_key == s"363.l.63462.t.${id}")

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
    DraftSummary(
      corePicks = finalPlayers.sortBy(_.draftPos),
      bestPick = bestPickPlayer,
      worstPick = worstPickPlayer,
      mostAccurate = mostAccuratePlayer,
      score = draftScore,
      teamName = teamName
    )
  }

}
