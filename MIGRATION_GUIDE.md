# Hướng Dẫn Migration - XSS Clean v1.0.0

## 📋 Tổng Quan

Hướng dẫn này giúp bạn migrate từ các phiên bản cũ của XSS Clean lên phiên bản 1.0.0. Phiên bản 1.0.0 có nhiều thay đổi quan trọng về API và cấu hình.

## 🔄 Thay Đổi Chính

### 1. Version Management

**Trước (SNAPSHOT):**

```xml
<version>1.0.0-SNAPSHOT</version>
```

**Sau (Release):**

```xml
<version>1.0.0</version>
```

### 2. Spring Boot Auto-Configuration

**Trước:**

```java
@Configuration
@ConditionalOnProperty(prefix = "xss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XssProperties.class)
public class XssAutoConfiguration {
    // Cấu hình cũ
}
```

**Sau:**

```java
@Configuration
@ConditionalOnProperty(prefix = "xss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XssProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)  // Thêm mới
public class XssAutoConfiguration {
    // Cấu hình mới với RequestBodySanitizerAdvice bean
}
```

### 3. Bean Registration

**Thay đổi mới:**

- Thêm `RequestBodySanitizerAdvice` bean tự động
- Cải thiện ordering của auto-configuration
- Thêm validation support

## 🚀 Migration Steps

### Bước 1: Cập Nhật Dependencies

```xml
<!-- Cập nhật version -->
<dependency>
    <groupId>io.github.haiphamcoder</groupId>
    <artifactId>xss-clean-core</artifactId>
    <version>1.0.0</version>  <!-- Thay đổi từ SNAPSHOT -->
</dependency>

<dependency>
    <groupId>io.github.haiphamcoder</groupId>
    <artifactId>xss-clean-spring</artifactId>
    <version>1.0.0</version>  <!-- Thay đổi từ SNAPSHOT -->
</dependency>
```

### Bước 2: Cập Nhật Cấu Hình

**application.yml cũ:**

```yaml
xss:
  enabled: true
  strategy: owasp
```

**application.yml mới:**

```yaml
xss:
  enabled: true
  strategy: owasp
  throw-on-violation: false    # Thêm mới
  log-violation: true         # Thêm mới
  default-profile: simple     # Thêm mới
```

### Bước 3: Cập Nhật Code

#### 3.1. Loại Bỏ Manual Bean Registration

**Trước:**

```java
@Configuration
public class XssConfig {
    
    @Bean
    public RequestBodySanitizerAdvice requestBodySanitizerAdvice(CleanerService cleanerService) {
        return new RequestBodySanitizerAdvice(cleanerService);
    }
}
```

**Sau:**

```java
// Không cần manual registration nữa
// Bean sẽ được tự động tạo bởi XssAutoConfiguration
```

#### 3.2. Sử Dụng Annotation Validation

**Trước:**

```java
public class User {
    private String name;
    private String email;
    
    // Chỉ có manual cleaning
}
```

**Sau:**

```java
public class User {
    @NoXss(message = "Tên không được chứa mã độc hại")
    private String name;
    
    private String email; // Vẫn được làm sạch tự động
}
```

### Bước 4: Cập Nhật Test Cases

**Trước:**

```java
@Test
void testBasicCleaning() {
    CleanerService cleaner = new OwaspCleanerService();
    String result = cleaner.clean("<script>alert('XSS')</script>Hello");
    assertFalse(result.contains("<script>"));
}
```

**Sau:**

```java
@Test
void testBasicCleaning() {
    CleanerService cleaner = new OwaspCleanerService();
    String result = cleaner.clean("<script>alert('XSS')</script>Hello");
    assertFalse(result.contains("<script>"));
    assertTrue(result.contains("Hello"));
}

@Test
void testObjectCleaning() {
    TestUser user = new TestUser();
    user.name = "<script>alert('XSS')</script>John";
    
    CleanerService cleaner = new OwaspCleanerService();
    cleaner.cleanObject(user);
    
    assertFalse(user.name.contains("<script>"));
    assertTrue(user.name.contains("John"));
}
```

## 🔧 Cấu Hình Nâng Cao

### 1. Custom Profiles

**Thêm vào application.yml:**

```yaml
xss:
  profiles:
    strict:
      allowed-tags: "p,br"
      allowed-attributes: "class"
    lenient:
      allowed-tags: "p,br,b,i,a,img"
      allowed-attributes: "class,href,src"
    custom:
      allowed-tags: "div,span,p,br,strong,em"
      allowed-attributes: "class,id,style"
```

