# PART 1. Java

## 1-0. 저장소 개요

```
backendjava/src/
├─ basic/         step01~step10. 개념을 최소 코드로 압축한 커리큘럼
│  step01_syntax  변수/연산자/제어문/문자열
│  step02_array   배열
│  step03_method  메서드
│  step04_class   클래스와 객체, 캡슐화
│  step05_static  static 변수/메서드
│  step06_inheritance  상속, 오버라이딩, 업캐스팅
│  step07_polymorphism 추상 클래스, 인터페이스, 다형성
│  step08_exception    try-catch-finally, throws, 사용자 정의 예외
│  step09_collection   List / Map / Set
│  step10_layer        entity-control-exception 3계층 종합
│
├─ chapter1~6/    교재 진도용 실습 (문법 → OOP → 컬렉션)
│  chapter1 first        HelloWorld, MyDate, 계산기, String
│  chapter2 controlstmt  if/switch/for/while + util(Scanner, Random)
│  chapter3 oop          MyDate, Person (캡슐화·생성자·static)
│  chapter4 array        int배열, 2차원배열, 객체배열
│  chapter5 oop          bad/good 비교, inheritance, abstract, interface, printer
│  chapter6 collections  ArrayList 실습
│
├─ mylab/         요구사항이 있는 미니 프로젝트 4개
│  bank/    은행 계좌 (상속+다형성+예외)  ← 완성도 가장 높음
│  book/    출판물 관리 (다형성+통계)
│  library/ 도서관 대출/반납
│  student/ 학생 정보 + 학년 검증 예외
│
└─ workshop/      개념 하나씩 다루는 소규모 실습
   account/ 계좌 + 사용자 정의 예외(필드 추가형)
   animal/  추상클래스 + 인터페이스 동시 사용
   book/    Publication 상속 계층 (mylab/book의 원형)
   person/  주민번호 파싱, 배열 vs List 비교
```

- 빌드 도구 없음(Eclipse 프로젝트). 테스트 프레임워크 없음.
- **모든 실행은 각 클래스의 `main` + `System.out.println` 확인 방식.**
- 같은 주제가 `workshop`(초안) → `mylab`(개선판)으로 두 번 등장하는 경우가 많다. 비교해서 보면 "무엇이 개선인가"가 보인다.

---

## 1-1. Java 시작하기 — JVM · JRE · JDK

### 개념

| 구성 | 내용 |
|---|---|
| **JVM** | 바이트코드를 해석·실행하는 가상 기계. **JVM 자체는 OS에 종속적**이지만, 그 위에서 도는 바이트코드는 플랫폼 독립적 |
| **JRE** | JVM + 클래스 라이브러리(API). **실행**만 가능 |
| **JDK** | JRE + 개발 도구(javac 등). **개발 + 실행** 가능 |

**Java 프로그램 수행 3단계**
1. **컴파일** — `javac`가 `.java` → `.class` 바이트코드로 변환
2. **바이트코드 검증** — 클래스 로더가 로드 → Bytecode Verifier가 보안·유효성 확인
3. **런타임** — 인터프리터가 한 줄씩 해석하거나, JIT 컴파일러가 기계어로 변환해 실행

**Garbage Collection**: 더 이상 참조되지 않는 메모리를 자동 해제. C++과 달리 개발자가 직접 해제하지 않는다. `System.gc()`로 요청은 가능하지만 실제 실행 시점은 JVM이 결정한다.

**Java 플랫폼**: J2SE(데스크톱) / J2EE(기업용 대규모) / J2ME(모바일·임베디드)

**주요 버전 변천** — J2SE 5.0(Generic, foreach, AutoBoxing) → 6(JAX-WS) → 7(String switch, try-with-resources) → **8(Lambda, Stream, Functional Interface, default method)** → 9.

(참고로 Java는 1991년 Sun의 James Gosling이 가전제품 내장용으로 시작한 'Green Project'에서 'Oak'라는 이름으로 출발했다가, 1994년 웹(WWW)에 맞춰 'Java'로 개명되었다. 시험에 나올 내용은 아니고 그냥 배경 지식.)

### 코드 위치

`chapter1/first/HelloWorld.java` — 명령줄 인수를 받는 첫 프로그램

```java
public class HelloWorld {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("주민번호와 이름을 아규먼트로 넣으세요!!");
            System.exit(0);   // 0:정상종료, -1:비정상종료
        }
        System.out.println("주민번호 : " + args[0]);
        System.out.println("주민번호뒤번호첫글자 :" + args[0].charAt(6));
        checkGender(args);
        System.out.println(checkGender(args[0]));
    }
    public static String checkGender(String ssn) { ... }   // 오버로딩 ①
    public static void   checkGender(String[] args) { ... } // 오버로딩 ②
}
```

**해설**
- `main(String[] args)`의 `args`가 자료에서 말한 **Command-Line Arguments**다. `java HelloWorld 7212121028102 이성호`로 실행하면 배열에 순서대로 담긴다.
- `args.length`로 개수를 먼저 검사하는 것이 관례. 안 하면 `ArrayIndexOutOfBoundsException`.
- 같은 이름 `checkGender`가 **매개변수 타입만 다르게** 두 개 있다 → 메서드 오버로딩(1-8절)의 첫 등장.
- `System.exit(0)`은 `finally`도 건너뛰고 즉시 종료시킨다는 점을 예외 절(1-12)과 연결해서 기억할 것.


---

## 1-2. 객체 지향 — 추상화 · 클래스 · 패키지

### 개념

**추상화(Abstraction)**: 현실 세계의 객체에서 프로그래밍 관점에서 관심 있는 **속성(멤버 변수)**과 **행동(멤버 메서드)**만 뽑아내는 것. 그 결과물이 **클래스**다.

- 클래스는 설계도일 뿐, 값을 가진 객체가 아니다.
- 실제 값을 갖는 **객체(인스턴스)**는 `new`로 메모리에 할당된다.

**패키지와 임포트**

| | 규칙 |
|---|---|
| `package` | 관련 클래스를 묶음. **한 소스 파일에 딱 한 번만** |
| `import` | 다른 패키지의 클래스를 참조. 여러 번 가능. **같은 패키지면 불필요** |

**소스 파일 레이아웃**: `package` 1회 → `import` n회 → 클래스 선언. **public 클래스는 파일당 하나**, 이름은 파일명과 같아야 한다.

**명명 규칙(Naming Rule)**

| 대상 | 규칙 | 예 |
|---|---|---|
| 클래스 / 인터페이스 | 대문자 시작, 단어마다 대문자 | `MemberEntity`, `ConnectionPool` |
| 메서드 / 변수 | 소문자 시작, 이후 단어 첫 글자 대문자 | `displayMovieInfo()`, `memberName` |
| 상수 | 전부 대문자 + `_` 구분 | `MAX_NUMBER` |

### 코드 위치와 해설

이 저장소 전체가 이 명명 규칙을 지키고 있는데, 특히 **패키지 구조 자체가 역할을 표현**하고 있다는 점이 핵심이다.

```
mylab.bank.entity      ← 데이터를 담는 클래스 (Account, Bank, SavingsAccount...)
mylab.bank.control     ← 흐름을 지휘하는 클래스 (BankDemo)
mylab.bank.exception   ← 예외 클래스 (AccountNotFoundException...)
```

`mylab/bank/control/BankDemo.java` 상단을 보면 import가 이렇게 잡힌다.

```java
package mylab.bank.control;

import mylab.bank.entity.Bank;
import mylab.bank.entity.SavingsAccount;
import mylab.bank.exception.AccountNotFoundException;
import mylab.bank.exception.InsufficientBalanceException;
```

**해설**
- `BankDemo`는 `control` 패키지에 있으므로 `entity`, `exception` 패키지의 클래스를 쓰려면 **반드시 import**해야 한다. 자료에서 말한 "같은 패키지 내면 import 불필요"의 반대 사례다.
- `CheckingAccount`는 import에 없다. `bank.createCheckingAccount(...)`의 **반환값을 변수에 담지 않기 때문**이다. 타입 이름을 코드에 쓰지 않으면 import도 필요 없다는 게 눈으로 확인된다.

🔗 이 패키지 3분할이 PART 2의 `myspring.user.vo / dao / service / controller`, PART 3의 `entity / repository / controller`로 이어진다.

---

## 1-3. 캡슐화 — private 필드 + getter/setter

### 개념

**캡슐화**: 데이터와 기능을 하나로 묶고 외부에서 직접 접근하지 못하게 숨기는 것.

**왜 필요한가**: `MyDate`의 `day`를 public으로 두면 외부에서 `date.day = 32;` 같은 무효한 값을 넣을 수 있다 → 데이터 무결성 파괴.

**구현 방법**
1. **정보 은닉** — 멤버 변수를 `private`으로
2. **Getter/Setter** — `public` 메서드로 통제된 통로 제공, **setter 안에서 유효성 검사**

**캡슐화의 확장**: 데이터 보호뿐 아니라 **구현 상세를 숨기는 것**까지 포함한다. 내부 로직을 private으로 감추면, 내부가 바뀌어도 외부는 인터페이스만 쓰므로 유지보수가 쉬워진다.

### 코드 위치

**① 기본형 — `basic/step04_class/Product.java`**

이 파일 주석 자체가 entity 클래스의 5요소를 명시하고 있다.

```java
public class Product {

    // 1) 필드는 private 으로 감춘다.
    private String name;
    private int price;
    private int stock;

    // 2) 기본 생성자
    public Product() {
    }

    // 3) 값을 받는 생성자 (생성자 오버로딩)
    public Product(String name, int price) {
        this(name, price, 0);          // 다른 생성자 호출, 반드시 첫 줄
    }

    public Product(String name, int price, int stock) {
        this.name = name;
        setPrice(price);               // ★ 검증 로직이 있는 setter 를 재사용
        this.stock = stock;
    }

    // 4) getter / setter
    public int getPrice() { return price; }

    /** 가격을 설정한다. 음수는 허용하지 않는다. */
    public void setPrice(int price) {
        if (price < 0) {
            System.out.println("가격은 0 이상이어야 합니다. 0으로 설정합니다.");
            this.price = 0;
            return;
        }
        this.price = price;
    }

    // 5) toString()
    @Override
    public String toString() {
        return "Product [name=" + name + ", price=" + price + ", stock=" + stock + "]";
    }
}
```

**해설 — 이 코드가 보여주는 것**
- `setPrice`가 **검증을 담고 있다.** 필드를 private으로 감춘 진짜 이유가 이것이다. public 필드였다면 `p.price = -1000;`을 막을 방법이 없다.
- ⚠️ **생성자에서 `this.price = price`가 아니라 `setPrice(price)`를 호출**한 점. 생성자를 통해 들어오는 값도 똑같이 검증받게 만드는 패턴이다. 초보 코드에서 가장 자주 빠뜨리는 부분.
- `ProductDemo`에서 이 검증이 실제로 도는 걸 확인한다.
  ```java
  p3.setPrice(-1000);
  System.out.println(p3);   // price=0 으로 찍힌다
  ```

**② 같은 패턴 — `chapter3/oop/MyDate.java`** (자료의 MyDate 예제 그대로)

```java
public void setMonth(int month) {
    if(month >= 1 && month <= 12 ){
        this.month = month;
    }else {
        System.out.println("Month는 1~12 사이의 값만 가능합니다.");
    }
}
```

`TestMyDate`에서 `date.setMonth(13)`을 호출해 거부되는 것을 확인한다.

**③ setter가 파생 값을 만드는 응용 — `workshop/person/entity/PersonEntity.java`**

```java
public void setSsn(String ssn) {
    this.ssn = ssn;
    char genderNum = ssn.charAt(6);        // 주민번호 7번째 자리
    if(genderNum == '1' || genderNum == '3') {
        setGender('남');
    }else {  //2,4
        setGender('여');
    }
}
```

**해설**
- `gender` 필드에는 **외부에서 값을 넣지 않는다.** 주민번호를 넣으면 성별이 자동으로 계산된다.
- 이것이 "구현 상세를 숨긴다"의 실제 사례다. 외부는 성별 판정 규칙(7번째 자리, 1·3이면 남)을 알 필요가 없다.
- 생성자도 필드 대입 대신 setter를 호출한다.
  ```java
  public PersonEntity(String name, String ssn, String address, String phone) {
      setName(name);
      setSsn(ssn);      // ← 여기서 gender 가 함께 세팅된다
      setAddress(address);
      setPhone(phone);
  }
  ```
- ⚠️ `charAt(6)`이 7번째 자리인 이유는 인덱스가 0부터라서. `StringDemo.java`에도 같은 코드가 설명과 함께 반복되어 있다.

**④ boolean 필드의 getter 관례 — `basic/step10_layer/entity/Member.java`**

```java
public boolean isActive() {   // boolean 의 getter 는 관례상 is~ 로 시작
    return active;
}
public boolean isAdult() {    // 필드가 없는 "계산된" 값도 is~ 로
    return age >= 19;
}
```

🔗 이 `isXxx()` 관례는 PART 3의 Lombok `@Getter`가 boolean 필드에 대해 자동 생성하는 이름과 같다.

---

## 1-4. 생성자

### 개념

**생성자의 4가지 특징**
1. 클래스 이름과 동일
2. 반환 타입이 없음
3. **상속되지 않음**
4. `new`를 만났을 때 호출됨

**기본 생성자**: 개발자가 생성자를 **하나도 정의하지 않으면** 컴파일러가 자동 생성. ⚠️ **하나라도 정의하면 자동 생성되지 않는다** → `new MyDate();`가 컴파일 오류.

**`this()`와 `super()` 3원칙**
1. 반드시 **생성자의 첫 줄**
2. 둘을 **동시에** 쓸 수 없음
3. 명시하지 않으면 **묵시적으로 `super()`가 호출**된다 → 부모에 기본 생성자가 없으면 컴파일 에러

**객체 생성 시 초기화 순서**: 메모리 할당 → 기본값 초기화 → 명시적 초기화 → 생성자 초기화 → 참조 변수에 주소 할당

### 코드 위치

**① 기본 생성자를 명시적으로 남겨둔 이유 — `mylab/book/entity/Magazine.java`**

```java
public class Magazine extends Publication {
    private String publishPeriod;

    public Magazine() {                    // ← 굳이 빈 생성자를 쓴 이유
    }

    public Magazine(String title, String publishDate, int page, int price, String publishPeriod) {
        super(title, publishDate, page, price);   // 부모 생성자 호출
        this.publishPeriod = publishPeriod;       // 자신만의 속성 초기화
    }
}
```

**해설**
- 아래 인자 5개짜리 생성자를 정의했기 때문에 **기본 생성자가 자동 생성되지 않는다.** 그래서 위에 빈 생성자를 직접 써 준 것.
- `super(...)`가 첫 줄에 있다. 부모의 `title/publishDate/page/price`를 부모 생성자에게 맡기고, 자식은 자기 필드만 챙긴다 → **중복 제거**.
- ⚠️ `Publication`에도 기본 생성자가 명시되어 있다. 없으면 자식에서 `super()` 묵시 호출 시 에러가 난다.

