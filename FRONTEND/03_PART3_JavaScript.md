# PART 3. JavaScript

## 3-0. 저장소 개요

```
03_js/                     ← 「JavaScript 기초」 12개 장. 장마다 HTML 1개 + JS 1개
   ch01_console     script 붙이는 3가지, console.log/error/table
   ch02_variable    const 와 let, const 인데 내용이 바뀌는 경우
   ch03_datatype    자료형 6개, 문자열 메서드, 템플릿 리터럴
   ch04_operator    === vs ==, truthy/falsy, 삼항, || , ?. , ??
   ch05_control     if, early return, forEach / for...of / for
   ch06_function    화살표 함수 4단계 축약, 콜백, 괄호의 차이
   ch07_object      평평한 구조 vs 중첩 구조, JSON.stringify/parse
   ch08_array       forEach / map / filter / find, 표 그리기
   ch09_dom         요소 찾기, value/textContent/innerHTML, createElement
   ch10_event       addEventListener, preventDefault, DOMContentLoaded
   ch11_fetch       비동기, then 체인, response.ok, POST 옵션
   ch12_error       오류 6종을 직접 발생시켜 보는 페이지
   data/students.json   서버 없이 fetch 를 연습하기 위한 로컬 데이터

student_js/                ← 3부 실습 완성본 (실습 3-1 ~ 3-11)
   student.html     학생 등록 폼 + 학생 목록 표
   css/form.css     실습 3-3 의 외부 스타일
   js/form.js       ★ 400여 줄. 이 파트의 결과물이자 4부의 출발점
```

- 예제 페이지의 `out(...)` 은 `assets/demo.js` 가 제공하는 헬퍼다. `console.log` 대신 **화면에 결과를 찍어** 개발자 도구를 안 열어도 보이게 한 것.
- ⚠️ `03_js/data/students.json` 은 **Spring Boot 서버 응답과 똑같은 구조**로 만들어져 있다. `ch11_fetch.js` 위쪽의 `LOCAL_URL` 을 `` `${API_BASE_URL}/api/students` `` 로 바꾸면 **실습 3-6과 완전히 같은 코드**가 된다.

---

## 3-1. JavaScript 붙이기와 콘솔

### 개념

```html
<!-- ① 태그에 직접 (권장하지 않음) -->
<button onclick="alert('안녕')">클릭</button>

<!-- ② 내부 : body 끝에 script 태그 -->
<script>console.log("안녕");</script>

<!-- ③ 외부 파일 (실무 표준) -->
<script src="js/form.js"></script>
```

⚠️ **`script` 태그는 `</body>` 바로 앞에 둔다.** 브라우저는 HTML을 위에서 아래로 읽기 때문에, 스크립트를 위쪽에 두면 **아직 만들어지지도 않은 요소를 찾으려다 `null` 을 받는다.** `Cannot read properties of null` 오류의 큰 원인 중 하나.

⚠️ `<script src="..."></script>` 는 반드시 **닫는 태그**를 쓴다. `<script src="..." />` 는 동작하지 않는다.

| 함수 | 용도 |
|---|---|
| `console.log(x)` | 일반 출력. 객체를 펼쳐서 볼 수 있음 |
| `console.error(x)` | 빨간색으로 강조 |
| `console.table(arr)` | **배열을 표로** — 목록 확인에 아주 편리 |

`alert()` 도 값을 보여주지만 화면 전체를 멈추고, 객체를 넣으면 `[object Object]` 라는 쓸모없는 글자만 보여준다. **디버깅에는 언제나 `console.log`.**

### 코드 위치

`03_js/js/ch01_console.js`

```js
// 이 파일은 외부 JS 파일입니다. HTML 에는 script 태그 한 줄만 있습니다.
out("① 외부 JS 파일이 실행되었습니다.");

var students = [
    { id: 1, name: "홍길동", studentNumber: "20241234" },
    { id: 2, name: "김코딩", studentNumber: "20241235" }
];

document.getElementById("btnTable").addEventListener("click", function () {
    // console.table 은 배열을 표로 그려 줍니다. 목록 확인에 아주 편리합니다.
    console.table(students);
});
```

**해설**
- 🔗 실습 3-6의 `renderStudentTable` 안에서 `console.table(students)` 를 찍어 보면, **서버가 보낸 배열이 Console에 깔끔한 표로 나타난다.** 표가 화면에 안 그려질 때 "데이터가 안 온 것인지, 그리는 코드가 잘못된 것인지"를 1초 만에 가른다.

⚠️ Console에 아무것도 안 찍힌다 → ① `script` 가 `</body>` 앞에 있는지 ② `src` 경로(Network 탭 404) ③ **코드 위쪽에서 오류가 나서 멈춘 것은 아닌지**

---

## 3-2. 변수 — const 와 let

### 개념

| 키워드 | 재할당 | 언제 |
|---|---|---|
| `const` | 불가 | **기본값. 대부분 이것** |
| `let` | 가능 (블록 스코프) | 값이 바뀌어야 할 때만 |
| `var` | 가능 (함수 스코프, 옛날 방식) | **쓰지 마세요** |

**원칙은 간단하다. 일단 `const` 로 쓰고, 값을 바꿔야 해서 오류가 나면 그때 `let` 으로 바꾼다.** 이렇게 하면 "이 값은 안 바뀐다"는 정보가 코드에 그대로 드러나 읽기가 쉬워진다.

**const 인데 내용이 바뀌는 경우**

```js
const student = { name: "홍길동" };
student.name = "김코딩";       // OK — 객체의 속성은 바꿀 수 있음
student = { name: "김코딩" };  // TypeError — 변수 자체를 갈아끼우는 것은 불가

const myList = [1, 2];
myList.push(3);                // OK — 배열에 요소 추가는 가능
```

**`const` 는 "이 이름이 가리키는 대상을 바꾸지 마라"는 뜻이지, "그 대상의 내용을 바꾸지 마라"는 뜻이 아니다.**

**이름 짓기** — 낙타 표기법(`studentTableBody`), 절대 안 바뀌는 설정값은 대문자와 밑줄(`API_BASE_URL`). **대소문자를 구분한다.** `studentForm` 과 `studentform` 은 완전히 다른 변수다.

### 코드 위치

`03_js/js/ch02_variable.js`

```js
// const : 재할당 불가. 대부분의 값은 이것으로 충분합니다.
const API_BASE_URL = "http://localhost:8080";

// let : 값이 바뀌어야 하는 경우에만 사용합니다.
let editingStudentId = null;

// let 은 다시 넣을 수 있습니다 — 실습 3-9 의 모드 전환이 이 방식입니다.
editingStudentId = 7;
```

`student_js/js/form.js` 첫 4줄 — **3부 전체의 상태가 여기 다 있다**

```js
// 전역 변수
const API_BASE_URL = "http://localhost:8080";
// 현재 수정 중인 학생 ID
let editingStudentId = null;
```

