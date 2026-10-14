# PART 3. Spring Boot 3

> **〔v1.3 추가〕** 제목은 "Spring Boot 3"으로 두었지만, 실제 `pom.xml`의 부모 버전은 **`spring-boot-starter-parent 4.0.8`** 이다(`MySpringBootAdmin`·`SpringBoot-Gradle-App`만 3.1.5). 이 파트의 개념 설명은 Boot 3·4에 공통으로 유효하며, 버전별 차이(Spring Framework 7 / Hibernate 7.1 / Jackson 3 / 모듈형 스타터)는 **5-0절**에 정리했다.

## 3-0. 저장소 개요

```
springboot/
├─ springboot-student/   ← 메인 학습 프로젝트 (Student · DTO · ErrorCode 포함)
│  main/java/com/rookies6/myspringboot4project/
│     MySpringBoot4ProjectApplication.java   @SpringBootApplication
│     config/     CustomVO, ProdConfig(@Profile), TestConfig(@Profile), WebConfig
│     controller/ UserController(Thymeleaf), UserRestController(REST), dto/StudentDTO
│     entity/     User, Customer, Student
│     exception/  BusinessException, ErrorCode, advice/DefaultExceptionAdvice, ErrorObject
│     property/   MyBootProperties(@ConfigurationProperties)
│     repository/ UserRepository, CustomerRepository, StudentRepository
│     runner/     MyRunner, DatabaseRunner, DataInitRunner
│  main/resources/ application.properties, application-prod/-test.properties, templates/, static/
│
├─ springboot/           ← 위와 거의 동일 + Spring Security 추가
│     auth/config/SecurityConfig.java
│     auth/userinfo/  UserInfo, UserInfoController, UserInfoRepository,
│                     UserInfoUserDetails, UserInfoUserDetailsService
│     (Student·DTO·ErrorCode 는 없음)
│
├─ SpringBoot-Gradle-App/  ← Gradle 빌드 버전 (Account, BaseConfiguration 등)
└─ MySpringBootAdmin/      ← Actuator UI 서버 (@EnableAdminServer, port 8090)
```

**⚠️ `springboot`와 `springboot-student`의 관계**
`diff` 결과 두 프로젝트는 패키지 구조가 같고 다음만 다르다.

| | `springboot` | `springboot-student` |
|---|---|---|
| Security | **있음** (`auth/` 패키지 전체) | 없음 |
| Student 도메인 | 없음 | **있음** (`Student`, `StudentRepository`, `StudentDTO`, `DataInitRunner`) |
| 예외 | `BusinessException` 기본형 | + **`ErrorCode` enum** |
| 예외 처리 | `ErrorObject` 반환 | + **`ProblemDetail`(Spring 6)**, `@Valid` 처리 |
| `UserRestController` | `@PreAuthorize` 있음 | 없음 |

**즉 `springboot-student`가 "도메인·예외 설계"를, `springboot`가 "인증·인가"를 담당하는 분기다.** 두 개를 합치면 하나의 완성된 앱이 된다.

---

## 3-1. 스프링 부트란 — 자동 설정의 원리

### 개념

**목표**: **단독 실행 가능한 제품 수준의 스프링 애플리케이션**을 쉽고 빠르게.

**주요 기능 4가지**
1. 단독 실행 가능한 애플리케이션 제작
2. **내장 서버**(Tomcat, Jetty, UnderTow) — 별도 서버 설치 불필요
3. 최대한 **자동화된 설정**
4. **XML 설정 없는** 단순한 설정 방식

**의존성 관리**
```
pom.xml → spring-boot-starter-parent
        → spring-boot-dependencies
```
`spring-boot-starter-*` 라이브러리는 parent가 버전을 관리하므로 **버전을 쓰지 않는다.** 버전을 바꾸려면 `<properties>`에 `<spring-framework.version>6.1.x</spring-framework.version>`처럼 명시.

**⚠️ `@SpringBootApplication` = 3개 어노테이션의 합** — 반드시 외울 것

| 구성 | 역할 |
|---|---|
| `@SpringBootConfiguration` | `@Configuration`의 부트 버전 |
| **`@ComponentScan`** | **1단계**: 이 클래스가 있는 패키지부터 아래로 `@Component`/`@Service`/`@Repository`/`@Controller` 스캔 |
| **`@EnableAutoConfiguration`** | **2단계**: 자주 쓰이는 Bean들을 자동 등록. 목록은 `spring-boot-autoconfigure-3.X.jar`의 `META-INF/spring.factories`에 있고, `@Configuration` + **`@ConditionalOnXxx`**로 조건부 등록 |

**⚠️ 그래서 `@SpringBootApplication` 클래스는 최상위(디폴트) 패키지에 있어야 한다.** 하위 패키지만 스캔하기 때문. 이걸 어기면 "Bean을 못 찾는다"는 오류가 난다.

**웹 애플리케이션 타입**

| 값 | ApplicationContext 구현체 |
|---|---|
| `SERVLET` | `AnnotationConfigServletWebServerApplicationContext` |
| `REACTIVE` | `AnnotationConfigReactiveWebServerApplicationContext` |
| `NONE` | `AnnotationConfigApplicationContext` (웹 아님) |

MVC와 WebFlux가 둘 다 있으면 **MVC가 우선**.

### 코드 — `MySpringBoot4ProjectApplication.java`

```java
@SpringBootApplication
public class MySpringBoot4ProjectApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MySpringBoot4ProjectApplication.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);   // 웹 타입 명시
        application.run(args);
    }

    @Bean
    public String myBean() {
        return "Hello Bean";
    }
}
```

**해설**
- `SpringApplication.run(클래스, args)` 한 줄로 끝낼 수도 있는데(`MySpringBootAdminApplication`이 그렇게 한다), 여기서는 **객체를 만들어 설정을 준 뒤 `run()`**했다. 자료 1.4절의 방식. 이래야 `setWebApplicationType`, `addListeners`, `setBanner` 같은 커스터마이징이 가능하다.
- `WebApplicationType.SERVLET`은 어차피 기본값이다(spring-boot-starter-web이 있으면). **명시해서 눈으로 확인하려는 학습용 코드.**
- ⚠️ **`@Bean public String myBean()`** — `String`도 Bean이 될 수 있다는 실험. 실무에서는 이렇게 쓰지 않지만, **"Bean은 특별한 타입이 아니라 컨테이너가 관리하는 객체일 뿐"**이라는 것을 보여준다. 🔗 PART 2의 POJO 개념 연장선.

**비교 — `MySpringBootAdmin`은 최소 형태**
```java
@SpringBootApplication
@EnableAdminServer                            // Actuator UI 서버로 만든다
public class MySpringBootAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(MySpringBootAdminApplication.class, args);
    }
}
```

### ⚠️ PART 2에서 사라진 것들 — 자동 설정이 대체한 목록

이 표가 **PART 2 → PART 3 이행의 핵심**이다.

| SpringFW에서 직접 했던 것 | Spring Boot에서는 |
|---|---|
| `web.xml`의 `<servlet>DispatcherServlet</servlet>` | **자동 등록** |
| `ContextLoaderListener` + 컨텍스트 2분할 | **불필요** (단일 컨텍스트) |
| `CharacterEncodingFilter` 설정 | **자동** (UTF-8 기본) |
| `<mvc:annotation-driven/>` / `@EnableWebMvc` | **자동** (`WebMvcAutoConfiguration`) |
| `<bean> ViewResolver` | **자동** (Thymeleaf 등 감지) |
| `<bean> DataSource` + driver/url/user/pw | `application.properties` **4줄** |
| `<bean> SqlSessionFactoryBean` | JPA면 **불필요** |
| `<bean> DataSourceTransactionManager` | **자동** |
| `<tx:annotation-driven/>` / `@EnableTransactionManagement` | **자동** |
| `<context:component-scan>` | `@SpringBootApplication`에 **포함** |
| 톰캣 설치 + WAR 배포 | **내장 톰캣** + `java -jar` |

**PART 2의 `beans.xml`(80줄) + `beans-web.xml`(30줄) + `web.xml`(45줄) ≈ 155줄이 `application.properties` 10줄로 줄었다.** 이것이 "XML 설정 없이"의 실체다.

---

## 3-2. 외부 설정 — properties · @Value · @ConfigurationProperties

### 개념

**설정 소스**: Properties, YAML, 환경변수, 커맨드 라인 인수

**⚠️ 프로퍼티 우선순위** (높은 것부터, 자료 1.5절 — 자주 나옴)
1. 유저 홈의 `spring-boot-dev-tools.properties`
2. 테스트의 `@TestPropertySource`
3. `@SpringBootTest`의 `properties` 속성
4. **커맨드 라인 아규먼트** (`--myboot.name=x`)
5. `SPRING_APPLICATION_JSON`
6~8. ServletConfig / ServletContext / JNDI
9. `System.getProperties()`
10. **OS 환경 변수**
11. `RandomValuePropertySource`
12~15. **JAR 밖 > JAR 안**, **프로파일용 > 일반**
16. `@PropertySource`
17. 기본 프로퍼티

**핵심 규칙 3가지**: 커맨드라인 > 파일 / JAR 밖 > JAR 안 / 프로파일용 > 일반

**두 가지 주입 방식**

| | `@Value` | `@ConfigurationProperties` |
|---|---|---|
| 대상 | 값 하나씩 | **여러 값을 묶어서** |
| 타입 안전 | ❌ 문자열 | ⭕ **type-safe** |
| 유지보수 | 흩어짐 | **한 클래스에 모임** |
| 필요 의존성 | — | `spring-boot-configuration-processor` |

### 코드 — `application.properties` 3종

```properties
# ── application.properties (공통) ──
spring.application.name=MySpringBoot4Project
#server.port=8087

myboot.age=${random.int(1,100)}        # ★ RandomValuePropertySource
myboot.fullName=${myboot.name} Boot     # ★ 다른 프로퍼티 참조

spring.profiles.active=prod             # ★ 활성 프로파일 결정

management.endpoints.web.exposure.include=*        # Actuator 전체 공개
spring.boot.admin.client.url=http://localhost:8090 # Admin 서버 주소
```

```properties
# ── application-prod.properties (운영) ──
myboot.name=Spring 운영환경
logging.level.com.rookies6.myspringboot4project=info

spring.datasource.url=jdbc:mariadb://${DB_HOST:127.0.0.1}:${DB_PORT:3306}/${DB_DATABASE:boot_db}
spring.datasource.username=${DB_USERNAME:boot}
spring.datasource.password=${DB_PASSWORD:boot}
spring.datasource.driverClassName=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
```

```properties
# ── application-test.properties (테스트) ──
myboot.name=Spring 테스트환경
logging.level.com.rookies6.myspringboot4project=debug

spring.datasource.url=jdbc:h2:mem:testdb        # ★ 인메모리 H2
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
```

**해설 — 이 3개 파일에 자료 1.5~1.7절이 다 들어 있다**

