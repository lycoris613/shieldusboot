# PART 5. 신규 진도 ② — 1:N 완성 · 페이징 · 검색 · JWT  〔v1.3 추가〕

> **이 파트는 v1.3에서 새로 추가한 것이다.** 재료는 다음 여섯 가지다.
> - `springboot-student-1toN.zip` — **업데이트본** (PART 4에서 "미완성"이라 했던 1toN이 완성됨)
> - `springboot4-student-1toN-paging.zip` — **신규** (1toN + 페이징 + 검색)
> - 강의 문서 **2-4**(Student–StudentDetail 1:1), **2-5**(+Department 1:N), **2-6**(페이징), **2-7**(Spring Security + JWT)
>
> PART 4가 "코드에서만 읽어낸 내용"이었다면, PART 5는 **강의 문서의 설명·실측 로그와 코드를 맞대어 놓은 것**이다.
> 그래서 PART 4에서 이미 다룬 개념(주인·mappedBy·fetch join 기본·Projection 기본)은 반복하지 않고, **문서가 새로 보태는 것 + 코드에서 새로 생긴 것 + 둘이 어긋나는 곳**을 중심으로 정리한다.

## 5-0. 무엇이 바뀌었나 — 변경 요약

### ⚠️ 먼저 짚을 사실 — 이 저장소는 Spring Boot **4.0.8** 이다

PART 3의 제목이 "Spring Boot 3"이지만, 실제 `pom.xml`을 열어 보면 메인 프로젝트들은 전부 Boot 4다.

```xml
<!-- springboot / springboot-student / -1to1 / -1toN / springboot4-student-1toN-paging 공통 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.8</version>
</parent>
...
<artifactId>spring-boot-starter-webmvc</artifactId>          <!-- (Boot 3 의 starter-web 에 해당) -->
<artifactId>spring-boot-h2console</artifactId>
<artifactId>spring-boot-starter-data-jpa-test</artifactId>    <!-- 기능별로 쪼개진 test 스타터 -->
<artifactId>spring-boot-starter-webmvc-test</artifactId>
```

(예외: `MySpringBootAdmin`, `SpringBoot-Gradle-App`만 3.1.5)

| 구성 요소 | Boot 3.x (PART 3 설명의 기준) | **Boot 4.0 (이 저장소)** |
|---|---|---|
| Spring Framework | 6.x | **7.0** |
| Spring Security | 6.x | **7.0** |
| Hibernate | 6.x | **7.1** |
| Spring Data | 2023~2024.x (commons 3.x) | **2025.1** (commons/jpa 4.0.x) |
| Jackson | 2.x | **3.0이 기본** (2.x는 deprecated 형태로 지원) |
| 스타터 | `starter-web`, `starter-test` 통짜 | **기능별 모듈 스타터** (`starter-webmvc`, `*-test` 분리) |

**해설**
- PART 3~4에서 설명한 개념(자동 설정·JPA·Security의 구조)은 **그대로 유효**하다. 이 절은 "틀렸다"가 아니라 "실제 버전은 이것"이라는 보정이다.
- ⚠️ 강의 문서 2-6이 `Pageable.class` 위치를 확인하며 `spring-data-commons-3.4.5.jar`라고 적은 것은 **Boot 3.4 계열 기준의 캡처**다. 이 저장소(4.0.8)에서 같은 확인을 하면 `spring-data-commons-4.0.x.jar`가 보일 것이다. **"Pageable은 commons에만 있고 jpa에는 없다"는 결론은 버전과 무관하게 같다.**
- ⚠️ `DepartmentDTO`가 `import com.fasterxml.jackson.annotation.JsonInclude;`를 쓰는데도 잘 동작하는 이유: Jackson 3는 **databind 등 본체 패키지를 `tools.jackson.*`으로 옮겼지만 애노테이션 패키지(`com.fasterxml.jackson.annotation`)는 유지**했기 때문이다. `@JsonInclude`, `@JsonIgnore` 같은 애노테이션만 쓰는 코드는 영향이 없다.
- 🔗 4-2절에서 "DTO를 안 쓸 때의 대안"으로 소개한 `jackson-datatype-hibernate6` / `Hibernate6Module`은 **Jackson 2 + Hibernate 6 조합의 좌표**다. Boot 4(Jackson 3 + Hibernate 7)에서 이 방식을 쓰려면 **버전에 맞는 모듈을 따로 확인**해야 한다. 이 저장소는 DTO 변환을 쓰므로 해당 없음 — **DTO 변환이 버전에 흔들리지 않는 해법이라는 점이 한 번 더 확인되는 지점**이다.
- 🔗 4-6절의 "Hibernate 6부터 컬렉션 fetch join 중복이 자동 제거된다"는 Hibernate 7에서도 그대로다. 강의 문서 2-5도 "실제 응답에서 학생 중복이 없는 것을 확인했다"고 적고 있다.

### 프로젝트별 변동

| 프로젝트 | 변동 | 내용 |
|---|---|---|
| `springboot-student-1toN` | **업데이트** | `DepartmentService`·`DepartmentController` **신설**, `StudentService`가 학과 연결, 조회 메서드 전부 학과까지 fetch join, 헬퍼 null-safe화, CORS에 `4173` 추가 |
| **`springboot4-student-1toN-paging`** | **신규** | 위 1toN + **페이징**(`Pageable`/`Page`) + **검색**(`StudentSearchService`/`Controller`) + 컬렉션 페이징 **실험용 데모 엔드포인트** |
| (강의 문서 2-7) | 문서만 | **JWT 인증** — 코드 zip에는 아직 없음. 문서의 코드 발췌로 정리한다 (5-11절) |

**두 zip의 관계** — `diff -rq` 결과 paging 버전은 1toN 업데이트본의 **상위 집합**이다. 달라진 파일은 `DepartmentController`, `StudentController`, `DepartmentRepository`, `StudentRepository`, `DepartmentService`, `StudentService`, `StudentDTO`(주석 2줄) 뿐이고, 신규는 `StudentSearchController`, `StudentSearchService` 두 개다. 나머지(엔티티·`DataInitRunner`·`ErrorCode`·설정 파일)는 **완전히 같다.** 그래서 이 파트의 코드 인용은 **paging 버전을 기준**으로 하고, 1toN에만 해당하는 내용은 따로 표시한다.

### 진화 관계 (PART 4 그림의 연장)

```
springboot-student-1toN (v1.2 시점)   엔티티·리포지토리·DTO 까지만   ← 4-7 "미완성"
   │  ＋ DepartmentService / DepartmentController
   │  ＋ createStudent / updateStudent 에서 학과 연결
   │  ＋ 조회 메서드 전부 studentDetail + department fetch join
   ▼
springboot-student-1toN (업데이트)    1:N 완성, N+1 전 구간 제거
   │  ＋ Pageable / Page / countQuery
   │  ＋ StudentSearchService / StudentSearchController
   │  ＋ 컬렉션 fetch join + 페이징 실험 엔드포인트
   ▼
springboot4-student-1toN-paging       페이징 · 검색
   │  ＋ (문서 2-7) Spring Security + JWT, Student ↔ UserInfo @ManyToOne
   ▼
(다음 zip 예상) ...-paging-JWT
```

### 강의 문서와 코드가 어긋나는 곳 — 한 표로

문서를 보며 코드를 따라 칠 때 **헷갈리기 쉬운 지점**이다. 어느 쪽이 "틀렸다"기보다 문서가 여러 시점의 스냅샷을 섞고 있다고 보면 된다.

| 항목 | 강의 문서 | 이 저장소 코드 | 비고 |
|---|---|---|---|
| `StudentDetail` 제약 (2-4) | `address` NOT NULL, `email` UNIQUE만(선택 입력) | `address` null 허용, `email` NOT NULL+UNIQUE | 2-5 문서의 "검증 규칙 일치" 표는 **코드 쪽과 같다**. 2-4 발췌가 이전 버전 |
| flush 버그 오류 메시지 | 2-4: `NULL not allowed for column "ADDRESS"` / 2-5: `"EMAIL"` | — | 위 제약 차이 때문에 **처음 걸리는 NOT NULL 컬럼이 달라진 것**. 원인은 같다 (5-2절) |
| `Department.students` cascade | (2-5 초기 설명의 엔티티 발췌에 `cascade = ALL`이 보이는 판본이 있음) | **cascade 없음** | 2-5 본문 자체가 "cascade 가 없으므로 학생이 남아 있으면 FK 제약 위반"이라고 설명 → **코드가 맞다** (🔗 4-4) |
| `StudentDetailService` / `/api/students/detail` | 2-5·2-6에 등장 | **없음** | 강사 저장소에만 있는 클래스 |
| `OneToManyFetchJoinPagingTest` | 2-6에 언급 | **없음** | 같은 이유 |
| `Request.detailRequest`의 `@NotNull` | 2-6: "추가했다" | **주석 처리** (`//@NotNull(...)`) | 5-3절 참고 |
| 학과 단건/등록/수정의 학생 수 | 2-5: "COUNT 쿼리 1번으로 학생 수를 채운다" | `Response.fromEntity()`가 `getStudents().size()` | 5-4절 ⚠️ 참고 |
| 테스트 JSON의 키 이름 (2-4) | POST 예시가 `"detail": {...}` | DTO 필드는 `detailRequest` | **조용히 무시된다** (5-1절 ⑦) |
| JWT 관련 전부 (2-7) | 코드 발췌 있음 | **zip에 없음** | 5-11절은 문서 기준 |

---

## 5-1. 강의 문서 2-4가 보태는 1:1 개념 — PART 4에 없던 것만

PART 4(4-3~4-6)는 코드에서 읽어낸 것이라 **"실제로 어떤 SQL이 몇 번 나갔는가"** 같은 근거가 비어 있었다. 2-4 문서는 그 근거를 실측 로그로 채워 준다.

### ① 객체는 참조로, 테이블은 FK + JOIN으로 찾아간다

```
[ 자바 객체 ]                               [ 데이터베이스 테이블 ]
  StudentDetail detail =                      students        student_details
      student.getStudentDetail();             student_id  <---  student_id (FK)
  참조(reference)를 타고 찾아간다              외래키(FK)로 JOIN 해서 찾아간다
```
- JPA의 연관관계 매핑은 **이 두 방식의 차이를 메우는 장치**다. 개발자는 객체 방식으로 쓰고, SQL은 JPA가 만든다.
- 1:1로 테이블을 나누는 이유(문서): **자주 쓰지 않는 정보를 분리하면 기본 조회가 가벼워지고 관리가 편하다.** (예: User–UserProfile)

### ② 프록시 초기화 — "언제" 쿼리가 나가는가

- LAZY면 JPA는 실제 객체 대신 **프록시(가짜 객체)**를 넣어 둔다.
- ⚠️ `student.getStudentDetail()` **까지는** 쿼리가 안 나간다. `.getAddress()`처럼 **실제 값을 꺼낼 때** 조회된다. 이것을 **초기화(initialize)**라 한다.
- ⚠️ 한 가지 더 — 프록시에 대해 **`getId()`만 호출하면 초기화되지 않는다.** 식별자는 프록시가 이미 들고 있기 때문이다. `student.getDepartment().getId()`는 쿼리 없이 FK 값을 돌려준다. (🔗 B-3 ⑤의 프록시 설명 보충 — "`getName()`을 호출하는 순간 초기화 쿼리"와 대비된다)

**LAZY vs EAGER 실제 SQL (문서 2-4)**
```
[ LAZY ]  findById(1L)                → SELECT * FROM students WHERE student_id = 1          (1번)
          .getStudentDetail().getAddress() → SELECT * FROM student_details WHERE student_id = 1 (필요할 때 +1)

[ EAGER ] findById(1L)                → SELECT ... FROM students s
                                          LEFT JOIN student_details sd ON s.student_id = sd.student_id
                                         WHERE s.student_id = 1                              (1번, 한꺼번에)
```
> 단건 `findById`에서의 EAGER는 조인 한 번으로 끝난다. **EAGER가 N+1을 일으키는 것은 목록(`findAll`, JPQL) 조회 때**다 — JPQL은 EAGER 설정을 보지 않고 먼저 SQL을 만들고, 로딩 후에 EAGER를 채우려고 추가 쿼리를 날리기 때문. 🔗 4-6절 "EAGER로 바꾸면 해결될까? 아니다"의 근거.

### ③ ⚠️ mappedBy 쪽 `@OneToOne`은 LAZY가 "늦게 조회"조차 아니다 — 로그로 확인

4-3절 ⑤에서 "EAGER처럼 동작한다"고 적었다. 문서 2-4는 이것을 더 정확히 말한다.

- `Student.studentDetail`(mappedBy)은 **`Student`를 로딩하는 순간 상세정보 조회 쿼리가 곧바로 함께 실행된다.**
- 이유: FK가 `student_details`에만 있어서 `students`만 봐서는 **상세정보가 있는지 없는지조차 모른다.** 프록시를 넣을지 `null`을 넣을지 판단하려면 결국 조회해야 한다.

| 필드 | 쪽 | LAZY 동작 |
|---|---|---|
| `StudentDetail.student` | 주인(FK 보유) | **정상** — 프록시 |
| `Student.studentDetail` | mappedBy | **동작 안 함** — 로딩 시점에 즉시 별도 쿼리 |
| `Student.department` | 주인(`@ManyToOne`, FK 보유) | **정상** |
| `Department.students` | mappedBy(`@OneToMany`) | **정상** — 컬렉션은 비어 있는지 여부를 몰라도 "컬렉션 래퍼"를 넣어 둘 수 있으므로 |

**증거 로그 — `updateStudent`의 `findById()` 직후 (문서 2-4 Step 8 ⑤)**
```
Hibernate: select s1_0.student_id,s1_0.name,s1_0.student_number from students s1_0 where s1_0.student_id=?
Hibernate: select sd1_0.student_detail_id,sd1_0.address,... from student_details sd1_0 where sd1_0.student_id=?
                                              ↑ getStudentDetail() 을 부르기 "전에" 이미 나갔다
Hibernate: select sd1_0.student_detail_id from student_details sd1_0 where sd1_0.email=? limit ?
Hibernate: update student_details set address=?,date_of_birth=?,email=?,phone_number=?,student_id=? where student_detail_id=?
```

**해설**
- ⚠️ 그래서 문서의 결론이 이렇다: **"N+1은 `fromEntity()`에서 상세정보를 꺼내 썼기 때문이 아니라, 꺼내 쓰지 않아도 이미 조회되어서 생긴다."** 목록을 `findAll()`로 가져오는 순간 학생 수만큼 상세정보 쿼리가 확정된다.
- ⚠️ 그리고 이 쿼리는 **`default_batch_fetch_size`로도 묶이지 않는다** (5-5절 실측). 해결책은 **fetch join 하나뿐**이다.
- 🔗 4-6절에서 "`updateStudent`의 `findById()` 이후 `getStudentDetail()` 호출 시 추가 쿼리"라고 적은 것의 정확한 모습이 위 로그다 — **쿼리는 2번이 맞지만, 두 번째 쿼리가 나가는 시점은 `findById()` 안**이다.

### ④ JPQL을 쓸 때의 규칙 (문서 2-4 Step 3)

```
[ SQL ]   SELECT * FROM students s
          LEFT JOIN student_details sd ON s.student_id = sd.student_id
          WHERE s.student_id = 1

[ JPQL ]  SELECT s FROM Student s
          LEFT JOIN FETCH s.studentDetail
          WHERE s.id = :id
```
| 규칙 | 설명 |
|---|---|
| `Student` | 테이블명(`students`)이 아니라 **엔티티 클래스명**. **대소문자 구분** |
| `s.id` | 컬럼명(`student_id`)이 아니라 **엔티티 필드명** |
| `s.studentDetail` | **ON 조건을 쓰지 않는다** — 엔티티에 연관관계가 이미 정의되어 있으므로 |
| `SELECT s` | `SELECT *`가 아니라 **별칭**을 선택 |
| `nativeQuery = true` | SQL을 그대로 쓰고 싶을 때 `@Query(value="...", nativeQuery=true)` |

**Fetch Join 사용 시 주의 5가지 (문서 2-4)**
1. `JOIN FETCH`는 **내부 조인** — 짝이 없는 행은 빠진다
2. 짝이 없어도 포함하려면 `LEFT JOIN FETCH`
3. 1:N 컬렉션 fetch join은 중복 행 → (Hibernate 5까지는) `DISTINCT` 필요
4. 1:N 컬렉션 fetch join + `Pageable` → **메모리 페이징 위험** (5-8절)
5. ⚠️ **fetch join 대상에는 별칭을 주더라도 WHERE 조건에 쓰지 않는 것이 원칙** — 이유는 5-8절 ④ "반쪽짜리 컬렉션"

### ⑤ Query Method 이름 규칙 — 키워드 표 (문서 2-4)

🔗 3-4절에서 `find/exists/count/delete + By + 필드명 + 키워드`로 한 줄 요약했던 것의 전체 표다.

```
[ 동작을 결정하는 접두사 ]
   findBy... / getBy... / readBy...   조회
   existsBy...                        존재 여부 (boolean)
   countBy...                         개수 (long)
   deleteBy... / removeBy...          삭제

[ 조건을 조합하는 키워드 ]
   And / Or             findByNameAndStudentNumber(String, String)
   Between              findByDateOfBirthBetween(LocalDate, LocalDate)
   LessThan / GreaterThan
   Like / Containing    findByNameContaining(String)
   IsNull / IsNotNull
   OrderBy              findByNameOrderByStudentNumberDesc(String)

[ 연관 엔티티를 타고 들어가기 ]
   findByStudentId(Long)      → StudentDetail.student 의 id 를 조건으로 ( student + Id )
```
- `StudentDetailRepository.findByStudentId(Long)`이 **연관 필드를 타고 들어가는 Query Method**의 실제 사례다. `studentId`라는 필드는 없다 — `student` 필드의 `id`로 해석된다.
- ⚠️ 이름 규칙으로 표현하기 어려우면 `@Query`로 쓴다. 그리고 **`@Query`가 붙으면 메서드 이름은 쿼리 생성에 쓰이지 않는다** — 5-9절 `findByNameContainingIgnoreCase`가 그 예.

### ⑥ ⚠️ `existsBy`·`existsById`·`deleteById`가 실제로 내보내는 SQL

문서 2-4의 로그가 **"메서드 이름만 보고 짐작한 SQL"과 다른 부분**을 보여 준다.

```
existsByEmail(email)   → select sd1_0.student_detail_id from student_details sd1_0 where sd1_0.email=? limit ?
existsById(id)         → select count(*) from students s1_0 where s1_0.student_id=?
deleteById(id)         → select s1_0.student_id,... from students s1_0 where s1_0.student_id=?      ← 먼저 조회!
                         select sd1_0.student_detail_id,... from student_details sd1_0 where sd1_0.student_id=?
                         delete from student_details where student_detail_id=?                    ← cascade
                         delete from students where student_id=?
```

**해설**
1. **파생 쿼리 `existsBy...`는 `count(*)`가 아니라 "식별자 하나 + `limit`"** 로 나간다. 하나라도 찾으면 멈추므로 count보다 싸다. (🔗 3-4절 표의 `select count(*) > 0`은 〔v1.3 정정〕 참고)
2. **`existsById`는 `count(*)`** 로 나간다 — 같은 "exists"라도 구현 경로가 다르다(`SimpleJpaRepository`의 기본 메서드).
3. ⚠️ **`deleteById`는 "조회 후 삭제"다.** `EntityManager.remove()`는 영속 엔티티만 지울 수 있으므로 먼저 `findById`로 불러온다. 그 과정에서 mappedBy `@OneToOne`인 상세정보까지 조회되고(③), `cascade = ALL`이므로 **자식(`student_details`)을 먼저 지우고 부모를 지운다**(FK 순서).
   - 그래서 `deleteStudent`는 `existsById`(1) + 조회(2) + 삭제(2) = **SQL 5번**이다. `existsById` 검사는 "없는 id면 404"를 주기 위한 것이다. (Spring Data 3 이후 `deleteById`는 없는 id에 예외를 던지지 않고 조용히 넘어가므로, 이 검사가 없으면 **없는 id 삭제가 204로 성공**해 버린다.)
4. **UPDATE는 바뀐 컬럼만이 아니라 전 컬럼**을 SET 한다 (`update student_details set address=?,date_of_birth=?,email=?,phone_number=?,student_id=? ...`). Hibernate 기본값이다. 바뀐 컬럼만 보내려면 엔티티에 `@DynamicUpdate`를 붙인다. 🔗 4-2절 변경 감지의 실제 SQL 모양.

### ⑦ ⚠️ UNIQUE 컬럼과 NULL, 그리고 "애플리케이션 중복 검사"의 한계

문서 2-4가 짚은 두 가지는 **DB 설계 감각**에 관한 것이라 따로 적는다.