**해설**
- 실습 3-4의 변수가 전부 `const` 이고, **실습 3-9에서 모드를 저장하는 `editingStudentId` 하나만 `let`** 인 이유가 이것이다.
- 🔗 **이 `let` 변수 하나가 화면 전체의 모드를 결정한다.** 값이 있으면 수정 모드, `null` 이면 등록 모드. 이것이 React 등에서 말하는 **"상태(state)"의 가장 원시적인 형태**다. 5부에서 `const [editingId, setEditingId] = useState(null)` 이 되는 바로 그 변수다.

---

## 3-3. 자료형과 템플릿 리터럴

### 개념

| 자료형 | 예 | 설명 |
|---|---|---|
| 문자열(string) | `"홍길동"` | 따옴표로 감쌈 |
| 숫자(number) | `42`, `3.14` | **정수와 소수의 구분이 없음** |
| 불리언(boolean) | `true`, `false` | 조건문의 결과 |
| `null` | `null` | "값이 없음"을 **개발자가 명시적으로** 넣은 것 |
| `undefined` | `undefined` | **아직 아무 값도 넣지 않은** 상태 |
| 객체(object) | `{ name: "홍길동" }` | 7절에서 |

`null` 과 `undefined` 는 둘 다 "없음"이지만 유래가 다르다. **`null` 은 "내가 일부러 비워 뒀다", `undefined` 는 "아무도 채우지 않았다".**

🔗 실습 3-7에서 이메일이 비었을 때 `|| null` 로 바꿔 보내는 이유는, **서버가 빈 문자열은 해석하지 못하고 `null` 은 "값 없음"으로 이해하기 때문**이다.

**문자열 메서드** : `trim()` `length` `toUpperCase()` `split(",")` `includes("길")`

**템플릿 리터럴 — 백틱 문자열**

```js
// 예전 방식 — 따옴표와 + 로 이어 붙임
const url1 = API_BASE_URL + "/api/students/" + id;

// 템플릿 리터럴 — 훨씬 읽기 쉬움
const url2 = `${API_BASE_URL}/api/students/${id}`;

// 여러 줄도 그대로
const row = `
    <td>${student.name}</td>
    <td>${student.studentNumber}</td>
`;
```

