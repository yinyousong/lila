package controllers

import lila._
import views._

import play.api.mvc._
import play.api.mvc.Results._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Akka

import scalaz.effects._

object Main extends LilaController {

  private lazy val runCommand = lila.cli.Main.main(env) _

  def websocket = WebSocket.async[JsValue] { implicit req ⇒
    implicit val ctx = reqToCtx(req)
    env.site.socket.join(
      uidOption = get("sri"),
      username = ctx.me map (_.username),
      flag = get("flag")
    )
  }

  def cli = OpenBody { implicit ctx ⇒
    implicit val req = ctx.body
    IOResult {
      Form(single(
        "c" -> nonEmptyText
      )).bindFromRequest.fold(
        err ⇒ putStrLn("bad command") inject BadRequest(),
        command ⇒ for {
          _ ← putStrLn(command)
          res ← runCommand(command.split(" "))
          _ ← putStrLn(res)
        } yield Ok(res)
      )
    }
  }

  def captchaCheck(id: String) = Open { implicit ctx ⇒
    Ok(env.site.captcha get id valid ~get("solution") fold (1, 0))
  }
}
