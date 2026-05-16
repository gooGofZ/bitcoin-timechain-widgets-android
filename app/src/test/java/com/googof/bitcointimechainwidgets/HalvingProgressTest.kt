package com.googof.bitcointimechainwidgets

import com.googof.bitcointimechainwidgets.data.calculateHalvingProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class HalvingProgressTest {

    @Test
    fun `start of cycle returns 0 percent`() {
        assertEquals(0.0, calculateHalvingProgress(210_000), 0.0001)
    }

    @Test
    fun `end of cycle returns 100 percent`() {
        assertEquals(100.0, calculateHalvingProgress(0), 0.0001)
    }

    @Test
    fun `halfway through cycle returns 50 percent`() {
        assertEquals(50.0, calculateHalvingProgress(105_000), 0.0001)
    }

    @Test
    fun `one block remaining returns near 100 percent`() {
        val expected = (209_999.0 / 210_000.0) * 100.0
        assertEquals(expected, calculateHalvingProgress(1), 0.0001)
    }

    @Test
    fun `quarter through cycle returns 75 percent`() {
        assertEquals(75.0, calculateHalvingProgress(52_500), 0.0001)
    }

    @Test
    fun `result is always between 0 and 100`() {
        listOf(0, 1, 52_500, 105_000, 209_999, 210_000).forEach { blocks ->
            val result = calculateHalvingProgress(blocks)
            assert(result in 0.0..100.0) { "Expected 0–100 but got $result for $blocks blocks" }
        }
    }
}