**② `this()`로 생성자 재사용 — `chapter5/oop/printer/LaserPrinter.java`**

```java
public LaserPrinter(String id) {
    super(id);                    // 부모 클래스의 생성자를 호출
    tonerCapacity = 200.0;
    tonerReductionRate = 0.3;
}

public LaserPrinter(String id, double tonerCapacity, double tonerReductionRate) {
    this(id);                     // 같은 클래스내의 다른 생성자를 호출
    this.tonerCapacity = tonerCapacity;
    this.tonerReductionRate = tonerReductionRate;
}
```

**해설 — `super()`와 `this()`의 역할 분담이 한 파일에 다 있다**
- 인자 1개 생성자 → `super(id)`로 부모에게 id를 넘기고 기본값을 채운다.
- 인자 3개 생성자 → `this(id)`로 **위 생성자를 먼저 실행**시킨 뒤 값을 덮어쓴다.
- 결과적으로 `super(id)`는 한 곳에만 쓰여 있다. 이게 자료가 말한 "생성자 오버로딩 + this() 재사용"의 정석 형태.

**③ 생성자 호출 순서를 눈으로 확인 — `chapter5/oop/inheritance/`**

이 패키지는 모든 생성자에 println을 박아 **호출 순서를 출력으로 보게** 만들어 놓았다.

```java
// Employee.java
public Employee() {
    super();                                  // Object 의 생성자
    System.out.println("Employee default constructor 호출됨");
}
public Employee(String name, double salary) {
    System.out.println("Employee constructor 호출됨 " + name);
    ...
}

// Manager.java
public Manager(String name, double salary) {
    super(name, salary);
    System.out.println("Manager 생성자 호출됨 " + name );
}
public Manager(String name, double salary, String dept) {
    this(name,salary);                        // ← this() 먼저
    this.dept = dept;
    System.out.println("Manager 생성자 호출됨 " + dept);
}
```

`new Manager("고길동",100.0,"인사부")` 실행 시 출력 순서:
```
Employee constructor 호출됨 고길동      ← 부모가 먼저
Manager 생성자 호출됨 고길동            ← this() 가 부른 생성자
Manager 생성자 호출됨 인사부            ← 마지막
```

**해설**: "생성자는 부모 → 자식 순으로 실행된다"를 말로만 외우지 말고 이 출력으로 확인할 것. 면접에서 자주 나온다.

**④ this()로 기본값 채우기 — `workshop/animal/entity/Cat.java`**

```java
public Cat() {
    this("");            // 이름을 빈 문자열로
}
public Cat(String name) {
    super(4);            // 고양이는 다리 4개
    this.name = name;
}
```


---

## 1-5. 데이터 타입 · 변수 Scope · 형변환

### 개념

**Primitive vs Reference**

| 구분 | 종류 | 기본값 | 저장하는 것 |
|---|---|---|---|
| Primitive | boolean / char / byte, short, int, long / float, double | false, `'\u0000'`, 0, 0.0 | **값 자체** |
| Reference | 나머지 전부 | `null` | **객체의 주소** |

- 정수 리터럴의 기본형은 `int`, 실수 리터럴의 기본형은 `double` → `long`은 `L`, `float`는 `F`를 붙인다.
- `char`는 16비트 유니코드. `'\uAC00'` 형태 표현 가능.

**변수의 Scope (선언 위치에 따른 3분류)**

| 종류 | 생명주기 | 자동 초기화 | 참조 범위 | 메모리 |
|---|---|---|---|---|
| **지역 변수** | 메서드 시작~종료 | ❌ **안 됨** | 그 메서드 내부 | 스택 |
| **멤버(인스턴스) 변수** | 객체 생성~해제 | ⭕ 됨 | 한 객체 내 | 힙 |
| **클래스(static) 변수** | 클래스 로드~앱 종료 | ⭕ 됨 | 전 객체 공통 | 스태틱 영역 |

⚠️ **지역 변수는 자동 초기화되지 않는다.** `int y; if(x>50){y=9;} z=y+x;` → if가 거짓일 때 y가 미초기화이므로 **컴파일 오류**.

**Promotion / Casting**
- 대원칙: **정보를 잃지 않는 방향**으로만 자동 변환
- Promotion(자동): `byte → short → int → long → float → double`, `char → int`
- Casting(강제): 상위 → 하위. `(int)longVal`. 범위를 벗어나면 정보 손실.

### 코드 위치

**① `basic/step01_syntax/VariableDemo.java`** — 자료의 표를 그대로 코드로 옮긴 파일

```java
// 1. 기본형 : 값 자체를 저장
int page = 328;
double balance = 10000.0;       // 돈 계산에 자주 사용
char gender = '남';             // 작은따옴표
boolean available = true;
long big = 12345678901L;        // long 은 끝에 L
float rate = 0.03f;             // float 은 끝에 f

// 2. 참조형 : 객체의 주소를 저장
String title = "실용주의프로그래머";   // 큰따옴표

// 3. 상수 : final
final double DISCOUNT_RATE = 0.9;

// 4. 형변환
int intValue = 100;
double doubleValue = intValue;        // 자동 (작은 → 큰)
double price = 9900 * 0.85;
int discounted = (int) price;         // 강제, 소수점 버려짐

// 5. ★정수끼리 나누면 소수점이 사라진다
int count = 3, total = 10;
System.out.println(total / count);            // 3        ← 잘못된 평균
System.out.println(total / (double) count);   // 3.333... ← 올바른 평균

// 6. 지역변수는 반드시 초기화 후 사용
int sum;      // 선언만
sum = 0;      // 초기화
```

**⚠️ 5번이 실전에서 가장 자주 터지는 버그**다. 이 저장소 안에서도 평균 계산이 나오는 곳마다 `(double)` 캐스팅이 반복된다.

```java
// MemberManager.getAverageAge()
return (double) total / members.size();   // 정수 나눗셈 주의

// ArrayDemo
double average = (double) total / prices.length;

// StatisticsAnalyzer.calculatePublicationDistribution()
distribution.put(type, countByType.get(type) * 100.0 / total);  // 100.0 으로 실수화
```
마지막 것은 캐스팅 대신 **`100`이 아니라 `100.0`을 곱해서** 실수로 승격시키는 방법이다. 둘 다 알아둘 것.

**② `chapter1/first/TestDataType.java`** — 타입 경계와 형변환 실험

```java
short num1 = 128;
short num2 = 20;
short num3 = (short)(num1 + num2);   // ⚠️ short+short 의 결과는 int → 캐스팅 필요

byte b = 127;      // byte 범위 -128 ~ 127
b = (byte)num1;    // 128 을 byte 로 → 오버플로우

float pie  = 3.14F;
float pie2 = (float)3.14;   // 실수 리터럴 기본형이 double 이므로

char ch = '\uac00';   // 유니코드로 '가'
long num4 = 3000000L * 3000000L * 3000000L;   // L 없으면 int 오버플로우
long regNo = 7201052347538L;                  // 주민번호는 int 범위 초과

System.out.println('A');        // 65 가 아니라 A
System.out.println('A' + 'B');  // 131  ← char + char 는 int 연산
System.out.println('1' + 2);    // 51   ← '1' 은 49
```

**해설 — 자료의 Promotion 규칙이 실제로 나타나는 지점**
- `'A' + 'B'`가 `"AB"`가 아니라 `131`인 이유: `char`는 산술 연산 시 `int`로 promotion된다.
- `short + short`도 결과가 `int`다. 그래서 다시 `short`에 담으려면 캐스팅이 필요하다. **자바에서 산술 연산의 최소 단위는 int**라는 규칙.

**③ Scope와 static initializer — `chapter3/oop/Person.java`**

```java
public class Person {
    private String name;           // 인스턴스 변수
    static private int fingerCount;  // 클래스 변수

    public Person() {
        System.out.println("default constructor가 호출됨");
    }

    static {                       // static initializer
        System.out.println("static initializer 호출됨");
        fingerCount = 100;
    }
}
```

`PersonMain.java`에서 이걸 확인한다.

```java
Person person = null;
System.out.println(person.getFingerCount() + " " + Person.getFingerCount());
```

**해설 — 이 두 줄이 이 파일의 핵심**
- `person`이 **`null`인데도 `person.getFingerCount()`가 NullPointerException 없이 동작한다.** static 메서드는 객체가 아니라 **클래스**에 속하기 때문이다.
- 컴파일러가 `person.getFingerCount()`를 `Person.getFingerCount()`로 바꿔서 처리한다. 그래서 `null`이어도 무관.
- ⚠️ 다만 **이렇게 쓰면 안 된다.** static 멤버는 `클래스이름.멤버`로 접근하는 것이 원칙. 이 코드는 "왜 동작하는가"를 보여주기 위한 실험 코드다.
- `static` 블록은 **클래스 로드 시 딱 한 번** 실행되므로, 출력 순서는 `static initializer` → `default constructor`가 된다.

---

## 1-6. 연산자와 흐름 제어

### 개념

**연산자 우선순위** (높음 → 낮음): 단항 → 산술 → 쉬프트 → 비교 → 비트 → 논리 → 삼항 → 대입

**Short-circuit(단축 평가)**
- `&&`: 앞이 false면 뒤를 **평가하지 않음**
- `||`: 앞이 true면 뒤를 **평가하지 않음**
- `&`, `|`: **양쪽 모두 평가**

⚠️ 자료의 결정적 예시:
```java
if (divisor != 0 &  dividend/divisor > 10)  // divisor==0 이면 ArithmeticException
if (divisor != 0 && dividend/divisor > 10)  // 안전
```

**switch**: `expr`는 int로 표현 가능한 값(byte, short, char, int). `break`가 없으면 **fall-through**.

**반복문 3종**: `for`(횟수를 알 때) / `while`(조건이 만족하는 동안) / `do~while`(**최소 1회는 실행**)

### 코드 위치

**① `basic/step01_syntax/ControlFlowDemo.java`** — 6가지 제어문을 한 파일에

```java
// 2. switch : 주민번호 7번째 자리로 성별 (PersonEntity 와 동일한 규칙)
char genderNum = '2';
switch (genderNum) {
case '1':
case '3':                          // ★ case 를 연달아 쓰면 OR 처럼 동작
    System.out.println("성별 : 남");
    break;                         // break 를 빠뜨리면 아래 case 로 흘러간다
case '2':
case '4':
    System.out.println("성별 : 여");
    break;
default:
    System.out.println("판별 불가");
}

// 5. do-while : 최소 1번은 실행된다
int n = 0;
do {
    System.out.println("do-while 은 최소 한 번 실행 : " + n);
    n++;
} while (n < 1);

// 6. break / continue
String[] names = { "이순호", "김하늘", "박영선" };
for (int i = 0; i < names.length; i++) {
    if (names[i].equals("김하늘")) {
        System.out.println("찾음! 인덱스 " + i);
        break;                     // 찾았으면 즉시 탈출
    }
}
for (int i = 1; i <= 5; i++) {
    if (i % 2 == 0) continue;      // 이번 회차만 건너뛴다
    System.out.println("홀수 : " + i);
}
```

**해설**
- `case '1': case '3':`을 붙여 쓴 것이 **의도적인 fall-through 활용**이다. 자료가 경고한 "break 빠뜨리면 흘러간다"를 역이용한 정상 패턴.
- 같은 성별 판정 로직이 `HelloWorld`에선 if-else로, 여기선 switch로, `PersonEntity`에선 if-else로 세 번 나온다. **같은 요구사항을 다른 문법으로 푸는 비교**로 읽으면 좋다.

**② 삼항 연산자 — `basic/step01_syntax/OperatorDemo.java`**

```java
double amount = 50000, limit = 30000;
String result = (amount > limit) ? "한도 초과" : "출금 가능";

// ⚠️ 문자열 결합 함정
System.out.println("1 + 2 = " + 1 + 2);     // "1 + 2 = 12"  ← 왼쪽부터 결합
System.out.println("1 + 2 = " + (1 + 2));   // "1 + 2 = 3"   ← 괄호로 먼저 계산
```

**해설**: `+`는 한쪽이 String이면 결합 연산자가 된다. **왼쪽에서 오른쪽으로** 평가되므로 `"..." + 1`이 먼저 문자열이 되고, 그 뒤 `+ 2`도 결합이 된다.

삼항 연산자는 `Member.toString()`에서도 실전 사용된다.
```java
return String.format("[%s] %s (%d세) - %s", id, name, age, active ? "활동" : "휴면");
```

**③ 반복문 실습 — `chapter2/controlstmt/`**

```java
// TestForLoop : 1~10 중 2의 배수도 3의 배수도 아닌 수의 합
for(int i=1; i<=10;i++) {
    if(i % 2 != 0  &&  i % 3 != 0) sum += i;
}

// TestWhile : 같은 결과를 for 와 while 로 두 번, 중첩 반복도 두 번
for (int i = 1; i <= 100 ; i++) sum += i;
while(i <= 100) { sum += i; i++; }

for(int j=0; j<=10; j++) {           // 별 찍기 (중첩 for)
    for(int k=0;k<=j;k++) System.out.print("*");
    System.out.println("");
}
```

**해설**: `for`와 `while`이 **완전히 상호 변환 가능**하다는 것을 같은 결과로 보여주는 구성이다. 중첩 반복도 for/while 두 벌로 되어 있다.

**④ 조건문 → 메서드 추출 — `chapter2/controlstmt/TestIf_Switch.java`**

```java
Calendar calendar = Calendar.getInstance();
int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

if(dayOfWeek == 1) { ... } else if(dayOfWeek == 2) { ... }   // if 버전

String dayStr = getDayOfWeek(dayOfWeek);                     // switch 버전 (메서드로 추출)

public static String getDayOfWeek (int day) {
    String dayOfWeekStr = null;
    switch (day) {
    case 1: dayOfWeekStr="일요일"; break;
    ...
    }
    return dayOfWeekStr;
}
```

**해설**: **같은 분기 로직을 if-else 인라인 / switch 메서드 두 방식으로 나란히** 둔 코드. 값이 정해진 몇 가지 중 하나일 때는 switch + 메서드 추출이 읽기 좋다는 것을 보여준다.

**⑤ 기타 유틸 — `chapter2/util/`**

```java
// TestRandomNumber : 1~100 난수
randomInt = (int)(Math.random() * 100) + 1;

// TestCheckNumber : 문자가 숫자인지 판정
if( ch >= '0' &&  ch <= '9') { ... }   // char 를 범위 비교

// TestScanner : do-while 로 입력 받기
Scanner scan = new Scanner(System.in);
name = scan.next();       // 공백 전까지
kor  = scan.nextInt();    // 정수
```

