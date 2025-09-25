# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## 2025-09-26

### Added

- Comprehensive XSS cleaning framework in `xss-clean-core`:
  - `ReflectionCleaner` with support for arrays, collections, maps, and bean properties/fields
  - `CleanerService` interface and strategies: `OwaspCleanerService`, `JsoupCleanerService`
  - Uses `UnaryOperator<String>` for type-safe sanitization
  - Circular reference protection and null-safety
  - Tests: `CleanerServiceTest` (all passing)

### Changed

- Refactor to reduce cognitive complexity by extracting helpers in `ReflectionCleaner`

### Fixed

- Remove accessibility bypasses; operate only on safe access paths
- Address generics wildcard capture with localized, safe casts

### Deprecated

### Removed

- Legacy `AppTest` replaced with `CleanerServiceTest`

### Security

- Add OWASP Java HTML Sanitizer and JSoup sanitization options

## 2025-09-25

### Added

- Initial multi-module setup: `xss-clean-core`, `xss-clean-spring`
- Build tooling, testing (JUnit 5), and documentation scaffolding

---

[Unreleased]: https://github.com/haiphamcoder/xss-clean/compare/v1.0.0...HEAD
