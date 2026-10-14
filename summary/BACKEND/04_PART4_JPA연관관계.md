# PART 4. 신규 진도 — 계층 분리와 JPA 연관관계 매핑

> **이 파트는 `springboot_update.zip`으로 추가된 진도를 정리한 것이다.**
> PART 3까지가 "단일 엔티티를 다루는 법"이었다면, PART 4는 **엔티티끼리 관계를 맺는 법**과 **Service 계층을 제대로 세우는 법**을 다룬다.
> 강의 문서(BackEnd_Java_Spring.docx)는 이전 버전과 **내용이 동일**하므로, 이 파트는 전적으로 **코드에서 읽어낸 내용**이다.

## 4-0. 무엇이 바뀌었나 — 변경 요약

### 프로젝트별 변동

| 프로젝트 | 변동 | 내용 |
|---|---|---|
| `springboot` | **변동 없음** | Security 실습 그대로 (PART 3 3-9절) |
| `springboot-student` | **변경** | **Service 계층 신설**, `StudentController` 신설, 예외 Advice 전환, Admin 클라이언트 비활성화 |
| **`springboot-student-1to1`** | **신규** | `@OneToOne` 양방향 매핑 (`Student` ↔ `StudentDetail`) |
| **`springboot-student-1toN`** | **신규** | `@ManyToOne` / `@OneToMany` (`Department` ↔ `Student`) + Projection |
| `MySpringBootAdmin` | 변동 없음 | — |
| `SpringBoot-Gradle-App` | 변동 없음 | (4-9절에서 짧게 다룸) |
| `springboothw` | — | 요청대로 제외 |

### 세 프로젝트의 진화 관계 — 이것이 이번 진도의 뼈대다

```
springboot-student            엔티티 1개 (Student)
   │                          Controller → Service → Repository 3계층 완성
   │  ＋ StudentDetail 추가
   ▼
springboot-student-1to1       @OneToOne 양방향
   │                          연관 엔티티를 DTO에 중첩, cascade, fetch join
   │  ＋ Department 추가
   ▼
springboot-student-1toN       @ManyToOne / @OneToMany
                              컬렉션 매핑, N+1 문제, Projection
```

**⚠️ 세 프로젝트는 "같은 앱을 단계별로 키운 것"이다.** PART 2의 `SpringFWXml` / `SpringFWConfig`가 "같은 앱을 설정 방식만 바꾼 것"이었다면, 여기는 **같은 앱에 관계를 하나씩 붙여나간 것**이다. 그래서 **diff로 읽는 것이 가장 효율적**이고, 이 파트도 그 순서로 정리한다.

### 이번 진도에서 새로 등장한 것 전체 목록

| 분류 | 항목 |
|---|---|
| **계층** | `@Service` + `@Transactional(readOnly=true)` 클래스 레벨 전략 |
| **영속성** | **변경 감지(Dirty Checking)** — `save()` 없이 UPDATE |
| **연관관계** | `@OneToOne`, `@ManyToOne`, `@OneToMany`, `mappedBy`, `@JoinColumn` |
| | **연관관계의 주인**, 양방향 매핑, `cascade = CascadeType.ALL` |
| | `FetchType.LAZY` / 지연 로딩 |
| **쿼리** | **JPQL** (`@Query`), `@Param`, **`LEFT JOIN FETCH`** |
| **성능** | **N+1 문제**와 fetch join 해결 |
| **조회 최적화** | **Projection** (`DepartmentSummary` 인터페이스) |
| **DTO** | 중첩 DTO(`Request` 안의 `StudentDetailDTO`), `SimpleResponse` 분리, `@JsonInclude` |
| | `@Valid` **중첩 검증**, `@NotNull` |
| **Lombok** | `@Builder.Default` |
| **Web** | `CorsFilter` + `FilterRegistrationBean` 방식 CORS |
| **Java 8** | `Stream` (`filter`/`map`/`toList`), 메서드 참조 — `StreamTest` 신설 |
| **REST** | `ResponseEntity`로 201/204 반환 |

---

## 4-1. Service 계층의 도입 — 3계층의 완성

### 왜 지금 생겼나

PART 3의 `UserRestController`는 **Repository를 직접 호출**했다. 3-5절에서 "Service 계층이 없다"고 지적했던 그 부분이다. 이번 진도에서 `StudentService`가 생기며 **PART 1·2에서 배운 3계층이 Boot에서도 완성**된다.

```
PART 1   MemberDemo      →  MemberManager    →  List<Member>
PART 2   UserController  →  UserServiceImpl  →  UserDao  →  DB
PART 3   UserRestController ─────────────────→ UserRepository   ⚠️ Service 없음
PART 4   StudentController → StudentService  →  StudentRepository → DB   ✅
```

### 코드 — `service/StudentService.java` (springboot-student)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)                    // ★ 클래스 기본값: 읽기 전용
public class StudentService {

    private final StudentRepository studentRepository;

    // ── 조회 (클래스의 readOnly 적용) ─────────────────
    public List<StudentDTO.Response> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(entity -> StudentDTO.Response.fromEntity(entity))
                .toList();
    }

    public StudentDTO.Response getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "id", id));
        return StudentDTO.Response.fromEntity(student);
    }

    public StudentDTO.Response getStudentByStudentNumber(String studentNumber) {
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "student number", studentNumber));
        return StudentDTO.Response.fromEntity(student);
    }

    // ── 등록 ─────────────────────────────────────────
    @Transactional                                  // ★ 쓰기 트랜잭션으로 재정의
    public StudentDTO.Response createStudent(StudentDTO.Request request) {
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE,
                    request.getStudentNumber());
        }
        Student student = Student.builder()
                .name(request.getName())
                .studentNumber(request.getStudentNumber())
                .build();
        Student savedStudent = studentRepository.save(student);
        return StudentDTO.Response.fromEntity(savedStudent);
    }

    // ── 수정 ─────────────────────────────────────────
    @Transactional
    public StudentDTO.Response updateStudent(Long id, StudentDTO.Request request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "id", id));

        // ★ 학번을 바꾸는 경우에만 중복 검사
        if (!student.getStudentNumber().equals(request.getStudentNumber()) &&
                studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE,
                    request.getStudentNumber());
        }
        student.setName(request.getName());
        student.setStudentNumber(request.getStudentNumber());
        return StudentDTO.Response.fromEntity(student);     // ★★ save() 가 없다!
    }

    // ── 삭제 ─────────────────────────────────────────
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Student", "id", id);
        }
        studentRepository.deleteById(id);
    }
}
```

**해설 — 이 클래스가 담고 있는 5가지**

1. **⚠️ `@Transactional(readOnly = true)` 클래스 레벨 + 메서드 레벨 재정의**
   - 조회 메서드는 별도 어노테이션이 없으므로 **클래스의 `readOnly=true`**를 그대로 쓴다
   - 쓰기 메서드(`create`/`update`/`delete`)만 `@Transactional`로 **덮어쓴다**(기본 `readOnly=false`)
   - 🔗 **PART 2 2-6절의 `SpringFWConfig` `UserServiceImpl`과 완전히 같은 전략**이고, 더 거슬러 올라가면 XML의 `<tx:method name="get*" read-only="true"/>`와 같은 목적이다. **세 파트에서 같은 문제를 세 가지 문법으로 푼 것.**
   - `readOnly=true`의 이득: 하이버네이트가 **스냅샷을 만들지 않아** 메모리·성능이 좋아지고, 실수로 쓰기가 일어나면 막힌다.

2. **⚠️ `updateStudent`에 `save()`가 없는데 UPDATE가 나간다 — 변경 감지(Dirty Checking)**
   - 이것이 이번 진도에서 **가장 중요한 개념**이다. 4-2절에서 따로 다룬다.

3. **검증 로직이 Service에 있다.** `existsByStudentNumber`로 중복을 확인하고 `BusinessException`을 던진다.
   - 🔗 PART 3 3-4절에서 "`existsByStudentNumber`를 만들어놓고 안 쓴다"고 지적했던 메서드가 **여기서 제 역할을 찾았다.**
   - ⚠️ `updateStudent`의 `if (!student.getStudentNumber().equals(request...) && exists(...))` 조건이 정교하다. **학번을 안 바꾸는 경우까지 중복 검사를 하면 자기 자신에 걸려서 항상 실패**한다. 이 `&&` 앞부분이 그걸 막는다. 실무에서 자주 놓치는 버그.

4. **Entity ↔ DTO 변환이 Service의 책임**이다.
   - Controller는 DTO만 보고, Repository는 Entity만 본다. **Service가 경계에서 번역한다.**
   - `.stream().map(...).toList()` — 🔗 부록 B-1에서 Stream을 따로 정리한다.

5. **Service가 `StudentDTO.Response`를 반환한다.** Entity를 그대로 내보내면 3-6절에서 지적한 문제(지연 로딩 오류, 내부 필드 노출)가 생긴다.

### 코드 — `controller/StudentController.java`

```java
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;      // ★ Repository 가 아니라 Service

    @GetMapping
    public ResponseEntity<List<StudentDTO.Response>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO.Response> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/number/{studentNumber}")            // ★ 경로로 구분
    public ResponseEntity<StudentDTO.Response> getStudentByStudentNumber(
            @PathVariable String studentNumber) {
        return ResponseEntity.ok(studentService.getStudentByStudentNumber(studentNumber));
    }

    @PostMapping
    public ResponseEntity<StudentDTO.Response> createStudent(
            @Valid @RequestBody StudentDTO.Request request) {
        StudentDTO.Response createdStudent = studentService.createStudent(request);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);   // ★ 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO.Response> updateStudent(
            @PathVariable Long id, @Valid @RequestBody StudentDTO.Request request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();                         // ★ 204
    }
}
```

**해설 — `UserRestController`(PART 3)와 비교하면 전부 개선되었다**

| 항목 | `UserRestController` (PART 3) | `StudentController` (PART 4) |
|---|---|---|
| 의존 | `UserRepository` 직접 | **`StudentService`** |
| 요청 타입 | `@RequestBody User` (Entity) | **`@Valid @RequestBody StudentDTO.Request`** |
| 응답 타입 | `User` (Entity) | **`StudentDTO.Response`** |
| 등록 응답 | `200 OK` + 객체 | **`201 CREATED`** |
| 삭제 응답 | `200 OK` + 문자열 | **`204 NO CONTENT`** |
| 이메일 조회 | `/{email}/` (끝 슬래시로 구분 ⚠️) | **`/number/{studentNumber}`** (경로로 구분) |
| 비즈니스 로직 | Controller 안 | **Service** |

**⚠️ `/number/{studentNumber}`가 3-5절에서 지적한 설계 문제의 정답이다.**
PART 3에서는 `/{id}`와 `/{email}/`을 **URL 끝 슬래시**로 구분해 매우 취약했다. 여기서는 **경로 세그먼트를 추가**해 명확히 갈랐다. 같은 사람이 쓴 코드가 한 단계 발전한 지점.

**HTTP 상태 코드 3종 정리**

| 코드 | 언제 | 코드 |
|---|---|---|
| `200 OK` | 조회·수정 성공 | `ResponseEntity.ok(body)` |
| `201 CREATED` | **등록 성공** | `new ResponseEntity<>(body, HttpStatus.CREATED)` |
| `204 NO CONTENT` | **삭제 성공(본문 없음)** | `ResponseEntity.noContent().build()` |

🔗 PART 2에서 `Boolean.TRUE`를 반환하던 것 → PART 3 `ResponseEntity.ok("...삭제되었습니다")` → **PART 4 `204 noContent()`**. REST 규약에 맞게 3단계로 발전했다. `ResponseEntity<Void>`는 "본문이 없다"를 타입으로 표현한 것.

---

## 4-2. 영속성 컨텍스트와 변경 감지 — save() 없이 UPDATE가 되는 이유

⚠️ **자료 2.10절이 "영속성 컨텍스트가 Entity를 관리할 때 사용하는 전략" 5가지를 나열만 하고 넘어갔는데, 이번 코드로 그중 핵심이 실제로 동작한다.** PART 3에서 개념만 적었던 부분을 여기서 채운다.

### 개념 — 5대 전략

| 전략 | 내용 |
|---|---|
| **1차 캐시** | 같은 트랜잭션 안에서 같은 id를 두 번 조회하면 **DB에 한 번만** 간다 |
| **동일성 보장** | 그 두 객체는 `==` 비교가 **true**다 |
| **쓰기 지연** | `save()`를 해도 SQL이 즉시 안 나가고 **커밋 시점에 모아서** 나간다 |
| **변경 감지(Dirty Checking)** | **조회한 엔티티의 필드를 바꾸면 커밋 시 자동으로 UPDATE** |
| **지연 로딩(Lazy)** | 연관 엔티티를 **실제로 쓸 때** 조회한다 |

### 코드 — 변경 감지가 일어나는 자리

```java
@Transactional
public StudentDTO.Response updateStudent(Long id, StudentDTO.Request request) {
    Student student = studentRepository.findById(id)     // ① 영속 상태로 조회
            .orElseThrow(...);
    ...
    student.setName(request.getName());                  // ② 필드 변경
    student.setStudentNumber(request.getStudentNumber());
    return StudentDTO.Response.fromEntity(student);       // ③ save() 없이 끝
}                                                        // ④ 메서드 종료 = 커밋
```

**무슨 일이 일어나는가**

```
① findById()        → DB 조회 → 영속성 컨텍스트가 엔티티를 관리 상태로 보관
                      동시에 조회 시점의 "스냅샷"을 떠둔다
