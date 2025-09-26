# XSS Clean

A comprehensive Java library for sanitizing and validating untrusted input to prevent XSS (Cross-Site Scripting) attacks. Provides both standalone utilities and Spring Boot integration.

## Features

- **Multiple Sanitization Strategies**: OWASP Java HTML Sanitizer and JSoup support
- **Reflection-based Cleaning**: Automatically sanitize complex objects, collections, and arrays
- **Spring Boot Integration**: Auto-configuration, filters, and validation support
- **Circular Reference Protection**: Safe handling of circular object references
- **Type Safety**: Full generics support and null safety
- **High Performance**: Optimized for production use

## Modules

- **`xss-clean-core`**: Core sanitization utilities independent of frameworks
- **`xss-clean-spring`**: Spring Boot integration with auto-configuration, filters, and validation

## Requirements

- Java 17+
- Maven 3.9.5+
- Spring Boot 3.4+ (for Spring module)

## Quick Start

### Core Library Usage

```java
import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import io.github.haiphamcoder.xss.policy.JsoupCleanerService;

// Basic string sanitization
CleanerService cleaner = new OwaspCleanerService();
String clean = cleaner.clean("<script>alert('XSS')</script>Hello World");
// Result: "Hello World"

// Object sanitization
class User {
    public String name = "<script>alert('XSS')</script>John";
    public String email = "john@example.com";
}

User user = new User();
cleaner.cleanObject(user);
// user.name is now sanitized
```

### Spring Boot Integration

Add dependency:

```xml
<dependency>
  <groupId>io.github.haiphamcoder</groupId>
  <artifactId>xss-clean-spring</artifactId>
  <version>1.0.0</version>
</dependency>
```

Configure in `application.yml`:

```yaml
xss:
  enabled: true
  strategy: owasp  # or "jsoup"
  throw-on-violation: false
  log-violation: true
```

The library automatically:

- Sanitizes request parameters and headers via `XssFilter`
- Sanitizes JSON request bodies via `RequestBodySanitizerAdvice`
- Provides validation with `@NoXss` annotation

## Examples

### 1. Basic String Sanitization

```java
import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;

CleanerService cleaner = new OwaspCleanerService();

// Remove dangerous HTML
String input = "<script>alert('XSS')</script><p>Safe content</p>";
String output = cleaner.clean(input);
// Result: "<p>Safe content</p>"
```

### 2. Object Sanitization

```java
class BlogPost {
    public String title;
    public String content;
    public List<String> tags;
    public Map<String, String> metadata;
}

BlogPost post = new BlogPost();
post.title = "<script>alert('XSS')</script>My Blog Post";
post.content = "<img src=x onerror=alert(1)>Content here";
post.tags = Arrays.asList("<script>alert('XSS')</script>tech", "java");
post.metadata = Map.of("author", "<script>alert('XSS')</script>John");

cleaner.cleanObject(post);
// All string fields are now sanitized
```

### 3. Spring Boot Validation

```java
import io.github.haiphamcoder.xss.annotation.NoXss;
import jakarta.validation.Valid;

@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // XSS cleaning happens automatically
        return ResponseEntity.ok(user);
    }
}

class User {
    @NoXss
    private String name;
    
    private String email;
    
    // getters and setters
}
```

### 4. Custom Sanitization Strategies

```java
// OWASP with custom policy
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

PolicyFactory customPolicy = Sanitizers.FORMATTING
    .and(Sanitizers.LINKS)
    .and(Sanitizers.BLOCKS);
CleanerService owaspCleaner = new OwaspCleanerService(customPolicy);

// JSoup with custom safelist
import org.jsoup.safety.Safelist;

Safelist customSafelist = Safelist.basic();
CleanerService jsoupCleaner = new JsoupCleanerService(customSafelist);
```

## Configuration

### Spring Boot Properties

```yaml
xss:
  enabled: true                    # Enable/disable XSS cleaning
  strategy: owasp                  # "owasp" or "jsoup"
  throw-on-violation: false        # Throw exception on XSS detection
  log-violation: true             # Log XSS violations
  default-profile: simple         # Default cleaning profile
  profiles:                       # Custom profiles
    strict:
      allowed-tags: "p,br"
      allowed-attributes: "class"
    lenient:
      allowed-tags: "p,br,b,i,a,img"
      allowed-attributes: "class,href,src"
```

### Programmatic Configuration

```java
@Configuration
public class XssConfig {
    
    @Bean
    public CleanerService customCleaner() {
        // Your custom implementation
        return new CustomCleanerService();
    }
}
```

## Build and Test

```bash
# Build all modules
mvn clean package

# Run tests
mvn test

# Install to local repository
mvn clean install

# Run specific module tests
mvn -pl xss-clean-core -am clean test
```

## Dependencies

### Core Module

- OWASP Java HTML Sanitizer 20240325.1
- JSoup 1.21.2

### Spring Module

- Spring Boot 3.5.5
- Spring Boot Starter Web
- Spring Boot Starter Validation

## Security Considerations

- **Input Validation**: Always validate input before sanitization
- **Output Encoding**: Sanitization is not a replacement for proper output encoding
- **Content Security Policy**: Use CSP headers as an additional security layer
- **Regular Updates**: Keep dependencies updated for latest security patches

## Performance

- **Reflection Caching**: Reflection metadata is cached for better performance
- **Circular Reference Detection**: Uses `IdentityHashMap` for efficient cycle detection
- **Lazy Loading**: Components are loaded only when needed

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Ensure all tests pass (`mvn clean test`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Version Matrix

| Java | Spring Boot | xss-clean |
|------|-------------|-----------|
| 17   | 3.4+        | 1.0.0     |
| 17   | 3.5+        | 1.0.0     |

## License

MIT License - see the [LICENSE](LICENSE) file for details.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for detailed version history.