1. **`myboot.fullName=${myboot.name} Boot`** — `myboot.name`은 프로파일 파일에만 있다. 그래서 prod면 `"Spring 운영환경 Boot"`, test면 `"Spring 테스트환경 Boot"`가 된다. **공통 파일이 프로파일 파일의 값을 참조**하는 구조.
2. **`${random.int(1,100)}`** — 자료 우선순위 11번 `RandomValuePropertySource`. 재시작할 때마다 값이 바뀐다.
3. **⚠️ `${DB_HOST:127.0.0.1}` 문법이 이 저장소에서 가장 실무적인 부분이다.**
   - `${환경변수:기본값}` — OS 환경변수 `DB_HOST`가 있으면 그걸 쓰고, 없으면 `127.0.0.1`
   - 주석 처리된 하드코딩 버전이 바로 위에 남아 있다:
     ```properties
     #spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/boot_db
     ```
   - **왜 바꿨나**: DB 비밀번호를 소스에 넣으면 Git에 올라간다. 환경변수로 빼면 서버마다 다른 값을 쓸 수 있고 소스는 안전하다. 🔗 자료 우선순위 10번(OS 환경 변수)을 활용한 것.
   - 이것이 Docker/Kubernetes 배포의 표준 방식이다.
4. **`logging.level.패키지=info|debug`** — 같은 코드인데 프로파일에 따라 로그 양이 달라진다. 개발은 debug, 운영은 info. 자료 1.8절.
5. **DB가 프로파일에 따라 통째로 바뀐다** — test는 H2 인메모리, prod는 MariaDB. **자바 코드는 한 줄도 안 바뀐다.** 🔗 PART 2에서 `DataSource` Bean을 XML/JavaConfig로 갈아끼우던 것을 프로퍼티 파일 교체로 해결.

### 코드 — `@ConfigurationProperties`

```java
@Component
@ConfigurationProperties("myboot")      // ★ myboot.* 프로퍼티를 이 클래스에 바인딩
@Getter @Setter                          // ★ Lombok — setter 가 있어야 바인딩된다
public class MyBootProperties {
    private String name;                 // myboot.name
    private int age;                     // myboot.age
    private String fullName;             // myboot.fullName
}
```

**해설**
- **필드 이름이 프로퍼티 키와 매칭**된다. `myboot.fullName` → `fullName`.
- ⚠️ **`@Setter`가 필수다.** Spring이 `setName()`, `setAge()`를 호출해 값을 넣는다. 🔗 PART 2의 `<property name="name">`이 setter를 찾던 것과 **정확히 같은 원리**.
- `age`가 `int`인데 프로퍼티는 문자열이다. Spring이 **자동 타입 변환**을 해준다. 이것이 "type-safe"의 의미 — 숫자가 아니면 기동 시점에 에러가 난다.

### 코드 — `MyRunner.java`에서 두 방식 비교

```java
@Component
public class MyRunner implements ApplicationRunner {

    @Value("${spring.application.name}")   // ① @Value — 하나씩
    String applicationName;
    @Value("${myboot.name}")
    private String name;
    @Value("${myboot.age}")
    private int age;

    @Autowired
    private Environment environment;        // ② Environment — 코드에서 꺼내기

    @Autowired
    private MyBootProperties properties;    // ③ @ConfigurationProperties — 묶어서

    @Autowired
    private CustomVO customVO;              // ④ 프로파일별 Bean

    private Logger logger = LoggerFactory.getLogger(MyRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Logger 구현체 클래스명 {}", logger.getClass().getName());
        logger.debug("Application Name = {}", applicationName);

        // 커맨드라인 아규먼트 이름 출력 — 같은 일을 3가지 문법으로
        args.getOptionNames().forEach(new Consumer<String>() {      // 익명 클래스
            @Override public void accept(String s) { System.out.println("arg name = " + s); }
        });
        args.getOptionNames().forEach(name -> System.out.println(name));   // 람다
        args.getOptionNames().forEach(System.out::println);                // 메서드 참조

        logger.debug("${myboot.name} = {}", name);                       // ①
        logger.debug("${myboot.fullName} = {}", environment.getProperty("myboot.fullName")); // ②
        logger.info("MyBootProperties getName() = {}", properties.getName());               // ③
        logger.debug("현재 활성화 되어있는 CustomVO= {}", customVO);                          // ④
    }
}
```

**해설 — 이 한 파일이 자료 1.4~1.8절을 전부 실습한다**

1. **`ApplicationRunner`** (자료 1.4절) — 애플리케이션 기동 완료 후 자동 실행된다. `run(ApplicationArguments args)`로 커맨드라인 인수를 받는다.
   - ⚠️ `--bar`(Program arguments)는 `args.containsOption("bar")`로 잡히지만, `-Dfoo`(VM arguments)는 **잡히지 않는다.** 자료가 짚은 차이.
2. **⚠️ 같은 반복을 익명 클래스 → 람다 → 메서드 참조 3단계로 쓴 부분이 백미다.**
   ```java
   .forEach(new Consumer<String>() { public void accept(String s) {...} })   // Java 7 이하 스타일
   .forEach(name -> System.out.println(name))                                // Java 8 람다
   .forEach(System.out::println)                                             // 메서드 참조
   ```
   🔗 **PART 1 자료 1.1절이 언급한 "J2SE 8: Lambda, Functional Interface"가 여기서 처음 실제로 쓰인다.** `Consumer<String>`이 추상 메서드 하나짜리 **함수형 인터페이스**라서 람다로 축약되는 것.
3. **`logger.debug("... {}", 값)`** — 문자열 결합(`+`)이 아니라 **`{}` 플레이스홀더**를 쓴다. 로그 레벨이 debug가 아니면 결합 자체가 일어나지 않아 성능상 유리하다. 🔗 PART 2 `LoggingAspect`의 `logger.isDebugEnabled()` 체크와 같은 목적을 더 우아하게 해결한 것.
4. **`logger.getClass().getName()`을 찍는 이유** — 자료 1.8절의 "로깅 퍼사드 vs 로거"를 확인하려는 것. `org.slf4j.Logger`(퍼사드) 인터페이스로 받았는데 실제 구현체는 `ch.qos.logback.classic.Logger`(로거)로 찍힌다. **스프링 부트 기본 로거가 Logback**임을 눈으로 보는 코드.

---

## 3-3. 프로파일 — 환경별 Bean 교체

### 개념

`@Profile("이름")`을 붙인 Bean은 **그 프로파일이 활성화됐을 때만** 등록된다. `spring.profiles.active`로 결정. JAR 실행 시 `--spring.profiles.active=prod`가 프로퍼티 파일보다 **우선**한다(우선순위 4번 > 13번).

### 코드

```java
// config/CustomVO.java — 환경별로 달라질 값을 담는 객체
@Builder @Getter @ToString
public class CustomVO {
    private String mode;
    private double rate;
}

// config/ProdConfig.java
@Configuration
@Profile("prod")                      // ★ prod 일 때만 등록
public class ProdConfig {
    @Bean
    public CustomVO customVO() {
        return CustomVO.builder().mode("운영 환경").rate(1.5).build();
    }
}

// config/TestConfig.java
@Configuration
@Profile("test")                      // ★ test 일 때만 등록
public class TestConfig {
    @Bean
    public CustomVO customVO() {
        return CustomVO.builder().mode("테스트 환경").rate(0.5).build();
    }
}
```

**해설**
- **`customVO()`라는 같은 이름의 `@Bean`이 두 클래스에 있다.** 보통이면 이름 충돌이지만, `@Profile`이 갈라주므로 **런타임에는 항상 하나만 등록**된다.
- `MyRunner`가 `@Autowired CustomVO customVO`로 받으면 활성 프로파일에 따라 다른 값이 들어온다. 코드는 그대로.
- 🔗 **PART 2의 "같은 앱을 XML/JavaConfig 두 벌로 만든 것"과 문제의식이 같다.** 다만 여기서는 **런타임에 자동으로 갈린다**는 것이 다르다.

**`@Builder` 패턴 (Lombok)**
```java
CustomVO.builder().mode("운영 환경").rate(1.5).build();
```
- 생성자 `new CustomVO("운영 환경", 1.5)`와 결과는 같지만 **어떤 값이 어떤 필드인지 이름으로 드러난다.**
- 🔗 PART 1의 생성자 오버로딩(`Publication(title, publishDate, page, price)`)이 인자 4개만 넘어도 헷갈렸던 것을 떠올리면, 빌더가 왜 나왔는지 이해된다. **필드가 많을수록 유리하다.**

**⚠️ 프로파일 전환 3가지 방법 (우선순위 순)**
```bash
# ① 커맨드라인 (가장 우선)
java -jar app.jar --spring.profiles.active=prod
# ② 환경변수
export SPRING_PROFILES_ACTIVE=prod
# ③ application.properties (가장 낮음)
spring.profiles.active=prod
```
운영 배포에서는 **①이나 ②**를 쓴다. 파일에 박아두면 배포 대상마다 파일을 고쳐야 하기 때문.


---

## 3-4. Spring Data JPA — Entity와 Repository

### 개념

**ORM · JPA · Hibernate의 관계** — 셋을 정확히 구분할 것

| | 정체 |
|---|---|
| **ORM** | 객체와 릴레이션의 **개념적 불일치를 해결하는 방법론/프레임워크** |
| **JPA** | ORM을 위한 **Jakarta 표준 스펙**(인터페이스) |
| **Hibernate** | JPA 스펙의 **구현체** |
| **Spring Data JPA** | JPA를 **한 단계 더 추상화**한 Spring 프로젝트. Repository 인터페이스 제공 |

**Spring Data JPA가 하는 일**: 개발자가 정해진 규칙대로 finder 메서드 이름을 쓰면, **Spring이 그 이름에 맞는 쿼리를 수행하는 구현체를 만들어 Bean으로 등록**해준다.

**Entity 어노테이션**

| | 역할 |
|---|---|
| `@Entity` | DB 테이블과 매핑되는 클래스 |
| `@Table(name=)` | 테이블 이름 지정 |
| `@Id` | 기본키 |
| `@GeneratedValue(strategy=)` | PK 자동 생성. **AUTO**(기본) / **IDENTITY**(DB의 auto_increment) / **SEQUENCE** / **TABLE** |
| `@Column` | 컬럼 속성 (`nullable`, `unique`, `updatable`) |

**`spring.jpa.hibernate.ddl-auto` 5가지** — ⚠️ 시험/실무 모두 중요

| 값 | 동작 | 언제 |
|---|---|---|
| `create` | 기존 테이블 **삭제 후** 새로 생성 | 초기 학습 |
| `create-drop` | + 종료 시 삭제 | 테스트 |
| `update` | 기존 유지, **변경분만 반영**. 데이터 보존 | **개발** |
| `validate` | 엔티티와 테이블 매핑만 **검증**. 안 맞으면 `SchemaManagementException` | **운영** |
| `none` | 아무것도 안 함 | 운영 |

**Dialect(방언)**: ANSI SQL 외에 DBMS마다 다른 문법이 있다. JPA가 SQL을 직접 생성하므로 **어떤 DBMS인지 알려줘야 한다.** `spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect`

**⚠️ `GenerationType.AUTO`가 MariaDB에서 실제로 만드는 것 — 표만 보면 놓치는 부분**

`AUTO`(기본값)를 그대로 두면, MariaDB는 `auto_increment`가 아니라 **별도의 시퀀스 테이블**을 만들어 PK를 채번한다.

```sql
-- ddl-auto=create, GenerationType.AUTO 일 때 실제로 생성되는 것
create sequence customers_seq start with 1 increment by 50 nocache;
create table customers (id bigint not null, customer_id varchar(255) not null,
    customer_name varchar(255) not null, primary key (id)) engine=InnoDB;
```
```
MariaDB [boot_db]> desc customers_seq;
+-----------------------+------+
| Field                 | ...  |
+-----------------------+------+
| next_not_cached_value | ...  |   ← 다음 발급할 값
| increment              | ...  |   ← 50 씩 증가 (기본 allocationSize)
+-----------------------+------+
```