1. **UNIQUE는 NULL을 막지 않는다.** SQL에서 `NULL != NULL`이므로 UNIQUE 컬럼에 NULL이 **여러 개** 들어갈 수 있다. (2-4 판본처럼 `email`을 선택 입력 + UNIQUE로 두면 "이메일 안 쓴 학생 여러 명"이 가능하다.) 반대로 "없으면 안 되는" 값이면 `nullable = false`를 **따로** 줘야 한다. → 이 저장소의 `phoneNumber`/`email`이 `nullable = false, unique = true` 두 개를 다 쓰는 이유.
2. **`existsByEmail()` 같은 애플리케이션 중복 검사만으로는 동시 요청을 못 막는다.**
   ```
   요청 A: existsByEmail("x") → false ─┐
   요청 B: existsByEmail("x") → false ─┤ 둘 다 통과
   요청 A: INSERT email='x'   → 성공   │
   요청 B: INSERT email='x'   → ???    ┘  ← DB UNIQUE 제약이 있어야 여기서 막힌다
   ```
   **DB 제약(UNIQUE)이 최종 방어선**이고, 서비스의 `exists` 검사는 "친절한 409 메시지"를 위한 1차 필터다. 🔗 PART 2 2-6절 트랜잭션 격리수준 — `@Transactional`만으로는 이 경쟁 상태가 해결되지 않는다(기본 격리수준에서 두 트랜잭션이 서로의 미커밋 INSERT를 못 보므로).
   - ⚠️ 이 경우 B는 `DataIntegrityViolationException`(RuntimeException)이 나고, 이 프로젝트의 Advice 구조상 **500**으로 응답된다(5-10절). 실무라면 이 예외를 잡아 409로 바꾸는 핸들러를 둔다.

### ⑧ ⚠️ 테스트 JSON의 키 이름 — `"detail"` vs `"detailRequest"`

문서 2-4 Step 8의 POST 예시는 이렇게 되어 있다.
```json
{ "name": "홍길동", "studentNumber": "S12345",
  "detail": { "address": "서울시 강남구 ...", "phoneNumber": "010-1234-5678", ... } }
```
그런데 `StudentDTO.Request`의 필드 이름은 **`detailRequest`** 다. (응답 DTO의 필드가 `detail`이다.)

- Spring Boot의 기본 Jackson 설정은 **모르는 키를 무시**한다(`FAIL_ON_UNKNOWN_PROPERTIES = false`). 그래서 이 요청은 **에러 없이 201**이 나오지만, `detailRequest`는 `null` → **상세정보가 저장되지 않는다.**
- 같은 문서의 PUT 예시(`"detailRequest": {...}`)는 맞게 되어 있고, "김둘리" 수정 예시는 다시 `"detail"`이다 → **이 요청도 200이지만 상세정보는 추가되지 않는다.**
- 🔗 4-4절 해설 4번 "`Request`는 `detailRequest`, `Response`는 `detail`" — 요청/응답 스펙을 따로 설계한 대가로 **이름이 비슷해서 헷갈리는 함정**이 생긴 것. Postman으로 따라 칠 때 응답의 `detail`이 `null`이면 **키 이름부터 의심**할 것.

### ⑨ Controller는 한 줄도 안 바뀌었다 (문서 2-4 Step 6, 2-5 Step 7)

- 2-3 → 2-4(1:1 추가) → 2-5(1:N 추가)를 거치며 엔티티 구조가 크게 바뀌었는데 **`StudentController`는 그대로**다. 다루는 타입(`StudentDTO.Request/Response`)이 같기 때문이다.
- 🔗 4-1절 3계층 — **계층을 나누면 한쪽 변경이 다른 쪽으로 번지지 않는다**는 것의 실제 증거. (paging 버전에서 `/paged`가 **추가**됐을 뿐 기존 메서드는 그대로다.)

---

## 5-2. flush 시점 버그 — "save() 안 했는데 INSERT가 나갔다"

**문서 2-4·2-5가 공통으로 "실제로 500이 났던 부분"이라고 기록한 사례**이고, 코드에도 주석으로 남아 있다. 4-2절 변경 감지를 이해했다면, 이 절은 그 **반대 방향의 함정**이다.

### 발생한 오류 (문서 2-5)

```
POST /api/students          { "name":"Repro Student", "studentNumber":"RP001", "departmentId":1 }
   → 201 Created, "detail": null                 ( 상세정보 없이 등록 )

PUT  /api/students/9        { ..., "detailRequest": { "address":"Some Address",
                                                      "phoneNumber":"010-5555-6666",
                                                      "email":"repro@example.com" } }
   → 500 Internal Server Error
     insert into student_details (address,date_of_birth,email,phone_number,student_id,student_detail_id)
            values (?,?,?,?,?,default)
     NULL not allowed for column "EMAIL"   [23502-232]
```
(`[23502-232]`는 H2의 NOT NULL 위반 오류 코드 — 문서 로그는 H2로 실측한 것)

### 원인 — 문제가 됐던 순서

```java
StudentDetail studentDetail = student.getStudentDetail();
// (1) 값이 비어 있는 StudentDetail 을 먼저 만들어 연결한다
if (studentDetail == null) {
    studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    student.setStudentDetail(studentDetail);      // ← 영속 상태의 Student 에 연결됨
}
// (2) 중복 검사 - 여기서 조회 쿼리가 실행된다
if (isEmailChangingAndExists(studentDetail, request.getDetailRequest())) { ... }
// (3) 이제서야 값을 채운다
studentDetail.setAddress(request.getDetailRequest().getAddress());
```

```
(1) student 는 findById 로 가져온 "영속" 상태 + studentDetail 에 cascade = ALL
      → 비어 있는 StudentDetail 도 "저장 대상"이 된다 (아직 SQL 은 안 나감 — 쓰기 지연)
(2) existsByEmail() = 조회 쿼리
      → FlushMode.AUTO : "조회 결과가 정확하려면 쌓인 변경을 먼저 DB 에 반영해야 한다"
      → 조회 SQL 직전에 flush → 빈 StudentDetail 의 INSERT 가 나감
      → email NOT NULL 위반 → 예외
(3) 여기는 실행되지도 못한다
```

### 해결 — 코드 (`StudentService.updateStudent`, 현재 버전)

```java
if (request.getDetailRequest() != null) {
    StudentDetail studentDetail = student.getStudentDetail();

    // 중복 검사를 먼저 수행한다.
    // 검사용 조회 쿼리가 실행되면 영속성 컨텍스트가 flush 되는데,
    // 값이 채워지지 않은 StudentDetail 을 먼저 연결해 두면
    // email, phoneNumber 등 NOT NULL 컬럼에 null 이 들어가 제약조건 위반이 발생한다.
    if (isEmailChangingAndExists(studentDetail, request.getDetailRequest())) {
        throw new BusinessException(ErrorCode.EMAIL_DUPLICATE, request.getDetailRequest().getEmail());
    }
    if (isPhoneNumberChangingAndExists(studentDetail, request.getDetailRequest())) {
        throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATE, request.getDetailRequest().getPhoneNumber());
    }

    // 검사가 끝난 뒤에 생성하고 연결한다
    if (studentDetail == null) {
        studentDetail = new StudentDetail();
        studentDetail.setStudent(student);           //양방향 연관관계 저장
        student.setStudentDetail(studentDetail);
    }
    studentDetail.setAddress(request.getDetailRequest().getAddress());
    studentDetail.setPhoneNumber(request.getDetailRequest().getPhoneNumber());
    studentDetail.setEmail(request.getDetailRequest().getEmail());
    studentDetail.setDateOfBirth(request.getDetailRequest().getDateOfBirth());
}
```

### 헬퍼 메서드도 함께 바뀌었다 — v1.2 시점 vs 현재

```java
// ── v1.2 시점 (1toN 구버전) ─────────────────────────────
private boolean isEmailChangingAndExists(StudentDetail currentDetail, StudentDTO.StudentDetailDTO newDetail) {
    return newDetail.getEmail() != null &&
           !newDetail.getEmail().isEmpty() &&
           (currentDetail.getEmail() == null ||                 // ← currentDetail 이 null 이면 NPE!
                   !currentDetail.getEmail().equals(newDetail.getEmail())) &&
           studentDetailRepository.existsByEmail(newDetail.getEmail());
}

// ── 현재 ─────────────────────────────────────────────
private boolean isEmailChangingAndExists(StudentDetail currentDetail, StudentDTO.StudentDetailDTO newDetail) {
    //상세정보가 아직 없는 경우 currentDetail 은 null 이다
    String currentEmail = currentDetail == null ? null : currentDetail.getEmail();
    //email 은 @NotBlank 로 검증되므로 null / 빈 문자열 검사는 필요 없다
    //값이 실제로 바뀔 때만 중복을 검사한다 ( 자기 자신의 값은 중복이 아니다 )
    return !newDetail.getEmail().equals(currentEmail) &&
            studentDetailRepository.existsByEmail(newDetail.getEmail());
}
```

**해설 — 이 절에서 배울 것 5가지**

1. **⚠️ 조회 쿼리(`findBy…`, `existsBy…`, `countBy…`, JPQL)는 flush를 유발한다.** "`save()`를 안 불렀으니 아직 DB에 안 갔겠지"는 틀린 가정이다. 🔗 4-2절 "쓰기 지연"은 **커밋 때만** 모아 보내는 게 아니라 **JPQL 실행 직전에도** 모아 보낸다. (예외: `findById`는 내부적으로 `EntityManager.find()`라 flush를 유발하지 않는다 — AUTO flush는 **JPQL/Criteria/네이티브 쿼리** 실행 직전에 일어난다. `existsBy…`/`countBy…` 같은 파생 쿼리는 JPQL로 만들어지므로 flush 대상이다.)
2. **영속 엔티티에 새 엔티티를 연결하는 순간 그것도 저장 대상이 된다.** cascade가 있으면 더욱. → **새 엔티티는 값을 다 채운 뒤 연결하거나, 검증을 먼저 끝내고 연결한다.** `createStudent`는 `StudentDetail.builder()`로 **값을 전부 채운 뒤** 연결하므로 애초에 이 문제가 없다.
3. **두 검사를 모두 생성 앞으로 옮겨야 한다.** 문서 2-6의 경고: "하나라도 뒤에 남으면 같은 오류가 발생한다."
4. **헬퍼의 null 처리 방향을 뒤집었다.** `newDetail.getEmail()`은 `@NotBlank`로 검증되어 **절대 null이 아니므로**, 이쪽을 기준으로 `.equals(currentEmail)`을 부르면 `currentEmail`이 null이어도 안전하다. 🔗 PART 1 1-13절 "`equals()` 전 null 체크 습관"의 다른 형태 — **null이 아님이 보장된 쪽에서 `equals`를 호출하면 null 체크 자체가 필요 없다.** (`"CS".equals(code)`가 `code.equals("CS")`보다 안전한 것과 같은 원리)
   - 구버전은 `currentDetail.getEmail()`에서 **상세정보 없는 학생이면 NPE** → Advice의 `RuntimeException` 핸들러가 받아 **500**이었다.
5. ⚠️ 🔗 4-4절에서 "`hasEmailAndExists`는 null 체크 3단, `hasDetailAndPhoneNumberExists`는 없음 — 비대칭"이라고 지적했었다. 현재 코드는 **이메일 쪽 null 체크를 지워서** 대칭을 맞췄다(`@NotBlank`를 믿는 쪽으로). 대칭은 맞았지만 **"Service만 따로 호출하면 `@Valid`를 안 거친다"는 4-4절의 우려는 그대로 남는다** — 테스트 코드에서 Service를 직접 부를 때 주의.

### 조용히 하나 더 — 이름·학번 UPDATE도 일찍 나간다

```java
student.setName(request.getName());                 // ① 변경
student.setStudentNumber(request.getStudentNumber());
student.setDepartment(department);
...
if (isEmailChangingAndExists(...)) { ... }          // ② existsByEmail → flush → students UPDATE 가 여기서 나감
```
같은 원리로 `students`의 UPDATE도 **커밋이 아니라 ②에서** 나간다. 이 경우는 값이 다 채워져 있어 문제가 없고, 이후 예외가 나면 **트랜잭션 롤백으로 함께 취소**된다(flush ≠ commit). → **flush는 "DB에 SQL을 보냄", commit은 "확정"** 이라는 구분이 핵심이다.

---

## 5-3. 1:N 완성 — 학생에게 학과를 연결한다

🔗 4-7절 "⚠️ 1toN은 아직 미완성이다"와 부록 A 4단계 예측 1번이 **그대로 구현**되었다.

### 코드 — `StudentService.createStudent` (현재)

```java
@Transactional
public StudentDTO.Response createStudent(StudentDTO.Request request) {
    // Validate department exists
    Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Department", "id", request.getDepartmentId()));

    if (studentRepository.existsByStudentNumber(request.getStudentNumber())) { ... STUDENT_NUMBER_DUPLICATE }
    if (hasEmailAndExists(request.getDetailRequest())) { ... EMAIL_DUPLICATE }
    if (hasDetailAndPhoneNumberExists(request.getDetailRequest())) { ... PHONE_NUMBER_DUPLICATE }

    Student student = Student.builder()
            .name(request.getName())
            .studentNumber(request.getStudentNumber())
            //Student 와 Department 연관관계 저장
            .department(department)
            .build();

    if (request.getDetailRequest() != null) {
        StudentDetail studentDetail = StudentDetail.builder()
                ...
                //StudentDetail 과 Student 연관관계 저장
                .student(student)
                .build();
        //Student 와 StudentDetail 연관관계 저장
        student.setStudentDetail(studentDetail);
    }
    Student savedStudent = studentRepository.save(student);
    return StudentDTO.Response.fromEntity(savedStudent);
}
```

### 코드 — `updateStudent`에서 학과 변경

```java
Student student = studentRepository.findById(id).orElseThrow(...);
// Validate department exists (if changing)
Department department = departmentRepository.findById(request.getDepartmentId()).orElseThrow(...);
...
student.setName(request.getName());
student.setStudentNumber(request.getStudentNumber());
student.setDepartment(department);                   // ← FK 변경. 변경 감지로 UPDATE
```

### 해설

1. **⚠️ 왜 `department.getStudents().add(student)`는 없는가 — 주인 쪽 한 번이면 FK가 저장된다.**
   - `Student.department`가 주인이므로 `.department(department)` 한 번으로 `students.department_id`가 채워진다.
   - 문서 2-5 정리: **"외래키 저장은 1:1과 1:N 모두 주인 쪽 설정 한 번이면 된다."** (`studentDetail.setStudent(student)` / `student.setDepartment(department)`)
2. **⚠️ 그럼 1:1에서 `student.setStudentDetail(...)`(비주인 쪽)은 왜 부르나 — "양방향 일관성" 말고 이유가 하나 더 있다: cascade.**
   - `Student.studentDetail`에 `cascade = ALL`이 있고, **저장은 `studentRepository.save(student)` 하나뿐**이다. cascade는 **`student`가 참조하는 객체**에만 전파되므로, `student.setStudentDetail(...)`을 빼면 **상세정보가 아예 저장되지 않는다.** (문서 2-5)
   - 반대로 `Department.students`에는 cascade가 없고 학생은 직접 저장하므로, `department.getStudents().add(...)`를 **안 해도 DB는 정상**이다.
   - 단, **같은 트랜잭션 안에서 `department.getStudents()`를 읽을 일이 있다면** 양쪽 다 넣어야 값이 보인다(🔗 4-4절 (B)의 이유). 이 코드는 그런 읽기가 없으므로 생략한 것이다.
3. **편의 메서드는 "의도적으로" 만들지 않았다 (문서 2-5).** 🔗 4-4절 "이 저장소에는 아직 없다 — 개선 포인트"와 부록 A 예측 4번(`department.addStudent`)은 **이번 진도에서 채택되지 않았다.** 문서의 설명은 위 1·2번과 같다 — "주인 쪽 설정으로 FK는 충분하고, cascade가 없는 컬렉션은 저장에 영향이 없다." 편의 메서드는 **같은 트랜잭션에서 양쪽을 다 읽는 코드가 생길 때** 도입하면 된다.
4. **`departmentId`는 `Long` + `@NotNull`** (DTO는 이미 v1.2 시점에 있었다)
   ```java
   @NotNull(message = "Department ID is required")
   private Long departmentId;
   ```
   - 문자열이 아니므로 `@NotBlank`가 아니라 **`@NotNull`**. 🔗 3-4절 `@NotEmpty`/`@NotBlank` 비교표 — `@NotBlank`는 **문자열(`CharSequence`) 전용**이라 `Long`에는 붙일 수 없다.
   - **엔티티(`Department`) 통째가 아니라 ID만 받는다.** 클라이언트가 학과 정보 전체를 보낼 필요가 없고, 서버가 ID로 조회해 **존재 여부까지 검증**한다(없으면 404).
   - ⚠️ 원시형 `long`이었다면 JSON에서 빠졌을 때 `0`이 들어가 `@NotNull`이 무력해진다. **래퍼 타입 `Long`이어야 "안 보냄 = null"을 구분**할 수 있다. 🔗 PART 1 1-13절 Wrapper.
5. **`updateStudent`는 여전히 `findById` + 마지막에 `save()`** — 🔗 4-2절(불필요한 `save()`)·4-6절(fetch join 아님) 지적이 **그대로 남아 있다.** 조회 메서드들은 전부 fetch join으로 바뀌었는데 수정 경로만 남은 셈. 쿼리 수로 보면 `findById`(학생 + mappedBy 상세정보 = 2) + 학과 `findById`(1) + `exists` 검사들 + UPDATE다. 수정은 목록이 아니라 N+1은 아니므로 **우선순위가 낮아서 남겨 둔 것**으로 읽힌다.

### `Request.detailRequest`의 `@NotNull`이 주석인 이유 (paging 버전)

```java
//StudentDetail 의 phoneNumber, email 이 NOT NULL 이므로 상세정보는 반드시 입력해야 한다
//@NotNull(message = "Student detail is required")
@Valid
private StudentDetailDTO detailRequest;
```
- 주석의 논리("상세정보 필수")를 켜면 **상세정보 없는 학생 등록(문서 2-6 테스트 2.5 `"Simple Student"`)이 400**이 된다. 테스트 시나리오가 "상세정보 없이 등록"을 여전히 포함하므로 **꺼 둔 것**으로 보인다.
- 문서 2-6 변경 목록은 "추가했다"고 적었지만 이 저장소에서는 비활성 — **문서와 코드 중 코드를 기준으로 동작을 예상할 것.**
- 🔗 `findByIdWithStudentDetail`의 주석도 같은 맥락으로 바뀌었다: *"API 는 상세정보를 필수로 받지만, DB 에 직접 들어간 데이터 등 상세정보가 없는 행이 있어도 학생 조회가 누락되지 않도록 외부 조인을 유지한다"* — **입력 검증을 어떻게 바꾸든 조회는 LEFT JOIN으로 방어한다**는 태도.

### `getStudentsByDepartmentId` — 빈 목록이 아니라 404

```java
public List<StudentDTO.Response> getStudentsByDepartmentId(Long departmentId) {
    // Validate department exists
    if (!departmentRepository.existsById(departmentId)) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Department", "id", departmentId);
    }
    return studentRepository.findByDepartmentId(departmentId)
            .stream().map(StudentDTO.Response::fromEntity).toList();
}
```
- **"학과가 없음"과 "학과는 있는데 학생이 0명"을 구분**한다. 존재 확인 없이 조회만 하면 둘 다 `[]`가 되어 클라이언트가 구분할 수 없다.
- 그래서 이 엔드포인트는 SQL이 **2번**(존재 확인 + 목록)이다. 문서 2-5 측정표의 "`GET /api/departments/1/students` 2번 — 건수에 비례하지 않음"이 이것.
- ⚠️ 5-9절의 검색 엔드포인트 `GET /api/search/departments/{id}/students`는 **이 검사가 없다** → 없는 학과면 **빈 페이지(200)**. 같은 "학과별 학생" 기능인데 응답 정책이 다르다.

### `DataInitRunner` — 초기 데이터 (두 zip 공통)

```
CS  Computer Science          Alice Johnson(CS001), Bob Smith(CS002), Helen Lee(CS003)
EE  Electrical Engineering    Charlie Brown(EE001), Diana Wilson(EE002)
ME  Mechanical Engineering    Edward Davis(ME001)
BA  Business Administration   Fiona Garcia(BA001), George Martinez(BA002)
```
- **학과 4개 / 학생 8명** — 문서 2-5·2-6의 모든 측정("학생 8명 / 학과 4개")이 이 데이터 기준이다.
- ⚠️ **Helen Lee(CS003)는 이 코드에서는 상세정보가 "있다"** — 단 `email`·`phoneNumber`만 있고 `address`·`dateOfBirth`는 없다.
  ```java
  //email과 phonenumber 만 가진 StuentDetail 객체생성하기
  StudentDetail detail8 = StudentDetail.builder()
          .phoneNumber("010-6789-0789")
          .email("helen@example.com")
          .build();
  ```
  상세정보 **없는** Helen 버전은 주석으로 남아 있다. 문서 2-4의 "`GET /api/students/8` → `"detail":null`" 로그는 **주석 쪽(상세정보 없는) 버전으로 실행한 결과**다. 이 저장소로 실행하면 Helen의 `detail`은 `{"address":null, "phoneNumber":"010-6789-0789", ...}`로 나온다. → **"상세정보 없는 학생" 시나리오를 재현하려면 POST로 `detailRequest` 없이 새로 등록**해야 한다.
- `run()`에 `@Transactional`이 붙어 있다 — `DataInitRunner`도 스프링 빈(프록시)이므로 동작한다. 🔗 PART 2 2-6절 "프록시를 거쳐 호출되어야 트랜잭션이 걸린다" — 러너는 스프링이 빈을 통해 호출하므로 조건을 만족한다.

---

## 5-4. DepartmentService · DepartmentController — 학과 CRUD

🔗 부록 A 4단계 예측 2·3번(`findAllSummaries()` → 목록, `findByIdWithStudents()` → 상세)이 **그대로** 적용되었다.

