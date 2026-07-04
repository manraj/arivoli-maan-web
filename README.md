# arivoli-maan-web

Reusable Spring Boot web/API framework library for Arivoli Academy services.

## Overview

`arivoli-maan-web` builds on [arivoli-maan-core](https://github.com/arivoliacademy/arivoli-maan-core) and provides auto-configured web standards for Spring Boot applications: global exception handling, validation error mapping, correlation ID propagation, and request logging.

Add the dependency and the framework activates automatically — no manual `@Import` required.

## Requirements

- Java 21+
- Gradle 8.x (wrapper included)
- Spring Boot 3.4+
- `arivoli-maan-core` 0.1.0

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.arivoliacademy:arivoli-maan-web:0.1.0'
}
```

This transitively includes `arivoli-maan-core`, `spring-boot-starter-web`, and `spring-boot-starter-validation`.

### Maven

```xml
<dependency>
    <groupId>com.arivoliacademy</groupId>
    <artifactId>arivoli-maan-web</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Package Structure

| Package | Purpose |
|---------|---------|
| `com.arivoliacademy.maan.web.autoconfigure` | Spring Boot auto-configuration |
| `com.arivoliacademy.maan.web.config` | Configuration properties |
| `com.arivoliacademy.maan.web.constants` | Web layer constants |
| `com.arivoliacademy.maan.web.exception` | Global and validation exception handlers |
| `com.arivoliacademy.maan.web.filter` | Servlet filters (correlation ID, request logging) |
| `com.arivoliacademy.maan.web.helper` | Optional API response helpers |

## Auto-Configuration

Registration file:

`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

```
com.arivoliacademy.maan.web.autoconfigure.MaanWebAutoConfiguration
```

When a Spring Boot web application starts, the following beans are registered automatically:

- `GlobalExceptionHandler` — maps `MaanException` and unexpected errors to `ErrorResponse`
- `ValidationExceptionHandler` — maps validation failures to field-level `ErrorResponse`
- `CorrelationIdFilter` — ensures `X-Correlation-Id` and `X-Request-Id` on every request
- `RequestLoggingFilter` — logs request method, URI, status, and duration

## Configuration

All settings use the `maan.web` prefix:

```yaml
maan:
  web:
    correlation-id:
      enabled: true
      header-name: X-Correlation-Id
    request-logging:
      enabled: true
      include-query-string: true
      include-client-info: false
```

| Property | Default | Description |
|----------|---------|-------------|
| `maan.web.correlation-id.enabled` | `true` | Enable correlation ID filter |
| `maan.web.correlation-id.header-name` | `X-Correlation-Id` | Request/response header name |
| `maan.web.request-logging.enabled` | `true` | Enable request logging filter |
| `maan.web.request-logging.include-query-string` | `true` | Include query string in log output |
| `maan.web.request-logging.include-client-info` | `false` | Include client IP in log output |

## Usage Examples

### Standard controller response

```java
import com.arivoliacademy.maan.core.api.ApiResponse;
import com.arivoliacademy.maan.web.helper.ApiResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @GetMapping("/{id}")
    public ApiResponse<CourseDto> getCourse(@PathVariable Long id) {
        CourseDto course = courseService.findById(id);
        return ApiResponse.success("Course retrieved", course);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseDto>> create(@RequestBody CreateCourseRequest request) {
        CourseDto created = courseService.create(request);
        return ApiResponseHelper.created(created);
    }
}
```

### Throwing framework exceptions

Service code throws exceptions from `arivoli-maan-core`; this library maps them automatically:

```java
import com.arivoliacademy.maan.core.exception.ResourceNotFoundException;
import com.arivoliacademy.maan.core.exception.ConflictException;

public CourseDto findById(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course", id));
}

public CourseDto create(CreateCourseRequest request) {
    if (repository.existsByCode(request.code())) {
        throw new ConflictException("Course code already exists");
    }
    return repository.save(toEntity(request));
}
```

**404 response:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Course not found with identifier: 42",
  "code": "MAAN-404",
  "fieldErrors": [],
  "timestamp": "2026-07-04T16:44:13.897Z"
}
```

### Validation errors

Use `@Valid` on request bodies. Failures are mapped to a standard error response with field details:

```java
public record CreateCourseRequest(
        @NotBlank String code,
        @NotBlank String title
) {}
```

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "code": "MAAN-400",
  "fieldErrors": [
    {
      "field": "code",
      "message": "must not be blank",
      "rejectedValue": null
    }
  ],
  "timestamp": "2026-07-04T16:44:13.897Z"
}
```

### Correlation ID in logs

The correlation ID filter stores values in SLF4J MDC under `correlationId` and `requestId`. Use them in your log pattern:

```yaml
logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{correlationId:-},%X{requestId:-}]"
```

Incoming clients may pass their own correlation ID:

```http
GET /courses/1 HTTP/1.1
X-Correlation-Id: client-trace-abc123
```

If omitted, a UUID is generated and returned in the response headers.

## Local Development

When developing `arivoli-maan-web` alongside `arivoli-maan-core`, the composite build in `settings.gradle` resolves the core dependency locally:

```gradle
includeBuild('../arivoli-maan-core')
```

## Build

```bash
./gradlew clean build
```

## Design Principles

- Depends on `arivoli-maan-core` for shared models and exceptions
- No JPA or database dependencies
- No Spring Security or JWT (planned as separate modules)
- No business-specific or customer-specific logic
- Auto-configuration only — services opt in by adding the dependency

## Related Projects

| Project | Description |
|---------|-------------|
| [arivoli-maan-core](https://github.com/arivoliacademy/arivoli-maan-core) | Core API models, exceptions, utilities, and constants |

## License

Apache License 2.0. See [LICENSE](LICENSE).