`GenerationType.IDENTITY`로 명시하면 이것과 다르게, **컬럼 자체가 `auto_increment`**로 만들어진다.
```sql
create table customers (id bigint not null auto_increment, ...) engine=InnoDB;
```

**해설**
- `AUTO`는 하이버네이트가 시퀀스를 기본 채번 전략으로 선택하는데, **MariaDB/MySQL은 원래 시퀀스가 없는 DB**라 하이버네이트가 **테이블로 흉내낸 것**이 `customers_seq`다. `increment by 50`인 이유는 매번 DB에 값을 물어보지 않고 **한 번에 50개를 미리 확보**해 메모리에서 쓰기 위해서(성능 최적화). 이 저장소의 `User`/`Student`는 전부 `IDENTITY`를 명시했기 때문에 이 시퀀스 테이블이 생기지 않는다 — **표에서 `IDENTITY`를 "MySql, MariaDB"용이라고 적어둔 이유가 이것**이다.
- ⚠️ 시퀀스 테이블이 생겼다고 당황할 필요는 없다. 다만 `AUTO`를 쓰다가 "테이블이 하나 더 생겼는데?"라고 놀랄 수 있으므로, **의도적으로 IDENTITY를 명시하는 편이 예측 가능하다.**

**DBCP**: HikariCP(**기본**) / Tomcat CP / Commons DBCP2

**영속성 컨텍스트(PersistenceContext)**
- `EntityManagerFactory`는 앱 전체에서 **하나**, 거기서 `EntityManager`를 생성
- ⚠️ **JPA의 모든 데이터 변경은 반드시 트랜잭션 안에서** 실행되어야 한다
- Entity 관리 5대 전략: **1차 캐시 / 동일성 보장 / 쓰기 지연 / 변경 감지(Dirty Checking) / 지연 로딩**
- **Flush**: 영속성 컨텍스트의 변경 내용을 DB에 반영하는 과정

### 코드 — Entity 3종

```java
// entity/User.java
@Entity
@Table(name = "users")
@Getter @Setter                                    // Lombok
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "Name(이름)은 필수 입력 항목입니다.")     // ★ 검증
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email 주소는 필수 입력 항목입니다.")
    @Email
    private String email;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp                                          // ★ 등록 시각 자동
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

```java
// entity/Customer.java
@Entity
@Table(name = "customers")
@DynamicUpdate                                     // ★ 변경된 컬럼만 UPDATE
@Getter @Setter
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String customerId;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

```java
// entity/Student.java
@Entity
@Table(name = "students")
@NoArgsConstructor @AllArgsConstructor @Builder    // ★ Lombok 3종
@Getter @Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")                   // ★ 컬럼명 다르게
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String studentNumber;
}
```

**해설 — PART 2의 `UserVO`와 비교하면 무엇이 달라졌나**

| | `UserVO` (SpringFW) | `User` (Boot) |
|---|---|---|
| 테이블 매핑 | `SqlMapConfig.xml`의 typeAlias + `*Mapper.xml`의 SQL | **`@Entity` + `@Table`** |
| PK | `Long id` (그냥 필드) | **`@Id @GeneratedValue`** |
| 제약조건 | DB에만 존재 | **`@Column(nullable, unique)`로 코드에 명시** |
| getter/setter | **50줄 직접 작성** | **`@Getter @Setter` 2줄** |
| 검증 | 없음 | **`@NotEmpty` `@Email`** |
| 등록시각 | 없음 | **`@CreationTimestamp` 자동** |

**핵심 포인트**

1. **⚠️ `updatable = false`가 `createdAt`에 붙은 이유**: 등록 시각은 한 번 정해지면 바뀌면 안 된다. UPDATE 문에서 이 컬럼을 아예 빼버린다. `@CreationTimestamp`와 짝을 이루는 설계.
2. **`@Column(name = "student_id")`** — 자바 필드는 `id`, DB 컬럼은 `student_id`. 이름이 다를 때 매핑을 명시한다. 명시하지 않으면 카멜케이스가 스네이크케이스로 자동 변환된다(`studentNumber` → `student_number`).
3. **⚠️ `@DynamicUpdate`(Customer만)** — JPA는 기본적으로 **모든 컬럼을 UPDATE**한다. 이름 하나 바꿔도 `UPDATE customers SET customer_id=?, customer_name=?, created_at=?`. `@DynamicUpdate`를 붙이면 **바뀐 컬럼만** SQL에 넣는다. 컬럼이 많은 테이블에서 성능 차이가 난다.
4. **`@Builder`(Student만)** — `DataInitRunner`에서 `Student.builder().name(...).studentNumber(...).build()`로 쓰기 위해서. `@Builder`를 쓰려면 `@AllArgsConstructor`가 필요하고, JPA는 `@NoArgsConstructor`를 요구하므로 **셋을 함께 붙인 것**. 🔗 PART 1의 "기본 생성자 자동 생성 예외" 규칙이 여기서도 살아 있다.

   **⚠️ `@Builder`를 왜 쓰는가 — 생성자 인자 폭발(telescoping constructor) 문제**
   선택적 필드가 여러 개인 클래스를 생성자만으로 다루면 이렇게 된다.
   ```java
   new Car("aa","bb", null, null, "dd", null, null, null…. )   // 어느 자리가 뭔지 안 보인다
   ```
   인자가 늘어날수록 **몇 번째가 무슨 값인지 호출부만 보고는 알 수 없다.** 그래서 아래처럼 내부 정적 클래스로 "필수 필드는 생성자로, 선택 필드는 메서드 체이닝으로" 받는 빌더 패턴이 나왔다.
   ```java
   public class Computer {
       private String cpu;   // 필수
       private String gpu;   // 선택
       private String ram;   // 선택

       private Computer(Builder builder) {
           this.cpu = builder.cpu; this.gpu = builder.gpu; this.ram = builder.ram;
       }
       public static class Builder {
           private String cpu, gpu, ram;
           public Builder(String cpu) { this.cpu = cpu; }              // 필수 필드
           public Builder gpu(String gpu) { this.gpu = gpu; return this; }   // 선택 필드, 자기 자신 반환
           public Builder ram(String ram) { this.ram = ram; return this; }
           public Computer build() { return new Computer(this); }
       }
   }
   // 사용
   Computer c1 = new Computer.Builder("Intel i7").gpu("RTX 3080").ram("16GB").build();
   Computer c2 = new Computer.Builder("AMD Ryzen 5").build();               // gpu/ram 생략 가능
   ```
   Lombok의 `@Builder`는 **이 클래스 전체를 어노테이션 한 줄로 생성**해주는 것이다. `Student.builder().name(...).studentNumber(...).build()`가 위 `Computer.Builder` 패턴과 동일한 구조라는 것을 알면, 왜 대입 순서를 안 지켜도 되고 필드를 빠뜨려도 컴파일이 되는지 이해된다.
5. **⚠️ Entity에 검증 어노테이션(`@NotEmpty`)이 붙어 있는 것은 논쟁적이다.** Entity는 DB 매핑용, 검증은 요청 데이터용이라 역할이 섞인다. `springboot-student`가 `StudentDTO`를 따로 만든 것이 그 개선이다(3-6절).
6. **⚠️ `User`는 `@NotEmpty`, `StudentDTO`(3-6절)는 `@NotBlank`를 쓴다 — 같은 "필수 입력" 검증인데 왜 다른 어노테이션인가**

   | | 공백 문자열(`" "`) | 빈 문자열(`""`) | `null` |
   |---|---|---|
   | `@NotEmpty` | **통과** | 거부 | 거부 |
   | `@NotBlank` | **거부** (trim 후 체크) | 거부 | 거부 |

   `@NotBlank`가 `@NotEmpty`보다 엄격하다. `name=" "`(공백 한 칸)처럼 눈에 안 보이는 값이 들어오는 걸 막으려면 `@NotBlank`를 써야 한다. 이 저장소에서는 `User.name`이 `@NotEmpty`라 공백만 있는 이름도 등록이 되고, `StudentDTO`는 `@NotBlank`라 막힌다 — **의도적 차이라기보다는 두 엔티티가 서로 다른 시점에 작성된 흔적**으로 보는 것이 맞다. 새로 작성할 때는 `@NotBlank`가 기본 선택지다.