⚠️ `Math.random()`은 `0.0 이상 1.0 미만`을 반환한다. `*100`하면 `0~99.99...`, `(int)` 캐스팅으로 `0~99`, `+1`해서 `1~100`. 이 3단계를 정확히 기억할 것.

---

## 1-7. 배열

### 개념

- 배열은 **객체**다. 배열의 이름은 주소를 담은 **참조 변수**.
- ⚠️ **선언 위치 주의**: `int[] a, b;` → 둘 다 배열. `int a[], b;` → a만 배열, b는 int.
- **크기는 한 번 정하면 변경 불가.** 새 크기로 `new`하면 새 객체가 만들어지고 기존 배열은 GC 대상.
- 생성 시 자동 초기화: 기본형은 0/false, 참조형은 `null`.
- `System.arraycopy(원본, 원본시작, 대상, 대상시작, 길이)` — 참조형 배열을 복사하면 **같은 객체를 함께 참조**하게 된다.

### 코드 위치

**① `basic/step02_array/ArrayDemo.java`**

```java
int[] scores = new int[5];      // 기본값 0 으로 자동 초기화
System.out.println(scores[4]);      // 0
System.out.println(scores.length);  // ★ 괄호 없는 length (String 은 length())

String[] names = { "이순호", "김하늘", "박영선", "정민서" };  // 선언과 동시에

for (int i = 0; i < names.length; i++) { ... }  // 인덱스가 필요할 때
for (String name : names) { ... }               // 향상된 for, 인덱스 불필요할 때

// 합계 / 평균 / 최댓값 (한 번의 순회로)
int total = 0, max = prices[0];
for (int price : prices) {
    total += price;
    if (price > max) max = price;
}
double average = (double) total / prices.length;

// 값 찾기 : 못 찾았음을 -1 로 표시하는 관례
int foundIndex = -1;
for (int i = 0; i < names.length; i++) {
    if (names[i].equals(target)) { foundIndex = i; break; }
}

// 자주 만나는 오류
try {
    System.out.println(names[10]);
} catch (ArrayIndexOutOfBoundsException e) { ... }

// 2차원 배열
int[][] table = { { 1, 2, 3 }, { 4, 5, 6 } };
for (int[] row : table) { for (int value : row) { ... } }
```

**해설 — 시험에 나오는 3가지**
1. **`length` vs `length()`**: 배열은 필드라 괄호 없음, String은 메서드라 괄호 있음, List는 `size()`.
2. **최댓값 초기값을 `prices[0]`으로** 잡은 것. `0`으로 잡으면 전부 음수인 배열에서 틀린다.
3. **`-1` 관례**: "못 찾았음"을 나타내는 인덱스. `String.indexOf()`도 같은 규칙을 쓴다.

**② 다차원 배열 — `chapter4/array/TestIntMultiArray.java`**

```java
int[][] nums;
int[] nums2 [] = { {10,20},{30,40},{50,60} };   // ⚠️ 이것도 2차원 배열
int nums3 [][];                                  // ⚠️ 이것도

nums = new int[3][2];
System.out.println(nums.length);      // 3  (행 개수)
System.out.println(nums[0].length);   // 2  (열 개수)
```

**해설**: `int[] nums2[]`처럼 **대괄호가 타입 뒤와 변수 뒤에 나뉘어 있어도 2차원 배열**이다. 자료가 경고한 `int a[], b;` 함정과 같은 뿌리. 실무에서는 `int[][]` 형태로 통일해서 쓴다.

**③ 참조형 배열 — `chapter4/array/TestMyDateArray.java`**

```java
MyDate[] dates = new MyDate[3];    // 이 시점엔 전부 null
fillArrayNew(dates);               // ★ new 로 실제 객체를 채워야 쓸 수 있다

private static void fillArrayNew(MyDate[] dates) {
    dates[0] = new MyDate(2013, 7, 17);
    dates[1] = new MyDate(2014, 8, 1);
    dates[2] = new MyDate(2015, 9, 3);
}
```

**해설 — 자료의 "Reference Type Array" 그 자체**
- `new MyDate[3]`은 **객체 3개를 만드는 게 아니라, 객체 주소를 담을 칸 3개를 만드는 것**이다. 각 칸은 `null`.
- 채우지 않고 `dates[0].getMonth()`를 호출하면 `NullPointerException`.
- ⚠️ 배열을 메서드에 넘기면 **주소가 전달**되므로 메서드 안에서 채운 것이 밖에서도 보인다. 이게 `fillArrayNew(dates)`가 `void`인데도 동작하는 이유다.

**④ 배열의 한계 → List로 가는 다리 — `workshop/person/control/`**

같은 기능이 두 파일로 나란히 구현되어 있다.

```java
// PersonManager.java — 배열
PersonEntity[] persons = new PersonEntity[10];    // 크기를 10 으로 고정해야 함
personManager.fillPersons(persons);
persons[0] = new PersonEntity("이성호", ...);
for (PersonEntity person : persons) { ... }

// PersonManagerList.java — List
List<PersonEntity> personList = new ArrayList<>();  // 크기 미리 정할 필요 없음
persons.add(new PersonEntity("이성호", ...));
for (PersonEntity person : persons) { ... }         // 순회는 동일
```

**해설 — 이 두 파일을 나란히 놓고 보는 것이 이 절의 핵심**

| | 배열 | List |
|---|---|---|
| 크기 | `new PersonEntity[10]` — 미리 고정 | 자동 확장 |
| 추가 | `persons[0] = ...` — 인덱스 직접 관리 | `add(...)` |
| 개수 | `.length` | `.size()` |
| 순회 | 향상된 for — **동일** | 향상된 for — **동일** |

⚠️ 배열 버전은 사람이 11명이 되는 순간 코드를 고쳐야 한다. 자료가 말한 "배열은 크기 변경 불가 → 컬렉션은 자동 확장"의 실물 비교다.


---

## 1-8. 메서드 · 오버로딩 · 값 전달 방식

### 개념

**메서드 구조**: `[접근제한자] [static] 반환타입 메서드명(매개변수) { ... return 값; }`

**오버로딩(Overloading)**: 같은 클래스, 같은 이름, **매개변수의 개수나 타입이 다른** 메서드. 반환 타입과 접근 제한자는 상관없다. `System.out.println()`이 대표 사례.

**Pass-by-value — 자바는 항상 값 전달이다**

| 넘기는 것 | 전달되는 값 | 메서드 안에서 바꾸면 |
|---|---|---|
| Primitive | **값의 복사본** | 원본 안 바뀜 |
| Reference | **주소의 복사본** | `ref = new ...` → 원본 안 바뀜<br>`ref.setDay(4)` → **원본 바뀜** |

⚠️ 참조형이라고 무조건 원본이 바뀌는 게 아니다. **"참조를 갈아끼우면" 안 바뀌고, "참조가 가리키는 객체의 속성을 바꾸면" 바뀐다.**

### 코드 위치

**`basic/step03_method/MethodDemo.java`** — 이 개념 전체가 한 파일에 실험으로 들어있다

```java
public static void main(String[] args) {
    // 1. static 메서드는 객체 없이 클래스 이름으로 바로 호출
    int sum = add(3, 5);

    // 2. non-static 메서드는 반드시 객체를 만들어야 호출 가능
    MethodDemo demo = new MethodDemo();
    demo.printGreeting("김하늘");

    // 3. 오버로딩
    System.out.println(discount(10000));         // 기본 10%
    System.out.println(discount(10000, 0.2));    // 지정 20%

    // 5. 배열을 넘기면 "주소"가 전달 → 메서드 안에서 원본이 바뀐다
    int[] values = { 1, 2, 3 };
    doubleAll(values);
    for (int v : values) System.out.print(v + " ");   // 2 4 6

    // 6. 기본형을 넘기면 "값"이 복사 → 원본은 안 바뀐다
    int number = 10;
    changeValue(number);
    System.out.println("number = " + number);         // 여전히 10

    // 7. 가변인자
    System.out.println("총합 : " + sumAll(1, 2, 3, 4, 5));
}

/** 기본 할인율 10% */
public static int discount(int price) {
    return discount(price, 0.1);        // ★ 오버로딩된 다른 메서드를 재사용
}
public static int discount(int price, double rate) {
    return (int) (price * (1 - rate));
}

public static void doubleAll(int[] values) {   // 원본이 변경된다
    for (int i = 0; i < values.length; i++) values[i] *= 2;
}
public static void changeValue(int value) {    // 호출한 쪽에 영향 없음
    value = 999;
}
public static int sumAll(int... numbers) {     // 내부적으로 배열이다
    int total = 0;
    for (int n : numbers) total += n;
    return total;
}
```

**해설 — 5번과 6번이 이 파일의 존재 이유**
- `doubleAll(values)`가 `void`인데 배열이 바뀐다 → 주소가 넘어갔기 때문.
- `changeValue(number)`는 `value = 999`로 **참조(값)를 갈아끼운 것**이라 원본과 무관.
- 두 사례를 합치면 자료의 `changeObjectRef` vs `changeObjectAttr` 구분이 완성된다.
  - `ref = new MyDate(...)` = 갈아끼우기 → 원본 그대로
  - `ref.setDay(4)` = 내용 변경 → **원본 변경**

**같은 현상을 다른 각도로 — `basic/step04_class/ProductDemo.java`**

```java
Product alias = p2;
alias.setPrice(79000);
System.out.println("p2 도 함께 바뀐다 : " + p2);   // p2 의 가격도 79000
```

**해설**: 두 참조 변수가 같은 객체를 가리키는 자료의 `MyDate t = s;` 그림 그대로. 🔗 1-13절 String의 `==`/`equals` 차이와 같은 뿌리다.

**오버로딩의 다른 사례들**

```java
// chapter1/first/HelloWorld.java — 매개변수 타입이 다름
public static String checkGender(String ssn)
public static void   checkGender(String[] args)   // 반환 타입이 달라도 무관

// mylab/bank/entity/Bank.java — 계좌 종류별로 생성 메서드 분리
public SavingsAccount  createSavingsAccount(String ownerName, double initialBalance, double interestRate)
public CheckingAccount createCheckingAccount(String ownerName, double initialBalance, double withdrawalLimit)
```

⚠️ `Bank`의 두 메서드는 **이름이 다르므로 오버로딩이 아니다.** 매개변수가 `(String, double, double)`로 동일하기 때문에 이름을 같게 하면 컴파일 에러가 난다. **"매개변수 타입이 같으면 오버로딩 불가"**를 보여주는 반례로 읽으면 좋다.

---

## 1-9. static과 final

### 개념

**static**
- 객체가 아니라 **클래스 자체**와 연관 → Class 변수 / Class 메서드
- **클래스 로드 시** 생성되어 모든 객체가 공유
- 객체 생성 없이 `클래스이름.멤버`로 호출
- ⚠️ **static 메서드 안에서는 `this`, `super`, non-static 멤버를 쓸 수 없다.** (객체 없이 호출될 수 있으므로) 지역 변수는 가능.

**final**

| 붙는 곳 | 의미 |
|---|---|
| 클래스 | 상속 불가 |
| 메서드 | 오버라이딩 불가 |
| 변수 | 상수. 한 번 초기화되면 변경 불가 |

- `blank final variable`: 선언 시 초기화하지 않을 수 있지만, **모든 생성자에서 초기화**해야 한다.

### 코드 위치

**① `basic/step05_static/Counter.java`** — static 변수로 일련번호 발급

```java
public class Counter {
    /** 지금까지 생성된 객체 수 (모든 객체가 공유) */
    private static int totalCount = 0;

    /** 변하지 않는 값은 static final 상수로 */
    private static final int START_NUMBER = 1000;

    /** 이 객체만의 고유 번호 (객체마다 다름) */
    private int id;

    public Counter() {
        totalCount++;                          // 공유 변수를 증가시키고
        this.id = START_NUMBER + totalCount;   // 그 값을 이 객체의 번호로
    }

    public static int getTotalCount() { return totalCount; }

    /** static 메서드 안에서는 인스턴스 변수(id)나 this 를 쓸 수 없다 */
    public static String makeCode(String prefix, int number) {
        return prefix + number;
    }
}
```

**해설 — static 변수의 전형적 용도 3가지가 다 있다**
1. `totalCount` — **공유 카운터**. 생성자에서 증가시키면 "지금까지 몇 개 만들어졌나"를 추적할 수 있다.
2. `START_NUMBER` — **`static final` 상수**. 대문자 + 언더스코어 명명 규칙.
3. `id` — 대비를 위한 **인스턴스 변수**. 객체마다 다르다.

`StaticDemo.java`로 확인:
```java
System.out.println("생성 전 총 개수 : " + Counter.getTotalCount());  // 0
Counter c1 = new Counter();  // #1001
Counter c2 = new Counter();  // #1002
Counter c3 = new Counter();  // #1003
System.out.println("생성 후 총 개수 : " + Counter.getTotalCount());  // 3

// 우리가 늘 쓰던 것들도 사실 static
Math.max(10, 20);
Integer.parseInt("100");
String.valueOf(3.14);

// main 도 static → 인스턴스 메서드를 쓰려면 객체를 먼저 만들어야 한다
StaticDemo demo = new StaticDemo();
demo.hello();
```

**⚠️ 마지막 부분이 초보자가 가장 자주 막히는 지점이다.** `main`이 static이라서 같은 클래스의 인스턴스 메서드도 그냥은 못 부른다. 이 저장소의 거의 모든 `main`이 자기 자신의 객체를 먼저 만드는 이유다.
```java
PersonManager personManager = new PersonManager();   // workshop/person
TestMyDateArray test = new TestMyDateArray();        // chapter4/array
```

**② 실전 적용 — `mylab/bank/entity/Bank.java`**

```java
public class Bank {
    private List<Account> accounts;
    private int nextAccountNumber;      // ← 여기선 인스턴스 변수

    public Bank() {
        this.accounts = new ArrayList<>();
        this.nextAccountNumber = 1000;
    }

    public SavingsAccount createSavingsAccount(...) {
        String accountNumber = "AC" + nextAccountNumber++;   // AC1000, AC1001...
        ...
    }
}
```

**해설 — `Counter`와의 의도적 차이**
- `Counter`는 `totalCount`가 **static**이라 프로그램 전체에서 하나.
- `Bank`는 `nextAccountNumber`가 **인스턴스 변수**라 은행 객체마다 따로 1000부터 시작한다.
- 어느 쪽이 맞는지는 요구사항에 달렸다. **"은행이 여러 개면 계좌번호가 겹쳐도 되는가?"**가 판단 기준. 이 설계 선택을 의식적으로 하는 것이 중요하다.

**③ 같은 패턴 — `basic/step10_layer/control/MemberManager.java`**

```java
private int nextNumber;
private static final String ID_PREFIX = "M";   // 접두어는 상수

public MemberManager() {
    this.nextNumber = 1001;
}
private String generateId() {                  // ★ private — 외부에 노출 안 함
    return ID_PREFIX + (nextNumber++);
}
```

