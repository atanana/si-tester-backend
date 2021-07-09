package com.atanana.com.atanana.sitester

import kotlinx.serialization.Serializable

@Serializable
data class Player(val name: String, val isOwner: Boolean = false)