### 코드 — Repository 3종

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String name);
    List<User> findByNameContaining(String name);        // LIKE %name%
}

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(String customerId);
    List<Customer> findByCustomerNameContains(String customerName);
}

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
    boolean existsByStudentNumber(String studentNumber);   // ★ 존재 여부만
}
```

**해설 — 이것이 이 파트에서 가장 놀라운 부분이다**

1. **⚠️ 구현체가 없다. SQL도 없다.** `JpaRepository<엔티티, PK타입>`을 상속하기만 하면 `save`, `findById`, `findAll`, `delete`, `count` 등이 **이미 다 있다.**
2. **메서드 이름이 곧 쿼리다.**
   ```
   findByEmail(String)             → select * from users where email = ?
   findByNameContaining(String)    → ... where name like %?%
   existsByStudentNumber(String)   → select s1_0.student_id from students s1_0 where s1_0.student_number=? limit ?
                                     〔v1.3 정정〕 count(*) 가 아니라 "식별자 하나 + limit" (강의 문서 2-4 실측 로그, 5-1절 ⑥)
   ```
   `find/exists/count/delete` + `By` + `필드명` + `키워드(Containing, GreaterThan, Between, OrderBy...)`
3. **🔗 PART 1 → 2 → 3의 데이터 접근 진화가 여기서 완결된다.**

   | 단계 | `email로 사용자 찾기`를 어떻게 쓰나 |
   |---|---|
   | PART 1 `MemberManager` | `for` 루프 + `equals()` 직접 순회 (10줄) |
   | PART 2 `UserDaoImplJDBC` | `jdbcTemplate.queryForObject("select * from users where userid=?", ...)` + `RowMapper` |
   | PART 2 `UserDaoImpl` (MyBatis) | `session.selectOne("userNS.selectUserById", id)` + XML에 SQL |
   | PART 2 `UserDaoImplMapper` | `userMapper.selectUserById(id)` + XML에 SQL |
   | **PART 3 `UserRepository`** | **`findByEmail(email)` — 선언만 하면 끝** |

   **1-17절에서 예고한 로드맵이 여기서 끝난다.** 각 단계가 무엇을 없앴는지 이 표로 정리해 둘 것.
4. **⚠️ `@Repository`가 `StudentRepository`에만 붙어 있다.** 사실 **셋 다 없어도 된다.** Spring Data JPA가 `JpaRepository` 상속 인터페이스를 자동으로 찾아 Bean으로 등록하기 때문. 일관성 없이 하나만 붙어 있는 것은 실수에 가깝다.
5. **`Optional<T>` 반환 (자료 2.8절)** — Java 8이 도입한 "값이 없을 수도 있음"을 표현하는 래퍼.
   - 🔗 **PART 1 1-14절의 "예외 vs null" 논쟁의 최종 해답이 이것이다.**
     - `Library.findBookByTitle()` → `null` 반환 → 호출부가 null 체크를 **잊을 수 있다**
     - `findByEmail()` → `Optional<User>` → 호출부가 **반드시 값을 꺼내는 코드를 써야** 한다
   - 즉 **"없을 수 있음"을 타입으로 강제 표현**한다.

### 코드 — Optional 활용 4가지 (`CustomerRepositoryTest.java`)

```java
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Rollback(value = false)     // ★ 등록된 데이터를 확인하려고 Rollback 하지 않음
    void testCreate() {
        Customer customer = new Customer();
        customer.setCustomerId("D002");
        customer.setCustomerName("길동3");
        Customer addCustomer = customerRepository.save(customer);
        assertThat(addCustomer).isNotNull();
        assertThat(addCustomer.getCustomerName()).isEqualTo("길동3");
    }

    @Test
    void testFindBy() {
        Optional<Customer> optionalCustomer = customerRepository.findById(1L);
        if(optionalCustomer.isPresent()){                    // ① isPresent + get
            Customer existCustomer = optionalCustomer.get();
            assertThat(existCustomer.getId()).isEqualTo(1L);
        }
        optionalCustomer.ifPresent(customer ->                // ② ifPresent + 람다
                System.out.println(customer.getCustomerName()));
    }

    @Test
    void testFindByNotFound() {
        Customer notFoundCustomer = customerRepository.findByCustomerId("B001")
                .orElseGet(() -> new Customer());            // ③ 없으면 빈 객체
        assertThat(notFoundCustomer.getCustomerId()).isNull();

        Customer notFound = customerRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));  // ④ 없으면 예외
    }
}
```

**해설 — Optional 사용법 4가지가 한 파일에**

| 방식 | 코드 | 언제 |
|---|---|---|
| ① `isPresent()` + `get()` | `if(opt.isPresent()) opt.get()` | **가장 안 좋다.** null 체크와 다를 게 없음 |
| ② `ifPresent(람다)` | `opt.ifPresent(c -> ...)` | 있을 때만 뭔가 할 때 |
| ③ `orElseGet(공급자)` | `opt.orElseGet(() -> new Customer())` | 기본값이 필요할 때 |
| ④ **`orElseThrow(공급자)`** | `opt.orElseThrow(() -> new ...)` | **없으면 오류인 경우. 실무 표준** |

⚠️ `orElse(x)`와 `orElseGet(() -> x)`의 차이: `orElse`는 **값이 있어도 x를 미리 만든다**. 객체 생성 비용이 크면 `orElseGet`을 쓴다.

**테스트 어노테이션**
- **`@SpringBootTest`** — 전체 애플리케이션 컨텍스트를 띄운다. 🔗 PART 2의 `@ExtendWith(SpringExtension.class)` + `@ContextConfiguration` 조합을 **하나로 합친 것.** ⚠️ Repository·Service 등 **모든 계층**을 다 띄우므로 무겁고, 여러 메서드를 하나의 트랜잭션으로 묶으려면 클래스에 `@Transactional`을 **따로 선언**해야 한다(자동으로 안 걸린다).
- **`@DataJpaTest`** — `@SpringBootTest`의 가벼운 버전. **Repository 계층만** 띄운다(Controller·Service는 로딩 안 함). ⚠️ `@Transactional`이 **기본 포함**되어 있어서 테스트가 끝나면 자동 롤백된다. `CustomerRepositoryTest`처럼 Repository 메서드만 검증하는 테스트라면 `@SpringBootTest`보다 이쪽이 더 적합하고 빠르다 — 이 저장소는 `@SpringBootTest`로 통일했지만, 실무에서는 계층별로 나눠 쓴다.
- **⚠️ `@Rollback(value = false)`** — 스프링 테스트는 기본적으로 **끝나면 롤백**한다(DB를 더럽히지 않기 위해). false로 두면 실제로 남는다. 주석대로 "등록된 데이터를 눈으로 확인"하려는 학습용 설정. **실무에서는 쓰지 않는다.**
- `assertThat(...)` — AssertJ. `assertEquals(예상, 실제)`보다 읽기 좋다. 🔗 PART 2는 JUnit의 `assertEquals`를 썼다.

### 코드 — `DatabaseRunner` : 연결 정보 확인

```java
@Component
@Order(1)
@Slf4j                                     // ★ Lombok — logger 필드 자동 생성
public class DatabaseRunner implements ApplicationRunner {

    @Autowired
    DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("DataSource 구현객체는 {} ", dataSource.getClass().getName());
        try (Connection connection = dataSource.getConnection()) {       // ★ try-with-resources
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("DB Product Name = {}", metaData.getDatabaseProductName());
            log.info("DB URL = {}", metaData.getURL());
            log.info("DB Username = {}", metaData.getUserName());
        }
    }
}
```

**해설**
- **`dataSource.getClass().getName()`을 찍는 것이 이 클래스의 핵심 목적**이다. 아무 설정도 안 했는데 출력은 `com.zaxxer.hikari.HikariDataSource` — **HikariCP가 자동으로 붙었다는 증거.**
  🔗 PART 2에서는 `SimpleDriverDataSource`(풀 없음)나 `BasicDataSource`(DBCP2)를 **직접 Bean으로 등록**해야 했다. 자동 설정이 무엇을 해줬는지 눈으로 확인하는 코드.
- **`try (Connection connection = ...)`** — try-with-resources. 블록이 끝나면 `close()`가 자동 호출된다.
  🔗 **PART 1 자료 1.1절이 언급한 "J2SE 7: try-with-resources"가 실제로 쓰인 자리이자, 12장 JDBC의 `finally { conn.close(); }`를 대체한 문법이다.**
- **`@Slf4j`** — Lombok이 `private static final Logger log = LoggerFactory.getLogger(...)`를 자동 생성. `MyRunner`는 이 줄을 직접 썼다. **같은 저장소에 두 방식이 다 있으니 비교할 것.**
- **`@Order(1)`** — Runner가 여러 개일 때 실행 순서. 숫자가 낮을수록 먼저.
  ⚠️ 그런데 `DataInitRunner`도 `@Order(1)`이다. **순서가 보장되지 않는다.** 다른 값을 줘야 한다.

### 코드 — `DataInitRunner` : 초기 데이터 적재

```java
@Component
@Order(1)
@RequiredArgsConstructor                   // ★ final 필드로 생성자 자동 생성
@Slf4j
public class DataInitRunner implements CommandLineRunner {

    private final StudentRepository studentRepository;    // ★ final + 생성자 주입

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        createStudents();
        log.info("Data initialization completed successfully");
    }

    private void createStudents() {
        Student student1 = createStudent("Alice Johnson", "CS001");
        ...
        List<Student> students = studentRepository.saveAll(
                List.of(student1, student2, ..., student8));    // ★ 일괄 저장
        log.info("Created {} students", students.size());
    }

    private Student createStudent(String name, String studentNumber) {
        return Student.builder().name(name).studentNumber(studentNumber).build();
    }
}
```

**해설**

1. **⚠️ `@RequiredArgsConstructor` + `private final`이 이 저장소에서 가장 중요한 패턴이다.**
   ```java
   @RequiredArgsConstructor
   public class DataInitRunner {
       private final StudentRepository studentRepository;
   }
   // ↑ Lombok 이 아래를 자동 생성한다
   public DataInitRunner(StudentRepository studentRepository) {
       this.studentRepository = studentRepository;
   }
   ```
   - **생성자가 하나면 `@Autowired`를 생략할 수 있다**(Spring 4.3+). 그래서 어노테이션이 안 보인다.
   - 🔗 **PART 2 2-2절에서 "생성자 주입이 필드 주입보다 권장된다"고 한 이유 3가지를 전부 만족한다**: `final`로 불변, 순환 참조 조기 발견, Spring 없이 테스트 가능.
   - **`@Autowired` 필드 주입(`MyRunner`, `DatabaseRunner`)과 생성자 주입(`DataInitRunner`, `UserController`, `UserRestController`)이 저장소에 섞여 있다.** 후자가 현재 표준.
2. **`CommandLineRunner` vs `ApplicationRunner`**
   - `CommandLineRunner.run(String... args)` — **원본 문자열 배열** 그대로
   - `ApplicationRunner.run(ApplicationArguments args)` — `--key=value`를 **파싱해서** 제공
   - 인수를 해석해야 하면 후자, 그냥 실행만 하면 전자.
3. **`@Transactional`이 붙은 이유** — 자료 2.10절 "JPA의 모든 데이터 변경은 반드시 트랜잭션 안에서". 8명을 저장하다 5번째에서 실패하면 **전부 롤백**된다.
4. **`saveAll(List.of(...))`** — 한 번에 저장. `List.of()`는 Java 9의 불변 리스트 팩토리.
5. ⚠️ **`existsByStudentNumber`를 만들어 놓고 쓰지 않는다.** `ddl-auto=create`라 매번 테이블이 새로 만들어져 중복이 안 생기기 때문. 하지만 `update`로 바꾸면 **재시작할 때마다 중복 키 오류**가 난다. 이럴 때 쓰라고 만든 메서드다.
   ```java
   if (!studentRepository.existsByStudentNumber(studentNumber)) { ... }   // 이렇게 써야 안전
   ```


---

## 3-5. Web MVC — REST Controller

### 개념

**자동 설정**: `spring-boot-autoconfigure.jar`의 `WebMvcAutoConfiguration`이 Spring MVC 설정을 대신한다. 개발자가 직접 할 일이 없다. 추가 설정이 필요하면 `@Configuration` + `WebMvcConfigurer` 구현.

**`@RestController` = `@Controller` + `@ResponseBody`**

**Jackson이 하는 일**
- **Serialization(직렬화)**: Java 객체 → JSON (`@ResponseBody`)
- **DeSerialization(역직렬화)**: JSON → Java 객체 (`@RequestBody`)
- `ContentNegotiatingViewResolver`가 **Content-type 기준으로 어떤 Converter를 쓸지 결정**한다.

**REST 제약조건 6가지** (자료 3.3절)
1. **Client/Server** — 독립적 진화 가능
2. **Stateless** — 서버가 세션·쿠키 같은 context를 저장하지 않음
3. **Cache** — HTTP 캐시 기능 활용
4. **Layered System** — 보안·로드밸런싱·암호화 계층 추가 가능
5. **Uniform Interface** — 자원 식별, HTTP Method로 표현, self-descriptive 메시지, **HATEOAS**
6. (Code on Demand — 선택)

**REST가 필요한 이유**: 분산 시스템 / 웹 브라우저 이외의 클라이언트(모바일 등)

**정적 리소스 기본 위치** (4곳)
`classpath:/static` · `classpath:/public` · `classpath:/resources/` · `classpath:/META-INF/resources`

### 코드 — `UserRestController.java`

```java
@RestController
@Slf4j
@RequestMapping("/api/users")              // ★ 클래스 레벨 공통 경로
@RequiredArgsConstructor
public class UserRestController {

    private final UserRepository userRepository;

    @PostMapping                            // POST /api/users
    public User createUser(@RequestBody User userDetail){
        return userRepository.save(userDetail);
    }

