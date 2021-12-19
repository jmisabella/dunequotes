
package models

import akka.actor._
import play.api.libs.json._
import play.api.libs.json.Json
import models.modules.QuoteService
import models.classes.State
import java.util.Calendar

object SimpleWebSocketActor {
  def props(clientActorRef: ActorRef) = Props(new SimpleWebSocketActor(clientActorRef))
}

class SimpleWebSocketActor(clientActorRef: ActorRef) extends Actor {
  val logger = play.api.Logger(getClass)
  logger.info(s"SimpleWebSocketActor class started")

  // this is where we receive json messages sent by the client,
  // and send them a json reply
  def receive = {
    case jsValue: JsValue =>
        val clientMessage = getMessage(jsValue)

        val rootPath: String = play.Environment.simple().rootPath().getAbsolutePath() match {
          case p if (p.endsWith(".")) => p.reverse.tail.reverse
          case p => p
        }

        val quotesConfigPath: String = rootPath + "conf/quotes.json"
        val historyConfigPath: String = rootPath + "conf/history.json"
        val historyLimit = 60
        val prevStateCheck: Either[String, State] = QuoteService.initialState(quotesConfigPath, historyConfigPath, historyLimit, Calendar.DATE)

        val (nextState, response): (Option[State], String) = prevStateCheck match {
          case Left(e) => (None, s"Error occurred initializing state: $e")
          case Right(s) => clientMessage.trim().toLowerCase() match {
            case "featured" => QuoteService.get.featuredQuote(s) match {
              case None => (None, "no featured quote")
              case Some(q) if (q.toString() == "") => (Some(s), "empty featured quote")
              case Some(q) => (Some(s), q.toString())
            }
            case "random" => QuoteService.get.randomQuote(s) match {
              case (s2, q) => (Some(s2), q.toString())
            }
          }
        }
        val (finalState, finalResponse): (Option[State], String) = nextState match {
          case None => (None, response)
          case Some(s) => QuoteService.nextState(s) match {
            case Left(e) => (None, e)
            case Right(s3) => (Some(s3), response)
          }
        }
        println("FINAL RESPONSE: " + finalResponse)
        val json: JsValue = Json.parse(s"""{"body": $finalResponse}""")

        // val json: JsValue = Json.parse(s"""{"body": "You said, ‘$clientMessage’"}""")
        clientActorRef ! (json)
  }

  // parse the "message" field from the json the client sends us
  def getMessage(json: JsValue): String = (json \ "message").as[String]

}

