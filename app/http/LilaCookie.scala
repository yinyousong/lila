package lila
package http

import play.api.mvc.{ Cookie, Session, RequestHeader }

import ornicar.scalalib.Random

object LilaCookie {

  private val domainRegex = """^.+(\.[^\.]+\.[^\.]+)$""".r

  private def domain(req: RequestHeader): String =
    domainRegex.replaceAllIn(req.domain, _ group 1)

  val sessionId = "sid"

  def makeSessionId(implicit req: RequestHeader) = session(sessionId, Random nextString 8)

  def session(name: String, value: String)(implicit req: RequestHeader): Cookie = withSession { session =>
    session + (name -> value)
  }

  def withSession(op: Session ⇒ Session)(implicit req: RequestHeader): Cookie = cookie(
    Session.COOKIE_NAME,
    Session.encode(Session.serialize(op(req.session)))
  )

  def cookie(name: String, value: String, maxAge: Option[Int] = None)(implicit req: RequestHeader): Cookie = Cookie(
    name,
    value,
    maxAge | Session.maxAge,
    "/",
    domain(req).some,
    Session.secure,
    Session.httpOnly)
}
