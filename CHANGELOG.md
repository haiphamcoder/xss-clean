# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2025-09-26

### Added

- **Maven Central Deployment Ready**: Version 1.0.0 release
- **Enhanced Test Coverage**: 
  - Core module: 11 comprehensive test cases
  - Spring module: 8 integration test cases
- **Spring Boot Auto-Configuration**:
  - `XssAutoConfiguration` with proper ordering
  - `RequestBodySanitizerAdvice` bean registration
  - `XssFilter` for request parameter sanitization
- **Comprehensive Documentation**:
  - Detailed README with usage examples
  - Configuration guide for Spring Boot
  - Security considerations and best practices
- **Build System Improvements**:
  - Maven Central publishing configuration
  - GPG signing setup
  - Javadoc generation
  - Sources and javadoc JARs

### Changed

- **Version Management**: Changed from SNAPSHOT to release version 1.0.0
- **Spring Integration**: Improved auto-configuration with proper bean ordering
- **Documentation**: Complete rewrite of README with examples and guides

### Fixed

- **Spring Boot Integration**: Fixed missing bean registrations
- **Test Coverage**: Added comprehensive test scenarios for all features
- **Build Configuration**: Proper Maven Central publishing setup

### Security

- **XSS Protection**: OWASP Java HTML Sanitizer and JSoup integration
- **Input Validation**: `@NoXss` annotation for validation
- **Request Sanitization**: Automatic sanitization of request parameters and JSON bodies

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
[1.0.0]: https://github.com/haiphamcoder/xss-clean/compare/v0.1.0...v1.0.0
