package lila
package cli

import scalaz.effects._

import lila.parseIntOption
import lila.core.{ Global, CoreEnv }

object Main {

  def main(env: CoreEnv)(args: Array[String]): IO[String] = {

    def users = Users(env.user.userRepo, env.security.store)
    def games = Games(env)
    def i18n = I18n(env.i18n)
    def titivate = env.titivate
    def forum = Forum(env.forum)
    def infos = Infos(env)
    def wiki = Wiki(env.wiki)
    def search = Search(env.search)
    def teams = Teams(env.team.teamRepo, env.user.userRepo, env.team.api)

    args.toList match {
      case "average-elo" :: Nil               ⇒ infos.averageElo
      case "i18n-js-dump" :: Nil              ⇒ i18n.jsDump
      case "i18n-fix" :: Nil                  ⇒ i18n.fileFix
      case "i18n-fetch" :: from :: Nil        ⇒ i18n fetch from
      case "user-enable" :: uid :: Nil        ⇒ users enable uid
      case "user-disable" :: uid :: Nil       ⇒ users disable uid
      case "user-passwd" :: uid :: pwd :: Nil ⇒ users.passwd(uid, pwd)
      case "user-track" :: uid :: Nil         ⇒ users track uid
      case "user-roles" :: uid :: Nil         ⇒ users roles uid
      case "user-grant" :: uid :: roles       ⇒ users.grant(uid, roles)
      case "forum-denormalize" :: Nil         ⇒ forum.denormalize
      case "forum-typecheck" :: Nil           ⇒ forum.typecheck
      case "game-cleanup-next" :: Nil         ⇒ titivate.cleanupNext inject "Done"
      case "game-cleanup-unplayed" :: Nil     ⇒ titivate.cleanupUnplayed inject "Done"
      case "game-finish" :: Nil               ⇒ titivate.finishByClock inject "Done"
      case "game-per-day" :: Nil              ⇒ games.perDay(30)
      case "game-per-day" :: days :: Nil      ⇒ games.perDay(parseIntOption(days) err "days: Int")
      case "wiki-fetch" :: Nil                ⇒ wiki.fetch
      case "search-reset" :: Nil              ⇒ search.reset
      case "team-join" :: team :: users       ⇒ teams.join(team, users)
      case "team-quit" :: team :: users       ⇒ teams.quit(team, users)
      case "team-enable" :: uid :: Nil        ⇒ teams enable uid
      case "team-disable" :: uid :: Nil       ⇒ teams disable uid
      case "team-delete" :: uid :: Nil        ⇒ teams delete uid
      case _                                  ⇒ io("Unknown command: " + args.mkString(" "))
    }
  }
}
