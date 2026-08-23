package com.vamsi.ticker

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * A [Clock] test double that holds a mutable [Instant], allowing tests to control
 * and advance time deterministically.
 *
 * Thread-safe for concurrent calls to [advanceBy], [setTo], and [now].
 */
@OptIn(ExperimentalAtomicApi::class)
public class MutableClock(instant: Instant) : Clock {

    private val current = AtomicReference(instant)

    override fun now(): Instant = current.load()

    /**
     * Advances the clock forward by the given non-negative [duration].
     *
     * @throws IllegalArgumentException if [duration] is negative. Use [setTo] to move backwards.
     */
    public fun advanceBy(duration: Duration) {
        require(!duration.isNegative()) {
            "advanceBy requires a non-negative duration, but was $duration. Use setTo() to move the clock backwards."
        }
        while (true) {
            val previous = current.load()
            if (current.compareAndSet(previous, previous + duration)) return
        }
    }

    /**
     * Sets the clock to an arbitrary [instant], allowing both forward and backward jumps.
     */
    public fun setTo(instant: Instant) {
        current.store(instant)
    }

    override fun toString(): String = "MutableClock(current=${now()})"
}