백틱(`` ` ``)은 키보드 숫자 1 왼쪽, Tab 위. **작은따옴표와 다르다.**

### 코드 위치

`03_js/js/ch03_datatype.js` — **실습 3-5에서 만나는 오류를 그대로 재현해 둔 부분이 핵심**

```js
document.getElementById("btnTrim").addEventListener("click", function () {
    // 실습 3-5 에서 만나는 오류를 그대로 재현합니다.
    const value = null;              // name 속성이 없는 입력창에서 오는 값
    try {
        out(value.trim());
    } catch (error) {
        out("오류 발생 →", error.name + ": " + error.message);
        out("formData.get('address') 가 null 을 돌려주면 .trim() 에서 이 오류가 납니다.");
        out("해당 input 에 name 속성이 있는지 확인하세요.");
    }
});
```

**해설**
- `formData.get("name").trim()` 에서 `trim()` 을 쓰는 이유는, **사용자가 공백만 입력하고 등록을 눌러도 통과되는 것을 막기 위해서**다.
- 🔗 이 오류가 1-6절(HTML의 `name` 속성)과 3-9절(FormData)을 잇는 고리다. **`name` 속성 누락 → FormData 에 안 담김 → `get()` 이 null → `.trim()` 폭발.** 이 사슬을 통째로 기억할 것.

⚠️ 화면에 `${student.name}` 이 **글자 그대로** 보인다 → 백틱이 아니라 따옴표를 썼다.
⚠️ 백틱 안에 `//` 주석을 넣으면 **주석이 아니라 그냥 글자로 화면에 출력된다.** 주석은 반드시 백틱 **바깥**에.

---

## 3-4. 연산자 — ===, truthy/falsy, 삼항, ||, ?., ??

### 개념

| 연산자 | 의미 | 주의 |
|---|---|---|
| `===` | 값과 자료형이 모두 같다 | **항상 이것을 쓰세요** |
| `!==` | 값이나 자료형이 다르다 | 항상 이것 |
| `==` | 자료형을 맞춰본 뒤 비교 | `1 == "1"` 이 true. **혼란의 원인** |

`0 == ""` 가 true, `null == undefined` 가 true 처럼 직관에 어긋나는 결과가 많다. `===` 를 쓰면 이런 문제가 아예 생기지 않는다.

**참 같은 값, 거짓 같은 값**

| 거짓으로 취급(falsy) — 6개 | 나머지는 전부 참(truthy) |
|---|---|
| `false`, `0`, `""`(빈 문자열), `null`, `undefined`, `NaN` | `"0"`, `"false"`, `[]`(빈 배열), `{}`(빈 객체) **도 모두 참** |

⚠️ **빈 배열 `[]` 과 빈 객체 `{}` 가 참이라는 점에 주의.** 배열이 비었는지 확인하려면 `if (list.length === 0)` 처럼 `length` 를 봐야 한다.

**삼항 연산자와 || 기본값**

```js
student.detail ? student.detail.address : "-"           // 조건 ? 참 : 거짓
errorData.message || "학생 등록에 실패했습니다."           // 왼쪽이 falsy 면 오른쪽
formData.get("email").trim() || null
```

**?? 와 ?. — 최신 문법 (ES2020)**

`??`(널 병합)는 `||` 와 비슷해 보이지만 **걸러내는 범위가 다르다.** `||` 는 falsy 6개를 모두 밀어내고, `??` 는 **`null` 과 `undefined` 두 가지만** 밀어낸다.

```js
const user = { name: "김코딩", age: 0, address: null };

user.age || 25    // 25  ← 0 이 falsy 라 밀려난다
user.age ?? 25    // 0   ← 의도한 0 이 그대로 유지된다
```

⚠️ **나이·수량·점수처럼 `0` 이 의미를 갖는 값에는 `||` 대신 `??` 를 쓴다.**
반대로 실습 3-7의 `formData.get("email").trim() || null` 은 **빈 문자열도 걸러내야 하므로 `||` 가 맞다.**
"무엇을 없는 값으로 볼 것인가"를 먼저 정하고 고르면 헷갈리지 않는다.

`?.`(옵셔널 체이닝)은 "중간에 값이 없으면 오류 대신 `undefined` 를 돌려줘"라는 뜻이다.

```js
student.detail.address       // TypeError — undefined 에 점을 찍었다
student.detail?.address      // undefined — 오류 없이 넘어간다
student.detail?.address ?? "-"   // "-"  ← 두 연산자를 함께
student.getName?.()          // 함수가 없으면 undefined (호출하지 않음)
list?.[0]                    // 배열이 없으면 undefined
```

### 코드 위치

`03_js/js/ch04_operator.js` — **falsy 12개를 한 번에 찍어 보는 부분**

```js
document.getElementById("btnTruthy").addEventListener("click", function () {
    const values = [false, 0, "", null, undefined, NaN,
                    "0", "false", [], {}, 1, "홍길동"];

    values.forEach(function (v) {
        // 값 앞에 ! 를 두 번 붙이면 불리언으로 바뀝니다.
        out((!!v ? "참   " : "거짓 ") + "  " + label);
    });
    out("실습 3-5 의 if (!student.name) 이 이 성질을 씁니다.");
});

document.getElementById("btnTernary").addEventListener("click", function () {
    const withDetail = { name: "홍길동", detail: { address: "서울시 강남구" } };
    const noDetail   = { name: "김코딩" };

    out("detail 있음 :", withDetail.detail ? withDetail.detail.address : "-");
    out("detail 없음 :", noDetail.detail ? noDetail.detail.address : "-");

    // ?. 옵셔널 체이닝 + ?? 널 병합 (최신 문법)
    out("?. 와 ??    :", noDetail?.detail?.address ?? "-");
});
```

**해설**
- 🔗 교재는 이해하기 쉬운 삼항 연산자를 먼저 쓰고, `student_js/js/form.js` 의 최종본에서는 이미 `?.` 와 `??` 로 바뀌어 있다. 4부·5부는 전부 `?.` / `??` 다. **같은 뜻이고, 중첩이 깊어질수록 차이가 커진다.**

```js
// form.js renderStudentTable — 최신 문법으로 쓴 최종형
<td>${student.detail?.address ?? "-"}</td>
<td>${student.detail?.email ?? "-"}</td>
```

⚠️ `SyntaxError: Unexpected token '??'` → `||` 와 `??` 를 괄호 없이 섞어 썼다. `(a || b) ?? c` 처럼 묶어야 한다.
⚠️ `?.` 를 썼는데도 같은 오류 → `student?.detail.address` 는 `student` 까지만 보호한다. **없을 수 있는 단계마다** `student?.detail?.address` 처럼 붙여야 한다.

---

## 3-5. 조건문과 반복문 — 그리고 early return

### 개념

```js
if (editingStudentId) {
    updateStudent(editingStudentId, studentData);   // 수정 모드
} else {
    createStudent(studentData);                      // 등록 모드
}
```

**실습 3-9의 이 코드가 3부에서 가장 중요한 조건문이다.** `editingStudentId` 에 값이 있으면(truthy) 수정, `null` 이면(falsy) 등록. **변수 하나가 화면 전체의 모드를 결정하는 구조.**

**early return — 검증 코드의 기본 형태**

`return` 을 만나면 함수는 그 자리에서 즉시 끝난다. **"문제가 있으면 바로 나가고, 끝까지 살아남으면 통과"**로 쓰면 `if` 를 여러 겹 중첩하지 않아도 되어 훨씬 읽기 쉽다.

**반복문 3종**

```js
students.forEach((student) => console.log(student.name));   // 가장 많이 씀
for (const student of students) { if (...) break; }         // 중간에 멈춰야 할 때
for (let i = 0; i < students.length; i++) { }               // 인덱스가 필요할 때
```

본 교재는 `forEach` 만 쓴다. 배열의 모든 요소를 하나씩 처리하는 데 가장 읽기 쉽기 때문.

### 코드 위치

`03_js/js/ch05_control.js` — **검증 함수를 4가지 데이터로 돌려보는 예제**

```js
// 실습 3-9 와 같은 "상태" 변수
let editingStudentId = null;

document.getElementById("btnMode").addEventListener("click", function () {
    // 버튼을 누를 때마다 등록 모드 <-> 수정 모드를 오갑니다.
    editingStudentId = editingStudentId ? null : 7;

    if (editingStudentId) {
        out("  → 수정 모드 : updateStudent(" + editingStudentId + ", data) 호출");
        out("  → 버튼 문구를 '학생 수정' 으로 바꾸고 취소 버튼을 보여줍니다.");
    } else {
        out("  → 등록 모드 : createStudent(data) 호출");
    }
});

/**
 * 학생 데이터가 유효한지 검사한다. (실습 3-5 와 같은 구조)
 */
function validateStudent(student) {
    if (!student.name) {
        out("  검증 실패 : 이름을 입력해주세요.");
        return false;                 // early return
    }
    if (!student.studentNumber) {
        return false;
    }
    if (!student.detailRequest.address) {
        return false;
    }
    return true;
}
```

`student_js/js/form.js` — 실제 제출 핸들러

```js
// 유효성 검사
if (!validateStudent(studentData)) {
    return;
}

// 수정
if (editingStudentId) {
    updateStudent(editingStudentId, studentData);
} else {
    // 등록
    createStudent(studentData);
}
```

**해설**
- 🔗 이 `validateStudent` 가 4부에서 `lib/validation.js` 의 **순수 함수**로 옮겨 간다. `alert` 를 제거하고 **오류 메시지 문자열을 돌려주는** 형태로 바뀐다(4-8절). 5부에서는 그 파일을 **한 줄도 안 고치고 그대로 가져다 쓴다.**

---

## 3-6. 함수와 화살표 함수

### 개념

**`loadStudents` 와 `loadStudents()` 는 다르다.** 괄호가 없으면 "함수 그 자체", 있으면 "지금 실행하라". **이 차이가 10절(이벤트)에서 결정적으로 중요해진다.**

**화살표 함수 — 축약 4단계**

```js
function (n) { return n * 2; }   // ① 전통적인 표기
(n) => { return n * 2; }         // ② 화살표 함수
(n) => n * 2                     // ③ 중괄호와 return 생략
n => n * 2                       // ④ 매개변수 괄호까지 생략
```

| 규칙 | 설명 |
|---|---|
| 매개변수가 하나면 | 괄호를 생략할 수 있다 |
| 매개변수가 없으면 | **빈 괄호가 필요하다** : `() => ...` |
| 몸통이 한 줄이면 | 중괄호와 `return` 을 동시에 생략 |
| **중괄호를 쓰면** | **`return` 을 반드시 직접 써야 한다** (빠뜨리면 undefined) |
| 객체를 바로 돌려주려면 | 괄호로 감싼다 : `() => ({ ok: true })` |

**콜백(callback)** — 다른 함수에 넘겨주는 함수. `then( )` 괄호 안에 들어간 것은 **"나중에 실행할 함수"**다. 지금 실행하는 것이 아니라 "다 되면 이걸 해 줘" 하고 맡겨두는 것. 화살표 함수가 널리 쓰이는 이유는 **이런 콜백을 짧게 쓸 수 있기 때문**이다.

**호이스팅** — `function` 으로 선언한 함수는 파일 전체에서 미리 인식된다. 그래서 정의보다 위에서 불러도 동작한다. 반면 **화살표 함수는 `const` 에 담기므로 정의보다 위에서 부르면 오류가 난다.** → 4-3절 TDZ.

### 코드 위치

`03_js/js/ch06_function.js`

```js
// function 으로 선언한 함수는 파일 전체에서 미리 인식됩니다(호이스팅).
out("greet 호출 결과 :", greet("홍길동"));
function greet(name) { return name + "님, 안녕하세요."; }

document.getElementById("btnReturn").addEventListener("click", function () {
    const good = (n) => { return n * 2; };   // 중괄호 + return
    const bad  = (n) => { n * 2; };          // 중괄호를 썼는데 return 누락

    out("return 있음 →", good(21));
    out("return 없음 →", bad(21), "  ← undefined 가 돌아옵니다");
});

document.getElementById("btnParen").addEventListener("click", function () {
    // 잘못된 방식 : 지금 즉시 실행되고, 그 반환값(문자열)이 등록됩니다.
    tempButton.addEventListener("click", sayHi());
    out("  → 클릭하지도 않았는데 위에서 이미 실행되었습니다.");

    // 올바른 방식 : 함수 자체를 넘깁니다.
    out("올바른 방식 — addEventListener('click', sayHi)");
});
```

**해설**
- 마지막 실험이 이 장의 핵심이다. **괄호를 붙이면 등록하는 그 순간 함수가 실행되어 버리고, 정작 클릭했을 때는 아무 일도 일어나지 않는다.**
- 🔗 5부 React에서 같은 실수를 하면 훨씬 심각해진다. `onClick={onDelete(id)}` 는 그리는 순간 실행되고, 그 함수가 `setState` 를 부르면 다시 그려지고, 또 실행되어 **무한 반복**에 빠진다 → `Too many re-renders` (5-5절).

---

## 3-7. 객체 — 3부의 최대 고비

### 개념

```js
const student = { name: "홍길동", studentNumber: "20241234", age: 20 };

student.name         // "홍길동" — 점 표기법 (주로 이걸 씁니다)
student["name"]      // "홍길동" — 대괄호 표기법
student.age = 21;    // 값 변경
student.email = "a@b.com";  // 새 속성 추가
```

**중첩 객체 — `detailRequest` 를 이해하는 열쇠**

```js
// 평평한 구조 (실습 3-5까지)
const studentData = {
    name: "홍길동", studentNumber: "20241234",
    address: "서울시", phoneNumber: "010-1234-5678"
};
studentData.address                        // "서울시"

// 중첩 구조 (실습 3-7부터)
const studentData2 = {
    name: "홍길동", studentNumber: "20241234",
    detailRequest: {                        // 객체 안의 객체
        address: "서울시",
        phoneNumber: "010-1234-5678"
    }
};
studentData2.detailRequest.address         // "서울시" — 점을 두 번
studentData2.address                       // undefined — 여기엔 없습니다!
```

⚠️ **실습 3-7에서 "`validateStudent` 도 함께 수정합니다"라고 하는 이유가 바로 이것이다.** 데이터 구조를 바꿨는데 검증 코드가 여전히 `student.address` 를 보고 있으면 `undefined` 가 나오고, 그 `undefined` 에 다시 점을 찍으면 `Cannot read properties of undefined (reading 'address')` 가 난다.

⚠️ **프론트엔드가 보내는 JSON의 키 이름과 구조는 서버가 받는 클래스(DTO)와 한 글자도 달라서는 안 된다.** `detailRequest` 를 `detail` 로 잘못 쓰면 서버에서 값이 전부 `null` 이 된다.
**요청은 `detailRequest`, 응답은 `detail` 로 이름이 다르다는 점도 함께 기억할 것.**

**JSON — 객체를 글자로**

HTTP는 문자열만 실어 나를 수 있다. 그래서 객체를 서버로 보내려면 문자열로 바꿔야 하고, 받을 때는 다시 객체로 되돌려야 한다.

```js
JSON.stringify(studentData)   // 객체 → 문자열 (보낼 때)
JSON.parse(text)              // 문자열 → 객체 (받을 때)
response.json()               // 응답 본문을 객체로 (11절에서)
```

**`JSON.stringify` 와 `response.json()` 은 한 쌍이다. 보낼 때 stringify, 받을 때 json(). 세트로 기억.**

- `stringify` 는 값이 함수이거나 `Symbol`, `undefined` 인 속성을 **결과에서 아예 뺀다.**
- 자기 자신을 참조하는 객체(순환 참조)가 있으면 오류 → `Converting circular structure to JSON`
- `JSON.stringify(obj, null, 2)` 로 **들여쓰기 2칸**의 보기 좋은 출력을 얻는다.

⚠️ **`response.json()` 은 내부적으로 `JSON.parse` 를 대신 해 주는 함수다.** 그래서 실습에서 `JSON.parse` 를 직접 쓸 일은 거의 없다. 직접 쓰는 경우는 `localStorage` 에 문자열로 저장해 둔 값을 다시 객체로 만들 때 정도.

⚠️ `Unexpected token '<' ... is not valid JSON` → **서버가 JSON이 아니라 HTML 오류 페이지를 돌려줬다.** URL과 상태 코드를 먼저 확인할 것 — 주소를 잘못 짚어 404 안내 페이지(HTML)를 받았을 때 이 오류가 흔하다.

### 코드 위치

`03_js/js/ch07_object.js` — **평평한 구조와 중첩 구조를 버튼 두 개로 대조**

```js
document.getElementById("btnNested").addEventListener("click", function () {
    // 실습 3-7 부터 쓰는 중첩 구조 (서버 DTO 에 맞춘 형태)
    const studentData = {
        name: "홍길동",
        studentNumber: "20241234",
        detailRequest: {
            address: "서울시 강남구",
            phoneNumber: "010-1234-5678",
            email: "hong@test.com",
            dateOfBirth: "2000-01-01"
        }
    };

    out("studentData.detailRequest.address →", studentData.detailRequest.address);
    out("studentData.address               →", studentData.address);
    out("두 번째가 undefined 인 이유 : address 는 detailRequest 안에 있습니다.");
    out("이것이 실습 3-7 에서 validateStudent 도 함께 고쳐야 하는 이유입니다.");
});

document.getElementById("btnError").addEventListener("click", function () {
    const student = { name: "홍길동" };     // detail 이 없는 학생

    try {
        out(student.detail.address);        // undefined 에 점을 찍으면 오류
    } catch (error) {
        out("오류 발생 →", error.name + ": " + error.message);
    }

    out("  삼항 연산자 →", student.detail ? student.detail.address : "-");
    out("  최신 문법   →", student.detail?.address ?? "-");
});
```

`student_js/js/form.js` — 실제 전송 데이터 조립

```js
//FormData에 저장된 값을 추출하여 서버로 전송할 중첩된 객체를 다시 생성하기
const studentData = {
    name: formData.get("name").trim(),
    studentNumber: formData.get("studentNumber").trim(),
    detailRequest: {
        address: formData.get("address").trim() || null,
        phoneNumber: formData.get("phoneNumber").trim(),
        email: formData.get("email").trim() || null,
        dateOfBirth: formData.get("dateOfBirth") || null,
    },
};
```

**해설**
- **평평한 폼 6칸 → 중첩된 요청 객체**로 모양을 바꾸는 이 코드가 3부 후반의 심장이다.
- 🔗 4부에서 이 블록이 `ui/studentForm.js` 의 `collectStudentData()` 로 떨어져 나가고(4-7절), 5부에서는 `lib/studentData.js` 의 `toRequest(form)` 이 된다(5-9절). **이름만 바뀌고 하는 일은 똑같다.**

**✅ 디버깅 습관** — `console.log(JSON.stringify(studentData, null, 2))` 를 쓰면 중첩 구조가 한눈에 들어온다. **F12 → Network → Payload 탭에서 보는 모양과 같아서**, 내가 만든 객체와 실제로 보낸 JSON을 비교하기에 좋다.

---

## 3-8. 배열과 배열 메서드

### 개념

```js
const students = [ { id: 1, name: "홍길동" }, { id: 2, name: "김코딩" } ];
students.length     // 2
students[0]         // { id: 1, name: "홍길동" } — 0부터
students[0].name    // "홍길동"
students.push(x)    // 끝에 추가
```

**서버가 목록을 돌려줄 때 오는 것이 바로 이 형태다 — "객체가 들어 있는 배열".**

| 메서드 | 하는 일 | 돌려주는 것 |
|---|---|---|
| `forEach` | 하나씩 꺼내 무언가 실행 | **없음 (undefined)** |
| `map` | 하나씩 변환해 새 배열을 만듦 | **새 배열** |
| `filter` | 조건에 맞는 것만 골라냄 | 새 배열 |
| `find` | 조건에 맞는 첫 번째 하나 | 요소 하나 (없으면 undefined) |
| `includes` | 그 값이 들어 있는지 | true / false |

⚠️ **`forEach` 의 결과를 변수에 담으면 `undefined` 다.** 변환이 목적이라면 `map` 을 써야 한다.

### 코드 위치

`03_js/js/ch08_array.js` — **"목록 중복"의 정체를 버튼 두 개로 보여주는 예제**

```js
/**
 * 학생 배열을 표에 그린다. (실습 3-6 과 같은 구조)
 */
function renderStudentTable(list, clearFirst) {
    if (clearFirst) {
        studentTableBody.innerHTML = "";          // (1) 기존 목록 비우기
    }

    list.forEach((student) => {                   // (2) 배열을 하나씩 순회
        const row = document.createElement("tr");  // (3) 빈 행 만들기

        // (4) 행 내용 채우기 — 주석은 반드시 백틱(`) 바깥에 둡니다
        row.innerHTML = `
            <td>${student.name}</td>
            <td>${student.studentNumber}</td>
            <td>${student.detail ? student.detail.address : "-"}</td>
            <td>${student.detail ? student.detail.email || "-" : "-"}</td>
        `;

        studentTableBody.appendChild(row);         // (5) 화면에 붙이기
    });
}

