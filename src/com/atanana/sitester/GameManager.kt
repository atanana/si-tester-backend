package com.atanana.com.atanana.sitester

class GameManager {

    private var playersMap = emptyMap<String, Player>()

    val players: List<Player>
        get() = playersMap.values.toList()

    var currentQueue = emptyList<String>()
        private set

    private var owner: String? = null

    @Synchronized
    fun addPlayer(name: String): Boolean {
        return if (playersMap.containsKey(name)) {
            false
        } else {
            playersMap = playersMap + (name to Player(name, isOwner = owner == name))
            true
        }
    }

    @Synchronized
    fun removePlayer(name: String) {
        playersMap = playersMap - name
        currentQueue = currentQueue - name
    }

    @Synchronized
    fun changeOwner(ownerName: String): Boolean {
        playersMap = playersMap.mapValues { (_, player) ->
            player.copy(isOwner = player.name == ownerName)
        }

        val result = ownerName != owner
        owner = ownerName
        return result
    }

    @Synchronized
    fun onPlayerAction(name: String): Boolean {
        val player = playersMap[name] ?: return false
        return if (player.isOwner) {
            if (currentQueue.isEmpty()) {
                false
            } else {
                currentQueue = emptyList()
                true
            }
        } else {
            if (!currentQueue.contains(name)) {
                currentQueue = currentQueue + name
                true
            } else {
                false
            }
        }
    }
}