**해설**: ID 생성 규칙을 `private` 메서드로 숨겼다. 🔗 1-3절 "구현 상세를 숨긴다"의 또 다른 사례. 나중에 규칙이 `M1001` → `MEM-2024-001`로 바뀌어도 이 메서드만 고치면 된다.

**④ final의 다른 사용 — `chapter1/first/TestFooBarBaz.java`**

```java
final static int MAX_NUMBER = 20;      // 클래스 상수
for(int i=1;i<=MAX_NUMBER;i++) { ... }
```

`basic/step07_polymorphism/Circle.java`:
```java
private static final double PI = 3.141592;   // 매직 넘버를 상수로
```

---

## 1-10. 상속 — extends · super · 오버라이딩 · 접근 제한자

### 개념

**상속**: `class 자식 extends 부모`. **자바는 단일 상속만** 지원(다중 상속의 모호성 배제). 그 제약은 **인터페이스**로 보완한다.

**장점**: 코드 재사용 / 검증된 코드로 오류 최소화 / 공통점 통일화

**접근 제한자**

| 제한자 | 참조 가능 범위 | 클래스에 적용 |
|---|---|---|
| `private` | 클래스 내부만 | ❌ |
| `default`(생략) | 같은 패키지 | ⭕ |
| `protected` | 같은 패키지 + **상속 관계면 패키지 무관** | ❌ |
| `public` | 어디서나 | ⭕ |

**오버라이딩(Overriding)**: 자식이 부모 메서드의 **본문을 재정의**.

**오버라이딩 vs 오버로딩**

| | 오버라이딩 | 오버로딩 |
|---|---|---|
| 위치 | 하위 클래스 | 같은 클래스 |
| 이름 | 동일 | 동일 |
| 매개변수 | **동일해야 함** | **달라야 함** |
| 반환 타입 | 동일 | 달라도 됨 |
| 접근 제한자 | **부모보다 넓거나 같아야** | 무관 |
| throws 예외 | **부모와 같거나 더 구체적(자식 예외)** | 무관 |

⚠️ 마지막 두 줄이 시험 단골이다. `public` → `private`으로 오버라이딩 불가. `IOException` → `Exception`(부모)으로 throws 불가.

**super**: 부모 객체를 가리킴. `super.멤버`, `super.메서드()`, `super(...)`(부모 생성자).

### 코드 위치

**① 기본형 — `basic/step06_inheritance/`**

```java
// Employee.java (부모)
public class Employee {
    private String name;
    private int baseSalary;
    protected String department;      // ★ 자식이 직접 접근 가능하도록 protected

    public int calculateSalary() {    // 자식이 재정의할 메서드
        return baseSalary;
    }
    public void printInfo() {
        System.out.println(name + " (" + department + ") 급여 : "
                            + calculateSalary() + "원");   // ★ 여기가 중요
    }
    @Override
    public String toString() {
        return "이름: " + name + ", 부서: " + department + ", 기본급: " + baseSalary + "원";
    }
}

// Manager.java (자식)
public class Manager extends Employee {
    private int bonus;                       // 자식만 갖는 추가 속성

    public Manager(String name, int baseSalary, String department, int bonus) {
        super(name, baseSalary, department); // 부모 생성자 호출, 첫 줄
        this.bonus = bonus;
    }
    @Override
    public int calculateSalary() {
        return super.calculateSalary() + bonus;   // ★ 부모 로직 + 추가분
    }
    @Override
    public String toString() {
        return super.toString() + ", 보너스: " + bonus + "원";
    }
    public void approve(String document) {        // 자식만의 메서드
        System.out.println(getName() + " 팀장이 [" + document + "] 을(를) 결재했습니다.");
    }
}
```

**해설 — 이 두 파일에 상속의 핵심이 다 있다**

1. **`super.calculateSalary() + bonus`** — 오버라이딩은 "덮어쓰기"가 아니라 **"부모 것을 쓰면서 확장"**할 수 있다. `toString()`도 같은 패턴.
2. **`printInfo()`가 부모에만 있는데 자식에서 다르게 동작한다.** 부모 코드 안의 `calculateSalary()` 호출이 **실행 시점에 실제 객체의 메서드로 연결**되기 때문이다. 이것이 다음 절 **가상 메서드 호출**의 실물이다.
3. **`protected String department`** — private이면 자식이 못 쓰고, public이면 아무나 쓴다. 자료 표의 protected 행이 왜 존재하는지 보여주는 코드.
4. ⚠️ `name`은 `private`이라 `Manager.approve()`에서 `name`이 아니라 **`getName()`**을 쓴다. private은 상속돼도 직접 접근 불가.

`InheritanceDemo.java`로 확인:
```java
Employee e = new Employee("김사원", 3000000, "개발팀");
Manager  m = new Manager("박팀장", 5000000, "개발팀", 1000000);
System.out.println("사원 급여 : " + e.calculateSalary());   // 3000000
System.out.println("팀장 급여 : " + m.calculateSalary());   // 6000000 (기본급+보너스)

Employee ref = new Manager("이팀장", 4800000, "기획팀", 800000);   // 업캐스팅
System.out.println("업캐스팅 후 급여 : " + ref.calculateSalary()); // 자식 메서드 실행!
if (ref instanceof Manager) {
    ((Manager) ref).approve("휴가원");    // 다운캐스팅해야 자식 메서드 호출 가능
}
```

**② 상속의 필요성을 bad/good으로 대비 — `chapter5/oop/bad/` vs `chapter5/oop/good/`**

이 두 패키지가 **이 저장소에서 가장 교육적인 구성**이다.

```java
// ── bad 패키지 : 상속 없음 ──────────────────────────
public class MereClerk {                 // 평사원
    String name;  double salary;
    public String getName()   { return this.name; }
    public double getSalary() { return this.salary; }
    public void manageSalary(double rate) {
        salary = salary + salary*(rate/100);
    }
}
public class Manager {                   // 관리자 — 거의 똑같은 코드가 통째로 중복
    String name;  double salary;
    public String getName()   { return this.name; }
    public double getSalary() { return this.salary; }
    public void manageSalary(double rate) {
        salary = salary + salary*(rate/100);
        salary += 20;                    // 이 한 줄만 다르다
    }
}

// InFlexibleCompanyDemo.java — 호출하는 쪽도 타입마다 따로 써야 한다
MereClerk mereClerk1 = new MereClerk("철수", 100);
MereClerk mereClerk2 = new MereClerk("영희", 100);
Manager   manager    = new Manager("홍길동", 200);
mereClerk1.manageSalary(10);
mereClerk2.manageSalary(10);
manager.manageSalary(10);                // 타입별로 3줄
```

```java
// ── good 패키지 : 추상 클래스로 공통 추출 ────────────
public abstract class Employee {
    private String name;
    protected double salary;             // 자식이 계산에 써야 하므로 protected
    public String getName()   { return this.name; }
    public double getSalary() { return this.salary; }
    public abstract void manageSalary(double rate);   // 구현은 자식에게 위임
}

// FlexibleCompanyDemo.java — 배열 하나로 일괄 처리
Employee[] emps = new Employee[3];
emps[0] = new MereClerk("철수", 100);
emps[1] = new MereClerk("영희", 100);
emps[2] = new Manager("홍길동", 200, "인사부");
for(Employee emp: emps) {
    if(emp instanceof Manager) {
        System.out.print("부서명 = " + ((Manager)emp).getDeptName() + " 관리자 ");
    }
    System.out.println(emp.getName() + "의 현재 월급은 " + emp.getSalary() + " 만원 입니다.");
}
```

**해설 — bad → good 이 해결한 것**

| | bad | good |
|---|---|---|
| 중복 코드 | name/salary/getter가 클래스마다 복사 | `Employee`에 한 번 |
| 새 직군 추가 | 클래스 통째로 복사 | `Employee` 상속 한 줄 |
| 일괄 처리 | 타입별로 변수·반복 따로 | `Employee[]` 하나로 순회 |
| 급여 인상 규칙 변경 | 모든 클래스 수정 | 해당 자식만 수정 |

⚠️ 여기서 `Employee`를 `abstract`로 만든 이유: **"직원"이라는 실체는 존재하지 않는다.** 평사원이거나 관리자일 뿐. `new Employee()`를 막아야 한다. 🔗 1-12절.

**③ 오버라이딩 시 super 사용 — `mylab/bank/entity/CheckingAccount.java`**

```java
@Override
public void withdraw(double amount) throws InsufficientBalanceException {
    if (amount > withdrawalLimit) {
        throw new WithdrawalLimitExceededException("출금 한도를 초과했습니다. 한도: " + withdrawalLimit + "원");
    }
    super.withdraw(amount);      // ★ 자기 검증만 추가하고 나머지는 부모에게
}
```

**해설 — 자료의 "오버라이딩 시 throws 규칙"이 실제로 적용된 지점**
- 부모 `Account.withdraw()`는 `throws InsufficientBalanceException`.
- 자식은 **`WithdrawalLimitExceededException`을 던지는데**, 이 클래스는 `InsufficientBalanceException`을 상속한다.
  ```java
  public class WithdrawalLimitExceededException extends InsufficientBalanceException
  ```
- 즉 **"부모와 같거나 더 구체적인 예외"** 규칙을 지켰기 때문에 `throws` 선언은 그대로 `InsufficientBalanceException`이어도 된다.
- ⚠️ 만약 `WithdrawalLimitExceededException`이 `Exception`을 직접 상속했다면 컴파일 에러가 났을 것이다. 자료의 `TestB2` 반례가 바로 이것.
- 호출부에서도 이 계층 덕을 본다.
  ```java
  catch (AccountNotFoundException | InsufficientBalanceException e)   // 멀티 catch
  ```
  `WithdrawalLimitExceededException`을 따로 잡지 않아도 부모 타입으로 잡힌다.


---

## 1-11. 다형성 — 업/다운캐스팅 · instanceof · 가상 메서드 호출

### 개념

**다형성**: 하나의 참조 변수가 여러 형태의 객체를 참조할 수 있는 것.
```java
Employee e = new Manager();   // OK  (부모 참조 ← 자식 객체)
Manager m = new Employee();   // 오류 (자식 참조 ← 부모 객체)
```

**가상 메서드 호출(Virtual Method Invocation)**
- **컴파일 시점**: 참조 변수의 타입으로 "이 메서드가 있는가"만 검사
- **실행 시점**: 실제 생성된 객체의 메서드가 실행

**다형성을 쓰는 3가지 이유**
1. 동질적 컬렉션 — `MyDate[] dates`
2. **이질적 컬렉션** — `Employee[] staff`에 Manager와 Employee를 섞어 담기
3. **다형적 인자** — `getTax(Employee e)` 하나로 모든 자식 타입 처리

**캐스팅**
- **업캐스팅**: 자식 → 부모. 캐스팅 연산자 **불필요**. 안전.
- **다운캐스팅**: 부모 → 자식. 컴파일은 통과하지만 **런타임에 `ClassCastException` 가능**.
- **안전한 다운캐스팅**: `instanceof`로 먼저 확인.

**instanceof**: `A instanceof B` → A가 B이거나 B의 자식이면 true. 부모/자식 관계가 아예 없으면 **컴파일 에러**.

### 코드 위치

**① 종합 실습 — `basic/step07_polymorphism/PolymorphismDemo.java`**

```java
// 1. 같은 객체를 세 가지 타입으로 받기
Circle   c1 = new Circle(5);    // 자기 타입      : 모든 메서드 사용 가능
Shape    c2 = new Circle(3);    // 부모 타입      : Shape 의 메서드만
Drawable c3 = new Circle(1);    // 인터페이스 타입 : Drawable 의 메서드만

c1.draw();        // OK
c1.printArea();   // OK
c2.printArea();   // Shape 의 메서드 → OK
// c2.draw();     // ✗ 컴파일 에러 — Shape 에 draw() 가 없다
c3.draw();        // Drawable 의 메서드 → OK
c3.printColor();  // default 메서드

// 2. 이질적 컬렉션 + 가상 메서드 호출
Shape[] shapes = { new Circle(2), new Rectangle(3, 4), new Circle(1.5) };
double total = 0;
for (Shape shape : shapes) {
    shape.printArea();          // ★ 각 객체의 getArea() 가 실행된다
    total += shape.getArea();
}

// 3. 안전한 다운캐스팅
for (Shape shape : shapes) {
    if (shape instanceof Circle) {
        Circle circle = (Circle) shape;
        System.out.println("원 - 반지름 : " + circle.getRadius());
    } else if (shape instanceof Rectangle) {
        Rectangle rect = (Rectangle) shape;
        System.out.println("사각형 - 가로 : " + rect.getWidth());
    }
}

// 4. 잘못된 다운캐스팅 → 런타임 예외
try {
    Shape shape = new Circle(1);
    Rectangle wrong = (Rectangle) shape;   // 컴파일은 통과, 실행 시 폭발
} catch (ClassCastException e) {
    System.out.println("예외 발생 : 잘못된 형변환입니다.");
}

// 5. 다형적 인자
describe(new Circle(10));
describe(new Rectangle(1, 2));

public static void describe(Shape shape) {          // Shape 하나로 모든 자식 처리
    System.out.printf("[%s] 넓이 %.2f%n", shape.getName(), shape.getArea());
}
```

**해설 — 이 파일 하나로 자료 7.2절 전체가 커버된다**
- **1번**: 같은 `Circle` 객체인데 **참조 타입에 따라 쓸 수 있는 메서드가 달라진다.** 컴파일 시점 검사가 참조 타입 기준이라는 증거.
- **2번**: `shape.printArea()`가 Circle이면 `PI*r*r`, Rectangle이면 `w*h`로 계산된다. **코드는 한 줄인데 동작이 객체마다 다르다** = 가상 메서드 호출.
- **4번**: 컴파일러는 `Shape → Rectangle` 캐스팅을 "가능성이 있다"고 판단해 통과시킨다. 실제 객체가 Circle이라 런타임에 터진다. **그래서 3번처럼 instanceof를 먼저 쓰는 것.**

관련 클래스:
```java
// Shape.java
public abstract class Shape {
    protected String name;
    public abstract double getArea();            // 자식이 반드시 구현
    public void printArea() {                    // 공통 로직
        System.out.printf("%s 의 넓이 : %.2f%n", name, getArea());
    }
}
// Circle.java — 상속과 구현을 동시에
public class Circle extends Shape implements Drawable {
    @Override public double getArea() { return PI * radius * radius; }
    @Override public void   draw()    { ... }
}
```

**② 다형성으로 조건 분기를 처리 — `mylab/book/control/ManageBook.java`**

