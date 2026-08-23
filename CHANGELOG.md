# Changelog

## Unreleased

### Changed

- Library version on `main` is `0.1.1-SNAPSHOT`. Published coordinates remain `0.1.0` until the next release.
- `asClock` `toString()` uses the function name `asClock`, not `SchedulerClock`.
- README: thread-safety wording matches the JVM lost-update test; ABI check task is `checkLegacyAbi`; `asClock` documents millisecond grain.

### Added

- Windows CI job runs `mingwX64Test`.
- `asClock` test that `now()` equals `start` plus `TestCoroutineScheduler.currentTime` in milliseconds.

## 0.1.0 — 23 August 2026

Initial release of `io.github.ivamsi:ticker` and `io.github.ivamsi:ticker-coroutines`.

- `MutableClock` and `FixedClock` test doubles for `kotlin.time.Clock`.
- `TestCoroutineScheduler.asClock` adapter from coroutine virtual time to clock time.
- Kotlin Multiplatform targets: JVM, JS (Node.js), Wasm/JS (Node.js), iOS (x64, arm64, simulator arm64), macOS arm64, Linux x64, MinGW x64.
