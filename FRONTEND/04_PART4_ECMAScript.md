# PART 4. ECMAScript (ES6+) — 모듈과 Vite

## 4-0. 저장소 개요

```
04_es6/                    ← 「ECMAScript 기초」 3~9장 (1·2·10장은 개념·도구라 예제 없음)
   ch03_variable    var 의 3가지 문제, TDZ, setTimeout 반복문 실험
   ch04_template_arrow  템플릿 리터럴, 화살표 축약 4단계, this 비교
   ch05_destructuring   객체·배열 분해, 매개변수 분해, 메서드 분해와 this
   ch06_spread      전개/나머지, 얕은 복사, 기본 매개변수, 단축·계산된 속성명
   ch07_async       Promise 3상태, 실행 순서, then 체인, async/await, Promise.all
   ch08_module      mortgage.js 를 named / default / * as 세 방식으로 import
   ch09_vite        import.meta 확인 (실제 Vite 실습은 터미널에서)
   js/mortgage.js   ★ 모듈 예제 — export 를 붙인 것만 나간다
   data/posts.json  data/users.json   fetch 연습용

   step/            ★ 실습 4-5 ~ 4-11 의 긴 코드 (student_ecma 에 놓을 파일들)
      4-5_studentApi.js     request() 까지
      4-6_studentApi.js     CRUD 완성본
      4-6_main_cleanup.js   중간 상태
      4-7_studentForm.js    collectStudentData()
      4-8_validation.js     검사 순수 함수
      4-9_message.js        메시지 모듈
      4-9_studentForm.js    폼 모듈 완성본
      4-10_studentTable.js  표 모듈 완성본
      4-11_main.js          main.js 완성본
```

**student_ecma 의 최종 구조 (실습 4-3에서 미리 그려 두는 지도)**

```
src/
  main.js               앱 조립 — 이벤트 연결과 초기 로드
  config.js             주소와 헤더
  api/studentApi.js     서버 통신만
  lib/validation.js     검사만
  ui/message.js         메시지 표시만
  ui/studentForm.js     폼 다루기만
  ui/studentTable.js    표 그리기만
```

**3부 → 4부, 무엇이 달라지는가**

| 주제 | 3부 (form.js) | 4부 (student_ecma) |
|---|---|---|
| 파일 구성 | **1개 파일 400여 줄** | **역할별 7개 모듈** |
| 서버 주소 | 코드에 하드코딩 | `.env` + `import.meta.env` |
| 비동기 | `fetch().then().then().catch()` | `async / await` + `try / catch` |
| 오류 처리 | 함수마다 같은 코드 반복 | **`request()` 하나로 통합** |
| 폼 값 수집 | 제출 핸들러 안에 섞여 있음 | `collectStudentData()` |
| 없는 값 방어 | `student.detail ? ... : "-"` | `detail?.address ?? "-"` |
| 표의 버튼 | `onclick="editStudent(3)"` | **`data-action` + 이벤트 위임** |
| 값 출력 | `innerHTML` 에 문자열 조합 | **`textContent`** (태그가 실행되지 않음) |
| 검사 결과 | 함수 안에서 `alert()` | **메시지를 돌려주고 호출한 쪽이 표시** |
| 실행 방법 | HTML 파일을 직접 열기 | `npm run dev` / `npm run build` |

**기능은 한 줄도 바뀌지 않는다.** 바뀌는 것은 코드의 모양뿐이다.

---

## 4-0-1. PART 4 완료 — student_ecma 실물 상태

저장소에 `student_ecma` Vite 프로젝트가 **실습 4-1 ~ 4-11 을 모두 마친 상태**로 들어와 있다. 4부에서 목표로 했던 "역할별 모듈 7개" 구조가 완성됐다.

```
student_ecma/
  package.json          "type": "module",  vite ^8.3.0
  .env.development      VITE_API_BASE_URL=http://localhost:8080
                        VITE_MODE=TEST                     ← 실습 4-2 에서 추가
  .env.production       VITE_API_BASE_URL=http://localhost:8080
                        VITE_MODE=PROD
  .gitignore            node_modules, dist  (⚠️ .env* 는 아직 없음)
  index.html            <h1>학생 관리 시스템 <span id="appMode"></span></h1>
  public/favicon.svg  public/icons.svg
  src/
    main.js             ✅ 조립만 한다. renderStudentTable 이 사라졌다
    style.css           ✅ .app-mode / .app-mode.test / .app-mode.prod 추가
    config.js           ✅ API_BASE_URL · STUDENTS_URL · JSON_HEADERS · APP_MODE
    api/studentApi.js   ✅ request() + CRUD 5개
    lib/validation.js   ✅ 검사 순수 함수
    ui/message.js       ✅ 메시지 · 로딩
    ui/studentForm.js   ✅ 폼 읽기 · 채우기 · 모드 전환
    ui/studentTable.js  ✅ 표 그리기 (실습 4-10 에서 새로 생김)
```

| 실습 | 상태 | 근거 |
|---|---|---|
| 4-1 ~ 4-2 | ✅ | Vite 프로젝트 + `config.js` + `.env` 2개 |
| 4-3 ~ 4-6 | ✅ | `api/studentApi.js` 에 `request()` 와 CRUD 5개 |
| 4-7 ~ 4-9 | ✅ | `ui/studentForm.js`, `ui/message.js`, `lib/validation.js` |
| **4-10** | ✅ | **`ui/studentTable.js` 신설. `onclick` 과 `window.*` 두 줄이 사라짐** |
| **4-11** | ✅ | **`main.js` 에서 래퍼 함수와 `as` 별칭이 사라지고 `startEdit`/`removeStudent` 로 정리됨** |
| 4-12 | △ | `.env.production` 은 만들었으나 아직 `localhost`. `dist` 없음 |

### 1.2 버전에서 달라진 것 — 사라진 세 가지

문서 1.2 를 쓸 때는 프로젝트가 실습 4-9 까지였다. 그때 "임시"라고 적어 둔 것들이 **실제로 전부 없어졌다.** 무엇이 왜 없어졌는지가 4-10·4-11 의 핵심이다.

| 1.2 시점(실습 4-9)에 있던 것 | 지금 | 없어진 까닭 |
|---|---|---|
| `window.editStudent = editStudent` 두 줄 | **삭제됨** | `onclick` 을 걷어내 전역에 올릴 이유가 없어졌다 |
| `import { fetchStudents as fetchStuAll, ... }` 별칭 5개 | **삭제됨** | `main.js` 의 래퍼 함수가 없어져 이름이 겹치지 않는다 |
| `main.js` 안의 `renderStudentTable` (innerHTML + onclick) | **`ui/studentTable.js` 로 이동** | 표 그리기는 화면의 일이다 |
| `document.addEventListener("DOMContentLoaded", ...)` | **삭제됨** | `type="module"` 은 HTML 을 다 읽은 뒤 실행된다 (4-8절 ③) |
| `main.js` 의 `createStudent`/`updateStudent` 래퍼 | **제출 핸들러 안으로 흡수** | 한 곳에서만 쓰이는 함수였다 |
| `editStudent` / `deleteStudent` | **`startEdit` / `removeStudent`** | api 함수와 이름이 겹치지 않게 |

**⚠️ 이 표가 4부의 결론이다.** "모듈로 나눴지만 표만 옛날 방식"이던 중간 상태가 정리되면서, `main.js` 에서 `document` 를 쓰는 곳은 **모드 배지 한 군데**만 남았다.

### 실물 `ui/studentTable.js` — 교재 step 파일과 다른 점

실제 프로젝트 코드는 교재의 step 예시와 **구조가 조금 다르다.** 셀을 만드는 일을 `addCell`, 버튼을 만드는 일을 `createActionButton` 이라는 작은 함수로 빼냈고, `DocumentFragment` 는 쓰지 않는다.

```js
// main.js 가 여기에 클릭 이벤트를 건다. 그래서 export 한다.
export const studentTableBody = document.getElementById("studentTableBody");

// 표의 열 개수. 아래 colSpan 에 쓴다.
const COLUMN_COUNT = 7;

/* 표 전체를 가로지르는 한 줄짜리 행을 만든다.
   "등록된 학생이 없습니다" 안내와 오류 안내가 함께 쓴다. */
function createMessageRow(message, className = "empty-row") {
    const row = document.createElement("tr");
    const cell = document.createElement("td");

    cell.colSpan = COLUMN_COUNT;   // 한 칸이 7칸 너비를 차지하게 한다
    cell.className = className;
    cell.textContent = message;
    row.appendChild(cell);

    return row;
}

// <td>값</td> 하나를 만들어 행에 붙인다. 아래에서 여섯 번 부른다.
function addCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = value;      // 태그가 섞여 있어도 글자로만 보인다
    row.appendChild(cell);
}

/* 수정 또는 삭제 버튼 하나를 만든다.
   버튼에 "무슨 동작인지"와 "어느 학생인지"를 새겨 두면,
   나중에 클릭을 받은 main.js 가 그것만 보고 판단할 수 있다. */
function createActionButton(action, label, className, id) {
    const button = document.createElement("button");

    // type 을 안 주면 폼 안의 버튼은 submit 으로 동작한다.
    button.type = "button";
    button.className = className;
    button.textContent = label;

    // dataset.action 에 넣으면 HTML 에는 data-action="edit" 로 나온다.
    button.dataset.action = action;
    button.dataset.id = id;

    return button;
}

function createStudentRow(student) {
    const { id, name, studentNumber, detail } = student;
    const row = document.createElement("tr");

    addCell(row, name);
    addCell(row, studentNumber);
    addCell(row, detail?.address ?? "-");
    addCell(row, detail?.phoneNumber ?? "-");
    addCell(row, detail?.email ?? "-");
    addCell(row, detail?.dateOfBirth ?? "-");

    const actionCell = document.createElement("td");
    actionCell.appendChild(createActionButton("edit", "수정", "edit-btn", id));
    actionCell.appendChild(createActionButton("delete", "삭제", "delete-btn", id));

    row.appendChild(actionCell);
    return row;
}

export function renderStudentTable(students = []) {
    // 먼저 비우지 않으면 목록을 새로고침할 때마다 같은 학생이 쌓인다.
    studentTableBody.innerHTML = "";

    if (students.length === 0) {
        studentTableBody.appendChild(createMessageRow("등록된 학생이 없습니다."));
        return;
    }

    students.forEach((student) => {
        studentTableBody.appendChild(createStudentRow(student));
    });
}

export function renderTableError(message = "오류: 데이터를 불러올 수 없습니다.") {
    studentTableBody.innerHTML = "";
    studentTableBody.appendChild(createMessageRow(message, "error-row"));
}
```

| | 교재 step 파일 (4-10절에 실린 것) | **실물 코드** |
|---|---|---|
| 셀 만들기 | `values` 배열 + `forEach` | **`addCell(row, value)` 함수 6번 호출** |
| 버튼 만들기 | 객체 배열 + `forEach` + 구조 분해 | **`createActionButton(action, label, className, id)`** |
| 붙이기 | `DocumentFragment` 로 모아서 한 번에 | **`forEach` 로 하나씩 `appendChild`** |
| 함수 시그니처 | `renderStudentTable(students = [], { emptyMessage } = {})` | **`renderStudentTable(students = [])`** |

⚠️ **둘 다 맞다.** 실물 쪽은 "한 줄짜리 헬퍼 함수로 이름을 붙이는" 방식이고, step 쪽은 "데이터를 배열로 두고 훑는" 방식이다. 실물은 `DocumentFragment` 를 생략했는데, **행이 수십 개 수준이면 체감 차이가 없기 때문**이다. 최적화는 느려진 것을 확인한 뒤에 하는 것이라는 원칙이 여기에도 적용된다. → 5-11절 `useMemo`

### 실물 `main.js` — 완성형

