# XSS Clean v1.0.0

## 📖 Giới Thiệu

**XSS Clean v1.0.0** là thư viện Java mạnh mẽ giúp bảo vệ ứng dụng khỏi các cuộc tấn công XSS (Cross-Site Scripting). Thư viện cung cấp khả năng làm sạch và xác thực dữ liệu đầu vào không đáng tin cậy một cách tự động và hiệu quả.

### ✨ Tính Năng Chính

- **🛡️ Bảo Mật XSS**: Tích hợp OWASP Java HTML Sanitizer và JSoup
- **🔄 Làm Sạch Tự Động**: Sử dụng reflection để làm sạch các đối tượng phức tạp
- **🌱 Spring Boot Tích Hợp**: Auto-configuration và validation tự động
- **⚡ Hiệu Suất Cao**: Tối ưu hóa cho môi trường production
- **🔒 An Toàn**: Xử lý circular references và null safety

## 🚀 Cài Đặt

### Maven

Thêm dependency vào `pom.xml`:

```xml
<!-- Core library (bắt buộc) -->
<dependency>
    <groupId>io.github.haiphamcoder</groupId>
    <artifactId>xss-clean-core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Spring Boot integration (tùy chọn) -->
<dependency>
    <groupId>io.github.haiphamcoder</groupId>
    <artifactId>xss-clean-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
// Core library
implementation 'io.github.haiphamcoder:xss-clean-core:1.0.0'

// Spring Boot integration
implementation 'io.github.haiphamcoder:xss-clean-spring:1.0.0'
```

## 🔧 Sử Dụng Cơ Bản

### 1. Làm Sạch Chuỗi Đơn Giản

```java
import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;

// Khởi tạo service
CleanerService cleaner = new OwaspCleanerService();

// Làm sạch chuỗi có chứa XSS
String input = "<script>alert('XSS Attack!')</script>Xin chào thế giới!";
String clean = cleaner.clean(input);
// Kết quả: "Xin chào thế giới!" (đã loại bỏ script tag)
```

### 2. Làm Sạch Đối Tượng Phức Tạp

```java
// Định nghĩa class User
class User {
    public String name;
    public String email;
    public List<String> hobbies;
    public Map<String, String> profile;
}

// Tạo đối tượng với dữ liệu có thể chứa XSS
User user = new User();
user.name = "<script>alert('XSS')</script>Nguyễn Văn A";
user.email = "user@example.com";
user.hobbies = Arrays.asList("Lập trình", "<img src=x onerror=alert(1)>Hacking");
user.profile = Map.of("bio", "<script>alert('XSS')</script>Tôi là developer");

// Làm sạch toàn bộ đối tượng
cleaner.cleanObject(user);

// Tất cả các trường String đã được làm sạch tự động
System.out.println(user.name); // "Nguyễn Văn A"
System.out.println(user.hobbies); // ["Lập trình", "Hacking"]
```

## 🌱 Tích Hợp Spring Boot

### 1. Cấu Hình Tự Động

Thư viện sẽ tự động cấu hình khi bạn thêm dependency `xss-clean-spring`:

```yaml
# application.yml
xss:
  enabled: true                    # Bật/tắt XSS cleaning
  strategy: owasp                  # "owasp" hoặc "jsoup"
  throw-on-violation: false        # Ném exception khi phát hiện XSS
  log-violation: true             # Ghi log khi phát hiện XSS
  default-profile: simple         # Profile mặc định
```

### 2. Controller Tự Động Làm Sạch

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Dữ liệu trong user đã được làm sạch tự động
        // Không cần gọi cleanObject() thủ công
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam String search) {
        // Tham số search cũng đã được làm sạch tự động
        return ResponseEntity.ok(userService.search(search));
    }
}
```

### 3. Validation với Annotation

```java
public class User {
    @NoXss(message = "Tên không được chứa mã độc hại")
    private String name;
    
    @NoXss(message = "Email không được chứa mã độc hại")
    private String email;
    
    private String phone; // Không có annotation, sẽ được làm sạch tự động
}
```

### 4. Cấu Hình Tùy Chỉnh

```java
@Configuration
public class XssConfig {
    
    @Bean
    @Primary
    public CleanerService customCleaner() {
        // Sử dụng JSoup thay vì OWASP
        return new JsoupCleanerService(Safelist.basic());
    }
    
    @Bean
    public XssProperties customXssProperties() {
        XssProperties props = new XssProperties();
        props.setStrategy("jsoup");
        props.setThrowOnViolation(true);
        return props;
    }
}
```

## 🛡️ Các Chiến Lược Làm Sạch

### 1. OWASP Strategy (Mặc định)

```java
// Sử dụng OWASP Java HTML Sanitizer
CleanerService owaspCleaner = new OwaspCleanerService();

// Cấu hình tùy chỉnh
PolicyFactory customPolicy = Sanitizers.FORMATTING
    .and(Sanitizers.LINKS)
    .and(Sanitizers.BLOCKS);
CleanerService customOwaspCleaner = new OwaspCleanerService(customPolicy);
```

### 2. JSoup Strategy

```java
// Sử dụng JSoup
CleanerService jsoupCleaner = new JsoupCleanerService();

