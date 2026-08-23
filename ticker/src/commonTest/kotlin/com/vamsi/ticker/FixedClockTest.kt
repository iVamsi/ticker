package com.vamsi.ticker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class FixedClockTest {

    @Test
    fun `now always returns the instant the clock was created with`() {
        val instant = Instant.parse("2026-01-01T00:00:00Z")

        val clock = FixedClock(instant)

        assertEquals(instant, clock.now())
        assertEquals(instant, clock.now())
    }

    @Test
    fun `works with epoch and boundary instants`() {
        val epoch = Instant.fromEpochMilliseconds(0)
        assertEquals(epoch, FixedClock(epoch).now())

        val past = Instant.DISTANT_PAST
        assertEquals(past, FixedClock(past).now())

        val future = Instant.DISTANT_FUTURE
        assertEquals(future, FixedClock(future).now())
    }

    @Test
    fun `toString includes current instant`() {
        val instant = Instant.parse("2026-01-01T00:00:00Z")
        val clock = FixedClock(instant)

        assertEquals("FixedClock(instant=2026-01-01T00:00:00Z)", clock.toString())
    }
}
