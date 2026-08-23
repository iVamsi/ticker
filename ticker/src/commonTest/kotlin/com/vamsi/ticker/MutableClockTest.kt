package com.vamsi.ticker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class MutableClockTest {

    @Test
    fun `now returns the instant the clock was created with`() {
        val start = Instant.parse("2026-01-01T00:00:00Z")

        val clock = MutableClock(start)

        assertEquals(start, clock.now())
    }

    @Test
    fun `advanceBy moves the clock forward by the given duration`() {
        val start = Instant.parse("2026-01-01T00:00:00Z")
        val clock = MutableClock(start)

        clock.advanceBy(30.seconds)

        assertEquals(start + 30.seconds, clock.now())
    }

    @Test
    fun `advanceBy accumulates across repeated calls`() {
        val start = Instant.parse("2026-01-01T00:00:00Z")
        val clock = MutableClock(start)

        clock.advanceBy(10.seconds)
        clock.advanceBy(20.seconds)

        assertEquals(start + 30.seconds, clock.now())
    }

    @Test
    fun `advanceBy zero duration is a no-op`() {
        val start = Instant.parse("2026-01-01T00:00:00Z")
        val clock = MutableClock(start)

        clock.advanceBy(Duration.ZERO)

        assertEquals(start, clock.now())
    }

    @Test
    fun `advanceBy supports sub-millisecond precision`() {
        val start = Instant.parse("2026-01-01T00:00:00Z")
        val clock = MutableClock(start)

        clock.advanceBy(500.microseconds)
        clock.advanceBy(250.nanoseconds)

        assertEquals(start + 500.microseconds + 250.nanoseconds, clock.now())
    }

    @Test
    fun `setTo moves the clock to an arbitrary instant`() {
        val clock = MutableClock(Instant.parse("2026-01-01T00:00:00Z"))
        val target = Instant.parse("2025-06-15T12:00:00Z")

        clock.setTo(target)

        assertEquals(target, clock.now())
    }

    @Test
    fun `advanceBy and setTo can be interleaved`() {
        val clock = MutableClock(Instant.parse("2026-01-01T00:00:00Z"))

        clock.advanceBy(10.seconds)
        assertEquals(Instant.parse("2026-01-01T00:00:10Z"), clock.now())

        clock.setTo(Instant.parse("2020-01-01T00:00:00Z"))
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), clock.now())

        clock.advanceBy(5.seconds)
        assertEquals(Instant.parse("2020-01-01T00:00:05Z"), clock.now())
    }

    @Test
    fun `advanceBy rejects negative durations with informative message`() {
        val clock = MutableClock(Instant.parse("2026-01-01T00:00:00Z"))

        val exception = assertFailsWith<IllegalArgumentException> {
            clock.advanceBy((-1).seconds)
        }

        assertTrue(exception.message?.contains("Use setTo() to move the clock backwards") == true)
    }

    @Test
    fun `toString includes current instant`() {
        val start = Instant.parse("2026-01-01T00:00:00Z")
        val clock = MutableClock(start)

        assertEquals("MutableClock(current=2026-01-01T00:00:00Z)", clock.toString())

        clock.advanceBy(10.seconds)
        assertEquals("MutableClock(current=2026-01-01T00:00:10Z)", clock.toString())
    }
}
