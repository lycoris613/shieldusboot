# PART 2. Spring Framework

## 2-0. 저장소 개요

```
springfw/
├─ MySpringFW/       ← 순수 DI 개념 학습 (DB·웹 없음, spring-context + spring-test 만)
│  main/java/myspring/di/xml/      Hello · Printer      — XML 로 조립
│  main/java/myspring/di/annot/    HelloBean · PrinterBean — @Component 로 조립
│  main/java/myspring/di/*/config/ HelloConfig          — @Configuration 으로 조립
│  main/java/mylab/               제출 과제 4종 (order/user/notification/student)
│  main/resources/                hello-bean.xml, hello-annot.xml, values.properties
│  test/java/                     JUnit5 + SpringExtension
│
├─ SpringFWXml/      ← 같은 웹앱을 전부 XML 로 설정
│  main/resources/beans.xml       루트: DataSource·Tx·AOP·MyBatis·서비스/DAO 스캔
│  main/resources/beans-web.xml   서블릿: Controller 스캔·ViewResolver·<mvc:annotation-driven>
│  main/resources/*Mapper.xml     MyBatis SQL
│  main/webapp/WEB-INF/web.xml    DispatcherServlet, ContextLoaderListener, 인코딩 필터
│
└─ SpringFWConfig/   ← 완전히 같은 웹앱을 전부 Java @Configuration 으로 설정
   main/java/myspring/config/AppConfig / DatabaseConfig / MyBatisConfig / MvcConfig
```

- 빌드: Maven, Spring **5.2.15.RELEASE**, Java 1.8
- 테스트: `MySpringFW`는 JUnit 5(Jupiter)+`SpringExtension`, 나머지 둘은 JUnit 4
- DB: MariaDB, WAS: Tomcat

**⚠️ 이 저장소를 읽는 핵심 요령**
`SpringFWXml`과 `SpringFWConfig`는 **자바 코드가 거의 동일하고 설정 방식만 다르다.** 실제 `diff` 결과 차이나는 파일은 4개뿐이다.
- `myspring/config/*` — SpringFWConfig에만 존재 (Java Config)
- `UserServiceImpl` — SpringFWConfig에만 `@Transactional`이 붙어 있음
- `UserMapper` — `@MyMapper`(직접 만든 빈 어노테이션) vs `@Mapper`(MyBatis 제공)
- `StudentMapper` — 위와 같은 차이

**즉 "같은 애플리케이션을 XML로 조립할 때와 Java로 조립할 때 무엇이 어떻게 달라지는가"를 정면 비교하도록 만들어진 저장소다.**

---

## 2-1. 프레임워크와 Spring의 개념

### 개념

**프레임워크란**: 성능·보안·확장성 같은 **비기능적 요구사항**을 만족하는 구조와 기능을 안정적으로 실행하도록 제어해 주는 라이브러리 덩어리. 애플리케이션들의 공통점을 찾아 하부 구조를 제공한다.

**⚠️ 라이브러리 vs 프레임워크 — 시험 단골**

| | 실행 제어의 주체 |
|---|---|
| **라이브러리** | **개발자 코드**가 라이브러리를 호출한다 |
| **프레임워크** | **프레임워크**가 개발자가 만든 클래스를 호출한다 |

이 역전이 곧 **IoC(제어의 역전)**다.

**프레임워크의 3구성요소**: IoC + 클래스 라이브러리 + 디자인 패턴
- 프레임워크는 "Semi Complete(반제품)"이다.
- 디자인 패턴은 구조적 가이드라인만, 프레임워크는 **패턴 + 그 패턴이 적용된 기반 코드**를 함께 제공한다.

**Spring Framework**: 자바 엔터프라이즈 개발을 편하게 해주는 **오픈소스 경량급 애플리케이션 프레임워크**
- **애플리케이션 프레임워크** — 특정 계층/기술에 국한되지 않고 전 영역 포괄
- **경량급** — 단순한 웹 컨테이너에서도 고급 기술 사용 가능
- **엔터프라이즈 개발 용이** — Low Level에 신경 쓰지 않고 비즈니스 로직에 전념

**Spring 삼각형 (4대 전략)**

| 전략 | 내용 |
|---|---|
| **PSA** (Portable Service Abstraction) | 트랜잭션·OXM·데이터 액세스 예외 변환 등 기술 복잡함을 추상화로 분리 |
| **DI** | 확장 가능한 객체를 만들고 관계는 **외부에서 동적으로** 설정 |
| **AOP** | 로직 코드에 남은 기술 관련 코드를 **별도 모듈로** 분리 |
| **POJO** | 특정 환경·규약에 종속되지 않는 순수 자바 객체 |

**Spring 기능 요소**: Core 컨테이너(BeanFactory) / Context / DAO(JDBC 추상화) / ORM(MyBatis·Hibernate·JPA 연동) / AOP / Web / WebMVC

### 코드로 보는 POJO

**`myspring/di/xml/Hello.java`** — Spring을 쓰는데 Spring 코드가 한 줄도 없다

```java
package myspring.di.xml;

public class Hello {                     // ★ 어떤 인터페이스도 구현하지 않음
    String name;
    Printer printer;                     // ★ 어떤 Spring 클래스도 상속하지 않음
    List<String> names;

    public Hello() {
        System.out.println(this.getClass().getName() + "기본생성자 호출됨!");
    }
    public Hello(String name, Printer printer) {
        System.out.println(this.getClass().getName() + "오버로딩 생성자 호출됨!");
        setName(name);
        setPrinter(printer);
    }
    public void setName(String name) {
        System.out.println("setName() 메서드 호출됨" + name);
        this.name = name;
    }
    public void setPrinter(Printer printer) {
        System.out.println("SetPrinter() 메서드 호출됨" + printer.getClass().getName());
        this.printer = printer;
    }
    public String sayHello() { return "Hello " + name; }
    public void print() { this.printer.print(sayHello()); }
}
```

**해설 — 이 파일이 POJO의 정의 그 자체다**
- `import org.springframework...`이 **하나도 없다.** Spring 없이도 `new Hello()`로 그냥 쓸 수 있는 순수 자바 클래스다.
- 그런데도 Spring이 이 객체를 만들고 의존성을 넣어준다. **이것이 "특정 환경이나 규약에 종속되지 않는다"의 의미.**
- 🔗 PART 1의 `Product`, `Member`와 구조가 완전히 같다. **1-3절 캡슐화 그대로 필드 + 생성자 + setter.**
- 생성자와 setter에 `println`이 박혀 있는 이유: **컨테이너가 언제 무엇을 호출하는지 눈으로 확인하기 위해서다.** 테스트를 돌리면 `기본생성자 호출됨 → setName() 호출됨 → SetPrinter() 호출됨` 순서가 찍힌다. Setter Injection의 동작이 그대로 보인다.

**`Printer` 인터페이스와 두 구현체**

```java
public interface Printer {
    public void print(String message);
}

public class ConsolePrinter implements Printer {      // 화면에 출력
    public void print(String message) { System.out.println(message); }
}

public class StringPrinter implements Printer {       // 내부 버퍼에 쌓아둠
    private StringBuffer buffer = new StringBuffer();
    public void print(String message) { this.buffer.append(message); }
    public String toString() { return this.buffer.toString(); }
}
```

**해설 — 왜 인터페이스를 두는가**
- `Hello`는 `Printer` **인터페이스**에만 의존한다. `ConsolePrinter`인지 `StringPrinter`인지 모른다.
- 그래서 **설정만 바꾸면 구현을 갈아끼울 수 있다.** 이것이 DI의 목적인 **결합도 제거**.
- `StringPrinter`가 버퍼에 쌓는 이유: **테스트에서 출력 결과를 검증하기 위해서다.**
  ```java
  hello1.print();
  Printer printer = context.getBean("strPrinter", Printer.class);
  assertEquals("Hello 스프링", printer.toString());   // ★ 출력 내용을 검증할 수 있다
  ```
  `ConsolePrinter`는 화면에 찍고 끝이라 검증이 불가능하다. **테스트 가능성이 인터페이스 분리의 실제 이득**이라는 것을 보여주는 설계.
- 🔗 PART 1의 `Shape`/`Printer`(chapter5) 추상화와 같은 구조. 다만 여기서는 **구현체 선택을 컨테이너가 한다.**

---

## 2-2. IoC와 DI — 3가지 조립 방법

### 개념

**IoC**: 객체의 생성·생명주기 관리 제어권이 개발자가 아닌 **컨테이너**에게 있는 것.

**IoC의 두 갈래**
- **DL (Dependency Lookup)** — 컨테이너 API로 Bean을 **검색**해서 가져옴 (`getBean()`)
- **DI (Dependency Injection)** — 컨테이너가 의존 관계를 **자동으로 연결**해 줌 ← Spring은 주로 이것

**DI의 3유형**: Setter Injection / Constructor Injection / Method Injection

**핵심 용어**

| 용어 | 뜻 |
|---|---|
| **Bean** | 스프링이 IoC 방식으로 생성·관리하는 객체 |
| **BeanFactory** | IoC 핵심 컨테이너. Bean 등록·생성·조회·반환. `getBean()` 보유 |
| **ApplicationContext** | BeanFactory를 **확장**한 것. 부가 서비스 추가 제공. **실무에서 주로 이것** |
| **설정 메타정보** | 컨테이너가 IoC를 적용하기 위해 사용하는 정보 (XML/어노테이션/Java) |

**Bean 등록 3전략**

| 전략 | 방법 | 장점 | 단점 |
|---|---|---|---|
| **(1) XML 단독** | `<bean>` 태그로 전부 명시 | **모든 Bean을 한눈에** 확인 | Bean이 많아지면 XML 관리 부담, 협업 시 충돌 |
| **(2) 어노테이션 + XML** (2.5~) | `@Component` + `<context:component-scan>` | XML 관리 수고 감소, 개발 속도 향상 | **전체 Bean과 의존관계를 한눈에 못 봄** |
| **(3) Java Config 단독** (3.0~) | `@Configuration` + `@Bean` | XML 전혀 불필요, **타입 안전** | 설정도 컴파일 대상 |

**Bean 등록 어노테이션** (전부 `@Component`의 구체화)

| | 용도 |
|---|---|
| `@Component` | 일반 스테레오 타입. `<bean>`과 동일 |
| `@Repository` | 퍼시스턴스 레이어 (파일·DB 접근) |
| `@Service` | 서비스 레이어 (비즈니스 로직) |
| `@Controller` | 프리젠테이션 레이어 (웹 요청/응답) |

**의존관계 주입 어노테이션**

| | 연결 기준 | 적용 위치 |
|---|---|---|
| `@Autowired` | **Type** | 변수, setter, 생성자, 일반 메서드 |
| `@Resource` | **Name** | 변수, setter |
| `@Value` | 단순 값 | 변수 |
| `@Qualifier` | `@Autowired`와 함께, **같은 타입 Bean이 여러 개일 때** 이름으로 특정 |

⚠️ **`@Autowired`는 타입으로, `@Resource`는 이름으로.** 이 한 줄이 시험에 계속 나온다.

### 코드 — 전략 1: XML 단독

**설정: `MySpringFW/src/main/resources/hello-bean.xml`**

```xml
<!-- StringPrinter 클래스를 SpringBean으로 설정 -->
<bean id="strPrinter" class="myspring.di.xml.StringPrinter" />
<!-- ConsolePrinter 클래스를 SpringBean으로 설정 -->
<bean id="conPrinter" class="myspring.di.xml.ConsolePrinter" />

<!-- Setter Injection 방식 -->
<bean id="hello" class="myspring.di.xml.Hello" scope="singleton">
    <!-- setName("스프링") 메서드 호출 -->
    <property name="name" value="스프링" />
    <!-- setPrinter(new StringPrinter()) 메서드 호출 -->
    <property name="printer" ref="strPrinter" />
</bean>

<!-- Constructor Injection 방식 -->
<bean id="helloC" class="myspring.di.xml.Hello">
    <constructor-arg index="0" value="생성자" />
    <constructor-arg index="1" ref="conPrinter" />
</bean>
```

**해설 — XML 한 줄 한 줄이 자바 코드로 무엇인가**

| XML | 대응하는 자바 코드 |
|---|---|
| `<bean id="strPrinter" class="...StringPrinter"/>` | `StringPrinter strPrinter = new StringPrinter();` |
| `<property name="name" value="스프링"/>` | `hello.setName("스프링");` |
| `<property name="printer" ref="strPrinter"/>` | `hello.setPrinter(strPrinter);` |
| `<constructor-arg index="0" value="생성자"/>` | `new Hello("생성자", conPrinter)` |

⚠️ **`value`와 `ref`의 차이가 핵심이다.**
- `value` — 단순 값(문자열·숫자). Bean이 아님
- `ref` — **다른 Bean의 id**를 참조

