# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.4] - 2025-09-26

### Added

- **Spring Boot Auto-Configuration Reliability**: Added `spring.factories` as backup mechanism for auto-configuration
- **Comprehensive Auto-Configuration Tests**: Added `AutoConfigurationTest` with `ApplicationContextRunner` for testing different scenarios
- **Spring Boot Integration Tests**: Added `SpringBootAutoConfigurationTest` to verify auto-configuration loading
- **Backward Compatibility**: Support for both Spring Boot 2.7+ (`.imports` file) and older versions (`spring.factories`)

### Fixed

- **Auto-Configuration Issues**: Fixed auto-configuration not working in some environments
- **Bean Creation Reliability**: Ensured all XSS beans are created correctly in different Spring Boot versions
- **Test Coverage**: Improved test coverage for auto-configuration scenarios

### Changed

- **Dual Auto-Configuration Support**: Both `META-INF/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and `META-INF/spring.factories` are now included
- **Test Structure**: Improved test organization with dedicated auto-configuration test classes

## [1.0.3] - 2025-09-26

### Fixed

- **Configuration Respect**: `@NoXss` annotation now properly respects `xss.enabled=false` configuration
- **Property Reading**: Use `@Value` annotation to read `xss.enabled` property directly
- **Validation Skip**: Skip validation completely when XSS is disabled
- **Test Coverage**: Added dedicated test for disabled XSS validation scenario

### Improved

- **Configuration Handling**: Better handling of configuration properties in production environments
- **Backward Compatibility**: Maintains full compatibility with existing code
- **Test Quality**: Enhanced test coverage with specific scenarios

## [1.0.2] - 2025-09-26

### Fixed

- **Critical Fix**: Improved `NoXssValidator` resilience for production environments
- **Dependency Injection**: Replaced constructor injection with `@Autowired ApplicationContext`
- **Fallback Mechanism**: Added automatic fallback to `OwaspCleanerService` when `CleanerService` bean not found
- **Null Safety**: Enhanced null safety checks in validation methods
- **Auto-Configuration**: Removed manual bean registration to prevent conflicts

### Improved

- **Error Handling**: Better error handling for missing Spring context
- **Production Stability**: More robust operation in complex Spring environments
- **Backward Compatibility**: Maintains full compatibility with existing code

## [1.0.1] - 2025-09-26

### Fixed

- **Critical Fix**: Resolved `UnsatisfiedDependencyException` when using `@NoXss` annotation
- **Spring Integration**: Added `NoXssValidator` bean registration in `XssAutoConfiguration`
- **Dependency Injection**: Fixed constructor dependency injection for validation components
- **Auto-Configuration**: Improved Spring Boot auto-configuration reliability

### Added

- **Comprehensive Documentation**: Added detailed Vietnamese documentation
  - `GUIDE.md`: Complete usage guide in Vietnamese
  - `MIGRATION_GUIDE.md`: Migration guide from previous versions
  - `TROUBLESHOOTING.md`: Comprehensive troubleshooting guide
- **Enhanced Testing**: Added test case for `NoXssValidator` bean creation
- **Better Error Handling**: Improved error messages and debugging information

### Improved

- **Documentation Quality**: Enhanced all documentation with practical examples
- **Developer Experience**: Better troubleshooting guides and migration support
- **Code Quality**: Improved test coverage and error handling

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

[Unreleased]: https://github.com/haiphamcoder/xss-clean/compare/v1.0.3...HEAD
[1.0.3]: https://github.com/haiphamcoder/xss-clean/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/haiphamcoder/xss-clean/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/haiphamcoder/xss-clean/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/haiphamcoder/xss-clean/compare/v0.1.0...v1.0.0
