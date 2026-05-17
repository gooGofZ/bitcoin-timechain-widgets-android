package com.googof.bitcointimechainwidgets

import com.googof.bitcointimechainwidgets.util.formatIsoDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatIsoDateTest {

    @Test
    fun `empty string returns empty string`() {
        assertEquals("", formatIsoDate("", "d MMM yyyy"))
    }

    @Test
    fun `ISO with milliseconds parses correctly`() {
        assertEquals("1 Jan 2028", formatIsoDate("2028-01-01T12:00:00.000Z", "d MMM yyyy"))
    }

    @Test
    fun `ISO without milliseconds parses correctly`() {
        assertEquals("1 Jan 2028", formatIsoDate("2028-01-01T12:00:00Z", "d MMM yyyy"))
    }

    @Test
    fun `date only format parses correctly`() {
        assertEquals("15 Jun 2025", formatIsoDate("2025-06-15", "d MMM yyyy"))
    }

    @Test
    fun `dd-MM-yyyy format parses correctly`() {
        assertEquals("20 Mar 2024", formatIsoDate("20/03/2024", "d MMM yyyy"))
    }

    @Test
    fun `MM-dd-yyyy format parses correctly`() {
        assertEquals("20 Mar 2024", formatIsoDate("03/20/2024", "d MMM yyyy"))
    }

    @Test
    fun `unrecognised format returns original string`() {
        val garbage = "not-a-date"
        assertEquals(garbage, formatIsoDate(garbage, "d MMM yyyy"))
    }

    @Test
    fun `output pattern is applied correctly for long format`() {
        assertEquals("1 January 2028 12:00", formatIsoDate("2028-01-01T12:00:00Z", "d MMMM yyyy HH:mm"))
    }

    @Test
    fun `different months format correctly`() {
        assertEquals("25 Dec 2025", formatIsoDate("2025-12-25T00:00:00Z", "d MMM yyyy"))
        assertEquals("4 Jul 2026", formatIsoDate("2026-07-04T00:00:00Z", "d MMM yyyy"))
    }
}
