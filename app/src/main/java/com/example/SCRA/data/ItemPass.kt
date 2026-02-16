package com.example.SCRA.data

import kotlinx.serialization.Serializable

@Serializable
data class ItemPass(
    val name: String = "",
    val value: String = "",
    val color: String = ""
)

