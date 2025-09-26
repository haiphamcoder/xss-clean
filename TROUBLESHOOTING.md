# Troubleshooting Guide - XSS Clean

## 🚨 Common Issues và Solutions

### 1. UnsatisfiedDependencyException với @NoXss

**Lỗi:**

```text
org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'io.github.haiphamcoder.xss.annotation.NoXssValidator': 
Unsatisfied dependency expressed through constructor parameter 0: 
No qualifying bean of type 'io.github.haiphamcoder.xss.CleanerService' available
```

**Nguyên nhân:**

- `NoXssValidator` cần `CleanerService` bean nhưng không tìm thấy
- Auto-configuration chưa được kích hoạt đúng cách

**Giải pháp:**

1. **Đảm bảo có dependency đúng:**

    ```xml
    <dependency>
        <groupId>io.github.haiphamcoder</groupId>
        <artifactId>xss-clean-spring</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```

2. **Kiểm tra cấu hình:**

    ```yaml
    # application.yml
    xss:
    enabled: true  # Đảm bảo enabled = true
    ```

3. **Kiểm tra @EnableAutoConfiguration:**

    ```java
    @SpringBootApplication
    @EnableAutoConfiguration  // Đảm bảo có annotation này
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```

4. **Kiểm tra version Spring Boot:**

    ```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.0</version>  <!-- Tối thiểu 3.4.0 -->
    </parent>
    ```

### 2. XSS Cleaning không hoạt động

**Triệu chứng:**

- Dữ liệu XSS không được làm sạch
- Script tags vẫn còn trong output

**Giải pháp:**

1. **Kiểm tra cấu hình:**

    ```yaml
    xss:
    enabled: true
    strategy: owasp  # hoặc "jsoup"
    log-violation: true  # Để debug
    ```

2. **Kiểm tra RequestBodySanitizerAdvice:**

    ```java
    @RestController
    public class TestController {
        
        @PostMapping("/test")
        public ResponseEntity<String> test(@RequestBody String input) {
            // Input đã được làm sạch tự động
            return ResponseEntity.ok(input);
        }
    }
    ```

3. **Kiểm tra log:**

    ```yaml
    logging:
    level:
        io.github.haiphamcoder.xss: DEBUG
    ```

### 3. Validation không hoạt động

**Triệu chứng:**

- `@NoXss` annotation không validate
- Constraint violations không được báo

**Giải pháp:**

1. **Đảm bảo có validation dependency:**

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    ```

2. **Sử dụng @Valid:**

    ```java
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        return ResponseEntity.ok(user);
    }
    ```

3. **Kiểm tra annotation:**

```java
public class User {
    @NoXss(message = "Tên không được chứa mã độc hại")
    private String name;
}
```

### 4. Performance Issues

**Triệu chứng:**

- Ứng dụng chậm khi xử lý dữ liệu lớn
- Memory usage cao

**Giải pháp:**

1. **Sử dụng JSoup cho performance tốt hơn:**

    ```yaml
    xss:
    strategy: jsoup
    ```

2. **Cấu hình tùy chỉnh:**

    ```java
    @Bean
    @Primary
    public CleanerService performanceCleaner() {
        return new JsoupCleanerService(Safelist.none());
    }
    ```

3. **Disable XSS cleaning cho một số endpoint:**

    ```java
    @PostMapping("/public-api")
    @NoXss  // Chỉ validate, không clean
    public ResponseEntity<String> publicApi(@RequestBody String data) {
        return ResponseEntity.ok(data);
    }
    ```

### 5. Circular Reference Issues

**Lỗi:**

```text
StackOverflowError trong ReflectionCleaner
```

**Giải pháp:**

1. **Sử dụng @NoXss để skip:**

    ```java
    public class User {
        @NoXss
        private String name;
        
        private User parent;  // Sẽ được skip để tránh circular reference
    }
    ```

2. **Cấu hình depth limit:**

    ```java
    @Bean
    public CleanerService safeCleaner() {
        // ReflectionCleaner tự động xử lý circular references
        return new OwaspCleanerService();
    }
    ```

### 6. Configuration không được load

**Triệu chứng:**

- Properties không được đọc
- Default values được sử dụng

**Giải pháp:**

1. **Kiểm tra file cấu hình:**

    ```yaml
    # application.yml (không phải application.yaml)
    xss:
    enabled: true
    strategy: owasp
    ```

2. **Kiểm tra profile:**

    ```yaml
    spring:
    profiles:
        active: dev
    ```

3. **Sử dụng @ConfigurationProperties:**

    ```java
    @ConfigurationProperties(prefix = "xss")
    @Component
    public class XssProperties {
        private boolean enabled = true;
        private String strategy = "owasp";
        // getters and setters
    }
    ```

## 🔍 Debugging Tips

### 1. Enable Debug Logging

```yaml
logging:
  level:
    io.github.haiphamcoder.xss: DEBUG
    org.springframework.boot.autoconfigure: DEBUG