② setName()         → 자바 객체의 필드만 바뀐다. SQL 은 아직 안 나간다
③ (save 호출 없음)
④ 트랜잭션 커밋      → flush 발생
                      현재 엔티티 vs 스냅샷 비교 → 달라진 필드 발견
                      → UPDATE students SET name=?, student_number=? WHERE student_id=?
```

**⚠️ 이해해야 할 3가지**

1. **`@Transactional`이 없으면 동작하지 않는다.** 트랜잭션이 끝나는 시점이 없으면 flush도 없다. 조회만 하고 끝난다.
2. **`findById()`로 가져온 엔티티만 영속 상태다.** `new Student()`로 만든 객체는 아무리 필드를 바꿔도 DB와 무관하다(비영속 상태).
3. **`createStudent`에는 `save()`가 있고 `updateStudent`에는 없는 이유**
   - `create` — `Student.builder()...build()`로 **새로 만든 객체**는 비영속 상태. `save()`로 영속성 컨텍스트에 넣어줘야 한다
   - `update` — `findById()`로 가져온 객체는 **이미 영속 상태**. 변경 감지가 알아서 한다

**엔티티 생명주기 4단계**

| 상태 | 뜻 | 예 |
|---|---|---|
| **비영속(new)** | 컨텍스트와 무관 | `new Student()` |
| **영속(managed)** | 컨텍스트가 관리 중 | `findById()` 결과, `save()` 직후 |
| **준영속(detached)** | 관리에서 분리됨 | 트랜잭션 종료 후의 엔티티 |
| **삭제(removed)** | 삭제 예약 | `delete()` 호출 후 |

**⚠️ 그런데 1to1 프로젝트의 `updateStudent`에는 `save()`가 다시 들어간다.**
```java
Student updatedStudent = studentRepository.save(student);      // 1to1 버전
return StudentDTO.Response.fromEntity(updatedStudent);
```
**이건 불필요한 호출이다.** 영속 상태 엔티티에 `save()`를 부르면 JPA가 `merge()`를 수행하는데, 이미 영속이므로 사실상 아무 일도 안 하고 같은 객체를 돌려준다. **동작에는 문제없지만 변경 감지를 이해하지 못했을 때 붙이게 되는 코드**다. `springboot-student`(save 없음)와 `1to1`(save 있음)을 나란히 놓고 보면 둘 다 같은 결과를 낸다는 것을 확인할 수 있다.

**⚠️ 준영속 상태에서의 함정 — 실무에서 가장 많이 터지는 것**
```java
@Transactional(readOnly = true)
public StudentDTO.Response getStudentById(Long id) {
    Student student = studentRepository.findById(id).orElseThrow(...);
    return StudentDTO.Response.fromEntity(student);     // ★ 여기서 DTO 로 변환하는 이유
}
```
만약 Entity를 그대로 Controller에 반환하면, **트랜잭션이 이미 끝나 준영속 상태**가 된다. 그 상태에서 지연 로딩된 연관 엔티티(`student.getStudentDetail()`)에 접근하면 **`LazyInitializationException`**이 터진다.
〔v1.3 정정〕 이것은 **`spring.jpa.open-in-view=false`일 때** 문자 그대로 성립한다. Spring Boot 기본값(`true`, OSIV)에서는 요청이 끝날 때까지 영속성 컨텍스트가 열려 있어 컨트롤러에서도 지연 로딩이 **예외 없이 되고**, 대신 보이지 않는 쿼리가 나간다. 어느 쪽이든 "서비스 안에서 DTO로 변환"이 정답이라는 결론은 같다 — 5-10절.
→ **Service 안(트랜잭션 안)에서 DTO로 변환해 내보내는 것이 정답**이고, 이 코드가 정확히 그렇게 하고 있다. 🔗 3-6절 DTO의 존재 이유가 하나 더 늘었다.

**⚠️ 그런데 함정이 하나 더 있다 — 트랜잭션 "안"에서 Entity를 반환해도 안전하지 않을 수 있다.**
`getStudentDetail()`을 아예 호출하지 않고 Entity를 그대로 `@RestController`가 리턴하면, 이번엔 `LazyInitializationException`이 아니라 **Jackson 직렬화 단계에서 다른 예외**가 난다.
```
Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor]
```
LAZY 필드에 들어있는 것은 실제 `StudentDetail` 객체가 아니라 **하이버네이트가 만든 프록시(가짜 객체)**다. Jackson은 이 프록시의 내부 구조(`ByteBuddyInterceptor`)를 어떻게 JSON으로 바꿔야 할지 몰라서 실패한다. **이 예외는 트랜잭션 안에서 일어나도 막을 수 없다** — Jackson의 직렬화는 트랜잭션과 무관한 별개 단계이기 때문이다.

- **정석 해법**: 지금 이 코드처럼 **DTO로 변환**하는 것. DTO는 프록시가 아니라 순수 값만 담으므로 문제가 안 생긴다.
- **대안**(DTO를 안 쓸 경우): `jackson-datatype-hibernate6` 라이브러리를 추가하고 `Hibernate6Module`을 Bean으로 등록하면, 초기화 안 된 프록시 필드를 **자동으로 `null` 처리**해서 직렬화 오류를 피할 수 있다. 다만 이건 "오류는 안 나지만 데이터가 조용히 빠지는" 방식이라, **DTO 변환이 더 안전하고 명시적인 해법**이다.

즉 LAZY 연관관계를 Controller까지 들고 나가면 **두 가지 서로 다른 예외가 기다리고 있다** — 연관 엔티티의 값 자체를 건드리면 `LazyInitializationException`(트랜잭션 문제), 건드리지 않고 그대로 반환해도 Jackson이 프록시를 만나면 `ByteBuddyInterceptor` 오류(직렬화 문제). **DTO 변환은 이 둘을 한 번에 막아준다.**


---

## 4-3. @OneToOne — 1:1 양방향 매핑

### 개념 — 연관관계 매핑의 3대 개념

⚠️ **자료에 없는 내용이지만, 이번 진도의 핵심이자 JPA에서 가장 많이 틀리는 부분이다.**

**① 방향(Direction)**
- **단방향** — 한쪽만 상대를 필드로 가진다
- **양방향** — 양쪽이 서로를 필드로 가진다. (DB에는 방향 개념이 없다. **객체에만 있는 개념**이다)

**② 연관관계의 주인(Owner) — 가장 중요**
- DB에서 두 테이블을 잇는 것은 **외래 키(FK) 하나**뿐이다
- 양방향이면 객체는 둘인데 FK는 하나 → **누가 FK를 관리할지 정해야 한다**
- **FK를 가진 쪽(= `@JoinColumn`을 쓴 쪽)이 주인**
- 주인이 아닌 쪽은 **`mappedBy`로 "나는 주인이 아니고, 저쪽 필드가 주인이다"라고 선언**한다
- ⚠️ **주인이 아닌 쪽에 값을 넣어도 DB에 반영되지 않는다.** 읽기 전용이다

**③ 조회 시점(Fetch)**
- `FetchType.EAGER` — 엔티티를 조회할 때 **연관 엔티티도 즉시 함께** 조회
- `FetchType.LAZY` — 실제로 **쓸 때** 조회 (프록시 객체를 넣어둔다)
- **실무 원칙: 모든 연관관계를 LAZY로.** EAGER는 예측 불가능한 쿼리를 만든다

### 코드 — `Student` ↔ `StudentDetail` (1to1 프로젝트)

```java
// entity/Student.java — 주인이 아닌 쪽
@Entity
@Table(name = "students")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String studentNumber;

    @OneToOne(fetch = FetchType.LAZY,
              mappedBy = "student",            // ★ StudentDetail.student 가 주인
              cascade = CascadeType.ALL)       // ★ 영속성 전이
    private StudentDetail studentDetail;
}
```

```java
// entity/StudentDetail.java — 연관관계의 주인
@Entity
@Table(name = "student_details")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
public class StudentDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_detail_id")
    private Long id;

    @Column
    private String address;                    // 선택 입력

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private LocalDate dateOfBirth;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", unique = true)   // ★ FK 를 가진다 = 주인
    private Student student;
}
```

**생성되는 테이블**
```sql
students          (student_id PK, name, student_number UNIQUE)
student_details   (student_detail_id PK, address, phone_number UNIQUE,
                   email UNIQUE, date_of_birth,
                   student_id FK UNIQUE → students.student_id)   ← FK 는 여기만