document.getElementById("btnDup").addEventListener("click", function () {
    renderStudentTable(students, false);          // 비우지 않고 추가만
    out("여러 번 누르면 계속 쌓입니다. 이것이 '목록 중복' 문제의 정체입니다.");
});
```

**해설**
- ⚠️ **`innerHTML = ""` 로 먼저 비우지 않으면 목록을 새로고침할 때마다 같은 학생이 두 번, 세 번 쌓인다.** "등록했더니 목록이 중복돼요"의 원인은 거의 항상 이 한 줄이다.
- 예제 데이터의 세 번째 학생(`이자바`)은 `detail: null` 이다. **삼항 연산자 덕분에 `-` 로 표시된다.** 방어 코드가 왜 필요한지를 데이터로 보여주는 구성.
- 🔗 5부에서는 이 "비우고 다시 채우기"가 통째로 사라진다. `students` 가 바뀌면 React가 알아서 다시 그린다(5-5절).

---

## 3-9. DOM — 화면과 대화하기

### 개념

브라우저는 HTML 파일을 읽어 메모리 안에 가계도 모양의 구조를 만든다. 이것이 **DOM(Document Object Model)**이다. JavaScript는 HTML 파일을 직접 고치는 것이 아니라 **이 메모리 속 구조를 고친다.** 그래서 화면은 바뀌지만 원본 `.html` 파일은 그대로다.

```
document                       <-- 문서 전체 (뿌리)
  |- html
      |- body
          |- form#studentForm          <-- getElementById("studentForm")
          |- table#studentTable
              |- tbody#studentTableBody