```java
Publication[] publications = {
    new Magazine("마이크로소프트", "2007-10-01", 328, 9900, "매월"),
    new Novel("빠삐용", "2007-07-01", 396, 9800, "베르나르베르베르", "현대소설"),
    new ReferenceBook("실용주의프로그래머", "2007-01-14", 496, 25000, "소프트웨어공학"),
    ...
};

// 하나의 배열, 하나의 반복문으로 전부 출력
for (int i = 0; i < publications.length; i++) {
    System.out.println((i + 1) + ". " + publications[i]);   // toString() 이 각자 다르게
}

// 타입별로 다른 할인율 적용
public static void modifyPrice(Publication publication) {
    int currentPrice = publication.getPrice();
    if (publication instanceof Magazine) {
        publication.setPrice((int) (currentPrice * 0.6));       // 40% 할인
    } else if (publication instanceof Novel) {
        publication.setPrice((int) (currentPrice * 0.8));       // 20% 할인
    } else if (publication instanceof ReferenceBook) {
        publication.setPrice((int) (currentPrice * 0.9));       // 10% 할인
    }
}
```

**해설 — 자료가 말한 "다형적 인자 + instanceof" 패턴 그대로**
- 매개변수가 `Publication` 하나 → 세 자식 타입 모두 받는다.
- 타입별 규칙이 다르므로 `instanceof`로 분기.
- ⚠️ **하지만 이건 사실 개선의 여지가 있는 코드다.** 할인율이 각 클래스의 책임이라면 `Publication`에 `abstract double getDiscountRate()`를 두고 자식이 구현하는 편이 낫다. 그러면 `modifyPrice`는 `publication.setPrice((int)(currentPrice * publication.getDiscountRate()))` 한 줄이 되고, 새 출판물 타입이 늘어나도 이 메서드는 안 고쳐도 된다.
- 이 개선 방향이 바로 `chapter5/oop/good`의 `abstract manageSalary()`가 택한 길이다. **두 코드를 비교해보면 "언제 instanceof를 쓰고 언제 추상 메서드를 쓰는가"의 감이 잡힌다.**

**③ 같은 패턴의 원형 — `workshop/book/control/ManageBook.java`**

```java
Magazine pub1 = new Magazine();
pub1.getPublishingPeriod();          // 자기 타입이라 바로 호출 가능

Publication pub = new Magazine();    // 업캐스팅
pub.getPage();                       // OK — Publication 의 메서드
((Magazine)pub).getPublishingPeriod();  // ★ 다운캐스팅해야 자식 메서드 호출

public static void printSubInfo(Publication pub) {
    if (pub instanceof Magazine) {
        Magazine m = (Magazine)pub;
        System.out.println(m.getPublishingPeriod());
    } else if(pub instanceof Novel) {
        Novel n = (Novel)pub;
        System.out.println(n.getAuthor() + " " + n.getGenre());
    } else if(pub instanceof ReferenceBook) {
        ReferenceBook r = (ReferenceBook)pub;
        System.out.println(r.getField());
    }
}
```

**해설**: `workshop/book`이 원형, `mylab/book`이 확장판이다.
- `workshop`은 `Publication`에 `toString()`이 `return this.title;`뿐이라 출력이 단순하다.
- `mylab`은 각 자식이 `toString()`을 오버라이딩해서 `super.toString() + " [잡지] 발행주기:..."` 형태로 만든다. **다운캐스팅 + instanceof 없이도 타입별 정보가 출력**된다.
- 즉 **`mylab` 버전은 `printSubInfo` 같은 메서드가 아예 필요 없어졌다.** 오버라이딩으로 instanceof를 제거한 좋은 사례.

**④ 통계 계산에서의 instanceof — `mylab/book/control/StatisticsAnalyzer.java`**

```java
private String getPublicationType(Publication pub) {
    if (pub instanceof Novel)              return "소설";
    else if (pub instanceof Magazine)      return "잡지";
    else if (pub instanceof ReferenceBook) return "참고서";
    else                                   return "기타";
}

public Map<String, Double> calculateAveragePriceByType(Publication[] publications) {
    Map<String, Double>  totalByType = new HashMap<>();
    Map<String, Integer> countByType = new HashMap<>();
    for (Publication pub : publications) {
        String type = getPublicationType(pub);
        totalByType.put(type, totalByType.getOrDefault(type, 0.0) + pub.getPrice());
        countByType.put(type, countByType.getOrDefault(type, 0) + 1);
    }
    Map<String, Double> averageByType = new HashMap<>();
    for (String type : totalByType.keySet()) {
        averageByType.put(type, totalByType.get(type) / countByType.get(type));
    }
    return averageByType;
}
```

**해설**
- **`instanceof`를 한 곳(`getPublicationType`)으로 모았다.** 타입 판정 로직이 흩어지지 않게 하는 좋은 습관.
- 합계용 Map과 개수용 Map **두 개를 동시에 굴려서 평균**을 낸다. 🔗 1-15절 `getOrDefault` 패턴.
- `else return "기타"` — 새 타입이 추가돼도 터지지 않게 하는 방어.

**⑤ 다형성 + 예외를 함께 — `mylab/bank/`**

```java
// Account.java (추상 부모)
public abstract class Account {
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) throw new InsufficientBalanceException(...);
        balance -= amount;
    }
}
// SavingsAccount — 이자 기능 추가
public void applyInterest() {
    double interest = getBalance() * interestRate / 100;
    deposit(interest);
}
// CheckingAccount — withdraw 를 오버라이딩해 한도 검사 추가

// Bank.java — List<Account> 로 두 종류를 함께 관리
private List<Account> accounts;

public void transfer(String from, String to, double amount)
        throws AccountNotFoundException, InsufficientBalanceException {
    Account f = findAccount(from);
    Account t = findAccount(to);
    f.withdraw(amount);      // ★ 실제 타입에 따라 한도 검사 유무가 갈린다
    t.deposit(amount);
}
```

**해설 — 이 프로젝트가 저장소에서 완성도가 가장 높은 이유**
- `transfer`는 계좌가 저축인지 체킹인지 **모른다.** `f.withdraw()`가 알아서 맞는 로직을 탄다.
- 체킹 계좌에서 한도 초과로 출금하면 `WithdrawalLimitExceededException`이, 저축 계좌에서 잔액 초과면 `InsufficientBalanceException`이 나온다. **호출부는 부모 예외 하나만 잡으면 된다.**
- 이 구조가 다형성(1-11) + 상속(1-10) + 예외 계층(1-14)이 맞물린 결과다.

---

## 1-12. 추상 클래스와 인터페이스

### 개념

**추상 클래스**
- `abstract` 키워드로 선언한 **미완성 클래스**
- 추상 메서드가 있으면 **반드시** abstract 클래스여야 한다 (역은 성립 안 함 — 추상 메서드 없어도 abstract 가능)
- **객체 생성 불가**, 하지만 **참조 변수로는 사용 가능** (`Appliance app = new Radio();`)
- 일반 메서드, 멤버 변수도 가질 수 있다
- 추상 메서드는 본문 없이 `;`로 끝남

**인터페이스**
- 모든 메서드가 abstract인 클래스
- 메서드는 묵시적으로 `public abstract`, 멤버 변수는 묵시적으로 `public static final`
- `implements`로 구현, **다중 구현 가능** → 단일 상속 제약을 보완
- 객체 생성 불가, 참조 변수로는 사용 가능

**언제 뭘 쓰나**

| | 추상 클래스 | 인터페이스 |
|---|---|---|
| 관계 | "~이다"(is-a). 공통 **속성 + 구현**을 물려줌 | "~할 수 있다"(can-do). 기능 규약만 |
| 상속 | 하나만 | 여러 개 |
| 필드 | 가능 | `public static final` 상수만 |

### 코드 위치

**① 최소 예제 — `chapter5/oop/myabstract/`**

```java
public abstract class EmptyCan {
    public abstract void printContent();
    public abstract void printName();
    public void sayHello() { }         // ★ 일반 메서드도 가질 수 있다
}
public class BeerCan extends EmptyCan {
    @Override public void printContent() { System.out.println("흑맥주 입니다."); }
    @Override public void printName()    { System.out.println("OB 맥주 입니다."); }
}
// TestAbstract.java
EmptyCan can = new BeerCan();   // 참조 변수로는 사용 가능
// EmptyCan can = new EmptyCan();  ✗ 객체 생성 불가
```

**② 최소 예제 — `chapter5/oop/myinteface/`**

```java
public interface IRemoteControl {
    static final int ON = 1;       // ← 사실 public static final 이 묵시적으로 붙는다
    void powerOn();                // ← 사실 public abstract 이 묵시적으로 붙는다
    public abstract void powerOff();  // 명시해도 되지만 중복
}
public class LGRemoteTv implements IRemoteControl{
    @Override  //오버라이딩 규칙을 지켰는지를 체크하는 어노테이션(annotation)
    public void powerOn()  { System.out.println("TV 전원 On!!"); }
    @Override
    public void powerOff() { System.out.println("TV 전원 Off!!"); }
    public String getBrandName() { return "LG TV"; }   // 인터페이스에 없는 자기 메서드
}
```

`TestInterface.java`:
```java
System.out.println(IRemoteControl.ON);      // 상수는 객체 없이 인터페이스명으로 접근

IRemoteControl rc = new LGRemoteTv();       // 사용 가능 메서드 2개
rc.powerOn();  rc.powerOff();
// rc.getBrandName();  ✗ 인터페이스에 없으므로 불가

LGRemoteTv lgTv = new LGRemoteTv();         // 사용 가능 메서드 3개
System.out.println(lgTv.getBrandName());    // OK
```

**해설**: 주석의 "2개 / 3개"가 핵심이다. **참조 타입이 무엇이냐에 따라 접근 가능한 메서드 수가 달라진다.** 1-11절 `Circle c1 / Shape c2 / Drawable c3`와 완전히 같은 이야기.

**③ 상속 + 구현 동시 — `workshop/animal/`**

```java
public abstract class Animal {
    protected int legs;
    protected Animal(int legs) { this.legs = legs; }   // ★ protected 생성자
    public abstract void eat();
    public void walk() { System.out.println("동물은 " + legs + " 발로 걷는다."); }
}
public interface Pet {
    String getName();
    void setName(String name);
    public abstract void play();
}

public class Cat    extends Animal implements Pet { ... }   // 상속 1 + 구현 1
public class Fish   extends Animal implements Pet { ... }
public class Spider extends Animal { ... }                 // Pet 아님 (애완동물 X)
```

`AnimalTest.java`:
```java
Cat    cat1 = new Cat();   //5개   getName,setName,play,eat,walk
Pet    cat2 = new Cat();   //3개   getName,setName,play
Animal cat3 = new Cat();   //2개   eat,walk

cat1.setName("톰"); cat1.play(); cat1.eat(); cat1.walk();   // 전부 가능
cat2.setName("미요"); cat2.play();                           // Pet 것만
// cat2.eat();  ✗ Pet 에 eat() 이 없다
cat3.eat(); cat3.walk();                                    // Animal 것만
```

**해설 — 이 예제가 추상클래스와 인터페이스의 차이를 가장 잘 보여준다**
- **`Animal`(추상 클래스)** = "무엇인가" → 고양이는 **동물이다**. 다리 개수(`legs`)라는 **상태를 물려준다.**
- **`Pet`(인터페이스)** = "무엇을 할 수 있는가" → 고양이는 **애완동물 역할을 할 수 있다.** 상태 없이 기능 규약만.
- **`Spider`는 `Animal`이지만 `Pet`이 아니다.** 거미는 먹고 걷지만 이름을 갖고 놀아주지는 않는다. → **인터페이스는 필요한 클래스만 골라서 구현**할 수 있다는 것.
- ⚠️ `Fish`는 `super(0)` — 물고기 다리 0개. 추상 클래스 생성자에 값을 넘기는 방식.
- `Animal`의 생성자가 `protected`인 이유: 어차피 `new Animal()`이 불가능하니 public일 이유가 없고, 자식만 `super(...)`로 부르면 되기 때문.

**④ 인터페이스의 default 메서드 (Java 8+) — `basic/step07_polymorphism/Drawable.java`**

```java
public interface Drawable {
    String DEFAULT_COLOR = "검정";       // public static final 묵시적
    void draw();                         // public abstract 묵시적
    default void printColor() {          // ★ 구현을 가진 메서드
        System.out.println("기본 색상 : " + DEFAULT_COLOR);
    }
}
```

구현 클래스에서 선택적으로 재정의:
```java
// Circle — printColor() 를 재정의하지 않음 → 기본 구현 사용 ("기본 색상 : 검정")
// Rectangle — 재정의함
@Override
public void printColor() { System.out.println("사각형 색상 : 파랑"); }
```

**해설**: 자료 1-1절에서 언급한 **"J2SE 8의 default method"**가 실제로 쓰인 유일한 곳이다.
- 원래 인터페이스에 메서드를 추가하면 **모든 구현 클래스가 깨진다.** `default`는 기본 구현을 제공해 이 문제를 푼다.
- 이제 인터페이스가 "모든 메서드가 abstract"라는 정의는 Java 8 이후로는 **정확하지 않다.** `default`, `static` 메서드도 가질 수 있다.
- 🔗 PART 2의 Spring `UserDao` 인터페이스, PART 3의 `JpaRepository`가 이 개념 위에 서 있다.

**⑤ 템플릿 형태 — `chapter5/oop/printer/`**

```java
public abstract class Printer {
    private String id;
    public Printer(String id) { this.id = id; }
    public String getId() { return id; }
    public abstract void print(String msg);      // 출력 방식은 자식이 결정
}
// DotPrinter / InkjetPrinter / LaserPrinter 가 각각 print() 구현
```

`TestPrinter.java`:
```java
Printer[] printers = new Printer[3];
printers[0] = new DotPrinter("101");
printers[1] = new InkjetPrinter("102",100,0.5);
printers[2] = new LaserPrinter("103",100,0.2);
String[] msg = {"Hello","Java","Choco"};
// 배열 순회로 서로 다른 프린터에 각각 출력
```

**해설**: `Shape`/`Employee`/`Animal`/`Printer` — **같은 추상 클래스 패턴이 저장소에 4번 반복**된다. 반복 자체가 학습 의도다. 하나만 확실히 이해하면 나머지는 같은 구조.


---

## 1-13. Object 클래스 · String · Wrapper

### 개념

**Object** — 모든 클래스의 부모. `extends`가 없으면 묵시적으로 `extends Object`.

| 메서드 | 기본 동작 | 왜 오버라이딩하나 |
|---|---|---|
| `equals()` | `==`로 **주소 비교** | 내용(값)을 비교하려면 |
| `toString()` | `클래스명@해시코드` | 사람이 읽을 수 있게. **문자열 결합 시 자동 호출** |

**String**
- **불변(immutable)**. 변경 메서드는 새 객체를 만든다.
- `"HELLO"` 리터럴 → **String literal pool** (같은 리터럴은 같은 객체)
- `new String("HELLO")` → **힙**. 항상 새 객체.

```java
String str1 = "HELLO";  String str2 = "HELLO";
String str3 = new String("HELLO");  String str4 = new String("HELLO");
str1 == str2   // true   같은 풀 객체
str2 == str3   // false  풀 vs 힙
str3 == str4   // false  각각 다른 객체
str1.equals(str4)  // true  내용 비교
```

