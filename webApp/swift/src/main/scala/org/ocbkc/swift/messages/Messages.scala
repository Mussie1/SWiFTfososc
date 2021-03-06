package org.ocbkc.swift.messages

import org.ocbkc.swift.model.Player
import org.ocbkc.swift.OCBKC._
import org.ocbkc.swift.OCBKC.ConstitutionTypes._
import net.liftweb.util.Mailer
import net.liftweb.util.Mailer._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import System.err.println
import org.ocbkc.swift.global._

case class Mail(to: Option[String], subject:String, body:String)

object MailMessage
{  /** Try to use the auxiliary methods (mailAllFollowersUpdate, etc.) instead of this one when possible.
     */
   def mailUpdate(const: Constitution, mail:Mail, players:List[Player]) =
   {  def sendupdatemail(follower:Player) =
      {  println("sendupdatemail called")
         println("   follower email = " + follower.email.get)
         Mailer.sendMail(From("cg@xs4all.nl"), Subject(mail.subject), To(follower.email.get), new PlainMailBodyType(mail.body))
         println("   mail sent!")
      }

      players.foreach( sendupdatemail )
   }
  
   def mailOtherFollowersUpdate(const: Constitution, mail:Mail, thisFollower: Player ) =
   {  mailUpdate(const, mail, const.followersAsPlayerObjs.filterNot(_ == thisFollower) )
   }

   def mailAllFollowersUpdate(const: Constitution, mail:Mail) =
   {  mailUpdate(const, mail, const.followersAsPlayerObjs )
   }

   private def sentenceOpening(const:Constitution) = "Constitution " + const.constiId

   def newPublication(const:Constitution):Mail =
Mail(
None,
sentenceOpening(const) + " has been edited by someone else...",
"""Constitution """ + const.constiId + """ has been edited by someone else. If you want to review the changes please visit this link:

""" + GlobalConstant.SWIFTURL  + "/constitution?id=" + const.constiId + """

""" + how2unfollow
)

   def newfollower(const:Constitution) =
Mail(
None,
sentenceOpening(const) + " has a new follower!"
,
"""Great news... constitution """ + const.constiId + """, a constitution which you already follow, has a new follower. Visit this link to see all followers:

""" + GlobalConstant.SWIFTURL  + "/constitution?id=" + const.constiId + """

""" + how2unfollow
)

   def lostfollower(const:Constitution) =
      Mail(
         None,
sentenceOpening(const) + " lost a follower :-(..."
         ,
sentenceOpening(const) + """ lost a follower. Visit the following link to see all followers:

""" + link2consti(const) + """

""" + how2unfollow
      )

   def newFluencyScore(const:Constitution, releaseId:VersionId) =
   {  Mail(
         None,
         sentenceOpening(const) + " received a fluency score!",
         "Release " + const.releaseIndex(releaseId) + " of " + sentenceOpening(const) + """, a constitution which you follow, received its first or an updated fluency score! You are a follower of this constitution, so quickly click the following link to compare the score of this release, with scores of other constitutions (is it better?), and the previous releases of this constitution (has it improved?):

""" + link2consti(const) + """

""" + how2unfollow // TODO &y2013.01.28.18:56:53& also include score numbers here.
      )
   }

   private val how2unfollow =
"""You are receiving this email because you are a follower of the mentioned constitution of the SWiFT game. If you want to unfollow the constitution, please visit the above link.
"""
   private def link2consti(const:Constitution) = GlobalConstant.SWIFTURL  + "/constitution?id=" + const.constiId
}