// Cấu hình tùy chỉnh
Safelist customSafelist = Safelist.basic()
    .addTags("p", "br", "strong", "em")
    .addAttributes("p", "class");
CleanerService customJsoupCleaner = new JsoupCleanerService(customSafelist);
```

## 📝 Ví Dụ Thực Tế

### 1. Blog System

```java
@RestController
@RequestMapping("/api/posts")
public class BlogController {
    
    @PostMapping
    public ResponseEntity<BlogPost> createPost(@RequestBody @Valid BlogPost post) {
        // Dữ liệu đã được làm sạch tự động
        return ResponseEntity.ok(blogService.save(post));
    }
}

class BlogPost {
    @NoXss
    private String title;
    
    @NoXss
    private String content;
    
    private String author;
    private List<String> tags;
    private Map<String, String> metadata;
}
```

### 2. E-commerce System

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Tất cả thông tin sản phẩm được làm sạch tự động
        return ResponseEntity.ok(productService.save(product));
    }
}

class Product {
    @NoXss
    private String name;
    
    @NoXss
    private String description;
    
    private BigDecimal price;
    private List<String> categories;
    private Map<String, String> specifications;
}
```

## ⚙️ Cấu Hình Nâng Cao

### 1. Profiles Tùy Chỉnh

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

### 2. Logging Configuration

```yaml
logging:
  level:
    io.github.haiphamcoder.xss: DEBUG
```

### 3. Performance Tuning

```java
@Configuration
public class XssPerformanceConfig {
    
    @Bean
    public CleanerService optimizedCleaner() {
        // Sử dụng JSoup cho hiệu suất tốt hơn
        return new JsoupCleanerService(Safelist.none());
    }
}
```

## 🔍 Debugging và Troubleshooting

### 1. Kiểm Tra Log

```java
@Slf4j
@RestController
public class DebugController {
    
    @PostMapping("/debug")
    public ResponseEntity<String> debug(@RequestBody String input) {
        log.info("Input trước khi làm sạch: {}", input);
        
        CleanerService cleaner = new OwaspCleanerService();
        String cleaned = cleaner.clean(input);
        
        log.info("Input sau khi làm sạch: {}", cleaned);
        
        return ResponseEntity.ok(cleaned);
    }
}
```

### 2. Test XSS Protection

```java
@Test
public void testXssProtection() {
    CleanerService cleaner = new OwaspCleanerService();
    
    String[] maliciousInputs = {
        "<script>alert('XSS')</script>",
        "<img src=x onerror=alert(1)>",
        "javascript:alert('XSS')",
        "<iframe src='javascript:alert(1)'></iframe>"
    };
    
    for (String input : maliciousInputs) {
        String cleaned = cleaner.clean(input);
        assertFalse(cleaned.contains("<script>"));
        assertFalse(cleaned.contains("javascript:"));
        assertFalse(cleaned.contains("onerror"));
    }
}
```

## 🚨 Lưu Ý Bảo Mật

### 1. Không Phải Giải Pháp Toàn Diện

- XSS Clean chỉ làm sạch dữ liệu đầu vào
- Vẫn cần áp dụng các biện pháp bảo mật khác
- Sử dụng Content Security Policy (CSP) headers
- Validate dữ liệu trước khi xử lý

### 2. Best Practices

```java
@RestController
public class SecureController {
    
    @PostMapping("/secure")
    public ResponseEntity<String> secureEndpoint(@RequestBody @Valid SecureData data) {
        // 1. Validation trước
        if (data.getName() == null || data.getName().trim().isEmpty()) {
            throw new ValidationException("Tên không được để trống");
        }
        
        // 2. XSS Clean đã được áp dụng tự động
        // 3. Xử lý business logic
        String result = businessService.process(data);
        
        // 4. Output encoding khi trả về
        return ResponseEntity.ok(escapeHtml(result));
    }
}
```

## 📊 Performance và Monitoring

### 1. Metrics

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

### 2. Health Check

```java
@Component
public class XssHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Kiểm tra XSS Clean service có hoạt động không
        try {
            CleanerService cleaner = new OwaspCleanerService();
            String test = cleaner.clean("<script>test</script>");
            return Health.up()
                .withDetail("xss-clean", "Hoạt động bình thường")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("xss-clean", "Lỗi: " + e.getMessage())
                .build();
        }
    }
}
```

## 🎯 Kết Luận

XSS Clean v1.0.0 cung cấp một giải pháp toàn diện và dễ sử dụng để bảo vệ ứng dụng Java khỏi các cuộc tấn công XSS. Với tích hợp Spring Boot mạnh mẽ và khả năng làm sạch tự động, thư viện giúp developers tập trung vào business logic mà không phải lo lắng về bảo mật XSS.

### Liên Hệ và Hỗ Trợ

- **GitHub**: <https://github.com/haiphamcoder/xss-clean>
- **Issues**: <https://github.com/haiphamcoder/xss-clean/issues>
- **Email**: <ngochai285nd@gmail.com>

### License

MIT License - Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

---

***Chúc bạn sử dụng XSS Clean thành công! 🚀***
