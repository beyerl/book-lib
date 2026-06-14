package com.lspace.booklib.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class WorkDto(
    @SerialName("title") val title: String? = null,
    // The OpenLibrary "description" field is sometimes a plain string and
    // sometimes an object of the form {"type": "...", "value": "..."}.
    @SerialName("description") val description: JsonElement? = null,
) {
    fun descriptionText(): String? = when (val d = description) {
        is JsonPrimitive -> if (d.isString) d.content else null
        is JsonObject -> (d["value"] as? JsonPrimitive)?.jsonPrimitive?.content
        else -> null
    }
}
