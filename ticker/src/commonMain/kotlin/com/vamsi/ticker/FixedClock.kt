package com.vamsi.ticker

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * A [Clock] test double that always returns a constant, unchanging [Instant].
 *
 * Useful in tests where time should remain frozen at a specific moment.
 */
public class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant

    override fun toString(): String = "FixedClock(instant=$instant)"
}
