package com.example.SCRA.data

import kotlinx.serialization.Serializable

@Serializable
data class ScraList (
    var typeCode:    String,
    var code:       String,
)
