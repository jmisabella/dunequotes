package controllers

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer
import models.SimpleWebSocketActor
import play.api.libs.json._

class WebSocketsController @Inject() (cc: ControllerComponents)(implicit system: ActorSystem)
extends AbstractController(cc)
{
    val logger = play.api.Logger(getClass)

    // call this to display index.scala.html
    def index = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.index())
    }

    // the WebSocket
    def ws = WebSocket.accept[JsValue, JsValue] { requestHeader =>
        ActorFlow.actorRef { actorRef =>
            SimpleWebSocketActor.props(actorRef)
        }
    }

}

