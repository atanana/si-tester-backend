package com.atanana.com.atanana.sitester

class GameManager {

    var players = emptyList<Player>()
        private set

    var currentQueue = emptyList<String>()
        private set

    fun addPlayer(name: String): Boolean {
        return if (players.any { it.name == name }) {
            false
        } else {
            players = players + Player(name)
            true
        }
    }

    fun changeOwner(ownerName: String) {
        players = players.map { player ->
            player.copy(isOwner = player.name == ownerName)
        }
    }

    fun resetQueue() {
        currentQueue = emptyList()
    }

    fun onPlayerAction(name: String) {
        if (!currentQueue.contains(name)) {
            currentQueue = currentQueue + name
        }
    }

    fun resetGame() {
        players = emptyList()
        currentQueue = emptyList()
    }
}