```js
import './style.css'
import {
    fetchStudents, fetchStudent, createStudent, updateStudent, deleteStudent,
} from './api/studentApi';                 // ← 별칭(as)이 사라졌다
import {
    studentForm, collectStudentData, cancelButton,
    fillForm, setEditMode, resetForm, scrollToForm,
} from "./ui/studentForm.js";
import { validateStudent } from "./lib/validation.js";
import { showError, showSuccess, clearMessages, setLoading } from "./ui/message.js";
import { renderStudentTable, renderTableError, studentTableBody } from "./ui/studentTable.js";
import { APP_MODE } from "./config.js";

// 현재 수정 중인 학생 ID — main.js 가 기억하는 유일한 상태
let editingStudentId = null;

/* ── 모드 표시 ──────────────────────────────────────────── */

// 제목 옆에 TEST 또는 PROD 를 적는다. 값은 .env 에서 오고 Vite 가 넣어 준다.
const appModeBadge = document.getElementById("appMode");
appModeBadge.textContent = APP_MODE;

// 모드에 따라 색을 다르게 한다. classList.add 로 클래스를 하나 더 붙인다.
if (APP_MODE === "PROD") {
    appModeBadge.classList.add("prod");
} else {
    appModeBadge.classList.add("test");
}

// 핸들러 안에서 await 을 쓰려면 함수에 async 를 붙여야 한다.
studentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessages();

    const studentData = collectStudentData();

    const errorMessage = validateStudent(studentData);
    if (errorMessage) {
        showError(errorMessage);
        return;
    }

    try {
        if (editingStudentId) {
            await updateStudent(editingStudentId, studentData);
            showSuccess("학생 정보가 성공적으로 수정되었습니다.");
        } else {
            await createStudent(studentData);
            showSuccess("학생이 성공적으로 등록되었습니다.");
        }

        editingStudentId = null;
        resetForm();
        await loadStudents();
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
    }
});

cancelButton.addEventListener("click", () => {
    editingStudentId = null;
    resetForm();
    clearMessages();
});

async function loadStudents() {
    setLoading(true);
    try {
        const students = await fetchStudents();
        renderStudentTable(students);
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
        renderTableError();
    } finally {
        setLoading(false);   // 성공 경로와 실패 경로에 두 번 적지 않아도 된다
    }
}

/* 버튼마다 이벤트를 걸지 않는 이유는, 표를 다시 그릴 때마다
   버튼이 새로 만들어져 매번 다시 걸어야 하기 때문이다. */
studentTableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    const { action, id } = button.dataset;

    if (action === "edit") {
        await startEdit(Number(id));
    } else if (action === "delete") {
        await removeStudent(Number(id));
    }
});

async function startEdit(studentId) {
    clearMessages();
    try {
        const student = await fetchStudent(studentId);
        fillForm(student);
        editingStudentId = studentId;
        setEditMode(true);
        scrollToForm();
    } catch (error) {
        showError(error.message);
    }
}

async function removeStudent(studentId) {
    if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) return;

    try {
        await deleteStudent(studentId);
        showSuccess("학생이 성공적으로 삭제되었습니다.");

        // 수정 중이던 학생을 삭제했다면 폼도 등록 모드로 되돌린다.
        // 이걸 빠뜨리면 없는 학생을 수정하려다 404 가 난다.
        if (editingStudentId === studentId) {
            editingStudentId = null;
            resetForm();
        }
        await loadStudents();
    } catch (error) {
        showError(error.message);
    }
}

// DOMContentLoaded 없이 곧바로 부른다
loadStudents();
```

### 실습 4-2 의 확장 — APP_MODE 배지

실습 4-2 에서 `.env` 로 뺀 것이 서버 주소 하나였는데, **모드 표시가 하나 더 붙었다.** 환경 변수의 쓰임을 눈으로 보여 주는 장치다.

```js
// config.js 에 추가된 줄
// 지금 테스트 모드인지 운영 모드인지.
//   npm run dev   → .env.development 를 읽어 "TEST"
//   npm run build → .env.production 을 읽어 "PROD"
// .env 파일이 없을 때를 대비해 기본값을 "TEST" 로 둔다.
// 운영 화면에 실수로 TEST 가 뜨는 편이, 테스트 화면에 PROD 가 뜨는 것보다 안전하다.
export const APP_MODE = import.meta.env?.VITE_MODE ?? "TEST";
```

```css
/* style.css 에 추가된 규칙 */
.app-mode {
    font-size: 0.5em;          /* 제목보다 훨씬 작게 — em 은 부모 글자 크기의 배수다 */
    vertical-align: middle;    /* 제목 글자의 가운데 높이에 맞춘다 */
    padding: 3px 10px;
    border-radius: 12px;
    color: white;
    letter-spacing: 1px;
}
.app-mode.test { background-color: #6c757d; }   /* 개발 — 눈에 띄지 않는 회색 */
.app-mode.prod { background-color: #dc3545; }   /* 운영 — 실수 방지를 위한 빨강 */
```

**해설 — 기본값을 `"TEST"` 로 둔 판단에 주목**
주석이 이유를 정확히 적어 두었다. **"운영 화면에 실수로 TEST 가 뜨는 편이, 테스트 화면에 PROD 가 뜨는 것보다 안전하다."** 기본값을 고를 때 "어느 쪽으로 틀리는 것이 덜 위험한가"를 기준으로 삼는 사고방식이다.

🔗 이 배지가 **5부에서 그대로 다시 나온다.** `.env` 도 `config.js` 도 `style.css` 도 한 줄 안 고치고, `App.jsx` 한 파일만 고쳐서 같은 화면을 만든다 → 5-13절 실습 5-10.

---


## 4-1. ECMAScript 란 무엇인가

### 개념

> **JavaScript = ECMAScript(언어의 핵심 문법) + 실행 환경이 주는 기능**

| 구분 | 무엇인가 | 예 |
|---|---|---|
| **ECMAScript** | 언어의 표준 문법 규격 (ECMA-262) | `let`/`const`, 화살표 함수, Promise, 모듈, class |
| **브라우저** | ECMAScript에 화면·통신 기능을 얹은 실행 환경 | `document`, `fetch`, `alert`, `localStorage` |
| **Node.js** | 브라우저 밖에서 JS를 실행하는 환경 | `fs`(파일), `http`(서버) |

**`document` 나 `fetch` 는 ECMAScript 표준에 없다.** 브라우저가 제공하는 기능이다. 그래서 같은 코드라도 Node.js에서 `document` 를 부르면 `document is not defined` 가 난다.

| 버전 | 연도 | 대표 문법 |
|---|---|---|
| ES5 | 2009 | `var`, `function`, JSON, `forEach`/`map`/`filter` |
| **ES6 (ES2015)** | 2015 | **`let`/`const`, 화살표 함수, 템플릿 리터럴, 구조 분해, 전개 구문, 모듈, Promise, class** |
| ES2017 | 2017 | `async`/`await`, `Object.entries` |
| ES2020 | 2020 | `?.`, `??`, `Promise.allSettled` |

**버전 번호를 외울 필요는 없다. 실무에서 중요한 구분은 딱 하나, "ES5인가 ES6+인가".**

### 코드 위치

이 장은 예제 코드가 없다(개념). 다만 `04_es6/index.html` 목차에 **1·2·10장은 실행 예제가 없다**고 명시돼 있다.

⚠️ `Unexpected token '=>'` → 브라우저가 화살표 함수를 모르는 구형 환경이거나, 파일이 JS가 아닌 형식으로 해석되고 있다.
⚠️ `Cannot use import statement outside a module` → `import` 를 썼는데 `<script type="module">` 이 아니다. → 4-8절.

---

## 4-2. 빌드 도구 — 왜 필요한가

### 개념

3부까지는 `.html` 에 `<script src="js/form.js">` 를 붙여 그대로 실행했다. 프로젝트가 커지면 세 가지 문제를 만난다.

- **호환성** — 최신 문법이 오래된 브라우저에서 안 돌아간다.
- **파일 수** — js 파일이 수십 개가 되면 `<script>` 태그를 **순서까지 맞춰** 일일이 넣어야 한다.
- **실수** — 오타나 안 쓰는 변수를 실행하기 전에는 알 수 없다.

| 해결하려는 문제 | 도구 | 대표 제품 |
|---|---|---|
| 옛 브라우저 호환 | 변환기(트랜스파일러) | **Babel**, SWC |
| 파일이 너무 많음 | 번들러 | Webpack, **Rollup**, esbuild |
| 실행 전 실수 찾기 | 정적 분석기 | **ESLint** |
| 개발 서버와 빌드 통합 | 빌드 도구 | **Vite**, ~~CRA~~(종료) |
| 라이브러리 설치·실행 | 패키지 관리자 | **npm**, yarn, pnpm |

```js
// 내가 쓴 코드 (ES6)
const add = (a, b) => a + b;

// Babel 이 변환한 코드 (ES5) — 뜻은 같지만 옛 브라우저도 이해한다
var add = function (a, b) { return a + b; };
```

⚠️ **변환은 문법만 바꿔 준다.** `Promise` 나 `fetch` 처럼 "옛 브라우저에 아예 없는 기능"은 문법 변환만으로 생기지 않아서, 그 기능을 대신 채워 넣는 코드(**폴리필, polyfill**)를 함께 넣어야 한다.

**Node.js · npm · package.json**

| 이름 | 역할 |
|---|---|
| **Node.js** | 브라우저 밖에서 JS를 실행하는 환경. 모든 빌드 도구가 이 위에서 동작 |
| **npm** | 라이브러리를 내려받고(`install`), 스크립트를 실행하는(`run`) 도구 |
| **package.json** | 프로젝트 이름·버전·의존성 목록·실행 스크립트 |
| **package-lock.json** | **실제로 설치된 정확한 버전**을 기록. 누가 설치해도 같은 결과 |
| **node_modules** | 내려받은 라이브러리 실물. **용량이 크고 복원 가능하므로 git에 올리지 않는다** |

```json
{
  "scripts": {
    "dev": "vite",             // npm run dev
    "build": "vite build",     // npm run build
    "preview": "vite preview"
  },
  "dependencies":    { "axios": "^1.7.2" },   // 실행에 필요
  "devDependencies": { "vite": "^7.1.1" }     // 개발할 때만 필요
}
```

⚠️ 다른 사람의 프로젝트를 받아 왔는데 실행이 안 된다면 **먼저 `npm install` 을 했는지 확인.**

⚠️ CRA(Create React App)는 오랫동안 React 프로젝트의 기본 시작점이었지만 **지금은 유지보수가 멈춰 새 프로젝트에는 권장되지 않는다.** 오늘날의 표준은 **Vite**.

---

## 4-3. 변수 선언 — var, let, const 와 TDZ

### 개념

**var 의 세 가지 문제**

```js
// (1) 블록을 무시한다 — 유효 범위가 함수 전체
if (true) { var x = 10; }
console.log(x);        // 10 — 블록 밖에서도 살아 있다

for (var i = 0; i < 3; i++) { }
console.log(i);        // 3 — 반복문이 끝나도 남아 있다

// (2) 같은 이름으로 다시 선언해도 조용히 통과한다
var name = "홍길동";
var name = "김코딩";    // 오류 없음 — 앞의 값이 소리 없이 사라진다

// (3) 선언 전에 써도 오류가 나지 않는다
console.log(age);      // undefined (오류가 아니다!)
var age = 20;
```

⚠️ **(2)번이 특히 위험하다.** 파일이 길어지면 위쪽에서 이미 쓴 이름을 아래쪽에서 다시 `var` 로 선언해도 **아무 경고가 없다.** 값이 조용히 덮어써지고, 문제는 한참 뒤 엉뚱한 곳에서 드러난다.

| 구분 | var | let | const |
|---|---|---|---|
| 유효 범위 | **함수 전체** | 블록 `{ }` 안 | 블록 `{ }` 안 |
| 재선언 | 가능 — **조용히 덮어씀** | 불가 (SyntaxError) | 불가 |
| 재할당 | 가능 | 가능 | **불가 (TypeError)** |
| 선언 전 사용 | **undefined 가 나옴** | ReferenceError | ReferenceError |
| 언제 쓰나 | **쓰지 않는다** | 값이 바뀔 때만 | **기본값** |

**호이스팅과 TDZ**

호이스팅은 "선언이 위로 끌어올려진 것처럼 동작하는 성질"이다.