```

### 2. Kiểm tra Bean Creation

```java
@Component
public class XssDebugComponent {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @PostConstruct
    public void debugBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            if (beanName.contains("xss") || beanName.contains("Xss")) {
                System.out.println("XSS Bean: " + beanName);
            }
        }
    }
}
```

### 3. Test XSS Protection

```java
@RestController
public class DebugController {
    
    @Autowired
    private CleanerService cleanerService;
    
    @PostMapping("/debug-xss")
    public ResponseEntity<Map<String, String>> debugXss(@RequestBody String input) {
        Map<String, String> result = new HashMap<>();
        result.put("original", input);
        result.put("cleaned", cleanerService.clean(input));
        result.put("hasXss", !input.equals(cleanerService.clean(input)));
        return ResponseEntity.ok(result);
    }
}
```

## 📊 Health Check

### 1. Custom Health Indicator

```java
@Component
public class XssHealthIndicator implements HealthIndicator {
    
    @Autowired
    private CleanerService cleanerService;
    
    @Override
    public Health health() {
        try {
            String test = cleanerService.clean("<script>test</script>");
            return Health.up()
                .withDetail("xss-clean", "Hoạt động bình thường")
                .withDetail("test-result", test)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("xss-clean", "Lỗi: " + e.getMessage())
                .build();
        }
    }
}
```

### 2. Metrics

```java
@Component
public class XssMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter xssDetectedCounter;
    
    public XssMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.xssDetectedCounter = Counter.builder("xss.detected")
            .description("Số lần phát hiện XSS")
            .register(meterRegistry);
    }
    
    public void recordXssDetection() {
        xssDetectedCounter.increment();
    }
}
```

## 🆘 Khi Nào Cần Hỗ Trợ

Nếu bạn gặp vấn đề không có trong guide này:

1. **Kiểm tra version:**
   - XSS Clean: 1.0.0
   - Spring Boot: 3.4.0+
   - Java: 17+

2. **Thu thập thông tin:**
   - Stack trace đầy đủ
   - Cấu hình application.yml
   - Code example gây lỗi
   - Log output

3. **Tạo issue trên GitHub:**
   - <https://github.com/haiphamcoder/xss-clean/issues>
   - Mô tả chi tiết vấn đề
   - Attach log files nếu cần

4. **Liên hệ trực tiếp:**
   - Email: <ngochai285nd@gmail.com>
   - Subject: [XSS Clean] - Mô tả vấn đề

## 📚 Tài Liệu Tham Khảo

- [Hướng Dẫn Sử Dụng Chi Tiết](GUIDE.md)
- [Migration Guide](MIGRATION_GUIDE.md)
- [README](README.md)
- [Spring Boot Validation](https://spring.io/guides/gs/validating-form-input/)
- [OWASP XSS Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html)

---

***Chúc bạn sử dụng XSS Clean thành công! 🚀***