⚠️ **문자열 비교는 반드시 `equals()`.** null 체크 습관: `if (str1 != null && str1.equals(str2))`

**StringBuffer** — 가변(mutable). 버퍼를 두어 문자열 연산이 빠르다. ⚠️ `equals()`를 오버라이딩하지 **않았으므로** 내용 비교하려면 `sb1.toString().equals(sb2.toString())`.

**Wrapper 클래스** — primitive를 객체화. `int↔Integer`, `char↔Character` 등.
- Boxing: `Integer wInt = pInt;` (Java 5.0부터 자동)
- Unboxing: `int p2 = wInt;`
- 문자열 → 숫자: `Integer.parseInt("2")` 또는 `Integer.valueOf("2").intValue()`

### 코드 위치

**① `chapter1/first/EqualsTest.java`** — 자료의 equals 절 전체를 실험

```java
String str1 = "abc";
String str2 = new String("abc");
if( str1.equals( str2 )) { ... }              // true — 내용이 같다
if( str1.equalsIgnoreCase( str2 )) { ... }    // true — 대소문자 무시

Set<String> strSet = new HashSet<>();
strSet.add(str1);      // "abc"
strSet.add(str2);      // "abc" — ★ 중복으로 판단되어 저장 안 됨
strSet.add("bfs");
// 출력 : abc, bfs (2개)

MyDate date1 = new MyDate( 6, 9, 2006 );
MyDate date2 = new MyDate( 6, 9, 2006 );      // 내용은 같지만 다른 객체
MyDate date3 = date2;                          // 같은 객체를 참조
System.out.println("date1 hashCode " + date1.hashCode());
```

**해설 — 이 파일의 핵심 대비**
- `str1`과 `str2`는 **다른 객체인데** HashSet에 넣으면 **하나만 저장**된다. `String`이 `equals()`와 `hashCode()`를 **오버라이딩해 뒀기 때문**.
- 반면 `MyDate`는 오버라이딩을 하지 않았으므로 `date1`과 `date2`는 **내용이 같아도 다른 객체로 취급**된다. hashCode도 다르게 나온다.
- 🔗 자료 10.6절의 **"HashSet은 hashCode()와 equals()를 재정의해야 중복 저장을 방지"**가 이 코드로 증명된다. `MyDate`를 HashSet에 넣으면 내용이 같아도 둘 다 저장된다.

**② `basic/step01_syntax/StringDemo.java`** — String 실전 메서드 모음

```java
String title = "실용주의프로그래머";
title.length();              // 길이
title.charAt(0);             // 첫 글자
title.substring(0, 4);       // 0 이상 4 미만
title.contains("주의");
title.indexOf("프로그래머");

// 날짜 문자열에서 연도만 (StatisticsAnalyzer 와 같은 방식)
String year = "2007-01-14".substring(0, 4);

// 주민번호 7번째 자리 (PersonEntity 와 같은 방식)
char genderNum = "7212121028102".charAt(6);

// ★가장 중요★ 문자열 비교
String s1 = "김하늘";  String s2 = "김하늘";  String s3 = new String("김하늘");
s1 == s2         // true  (같은 상수풀 객체)
s1 == s3         // false (새로 만든 객체)
s1.equals(s3)    // true  ← 항상 이렇게 비교
"ABC".equalsIgnoreCase("abc")   // true

// 반면 char, int 같은 기본형은 == 로 비교한다
char g = '여';
g == '여'        // true

// 분리와 결합
String[] tokens = "이순호,032-392-2932,인천 남동구".split(",");
for (String token : tokens) System.out.println(token.trim());

// 서식 출력 : %s 문자열, %d 정수, %f 실수, %,d 천단위 콤마, %n 줄바꿈
System.out.printf("%s - %,d원%n", title, 25000);
System.out.printf("평균 : %.2f%%%n", 33.333);   // %% 는 % 문자 자체

// 여러 번 이어붙일 때는 StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 1; i <= 3; i++) sb.append("항목").append(i).append(" ");
System.out.println(sb.toString().trim());
```

**해설**
- **기본형은 `==`, 참조형은 `equals()`.** 이 구분이 `PersonManager.findByGender`에서 실제로 갈린다.
  ```java
  if(person.getGender() == gender)          // char → ==
  if(person.getName().equals(name))         // String → equals()
  ```
  같은 파일 안에서 두 비교 방식이 나란히 나온다. **왜 다른지 설명할 수 있어야 한다.**
- `StringBuilder`는 자료의 `StringBuffer`와 거의 같다. 차이는 **동기화 여부**: `StringBuffer`는 thread-safe, `StringBuilder`는 아니지만 더 빠르다. 단일 스레드면 `StringBuilder`.
- 🔗 자료 12.4절 JDBC에서 SQL 쿼리를 `StringBuffer.append()`로 조립하는 것과 같은 이유(문자열 결합 시 새 객체가 계속 생기는 것을 막기 위해).

**③ toString() 오버라이딩의 3가지 스타일**

```java
// ① 기본형 — basic/step04_class/Product.java
@Override
public String toString() {
    return "Product [name=" + name + ", price=" + price + ", stock=" + stock + "]";
}

// ② String.format 사용 — basic/step10_layer/entity/Member.java
@Override
public String toString() {
    return String.format("[%s] %s (%d세) - %s", id, name, age, active ? "활동" : "휴면");
}

// ③ super.toString() 확장 — mylab/book/entity/Novel.java
@Override
public String toString() {
    return super.toString() + " [소설] 저자:" + author + ", 장르:" + genre
            + ", " + getPage() + "쪽, " + getPrice() + "원, 출판일:" + getPublishDate();
}
```

**해설**
- ①은 IDE 자동 생성 형식. 디버깅용으로 무난.
- ②는 서식이 필요할 때. 정렬된 출력에 유리.
- ③은 **상속 계층에서 각 단계가 자기 정보만 추가**하는 방식. 부모가 `title`, 자식이 `[소설] 저자...`를 붙인다. 🔗 1-10절 `super.calculateSalary() + bonus`와 같은 아이디어.
- `System.out.println(객체)`가 `toString()`을 자동 호출하기 때문에, `mylab/book/ManageBook`은 `println(publications[i])` 한 줄로 타입별 상세 정보를 전부 출력한다.

**④ Wrapper와 오토박싱 — `basic/step09_collection/ListDemo.java`**

```java
List<Integer> prices = new ArrayList<>();
prices.add(9900);        // int 9900 → Integer 로 자동 박싱
prices.add(11000);
int total = 0;
for (int price : prices) {   // Integer → int 자동 언박싱
    total += price;
}
```

`chapter6/collections/TestArrayList.java`:
```java
list.add(new Integer(100));   // 명시적 박싱 (구식)
list.add(200);                // 자동 박싱 (Java 5.0+)
```

**해설**: **컬렉션에는 primitive를 담을 수 없다.** `List<int>`는 불가능하고 `List<Integer>`만 가능하다. 오토박싱 덕분에 코드에서는 티가 안 날 뿐. ⚠️ `new Integer(100)`은 Java 9부터 deprecated — `Integer.valueOf(100)`을 쓴다.

`chapter1/first/MyCalcurator.java`:
```java
int num1 = Integer.parseInt(args[0]);   // String → int
```
자료의 `Integer.parseInt("2")` 그대로. 명령줄 인수는 항상 String이므로 숫자 계산 전에 반드시 변환이 필요하다.

---

## 1-14. 예외 처리

### 개념

**Error vs Exception**
- **Error**: 메모리 부족 등 **처리 불가능한** 치명적 상황
- **Exception**: 미리 예상하고 처리할 수 있는 가벼운 오류

**Exception 계층**
```
Throwable
├─ Error                  (처리 불가)
└─ Exception
   ├─ RuntimeException    ← Unchecked. 컴파일 시 체크 안 함
   │  ArithmeticException, NullPointerException,
   │  IndexOutOfBoundsException, ClassCastException,
   │  NegativeArraySizeException, NumberFormatException
   └─ 그 외               ← Checked. 컴파일 시 반드시 처리 필요
      IOException, FileNotFoundException, EOFException, SQLException
```

⚠️ **RuntimeException은 대개 프로그래머의 버그**다. 예외 처리보다 **코드 수정**이 정답.
- `ArithmeticException` → if로 0 검사
- `NullPointerException` → null 체크
- `ArrayIndexOutOfBoundsException` → `length`로 범위 확인
- `ClassCastException` → `instanceof`로 먼저 확인

**처리 방법 2가지**

**① try-catch-finally (직접 처리)**
- ⚠️ **catch 순서**: 자식 예외를 먼저, 부모 예외를 나중에. 반대로 쓰면 **컴파일 에러**.
- `finally`: 예외 발생 여부와 무관하게 실행. **중간에 `return`을 만나도 실행된다.** 단 `System.exit()`는 예외.

**② throws (호출한 쪽에 위임)**
- Call Stack Mechanism. 계속 위임하면 `main`까지 올라가고, 거기서도 안 잡으면 비정상 종료.

**사용자 정의 예외**: `Exception`을 상속. `throw new MyException("메시지")`.

### 코드 위치

**① 종합 — `basic/step08_exception/ExceptionDemo.java`**

```java
// 1. 기본 try-catch
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("예외 발생 : " + e.getMessage());   // "/ by zero"
}

// 2. 다중 catch + finally — ★ 순서가 중요
String[] data = { "10", "abc" };
for (int i = 0; i < 3; i++) {          // i=2 는 배열 범위 밖
    try {
        System.out.println("변환 결과 : " + Integer.parseInt(data[i]));
    } catch (NumberFormatException e) {              // 자식
        System.out.println("숫자 형식이 아닙니다 : " + e.getMessage());
    } catch (ArrayIndexOutOfBoundsException e) {     // 자식
        System.out.println("배열 범위를 벗어났습니다.");
    } catch (Exception e) {                          // ★ 부모는 반드시 마지막
        System.out.println("그 밖의 예외 : " + e);
    } finally {
        System.out.println("  → " + i + "번째 처리 종료");   // 항상 실행
    }
}

// 3. 의도적으로 예외 던지기 (RuntimeException 계열 — throws 불필요)
public static void deposit(int amount) {
    if (amount <= 0) throw new IllegalArgumentException("입금액은 양수여야 합니다.");
}

// 4. 사용자 정의 예외 (Checked — throws 필수)
public static void setAge(int age) throws InvalidAgeException {
    if (age < 0 || age > 150) {
        throw new InvalidAgeException("나이는 0~150 사이여야 합니다. 입력값 : " + age);
    }
}

// 5. throws 로 위임 체인
public static void process() throws InvalidAgeException {
    setAge(200);          // 여기서 처리하지 않고 호출한 쪽으로 넘김
}
// main 에서 최종 처리
try { process(); } catch (InvalidAgeException e) { ... }
```

```java
// InvalidAgeException.java
public class InvalidAgeException extends Exception {   // Checked Exception
    private static final long serialVersionUID = 1L;
    public InvalidAgeException(String message) {
        super(message);        // 메시지를 부모에게 넘기면 getMessage() 로 꺼낼 수 있다
    }
}
```

**해설 — 이 한 파일이 자료 9장 전체를 커버한다**

1. **`data[i]`의 반복이 절묘하다.** `i=0`은 정상, `i=1`은 `NumberFormatException`, `i=2`는 `ArrayIndexOutOfBoundsException`. **한 번의 반복으로 세 가지 경로를 다 보여준다.**
2. **`catch (Exception e)`가 마지막**인 이유: 앞에 두면 모든 예외를 다 잡아버려 뒤의 catch는 **도달 불가능 코드**가 되고 컴파일 에러가 난다.
3. **`finally`가 매번 출력된다** — 예외가 났든 안 났든.
4. **`IllegalArgumentException`(3번)과 `InvalidAgeException`(4번)의 차이가 핵심이다.**
   - `IllegalArgumentException`은 `RuntimeException`의 자식 → **Unchecked** → `throws` 선언 불필요, 호출부도 try-catch 강제 안 됨
   - `InvalidAgeException`은 `Exception` 직속 → **Checked** → `throws` 필수, 호출부는 반드시 처리
   - ⚠️ 이 선택이 API 설계의 큰 결정이다. **"호출자가 반드시 대응해야 하는가"**가 기준.
5. `serialVersionUID` — 예외 클래스는 `Serializable`이므로 붙이는 것이 관례(경고 제거용).

**② 예외에 데이터를 담기 — `workshop/account/exception/InsufficientBalanceException.java`**

```java
public class InsufficientBalanceException extends Exception {
    private int currentBalance;          // ★ 예외가 추가 정보를 들고 다닌다

    public InsufficientBalanceException(String errorMessage, int currentBalance) {
        super(errorMessage);
        this.currentBalance = currentBalance;
    }
    public int getCurrentBalance() { return currentBalance; }
}
```

던지는 쪽:
```java
public void withdraw(int amount) throws InsufficientBalanceException {
    if( amount > balance ) {
        throw new InsufficientBalanceException("잔액이 부족합니다. 현재 잔액은 = ", this.balance);
    }
    this.balance -= amount;
}
```
받는 쪽:
```java
catch(InsufficientBalanceException exp) {
    System.out.println("Error 메시지 = " + exp.getMessage() + exp.getCurrentBalance());
}
```

**해설 — 이것이 사용자 정의 예외를 만드는 진짜 이유다**
- 메시지만 필요하면 `IllegalArgumentException`으로 충분하다.
- **호출부에 "잔액이 얼마인지"라는 구조화된 정보를 전달**해야 하니까 별도 클래스를 만든 것.
- 🔗 PART 3의 `BusinessException` + `ErrorCode`가 이 아이디어를 확장한 형태다.

**③ 예외 계층 설계 — `mylab/bank/exception/`**

```java
public class InsufficientBalanceException extends Exception { ... }
public class WithdrawalLimitExceededException extends InsufficientBalanceException { ... }
public class AccountNotFoundException extends Exception { ... }
```

**해설**
- `WithdrawalLimitExceededException`이 `InsufficientBalanceException`을 상속한 덕에 **호출부가 부모 하나만 잡으면 둘 다 처리된다.**
- 그러면서도 필요하면 `catch (WithdrawalLimitExceededException e)`로 세분화할 수도 있다.
- **예외도 상속 계층으로 설계한다**는 것이 이 패키지의 교훈.

`BankDemo.java`의 멀티 catch:
```java
try {
    bank.deposit("AC1000", 5000);
    bank.withdraw("AC1001", 3000);
    hong.applyInterest();
    bank.transfer("AC1002", "AC1001", 5000);
} catch (AccountNotFoundException | InsufficientBalanceException e) {   // Java 7+
    System.out.println("예외 발생: " + e.getMessage());
}
```

