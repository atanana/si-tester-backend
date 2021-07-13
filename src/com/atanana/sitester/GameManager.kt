package com.atanana.com.atanana.sitester

class GameManager {

    var players = emptyList<Player>()
        private set

    var currentQueue = emptyList<String>()
        private set

    @Synchronized
    fun addPlayer(name: String): Boolean {
        return if (players.any { it.name == name }) {
            false
        } else {
            players = players + Player(name)
            true
        }
    }

    @Synchronized
    fun removePlayer(name: String) {
        players = players.filterNot { it.name == name }
        currentQueue = currentQueue - name
    }

    @Synchronized
    fun changeOwner(ownerName: String) {
        players = players.map { player ->
            player.copy(isOwner = player.name == ownerName)
        }
    }

    @Synchronized
    fun resetQueue() {
        currentQueue = emptyList()
    }

    @Synchronized
    fun onPlayerAction(name: String) {
        if (!currentQueue.contains(name)) {
            currentQueue = currentQueue + name
        }
    }

    @Synchronized
    fun resetGame() {
        players = emptyList()
        currentQueue = emptyList()
    }
}