⚠️ **`<property name="name">`의 `name`은 필드명이 아니라 setter 이름에서 온다.** `name` → `setName()`을 찾아 호출한다. 그래서 setter가 없으면 실패한다.

⚠️ **JavaBeans 네이밍 규칙에는 잘 알려지지 않은 예외가 하나 있다.** 필드명의 **첫 두 글자가 모두 대문자**면 getter/setter에서 첫 글자를 소문자로 바꾸는(decapitalize) 규칙이 적용되지 않는다.
```java
String myFirstName;         // O  →  getMyFirstName() / setMyFirstName()
String NAME;                 // X  →  대문자 필드명 자체가 관례 위반
String URL;                  // getURL() (getUrl() 아님) — 첫 두 글자가 대문자라 그대로 유지됨
```
`<property name="myFirstName">`처럼 XML에서 프로퍼티명을 쓸 때, 필드명이 이 규칙에 걸리는 경우(`URL`, `ID` 같은 약어) **getter/setter 이름이 예상과 달라질 수 있다.** 흔히 겪는 일은 아니지만, `@ConfigurationProperties`나 `@ModelAttribute` 바인딩이 약어가 들어간 필드에서 안 될 때 이 규칙을 의심해볼 것.

**같은 `Hello` 클래스로 Bean 2개(`hello`, `helloC`)를 만든 것**도 눈여겨볼 것. **클래스 하나 = Bean 하나가 아니다.** 설정만 다르면 몇 개든 만들 수 있다.

**사용: `test/java/myspring/di/xml/HelloBeanJunitTest.java`**

```java
public class HelloBeanJunitTest {
    ApplicationContext context;

    @BeforeEach
    void setup( ) {
        context = new GenericXmlApplicationContext("classpath:hello-bean.xml");
    }

    @Test
    void helloBeanSetter() {
        Hello hello1 = (Hello)context.getBean("hello");            // DL 방식 ①
        Hello hello2 = context.getBean("hello", Hello.class);      // DL 방식 ② (캐스팅 불필요)
        System.out.println(hello1 == hello2);
        assertSame(hello1, hello2);                                 // ★ 싱글톤 증명
        assertEquals("Hello 스프링", hello1.sayHello());
        hello1.print();
        Printer printer = context.getBean("strPrinter", Printer.class);
        assertEquals("Hello 스프링", printer.toString());           // ★ 주입된 printer 에 쌓였다
    }
}
```

**해설 — 이 테스트가 증명하는 3가지**
1. **`assertSame(hello1, hello2)`** — 같은 id로 두 번 꺼내도 **같은 객체**다. Spring Bean의 기본 스코프가 **싱글톤**임을 증명한다. `assertEquals`가 아니라 `assertSame`을 쓴 것이 포인트(🔗 자료 2.7절: `assertEquals`는 값, `assertSame`은 레퍼런스).
2. **`hello1.print()` 후 `printer.toString()`이 "Hello 스프링"** — `hello`에 주입된 `strPrinter`와 컨테이너에서 꺼낸 `strPrinter`가 **같은 객체**라는 뜻. DI가 실제로 일어났다는 증거.
3. `getBean("hello")`는 `Object`를 반환하므로 캐스팅이 필요하고, `getBean("hello", Hello.class)`는 타입까지 지정하므로 캐스팅이 불필요하다. **후자를 쓰는 게 좋다.**

⚠️ 이 방식은 **DL(Dependency Lookup)**이다. `getBean()`을 직접 호출하므로. 아래 Spring-Test 방식이 **DI**다.

**Spring-Test 방식: `HelloBeanSpringTest.java`**

```java
@ExtendWith(SpringExtension.class)                         // JUnit5 확장 (JUnit4는 @RunWith)
@ContextConfiguration(locations = "classpath:hello-bean.xml")
public class HelloBeanSpringTest {

    @Autowired
    @Qualifier("helloC")     // ★ Hello 타입 Bean 이 2개(hello, helloC)이므로 이름으로 특정
    Hello hello;

    @Resource(name = "hello") // ★ @Resource 는 이름으로 연결 → Qualifier 불필요
    Hello helloSetter;

    @Test
    void helloBeanCons(){
        assertEquals("Hello 생성자", hello.sayHello());       // helloC (Constructor Injection)
        assertEquals("Hello 스프링", helloSetter.sayHello()); // hello  (Setter Injection)
        hello.print();
    }
}
```

**해설 — 이 파일이 `@Autowired`와 `@Resource`의 차이를 정확히 보여준다**
- `Hello` 타입 Bean이 **`hello`와 `helloC` 두 개** 있다.
- `@Autowired`는 **타입으로** 찾으므로 어느 것인지 모른다 → `NoUniqueBeanDefinitionException` 발생 → **`@Qualifier("helloC")`로 이름을 지정**해야 한다.
- `@Resource(name="hello")`는 **처음부터 이름으로** 찾으므로 추가 어노테이션이 필요 없다.
- ⚠️ 자료의 "동일한 타입의 Bean이 여러 개 존재할 때 @Qualifier를 같이 사용해야 한다"가 **이 코드로 실증된다.**
- `getBean()`이 사라졌다. 컨테이너가 필드에 직접 주입 → **DL이 아니라 DI**.

### 코드 — 전략 2: 어노테이션 + XML

**설정: `hello-annot.xml`** (단 두 줄)

```xml
<!-- DI전략2에 해당하는 Configuration file -->
<context:component-scan base-package="myspring.di.annot" />
<!-- properties file의 정보 설정 -->
<context:property-placeholder location="classpath:values.properties" />
```

**클래스: `myspring/di/annot/`**

```java
@Component("stringPrinter")                       // <bean id="stringPrinter" .../> 와 동일
public class StringPrinterBean implements PrinterBean {
    private StringBuffer buffer = new StringBuffer();
    public void print(String message) { this.buffer.append(message); }
    public String toString() { return this.buffer.toString(); }
}

@Component("consolePrinter")
public class CosnolePrinterBean implements PrinterBean { ... }

@Component("helloBean")
public class HelloBean {
    @Value("${myName}")                            // properties 파일의 값 주입
    String name;

    @Autowired
    @Qualifier("stringPrinter")                    // PrinterBean 이 2개이므로 이름 특정
    PrinterBean printer;

    @Value("#{'${myNameList}'.split(',')}")        // ★ SpEL 로 문자열을 List 로
    List<String> names;

    public String sayHello() { return "Hello " + name; }
    public void print() { this.printer.print(sayHello()); }
}
```

**`values.properties`**
```properties
myName=어노테이션
myName2=어노테이션생성자
myNameList=Java,SpringFW,SpringBoot
```

**해설 — XML 20줄이 어노테이션 3개로 줄었다**
- `<bean>` 태그가 전부 사라지고 **클래스 위의 `@Component`가 대신**한다.
- `<property value="...">` → **`@Value`**
- `<property ref="...">` → **`@Autowired` + `@Qualifier`**
- **⚠️ `@Value("#{'${myNameList}'.split(',')}")`가 이 파일의 백미다.**
  - `${myNameList}` — 프로퍼티 치환자. `values.properties`에서 `Java,SpringFW,SpringBoot`를 꺼낸다
  - `#{ ... }` — **SpEL(Spring Expression Language)**. 표현식을 실행한다
  - `.split(',')` — 문자열을 배열로 쪼갠다 → `List<String>`에 주입
  - 즉 **XML의 `<list><value>...</value></list>` 3줄을 문자열 프로퍼티 + SpEL 한 줄로 대체**한 것.

**생성자 주입 버전: `HelloBeanCons.java`**

```java
@Component("helloBean")
public class HelloBeanCons {
    String name;
    PrinterBean printer;

    public HelloBeanCons() { ... }

    @Autowired                                      // ★ 생성자에 붙인다
    public HelloBeanCons(@Value("${myName2}") String name,
                         @Qualifier("consolePrinter") PrinterBean printer) {
        this.name = name;
        this.printer = printer;
    }
}
```

**해설**
- `@Value`와 `@Qualifier`를 **생성자 파라미터에** 붙였다. XML의 `<constructor-arg>`에 대응.
- ⚠️ 이쪽이 `HelloBean`(필드 주입)보다 **권장되는 방식**이다. 이유:
  1. 필드를 `final`로 만들 수 있어 **불변 보장**
  2. 순환 참조를 **컴파일/기동 시점에** 발견
  3. Spring 없이 `new HelloBeanCons(...)`로 **테스트하기 쉬움**
- 🔗 PART 3에서는 Lombok `@RequiredArgsConstructor`가 이 생성자를 자동 생성해준다.

**⚠️ 두 클래스 모두 `@Component("helloBean")`으로 같은 이름을 쓰고 있다.** 실제로 둘 다 스캔되면 이름 충돌이 난다. 테스트에서도 `helloBeanAnnot()`에 `@Disabled`가 붙어 있는 것을 보면, 둘을 번갈아 실험하며 만든 코드로 보인다.

### 코드 — 전략 3: Java Config 단독

**`myspring/di/annot/config/HelloBeanConfig.java`** — 스캔 방식

```java
@Configuration
@ComponentScan(basePackages = {"myspring.di.annot"})      // <context:component-scan>
@PropertySource(value = "classpath:values.properties")     // <context:property-placeholder>
public class HelloBeanConfig {
}
```

**해설**: 클래스 몸통이 **비어 있다.** `hello-annot.xml`을 자바 클래스로 그대로 옮긴 것. XML 파일이 완전히 사라졌다.

**`myspring/di/xml/config/HelloConfig.java`** — 직접 조립 방식

```java
@Configuration
@PropertySource(value="classpath:values.properties")
public class HelloConfig {

    @Autowired
    Environment env;                       // 프로퍼티 값을 읽는 Spring 객체

    @Bean
    public Printer stringPrinter() { return new StringPrinter(); }

    @Bean
    public Printer consolePrinter() { return new ConsolePrinter(); }

    @Bean @Scope(value = "singleton")
    public Hello hello() {
        Hello hello = new Hello();
        String propName = env.getProperty("myName");
        hello.setName(propName);                 // <property name="name" .../>
        hello.setPrinter(stringPrinter());       // <property name="printer" ref="strPrinter"/>
        return hello;
    }
}
```

**해설 — XML의 각 태그가 자바 코드로 1:1 대응된다**

| XML | Java Config |
|---|---|
| `<bean id="strPrinter" class="StringPrinter"/>` | `@Bean public Printer stringPrinter()` — **메서드 이름이 Bean id** |
| `<property name="name" value="스프링"/>` | `hello.setName(propName);` |
| `<property name="printer" ref="strPrinter"/>` | `hello.setPrinter(stringPrinter());` |
| `scope="singleton"` | `@Scope(value = "singleton")` |
| `<context:property-placeholder>` | `@PropertySource` + `Environment.getProperty()` |

**⚠️ `hello()` 안에서 `stringPrinter()`를 직접 호출하는데 왜 매번 새 객체가 안 만들어지는가?**
`@Configuration` 클래스는 Spring이 **CGLIB으로 프록시**를 만들어서, `@Bean` 메서드 호출을 가로채 **이미 만들어진 싱글톤을 반환**한다. 그냥 자바 메서드 호출처럼 보이지만 아니다. 이것이 `@Configuration`과 `@Component`의 결정적 차이다.

**⚠️ 이 파일에는 실수가 남아 있다.**
```java
            <!-- setName("스프링") 메서드 호출 -->
            <property name="name" value="스프링" />
```
XML 조각이 자바 코드 중간에 그대로 붙어 있다. **주석 처리가 안 된 XML이라 컴파일이 되지 않는다.** XML → Java Config로 옮기다 만 흔적. 실제로 쓰려면 지우거나 `/* */`로 감싸야 한다.

**테스트: 로더가 달라진다**

```java
// XML 설정 로드
@ContextConfiguration(locations = "classpath:hello-bean.xml")

// Java Config 로드 ①
@ContextConfiguration(classes = HelloConfig.class)

// Java Config 로드 ② — loader 명시 (자료 2.12절)
@ContextConfiguration(classes = HelloBeanConfig.class,
                      loader = AnnotationConfigContextLoader.class)
```

⚠️ Spring 3.1 이후에는 `classes` 속성만 써도 자동으로 `AnnotationConfigContextLoader`가 적용되므로 `loader`는 생략 가능하다. 자료에 나온 명시적 방식과 생략 방식이 **저장소에 둘 다 있다.**

### 3전략 정면 비교표