### 코드 — `service/DepartmentService.java` (paging 버전, 페이징 메서드는 5-7·5-8절에서)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;

    /*
     * [ findAll() 을 쓰지 않는 이유 ]
     *
     *   return departmentRepository.findAll().stream()
     *           .map(d -> SimpleResponse.fromEntity(d, (long) d.getStudents().size()))
     *           .toList();
     *
     * 위와 같이 작성하면 학생 수를 세기 위해 학생 엔티티를 전부 로딩해야 한다.
     * 게다가 Student.studentDetail 은 mappedBy 쪽 @OneToOne 이라 LAZY 가 동작하지 않고
     * default_batch_fetch_size 도 적용되지 않아 학생 수만큼 상세정보 조회가 추가된다.
     *
     *   학과 4개 / 학생 8명 기준 실제 측정
     *     findAll() 방식        : SQL 10번 ( 학과1 + 학생1 + 상세정보8 ), 20행 로딩
     *     findAllSummaries() 방식 : SQL  1번, 4행 로딩
     *
     * 응답에 필요한 것은 id, name, code, studentCount 네 개뿐이므로
     * COUNT 결과만 조회하는 프로젝션을 사용한다.
     */

    // 모든 학과 조회 - 학과 정보와 학생 수를 한 번의 쿼리로 함께 가져온다
    public List<DepartmentDTO.SimpleResponse> getAllDepartments() {
        return departmentRepository.findAllSummaries()
                .stream()
                .map(DepartmentDTO.SimpleResponse::fromSummary)
                .toList();
    }

    public DepartmentDTO.Response getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Department", "id", id));
        return DepartmentDTO.Response.fromEntity(department);
    }

    public DepartmentDTO.Response getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCodeWithStudents(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Department", "code", code));
        return DepartmentDTO.Response.fromEntity(department);
    }

    @Transactional
    public DepartmentDTO.Response createDepartment(DepartmentDTO.Request request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ErrorCode.DEPARTMENT_CODE_DUPLICATE, request.getCode());
        }
        if (departmentRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NAME_DUPLICATE, request.getName());
        }
        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();
        Department savedDepartment = departmentRepository.save(department);
        return DepartmentDTO.Response.fromEntity(savedDepartment);
    }

    @Transactional
    public DepartmentDTO.Response updateDepartment(Long id, DepartmentDTO.Request request) {
        Department department = departmentRepository.findById(id).orElseThrow(...);

        // Check if another department already has the code
        if (!department.getCode().equals(request.getCode()) &&
                departmentRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ErrorCode.DEPARTMENT_CODE_DUPLICATE, request.getCode());
        }
        // Check if another department already has the name
        if (!department.getName().equals(request.getName()) &&
                departmentRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NAME_DUPLICATE, request.getName());
        }
        department.setName(request.getName());
        department.setCode(request.getCode());

        Department updatedDepartment = departmentRepository.save(department);
        return DepartmentDTO.Response.fromEntity(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Department", "id", id);
        }
        // Check if department has students
        Long studentCount = studentRepository.countByDepartmentId(id);
        if (studentCount > 0) {
            throw new BusinessException(ErrorCode.DEPARTMENT_HAS_STUDENTS, id, studentCount);
        }
        departmentRepository.deleteById(id);
    }
}
```

### 해설

1. **서비스 구조는 `StudentService`의 복사판이다** — 클래스 `readOnly=true` + 쓰기 메서드만 `@Transactional`, 등록 전 `exists` 중복 검사, 수정 시 "값이 바뀔 때만" 중복 검사(자기 자신 제외). 🔗 4-1절 해설 1·3번이 **새 도메인에 그대로 반복**된다 — 패턴을 한 번 익히면 도메인이 늘어도 같은 모양이라는 것.
2. **⚠️ 삭제 전 학생 수 검사 → 409 (가장 중요한 설계 포인트)**
   - `Department.students`에 cascade가 **없다**(🔗 4-4). 그러면 학생이 남은 학과를 지울 때 무슨 일이 생기나?
     ```
     org.hibernate.exception.ConstraintViolationException
       → Referential integrity constraint violation:
         "FK...: PUBLIC.STUDENTS FOREIGN KEY(DEPARTMENT_ID) REFERENCES PUBLIC.DEPARTMENTS"
     ```
     학생이 함께 지워지지 않고 **DB의 FK 제약에 걸려 예외** → 이 프로젝트 Advice 구조상 **500**.
   - 그래서 **미리 `countByDepartmentId`로 세어서 "학생이 있어 삭제할 수 없다"는 명확한 409**로 응답한다(문서 2-5: "미리 검사해서 명확한 오류로 응답하는 것이 더 낫다").
     ```json
     { "statusCode": 409,
       "message": "Cannot delete department with id: 1. It has 3 students",
       "timestamp": "2026-09-09 20:29:42 수 오후" }
     ```
   - 🔗 `ErrorCode.DEPARTMENT_HAS_STUDENTS("Cannot delete department with id: %s. It has %s students", CONFLICT)` — `%s` 두 개에 `id, studentCount`가 **가변 인자(`Object... args`)** 로 들어간다. 🔗 3-7절 `formatMessage(Object... args)`.
   - ⚠️ 5-1절 ⑦과 같은 이유로, 이 검사와 `deleteById` 사이에 누가 학생을 추가하면 여전히 FK 예외가 날 수 있다. **최종 방어선은 DB FK 제약**이고 서비스 검사는 친절한 메시지용이다.
3. **ErrorCode 추가분** — `DEPARTMENT_CODE_DUPLICATE`, `DEPARTMENT_NAME_DUPLICATE`(둘 다 409). 이제 enum에 주석으로 **도메인별 그룹**이 나뉘어 있다(Common / Student / StudentDetail / Book / Department).
   - ⚠️ 그런데 **엔티티에서 `name`은 UNIQUE가 아니다** (`@Column(nullable = false)`만, `code`만 `unique = true`). 이름 중복은 **서비스 검사로만** 막고 있어 5-1절 ⑦의 경쟁 상태에 무방비다. 업무 규칙상 이름도 유일해야 한다면 `unique = true`를 추가하는 것이 일관적이다.
4. **`updateDepartment`에도 불필요한 `save()`** — 🔗 4-2절. 변경 감지로 충분하다.
5. **⚠️ `DepartmentDTO.Response.fromEntity()`의 숨은 비용 — 문서 서술과 코드가 다른 지점**
   ```java
   public static Response fromEntity(Department department) {
       return Response.builder()
               ...
               .studentCount((long) department.getStudents().size())      // ← 컬렉션 로딩
               .students(department.getStudents().stream()
                       .map(StudentDTO.SimpleResponse::fromEntity).toList())
               .build();
   }
   ```
   | 호출 경로 | `department.getStudents()`의 상태 | 추가 SQL |
   |---|---|---|
   | `getDepartmentById` / `getDepartmentByCode` | fetch join으로 **이미 로딩** | 없음 (1번) |
   | `createDepartment` | 새 객체라 `@Builder.Default`의 **빈 `ArrayList`** | 없음 → `"studentCount":0, "students":[]` |
   | **`updateDepartment`** | `findById`라 **미초기화 컬렉션** | **학생 목록 1번 + 학생마다 상세정보(mappedBy 1:1) N번** |
   | 5-8절 데모 (A)(B) | (A) fetch join / (B) 미초기화 | (B)는 학과마다 학생 조회 |
   - 문서 2-5는 "단건 조회와 등록/수정은 COUNT 쿼리 1번으로 학생 수를 채운다"고 적었지만, **이 저장소의 `Response.fromEntity`는 COUNT 쿼리가 아니라 컬렉션을 로딩해서 센다.** 학생 3명인 CS 학과를 수정하면 `findById`(1) + 학생 목록(1, batch size로 IN 절 가능) + 상세정보(3, **batch 안 됨**) + `exists` 검사 + UPDATE 가 나간다.
   - 개선하려면 수정 경로에서도 `findByIdWithStudents(id)`를 쓰거나, 응답에 학생 목록이 필요 없다면 `countByDepartmentId`로 숫자만 채우는 별도 팩토리를 둔다.
   - 🔗 4-7절 해설 4번 "`getStudents().size()`는 fetch join으로 가져온 경우에만 안전하다"의 **실제 위반 사례**가 수정 경로다.

### 코드 — `controller/DepartmentController.java` (1toN 업데이트본 기준 엔드포인트)

```java
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final StudentService studentService;           // ★ 두 서비스를 주입

    @GetMapping                  public ResponseEntity<List<DepartmentDTO.SimpleResponse>> getAllDepartments() { ... }
    @GetMapping("/{id}")         public ResponseEntity<DepartmentDTO.Response> getDepartmentById(@PathVariable Long id) { ... }
    @GetMapping("/code/{code}")  public ResponseEntity<DepartmentDTO.Response> getDepartmentByCode(@PathVariable String code) { ... }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<StudentDTO.Response>> getStudentsByDepartmentId(@PathVariable Long id) {
        List<StudentDTO.Response> students = studentService.getStudentsByDepartmentId(id);   // ★ StudentService
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO.Response> createDepartment(@Valid @RequestBody DepartmentDTO.Request request) {
        DepartmentDTO.Response createdDepartment = departmentService.createDepartment(request);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);                   // 201
    }

    @PutMapping("/{id}")         ... ResponseEntity.ok(...)                                   // 200
    @DeleteMapping("/{id}")      ... ResponseEntity.noContent().build()                       // 204
}
```

| 메서드 | URL | 설명 | 응답 |
|---|---|---|---|
| GET | `/api/departments` | 전체 학과 (+학생 수, Projection) | 200 |
| GET | `/api/departments/{id}` | 학과 상세 + 학생 목록 (fetch join) | 200 |
| GET | `/api/departments/code/{code}` | 학과코드로 조회 | 200 |
| GET | `/api/departments/{id}/students` | 학과별 학생 목록 | 200 / 404 |
| POST | `/api/departments` | 학과 등록 | **201** / 409 |
| PUT | `/api/departments/{id}` | 학과 수정 | 200 / 404 / 409 |
| DELETE | `/api/departments/{id}` | 학과 삭제 | **204** / 404 / **409(학생 있음)** |
| (paging) GET | `/api/departments/paged` | 페이징 목록 | 200 |
| (paging) GET | `/api/departments/{id}/students/paged` | 학과별 학생 페이징 | 200 / 404 |
| (paging) GET | `/api/departments/demo/fetch-join-paged` | **실험용** (A) | 200 |
| (paging) GET | `/api/departments/demo/plain-join-paged` | **실험용** (B) | 200 |

**해설**
1. **`/{id}/students`가 `StudentService`를 쓴다** — 응답이 `StudentDTO.Response` 목록이므로 학생 서비스의 책임이다. **컨트롤러는 URL 구조(학과 하위 자원)를 따르고, 서비스는 도메인(학생)을 따른다.** 🔗 PART 2 2-8절 REST 자원 계층 URL.
2. **`/code/{code}`로 경로를 나눈 것** — 🔗 4-1절 "`/number/{studentNumber}`가 3-5절 설계 문제의 정답"과 같은 방식.
3. **`/paged`와 `/{id}`의 충돌은 없다.** 둘 다 `GET /api/departments/xxx` 모양이지만, Spring MVC는 **리터럴 경로(`/paged`)를 변수 경로(`/{id}`)보다 우선**한다(더 구체적인 패턴이 이긴다). 선언 순서와 무관하다. 만약 이 규칙이 없었다면 `"paged"`를 `Long id`로 바꾸려다 **타입 변환 예외**가 났을 것이다(5-10절).
4. 문서 2-5의 **테스트 순서 권장**: Department를 먼저 만들고 Student를 만든다 / 존재하는 `departmentId`를 쓴다 / unique 제약(학과 코드·학번·이메일·전화번호)을 확인한다 / Student 삭제 시 StudentDetail이 함께 지워지는지(cascade) 확인한다.

---

## 5-5. N+1 전 구간 제거 — 문서 2-5의 실측 결과

4-6절은 N+1의 **원리**였다. 이 절은 **이 앱의 모든 엔드포인트에서 실제로 몇 번 나갔고, 무엇으로 줄였나**다.

### 개선 전에 있던 문제 3가지 (문서 2-5)

1. **`DepartmentDTO.SimpleResponse.fromEntity()`가 `department.getStudents().size()`를 호출**했다 → 학생 목록을 조회할 때마다 **각 학생이 속한 학과의 학생 컬렉션 전체를 로딩해서 개수만 세고 버렸다.**
2. **`Student.studentDetail`은 mappedBy `@OneToOne`** → 학생을 조회할 때마다 상세정보 쿼리가 따라붙었다(5-1절 ③).
3. **`DepartmentService.getAllDepartments()`가 학과마다 COUNT 쿼리**를 실행했다 → 학과 수만큼 쿼리.

### ⚠️ `default_batch_fetch_size`가 듣는 곳 / 안 듣는 곳

`application-prod.properties`에 `default_batch_fetch_size=100`이 켜져 있는데도(🔗 4-6절) N+1이 남았던 이유다.

| 연관관계 | 배치(IN 절) 적용 |
|---|---|
| `@OneToMany` 컬렉션 (`Department.students`) | **적용됨** — 여러 학과의 students를 IN 하나로 |
| `@ManyToOne` 프록시 (`Student.department`) | **적용됨** |
| **mappedBy `@OneToOne`** (`Student.studentDetail`) | **적용 안 됨** — 엔티티 로딩 시점에 개별 쿼리로 즉시 조회되므로 모아 둘 "미초기화 프록시"가 없다 |

**실측 로그 — `GET /api/departments/1` (개선 전, SQL 4번)**
```
1) select ... from departments d1_0 left join students s1_0 on ...
2) select ... from student_details sd1_0 where sd1_0.student_id=?
3) select ... from student_details sd1_0 where sd1_0.student_id=?
4) select ... from student_details sd1_0 where sd1_0.student_id=?
   -> 2)~4) 가 IN 절로 묶이지 않는다. 배치 설정이 듣지 않는 구간이다.
   -> 응답에는 학생의 id / name / studentNumber 만 쓰이는데도 상세정보를 조회한다.
```
→ **"배치 설정에만 의존하지 말고, 조회 메서드에서 fetch join으로 함께 가져와야 한다"** (문서 결론). 🔗 4-6절 "두 겹 방어"의 한계 — 배치는 **mappedBy 1:1 구간을 받쳐 주지 못한다**(〔v1.3 정정〕 참고).

**문서 2-5의 배치 크기 권장**
- `default_batch_fetch_size`는 **글로벌**, `@BatchSize`는 **개별** 최적화(컬렉션은 필드에, 엔티티는 클래스에 선언).
- **100~1000 사이** 권장. IN 절 파라미터를 1000개로 제한하는 DB가 있다.
- 1000으로 잡으면 한 번에 1000개를 로드해 **DB 순간 부하**가 커질 수 있다. 하지만 **애플리케이션 메모리 사용량은 100이든 1000이든 같다** — 결국 전체를 다 로딩하므로.

### 개선 — Repository

```java
// StudentRepository : 목록·학번·학과별 조회 전부 "상세정보 + 학과" 를 함께
@Query("SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department")
List<Student> findAllWithDetails();                     // (구) findAllWithStudentDetail()

@Query("SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department "
        + "WHERE s.id = :id")
Optional<Student> findByIdWithAllDetails(@Param("id") Long id);   // getStudentById 가 사용

@Query("SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department "
        + "WHERE s.studentNumber = :studentNumber")
