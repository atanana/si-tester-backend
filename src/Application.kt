package com.atanana

import com.atanana.com.atanana.sitester.*
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.http.content.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
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
            val helper = WsSafeHelper(this, this@module)

            for (message in gameMiddleware.getInitialMessages()) {
                helper.safeSend(message)
            }

            gameMiddleware.messages
                .onEach(helper::safeSend)
                .launchIn(this)

            var currentName: String? = null

            try {
                for (frame in incoming) {
                    helper.safe {
                        val data = (frame as Frame.Text).readText()
                        val message = Json.decodeFromString<ClientMessage>(data)

                        if (currentName == null) {
                            currentName = message.name
                        }

                        gameMiddleware.processMessage(message)
                    }
                }
            } finally {
                currentName?.let {
                    gameMiddleware.removePlayer(it)
                }
            }
        }
    }
}