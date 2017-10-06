package v1.users

import v1.games.Game
import v1.leagues.{League, Team}

/**
  * Created by cleclair on 2017-09-20.
  */
case class User(games: Set[Game] = Set(), leagues: Set[League] = Set(), teams: Set[Team] = Set()) {

  def leagueIDs(): Set[String] = teams.map(_.team_key.split(".")(2))


}
