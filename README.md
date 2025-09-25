# XSS Clean

Utilities to help sanitize and validate untrusted input to reduce XSS risk.

## Modules

- `xss-clean-core`: core utilities independent of frameworks
- `xss-clean-spring`: Spring Boot integration and auto-configuration

## Requirements

- Java 17+
- Maven 3.9.5+

## Build and Test

```bash
mvn clean package
```

Run a single module:

```bash
mvn -pl xss-clean-core -am clean test
```

Install to local repo:

```bash
mvn clean install
```

## Use in Your Project

Add the dependency you need.

Core library:

```xml
<dependency>
  <groupId>io.github.haiphamcoder</groupId>
  <artifactId>xss-clean-core</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Spring Boot starter:

```xml
<dependency>
  <groupId>io.github.haiphamcoder</groupId>
  <artifactId>xss-clean-spring</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Note: When using `-SNAPSHOT` versions, ensure your repository settings allow snapshots or build locally first (`mvn install`).

## Version Matrix

- Java: 17
- Spring Boot (for `xss-clean-spring`): 3.5.x

## Contributing

1. Fork and create a feature branch
2. Write tests
3. `mvn clean package`
4. Open a PR

## License

MIT — see the `LICENSE` file for details.
