package v1.games

/**
  * Created by cleclair on 2017-09-20.
  */
case class Game(game_key: String,
                game_id: String,
                name: String,
                code: String,
                url: String,
                season: String,
                is_registration_over: String,
                is_game_over: String,
                is_offseason: String
               )
