package com.atanana

import com.atanana.com.atanana.sitester.ClientMessage
import com.atanana.com.atanana.sitester.GameManager
import com.atanana.com.atanana.sitester.GameMiddleware
import com.atanana.com.atanana.sitester.ServerMessage
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.http.content.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(5)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val gameMiddleware = GameMiddleware(GameManager())

    routing {
        static("") {
            resources("static")
            defaultResource("static/index.html")
        }

        webSocket("/ws") {
            for (message in gameMiddleware.getInitialMessages()) {
                send(message)
            }

            gameMiddleware.messages
                .onEach(this::safeSend)
                .launchIn(this)

            while (true) {
                safe {
                    val frame = incoming.receive()
                    val data = (frame as Frame.Text).readText()
                    val message = Json.decodeFromString<ClientMessage>(data)
                    gameMiddleware.processMessage(message)
                }
            }
        }
    }
}

private suspend inline fun WebSocketSession.safe(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        try {
            val errorMessage = ServerMessage.Error(e.message ?: "Unknown error")
            send(errorMessage)
        } catch (e: Exception) {
            // do nothing
        }
    }
}

private suspend fun WebSocketSession.send(message: ServerMessage) {
    val data = Json.encodeToString(message)
    send(Frame.Text(data))
}

private suspend fun WebSocketSession.safeSend(message: ServerMessage) = safe { send(message) }