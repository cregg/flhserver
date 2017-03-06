package models

/**
  * Created by cleclair on 2017-03-05.
  */
case class Player(id: String, name: String, rank: Int = -1, draftPos: Int = -1) {

  override def toString: String = s"Name: $name\nCurrent Rank: $rank \nDraft Position: $draftPos\nScore: ${draftPos - rank}"

}