| 항목 | 전략1 (XML) | 전략2 (어노테이션+XML) | 전략3 (Java Config) |
|---|---|---|---|
| Bean 등록 | `<bean id class>` | `@Component("id")` | `@Bean` (메서드명=id) |
| 값 주입 | `<property value>` | `@Value` | `setter` 직접 호출 |
| 참조 주입 | `<property ref>` | `@Autowired`+`@Qualifier` | 메서드 직접 호출 |
| 생성자 주입 | `<constructor-arg>` | 생성자에 `@Autowired` | `new X(...)` |
| 스캔 | — | `<context:component-scan>` | `@ComponentScan` |
| 프로퍼티 | `<context:property-placeholder>` | 동일 | `@PropertySource` |
| 스코프 | `scope="singleton"` | `@Scope` | `@Scope` |
| 컨텍스트 로드 | `GenericXmlApplicationContext` | 동일 | `AnnotationConfigApplicationContext` |
| **한눈에 보기** | ⭕ 최고 | ❌ 흩어짐 | ⭕ 좋음 |
| **타입 안전** | ❌ 문자열 | ⭕ | ⭕ |
| **외부 라이브러리 Bean 등록** | ⭕ | ❌ (소스 수정 불가) | ⭕ |

⚠️ **마지막 행이 실무에서 중요하다.** `@Component`는 **내가 소스를 고칠 수 있는 클래스**에만 붙일 수 있다. 외부 라이브러리 클래스(예: `DataSource`)를 Bean으로 등록하려면 XML의 `<bean>`이나 Java Config의 `@Bean`을 써야 한다. 그래서 **실무는 `@Component` + `@Bean` 혼용**이 표준이다. `SpringFWConfig`의 `DatabaseConfig`가 정확히 이 이유로 존재한다.


---

## 2-3. 제출 과제 4종 — DI 유형별 연습

`MySpringFW/src/main/java/mylab/`의 4개 과제가 **DI의 각 유형을 하나씩** 담당한다.

| 과제 | 연습하는 것 | 설정 위치 |
|---|---|---|
| `order` | **Setter + Constructor + List 컬렉션 주입** | `mylab-order-di.xml` |
| `student` | **Setter + List 주입 + 비즈니스 로직** | `mylab-student-di.xml` |
| `user` | **어노테이션 스캔 + `@Autowired` + `@Value`** | `mylab-user-di.xml` |
| `notification` | **Java Config `@Bean`으로 생성자 주입** | `NotificationConfig.java` |

### ① order — XML 3가지 주입을 한 파일에

```java
// Product.java — 기본 생성자 + 인자 생성자 + setter 를 모두 갖춘 이유
public class Product {
    private String id, name;
    private double price;
    public Product() {}                                            // setter 주입용
    public Product(String id, String name, double price) { ... }   // 생성자 주입용
    // getter/setter 전부
}

// ShoppingCart.java
public class ShoppingCart {
    private List<Product> products;
    public void setProducts(List<Product> products) { this.products = products; }
    public double getTotalPrice() {
        double total = 0.0;
        for (Product product : products) total += product.getPrice();
        return total;
    }
}

// OrderService.java
public class OrderService {
    private ShoppingCart shoppingCart;
    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }
    public double calculateOrderTotal() { return shoppingCart.getTotalPrice(); }
}
```

**설정: `mylab-order-di.xml`**

```xml
<!-- product1 : setter injection -->
<bean id="product1" class="mylab.order.di.xml.Product">
    <property name="id" value="P001" />
    <property name="name" value="노트북" />
    <property name="price" value="150000" />
</bean>

<!-- product2 : constructor injection -->
<bean id="product2" class="mylab.order.di.xml.Product">
    <constructor-arg value="P002" />
    <constructor-arg value="스마트폰" />
    <constructor-arg value="800000" />
</bean>

<!-- ★ List 컬렉션 주입 -->
<bean id="shoppingCart" class="mylab.order.di.xml.ShoppingCart">
    <property name="products">
        <list>
            <ref bean="product1" />
            <ref bean="product2" />
        </list>
    </property>
</bean>

<bean id="orderService" class="mylab.order.di.xml.OrderService">
    <property name="shoppingCart" ref="shoppingCart" />
</bean>
```

**해설 — 자료 2.5절 "컬렉션 타입의 값 주입"이 여기 있다**
- `<list>` 안에 **`<value>`가 아니라 `<ref>`**를 썼다. 단순 값 리스트가 아니라 **Bean 참조 리스트**이기 때문.
  - `<value>서울</value>` → `List<String>`
  - `<ref bean="product1"/>` → `List<Product>`
- `Set` 타입이면 `<list>` 대신 `<set>`, `Map`이면 `<map><entry key= value=/></map>`.
- **의존성 그래프가 4단이다**: `orderService → shoppingCart → [product1, product2]`. Spring이 **역순으로**(product 먼저) 만들어 조립한다.
- ⚠️ `product2`의 `<constructor-arg>`에 `index`가 없다. 순서대로 매칭되지만, **인자가 많거나 타입이 같으면 모호해질 수 있으므로** `index`나 `name`을 명시하는 편이 안전하다. (`product1`의 setter 방식과 비교해 볼 것)

### ② student — 컬렉션 주입 + 로직

```java
public class Course {
    private String courseId, courseName;
    private List<Student> students;
    public double getAverageScore() {
        if (students == null || students.isEmpty()) return 0;    // ★ 방어
        int total = 0;
        for (Student student : students) total += student.getScore();
        return (double) total / students.size();                  // ★ 정수 나눗셈 주의
    }
}

public class GradeService {
    private Course course;
    public List<Student> getHighScoreStudents(int threshold) {
        List<Student> highScoreStudents = new ArrayList<>();
        for (Student student : course.getStudents()) {
            if (student.getScore() >= threshold) highScoreStudents.add(student);
        }
        return highScoreStudents;
    }
    public String calculateGrade(String studentId) {
        for (Student student : course.getStudents()) {
            if (student.getId().equals(studentId)) {
                int score = student.getScore();
                if (score >= 90) return "A";
                else if (score >= 80) return "B";
                ...
                else return "F";
            }
        }
        return "학생을 찾을 수 없습니다.";
    }
}
```

**해설**
- 🔗 **PART 1과 완전히 같은 코드다.** `(double) total / students.size()`는 `MemberManager.getAverageAge()`와, `getHighScoreStudents`는 `ListDemo.findOver()`와, `calculateGrade`의 순회+`equals` 검색은 `Library.findBookByTitle()`과 동일한 패턴.
- **달라진 것은 단 하나: `course`를 `new`로 만들지 않고 컨테이너가 주입한다는 것.** 비즈니스 로직 자체는 순수 자바 그대로다. **"Spring을 배운다"는 것이 곧 "자바를 새로 배운다"가 아니라는 점**을 확인할 수 있는 지점.
- ⚠️ `calculateGrade`가 못 찾았을 때 `"학생을 찾을 수 없습니다."`라는 **문자열을 반환**한다. 정상 등급("A")과 오류가 같은 타입으로 섞인다. 🔗 1-14절의 "예외 vs null" 논의 연장선 — 예외를 던지는 편이 낫다.

### ③ user — 어노테이션 방식

```java
@Component
public class UserRepository {
    @Value("MySQL")                        // ★ 프로퍼티 파일 없이 리터럴 직접 주입
    private String dbType;
    public boolean saveUser(String userId, String name) {
        System.out.println("사용자 저장: " + userId + ", " + name + " (DB: " + dbType + ")");
        return true;
    }
}

@Component
public class SecurityService {
    public boolean authenticate(String userId, String password) {
        return password != null && !password.isEmpty();
    }
    public boolean authorize(String userId, String resource) { return true; }
}

@Component
public class UserService {
    @Autowired private UserRepository userRepository;      // ★ 필드 주입 2개
    @Autowired private SecurityService securityService;

    public boolean registerUser(String userId, String name, String password) {
        if (securityService.authenticate(userId, password)) {
            return userRepository.saveUser(userId, name);
        }
        return false;
    }
}
```

**해설 — 이 3개 클래스가 웹앱 3계층의 축소판이다**
- `UserService`(비즈니스) → `UserRepository`(저장) + `SecurityService`(인증)
- `@Value("MySQL")` — `${}` 없이 **리터럴 값**을 직접 넣었다. `@Value`가 프로퍼티 전용이 아니라는 것.
- 🔗 **이 구조가 그대로 `SpringFWXml`의 `UserServiceImpl → UserDao`로 확대된다.** 여기서는 `UserRepository`가 println만 하지만, 거기서는 실제 DB에 붙는다.

### ④ notification — Java Config로 생성자 주입

```java
// 클래스에는 어노테이션이 하나도 없다 — 순수 POJO
public class EmailNotificationService implements NotificationService {
    private String smtpServer;
    private int port;
    public EmailNotificationService(String smtpServer, int port) { ... }   // 생성자만
    @Override
    public void sendNotification(String message) {
        System.out.println("이메일 알림 전송: " + message + " (서버: " + smtpServer + ":" + port + ")");
    }
}

public class NotificationManager {
    private NotificationService emailService;
    private NotificationService smsService;
    public NotificationManager(NotificationService emailService, NotificationService smsService) { ... }
    public void sendNotificationByEmail(String message) { emailService.sendNotification(message); }
    public void sendNotificationBySms(String message)   { smsService.sendNotification(message); }
}
```

**설정: `NotificationConfig.java`**

```java
@Configuration
public class NotificationConfig {
    @Bean
    public EmailNotificationService emailNotificationService() {
        return new EmailNotificationService("smtp.gmail.com", 587);
    }
    @Bean
    public SmsNotificationService smsNotificationService() {
        return new SmsNotificationService("SKT");
    }
    @Bean
    public NotificationManager notificationManager() {
        return new NotificationManager(emailNotificationService(), smsNotificationService());
    }
}
```

**해설 — 이 과제가 Java Config의 존재 이유를 보여준다**
- `EmailNotificationService`에는 **기본 생성자가 없다.** `new EmailNotificationService("smtp.gmail.com", 587)`처럼 값을 반드시 넘겨야 한다.
- 이런 클래스는 `@Component`만으로는 등록할 수 없다. **생성자 인자를 어디선가 지정해야 하기 때문.**
- `@Bean` 메서드는 **평범한 자바 코드로 `new`를 하므로** 어떤 형태의 객체든 만들 수 있다.
- ⚠️ **`NotificationManager`가 `NotificationService` 타입 2개를 받는다.** `@Autowired`였다면 "둘 중 어느 것?"으로 모호해져 `@Qualifier`가 필요했을 것이다. Java Config에서는 **내가 직접 순서대로 넘기므로 모호함이 없다.** 이것이 Java Config의 실질적 장점.
- 🔗 이 패턴이 `SpringFWConfig/DatabaseConfig`의 `DataSource` 등록과 정확히 같다.

---

## 2-4. 3계층 웹 애플리케이션 — VO · DAO · Service · Controller

### 개념 (Layered Architecture)

| 레이어 | 역할 | Spring 어노테이션 |
|---|---|---|
| **Presentation** | 사용자 요청 분석·응답 | `@Controller` / `@RestController` |
| **Service** | 비즈니스 로직 수행, **트랜잭션** | `@Service` |
| **Data Access** | 데이터 저장·조회 | `@Repository` |

각 레이어는 **독립된 역할과 책임**을 가진다.

### 코드 — `SpringFWXml/src/main/java/myspring/user/`

**① VO (Value Object) — `vo/UserVO.java`**

```java
public class UserVO {
    private Long id;
    private String userId, name, gender, city;

    public UserVO() {}                                          // ★ 프레임워크가 요구
    public UserVO(String userId, String name, String gender, String city) { ... }
    public UserVO(Long id, String userId, String name, String gender, String city) {
        this(userId, name, gender, city);                       // ★ this() 재사용
        this.id = id;
    }
    // getter / setter 전부 + toString()
}
```

**해설**
- 🔗 **PART 1의 entity 클래스와 완전히 같은 구조다.** `private 필드 + 기본 생성자 + 오버로딩 생성자 + getter/setter + toString()`. `Member`, `Product`, `Publication`과 한 글자도 다르지 않은 설계.
- ⚠️ **기본 생성자가 필수인 이유**: MyBatis가 DB 결과를 객체로 만들 때 `new UserVO()`를 호출한 뒤 setter로 값을 채우기 때문. 없으면 런타임에 실패한다.
- `this(userId, name, gender, city)` — 🔗 1-4절 생성자 재사용 패턴 그대로.
- ⚠️ **`id`(DB PK, Long)와 `userId`(로그인 ID, String)가 다르다.** 자연키 대신 대리키를 두는 흔한 설계.

**② DAO 인터페이스 — `dao/UserDao.java`**

```java
public interface UserDao {
    public void insert(UserVO user);
    public List<UserVO> readAll();
    public void update(UserVO user);
    public void delete(String id);
    public UserVO read(String id);
}
```

**해설**: **CRUD 5개.** 🔗 PART 1 `MemberManager`의 `register/findById/findByName/remove`와 같은 역할인데, **인터페이스로 분리**했다는 점이 다르다. 이유는 바로 아래.

**③ DAO 구현체가 세 벌 있다 — 이 저장소의 핵심**