⚠️ **멀티 catch(`|`)는 자료에 없는 Java 7 문법**이다. 두 예외에 대한 처리가 동일할 때 쓴다. **단, 두 예외가 상속 관계면 쓸 수 없다**(부모 하나로 충분하므로 컴파일 에러). 그래서 여기서도 `AccountNotFoundException`과 `InsufficientBalanceException`처럼 형제 관계인 것들만 묶었다.

**④ Checked 예외를 setter에서 — `mylab/student/entity/Student.java`**

```java
public void setGrade(int grade) throws InvalidGradeException {
    if (grade < 1 || grade > 4) {
        throw new InvalidGradeException("학년은 1~4 사이의 값이어야 합니다.");
    }
    this.grade = grade;
}
```

**해설 — 1-3절 캡슐화의 발전형**
- `Product.setPrice()`는 잘못된 값에 **`println` + 기본값 대입**으로 대응했다.
- `Student.setGrade()`는 **예외를 던진다.**
- ⚠️ **후자가 옳다.** 전자는 호출자가 "설정에 실패했다"는 사실을 알 방법이 없다. 조용한 실패(silent failure)는 디버깅을 어렵게 만든다.
- **같은 저장소 안에 두 방식이 다 있으니 비교해서 보고, 실무에서는 예외 방식을 쓸 것.**

**⑤ 조회 실패를 예외로 vs null로 — 두 가지 대비**

```java
// ① 예외 — basic/step10_layer/control/MemberManager.java
public Member findById(String id) throws MemberNotFoundException {
    for (Member member : members) {
        if (member.getId().equals(id)) return member;
    }
    throw new MemberNotFoundException(id + " 회원을 찾을 수 없습니다.");
}

// ② null — mylab/library/entity/Library.java
public Book findBookByTitle(String title) {
    for (Book book : books) {
        if (book.getTitle().equals(title)) return book;
    }
    return null;          // ⚠️ 호출부가 null 체크를 잊으면 NPE
}
```

**해설 — 이 대비가 실무에서 가장 자주 논쟁되는 지점**

| | 예외 반환 | null 반환 |
|---|---|---|
| 장점 | 실패를 **강제로 인지**시킴, 이유를 메시지로 전달 | 가볍다, 흐름이 단순 |
| 단점 | 흐름 제어에 예외를 쓰면 무겁다 | **NPE 위험**, 호출부가 잊기 쉽다 |
| 적합 | "없으면 비정상"인 경우 | "없을 수도 있음"이 정상인 경우 |

`LibraryManagementSystem`에서 실제로 위험이 보인다.
```java
Book byTitle = library.findBookByTitle("자바의 정석");
System.out.println(byTitle);      // null 이면 "null" 이 출력될 뿐 터지진 않지만,
                                  // byTitle.getIsbn() 을 호출하면 NPE
```
🔗 PART 3에서는 이 문제를 `Optional<T>`와 `orElseThrow()`로 푼다. `Optional`은 "값이 없을 수 있음"을 **타입으로 강제 표현**한다.


---

## 1-15. 컬렉션 프레임워크

### 개념

**배열의 한계 → 컬렉션**: 배열은 크기 고정, 컬렉션은 저장할 때마다 자동 확장.

**3대 계열** (전부 `java.util`)

| 계열 | 중복 | 순서 | 구조 |
|---|---|---|---|
| **List** | 허용 | 유지 | 순서대로 저장 |
| **Set** | ❌ 불허 | 유지 안 함 | 수학의 집합. null 하나만 가능 |
| **Map** | 키 불가/값 가능 | 유지 안 함 | key-value 쌍(`Map.Entry`) |

**List 구현체**

| | 특징 |
|---|---|
| `ArrayList` | **조회에 효율적.** 초기 용량 10, 초과 시 자동 확장. 삭제 시 뒤 요소들이 앞으로 당겨짐 |
| `LinkedList` | **삽입/삭제에 효율적.** 인접 참조를 링크로 연결. 앞뒤 링크만 변경 |
| `Vector` | 성능은 나쁘지만 **동기화(thread safe)** |

**Set 구현체**: `HashSet`(해시로 빠름, ⚠️ **hashCode()/equals() 재정의 필요**) / `LinkedHashSet`(순서 유지) / `TreeSet`(정렬 유지)

**Map 구현체**: `HashMap`(빠름, null 키 1개 가능) / `LinkedHashMap`(순서 유지) / `Hashtable`(동기화, **null 키/값 불가**) / `TreeMap` / `Properties`

**순회 도구**

| | 사용 가능 | 메서드 |
|---|---|---|
| `Iterator` | Set, List | `hasNext()`, `next()`, `remove()` |
| `ListIterator` | **List만** | + `hasPrevious()`, `previous()`, `add()`, `set()` |
| `Enumeration` | 구형(Vector, Hashtable) | `hasMoreElements()`, `nextElement()` |

### 코드 위치

**① List 총정리 — `basic/step09_collection/ListDemo.java`**

```java
List<String> names = new ArrayList<>();
names.add("이순호");
names.add("김하늘");
names.add(1, "정민서");        // ★ 1번 위치에 끼워 넣기 (뒤 요소가 밀림)

names.size();                  // 배열은 length, List 는 size()
names.get(0);                  // 배열은 [0], List 는 get(0)
names.contains("김하늘");
names.indexOf("김하늘");
names.isEmpty();

names.set(0, "이순신");         // 교체
names.remove("박영선");         // ★ 값으로 삭제
names.remove(0);                // ★ 인덱스로 삭제

// 순회 3가지
for (String name : names) { ... }                     // 향상된 for
for (int i = 0; i < names.size(); i++) { ... }        // 인덱스 for
Iterator<String> it = names.iterator();               // Iterator
while (it.hasNext()) {
    String name = it.next();
    if (name.startsWith("정")) {
        it.remove();      // ★ 향상된 for 안에서 remove 하면 예외가 발생한다
    }
}

// 정렬
List<String> cities = new ArrayList<>(Arrays.asList("서울", "부산", "인천", "광주"));
Collections.sort(cities);      // 오름차순
Collections.reverse(cities);   // 역순

// 객체를 담고 조건 검색
public static List<Product> findOver(List<Product> products, int price) {
    List<Product> result = new ArrayList<>();
    for (Product p : products) {
        if (p.getPrice() > price) result.add(p);
    }
    return result;
}
```

**해설 — 반드시 짚고 넘어갈 3가지**

1. **⚠️ `remove(0)` vs `remove("박영선")`의 함정**
   `List<Integer>`에서 `list.remove(1)`은 **인덱스 1을 지우고**, `list.remove(Integer.valueOf(1))`은 **값 1을 지운다.** 오버로딩이 primitive int와 Object로 갈리기 때문. 시험에 자주 나온다.

2. **⚠️ 순회 중 삭제는 반드시 `Iterator.remove()`**
   ```java
   for (String name : names) {
       if (...) names.remove(name);   // ✗ ConcurrentModificationException
   }
   ```
   향상된 for는 내부적으로 Iterator를 쓰는데, 컬렉션이 밖에서 바뀌면 예외를 던진다. `it.remove()`는 Iterator가 자기 상태를 함께 갱신하므로 안전하다.

3. **`findOver`가 다형성의 또 다른 얼굴**
   매개변수와 반환 타입이 둘 다 `List<Product>` — 구현체가 `ArrayList`든 `LinkedList`든 상관없다. **인터페이스 타입으로 받는 것**이 원칙. 🔗 `List<String> names = new ArrayList<>();`처럼 선언 타입을 인터페이스로 쓰는 이유가 이것.

**② Map / Set 총정리 — `basic/step09_collection/MapDemo.java`**

```java
Map<String, Integer> stock = new HashMap<>();
stock.put("소설", 5);
stock.put("잡지", 3);
stock.put("소설", 10);        // ★ 같은 키에 다시 put 하면 값이 덮어써진다

stock.get("소설");                    // 10
stock.get("만화");                    // null  ← 없는 키
stock.getOrDefault("만화", 0);        // 0     ← ★ 기본값 사용
stock.containsKey("잡지");
stock.remove("참고서");

// 순회 2가지
for (String key : stock.keySet()) {
    System.out.println(key + " → " + stock.get(key));
}
for (Map.Entry<String, Integer> entry : stock.entrySet()) {   // ★ 더 효율적
    System.out.println(entry.getKey() + " = " + entry.getValue());
}

// ★★ 개수 세기 관용구 — 이 저장소에서 여러 번 반복된다
String[] types = { "소설", "잡지", "소설", "참고서", "소설", "잡지" };
Map<String, Integer> countByType = new HashMap<>();
for (String type : types) {
    countByType.put(type, countByType.getOrDefault(type, 0) + 1);
}
// {소설=3, 잡지=2, 참고서=1}

// 평균 내기 — 합계 Map + 개수 Map 두 개를 함께 굴린다
Map<String, Double>  totalByType = new HashMap<>();
Map<String, Integer> cntByType   = new HashMap<>();
for (int i = 0; i < names.length; i++) {
    totalByType.put(names[i], totalByType.getOrDefault(names[i], 0.0) + prices[i]);
    cntByType.put(names[i], cntByType.getOrDefault(names[i], 0) + 1);
}
Map<String, Double> averageByType = new HashMap<>();
for (String type : totalByType.keySet()) {
    averageByType.put(type, totalByType.get(type) / cntByType.get(type));
}

// Set — 중복 제거
Set<String> uniqueTypes = new HashSet<>();
for (String type : types) uniqueTypes.add(type);
// {소설, 잡지, 참고서}  ← 순서는 보장되지 않는다
```

**해설 — `getOrDefault` 관용구가 이 절의 핵심**

```java
map.put(key, map.getOrDefault(key, 0) + 1);
```
이 한 줄이 **"없으면 0에서 시작, 있으면 기존 값에 +1"**을 표현한다. 없었다면 이렇게 써야 한다.
```java
if (map.containsKey(key)) map.put(key, map.get(key) + 1);
else                      map.put(key, 1);
```

이 패턴이 저장소에서 실제로 재사용되는 곳:
- `MemberManager.countByAgeGroup()` — 성인/미성년 인원 집계
- `StatisticsAnalyzer.calculateAveragePriceByType()` — 출판물 종류별 평균가
- `StatisticsAnalyzer.calculatePublicationDistribution()` — 종류별 비율

```java
// MemberManager.countByAgeGroup()
public Map<String, Integer> countByAgeGroup() {
    Map<String, Integer> result = new HashMap<>();
    for (Member member : members) {
        String group = member.isAdult() ? "성인" : "미성년";
        result.put(group, result.getOrDefault(group, 0) + 1);
    }
    return result;
}
```

⚠️ **`keySet()` vs `entrySet()`**: `keySet()`으로 돌면 매번 `get(key)`를 다시 호출한다(해시 탐색 2회). `entrySet()`은 키와 값을 한 번에 꺼낸다. **값이 필요하면 `entrySet()`을 쓰는 게 맞다.**

**③ 컬렉션 실습 — `chapter6/collections/`**

```java
// TestArrayList.java — 제네릭 유무 비교
List<MyDate> list = new ArrayList<MyDate>();     // 제네릭 명시
List<Integer> list2 = new ArrayList<>();          // 다이아몬드 연산자 (Java 7+)
list2.add(new Integer(100));   // 명시적 박싱
list2.add(200);                // 자동 박싱

// TestArrayList2.java — List 를 메서드에 넘기기
public static void addValues(List<String> values) {   // ★ 주소가 전달된다
    values.add("고길동");
    values.add("둘리");
}
List<String> list = new ArrayList<>();
addValues(list);            // void 인데 list 가 채워진다
printValues(list);
```

**해설**: `addValues(list)`가 `void`인데 동작하는 이유는 1-8절 **참조 전달** 그대로다. `int[] values`를 넘겼던 `doubleAll()`과 완전히 같은 원리.

**④ 컬렉션 안의 객체는 참조로 공유된다 — 자료 10.6절의 경고**

```java
// ListDemo.java
List<Product> cart = new ArrayList<>();
cart.add(new Product("소설", 11000));
// cart 에 담긴 것은 객체의 "주소"다. 밖에서 그 객체를 수정하면 cart 안의 것도 바뀐다.
```

`MemberManager`가 이 문제를 의식하고 방어한 흔적:
```java
public List<Member> getAllMembers() {
    return new ArrayList<>(members);      // ★ 새 리스트로 복사해서 반환
}
```
**해설**: 그냥 `return members;`를 하면 외부에서 `getAllMembers().clear()`로 내부 목록을 지워버릴 수 있다. **새 ArrayList로 감싸서 반환**하면 리스트 구조는 보호된다. ⚠️ 단 이건 **얕은 복사(shallow copy)**라 안에 든 `Member` 객체 자체는 여전히 공유된다. `Library.getAllBooks()`도 같은 패턴.

---

## 1-16. I/O 스트림 — 개념만 (실습 코드 없음)

⚠️ **이 저장소에는 파일 I/O·직렬화 코드가 없다.** 자료 11장 내용은 개념으로만 정리하고, 관련 개념이 나중에 어디서 다시 등장하는지 표시해 둔다.

### 개념

**Stream**: 데이터의 연속된 흐름. **단방향**이다.

**4대 추상 클래스**

| | 입력 | 출력 |
|---|---|---|
| **Byte 단위** (이진 데이터) | `InputStream` | `OutputStream` |
| **Character 단위** (텍스트) | `Reader` | `Writer` |

**Node Stream** — 물리적 장치와 직접 연결
- Character: `FileReader/Writer`, `CharArrayReader/Writer`, `StringReader/Writer`, `PipedReader/Writer`
- Byte: `FileInputStream/OutputStream`, `ByteArrayInputStream/OutputStream`, `PipedInputStream/OutputStream`

**Processing Stream** — Node Stream 위에 얹어 기능을 추가
- Character: `BufferedReader/Writer`, `FilterReader/Writer`, **`InputStreamReader`(바이트→문자)**, `LineNumberReader`, `PushbackReader`, `PrintWriter`
- Byte: `BufferedInputStream/OutputStream`, `FilterInput/OutputStream`, **`OutputStreamWriter`(문자→바이트)**, `ObjectInput/OutputStream`, `DataInput/OutputStream`, `PrintStream`

**파일 읽기 정형 패턴**
```java
BufferedReader br = new BufferedReader(new FileReader(fileName));
String s;
while ((s = br.readLine()) != null) { ... }    // null 이면 파일 끝
br.close();                                     // finally 에서 닫는다
```

**파일 쓰기**
```java
PrintWriter pw = new PrintWriter(new FileWriter(fileName));
pw.println(msg);
pw.close();
```

**콘솔 I/O**
- `System.out` — PrintStream (표준 출력)
- `System.in` — InputStream (키보드 입력)
- `System.err` — PrintStream (에러 출력)
```java
BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
String line = in.readLine();      // IOException 처리 필요
```

