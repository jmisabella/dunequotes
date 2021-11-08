
package models

import akka.actor._
import play.api.libs.json._
import play.api.libs.json.Json

object SimpleWebSocketActor {
    def props(clientActorRef: ActorRef) = Props(new SimpleWebSocketActor(clientActorRef))
}

class SimpleWebSocketActor(clientActorRef: ActorRef) extends Actor {
    val logger = play.api.Logger(getClass)

    // this is where we receive json messages sent by the client,
    // and send them a json reply
    def receive = {
        case jsValue: JsValue =>
            val clientMessage = getMessage(jsValue)
            val json: JsValue = Json.parse(s"""{"body": "You said, ‘$clientMessage’"}""")
            clientActorRef ! (json)
    }

    // parse the "message" field from the json the client sends us
    def getMessage(json: JsValue): String = (json \ "message").as[String]

}

