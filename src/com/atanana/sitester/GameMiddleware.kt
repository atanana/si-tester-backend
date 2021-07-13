package com.atanana.com.atanana.sitester

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class GameMiddleware(private val gameManager: GameManager) {

    private val serverMessages = MutableSharedFlow<ServerMessage>()
    val messages: Flow<ServerMessage> = serverMessages

    suspend fun processMessage(message: ClientMessage) {
        when (message.type) {
            "ACTION" -> {
                gameManager.onPlayerAction(message.name)
                serverMessages.emit(ServerMessage.QueueUpdate(gameManager.currentQueue))
            }
            "INTRODUCE" -> {
                gameManager.addPlayer(message.name)
                serverMessages.emit(ServerMessage.PlayersUpdate(gameManager.players))
            }
        }
    }

    fun getInitialMessages(): List<ServerMessage> {
        return listOf(
            ServerMessage.PlayersUpdate(gameManager.players),
            ServerMessage.QueueUpdate(gameManager.currentQueue)
        )
    }

    suspend fun removePlayer(name: String) {
        gameManager.removePlayer(name)
        serverMessages.emit(ServerMessage.PlayersUpdate(gameManager.players))
        serverMessages.emit(ServerMessage.QueueUpdate(gameManager.currentQueue))
    }
}