```

| 방법 | 찾는 방식 | 돌려주는 것 |
|---|---|---|
| `document.getElementById("x")` | id로 | 요소 하나 (없으면 **null**) |
| `document.querySelector(".x")` | CSS 선택자로 | 첫 번째 요소 하나 |
| `document.querySelectorAll(".x")` | CSS 선택자로 모두 | 요소 목록 |
| `studentForm.name` | **form 안에서 `name` 속성으로** | 입력 요소 |

**form 요소는 특별하다.** `studentForm.name` 처럼 안에 있는 입력 요소를 **`name` 속성으로 바로 꺼낼 수 있다.** `document.getElementById("name").value` 와 `studentForm.name.value` 는 같은 결과. 여기서도 **id가 아니라 name** 이라는 점을 기억할 것.

| 속성 | 하는 일 | 주의 |
|---|---|---|
| `element.value` | 입력창의 값 읽기·쓰기 | input, select, textarea 전용 |
| `element.textContent` | 글자를 **글자 그대로** 넣기 | **안전. 기본으로 이것을** |
| `element.innerHTML` | **HTML로 해석해서** 넣기 | 태그가 살아남. **보안 주의** |
| `element.style.display` | CSS 값 직접 지정 | `'none'` / `'inline-block'` |

⚠️ **`textContent` 와 `innerHTML` 의 차이는 보안과 직결된다.** 사용자가 입력한 글자나 서버가 보낸 메시지를 `innerHTML` 로 넣으면 그 안의 `<script>` 가 **실제로 실행될 수 있다.** 실습 3-11에서 메시지 표시에 `textContent` 를 쓰는 이유가 이것.

**요소 만들어 붙이기 — 만들기 → 채우기 → 붙이기**

```js
const row = document.createElement("tr");   // 만들고
row.innerHTML = `<td>홍길동</td>`;           // 채우고
studentTableBody.appendChild(row);          // 붙인다
```

`appendChild` 를 하기 전까지는 **메모리에만 있고 화면에는 보이지 않는다.**

**FormData — 폼 값을 한 번에 읽기**

```js
const formData = new FormData(studentForm);
formData.get("name")            // <input name="name"> 의 값
```

| 메서드 | 하는 일 |
|---|---|
| `append(name, value)` | 새 키-값 추가 (파일도 가능) |
| `get(name)` | 첫 번째 값 (없으면 **null**) |
| `getAll(name)` | 같은 이름의 값을 모두 배열로 |
| `has(name)` / `set` / `delete` | 확인 / 수정 / 삭제 |
| `entries()` | 모든 키-값 쌍을 순회 가능한 형태로 |

```js
// 한 번에 객체로 바꾸기
const data = Object.fromEntries(formData.entries());
```

⚠️ **`formData.get()` 이 찾는 것은 `id` 가 아니라 `name` 속성이다.**

### 코드 위치

`03_js/js/ch09_dom.js` — **오류를 그대로 재현해 둔 부분**

```js
document.getElementById("btnText").addEventListener("click", function () {
    const danger = "<b>굵은 글씨</b>";

    target.textContent = danger;
    out("  화면에 태그가 글자 그대로 보입니다. 안전합니다.");

    setTimeout(function () {
        target.innerHTML = danger;
        out("innerHTML 로 넣으면 → 태그로 해석되어 굵게 보입니다.");
        out("  사용자 입력을 이렇게 넣으면 보안 위험이 있습니다.");
    }, 1200);
});

