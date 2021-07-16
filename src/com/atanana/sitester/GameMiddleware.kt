package com.atanana.com.atanana.sitester

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class GameMiddleware(private val gameManager: GameManager) {

    private val serverMessages = MutableSharedFlow<ServerMessage>()
    val messages: Flow<ServerMessage> = serverMessages

    suspend fun processMessage(message: ClientMessage) {
        when (message.type) {
            "ACTION" -> {
                if (gameManager.onPlayerAction(message.name)) {
                    updateQueue()
                }
            }
            "INTRODUCE" -> {
                if (gameManager.addPlayer(message.name)) {
                    updatePlayers()
                }
            }
            "MAKE_OWNER" -> {
                if (gameManager.changeOwner(message.name)) {
                    updatePlayers()
                }
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
        updatePlayers()
        updateQueue()
    }

    private suspend fun updateQueue() {
        serverMessages.emit(ServerMessage.QueueUpdate(gameManager.currentQueue))
    }

    private suspend fun updatePlayers() {
        serverMessages.emit(ServerMessage.PlayersUpdate(gameManager.players))
    }
}