Optional<Student> findByStudentNumber(@Param("studentNumber") String studentNumber);
```

```java
// DepartmentRepository : 학과 상세는 학생 + 학생의 상세정보까지
//학생마다 발생하던 상세정보 조회를 없애기 위해 studentDetail 까지 함께 가져온다
@Query("SELECT d FROM Department d "
        + "LEFT JOIN FETCH d.students s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "WHERE d.id = :id")
Optional<Department> findByIdWithStudents(@Param("id") Long id);

//학과코드로 조회할 때에도 소속 학생을 함께 가져온다 ( 지연로딩 추가 조회를 없앤다 )
@Query("SELECT d FROM Department d "
        + "LEFT JOIN FETCH d.students s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "WHERE d.code = :code")
Optional<Department> findByCodeWithStudents(@Param("code") String code);
```

**해설**
1. **ToOne은 몇 개를 fetch join해도 된다** — `studentDetail`(1:1) + `department`(N:1)를 한 쿼리에. 행이 늘지 않기 때문.
2. **`findByIdWithStudents`에 `s.studentDetail`까지 붙은 이유** — 학과 상세 응답은 학생의 id/name/studentNumber만 쓰는데도, 학생 엔티티를 로딩하는 순간 mappedBy 1:1 쿼리가 따라붙는다(5-1 ③). **응답에 안 쓰는 연관이라도 "로딩 자체가 쿼리를 부르면" fetch join 대상**이 된다. 🔗 4-6절 "컬렉션 1개 + 단건 1개 = 제약 준수"의 코드가 이것.
3. **`findByCode` → `findByCodeWithStudents`** — 기존 `findByCode`(파생 쿼리)는 학과만 가져와서 `Response.fromEntity`가 지연 로딩을 일으켰다. `findByCode`는 레포지토리에 남아 있지만 서비스는 더 이상 쓰지 않는다.
4. **`getStudentById`가 `findByIdWithStudentDetail` → `findByIdWithAllDetails`로 교체** — `Response.fromEntity`가 이제 학과(`department`)도 건드리므로. 🔗 4-6절 "DTO 변환 시점에 어떤 필드를 건드리는지가 곧 어떤 fetch join이 필요한지를 결정한다"의 실례. `findByIdWithStudentDetail`은 이제 **아무도 호출하지 않는다**(레포지토리에만 남음).

### 개선 — DTO에서 컬렉션 접근 제거

```java
// DepartmentDTO.SimpleResponse
/**
 * 학과의 기본 정보만 변환한다.
 * students 컬렉션에 접근하지 않으므로 지연로딩 추가 조회가 발생하지 않는다.
 * studentCount 는 채우지 않으므로 응답 JSON 에 나타나지 않는다.
 */
public static SimpleResponse fromEntity(Department department) {
    return SimpleResponse.builder()
            .id(department.getId())
            .name(department.getName())
            .code(department.getCode())            // 기존 : department.getStudents().size()
            .build();
}
```

**⚠️ "학생 응답에는 학과의 학생 수를 담지 않는다" — 문서 2-5가 든 이유 3가지**
1. 학생을 조회하는 요청인데 학과 통계가 섞이면 **응답의 목적이 흐려진다.**
2. 같은 학과 학생이 여러 명이면 **같은 숫자가 반복되는 중복 데이터**가 된다.
3. 학과별 학생 수는 **`GET /api/departments`가 이미 제공**한다.

```
GET /api/students       "department": { "id":1, "name":"Computer Science", "code":"CS" }
GET /api/departments    { "id":1, "name":"Computer Science", "code":"CS", "studentCount":3 }
```
🔗 4-7절 `@JsonInclude(NON_NULL)` 해설 — **같은 `SimpleResponse`가 두 경로에서 다른 모양으로 나가는 것**이 이 설계 결정의 결과다. 순환 참조를 `SimpleResponse`로 끊는 이유(4-7 해설 1)와, `@JsonIgnore` 대신 DTO를 쓰는 이유("엔티티에 화면 표현 관심사를 넣지 않아도 된다")도 문서 2-5가 같은 논리로 설명한다.

### 측정 결과 — 학생 8명 / 학과 4개 (문서 2-5)

| 엔드포인트 | 개선 전 | 개선 후 |
|---|---|---|
| `GET /api/students` | 1 + N + N | **1** |
| `GET /api/students/1` | 1 + 컬렉션 로딩 | **1** |
| `GET /api/students/number/CS001` | 2 + 컬렉션 로딩 | **1** |
| `GET /api/departments` | 1 + 4 | **1** |
| `GET /api/departments/1` | 4 | **1** |
| `GET /api/departments/code/CS` | 4 | **1** |
| `GET /api/departments/1/students` | 1 + N | **2** (존재 확인 + 목록) |
| `GET /api/students/detail` (강사 저장소 전용) | 1 + N + N | 1 |

**해설**: 전부 **데이터 건수와 무관한 상수**가 되었다. 학생이 100명이어도 그대로다. "1 + N + N"은 학생마다 **상세정보(N) + 소속 학과의 학생 컬렉션(N)** 이 따라붙던 것 — 위 문제 1·2번의 합이다.

**참고 — 1:1 단계(문서 2-4)의 측정**

| 메서드 | 개선 전 | 개선 후 |
|---|---|---|
| `findAllWithStudentDetail()` | 1 + N | 1 |
| `findByIdWithStudentDetail()` | 1 | 1 |
| `findByStudentNumber()` | 2 | 1 |

`findByStudentNumber`가 **"2 → 1"** 인 이유: 파생 쿼리 버전은 `select ... from students where student_number=?` + (mappedBy 1:1 즉시 조회) `select ... from student_details where student_id=?` 두 번이었다. 🔗 4-5절 해설 3번 "`findByStudentNumber`가 이름 규칙에 맞는데도 `@Query`를 붙인 이유"의 실측 근거.

### `findAll()` 방식이 "틀린 코드"는 아니다 (문서 2-5)

| | `findAll()` 방식 | Projection 방식 |
|---|---|---|
| SQL 개수 | 10번 | **1번** |
| DB에서 읽는 행 | 20행 (학과4 + 학생8 + 상세8) | **4행** |
| 학생 1,000명이라면 | 1,002번 | **1번** |

- `findAll()` 방식의 SQL 10번 = 학과 1 + 학생 1(**batch size 덕분에 4개 학과의 students를 IN 하나로**) + 상세정보 8(**batch 안 됨**). 학생 1,000명이면 1 + 1 + 1,000 = 1,002.
- ⚠️ 문서의 핵심 문장: **"`findAll()`이 틀린 코드는 아니다. 동작도 정상이고 응답도 같다. 다만 '개수만 필요한데 전체를 읽어 온다'는 점 때문에 쿼리가 10배가 되었다."** 조회한 엔티티를 **실제로 쓸 때**는 `findAll()`이나 fetch join이 맞다 — `getDepartmentById()`가 학생 목록을 응답에 담아야 하므로 fetch join을 쓰는 것처럼.

---

## 5-6. Projection 심화 — 문서 2-5가 보태는 것

🔗 4-7절이 인터페이스 Projection 하나를 코드로 설명했다. 문서 2-5는 **개념과 나머지 방식들**을 보탠다.

### 개념 — 관계대수의 Projection

- **Selection(선택)** = 행을 고른다 (`WHERE`) / **Projection(투영)** = 열을 고른다 (`SELECT 컬럼`)
- JPA에서 "엔티티 조회"는 **모든 열을 가져와 객체로 만드는 것**, "Projection"은 **필요한 열만 가져오는 것**이다.

| | 엔티티 조회 | Projection |
|---|---|---|
| 가져오는 것 | 모든 컬럼 + (연관 로딩) | 지정한 값만 |
| 영속성 컨텍스트 | 관리됨 (변경 감지 O) | **관리 안 됨** (값을 바꿔도 UPDATE 없음) |
| 용도 | 수정·연관 탐색 | **조회 전용**, 집계 |

### Projection 3가지 방식

```java
// ① Object[] — 가장 원시적
@Query("SELECT d.name, COUNT(s) FROM Department d LEFT JOIN d.students s GROUP BY d.name")
List<Object[]> countByDept();
// row[0] = (String) 이름, row[1] = (Long) 개수 — 형변환을 손으로 해야 한다

// ② 인터페이스 Projection — 이 저장소가 채택한 방식
public interface DepartmentSummary { Long getId(); String getName(); String getCode(); Long getStudentCount(); }
@Query("SELECT d.id AS id, ... COUNT(s) AS studentCount FROM Department d LEFT JOIN d.students s GROUP BY ...")
List<DepartmentSummary> findAllSummaries();

// ③ 생성자(클래스) Projection — JPQL 의 new 연산자
@Query("SELECT new com.example.dto.DeptDto(d.id, d.name, COUNT(s)) "
     + "FROM Department d LEFT JOIN d.students s GROUP BY d.id, d.name")
List<DeptDto> findDeptDtos();
// ⚠️ 패키지까지 포함한 전체 클래스명(FQCN)을 써야 하고, 인자 순서·타입이 생성자와 정확히 맞아야 한다
```

### 인터페이스 Projection의 동작 원리 (문서 2-5)

```
@Query("SELECT d.id AS id, d.name AS name, COUNT(s) AS studentCount ...")
                         |             |                    |
public interface DepartmentSummary {   |                    |
    Long   getId();          <---------+                    |
    String getName();        <----------------+             |
    Long   getStudentCount();<------------------------------+
}
  AS 별칭 studentCount  ->  getter 이름 getStudentCount()
  ( get 을 떼고 첫 글자를 소문자로 바꾼 이름이 별칭과 같아야 한다 )
```

**⚠️ 주의할 점 (문서 2-5)**
1. **별칭을 빠뜨리거나 이름이 다르면 `null`** — "가장 흔한 실수" (🔗 4-7 해설 1)
2. **Projection 결과는 엔티티가 아니다.** 값을 바꿔도 UPDATE가 안 나간다. 수정이 필요하면 엔티티를 조회해야 한다.
3. **`SELECT d FROM Department d`처럼 엔티티를 통째로 선택하면 Projection이 적용되지 않는다.** 컬럼을 하나씩 별칭과 함께 나열해야 한다.
4. **`@Query` 없이 메서드 이름 규칙만으로도 쓸 수 있다.** 반환 타입만 인터페이스로 바꾸면 된다. 예) `List<DepartmentSummary> findByCode(String code);` (단 이 경우 getter는 **엔티티 필드 이름과 대응**해야 하므로 `COUNT` 같은 계산 필드는 만들 수 없다 — `getStudentCount()`처럼 엔티티에 없는 이름이 섞이면 쿼리 생성 단계에서 오류가 날 수 있으니, 이름 규칙용 인터페이스는 따로 두는 편이 안전하다)
5. **Closed vs Open Projection**
   - **Closed**: getter가 실제 컬럼과 1:1 대응 → **필요한 컬럼만 SELECT** → 성능 이점
   - **Open**: `@Value("#{target.name + target.code}")`처럼 **SpEL 계산식** → **엔티티 전체를 읽은 뒤** 계산하므로 최적화 효과 없음. 🔗 PART 2 `@Value("#{...}")` SpEL과 같은 문법
6. ⚠️ **Projection에 `FETCH`를 섞으면 애플리케이션이 기동하지 않는다** — 5-8절 실험 (3).

---

## 5-7. 페이징 기초 — `Pageable` · `Page` · `Slice` (paging 버전)

### 먼저 — Spring Data Core와 Spring Data JPA는 다른 라이브러리다 (문서 2-6)

```
             spring-data-commons  ( Spring Data Core )
             Repository, CrudRepository, PagingAndSortingRepository
             Page, Pageable, PageRequest, Slice, Sort, Example, Limit
                              ▲
        ┌─────────────────────┼─────────────────────┬──────────────┐
   spring-data-jpa      spring-data-mongodb    spring-data-redis   -jdbc
   JpaRepository        MongoRepository        ...                 ...
   @Query(JPQL)         @Query(JSON)
   @Modifying, @EntityGraph, @Lock, @QueryHints
   JpaSpecificationExecutor
```

**Repository 인터페이스 계층 (문서 2-6 그림)**
```
                      Repository
                     ↗          ↖
     CrudRepository<T,ID>        PagingAndSortingRepository<T,ID>
     save / findById /            (findAll(Sort), findAll(Pageable) 선언)
     findAll(): Iterable / delete           ↑
            ↑                    ListPagingAndSortingRepository<T,ID>
     ListCrudRepository<T,ID>     findAll(Sort): List / findAll(Pageable): Page
     findAll(): List / saveAll              ↑
                     ↖                    ↗
                       JpaRepository<T,ID>
                       findAll(Example) / flush() / getOne(ID) ...
                              ↑
                       (우리의) StudentRepository
```
**`Page`/`Pageable` 타입 관계 (문서 2-6 그림)**
```
  «interface» Slice  ──uses──▶  «interface» Pageable               PageRequest
  getPageable(), getSort()      first(), getOffset(), getPageNumber(),   implements Pageable
        ▲                       getPageSize(), getSort(), hasPrevious(),  PageRequest(page, size, sort)
  «interface» Page              next()
  map(converter: Function)
```

**해설**
- **`Pageable`·`Page`·`Sort`는 JPA 기능이 아니라 Spring Data 공통 기능**이다(`Pageable.class`는 commons jar에만 있다). 그래서 MongoDB로 바꿔도 `Pageable`을 넘기고 `Page`를 받는 방식은 똑같다.
- **왜 나눴나** — 여러 저장소 모듈이 같은 계약을 재사용하고, 개선이 모든 모듈에 한 번에 적용되도록.
- ⚠️ **그렇다고 "저장소를 바꿔도 코드가 그대로"는 아니다** (문서 2-6)

  | 바뀌지 않는 것 | 바뀌는 것 |
  |---|---|
  | `Pageable`/`Page` 사용법 | 상속할 인터페이스 (`JpaRepository` ↔ `MongoRepository`) |
  | Query Method 이름 규칙 | `@Query` 내용 (JPQL ↔ JSON 쿼리) |
  | `Sort`, Projection | 엔티티 매핑 (`@Entity` ↔ `@Document`) |
- 🔗 3-9절 `ListCrudRepository`(`UserInfoRepository`) — 위 계층도에서 `CrudRepository`의 `List` 반환 버전. `JpaRepository`는 **양쪽 가지를 모두 상속**하므로 페이징도 되고 `List`도 돌려준다.

### 페이징의 본질 — `LIMIT` / `OFFSET` (문서 2-6)

```sql
SELECT * FROM students ORDER BY student_id LIMIT 3 OFFSET 0;   -- 1페이지 : 0건 건너뛰고 3건
SELECT * FROM students ORDER BY student_id LIMIT 3 OFFSET 3;   -- 2페이지 : 3건 건너뛰고 3건
SELECT * FROM students ORDER BY student_id LIMIT 3 OFFSET 6;   -- 3페이지 : 6건 건너뛰고 3건
     LIMIT  = 몇 건 가져올 것인가   ( = pageSize )
     OFFSET = 몇 건 건너뛸 것인가   ( = pageNumber * pageSize )
```

**⚠️ 문법이 DB마다 다르다 → Dialect가 대신 처리한다**

| DB | 같은 "3건 건너뛰고 3건" |
|---|---|
| MySQL / MariaDB (PostgreSQL·H2도 지원) | `LIMIT 3 OFFSET 3` 또는 **`LIMIT 3, 3`** (건너뛸 수, 가져올 수) |
| 표준 SQL:2008 · Oracle 12c+ · SQL Server 2012+ | `OFFSET 3 ROWS FETCH FIRST 3 ROWS ONLY` |
| Oracle 11g 이하 | `ROWNUM` 중첩 서브쿼리 |
| SQL Server 2008 이하 | `ROW_NUMBER() OVER (...)` + CTE |

```
GET /api/students/paged?page=1&size=3   ( MariaDB 로그 )
Hibernate: select ... from students s1_0
             left join student_details sd1_0 on ...
             left join departments d1_0 on ...
            order by s1_0.student_id asc
            limit ?,?                         ← 3 (offset), 3 (size)
Hibernate: select count(s1_0.student_id) from students s1_0
```
- **MariaDB의 `limit ?,?`는 `offset, size` 순서**다 — `LIMIT size OFFSET offset`과 숫자 순서가 **반대**라 로그 읽을 때 주의.
- `page=0`이면 건너뛸 것이 없어 `limit ?` 하나만 붙는다 (H2는 `fetch first ? rows only`).
- 🔗 3-4절 Dialect / 4-5절 "JPQL → Dialect에 맞는 SQL로 번역". **`Pageable`을 쓰는 가장 큰 이유가 이것** — DB를 바꿔도 코드를 고칠 필요가 없다.

| 페이징 방식 | 페이징 구문 | 정렬 기준 | Fetch Join |
|---|---|---|---|
| Query Method | 자동 | 엔티티 필드명 | 불가 (`@Query` 필요) |
| `@Query` (JPQL) | 자동 | 엔티티 필드명 | **가능** ← 이 저장소 |
| `@Query` (Native) | 자동 | **실제 컬럼명** | 불가 |
| 직접 SQL | 직접 작성 | 컬럼명 | 해당 없음 |

### `Pageable` 만들기와 속성

```java
Pageable pageable = PageRequest.of(0, 10);                                   // 0번 페이지, 10건
Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());      // 이름 오름차순
Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "id")); // 1번 페이지, id 내림차순
```
- ⚠️ **page는 0부터 시작한다.** 첫 페이지가 1이 아니다 — "가장 흔한 실수" (문서).

| 속성 | 의미 | 누가 정하나 |
|---|---|---|
| `pageNumber` | 몇 번째 페이지 (0부터) | 클라이언트 |
| `pageSize` | 한 페이지 건수 | 클라이언트 |
| `sort` | 정렬 | 클라이언트 |
| `offset` | 앞에서 건너뛸 건수 | **계산됨** (`page * size`) |
| `paged` / `unpaged` | 페이징 적용 여부 | 계산됨 / `Pageable.unpaged()` |

학생 8명을 `size=3`으로 조회한 실제 결과 (문서 2-6)
```
 page   size   offset   조회된 건수   학생 id
   0      3      0        3            1, 2, 3
   1      3      3        3            4, 5, 6
   2      3      6        2            7, 8          ← 남은 2건
```
`pageable.next()` / `previousOrFirst()` / `first()` / `withPage(3)` / `Pageable.unpaged()`로 다른 페이지를 만든다.

### `Page<T>` 응답 JSON — 필드 읽는 법 (문서 2-6, 전체 8명)

```
GET /api/students/paged?page=1&size=3&sort=name,asc
{
  "content": [ ... ],                  3건 — 현재 페이지 데이터
  "pageable": { "pageNumber": 1, "pageSize": 3, "offset": 3,
                "sort": { "sorted": true, ... }, "paged": true, "unpaged": false },
  "totalElements": 8,                  전체 건수      ( count 쿼리로 구함 )
  "totalPages": 3,                     전체 페이지 수  ( 8 / 3 올림 )
  "number": 1,                         현재 페이지 번호
  "size": 3,                           페이지 크기
  "numberOfElements": 3,               이 페이지에 실제로 담긴 건수
  "first": false, "last": false, "empty": false,
  "sort": { "sorted": true, ... }
}
```
- ⚠️ **`size` ≠ `numberOfElements`** — 마지막 페이지(page 2)는 `size=3`이지만 `numberOfElements=2`.
- **`totalElements`·`totalPages`는 count 쿼리를 실행해야 알 수 있는 값**이다.
- `first`/`last`로 화면의 이전/다음 버튼 활성화를 판단한다.

### 코드 — Controller에서 `Pageable` 받기

```java
// StudentController (paging 버전)
@GetMapping("/paged")
public ResponseEntity<Page<StudentDTO.Response>> getAllStudentsPaged(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable) {
    Page<StudentDTO.Response> students = studentService.getAllStudents(pageable);
    return ResponseEntity.ok(students);
}

// 페이징 처리 없는 학생 목록 조회 — 기존 엔드포인트는 그대로 유지
@GetMapping
public ResponseEntity<List<StudentDTO.Response>> getAllStudents() { ... }
```
```
요청 :  GET /api/students/paged?page=0&size=10&sort=name,asc
        page / size / sort 세 파라미터가 Pageable 하나로 바인딩된다
```

**해설**
1. **`Pageable` 파라미터는 스프링이 요청 파라미터에서 자동으로 만들어 준다.** (`PageableHandlerMethodArgumentResolver`가 담당한다 — 컨트롤러 파라미터 타입을 보고 값을 만들어 넣는 "인자 해석기"). `@RequestParam`을 하나하나 받지 않아도 되는 이유. 🔗 3-5절 `@PathVariable`·`@RequestBody`도 같은 구조로 채워진다.
2. **`@PageableDefault`** — 파라미터가 없을 때의 기본값. **생략하면 Spring Boot 기본 `size=20`**. 이 저장소는 모든 페이징 엔드포인트에 `size = 10`을 명시했다.
3. ⚠️ **최대 크기 기본값은 2000** — `size=1000000`을 보내도 2000으로 잘린다. `spring.data.web.pageable.max-page-size`로 조정. (악의적 대량 요청 방어)
4. **정렬 파라미터** — `sort=name,asc` / `sort=id,desc` / `sort=name,asc&sort=id,desc`(다중 정렬: 이름 같으면 id 내림차순).
5. **비페이징 엔드포인트를 지우지 않고 `/paged`를 추가**한 것 — 기존 클라이언트를 깨지 않는 **하위 호환**. 🔗 5-1 ⑨ "Controller는 안 바뀐다".

### 코드 — Service에서 `Page.map()`으로 DTO 변환

```java
// StudentService (paging 버전)
public Page<StudentDTO.Response> getAllStudents(Pageable pageable) {
    //findAll() 대신 Fetch Join 을 사용하여 N+1 문제를 해결한다
    Page<Student> students = studentRepository.findAllWithDetails(pageable);
    return students.map(StudentDTO.Response::fromEntity);
}

public Page<StudentDTO.Response> getStudentsByDepartmentId(Long departmentId, Pageable pageable) {
    if (!departmentRepository.existsById(departmentId)) { throw ... RESOURCE_NOT_FOUND }
    Page<Student> students = studentRepository.findByDepartmentId(departmentId, pageable);
    return students.map(StudentDTO.Response::fromEntity);
}

// DepartmentService (paging 버전)
public Page<DepartmentDTO.SimpleResponse> getAllDepartments(Pageable pageable) {
    return departmentRepository.findAllSummaries(pageable)
            .map(DepartmentDTO.SimpleResponse::fromSummary);
}
```
- **`Page.map()`은 페이지 정보(`totalElements`, `totalPages`, `number` …)는 그대로 두고 `content` 안의 요소만 변환**한다. `List`처럼 `.stream().map().toList()`를 하면 페이지 정보가 사라지므로 **`Page` 자체의 `map`을 쓴다.** 🔗 B-1 Stream의 `map`과 이름이 같은 이유 — "상자는 그대로, 내용물만 바꾼다"는 같은 발상(함수형의 functor).
- **같은 이름의 메서드가 `(Pageable)` 유무로 오버로딩**되어 있다 — `getAllStudents()` / `getAllStudents(Pageable)`. 🔗 PART 1 1-8절 오버로딩. 반환 타입이 `List` / `Page`로 다른 것은 오버로딩 조건과 무관(매개변수가 다르므로 성립).

### `Page` / `Slice` / `List`

| 반환 타입 | count 쿼리 | 알 수 있는 것 | 쓰임 |
|---|---|---|---|
| `Page<T>` | **실행함** | 전체 건수, 전체 페이지 수, 마지막 여부 | 페이지 번호 UI |
| `Slice<T>` | 실행 안 함 | **다음 페이지가 있는지만** | **무한 스크롤** |
| `List<T>` | 실행 안 함 | 목록만 (페이징 정보 없음) | 단순 조회 |

- `Slice`는 **`size + 1`건을 조회**해서 한 건이 더 오면 "다음 있음"으로 판단한다. count 쿼리가 없어 빠르다.
- 전체 페이지 수를 화면에 표시하지 않는다면 `Slice`가 낫다 (문서 2-6).

### ⚠️ count 쿼리가 "생략"되는 경우 — 문서 측정표의 비밀

문서 2-6의 측정표에 이런 줄이 있다.
```
GET /api/students/paged                   1 번   ← count 생략
GET /api/students/paged?page=1&size=3     2 번   ( 목록 + count )
GET /api/departments/paged                1 번   ← count 생략
```
- Spring Data는 **첫 페이지인데 가져온 건수가 `size`보다 적으면** "이게 전부다"라고 판단해 **count 쿼리를 생략**하고 `totalElements = 가져온 건수`로 채운다. 기본 `size=10`인데 학생이 8명이므로 한 번에 끝난 것.
- (마지막 페이지처럼 offset + 건수로 전체를 확정할 수 있을 때도 생략된다.)
- ⚠️ 이 최적화가 5-8절 실험 (2)에서 **`totalElements`까지 틀어지게** 만든다.

### 정렬은 반드시 지정한다 / OFFSET의 한계 (문서 2-6)

- 정렬이 없으면 DB가 순서를 보장하지 않는다 → **1페이지에 나온 행이 2페이지에 또 나오거나, 아예 빠질 수 있다.** 그래서 `@PageableDefault(sort = "id")`처럼 기본값을 둔다.
- `LIMIT 100000, 10`은 **앞의 10만 행을 읽고 버린 뒤** 10행을 준다 → 뒤 페이지일수록 느리다.
- 수만 건 이상이거나 뒤 페이지를 자주 본다면 **커서(keyset) 페이징**: `WHERE id > 마지막으로본ID ORDER BY id LIMIT 10`. 이 실습 규모에서는 OFFSET으로 충분하다.

### ⚠️ `Page`를 그대로 JSON으로 내보낼 때의 경고 (코드에는 없는 보충)

이 저장소는 컨트롤러가 `ResponseEntity<Page<...>>`를 **그대로 반환**한다. 최근 Spring Data(3.3 이후)는 이때 콘솔에 이런 경고를 남긴다.
```
Serializing PageImpl instances as-is is not supported, meaning that there is no guarantee
about the stability of the resulting JSON structure!
For a stable JSON structure, please use Spring Data's PagedModel
(globally via @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO))
```
- 뜻: 위의 `"pageable"`, `"sort"` 같은 **내부 구조가 버전에 따라 바뀔 수 있으니 API 응답 계약으로 믿지 말라**는 것.
- `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)`를 켜면 응답이 `{"content":[...], "page":{"size":..,"number":..,"totalElements":..,"totalPages":..}}` 형태의 **안정된 DTO**로 바뀐다.
- 실습에서는 경고만 뜨고 동작에는 문제가 없다. 🔗 3-6절 "Entity를 그대로 내보내지 말고 DTO로" — **`Page`라는 프레임워크 객체도 같은 원칙의 대상**이라는 것.

---

## 5-8. 페이징 + Fetch Join — 이번 진도의 핵심 함정

🔗 4-6절 "fetch join의 제약 1번 — 컬렉션 fetch join은 페이징이 불가능하다"를 **실제 코드·로그로 실험**한 것이 이 절이다.

### ① JOIN과 FETCH는 "다른 축"이다 (문서 2-6)

- **`LEFT` / `INNER`** → **어떤 행을 남길지** (짝이 없는 행을 버릴까?)
- **`FETCH`** → **조인한 엔티티를 함께 로딩할지** (SELECT 절에 넣을까?)
- 그래서 `LEFT JOIN FETCH`처럼 **겹쳐 쓴다.**

**FETCH 유무에 따른 실제 SQL**
```
-- FETCH 없음 : students 를 조인만 하고 컬럼은 가져오지 않는다
SELECT d FROM Department d LEFT JOIN d.students s
   -> select d1_0.department_id, d1_0.code, d1_0.name
      from departments d1_0 left join students s1_0 on ...

