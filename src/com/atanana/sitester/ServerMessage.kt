@file:Suppress("unused")

package com.atanana.com.atanana.sitester

import kotlinx.serialization.Serializable

@Serializable
sealed class ServerMessage(val messageType: String) {

    @Serializable
    class PlayersUpdate(val players: List<Player>) : ServerMessage("PLAYERS_UPDATE")

    @Serializable
    class QueueUpdate(val queue: List<String>) : ServerMessage("QUEUE_UPDATE")

    @Serializable
    class Error(val message: String) : ServerMessage("ERROR")
}