### 2. Custom Cleaner Service

```java
@Configuration
public class CustomXssConfig {
    
    @Bean
    @Primary
    public CleanerService customCleaner() {
        // Sử dụng JSoup với cấu hình tùy chỉnh
        Safelist customSafelist = Safelist.basic()
            .addTags("p", "br", "strong", "em")
            .addAttributes("p", "class");
        return new JsoupCleanerService(customSafelist);
    }
}
```

### 3. Monitoring và Logging

```yaml
logging:
  level:
    io.github.haiphamcoder.xss: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,metrics
```

## ⚠️ Breaking Changes

### 1. API Changes

- **Không có breaking changes** trong public API
- Tất cả existing code sẽ hoạt động bình thường

### 2. Configuration Changes

- Thêm các properties mới (optional)
- Cải thiện auto-configuration ordering
- Thêm validation support

### 3. Behavior Changes

- **Cải thiện**: Circular reference handling
- **Cải thiện**: Null safety
- **Cải thiện**: Performance optimization

## 🧪 Testing Migration

### 1. Unit Tests

```java
@SpringBootTest
class MigrationTest {
    
    @Autowired
    private CleanerService cleanerService;
    
    @Autowired
    private RequestBodySanitizerAdvice requestBodySanitizerAdvice;
    
    @Test
    void testAutoConfiguration() {
        assertNotNull(cleanerService);
        assertNotNull(requestBodySanitizerAdvice);
    }
    
    @Test
    void testBackwardCompatibility() {
        String input = "<script>alert('XSS')</script>Hello World";
        String result = cleanerService.clean(input);
        
        assertFalse(result.contains("<script>"));
        assertTrue(result.contains("Hello World"));
    }
}
```

### 2. Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testRequestBodySanitization() {
        TestUser user = new TestUser();
        user.name = "<script>alert('XSS')</script>John";
        user.email = "john@example.com";
        
        ResponseEntity<TestUser> response = restTemplate.postForEntity(
            "/api/users", user, TestUser.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().name.contains("<script>"));
    }
}
```

## 🚨 Troubleshooting

### 1. Common Issues

**Issue**: Bean không được tạo tự động

```java
// Solution: Kiểm tra @EnableAutoConfiguration
@SpringBootApplication
@EnableAutoConfiguration  // Đảm bảo có annotation này
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Issue**: Configuration không được load

```yaml
# Solution: Kiểm tra cấu hình
xss:
  enabled: true  # Đảm bảo enabled = true
```

**Issue**: Test failures

```java
// Solution: Cập nhật test expectations
@Test
void testUpdatedBehavior() {
    // Cập nhật assertions theo behavior mới
    String result = cleaner.clean("<script>alert('XSS')</script>Hello");
    assertFalse(result.contains("<script>"));
    assertTrue(result.contains("Hello"));
}
```

### 2. Performance Issues

```java
// Solution: Sử dụng JSoup cho performance tốt hơn
@Bean
public CleanerService performanceCleaner() {
    return new JsoupCleanerService(Safelist.none());
}
```

## 📊 Migration Checklist

- [ ] Cập nhật version từ SNAPSHOT sang 1.0.0
- [ ] Cập nhật application.yml với properties mới
- [ ] Loại bỏ manual bean registration (nếu có)
- [ ] Thêm @NoXss annotations (tùy chọn)
- [ ] Cập nhật test cases
- [ ] Chạy integration tests
- [ ] Kiểm tra performance
- [ ] Deploy và monitor

## 🎯 Kết Luận

Migration từ phiên bản cũ lên v1.0.0 rất đơn giản và không có breaking changes. Tất cả existing code sẽ hoạt động bình thường với các cải tiến mới.

### Lợi Ích Sau Migration

- ✅ Auto-configuration cải thiện
- ✅ Validation support
- ✅ Performance tối ưu
- ✅ Test coverage đầy đủ
- ✅ Documentation chi tiết

### Hỗ Trợ

Nếu gặp vấn đề trong quá trình migration, vui lòng:

1. Kiểm tra [Issues](https://github.com/haiphamcoder/xss-clean/issues)
2. Tạo issue mới với thông tin chi tiết
3. Liên hệ qua email: <ngochai285nd@gmail.com>

---

***Chúc bạn migration thành công! 🚀***