```js
console.log(x);   // undefined (오류가 아니다!)   — var : 선언만 끌어올려짐
var x = 10;

console.log(y);   // ReferenceError: Cannot access 'y' before initialization
let y = 10;       // let/const : 선언 전 구간이 TDZ 로 막혀 있다

loadStudents();               // 정상 동작 — function 선언식은 통째로 끌어올려진다
function loadStudents() { }

loadStudents2();              // ReferenceError — 화살표 함수는 const 규칙을 따른다
const loadStudents2 = () => { };
```

**TDZ(Temporal Dead Zone, 일시적 사각지대)** — `let`/`const` 도 끌어올려지기는 하지만 선언 전 구간의 접근을 막는다. **오류가 나는 편이 오히려 안전하다.** `undefined` 가 조용히 흘러가면 한참 뒤 엉뚱한 곳에서 문제가 드러나기 때문이다.

⚠️ **기존 `function` 을 화살표 함수로 바꿀 때 자주 만나는 문제**가 이것이다. 정의보다 위에서 부르던 코드가 갑자기 깨진다.

### 코드 위치

`04_es6/js/ch03_variable.js` — **`eval` 을 쓴 이유까지 주석으로 밝혀 둔 예제**

```js
// (3) function 선언은 통째로 끌어올려집니다. 정의보다 위에서 불러도 동작합니다.
out("[호이스팅] 정의보다 위에서 함수 호출 →", hoistedGreet());
function hoistedGreet() { return "function 선언은 파일 전체에서 미리 인식됩니다"; }

// (1) var 는 "선언"만 끌어올려지고 값은 올라가지 않습니다.
out("[호이스팅] var 선언 전에 읽기 →", varAge, "(오류가 아닙니다)");
var varAge = 20;

// (2) let / const 는 선언 전 구간이 TDZ 로 막혀 있습니다.
try { out(letAge); }
catch (error) { out("[TDZ] let 선언 전에 읽기 →", error.name + ": " + error.message); }
let letAge = 20;

// (4) 화살표 함수는 const 에 담기므로 변수 규칙을 따릅니다.
try { arrowGreet(); }
catch (error) {
    out("[TDZ] 화살표 함수를 정의보다 위에서 호출 →", error.name);
    out("     function 을 화살표 함수로 바꿀 때 자주 만나는 문제입니다.");
}
const arrowGreet = () => "화살표 함수";
```

**var 와 let 의 차이가 가장 극적으로 드러나는 예 — 반복문 + setTimeout**

```js
for (var i = 1; i <= 3; i++) {
    setTimeout(function () {
        out("var  i =", i, "  ← 셋 다 4. i 가 하나뿐이라 반복이 끝난 값을 본다");
    }, 100);
}

for (let j = 1; j <= 3; j++) {
    setTimeout(function () {
        out("let  j =", j, "  ← 1, 2, 3. 반복마다 새 j 가 만들어진다");
    }, 300);
}
```

**해설**
- 이 두 반복문이 `var` 와 `let` 의 차이를 가장 선명하게 보여준다. **`var` 는 변수가 하나뿐이라 콜백이 실행될 때는 이미 반복이 끝나 있고, `let` 은 반복마다 새 변수가 만들어진다.**
- ⚠️ 예제에서 `eval` 을 쓴 것은 **파일 전체가 문법 오류로 멈추지 않게 하기 위함**이다. 실제 코드에서는 이렇게 쓰지 말 것.
- `Object.freeze(student)` 로 내용까지 얼릴 수 있지만 실무에서 쓸 일은 드물다.

---

## 4-4. 템플릿 리터럴과 화살표 함수 — 그리고 this

### 개념

3권 3-3에서 다룬 백틱 문자열이 React로 가면 **클래스 이름을 조합할 때** 등 더 자주 쓰인다.

```js
const msg = `${student.name} 님의 점수는 ${student.score}점,
등급은 ${student.score >= 80 ? 'A' : 'B'} 입니다.`;
```

`${ }` 안에는 변수뿐 아니라 **계산식이나 함수 호출**도 넣을 수 있고, 줄바꿈도 그대로 유지된다.

**화살표 함수의 this — function 과 가장 큰 차이**

화살표 함수는 단순히 짧게 쓰는 문법이 아니다. **`this` 를 다루는 방식이 근본적으로 다르다.**

- `function` 으로 만든 함수 : **"누가 불렀는가"에 따라 `this` 가 그때그때 정해진다.**
- 화살표 함수 : **자기 자신의 `this` 를 아예 갖지 않고 바깥 코드의 `this` 를 그대로 쓴다.**

```js
const timer = {
    name: '타이머',
    startWrong: function () {
        setTimeout(function () {
            console.log(this.name);   // undefined — this 가 바뀌어 버림
        }, 1000);
    },
    startRight: function () {
        setTimeout(() => {
            console.log(this.name);   // '타이머' — 바깥의 this 를 그대로 사용
        }, 1000);
    }
};
```

**콜백 함수 안에서 `this` 가 사라지는 문제는 ES5 시절 가장 흔한 함정이었다. 화살표 함수는 이 문제를 문법 차원에서 없앴다.** 그래서 오늘날 콜백에는 거의 언제나 화살표 함수를 쓴다.

⚠️ **반대로 객체의 메서드를 정의할 때 화살표 함수를 쓰면 `this` 가 그 객체를 가리키지 않는다.**

### 코드 위치

`04_es6/js/ch04_template_arrow.js`

```js
// 객체를 바로 돌려주려면 괄호로 감쌉니다.
const makeOk    = () => ({ ok: true });
const makeWrong = () => { ok: true };     // 중괄호를 함수 몸통으로 해석 → undefined

out("makeOk()     =", makeOk());
out("makeWrong()  =", makeWrong(), "  ← 괄호로 감싸지 않으면 undefined");

/* --- this — function 과 화살표 함수의 근본적인 차이 --- */
const timer = {
    name: "타이머",
    startWrong: function () {
        setTimeout(function () {
            // 이 함수는 자기만의 this 를 갖는다 → timer 를 가리키지 않음
            out("function 콜백  : this 는", this === window ? "window (전역 객체)" : this);
        }, 100);
    },
    startRight: function () {
        setTimeout(() => {
            // 화살표 함수는 this 를 갖지 않고 바깥의 this 를 그대로 쓴다
            out("화살표 콜백    : this 는", this === timer ? "timer 객체" : this);
        }, 300);
    }
};
```

**해설**
- 🔗 React가 클래스 컴포넌트에서 함수 컴포넌트로 넘어간 이유 중 하나가 바로 이 `this` 다. 클래스에서는 생성자에서 `this.handleClick = this.handleClick.bind(this)` 를 해야 했다(5-8절). **Hooks에는 `this` 가 아예 없다.**

---

## 4-5. 구조 분해 할당 (Destructuring)

### 개념

객체의 속성을 변수로 한 번에 꺼내는 문법. **중괄호 안에 적는 이름은 반드시 객체의 속성 이름과 같아야 한다.**

```js
const { name, age, phone } = user;          // 한 줄로
const { phone: { home, mobile } } = user;   // 중첩된 것을 한 번에
const { name: userName } = user;            // 이름 바꿔 받기
const { address = '미입력' } = user;         // 값이 없으면(undefined) 기본값

const [first, second] = colors;             // 배열은 순서로
const [, , third] = colors;                 // 건너뛰기
[a, b] = [b, a];                            // 두 값 맞바꾸기 (임시 변수 불필요)
```

⚠️ `phone: { home, mobile }` 에서 **`phone` 은 변수가 되지 않는다.** "phone 안으로 들어가라"는 **길 안내**일 뿐. `phone` 자체도 필요하다면 따로 적어야 한다.

**매개변수 자리에서 바로 분해 — React에서 자주 보는 형태**

```js
function printStudent({ name, studentNumber }) {
    console.log(`${name} / ${studentNumber}`);
}
```

🔗 **React 컴포넌트의 `function StudentRow({ student, onDelete })` 같은 코드가 바로 이것이다.** props라는 객체를 통째로 받은 뒤 필요한 것만 매개변수 자리에서 꺼내는 것. **이 절을 익혀 두면 React 예제의 절반은 이미 읽을 수 있다.**

**메서드를 분해하면 this 가 사라진다**

```js
const obj = { names: 'React', greet() { return this.names; } };

obj.greet();                       // 'React' — 객체를 통해 부르면 정상
const { names, greet } = obj;
greet();                           // undefined — this 가 obj 가 아니게 됨
```

해결 : ① `obj.greet.bind(obj)` 로 `this` 를 고정 ② 애초에 `this` 를 쓰지 않게 만든다.

### 코드 위치

`04_es6/js/ch05_destructuring.js`

```js
// 한 번에 안쪽까지 — phone 은 변수가 되지 않고 "길 안내" 역할만 합니다.
const { phone: { home, mobile } } = user;

// 두 값을 맞바꾸기 — 임시 변수가 필요 없습니다.
let a = 1, b = 2;
[a, b] = [b, a];

document.getElementById("btnThis").addEventListener("click", function () {
    out("obj.greet()  →", obj.greet(), "  ← 객체를 통해 부르면 정상");

    // 구조 분해로 함수만 떼어내면 자신이 어느 객체의 것이었는지 잊어버립니다.
    const { names, greet } = obj;
    out("greet()      →", greet(), "  ← undefined!");

    out("해결 방법 1 — bind() 로 객체를 붙여 준다");
    const boundGreet = obj.greet.bind(obj);

    out("해결 방법 2 — 처음부터 this 를 쓰지 않는다");
    const obj2 = { names: "React", greet2: () => obj2.names };
});
```

`student_ecma/src/lib/validation.js` — **실전 사용**

```js
export function validateStudent(student) {
    // 구조 분해로 필요한 값만 꺼낸다. detailRequest 가 없을 때를 대비해 기본값 {} 를 둔다.
    const { name, studentNumber, detailRequest = {} } = student;
    const { phoneNumber, email } = detailRequest;

    // 한 줄로도 쓸 수 있다(실제 코드에 주석으로 남아 있는 대안)
    // const { name, studentNumber, detailRequest: { phoneNumber, email } = {} } = student;
    ...
}
```

`04_es6/step/4-10_studentTable.js`

```js
function createStudentRow(student) {
    const { id, name, studentNumber, detail } = student;
    ...
}
```

**해설**
- `detailRequest = {}` 기본값이 핵심이다. **이것이 없으면 `detailRequest` 가 없는 객체가 들어왔을 때 `Cannot destructure property ... of undefined` 가 난다.**

---

## 4-6. 전개 구문과 나머지 구문, 기본 매개변수

### 개념

**전개(spread) — 하나를 여러 개로 펼친다**

```js
const all   = [...a, ...b];      // 합치기
const copy  = [...a];            // 복사본 (a 와는 다른 배열)
Math.max(...scores);             // Math.max(88, 92, 75) 와 같다
[..."abc"];                      // ["a", "b", "c"]

const updated = { ...student, age: 21 };          // age 만 바꾼 새 객체
const merged  = { ...student, email: 'a@b.com' }; // 속성 추가하며 합치기
```

**뒤에 오는 값이 앞의 값을 덮어쓴다.**

```js
{ ...student, age: 21 }    // age 는 21 — 뒤에 쓴 값이 이긴다
{ age: 21, ...student }    // age 는 20 — student 의 값이 덮어쓴다
```

⚠️ **`const copy = a;` 는 같은 배열에 이름만 하나 더 붙이는 것**이라 `copy.push()` 가 `a` 까지 바꾼다. `[...a]` 는 새 배열을 만든다. "복사했는데 원본도 바뀌어요"의 대부분이 이 차이.

⚠️ **전개 구문은 한 겹만 복사한다(얕은 복사).** `{ ...student }` 를 해도 안쪽 객체는 원본과 같은 것을 가리킨다. 중첩된 값을 바꾸려면 안쪽도 함께 펼쳐야 한다.

```js
const deep = {
    ...origin,
    detailRequest: { ...origin.detailRequest, address: "부산" }
};
```

**나머지(rest) — 여러 개를 하나로 모은다**

