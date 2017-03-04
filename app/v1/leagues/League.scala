package v1.leagues

/**
  * Created by cleclair on 2017-01-22.
  */
case class League(league_key: String,
                  league_id: String,
                  name: String,
                  url: String,
                  draft_status: String,
                  num_teams: Double,
                  edit_key: String,
                  weekly_deadline: String,
                  league_update_timestamp: String,
                  scoring_type: String,
                  league_type: String,
                  renew: String,
                  renewed: String,
                  short_invitation_url: String,
                  allow_add_to_dl_extra_pos: Double,
                  is_pro_league: String,
                  is_cash_league: String,
                  current_week: Double,
                  start_week: String,
                  start_date: String,
                  end_week: String,
                  end_date: String,
                  game_code: String,
                  season: String)