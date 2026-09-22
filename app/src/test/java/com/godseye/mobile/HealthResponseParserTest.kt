package com.godseye.mobile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthResponseParserTest {
    @Test
    fun `valid health response is healthy`() {
        val result = HealthResponseParser.parse(
            """{"app":"gods-eye-view","status":"ready"}"""
        )
        assertTrue(result is HealthParseResult.Healthy)
    }

    @Test
    fun `unknown fields are allowed`() {
        val result = HealthResponseParser.parse(
            """{"app":"gods-eye-view","status":"ready","version":"mobile-1","extra":"anything"}"""
        )
        assertTrue(result is HealthParseResult.Healthy)
    }

    @Test
    fun `malformed JSON is rejected`() = assertCategory(
        """{"app":"gods-eye-view",SECRET_RESPONSE_CONTENT""",
        HealthParseResult.Category.MALFORMED_JSON
    )

    @Test
    fun `array root is rejected`() = assertCategory(
        """["gods-eye-view","ready"]""",
        HealthParseResult.Category.MALFORMED_JSON
    )

    @Test
    fun `missing app is rejected`() = assertCategory(
        """{"status":"ready"}""",
        HealthParseResult.Category.MISSING_REQUIRED_FIELD
    )

    @Test
    fun `missing status is rejected`() = assertCategory(
        """{"app":"gods-eye-view"}""",
        HealthParseResult.Category.MISSING_REQUIRED_FIELD
    )

    @Test
    fun `numeric app is rejected`() = assertCategory(
        """{"app":123,"status":"ready"}""",
        HealthParseResult.Category.WRONG_FIELD_TYPE
    )

    @Test
    fun `boolean status is rejected`() = assertCategory(
        """{"app":"gods-eye-view","status":true}""",
        HealthParseResult.Category.WRONG_FIELD_TYPE
    )

    @Test
    fun `null app is rejected`() = assertCategory(
        """{"app":null,"status":"ready"}""",
        HealthParseResult.Category.WRONG_FIELD_TYPE
    )

    @Test
    fun `object status is rejected`() = assertCategory(
        """{"app":"gods-eye-view","status":{}}""",
        HealthParseResult.Category.WRONG_FIELD_TYPE
    )

    @Test
    fun `unexpected app value is rejected`() = assertCategory(
        """{"app":"SECRET_UNEXPECTED_APP","status":"ready"}""",
        HealthParseResult.Category.UNEXPECTED_VALUE
    )

    @Test
    fun `unexpected status value is rejected`() = assertCategory(
        """{"app":"gods-eye-view","status":"SECRET_UNEXPECTED_STATUS"}""",
        HealthParseResult.Category.UNEXPECTED_VALUE
    )

    private fun assertCategory(input: String, expected: HealthParseResult.Category) {
        val result = HealthResponseParser.parse(input)
        assertTrue("Expected Invalid but got $result", result is HealthParseResult.Invalid)
        result as HealthParseResult.Invalid
        assertEquals(expected, result.category)
    }
}
