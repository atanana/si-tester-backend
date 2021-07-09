package com.atanana.com.atanana.sitester

import kotlinx.serialization.Serializable

@Serializable
data class ClientMessage(val type: String, val name: String)
