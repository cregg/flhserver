package models

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