-- FETCH 있음 : students 컬럼까지 가져와 d.students 를 채운다
SELECT d FROM Department d LEFT JOIN FETCH d.students
   -> select d1_0.department_id, d1_0.code, d1_0.name,
             s1_0.student_id, s1_0.name, s1_0.student_number      <- 학생 컬럼이 추가된다
      from departments d1_0 left join students s1_0 on ...
```

| 쿼리 | 남는 행 | 연관 엔티티 로딩 | 이 저장소의 사용처 |
|---|---|---|---|
| `JOIN` | 짝이 있는 것만 | 안 함 | — |
| `LEFT JOIN` | 짝이 없어도 유지 | 안 함 | `findAllSummaries()` — `COUNT(s)` **집계용** |
| `JOIN FETCH` | 짝이 있는 것만 | 함 | `StudentDetailRepository.findByIdWithStudent` (상세→학생은 항상 존재) |
| `LEFT JOIN FETCH` | 짝이 없어도 유지 | 함 | `findAllWithDetails()`, `findByIdWithStudents()` 등 대부분 |

**한 줄 정리 (문서)**: *LEFT는 "누구를 남길 것인가"(행), FETCH는 "같이 들고 올 것인가"(로딩).*

### ② ⚠️ FETCH한 컬렉션에 조건을 걸지 않는다 — "반쪽짜리 컬렉션"

```sql
-- 위험 : CS 로 시작하는 학생만 d.students 에 담긴다
SELECT d FROM Department d LEFT JOIN FETCH d.students s WHERE s.studentNumber LIKE 'CS%'

-- 안전 : 조건이나 집계가 목적이면 FETCH 없는 일반 JOIN 을 쓴다
SELECT d FROM Department d LEFT JOIN d.students s WHERE s.studentNumber LIKE 'CS%'
```
- FETCH는 "이 컬렉션을 **DB 결과로 채워라**"라는 뜻이다. WHERE로 일부만 남기면 **조건에 맞는 자식만 담긴 컬렉션**이 영속성 컨텍스트에 올라간다.
- 그 상태에서 `department.getStudents()`를 쓰면 **나머지 학생이 사라진 것처럼** 보이고, 같은 트랜잭션의 다른 코드도 그 반쪽 컬렉션을 본다. (최악의 경우 cascade/orphanRemoval과 겹치면 데이터 손상)
- 🔗 5-1 ④ "fetch join 대상은 WHERE에 쓰지 않는 것이 원칙"의 이유가 이것이다.

### ③ 페이징에서도 N+1은 그대로 생긴다

- `Pageable`은 **한 번에 읽는 건수만** 줄인다. 학생마다 따라붙는 추가 쿼리는 그대로다.
- 문서 2-6 실측: 학생 5명을 fetch join 없이 페이징하면 **SQL 8번**(목록 1 + count 1 + 상세정보 5 + 학과 1), `LEFT JOIN FETCH` 적용 시 **2번**(목록 + count).
  - "학과 1"인 이유 — `@ManyToOne` 프록시는 `default_batch_fetch_size`로 IN 하나에 묶이고, 상세정보 5는 mappedBy 1:1이라 묶이지 않는다(5-5절 표 그대로).

### ④ 핵심 규칙 — ToOne은 OK, 컬렉션은 금지

| 연관관계 | Fetch Join + `Pageable` | 이유 |
|---|---|---|
| `@ManyToOne`, `@OneToOne` | **사용 가능** (DB가 `limit`으로 페이징) | 짝이 하나라 **행이 늘지 않는다** |
| `@OneToMany` (컬렉션) | **사용 금지** (메모리 페이징 경고) | **자식 수만큼 행이 늘어난다** |

```java
// StudentRepository (paging 버전) — 클래스 위 주석
/*
 * [ 페이징과 Fetch Join ]
 *
 *   @ManyToOne, @OneToOne 같은 ToOne 연관관계는 Fetch Join 을 해도 결과 행이 늘어나지 않으므로
 *   Pageable 과 함께 사용해도 안전하다. 데이터베이스가 LIMIT / OFFSET 으로 페이징한다.
 *
 *   반면 @OneToMany 컬렉션을 Fetch Join 하면 행이 늘어나기 때문에
 *   Pageable 과 함께 쓰면 Hibernate 가 전체를 읽어 메모리에서 페이징한다. ( 위험 )
 */
@Query("SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department")
Page<Student> findAllWithDetails(Pageable pageable);      // ToOne 두 개 → 안전
```
- CS 학과에 학생이 3명이면 조인 결과는 **CS 3행**이다. DB의 `limit`은 **행**을 자르므로 "학과 2개"를 정확히 셀 수 없다.
- 그래서 Hibernate는 SQL에서 `limit`을 **빼고 전체를 읽은 뒤 메모리에서 잘라내며** 경고를 남긴다.
  ```
  WARN HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
  ```
  (해석: 컬렉션 Fetch Join에 페이징이 지정되어, 페이징을 메모리에서 처리합니다.)

### ⑤ 같은 메서드에 쿼리를 바꿔 넣어 본 4가지 실험 (문서 2-6)

대상: `DepartmentRepository.findAllSummaries(Pageable)` / `page=0, size=2, sort=id` / 데이터: CS 3명, EE 2명, ME 1명, BA 2명 / MariaDBDialect 로그

**(1) 컬렉션 `LEFT JOIN FETCH` + 페이징 → 경고, 전체를 읽는다**
```java
@Query(value = "SELECT d FROM Department d LEFT JOIN FETCH d.students",
       countQuery = "SELECT COUNT(d) FROM Department d")
Page<Department> findAllSummaries(Pageable pageable);
```
```
WARN HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
select d1_0.department_id,d1_0.code,d1_0.name,
       s1_0.department_id,s1_0.student_id,s1_0.name,s1_0.student_number
from departments d1_0 left join students s1_0 on d1_0.department_id=s1_0.department_id
order by d1_0.department_id                         <- limit 이 없다
select count(d1_0.department_id) from departments d1_0
결과 : 학과 2개 ( CS 3명, EE 2명 ),  totalElements = 4
```
- **결과는 맞다.** 하지만 모든 학과·학생을 읽은 뒤 2개만 돌려준다 → 데이터가 많아지면 느려지고 **`OutOfMemoryError`** 로 이어질 수 있다.
- ⚠️ **경고만 나오고 예외가 없어서 놓치기 쉽다.** `countQuery`를 지정해도 경고는 사라지지 않는다.

**(2) FETCH만 제거 → `limit`은 붙지만 결과가 틀린다**
```java
@Query(value = "SELECT d FROM Department d LEFT JOIN d.students",
       countQuery = "SELECT COUNT(d) FROM Department d")
Page<Department> findAllSummaries(Pageable pageable);
```
```
select d1_0.department_id,d1_0.code,d1_0.name
from departments d1_0 left join students s1_0 on d1_0.department_id=s1_0.department_id
order by d1_0.department_id limit ?                  <- limit 이 있다
select s1_0.department_id,s1_0.student_id,s1_0.name,s1_0.student_number
from students s1_0 where s1_0.department_id=?        <- 지연 로딩
결과 : 학과 1개 ( CS ),  totalElements = 1          <- 2개를 요청했는데 1개
```
- `limit 2`가 **CS의 행 2개**를 가져갔고, 학과로 합치니 1개가 되었다.
- ⚠️ 그리고 **첫 페이지가 `size`보다 적게 채워지자 Spring Data가 count 쿼리를 생략**해서(5-7절) `totalElements`까지 **1로 틀어졌다.** `countQuery`를 정확히 써 줬는데도 실행 자체가 안 된 것.

**(3) Projection에 FETCH를 섞으면 → 애플리케이션이 시작되지 않는다**
```java
@Query(value = "SELECT d.id AS id, d.name AS name, d.code AS code, COUNT(s) AS studentCount "
        + "FROM Department d LEFT JOIN FETCH d.students s "
        + "GROUP BY d.id, d.name, d.code ORDER BY d.id")
Page<DepartmentSummary> findAllSummaries(Pageable pageable);
```
```
Caused by: org.hibernate.query.SemanticException: Query specified join fetching,
  but the owner of the fetched association was not present in the select list
```
- 해석: FETCH를 지정했지만 **연관관계의 주인(`d`)이 SELECT 절에 없다.** FETCH는 **엔티티를 조회할 때만** 의미가 있다 — 값(`d.id`, `COUNT(s)`) 조회와는 함께 쓸 수 없다.
- ⚠️ Spring Data가 **기동 시점에 `@Query`를 검증**하므로 경고가 아니라 **시작 실패(`BeanCreationException`)** 로 나타난다. 🔗 3-2절 `@ConfigurationProperties` 타입 불일치가 기동 시점에 에러가 나는 것과 같은 "빠른 실패(fail-fast)" — 런타임까지 가지 않고 시작할 때 터지는 편이 오히려 안전하다.

**(4) 일반 JOIN + GROUP BY Projection → 정상 (이 저장소의 코드)**
```java
@Query(value = "SELECT d.id AS id, d.name AS name, d.code AS code, COUNT(s) AS studentCount "
        + "FROM Department d LEFT JOIN d.students s "          // ← FETCH 가 아니다
        + "GROUP BY d.id, d.name, d.code",
        countQuery = "SELECT COUNT(d) FROM Department d")
Page<DepartmentSummary> findAllSummaries(Pageable pageable);
```
```
select d1_0.department_id,d1_0.name,d1_0.code,count(s1_0.student_id)
  from departments d1_0
  left join students s1_0 on d1_0.department_id=s1_0.department_id
  group by d1_0.department_id,d1_0.name,d1_0.code
  order by 1 limit ?                                    <- DB 가 페이징한다
select count(d1_0.department_id) from departments d1_0
결과 : page=0 -> CS 3명, EE 2명
       page=1 -> ME 1명, BA 2명 ( limit ?,? ),  totalElements = 4
```
- **`GROUP BY`로 학과당 1행**이 되므로 `limit`이 학과 단위로 정확히 동작한다.
- 학생 엔티티를 만들지 않고 개수만 가져오므로 가볍다.
- 참고: 비페이징 버전(`List<DepartmentSummary> findAllSummaries()`)은 JPQL에 `ORDER BY d.id`가 박혀 있지만, 페이징 버전은 **`ORDER BY`를 빼고 `Pageable`의 `sort`에 맡긴다.** 둘을 겹치면 정렬이 두 번 붙어 헷갈린다.

| 쿼리 | 경고/오류 | SQL의 `limit` | 결과 |
|---|---|---|---|
| (1) `LEFT JOIN FETCH` | HHH90003004 경고 | **없음** | 학과 2개 (맞지만 전체 조회) |
| (2) `LEFT JOIN` | 없음 | 있음 | **학과 1개** (틀림) |
| (3) `LEFT JOIN FETCH` + GROUP BY Projection | **기동 실패** | — | — |
| (4) `LEFT JOIN` + GROUP BY Projection | 없음 | 있음 | 학과 2개 (**정상, 권장**) |

### ⑥ 코드 — 실험을 직접 호출해 보는 데모 엔드포인트 (paging 버전)

```java
// DepartmentRepository
//--------------------------------------------------------------------
// [ 수업 비교용 ] 1:N 컬렉션을 페이징과 함께 조회하는 잘못된 방법 두 가지.
// 실제 서비스 코드에서는 사용하지 않는다. 위의 findAllSummaries(Pageable) 이 정답이다.
//--------------------------------------------------------------------
/** (A) 컬렉션 Fetch Join + 페이징. 결과는 맞지만 SQL 에 limit 이 붙지 않아 전체를 읽은 뒤 메모리에서 잘라낸다. */
@Query(value = "SELECT d FROM Department d LEFT JOIN FETCH d.students",
        countQuery = "SELECT COUNT(d) FROM Department d")
Page<Department> findAllWithStudentsPaged(Pageable pageable);

/** (B) FETCH 를 뺀 일반 JOIN + 페이징. SQL 에 limit 은 붙지만 학생 수만큼 늘어난 행을 자르므로 학과 개수가 요청과 달라진다. */
@Query(value = "SELECT d FROM Department d LEFT JOIN d.students",
        countQuery = "SELECT COUNT(d) FROM Department d")
