# Java 后端异常与坑点复盘总结 (2025-11-28)

本文档旨在记录 Zenith Flow 项目架构整合过程中遇到的典型 Java 后端异常。通过复盘这些问题，深入理解 Spring Boot、MyBatis Plus、Jackson 以及多语言交互中的细节，以供后续开发参考。

## 1. 动态数据源注解失效问题

### 🔴 异常现象
启动应用或调用接口时，抛出 SQL 异常，提示表不存在。
```
org.postgresql.util.PSQLException: ERROR: relation "ai_model" does not exist
```
实际上该表存在于 `zenith_flow` (PostgreSQL) 库中，但报错显示应用正在查询默认数据源 (MySQL)，说明数据源切换失败。

### 🔍 根本原因 (Root Cause)
我们在 MyBatis 的 Mapper **接口**上使用了 `@DataSource("quant")` 注解。
在 Spring AOP 机制中，对于 JDK 动态代理（Interface-based proxy），切面注解直接加在接口上并不总是能被拦截器正确识别，特别是在某些 Spring Boot 版本或特定的动态数据源框架实现（如 `dynamic-datasource-spring-boot-starter` 的某些配置下）。
此外，如果 Service 层有事务注解 `@Transactional`，事务管理器可能会在数据源切换切面执行前就固定了连接，导致切换失效。

### ✅ 解决方案
1.  **移动注解位置**: 将 `@DataSource("quant")` 注解从 Mapper 接口移动到调用它的 **Service 实现类** (或具体方法) 上。Service 类通常是 CGLIB 代理或即使是 JDK 代理，Spring 对 Bean 实现类上的注解支持更为稳健。
2.  **初始化时机**: 原本在 `@PostConstruct` 中进行数据库查询，此时 Spring AOP 代理可能尚未完全构建完成或上下文未就绪。改为实现 `ApplicationRunner` 接口，在 `run` 方法中执行初始化逻辑。

### 🧠 深度解析
Spring 的 AOP 实际上是在 Bean 的外层包裹了一层代理。当调用方法时，先经过代理，代理根据注解决定是否切换数据源（ThreadLocal 存储 key），然后再调用目标方法。如果注解加在 Mapper 接口上，MyBatis 生成的也是代理对象，这里存在两层代理的交互问题。将注解提升至 Service 层是更佳实践，因为它明确了“业务逻辑单元”的数据源边界。

---

## 2. Jackson 不支持 Java 8 时间类型

### 🔴 异常现象
接口返回 JSON 时报错：
```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDate` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310"
```

### 🔍 根本原因
项目使用了 `java.time.LocalDate` 和 `LocalDateTime`。
Spring Boot 2.x 默认使用的 Jackson 版本（或配置）可能没有自动注册 `JavaTimeModule`。Jackson 核心库默认只支持传统的 `java.util.Date`，对于 Java 8 引入的 JSR-310 时间 API 需要额外的模块支持。
虽然我们在实体类上加了 `@JsonFormat`，但如果 `ObjectMapper` 本身不认识这个类型，格式化注解也无法生效。

### ✅ 解决方案
1.  **添加依赖**: 在 `pom.xml` 中引入 `jackson-datatype-jsr310`。
    ```xml
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
    </dependency>
    ```
2.  **显式注册**: 在配置 `ObjectMapper` 的地方（如 `WebMvcConfig`），手动注册模块。
    ```java
    mapper.registerModule(new JavaTimeModule());
    ```

### 🧠 深度解析
Jackson 采用模块化设计，将非核心功能剥离。`JavaTimeModule` 包含了一系列针对 Java 8 时间类型的 Serializer 和 Deserializer。在 Spring Boot 3.x 中，这通常是自动配置的，但在旧版本或自定义 `ObjectMapper` 配置中容易被遗漏。

---

## 3. ProcessBuilder 与 Python 脚本的编码冲突

### 🔴 异常现象
Java 调用 Python 训练脚本时失败，日志显示：
```
UnicodeEncodeError: 'gbk' codec can't encode character '\U0001f680'
```

### 🔍 根本原因
1.  **Python 脚本输出**: 脚本中使用了 Emoji (🚀) 来美化日志。
2.  **Windows 环境**: Windows 的默认控制台编码通常是 GBK (cp936)。
3.  **Java ProcessBuilder**: Java 捕获子进程输出时，如果未显式指定编码，会使用系统默认编码。当 Python 尝试将 Unicode (Emoji) 写入 GBK 编码的标准输出流时，Python 解释器会抛出编码错误，导致进程崩溃。

### ✅ 解决方案
**移除特殊字符**: 最直接的方法是修改 Python 脚本，移除所有可能引起编码问题的 Emoji 和特殊符号，只保留标准 ASCII 或确保能被 GBK 兼容的字符。
(进阶方案是在 Java 端控制 `ProcessBuilder` 的环境变量 `PYTHONIOENCODING=utf-8`，并在读取流时指定 UTF-8，但修改脚本最为稳妥)。

---

## 4. 配置属性名变更导致的注入失败

### 🔴 异常现象
应用启动失败：
```
Could not resolve placeholder 'renren.redis.open'
```

### 🔍 根本原因
在进行“去品牌化”重构时，我们将 `application.yml` 中的 `renren.redis.open` 修改为了 `zenith.redis.open`。
但是，部分 Java 代码（如 `RedisAspect.java`）中仍然使用 `@Value("${renren.redis.open}")` 引用旧的 key。

### ✅ 解决方案
全局搜索项目中的配置引用，确保 Java 代码中的 `@Value` 注解与 YAML 配置文件中的 Key 保持一致。

### 🧠 经验总结
重构配置项名称属于高风险操作。IDE 的重构工具通常无法关联 YAML Key 和 Java String Value。修改配置 Key 后，必须全文搜索旧 Key 字符串，确保没有遗漏。

---

## 5. 异常捕获类型不匹配

### 🔴 异常现象
编译错误：
```
java: 在相应的 try 语句主体中不能抛出异常错误 ai.onnxruntime.OrtException
```

### 🔍 根本原因
代码中写了 `try { ... } catch (OrtException e) { ... }`。
但是 `try` 块中调用的方法（如 `OrtEnvironment.close()` 或某些资源释放方法）实际上并没有声明抛出 `OrtException`（这是一个 Checked Exception）。Java 编译器强制要求 `catch` 块捕获的 Checked Exception 必须在 `try` 块中可能被抛出，否则视为不可达代码或类型错误。

### ✅ 解决方案
将 `catch (OrtException e)` 修改为 `catch (Exception e)`，捕获更通用的异常，或确认 `try` 块中具体抛出的异常类型。

---

## 总结 (Key Takeaways)

1.  **跨语言调用 (Java -> Python)** 要极度注意**字符编码**和**环境差异**。
2.  **Spring 魔法 (AOP/DI)** 有其边界，特别是在**接口代理**和**初始化生命周期**上，出现问题时优先考虑这些边界条件。
3.  **配置重构**需要细致的全局搜索，不能只改配置文件。
4.  **依赖管理**：对于日期时间等基础类型，要确认 JSON 库的兼容性配置。

