package com.vamsi.ticker.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

/**
 * Creates a [Clock] backed by this [TestCoroutineScheduler]'s virtual time.
 *
 * Each call to [Clock.now] computes the current instant as [start] plus the scheduler's
 * elapsed virtual time. Virtual time is exposed in whole milliseconds, so sub-millisecond
 * delays do not move this clock until a full millisecond has elapsed.
 *
 * @param start the baseline instant corresponding to virtual time 0.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun TestCoroutineScheduler.asClock(start: Instant): Clock {
    val scheduler = this
    return object : Clock {
        override fun now(): Instant = start + scheduler.currentTime.milliseconds

        override fun toString(): String = "asClock(start=$start, current=${now()})"
    }
}
