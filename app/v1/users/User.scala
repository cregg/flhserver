package v1.users

import v1.games.Game
import v1.leagues.League

/**
  * Created by cleclair on 2017-09-20.
  */
case class User(games: Set[Game] = Set(), leagues: Set[League] = Set())