| 구현체 | 기술 | 특징 |
|---|---|---|
| `UserDaoImplJDBC` | **Spring JdbcTemplate** | SQL을 자바 코드에 문자열로 |
| `UserDaoImpl` | **MyBatis SqlSession** | SQL은 XML에, 자바는 `"userNS.selectUserById"` 문자열로 호출 |
| `UserDaoImplMapper` | **MyBatis Mapper 인터페이스** | 문자열이 사라지고 **메서드 호출**로 |

**같은 인터페이스를 구현하므로 `UserServiceImpl`은 어느 것을 쓰든 코드를 안 고쳐도 된다.** 이것이 인터페이스를 둔 이유이자 DI의 진짜 효용이다.

**③-1. `UserDaoImplJDBC` — 자료 12장 JDBC의 Spring 버전**

```java
public class UserDaoImplJDBC implements UserDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // ★ ResultSet → 객체 변환 규칙을 클래스로 분리
    class UserMapper implements RowMapper<UserVO> {
        public UserVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserVO user = new UserVO();
            user.setUserId(rs.getString("userid"));
            user.setName(rs.getString("name"));
            user.setGender(rs.getString("gender"));
            user.setCity(rs.getString("city"));
            return user;
        }
    }

    @Override
    public UserVO read(String id) {
        String SQL = "select * from users where userid = ?";
        try {
            return jdbcTemplate.queryForObject(SQL, new Object[]{ id }, new UserMapper());
        } catch(EmptyResultDataAccessException e){
            return null;                    // ★ 없으면 null
        }
    }
    public void insert(UserVO user) {
        String SQL = "insert into users (userid, name, gender,city) values (?, ?, ?, ?)";
        jdbcTemplate.update(SQL, user.getUserId(), user.getName(), user.getGender(), user.getCity());
    }
    public List<UserVO> readAll() {
        return jdbcTemplate.query("select * from users", new UserMapper());
    }
    public void delete(String id) {
        jdbcTemplate.update("delete from users where userid = ?", id);
    }
    public void update(UserVO user) {
        String SQL = "update users set name = ?, gender = ?, city = ? where userid = ?";
        jdbcTemplate.update(SQL, user.getName(), user.getGender(), user.getCity(), user.getUserId());
    }
}
```

**해설 — ⚠️ 자료 12장 JDBC 5단계와 비교하면 무엇이 사라졌는지 명확하다**

| 순수 JDBC (자료 12.3) | JdbcTemplate |
|---|---|
| `Class.forName("...OracleDriver")` | **사라짐** (DataSource가 처리) |
| `DriverManager.getConnection(...)` | **사라짐** |
| `conn.createStatement()` | **사라짐** |
| `stmt.executeQuery()` / `executeUpdate()` | `jdbcTemplate.query()` / `update()` |
| `while(rset.next()) { rset.getString(...) }` | `RowMapper.mapRow()` **한 번만 작성** |
| `rset.close(); stmt.close(); conn.close();` | **사라짐** |
| `try-catch-finally` 지옥 | `SQLException`이 **런타임 예외로 변환**되어 강제 처리 불필요 |

**남은 것은 SQL 문자열과 결과 매핑 규칙뿐이다.** 이것이 자료 1.4절의 "DAO: JDBC에 대한 추상화 계층"과 삼각형 전략의 **PSA(서비스 추상화)**가 뜻하는 바다.

⚠️ **`?` 자리표시자를 쓰고 있다.** `jdbcTemplate.update(SQL, 값1, 값2, ...)`는 내부적으로 `PreparedStatement`를 만들어 바인딩한다. 자료 12.6절 그대로이자 **SQL Injection 방어**이기도 하다.

⚠️ **`EmptyResultDataAccessException`을 잡아 `null`로 바꾼 부분**: `queryForObject`는 결과가 0건이면 예외를 던진다. 🔗 1-14절의 "예외 vs null" 선택이 여기서도 반복된다.

**③-2. `UserDaoImpl` — MyBatis SqlSession 방식**

```java
@Repository("userDao")                                     // ★ Data Access Layer
public class UserDaoImpl implements UserDao {

    @Autowired
    private SqlSession session;

    @Override
    public UserVO read(String id) {
        return session.selectOne("userNS.selectUserById", id);      // 네임스페이스.SQL ID
    }
    public List<UserVO> readAll() {
        return session.selectList("userNS.selectUserList");
    }
    public void insert(UserVO user) {
        session.update("userNS.insertUser", user);
    }
    @Override
    public void update(UserVO user) {
        session.update("userNS.updateUser", user);
    }
    @Override
    public void delete(String id) {
        session.delete("userNS.deleteUser", id);
    }
}
```

**대응 SQL — `resources/UserMapper.xml`**

```xml
<mapper namespace="userNS">
    <select id="selectUserById" parameterType="string" resultType="User">
        select * from users where userid=#{value}
    </select>
    <select id="selectUserList" resultType="User">
        select * from users order by id
    </select>
    <insert id="insertUser" parameterType="User">
        insert into users (userid, name, gender, city)
        values(#{userId},#{name},#{gender},#{city} )
    </insert>
    <update id="updateUser" parameterType="User">
        update users set name = #{name}, gender = #{gender}, city = #{city}
        where userid = #{userId}
    </update>
    <delete id="deleteUser" parameterType="string">
        delete from users where userid = #{value}
    </delete>
</mapper>
```

**해설 — 자료 3장 MyBatis의 핵심이 여기 다 있다**
- **SQL이 자바 코드에서 완전히 사라졌다.** 자료가 말한 "SQL에 변경이 있을 때마다 자바 코드를 수정하거나 컴파일하지 않아도 된다"의 실물.
- `session.selectOne("userNS.selectUserById", id)` — **`네임스페이스 + "." + SQL ID`** 형식. 자료 3.4절 그대로.
- **`#{}` 자리표시자**
  - `#{value}` — 파라미터가 단일 값일 때의 기본 이름
  - `#{userId}` — 파라미터가 객체일 때 **getter를 호출**(`user.getUserId()`)
  - ⚠️ `#{}`는 `PreparedStatement`의 `?`로 변환된다(안전). `${}`는 문자열 그대로 치환된다(**SQL Injection 위험**). 반드시 구분할 것.
- **`resultType="User"`의 `User`는 어디서 왔나** → `SqlMapConfig.xml`의 typeAlias:
  ```xml
  <typeAliases>
      <typeAlias alias="User" type="myspring.user.vo.UserVO" />
      <typeAlias alias="Student" type="myspring.student.vo.StudentVO" />
      ...
  </typeAliases>
  ```
  전체 패키지명을 매번 쓰지 않기 위한 별칭. 자료 3.2절의 "MyBatis 설정파일: VO 객체의 정보를 설정한다"가 이것.
- ⚠️ **DB 컬럼 `userid`가 자바 필드 `userId`로 자동 매핑된다.** 대소문자 무시 매핑 덕분. 매핑이 안 되면 `resultMap`을 따로 정의해야 한다.

**⚠️ 이 방식의 결정적 약점 (자료 3.4절)**: `"userNS.selectUserById"`는 **문자열**이다. 오타가 나도 컴파일러가 못 잡고, IDE 자동완성도 안 된다. 그래서 다음 방식이 나왔다.

**③-3. `UserDaoImplMapper` — Mapper 인터페이스 방식**

```java
// dao/mapper/UserMapper.java — SQL 을 호출하는 자바 인터페이스
@Mapper                                    // SpringFWConfig 버전 (MyBatis 제공)
public interface UserMapper {
    UserVO selectUserById(String id);
    List<UserVO> selectUserList();
    void insertUser(UserVO userVO);
    void updateUser(UserVO userVO);
    void deleteUser(String id);
}

// dao/UserDaoImplMapper.java
public class UserDaoImplMapper implements UserDao {
    private UserMapper userMapper;

    @Override
    public UserVO read(String id) {
        return userMapper.selectUserById(id);        // ★ 문자열이 사라졌다
    }
    public List<UserVO> readAll() {
        return userMapper.selectUserList();
    }
    public void insert(UserVO user) { userMapper.insertUser(user); }
    ...
}
```

**해설 — 자료 3.4절 "Mapper 인터페이스"의 실물**
- `"userNS.selectUserById"` → `userMapper.selectUserById(id)`. **Type Safe**해졌다.
- ⚠️ **연결 규칙**: `패키지 + "." + 인터페이스명 + "." + 메서드명` = `네임스페이스 + "." + SQL ID`
  → 그래서 XML의 namespace를 `userNS`가 아니라 **`myspring.user.dao.mapper.UserMapper`**로 바꿔야 한다. 실제로 `UserMapper.xml`에 그 줄이 주석으로 남아 있다.
  ```xml
  <!-- <mapper namespace="myspring.user.dao.mapper.UserMapper"> -->
  <mapper namespace="userNS">
  ```
  **두 방식을 번갈아 실험한 흔적이다.**
- **`UserMapper` 인터페이스에는 구현체가 없다.** MyBatis가 런타임에 **동적 프록시**로 구현체를 만들어준다. 🔗 2-5절 AOP 프록시와 같은 원리.
- 인터페이스를 Bean으로 등록하는 설정:
  ```xml
  <!-- beans.xml -->
  <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
      <property name="basePackage" value="myspring.**.dao.mapper"/>
      <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
  </bean>
  ```
  Java Config 버전:
  ```java
  @MapperScan(basePackages = {"myspring.user.dao.mapper","myspring.student.dao.mapper"},
              sqlSessionFactoryRef="sqlSessionFactoryBean")
  ```

**⚠️ `MyMapper.java`가 재미있는 흔적이다** (SpringFWXml에만 존재)
```java
package myspring.user.dao.mapper;
public @interface MyMapper {          // 내용이 텅 빈 커스텀 어노테이션
}
```
`@Retention`도 `@Target`도 없는 **빈 어노테이션**이다. `SpringFWXml`의 `UserMapper`는 `@MyMapper`를, `SpringFWConfig`의 `UserMapper`는 MyBatis 제공 `@Mapper`를 쓴다.
→ **`MapperScannerConfigurer`가 `basePackage`로 스캔하므로 `SpringFWXml`에서는 어노테이션이 실제로 필요 없다.** 어노테이션을 직접 만들어보는 실험용 코드로 보인다.

**④ MyBatis-Spring 연결 설정 (자료 3.2절)**

```xml
<!-- SqlSessionFactoryBean : MyBatis 설정 + DataSource + Mapper XML 위치 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="configLocation" value="classpath:SqlMapConfig.xml" />
    <property name="mapperLocations">
        <list>
            <value>classpath:UserMapper.xml</value>
            <value>classpath:StudentMapper.xml</value>
        </list>
    </property>
</bean>

<!-- SqlSessionTemplate : Thread-safe 한 SqlSession 구현체 -->
<bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
    <constructor-arg ref="sqlSessionFactory" />
</bean>
```

**해설 — 자료 3.2절 표와 1:1 대응**

| 자료의 컴포넌트 | 이 코드 |
|---|---|
| MyBatis 설정파일 (SqlMapConfig.xml) | `configLocation` |
| SqlSessionFactoryBean | `sqlSessionFactory` Bean |
| **SqlSessionTemplate** | `sqlSession` Bean |
| Mapping 파일 (UserMapper.xml) | `mapperLocations` |
| Spring Bean 설정파일 (beans.xml) | 이 파일 자체 |

⚠️ **왜 `SqlSession`이 아니라 `SqlSessionTemplate`인가**: 자료 3.2절이 말한 대로 **`SqlSession`은 Thread-Safe하지 않다.** 웹 애플리케이션은 요청마다 스레드가 다르므로 싱글톤 Bean으로 두면 위험하다. `SqlSessionTemplate`은 Thread-safe하게 감싼 구현체라 **싱글톤으로 주입해도 안전하다.** `UserDaoImpl`이 `@Autowired SqlSession session`으로 받는 것이 실은 이 `SqlSessionTemplate`이다.

**⑤ Service — `service/UserServiceImpl.java`**

```java
@Service("userService")                    // ★ Service Layer
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userdao;                       // ★ 인터페이스에 의존 (구현체가 아니라)

    @Override public void insertUser(UserVO user)   { userdao.insert(user); }
    public List<UserVO> getUserList()               { return userdao.readAll(); }
    @Override public void deleteUser(String id)     { userdao.delete(id); }
    @Override public UserVO getUser(String id)      { return userdao.read(id); }
    @Override public void updateUser(UserVO user)   { userdao.update(user); }
}
```

**해설**
- **하는 일이 거의 없다.** DAO에 그대로 위임할 뿐. **그래서 왜 존재하는가?**
  1. **트랜잭션 경계**가 여기에 걸린다 (2-6절). 자료 5.1절: "트랜잭션은 주로 서비스의 메서드를 대상으로 한다."
  2. **여러 DAO를 조합**하는 로직이 생길 자리다. (예: 주문 시 재고 차감 + 결제 기록)
  3. **AOP 포인트컷의 대상**이다 (2-5절).
