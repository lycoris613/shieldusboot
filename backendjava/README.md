# backendjava — 자바 학습 저장소 정리

자바 문법 기초부터 객체지향 · 예외 처리 · 컬렉션 · 3계층 설계까지 단계별로 실습한 코드 모음이다.
이 문서는 **코드를 다시 읽을 때 옆에 두는 공부 자료**다. 개념마다

1. **정의 코드** — 그 개념이 실제로 어떻게 생겼는지
2. **쓰는 법** — 어디서 어떻게 호출하는지
3. **응용** — 어떻게 확장 · 변형하는지

순서로 정리했다. 길지만, 각 절은 독립적으로 읽어도 된다.

- 파일 수: 약 114개 `.java`
- 빌드 도구 없음 (Eclipse 프로젝트: `.classpath`, `.project`)
- 테스트 프레임워크 없음 → 모든 실행은 각 클래스의 `main` + `System.out.println`

---

## 목차

- [1. 저장소 한눈에 보기](#1-저장소-한눈에-보기)
- [2. 이 저장소를 관통하는 구조: 3계층](#2-이-저장소를-관통하는-구조-3계층)
- [3. Part 1 — 자바 문법 기초](#3-part-1--자바-문법-기초)
- [4. Part 2 — 클래스 · 캡슐화 · static](#4-part-2--클래스--캡슐화--static)
- [5. Part 3 — 상속](#5-part-3--상속)
- [6. Part 4 — 다형성 · 추상 클래스 · 인터페이스](#6-part-4--다형성--추상-클래스--인터페이스)
- [7. Part 5 — 예외 처리](#7-part-5--예외-처리)
- [8. Part 6 — 컬렉션](#8-part-6--컬렉션)
- [9. Part 7 — 3계층 종합 실습 (mylab / workshop)](#9-part-7--3계층-종합-실습-mylab--workshop)
- [10. 자주 쓰는 패턴 모음](#10-자주-쓰는-패턴-모음)
- [11. 심화 학습 포인트](#11-심화-학습-포인트)
- [12. 실행 방법](#12-실행-방법)
- [13. 스스로 점검하는 질문](#13-스스로-점검하는-질문)

---

## 1. 저장소 한눈에 보기

```
src/
├─ basic/        단계별 커리큘럼 (step01 ~ step10). 개념을 최소 코드로 압축.
│  ├─ step01_syntax        변수 / 연산자 / 제어문 / 문자열
│  ├─ step02_array         배열
│  ├─ step03_method        메서드
│  ├─ step04_class         클래스와 객체, 캡슐화
│  ├─ step05_static        static 변수 / 메서드
│  ├─ step06_inheritance   상속, 오버라이딩, 업캐스팅
│  ├─ step07_polymorphism  추상 클래스, 인터페이스, 다형성
│  ├─ step08_exception     try-catch-finally, throws, 사용자 정의 예외
│  ├─ step09_collection    List / Map / Set
│  └─ step10_layer         entity - control - exception 3계층 종합
│
├─ chapter1 ~ chapter6/    교재 진도용 실습 (문법 → OOP → 컬렉션)
│
├─ mylab/       종합 실습 4개 — 요구사항이 있는 미니 프로젝트
│  ├─ bank/      은행 계좌 관리 (상속 + 다형성 + 예외)  ← 가장 완성도 높음
│  ├─ book/      출판물 관리 (다형성 + 통계)
│  ├─ library/   도서관 대출/반납
│  └─ student/   학생 정보 + 학년 검증 예외
│
└─ workshop/    소규모 실습 4개 — 개념 하나씩
   ├─ account/   생성자 오버로딩 + 사용자 정의 예외(필드 포함)
   ├─ animal/    추상 클래스 + 인터페이스 동시 구현
   ├─ book/      다형적 아규먼트(Polymorphic Argument)
   └─ person/    배열 vs List 비교
```

읽는 순서: **`basic` 으로 개념을 잡고 → `mylab` / `workshop` 에서 응용을 확인**한다.
`basic` 각 단계 주석에는 "이 개념은 `mylab.bank...` 와 같은 자리다" 식으로 대응이 적혀 있다.

---

## 2. 이 저장소를 관통하는 구조: 3계층

`mylab`, `workshop`, `basic/step10` 의 모든 실습은 패키지를 3개로 나눈다.

| 계층 | 역할 | 넣는 것 | 넣지 않는 것 |
|---|---|---|---|
| `entity` | 데이터 한 건 | 필드(private), 생성자, getter/setter, `toString()`, 자기 데이터 판단 | 화면 출력, 목록 검색, 통계 |
| `control` | entity 여러 개 관리 | 등록/검색/삭제/통계, 실행 흐름(`main`) | 데이터 자체의 세부 규칙 |
| `exception` | 업무 규칙 위반 알림 | `Exception` 상속한 사용자 정의 예외 | — |

### 한 덩어리로 보기 — `basic/step10_layer`

**entity** — 데이터와 "자기 자신에 대한 판단"만 가진다.

```java
// src/basic/step10_layer/entity/Member.java
public class Member {
    private String id;
    private String name;
    private int age;
    private boolean active;              // 활동 회원 여부

    public Member() {}
    public Member(String id, String name, int age) {
        this.id = id; this.name = name; this.age = age;
        this.active = true;
    }
    // ... getter / setter 생략 ...

    public boolean isActive() { return active; }        // boolean getter 는 is~

    /** 성인 여부를 스스로 판단한다. (자기 데이터에 대한 판단은 entity 가) */
    public boolean isAdult() { return age >= 19; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d세) - %s", id, name, age, active ? "활동" : "휴면");
    }
}
```

**exception** — "못 찾음"을 `null` 대신 예외로.

```java
// src/basic/step10_layer/exception/MemberNotFoundException.java
public class MemberNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public MemberNotFoundException(String message) { super(message); }
}
```

**control** — entity 목록을 들고 등록/검색/통계를 담당.

```java
// src/basic/step10_layer/control/MemberManager.java
public class MemberManager {
    private List<Member> members;
    private int nextNumber;
    private static final String ID_PREFIX = "M";

    public MemberManager() {
        this.members = new ArrayList<>();
        this.nextNumber = 1001;
    }

    /** 회원을 등록하고 발급된 ID 를 반환한다. */
    public String register(String name, int age) {
        String id = generateId();
        members.add(new Member(id, name, age));
        return id;
    }
    private String generateId() { return ID_PREFIX + (nextNumber++); }

    /** ID 로 회원을 찾는다. 없으면 예외. */
    public Member findById(String id) throws MemberNotFoundException {
        for (Member member : members) {
            if (member.getId().equals(id)) return member;   // 문자열 비교는 equals()
        }
        throw new MemberNotFoundException(id + " 회원을 찾을 수 없습니다.");
    }

    /** 성인/미성년 인원수를 세어 Map 으로 반환. */
    public Map<String, Integer> countByAgeGroup() {
        Map<String, Integer> result = new HashMap<>();
        for (Member member : members) {
            String group = member.isAdult() ? "성인" : "미성년";
            result.put(group, result.getOrDefault(group, 0) + 1);
        }
        return result;
    }

    /** 원본이 외부에서 변경되지 않도록 복사본을 반환. */
    public List<Member> getAllMembers() { return new ArrayList<>(members); }
}
```

### 쓰는 법 — `main` 의 흐름

```java
// src/basic/step10_layer/control/MemberDemo.java  (요약)
MemberManager manager = new MemberManager();

String id1 = manager.register("김하늘", 25);
manager.register("이순호", 17);

// 예외를 던지는 메서드는 반드시 try-catch
try {
    Member found = manager.findById(id1);
    found.setActive(false);                 // 상태 변경
} catch (MemberNotFoundException e) {
    System.out.println("오류 : " + e.getMessage());
}

Map<String, Integer> group = manager.countByAgeGroup();
for (String key : group.keySet()) {
    System.out.println(key + " : " + group.get(key) + "명");
}
```

**이 골격이 `mylab.bank`, `mylab.library`, `workshop.person` 에서 그대로 반복된다.** 이름만 바뀔 뿐이다.

---

## 3. Part 1 — 자바 문법 기초

| 개념 | 대표 파일 |
|---|---|
| 변수 / 자료형 | [`VariableDemo`](src/basic/step01_syntax/VariableDemo.java), [`TestDataType`](src/chapter1/first/TestDataType.java) |
| 연산자 | [`OperatorDemo`](src/basic/step01_syntax/OperatorDemo.java) |
| 제어문 | [`ControlFlowDemo`](src/basic/step01_syntax/ControlFlowDemo.java), [`TestIf`](src/chapter2/controlstmt/TestIf.java), [`TestForLoop`](src/chapter2/controlstmt/TestForLoop.java) |
| 문자열 | [`StringDemo`](src/basic/step01_syntax/StringDemo.java), [`TestString`](src/chapter1/first/TestString.java) |
| 배열 | [`ArrayDemo`](src/basic/step02_array/ArrayDemo.java), [`TestIntMultiArray`](src/chapter4/array/TestIntMultiArray.java) |
| 메서드 | [`MethodDemo`](src/basic/step03_method/MethodDemo.java) |

### 메서드 — 정의 / 호출 / 인자 받기

```java
// src/chapter1/first/MyCalcurator.java  (요약)
public class MyCalcurator {
    public static void main(String[] args) {
        // 프로그램 실행 시 넘어온 문자열 인자를 받는다: java MyCalcurator 10 3
        if (args.length != 2) {
            System.out.println("아규먼트에 두개의 숫자를 입력해야 합니다.");
            System.exit(0);
        }
        int num1 = Integer.parseInt(args[0]);   // String → int
        int num2 = Integer.parseInt(args[1]);

        System.out.println("더하기 결과 : " + add(num1, num2));
        System.out.println("나머지 결과 : " + remainder(num1, num2));
    }

    public static int add(int op1, int op2)       { return op1 + op2; }
    public static int substract(int op1, int op2) { return op1 - op2; }   // (오타: subtract)
    public static int remainder(int op1, int op2) { return op1 % op2; }
}
```

**쓰는 법** — `static` 메서드는 객체 없이 `클래스명.메서드()`. 같은 클래스 안에서는 이름만으로 호출.

**응용** — `divide` 는 0 나눗셈 방어가 없다. 이렇게 고칠 수 있다.

```java
public static int divide(int op1, int op2) {
    if (op2 == 0) throw new IllegalArgumentException("0 으로 나눌 수 없습니다.");
    return op1 / op2;
}
```

### 문자열 비교 — `==` 금지, `equals()`

```java
// 잘못
if (name == "김하늘") { ... }          // 참조(주소) 비교 → 거의 항상 false

// 올바름  (저장소 전역에서 이 방식)
if (name.equals("김하늘")) { ... }
if (person.getGender() == gender) { ... }   // 단, char 같은 기본형은 == 로 값 비교 OK
```

---

## 4. Part 2 — 클래스 · 캡슐화 · static

### 4.1 entity 클래스의 5요소

```java
// src/basic/step04_class/Product.java
public class Product {

    // ① private 필드 — 데이터를 감춘다
    private String name;
    private int price;
    private int stock;

    // ② 기본 생성자 — 다른 생성자를 정의하면 자동 생성되지 않으므로 직접 선언
    public Product() {}

    // ③ 값을 받는 생성자 (오버로딩) — this(...) 로 다른 생성자에 위임, 반드시 첫 줄
    public Product(String name, int price) {
        this(name, price, 0);
    }
    public Product(String name, int price, int stock) {
        this.name = name;          // this.name = 필드, name = 매개변수
        setPrice(price);           // 검증 있는 setter 재사용
        this.stock = stock;
    }

    // ④ getter / setter — 통제된 접근 통로
    public int getPrice() { return price; }

    /** 음수는 허용하지 않는다. setter 안에 검증을 넣을 수 있는 게 private 으로 감추는 이유. */
    public void setPrice(int price) {
        if (price < 0) {
            System.out.println("가격은 0 이상이어야 합니다. 0으로 설정합니다.");
            this.price = 0;
            return;
        }
        this.price = price;
    }

    /** @return 판매 성공 여부 */
    public boolean sell(int amount) {
        if (amount <= 0 || amount > stock) return false;
        stock -= amount;
        return true;
    }

    // ⑤ toString() — println(객체) 시 자동 호출
    @Override
    public String toString() {
        return "Product [name=" + name + ", price=" + price + ", stock=" + stock + "]";
    }
}
```

**쓰는 법**

```java
Product p1 = new Product("소설", 11000);         // stock 은 0
Product p2 = new Product("잡지", 9900, 50);
p2.setPrice(-100);                               // → "가격은 0 이상..." 출력, price=0
System.out.println(p1);                          // toString() 자동 호출
boolean ok = p2.sell(10);
```

**응용** — 생성자에서 `this.month = month` 대신 검증 setter 를 호출하면 규칙이 한 곳에 모인다.

```java
// src/chapter3/oop/MyDate.java
public MyDate(int year, int month, int day) {
    this.year = year;
    setMonth(month);            // ← 1~12 검증을 생성자와 setter 가 공유
    this.day = day;
}
public void setMonth(int month) {
    if (month >= 1 && month <= 12) this.month = month;
    else System.out.println("Month는 1~12 사이의 값만 가능합니다.");
}
```

### 4.2 static — 클래스에 하나, 모든 객체가 공유

```java
// src/basic/step05_static/Counter.java
public class Counter {
    private static int totalCount = 0;                 // 공유 변수
    private static final int START_NUMBER = 1000;      // 안 변하는 값은 static final 상수
    private int id;                                    // 객체마다 다름

    public Counter() {
        totalCount++;                                  // 공유 변수 증가
        this.id = START_NUMBER + totalCount;           // 그 값을 이 객체 번호로
    }

    public int getId() { return id; }

    /** 객체 없이 Counter.getTotalCount() 로 호출 */
    public static int getTotalCount() { return totalCount; }

    // static 메서드 안에서는 인스턴스 변수(id)나 this 를 쓸 수 없다.
    public static String makeCode(String prefix, int number) { return prefix + number; }
}
```

**쓰는 법**

```java
Counter a = new Counter();   // totalCount 1 → a.id = 1001
Counter b = new Counter();   // totalCount 2 → b.id = 1002
System.out.println(Counter.getTotalCount());   // 2  (객체가 아니라 클래스명으로)
```

**응용** — `Bank` 가 계좌번호를 자동 증가시키는 것이 같은 원리다.

```java
// src/mylab/bank/entity/Bank.java
private int nextAccountNumber = 1000;   // (여기선 인스턴스 변수지만 발상은 동일)

public SavingsAccount createSavingsAccount(String ownerName, double initialBalance, double interestRate) {
    String accountNumber = "AC" + nextAccountNumber++;   // AC1000, AC1001, ...
    SavingsAccount account = new SavingsAccount(accountNumber, ownerName, initialBalance, interestRate);
    accounts.add(account);
    return account;
}
```

### 4.3 `equals()` / `hashCode()` — 항상 같이 재정의

```java
// src/chapter1/first/MyDate.java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MyDate other = (MyDate) obj;                    // 형변환(캐스팅)
    if (this.day != other.day) return false;
    if (this.month != other.month) return false;
    if (this.year != other.year) return false;
    return true;
}
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + day;
    result = prime * result + month;
    result = prime * result + year;
    return result;
}
```

**쓰는 법 / 응용** — 재정의하지 않으면 "내용이 같아도" 다른 객체로 취급된다.

```java
MyDate a = new MyDate(1, 1, 2000);
MyDate b = new MyDate(1, 1, 2000);
a == b          // false — 주소가 다름
a.equals(b)     // true  — equals 를 내용 비교로 재정의했으므로
// List.contains(), Set, Map 의 키 비교가 전부 equals()+hashCode() 에 의존한다.
```

---

## 5. Part 3 — 상속

### 5.1 왜 상속인가 — `bad` vs `good`

**bad** — 상속 없이 두 클래스가 필드 · getter 를 통째로 중복한다.

```java
// src/chapter5/oop/bad/MereClerk.java
public class MereClerk {
    String name;
    double salary;
    public MereClerk(String name, double salary) { this.name = name; this.salary = salary; }
    public String getName()   { return this.name; }
    public double getSalary() { return this.salary; }
    public void manageSalary(double rate) { salary = salary + salary * (rate / 100); }
}

// src/chapter5/oop/bad/Manager.java  — 위와 거의 똑같다 (중복!)
public class Manager {
    String name;
    double salary;
    public Manager(String name, double salary) { this.name = name; this.salary = salary; }
    public String getName()   { return this.name; }
    public double getSalary() { return this.salary; }
    public void manageSalary(double rate) {
        salary = salary + salary * (rate / 100);
        salary += 20;   // 관리자만 20만원 추가
    }
}
```

**good** — 공통은 추상 부모로, 차이(`manageSalary`)만 자식이 구현한다.

```java
// src/chapter5/oop/good/Employee.java
public abstract class Employee {
    private String name;
    protected double salary;                 // 자식이 직접 접근하도록 protected

    public Employee() { super(); }
    public Employee(String name, double salary) { this.name = name; this.salary = salary; }

    public String getName()   { return this.name; }
    public double getSalary() { return this.salary; }

    public abstract void manageSalary(double rate);   // 구현은 자식에게 강제
}
```
```java
// src/chapter5/oop/good/MereClerk.java
public class MereClerk extends Employee {
    public MereClerk(String name, double salary) { super(name, salary); }
    @Override
    public void manageSalary(double rate) { salary = salary + salary * (rate / 100); }
}
```
```java
// src/chapter5/oop/good/Manager.java
public class Manager extends Employee {
    private String deptName;

    public Manager(String name, double salary) { super(name, salary); }
    public Manager(String name, double salary, String deptName) {
        this(name, salary);                  // 같은 클래스의 다른 생성자 호출
        this.deptName = deptName;
    }
    @Override
    public void manageSalary(double rate) {
        salary = salary + salary * (rate / 100);
        salary += 20;
    }
    public String getDeptName() { return deptName; }
}
```

**쓰는 법**

```java
// src/chapter5/oop/good/FlexibleCompanyDemo.java  (요약)
Employee emp1 = new MereClerk("철수", 100);
Employee emp3 = new Manager("홍길동", 200, "인사부");   // 부모 타입 변수에 자식 객체 (업캐스팅)

Manager mgr = (Manager) emp3;                          // 자식 전용 메서드 쓰려면 다운캐스팅
System.out.println(mgr.getDeptName());

// 부모 타입 배열에 서로 다른 자식을 담는다 (Heterogeneous Array)
Employee[] emps = { new MereClerk("철수", 100), new Manager("홍길동", 200, "인사부") };
for (Employee emp : emps) {
    if (emp instanceof Manager) {
        System.out.print("부서: " + ((Manager) emp).getDeptName() + " ");
    }
    emp.manageSalary(10);                              // 실제 객체의 manageSalary 실행
    System.out.println(emp.getName() + " : " + emp.getSalary());
}
```

**응용** — "임원(Executive)" 이 새로 생겨도 `Employee` 를 상속해 `manageSalary` 만 구현하면 `FlexibleCompanyDemo` 의 반복문은 **한 줄도 안 고쳐도 된다**. 이게 상속 + 다형성의 목적.

### 5.2 오버라이딩 규칙과 `super`

```java
// src/basic/step06_inheritance/Employee.java
public class Employee {
    private String name;
    private int baseSalary;
    protected String department;

    public Employee(String name, int baseSalary, String department) {
        this.name = name; this.baseSalary = baseSalary; this.department = department;
    }
    public int getBaseSalary() { return baseSalary; }

    /** 자식이 재정의할 수 있다. */
    public int calculateSalary() { return baseSalary; }

    /** 자식이 그대로 물려받는다. 안에서 calculateSalary() 를 부르는 게 핵심. */
    public void printInfo() {
        System.out.println(name + " (" + department + ") 급여 : " + calculateSalary() + "원");
    }
}
```
```java
// src/basic/step06_inheritance/Manager.java
public class Manager extends Employee {
    private int bonus;

    public Manager(String name, int baseSalary, String department, int bonus) {
        super(name, baseSalary, department);    // 자식 생성자 첫 줄에서 부모 생성자 호출
        this.bonus = bonus;
    }

    @Override                                   // 오타를 컴파일 시점에 잡아준다
    public int calculateSalary() {
        return super.calculateSalary() + bonus; // 부모의 원래 동작 재사용 + 추가
    }

    public void approve(String document) {      // 자식에만 있는 메서드
        System.out.println(getName() + " 팀장이 [" + document + "] 결재");
    }
}
```

**쓰는 법 — 다형성의 핵심 동작**

```java
// src/basic/step06_inheritance/InheritanceDemo.java  (요약)
Employee e = new Employee("김사원", 3000000, "개발팀");
Manager  m = new Manager("박팀장", 5000000, "개발팀", 1000000);

e.printInfo();   // 김사원 ... 급여 : 3000000원
m.printInfo();   // 박팀장 ... 급여 : 6000000원
                 // ↑ 부모의 printInfo() 안에서 호출된 calculateSalary() 가
                 //   실제 객체(Manager)의 것으로 실행된다.

Employee ref = new Manager("이팀장", 4800000, "기획팀", 800000);   // 업캐스팅
ref.calculateSalary();          // 자식 메서드 실행 (5,600,000)
// ref.approve("휴가원");       // 컴파일 오류 — Employee 타입엔 approve 가 없다
if (ref instanceof Manager) {
    ((Manager) ref).approve("휴가원");   // 다운캐스팅 후 호출
}
```

### 5.3 상속 계층에서 생성자 연결 (실습 코드)

```java
// src/mylab/bank/entity/Account.java  (부모)
public abstract class Account {
    private String accountNumber;
    private String ownerName;
    private double balance;

    public Account(String accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        balance += amount;
        System.out.println(amount + "원 입금. 잔액: " + balance);
    }

    /** 하위 클래스가 제약을 추가하려고 재정의할 수 있다. */
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) throw new InsufficientBalanceException("잔액 부족. 현재: " + balance);
        balance -= amount;
    }
}
```
```java
// src/mylab/bank/entity/SavingsAccount.java  (자식 1 — 이자)
public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountNumber, String ownerName, double balance, double interestRate) {
        super(accountNumber, ownerName, balance);   // 부모 필드는 부모 생성자에게
        this.interestRate = interestRate;           // 내 필드만 여기서
    }
    public void applyInterest() {
        double interest = getBalance() * interestRate / 100;
        deposit(interest);                          // 물려받은 deposit 재사용
    }
}
```
```java
// src/mylab/bank/entity/CheckingAccount.java  (자식 2 — 출금 한도)
public class CheckingAccount extends Account {
    private double withdrawalLimit;

    public CheckingAccount(String accountNumber, String ownerName, double balance, double withdrawalLimit) {
        super(accountNumber, ownerName, balance);
        this.withdrawalLimit = withdrawalLimit;
    }

    @Override
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > withdrawalLimit) {
            throw new WithdrawalLimitExceededException("출금 한도 초과. 한도: " + withdrawalLimit);
        }
        super.withdraw(amount);                     // 잔액 검사 + 실제 출금은 부모에게 위임
    }
}
```

---

## 6. Part 4 — 다형성 · 추상 클래스 · 인터페이스

### 6.1 추상 클래스 vs 인터페이스

```java
// src/basic/step07_polymorphism/Shape.java  — 추상 클래스: 공통 필드 + 공통 구현을 가질 수 있다
public abstract class Shape {
    protected String name;
    protected Shape(String name) { this.name = name; }
    public String getName() { return name; }

    public abstract double getArea();              // 도형마다 다르므로 자식에게 맡김

    public void printArea() {                       // 공통 동작은 부모가 구현
        System.out.printf("%s 의 넓이 : %.2f%n", name, getArea());
    }
}
```
```java
// src/basic/step07_polymorphism/Drawable.java  — 인터페이스: "할 수 있는 능력" 의 약속
public interface Drawable {
    String DEFAULT_COLOR = "검정";                 // 자동 public static final 상수
    void draw();                                    // 자동 public abstract

    default void printColor() {                     // Java 8+ 기본 구현
        System.out.println("기본 색상 : " + DEFAULT_COLOR);
    }
}
```
```java
// src/basic/step07_polymorphism/Circle.java  — 상속 1개 + 구현 여러 개
public class Circle extends Shape implements Drawable {
    private static final double PI = 3.141592;
    private double radius;

    public Circle(double radius) { super("원"); this.radius = radius; }
    public double getRadius() { return radius; }

    @Override public double getArea() { return PI * radius * radius; }   // 추상 클래스 요구
    @Override public void draw() { System.out.println("반지름 " + radius + " 원"); }  // 인터페이스 요구
}
```

| | 추상 클래스 | 인터페이스 |
|---|---|---|
| 개수 | `extends` **1개만** | `implements` **여러 개** |
| 가질 수 있는 것 | 필드, 생성자, 일반 메서드 + 추상 메서드 | 상수, 추상 메서드, `default` 메서드 |
| 의미 | "무엇이다" (is-a) | "무엇을 할 수 있다" (can-do) |

### 6.2 참조 타입에 따라 쓸 수 있는 메서드가 달라진다

```java
// src/basic/step07_polymorphism/PolymorphismDemo.java  (요약)
Circle   c1 = new Circle(5);     // 자기 타입      → 모든 메서드
Shape    c2 = new Circle(3);     // 부모 타입      → Shape 의 메서드만
Drawable c3 = new Circle(1);     // 인터페이스 타입 → Drawable 의 메서드만

c1.draw();  c1.printArea();
c2.printArea();      // OK
// c2.draw();        // 컴파일 오류 — Shape 에 draw() 없음
c3.draw();           // OK
c3.printColor();     // default 메서드
```

`workshop.animal` 이 똑같은 구조다.

```java
// src/workshop/animal/entity/Cat.java
public class Cat extends Animal implements Pet {          // Animal(추상) + Pet(인터페이스)
    private String name;
    public Cat() { this(""); }
    public Cat(String name) { super(4); this.name = name; }   // 다리 4개

    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }
    @Override public void play() { System.out.println("고양이는 장화를 신고 놀아요!"); }
    @Override public void eat()  { System.out.println("고양이는 생선을 먹어요!"); }
}
```
```java
// src/workshop/animal/control/AnimalTest.java  (요약)
Cat    cat1 = new Cat();     // Cat 타입      → getName/setName/play/eat/walk 다 가능
Pet    cat2 = new Cat();     // Pet 타입      → getName/setName/play 만
Animal cat3 = new Cat();     // Animal 타입   → eat/walk 만

cat1.setName("톰"); cat1.play(); cat1.eat(); cat1.walk();
cat2.setName("미요"); cat2.play();
cat3.eat(); cat3.walk();
```

### 6.3 다형성 배열 + 타입별 분기 (실습에서 쓰는 형태)

```java
// src/mylab/book/control/ManageBook.java  (요약)
Publication[] publications = {
    new Magazine("마이크로소프트", "2007-10-01", 328, 9900, "매월"),
    new Novel("남한산성", "2007-04-14", 383, 11000, "김훈", "대하소설"),
    new ReferenceBook("실용주의프로그래머", "2007-01-14", 496, 25000, "소프트웨어공학"),
};

for (int i = 0; i < publications.length; i++) {
    System.out.println((i + 1) + ". " + publications[i]);   // 각자의 toString()
}

/** 실제 타입에 따라 다른 할인율 */
public static void modifyPrice(Publication publication) {
    int cur = publication.getPrice();
    if (publication instanceof Magazine)           publication.setPrice((int) (cur * 0.6));
    else if (publication instanceof Novel)         publication.setPrice((int) (cur * 0.8));
    else if (publication instanceof ReferenceBook) publication.setPrice((int) (cur * 0.9));
}
```

**응용 (더 나은 방식)** — `instanceof` 사슬 대신, 부모에 추상 메서드를 두고 각 자식이 답하게 한다.

```java
// Publication 에 추가
public abstract double getDiscountRate();

// Magazine
@Override public double getDiscountRate() { return 0.6; }
// Novel
@Override public double getDiscountRate() { return 0.8; }
// ReferenceBook
@Override public double getDiscountRate() { return 0.9; }

// 그러면 modifyPrice 는 분기가 사라진다
public static void modifyPrice(Publication p) {
    p.setPrice((int) (p.getPrice() * p.getDiscountRate()));
}
```

### 6.4 다형적 아규먼트 (Polymorphic Argument)

```java
// src/workshop/book/control/ManageBook.java  (요약)
// modifyPrice(Magazine), modifyPrice(Novel), modifyPrice(ReferenceBook) 를
// 따로 만들 필요 없이, 부모 타입 하나로 전부 받는다.
public static void modifyPrice(Publication pub) {
    double rate = 0.0;
    if (pub instanceof Magazine)      rate = 0.6;
    if (pub instanceof Novel)         rate = 0.8;
    if (pub instanceof ReferenceBook) rate = 0.9;
    pub.setPrice((int) (pub.getPrice() * rate));
}

// 하위 클래스 전용 정보 출력도 부모 타입 하나로
public static void printSubInfo(Publication pub) {
    if (pub instanceof Magazine)      System.out.println(((Magazine) pub).getPublishingPeriod());
    else if (pub instanceof Novel)    System.out.println(((Novel) pub).getAuthor());
    else if (pub instanceof ReferenceBook) System.out.println(((ReferenceBook) pub).getField());
}
```

---

## 7. Part 5 — 예외 처리

### 7.1 사용자 정의 예외 만들기

```java
// src/basic/step08_exception/InvalidAgeException.java
public class InvalidAgeException extends Exception {          // Exception 상속 → Checked
    private static final long serialVersionUID = 1L;
    public InvalidAgeException(String message) { super(message); }   // 메시지를 부모에게
}
```

- `extends Exception` → **Checked**. 호출한 쪽이 `try-catch` 또는 `throws` 를 **강제**당한다.
- `extends RuntimeException` → **Unchecked** (`IllegalArgumentException` 등). 강제되지 않는다. 검증 실패를 즉시 알릴 때.

**메시지 + 데이터를 함께 담기**

```java
// src/workshop/account/exception/InsufficientBalanceException.java
public class InsufficientBalanceException extends Exception {
    private int currentBalance;                              // 현재 잔액까지 전달

    public InsufficientBalanceException(String errorMessage, int currentBalance) {
        super(errorMessage);
        this.currentBalance = currentBalance;
    }
    public int getCurrentBalance() { return currentBalance; }
}
```

**예외 계층으로 묶기**

```java
// src/mylab/bank/exception/WithdrawalLimitExceededException.java
// 상위 타입 하나로 catch 하기 위해 InsufficientBalanceException 을 상속
public class WithdrawalLimitExceededException extends InsufficientBalanceException {
    public WithdrawalLimitExceededException(String message) { super(message); }
}
```

### 7.2 던지는 쪽 / 위임하는 쪽

```java
// 던진다 — src/mylab/student/entity/Student.java
public void setGrade(int grade) throws InvalidGradeException {
    if (grade < 1 || grade > 4) {
        throw new InvalidGradeException("학년은 1~4 사이의 값이어야 합니다.");
    }
    this.grade = grade;
}
```
```java
// 위임한다 — src/mylab/bank/entity/Bank.java
// 스스로 처리하지 않고 호출자에게 넘긴다
public void withdraw(String accountNumber, double amount)
        throws AccountNotFoundException, InsufficientBalanceException {
    findAccount(accountNumber).withdraw(amount);
}

public void transfer(String from, String to, double amount)
        throws AccountNotFoundException, InsufficientBalanceException {
    Account src = findAccount(from);
    Account dst = findAccount(to);
    src.withdraw(amount);        // 여기서 예외가 나면 dst.deposit 은 실행되지 않는다
    dst.deposit(amount);
}
```

### 7.3 잡는 쪽

```java
// src/mylab/bank/control/BankDemo.java  (요약)
try {
    bank.deposit("AC1000", 5000);
    bank.withdraw("AC1001", 3000);
    hong.applyInterest();
    bank.transfer("AC1002", "AC1001", 5000);
} catch (AccountNotFoundException | InsufficientBalanceException e) {   // 멀티 catch
    System.out.println("예외 발생: " + e.getMessage());
}

// 케이스별로 나눠 잡기
try {
    bank.withdraw("AC1001", 6000);          // 출금 한도 초과 → WithdrawalLimitExceededException
} catch (AccountNotFoundException | InsufficientBalanceException e) {
    System.out.println("예외 발생: " + e.getMessage());
}
```

```java
// src/basic/step08_exception/ExceptionDemo.java  (요약) — 다중 catch 순서 규칙
try {
    System.out.println(Integer.parseInt(data[i]));
} catch (NumberFormatException e) {           // 자식 예외 먼저
    System.out.println("숫자 형식 아님");
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("배열 범위 벗어남");
} catch (Exception e) {                       // 부모(Exception) 는 맨 마지막
    System.out.println("그 밖의 예외: " + e);
} finally {
    System.out.println("항상 실행 (자원 정리)");   // 예외 발생 여부와 무관
}
```

### 7.4 검색 실패: `null` vs 예외 — 저장소 안에서 둘 다 등장

```java
// 방식 A — 예외 (Bank, MemberManager)
public Account findAccount(String accountNumber) throws AccountNotFoundException {
    for (Account a : accounts) if (a.getAccountNumber().equals(accountNumber)) return a;
    throw new AccountNotFoundException(accountNumber + " 계좌를 찾을 수 없습니다.");
}
// 호출부: try-catch 를 강제당함 → 검사를 빠뜨릴 수 없다

// 방식 B — null (Library)
public Book findBookByTitle(String title) {
    for (Book b : books) if (b.getTitle().equals(title)) return b;
    return null;                               // 호출부가 null 체크를 잊으면 NPE
}
```

---

## 8. Part 5 — 컬렉션

### 8.1 배열 → List

```java
// src/basic/step09_collection/ListDemo.java  (요약)
List<String> names = new ArrayList<>();   // 왼쪽=인터페이스, 오른쪽=구현체
names.add("이순호");
names.add(1, "정민서");                    // 위치 지정 삽입

names.size();            // 배열의 length 대신
names.get(0);            // 배열의 [0] 대신
names.contains("김하늘");
names.set(0, "이순신");
names.remove("박영선");   // 값으로 삭제
names.remove(0);          // 인덱스로 삭제

// 순회 중 안전하게 삭제하려면 Iterator (향상된 for 안에서 remove 하면 예외)
Iterator<String> it = names.iterator();
while (it.hasNext()) {
    if (it.next().startsWith("정")) it.remove();
}

// 숫자는 int 가 아니라 Integer (오토박싱/언박싱)
List<Integer> prices = new ArrayList<>();
prices.add(9900);                         // int → Integer 자동
int total = 0;
for (int p : prices) total += p;          // Integer → int 자동
```

**응용 — 같은 기능을 배열 / List 로 각각** (`workshop.person`)

```java
// 배열판 — src/workshop/person/control/PersonManager.java
PersonEntity[] persons = new PersonEntity[10];    // 크기 고정
persons[0] = new PersonEntity("이성호", "7212121028102", "인천 계양구", "032-392-2932");
// ... 정확히 10개를 채워야 함. 하나라도 비면 순회 시 NPE

// List판 — src/workshop/person/control/PersonManagerList.java
List<PersonEntity> personList = new ArrayList<>();   // 크기 자동
personList.add(new PersonEntity("이성호", "7212121028102", "인천 계양구", "032-392-2932"));
// 몇 개든 상관없음
```

### 8.2 Map / Set + 핵심 패턴

```java
// src/basic/step09_collection/MapDemo.java  (요약)
Map<String, Integer> stock = new HashMap<>();
stock.put("소설", 5);
stock.put("소설", 10);                        // 같은 키 → 덮어씀
stock.get("만화");                            // null
stock.getOrDefault("만화", 0);                // 0

// 순회 2가지
for (String key : stock.keySet()) System.out.println(key + " → " + stock.get(key));
for (Map.Entry<String, Integer> e : stock.entrySet()) {   // 더 효율적
    System.out.println(e.getKey() + " = " + e.getValue());
}

// ★ 개수 세기
String[] types = { "소설", "잡지", "소설", "참고서", "소설" };
Map<String, Integer> count = new HashMap<>();
for (String t : types) count.put(t, count.getOrDefault(t, 0) + 1);

// ★ 합계 → 평균  (합계 Map + 개수 Map)
Map<String, Double> totalByType = new HashMap<>();
Map<String, Integer> cntByType  = new HashMap<>();
for (int i = 0; i < names.length; i++) {
    totalByType.put(names[i], totalByType.getOrDefault(names[i], 0.0) + prices[i]);
    cntByType.put(names[i],  cntByType.getOrDefault(names[i], 0) + 1);
}
Map<String, Double> avg = new HashMap<>();
for (String t : totalByType.keySet()) avg.put(t, totalByType.get(t) / cntByType.get(t));

// Set — 중복 불가
Set<String> unique = new HashSet<>();
for (String t : types) unique.add(t);        // {소설, 잡지, 참고서}
```

**응용 — 실습의 통계 클래스가 이 패턴 그대로다.**

```java
// src/mylab/book/control/StatisticsAnalyzer.java  (요약)
public Map<String, Double> calculateAveragePriceByType(Publication[] publications) {
    Map<String, Double>  totalByType = new HashMap<>();
    Map<String, Integer> countByType = new HashMap<>();
    for (Publication pub : publications) {
        String type = getPublicationType(pub);                       // "소설"/"잡지"/"참고서"
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

---

## 9. Part 6 — 3계층 종합 실습 (mylab / workshop)

각 실습의 진입점(`main`)과 학습 포인트.

| 실습 | 진입점 | 학습 포인트 |
|---|---|---|
| **bank** | [`BankDemo`](src/mylab/bank/control/BankDemo.java) | 추상 클래스 `Account` + 자식 2종(이자 / 한도) + 예외 3종 + 계좌 이체 |
| **book** | [`ManageBook`](src/mylab/book/control/ManageBook.java) · [`ShoppingCart`](src/mylab/book/control/ShoppingCart.java) | 다형성 배열 + `instanceof` 분기 + 통계 + 장바구니 |
| **library** | [`LibraryManagementSystem`](src/mylab/library/control/LibraryManagementSystem.java) | 상태를 가진 entity(`isAvailable`) — 대출/반납이 상태를 바꿈 |
| **student** | [`StudentTest`](src/mylab/student/control/StudentTest.java) | 최소 규모. setter 검증 + 사용자 정의 예외 |
| **account** | [`AccountTest`](src/workshop/account/control/AccountTest.java) | 생성자 오버로딩 + 데이터를 담은 예외 |
| **animal** | [`AnimalTest`](src/workshop/animal/control/AnimalTest.java) | 추상 클래스 + 인터페이스 동시 구현 |
| **person** | [`PersonManager`](src/workshop/person/control/PersonManager.java) · [`PersonManagerList`](src/workshop/person/control/PersonManagerList.java) | 배열 vs List |

### 예: library — 상태를 가진 entity

```java
// src/mylab/library/entity/Book.java  (요약)
public class Book {
    private String title, author, isbn;
    private int publishYear;
    private boolean isAvailable;

    public Book(String title, String author, String isbn, int publishYear) {
        this.title = title; this.author = author; this.isbn = isbn;
        this.publishYear = publishYear;
        this.isAvailable = true;                  // 생성 시 대출 가능
    }

    /** 대출 — 가능하면 상태를 바꾸고 true, 이미 대출 중이면 false */
    public boolean checkOut() {
        if (isAvailable) { isAvailable = false; return true; }
        return false;
    }
    public boolean returnBook() { isAvailable = true; return true; }
}
```
```java
// src/mylab/library/entity/Library.java  (요약) — control 역할
public class Library {
    private List<Book> books = new ArrayList<>();

    public Book findBookByISBN(String isbn) {
        for (Book b : books) if (b.getIsbn().equals(isbn)) return b;
        return null;
    }
    public boolean checkOutBook(String isbn) {
        Book b = findBookByISBN(isbn);
        return b != null && b.checkOut();          // 검색 + 상태 변경 위임
    }
    /** 조건에 맞는 것만 새 List 로 (findOver 패턴) */
    public List<Book> getAvailableBooks() {
        List<Book> result = new ArrayList<>();
        for (Book b : books) if (b.isAvailable()) result.add(b);
        return result;
    }
    public List<Book> getAllBooks() { return new ArrayList<>(books); }   // 방어적 복사
}
```
```java
// src/mylab/library/control/LibraryManagementSystem.java  (요약)
Library library = new Library("중앙 도서관");
library.addBook(new Book("자바의 정석", "남궁성", "978-89-01-14077-4", 2019));

String isbn = "978-89-01-14077-4";
if (library.checkOutBook(isbn)) System.out.println("대출 성공");
if (library.returnBook(isbn))   System.out.println("반납 성공");

for (Book b : library.getAvailableBooks()) System.out.println(b);
```

---

## 10. 자주 쓰는 패턴 모음

빠르게 다시 볼 수 있게 최소 형태로.

```java
// (1) 생성자 오버로딩 + this() 위임 — 인자 적은 쪽이 많은 쪽을 부른다
public Product(String name, int price) { this(name, price, 0); }

// (2) 상속 계층 생성자 연결 — 부모 필드는 super(), 내 필드만 여기서
public Magazine(String t, String d, int pg, int pr, String period) {
    super(t, d, pg, pr);
    this.publishPeriod = period;
}

// (3) 오버라이딩 + super 로 부모 동작 재사용
@Override public void withdraw(double amt) throws InsufficientBalanceException {
    if (amt > withdrawalLimit) throw new WithdrawalLimitExceededException("한도 초과");
    super.withdraw(amt);
}

// (4) 검색 실패 → 예외 (null 반환하지 않기), 문자열 비교는 equals()
public Member findById(String id) throws MemberNotFoundException {
    for (Member m : members) if (m.getId().equals(id)) return m;
    throw new MemberNotFoundException(id + " 없음");
}

// (5) Map 으로 개수 세기 / 평균
count.put(key, count.getOrDefault(key, 0) + 1);
average.put(type, totalByType.get(type) / countByType.get(type));

// (6) 다형성 배열 + 타입별 분기
for (Publication p : pubs) {
    if (p instanceof Magazine) p.setPrice((int)(p.getPrice() * 0.6));
    else if (p instanceof Novel) p.setPrice((int)(p.getPrice() * 0.8));
}

// (7) 조건에 맞는 것만 새 List 로
public static List<Product> findOver(List<Product> src, int price) {
    List<Product> result = new ArrayList<>();
    for (Product p : src) if (p.getPrice() > price) result.add(p);
    return result;
}

// (8) 방어적 복사 — 내부 컬렉션을 그대로 넘기지 않는다
public List<Member> getAllMembers() { return new ArrayList<>(members); }

// (9) boolean getter 는 is~, 자기 데이터 판단은 entity 가
public boolean isAdult() { return age >= 19; }

// (10) 향상된 for 안에서 삭제 금지 → Iterator 사용
Iterator<String> it = names.iterator();
while (it.hasNext()) if (it.next().startsWith("정")) it.remove();
```

---

## 11. 심화 학습 포인트

지금 코드로 학습은 충분하지만, "왜 이렇게 했을까 / 어떻게 하면 나을까" 를 고민하기 좋은 지점.

1. **`instanceof` 사슬 → 다형성** — [`mylab.book`](src/mylab/book/control/ManageBook.java) 은 `modifyPrice`, `calculateDiscountedPrice`, `getPublicationType` 세 곳에서 같은 분기를 반복한다. `Publication` 에 `getDiscountRate()` / `getTypeName()` 추상 메서드를 두면 분기가 사라진다. ([6.3](#63-다형성-배열--타입별-분기-실습에서-쓰는-형태) 참고)

2. **캡슐화 일관성** — [`mylab.bank.Account`](src/mylab/bank/entity/Account.java) 는 `setBalance`, `setAccountNumber` 가 `public`. 잔액을 `deposit`/`withdraw` 를 거치지 않고 바꿀 수 있어 한도 검사가 무력화된다.

3. **내부 컬렉션 노출** — [`Bank.getAccounts()`](src/mylab/bank/entity/Bank.java) 는 내부 `List` 를 그대로 반환한다. 같은 저장소의 [`MemberManager`](src/basic/step10_layer/control/MemberManager.java) · [`Library`](src/mylab/library/entity/Library.java) 는 복사본을 반환한다 — 규칙을 통일해볼 것.

4. **검색 실패: `null` vs 예외** — [`Library`](src/mylab/library/entity/Library.java)(null) 와 [`Bank`](src/mylab/bank/entity/Bank.java)(예외) 의 호출부 코드가 어떻게 달라지는지 비교. ([7.4](#74-검색-실패-null-vs-예외--저장소-안에서-둘-다-등장))

5. **입력값 방어** — [`PersonEntity.setSsn()`](src/workshop/person/entity/PersonEntity.java) 은 `ssn.charAt(6)` 앞에 null/길이 검사가 없다.

6. **돈을 `double` 로** — [`mylab.bank`](src/mylab/bank/entity/Account.java) 는 금액이 `double` 이라 소수점 오차 가능. 실무는 `BigDecimal` 또는 정수(원 단위).

7. **예외 계층** — [`WithdrawalLimitExceededException extends InsufficientBalanceException`](src/mylab/bank/exception/WithdrawalLimitExceededException.java) 은 "한도 초과 ≠ 잔액 부족" 인데 상속으로 묶었다. 공통 부모 `BankException` 을 두는 편이 자연스럽다.

8. **테스트를 JUnit 으로** — 현재는 `main` + 눈으로 출력 확인. [`BankDemo`](src/mylab/bank/control/BankDemo.java) 시나리오를 `@Test` 로 옮기면 "예외가 던져지는지" 를 자동 검증할 수 있다.

9. **정리 대상** — `workshop/animal/*` 의 `// TODO Auto-generated method stub`, 오타(`myinteface`, `substract`, `MyCalcurator`).

---

## 12. 실행 방법

Eclipse: `main` 이 있는 클래스에서 **Run As → Java Application** (`Ctrl + F11`).

명령줄:

```bash
# 컴파일
javac -encoding UTF-8 -d out $(find src -name "*.java")

# 실행 (패키지 전체 경로로 클래스 지정)
java -cp out mylab.bank.control.BankDemo
java -cp out basic.step10_layer.control.MemberDemo
java -cp out basic.step07_polymorphism.PolymorphismDemo
```

인자를 받는 예제:

```bash
java -cp out chapter1.first.MyCalcurator 10 3
```

---

## 13. 스스로 점검하는 질문

**기초**
- 기본형과 참조형의 차이는? 문자열 두 개를 `==` 로 비교하면 왜 위험한가?
- `static` 변수와 인스턴스 변수는 각각 누가 공유하는가? static 메서드에서 `this` 를 못 쓰는 이유는?

**객체지향**
- 필드를 `private` 으로 감추고 setter 를 여는 이유를 [`Product.setPrice()`](src/basic/step04_class/Product.java) 로 설명해보라.
- [`chapter5/oop/bad`](src/chapter5/oop/bad/InFlexibleCompanyDemo.java) 와 [`good`](src/chapter5/oop/good/FlexibleCompanyDemo.java) 에서 "임원(Executive) 추가" 요구가 오면 각각 어디를 고쳐야 하나?
- `Employee ref = new Manager(...)` 일 때 `ref.approve(...)` 가 컴파일 오류인 이유와 해결법은?
- 추상 클래스와 인터페이스는 각각 언제 쓰나? [`Cat`](src/workshop/animal/entity/Cat.java) 이 둘 다 쓰는 이유는?
- `equals()` 를 재정의하면 `hashCode()` 도 재정의해야 하는 이유는?

**예외**
- Checked 와 Unchecked 의 차이, 각각 언제 쓰나?
- 다중 `catch` 에서 `Exception` 을 맨 위에 쓰면 무슨 일이 생기나?
- [`Bank.transfer()`](src/mylab/bank/entity/Bank.java) 에서 `src.withdraw()` 가 예외를 던지면 `dst.deposit()` 은 실행되나? 잔액은 어떻게 되나?

**컬렉션 / 설계**
- 배열 대신 `ArrayList` 를 쓰면 좋은 점은? ([`PersonManager`](src/workshop/person/control/PersonManager.java) vs [`PersonManagerList`](src/workshop/person/control/PersonManagerList.java))
- `getOrDefault` 없이 개수 세기를 구현하면 코드가 어떻게 지저분해지나?
- `getAllMembers()` 가 복사본을 반환하지 않으면 어떤 사고가 날 수 있나?
- entity 에 넣어도 되는 메서드와 control 로 빼야 하는 메서드의 기준은?