```

**해설 — 이 두 클래스가 보여주는 것**

1. **`mappedBy = "student"`의 의미**
   - 문자열 `"student"`는 **테이블 컬럼명이 아니라 `StudentDetail` 클래스의 필드 이름**이다
   - "내(Student.studentDetail)가 아니라 `StudentDetail`의 `student` 필드가 관계를 관리한다"는 선언
   - ⚠️ 필드명을 바꾸면 이 문자열도 바꿔야 한다. 컴파일러가 안 잡아주는 지점

2. **`@JoinColumn(name = "student_id", unique = true)`**
   - `student_details` 테이블에 `student_id` FK 컬럼을 만든다
   - `unique = true`가 **1:1을 보장**한다. 이게 없으면 한 학생에게 상세정보가 여러 개 붙을 수 있어 사실상 1:N이 된다
   - ⚠️ **`@OneToOne` 어노테이션만으로는 DB 제약이 안 걸린다.** `unique = true`가 실제 제약을 만든다

3. **왜 `StudentDetail`이 주인인가**
   - 학생은 상세정보 없이 존재할 수 있지만(선택), 상세정보는 학생 없이 존재할 수 없다(필수)
   - **"더 많이 의존하는 쪽", 즉 FK를 갖는 것이 자연스러운 쪽이 주인**이 된다
   - 🔗 PART 2 `StudentVO`가 `private DeptVO dept;`를 갖고 있던 것과 같은 관계를, 여기서는 **양쪽에서** 표현한다

4. **`cascade = CascadeType.ALL` — 영속성 전이**
   - `Student`를 `save()`하면 `studentDetail`도 **함께 저장**된다
   - `Student`를 `delete()`하면 `studentDetail`도 **함께 삭제**된다
   - 4-4절에서 실제 동작을 본다

5. **양쪽 다 `LAZY`**
   - ⚠️ **`@OneToOne`에서 주인이 아닌 쪽(`Student.studentDetail`)의 LAZY는 실제로는 잘 동작하지 않는다.**
     `Student`를 조회할 때 하이버네이트는 `student_details`에 해당 행이 있는지 **알 수 없으므로**(FK가 반대편에 있어서), 프록시를 넣을지 null을 넣을지 판단하려면 어차피 조회해야 한다 → **EAGER처럼 동작**한다
   - 이것이 `@OneToOne` 양방향의 알려진 한계다. 그래서 이 프로젝트가 **fetch join으로 명시적으로 가져오는 방식**(4-6절)을 쓰는 것이다

### 코드 — `@ManyToOne` / `@OneToMany` (1toN 프로젝트)

```java
// entity/Department.java — 1(One) 쪽, 주인이 아님
@Entity
@Table(name = "departments")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToMany(
            mappedBy = "department",           // ★ Student.department 가 주인
            fetch = FetchType.LAZY
    )
    @Builder.Default                           // ★ Lombok — 빌더에서도 초기값 유지
    private List<Student> students = new ArrayList<>();
}
```

```java
// entity/Student.java (1toN) — N(Many) 쪽, 연관관계의 주인
@Entity
@Table(name = "students")
public class Student {
    ...
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "student", cascade = CascadeType.ALL)
    private StudentDetail studentDetail;       // 1:1 은 그대로

    @ManyToOne(fetch = FetchType.LAZY)         // ★ 추가
    @JoinColumn(name = "department_id")        // ★ FK 를 가진다 = 주인
    private Department department;
}
```

**생성되는 테이블**
```sql
departments  (department_id PK, name, code UNIQUE)
students     (student_id PK, name, student_number UNIQUE,
              department_id FK → departments.department_id)   ← FK 는 students 쪽
```

**해설 — ⚠️ 1:N에서는 주인이 항상 N쪽이다**

1. **왜 N쪽이 주인인가**
   - DB에서 1:N을 표현하는 방법은 **N쪽 테이블에 FK를 두는 것**밖에 없다
   - 학과 100개, 학생 10000명이면 FK는 `students`에 10000개 있는 것이 자연스럽다. `departments`가 학생 목록을 컬럼으로 들 수는 없다
   - **따라서 `@ManyToOne`이 붙는 쪽이 언제나 주인**이고, `@OneToMany`는 항상 `mappedBy`를 갖는다
   - **이 규칙에는 예외가 없다. 외워도 된다.**

2. **⚠️ `@Builder.Default`가 왜 필요한가 — 이번 진도의 숨은 함정**
   ```java
   @Builder.Default
   private List<Student> students = new ArrayList<>();
   ```
   - Lombok `@Builder`는 **필드 초기값을 무시하고 `null`로 채운다.**
   - `@Builder.Default` 없이 `Department.builder().name("CS").code("CS").build()`를 하면 **`students`가 `null`**이 된다
   - 그 상태로 `department.getStudents().size()`를 호출하면 **`NullPointerException`**
   - 실제로 `DepartmentDTO.Response.fromEntity()`가 `department.getStudents().size()`를 호출하므로 **이 어노테이션이 없으면 바로 터진다**
   - 🔗 PART 1 1-7절의 "참조형 배열은 생성해도 각 칸이 null"과 같은 계열의 함정. **컬렉션은 항상 빈 컬렉션으로 초기화**하는 것이 원칙

3. **`@ManyToOne`의 기본 fetch는 `EAGER`다.** 명시적으로 `LAZY`를 준 것이 중요하다.

   | 어노테이션 | 기본 fetch |
   |---|---|
   | `@ManyToOne` | **EAGER** ⚠️ |
   | `@OneToOne` | **EAGER** ⚠️ |
   | `@OneToMany` | LAZY |
   | `@ManyToMany` | LAZY |

   **`@ManyToOne`과 `@OneToOne`은 반드시 `fetch = FetchType.LAZY`를 명시**해야 한다. 이 코드가 둘 다 지키고 있다.

### 연관관계 매핑 총정리

| | 어노테이션 | FK 위치 | 주인 | 반대쪽 |
|---|---|---|---|---|
| **1:1** | `@OneToOne` | 선택 가능 | `@JoinColumn` 쓴 쪽 | `mappedBy` |
| **N:1** | `@ManyToOne` | **항상 N쪽** | **항상 N쪽** | — |
| **1:N** | `@OneToMany` | **항상 N쪽** | — | **항상 `mappedBy`** |
| **N:M** | `@ManyToMany` | 중간 테이블 | 선택 | `mappedBy` |

⚠️ **`@ManyToMany`는 실무에서 쓰지 않는다.** 중간 테이블에 컬럼을 추가할 수 없기 때문에, 중간 엔티티를 만들어 `@ManyToOne` 두 개로 푼다. (🔗 PART 2 `CourseStatusVO`가 `StudentVO`와 `CourseVO`를 둘 다 갖고 있던 것이 정확히 이 패턴이다 — 수강신청이라는 중간 엔티티)

---

## 4-4. cascade · 연관관계 설정 · 중첩 DTO

### 코드 — 연관관계를 양쪽에 걸어주는 코드 (`StudentService.createStudent`, 1to1)

```java
@Transactional
public StudentDTO.Response createStudent(StudentDTO.Request request) {
    // ① 중복 검사 3종
    if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
        throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE, request.getStudentNumber());
    }
    if (hasEmailAndExists(request.getDetailRequest())) {
        throw new BusinessException(ErrorCode.EMAIL_DUPLICATE, request.getDetailRequest().getEmail());
    }
    if (hasDetailAndPhoneNumberExists(request.getDetailRequest())) {
        throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATE, request.getDetailRequest().getPhoneNumber());
    }

    // ② 주 엔티티 생성
    Student student = Student.builder()
            .name(request.getName())
            .studentNumber(request.getStudentNumber())
            .build();

    // ③ 연관 엔티티 생성 + 양방향 연결
    if (request.getDetailRequest() != null) {
        StudentDetail studentDetail = StudentDetail.builder()
                .address(request.getDetailRequest().getAddress())
                .phoneNumber(request.getDetailRequest().getPhoneNumber())
                .email(request.getDetailRequest().getEmail())
                .dateOfBirth(request.getDetailRequest().getDateOfBirth())
                .student(student)              // ★ 주인 쪽에 연결 (FK 세팅됨)
                .build();
        student.setStudentDetail(studentDetail);   // ★ 반대쪽에도 연결
    }

    // ④ 저장 — cascade 덕분에 studentDetail 도 함께 저장된다
    Student savedStudent = studentRepository.save(student);
    return StudentDTO.Response.fromEntity(savedStudent);
}