- `@Autowired UserDao userdao` — **구현체가 아니라 인터페이스에 의존**한다. JDBC/SqlSession/Mapper 어느 구현체를 붙여도 이 클래스는 안 바뀐다.
- ⚠️ **`@Autowired`가 타입으로 찾는데 `UserDao` 구현체가 3개다.** 그런데 `@Repository`가 붙은 것은 `UserDaoImpl` 하나뿐이라(`UserDaoImplJDBC`, `UserDaoImplMapper`는 Bean이 아님) 모호함이 없다. **`@Repository`를 붙였다 뗐다 하며 구현체를 갈아끼우는 구조.** 이것이 DI가 주는 유연함의 실제 모습이다.

**⑥ MyBatis 동적 SQL — `StudentMapper.xml`**

지금까지 본 `UserMapper.xml`은 SQL이 고정된 **정적 SQL**이었다. `StudentMapper.xml`은 한 단계 더 나아가, **조건에 따라 SQL 조각이 붙거나 빠지는 동적 SQL**을 쓴다. 이 개념은 리포트에는 없지만 실제 코드에 쓰이고 있다.

```xml
<sql id="selectStudent">
    select * from student
</sql>

<select id="selectStudentByName" parameterType="String" resultMap="studentResultMap">
    <include refid="selectStudent" />
    where stu_name like CONCAT('%',#{keyword},'%')
</select>

<select id="selectStudentByGradeOrDay" parameterType="Student" resultMap="studentResultMap">
    <include refid="selectStudent" />
    <where>
        <if test="grade != null">
            stu_grade = #{grade}
        </if>
        <if test="daynight != null">
            and stu_daynight = #{daynight}
        </if>
    </where>
</select>
```

**해설 — 세 가지 태그가 하는 일**

1. **`<sql id="...">` + `<include refid="...">`** — 공통 SELECT 문을 조각으로 빼서 여러 쿼리에서 재사용한다. 🔗 자바의 메서드 추출과 같은 발상을 XML에서 한 것.
2. **`<if test="조건">`** — 자바 조건문과 똑같다. `test` 안의 `grade`, `daynight`는 파라미터 객체(`Student`)의 **필드 이름**이다. 그 필드가 `null`이 아니면 안의 SQL 조각이 통째로 붙는다.
3. **`<where>`** — 두 가지 일을 자동으로 해준다.
   - 안의 `<if>`가 **전부 빠지면** `WHERE` 절 자체를 생략한다
   - 첫 번째로 살아남은 조건 앞에 붙은 불필요한 `and`/`or`를 **자동으로 제거**한다
   - 이게 없으면 조건이 0개일 때 `WHERE` 뒤에 아무것도 안 남거나, 조건이 1개만 살아남았을 때 `WHERE and stu_daynight = ?`처럼 문법 오류가 나는 SQL이 만들어진다. 손으로 `WHERE 1=1 AND ...` 트릭을 쓰던 것을 `<where>` 태그가 대신해준다.

**호출하는 쪽 — `StudentClient.java`의 `dynamic()` 테스트**
```java
@Test
public void dynamic() {
    List<StudentVO> stu = studentDao.selectStudentByName("홍");     // 이름 검색

    StudentVO student = new StudentVO();
    student.setGrade("2학년");                                      // grade 만 채움, daynight 는 null
    List<StudentVO> stuList = studentDao.selectStudentByGradeOrDay(student);
    for (StudentVO studentVO : stuList) {
        System.out.println(studentVO);
    }
}
```
`student.setGrade("2학년")`만 하고 `daynight`는 비워뒀으므로, 실제로 나가는 SQL은 `<if test="daynight != null">` 블록이 통째로 빠진 `WHERE stu_grade = ?`가 된다. **"학년만 검색" / "학년+주야간 둘 다 검색"이라는 서로 다른 요구사항을, SQL을 두 벌 만들지 않고 하나의 동적 쿼리로 처리한 것**이다.

⚠️ **정적 SQL(`UserMapper.xml`)과 동적 SQL(`StudentMapper.xml`)을 같은 저장소에서 비교해 보는 것이 핵심이다.** 조회 조건이 고정이면 정적 SQL로 충분하고, 검색 화면처럼 **조건이 사용자마다 달라지면** 동적 SQL이 필요해진다.

---

## 2-5. AOP — 관점 지향 프로그래밍

### 개념

**핵심 기능(Core Concerns)** vs **부가 기능(Cross-cutting Concerns)**
- 핵심 = 업무(Biz) 로직 / 부가 = 로깅, 보안, 트랜잭션 등
- 객체지향 원칙만으로는 부가 기능을 핵심에서 분리하기 어렵다 → **AOP**

**AOP 용어 (반드시 외울 것)**

| 용어 | 뜻 |
|---|---|
| **Target** | 핵심 기능을 담은 모듈. 부가 기능을 부여할 **대상** |
| **Advice** | 타겟에 제공할 **부가 기능 코드** |
| **Pointcut** | Advice를 적용할 **타겟 메서드를 선별하는 표현식** |
| **JoinPoint** | Advice가 적용될 수 있는 **위치**. Pointcut에 의해 선택된 메서드 |
| **Aspect** | **Advice + Pointcut**. AOP의 기본 모듈. 싱글톤 |
| **Advisor** | Advice + Pointcut. **Spring AOP에서만 쓰는 용어** |
| **Weaving** | Pointcut이 결정한 JoinPoint에 Advice를 **삽입하는 과정** |

**Spring AOP의 3가지 특징**
1. **프록시(Proxy) 기반** — 타겟을 감싸는 프록시를 **런타임에** 생성
2. **프록시가 호출을 가로챈다(Intercept)** — 부가 기능 실행 → 타겟 호출 (또는 그 반대)
3. ⚠️ **메서드 조인 포인트만 지원** — 동적 프록시 기반이라 메서드 호출 시점에만 적용 가능. (AspectJ는 객체 생성·필드 조작·static 호출까지 가능)

**Advice 5종**

| 어노테이션 | XML | 시점 |
|---|---|---|
| `@Before` | `<aop:before>` | 메서드 실행 **전** |
| `@AfterReturning` | `<aop:after-returning>` | **정상 종료 후**. `returning`으로 리턴값 참조 |
| `@AfterThrowing` | `<aop:after-throwing>` | **예외 발생 시**. `throwing`으로 예외 참조. try-catch의 catch와 유사 |
| `@After` | `<aop:after>` | 정상/예외 **무관하게** 실행. **finally와 유사** |
| `@Around` | `<aop:around>` | **전 과정**을 감쌈. 가장 강력 |

⚠️ **`@Around`는 `ProceedingJoinPoint`를 필수로 받아야 한다.** 다른 Advice는 `JoinPoint`(선택).

**Pointcut 표현식 (execution)**
```
execution( [접근제한자] 리턴타입 [패키지.클래스.]메서드명(파라미터) [throws 예외] )
```

| 표현식 | 의미 |
|---|---|
| `execution(* hello(..))` | 이름이 hello인 메서드, 파라미터 무관 |
| `execution(* hello())` | hello 중 **파라미터 없는** 것만 |
| `execution(* myspring.user.service.UserServiceImpl.*(..))` | 그 클래스의 모든 메서드 |
| `execution(* myspring.user.service.*.*(..))` | 그 패키지의 모든 클래스. **서브 패키지 제외** |
| `execution(* myspring.user.service..*.*(..))` | `..` → **서브 패키지 포함** |
| `execution(* *..Target.*(..))` | 패키지 무관, Target이라는 이름의 모든 클래스 |

⚠️ **점 하나(`.`)와 점 둘(`..`)의 차이가 시험 포인트다.**

### 코드 — 방식 ①: XML + POJO Advice

**Advice 클래스: `aop/xml/PerformanceTraceAdvice.java`**

```java
public class PerformanceTraceAdvice {                 // ★ 어떤 어노테이션도 없는 POJO

    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String signatureString = joinPoint.getSignature().toShortString();
        System.out.println(signatureString + " 시작");
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();      // ★★ 타겟 메서드 실행
            return result;
        } finally {
            long finish = System.currentTimeMillis();
            System.out.println(signatureString + " 종료");
            System.out.println(signatureString + " 실행 시간 : " + (finish - start) + " ms");
        }
    }
}
```

**설정: `beans.xml`**

```xml
<aop:config>
    <aop:aspect id="traceAspect" ref="performanceTraceAdvice">
        <aop:around pointcut="execution(public * myspring.user..*(..))"
                    method="trace" />
    </aop:aspect>
</aop:config>

<bean id="performanceTraceAdvice" class="myspring.aop.xml.PerformanceTraceAdvice" />
```

**해설 — 이 20줄이 AOP의 정수다**

1. **`joinPoint.proceed()`가 전부다.** 이 한 줄이 **원래 실행되었어야 할 타겟 메서드**를 호출한다.
   - 그 **앞** 코드 = 전처리(Before)
   - 그 **뒤** 코드 = 후처리(After)
   - `try-finally`로 감쌌으므로 **예외가 나도 시간이 측정된다**
   - 이것이 `@Around` 하나로 5종 Advice를 다 흉내 낼 수 있는 이유.
2. **`UserServiceImpl`에는 시간 측정 코드가 한 줄도 없다.** 그런데 모든 메서드의 실행 시간이 찍힌다. 이것이 **관심사의 분리**.
   - AOP 없이 하려면 모든 메서드마다 `long start = ...` / `long finish = ...`를 복붙해야 한다.
3. **Pointcut `execution(public * myspring.user..*(..))` 해석**
   - `public` — public 메서드만
   - `*` — 리턴 타입 무관
   - `myspring.user..*` — `myspring.user` 패키지와 **모든 서브 패키지**의 모든 클래스
   - `(..)` — 파라미터 무관
   - → `myspring.user.vo/dao/service/controller` 전부가 대상
4. **`ref="performanceTraceAdvice"`가 가리키는 Bean이 아래 `<bean>`으로 등록되어 있다.** Advice 클래스도 **Bean이어야 한다.**
5. 🔗 **프록시의 감싸기 구조**는 PART 1의 `new BufferedReader(new FileReader(f))`와 같은 아이디어다. 대상을 감싸서 기능을 추가한다.

### 코드 — 방식 ②: @Aspect 어노테이션

**`aop/annot/LoggingAspect.java`** — 4종 Advice를 한 클래스에

```java
@Component                                       // ★ Bean 등록
@Aspect                                          // ★ Aspect 선언
public class LoggingAspect {

    protected static final Logger logger = LogManager.getLogger();

    @Before("execution(public * myspring..*(..))")
    public void before(JoinPoint joinPoint) {
        String signatureString = joinPoint.getSignature().getName();
        if( logger.isDebugEnabled() ) {
            logger.debug(">>>> @Before [ " + signatureString + " ] 메서드 실행 전처리 수행");
        }
        for (Object arg : joinPoint.getArgs()) {          // ★ 파라미터 참조
            logger.debug("@Before [ " + signatureString + " ] 아규먼트 " + arg);
        }
    }

    @AfterReturning(pointcut="execution(public * myspring.user.service.*.*(..))",
                    returning="ret")                       // ★ 리턴값을 ret 에 담는다
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        logger.debug("@AfterReturing [ ... ] 리턴값=" + ret);
    }

    @AfterThrowing(pointcut="execution(* *..UserService*.*(..))",
                   throwing="ex")                          // ★ 예외를 ex 에 담는다
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.debug("@AfterThrowing [ ... ] 예외=" + ex.getMessage());
    }

    @After("execution(* *..*.*User(..))")                   // ★ 이름이 ~User 로 끝나는 메서드
    public void afterFinally(JoinPoint joinPoint) {
        logger.debug("@After [ ... ] 메서드 실행 완료");
    }
}
```

**설정**
```xml
<!-- XML : @Aspect가 선언된 모든 Bean을 Aspect로 자동 등록해줌 -->
<aop:aspectj-autoproxy />
```
```java
// Java Config
@EnableAspectJAutoProxy  //<aop:aspectj-autoproxy />
```

**해설 — 자료 4.5절의 4종 Advice가 전부 구현되어 있다**

