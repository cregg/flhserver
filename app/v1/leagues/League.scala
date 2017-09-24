package v1.leagues

/**
  * Created by cleclair on 2017-09-20.
  */
case class Team_logo(size: String, url: String)
case class Team_logos(team_logo: Team_logo)
case class Roster_adds(coverage_type: String, coverage_value: String, value: String)
case class Manager(manager_id: String, nickname: String, guid: String, is_current_login: String, email: String, image_url: String)
case class Managers(manager: Manager)
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