| 쓰는 자리 | 이름 | 하는 일 |
|---|---|---|
| 값을 **쓰는** 자리 | 전개 (spread) | 하나를 여러 개로 펼친다 |
| 값을 **받는** 자리 | 나머지 (rest) | 여러 개를 하나로 모은다 |

```js
function sum(...numbers) { return numbers.reduce((acc, n) => acc + n, 0); }
const { name, ...rest } = { name: '홍길동', age: 20, email: 'a@b.com' };
const [head, ...tail] = [1, 2, 3];
```

⚠️ **나머지 매개변수는 반드시 마지막에.** `function f(...args, last)` 는 SyntaxError.

**기본 매개변수**

```js
function greet(name = '손님') { return `${name}님 환영합니다`; }

greet();            // 손님님 — 인자를 안 넘김
greet(undefined);   // 손님님 — undefined 는 "안 넘긴 것"으로 본다
greet(null);        // null님 — null 은 넘긴 값이므로 그대로 쓰인다
greet("");          // 님    — 빈 문자열도 넘긴 값이다
```

**기본값은 오직 `undefined` 일 때만 적용된다.** `??` 와 같은 규칙이며 `||` 와는 다르다.

```js
// 객체 매개변수 + 구조 분해 + 기본값 — React 예제에 자주 나오는 형태
function createStudent({ name, age = 20, email = null } = {}) { ... }
```

⚠️ **마지막 `= {}` 가 중요하다.** 이것이 없으면 인자 없이 불렀을 때 `undefined` 를 분해하려다 `Cannot destructure property ... of undefined` 가 난다.

**단축 속성명과 계산된 속성명**

```js
const student = { name, studentNumber };     // 변수 이름과 속성 이름이 같으면 한 번만
const obj = { [key]: 'a@b.com' };            // 속성 이름을 변수로 — 대괄호
```

**Object.entries 와 Object.fromEntries — 객체와 배열 사이**

객체에는 `map` 이나 `filter` 를 바로 쓸 수 없다(배열의 메서드이므로). 그래서 **"객체를 배열로 펼쳤다가 다시 객체로 되돌리는"** 한 쌍의 함수가 표준에 들어왔다.

```js
Object.keys(obj)                // 키만 모은 배열
Object.values(obj)              // 값만 모은 배열
Object.entries(obj)             // [키, 값] 쌍들의 배열 (ES2017)
Object.fromEntries(쌍들)         // 그 반대 (ES2019)
```

**실전에서 가장 자주 쓰이는 곳은 폼과 쿼리 문자열이다.**

```js
// 폼 전체를 객체 하나로
const data = Object.fromEntries(new FormData(studentForm).entries());

// 주소창의 ?page=2&size=10 을 객체로
Object.fromEntries(new URLSearchParams(location.search).entries());

// 실전에서 가장 많이 보게 되는 조합 — fromEntries + 구조 분해
const { name, studentNumber, address } =
    Object.fromEntries(new FormData(studentForm).entries());
```

⚠️ 같은 `name` 을 가진 입력이 여러 개(체크박스 등)면 **뒤의 값이 앞의 값을 덮어쓴다.** 전부 받아야 한다면 `formData.getAll("hobby")` 를 따로 쓴다.
⚠️ **폼과 쿼리 문자열의 값은 언제나 문자열이다.** 숫자가 필요하면 `Number(age)`.

### 코드 위치

`04_es6/js/ch06_spread.js` — **얕은 복사를 직접 겪게 만드는 실험**

```js
document.getElementById("btnShallow").addEventListener("click", function () {
    const origin = { name: "홍길동", detailRequest: { address: "서울시" } };

    const shallow = { ...origin };
    shallow.name = "김코딩";                       // 한 겹 바깥 — 원본에 영향 없음
    shallow.detailRequest.address = "부산시";      // 안쪽 — 원본까지 바뀐다!

    out("name 은 바뀌지 않았지만 detailRequest.address 는 원본까지 바뀌었습니다.");
    out("전개 구문은 한 겹만 복사하기 때문입니다(얕은 복사).");

    // 안쪽까지 새로 만들려면 안쪽도 함께 펼쳐야 합니다.
    const deep = {
        ...origin2,
        detailRequest: { ...origin2.detailRequest, address: "부산시" }
    };
});
```

**교재가 제시하는 축약형** — `fromEntries` + 구조 분해

```js
export function collectStudentData() {
    // FormData 를 한 번에 객체로 바꾼 뒤, 필요한 값만 구조 분해로 꺼낸다.
    const { name, studentNumber, address, phoneNumber, email, dateOfBirth } =
        Object.fromEntries(new FormData(studentForm).entries());

    return {
        name: name.trim(),
        studentNumber: studentNumber.trim(),
        detailRequest: {
            address: address.trim(),
            phoneNumber: phoneNumber.trim(),
            // 빈 문자열("")도 걸러야 하므로 여기서는 ?? 가 아니라 || 를 쓴다.
            email: email.trim() || null,
            dateOfBirth: dateOfBirth || null,
        },
    };
}
```

⚠️ **실제 `student_ecma` 프로젝트는 이 축약형을 쓰지 않았다.** 3부와 같은 `formData.get()` 방식을 그대로 유지한다.

`student_ecma/src/ui/studentForm.js` — **실물 코드**

```js
export function collectStudentData() {
    // FormData 는 폼 안의 입력칸을 name 속성으로 꺼내 쓸 수 있게 모아 준다.
    // id 가 아니라 name 이 열쇠다.
    const formData = new FormData(studentForm);

    // 서버는 학생 기본 정보와 상세 정보를 나눠서 받는다.
    return {
        name: formData.get("name").trim(),
        studentNumber: formData.get("studentNumber").trim(),
        detailRequest: {
            address: formData.get("address").trim() || null,
            phoneNumber: formData.get("phoneNumber").trim(),
            // 아무것도 입력하지 않으면 빈 문자열("")이 오는데,
            // ?? 는 빈 문자열을 통과시켜 서버로 "" 이 나가 버린다.
            email: formData.get("email").trim(),
            dateOfBirth: formData.get("dateOfBirth") || null,
        },
    };
}
```

**해설 — 두 코드를 비교해서 읽을 것**
- `fromEntries` 축약형을 쓰면 `formData.get("name").trim()` 여섯 줄이 **두 줄**로 줄어든다. 문법 연습으로는 이쪽이 목표.
- 하지만 **실물은 축약하지 않았다.** 입력칸이 6개뿐이라 가독성 이득이 크지 않고, `formData.get("주소")` 형태가 **어느 `name` 속성에서 오는 값인지 눈에 더 잘 띄기 때문**이다. 축약이 언제나 정답은 아니라는 실전 판단.
- ⚠️ **`email` 에는 `|| null` 이 없다.** 실습 4-8에서 **이메일이 선택 항목에서 필수 항목으로 바뀌었기 때문**이다. 빈 문자열 `""` 그대로 보내면 `validateStudent` 의 `if (!email)` 이 먼저 걸러낸다. 반면 `address` 와 `dateOfBirth` 는 여전히 `|| null`.
- **`??` 가 아니라 `||` 를 쓴 것**이 핵심이다. 빈 문자열도 걸러내야 하기 때문. 3-4절의 "무엇을 없는 값으로 볼 것인가"가 여기서 실제 판단이 된다.
- 🔗 React의 상태 갱신이 거의 항상 전개 구문 형태다. `setForm((prev) => ({ ...prev, [name]: value }))` — **전개 + 계산된 속성명**이 한 줄에 다 들어 있다(5-9절).

---

## 4-7. Promise 와 async / await

### 개념

| 상태 | 영문 | 뜻 |
|---|---|---|
| 대기 | pending | 아직 결과가 나오지 않은 상태 |
| 이행 | fulfilled | 성공적으로 완료되어 값이 담긴 상태 |
| 거부 | rejected | 실패가 확정된 상태 |

**한 번 이행이나 거부로 정해지면 그 상태는 다시 바뀌지 않는다.**

⚠️ **`new Promise` 를 직접 쓸 일은 실무에서 흔하지 않다.** `fetch` 나 `axios` 가 이미 Promise를 만들어 돌려준다. 중요한 것은 **"Promise를 받으면 `then`/`catch` 또는 `await` 로 결과를 꺼내 쓴다"**는 사용법 하나.

| 키워드 | 뜻 | 규칙 |
|---|---|---|
| `async` | 이 함수는 **Promise를 돌려준다** | 함수 앞에 붙인다 |
| `await` | 이 Promise가 끝날 때까지 기다린다 | **`async` 함수 안에서만** |
| `try / catch` | 기다리다 실패하면 catch로 | `then` 체인의 `.catch()` 자리 |

⚠️ **`async` 함수는 언제나 Promise를 돌려준다.** 그래서 `getData()` 의 결과를 바로 쓰려면 `const data = await getData();` 처럼 다시 `await` 하거나 `.then(...)` 을 써야 한다. **"async 함수를 불렀는데 Promise가 찍힌다"는 대부분 이 경우다.**

**Promise.all — 여러 요청을 한꺼번에**

```js
// 순서대로 — 0.3초 + 0.3초 = 0.6초
const a = await fetch(url1);
const b = await fetch(url2);

// 동시에 — 0.3초
const [ra, rb] = await Promise.all([fetch(url1), fetch(url2)]);
```

**서로 관계가 없는 요청이라면 `Promise.all` 로 동시에 보낸다.**

### 코드 위치

`04_es6/js/ch07_async.js` — **실행 시간을 직접 재서 비교하는 예제**

```js
/* 4. async / await — 같은 일을 위에서 아래로 */
async function getPosts() {
    try {
        const response = await fetch(POSTS_URL);
        if (!response.ok) {
            throw new Error("불러오기 실패 : " + response.status);
        }
        const posts = await response.json();
        return posts;
    } catch (error) {
        out("에러 발생 :", error.message);
        return [];
    }
}

document.getElementById("btnAwait").addEventListener("click", async function () {
    const posts = await getPosts();
    // async 함수는 언제나 Promise 를 돌려줍니다.
    out("await 없이 getPosts() 를 찍으면 →", getPosts());
    out("\"async 함수를 불렀는데 Promise 가 찍힌다\"는 대부분 이 경우입니다.");
});

/* 5. Promise.all — 여러 요청을 한꺼번에 */
document.getElementById("btnAll").addEventListener("click", async function () {
    // (1) 순서대로 — 앞 요청이 끝나야 뒤 요청이 시작됩니다.
    const t1 = performance.now();
    const r1 = await fetch(POSTS_URL);  const posts1 = await r1.json();
    const r2 = await fetch(USERS_URL);  const users1 = await r2.json();
    const sequential = Math.round(performance.now() - t1);

    // (2) 동시에 — 두 요청을 함께 보내고 전부 끝날 때까지 한 번만 기다립니다.
    const t2 = performance.now();
    const [r3, r4] = await Promise.all([fetch(POSTS_URL), fetch(USERS_URL)]);
    const [posts2, users2] = await Promise.all([r3.json(), r4.json()]);
    const parallel = Math.round(performance.now() - t2);

    out("로컬 파일이라 차이가 작지만, 실제 서버라면 요청 수만큼 차이가 벌어집니다.");
});
```

**해설**
- `performance.now()` 로 밀리초를 재서 **순차와 병렬의 차이를 숫자로 보여준다.** 로컬 파일이라 차이가 작다는 점까지 정직하게 적어 둔 것이 좋은 예제다.
- 🔗 이 `async/await` + `try/catch` 형태가 4부 `studentApi.js` 와 `main.js` 전체의 골격이 된다.

---

## 4-8. 모듈 — export 와 import

### 개념

3부까지는 `<script>` 태그를 여러 개 넣고 **모든 함수가 전역에 노출되는** 방식이었다. 파일이 늘어나면 이름이 겹치고, 어떤 파일이 어떤 파일을 필요로 하는지 알 수 없다.

| 구분 | 내보내기 | 가져오기 | 개수 |
|---|---|---|---|
| **named export** | `export const a = ...` | `import { a } from ...` | 여러 개 |
| **default export** | `export default a` | `import 아무이름 from ...` | **파일당 하나** |

```js
import { calculateAmortization as calcAmortization } from './mortgage';  // 이름 바꾸기
import * as mortgage from './mortgage';                                   // 통째로
```