1. **`joinPoint.getArgs()`** — 자료 4.3절이 말한 JoinPoint의 유용한 메서드. 호출 시 넘어온 파라미터를 전부 꺼낸다. `getSignature()`, `getTarget()`, `getThis()`도 같은 계열.
2. **`returning="ret"`와 파라미터 `Object ret`의 이름이 일치해야 한다.** 다르면 동작하지 않는다. `throwing="ex"` ↔ `Throwable ex`도 마찬가지. **시험/실습에서 자주 틀리는 지점.**
3. **Pointcut 4개가 전부 범위가 다르다** — 의도적인 실습 구성:
   - `execution(public * myspring..*(..))` — **myspring 전체**. 가장 넓다
   - `execution(public * myspring.user.service.*.*(..))` — service 패키지만
   - `execution(* *..UserService*.*(..))` — **클래스 이름이 `UserService`로 시작**하는 것 전부(패키지 무관)
   - `execution(* *..*.*User(..))` — **메서드 이름이 `User`로 끝나는 것**(insertUser, deleteUser, updateUser, getUser)
   - ⚠️ 마지막 두 개가 자료 4.4절의 `execution(* *..Target.*(..))` 유형 — **패키지 무관 이름 매칭**. 의도치 않은 클래스까지 걸릴 수 있으니 주의하라는 자료의 경고가 여기 해당한다.
4. **`logger.isDebugEnabled()` 체크**를 먼저 한다. 문자열 결합은 비용이 있으므로, 어차피 출력 안 될 로그면 결합조차 하지 않겠다는 최적화. **실무 습관.**
5. ⚠️ **XML 방식과 어노테이션 방식이 동시에 켜져 있다.** `beans.xml`에 `<aop:aspectj-autoproxy/>`(어노테이션용)와 `<aop:config>`(XML용)가 **둘 다** 있다. 즉 `UserServiceImpl`의 메서드 하나를 호출하면 `PerformanceTraceAdvice`와 `LoggingAspect`가 **모두** 적용된다. 두 방식을 비교 실습하기 위한 구성.

### XML ↔ 어노테이션 AOP 대응표

| | XML 방식 | 어노테이션 방식 |
|---|---|---|
| Aspect 선언 | `<aop:aspect ref="bean">` | `@Aspect` + `@Component` |
| 활성화 | `<aop:config>` | `<aop:aspectj-autoproxy/>` 또는 `@EnableAspectJAutoProxy` |
| Advice | `<aop:around method="trace">` | `@Around("포인트컷")` |
| Pointcut | `pointcut="execution(...)"` 속성 | 어노테이션 값 |
| Advice 클래스 | **순수 POJO** (Spring 의존 0) | Spring/AspectJ 어노테이션 필요 |
| 설정 위치 | XML 한 곳에 모임 | 각 Aspect 클래스에 분산 |

---

## 2-6. Spring Transaction

### 개념

**선언적 트랜잭션**: 코드에 영향을 주지 않으면서 메서드 실행 전후에 트랜잭션을 시작·종료한다. **프록시 AOP를 활용**한다. ⚠️ 프로그램에 의한 트랜잭션 처리는 **권장하지 않는다.**

**2가지 방식**: `<tx:advice>` 태그 / `@Transactional` 어노테이션

**트랜잭션 매니저**: `DataSourceTransactionManager` — Connection의 트랜잭션 API를 이용. **DataSource가 Bean으로 등록되어 있어야 한다.**

**전파(Propagation) 속성**

| 값 | 동작 |
|---|---|
| **REQUIRED** (기본값) | 기존 트랜잭션 있으면 참여, 없으면 새로 시작. **대부분 이걸로 충분** |
| **REQUIRES_NEW** | 항상 새로 시작. 진행 중인 것은 **보류** |
| **SUPPORTS** | 있으면 참여, 없으면 트랜잭션 없이 진행 |
| **MANDATORY** | 있으면 참여, **없으면 예외** |
| **NOT_SUPPORTED** | 트랜잭션 사용 안 함. 진행 중이면 보류 |
| **NEVER** | 트랜잭션 금지. 진행 중이면 **예외** |

**격리수준(Isolation) 속성**

| 값 | 특징 |
|---|---|
| **DEFAULT** | DB 드라이버 기본값. 대부분 READ_COMMITTED |
| **READ_UNCOMMITED** | 가장 낮음. 커밋 전 변화도 노출(Dirty Read). 가장 빠름 |
| **READ_COMMITED** | **가장 많이 사용.** 커밋 안 된 것은 못 읽음. 단, 읽은 로우를 남이 수정 가능(Non-repeatable Read) |
| **REPEATABLE_READ** | 읽은 로우의 수정을 막음. 단, **새 로우 추가는 못 막음**(Phantom Read) |
| **SERIALIZABLE** | 가장 강력·가장 느림. 순차 진행 |

**기타 속성**
- `timeout` — 초 단위 제한 시간
- `readOnly` — 읽기 전용. 성능 최적화 + 쓰기 방지. **읽기 전용에서 INSERT/UPDATE/DELETE 시 예외**
- ⚠️ **`rollbackFor`** — 선언적 트랜잭션은 **런타임 예외에만 롤백**한다. **체크 예외도 롤백하려면 `rollbackFor`로 명시**해야 한다. 시험 단골.

### 코드 — 방식 ①: `<tx:advice>` (SpringFWXml)

```xml
<!-- 1. Transaction Manager -->
<bean id="transactionManager"
      class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource" />
</bean>

<!-- 2. Transaction Advice : 메서드 이름별 속성 -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <tx:attributes>
        <tx:method name="get*" read-only="true"/>
        <tx:method name="updateUser" propagation="NOT_SUPPORTED"/>
        <tx:method name="update" propagation="MANDATORY" />
    </tx:attributes>
</tx:advice>

<!-- 3. Advisor = Advice + Pointcut -->
<aop:config>
    <aop:pointcut expression="execution(public * myspring.user..*(..))" id="txPointCut"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>
</aop:config>
```

**해설 — 이 설정이 자료 5.4절 그대로다**
1. **`<aop:advisor>`가 자료 4.1절의 "Advisor = Advice + Pointcut, Spring AOP에서만 쓰는 용어"의 실물이다.** `<aop:aspect>`가 아니라 `<aop:advisor>`를 쓴다는 점에 주목. 트랜잭션은 Spring이 제공하는 Advice를 쓰므로 Aspect가 아니라 Advisor다.
2. **`name="get*"`** — `getUser`, `getUserList` 등 get으로 시작하는 메서드는 **읽기 전용**. 조회에는 쓰기 잠금이 필요 없으니 성능이 좋아진다. 자료 표의 `name` 속성 설명 그대로.
3. **⚠️ 주석에 실험의 흔적과 오류 메시지가 그대로 남아 있다.** 이게 오히려 학습 자료로 훌륭하다.
   ```xml
   <!--No existing transaction found for transaction marked with propagation 'mandatory'-->
   <!-- <tx:method name="delete" propagation="MANDATORY"/> -->

   <!-- 예외=Existing transaction found for transaction marked with propagation 'never' -->
   ```
   - `MANDATORY`는 **기존 트랜잭션이 없으면 예외**를 던진다 → `delete`를 단독 호출하니 터졌다
   - `NEVER`는 **트랜잭션이 있으면 예외**를 던진다 → 이미 트랜잭션 안이라 터졌다
   - **자료 표의 설명이 실제 에러 메시지로 확인된 기록이다.** 전파 속성을 이해하는 데 이보다 좋은 자료가 없다.
4. **`updateUser`는 `NOT_SUPPORTED`, `update`는 `MANDATORY`** 조합이 흥미롭다.
   - `UserServiceImpl.updateUser()` → 트랜잭션 없이 실행
   - 그 안에서 `userdao.update()` 호출 → `MANDATORY`인데 트랜잭션이 없으니 **예외**
   - **의도적으로 실패를 만들어 전파 속성을 체감하게 하는 구성**이다.
5. ⚠️ **Pointcut이 `myspring.user..*`로 service뿐 아니라 dao·controller까지 포함**한다. 실무에서는 보통 `service` 계층에만 건다. 자료 5.1절 "트랜잭션은 주로 서비스의 메서드를 대상으로 한다"에 비추면 넓게 잡힌 편.

### 코드 — 방식 ②: `@Transactional` (SpringFWConfig)

**`SpringFWConfig`의 `UserServiceImpl`** — XML 버전과 **딱 이 부분만 다르다**

```java
@Service("userService")
@Transactional(readOnly = true)                          // ★ 클래스 레벨 기본값
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userdao;

    @Transactional(propagation = Propagation.REQUIRED)   // ★ 메서드에서 재정의
    @Override
    public void insertUser(UserVO user) { userdao.insert(user); }

    public List<UserVO> getUserList() { return userdao.readAll(); }   // 클래스 기본값(readOnly) 적용

    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED) // ★ 격리수준까지 지정
    @Override
    public void deleteUser(String id) { userdao.delete(id); }

    @Override
    public UserVO getUser(String id) { return userdao.read(id); }     // 클래스 기본값

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUser(UserVO user) { userdao.update(user); }
}
```

**활성화 설정**
```java
@Configuration
@PropertySource("classpath:value.properties")
@EnableTransactionManagement                    // ★ <tx:annotation-driven> 과 동일
public class DatabaseConfig {
    @Autowired private Environment env;

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driverClass"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
```

**해설 — 두 방식의 결정적 차이**

| | `<tx:advice>` (XML) | `@Transactional` |
|---|---|---|
| 대상 지정 | **메서드 이름 패턴** (`get*`) | 메서드에 **직접** 부착 |
| 코드 침투 | **없음** (POJO 유지) | 있음 (Spring 어노테이션 import) |
| 한눈에 보기 | ⭕ XML 한 곳에 다 모임 | ❌ 클래스마다 흩어짐 |
| 정밀도 | ❌ 이름이 같으면 다 걸림 | ⭕ 메서드 단위 정확 |
| 활성화 | `<aop:advisor>` | `<tx:annotation-driven>` / `@EnableTransactionManagement` |

**⚠️ `@Transactional`의 3대 함정 (실무에서 반드시 물어본다)**

1. **클래스 레벨 + 메서드 레벨이 겹치면 메서드가 이긴다.** 위 코드에서 클래스에 `readOnly=true`가 있지만 `insertUser`는 자기 `@Transactional`이 있으므로 `readOnly=false`(기본)로 동작한다. `getUserList`, `getUser`는 자기 것이 없으니 클래스의 `readOnly=true`가 적용된다. **XML의 `get*` 규칙과 정확히 같은 효과를 다른 방법으로 낸 것.**

2. **자기 호출(self-invocation)은 트랜잭션이 안 걸린다.**
   ```java
   public void a() { this.b(); }              // b() 의 @Transactional 이 무시된다
   @Transactional public void b() { ... }
   ```
   AOP는 **프록시**로 동작하는데, `this.b()`는 프록시를 거치지 않고 내부에서 직접 호출되기 때문이다. 🔗 2-5절 "프록시가 호출을 가로챈다"의 이면.

3. **`private` 메서드에는 안 걸린다.** 프록시가 오버라이딩할 수 없으므로.

**⚠️ DataSource 구현체가 두 프로젝트에서 다르다.**
- `SpringFWXml`: `SimpleDriverDataSource` — **커넥션 풀 없음.** 요청마다 커넥션을 새로 만든다. 학습용
- `SpringFWConfig`: `BasicDataSource`(Apache DBCP2) — **커넥션 풀 사용.** 실무용

🔗 PART 3 Spring Boot에서는 **HikariCP**가 기본으로 붙는다. 풀링의 필요성을 이 차이에서 짚어둘 것.

**⚠️ `value.properties`로 DB 정보를 분리한 것**은 자료 2.13절 그대로다. "환경(개발/테스트/운영)에 따라 자주 바뀌는 정보는 properties로 분리한다."
```xml
<context:property-placeholder location="classpath:value.properties" />
<bean id="dataSource" class="...SimpleDriverDataSource">
    <property name="driverClass" value="${db.driverClass}" />
    <property name="url" value="${db.url}" />
    ...
</bean>
```
🔗 PART 3의 `application.properties`가 이것의 발전형이다.


---

## 2-7. Spring MVC

### 개념

**MVC 패턴**: Business logic과 Presentation logic을 분리
- **Model** — 정보(데이터 + 비즈니스 로직). **DAO·Service 클래스에 해당**
- **View** — 화면. HTML/JSP
- **Controller** — Model과 View 사이 상호작용 관리. **Servlet**

**모델1 vs 모델2**
- 모델1 — Controller 역할을 **JSP**가 담당
- 모델2 — Controller 역할을 **Servlet**이 담당 ← Spring MVC

**Front Controller 패턴**: 모든 요청을 한 곳에서 받아 공통 작업(인증·권한 등)을 먼저 수행하고, 적절한 세부 Controller에게 위임. Spring MVC에서는 **`DispatcherServlet`**이 이 역할.

**Spring MVC 6대 구성요소**

| 구성요소 | 역할 |
|---|---|
| **DispatcherServlet** | 모든 요청 수신 → Controller에 전달 → 결과를 View에 전달 |
| **HandlerMapping** | URL로 어떤 핸들러를 쓸지 결정 |
| **Controller** | 요청 처리, Model 호출, 결과 반환 |
| **ModelAndView** | 처리한 데이터 + 화면 정보 보유 |
| **View** | 처리 결과 화면 정보 |
| **ViewResolver** | Controller가 리턴한 **뷰 이름**으로 실제 View 결정 |

