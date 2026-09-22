package com.godseye.mobile

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

internal sealed class HealthParseResult {
    data object Healthy : HealthParseResult()

    data class Invalid(val category: Category) : HealthParseResult()

    enum class Category {
        MALFORMED_JSON,
        MISSING_REQUIRED_FIELD,
        WRONG_FIELD_TYPE,
        UNEXPECTED_VALUE
    }
}

internal object HealthResponseParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = false
    }

    fun parse(response: String): HealthParseResult {
        val root = try {
            json.parseToJsonElement(response)
        } catch (_: Exception) {
            return HealthParseResult.Invalid(HealthParseResult.Category.MALFORMED_JSON)
        }

        val obj: JsonObject = try {
            root.jsonObject
        } catch (_: IllegalArgumentException) {
            return HealthParseResult.Invalid(HealthParseResult.Category.MALFORMED_JSON)
        }

        val appElement = obj["app"]
            ?: return HealthParseResult.Invalid(HealthParseResult.Category.MISSING_REQUIRED_FIELD)
        val statusElement = obj["status"]
            ?: return HealthParseResult.Invalid(HealthParseResult.Category.MISSING_REQUIRED_FIELD)

        val app = (appElement as? JsonPrimitive)
            ?.takeIf { it.isString }
            ?.content
            ?: return HealthParseResult.Invalid(HealthParseResult.Category.WRONG_FIELD_TYPE)

        val status = (statusElement as? JsonPrimitive)
            ?.takeIf { it.isString }
            ?.content
            ?: return HealthParseResult.Invalid(HealthParseResult.Category.WRONG_FIELD_TYPE)

        if (app != "gods-eye-view" || status != "ready") {
            return HealthParseResult.Invalid(HealthParseResult.Category.UNEXPECTED_VALUE)
        }

        return HealthParseResult.Healthy
    }
}