⚠️ **named export 는 이름이 계약이다.** 중괄호 안의 이름이 한 글자라도 다르면 오류.

**브라우저에서 모듈 쓰기**

```html
<script type="module" src="js/main.js"></script>
```

`type` 을 적지 않은 `script` 를 브라우저는 **고전 스크립트(classic script)** 로 읽고, `type="module"` 을 적으면 **모듈(module)** 로 읽는다. **이 한 글자 차이로 달라지는 것이 생각보다 많다.**

| 무엇이 | type 없는 script | `type="module"` |
|---|---|---|
| `import` · `export` | 문법 오류가 난다 | 쓸 수 있다 |
| 최상위 변수·함수 | **`window` 에 올라간다** | **그 파일 안에만 있다** |
| 실행 시점 | 태그를 만나는 즉시 | **HTML 을 다 읽은 뒤** |
| strict mode | 적어야 켜진다 | **언제나 켜져 있다** |
| 같은 파일을 여러 곳에서 | 요청한 만큼 실행 | **한 번만 실행** |
| 최상위 `this` | `window` | `undefined` |
| `file://` 로 열기 | 동작한다 | **CORS 오류가 난다** |

**① `import` 와 `export` 를 쓸 수 있다**
`type="module"` 을 붙이는 가장 큰 이유. 고전 스크립트에서는 `import` 라는 **문법 자체가 허용되지 않아, 파일을 읽는 단계에서 바로 오류**가 난다.

**② 최상위에 선언한 이름이 전역(`window`)에 올라가지 않는다 — 4부에서 가장 중요한 항목**

고전 스크립트에서 최상위에 적은 `var` 와 `function` 은 곧바로 `window` 의 속성이 된다. 그래서 다른 `script` 태그에서도 그냥 부를 수 있었다. 모듈은 그렇지 않다.

```html
<script>
  function hello() {}          <!-- 고전 스크립트 -->
</script>
<script>
  hello();                     <!-- 동작한다 (window.hello 가 있다) -->
</script>

<script type="module">
  function hello() {}          <!-- 모듈 -->
</script>
<script type="module">
  hello();                     <!-- ReferenceError: hello is not defined -->
</script>
```

**HTML 에 적는 `onclick="hello()"` 도 그 이름을 `window` 에서 찾는다.** 그래서 모듈 안의 함수는 `onclick` 으로 부를 수 없다. 🔗 코드를 모듈로 옮기면 `onclick` 을 `addEventListener` 로 바꾸게 되는 이유가 여기 있다 → 4-10절 이벤트 위임.

**③ `defer` 를 붙인 것처럼 동작한다 — 실행 시점이 다르다**

고전 스크립트는 브라우저가 `script` 태그를 만나는 **순간 HTML 읽기를 멈추고** 파일을 받아 실행한다. 그래서 `head` 에 두면 아직 만들어지지 않은 요소를 `getElementById` 로 찾지 못해 `null` 이 나온다. **모듈은 파일을 미리 받아 두었다가 HTML 을 다 읽은 뒤에 실행한다.**

```html
<head>
  <!-- 고전: 여기서 getElementById 를 하면 null 이 나온다 -->
  <script src="js/main.js"></script>

  <!-- 모듈: HTML 을 다 읽은 뒤 실행되므로 null 이 아니다 -->
  <script type="module" src="js/main.js"></script>
</head>
```

🔗 **`script` 를 `body` 맨 아래로 내리거나 `DOMContentLoaded` 를 기다리던 습관이 모듈에서는 필요 없다.** 3-1절과 3-10절에서 강조하던 규칙이 여기서 사라진다. 4부 `ui/message.js` 와 `ui/studentForm.js` 가 **파일 맨 위에서 곧바로 `getElementById` 를 해도 괜찮은 이유**가 이것이다.

**④ 언제나 strict mode 다**
`"use strict"` 를 적지 않아도 켜져 있다. 선언하지 않은 이름에 값을 넣으면 조용히 전역 변수가 만들어지는 대신 오류가 난다. **실수를 일찍 잡아 주는 쪽이다.**

```html
<script type="module">
  count = 10;      // ReferenceError: count is not defined
</script>
```

**⑤ 같은 파일은 한 번만 내려받아 한 번만 실행된다**

여러 파일이 같은 모듈을 `import` 해도 브라우저는 그 파일을 **한 번만 받아 한 번만 실행하고, 결과를 저장해 두고 나눠 쓴다.** 그래서 모듈 최상위에서 만든 객체는 **앱 전체가 같은 것을 보게 된다.**

```js
// config.js
console.log("config 실행");
export const api = { url: "http://localhost:8080" };

// a.js
import { api } from "./config.js";
// b.js
import { api } from "./config.js";

// "config 실행" 은 한 번만 찍힌다.
// a.js 가 본 api 와 b.js 가 본 api 는 같은 객체다.
```

🔗 4부 `ui/message.js` 의 `let messageTimer = null` 이 **모듈 전체에서 하나뿐인 변수**로 동작하는 근거가 이 성질이다.

**⑥ 최상위 `this` 가 `undefined` 다**
고전 스크립트의 최상위 `this` 는 `window` 였다. 모듈에서는 `undefined`. 전역 객체가 꼭 필요하면 `globalThis` 를 쓴다.

**⑦ `http://` 로 열어야 한다**
모듈을 불러오는 요청에는 **CORS 규칙이 적용된다.** HTML 파일을 탐색기에서 더블클릭해 `file://` 로 열면 모듈을 가져오지 못하고 CORS 오류가 난다. 개발 서버(4-9절)로 열어야 한다.

**경로를 적는 규칙도 다르다.** 브라우저에서 직접 모듈을 쓸 때는 `./` 또는 `/` 로 시작하고 **확장자 `.js` 까지 적어야 한다.**

```js
import { api } from "./config.js";   // 브라우저에서 직접 쓸 때
import { api } from "./config";      // 오류 — 확장자가 없다
import { api } from "config";        // 오류 — ./ 가 없다
```

Vite 같은 빌드 도구를 쓰면 이 규칙을 **도구가 대신 풀어 준다.** 실제로 `student_ecma/src/main.js` 는 `from './api/studentApi'` 처럼 확장자를 생략하고 있다.

> **■ 알아두기** `type="module"` 을 모르는 아주 오래된 브라우저를 위해 `nomodule` 이라는 짝이 있다. 모듈을 아는 브라우저는 `nomodule` 이 붙은 `script` 를 건너뛰고, 모르는 브라우저는 `type="module"` 을 알 수 없는 타입으로 보고 건너뛴다. 요즘 브라우저만 대상으로 한다면 쓸 일이 없다.
>
> ```html
> <script type="module" src="js/main.js"></script>
> <script nomodule src="js/legacy.js"></script>
> ```

⚠️ `Cannot use import statement outside a module` → `type="module"` 이 빠졌다.
⚠️ `Failed to resolve module specifier` → 경로에 `./` 를 안 붙였다. **브라우저에서 직접 쓸 때는 확장자(`.js`)까지 적어야 한다.** Vite 같은 번들러를 쓰면 생략 가능.
⚠️ CORS 오류 → 파일을 `file://` 로 열었다. **모듈은 `http://` 로 열어야 한다.**
⚠️ `onclick="hello()"` 가 `hello is not defined` → **모듈 안의 함수는 `window` 에 없다.** `addEventListener` 로 연결하거나, 임시로 `window.hello = hello` 처럼 직접 올려야 한다.

**■ 확인하기**
- `type="module"` 을 붙였을 때와 붙이지 않았을 때 무엇이 달라지는지 **세 가지 이상** 말할 수 있다.
- 모듈 안의 함수를 `onclick` 으로 부를 수 없는 까닭을 설명할 수 있다.
- `head` 에 둔 `script` 가 모듈일 때 `getElementById` 가 `null` 이 아닌 까닭을 설명할 수 있다.

### 코드 위치

`04_es6/js/mortgage.js` — **내보내는 쪽**

```js
/* named export : export 를 붙인 것만 밖으로 나갑니다. */
export const calculateMonthlyPayment = (principal, rate, years) => {
    const monthlyRate = rate / 12;
    const months = years * 12;
    if (monthlyRate === 0) return Math.round(principal / months);

    const payment = (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -months));
    return Math.round(payment);
};

/** 이 파일에서만 쓰는 함수 — export 를 붙이지 않으면 밖으로 나가지 않습니다. */
const formatWon = (value) => value.toLocaleString("ko-KR") + "원";

const calculateAmortization = (principal, rate, years) => { ... };

/* default export : 파일마다 하나만 지정할 수 있는 "대표 내보내기". */
export default calculateAmortization;
```

`04_es6/js/ch08_module.js` — **가져오는 쪽. 세 방식을 한 화면에서 비교**

```js
// default export 는 중괄호 없이, 이름은 마음대로
import calcAmortization from "./mortgage.js";

// named export 는 중괄호 안에 같은 이름으로
import { calculateMonthlyPayment } from "./mortgage.js";

// 전부 한 덩어리로 받기
import * as mortgage from "./mortgage.js";

out("mortgage 안에 들어 있는 이름 :", Object.keys(mortgage));
out("→ default 는 mortgage.default 로 꺼냅니다.");
out("→ formatWon 은 export 를 붙이지 않았으므로 목록에 없습니다.");

out("=== 모듈은 전역을 더럽히지 않습니다 ===");
out("window.calculateMonthlyPayment =", window.calculateMonthlyPayment);
out("→ undefined. 모듈 안의 이름은 밖으로 새어 나가지 않습니다.");
out("   3권까지 쓰던 <script> 방식은 모든 함수가 전역에 노출됐습니다.");
```

**해설**
- 마지막 실험이 이 장의 핵심이자 **4부 전체에서 가장 중요한 사실**이다. **모듈 안의 함수는 `window` 에 등록되지 않는다.**
- 🔗 그래서 3부의 `onclick="editStudent(3)"` 이 4부에서 `editStudent is not defined` 로 깨진다. 실습 4-1에서 임시로 `window.editStudent = editStudent` 두 줄을 넣어 두었다가 **실습 4-10에서 반드시 지운다.** 그 두 줄을 남겨 두는 것은 **모듈로 나눈 의미를 스스로 없애는 방식**이기 때문이다.
- `formatWon` 이 `Object.keys(mortgage)` 목록에 없다는 것으로 **"export 를 안 붙이면 밖으로 안 나간다"**를 눈으로 확인시킨다.

---

## 4-9. Vite 로 개발 환경 만들기

### 개념

Vite(비트)는 오늘날 가장 널리 쓰이는 프론트엔드 빌드 도구다. 개발 중에는 **고친 부분만 즉시 반영하는 HMR(Hot Module Replacement)**을 제공하고, 배포용 빌드는 내부적으로 Rollup을 쓴다.

```
npm create vite@latest
  Project name: student_ecma
  Select a framework: Vanilla        ← React 없이 순수 JS
  Select a variant: JavaScript
cd student_ecma
npm install          ← 이걸 해야 node_modules 가 생긴다
npm run dev
```

```
student_ecma/
  index.html         // 진입점. Vite 는 여기서 시작한다
  package.json
  public/            // 그대로 복사되는 정적 파일
  src/
    main.js          // 실제 코드의 시작점
    style.css
  vite.config.js
```

| 명령 | 언제 | 결과 |
|---|---|---|
| `npm install` | 프로젝트를 처음 받았을 때 | `node_modules` 생성 |
| `npm run dev` | 개발 중 | 개발 서버 (기본 `http://localhost:5173`) |
| `npm run build` | 배포 직전 | **`dist` 폴더**에 최적화된 결과물 |
| `npm run preview` | 빌드 결과 확인 | `dist` 를 실제 서버처럼 띄워 본다 |

⚠️ **Vite의 `index.html` 은 `public` 이 아니라 프로젝트 최상단에 있다.** `index.html` 자체를 진입점으로 삼기 때문이며, 이것이 CRA와 다른 점.

**환경 변수 — .env 파일**

```bash
# .env.development — npm run dev 일 때
VITE_API_BASE_URL=http://localhost:8080

# .env.production — npm run build 일 때
VITE_API_BASE_URL=https://api.myservice.com
```