    @GetMapping("/{id}")                    // GET /api/users/1
    public User getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return getUser(optionalUser);
    }

    // ★ Optional → 객체 or 예외 변환을 한 곳에 모았다
    private static User getUser(Optional<User> optionalUser) {
        return optionalUser.orElseThrow(
                () -> new BusinessException("User Not Found", HttpStatus.NOT_FOUND));
    }

    @GetMapping                             // GET /api/users
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{email}/")                // GET /api/users/a@b.com/
    public User getUserByEmail(@PathVariable String email){
        return getUser(userRepository.findByEmail(email));
    }

    @PatchMapping("/{email}/")              // ★ PATCH — 부분 수정
    public User updateUser(@PathVariable String email, @RequestBody User userDetail) {
        User existUser = getUser(userRepository.findByEmail(email));
        existUser.setName(userDetail.getName());       // 이름만 갱신
        return userRepository.save(existUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        User existUser = getUser(userRepository.findById(id));
        userRepository.delete(existUser);
        return ResponseEntity.ok("Id = " + id + " User가 삭제 되었습니다.");
    }
}
```

**해설 — PART 2의 `RestfulUserController`와 비교하면 발전이 명확하다**

| | PART 2 `RestfulUserController` | PART 3 `UserRestController` |
|---|---|---|
| 매핑 | `@RequestMapping(value=, method=)` 장문 | **`@GetMapping` 등 축약형** |
| 공통 경로 | 매번 `/users` 반복 | **클래스 레벨 `@RequestMapping("/api/users")`** |
| 의존성 | `@Autowired` 필드 | **`@RequiredArgsConstructor` 생성자 주입** |
| 서비스 계층 | `UserService` 경유 | ⚠️ **Repository 직접** |
| 없는 데이터 | 조용히 `null` 반환 | **`orElseThrow` → `BusinessException`** |
| 반환 | `Boolean.TRUE/FALSE` | **객체 / `ResponseEntity`** |

**핵심 포인트**

1. **⚠️ `orElseThrow`로 예외를 던지는 것이 이 컨트롤러의 가장 큰 개선이다.**
   - PART 2에서는 없는 사용자를 조회하면 `null`이 그대로 JSON `null`로 나갔다. 클라이언트는 성공(200)인지 실패인지 알 수 없다.
   - 여기서는 `BusinessException("User Not Found", HttpStatus.NOT_FOUND)`가 던져지고, `@RestControllerAdvice`가 잡아서 **404 + 에러 메시지**로 응답한다(3-7절).
   - 🔗 PART 1 1-14절 → PART 2 → 여기까지 **"조회 실패를 어떻게 표현할 것인가"**가 3파트에 걸쳐 발전한 것.
2. **`private static User getUser(Optional<User>)`로 중복 제거** — 4곳에서 같은 `orElseThrow`를 쓰므로 메서드로 뺐다. `static`인 이유는 인스턴스 상태를 안 쓰기 때문. 🔗 PART 1 1-9절.
3. **`@PatchMapping` vs `@PutMapping`**
   - **PUT** — 리소스 **전체** 교체 (안 보낸 필드는 null이 됨)
   - **PATCH** — **부분** 수정
   - 여기서는 `existUser.setName(...)`만 하므로 PATCH가 맞다. 자료 표에는 PUT만 있지만 **실무에서는 PATCH를 더 많이 쓴다.**
4. **`ResponseEntity<?>`** — 자료 3.7절의 그 타입. **상태 코드 + 헤더 + 바디를 직접 제어**할 수 있다.
   ```java
   ResponseEntity.ok(body)                      // 200
   ResponseEntity.status(HttpStatus.CREATED).body(x)  // 201
   ResponseEntity.noContent().build()           // 204
   ```
   🔗 PART 2에서 `Boolean.TRUE`를 반환하던 것의 정답이 이것이다.
5. **⚠️ `@GetMapping("/{id}")`와 `@GetMapping("/{email}/")`가 충돌 위험이 있다.**
   - `/api/users/1` → `{id}` (Long)
   - `/api/users/a@b.com/` → 끝의 `/` 때문에 구분된다
   - **URL 끝 슬래시로 두 엔드포인트를 구분하는 것은 매우 취약한 설계다.** `/api/users/email/{email}`처럼 경로를 분리하는 게 맞다.
6. **⚠️ Service 계층이 없다.** Controller가 Repository를 직접 쓴다. 🔗 PART 2 `RestfulStudentController`와 같은 문제. 소규모 학습 코드라 가능하지만, 여러 Repository를 조합하는 로직이 생기면 반드시 Service가 필요하다.
7. **`@RequestBody User`가 Entity를 직접 받는다** — 클라이언트가 `id`나 `createdAt`을 임의로 넣어 보낼 수 있다. **DTO를 쓰는 것이 정답**이고, `springboot-student`의 `StudentDTO`가 바로 그 개선판이다(3-6절).

### 코드 — `UserController.java` (Thymeleaf)

```java
@Controller                                 // ★ @RestController 아님
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/thymeleaf")
    public String leaf(Model model) {
        model.addAttribute("name","스프링부트");
        return "leaf";                      // → templates/leaf.html
    }

    @GetMapping("/index")
    public ModelAndView userList() {
        List<User> userList = userRepository.findAll();
        return new ModelAndView("index","users",userList);
    }

    @GetMapping("/signup")
    public String showSignUpForm(@ModelAttribute("userForm") User user) {
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid @ModelAttribute("userForm") User user,   // ★ @Valid
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";              // ★ 폼으로 되돌림
        }
        userRepository.save(user);
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("userForm", user);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id,
                             @Valid @ModelAttribute("userForm") User user,
                             BindingResult result) {
        if (result.hasErrors()) {
            user.setId(id);                 // ★ id 복원 (폼에 없으면 사라지므로)
            return "update-user";
        }
        userRepository.save(user);
        return "redirect:/index";
    }
}
```

**해설**

1. **`@Controller`(뷰 반환)와 `@RestController`(JSON 반환)가 한 프로젝트에 공존한다.** 같은 `User` 데이터를 웹 화면과 API로 각각 제공. 🔗 PART 2 `UserController` / `RestfulUserController` 구조 그대로.
2. **⚠️ `@Valid` + `BindingResult` 조합이 자료 3.13절의 핵심이다.**
   - `@Valid`가 `User`의 `@NotEmpty`, `@Email`을 검사한다
   - 실패하면 `FieldError`가 **`BindingResult`에 담긴다**
   - **`BindingResult`가 없으면 예외가 던져지고, 있으면 컨트롤러 안에서 처리할 수 있다.** 이 차이가 중요하다.
   - ⚠️ **`BindingResult`는 반드시 `@Valid` 붙은 파라미터 바로 뒤에 와야 한다.** 순서가 틀리면 동작하지 않는다.
3. **에러 시 `return "add-user"`로 같은 폼을 다시 보여준다.** Thymeleaf의 `th:errors="*{name}"`이 `BindingResult`의 메시지를 출력한다. **입력값도 유지된다** — `@ModelAttribute("userForm")`으로 모델에 담겨 있으므로.
4. **⚠️ `user.setId(id)`를 다시 넣는 이유**: 수정 폼에 `id` 입력 필드가 없으면 바인딩된 `user`의 `id`가 `null`이다. 검증 실패로 폼을 다시 그릴 때 id가 없으면 `th:action="@{/update/{id}(id=${user.id})}"`가 깨진다. **실전에서 겪어야 알 수 있는 버그를 잡아둔 코드.**
5. **`redirect:/index`** — 🔗 PART 2 2-7절의 PRG 패턴 그대로.

**Thymeleaf 표현식 4종 (자료 3.12절)**

| 표현식 | 용도 |
|---|---|
| `${...}` | **Variable** — Model의 값 |
| `*{...}` | **Selection** — `th:object`로 지정된 객체의 필드 |
| `#{...}` | **Message** — `message.properties` |
| `@{...}` | **Link URL** — URL 생성 |

```html
<html xmlns:th="http://www.thymeleaf.org">    <!-- ★ 반드시 필요 -->
<h1 th:text="${name}">Name</h1>
<h1>Hello, [[${name}]]</h1>                    <!-- 인라인 표현 -->

<tr th:each="user : ${users}">                 <!-- 반복 -->
    <td th:text="${user.name}"></td>
</tr>
<a th:href="@{/edit/{id}(id=${user.id})}">update</a>

<form th:action="@{/adduser}" th:object="${userForm}">
    <input type="text" th:field="*{name}" />   <!-- name 속성+id+value 자동 -->
    <span th:errors="*{name}"></span>          <!-- BindingResult 의 에러 -->
</form>
```

⚠️ **JSP를 권장하지 않는 이유** (자료 3.12절): JAR 패키징에서 동작하지 않아 WAR로 패키징해야 하고, Undertow는 JSP를 지원하지 않는다. 🔗 PART 2가 JSP + WAR + 톰캣이었던 것과 대비.