document.getElementById("btnNull").addEventListener("click", function () {
    // 실습 3-4 에서 만나는 오류를 그대로 재현합니다.
    const notExist = document.getElementById("studentform");   // 소문자 f (오타)

    try {
        notExist.addEventListener("click", function () {});
    } catch (error) {
        out("오류 발생 →", error.name + ": " + error.message);
        out("studentform 과 studentForm 은 완전히 다른 값입니다.");
    }
});
```

`student_js/js/form.js` — **DOM 참조를 파일 위쪽에 모아 두는 관례**

```js
// DOM 요소 참조
const studentForm = document.getElementById("studentForm");
const studentTableBody = document.getElementById("studentTableBody");
const submitButton = studentForm.querySelector('button[type="submit"]');
const cancelButton = studentForm.querySelector('.cancel-btn');

//에러메시지와 로딩메시지
const loadingMessage = document.getElementById('loadingMessage');
const formError = document.getElementById('formError');
```

**해설**
- `style.backgroundColor` 처럼 **CSS의 `background-color` 는 JS에서 낙타 표기법**이 된다. 하이픈이 사라지고 다음 글자가 대문자.
- 🔗 5부 React에서는 이 `getElementById` 블록이 통째로 사라진다. `App.jsx` 에는 **`document` 가 한 번도 나오지 않는다.** DOM 참조가 필요한 곳은 스크롤뿐이고 그것도 `useRef` 로 한다(5-6절).

⚠️ `Cannot read properties of null (reading 'addEventListener')` → `getElementById` 가 요소를 못 찾아 null을 돌려줬다. ① **id 철자와 대소문자** ② script 위치(`</body>` 앞인지)

---

## 3-10. 이벤트 — 사용자 동작에 반응하기

### 개념

```js
studentForm.addEventListener("submit", function (e) {
    e.preventDefault();      // 기본 이벤트 처리가 동작하지 못하도록 막는다
    console.log("폼 제출됨");
});
```

| 이벤트 | 언제 |
|---|---|
| `click` | 클릭했을 때 |
| `submit` | 폼이 제출될 때 (**form 요소에 등록**) |
| `change` | 값이 바뀌고 **포커스가 떠날 때** |
| `input` | **글자를 칠 때마다** |
| `DOMContentLoaded` | HTML 구조가 다 만들어졌을 때 (**document에 등록**) |

**`e.preventDefault()`** — `form` 태그는 태생적으로 "값을 서버에 보내고 페이지를 새로고침하는" 기본 동작을 갖는다. JavaScript로 직접 처리할 것이므로 이 동작을 막아야 한다.

> 실습 3-4에서 `e.preventDefault()` 를 **주석 처리하고** 버튼을 눌러보라는 실험이 있다. 주소창 끝에 `?name=&studentNumber=...` 가 붙으면서 화면이 깜빡 새로고침되고 Console 출력도 사라진다. **꼭 한 번 해 볼 것.**

**함수를 넘길 때 괄호를 붙이지 않는다**

```js
button.addEventListener("click", loadStudents);    // O 함수를 넘김
button.addEventListener("click", loadStudents());  // X 지금 실행해서 결과를 넘김
```

**`onclick` 속성 방식** — 동작은 하지만 권장되지 않는다. **함수가 반드시 전역에 있어야 하고**, 사용자 입력이 그대로 HTML에 들어가 보안 위험이 생길 수 있다. 실습 3-6이 이 방식을 쓰는 것은 이해하기 쉽기 때문.

### 코드 위치

`03_js/js/ch10_event.js` — **preventDefault 가 있는 폼과 없는 폼을 나란히**

```js
// DOMContentLoaded : HTML 구조가 완성되면 실행하라는 예약 명령
document.addEventListener("DOMContentLoaded", function () {
    out("DOMContentLoaded — HTML 구조가 완성되었습니다.");
    out("실습 3-4 에서 이 안에 loadStudents() 를 넣습니다.");
});

// --- preventDefault 가 있는 폼 ---
document.getElementById("goodForm").addEventListener("submit", function (e) {
    e.preventDefault();               // 기본 동작(전송 + 새로고침)을 막는다
    out("[goodForm] 폼 제출됨 — 화면이 새로고침되지 않았습니다.");
});

// --- preventDefault 가 없는 폼 (일부러) ---
document.getElementById("badForm").addEventListener("submit", function (e) {
    // e.preventDefault();  ← 미니 과제 : 이 줄의 주석을 풀어 보세요
    out("[badForm] 폼 제출됨 — 곧 새로고침되어 이 글자도 사라집니다.");
});
```

`student_js/js/form.js`

```js
// 초기화
document.addEventListener("DOMContentLoaded", function () {
    loadStudents();
});