⚠️ **환경 변수 이름은 반드시 `VITE_` 로 시작해야 한다.** 그렇지 않은 변수는 브라우저 코드에 전달되지 않는다.
⚠️ **`.env` 파일의 값은 빌드 결과물에 그대로 들어간다.** 비밀번호나 API 키처럼 숨겨야 하는 값은 **절대 넣으면 안 된다.** 그런 값은 서버에서 다뤄야 한다.
⚠️ `.gitignore` 에 `.env*` 를 추가한다. (`.env.example` 만 공유하는 것이 관례)

### 코드 위치

`04_es6/js/ch09_vite.js` — **왜 이 페이지에서는 실행할 수 없는지를 설명하는 예제**

```js
out("=== import.meta 는 브라우저 표준입니다 ===");
out("import.meta.url =", import.meta.url);

out("=== import.meta.env 는 Vite 가 채워 넣는 값입니다 ===");
out("import.meta.env =", import.meta.env);
out("→ undefined. 브라우저가 아니라 Vite 가 빌드할 때 넣어 주는 값이기 때문입니다.");

out("Vite 프로젝트 안에서라면 아래처럼 나옵니다.");
out("  import.meta.env.VITE_API_URL  →  http://localhost:8080/api");
out("  import.meta.env.MODE          →  development");
out("  import.meta.env.DEV           →  true");
```

**실습 4-2 — `src/config.js`**

```js
// import.meta.env 는 Vite 가 .env 파일의 값을 넣어 주는 자리다.
// ?. 왼쪽이 없으면(undefined) 멈추고 undefined 를 돌려준다.
//    Vite 없이 열었을 때 "Cannot read properties of undefined" 를 막는다.
// ?? 왼쪽이 null 이나 undefined 일 때만 오른쪽 값을 쓴다.
//    || 와 달리 빈 문자열("")과 0 은 그대로 살린다.
export const API_BASE_URL = import.meta.env?.VITE_API_BASE_URL ?? "http://localhost:8080";

// 3부의 `${API_BASE_URL}/api/students` 를 매번 쓰던 것을 한 번만 만들어 둔다.
export const STUDENTS_URL = `${API_BASE_URL}/api/students`;

// POST 와 PUT 에서 똑같이 쓰던 헤더. 3부에서는 함수마다 적어 두었다.
export const JSON_HEADERS = {
    "Content-Type": "application/json",
};
```

**해설**
- 이 12줄짜리 파일이 3부의 흩어진 상수 세 종류를 전부 흡수한다. **`?.` 와 `??` 를 함께 쓴 이유를 주석으로 정확히 밝혀 둔 것**이 이 파일의 교육적 가치다.
- 🔗 5부 React 프로젝트에서는 **이 `config.js` 를 한 줄도 고치지 않고 그대로 복사한다.** `document` 를 건드리지 않는 순수 모듈이기 때문.

⚠️ `import.meta.env.VITE_API_BASE_URL` 이 undefined → ① `VITE_` 접두사가 없거나 ② **`.env` 파일을 만든 뒤 개발 서버를 다시 시작하지 않았다.**

---

## 4-10. 실습 4-1 ~ 4-12 — form.js 를 7개 모듈로

### 실습 목록

| 실습 | 내용 | 주로 쓰는 문법 |
|---|---|---|
| 4-1 | Vite 프로젝트 만들기와 기존 코드 옮기기 | npm, 모듈 스크립트 |
| 4-2 | 서버 주소를 `.env` 로 분리 | `import.meta.env`, `??` |
| 4-3 | 서버 통신을 모듈로 분리 | `export` / `import` |
| 4-4 | then 체인을 `async / await` 으로 | `async`, `await`, `try/catch` |
| 4-5 | 반복되는 fetch 를 `request()` 하나로 | 기본 매개변수, `??` |
| 4-6 | 나머지 CRUD 를 화살표 함수로 | 화살표 함수, 템플릿 리터럴 |
| 4-7 | 폼 값 수집을 `studentForm.js` 로 | 모듈 분리 |
| 4-8 | 유효성 검사를 순수 함수로 | 구조 분해 기본값, early return |
| 4-9 | 메시지와 폼을 화면 모듈로 | 기본 매개변수, `?.` 와 `??` |
| 4-10 | 표 다시 그리기 — `onclick` 걷어내기 | **이벤트 위임, dataset, textContent** |
| 4-11 | `main.js` 로 조립 | import 정리, 이벤트 연결 |
| 4-12 | 빌드와 배포 | `npm run build`, `.env.production` |

### 코드 — `api/studentApi.js` (실습 4-5, 4-6 완성본)

3부에서 함수마다 반복되던 오류 처리를 **`request()` 하나로 통합**하는 것이 이 단계의 전부다.

```js
/** 상태 코드별 기본 메시지. 서버가 message 를 주지 않을 때 쓴다. */
const DEFAULT_MESSAGES = {
    400: "입력한 값이 올바르지 않습니다.",
    404: "존재하지 않는 학생입니다.",
    409: "이미 등록된 학번입니다.",
    500: "서버에서 오류가 발생했습니다.",
};

async function request(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        // 서버가 JSON 이 아닌 오류 페이지를 줄 수도 있으므로 방어한다.
        const errorData = await response.json().catch(() => ({}));

        const message =
            errorData.message ??
            DEFAULT_MESSAGES[response.status] ??
            `요청에 실패했습니다. (${response.status})`;

        throw new Error(message);
    }

    // 204 No Content — 삭제 성공처럼 돌려줄 본문이 없는 경우
    if (response.status === 204) {
        return null;
    }

    return response.json();
}

/** 학생 목록을 가져온다. */
export const fetchStudents = () => request(STUDENTS_URL);

/** 학생 한 명을 가져온다. */
export const fetchStudent = (id) => request(`${STUDENTS_URL}/${id}`);

/** 학생을 새로 등록한다. */
export const createStudent = (student) =>
    request(STUDENTS_URL, {
        method: "POST",
        headers: JSON_HEADERS,
        body: JSON.stringify(student),
    });

/** 학생 정보를 수정한다. */
export const updateStudent = (id, student) =>
    request(`${STUDENTS_URL}/${id}`, {
        method: "PUT",
        headers: JSON_HEADERS,
        body: JSON.stringify(student),
    });

/** 학생을 삭제한다. */
export const deleteStudent = (id) =>
    request(`${STUDENTS_URL}/${id}`, { method: "DELETE" });
```

**해설**
- **3부에서 `loadStudents` / `createStudent` / `updateStudent` / `deleteStudent` 네 함수에 흩어져 있던 오류 처리가 `request()` 하나로 모였다.** 아래 CRUD 다섯 줄에는 오류 처리 코드가 한 줄도 없다.
- `errorData.message ?? DEFAULT_MESSAGES[status] ?? 기본문구` — **3단 폴백**. 서버가 준 메시지 → 상태 코드별 문구 → 최후의 문구. `||` 가 아니라 `??` 를 쓴 것에 주목.
- `if (response.status === 204) return null;` — 3-11절의 `Unexpected end of JSON input` 을 **구조적으로** 막았다. 3부에서는 삭제 함수 안에서 `json()` 을 안 부르는 방식이었지만, 여기서는 **모든 요청이 같은 규칙을 따른다.**
- `options = {}` 기본 매개변수 덕분에 GET 요청은 두 번째 인자를 생략할 수 있다.
- 🔗 **이 파일도 5부에서 한 줄도 안 고치고 그대로 가져간다.**

### 코드 — `lib/validation.js` (실습 4-8) — **실물 코드**

```js
// 학번: 영문 2자 + 숫자 3자 (예: EE002)
//   ^     문자열의 시작        [A-Z]{2}  영문 대문자 2개
//   \d{3} 숫자 3개             $         문자열의 끝
//   /i    대소문자를 가리지 않는다 (ee002 도 통과)
const STUDENT_NUMBER_PATTERN = /^[A-Z]{2}\d{3}$/i;

// 전화번호: 숫자, 하이픈, 공백만 한 개 이상(+)
const PHONE_PATTERN = /^[0-9-\s]+$/;

// 이메일: @ 앞뒤와 점 뒤에 "공백도 @도 아닌 글자"가 있어야 한다
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// test() 는 정규식과 맞으면 true, 아니면 false 를 돌려준다.
export const isValidEmail = (email) => EMAIL_PATTERN.test(email);

// 앞뒤에 공백이 섞여 들어올 수 있으므로 trim() 으로 지운 뒤 검사한다.
export const isValidStudentNumber = (studentNumber) =>
    STUDENT_NUMBER_PATTERN.test(studentNumber.trim());

/* 문제가 없으면 null, 있으면 첫 번째 오류 메시지를 돌려준다.
   3부에서는 여기서 alert 을 띄우고 true / false 를 돌려주었다. */
export function validateStudent(student) {
    // detailRequest = {} 는 기본값이다. detailRequest 가 없는 객체가
    // 들어와도 빈 객체로 대신해 "Cannot destructure property" 를 막는다.
    const { name, studentNumber, detailRequest = {} } = student;
    const { phoneNumber, email } = detailRequest;

    // 문제를 찾으면 그 자리에서 바로 돌려주고 끝낸다(early return).
    if (!name) return "이름을 입력해주세요.";

    // || 는 왼쪽이 거짓이면 오른쪽을 검사한다.
    // !studentNumber 가 먼저 걸러 주므로 안쪽 trim() 이 안전하다.
    if (!studentNumber || !isValidStudentNumber(studentNumber)) {
        return "학번을 입력하지 않거나 올바른 형식이 아닙니다.";
    }

    if (!phoneNumber || !PHONE_PATTERN.test(phoneNumber)) {
        return "전화번호를 입력하지 않거나 올바른 전화번호 형식이 아닙니다.";
    }

    // 이메일은 선택이 아니라 필수다.
    // collectStudentData 가 빈 칸을 그대로 "" 로 넘기므로 !email 에 걸린다.
    if (!email || !isValidEmail(email)) {
        return "이메일을 입력하지 않거나 올바른 이메일 형식이 아닙니다.";
    }

    // 여기까지 왔으면 문제가 없다는 뜻이다.
    return null;
}
```

**⚠️ 검사 규칙이 3부에서 바뀌었다 — 이 표를 반드시 확인할 것**

| 항목 | 3부 `form.js` | 4부 `validation.js` (실물) |
|---|---|---|
| 학번 | 영문·숫자면 통과 (`/^[A-Za-z0-9]+$/`) | **영문 2자 + 숫자 3자 (`EE002`)** — `/^[A-Z]{2}\d{3}$/i` |
| 이메일 | **선택** — 입력된 경우에만 검사 | **필수** — 비어 있으면 통과 못 함 |
| 주소 | 필수 검사 있음 | **검사하지 않음** (HTML `required` 가 이미 막는 자리로 판단) |
| 검사 실패 | `alert()` + `false` 반환 | **메시지 문자열 반환** |

- ⚠️ **학번 형식이 좁아졌다.** `20241234` 같은 숫자 학번은 이제 통과하지 못한다. `CS001`, `EE002` 형태여야 한다. `index.html` 의 `placeholder="CS001"` 이 그 힌트.
- `/i` 플래그 덕분에 `ee002` 처럼 소문자로 써도 통과한다.
- **검사 항목 하나에 두 조건을 `||` 로 묶은 것**에 주목. `!studentNumber || !isValidStudentNumber(studentNumber)` — 왼쪽이 먼저 빈 값을 걸러 주므로 오른쪽의 `.trim()` 이 `null` 에서 터지지 않는다. **단락 평가(short-circuit)를 방어 코드로 쓰는 전형적인 패턴.**
- `isValidStudentNumber` 는 `export` 되어 있지만 현재 `validateStudent` 안에서만 쓰인다.

**⚠️ 3부와의 결정적인 차이 — `alert` 가 사라졌다**

```js
// 3부 form.js
if (!student.name) {
    alert("이름을 입력해주세요.");    // 검사 함수가 화면까지 건드린다
    return false;
}

// 4부 validation.js
if (!name) return "이름을 입력해주세요.";   // 메시지만 돌려주고 끝
```

