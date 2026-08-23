package com.vamsi.ticker.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class SchedulerClockTest {

    private val start = Instant.parse("2026-01-01T00:00:00Z")

    @Test
    fun `clock reads the start instant before any virtual time passes`() = runTest {
        val clock = testScheduler.asClock(start)

        assertEquals(start, clock.now())
    }

    @Test
    fun `advanceTimeBy moves the clock`() = runTest {
        val clock = testScheduler.asClock(start)

        advanceTimeBy(30.minutes)

        assertEquals(start + 30.minutes, clock.now())
    }

    @Test
    fun `delay inside the test moves the clock`() = runTest {
        val clock = testScheduler.asClock(start)

        delay(5.minutes)

        assertEquals(start + 5.minutes, clock.now())
    }

    @Test
    fun `interleaving advanceTimeBy and delay accumulates correctly`() = runTest {
        val clock = testScheduler.asClock(start)

        advanceTimeBy(10.minutes)
        assertEquals(start + 10.minutes, clock.now())

        delay(15.minutes)
        assertEquals(start + 25.minutes, clock.now())

        advanceTimeBy(1.hours)
        assertEquals(start + 85.minutes, clock.now())
    }

    @Test
    fun `multiple clocks backed by the same scheduler advance together`() = runTest {
        val clock1 = testScheduler.asClock(start)
        val clock2 = testScheduler.asClock(start + 1.hours)

        advanceTimeBy(20.minutes)

        assertEquals(start + 20.minutes, clock1.now())
        assertEquals(start + 1.hours + 20.minutes, clock2.now())
    }

    @Test
    fun `toString includes start and current instant`() = runTest {
        val clock = testScheduler.asClock(start)
        assertEquals("SchedulerClock(start=2026-01-01T00:00:00Z, current=2026-01-01T00:00:00Z)", clock.toString())

        advanceTimeBy(10.minutes)
        assertEquals("SchedulerClock(start=2026-01-01T00:00:00Z, current=2026-01-01T00:10:00Z)", clock.toString())
    }
}