// 폼 제출 이벤트 핸들러
studentForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = new FormData(studentForm);
    ...
});
```

`renderStudentTable` 안 — **onclick 방식 (4부에서 걷어낸다)**

```js
<button class="edit-btn" onclick="editStudent(${student.id})">수정</button>
<button class="delete-btn" onclick="deleteStudent(${student.id})">삭제</button>
```

**해설**
- 🔗 **이 `onclick` 두 줄이 4부에서 가장 큰 변화를 일으킨다.** 모듈 안에서 만든 함수는 전역(`window`)에 등록되지 않기 때문에, HTML 속성에서 이름으로 부를 수 없다. → `editStudent is not defined`. 그래서 4부 실습 4-10에서 **`data-action` + 이벤트 위임**으로 바뀐다(4-10절).
- 🔗 5부에서는 이벤트 위임조차 필요 없어진다. `onClick={() => onEdit(id)}` 한 줄(5-5절).

---

## 3-11. 비동기와 fetch — 서버와 대화하기

### 개념

| 방식 | 비유 | 결과 |
|---|---|---|
| **동기** | 주문하고 계산대 앞에 서서 음식을 기다림 | 뒷사람이 아무것도 못 함 (화면 멈춤) |
| **비동기** | 주문하고 **진동벨**을 받아 자리로 감. 울리면 찾으러 감 | 기다리는 동안 다른 일 가능 (화면 정상) |

`fetch()` 는 **진동벨을 주는 함수**다. 호출하는 즉시 `Promise` 라는 진동벨을 돌려주고, 실제 데이터는 나중에 도착한다. `then()` 은 "벨이 울리면 이걸 해라"라고 미리 적어두는 메모.

**then 체인**

| 단계 | 비유 | 실제로 일어나는 일 |
|---|---|---|
| 첫 번째 `then` | 택배가 문 앞에 도착했다는 알림 | 상태 코드와 헤더는 볼 수 있음. **본문은 아직** |
| `response.json()` | 상자를 뜯어 내용물을 꺼내는 중 | 본문을 객체로 변환. **이것도 비동기** |
| 두 번째 `then` | 내용물을 손에 들었다 | `students` 배열을 실제로 쓸 수 있음 |
| `catch` | 어디선가 사고 | **체인 어느 단계의 오류든 여기로 모임** |

**가장 중요한 함정 : `response.ok`**

⚠️ **`fetch` 는 서버가 404나 500을 돌려줘도 `catch` 로 가지 않는다.** "서버에 연락이 닿았고 답장을 받았다"는 이유로 **성공으로 처리**하기 때문이다. `catch` 가 잡는 것은 서버가 꺼져 있거나 인터넷이 끊긴 것처럼 **"답장 자체를 못 받은"** 경우뿐이다. 그래서 `if (!response.ok) throw ...` 두 줄을 **우리가 직접 써야 한다.**

| 상황 | 어디서 처리되나 |
|---|---|
| 정상 응답 (200) | 첫 번째 then — `response.ok` 가 true |
| 404 / 409 / 500 | **첫 번째 then** — `response.ok` 가 false → 직접 throw 해야 catch로 |
| 서버가 꺼져 있음 | catch — `Failed to fetch` |
| CORS 차단 | catch — 브라우저가 응답을 읽지 못하게 막음 |

**보낼 때 — method, headers, body**

| 옵션 | 역할 | 빠뜨리면 |
|---|---|---|
| `method` | HTTP 메서드. 생략하면 GET | **405 Method Not Allowed** |
| `headers` | 본문이 JSON임을 알림 | **415 Unsupported Media Type** |
| `body` | 실제로 보낼 데이터 | **서버에서 값이 null (400)** |

| 메서드 | 뜻 | 실습 |
|---|---|---|
| GET | 가져와라 | 3-6 목록 조회, 3-9 한 건 조회 |
| POST | 새로 만들어라 | 3-7 등록 |
| PUT | 이 번호의 것을 이 내용으로 바꿔라 | 3-10 수정 |
| DELETE | 지워라 | 3-8 삭제 |

**HTTP 상태 코드**

| 코드 | 의미 | 언제 |
|---|---|---|
| 200 / 201 | 성공 / 생성 성공 | 조회, 등록 |
| **204** | 성공했지만 **본문이 없음** | 삭제 성공 |
| 400 | 요청 형식 오류 | 필수값 누락, 날짜 형식 |
| 404 | 대상 없음 | 없는 ID로 수정·삭제 |
| 409 | **충돌** | 이미 있는 학번으로 등록 |
| 500 | 서버 내부 오류 | **서버 로그를 봐야 함** |

⚠️ **204는 "성공했지만 돌려줄 내용이 없다"는 뜻.** 이때 `response.json()` 을 부르면 읽을 본문이 없어 `Unexpected end of JSON input` 이 난다. 실습 3-8에서 삭제 성공 시 `json()` 을 부르지 않는 이유.

**async / await** — `then` 체인을 더 읽기 쉽게 쓰는 문법.

⚠️ **본문(body)은 한 번만 읽을 수 있다.** `await response.json()` 으로 이미 읽은 뒤 다시 `return response.json()` 을 부르면 `body stream already read`. **실패 분기에서는 반드시 `throw` 로 빠져나가야 하는 이유다.**

**Promise의 세 가지 상태** : `pending`(대기) → `fulfilled`(이행) 또는 `rejected`(거부). **한 번 정해지면 다시 바뀌지 않는다.**

### 코드 위치

`03_js/js/ch11_fetch.js` — **비동기를 눈으로 확인하는 실험**

```js
document.getElementById("btnOrder").addEventListener("click", function () {
    out("① 요청을 보내기 직전");

    fetch(LOCAL_URL)
        .then((response) => response.json())
        .then((students) => {
            out("③ 응답이 도착했습니다. 학생 수 :", students.length);
            out("②가 ③보다 먼저 찍혔습니다. 이것이 비동기입니다.");
            out("fetch 는 진동벨만 주고 즉시 다음 줄로 넘어갑니다.");
        });

    out("② 요청을 보낸 직후 (아직 응답은 오지 않았습니다)");
});
```

**404가 catch로 가지 않는다는 것을 직접 보여주는 부분**

```js
document.getElementById("btnFail").addEventListener("click", function () {
    fetch("data/없는파일.json")
        .then((response) => {
            out("  then 으로 들어왔습니다! (catch 가 아닙니다)");
            out("  response.ok     :", response.ok);      // false
            out("  response.status :", response.status);  // 404
            out("fetch 는 서버가 404 를 돌려줘도 '답장을 받았다'는 이유로");
            out("성공으로 처리합니다. 그래서 if (!response.ok) throw 가 필요합니다.");

            if (!response.ok) {
                throw new Error("파일을 찾을 수 없습니다. (404)");
            }
            return response.json();
        })
        .catch((error) => {
            out("직접 throw 했으므로 이제 catch 로 왔습니다 →", error.message);
        });
});
```

`student_js/js/form.js` — **then 버전과 async/await 버전이 둘 다 남아 있다**

```js
// 학생 목록 로드 함수 (then 버전 — 실습 3-6)
function loadStudents_then() {
    fetch(`${API_BASE_URL}/api/students`)
        .then((response) => {
            if (!response.ok) {
                throw new Error("학생 목록을 불러오는데 실패했습니다.");
            }
            return response.json();       //JSON.parse()
        })
        .then((students) => renderStudentTable(students))
        .catch((error) => { console.error("Error:", error); });
}

//async/await 구문을 사용한 학생 목록 로드 함수 (최종형)
async function loadStudents() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/students`);
        if (!response.ok) throw new Error("학생 목록을 불러오는데 실패했습니다.");

        const students = await response.json();
        renderStudentTable(students);
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
        studentTableBody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center; color: #dc3545;">
                        오류: 데이터를 불러올 수 없습니다.
                    </td>
                </tr>
            `;
    }
}
```

**POST — 실습 3-7**

```js
async function createStudent(studentData) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/students`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(studentData),
        });

        const data = await response.json();

        if (!response.ok) {
            const defaultMsg = response.status === 409
                ? "이미 등록된 학번(이메일,전화번호)입니다."
                : "학생 등록에 실패했습니다.";
            // response.json() 로 받은 객체가 백엔드에서는 ErrorObject
            throw new Error(data.message || defaultMsg);
        }

        showSuccess("학생이 성공적으로 등록되었습니다.");
        resetForm();
        loadStudents();
        return data;
    } catch (error) {
        console.error("Error:", error.message);
        showError(error.message);
    }
}
```

**DELETE — 실습 3-8. 204를 다루는 방식에 주목**

```js
async function deleteStudent(studentId) {
    if (!confirm('정말로 이 학생을 삭제하시겠습니까?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/api/students/${studentId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            const defaultMsg = response.status === 404
                ? "존재하지 않는 학생입니다." : "학생 삭제에 실패했습니다.";
            throw new Error(errorData.message || defaultMsg)
        }
        // 성공했을 때는 json() 을 부르지 않는다 — 204 이므로 본문이 없다

        showSuccess('학생이 성공적으로 삭제되었습니다.');
        loadStudents(); // 목록 새로고침
    } catch (error) {
        console.error('Error:', error);
        showError(error.message);
    }
}
```