**검사 함수는 "무엇이 잘못됐는지"만 말하고, 그것을 어떻게 보여줄지는 호출한 쪽이 정한다.** 이것이 **순수 함수**로 만든다는 뜻이다.
- 화면이 없는 환경(테스트 코드, Node.js)에서도 그대로 쓸 수 있다.
- 나중에 `alert` 를 토스트로 바꾸고 싶으면 이 파일은 안 건드려도 된다.
- 🔗 5부에서 이 파일을 **그대로** 가져다 `setMessage({ text: errorMessage, type: "error" })` 로 쓴다.

### 코드 — `ui/message.js` (실습 4-9)

```js
const COLORS = { error: "#dc3545", success: "#28a745" };

/** 성공 메시지가 저절로 사라지기까지의 시간(ms) */
const MESSAGE_TIMEOUT = 3000;

/** 자동 초기화 예약. 새 메시지가 오면 이전 예약을 취소한다. */
let messageTimer = null;

/**
 * @param {"error"|"success"} [type] 기본값 "error"
 * @param {number} [timeout] 자동으로 지우기까지의 ms. 0 이면 지우지 않는다(기본값)
 */
export function showMessage(text, type = "error", timeout = 0) {
    clearTimeout(messageTimer);          // 앞선 예약을 취소한다

    formError.textContent = text;
    formError.style.color = COLORS[type] ?? COLORS.error;
    formError.style.display = "block";

    // 오류는 사용자가 고칠 때까지 남기고, 성공만 시간이 지나면 지운다.
    if (timeout > 0) {
        messageTimer = setTimeout(clearMessages, timeout);
    }
}

export const showError   = (text) => showMessage(text, "error");
export const showSuccess = (text) => showMessage(text, "success", MESSAGE_TIMEOUT);

/** @param {boolean} [isLoading] 기본값 true */
export function setLoading(isLoading = true) {
    loadingMessage.style.display = isLoading ? "block" : "none";
}
```

**해설**
- 3부의 `showError` / `showSuccess` 두 함수가 **`showMessage` 하나 + 기본 매개변수**로 합쳐졌다.
- **설계 판단 하나가 들어 있다** : 3부는 오류도 3초 뒤 사라졌지만, 4부는 **오류는 사용자가 고칠 때까지 남기고 성공만 자동으로 지운다.** `timeout = 0` 기본값이 그 표현.
- `clearTimeout(messageTimer)` 를 맨 앞에 두어 **새 메시지가 오면 이전 예약을 취소**한다. 이것이 없으면 "3초 전 예약된 지우기"가 새 메시지까지 지운다.
- 3부에서 **선언만 하고 쓰지 않던 `loadingMessage` 가 여기서 실제로 동작하게 된다.**
- 🔗 5부에서는 이 타이머 관리가 `useEffect` 의 **정리 함수(cleanup)** 로 대체된다(5-10절). React가 이전 예약을 알아서 취소한다.

### 코드 — `ui/studentForm.js` (실습 4-9 완성본)

```js
export const studentForm = document.getElementById("studentForm");
export const cancelButton = document.getElementById("cancelButton");

const submitButton  = studentForm.querySelector('button[type="submit"]');
const formContainer = studentForm.closest(".form-container");

/** 서버에서 받은 학생 정보로 폼을 채운다. */
export function fillForm(student) {
    const { name, studentNumber, detail } = student;

    studentForm.name.value = name;
    studentForm.studentNumber.value = studentNumber;

    // detail 이 없으면 ?. 가 undefined 를 돌려주고, ?? 가 빈 문자열로 바꿔 준다.
    studentForm.address.value     = detail?.address ?? "";
    studentForm.phoneNumber.value = detail?.phoneNumber ?? "";
    studentForm.email.value       = detail?.email ?? "";
    studentForm.dateOfBirth.value = detail?.dateOfBirth ?? "";
}

/** 등록 모드와 수정 모드를 전환한다. 인자를 생략하면 등록 모드다. */
export function setEditMode(isEditing = false) {
    // 조건 ? 참일 때 값 : 거짓일 때 값  (삼항 연산자)
    submitButton.textContent = isEditing ? "학생 수정" : "학생 등록";
    cancelButton.style.display = isEditing ? "inline-block" : "none";

    // classList.toggle 의 두 번째 인자는 "뒤집기"가 아니라 "강제"다.
    //   true 면 무조건 넣고, false 면 무조건 뺀다.
    // 두 번째 인자가 없으면 부를 때마다 넣었다 뺐다 하므로,
    // setEditMode(true) 가 두 번 불리면 강조 표시가 사라져 버린다.
    // 이 클래스는 style.css 에서 폼 왼쪽에 녹색 띠를 그린다.
    formContainer.classList.toggle("editing", isEditing);
}

/** 폼을 비우고 등록 모드로 되돌린다. */
export function resetForm() {
    studentForm.reset();         // 입력칸을 모두 비운다
    setEditMode(false);
}

/** 수정 버튼을 눌렀을 때 폼이 보이도록 부드럽게 스크롤한다. */
export function scrollToForm() {
    studentForm.scrollIntoView({ behavior: "smooth" });
}
```

**해설**
- **`setEditMode` 함수 하나가 세 군데를 한꺼번에 바꾼다.** 3부에서는 버튼 글자, 취소 버튼 표시, 컨테이너 클래스를 각각 다른 자리에서 고쳤다.
- ⚠️ **`classList.toggle(클래스, 조건)` 의 두 번째 인자를 반드시 기억할 것.** 이 인자가 있으면 "강제 설정"(true면 추가, false면 제거)이고, **없으면 "뒤집기"가 된다.**

| 코드 | 동작 | `setEditMode(true)` 를 두 번 부르면 |
|---|---|---|
| `toggle("editing")` | 있으면 빼고 없으면 넣는다 | **첫 번째는 들어가고, 두 번째에 사라진다** ← 버그 |
| `toggle("editing", isEditing)` | `isEditing` 이 true 면 무조건 넣는다 | 두 번 다 들어가 있다 ← 정상 |

수정 중인 학생을 눌렀다가 다른 학생을 또 누르면 `setEditMode(true)` 가 연달아 불린다. 두 번째 인자가 없으면 그때 녹색 띠가 사라진다. **"이럴 때만 생기는 이상한 버그"의 전형.**

- `submitButton` 과 `formContainer` 는 `export` 하지 않는다. **이 파일 안에서만 쓰기 때문.** 내보낼 것과 감출 것을 가르는 기준이 여기 그대로 드러난다.
- `formContainer` 를 `closest(".form-container")` 로 찾는 점도 눈여겨볼 것. **자기 자신부터 부모 쪽으로 거슬러 올라가는 탐색**이다(4-10절의 이벤트 위임에서 같은 함수를 쓴다).
- ⚠️ **그래도 여전히 "명령형"이다.** 세 줄을 우리가 직접 써야 하고, 한 줄을 빠뜨리면 화면이 어긋난다. 🔗 5부에서 이 문제가 완전히 사라진다. `isEditing` 값 하나만 정하면 세 군데가 저절로 따라온다(5-2절).

### 코드 — `ui/studentTable.js` (실습 4-10 완성본)

**이번 실습이 4부에서 가장 큰 변화다 — `onclick` 을 걷어낸다.**

```js
const studentTableBody = document.getElementById("studentTableBody");
const COLUMN_COUNT = 7;

/**
 * 한 줄짜리 안내 행을 만든다. 빈 목록 안내와 오류 안내가 함께 쓴다.
 */
function createMessageRow(message, className = "empty-row") {
    const row = document.createElement("tr");
    const cell = document.createElement("td");

    cell.colSpan = COLUMN_COUNT;   // 한 칸이 표 전체 너비를 차지하게 한다
    cell.className = className;
    cell.textContent = message;
    row.appendChild(cell);

    return row;
}

function createStudentRow(student) {
    const { id, name, studentNumber, detail } = student;
    const row = document.createElement("tr");

    const values = [
        name,
        studentNumber,
        detail?.address ?? "-",
        detail?.phoneNumber ?? "-",
        detail?.email ?? "-",
        detail?.dateOfBirth ?? "-",
    ];

    values.forEach((value) => {
        const cell = document.createElement("td");
        cell.textContent = value;      // 태그가 실행되지 않는다
        row.appendChild(cell);
    });

    // 액션 칸 — 버튼에 어떤 동작인지, 어떤 학생인지를 data 속성으로 새겨 둔다.
    const actionCell = document.createElement("td");

    [
        { action: "edit",   label: "수정", className: "edit-btn" },
        { action: "delete", label: "삭제", className: "delete-btn" },
    ].forEach(({ action, label, className }) => {
        const button = document.createElement("button");

        button.type = "button";
        button.className = className;
        button.textContent = label;
        button.dataset.action = action;   // data-action="edit"
        button.dataset.id = id;           // data-id="3"

        actionCell.appendChild(button);
    });

    row.appendChild(actionCell);
    return row;
}

export function renderStudentTable(students = [], { emptyMessage = "등록된 학생이 없습니다." } = {}) {
    // 먼저 비우지 않으면 목록을 새로고침할 때마다 같은 학생이 쌓인다.
    studentTableBody.innerHTML = "";

    if (students.length === 0) {
        studentTableBody.appendChild(createMessageRow(emptyMessage));
        return;
    }

    // 여러 행을 한 번에 붙이기 위한 임시 그릇 (화면을 한 번만 다시 그린다)
    const fragment = document.createDocumentFragment();
    students.forEach((student) => fragment.appendChild(createStudentRow(student)));

    studentTableBody.appendChild(fragment);
}

export function renderTableError(message = "오류: 데이터를 불러올 수 없습니다.") {
    studentTableBody.innerHTML = "";
    studentTableBody.appendChild(createMessageRow(message, "error-row"));
}
```

**세 가지가 바뀌었다**

| 항목 | 3부 | 4부 | 왜 |
|---|---|---|---|
| 값 출력 | `innerHTML` 에 문자열 조합 | **`textContent`** | 사용자 입력의 태그가 실행되지 않음 |
| 버튼 연결 | `onclick="editStudent(3)"` | **`data-action` + `data-id`** | 모듈 함수는 전역에 없음 |
| 붙이기 | 행마다 `appendChild` | **`DocumentFragment`** | 화면을 한 번만 다시 그린다 |

### 이벤트 위임 — `main.js` 쪽

```js
studentTableBody.addEventListener("click", async (event) => {
    // 클릭된 지점에서 가장 가까운 data-action 버튼을 찾는다.
    const button = event.target.closest("button[data-action]");
    if (!button) return;             // 버튼이 아닌 곳을 눌렀다

    const { action, id } = button.dataset;

    if (action === "edit") {
        await startEdit(Number(id));
    } else if (action === "delete") {
        await removeStudent(Number(id));
    }
});
```

| 방식 | 이벤트 등록 횟수 | 새로 만든 행 | 함수의 위치 |
|---|---|---|---|
| `onclick` 속성 | 행마다 | 자동으로 동작 | **전역에 있어야 함** |
| 버튼마다 `addEventListener` | 행마다 | **다시 등록해야 함** | 어디든 가능 |
| **이벤트 위임 (tbody 1회)** | **1번** | **자동으로 동작** | **어디든 가능** |

**`event.target` 과 `closest()` — 무엇이 눌렸는지 가려내기**

`tbody` 한 곳에만 이벤트를 걸었으므로 표 안의 클릭이 전부 이 함수로 들어온다. 이름 칸을 눌러도, 빈 곳을 눌러도.

- `event.target` 은 이벤트를 걸어 둔 요소(`tbody`)가 아니라 **마우스가 실제로 닿은 가장 안쪽 요소**다.
- `closest(선택자)` 는 자기 자신부터 검사하고 부모로 **거슬러 올라가며** 맞는 첫 요소를 돌려준다. 끝까지 없으면 `null`. **`querySelector` 가 자식 쪽으로 내려가는 것과 방향이 반대다.**
- `"button[data-action]"` 은 **속성 선택자**. 표 안에 다른 버튼이 생겨도 걸리지 않는다.

