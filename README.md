# Ticker

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3+-purple.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-0.1.0-red.svg)](https://central.sonatype.com/artifact/io.github.ivamsi/ticker/0.1.0)

Kotlin Multiplatform test doubles for `kotlin.time.Clock`.

Whenever you write code that deals with TTLs, token expiration, retry backoffs, or cache eviction, you need a deterministic way to control time in your tests. Ticker provides lightweight, thread-safe clock doubles designed specifically for this purpose.

```kotlin
val clock = MutableClock(Instant.parse("2026-01-01T00:00:00Z"))
val cache = MyCache(ttl = 5.minutes, clock = clock)

cache.put("key", "value")
clock.advanceBy(6.minutes)

assertNull(cache.get("key"))
```

## Why not just hand-roll a test clock?

Writing a quick fake clock in a couple lines usually introduces subtle bugs:

1. **Lost updates under concurrency:** A naive `instant += duration` implementation relies on a non-atomic read-modify-write. When multiple threads or concurrent test workers advance time simultaneously, updates get silently lost. `MutableClock` uses an `AtomicReference` compare-and-set loop, so concurrent `advanceBy`, `setTo`, and `now` calls do not drop updates. Lost-update tests run on the JVM.
2. **Desynchronization in coroutine tests:** When using `runTest`, calling `advanceTimeBy(1.hours)` advances virtual time on the test dispatcher, but leaves independent clock objects behind. This causes code checking `clock.now()` to see time standing still while delayed coroutines resume. The `ticker-coroutines` module seamlessly bridges `TestCoroutineScheduler` virtual time to `kotlin.time.Clock`.

## Installation

Add the dependencies to your test source set:

```kotlin
kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation("io.github.ivamsi:ticker:0.1.0")
            implementation("io.github.ivamsi:ticker-coroutines:0.1.0") // optional
        }
    }
}
```

## API Overview

| Class / Function | Purpose |
| :--- | :--- |
| `MutableClock(instant)` | Controllable clock supporting `advanceBy(duration)` and `setTo(instant)`. |
| `FixedClock(instant)` | Immutable clock that always returns a constant instant. |
| `TestCoroutineScheduler.asClock(start)` | Clock derived from coroutine virtual time, in whole milliseconds (`ticker-coroutines`). |

> **Tip:** `advanceBy` only accepts non-negative durations because stepping backward during an "advance" is almost always a test logic error. To jump backward or simulate time synchronization, use `setTo(instant)`.

### Using with `runTest`

`asClock` reads `TestCoroutineScheduler.currentTime` on every `now()`. Virtual time is whole milliseconds, so a sub-millisecond delay does not move the clock until a full millisecond elapses. `advanceTimeBy` and `delay` still keep clock time and virtual time in step:

```kotlin
@Test
fun `session expires after timeout`() = runTest {
    val clock = testScheduler.asClock(start = Instant.parse("2026-01-01T00:00:00Z"))
    val sessionManager = SessionManager(timeout = 15.minutes, clock = clock)

    sessionManager.login("user_123")
    advanceTimeBy(16.minutes)

    assertFalse(sessionManager.isSessionActive("user_123"))
}
```

## Supported Targets

Ticker supports all major Kotlin Multiplatform targets:
- **JVM / Android** (Android projects consume the JVM artifact)
- **JavaScript** (Node.js)
- **WebAssembly** (Wasm/JS Node.js)
- **Apple:** iOS (x64, arm64, simulator arm64), macOS (arm64)
- **Linux:** Linux x64
- **Windows:** MinGW x64

### Running iOS Simulator Tests Locally

Running iOS simulator tests requires an installed runtime and a device configured for your Xcode SDK. If Kotlin reports:
> *"Xcode does not support simulator tests for ios_simulator_arm64. Check that requested SDK is installed."*

You can verify your local simulator setup:

```bash
xcrun simctl list runtimes            # check if the required iOS runtime is installed
xcrun simctl list devices available   # check if an active device exists for that runtime
```

If the runtime is missing, download it via `xcodebuild -downloadPlatform iOS`. If the runtime is installed but no devices exist, create one:

```bash
xcrun simctl create "iPhone 16" \
  com.apple.CoreSimulator.SimDeviceType.iPhone-16 \
  com.apple.CoreSimulator.SimRuntime.iOS-26-5
```

## Dependencies & Safety

- **Zero extra dependencies:** `:ticker` depends solely on the Kotlin standard library. `:ticker-coroutines` adds only `kotlinx-coroutines-test`.
- **Internal Atomics:** `MutableClock` uses Kotlin's `kotlin.concurrent.atomics` internally without leaking experimental opt-ins into your consumer code.
- **Strict ABI Validation:** Both artifacts enforce Kotlin Explicit API mode and ABI dumps. CI runs `./gradlew checkLegacyAbi` so a public-API change has to update the dump files.

## License

```text
Copyright 2026 Vamsi Vaddavalli

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0
```

