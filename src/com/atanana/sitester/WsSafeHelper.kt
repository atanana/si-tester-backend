package com.atanana.com.atanana.sitester

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WsSafeHelper(private val session: WebSocketSession, application: Application) {
    private val log = application.environment.log

    suspend fun send(message: ServerMessage) {
        val data = Json.encodeToString(message)
        session.send(Frame.Text(data))
    }

    suspend fun safe(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            log.error(e.message, e)

            try {
                val errorMessage = ServerMessage.Error(e.message ?: "Unknown error")
                send(errorMessage)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    suspend fun safeSend(message: ServerMessage) = safe { send(message) }
}