| 클릭한 곳 | `event.target` | `closest("button[data-action]")` |
|---|---|---|
| 수정 버튼 | `<button data-action="edit">` | 자기 자신이 조건에 맞음 → 그 버튼 |
| 이름 칸 | `<td>홍길동</td>` | td → tr → tbody 로 올라가도 없음 → **null** |
| 표의 빈 곳 | `<tr>` 또는 `<tbody>` | **null** |

⚠️ **`if (!button) return;` 이 없으면** 버튼이 아닌 곳을 눌렀을 때 `button.dataset` 에서 오류가 난다.

⚠️ **`event.target === button` 처럼 직접 비교하지 않는 이유** — 지금은 버튼 안에 글자만 있어 `event.target` 이 곧 버튼이지만, **나중에 버튼 안에 아이콘을 넣으면 `event.target` 은 그 아이콘이 된다.** `closest` 는 한 칸 위로 올라가 버튼을 찾아 주므로 그때도 코드를 안 고쳐도 된다.

⚠️ **`dataset.id` 는 언제나 문자열이다.** `data-id="3"` 이면 `"3"` 이 온다. 그래서 `Number(id)` 로 바꿔 넘긴다. **이것을 잊으면 `editingStudentId === studentId` 비교가 숫자와 문자열 비교가 되어 어긋난다.**

### 코드 — `main.js` (실습 4-11 완성본)

```js
import "./style.css";

import { fetchStudents, fetchStudent,
         createStudent, updateStudent, deleteStudent } from "./api/studentApi.js";
import { validateStudent } from "./lib/validation.js";
import { studentForm, cancelButton, collectStudentData,
         fillForm, setEditMode, resetForm, scrollToForm } from "./ui/studentForm.js";
import { renderStudentTable, renderTableError, studentTableBody } from "./ui/studentTable.js";
import { showError, showSuccess, clearMessages, setLoading } from "./ui/message.js";

/** 수정 중인 학생 ID. 값이 있으면 수정 모드, null 이면 등록 모드. */
let editingStudentId = null;

async function loadStudents() {
    setLoading(true);

    try {
        const students = await fetchStudents();
        renderStudentTable(students);
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
        renderTableError();
    } finally {
        // 성공하든 실패하든 로딩 표시는 반드시 끈다.
        setLoading(false);
    }
}

studentForm.addEventListener("submit", async (event) => {
    event.preventDefault();          // 페이지 새로고침 막기
    clearMessages();

    const studentData = collectStudentData();

    // 검사에 걸리면 메시지만 보여 주고 끝낸다(early return).
    const errorMessage = validateStudent(studentData);
    if (errorMessage) {
        showError(errorMessage);
        return;
    }

    try {
        if (editingStudentId) {
            await updateStudent(editingStudentId, studentData);
            showSuccess("학생 정보가 성공적으로 수정되었습니다.");
        } else {
            await createStudent(studentData);
            showSuccess("학생이 성공적으로 등록되었습니다.");
        }

        editingStudentId = null;
        resetForm();
        await loadStudents();         // 목록 새로고침
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);     // 서버가 보낸 실제 메시지
    }
});

async function removeStudent(studentId) {
    if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) return;

    try {
        await deleteStudent(studentId);
        showSuccess("학생이 성공적으로 삭제되었습니다.");

        // 수정 중이던 학생을 삭제했다면 폼도 등록 모드로 되돌린다.
        if (editingStudentId === studentId) {
            editingStudentId = null;
            resetForm();
        }
        await loadStudents();
    } catch (error) {
        showError(error.message);
    }
}

// type="module" 스크립트는 HTML 을 다 읽은 뒤 실행되므로
// DOMContentLoaded 를 기다릴 필요가 없습니다.
loadStudents();
```

**해설 — 이 파일에서 읽어야 할 다섯 가지**
1. **`main.js` 가 기억하는 상태는 `editingStudentId` 하나뿐이다.** 나머지는 전부 모듈에 있다.
2. `finally` 블록 — **성공하든 실패하든 로딩 표시는 반드시 끈다.** 3부에서 빠뜨리기 쉬웠던 부분.
3. `validateStudent` 가 **메시지를 돌려주므로** `main.js` 가 `showError` 로 표시한다. 역할 분리의 결과.
4. 삭제할 때 **수정 중이던 학생을 삭제했다면 폼도 되돌린다.** 3부에는 없던 방어.
5. ⚠️ **마지막 줄** — `type="module"` 스크립트는 HTML을 다 읽은 뒤 실행되므로 `DOMContentLoaded` 가 필요 없다. 3부의 `document.addEventListener("DOMContentLoaded", ...)` 가 사라진 이유.

### 실습 4-12 — 빌드와 배포

```bash
npm run build      # dist 폴더 생성
npm run preview    # 개발 서버 없이도 동작하는지 확인
```

`dist` 안의 js 파일에 `.env.production` 의 주소가 들어 있으면 성공. **개발 서버(`npm run dev`)는 배포에 쓰는 것이 아니다.**

⚠️ 화면이 하얗고 404가 잔뜩 → `dist` 를 하위 경로에 올렸다면 `vite.config.js` 에 `base` 설정이 필요하다.

---

## 4-11. form.js ↔ student_ecma 대응표 (부록 A)

3부의 코드가 어디로 갔는지 찾을 때 사용한다.

| form.js 의 코드 | 옮겨간 곳 | 달라진 점 |
|---|---|---|
| `const API_BASE_URL = "..."` | `src/config.js` | `.env` 에서 읽어 옴 |
| `loadStudents()` | `main.js` + `api/studentApi.js` | async/await, 로딩 표시 추가 |
| `renderStudentTable()` | `ui/studentTable.js` | **textContent, 이벤트 위임** |
| `createStudent()` | `api/studentApi.js` | **request() 재사용**, 화살표 함수 |
| `updateStudent()` | `api/studentApi.js` | 위와 동일 |
| `deleteStudent()` | `api/studentApi.js` + `main.js` | **통신과 confirm 을 분리** |
| `editStudent()` | `main.js` `startEdit()` + `ui/studentForm.js` | 통신과 폼 채우기를 분리 |
| `validateStudent()` | `lib/validation.js` | **alert 제거, 메시지 반환** + 주소 검사 삭제, 이메일 필수화 |
| `isValidEmail()` | `lib/validation.js` | 화살표 함수 |
| `isValidStudentNumber()` | `lib/validation.js` | **학번 규칙 강화 — `EE002` 형식으로 좁힘** |
| `resetForm()` | `ui/studentForm.js` | `setEditMode` 와 함께 정리 |
| `showError` / `showSuccess` | `ui/message.js` | **showMessage 하나로 통합**, 성공은 3초 뒤 자동 소멸 |
| (쓰이지 않던 `loadingMessage`) | `ui/message.js` `setLoading()` | **실제로 동작하게 됨** |
| (쓰이지 않던 `cancelButton`) | `ui/studentForm.js` | **실제로 동작하게 됨** |

### 이 프로젝트에서 쓴 ES6+ 문법 (부록 B)

| 문법 | 쓰인 곳 | 무엇을 해결했나 |
|---|---|---|
| `const` / `let` | 전 구간 | var 를 쓰지 않아 스코프 사고를 막음 |
| 템플릿 리터럴 | `config.js`, `studentApi.js` | URL 조합을 읽기 쉽게 |
| 화살표 함수 | API 5개 함수, 콜백 전부 | 짧게 쓰고 this 문제를 피함 |
| 구조 분해 | `fillForm`, `createStudentRow`, `validateStudent` | 필요한 값만 한 줄로 |
| 구조 분해 기본값 | `validateStudent` 의 `detailRequest = {}` | 값이 없어도 안 터지게 |
| 기본 매개변수 | `request(url, options = {})`, `setLoading(true)`, `showMessage` 의 `timeout = 0` | 생략 가능한 인자 |
| `?.` 옵셔널 체이닝 | `fillForm`, `createStudentRow` 의 `detail?.` | **detail 이 없어도 오류가 안 남** |
| `??` 널 병합 | `config.js`, `request()`, 표의 `"-"` | 빈 문자열과 0을 살리면서 기본값 |
| `async / await` | `main.js`, `studentApi.js` 전부 | then 체인을 없앰 |
| `try / catch / finally` | `loadStudents` 등 | 오류 처리와 뒷정리를 한곳에 |
| **모듈** (`export`/`import`) | 7개 파일 | **전역 오염 제거, 역할 분리** |
| `import.meta.env` | `config.js` | 환경별 설정 분리 |
| `dataset` / `closest` | `studentTable.js`, `main.js` | **onclick 없이 이벤트 처리** |

---

## 4-12. ECMAScript 파트 자가 점검 질문

**변수·스코프**
1. `var` 의 세 가지 문제를 말하라. 그중 가장 위험한 것은? → `ch03_variable.js`
2. TDZ가 무엇이며, `undefined` 가 나오는 것보다 오류가 나는 편이 나은 이유는?
3. `for (var i...)` 안의 `setTimeout` 이 전부 같은 값을 찍는 이유는?
4. `function` 을 화살표 함수로 바꿨더니 갑자기 오류가 난다. 왜인가?

**문법**
5. 화살표 함수가 `function` 과 근본적으로 다른 점 하나는?
6. `() => { ok: true }` 가 undefined를 돌려주는 이유와 해결책은?
7. `const { phone: { home } } = user` 에서 `phone` 은 변수가 되나?
8. `{ ...a, x: 1 }` 과 `{ x: 1, ...a }` 의 결과가 다른 경우는?
9. 전개 구문으로 복사했는데 원본이 바뀐다. 원인과 해결은? → `ch06_spread.js`
10. `function f({ name } = {})` 에서 마지막 `= {}` 를 빼면 어떤 오류가 나나?
11. `Object.fromEntries(new FormData(form).entries())` 를 한국어로 풀어 말하라.
12. 같은 이름의 체크박스가 여러 개일 때 `fromEntries` 의 결과는?

**비동기·모듈**
13. `async` 함수를 불렀는데 `Promise { <pending> }` 가 찍힌다. 왜인가?
14. `Promise.all` 을 쓰면 빨라지는 이유와, 쓰면 안 되는 경우는?
15. `export` 를 안 붙인 함수는 `import * as m` 으로 받으면 보이나? → `ch08_module.js`
16. **모듈 안의 함수를 `onclick="fn()"` 으로 부를 수 없는 이유는?**
17. `type="module"` 없이 `import` 를 쓰면 어떤 오류가 나나?

**Vite·구조**
18. `.env` 변수 이름에 `VITE_` 를 붙여야 하는 이유는?
19. `.env` 에 API 키를 넣으면 안 되는 이유는?
20. 4부 `request()` 함수가 3부 코드의 무엇을 없앴나? 상태 204를 어떻게 다루나?
21. `validateStudent` 에서 `alert` 를 걷어내고 문자열을 반환하게 바꾼 이유 세 가지는?
22. `closest()` 와 `querySelector()` 의 탐색 방향 차이는?
23. `dataset.id` 를 `Number()` 로 감싸지 않으면 어디가 깨지나?
24. `main.js` 맨 아래에서 `DOMContentLoaded` 없이 `loadStudents()` 를 바로 부를 수 있는 이유는?

**실물 코드 (student_ecma)**
25. `main.js` 가 `fetchStudents as fetchStuAll` 처럼 **`as` 로 별칭**을 붙인 이유는? 언제 없어지나? → 4-0-1절
26. `main.js` 맨 아래 `window.editStudent = editStudent` 두 줄은 무엇이며, 언제 지우나?
27. `classList.toggle("editing")` 과 `classList.toggle("editing", isEditing)` 의 차이는? 어떤 버그가 생기나?
28. `collectStudentData` 에서 `address` 에는 `|| null` 이 있는데 `email` 에는 없다. 왜인가?
29. 4부 `validateStudent` 에서 **주소 검사가 사라진** 이유는? 학번 형식은 어떻게 바뀌었나?
30. `!studentNumber || !isValidStudentNumber(studentNumber)` 에서 **순서를 바꾸면** 어떤 오류가 나나?
31. `updateStudent` 에서 `resetForm()` 을 `showSuccess()` **앞에** 두는 이유는?
