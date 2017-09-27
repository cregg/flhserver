package v1

object YahooRoutes {

  val playersFromTeam = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.games%20where%20use_login%3D1%20and%20game_key%20in%20('nhl')&format=json"
  val playersFromLeague = "https://fantasysports.yahooapis.com/fantasy/v2/league/363.l.63462/players?sort=AR&format=json&count=25&start={start}"
  val playersFromLeagueSubId = "https://fantasysports.yahooapis.com/fantasy/v2/league/{id}/players?sort=AR&format=json&count=25&start={start}"
  val usersTeams = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.teams%20where%20game_key%3D%22352%22%20and%20use_login%3D1&format=json&diagnostics=true&callback="
  val gamesFromUser = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.games%20where%20use_login%3D1%20and%20game_key%20in%20('nhl')&format=json"
  val usersRoster = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.teams.roster%20where%20use_login%3D1%20and%20game_key%3D363&format=json&diagnostics=true"
  val draftResults = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.draftresults%20where%20league_key%3D'363.l.63462'&format=json&diagnostics=false"
  val playersFromTeamReplaceId = "https://fantasysports.yahooapis.com/fantasy/v2/team/:id/players?format=json"
  val usersGamesResourceYQL = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.games%20where%20game_key%20in%20('nhl')&format=json&diagnostics=true&callback="
  val usersLeaguesResourceYQL = "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20fantasysports.teams%20where%20use_login%3D1%20and%20game_key%20in%20({game_ids})&format=json"

//  val usersLeaguesResource = "https://fantasysports.yahooapis.com/fantasy/v2/users;use_login=1/games;game_keys=nhl/teams&format=json"

  val usersGamesResource = "https://fantasysports.yahooapis.com/fantasy/v2/users;use_login=1/games;is_available=1?format=json"
  val usersTeamsResource = "https://fantasysports.yahooapis.com/fantasy/v2/users;use_login=1/teams?format=json"
}