### 코드 — `WebConfig.java` (MVC 확장)

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mobile/**")        // 반드시 mobile 다음에 / 필요
                .addResourceLocations("classpath:/mobile/")
                .setCachePeriod(20);                     // 20초 캐시
    }
}
```

**해설**
- 기본 4곳(`static`, `public`, `resources`, `META-INF/resources`) **외에** `classpath:/mobile/`을 추가로 매핑한다.
- **`WebMvcConfigurer`를 구현하면 자동 설정을 유지하면서 추가만 한다.** ⚠️ `@EnableWebMvc`를 붙이면 **자동 설정이 꺼지므로** 붙이면 안 된다. 🔗 PART 2 `MvcConfig`는 `@EnableWebMvc`를 붙였다 — Boot가 아니라서 자동 설정이 없었기 때문. **같은 인터페이스인데 부트에서는 어노테이션 하나가 독이 된다.**
- ⚠️ `addResourceHandler("/mobile/**")`에서 `**`를 빠뜨리면 하위 경로가 매칭되지 않는다. 주석이 경고하는 부분.
- **CORS도 같은 클래스에서 설정한다** (자료 3.15절):
  ```java
  @Override
  public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
              .allowedOrigins("http://localhost:3000")
              .allowedMethods("*");
  }
  ```
  **Origin = 스키마 + 호스트 + 포트.** 하나라도 다르면 브라우저가 차단(SOP)하므로 CORS로 허용해야 한다. 메서드 단위로는 `@CrossOrigin(origins="...")`.

---

## 3-6. DTO 패턴 — Entity와 요청/응답의 분리

⚠️ **자료에는 DTO가 `ErrorObject` 정도로만 나오지만, `springboot-student`의 `StudentDTO`는 실무에서 가장 중요한 패턴 중 하나다.**

### 코드 — `controller/dto/StudentDTO.java`

```java
public class StudentDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {                          // ★ 들어오는 데이터
        @NotBlank(message = "Student name is required")
        @Size(max = 100, message = "Student name cannot exceed 100 characters")
        private String name;

        @NotBlank(message = "Student number is required")
        @Size(max = 20, message = "Student number cannot exceed 20 characters")
        private String studentNumber;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {                         // ★ 나가는 데이터
        private Long id;
        private String name;
        private String studentNumber;

        public static Response fromEntity(Student student) {   // ★ 변환 메서드
            return Response.builder()
                    .id(student.getId())
                    .name(student.getName())
                    .studentNumber(student.getStudentNumber())
                    .build();
        }
    }
}
```

**해설 — 왜 Entity를 직접 쓰지 않는가**

1. **⚠️ `Request`에 `id`가 없다.** 클라이언트가 PK를 마음대로 정할 수 없게 막는다. `User` Entity를 `@RequestBody`로 받았던 `UserRestController`는 이게 뚫려 있다.
2. **`Response`에 노출할 필드만 담는다.** 비밀번호·내부 플래그 같은 것이 실수로 나가지 않는다. (`UserInfo` 엔티티를 그대로 반환하면 **암호화된 비밀번호가 JSON에 실린다.**)
3. **검증 규칙이 DTO에 있다.** Entity는 DB 매핑에만 집중한다. 🔗 3-4절에서 지적한 "Entity에 `@NotEmpty`가 붙은 문제"의 해답.
4. **API 스펙과 DB 스키마가 분리된다.** 컬럼명을 바꿔도 API 응답은 그대로 유지할 수 있다.
5. **`static class`로 중첩** — `StudentDTO.Request`, `StudentDTO.Response`로 묶어서 관리. 파일이 늘어나지 않는다. 🔗 PART 1 `ListDemo`의 `static class Product`와 같은 문법.
6. **`fromEntity(Student)`가 static 팩토리 메서드** — Entity → DTO 변환 책임을 DTO가 가진다. 반대 방향(`toEntity()`)도 흔히 만든다.

**⚠️ `@Data`는 주의해서 쓸 것**
`@Data` = `@Getter` + `@Setter` + `@ToString` + `@EqualsAndHashCode` + `@RequiredArgsConstructor`. DTO에는 편하지만 **Entity에 붙이면 위험하다** — `@EqualsAndHashCode`가 모든 필드를 비교하는데, 연관 관계가 있으면 무한 순환이 생긴다. **저장소의 Entity들이 `@Getter @Setter`만 쓰고 `@Data`를 안 쓴 것은 옳은 선택이다.**

---

## 3-7. 예외 처리 — BusinessException · ErrorCode · @RestControllerAdvice

### 개념

**스프링 부트 기본 예외 처리**: `BasicErrorController`가 자동 등록되어 에러를 JSON(또는 HTML)으로 반환.

**커스텀 처리 2가지**
- **`@ExceptionHandler`** — 컨트롤러 **내부** 예외 처리
- **`@ControllerAdvice` / `@RestControllerAdvice`** — **전역** 예외 처리

**HTML 에러 페이지**: `src/main/resources/static/error/404.html`, `5xx.html`처럼 **상태 코드 이름**으로 만든다.

### 코드 — 예외 클래스

```java
// exception/BusinessException.java
@Getter
public class BusinessException extends RuntimeException {      // ★ Unchecked
    private static final long serialVersionUID = 1L;
    private String message;
    private HttpStatus httpStatus;                             // ★ HTTP 상태를 예외가 안다

    public BusinessException(String message) {
        this(message, HttpStatus.EXPECTATION_FAILED);          // 417 기본값
    }
    public BusinessException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
    public BusinessException(ErrorCode errorCode, Object... args) {   // ★ ErrorCode 버전
        this.message = errorCode.formatMessage(args);
        this.httpStatus = errorCode.getHttpStatus();
    }
}
```

```java
// exception/ErrorCode.java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    RESOURCE_NOT_FOUND("%s not found with %s: %s", HttpStatus.NOT_FOUND),
    RESOURCE_DUPLICATE("%s already exists with %s: %s", HttpStatus.CONFLICT),
    RESOURCE_ALREADY_EXISTS("%s already exists: %s", HttpStatus.CONFLICT),
    STUDENT_NUMBER_DUPLICATE("Student already exists with student number: %s", HttpStatus.CONFLICT),
    ISBN_DUPLICATE("Book already exists with ISBN: %s", HttpStatus.CONFLICT);

    private final String messageTemplate;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
```

**해설 — ⚠️ 이 두 클래스가 이 저장소 예외 설계의 정점이다**

1. **`RuntimeException`을 상속(Unchecked)한 이유** 🔗 PART 1 1-14절
   - Checked였다면 모든 Controller 메서드에 `throws BusinessException`을 써야 한다.
   - 웹 애플리케이션에서 예외는 **`@RestControllerAdvice`가 한 곳에서 잡으므로** 중간 계층이 신경 쓸 필요가 없다. **Unchecked가 맞다.**
   - 🔗 PART 1의 `InvalidAgeException`(Checked)과 대비. 콘솔 앱과 웹 앱에서 선택이 달라진다.
2. **예외가 `HttpStatus`를 들고 다닌다.**
   - 🔗 **PART 1 `InsufficientBalanceException`이 `currentBalance`를 필드로 가졌던 것과 정확히 같은 아이디어다.** 예외에 구조화된 정보를 실어 보낸다.
   - 덕분에 Advice가 `e.getHttpStatus()`만 읽으면 되고, `if(예외종류)` 분기가 필요 없다.
3. **`ErrorCode` enum이 하는 일**
   - **메시지 템플릿 + HTTP 상태를 한 쌍으로 묶어** 상수화한다.
   - `String.format`의 `%s`에 인자를 채운다:
     ```java
     new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Student", "id", 5)
     // → "Student not found with id: 5", 404
     ```
   - **에러 메시지가 코드 여기저기 흩어지지 않는다.** 다국어 대응, 에러 코드 문서화가 쉬워진다.
   - `@RequiredArgsConstructor`가 `final` 필드 2개로 enum 생성자를 만들어준다.
   - 🔗 **PART 1 자료 1.1절이 언급한 "J2SE 5.0: Type Safe Enum"의 본격적 활용.** enum이 단순 상수가 아니라 **필드와 메서드를 갖는 객체**라는 것을 보여준다.
4. **생성자 3개 오버로딩** 🔗 PART 1 1-4절 — 메시지만 / 메시지+상태 / ErrorCode. 점진적으로 발전한 흔적이 그대로 남아 있다.

### 코드 — `DefaultExceptionAdvice.java`

```java
@RestControllerAdvice                          // ★ 전역 예외 처리
@Slf4j
public class DefaultExceptionAdvice {

    // ① Spring 6 의 ProblemDetail (RFC 7807)
    @ExceptionHandler(BusinessException.class)
    protected ProblemDetail handleException(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus());
        problemDetail.setTitle("Not Found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // ② JSON 파싱 실패 (요청 본문이 깨졌을 때)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    // ③ 그 밖의 모든 런타임 예외 → 500
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorObject> handleException(RuntimeException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage(e.getMessage());
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(errorObject, HttpStatusCode.valueOf(500));
    }

    // ④ @Valid 검증 실패 → 필드별 에러 맵
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // FieldError 를 순회하며 필드명 → 메시지 맵 구성
        ...
    }
}
```

**해설**

1. **⚠️ `@ExceptionHandler`가 4개인데 순서가 아니라 "구체성"으로 매칭된다.**
   - `BusinessException`은 `RuntimeException`의 자식이다.
   - ③이 `RuntimeException`을 잡지만, **더 구체적인 ①이 먼저 매칭**된다.
   - 🔗 **PART 1 1-14절의 "catch 블록은 자식을 먼저, 부모를 나중에" 규칙과 결과가 같지만 메커니즘이 다르다.** try-catch는 **작성 순서**로, `@ExceptionHandler`는 **타입 계층의 근접도**로 결정된다. 그래서 Advice에서는 순서를 신경 쓰지 않아도 된다.
2. **`ProblemDetail` (Spring 6 / Boot 3 신규)** — RFC 7807 표준 에러 응답 형식.
   ```json
   {
     "type": "about:blank",
     "title": "Not Found",
     "status": 404,
     "detail": "User Not Found",
     "instance": "/api/users/4",
     "errorCategory": "Generic",
     "timestamp": "2026-..."
   }
   ```
   - `instance`는 RFC 7807 표준 필드로, **요청이 들어온 URI**를 자동으로 채워준다. `setTitle`·`setDetail`처럼 개발자가 직접 넣지 않아도 Spring이 요청 컨텍스트에서 채운다 — 어떤 요청에서 이 오류가 났는지 클라이언트/로그에서 바로 알 수 있게 해주는 필드다.
   - `setProperty()`로 표준 외 필드를 추가할 수 있다.
   - **`ErrorObject`(직접 만든 DTO)와 `ProblemDetail`(표준)이 한 클래스에 공존**한다. 표준으로 옮겨가는 과도기 코드.
   - ⚠️ `setTitle("Not Found")`가 **하드코딩**되어 있다. `BusinessException`이 404가 아닐 수도 있는데(기본값은 417) title은 항상 "Not Found"다. `e.getHttpStatus().getReasonPhrase()`를 쓰는 게 맞다.
3. **`ErrorObject` (자료 3.5절)**
   ```java
   @Data
   public class ErrorObject {
       private Integer statusCode;
       private String message;
       private String timestamp;
       public String getTimestamp() {                     // ★ getter 안에서 생성
           LocalDateTime ldt = LocalDateTime.now();
           return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss E a", Locale.KOREA).format(ldt);
       }
   }
   ```
   ⚠️ **`getTimestamp()`가 호출될 때마다 "지금" 시각을 만든다.** 예외 발생 시각이 아니라 **직렬화 시각**이다. 미묘하지만 로그 분석 때 문제가 될 수 있다. 필드에 넣고 생성자에서 정하는 것이 맞다.
4. **`MethodArgumentNotValidException`** — `@Valid`가 REST 컨트롤러(`@RequestBody`)에서 실패할 때 던져지는 예외.
   - ⚠️ **`@ModelAttribute`(폼)에서 `BindingResult`를 받으면 예외가 안 나고, `@RequestBody`에서 `BindingResult` 없이 실패하면 이 예외가 난다.** 3-5절 `UserController`와 대비되는 지점.
   - 필드별로 `{"name": "이름은 필수입니다", "email": "형식이 잘못됨"}` 형태의 맵을 만들어 응답한다.
5. **`log.error(e.getMessage(), e)`** — 두 번째 인자로 예외 객체를 넘기면 **스택 트레이스까지 기록**된다. 메시지만 남기면 원인을 못 찾는다.

**⚠️ 예외 처리의 전체 흐름 정리**

```
Repository.findById(5)  →  Optional.empty()
        ↓ orElseThrow
BusinessException("User Not Found", 404)     ← 컨트롤러에서 발생
        ↓ (Controller 밖으로 전파)
DefaultExceptionAdvice.handleException()      ← @RestControllerAdvice 가 가로챔
        ↓
ProblemDetail (status=404, detail="User Not Found")
        ↓ Jackson 직렬화
클라이언트에 404 + JSON 응답
```

🔗 **PART 2에서는 `@ExceptionHandler`를 컨트롤러마다 붙여야 했다.** 여기서는 한 클래스가 모든 컨트롤러를 커버한다. AOP와 같은 발상(횡단 관심사의 분리)이다.


---

## 3-8. Actuator — 운영 모니터링

### 개념

**역할**: 애플리케이션 운영 환경에 필요한 기능 제공. **Endpoints + Metrics + 모니터링**.

- JMX 또는 HTTP로 접근
- **`shutdown`을 제외한 모든 Endpoint는 기본 활성화**
- 하지만 HTTP 노출은 **`health`, `info`만** 기본 공개
- 전체 공개: `management.endpoints.web.exposure.include=*`
- 접근: `http://localhost:8080/actuator`

**모니터링 도구**: Jconsole(JDK 내장) / VisualVM / **Spring Boot Admin**(Actuator UI)

### 코드 — 설정과 Admin 서버

```properties
# springboot-student/application.properties
management.endpoints.web.exposure.include=*
spring.boot.admin.client.url=http://localhost:8090
```

```java
// MySpringBootAdmin — Admin 서버 프로젝트
@SpringBootApplication
@EnableAdminServer                          // ★ 이 한 줄이 전부
public class MySpringBootAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(MySpringBootAdminApplication.class, args);
    }
}
```

**해설 — 저장소가 2개 프로젝트로 나뉜 이유**

```
MySpringBootAdmin (port 8090)          springboot-student (port 8080)
  @EnableAdminServer                     spring-boot-starter-actuator
  spring-boot-admin-starter-server  ←──  spring-boot-admin-starter-client
                                         spring.boot.admin.client.url=http://localhost:8090
```

- **Client가 기동하면서 Server에 자기 주소를 등록**하고, Server가 Client의 Actuator 엔드포인트를 주기적으로 호출해 화면에 그린다.
- ⚠️ **`MySpringBootAdmin`은 별도로 띄워야 한다.** 두 프로젝트를 동시에 실행해야 화면이 보인다.
- ⚠️ **`management.endpoints.web.exposure.include=*`는 개발용이다.** 운영에서는 `env`, `beans`, `configprops` 같은 엔드포인트가 **환경변수·DB 비밀번호를 노출**할 수 있다. 실무에서는 `health,info,metrics`만 열고 Spring Security로 보호한다.

**주요 엔드포인트**

| | 내용 |
|---|---|
| `/actuator/health` | 애플리케이션·DB 상태 |
| `/actuator/info` | 빌드/버전 정보 |
| `/actuator/metrics` | JVM 메모리, CPU, 요청 수 |
| `/actuator/beans` | 등록된 모든 Bean 목록 |
| `/actuator/env` | 환경변수·프로퍼티 |
| `/actuator/mappings` | URL ↔ 컨트롤러 매핑 |
| `/actuator/loggers` | 로그 레벨 **런타임 변경** |

🔗 **`/actuator/beans`가 학습에 유용하다.** 자동 설정이 등록한 Bean 목록을 볼 수 있어, "PART 2에서 XML로 직접 등록하던 것들이 실제로 등록되어 있는지"를 눈으로 확인할 수 있다.

---

## 3-9. Spring Security

### 개념

**기능**: 웹 시큐리티 + 메서드 시큐리티. Basic / Form / OAuth / LDAP 인증 지원.

**⚠️ 의존성만 추가하면 일어나는 일** (자동 설정)
- `SecurityAutoConfiguration`, `UserDetailsServiceAutoConfiguration`이 동작
- **모든 요청에 인증 요구**
- Username은 `user`, **Password는 실행 시마다 랜덤 생성되어 콘솔에 출력**

**인증 아키텍처 (자료 5.3절 — 반드시 이해할 것)**

```
① 사용자가 Form 으로 id/pw 전송
        ↓
② UsernamePasswordAuthenticationFilter 가 가로챔(intercept)
        ↓
③ UsernamePasswordAuthenticationToken 을 만들어
   AuthenticationManager(구현체 ProviderManager)에게 위임
        ↓
④⑤ AuthenticationProvider 에게 Authentication 객체 전달
        ↓
⑥ UserDetailsService.loadUserByUsername(id) 호출
   → DB 에서 사용자 정보를 UserDetails 객체로 받아옴
        ↓
⑦ AuthenticationProvider 가 입력 pw 와 UserDetails 의 pw 를 비교(PasswordEncoder)
        ↓
⑧ 성공 → Authentication 을 SecurityContextHolder 에 담고
         AuthenticationSuccessHandler 실행
   실패 → AuthenticationFailureHandler 실행
```

**개발자가 구현해야 할 것은 ⑥의 `UserDetailsService`와 그것이 반환할 `UserDetails`뿐이다.** 나머지는 프레임워크가 한다.

### 코드 — ① Entity와 UserDetails

```java
// auth/userinfo/UserInfo.java — DB 에 저장될 사용자
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)  private String name;
    @Column(unique = true, nullable = false) private String email;
    @Column(nullable = false)  private String password;      // ★ 암호화되어 저장
    @Column(nullable = false)  private String roles;         // ★ "ROLE_ADMIN,ROLE_USER"
}
```

```java
// auth/userinfo/UserInfoUserDetails.java — Security 가 이해하는 형태로 변환
public class UserInfoUserDetails implements UserDetails {
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private UserInfo userInfo;

    public UserInfoUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.email = userInfo.getEmail();
        this.password = userInfo.getPassword();
        // "ROLE_ADMIN,ROLE_USER" → Split → Stream → List<GrantedAuthority>
        this.authorities = Arrays.stream(userInfo.getRoles().split(","))
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                .collect(Collectors.toList());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }   // ★ email 을 username 으로

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
```

**해설 — ⚠️ 이 클래스가 어댑터 패턴의 교과서적 사례다**

1. **`UserInfo`(내 도메인)와 `UserDetails`(Security 규약)를 잇는 어댑터**다.
   - Security는 `UserDetails` 인터페이스만 안다.
   - 내 앱은 `UserInfo` Entity를 쓴다.
   - `UserInfoUserDetails`가 **`UserInfo`를 감싸서 `UserDetails`인 척한다.**
   - 🔗 PART 1 1-12절 인터페이스 구현의 실무 사례. **"할 수 있다(can-do)"를 만족시키기 위해 구현한 것.**
2. **`getUsername()`이 `email`을 반환한다.** Security의 "username"은 **로그인 식별자**를 뜻할 뿐 이름이 아니다. 이메일로 로그인하는 설계.
3. **`roles` 문자열 → `List<GrantedAuthority>` 변환**
   ```java
   Arrays.stream("ROLE_ADMIN,ROLE_USER".split(","))     // Stream<String>
         .map(roleName -> new SimpleGrantedAuthority(roleName))  // Stream<SimpleGrantedAuthority>
         .collect(Collectors.toList());                  // List<GrantedAuthority>
   ```
   🔗 **PART 1 자료 1.1절의 "J2SE 8: Stream, Lambda"가 여기서 본격적으로 쓰인다.** `for` 루프로 쓰면:
   ```java
   List<GrantedAuthority> list = new ArrayList<>();
   for (String roleName : userInfo.getRoles().split(",")) {
       list.add(new SimpleGrantedAuthority(roleName));
   }
   ```
   **저장소 전체에서 Stream이 실제로 쓰인 거의 유일한 자리다.** 🔗 PART 2 `HelloBean`의 `@Value("#{'${myNameList}'.split(',')}")`와 발상이 같다(문자열 → 리스트).
4. **⚠️ `is~` 메서드 4개가 전부 `true`다.** 계정 만료·잠금·비밀번호 만료·활성화 여부를 전부 통과시킨다. 실무에서는 `UserInfo`에 `enabled`, `locked` 컬럼을 두고 그 값을 반환한다. **학습용 단순화.**
5. **`roles`를 하나의 문자열 컬럼에 콤마로 저장한 것**도 단순화다. 정석은 별도 테이블 + `@ManyToMany`.

### 코드 — ② UserDetailsService

```java
@Service
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired private UserInfoRepository repository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> optionalUserInfo = repository.findByEmail(username);
        return optionalUserInfo.map(userInfo -> new UserInfoUserDetails(userInfo))
                //userInfo.map(UserInfoUserDetails::new)      ← 메서드 참조로 축약 가능
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }

    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));   // ★ 암호화
        UserInfo savedUserInfo = repository.save(userInfo);
        return savedUserInfo.getName() + " user added!!";
    }
}
```

```java
// auth/userinfo/UserInfoRepository.java
public interface UserInfoRepository extends ListCrudRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
}
```

**해설**

1. **⚠️ `Optional.map().orElseThrow()` 체인이 이 저장소에서 가장 세련된 코드다.**
   ```java
   optionalUserInfo.map(userInfo -> new UserInfoUserDetails(userInfo))   // 있으면 변환
                   .orElseThrow(() -> new UsernameNotFoundException(...)) // 없으면 예외
   ```
   `if (opt.isPresent()) { return new ...(opt.get()); } else { throw ...; }`를 두 줄로 압축한 것.
   주석에 **메서드 참조 버전(`UserInfoUserDetails::new`)**까지 적어두었다. 생성자 참조 문법.
2. **`passwordEncoder.encode()`** — 비밀번호는 **절대 평문 저장 금지**. `BCryptPasswordEncoder`가 단방향 해시로 만든다.
   - ⚠️ BCrypt는 **같은 비밀번호도 매번 다른 해시**를 만든다(salt 포함). 그래서 로그인 시 `encode` 결과를 비교하는 게 아니라 `matches(입력, 저장된해시)`를 쓴다. 이 비교는 `DaoAuthenticationProvider`가 자동으로 한다.
3. **`ListCrudRepository`** (Spring Data 3.0+) — `JpaRepository`보다 가벼운 인터페이스. `findAll()`이 `Iterable`이 아니라 `List`를 반환한다. **인증에는 페이징·정렬이 필요 없으므로 가벼운 것을 선택**한 것.
4. **`UsernameNotFoundException`** — Security 규약상의 예외. 던지면 Security가 인증 실패로 처리한다.
5. ⚠️ **`addUser`가 `UserDetailsService`에 있는 것은 역할 혼재다.** 인증 조회와 회원 등록은 다른 책임. 별도 `UserInfoService`로 빼는 게 맞다.

### 코드 — ③ SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity                       // ★ @PreAuthorize 활성화
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/users/welcome","/userinfos/new").permitAll()
                        .requestMatchers("/api/users/**").authenticated();
                })
                .formLogin(withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**해설**

1. **`SecurityFilterChain`을 `@Bean`으로 반환하는 것이 Spring Security 6 방식**이다. 이전에는 `WebSecurityConfigurerAdapter`를 상속했지만 **deprecated**되었다. 🔗 PART 2 `MvcConfig extends WebMvcConfigurerAdapter`와 같은 흐름(상속 → 컴포지션/람다).
2. **람다 DSL** — `csrf -> csrf.disable()`, `auth -> {...}`. 이것도 Security 6 스타일. 이전의 `.and()` 체인 방식은 deprecated.
3. **⚠️ `csrf.disable()`은 REST API라서 가능하다.**
   - CSRF는 브라우저가 쿠키를 자동 전송하는 것을 악용하는 공격.
   - REST API가 토큰 기반이라면 CSRF 위험이 없다.
   - **하지만 이 코드는 `formLogin()`을 쓰므로 세션 기반이다.** 세션 + CSRF 비활성화는 **실제로 취약하다.** 학습 편의를 위한 설정이고, 실무에서는 켜두거나 완전한 토큰 방식(JWT)으로 가야 한다.
4. **인가 규칙의 순서가 중요하다.**
   ```java
   .requestMatchers("/api/users/welcome","/userinfos/new").permitAll()  // ① 먼저 예외 허용
   .requestMatchers("/api/users/**").authenticated();                   // ② 나머지는 인증
   ```
   ⚠️ **순서를 바꾸면 `/api/users/welcome`이 ②에 먼저 걸려 인증을 요구하게 된다.** 좁은 규칙 먼저, 넓은 규칙 나중. 🔗 PART 1 1-14절 catch 블록 순서와 같은 원리.
   - `/userinfos/new`를 열어둔 이유: **회원가입은 로그인 없이 되어야 하므로.** 닭과 달걀 문제를 푸는 설정.
5. **`DaoAuthenticationProvider`** — 자료 5.3절 흐름도의 ⑤~⑦을 담당하는 구현체. `UserDetailsService`로 사용자를 찾고 `PasswordEncoder`로 비밀번호를 비교한다. **개발자는 두 부품만 끼워주면 된다.**
6. **자료 5.4절의 `InMemoryUserDetailsManager` 방식**도 import에 남아 있다.
   ```java
   UserDetails admin = User.withUsername("adminboot").password(encoder.encode("pwd1"))
                           .roles("ADMIN").build();
   return new InMemoryUserDetailsManager(admin, user);
   ```
   **DB 없이 메모리에 사용자를 두는 방식 → DB 기반으로 발전한 흔적**이다. 자료 5.4 → 5.10의 진행 그대로.

### 코드 — ④ 메서드 보안

```java
// controller/UserRestController.java (springboot 프로젝트)
@GetMapping("/{id}")
@PreAuthorize("hasAuthority('ROLE_USER')")     // ★ 일반 사용자 이상
public User getUserById(@PathVariable Long id) { ... }

@GetMapping
@PreAuthorize("hasAuthority('ROLE_ADMIN')")    // ★ 관리자만
public List<User> getUsers() { ... }
```

```java
// auth/userinfo/UserInfoController.java — 회원 등록 (인증 불필요)
@RestController
@RequestMapping("/userinfos")
public class UserInfoController {
    @Autowired private UserInfoUserDetailsService service;

    @PostMapping("/new")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }
}
```

**해설**

1. **`@EnableMethodSecurity`가 있어야 `@PreAuthorize`가 동작한다.** 없으면 어노테이션이 조용히 무시된다.
2. **`hasAuthority('ROLE_ADMIN')` vs `hasRole('ADMIN')`**
   - `hasRole`은 **`ROLE_` 접두사를 자동으로 붙인다.** 즉 둘은 같은 결과.
   - `UserInfo.roles`에 `"ROLE_ADMIN"`을 통째로 저장했으므로 `hasAuthority`가 짝이 맞다.
   - 권한 없이 호출하면 **403 Forbidden**. (401 Unauthorized는 인증 자체가 안 된 경우 — 둘을 구분할 것)
3. **⚠️ `@PreAuthorize`도 AOP 프록시로 동작한다.** 🔗 PART 2 2-6절 `@Transactional`과 완전히 같은 함정을 공유한다:
   - self-invocation에는 안 걸림
   - private 메서드에는 안 걸림
   **`@Transactional` / `@PreAuthorize` / `@Aspect`가 전부 같은 프록시 메커니즘 위에 있다는 것**이 Spring을 관통하는 이해다.
4. **인가 설정이 두 곳에 나뉜다**
   - `SecurityFilterChain` — **URL 기준** (굵은 그물)
   - `@PreAuthorize` — **메서드 기준** (세밀한 그물)
   - `/api/users/**`는 필터에서 "인증만 되면 통과", 개별 메서드에서 "어떤 권한인지" 다시 검사. **2단 방어.**

### ⚠️ 실행 시나리오

```bash
# 1. 회원 등록 (인증 불필요 — permitAll)
POST /userinfos/new
{"name":"관리자","email":"admin@a.com","password":"pwd1","roles":"ROLE_ADMIN"}

# 2. 로그인 (formLogin)
POST /login  → 세션 발급

# 3. 목록 조회 — ROLE_ADMIN 필요
GET /api/users     → 200 (admin) / 403 (일반 사용자)

# 4. 단건 조회 — ROLE_USER 필요
GET /api/users/1   → ROLE_USER 가 없으면 403
```

⚠️ **`ROLE_ADMIN`만 가진 계정은 `/api/users/1`(ROLE_USER 요구)에 접근할 수 없다.** 역할 계층(role hierarchy)을 설정하지 않았기 때문. 실무에서는 `RoleHierarchy`로 `ADMIN > USER`를 선언하거나, `roles`에 `"ROLE_ADMIN,ROLE_USER"`를 함께 넣는다. `UserInfoUserDetails`가 콤마 split을 하는 이유가 이것이다.

---

## 3-10. 세 파트를 관통하는 최종 대조표

### 같은 기능, 세 가지 시대

| 관심사 | PART 1 (Java) | PART 2 (SpringFW) | PART 3 (Boot) |
|---|---|---|---|
| **데이터 객체** | `Member`, `Product` (getter/setter 직접) | `UserVO` (직접) | `User` **`@Entity` + Lombok** |
| **저장소** | `List<Member>` 메모리 | `UserDao` + JDBC/MyBatis + SQL | `UserRepository extends JpaRepository` |
| **조회 코드** | `for` + `equals` 10줄 | `session.selectOne("userNS.xxx", id)` | **`findByEmail(email)`** |
| **없는 데이터** | `null` 또는 예외 | `null` 또는 예외 | **`Optional` + `orElseThrow`** |
| **객체 생성** | `new` 직접 | **컨테이너가 DI** | 동일 (자동 스캔) |
| **의존성 주입** | — | XML `<property ref>` / `@Autowired` | **`@RequiredArgsConstructor` + final** |
| **비즈니스 로직** | `MemberManager` | `@Service UserServiceImpl` | (생략됨 — Controller 직결) |
| **웹 진입** | `main()` | `@Controller` + `web.xml` | `@RestController` (설정 0) |
| **예외** | `MemberNotFoundException` (Checked) | — | **`BusinessException`(Unchecked) + `ErrorCode`** |
| **예외 처리** | `try-catch` 호출부마다 | `@ExceptionHandler` 컨트롤러마다 | **`@RestControllerAdvice` 전역 1곳** |
| **트랜잭션** | 없음 | `<tx:advice>` / `@Transactional` | **`@Transactional` (자동 설정)** |
| **부가 기능** | 코드에 직접 | **AOP (`@Aspect`)** | 동일 + 자동 |
| **설정** | 없음 | XML 155줄 / Java Config 4클래스 | **`application.properties` 10줄** |
| **검증** | `setter` 안 `if` | 없음 | **`@Valid` + `@NotBlank`** |
| **테스트** | `main` + `println` | JUnit + `@ContextConfiguration` | **`@SpringBootTest`** |
| **배포** | `java 클래스명` | WAR + 톰캣 설치 | **`java -jar` (내장 톰캣)** |
| **인증** | 없음 | 없음 | **Spring Security** |

### ⚠️ 변하지 않은 것

**설계의 뼈대는 처음부터 끝까지 같다.**

```
데이터를 담는 객체   entity/Member  →  vo/UserVO      →  entity/User
저장·조회 담당       MemberManager  →  dao/UserDao    →  repository/UserRepository
비즈니스 로직        MemberManager  →  service/UserService  →  (Service)
외부와 대화          MemberDemo     →  controller/UserController  →  controller/UserRestController
오류 표현            exception/     →  (없음)          →  exception/BusinessException
```

**프레임워크가 바꾼 것은 "누가 객체를 만들고 연결하는가"와 "반복되는 기술 코드를 누가 대신 써주는가"뿐이다.** 역할 분담과 객체지향 원칙 자체는 PART 1에서 배운 그대로다. 이것이 이 문서 전체를 통해 반복해서 짚은 축이다.

### 프록시로 동작하는 것들 — 하나의 메커니즘

| 기능 | 어노테이션 | 공통 함정 |
|---|---|---|
| AOP | `@Aspect` + `@Around` 등 | self-invocation ❌ |
| 트랜잭션 | `@Transactional` | private ❌ |
| 메서드 보안 | `@PreAuthorize` | 인터페이스/상속 가능해야 함 |
| MyBatis Mapper | `@Mapper` 인터페이스 | 구현체 없이 동작 |
| `@Configuration` | `@Bean` 메서드 | CGLIB로 싱글톤 보장 |

**전부 "대상을 감싸는 객체를 런타임에 만들어 호출을 가로챈다"는 하나의 원리 위에 있다.** 🔗 PART 1의 `new BufferedReader(new FileReader(f))`가 그 원리의 가장 단순한 형태다.

---

## 3-11. SpringBoot 파트 자가 점검 질문

**부트 기본**
1. `@SpringBootApplication`을 구성하는 3개 어노테이션과 각 역할은?
2. `@SpringBootApplication` 클래스가 최상위 패키지에 있어야 하는 이유는?
3. `@EnableAutoConfiguration`이 등록할 Bean 목록은 어디에 정의되어 있나?
4. PART 2의 `web.xml` + `beans.xml` + `beans-web.xml`이 부트에서 어떻게 사라졌나? → 3-1절 표

**외부 설정**
5. 프로퍼티 우선순위에서 커맨드라인과 application.properties 중 어느 것이 이기나?
6. `${DB_HOST:127.0.0.1}` 문법의 의미와, 이렇게 쓰는 이유는?
7. `@Value`와 `@ConfigurationProperties`의 차이는?
8. `@ConfigurationProperties` 클래스에 `@Setter`가 반드시 필요한 이유는?
9. `application-prod.properties`와 `application.properties`가 충돌하면?
10. 프로파일을 전환하는 3가지 방법과 우선순위는?

**JPA**
11. ORM / JPA / Hibernate / Spring Data JPA 넷을 각각 한 문장으로 구분하라
12. `ddl-auto`의 5가지 값과, 개발·운영에 각각 적합한 것은?
13. Dialect를 설정해야 하는 이유는?
13-1. `GenerationType.AUTO`를 MariaDB에서 쓰면 실제로 무엇이 추가로 생성되나? `IDENTITY`와 무엇이 다른가?
14. `findByNameContaining`은 어떤 SQL이 되나? 구현체는 누가 만드나?
15. `@DynamicUpdate`가 없으면 어떤 SQL이 나가나?
16. `updatable = false`를 `createdAt`에 붙인 이유는?
16-1. `@Builder`가 해결하는 문제는? 왜 생성자 오버로딩만으로는 부족한가?
16-2. `@NotEmpty`와 `@NotBlank`의 차이는? `" "`(공백 한 칸)이 각각 통과하나?
16-3. `@SpringBootTest`와 `@DataJpaTest`는 무엇이 다른가? `@Transactional`은 어느 쪽에 기본 포함되나?
17. `Optional.orElse()`와 `orElseGet()`의 차이는?
18. PART 1 `Library.findBookByTitle()`의 null 반환 문제를 `Optional`이 어떻게 푸나?
19. `DataSourceRunner`가 찍는 DataSource 구현체 이름은? PART 2와 뭐가 다른가?

**Web·예외**
20. `@RequestBody`로 Entity를 직접 받으면 무엇이 위험한가? DTO가 어떻게 해결하나?
21. `@Valid` + `BindingResult`에서 `BindingResult`가 없으면 무슨 일이 생기나?
22. `@ExceptionHandler`가 여러 개일 때 어떤 기준으로 매칭되나? try-catch와 뭐가 다른가?
23. `BusinessException`이 `RuntimeException`을 상속한 이유는? PART 1의 `InvalidAgeException`과 왜 다른가?
24. `ErrorCode` enum이 해결하는 문제는?
25. `ProblemDetail`이 무엇이고 왜 표준인가? `instance` 필드는 누가 언제 채우나?
26. `ResponseEntity`가 `Boolean.TRUE` 반환보다 나은 이유는?
27. `WebMvcConfigurer`를 구현할 때 `@EnableWebMvc`를 붙이면 안 되는 이유는?
28. PUT과 PATCH의 차이는?

**Security**
29. 인증 흐름 8단계를 순서대로 설명하라. 개발자가 구현할 부분은 어디인가?
30. `UserInfoUserDetails`가 어댑터인 이유는?
31. `hasAuthority('ROLE_ADMIN')`과 `hasRole('ADMIN')`의 관계는?
32. `@PreAuthorize`가 `@Transactional`과 공유하는 함정 2가지는?
33. `csrf.disable()`이 이 코드에서 왜 위험할 수 있나?
34. 401과 403의 차이는?
35. `permitAll()`과 `authenticated()`의 순서를 바꾸면?

**통합**
36. `Member`(PART1) → `UserVO`(PART2) → `User`(PART3)의 변화를 코드로 설명하라
37. "이메일로 사용자 찾기"를 세 파트가 각각 어떻게 구현했나? → 3-4절 표
38. 프록시로 동작하는 스프링 기능 5가지를 들고, 공통 함정을 설명하라