**처리 흐름**
```
요청 → DispatcherServlet → HandlerMapping (Controller 찾기)
     → Controller 실행 → ModelAndView 반환
     → ViewResolver (뷰 이름 → 실제 View)
     → View 렌더링 → 응답
```

**핵심 어노테이션**

| | 역할 |
|---|---|
| `@Controller` | Controller 클래스 정의 |
| `@RequestMapping` | 요청 URL과 메서드 연결 |
| `@RequestParam` | 요청 파라미터 참조 (`?id=test`) |
| `@ModelAttribute` | 요청 파라미터를 **모델 객체로 바인딩** |
| `@PathVariable` | 파라미터를 **URL 경로**로 받음 (`/users/{id}`) |
| `@ExceptionHandler` | 컨트롤러 메서드의 예외 처리 |

**설정 태그**
- `<mvc:annotation-driven />` — HandlerMapping·HandlerAdapter 자동 등록, JSON(Jackson)/XML(JAXB) 변환, `@Valid` 검증, `@DateTimeFormat` 등 지원
- `<mvc:default-servlet-handler/>` — `url-pattern`을 `/`로 바꾸면 톰캣의 DefaultServlet이 죽어 **정적 자원(css/js/img)을 못 찾게 되는데**, 이를 해결

### 코드 — `web.xml` (진입점)

```xml
<!-- 1. 루트 컨텍스트 : 서비스·DAO·트랜잭션·AOP -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:beans.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<!-- 2. 한글 인코딩 필터 -->
<filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>encodingFilter</filter-name>
    <url-pattern>*.do</url-pattern>
</filter-mapping>

<!-- 3. DispatcherServlet : 서블릿 컨텍스트 (Controller·ViewResolver) -->
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:beans-web.xml</param-value>
    </init-param>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>            <!-- ★ *.do 가 아니라 / -->
</servlet-mapping>
```

**해설 — ⚠️ 컨텍스트가 왜 2개인가 (면접 단골)**

```
Root ApplicationContext  (beans.xml)        ← ContextLoaderListener 가 로드
   Service, DAO, DataSource, TransactionManager, AOP
        ↑ 부모
        │ 자식은 부모의 Bean을 참조할 수 있다 (반대는 불가)
        ↓
Servlet ApplicationContext (beans-web.xml)  ← DispatcherServlet 이 로드
   Controller, ViewResolver, HandlerMapping
```
- **Controller는 Service를 `@Autowired`할 수 있지만, Service는 Controller를 참조할 수 없다.** 계층 방향이 설정 구조로 강제된다.
- 그래서 컴포넌트 스캔이 **정확히 반으로 갈려 있다**:
  ```xml
  <!-- beans.xml : Controller 를 제외하고 스캔 -->
  <context:component-scan base-package="myspring.user,myspring.student,myspring.aop.annot">
      <context:exclude-filter type="annotation"
                              expression="org.springframework.stereotype.Controller"/>
  </context:component-scan>

  <!-- beans-web.xml : Controller 만 스캔 -->
  <context:component-scan base-package="myspring.user,myspring.student">
      <context:include-filter type="annotation"
                              expression="org.springframework.stereotype.Controller"/>
  </context:component-scan>
  ```
  **자료 2.10절의 `<context:include-filter>`·`<context:exclude-filter>`가 실제로 이렇게 쓰인다.**
- ⚠️ **둘 다 스캔하면 Controller가 두 번 등록**되어 트랜잭션·AOP가 이상하게 걸리는 문제가 생긴다. 필터가 필수인 이유.

**⚠️ `url-pattern`이 `/`인 이유** — 자료 6.6절 그대로. `@PathVariable`을 쓰려면 `/users/test` 같은 URL을 받아야 하는데 `*.do`로는 불가능하다. 그래서 `/`로 바꾸고, 대신 죽어버린 정적 자원 처리를 `<mvc:default-servlet-handler/>`로 되살린다.

⚠️ **인코딩 필터의 `url-pattern`은 여전히 `*.do`다.** DispatcherServlet은 `/`인데 필터는 `*.do`이므로, **REST 요청(`/users`)에는 인코딩 필터가 안 걸린다.** 한글이 깨질 수 있는 실제 버그 지점. `/*`로 바꾸는 것이 맞다.

### 코드 — `beans-web.xml`

```xml
<mvc:annotation-driven />
<mvc:default-servlet-handler/>

<bean id="viewResolver"
      class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/" />
    <property name="suffix" value=".jsp" />
</bean>
```

**해설**: Controller가 `return "userList";`를 하면 ViewResolver가 **`/userList.jsp`**로 변환한다. 이것이 자료 6.3절 "ViewResolver: 뷰 이름을 기반으로 실제 뷰를 결정"의 실물.

**Java Config 버전 — `MvcConfig.java`**

```java
@Configuration
@EnableWebMvc                                            // <mvc:annotation-driven />
@ComponentScan(basePackages = { "myspring.*.controller" })  // Controller 만 스캔
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();                             // <mvc:default-servlet-handler/>
    }

    @Bean
    public static InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/");
        bean.setSuffix(".jsp");
        return bean;
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setContentNegotiationManager(manager);
        List<ViewResolver> resolvers = new ArrayList<>();
        resolvers.add(jspViewResolver());
        resolver.setViewResolvers(resolvers);
        return resolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(new MappingJackson2HttpMessageConverter());  // JSON 변환
    }
}
```

**해설**
- `@ComponentScan("myspring.*.controller")`로 **패키지 이름 규칙만으로** Controller를 분리했다. XML의 include-filter보다 깔끔하다.
- `MappingJackson2HttpMessageConverter` — 자료 7.4절의 "MappingJacksonHttpMessageConverter가 UserModel 객체를 JSON으로 변환"이 바로 이것. **Jackson 라이브러리를 쓰는 지점.**
- ⚠️ `WebMvcConfigurerAdapter`는 **Spring 5부터 deprecated**다. 인터페이스에 default 메서드가 생겨서(🔗 1-12절 Java 8 default method!) 이제는 `implements WebMvcConfigurer`로 쓴다.

### 코드 — Controller

**`user/controller/UserController.java`**

```java
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // ① Model + 뷰 이름 String 반환
    @RequestMapping("/getUserList.do")
    public String getUserList(Model model) {
        List<UserVO> userList = userService.getUserList();
        model.addAttribute("userList", userList);
        return "userList";                       // → /userList.jsp
    }

    // ② ModelAndView 반환 + @RequestParam
    @RequestMapping("/getUser.do")
    public ModelAndView getUser(@RequestParam String id) {
        UserVO user = userService.getUser(id);
        return new ModelAndView("userInfo", "user", user);   // 뷰이름, 속성명, 값
    }

    // ③ 폼에 뿌릴 데이터를 Map 으로 묶어 전달
    @RequestMapping("/insertUserForm.do")
    public ModelAndView insertUserForm() {
        List<String> genderList = new ArrayList<String>();
        genderList.add("남");  genderList.add("여");
        List<String> cityList = new ArrayList<String>();
        cityList.add("서울");  cityList.add("부산");  cityList.add("대구");  cityList.add("제주");
        Map<String, List<String>> map = new HashMap<>();
        map.put("genderList", genderList);
        map.put("cityList", cityList);
        return new ModelAndView("userInsert", "map", map);
    }

    // ④ @ModelAttribute : 폼 파라미터를 객체로 자동 바인딩
    @RequestMapping("/insertUser.do")
    public String insertUser(@ModelAttribute UserVO user) {
        if (user != null) userService.insertUser(user);
        return "redirect:/getUserList.do";       // ★ 리다이렉트
    }

    // ⑤ @PathVariable : URL 경로에서 값 추출
    @RequestMapping(value = "/deleteUser.do/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/getUserList.do";
    }
}
```

**해설 — 자료 6.5절 어노테이션 4종이 전부 들어 있다**

1. **`Model` vs `ModelAndView`** (자료 6.3절)
   - `Model` + `return "뷰이름"` — 데이터와 뷰 이름을 **따로** 전달
   - `ModelAndView("뷰이름", "속성명", 값)` — **한 번에** 전달
   - 기능은 같다. **`Model` 방식이 요즘 스타일**이고 더 읽기 좋다.

2. **`@RequestParam String id`** — `/getUser.do?id=test`의 `id`를 받는다. 파라미터 이름과 변수 이름이 같으면 `@RequestParam("id")`처럼 이름을 명시하지 않아도 된다.

3. **`@ModelAttribute UserVO user`가 이 파일의 백미다.**
   - 폼에서 `userId`, `name`, `gender`, `city`가 넘어오면 Spring이
     ```java
     UserVO user = new UserVO();        // ← 기본 생성자 필요!
     user.setUserId(요청값);
     user.setName(요청값);
     ...
     ```
     을 **자동으로** 해준다.
   - ⚠️ **바인딩 규칙은 setter 이름**이다. 파라미터 `userId` → `setUserId()`. 이름이 안 맞으면 조용히 null이 된다.
   - 🔗 2-4절에서 `UserVO`에 기본 생성자가 왜 필요한지 여기서 답이 나온다.

4. **`return "redirect:/getUserList.do"`** — 등록/삭제 후 **목록으로 리다이렉트**. 왜 forward가 아니라 redirect인가?
   → **POST 후 새로고침 시 중복 등록을 막기 위해서다(PRG 패턴: Post-Redirect-Get).** forward면 URL이 `/insertUser.do`인 채로 남아 F5를 누르면 또 등록된다.

5. **`@PathVariable`은 `url-pattern="/"` 설정이 있어야 동작한다** (자료 6.6절). `/deleteUser.do/{id}` 형태의 URL은 `*.do` 패턴으로는 매칭되지 않는다.

⚠️ **`@RequestMapping`에 `method`가 없는 것들이 많다.** `/insertUser.do`가 GET으로도 호출된다는 뜻인데, **데이터를 변경하는 요청은 POST로 제한**하는 것이 원칙이다. 아래 REST 컨트롤러는 이 점을 지키고 있으니 비교해 볼 것.

---

## 2-8. REST

### 개념

**REST(REpresentational State Transfer)**: HTTP URI로 **자원**을 명시하고, HTTP Method로 그 자원을 **제어**하는 아키텍처.

| HTTP Method | CRUD |
|---|---|
| POST | Create (Insert) |
| GET | Read (Select) |
| PUT | Update or Create |
| DELETE | Delete |

**기존 방식 vs RESTful**
- 기존 게시판: **GET/POST만** 사용, URI가 **액션**을 나타냄 (`/deleteUser.do`)
- RESTful: **4가지 메서드 모두** 사용, URI가 **자원**을 나타냄 (`/users/{id}` + DELETE)

**핵심 어노테이션**

| | 역할 |
|---|---|
| `@RequestBody` | HTTP 요청 Body를 **Java 객체로** 받는다 |
| `@ResponseBody` | Java 객체를 **HTTP 응답 Body로** 보낸다 |
| `@RestController` | `@Controller` + 모든 메서드에 `@ResponseBody` |

**JSON vs XML**
- JSON — 경량 데이터 교환 형식. JavaScript 객체 표현식. **name-value 쌍** + **순서화된 리스트**. 요즘은 XML을 대체
- XML — 데이터 저장·전달용. 사용자가 태그를 마음대로 정의. **HTML은 표현, XML은 전달**에 초점
- **Jackson** — 가장 많이 쓰는 Java용 JSON 라이브러리

**표준 URI 설계**

| Action | URI | Method |
|---|---|---|
| 목록 | `/users` | GET |
| 단건 조회 | `/users/{id}` | GET |
| 등록 | `/users` | POST |
| 수정 | `/users` | PUT |
| 삭제 | `/users/{id}` | DELETE |

### 코드 — `user/controller/RestfulUserController.java`

```java
@RestController                        // = @Controller + @ResponseBody
public class RestfulUserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="/users", method=RequestMethod.GET,
                    produces = { "application/json"})
    public List<UserVO> getUserList() {
        return userService.getUserList();       // ★ 객체를 그대로 return → JSON 변환
    }

    @RequestMapping(value="/users/{id}", method=RequestMethod.GET,
                    produces = { "application/json"})
    public UserVO getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @RequestMapping(value="/users", method=RequestMethod.POST,
                    headers = {"Content-type=application/json"})
    public Boolean insertUser(@RequestBody UserVO user) {    // ★ JSON → 객체
        if (user != null) {
            userService.insertUser(user);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @RequestMapping(value="/users", method=RequestMethod.PUT,
                    headers = {"Content-type=application/json"})
    public Boolean updateUser(@RequestBody UserVO user) { ... }

    @RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
    public Boolean deleteUser(@PathVariable String id) { ... }

    // XML 로 응답하는 예
    @RequestMapping(value="/usersXml", method=RequestMethod.GET,
                    produces = { "application/xml"})
    public UserVOXML getUserListXml() {
        List<UserVO> list = userService.getUserList();
        return new UserVOXML("success", list);
    }
}
```

