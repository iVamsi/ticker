package com.vamsi.ticker

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class MutableClockConcurrencyTest {

    @Test
    fun `advanceBy loses no updates when called from many threads`() {
        val threads = 8
        val advancesPerThread = 1_000
        val start = Instant.parse("2026-01-01T00:00:00Z")
        val clock = MutableClock(start)

        val pool = Executors.newFixedThreadPool(threads)
        val ready = CountDownLatch(threads)
        val go = CountDownLatch(1)
        val done = CountDownLatch(threads)
        try {
            repeat(threads) {
                pool.execute {
                    ready.countDown()
                    go.await()
                    repeat(advancesPerThread) { clock.advanceBy(1.milliseconds) }
                    done.countDown()
                }
            }
            ready.await()
            go.countDown()
            check(done.await(30, TimeUnit.SECONDS)) { "threads did not finish" }
        } finally {
            pool.shutdown()
            pool.awaitTermination(5, TimeUnit.SECONDS)
        }

        assertEquals(start + (threads * advancesPerThread).milliseconds, clock.now())
    }
}