**직렬화(Serialization)**
- 객체 메모리를 바이트 형태로 만드는 것
- **멤버 변수만** 대상. static 변수·메서드·생성자는 제외
- 대상 클래스는 `java.io.Serializable`을 implements (**마커 인터페이스** — 구현할 메서드 없음)
- `ObjectOutputStream.writeObject()` / `ObjectInputStream.readObject()`
- 역직렬화 시 `ClassNotFoundException`, `IOException` 처리 필요

### 이 개념이 저장소에서 다시 나타나는 곳

| 자료의 개념 | 이 저장소에서 |
|---|---|
| `System.in` + `Scanner` | `chapter2/util/TestScanner.java`, `chapter1/first/MyCalculatorUsingScanner.java` — `Scanner`는 `InputStreamReader`를 감싼 편의 클래스 |
| `System.out` (PrintStream) | 전 파일의 `System.out.println` |
| 스트림을 `finally`에서 닫기 | 🔗 PART 2 `UserDaoImplJDBC`의 `conn.close()` — **동일한 자원 반납 패턴** |
| `Serializable` 마커 인터페이스 | `InvalidAgeException`의 `serialVersionUID`가 여기서 유래. 🔗 PART 3 JPA Entity도 종종 `Serializable`을 구현 |
| `BufferedReader`의 감싸기 구조 | 🔗 PART 2 Spring AOP의 **프록시**가 같은 아이디어(대상을 감싸 기능 추가) |

⚠️ **면접에서 자주 묻는 것**: "`InputStream`과 `Reader`의 차이" → **바이트 단위냐 문자 단위냐.** 이미지·동영상은 `InputStream`, 텍스트는 `Reader`. 한글이 깨지는 대부분의 문제가 이 선택을 잘못한 데서 온다.

---

## 1-17. JDBC — 개념만 (실습은 PART 2에서)

⚠️ **`backendjava`에는 JDBC 코드가 없다.** 다만 자료 12장 내용은 PART 2의 `springfw/SpringFWXml/src/main/java/myspring/user/dao/UserDaoImplJDBC.java`에서 **거의 그대로** 구현된다. 여기서는 개념을 정리하고, 실제 코드는 2-4절에서 다룬다.

### 개념

**JDBC**: Java에서 DB에 접근하는 표준 API (`java.sql`, `javax.sql`). 각 DBMS 벤더가 드라이버로 이 인터페이스를 구현한다.

**JDBC 5단계 (외워야 함)**

```
1. Driver 등록      Class.forName("oracle.jdbc.driver.OracleDriver");
2. Connection 획득  DriverManager.getConnection(url, user, password);
                    url = jdbc:oracle:thin:@IP:포트:SID
3. Statement 생성   Statement stmt = conn.createStatement();
4. SQL 전송/결과처리
     SELECT : ResultSet rset = stmt.executeQuery(query);
              while (rset.next()) { rset.getString("컬럼명"); }
     DML    : int updateCount = stmt.executeUpdate(query);
5. Resource 닫기    rset.close(); stmt.close(); conn.close();   ← ★ 역순
```

**ResultSet**: `next()`가 다음 행이 있으면 true를 반환하며 커서를 이동. `getString("ID")` 또는 `getString(2)`로 값을 꺼낸다.

**트랜잭션 수동 제어 (UPDATE 예시)**
```java
conn.setAutoCommit(false);            // 자동 커밋 끄기
int updateCount = stmt.executeUpdate(query.toString());
if (updateCount == 1) conn.commit();
else                  conn.rollback();
```

**Statement vs PreparedStatement**

| | Statement | PreparedStatement |
|---|---|---|
| SQL | 문자열로 직접 조립 | `?` 자리표시자 + `setXxx()` 바인딩 |
| 파싱 | **매 실행마다** | **한 번만** |
| 반복 실행 | 오버헤드 큼 | **성능 좋음** |
| 코드 | 직관적, 짧음 | 길어짐 |

```java
PreparedStatement pstmt = conn.prepareStatement("UPDATE EMP SET LAST_NAME = ? WHERE ID = ?");
pstmt.setString(1, "HITE2");
pstmt.setString(2, "10005");
pstmt.executeUpdate();
```

⚠️ **자료에는 안 나오지만 반드시 알아야 할 것**: `PreparedStatement`는 성능 때문만이 아니라 **SQL Injection 방어** 때문에 쓴다. 문자열 결합으로 SQL을 만들면 `'; DROP TABLE ...--` 같은 입력에 뚫린다. **실무에서는 예외 없이 PreparedStatement**를 쓴다.

**`StringBuffer`로 쿼리 조립** (자료 12.4절)
```java
StringBuffer query = new StringBuffer();
query.append("SELECT ID ").append(",LAST_NAME ").append("FROM EMP ");
```
🔗 1-13절에서 본 그 이유(문자열 결합 시 객체가 계속 생성되는 것을 피함)와 동일.

### 이 5단계가 어떻게 사라지는가 — 학습 로드맵

```
JDBC 직접 코딩 (자료 12장)
   Driver 등록 → Connection → Statement → 실행 → close
   ⚠️ 문제 : 매 DAO 메서드마다 이 5단계가 반복. try-catch-finally 지옥.
        ↓
Spring JdbcTemplate (PART 2)
   Connection 획득/반납을 프레임워크가 대신 → SQL 과 결과 매핑만 작성
        ↓
MyBatis (PART 2)
   SQL 을 XML 로 분리 → 자바 코드에서 SQL 문자열이 사라짐
        ↓
Spring Data JPA (PART 3)
   SQL 자체를 작성하지 않음 → 메서드 이름으로 쿼리 생성
```

**이 흐름이 세 저장소를 관통하는 두 번째 축이다.** 각 단계가 무엇을 없애줬는지를 기준으로 기억하면 정리가 쉽다.

---

## 1-18. 3계층 종합 — entity · control · exception

### 개념 (자료에는 없는, 이 저장소의 설계 원칙)

```
control     흐름을 지휘. main 이 있는 곳. 사용자와 대화.
   ↓ 사용
entity      데이터를 담는 객체. private 필드 + getter/setter + toString()
   ↓ 던짐
exception   비즈니스 규칙 위반을 표현
```

### 코드 위치 — `basic/step10_layer/`

**entity — `Member.java`** (1-3절 참고)

**control — `MemberManager.java`** : 저장·검색·통계를 담당

```java
public class MemberManager {
    private List<Member> members;                     // 저장소 역할
    private int nextNumber;
    private static final String ID_PREFIX = "M";

    public MemberManager() {
        this.members = new ArrayList<>();
        this.nextNumber = 1001;
    }

    // CREATE
    public String register(String name, int age) {
        String id = generateId();
        members.add(new Member(id, name, age));
        return id;
    }
    private String generateId() { return ID_PREFIX + (nextNumber++); }

    // READ — 단건(없으면 예외) / 다건(없으면 빈 리스트)
    public Member findById(String id) throws MemberNotFoundException {
        for (Member member : members) {
            if (member.getId().equals(id)) return member;
        }
        throw new MemberNotFoundException(id + " 회원을 찾을 수 없습니다.");
    }
    public List<Member> findByName(String name) {
        List<Member> result = new ArrayList<>();
        for (Member member : members) {
            if (member.getName().equals(name)) result.add(member);
        }
        return result;                    // ★ 없으면 빈 리스트 (null 아님)
    }

    // DELETE
    public boolean remove(String id) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId().equals(id)) {
                members.remove(i);
                return true;
            }
        }
        return false;                     // ★ 실패를 boolean 으로
    }

    // 통계
    public Map<String, Integer> countByAgeGroup() { ... }
    public double getAverageAge() {
        if (members.isEmpty()) return 0;              // ★ 0 나누기 방어
        ...
        return (double) total / members.size();
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members);   // 방어적 복사
    }
}
```

**control(실행) — `MemberDemo.java`**: `main`에서 시나리오대로 호출하고 예외를 처리

```java
MemberManager manager = new MemberManager();
String id1 = manager.register("김하늘", 25);
manager.register("이순호", 17);
manager.register("김하늘", 41);              // ★ 동명이인 — findByName 이 List 를 반환하는 이유

try {
    Member found = manager.findById(id1);
    found.setActive(false);                  // 휴면 처리
} catch (MemberNotFoundException e) { ... }

try {
    manager.findById("M9999");               // 없는 ID → 예외 확인
} catch (MemberNotFoundException e) { ... }

List<Member> sameName = manager.findByName("김하늘");   // 2명
```

**해설 — 이 구조가 왜 중요한가**

1. **`findById`는 예외, `findByName`은 리스트, `remove`는 boolean.**
   반환 방식이 셋 다 다르고, **각각 타당한 이유가 있다.**
   - ID는 유일하므로 없으면 비정상 → 예외
   - 이름은 중복 가능 → 0개도 정상 → 빈 리스트
   - 삭제는 성공/실패만 알면 됨 → boolean

2. **`main`이 `MemberManager` 안에 없다.** `MemberManager`는 "무엇을 할 수 있는가"만 알고, "어떤 순서로 할 것인가"는 `MemberDemo`가 정한다. **역할 분리.**

3. 🔗 **이 구조가 그대로 PART 2·3으로 이어진다.**

| backendjava | springfw | springboot |
|---|---|---|
| `entity.Member` | `vo.UserVO` | `entity.User` |
| `control.MemberManager` | `service.UserServiceImpl` + `dao.UserDaoImpl` | `service` + `repository.UserRepository` |
| `control.MemberDemo` (main) | `controller.UserController` | `controller.UserController` |
| `exception.MemberNotFoundException` | — | `exception.BusinessException` |
| `List<Member> members` (메모리) | DB 테이블 | DB 테이블 |

**⚠️ 가장 큰 차이는 `MemberManager`가 `List`에 저장하던 것을 DB가 대신하게 된다는 것 하나뿐이다.** 나머지 구조는 이름만 바뀐다. 이 대응표를 머리에 넣고 PART 2로 넘어가면 훨씬 수월하다.

### 나머지 종합 실습 3개 요약

| 프로젝트 | 무엇을 연습하는가 | 핵심 클래스 |
|---|---|---|
| `mylab/bank` | **상속 + 다형성 + 예외 계층** (완성도 최상) | `Account`(abstract) → `SavingsAccount`/`CheckingAccount`, `Bank`(관리), 예외 3종 |
| `mylab/book` | **다형성 + 통계(Map 활용)** | `Publication` → `Magazine`/`Novel`/`ReferenceBook`, `ShoppingCart`, `StatisticsAnalyzer` |
| `mylab/library` | **상태 관리(대출/반납) + 조회** | `Book`(isAvailable 상태), `Library`(검색 4종) |
| `mylab/student` | **setter 검증 + 사용자 정의 예외** | `Student.setGrade()` |

**`mylab/library/entity/Book.java`의 상태 전이가 볼 만하다.**
```java
private boolean isAvailable;      // 대출 가능 여부

public boolean checkOut() {
    if (isAvailable) {
        isAvailable = false;
        return true;              // 대출 성공
    }
    return false;                 // 이미 대출 중
}
public boolean returnBook() {
    isAvailable = true;
    return true;
}
```
⚠️ `returnBook()`은 **항상 true를 반환한다.** 대출 중이 아닌 책을 반납해도 성공이 된다. 요구사항에 따라 다르지만, 엄밀히 하려면 `if (!isAvailable)` 검사가 필요하다. **개선 포인트로 기억해 둘 것.**

**`ShoppingCart`는 `mylab/book`에서 가장 실전적인 클래스다.**
```java
public int calculateDiscountedPrice() {
    int total = 0;
    for (Publication item : items) {
        if (item instanceof Magazine)            total += item.getPrice() * 0.9;
        else if (item instanceof Novel)          total += item.getPrice() * 0.85;
        else if (item instanceof ReferenceBook)  total += item.getPrice() * 0.8;
        else                                     total += item.getPrice();
    }
    return total;
}
```
⚠️ `int total`에 `item.getPrice() * 0.9`(double)를 더하고 있다. **컴파일이 되는 이유는 `+=`가 암묵적 캐스팅을 포함하기 때문**(`total = (int)(total + ...)`). 매 항목마다 소수점이 버려져 총액이 미세하게 달라진다. **`double`로 누적한 뒤 마지막에 한 번만 캐스팅하는 것이 맞다.** 실무 코드리뷰에서 지적받는 전형적 사례.

---

## 1-19. Java 파트 자가 점검 질문

각 질문에 코드로 답할 수 있으면 이 파트는 정리된 것이다.

**문법·타입**
1. `total / count`가 3.33이 아니라 3이 나온다. 왜이고 어떻게 고치나? → `VariableDemo`
2. `'A' + 'B'`는 왜 `"AB"`가 아닌가? → `TestDataType`
3. 지역 변수와 멤버 변수의 초기화 차이는? → `VariableDemo`

**객체지향**
4. 필드를 `private`으로 하면 무엇이 좋은가? 코드로 답하라 → `Product.setPrice()`
5. 생성자를 하나라도 정의하면 왜 `new X()`가 안 되나? → `Magazine`
6. `this()`와 `super()`를 한 생성자에 같이 쓸 수 있나? → `LaserPrinter`
7. `Employee e = new Manager()`에서 `e.calculateSalary()`는 누구 것이 실행되나? → `InheritanceDemo`
8. `protected`는 언제 쓰나? → `Employee.department`, `Shape.name`
9. 추상 클래스와 인터페이스를 언제 나눠 쓰나? → `Animal` vs `Pet`
10. `Spider`가 `Pet`을 구현하지 않는 것이 왜 설계상 옳은가?
11. `instanceof` 없이 다운캐스팅하면 무슨 일이 일어나나? → `PolymorphismDemo`
12. `chapter5/oop/bad`가 `good`보다 나쁜 이유 4가지는?

**예외**
13. Checked와 Unchecked를 나누는 기준은? → `ExceptionDemo`의 `IllegalArgumentException` vs `InvalidAgeException`
14. `catch (Exception e)`를 맨 앞에 쓰면 왜 컴파일 에러인가?
15. 조회 실패를 예외로 할까 null로 할까? 판단 기준은? → `MemberManager` vs `Library`
16. 오버라이딩할 때 부모보다 넓은 예외를 던질 수 없는 이유는? → `CheckingAccount`

**컬렉션**
17. `List<Integer> list`에서 `list.remove(1)`은 무엇을 지우나?
18. 향상된 for 안에서 `remove()`하면 왜 터지나? → `ListDemo`
19. `map.getOrDefault(key, 0) + 1` 관용구를 안 쓰면 어떻게 써야 하나?
20. `HashSet`에 커스텀 객체를 넣을 때 무엇을 오버라이딩해야 하나? → `EqualsTest`
21. `keySet()`과 `entrySet()` 중 무엇을 언제 쓰나?

**설계**
22. `entity / control / exception` 3계층이 각각 무슨 책임을 지나? → `step10_layer`
23. `getAllMembers()`가 `new ArrayList<>(members)`를 반환하는 이유는?
24. JDBC 5단계를 순서대로 말하고, 각 단계가 이후 어떤 기술로 대체되는가?