**해설**
- `await response.json().catch(() => ({}))` 라는 관용구가 세 함수에 반복해서 나온다. **서버가 JSON이 아닌 HTML 오류 페이지를 줄 수도 있으므로 방어하는 것.** 실패하면 빈 객체를 대신 쓴다.
- `data.message || defaultMsg` — **서버가 준 메시지를 우선 쓰고, 없으면 상태 코드별 기본 문구.** 백엔드의 `ErrorObject`(`@RestControllerAdvice` 가 만든 것)가 바로 이 `data` 다.
- 🔗 이 세 함수에 **똑같은 오류 처리 코드가 세 번 반복된다.** 이것이 4부에서 `request()` 하나로 합쳐지는 이유다(4-5절).

**✅ 디버깅 습관** — **"서버가 이상한 것 같아요"라는 생각이 들면 먼저 F12 → Network → Payload 탭을 열어라.** 열에 아홉은 내가 보낸 JSON의 구조나 이름이 잘못되어 있다. **이 습관 하나가 프론트엔드 개발자의 기본기다.**

| 탭 | 무엇을 보나 |
|---|---|
| Headers | Request URL, Status Code |
| **Payload** | **내가 보낸 JSON** |
| Response | 서버가 돌려준 내용 |

---

## 3-12. 오류 메시지 읽는 법

### 개념

**초보자와 숙련자의 가장 큰 차이는 문법 지식이 아니라 오류 메시지를 읽느냐 안 읽느냐다. 메시지에는 이미 답이 적혀 있다.**

```
Uncaught TypeError: Cannot read properties of null (reading 'addEventListener')
                                                                 at form05.js:9
   |                 |                    |                          |
   |                 |                    |                          +-- 어디서
   |                 |                    +----------------------------- 무엇을 하려다
   |                 +-------------------------------------------------- 무엇이 null 이라서
   +-------------------------------------------------------------------- 오류의 종류
```

| 오류 종류 | 뜻 | 주로 이런 경우 |
|---|---|---|
| `SyntaxError` | 문법이 틀렸다 | 괄호·따옴표 짝이 안 맞음 |
| `ReferenceError` | 그런 이름이 없다 | 변수·함수 이름 오타, 아직 안 만듦 |
| `TypeError` | 그 타입에 그건 못 한다 | null/undefined에 점, 함수가 아닌 것에 괄호 |
| `NetworkError` | 통신 실패 | 서버 꺼짐, CORS |

**막혔을 때의 순서**
1. F12 → Console 탭. 빨간 메시지가 있는지 본다.
2. 메시지의 **파일명과 줄 번호를 클릭**해 해당 줄로 간다.
3. 그 줄에 나오는 변수를 `console.log` 로 찍어 실제 값을 확인한다.
4. 서버 통신이면 Network 탭 → **Payload** 와 Response 를 본다.
5. 그래도 모르겠으면 오류 메시지를 **그대로 검색**한다.

⚠️ **가장 흔한 실수는 오류 메시지를 읽지 않고 코드를 이리저리 바꿔보는 것이다. 그러면 원래 맞던 부분까지 망가진다.**

### 코드 위치

`03_js/js/ch12_error.js` — **오류 6종을 버튼으로 직접 발생시켜 보는 페이지**

```js
/**
 * 함수를 실행해 보고, 오류가 나면 종류와 메시지를 화면에 보여준다.
 */
function tryIt(label, fn, hint) {
    out("시도 :", label);
    try {
        fn();
    } catch (error) {
        console.error(error);          // Console 탭에서 원본과 줄 번호를 볼 수 있습니다
        out("  종류    :", error.name);
        out("  메시지  :", error.message);
        out("  해결    :", hint);
    }
}
```

| 버튼 | 재현하는 오류 | 힌트 |
|---|---|---|
| e1 | 만들지 않은 변수 사용 → `ReferenceError` | 철자·대소문자·정의 순서 |
| e2 | 못 찾은 요소에 `addEventListener` → `TypeError` | id 철자, script 위치 |
| e3 | `student.detailRequest.address` → `TypeError` | **보내는 JSON 구조 확인** |
| e4 | 객체에 `forEach` → `TypeError` | `console.log` 로 실제 값 확인. 배열이 아닐 수 있다 |
| e5 | `const` 재할당 → `TypeError` | `let` 으로 |
| e6 | 괄호 짝 불일치 → `SyntaxError` | 줄 번호 주변 괄호·따옴표 |

---

## 3-13. JavaScript 파트 자가 점검 질문

**기본기**
1. `script` 를 `</body>` 앞에 두는 이유는? 위에 두면 어떤 오류가 나나?
2. `const student = {...}` 인데 `student.name = "x"` 가 되는 이유는?
3. `0 == ""` 는 true인데 `0 === ""` 는 false다. 왜인가?
4. falsy 6개를 말하라. `[]` 와 `{}` 는 어느 쪽인가?
5. `user.age || 25` 와 `user.age ?? 25` 의 결과가 다른 경우는? → `ch04_operator.js`

**함수**
6. `loadStudents` 와 `loadStudents()` 의 차이는? 어디서 문제가 되나?
7. `(n) => { n * 2 }` 가 undefined를 돌려주는 이유는?
8. `() => { ok: true }` 가 undefined인 이유와 해결책은?
9. `function` 선언과 화살표 함수 중 정의보다 위에서 부를 수 있는 쪽은?

**객체·배열 (3부 핵심)**
10. 평평한 구조를 중첩 구조로 바꿀 때 **함께 고쳐야 하는 함수**는? 왜인가?
11. 서버로 보내는 키 이름을 `detailRequest` 에서 `detail` 로 바꾸면 무슨 일이 생기나?
12. `JSON.stringify` 와 짝이 되는 것은 무엇인가?
13. `forEach` 와 `map` 중 새 배열을 돌려주는 쪽은?
14. 목록이 중복해서 쌓인다. 렌더 함수의 어느 줄을 보나?

**DOM·이벤트**
15. `textContent` 와 `innerHTML` 중 안전한 쪽은? 왜인가?
16. `formData.get()` 이 찾는 것은 `id` 인가 `name` 인가?
17. `addEventListener("click", fn())` 을 하면 무슨 일이 생기나?
18. `e.preventDefault()` 를 빼고 제출하면 주소창이 어떻게 되나?

**비동기**
19. 404 응답은 `then` 으로 가나 `catch` 로 가나? 이유와 대처는?
20. `method` / `headers` / `body` 를 각각 빠뜨리면 어떤 상태 코드가 오나?
21. 삭제 성공(204)에 `response.json()` 을 부르면 무슨 오류가 나나?
22. `body stream already read` 가 나는 상황은?
23. `Failed to fetch` 와 `blocked by CORS policy` 는 각각 누구 문제인가?