Page<Department> findAllJoinPaged(Pageable pageable);
```
```java
// DepartmentController
//(A) 컬렉션 Fetch Join + 페이징 -> WARN HHH90003004, SQL 에 limit 이 없다
@GetMapping("/demo/fetch-join-paged")
public ResponseEntity<Page<DepartmentDTO.Response>> getDepartmentsFetchJoinPaged(
        @PageableDefault(size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(departmentService.getAllDepartmentsWithStudentsPaged(pageable));
}

//(B) FETCH 없는 일반 JOIN + 페이징 -> limit 은 붙지만 학과 개수가 틀어진다
@GetMapping("/demo/plain-join-paged")
public ResponseEntity<Page<DepartmentDTO.Response>> getDepartmentsPlainJoinPaged(
        @PageableDefault(size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(departmentService.getAllDepartmentsJoinPaged(pageable));
}
```

| 호출 | 쿼리 | 실제 결과 |
|---|---|---|
| `GET /api/departments/demo/fetch-join-paged?page=0&size=2` | (A) | 학과 2개 (CS 3명, EE 2명) / totalElements 4 / **WARN HHH90003004, limit 없음** |
| `GET /api/departments/demo/plain-join-paged?page=0&size=2` | (B) | **학과 1개 (CS)** / totalElements 1 / limit은 붙는다 |
| `GET /api/departments/paged?page=0&size=2` | (4) | 학과 2개 (CS 3명, EE 2명) / totalElements 4 / 경고 없음, SQL 2번 |

**해설**
- 데모 엔드포인트의 기본 `size=2`는 **실험 결과가 바로 드러나도록** 고른 값이다(CS가 3명이라 `limit 2`에서 틀어짐이 보인다).
- ⚠️ 데모는 `DepartmentDTO.Response.fromEntity`(학생 목록 포함)를 쓰므로 (B)는 학과마다 학생 컬렉션 지연 로딩 + 학생마다 상세정보 쿼리가 **추가로** 붙는다(5-4 ⚠️). 로그가 문서보다 길게 찍혀도 이상한 게 아니다.
- **"잘못된 코드를 지우지 않고 이름을 붙여 남겨 둔 것"** 자체가 학습용 설계다. 🔗 PART 2 `beans.xml`에 실패 로그를 주석으로 남겨 둔 것과 같은 태도(부록 A 2단계 표).

### ⑦ 그럼 어떻게 작성하나 — 필요별 정답표 (문서 2-6)

| 필요한 데이터 | 작성 방법 | 이 저장소의 메서드 |
|---|---|---|
| 목록 + 자식 **개수** | `LEFT JOIN` + `COUNT` + `GROUP BY` Projection | `DepartmentRepository.findAllSummaries(Pageable)` |
| 단건 + 자식 **목록** | `LEFT JOIN FETCH` (**`Pageable` 없음**) | `DepartmentRepository.findByIdWithStudents(id)` |
| 목록 + 자식 **목록** | **부모만 페이징** + `default_batch_fetch_size` | 없음 (이 예제에서는 사용하지 않음) |
| 목록 + **ToOne** 연관 | `LEFT JOIN FETCH` + `Pageable` | `StudentRepository.findAllWithDetails(Pageable)` |

- 학과 목록 화면에는 보통 **학생 수만** 필요하다. 학생 목록은 학과 **상세**(`GET /api/departments/{id}`)에서 가져온다 — 학과 1개 + `Pageable` 없음이므로 **컬렉션 fetch join이 안전**하다.
- 목록에서 자식 목록까지 꼭 필요하면 **fetch join 없이 부모만 페이징**하고, 자식은 `default_batch_fetch_size`(이미 `application-prod.properties`에 100)로 **IN 절 한 번에** 채운다. 🔗 4-6절 끝 "페이징이 필요한 컬렉션 연관관계의 실무 표준 해법"의 구체적 형태.

**개발 중 경고를 예외로 바꾸는 설정 (문서 2-6)**
```properties
spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch=true
```
```
-> HibernateException: setFirstResult() or setMaxResults() specified with collection
   fetch join (in-memory pagination was about to be applied, but
   'hibernate.query.fail_on_pagination_over_collection_fetch' is enabled)
```
→ **"경고라서 놓치는" 문제를 "시작부터 터지게" 바꾸는 안전장치.** 개발/테스트 프로파일에 켜 두면 좋다. 🔗 3-3절 프로파일별 설정 — `application-test.properties`에 두기 좋은 설정.

### ⑧ `countQuery` — 언제 쓰고 언제 생략하나

**원칙 (StudentRepository 클래스 주석)**
```
 *   [ countQuery ]
 *   생략하면 Spring Data 가 원본 쿼리에서 FETCH 를 떼어내 count 쿼리를 자동으로 만든다.
 *   자동 생성 SQL 이 직접 작성한 것과 같으면 지정하지 않고,
 *   불필요한 조인/그룹핑이 남는 경우에만 지정한다.
 *   예 ) WHERE s.department.id 조건 -> 자동 생성 count 쿼리에 departments 조인이 남는다
```

| 메서드 | 생략했을 때 자동 생성 count SQL | `countQuery` |
|---|---|---|
| `findAllWithDetails(Pageable)` | `select count(s1_0.student_id) from students s1_0` | 생략 |
| `findByNameContainingIgnoreCase` / `findByStudentNumberContainingIgnoreCase` | `... from students s1_0 where lower(s1_0.name) like ...` | 생략 |
| `findByDepartmentId(id, Pageable)` / `findByDepartmentIdAndNameContainingIgnoreCase` | `... from students s1_0 left join departments d1_0 ... where d1_0.department_id=?` | **지정** (불필요한 조인) |
| `findAllSummaries(Pageable)` | `left join students` + `group by`가 그대로 남는다 | **지정** (조인 + 그룹핑) |

```java
//WHERE 에 s.department 가 있어 자동 생성 count 쿼리에 departments 조인이 남으므로 직접 지정한다
@Query(value = "SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department "
        + "WHERE s.department.id = :departmentId",
        countQuery = "SELECT COUNT(s) FROM Student s WHERE s.department.id = :departmentId")
Page<Student> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);
```

**해설**
1. **자동 count 쿼리는 "FETCH만 뗀" 원본이다.** 그래서 `WHERE s.department.id`처럼 **FETCH 대상 경로를 조건에 쓰면**, FETCH는 떨어져도 그 경로를 위한 **`departments` 조인이 남는다.** 직접 쓴 `countQuery`는 `where s1_0.department_id=?`로 **조인 없이** 처리된다 — Hibernate가 `s.department.id`를 **FK 컬럼(`students.department_id`)으로 바로 번역**하기 때문이다. (〔v1.3 정정〕 4-5절 해설 2 참고)
2. **`findAllSummaries(Pageable)`는 반드시 지정해야 한다.** 생략하면:
   ```
   select count(d1_0.department_id)
   from departments d1_0 left join students s1_0 on d1_0.department_id=s1_0.department_id
   group by d1_0.department_id,d1_0.name,d1_0.code
   ```
   → **그룹마다 한 행씩 count가 나온다**(학과 4개면 4행). 전체 건수 한 개가 아니다. 지정하면 `select count(d1_0.department_id) from departments d1_0` 한 줄.
3. ⚠️ **`countQuery`의 WHERE 조건은 목록 쿼리와 똑같이 맞춰야** `totalElements`가 정확하다. `findByDepartmentIdAndNameContainingIgnoreCase`의 countQuery가 이름 조건(`LOWER(s.name) LIKE ...`)까지 그대로 복사한 이유.
4. ⚠️ **`countQuery`는 메모리 페이징(HHH90003004)과 무관하다** — count만 바꿀 뿐 목록 쿼리를 고치지 못한다.

**직접 확인하는 방법 (문서 2-6)**
1. `spring.jpa.show-sql=true` 확인 (이 저장소 `application-prod.properties`에 켜져 있다)
2. `GET /api/students/paged?page=0&size=5` → SQL 2개(목록 `limit ?,?` + `count`)
3. `GET /api/students` → SQL 1개
4. → **`totalElements`를 알려면 전체를 세야 한다**는 것을 SQL로 확인. `Slice`로 바꾸면 count가 사라지는 것도 같은 방법으로.

---

## 5-9. 검색 — `StudentSearchService` · `StudentSearchController` (paging 버전 신규)

### 코드 — `repository/StudentRepository.java` 검색 메서드

```java
//WHERE 조건이 Student 자신의 컬럼만 있어서 countQuery를 생략하였음
@Query("SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department "
        + "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
Page<Student> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

@Query("SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department "
        + "WHERE LOWER(s.studentNumber) LIKE LOWER(CONCAT('%', :studentNumber, '%'))")
Page<Student> findByStudentNumberContainingIgnoreCase(@Param("studentNumber") String studentNumber,
                                                      Pageable pageable);

//WHERE 조건에 s.department 가 있어 자동 생성 countQuery에 departments 조인이 남으므로 직접 지정한다
@Query(value = "SELECT s FROM Student s "
        + "LEFT JOIN FETCH s.studentDetail "
        + "LEFT JOIN FETCH s.department "
        + "WHERE s.department.id = :departmentId "
        + "AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))",
        countQuery = "SELECT COUNT(s) FROM Student s "
                + "WHERE s.department.id = :departmentId "
                + "AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
Page<Student> findByDepartmentIdAndNameContainingIgnoreCase(@Param("departmentId") Long departmentId,
                                                            @Param("name") String name,
                                                            Pageable pageable);
```

### 코드 — `service/StudentSearchService.java`

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentSearchService {

    private final StudentRepository studentRepository;

    /** 학생 검색 (이름 또는 학번으로) */
    public Page<StudentDTO.Response> searchStudents(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            //findAll() 은 Fetch Join 이 없어 학생 수만큼 추가 쿼리가 발생한다 ( N+1 )
            return studentRepository.findAllWithDetails(pageable)
                    .map(StudentDTO.Response::fromEntity);
        }
        // 학번으로 검색 시도
        if (keyword.matches("^[A-Z]+\\d+$")) {
            return studentRepository.findByStudentNumberContainingIgnoreCase(keyword, pageable)
                    .map(StudentDTO.Response::fromEntity);
        }
        // 이름으로 검색
        return studentRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(StudentDTO.Response::fromEntity);
    }

    /** 부서별 학생 검색 */
    public Page<StudentDTO.Response> searchStudentsByDepartment(Long departmentId, String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return studentRepository.findByDepartmentId(departmentId, pageable)
                    .map(StudentDTO.Response::fromEntity);
        }
        return studentRepository.findByDepartmentIdAndNameContainingIgnoreCase(departmentId, keyword, pageable)
                .map(StudentDTO.Response::fromEntity);
    }

    public Page<StudentDTO.Response> searchStudentsByName(String name, Pageable pageable) { ... findByNameContainingIgnoreCase ... }
    public Page<StudentDTO.Response> searchStudentsByStudentNumber(String studentNumber, Pageable pageable) { ... }
}
```

### 코드 — `controller/StudentSearchController.java`

```java
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class StudentSearchController {

    private final StudentSearchService studentSearchService;

    /** 학생 통합 검색 (이름 또는 학번)  GET /api/search/students?keyword=Alice&page=0&size=10&sort=name,asc */
    @GetMapping("/students")
    public ResponseEntity<Page<StudentDTO.Response>> searchStudents(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) { ... }

    /** 부서별 학생 검색  GET /api/search/departments/1/students?keyword=Alice&page=0&size=10 */
    @GetMapping("/departments/{departmentId}/students")
    public ResponseEntity<Page<StudentDTO.Response>> searchStudentsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) { ... }

    /** 이름으로 학생 검색  GET /api/search/students/by-name?name=Alice */
    @GetMapping("/students/by-name")
    public ResponseEntity<Page<StudentDTO.Response>> searchStudentsByName(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) { ... }

    /** 학번으로 학생 검색  GET /api/search/students/by-number?studentNumber=CS001 */
    @GetMapping("/students/by-number")
    public ResponseEntity<Page<StudentDTO.Response>> searchStudentsByStudentNumber(
            @RequestParam String studentNumber,
            @PageableDefault(size = 10, sort = "studentNumber", direction = Sort.Direction.ASC) Pageable pageable) { ... }
}
```

### 해설

1. **⚠️ 메서드 이름이 파생 쿼리 규칙처럼 생겼지만, 실제 쿼리는 `@Query`다.**
   - `findByNameContainingIgnoreCase`는 이름만 보면 Query Method 규칙(`Containing` + `IgnoreCase`)으로 자동 생성될 것 같다. 하지만 **`@Query`가 붙으면 이름은 쿼리 생성에 쓰이지 않는다.**
   - 그럼 왜 `@Query`를 썼나 — **파생 쿼리는 fetch join을 못 한다**(5-7절 표: "Query Method → Fetch Join 불가"). 이름 규칙으로 두면 검색 결과 학생마다 상세정보 N+1이 생긴다.
   - 이름을 규칙대로 지은 것은 **"무슨 조건인지"를 이름으로 드러내기 위한 관례**다. 🔗 4-5절 해설 3번(`findByStudentNumber`에 `@Query`)과 같은 패턴.
2. **`LOWER(...) LIKE LOWER(CONCAT('%', :name, '%'))`** — 대소문자 무시 + 부분 일치. `IgnoreCase`와 `Containing`을 JPQL로 직접 쓴 형태.
   - ⚠️ 컬럼에 `LOWER()`를 씌우면 **일반 인덱스를 못 탄다.** `'%...'`로 시작하는 LIKE도 마찬가지. 데이터가 커지면 전문 검색(Full-text)이나 별도 검색 엔진을 쓰는 이유. 실습 규모에서는 문제없다.
3. **`StringUtils.hasText(keyword)`** — `null`, `""`, `"   "`(공백만) 모두 false. `keyword == null`만 검사하면 `?keyword=`(빈 문자열)가 `LIKE '%%'`로 흘러간다. 🔗 3-4절 `@NotBlank` 비교표와 같은 기준(공백만 있는 문자열도 "없음").
   - 키워드가 없으면 **`findAll()`이 아니라 `findAllWithDetails(pageable)`** — 주석대로 N+1 방지. 검색 기능을 새로 만들면서도 **5-5절의 원칙을 놓치지 않은 것.**
4. **⚠️ 학번/이름 분기 정규식 `^[A-Z]+\\d+$`의 함정 — 대소문자를 구분한다.**
   | keyword | 정규식 | 실제 검색 | 결과 |
   |---|---|---|---|
   | `CS001` | 매치 | 학번 LIKE `%cs001%` (LOWER) | Alice ✅ |
   | `cs001` | **불일치** (소문자) | **이름** LIKE `%cs001%` | **0건** ⚠️ |
   | `CS` | 불일치 (숫자 없음) | 이름 LIKE `%cs%` | 이름에 "cs"가 들어간 학생 (학번 CS와 무관) |
   | `Alice` | 불일치 | 이름 | Alice ✅ |
   - 리포지토리는 `LOWER()`로 대소문자를 무시하는데 **분기 판단은 대문자만** 본다 → 사용자가 소문자로 학번을 치면 이름 검색으로 새서 빈 결과. `keyword.toUpperCase().matches(...)`로 판단하거나 `(?i)` 플래그를 쓰면 일관된다.
   - 문서 2-6의 테스트 3.5(`by-number?studentNumber=cs`)가 잘 되는 것은 **전용 엔드포인트라 분기를 안 거치기 때문**이다.
5. **`@RequestParam(required = false)`** vs **`@RequestParam`(기본 required=true)**
   - 통합 검색은 키워드가 없어도 된다(전체 목록) → `required = false`.
   - `/by-name`, `/by-number`는 필수 → 빠뜨리면 `MissingServletRequestParameterException` → **400** (이 예외는 `RuntimeException`이 아니라서 이 프로젝트의 500 핸들러에 걸리지 않고 스프링 기본 처리로 400이 나간다 — 5-10절).
6. **기본 정렬이 엔드포인트마다 다르다** — 학생 목록 `/paged`는 `id`, 검색은 `name`, 학번 검색은 `studentNumber`. **"검색 결과는 사람이 읽기 좋은 순서"** 라는 의도.
7. ⚠️ **부서별 검색은 학과 존재 확인을 안 한다** — `GET /api/search/departments/999/students` → **200 + 빈 페이지.** 5-3절의 `/api/departments/999/students`(404)와 정책이 다르다. 검색은 "결과 없음"이 자연스럽다는 판단일 수 있지만, **같은 자원에 두 가지 응답 정책**이 생긴 것은 기억해 둘 것.
8. **`/api/search` 컨트롤러를 따로 뺀 이유** — 조회(`/api/students`)와 검색의 **관심사 분리**. 검색 조건이 늘어나도 기존 CRUD 컨트롤러가 커지지 않는다. 서비스도 `StudentSearchService`로 따로 두어 `StudentService`(CRUD + 트랜잭션 쓰기)와 역할이 섞이지 않게 했다. 🔗 3-9절 해설 5번 "`addUser`가 `UserDetailsService`에 있는 것은 역할 혼재" — 이번에는 처음부터 나눴다.

### 측정 결과 — 학생 8명 / 학과 4개, H2 실측 (문서 2-6)

| 엔드포인트 | 쿼리 수 |
|---|---|
| `GET /api/students` | 1 |
| `GET /api/students/paged` | **1** (count 생략) |
| `GET /api/students/paged?page=1&size=3` | 2 (목록 + count) |
| `GET /api/students/1` · `/number/CS001` · `/number/CS003` | 1 |
| `GET /api/departments` | 1 |
| `GET /api/departments/paged` | **1** (count 생략) |
| `GET /api/departments/paged?size=3` | 2 |
| `GET /api/departments/1` · `/code/CS` | 1 |
| `GET /api/departments/1/students` | 2 (존재확인 + 목록) |
| `GET /api/departments/1/students/paged?size=2` | 3 (존재확인 + 목록 + count) |
| `GET /api/search/students` | 1 |
| `GET /api/search/students?size=3` | 2 |
| `GET /api/search/students?keyword=Alice` · `?keyword=CS001` | 1 |
| `GET /api/search/students/by-name?name=a` · `/by-number?studentNumber=CS` | 1 |
| `GET /api/search/departments/1/students` | 1 |

**해설**: **모든 조회가 건수와 무관한 상수**다. "1번"인 페이징 요청은 전부 **결과가 `size`보다 적은 첫 페이지**라 count가 생략된 것(5-7절). `/api/search/departments/1/students`가 1번인 것은 **존재 확인이 없기 때문**(위 해설 7).

### 문서 2-6 테스트 시나리오 요약

| 번호 | 요청 | 확인할 것 |
|---|---|---|
| 1.2 | `GET /api/departments/paged?page=0&size=2&sort=name,asc` | 정렬 파라미터가 Projection 별칭(`name`)에도 적용 |
| 1.5 | `GET /api/departments/1/students/paged?page=0&size=2&sort=name,asc` | 학과별 페이징 |
| 1.6 | `POST /api/departments` `{"name":"Information Technology","code":"IT"}` | 응답 `"studentCount":0, "students":[]` (5-4 표 — `@Builder.Default` 빈 리스트) |
| 2.5 | `POST /api/students` `{"name":"Simple Student","studentNumber":"CS998","departmentId":1}` | 상세정보 없이 등록 → 201 (5-3 `@NotNull` 주석 처리 이유) |
| 2.6 | `PUT /api/students/10` (학과 1→2, 상세정보 추가) | flush 순서 수정 확인(5-2), 학과 변경 |
| 3.1 | `GET /api/search/students?keyword=Alice&...` | 이름 분기 |
| 3.2 | `GET /api/search/students?keyword=CS001` | 학번 분기 (대문자!) |
| 3.3 | `GET /api/search/departments/1/students?keyword=Smith` | 학과 + 이름 |
| 3.4·3.5 | `/by-name?name=b`, `/by-number?studentNumber=cs` | 전용 엔드포인트는 소문자도 OK |

---

## 5-10. 에러 응답 다시 보기 — `RuntimeException` 핸들러가 삼키는 것들

🔗 3-7절·4-8절 ①. 이번 진도에서 엔드포인트가 늘면서 **`DefaultExceptionAdvice`의 한 가지 구조적 특징**이 여러 곳에서 드러난다.

```java
@ExceptionHandler(BusinessException.class)             → ex.getHttpStatus() (404/409 …)
@ExceptionHandler(HttpMessageNotReadableException.class) → 400
@ExceptionHandler(MethodArgumentNotValidException.class) → 400 (+ 필드별 errors 맵)
@ExceptionHandler(RuntimeException.class)              → 500   ← ★ 나머지 런타임 예외 전부
```
- `@ExceptionHandler`는 **가장 구체적인 타입**을 고르므로, 위 세 개에 해당하지 않는 **모든 `RuntimeException`이 500**으로 떨어진다.

| 상황 | 예외 | `RuntimeException`인가 | 이 프로젝트 응답 | "원래" 기대하는 응답 |
|---|---|---|---|---|
| `GET /api/students/abc` | `MethodArgumentTypeMismatchException` | **예** | **500** | 400 |
| `?sort=없는필드` | Spring Data/Hibernate 쿼리 예외 (`PropertyReferenceException` 등) | **예** | **500** | 400 |
| 동시 요청으로 UNIQUE 위반 (5-1 ⑦) | `DataIntegrityViolationException` | **예** | **500** | 409 |
| 학생 있는 학과 삭제 (검사 없었다면) | `ConstraintViolationException` 계열 | **예** | **500** | 409 → 그래서 5-4에서 사전 검사 |
| 구버전 헬퍼 NPE (5-2) | `NullPointerException` | **예** | **500** | — (버그) |
| `/by-name`에 `name` 누락 | `MissingServletRequestParameterException` | 아니오 (`ServletException`) | 400 (스프링 기본) | 400 |
| **(JWT, 5-11) 권한 부족** | `AccessDeniedException` | **예** | **500** ⚠️ | **403** |

**해설**
- ⚠️ **"잡히지 않은 예외는 전부 500"은 안전한 기본값이지만, 클라이언트 잘못(4xx)까지 서버 잘못(5xx)으로 보고하게 된다.** 이번 진도의 `countByDepartmentId` 사전 검사(5-4)는 이 문제를 **예외가 나기 전에 피해 가는** 방식이고, 5-11절 JWT의 Step 10은 **해당 예외용 핸들러를 추가하는** 방식이다.
- 🔗 PART 1 1-14절 catch 블록 순서 — "좁은 예외 먼저, 넓은 예외 나중"과 같은 구조다. `@ExceptionHandler`는 선언 순서가 아니라 **타입의 구체성**으로 고른다는 점만 다르다.

### ⚠️ OSIV(Open Session In View) — "트랜잭션 밖 LAZY 접근 = 예외"의 전제 조건

4-2절에서 "Entity를 Controller로 반환하면 트랜잭션이 끝나 `LazyInitializationException`"이라고 적었고, 문서 2-5도 같은 말을 한다(*"트랜잭션이 끝난 뒤 접근하면 LazyInitializationException이 발생합니다. 그래서 … Service 안에서 DTO 변환까지 끝냅니다."*). 이것은 **원리로서는 맞지만, Spring Boot 기본 설정에서는 실제로 잘 안 터진다.**

- Spring Boot는 기본값으로 **`spring.jpa.open-in-view=true`** 다. 이 설정은 **HTTP 요청이 끝날 때까지 영속성 컨텍스트(세션)를 열어 둔다.** 트랜잭션은 서비스에서 끝나도 **세션이 살아 있으므로 컨트롤러·Jackson 직렬화 단계에서도 LAZY 로딩이 된다.**
- 그래서 기동 시 이런 경고가 찍힌다(이 저장소도 설정을 바꾸지 않았으므로 찍힌다).
  ```
  WARN ... spring.jpa.open-in-view is enabled by default. Therefore, database queries may be
           performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
  ```
- ⚠️ **문제는 "예외가 안 난다"는 것 자체다.** 컨트롤러나 JSON 변환 중에 **보이지 않는 쿼리**가 나가고(N+1이 숨는다), 요청이 끝날 때까지 DB 커넥션을 붙잡는다.
- 실무에서는 `spring.jpa.open-in-view=false`로 끄고, **이 저장소처럼 서비스 안에서 DTO 변환을 끝내는 방식**을 강제하는 경우가 많다. 이 설정을 끄면 4-2절·문서 2-5의 설명이 **문자 그대로** 성립한다.
- 🔗 **이 저장소의 방식(서비스 안에서 fetch join + DTO 변환)은 OSIV를 켜든 끄든 올바르게 동작한다.** 그래서 이 설계가 "정답"인 것이다.

---

## 5-11. Spring Security + JWT (강의 문서 2-7)

> ⚠️ **이 절의 코드는 강의 문서 2-7의 발췌다.** 업로드된 zip에는 JWT 코드가 아직 없다. 다음 zip(예상: `...-paging-JWT`)이 오면 실제 코드와 대조해 보완할 자리다.
> 🔗 PART 3 3-9절(폼 로그인 방식 Security)을 먼저 보고 오면 비교가 쉽다.

### 목표 — 인증과 인가

| 구분 | 질문 | 이 예제에서의 답 |
|---|---|---|
| **인증** (Authentication) | 너 누구니? | 이메일 + 비밀번호로 로그인 → **JWT 토큰**을 받는다 |
| **인가** (Authorization) | 그걸 할 수 있니? | `ROLE_USER`는 **조회만**, `ROLE_ADMIN`은 등록·수정·삭제까지 |

### ① 요청은 필터 체인을 먼저 지난다

```
HTTP 요청
   ↓
[ SecurityFilterChain ]
   1. CorsFilter                 CORS 처리
   2. JwtAuthenticationFilter    (직접 만든 필터) 토큰을 확인해 인증 정보를 넣는다
   3. ...
   4. AuthorizationFilter        인증/권한을 확인한다  -> 실패하면 401 / 403
   ↓
DispatcherServlet -> Controller ( @PreAuthorize 가 한 번 더 확인 )
```

| 구성 요소 | 역할 | 이 예제의 구현 |
|---|---|---|
| `SecurityFilterChain` | 어떤 URL을 열지, 어떤 필터를 끼울지 | `SecurityConfig` |
| `AuthenticationManager` | 인증 요청을 받아 Provider에 위임 | `AuthenticationConfiguration`에서 꺼내 빈 등록 |
| `AuthenticationProvider` | 실제 인증 (조회 + 비밀번호 비교) | `DaoAuthenticationProvider` (**자동 구성**) |
| `UserDetailsService` | 이름으로 사용자 조회 | `UserInfoUserDetailsService` |
| `UserDetails` | 인증용 사용자 정보 | `UserInfoUserDetails` |
| `PasswordEncoder` | 암호화·비교 | `BCryptPasswordEncoder` |
| `SecurityContextHolder` | 인증 결과 보관 | `JwtAuthenticationFilter`가 채운다 |

**인증 아키텍처 그림 (문서 2-7, 번호 순)**
```
 ①HttpRequest → AuthenticationFilter ──②──▶ UsernamePasswordAuthenticationToken
                     │  ▲
                   ③ │  │⑨
                     ▼  │
               AuthenticationManager(«interface», 구현: ProviderManager)
                     │  ▲
                   ④ │  │⑧
                     ▼  │
               AuthenticationProvider(s) ──⑤──▶ UserDetailsService ──⑥──▶ UserDetails(«interface», 구현: User)
                                         ◀──⑦──
 ⑩ AuthenticationFilter → SecurityContextHolder [ SecurityContext [ Authentication ] ]
```
| 번호 | 하는 일 |
|---|---|
| 1~2 | 필터가 요청에서 아이디/비밀번호를 꺼내 `UsernamePasswordAuthenticationToken`을 만든다 |
| 3~4 | `AuthenticationManager`가 적절한 `AuthenticationProvider`를 고른다 |
| 5~7 | Provider가 `UserDetailsService`로 사용자를 조회하고 비밀번호를 비교한다 |
| 8~9 | 성공하면 권한이 담긴 `Authentication`이 돌아온다 |
| 10 | `SecurityContextHolder`에 저장. 이후 코드는 여기서 "로그인한 사용자"를 꺼낸다 |

- 🔗 3-9절 8단계 흐름도와 **같은 그림**이다. 차이는 **누가 1~2단계와 10단계를 하느냐**:
  - 폼 로그인(3-9): `UsernamePasswordAuthenticationFilter`가 1~10을 한 번에, 결과를 **세션**에 저장.
  - JWT(2-7): **1~2단계는 로그인 API(`/api/userinfos/login`)가 직접**, 이후 요청의 **10단계는 `JwtAuthenticationFilter`가 매 요청마다** 한다(세션 없음).

### ② JWT란 무엇인가

**한 줄 정의 (문서)**: *"서버가 서명해서 발급한, 스스로 내용을 담고 있는 출입증."* 점(`.`)으로 구분된 세 부분, 각 부분은 **Base64URL 인코딩**.
```
eyJhbGciOiJIUzI1NiJ9  .  eyJzdWIiOiJhZG1pbkBhYS5jb20iLCJpYXQiOjE3ODk2...  .  hjZm2oFdq8dn...
[ 1. Header ]            [ 2. Payload ]                                     [ 3. Signature ]

Header    { "alg": "HS256" }                    어떤 알고리즘으로 서명했는가
Payload   { "sub": "admin@aa.com",              누구의 토큰인가 ( subject )
            "iat": 1789640911,                  발급 시각 ( issued at )
            "exp": 1789644511 }                 만료 시각 ( expiration )
Signature HMACSHA256( Header + "." + Payload, 비밀키 )    위조 여부를 확인하는 값
```
- ⚠️ **Payload는 암호화가 아니라 인코딩이다.** jwt.io에 붙여 넣으면 누구나 읽는다 → **비밀번호·주민번호 같은 민감 정보를 넣으면 안 된다.**
- **서명은 비밀키를 아는 서버만 만들 수 있다.** 한 글자라도 바꾸면 검증 실패. → **JWT는 "숨기는" 기술이 아니라 "위조를 막는" 기술이다.**
- 🔗 3-9절 BCrypt(단방향 해시)와 구분: BCrypt는 **되돌릴 수 없게 저장**, HMAC 서명은 **변조를 탐지**. 둘 다 "암호화"가 아니다.

**세션 방식과 비교 (문서 2-7)**

| 비교 항목 | 세션 + 쿠키 | JWT |
|---|---|---|
| 로그인 상태 보관 | 서버 메모리(또는 Redis) | **토큰 안에** (서버는 저장 안 함) |
| 서버 확장(scale-out) | 여러 대면 세션 공유 필요 | **어느 서버든 서명만 검증** |
| 클라이언트 | 브라우저 쿠키 의존 | 모바일·SPA·서버 간 호출 동일 |
| 전달 | `Cookie: JSESSIONID=...` | **`Authorization: Bearer <토큰>`** |
| 로그아웃/강제 만료 | 세션 삭제로 끝 | **회수가 어렵다** (그래서 만료를 짧게) |
| 이 예제 | — | `SessionCreationPolicy.STATELESS` |

→ JWT는 **서버가 상태를 갖지 않는(stateless)** 인증이라 REST와 잘 맞는다. 대신 **발급한 토큰을 즉시 무효화하기 어렵다** → 만료를 짧게 + 필요하면 **리프레시 토큰**.

**발급·사용 흐름 (문서 2-7 그림)**
```
Client ─① username/password ─────────────▶ Web Server ─② Creates JWT
Client ◀────────────── ③ JWT ──────────── Web Server
Client ④ Store JWT
Client ─⑤ 요청마다 JWT ──────────────────▶ Web Server ─⑥ Validates JWT

1) POST /login  username=..&password=..        →  2) HTTP 200 OK  token: JWT (Bearer Token)
3) POST/GET /users  Authorization: Bearer JWT  →  4) HTTP 200 OK  {name: ..., email: ..., orders: [...]}
```

**JWT vs OAuth 2.0 (문서 2-7)** — *"OAuth 2.0은 출입증 발급 절차 및 시스템이고, JWT는 그 과정에서 실제로 발급해 주는 플라스틱 카드(토큰) 종류 중 하나."*

| 구분 | JWT | OAuth 2.0 |
|---|---|---|
| 정체 | **토큰 형식** (RFC 7519) | **권한 위임 프레임워크** (RFC 6749) |
| 해결하는 문제 | "이 토큰이 진짜인가"를 서명으로 확인 | "내 정보를 다른 서비스에 어디까지 넘길까"를 절차로 정의 |
| 등장 인물 | 서버 ↔ 클라이언트 | 자원 소유자, 클라이언트 앱, 인가 서버, 자원 서버 |
| 주요 용도 | 인증 데이터 전달, 세션 대체 | 소셜 로그인(구글·카카오), API 접근 권한 위임 |
| 이 예제 | **직접 로그인 + 직접 발급** | 사용 안 함 |

### ③ 추가·수정되는 클래스 (문서 2-7)

```
com.basic.myspringboot            (문서 기준 패키지 — 이 저장소는 com.rookies6.myspringboot4project)
 └ security
    ├ config      SecurityConfig.java                보안 설정의 중심 (필터 체인, 공개 URL, 401/403 처리)
    ├ jwt         JwtService.java                     토큰 생성 · 검증
    │             JwtAuthenticationFilter.java        요청마다 토큰을 확인해 인증 정보를 넣는 필터
    ├ models      UserInfo.java                       사용자 엔티티 ( user_info 테이블 )
    │             UserInfoRepository.java             findByEmail
    │             UserInfoUserDetails.java            UserInfo → UserDetails 변환
    ├ service     UserInfoUserDetailsService.java     이메일로 사용자를 찾아 UserDetails 로
    ├ controller  UserInfoController.java             회원가입 / 로그인 / 내 정보
    │   └ dto     SignUpRequest, AuthRequest, AuthResponse, UserInfoResponse
    ├ annotation  CurrentUser.java                    (Step 13)
    └ runner      UserInfoInsertRunner.java           실습용 계정 2개
```

| 기존 클래스 | 변경 |
|---|---|
| `Student` | `UserInfo`와 **`@ManyToOne`** 추가 (`user_info_id` 컬럼) |
| `StudentController` | `@PreAuthorize`로 권한, 등록 시 `@AuthenticationPrincipal`로 로그인 사용자 주입 |
| `StudentService` | `createStudent(request, currentUser)` — 등록자 저장 |
| `DefaultExceptionAdvice` | `AuthenticationException` → 401, `AccessDeniedException` → 403 |
| `pom.xml` | `spring-boot-starter-security`, `jjwt-api` / `jjwt-impl` / `jjwt-jackson` **0.12.7** |
| `application.properties` | `jwt.secret`, `jwt.access-expire-seconds` |

- 🔗 3-9절의 `springboot` 프로젝트는 `auth/userinfo/` 패키지에 같은 이름의 클래스들(`UserInfo`, `UserInfoUserDetails`, `UserInfoUserDetailsService`, `UserInfoController`)이 있었다. **2-7은 그것을 `security/` 아래로 역할별 하위 패키지(config/jwt/models/service/controller)로 재배치**하고 JWT 부품을 더한 것이다.

### ④ 단계별 코드 (문서 2-7 Step 1~14)

**Step 1 — 의존성**
```xml
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
<dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-api</artifactId><version>0.12.7</version></dependency>
<dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-impl</artifactId><version>0.12.7</version></dependency>
<dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-jackson</artifactId><version>0.12.7</version></dependency>
```
- jjwt는 **API / 구현 / JSON 처리가 세 모듈로 나뉜다** — 코드는 `jjwt-api`에만 의존하고, 구현은 런타임에 붙는 구조. 🔗 3-2절 SLF4J(퍼사드) + Logback(구현)과 같은 발상.
- Spring Boot가 버전을 관리하지 않는 라이브러리라 **`<version>`을 직접 적었다.** 🔗 3-1절 starter-parent의 버전 관리 — 관리 대상이 아닌 것만 명시한다.

**Step 2 — 사용자 엔티티**
```java
@Entity
public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)                 private String name;
    @Column(unique = true, nullable = false)  private String email;     // 로그인 아이디
    @Column(nullable = false)                 private String password;  // BCrypt 해시
    @Column(nullable = false)                 private String roles;     // "ROLE_ADMIN,ROLE_USER"
}
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String username);
}
```
- 🔗 3-9절과 같은 엔티티. 리포지토리 부모가 `ListCrudRepository` → `JpaRepository`로 바뀌었다(어느 쪽이든 동작).

**Step 3 — `UserDetails` / `UserDetailsService`**
```java
public class UserInfoUserDetails implements UserDetails {
    public UserInfoUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.email = userInfo.getEmail();
        this.password = userInfo.getPassword();
        //"ROLE_ADMIN,ROLE_USER" -> [ROLE_ADMIN, ROLE_USER]
        String roles = userInfo.getRoles() == null ? "" : userInfo.getRoles();
        this.authorities = Arrays.stream(roles.split(","))
                .map(String::trim).filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    @Override public String getUsername() { return email; }   // 로그인 아이디 = 이메일
    public UserInfo getUserInfo() { return userInfo; }        // 컨트롤러에서 꺼내 쓰기 위해
}

@Service
@RequiredArgsConstructor
public class UserInfoUserDetailsService implements UserDetailsService {
    private final UserInfoRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByEmail(username)
                .map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }
}
```
**3-9절 버전과 달라진 점**
| | 3-9절 (`springboot`) | 2-7 |
|---|---|---|
| roles null | `userInfo.getRoles().split(",")` → **null이면 NPE** | `null`이면 `""`로 |
| 공백·빈 항목 | 그대로 (`"ROLE_A, ROLE_B"`면 `" ROLE_B"`라는 이상한 권한) | **`trim` + 빈 항목 제거** |
| 생성 | `roleName -> new SimpleGrantedAuthority(roleName)` | **생성자 참조** `SimpleGrantedAuthority::new` |
| 주입 | `@Autowired` 필드 | **`@RequiredArgsConstructor` 생성자 주입** |
| `addUser` | `UserDetailsService`에 있음 (역할 혼재 ⚠️) | **컨트롤러(회원가입 API)로 이동** — 3-9절 해설 5번 지적이 해소됨 |
| 원본 엔티티 | 필드로만 보관 | **`getUserInfo()` 공개** → Step 12에서 사용 |
- 🔗 B-1 Stream·메서드 참조. `filter(role -> !role.isEmpty())`는 `"ROLE_ADMIN,"`처럼 끝에 쉼표가 있을 때 생기는 빈 문자열 권한을 걸러 낸다.

**Step 4 — 최소 보안 설정**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())   // REST API 라 CSRF 토큰이 필요 없다
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/userinfos/welcome",
                                         "/api/userinfos/new",
                                         "/api/userinfos/login").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .build();
    }
}
```
- ⚠️ **`DaoAuthenticationProvider`를 직접 빈으로 만들지 않았다.** `UserDetailsService`(`@Service`)와 `PasswordEncoder` 빈이 있으면 **Boot가 자동 구성**한다. 🔗 3-9절은 `authenticationProvider()` 빈을 손으로 만들었다 — **둘 다 되지만, 자동 구성에 맡기는 쪽이 코드가 짧다.** (단, `UserDetailsService` 빈이 둘 이상이면 자동 구성이 물러난다.)
- 🔗 3-9절 해설 3번 "**세션(formLogin) + CSRF 비활성화는 취약하다, 실무는 토큰 방식(JWT)으로**" — 2-7이 바로 그 "토큰 방식"이다. JWT를 `Authorization` 헤더로 보내면 **브라우저가 자동으로 붙이지 않으므로** CSRF 공격의 전제가 성립하지 않는다. **이제서야 `csrf.disable()`이 정당해진다.**
- 🔗 3-9절 해설 4번 "좁은 규칙 먼저" — `permitAll` 3개 → `/api/**` 인증 → 나머지 허용 순서.
- ⚠️ `anyRequest().permitAll()` — `/api/**`가 아닌 경로(정적 리소스, `/actuator/**` 등)는 **전부 열려 있다.** 이 저장소는 `management.endpoints.web.exposure.include=*`(🔗 3-8)이므로, JWT를 붙인 뒤에도 **액추에이터 전 엔드포인트가 인증 없이 열린다.** 운영이라면 `/actuator/**`를 따로 막아야 한다.

**Step 5 — 실습 계정 러너**
```java
@Order(1)
@Component
@RequiredArgsConstructor
public class UserInfoInsertRunner implements ApplicationRunner {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(ApplicationArguments args) {
        if (userInfoRepository.count() > 0) return;   // 이미 있으면 건너뛴다
        UserInfo admin = new UserInfo();
        admin.setName("adminboot");
        admin.setEmail("admin@aa.com");
        admin.setPassword(passwordEncoder.encode("pwd1"));   // 반드시 암호화
        admin.setRoles("ROLE_ADMIN,ROLE_USER");
        // userboot / user@aa.com / pwd2 / ROLE_USER 도 같은 방식으로 생성
        userInfoRepository.saveAll(List.of(admin, user));
    }
}
```
| 계정 | 비밀번호 | 권한 | 할 수 있는 일 |
|---|---|---|---|
| `admin@aa.com` | `pwd1` | `ROLE_ADMIN, ROLE_USER` | 조회 + 등록·수정·삭제 |
| `user@aa.com` | `pwd2` | `ROLE_USER` | 조회만 |
- ⚠️ `DataInitRunner`도 `@Order(1)`이다. **같은 순서값이면 실행 순서가 보장되지 않는다.** 두 러너가 서로 의존하지 않으니 지금은 문제없지만, Step 12처럼 **학생에 등록자를 넣는 초기 데이터**를 만들게 되면 `UserInfoInsertRunner`가 먼저 돌아야 한다 → 순서값을 분리할 것. 🔗 3-4절 `DatabaseRunner`/`DataInitRunner`의 `@Order(1)` 중복 지적("순서가 보장되지 않는다")과 같은 문제가 한 번 더 생기는 것.
- 🔗 3-9절 해설 "`ROLE_ADMIN`만 가진 계정은 `ROLE_USER` API에 못 들어간다 — 그래서 `ROLE_ADMIN,ROLE_USER`를 함께 넣는다" — admin 계정이 **정확히 그렇게** 만들어진다.

**Step 6·7 — 로그인 API와 토큰 생성**
```java
// AuthRequest
@Data
public class AuthRequest {
    @NotNull @Size(min = 2) @Email   private String email;
    @NotNull @Size(min = 4)          private String password;
}
// AuthResponse
@Getter @AllArgsConstructor
public class AuthResponse {
    private String accessToken;   // 발급된 JWT
    private String tokenType;     // "Bearer"
    private long expiresIn;       // 만료까지 남은 초
    private String email; private String name;
    private List<String> roles;   // ["ROLE_ADMIN","ROLE_USER"] - 화면 표시용
}
// SecurityConfig - 로그인 API 에서 인증을 직접 호출하기 위해 AuthenticationManager 를 빈으로 꺼낸다
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```
```java
// UserInfoController
@PostMapping("/login")
public ResponseEntity<AuthResponse> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
    //인증에 성공하면 principal 에 UserInfoUserDetails 가 들어 있다
    UserInfoUserDetails userDetails = (UserInfoUserDetails) authentication.getPrincipal();
    String token = jwtService.generateToken(userDetails.getUsername());
    return ResponseEntity.ok(AuthResponse.of(token, jwtService.getAccessExpireSeconds(),
            UserInfoResponse.from(userDetails)));      // 토큰 + 사용자 정보 + 권한
}
```
```properties
# application.properties - 비밀키는 소스에 두지 않는다
jwt.secret=${JWT_SECRET:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}
jwt.access-expire-seconds=3600
```
```java
@Component
public class JwtService {
    private final SecretKey key;
    private final long accessExpireSeconds;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-expire-seconds}") long accessExpireSeconds) {
        //HS256 은 최소 256비트(32바이트) 이상의 키를 요구한다
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpireSeconds = accessExpireSeconds;
    }

    public String generateToken(String userName) {
        Date expireDate = Date.from(Instant.now().plusSeconds(accessExpireSeconds));
        return Jwts.builder()
                .signWith(key, ALGORITHM)      // 비밀키로 서명 ( HS256 )
                .subject(userName)             // sub : 누구의 토큰인가
                .issuedAt(new Date())          // iat : 발급 시각
                .expiration(expireDate)        // exp : 만료 시각
                .compact();
    }
}
```

**해설**
1. **`authenticationManager.authenticate(...)` 한 줄이 그림의 3~9단계 전부**다. 실패하면 **예외를 던지므로** 성공 여부를 따로 `if`로 검사할 필요가 없다 → `BadCredentialsException` → (Step 10 핸들러) **401**.
2. **`@Valid`를 붙여야 `AuthRequest`의 검증이 동작**한다(문서 강조). 🔗 3-5절.
3. **`jwt.secret=${JWT_SECRET:기본값}`** — 🔗 3-2절 `${DB_HOST:127.0.0.1}`와 같은 **환경변수 + 기본값** 문법. 운영에서는 `JWT_SECRET` 환경변수로 덮어쓴다.
   - ⚠️ 주석은 "비밀키는 소스에 두지 않는다"지만 **기본값 자체가 소스에 들어 있다.** 환경변수를 깜빡하면 **공개 저장소에 올라간 키로 서명**하게 된다 → 누구나 유효한 토큰을 위조할 수 있다. 운영 프로파일에서는 기본값을 빼서 **없으면 기동 실패**하게 두는 편이 안전하다.
   - ⚠️ `secret.getBytes()`는 이 16진수 문자열을 **16진수로 해석하지 않고 글자 그대로(64바이트)** 키로 쓴다. 32바이트 이상이라 동작은 하지만, "256비트 키를 16진수로 적었다"는 의도와는 다르다. (Base64 문자열을 쓰고 `Decoders.BASE64.decode(secret)`로 푸는 것이 jjwt의 일반적인 방식)
4. **생성자 주입 + `@Value`** — 🔗 PART 2 2-2절 생성자 주입, 3-2절 `@Value`. `final` 필드라 불변.
5. **토큰에 넣는 것은 `sub`(이메일)·`iat`·`exp` 셋뿐** — 권한(`roles`)은 넣지 않았다(Step 14).

**Step 8 — 토큰 검증 필터 + STATELESS**
```java
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    ...
    return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> ... )
            //세션을 만들지 않는다 ( 토큰만으로 인증한다 )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```
```java
// JwtAuthenticationFilter 의 핵심
private void authenticate(HttpServletRequest request, String token) {
    try {
        String username = jwtService.extractUsername(token);                        // 서명 검증 + sub 추출
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtService.validateToken(token, userDetails)) {                          // 사용자 일치 + 만료 확인
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    } catch (JwtException | UsernameNotFoundException | IllegalArgumentException ex) {
        //만료 / 위조 / 없는 사용자 : 인증하지 않고 넘어간다
        log.debug("JWT 인증 실패 : {}", ex.getMessage());
        SecurityContextHolder.clearContext();
    }
}
```
```
[JwtAuthenticationFilter]  Authorization 헤더에 "Bearer " 가 있는가?
     ├ 없다  --> 인증 없이 통과 --> AuthorizationFilter 가 401
     └ 있다
          ├ extractUsername(token)            실패(위조·만료) --> catch --> 인증 없이 통과 --> 401
          ├ loadUserByUsername(email)
          ├ validateToken(token, userDetails)
          └ SecurityContextHolder 에 Authentication 저장 ( 권한 목록 포함 )
     ↓
[AuthorizationFilter]   인증되었는가?     아니면 401
     ↓
[@PreAuthorize]         권한이 맞는가?    아니면 403
     ↓
[Controller]            @AuthenticationPrincipal 로 로그인 사용자를 꺼내 쓸 수 있다
```

| `SessionCreationPolicy` | 의미 |
|---|---|
| `ALWAYS` | 항상 세션 생성 |
| `IF_REQUIRED` | 필요할 때 생성 (**기본값**) |
| `NEVER` | 직접 만들지는 않지만 있으면 사용 |
| **`STATELESS`** | **만들지도, 사용하지도 않는다** (JWT) |

**해설**
1. ⚠️ **토큰이 잘못돼도 필터는 예외를 던지지 않고 "그냥 통과"시킨다** — 문서가 가장 강조하는 부분. 필터에서 예외를 던지면 `DispatcherServlet` 바깥이라 `@RestControllerAdvice`가 잡지 못하고 서블릿 컨테이너의 기본 에러가 나간다. **인증 없이 넘기면 뒤의 인가 단계가 401을 일관되게 응답**한다. → "판단은 필터가, 응답은 인가 단계가".
2. **`new UsernamePasswordAuthenticationToken(userDetails, null, authorities)`** — 3개 인자 생성자는 **"인증 완료" 상태**를 만든다(2개 인자는 "인증 전"). 비밀번호 자리에 `null` — 이미 검증했으니 들고 다닐 이유가 없다.
3. **`addFilterBefore(..., UsernamePasswordAuthenticationFilter.class)`** — 폼 로그인 필터 **앞**에 끼운다. 폼 로그인을 안 쓰더라도 필터 체인 안의 "위치 기준점"으로 쓰는 관례.
4. **매 요청마다 `loadUserByUsername` → DB 조회가 한 번씩 나간다.** 토큰만 믿지 않고 **사용자가 아직 존재하는지·권한이 바뀌었는지**를 확인하는 장점이 있지만, "서버가 아무것도 저장하지 않는다"는 JWT의 이점을 일부 포기한 것이다. 권한을 토큰 클레임에 넣으면 DB 조회를 생략할 수 있다(Step 14의 트레이드오프와 연결).
5. ⚠️ **(보충) 필터를 `@Component`로 등록할 때의 함정** — Spring Boot는 `Filter` 타입 빈을 **서블릿 필터로도 자동 등록**한다. 그러면 같은 필터가 **Security 체인 안에서 한 번, 밖에서 또 한 번** 실행될 수 있다. 보통 `OncePerRequestFilter`를 상속해(한 요청에 한 번만 실행 보장) 이 문제를 피하고, 필요하면 `FilterRegistrationBean.setEnabled(false)`로 자동 등록을 끈다. (문서 발췌에 클래스 선언부가 없어 이 저장소가 어떻게 했는지는 다음 zip에서 확인할 것.) 🔗 4-8절 ③ `FilterRegistrationBean`(CorsFilter 순서 지정)과 같은 도구.
6. 🔗 4-8절 ③ "`CorsFilter`를 `HIGHEST_PRECEDENCE`로 두는 이유 — Security가 preflight(OPTIONS)를 401로 막으므로" — **JWT를 붙이는 지금이 바로 그 설정이 실제로 필요해지는 시점**이다. `/api/**`가 `authenticated()`이므로 CORS 필터가 먼저 돌지 않으면 브라우저의 preflight가 401로 막힌다.

**Step 9 — 권한 처리**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity      // 메서드 단위 권한 검사를 켠다
public class SecurityConfig { ... }

@RestController
@RequestMapping("/api/students")
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")   // 조회는 둘 다 가능
public class StudentController {
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")                // 메서드 설정이 우선한다
    public ResponseEntity<StudentDTO.Response> createStudent( ... ) { ... }
}
```
| 대상 | 조회 (GET) | 등록·수정·삭제 |
|---|---|---|
| `/api/students` | `ROLE_USER` 또는 `ROLE_ADMIN` | `ROLE_ADMIN` |
| `/api/students/detail` | 〃 | 〃 |
| `/api/departments` | 〃 | 〃 |
| `/api/search` | 〃 | — |
- **클래스에 공통 규칙, 메서드에 예외 규칙** — 🔗 4-1절 `@Transactional(readOnly=true)` 클래스 + 메서드 재정의와 **완전히 같은 패턴**. "메서드 설정이 클래스 설정보다 우선한다."
- 🔗 3-9절 해설 2 `hasAuthority` vs `hasRole`, 해설 3 "`@PreAuthorize`도 AOP 프록시" 그대로.
- 확인: user 토큰으로 `DELETE /api/departments/1` → **403**.

**Step 10 — 401/403 응답 정리**
```java
// SecurityConfig - 폼 로그인을 쓰지 않고 401 / 403 을 직접 응답한다
.exceptionHandling(ex -> ex
        .authenticationEntryPoint(authenticationEntryPoint())   // 인증 실패 401
        .accessDeniedHandler(accessDeniedHandler()))            // 권한 부족 403

// DefaultExceptionAdvice - 로그인 API 에서 발생하는 예외도 맞춘다
@ExceptionHandler(AuthenticationException.class)   -> 401
@ExceptionHandler(AccessDeniedException.class)     -> 403
```
- 문서: *"기본 설정으로는 토큰이 없을 때 로그인 페이지로 리다이렉트(302)되고, 권한 부족은 500으로 나간다."*
- ⚠️ **왜 권한 부족이 500이었나 — 5-10절 표의 마지막 줄이 이것이다.** `@PreAuthorize`는 **컨트롤러 메서드 호출 시점(DispatcherServlet 안)** 에 `AccessDeniedException`을 던진다. 그러면 Security 필터보다 **`@RestControllerAdvice`가 먼저** 예외를 받는데, 이 프로젝트에는 **`RuntimeException` → 500 핸들러**가 있다. `AccessDeniedException`은 `RuntimeException`이므로 거기에 걸려 500이 된다. → **그래서 Advice에 `AccessDeniedException` 전용 핸들러(403)를 추가**해야 한다.
- 반면 "토큰 없음"은 **필터 단계(`AuthorizationFilter`)** 에서 판정되므로 Advice가 아니라 **`authenticationEntryPoint`** 가 응답한다. → **401은 Security 설정에서, 403은 두 곳 모두에서** 맞춰야 하는 이유.
- 로그인 API(`authenticate()`)의 `BadCredentialsException`도 컨트롤러 안에서 나므로 Advice의 `AuthenticationException` 핸들러가 401로 바꾼다.

**Step 11 — 회원가입은 권한을 받지 않는다**
```java
@PostMapping("/new")
public ResponseEntity<String> addNewUser(@Valid @RequestBody SignUpRequest request) {
    if (repository.findByEmail(request.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다 : " + ...);
    }
    UserInfo userInfo = new UserInfo();
    userInfo.setName(request.getName());
    userInfo.setEmail(request.getEmail());
    userInfo.setPassword(passwordEncoder.encode(request.getPassword()));
    userInfo.setRoles(DEFAULT_ROLE);      // 서버가 ROLE_USER 로 고정한다
    ...
}
```
- ⚠️ **3-9절의 보안 구멍을 정확히 막은 것이다.** 3-9절 실행 시나리오는 이랬다:
  ```
  POST /userinfos/new
  {"name":"관리자","email":"admin@a.com","password":"pwd1","roles":"ROLE_ADMIN"}   ← 누구나 스스로 관리자가 된다
  ```
  엔티티(`UserInfo`)를 `@RequestBody`로 그대로 받으면 **클라이언트가 `roles`까지 정할 수 있다**(Mass Assignment). **`roles` 필드가 없는 DTO(`SignUpRequest`)** 로 받고 서버가 `ROLE_USER`로 고정하면 해결된다.
- 🔗 3-6절 "Entity를 요청 타입으로 쓰지 말고 DTO로" — **DTO 분리의 가장 강력한 보안상 이유**가 여기서 드러난다. (5-1절 ⑧처럼 **모르는 키는 무시**되므로, `"roles"`를 보내도 에러 없이 그냥 버려진다.)
- 확인: `roles`를 넣어 가입해도 무시 → 그 계정으로 관리자 API 호출 시 403.

**Step 12 — 로그인한 사용자를 엔티티에 저장**
```java
// Student 엔티티
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_info_id")
private UserInfo userInfo;

// StudentController
@PostMapping
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public ResponseEntity<StudentDTO.Response> createStudent(
        @Valid @RequestBody StudentDTO.Request request,
        @AuthenticationPrincipal(expression = "userInfo") UserInfo currentUser) {
    StudentDTO.Response createdStudent = studentService.createStudent(request, currentUser);
    return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
}

// StudentService
Student student = Student.builder()
        .name(request.getName())
        .studentNumber(request.getStudentNumber())
        .department(department)
        .userInfo(currentUser)        // 등록한 사용자
        .build();
```
```
+--------------+---------------+-----+-----------------+----------------+
| user_info_id | department_id | id  | name            | student_number |
+--------------+---------------+-----+-----------------+----------------+
|            1 |             1 | 197 | Simple Student  | CS998          |
+--------------+---------------+-----+-----------------+----------------+
```
- **`Student` ↔ `UserInfo`는 N:1** — 한 사용자가 여러 학생을 등록한다. **N쪽(`Student`)이 FK(`user_info_id`)를 갖고 주인**이 된다. 🔗 4-3절 "1:N에서 주인은 항상 N쪽 — 예외 없다" 규칙이 **세 번째 관계에서도** 그대로. `UserInfo` 쪽에 `@OneToMany`는 두지 않았다 → **단방향**(필요 없으면 반대쪽을 안 만든다).
- **`@AuthenticationPrincipal(expression = "userInfo")`** — `SecurityContext`의 principal(`UserInfoUserDetails`)에 대해 SpEL `userInfo`를 평가 → `getUserInfo()`가 호출되어 **`UserInfo` 엔티티가 주입**된다. 🔗 PART 2 SpEL `#{...}`.
- ⚠️ **(보충) 이 `UserInfo`는 필터에서 조회된 "준영속" 엔티티다.** 필터의 `loadUserByUsername`은 서비스 트랜잭션 밖에서 실행됐으므로, `createStudent` 트랜잭션 입장에서는 영속성 컨텍스트에 없는 객체다. 그래도 **`@ManyToOne` 연결에는 문제가 없다** — 저장 시 필요한 것은 **FK 값(id)** 뿐이기 때문이다(`Student.userInfo`에 cascade가 없으므로 `UserInfo`를 저장하려 하지도 않는다). 🔗 4-2절 엔티티 생명주기 — 준영속 객체의 **필드를 바꾸면 반영되지 않지만, 참조로 연결하는 것은 된다.**
- ⚠️ 이 컬럼이 생기면 `StudentDTO.Response`에 등록자를 담을지, 담는다면 `UserInfo` 전체가 아니라 **이름/이메일만** 담을지 결정해야 한다. `UserInfo`에는 **비밀번호 해시**가 있으므로 엔티티째 응답에 실으면 안 된다. 🔗 3-6절 DTO의 존재 이유.

**Step 13 — `@CurrentUser` 메타 애노테이션**
```java
// security/annotation/CurrentUser.java
@Target(ElementType.PARAMETER)          // 메서드 파라미터에만 붙일 수 있다
@Retention(RetentionPolicy.RUNTIME)     // 실행 시점까지 남아 있어야 스프링이 읽는다
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : userInfo")
public @interface CurrentUser {
}

// StudentController - 파라미터가 짧아진다
@PostMapping
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public ResponseEntity<StudentDTO.Response> createStudent(
        @Valid @RequestBody StudentDTO.Request request,
        @CurrentUser UserInfo currentUser) { ... }
```
```
insert into students (department_id,name,student_number,user_info_id,student_id) values (?,?,?,?,default)
binding parameter (1:BIGINT)  <- [1]                 department_id
binding parameter (2:VARCHAR) <- [Simple Student]    name
binding parameter (3:VARCHAR) <- [CS998]             student_number
binding parameter (4:BIGINT)  <- [1]                 user_info_id  <- 로그인한 admin 계정
```
**해설**
1. **메타 애노테이션** — 애노테이션에 애노테이션을 붙여 **한 번 정의하고 재사용**. 문자열 표현식(`"userInfo"`)은 오타가 나도 **컴파일에서 안 잡히므로** 한 곳에 모아 두는 것이 안전하다. 🔗 3-1절 `@SpringBootApplication`이 `@Configuration + @EnableAutoConfiguration + @ComponentScan`의 메타 애노테이션인 것과 같은 원리.
2. **`@Target`** = 붙일 수 있는 위치, **`@Retention`** = 유지 시점. ⚠️ **`RUNTIME`이 아니면 스프링이 리플렉션으로 읽지 못해 값이 주입되지 않는다**(기본값 `CLASS`는 런타임에 사라진다). 🔗 PART 1 1-12절 인터페이스 / 애노테이션도 결국 `@interface`로 선언하는 특수 인터페이스.
3. **`#this == 'anonymousUser' ? null : userInfo`** — 인증 없이 접근 가능한 API에서는 principal이 `UserInfoUserDetails`가 아니라 **문자열 `"anonymousUser"`** 다(익명 인증). 그 상태로 `userInfo`를 평가하면 문자열에 `getUserInfo()`가 없어 오류 → 삼항 연산자로 **`null`을 주입**한다. 🔗 PART 1 1-6절 삼항 연산자.

**Step 14 — 클라이언트에게 권한 알려 주기 (로그인 응답 + `/me`)**
```
# 토큰의 Payload 를 풀어 보면
{ "sub": "admin@aa.com", "iat": 1789760454, "exp": 1789764054 }      <- roles 가 없다
```
```java
// UserInfoResponse - 인증된 사용자에서 응답을 만든다
public static UserInfoResponse from(UserInfoUserDetails userDetails) {
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
    return new UserInfoResponse(userDetails.getUsername(), userDetails.getUserInfo().getName(), roles);
}

// UserInfoController - 내 정보
@GetMapping("/me")
public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal UserInfoUserDetails userDetails) {
    return ResponseEntity.ok(UserInfoResponse.from(userDetails));
}
```
```
POST /api/userinfos/login  ( admin )
-> { "accessToken":"eyJ...", "tokenType":"Bearer", "expiresIn":3600,
     "email":"admin@aa.com", "name":"adminboot", "roles":["ROLE_ADMIN","ROLE_USER"] }
GET /api/userinfos/me  ( user 토큰 )  -> 200 { "email":"user@aa.com", "name":"userboot", "roles":["ROLE_USER"] }
GET /api/userinfos/me  ( 토큰 없음 )  -> 401
```
- 클라이언트는 권한에 따라 **등록·삭제 버튼을 숨기고 싶어 한다.** 그런데 토큰에는 `roles`가 없다 → **로그인 응답에 사용자 정보·권한을 담고**, 새로고침 뒤 토큰만 남았을 때를 위해 **`GET /me`** 를 둔다.
- ⚠️ **`roles`는 화면 표시용**이다. 클라이언트가 값을 바꿔도 **서버의 `@PreAuthorize` 검사는 그대로**다 — 권한 판단은 언제나 서버가 **DB에서 다시 읽은** 권한으로 한다(Step 8 해설 4).
- JWT에 `roles` 클레임을 넣는 방법도 있지만, **클라이언트가 토큰을 해석해야 하므로** 응답에 담는 쪽을 택했다(문서). (문서 초반 Postman 캡처는 `accessToken`/`tokenType`/`expiresIn` 3개만 오던 **Step 7 시점**의 응답이다.)
- `/me`는 공개 URL 목록에 없으므로 `/api/**` 규칙에 따라 **토큰이 필요하다.**

### ⑤ 상황별 응답 코드 (문서 2-7, 실행 확인값)

| 상황 | 응답 | 누가 응답하나 |
|---|---|---|
| 토큰 없이 호출 | **401** | `AuthenticationEntryPoint` |
| 만료·위조된 토큰 | **401** | 필터가 통과시킴 → 인가 단계에서 차단 |
| 비밀번호 틀림 | **401** | `BadCredentialsException` → Advice |
| 권한 부족 (USER가 삭제) | **403** | `@PreAuthorize` → `AccessDeniedException` → Advice/Handler |
| 요청 형식 오류 (이메일 형식) | **400** | `@Valid` |
| 정상 토큰 | 200 | — |

### ⑥ 자주 만나는 오류 (문서 2-7)

| 증상 | 원인 | 해결 |
|---|---|---|
| 모든 요청이 401 | 헤더 누락 또는 형식 오류 | `Authorization: Bearer <토큰>` (**Bearer 뒤 공백 1칸**) |
| 로그인은 되는데 403 | 권한 부족 계정 | admin으로 재로그인 / `@PreAuthorize` 확인 |
| 토큰이 갑자기 401 | **만료** (기본 60분) | 다시 로그인 |
| 기동 실패: key size | `jwt.secret`이 32바이트 미만 | 더 긴 문자열 (HS256은 256비트 이상) |
| 로그인 500 | `roles`가 null인 계정 | 회원가입 API로 만든 계정 사용 (서버가 `ROLE_USER` 지정) |
| 401 대신 302 리다이렉트 | `formLogin`이 켜져 있음 | `formLogin` 제거 + EntryPoint로 401 |

- "로그인 500 — roles가 null"은 **3-9절 방식의 `UserInfoUserDetails`(null 체크 없음)** 에서 나는 NPE다. 2-7의 생성자는 null을 `""`로 바꾸지만, `roles` 컬럼이 `nullable = false`라 **DB에 직접 넣은 데이터**가 아니면 애초에 null이 될 수 없다.
- "401 대신 302" — 🔗 3-9절은 `formLogin(withDefaults())`를 썼다. **REST API에 폼 로그인이 켜져 있으면 인증 실패가 "로그인 페이지로 이동"(302)** 이 되어 프론트엔드가 처리하기 어렵다.

### ⑦ 3-9절(폼 로그인) ↔ 2-7(JWT) 한눈에

| | 3-9 `springboot` | 2-7 JWT |
|---|---|---|
| 로그인 | `formLogin()` → `POST /login` | **`POST /api/userinfos/login`** (직접 만든 API) |
| 로그인 결과 | 세션(`JSESSIONID` 쿠키) | **JWT** (응답 본문) |
| 이후 요청 | 쿠키 자동 전송 | **`Authorization: Bearer`** 직접 전송 |
| 세션 | `IF_REQUIRED` (기본) | **`STATELESS`** |
| 인증 상태 복원 | 세션에서 | **`JwtAuthenticationFilter`가 매 요청** |
| `csrf.disable()` | ⚠️ 취약 (세션+쿠키) | **정당** (헤더 토큰) |
| `AuthenticationProvider` | 수동 빈 | **자동 구성** |
| 회원가입 | `@RequestBody UserInfo` (**roles 주입 가능 ⚠️**) | **`SignUpRequest` + 서버가 `ROLE_USER` 고정** |
| 인증 실패 응답 | 302 (로그인 페이지) | **401 JSON** |
| 권한 부족 응답 | (Advice에 따라 500 가능) | **403 JSON** |
| 로그인 사용자 사용 | — | **`@CurrentUser UserInfo`** → `Student.userInfo` |

### ⑧ 다음 단계 (문서 2-7)

- **리프레시 토큰** — 액세스 토큰을 짧게(예: 15분), 리프레시 토큰으로 재발급
- **로그아웃 / 토큰 무효화** — 블랙리스트 또는 토큰 버전 관리 (JWT의 "회수 불가" 약점 보완)
- **소셜 로그인** — OAuth 2.0 / OIDC (`spring-boot-starter-oauth2-client`)

---

## 5-12. PART 5 자가 점검 질문

**버전·환경**
1. 이 저장소의 실제 Spring Boot 버전은? 그에 따라 Hibernate·Spring Security·Jackson의 메이저 버전은 각각 무엇인가?
2. Jackson 3로 올라갔는데도 `com.fasterxml.jackson.annotation.JsonInclude` import가 그대로 동작하는 이유는?

**1:1 심화 (2-4)**
3. `student.getStudentDetail()`까지는 쿼리가 안 나가고 `.getAddress()`에서 나가는 이유는? 그런데 이 저장소의 `Student.studentDetail`은 왜 이 설명대로 동작하지 않나?
4. 프록시에서 `getId()`를 호출하면 초기화가 일어나나?
5. `existsByEmail`, `existsById`, `deleteById`가 실제로 내보내는 SQL은 각각 어떤 모양인가? `deleteById` 전에 `existsById`를 검사하는 이유는?
6. UNIQUE 컬럼에 NULL이 여러 개 들어갈 수 있는 이유는? 애플리케이션 중복 검사만으로 부족한 이유는?
7. 문서의 POST 예시처럼 `"detail": {...}`로 보내면 어떤 응답이 오고, 무엇이 저장되나?

**flush (5-2)**
8. `save()`를 호출하지 않았는데 `student_details` INSERT가 나간 이유를 FlushMode와 cascade로 설명하라.
9. 해결 방법 두 가지는? 검사 두 개 중 하나만 앞으로 옮기면?
10. 헬퍼에서 `!newDetail.getEmail().equals(currentEmail)` 방향으로 쓰면 null 체크가 필요 없는 이유는?
11. flush와 commit의 차이는? flush 후 예외가 나면 이미 나간 UPDATE는 어떻게 되나?

**1:N 완성 (5-3, 5-4)**
12. `department.getStudents().add(student)`를 안 해도 FK가 저장되는 이유는? 그럼 1:1에서 `student.setStudentDetail()`은 왜 꼭 필요한가?
13. `departmentId`를 `long`이 아니라 `Long`으로 받아야 하는 이유는?
14. 학생이 있는 학과를 지우면 cascade가 없는 상태에서 무슨 일이 생기나? 이 코드는 그것을 어떻게 409로 바꾸나?
15. `updateDepartment`의 응답을 만들 때 추가로 나가는 쿼리는? `getDepartmentById`와 무엇이 다른가?
16. `/api/departments/paged`와 `/api/departments/{id}`가 충돌하지 않는 이유는?

**N+1·Projection (5-5, 5-6)**
17. `default_batch_fetch_size`가 적용되는 연관 3종과 적용되지 않는 1종은?
18. `findAll()` 방식 학과 목록이 SQL 10번인 내역을 설명하라. 학생 1,000명이면?
19. 학생 응답의 학과 정보에 `studentCount`를 넣지 않은 이유 3가지는?
20. Projection 3방식(Object[] / 인터페이스 / 생성자)을 비교하라. Closed와 Open Projection의 차이는?

**페이징 (5-7, 5-8)**
21. `Pageable`이 JPA가 아니라 Spring Data Commons에 있는 이유는?
22. MariaDB 로그의 `limit ?,?`에서 두 숫자는 각각 무엇인가?
23. `size`와 `numberOfElements`가 다른 경우는? `totalElements`는 어떻게 구하나?
24. `@PageableDefault`를 생략하면 기본 size는? `size=1000000`을 보내면?
25. `Page.map()`을 쓰는 이유는? `.stream().map().toList()`와 무엇이 다른가?
26. `Page`·`Slice`·`List`를 count 쿼리 관점에서 비교하라.
27. `GET /api/students/paged`가 SQL 1번인 이유는?
28. ToOne fetch join은 페이징과 함께 써도 되고 컬렉션 fetch join은 안 되는 이유를 "행"으로 설명하라.
29. 실험 (1)~(4)의 결과를 각각 말하라. (2)에서 `totalElements`까지 틀린 이유는? (3)이 경고가 아니라 기동 실패인 이유는?
30. "목록 + 자식 목록"이 꼭 필요할 때의 해법은?
31. `countQuery`를 지정해야 하는 경우 2가지를 이 저장소 메서드로 들어라. `countQuery`로 HHH90003004가 해결되나?
32. fetch join한 컬렉션에 WHERE 조건을 걸면 안 되는 이유는?

**검색·예외 (5-9, 5-10)**
33. `findByNameContainingIgnoreCase`에 `@Query`가 붙은 이유는? 이때 메서드 이름은 어떤 역할을 하나?
34. `keyword=cs001`로 통합 검색하면 어떻게 되나? 고치는 방법은?
35. `/api/search/departments/999/students`와 `/api/departments/999/students`의 응답 차이는?
36. `GET /api/students/abc`가 이 프로젝트에서 500이 되는 이유는?
37. OSIV란? Boot 기본값에서 컨트롤러의 LAZY 접근이 예외 없이 되는 이유와, 그게 왜 문제인가?

**JWT (5-11)**
38. JWT의 세 부분과 각 역할은? Payload에 비밀번호를 넣으면 안 되는 이유는?
39. 세션 방식 대비 JWT의 장점 2가지와 약점 1가지는? 약점의 보완책은?
40. JWT와 OAuth 2.0의 관계를 한 문장으로 말하라.
41. 필터가 잘못된 토큰에 예외를 던지지 않고 통과시키는 이유는?
42. `STATELESS`의 의미는? 3-9절의 `csrf.disable()`은 취약했는데 여기서는 괜찮은 이유는?
43. `@PreAuthorize` 권한 부족이 (Step 10 이전에) 500으로 나가던 이유를 이 프로젝트의 Advice 구조로 설명하라.
44. 회원가입을 `SignUpRequest`로 받는 이유는? 3-9절 방식의 구멍은?
45. `@AuthenticationPrincipal(expression = "userInfo")`는 무엇을 주입하나? `@CurrentUser`의 삼항식이 필요한 이유는? `@Retention(RUNTIME)`이 빠지면?
46. 필터에서 조회된 `UserInfo`(준영속)를 `Student.userInfo`에 넣어도 저장이 되는 이유는?
47. 로그인 응답에 `roles`를 담는 이유와, 그 값을 클라이언트가 바꿔도 안전한 이유는?
48. `jwt.secret=${JWT_SECRET:...}`의 기본값이 소스에 있는 것이 왜 위험한가?