**해설 — 자료 7.4절 표와 정확히 일치한다**

1. **URI가 전부 `/users`로 통일되고, 동작은 HTTP Method가 구분한다.**
   ```
   UserController (기존)          RestfulUserController (REST)
   /getUserList.do        →       GET    /users
   /getUser.do?id=test    →       GET    /users/test
   /insertUser.do         →       POST   /users
   /updateUser.do         →       PUT    /users
   /deleteUser.do/{id}    →       DELETE /users/{id}
   ```
   **같은 저장소에 두 스타일이 나란히 있으니 직접 비교해 볼 것.** 자료 7.1절의 "URI는 액션이 아니라 제어하려는 자원을 나타낸다"가 무슨 뜻인지 이 대조로 확실해진다.

2. **`@RestController` 덕분에 `return userList;` 한 줄로 JSON이 나간다.**
   - `@Controller`였다면 `"userList"`를 **뷰 이름**으로 해석해 `/userList.jsp`를 찾았을 것이다.
   - `@ResponseBody`가 있으면 **HttpMessageConverter**(Jackson)가 객체를 JSON으로 직렬화해 Body에 쓴다.
   - 자료 7.4절의 "MappingJacksonHttpMessageConverter가 리턴값인 객체를 JSON으로 변환"이 이것.

3. **`@RequestBody UserVO user`** — 요청 Body의 JSON을 `UserVO`로 역직렬화.
   - ⚠️ `@ModelAttribute`(폼 파라미터)와 **혼동하지 말 것.**
     - `@ModelAttribute` — `userId=test&name=홍길동` (폼 인코딩)
     - `@RequestBody` — `{"userId":"test","name":"홍길동"}` (JSON Body)
   - 둘 다 `UserVO`를 만들지만 **입력 형식이 다르다.**

4. **`produces`와 `headers` 속성**
   - `produces = {"application/json"}` — **응답** Content-Type 지정
   - `headers = {"Content-type=application/json"}` — **요청** 헤더 조건. 이 헤더가 없으면 매핑되지 않는다
   - `/users`와 `/usersXml`이 `produces`만 다르게 해서 JSON/XML을 나눠 낸다.

5. ⚠️ **`Boolean`을 반환하는 것은 REST 관례가 아니다.** 실무에서는 HTTP 상태 코드로 표현한다.
   - 등록 성공 → `201 Created`
   - 삭제 성공 → `204 No Content`
   - 실패 → `400 Bad Request`
   🔗 PART 3에서 `ResponseEntity`로 이 문제를 푼다.

**`student/controller/RestfulStudentController.java` — 같은 패턴 + 문제점**

```java
@RestController
public class RestfulStudentController {

    @Autowired
    private StudentDao studentDao;          // ⚠️ Service 를 건너뛰고 DAO 직접 주입

    @RequestMapping(value = "/students/{code}", method = RequestMethod.GET)
    public StudentVO getStudent(@PathVariable int code) {          // ★ int 자동 변환
        return studentDao.selectStudentByCode(code);
    }

    @PostMapping("/coursestatus")                                   // ★ 축약 어노테이션
    public int insertCourseStatus(@RequestBody CourseStatusVO courseStatusVO)  { ... }

    @PostMapping("/coursestatus")                                   // ⚠️ URL 중복!
    public int insertCourseStatus(@RequestParam int student,
                                  @RequestParam int course,
                                  @RequestParam int score)  { ... }
}
```

**해설 — 이 파일에는 배울 점과 문제점이 함께 있다**

**배울 점**
- **`@PathVariable int code`** — URL의 문자열 `"101"`이 `int`로 자동 변환된다. Spring의 타입 컨버전. 자료 6.6절 `<mvc:annotation-driven>`이 등록해주는 `ConversionService` 덕분.
- **`@PostMapping("/coursestatus")`** — `@RequestMapping(value=..., method=RequestMethod.POST)`의 축약형. `@GetMapping`, `@PutMapping`, `@DeleteMapping`도 같은 계열. **Spring 4.3+ 문법이라 자료에는 없지만 실무 표준.**

**⚠️ 문제점 2가지**
1. **`@PostMapping("/coursestatus")`가 두 개다.** URL과 Method가 같으므로 **애플리케이션 기동 시 `IllegalStateException: Ambiguous mapping`이 발생한다.** JSON Body 방식과 폼 파라미터 방식을 둘 다 실험하다 남은 코드로 보인다. 하나를 주석 처리하거나 URL을 분리해야 한다.
2. **Controller가 Service를 건너뛰고 DAO를 직접 쓴다.** 자료 3.3절의 Layered Architecture 원칙 위반. `UserController`는 `UserService`를 거치는데 여기는 아니다. **트랜잭션 경계가 Service에 걸려 있으므로, DAO 직접 호출은 트랜잭션 없이 실행된다.** 🔗 2-6절과 함께 보면 왜 문제인지 명확해진다.

**⚠️ `StudentVO`의 구조도 눈여겨볼 것 — 객체 그래프 매핑**

```java
public class StudentVO {
    private Long id;
    private Integer code;
    private String name;
    private Integer age;
    private String grade;
    private String daynight;
    private DeptVO dept;                          // ★ 1:1 연관 객체
    private List<CourseStatusVO> courseStatus;    // ★ 1:N 연관 컬렉션
}
```
- `UserVO`가 **플랫한 값 객체**인 반면 `StudentVO`는 **다른 VO를 품고 있다.**
- MyBatis에서 이런 중첩 구조를 채우려면 `resultMap`의 `<association>`(1:1) / `<collection>`(1:N)이 필요하다. `StudentMapper.xml`에 그 설정이 들어간다.
- 🔗 PART 3의 JPA `@ManyToOne` / `@OneToMany`가 이 매핑을 어노테이션으로 대체한다.

### Spring MVC 예외 처리

자료 6.7절: `@ExceptionHandler`를 컨트롤러 메서드에 설정하면 그 컨트롤러의 예외를 처리할 수 있다. 예외 Type과 Message를 보여줄 JSP(`viewError.jsp`)를 작성.

`UserController`가 `@ExceptionHandler`를 import하고 있다. 🔗 **PART 3에서는 이것이 `@RestControllerAdvice`로 발전해 전역 예외 처리가 된다.** 컨트롤러마다 붙이는 대신 한 곳에서 전부 처리.

---

## 2-9. XML ↔ 어노테이션 ↔ Java Config 최종 대조표

**이 표 하나가 PART 2 전체 요약이다.**

| 기능 | XML (`SpringFWXml`) | Java Config (`SpringFWConfig`) |
|---|---|---|
| **Bean 등록** | `<bean id class>` | `@Bean` / `@Component` |
| **컴포넌트 스캔** | `<context:component-scan base-package>` | `@ComponentScan(basePackages)` |
| **설정 분할** | `<import resource>` | `@Import({A.class, B.class})` |
| **프로퍼티** | `<context:property-placeholder location>` | `@PropertySource` + `Environment` |
| **DataSource** | `<bean class="SimpleDriverDataSource">` | `@Bean DataSource dataSource()` (BasicDataSource) |
| **TransactionManager** | `<bean class="DataSourceTransactionManager">` | `@Bean PlatformTransactionManager` |
| **트랜잭션 활성화** | `<tx:advice>` + `<aop:advisor>` | `@EnableTransactionManagement` + `@Transactional` |
| **AOP 활성화** | `<aop:aspectj-autoproxy/>` | `@EnableAspectJAutoProxy` |
| **AOP 설정** | `<aop:config><aop:aspect>` | `@Aspect` + `@Before/@Around/...` |
| **MyBatis Factory** | `<bean class="SqlSessionFactoryBean">` | `@Bean SqlSessionFactory` |
| **Mapper 스캔** | `<bean class="MapperScannerConfigurer">` | `@MapperScan(basePackages)` |
| **MVC 활성화** | `<mvc:annotation-driven/>` | `@EnableWebMvc` |
| **정적 자원** | `<mvc:default-servlet-handler/>` | `configureDefaultServletHandling()` |
| **ViewResolver** | `<bean class="InternalResourceViewResolver">` | `@Bean InternalResourceViewResolver` |
| **JSON 변환** | `<mvc:annotation-driven/>`이 자동 | `configureMessageConverters()` |

**`AppConfig.java`가 이 모든 것을 묶는 진입점이다.**
```java
@Import({DatabaseConfig.class, MyBatisConfig.class})
@ComponentScan(basePackages = {"myspring.*.service","myspring.*.dao","myspring.aop.annot"})
@EnableAspectJAutoProxy  //<aop:aspectj-autoproxy />
public class AppConfig {
}
```
`beans.xml` 하나가 하던 일을 **`AppConfig` + `DatabaseConfig` + `MyBatisConfig` 세 클래스로 나눈 것.** 역할별로 파일이 갈려 XML보다 오히려 찾기 쉽다.

⚠️ **`AppConfig`에 `@Configuration`이 빠져 있다.** `@Import`, `@ComponentScan`만 있고 `@Configuration`이 없다. 동작은 하지만(lite mode) **`@Bean` 메서드 간 싱글톤 보장이 안 되는 모드**로 취급될 수 있으므로 붙이는 것이 맞다.

---

## 2-10. SpringFW 파트 자가 점검 질문

**프레임워크·IoC**
1. 라이브러리와 프레임워크의 차이를 한 문장으로? → 실행 제어의 주체
2. `Hello.java`에 Spring import가 하나도 없는데 왜 Spring Bean인가? → POJO
3. DL과 DI의 차이는? → `getBean()` vs `@Autowired`
4. Bean의 기본 스코프는? 코드로 어떻게 증명하나? → `assertSame`

**DI**
5. `@Autowired`와 `@Resource`의 차이는? → `HelloBeanSpringTest`
6. 같은 타입 Bean이 2개일 때 어떻게 하나? → `@Qualifier`
7. `<property value>`와 `<property ref>`의 차이는?
8. `<property name="printer">`의 `name`은 필드명인가 setter명인가?
9. `@Component`로 등록할 수 없는 클래스는 어떤 것인가? → 외부 라이브러리
10. `@Configuration` 클래스의 `@Bean` 메서드를 직접 호출해도 왜 싱글톤인가? → CGLIB 프록시
11. 생성자 주입이 필드 주입보다 권장되는 이유 3가지는?

**데이터 접근**
12. 순수 JDBC 5단계 중 JdbcTemplate이 없애준 것은? → 2-4절 표
13. `#{}`와 `${}`의 차이는? 왜 중요한가? → SQL Injection
14. `SqlSession` 대신 `SqlSessionTemplate`을 쓰는 이유는? → Thread-safe
15. Mapper 인터페이스 방식의 장점은? → Type Safe
16. `UserDao` 구현체가 3개인데 `UserServiceImpl`은 왜 안 고쳐도 되나?
16-1. `<if>` / `<where>` / `<sql>`+`<include>`는 각각 어떤 문제를 해결하나? → `StudentMapper.xml`

**AOP**
17. Target / Advice / Pointcut / JoinPoint / Aspect / Advisor / Weaving을 각각 설명하라
18. `joinPoint.proceed()`가 하는 일은? → `PerformanceTraceAdvice`
19. `execution(* a.b.*.*(..))`와 `execution(* a.b..*.*(..))`의 차이는?
20. Spring AOP가 메서드 조인 포인트만 지원하는 이유는? → 동적 프록시
21. `@AfterReturning(returning="ret")`에서 `ret`이라는 이름은 어디와 일치해야 하나?

**트랜잭션**
22. `MANDATORY`와 `NEVER`는 각각 언제 예외를 던지나? → `beans.xml` 주석
23. 격리수준 5단계를 낮은 것부터 나열하고, 기본값은?
24. 체크 예외가 발생하면 롤백되나? → `rollbackFor`
25. `@Transactional`이 self-invocation에서 안 걸리는 이유는?
26. `<tx:advice name="get*" read-only="true">`와 클래스 레벨 `@Transactional(readOnly=true)`는 어떻게 같은 효과를 내나?

**MVC·REST**
27. 컨텍스트가 Root/Servlet 두 개인 이유는? 어느 쪽이 부모인가?
28. `<context:exclude-filter>`로 Controller를 제외하지 않으면 무슨 일이 생기나?
29. `url-pattern`을 `/`로 바꾸면 왜 `<mvc:default-servlet-handler/>`가 필요한가?
30. `@ModelAttribute`와 `@RequestBody`의 차이는?
31. 등록 후 `redirect:`를 쓰는 이유는? → PRG 패턴
32. `@RestController`가 `@Controller`와 다른 점은?
33. `/deleteUser.do/{id}`와 `DELETE /users/{id}` 중 REST에 맞는 것과 그 이유는?
