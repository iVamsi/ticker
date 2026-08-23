# Ticker

Test-only test doubles for `kotlin.time.Clock`. Production code depends on `Clock`. Tests depend on Ticker.

## Language

**Ticker**:
A test-only library of test doubles for `kotlin.time.Clock`.
_Avoid_: production clock, app time source

**Clock time**:
The `Instant` a `Clock` reports.
_Avoid_: virtual time, scheduler time

**Virtual time**:
The elapsed-time offset held by `TestCoroutineScheduler`.
_Avoid_: clock time, wall clock

**MutableClock**:
A Clock with an independently owned Instant.
_Avoid_: fake clock, test clock

**FixedClock**:
A Clock with an immutable Instant fixed at construction.
_Avoid_: frozen clock, static clock, test clock

**asClock**:
The adapter from virtual time to clock time.
_Avoid_: SchedulerClock, MutableClock
