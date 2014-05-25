package org.ocbkc.swift.snippet

import scala.xml.{Text, NodeSeq}
import org.ocbkc.swift.OCBKC.ConstitutionTypes._
import org.ocbkc.swift.global.Logging._
import net.liftweb.widgets.tablesorter.TableSorter
import net.liftweb.util.Helpers._
import org.ocbkc.swift.OCBKC.Constitution
import org.ocbkc.swift.OCBKC.scoring.PlayerScores
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Full
import org.ocbkc.swift.model._
import net.liftweb.mapper.By



class analyseFluencySessionsPlayer {
  val sesCoordLR = SesCoord.is // extract session coordinator object from session variable

  def sessionPlayerTable(ns:NodeSeq, playerID: Player):NodeSeq =
  {  log("sessionPlayerTable called")

    TableSorter("#analyseFluencySessionsPlayer")

    implicit val displayIfNone = "-"

    // create headers
    val header =
      bind(
        "top", chooseTemplate("top", "row", ns),
        "session"        -> <b>Session</b>,
        "score"         -> <b>Score</b>,
        "transTime"        -> <b>Translation Time</b>,
        "answerCor"   -> <b>Answer Correct</b>,
        "detailsLink"        -> <b>Details Link</b>

      )

//header
    // create data rows
    log("Fluency Score: "+ PlayerScores.fluencyScoreSample(playerID).toString())
   header ++
     (sesCoordLR.sessionsPlayedBy(playerID) zip PlayerScores.fluencyScoreSample(playerID)).flatMap
     {
       session =>

         bind( "top", chooseTemplate("top", "row", ns),
           "session"        -> { Text(session._1.id.toString) },
           "score"         -> { Text(session._2.toDouble.toString) },
           "transTime"        -> { Text(session._1.durationTranslation.get.toString) },
           "answerCor"   -> { Text(session._1.answerPlayerCorrect.get match { case true => "Yes" case false => "No"}) },
           "detailsLink"        -> { SHtml.link("analyseFluencySessionDetails.html?session_id="+session._1.id.toString,()=>(),Text("Details Link")) }

         )

     }
           /*
      Constitution.playersWithRelease(release_id).flatMap
      {  p =>
      {  val df = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")


      }
      }*/

  }

  def render(ns: NodeSeq): NodeSeq =
  {  S.param("player_id") match
  {  case Full(player_id) =>
    {  val msgStart = "Input player_ID: " + player_id
      println(Player.find(By(Player.id, player_id.toLong)))
      val player = Player.find(By(Player.id, player_id.toLong))
      if( !player.isEmpty)
      {  log( msgStart + " found!")
        val constiOption:Option[Constitution] = Constitution.getById(player.get.firstChosenConstitution.is)
        log(constiOption.toString)
//I get sometimes a None.Get. But the player exists, why is constiOption empty, if there are sessions played..?
        bind( "top", ns,
          "sessionPlayerTable" -> sessionPlayerTable(ns, player.get),
          "player" -> Text(player.get.firstName),
        "constName" -> Text(constiOption.get.constiId.toString),
        "release" -> Text(constiOption.get.currentVersionId)

        )
      } else
      {  log( "[POTENTIAL_BUG] " + msgStart + " not found.")
        S.redirectTo("../index")
      }
    }

    case _ =>
    {  log(" Parameter player_id missing in URL.")
      S.redirectTo("../index")
    }
  }
  }
//TODO by KOEN


}