// ── 중복 검사 헬퍼 (private) ─────────────────────────
private boolean hasEmailAndExists(StudentDTO.StudentDetailDTO detailRequest) {
    return detailRequest != null &&
           detailRequest.getEmail() != null &&
           !detailRequest.getEmail().isEmpty() &&
           studentDetailRepository.existsByEmail(detailRequest.getEmail());
}
private boolean hasDetailAndPhoneNumberExists(StudentDTO.StudentDetailDTO detailRequest) {
    return detailRequest != null &&
           studentDetailRepository.existsByPhoneNumber(detailRequest.getPhoneNumber());
}
```

**해설 — ⚠️ ③번이 이 코드에서 가장 중요하다**

```java
.student(student)                    // (A) 주인 쪽 설정 — 이것만 DB 에 반영된다
student.setStudentDetail(studentDetail);  // (B) 반대쪽 설정 — 객체 그래프 일관성용
```

- **(A)만 있어도 DB는 정상**이다. FK를 가진 주인 쪽이니까.
- **(B)가 필요한 이유**: 같은 트랜잭션 안에서 `student.getStudentDetail()`을 호출하면, (B)가 없으면 **`null`이 나온다.** 영속성 컨텍스트는 자바 객체를 그대로 들고 있을 뿐, DB를 다시 조회하지 않기 때문.
  - 실제로 바로 아래 `StudentDTO.Response.fromEntity(savedStudent)`가 `student.getStudentDetail()`을 호출한다 → **(B)가 없으면 상세정보가 빠진 응답이 나간다**
- **양방향 연관관계는 항상 양쪽에 값을 넣어야 한다.** 이것을 메서드로 묶은 것을 **연관관계 편의 메서드**라 부른다.
  ```java
  // Student 에 이런 메서드를 두는 것이 정석
  public void addStudentDetail(StudentDetail detail) {
      this.studentDetail = detail;
      detail.setStudent(this);
  }
  ```
  **이 저장소에는 아직 없다. 개선 포인트로 기억해 둘 것.**

**`cascade = CascadeType.ALL`의 효과**
```java
studentRepository.save(student);    // student 만 저장했는데
// → INSERT INTO students ...
// → INSERT INTO student_details ...   ★ 자동으로 함께
```
- cascade가 없으면 `studentDetailRepository.save(studentDetail)`을 **따로 호출**해야 한다
- `CascadeType` 종류: `PERSIST`(저장) / `MERGE`(병합) / `REMOVE`(삭제) / `REFRESH` / `DETACH` / **`ALL`**(전부)
- ⚠️ **cascade는 "소유 관계"가 명확할 때만 쓴다.** `Student`가 사라지면 `StudentDetail`도 의미가 없으므로 적절하다. 반면 `Department`에 cascade를 걸면 **학과를 지울 때 학생이 전부 지워지므로** 절대 안 된다. 실제로 `Department.students`에는 cascade가 **없다.** 올바른 판단이다.
- **`orphanRemoval = true`** — 부모의 컬렉션에서 자식을 빼면 DB에서도 삭제. 이 저장소에는 아직 없다.

**⚠️ 검증 헬퍼 메서드의 null 체크가 비대칭이다**
```java
hasEmailAndExists      → detailRequest != null && email != null && !email.isEmpty() && exists
hasDetailAndPhoneNumberExists → detailRequest != null && exists       ← ★ phoneNumber null 체크 없음
```
`phoneNumber`가 `null`이면 `existsByPhoneNumber(null)`이 호출된다. DTO에 `@NotBlank`가 있어 `@Valid`에서 걸리긴 하지만, **Service만 따로 호출하면 뚫린다.** 이메일 쪽과 같은 수준으로 맞추는 것이 안전하다.

### 코드 — 중첩 DTO (`StudentDTO`, 1to1)

```java
public class StudentDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank(message = "Student name is required")
        @Size(max = 100, message = "Student name cannot exceed 100 characters")
        private String name;

        @NotBlank(message = "Student number is required")
        @Size(max = 20, message = "Student number cannot exceed 20 characters")
        private String studentNumber;

        @Valid                                        // ★★ 중첩 객체까지 검증
        private StudentDetailDTO detailRequest;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentDetailDTO {
        @Size(max = 200, message = "Address cannot exceed 200 characters")
        private String address;                       // 선택 → @NotBlank 없음

        @NotBlank(message = "Phone number is required")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        private String phoneNumber;

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        private LocalDate dateOfBirth;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String studentNumber;
        private StudentDetailResponse detail;

        public static Response fromEntity(Student student) {
            // ★ 연관 엔티티가 없을 수도 있으므로 null 체크
            StudentDetailResponse detailResponse = student.getStudentDetail() != null
                    ? StudentDetailResponse.builder()
                        .id(student.getStudentDetail().getId())
                        .address(student.getStudentDetail().getAddress())
                        .phoneNumber(student.getStudentDetail().getPhoneNumber())
                        .email(student.getStudentDetail().getEmail())
                        .dateOfBirth(student.getStudentDetail().getDateOfBirth())
                        .build()
                    : null;

            return Response.builder()
                    .id(student.getId())
                    .name(student.getName())
                    .studentNumber(student.getStudentNumber())
                    .detail(detailResponse)
                    .build();
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentDetailResponse { ... }
}
```

**해설**

1. **⚠️ `@Valid`가 필드에 붙은 것이 핵심이다.**
   - Controller의 `@Valid @RequestBody StudentDTO.Request`는 **Request의 필드만** 검사한다
   - `detailRequest` 안의 `@NotBlank`, `@Email`까지 검사하려면 **필드에도 `@Valid`를 붙여야 한다**
   - 이것을 **cascaded validation(연쇄 검증)**이라 한다. 붙이지 않으면 전화번호가 비어도 통과한다
   - 🔗 PART 3 3-5절 `@Valid` 설명의 심화판

2. **엔티티의 제약과 DTO의 검증이 대응된다** — 1toN 버전의 주석이 이 대응을 명시한다
   ```java
   //엔티티에서 null 을 허용하는 선택 입력 항목이므로 필수 검증을 하지 않는다
   @Size(max = 200) private String address;             // @Column (nullable 기본 true)

   //엔티티에서 NOT NULL, UNIQUE 이므로 필수 항목이다
   @NotBlank @Size(max=20) private String phoneNumber;  // @Column(nullable=false, unique=true)
   ```
   **엔티티 제약 → DTO 검증 규칙을 1:1로 맞추는 것**이 원칙이다. 어긋나면 검증을 통과한 요청이 DB 제약에 걸려 500 에러가 난다.

3. **⚠️ 1to1 → 1toN에서 email `@Size`가 20 → 100으로 늘었다.**
   ```java
   // 1to1
   @Size(max = 20, message = "Email cannot exceed 20 characters")
   // 1toN
   @Size(max = 100, message = "Email cannot exceed 100 characters")
   ```
   `alice@example.com`은 17자라 통과하지만, 조금만 긴 이메일이면 **20자 제한에 걸려 등록이 실패**한다. **실습 중 발견해서 고친 버그**로 보인다. 이메일은 RFC상 최대 254자이므로 100도 부족할 수 있다.

4. **`Response`가 `Request`와 구조가 다르다** — `Request`는 `detailRequest`, `Response`는 `detail`. 요청과 응답의 스펙을 독립적으로 설계할 수 있다는 것이 DTO의 이점.

5. **`fromEntity`의 null 체크 삼항 연산자** 🔗 PART 1 1-6절. 연관 엔티티가 선택적이면 반드시 필요하다.


---

## 4-5. JPQL과 @Query — 메서드 이름으로 안 되는 쿼리

### 개념

PART 3에서는 **메서드 이름만으로** 쿼리가 만들어졌다(`findByEmail`). 하지만 조인·집계·특정 필드만 조회 같은 것은 이름으로 표현할 수 없다. 이때 **`@Query`로 JPQL을 직접 작성**한다.

**⚠️ JPQL은 SQL이 아니다.**

| | SQL | JPQL |
|---|---|---|
| 대상 | **테이블·컬럼** | **엔티티·필드** |
| 예 | `SELECT * FROM students WHERE student_number = ?` | `SELECT s FROM Student s WHERE s.studentNumber = :studentNumber` |
| 대소문자 | 무관 | **엔티티명·필드명은 자바 그대로** |
| 반환 | 행 | **엔티티 객체** |

JPQL을 쓰면 하이버네이트가 **Dialect에 맞는 SQL로 번역**한다. 🔗 PART 3 3-4절 Dialect가 여기서 쓰인다.

### 코드 — `StudentRepository` (1toN 최종형)

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ① 메서드 이름 + JPQL 을 함께 (fetch join 을 위해)
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.studentDetail WHERE s.studentNumber = :studentNumber")
    Optional<Student> findByStudentNumber(@Param("studentNumber") String studentNumber);

    // ② 메서드 이름만 (JPQL 불필요)
    boolean existsByStudentNumber(String studentNumber);

    // ③ 단건 + 연관 엔티티 함께
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.studentDetail WHERE s.id = :id")
    Optional<Student> findByIdWithStudentDetail(@Param("id") Long id);

    // ④ 전체 + 연관 엔티티 함께
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.studentDetail")
    List<Student> findAllWithStudentDetail();

    // ⑤ 연관 엔티티 2개를 한 번에 (1toN 에서 추가)
    @Query("SELECT s FROM Student s "
         + "LEFT JOIN FETCH s.studentDetail "
         + "LEFT JOIN FETCH s.department "
         + "WHERE s.department.id = :departmentId")
    List<Student> findByDepartmentId(@Param("departmentId") Long departmentId);

    // ⑥ 집계
    @Query("SELECT COUNT(s) FROM Student s WHERE s.department.id = :departmentId")
    Long countByDepartmentId(@Param("departmentId") Long departmentId);
}
```

**해설**

1. **`@Param("studentNumber")`과 `:studentNumber`가 짝을 이룬다.**
   - JPQL의 **이름 기반 파라미터**. `?1` 같은 위치 기반도 되지만 이름 기반이 안전하다
   - ⚠️ 자바 컴파일 옵션 `-parameters`가 켜져 있으면 `@Param` 생략도 가능하지만, **명시하는 것이 안전**하다

2. **`s.department.id`처럼 점으로 타고 들어간다** — 객체 그래프 탐색. SQL이라면 조인을 써야 할 것을 JPQL은 이렇게 표현한다. (〔v1.3 정정〕 `s.department.id`처럼 **식별자(id)까지만** 타고 들어가면 Hibernate는 조인 없이 FK 컬럼 `students.department_id`로 바로 번역한다 — 강의 문서 2-6의 count 쿼리 로그 `where s1_0.department_id=?`, 5-8절 ⑧. `s.department.name`처럼 id가 아닌 필드를 타고 들어가면 그때 **암묵적 조인(INNER JOIN)** 이 생긴다)

3. **⚠️ `findByStudentNumber`가 메서드 이름 규칙에 맞는데도 `@Query`를 붙였다.**
   - 이름만 두면 **`studentDetail`이 함께 조회되지 않는다.** LAZY이므로
     〔v1.3 정정〕 정확히는 mappedBy 쪽 `@OneToOne`이라 LAZY가 듣지 않고 **별도 쿼리로 즉시** 조회되어 총 **2번** 나간다(같은 쿼리로 함께 오지 않을 뿐) — 5-1절 ③, 5-5절 측정표 "`findByStudentNumber()` 2 → 1"
   - `LEFT JOIN FETCH`를 쓰려고 일부러 JPQL을 작성한 것 → **4-6절 N+1 문제의 해법**

4. **`LEFT JOIN` vs `INNER JOIN`**
   - `LEFT JOIN FETCH`를 쓴 이유: **`studentDetail`이 없는 학생도 결과에 포함**시키기 위해
   - `JOIN FETCH`(inner)를 쓰면 상세정보가 없는 학생은 **결과에서 통째로 빠진다**
   - `StudentDetailRepository`의 `findByIdWithStudent`는 `JOIN FETCH`(inner)를 썼는데, 상세정보는 항상 학생을 가지므로 맞다
   ```java
   @Query("SELECT sd FROM StudentDetail sd JOIN FETCH sd.student WHERE sd.id = :id")
   Optional<StudentDetail> findByIdWithStudent(@Param("id") Long id);
   ```
   **선택적 관계는 LEFT, 필수 관계는 INNER.** 이 코드가 정확히 구분해서 쓰고 있다.

---

## 4-6. N+1 문제와 fetch join — 이번 진도의 핵심 성능 개념

⚠️ **자료에 없지만 JPA에서 가장 중요한 성능 이슈이고, 이 저장소의 `@Query`들이 전부 이것 때문에 존재한다.**

### 문제 — N+1이란

학생 8명을 조회하고 각자의 상세정보를 출력한다고 하자. **fetch join 없이** 하면:

```java
List<Student> students = studentRepository.findAll();        // ① SELECT * FROM students
for (Student s : students) {
    System.out.println(s.getStudentDetail().getEmail());      // ② 매번 SELECT ... WHERE student_id=?
}
```

```
쿼리 1회  : SELECT * FROM students                     ← 1
쿼리 8회  : SELECT * FROM student_details WHERE ...    ← N (학생 수만큼)
─────────────────────────────────────────────────
총 9회 = 1 + N        ⚠️ 학생이 1000명이면 1001번
```

**LAZY이기 때문에 생기는 문제다.** 목록을 가져올 때는 상세정보를 안 가져오고, 실제로 쓸 때(`getStudentDetail()`) 하나씩 조회한다.
〔v1.3 정정〕 일반적인 LAZY(`@ManyToOne` 등)는 이 설명대로다. 그러나 이 예시의 `Student.studentDetail`은 **mappedBy 쪽 `@OneToOne`이라 `findAll()`이 학생을 로딩하는 시점에 이미 N번이 나간다** — `getStudentDetail()`을 부르지 않아도 N+1이다(강의 문서 2-4, 5-1절 ③). 쿼리 개수(1+N)는 같다.

⚠️ **EAGER로 바꾸면 해결될까? 아니다.** EAGER여도 `findAll()`은 여전히 학생 목록을 먼저 가져온 뒤 각각에 대해 추가 쿼리를 날린다. **오히려 안 쓸 때도 항상 가져와서 더 나쁘다.**

**⚠️ 실제 로그로 본 4가지 시나리오 (Employee-Department 예제 기준)**

같은 `@ManyToOne` 연관관계를 놓고, 접근 방식에 따라 실제 Hibernate가 찍는 쿼리 수가 이렇게 달라진다.

| 시나리오 | 쿼리 수 |
|---|---|
| `findAll()`만 호출, 연관 필드는 안 건드림 (LAZY) | **1개** |
| `@ManyToOne(fetch = EAGER)` — 항상 함께 가져옴 | **4개** (Employee 1 + Department 3, 안 써도 매번) |
| LAZY인데 각 Employee의 Department를 실제로 읽음 (N+1 발생) | **4개** |
| 위 상태에서 `default_batch_fetch_size=100` 적용 | **2개** (`IN` 절로 묶임) |
| `LEFT JOIN FETCH`로 한 번에 조회 | **1개** |

**직원 1명 + 부서 100개 규모로 커지면 차이가 극적이다.**
```
default_batch_fetch_size 적용 전 :  101개 쿼리   (직원 목록 1 + 부서 조회 100번)
default_batch_fetch_size 적용 후 :    2개 쿼리   (직원 목록 1 + IN(...) 배치 조회 1)
```
**100배 넘게 줄어든다.** N+1이 "이론적으로 안 좋다"가 아니라 **실제 서비스에서 응답 시간을 좌우하는 실질적 문제**라는 것을 이 숫자가 보여준다.

### 해결 — fetch join

```java
@Query("SELECT s FROM Student s LEFT JOIN FETCH s.studentDetail")
List<Student> findAllWithStudentDetail();
```

```sql
-- 실제 나가는 SQL 단 1회
SELECT s.*, sd.*
FROM students s
LEFT OUTER JOIN student_details sd ON s.student_id = sd.student_id
```

**⚠️ `JOIN`과 `JOIN FETCH`의 차이가 핵심이다**

| | 결과 |
|---|---|
| `JOIN` | 조인은 하지만 **연관 엔티티를 영속성 컨텍스트에 채우지 않는다** → 여전히 N+1 |
| **`JOIN FETCH`** | 조인 결과로 **연관 엔티티까지 함께 로딩**한다 → N+1 해결 |

`FETCH` 키워드 하나가 전부다. **빼먹으면 쿼리는 나가는데 N+1이 그대로 남는다.**

### 코드 — Service가 어떤 메서드를 쓰는지 보라

```java
// springboot-student (연관관계 없음)
public List<StudentDTO.Response> getAllStudents() {
    return studentRepository.findAll()                    // 그냥 findAll
            .stream().map(StudentDTO.Response::fromEntity).toList();
}

// springboot-student-1to1 (연관관계 생김)
public List<StudentDTO.Response> getAllStudents() {
    return studentRepository.findAllWithStudentDetail()    // ★ fetch join 버전으로 교체
            .stream().map(StudentDTO.Response::fromEntity).toList();
}

public StudentDTO.Response getStudentById(Long id) {
    Student student = studentRepository.findByIdWithStudentDetail(id)   // ★ 마찬가지
            .orElseThrow(...);
    return StudentDTO.Response.fromEntity(student);
}
```

**해설 — 왜 이렇게 바뀌었나**
- `fromEntity`가 `student.getStudentDetail()`을 호출한다 → **LAZY라면 여기서 추가 쿼리가 터진다**
- 목록 조회면 학생 수만큼 → N+1
- 그래서 **`fromEntity`를 쓰는 모든 조회 메서드를 fetch join 버전으로 바꾼 것**이다
- **DTO 변환 시점에 어떤 필드를 건드리는지가 곧 어떤 fetch join이 필요한지를 결정한다.** 이 대응 관계를 의식하는 것이 JPA를 쓰는 핵심 감각이다

**⚠️ 그런데 `updateStudent`는 아직 `findById`를 쓴다 (1to1)**
```java
Student student = studentRepository.findById(id).orElseThrow(...);   // fetch join 아님
...
StudentDetail studentDetail = student.getStudentDetail();             // ★ 여기서 추가 쿼리
```
단건이라 N+1은 아니지만 **쿼리가 2번 나간다.** (〔v1.3 정정〕 두 번째 쿼리가 나가는 시점은 `getStudentDetail()` 호출이 아니라 **`findById()` 안**이다 — 강의 문서 2-4의 수정 로그, 5-1절 ③) `findByIdWithStudentDetail(id)`로 바꾸면 1번으로 줄어든다. **개선 포인트.**

### ⚠️ fetch join의 제약 3가지

1. **컬렉션(`@OneToMany`) fetch join은 페이징이 불가능하다.**
   - 조인하면 학과 1개가 학생 수만큼 행으로 뻥튀기되므로, DB의 `LIMIT`이 의미가 없어진다
   - 하이버네이트는 **경고를 남기고 전체를 메모리에 올린 뒤 자바에서 페이징**한다 → 데이터가 많으면 OOM
   - 해결: `@BatchSize`, `join fetch` 대신 별도 조회, 또는 Projection(4-7절)

2. **컬렉션 fetch join은 1개만 가능하다.**
   - `LEFT JOIN FETCH d.students` + `LEFT JOIN FETCH d.professors`를 동시에 하면 **카테시안 곱**이 되어 `MultipleBagFetchException`
   - `DepartmentRepository.findByIdWithStudents`가 컬렉션(`d.students`) 1개 + 단건(`s.studentDetail`) 1개만 fetch한 것은 **이 제약을 지킨 것**이다
   ```java
   @Query("SELECT d FROM Department d "
        + "LEFT JOIN FETCH d.students s "        // 컬렉션 — 1개만 가능
        + "LEFT JOIN FETCH s.studentDetail "     // 단건 — 여러 개 가능
        + "WHERE d.id = :id")
   Optional<Department> findByIdWithStudents(@Param("id") Long id);
   ```

3. **컬렉션 fetch join은 중복 행이 생긴다.**
   - 학과 1개 + 학생 5명 → 결과 5행 → `Department` 객체가 5개로 보인다
   - 해결: JPQL에 `SELECT DISTINCT d` 추가 (Hibernate 6부터는 자동 처리)
   - ⚠️ **이 코드에는 `DISTINCT`가 없다.** Spring Boot 3 = Hibernate 6이라 자동 제거되지만(〔v1.3〕 이 저장소는 실제로 Boot 4 = **Hibernate 7.1**이며 동일하게 자동 제거된다 — 5-0절), **Hibernate 5 이하 프로젝트로 옮기면 중복이 나온다.** 알고 있을 것

### ⚠️ 또 하나의 N+1 해법 — `default_batch_fetch_size` (실제로 설정되어 있다)

**`application-prod.properties`에 fetch join과는 별개로, N+1을 겨냥한 설정이 명시적 주석과 함께 켜져 있다.**

```properties
# (1to1)  N+1 Problem 해결하기 위한 설정
# (1toN)  1:N 연관관계에서의 N+1 Problem 해결하기 위한 설정 in( ?,?,? ... )
spring.jpa.properties.hibernate.default_batch_fetch_size=100
```

**해설 — fetch join과 무엇이 다른가**

- fetch join은 **쿼리 작성 시점**에 "이 연관관계는 항상 함께 가져오겠다"고 SQL에 박아버리는 방식이다
- `default_batch_fetch_size`는 **LAZY를 그대로 유지**하면서, 지연 로딩이 실제로 일어날 때 **한 건씩이 아니라 N건씩 묶어서** 가져오는 설정이다

```sql
-- 이 설정이 없을 때 (N+1)
SELECT * FROM student_details WHERE student_id = 1;
SELECT * FROM student_details WHERE student_id = 2;
SELECT * FROM student_details WHERE student_id = 3;
... (학생 수만큼)

-- default_batch_fetch_size=100 이 있을 때
SELECT * FROM student_details WHERE student_id IN (1,2,3,...,100);   -- ★ IN 절로 한번에
```

- 주석의 `in( ?,?,? ... )`이 정확히 이것을 가리킨다. **최초 접근 시 그 엔티티 하나만이 아니라, 같은 영속성 컨텍스트 안의 "미초기화 프록시들"을 최대 100개까지 모아 `IN` 절 하나로 조회**한다
- ⚠️ **fetch join과 경쟁 관계가 아니라 상호 보완 관계다.**
  - `findAllWithStudentDetail()`처럼 **미리 fetch join을 걸어둔 경로**는 이 설정과 무관하게 이미 1쿼리다
  - 반면 **fetch join을 깜빡했거나, 애초에 어디서 지연 로딩이 터질지 예측하기 어려운 경로**(예: `updateStudent`의 `findById()` 이후 `getStudentDetail()` 호출, 4-6절에서 "개선 포인트"로 남겨둔 그 지점)에서는 **이 설정이 안전망 역할**을 한다
    〔v1.3 정정〕 단 방금 예로 든 `Student.studentDetail`은 mappedBy 쪽 `@OneToOne`이라 **배치가 적용되지 않는 구간**이다(강의 문서 2-5 실측: batch size=100인데도 상세정보 3건이 개별 쿼리 3번). 이 설정이 실제로 안전망이 되는 것은 **`@ManyToOne` 프록시와 `@OneToMany` 컬렉션**이다 — 5-5절 표
  - 즉 **"fetch join으로 놓친 자리를 batch size가 뒤에서 받쳐준다"** — 두 겹 방어

⚠️ **값 `100`은 임의의 숫자가 아니라 DB의 `IN` 절 파라미터 개수 제한, 그리고 "한 화면에 몇 건을 보여줄 것인가"를 함께 고려해 정하는 값이다.** 너무 작으면 여전히 쿼리가 여러 번 나가고(예: 250건이면 100+100+50, 3번), 너무 크면 `IN` 절 자체가 무거워진다. **100은 실무에서 흔히 쓰는 기본값**이다.

**왜 1toN에서 주석이 "1:N 연관관계에서의"로 더 구체화되었나** — `Department.students`(`@OneToMany`) 컬렉션이 추가되면서, 4-6절 제약 1번("컬렉션 fetch join은 페이징이 안 된다")의 대안으로 이 설정의 역할이 더 커졌기 때문으로 보인다. 컬렉션은 fetch join 대신 LAZY로 두고 `default_batch_fetch_size`로 N+1을 완화하는 것이 **페이징이 필요한 컬렉션 연관관계의 실무 표준 해법**이다.

---

## 4-7. Projection — 필요한 필드만 조회하기

⚠️ **자료에 없는 내용. 1toN 프로젝트에서 새로 등장했다.**

### 개념

엔티티 전체가 아니라 **필요한 컬럼만** 가져오는 기법. Spring Data JPA는 **인터페이스만 선언하면** 자동으로 채워준다.

**왜 필요한가**: 학과 목록 화면에 "학과명 + 학생 수"만 필요한데, 엔티티를 가져오면 **학생 전체를 다 로딩**해야 한다. 학생 10000명이면 재앙.

### 코드 — `repository/DepartmentSummary.java`

```java
public interface DepartmentSummary {          // ★ 구현체 없음. getter 만
    Long getId();
    String getName();
    String getCode();
    Long getStudentCount();                   // ★ 엔티티에 없는 계산 필드
}
```

```java
// repository/DepartmentRepository.java
@Query("SELECT d.id AS id, d.name AS name, d.code AS code, COUNT(s) AS studentCount "
     + "FROM Department d LEFT JOIN d.students s "
     + "GROUP BY d.id, d.name, d.code ORDER BY d.id")
List<DepartmentSummary> findAllSummaries();
```

**해설 — ⚠️ 이 조합이 동작하는 원리**

1. **`AS 별칭`과 인터페이스의 getter 이름이 일치해야 한다.**
   ```
   d.id    AS id           →  getId()
   d.name  AS name         →  getName()
   COUNT(s) AS studentCount →  getStudentCount()
   ```
   **별칭을 빼먹거나 이름이 다르면 `null`이 들어간다.** 컴파일러가 안 잡아준다.

2. **Spring Data JPA가 런타임에 프록시 구현체를 만든다.** 🔗 PART 2 2-4절 MyBatis Mapper 인터페이스, 3-10절 프록시 목록에 하나가 더 추가된 셈이다.

3. **`COUNT(s)`는 엔티티에 없는 값이다.** DB에서 집계해서 가져오므로 **학생 엔티티를 하나도 로딩하지 않는다.**
   ```sql
   SELECT d.department_id, d.name, d.code, COUNT(s.student_id)
   FROM departments d LEFT JOIN students s ON s.department_id = d.department_id
   GROUP BY d.department_id, d.name, d.code ORDER BY d.department_id
   ```
   **쿼리 1번, 행 4개(학과 수).** 엔티티 방식이었다면 학생 전체를 메모리에 올렸을 것이다.

4. **`LEFT JOIN`이라 학생이 0명인 학과도 `studentCount=0`으로 나온다.** `INNER JOIN`이면 빈 학과가 목록에서 사라진다. 🔗 4-5절의 LEFT/INNER 판단 기준.

### 코드 — DTO와의 연결 (`DepartmentDTO`)

```java
public class DepartmentDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank(message = "Department name is required")
        @Size(max = 100) private String name;
        @NotBlank(message = "Department code is required")
        @Size(max = 10)  private String code;
    }

    // ── 상세 응답 : 학생 목록까지 ────────────────────
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String code;
        private Long studentCount;
        private List<StudentDTO.SimpleResponse> students;    // ★ 학생 요약 목록

        public static Response fromEntity(Department department) {
            return Response.builder()
                    .id(department.getId())
                    .name(department.getName())
                    .code(department.getCode())
                    .studentCount((long) department.getStudents().size())
                    .students(department.getStudents().stream()
                            .map(StudentDTO.SimpleResponse::fromEntity)
                            .toList())
                    .build();
        }
    }

    // ── 목록/중첩용 요약 응답 ──────────────────────
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)              // ★ null 필드는 JSON 에서 생략
    public static class SimpleResponse {
        private Long id;
        private String name;
        private String code;
        private Long studentCount;

        public static SimpleResponse fromEntity(Department department) {
            return SimpleResponse.builder()
                    .id(department.getId()).name(department.getName())
                    .code(department.getCode())
                    .build();                                // ★ studentCount 안 채움 → null
        }

        public static SimpleResponse fromSummary(DepartmentSummary summary) {
            return SimpleResponse.builder()
                    .id(summary.getId()).name(summary.getName())
                    .code(summary.getCode())
                    .studentCount(summary.getStudentCount())  // ★ 여기선 채움
                    .build();
        }
    }
}
```

**해설 — ⚠️ 무한 순환 참조를 피하는 설계가 핵심이다**

1. **`Department.Response`는 `StudentDTO.SimpleResponse`를 담고, `Student.Response`는 `DepartmentDTO.SimpleResponse`를 담는다.**
   ```
   DepartmentDTO.Response  →  List<StudentDTO.SimpleResponse>   (id, name, studentNumber 만)
   StudentDTO.Response     →  DepartmentDTO.SimpleResponse      (id, name, code 만)
   ```
   - 만약 양쪽 다 `Response`(전체)를 담았다면 **`Department → Student → Department → Student → ...` 무한 순환**이 되어 `StackOverflowError` 또는 무한 JSON이 나온다
   - **`SimpleResponse`(연관관계를 담지 않는 얕은 DTO)를 따로 만드는 것이 정석 해법**이다
   - 🔗 양방향 연관관계의 대가를 DTO 레벨에서 치르는 것. 엔티티를 그대로 JSON으로 내보내면 안 되는 결정적 이유 중 하나

2. **`fromEntity`와 `fromSummary` 두 개의 팩토리 메서드**
   - `fromEntity(Department)` — 엔티티에서. `studentCount`를 **안 채운다**(세려면 학생을 로딩해야 하므로)
   - `fromSummary(DepartmentSummary)` — Projection에서. `studentCount`를 **채운다**(DB가 세줬으므로)
   - **같은 DTO를 두 경로로 만들되, 상황에 따라 채워지는 필드가 다르다**

3. **`@JsonInclude(JsonInclude.Include.NON_NULL)`가 그래서 필요하다.**
   - `fromEntity`로 만들면 `studentCount`가 `null`인데, 그대로 직렬화하면 `"studentCount": null`이 나간다
   - 이 어노테이션이 **null 필드를 JSON에서 아예 생략**해준다
   ```json
   // fromSummary 로 만든 경우
   {"id":1, "name":"Computer Science", "code":"CS", "studentCount":3}
   // fromEntity 로 만든 경우 — studentCount 자체가 없다
   {"id":1, "name":"Computer Science", "code":"CS"}
   ```

4. **⚠️ `Department.Response.fromEntity`의 `department.getStudents().size()`는 주의가 필요하다.**
   - `students`가 LAZY이므로, **fetch join 없이 호출하면 이 시점에 학생 전체를 조회**한다
   - 그래서 `findByIdWithStudents`(fetch join)로 가져온 경우에만 안전하다
   - 🔗 4-3절의 `@Builder.Default`가 없으면 여기서 NPE가 난다고 한 그 지점이기도 하다

### ⚠️ 1toN은 아직 미완성이다

```java
// StudentService 에 Department 참조가 0건
// DepartmentService / DepartmentController 는 아직 없음
// 하지만 StudentDTO.Request 에는 이미 departmentId 가 있다
@NotNull(message = "Department ID is required")
private Long departmentId;
```

**현재 상태**: 엔티티·리포지토리·DTO는 다 만들어졌지만, **Service에서 `departmentId`를 실제로 쓰는 코드가 아직 없다.**
→ `createStudent`에서 `departmentRepository.findById(request.getDepartmentId())`로 학과를 찾아 `student.setDepartment(department)`를 해주는 부분이 **다음 진도**일 가능성이 높다.
⚠️ 지금 상태로 POST를 하면 `departmentId`를 필수로 받아놓고 **무시**하므로, 학생의 `department_id`가 `null`로 저장된다. **이 부분이 비면 학생-학과 연결이 안 되므로 다음 수업에서 반드시 채워질 자리다.**

> **〔v1.3 추가〕 → 업데이트된 `springboot-student-1toN`에서 완성되었다.** `createStudent`/`updateStudent`가 `departmentRepository.findById(...)`로 학과를 찾아 연결하고, `DepartmentService`·`DepartmentController`가 신설되었다. 위 예측 중 편의 메서드(`addStudent`)만 **의도적으로 채택되지 않았다**. 자세한 내용은 **5-3·5-4절**.


---

## 4-8. 그 밖의 변경사항

### ① 예외 Advice — ProblemDetail → ErrorObject로 되돌아감

`springboot-student`의 `DefaultExceptionAdvice`에서 **두 핸들러의 주석이 서로 뒤바뀌었다.**

```java
// ── 이전(PART 3 시점) : ProblemDetail 사용 ──
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorObject> handleResourceNotFoundException(...)   ← 주석
    @ExceptionHandler(BusinessException.class)
    protected ProblemDetail handleException(BusinessException e) { ... }        ← 활성

// ── 현재(업데이트 후) : ErrorObject 사용 ──
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(BusinessException ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(ex.getHttpStatus().value());
        errorObject.setMessage(ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, HttpStatusCode.valueOf(ex.getHttpStatus().value()));
    }                                                                            ← 활성
//    @ExceptionHandler(BusinessException.class)
//    protected ProblemDetail handleException(BusinessException e) { ... }       ← 주석
```

**해설**
- ⚠️ **둘 다 활성화하면 `BusinessException`에 핸들러가 2개가 되어 기동 시 오류**가 난다. 그래서 한쪽은 반드시 주석이어야 한다.
- **응답 형태가 달라진다**
  ```json
  // ErrorObject 방식 (현재)
  { "statusCode": 404, "message": "Student not found with id: 5",
    "timestamp": "2026-09-12 14:03:11 금 오후" }

  // ProblemDetail 방식 (이전)
  { "type":"about:blank", "title":"Not Found", "status":404,
    "detail":"Student not found with id: 5",
    "errorCategory":"Generic", "timestamp":"2026-..." }
  ```
- **어느 쪽이 맞나**: 표준(RFC 7807)은 `ProblemDetail`이다. 다만 3-7절에서 지적했듯 이 코드의 `ProblemDetail`은 `setTitle("Not Found")`가 **하드코딩**되어 있어, 404가 아닌 상황(기본값 417 등)에서도 "Not Found"로 나가는 문제가 있었다. **둘을 번갈아 실험하며 응답 형태를 비교해본 흔적**으로 읽는 것이 자연스럽다.
- **`ErrorObject`의 `getTimestamp()` 문제는 여전하다** — getter가 호출될 때마다 "지금"을 만든다(3-7절). 되살렸으니 이 점은 다시 짚어둘 것.

### ② Spring Boot Admin 클라이언트 비활성화

```properties
# 이전
spring.boot.admin.client.url=http://localhost:8090
# 현재
#spring.boot.admin.client.url=http://localhost:8090
```
**해설**: Admin 서버(`MySpringBootAdmin`, 8090)를 안 띄운 상태에서 앱을 실행하면 **등록 실패 로그가 계속 찍힌다.** 그걸 막으려고 꺼둔 것. 🔗 3-8절에서 "두 프로젝트를 동시에 실행해야 한다"고 한 부분의 실제 귀결.

### ③ CORS — 필터 방식으로 전환 (1to1 / 1toN 신규)

**CORS가 왜 필요한가 — SOP와 Ajax부터**

- **SOP(Same Origin Policy, 동일 출처 정책)**: 브라우저는 기본적으로 **자신을 서빙한 출처(scheme+host+port)와 다른 곳으로의 요청**을 막는다. 보안을 위한 브라우저의 기본 동작이다.
- **Ajax(Asynchronous JavaScript and XML)**: 페이지를 새로고침하지 않고 `XMLHttpRequest`(또는 `fetch`)로 서버와 비동기 통신하는 방식. React 같은 SPA가 REST API를 호출할 때 쓰는 방식이 이것이다.
- 프론트(`http://localhost:3000`, React)가 백엔드(`http://localhost:8080`, Spring Boot)로 Ajax 요청을 보내면, **포트가 다르므로 SOP에 걸려 브라우저가 응답을 막는다.**
- **CORS(Cross-Origin Resource Sharing)**는 서버가 "이 출처는 허용한다"고 명시적으로 선언해서 **SOP를 우회(정확히는, 서버 승인 하에 예외를 허용)하는 표준 메커니즘**이다.

**즉 CORS 설정은 브라우저 보안 정책과 실제 프론트-백엔드 분리 배포 사이의 간극을 메우는 것**이다. 🔗 4-0절에서 짚었듯 `app.cors.allowed-origins`에 `localhost:3000`(React 개발 서버로 추정) 등이 들어있는 것은, 이 저장소가 **별도의 프론트엔드 프로젝트와 연동될 준비를 이미 하고 있다는 신호**다.

PART 3에서는 `WebMvcConfigurer.addCorsMappings()` 방식만 있었다. 새 프로젝트에는 **`CorsFilter` 방식**이 추가됐다.

```java
@Configuration
public class CorsConfig {

    private static final long PREFLIGHT_MAX_AGE_SECONDS = 3600L;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;          // ★ 프로퍼티에서 주입

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);              // ★ 쿠키 허용
        configuration.setMaxAge(PREFLIGHT_MAX_AGE_SECONDS);   // ★ preflight 캐시 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean<CorsFilter> bean =
                new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);            // ★ 가장 먼저 실행
        return bean;
    }
}
```

**해설 — 두 방식의 차이**

| | `WebMvcConfigurer.addCorsMappings` (PART 3) | **`CorsFilter` (신규)** |
|---|---|---|
| 동작 위치 | **DispatcherServlet 이후** (MVC 레벨) | **서블릿 필터** (MVC 이전) |
| Security와 조합 | Security 필터가 먼저 막으면 **CORS 헤더 없이 거부** | 필터 순서를 앞으로 빼서 **먼저 처리 가능** |
| 설정 | 간단 | 세밀(credentials, maxAge 등) |

⚠️ **`bean.setOrder(Ordered.HIGHEST_PRECEDENCE)`가 핵심이다.** Spring Security를 쓰면 인증 필터가 CORS preflight(`OPTIONS`) 요청을 **인증 없다며 401로 막아버린다.** 브라우저는 그걸 CORS 오류로 표시한다. CORS 필터를 **가장 먼저** 실행시켜야 이 문제가 풀린다. 🔗 PART 3 3-9절 Security와 함께 쓸 때 반드시 필요한 설정.

**`setAllowCredentials(true)`의 함정**
- 쿠키·인증 헤더를 주고받으려면 필요하다
- ⚠️ 그런데 **`allowedOrigins`에 `"*"`를 쓰면 브라우저가 거부**한다. 반드시 구체적인 origin을 나열해야 한다
- 그래서 `@Value("${app.cors.allowed-origins}")`로 **프로퍼티에서 목록을 주입**받는 것이다. 🔗 PART 3 3-2절 `${DB_HOST:...}` 환경변수 분리와 같은 발상 — 환경마다 다른 값
- `List<String>`으로 주입되므로 프로퍼티는 콤마 구분:
  ```properties
  app.cors.allowed-origins=http://localhost:3000,http://localhost:5173,http://127.0.0.1:5500
  ```
  (실제 설정값. `5500`은 VS Code Live Server 같은 정적 프론트 개발 서버 포트로 보인다 — 별도 프론트엔드 프로젝트와 연동할 준비가 되어 있다는 신호다.)
  🔗 PART 2 `@Value("#{'${myNameList}'.split(',')}")`가 SpEL로 억지로 하던 것을, Boot는 **타입만 `List<String>`으로 두면 자동 변환**해준다.

**`setMaxAge(3600)`** — preflight(`OPTIONS`) 응답을 1시간 캐시. 매 요청마다 OPTIONS가 한 번 더 나가는 낭비를 줄인다.

### ④ Stream 실습 테스트 신설 (`StreamTest.java`)

```java
public class StreamTest {
    @Test
    void stream() {
        List<String> names = List.of("Alice", "Bob", "Charlie", "Diana");

        // ── 기존 방식 : for + if ──────────────────
        List<String> result1 = new ArrayList<>();
        for (String name : names) {
            if (name.length() > 3) {
                result1.add(name.toUpperCase());
            }
        }
        System.out.println(result1);

        // ── Stream 방식 ──────────────────────────
        List<String> result2 = names.stream()        // ① 스트림 생성 List<String> → Stream<String>
                .filter(name -> name.length() > 3)   // ② 중간 연산 - 길이가 3 초과인 것만
                .map(name -> name.toUpperCase())     //   중간 연산 - 대문자로 변환
                .toList();                           // ③ 최종 연산 - Stream<String> → List<String>
        System.out.println(result2);
    }
}
```

**해설**: 같은 결과를 **for문과 Stream 두 벌로** 나란히 둔 비교 코드. 🔗 PART 1 자료 1.1절이 "J2SE 8: Lambda, Stream"을 언급만 하고 넘어갔던 것을 이제 실습으로 채운 것이다. **부록 B-1에서 Stream을 본격적으로 정리**한다.

### ⑤ `SpringBoot-Gradle-App` — 정리할 게 "딱 한 표"는 있다

변동은 없지만, **다른 프로젝트에 없는 두 가지**가 있어서 짚어둘 가치는 있다.

**(1) Maven ↔ Gradle 대조**

| | Maven (`pom.xml`) | Gradle (`build.gradle`) |
|---|---|---|
| 형식 | XML | Groovy/Kotlin DSL |
| 플러그인 | `<plugin>` | `plugins { id 'org.springframework.boot' version '3.1.5' }` |
| 의존성 | `<dependency><groupId>..<artifactId>..` | `implementation 'org.springframework.boot:spring-boot-starter-web'` |
| 컴파일 전용 | `<scope>provided</scope>` | `compileOnly 'org.projectlombok:lombok'` |
| 런타임 전용 | `<scope>runtime</scope>` | `runtimeOnly 'com.h2database:h2'` |
| 개발 전용 | `<optional>true</optional>` | `developmentOnly '...devtools'` |
| 애노테이션 처리 | `<path>` in compiler plugin | `annotationProcessor 'org.projectlombok:lombok'` |
| 빌드 | `mvnw package` | `gradlew build` |
| 실행 | `mvnw spring-boot:run` | `gradlew bootRun` |

```gradle
java { sourceCompatibility = '21' }      // ★ 다른 프로젝트(17)보다 상위 버전
```

**(2) Log4j2로 로거 교체 — 자료 1.9절이 Boot 프로젝트에서 적용된 사례**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-log4j2'
```
- 자료 1.9절은 "Logback을 제외하고 log4j2를 추가하라"고 했다
- ⚠️ **그런데 이 `build.gradle`에는 `spring-boot-starter-logging` 제외(exclusion)가 보이지 않는다.** Spring Boot Gradle 플러그인이 `starter-log4j2`를 감지해 자동 제외해주는 경우도 있지만, 명시하는 것이 안전하다:
  ```gradle
  configurations { all { exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging' } }
  ```
- 🔗 PART 3 3-2절에서 `MyRunner`가 `logger.getClass().getName()`을 찍어 **Logback임을 확인**했었다. **이 프로젝트에서 같은 코드를 돌리면 `org.apache.logging.log4j...`가 찍힐 것**이다. 퍼사드(SLF4J)는 그대로인데 구현체만 바뀌는 것을 눈으로 확인할 수 있는 자리. **자료 1.8절 "로깅 퍼사드 vs 로거"의 확인 지점이다.**
- ⚠️ **다만 Log4j2 자체가 이 저장소에서 낯선 것은 아니다.** PART 2 `SpringFWXml`의 `LoggingAspect.java`가 이미 `org.apache.logging.log4j.LogManager`를 **직접** import해서 쓰고 있고(`Logger logger = LogManager.getLogger();`), `resources/log4j2.xml`에 파일+콘솔 Appender 설정까지 갖춰져 있다. 차이는 **누가 로거 구현체를 고르는가**다 — `SpringFWXml`은 SLF4J 퍼사드 없이 **Log4j2 API를 코드에서 직접 호출**한 것이고, 이 Gradle 프로젝트는 **SLF4J 퍼사드 뒤에서 구현체만 Logback→Log4j2로 교체**한 것이다. 후자가 "퍼사드 vs 로거" 개념을 보여주기에 더 적합한 사례라 이 절에 남겨둔다.

**결론**: 위 두 표 외에는 `springboot-student`의 초기 버전과 내용이 겹치므로, **별도 절을 만들 필요는 없다**는 판단이 맞다.

**`MySpringBootAdmin`**도 마찬가지로 `@EnableAdminServer` 한 줄이 전부라 3-8절에 이미 다 들어가 있다.

---

## 4-9. PART 4 자가 점검 질문

**계층·트랜잭션**
1. `@Transactional(readOnly = true)`를 클래스에 걸고 쓰기 메서드만 재정의하는 전략을, PART 2의 XML은 어떻게 표현했나?
2. `readOnly = true`가 주는 실질적 이득 2가지는?
3. `updateStudent`에 `save()`가 없는데 UPDATE가 나가는 이유는?
4. 엔티티 생명주기 4단계를 말하고, `create`에는 `save()`가 필요하고 `update`에는 필요 없는 이유를 설명하라
5. Service에서 Entity가 아니라 DTO를 반환하는 이유 3가지는? (`LazyInitializationException` 포함)
5-1. LAZY 연관관계를 Controller까지 들고 나가면 생길 수 있는 **서로 다른 두 예외**는? 각각 언제 발생하나?
6. 등록 성공은 201, 삭제 성공은 204인 이유는?

**연관관계**
7. 연관관계의 주인은 무엇으로 결정되나? `mappedBy`는 무엇을 가리키나?
8. 1:N에서 주인이 항상 N쪽인 이유를 DB 관점에서 설명하라
9. `@ManyToOne`과 `@OneToOne`의 기본 fetch는? 왜 명시적으로 LAZY를 줘야 하나?
10. `@OneToOne`에서 `mappedBy`를 쓴 쪽의 LAZY가 잘 동작하지 않는 이유는?
11. `@JoinColumn(unique = true)`가 없으면 1:1이 어떻게 깨지나?
12. `@Builder.Default`를 빼면 어디서 NPE가 나나?
13. `.student(student)`만 하고 `student.setStudentDetail(...)`을 빼면 무슨 일이 생기나?
14. 연관관계 편의 메서드란 무엇이고, 이 저장소에 왜 없는 게 문제인가?
15. `Department.students`에 cascade를 걸면 안 되는 이유는?

**쿼리·성능**
16. JPQL과 SQL의 차이를 예시로 설명하라
17. N+1 문제가 발생하는 과정을 쿼리 개수로 설명하라. EAGER로 바꾸면 해결되나?
17-1. Employee 1개 + Department 100개 상황에서, `default_batch_fetch_size` 적용 전/후 쿼리 수는 각각 몇 개인가?
18. `JOIN`과 `JOIN FETCH`의 차이는?
19. `LEFT JOIN FETCH`와 `JOIN FETCH` 중 어느 것을 언제 쓰나? 이 코드의 두 사례를 들어라
20. 컬렉션 fetch join의 제약 3가지는?
21. `MultipleBagFetchException`은 언제 나나?
21-1. `default_batch_fetch_size`는 fetch join과 어떻게 다른가? 왜 두 방식을 함께 쓰나?
22. Projection 인터페이스가 값을 채우는 원리는? 별칭을 빼먹으면?
23. `COUNT(s)` Projection이 엔티티 조회보다 나은 점은?

**DTO·기타**
24. `@Valid`를 필드에 붙이는 이유는? 안 붙이면?
25. `SimpleResponse`를 따로 만드는 이유는? 안 만들면 무슨 일이?
26. `@JsonInclude(NON_NULL)`이 필요해진 맥락은?
27. `fromEntity`와 `fromSummary`가 채우는 필드가 다른 이유는?
27-1. SOP는 무엇을 막는 정책인가? CORS는 이를 어떻게 우회하나?
28. `CorsFilter`를 `HIGHEST_PRECEDENCE`로 두는 이유는?
29. `setAllowCredentials(true)`와 `allowedOrigins("*")`를 함께 쓸 수 없는 이유는?
30. 1toN 프로젝트에서 아직 구현되지 않은 부분은 어디인가? 지금 POST를 하면 어떻게 되나?  (〔v1.3〕 업데이트본에서 완성 — 이제는 "완성된 코드에서 학과가 어떻게 연결되는가"로 바꿔 답해 볼 것: 5-3절)
