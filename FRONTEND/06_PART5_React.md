# PART 5. React

## 5-0. 저장소 개요와 진도 현황

```
frontendhcj/
├─ 05_react/                  ← 「React 기초」 예제 10장 (ch02 ~ ch11)
│  ch02_component  ch03_jsx  ch04_props_state  ch05_event  ch06_ref
│  ch07_map  ch08_hooks  ch09_usestate  ch10_useeffect  ch11_useref
│  step/  5-2_main.jsx · 5-5_StudentTable.jsx · 5-6_StudentForm.jsx
│         5-6_studentData.js · 5-7_MessageBox.jsx · 5-10_App.jsx
│
├─ todolist_react/            ← ★ 첫 React 앱 (부록 E). 완성됨
│  src/App.jsx                ⚠️ 지금은 UseCallbackApp 을 그리는 "갈아 끼우는 자리"
│  src/AppTodo.jsx            TodoList 본체 — state 3개 + 핸들러 5개 (옛 App.jsx)
│  src/main.jsx               ⚠️ StrictMode 를 주석 처리해 둠 (E-19 확인용)
│  src/components/
│     MyComponent.jsx         클래스 컴포넌트 (몸풀기)
│     MyComponentFunc.jsx     같은 화면을 함수 컴포넌트로
│     TodoListTemplate.jsx    바깥 틀 (form · children 두 자리)
│     Form.jsx                입력칸 + 추가 버튼
│     TodoItemList.jsx        map 으로 TodoItem 들을 그린다  (memo)
│     TodoItem.jsx            할 일 한 줄                    (memo)
│     UseCallbackApp.jsx      ★ E-19 — useCallback 을 Set 크기로 확인
│
└─ student_react_first/       ← ★ 5부 실습. 실습 5-13 까지 완성
   src/App.jsx                state 6개 + useCallback 4개 + useEffect 2개
   src/main.jsx               createRoot + StrictMode
   src/config.js              ✅ 4부에서 그대로 복사
   src/api/studentApi.js      ✅ 4부에서 그대로 복사
   src/lib/validation.js      ✅ 4부에서 그대로 복사
   src/lib/studentData.js     EMPTY_FORM · toRequest · toFormValues (실습 5-6)
   src/style.css              4부 복사 + .error-message 의 display:none 주석 처리
   src/components/
      StudentTable.jsx        표 (실습 5-5) — memo 로 감쌈 (실습 5-13)
      StudentForm.jsx         폼 — 입력칸 여섯 개를 펼쳐 씀 (실습 5-6)
      StudentFormField.jsx    폼 — Field 로 묶음 (실습 5-12). App 은 이쪽을 import
      MessageBox.jsx          메시지 한 줄 (실습 5-7)
```

**진도 현황 — 지금 어디까지 왔는가**

| 자료 | 상태 |
|---|---|
| 「React 기초」 1~11장 (개념) | ✅ 강의 진행 |
| 부록 E — TodoList 앱 (E-1 ~ E-20) | ✅ `todolist_react` 완성. E-19 는 `UseCallbackApp.jsx`, **E-20 은 `todolist_react_router` 의 `TodoPage.jsx` 에 반영돼 있다** (5-12-7절) |
| 5부 실습 5-1 ~ 5-13 | ✅ `student_react_first` 완성 — **전부 실물 대조** |
| 「React 중급」 + 6부 실습 6-1 ~ 6-14 | ✅ PART 6 에서 다룬다 |

1.3 에서 "교재 기준"이라고 적었던 실습 5-5 ~ 5-12 는 이번에 **실물 코드와 한 줄씩 맞대어 봤다.** 교재와 거의 같고, 다른 곳은 그 자리에 ⚠️ **실물은…** 으로 적어 두었다.

**React 버전은 19.2.8**, Vite 8.3.0, `@vitejs/plugin-react` 6.1.1 이다.

---

## 5-1. React 란 무엇인가

### 개념

React 는 2013년 페이스북(현 Meta)이 공개한 오픈소스 JavaScript 라이브러리다. 한 문장으로 줄이면 이렇다.

> **React = 값(데이터)을 받아 화면(UI)을 돌려주는 라이브러리**

| 구분 | 내용 |
|---|---|
| React 가 해 주는 것 | 화면 그리기, 값이 바뀌었을 때 화면 갱신, 컴포넌트 단위 구성 |
| React 가 **안** 해 주는 것 | 서버 통신(fetch, Axios), 주소 이동(React Router), 폼 검증(Zod), 전역 상태(Redux, Zustand), 스타일(MUI) |

⚠️ **이 성격 때문에 React 를 "프레임워크"가 아니라 "라이브러리"라고 부른다.** 프레임워크(예: Angular)는 라우팅·통신·폼까지 한 벌로 정해 주고 그 틀 안에서 코드를 쓰게 하지만, React 는 화면 부분만 맡고 나머지는 우리가 고른다. **자유롭지만 선택할 것이 많다는 뜻이기도 하다.**

**왜 만들어졌나 — 명령형 DOM 조작의 한계**

3부와 4부에서 화면을 바꾸던 코드를 모아 보면 이렇다.

```js
studentTableBody.innerHTML = "";                  // 표를 비우고
students.forEach(... appendChild ...);            // 다시 채우고
loadingMessage.style.display = "block";           // 로딩을 켜고
submitButton.textContent = "학생 수정";            // 버튼 글자를 바꾸고
cancelButton.style.display = "inline-block";      // 취소 버튼을 보이고
formContainer.classList.toggle("editing", true);  // 테두리를 강조하고
```

전부 **"화면을 어떻게 바꿀지"를 한 단계씩 적은 것**이고, 이런 방식을 **명령형(imperative)**이라고 한다. 두 가지 문제가 있다.

| 문제 | 설명 |
|---|---|
| **상태가 화면에만 있다** | 지금 수정 모드인지 알려면 버튼 글자를 읽어 봐야 한다. 코드만 봐서는 알 수 없다 |
| **빠뜨리기 쉽다** | 수정 모드로 들어가려면 세 군데를 고쳐야 한다. 한 군데를 빠뜨리면 화면이 어긋나고, 그런 버그는 찾기 어렵다 |

**선언형 UI — 값만 정하면 된다**

React 는 "화면을 어떻게 바꿀지" 대신 **"이 값일 때 화면은 이렇게 생겼다"**만 적게 한다. 이것을 **선언형(declarative)**이라고 한다.

```jsx
// 값 하나로 세 군데가 한꺼번에 정해진다
const isEditing = editingId !== null;

// ① 버튼 글자
let actionLabel = "등록";
if (isEditing) { actionLabel = "수정"; }
<button type="submit">학생 {actionLabel}</button>

// ② 수정 모드일 때만 취소 버튼을 그린다
{isEditing && <button className="cancel-btn">취소</button>}

// ③ 폼 테두리 강조
let containerClass = "form-container";
if (isEditing) { containerClass = "form-container editing"; }
<div className={containerClass}>
```

**빠뜨릴 수가 없다. 세 군데가 모두 같은 값 하나를 보고 있기 때문이다.**

> **■ 외워 둘 한 줄** — **"화면 = f(값)"**
> 같은 값이면 언제나 같은 화면이 나오고, 화면을 바꾸려면 값을 바꾸면 된다.
> React 코드를 읽을 때 **"이 화면을 만든 값이 무엇인가"**를 먼저 찾으면 길을 잃지 않는다.

**가상 DOM — 다시 그려도 느리지 않은 이유**

"값이 바뀌면 화면을 다시 그린다"는 말을 들으면 걱정이 생긴다. 학생이 100명인 표에서 이름 하나가 바뀌었다고 표 전체를 다시 만들면 느릴 텐데.

실제로는 그렇게 하지 않는다. React 는 화면을 곧바로 고치지 않고, 먼저 **"설계도"를 만들어 견주어 본다.** 그 설계도가 **가상 DOM** 이다.

```
① 값이 바뀌면 화면 전체를          가상 DOM 은 화면이 아니라 JavaScript
   가상 DOM 에 새로 그린다    →    객체다. 만드는 데 돈이 거의 안 든다
          │
          ▼
② 바뀌기 전 가상 DOM 과            객체끼리 비교하는 일이라 빠르다.
   새 가상 DOM 을 견준다      →    이 과정을 재조정(reconciliation)이라 한다
          │
          ▼
③ 달라진 곳만 실제 DOM 에          실제 DOM 을 고치면 브라우저가 화면을
   반영한다                   →    다시 계산한다. 이 일이 비싸므로 최소로 줄인다
```

| 단계 | 이름 | 하는 일 |
|---|---|---|
| ① ② | **렌더(render)** | 컴포넌트 함수를 실행해 이번 화면의 설계도를 만들고, 이전 것과 견준다 |
| ③ | **커밋(commit)** | 달라진 곳만 실제 DOM 에 반영한다 |

학생 100명 표에서 한 명의 이름만 바뀌었다고 하자. ①②는 100명분을 다 거치지만 **전부 JavaScript 객체를 다루는 일**이다. ③에서 실제로 고쳐지는 것은 `td` 하나뿐이다. 그래서 "다 다시 그린다"고 써도 화면은 느려지지 않는다.

⚠️ **①에서 컴포넌트 함수가 다시 실행된다는 점을 기억할 것.** 값이 바뀔 때마다 몇 번이고 다시 실행된다. 그래서 컴포넌트 본문에 서버 요청이나 타이머를 두면 그것도 몇 번이고 실행된다. **그런 일은 `useEffect` 안에 넣어야 한다(5-10절).**

**단방향 데이터 흐름**

React 에서 데이터는 **위에서 아래로만** 흐른다. 부모가 자식에게 값을 주고, 자식은 그 값을 읽기만 한다.

```
App (students, form, editingId ... 값을 가진다)
 |
 |-- StudentForm  (form 을 받아 그린다)
 |
 +-- StudentTable (students 를 받아 그린다)
```

자식이 값을 바꾸고 싶으면 직접 고치지 않고, **부모가 준 함수를 부른다.** "바꿔 주세요"라고 알리는 것.

**React 생태계 — 무엇이 무엇인가**

| 이름 | 역할 |
|---|---|
| `react` | 컴포넌트와 Hook 을 만드는 핵심. **화면이 어디에 그려지는지는 모른다** |
| `react-dom` | 그 결과를 브라우저 DOM 에 그린다. `createRoot` 가 여기 있다 |
| `react-native` | 같은 React 로 모바일 앱 화면을 그린다 |
| `@vitejs/plugin-react` | Vite 가 JSX 를 변환하고 새로고침 없이 갱신(HMR)하게 해 준다 |
| `eslint-plugin-react-hooks` | Hook 규칙을 어겼는지 검사한다 (5-8절) |

🔗 `package.json` 에 `react` 와 `react-dom` 이 **함께** 들어가는 이유가 이것이다. React 는 "화면을 어떻게 생기게 할지"만 정하고, 실제로 어디에 그릴지는 `react-dom` 이 맡는다.

---

## 5-2. 컴포넌트

### 개념

컴포넌트는 화면을 이루는 조각이다. 그리고 React 에서 컴포넌트는 특별한 무엇이 아니라 **"화면을 돌려주는 함수"**다.

```jsx
function MessageBox() {
    return <span className="error-message">저장되었습니다.</span>;
}
export default MessageBox;
```

평범한 함수다. 다른 점은 돌려주는 값이 숫자나 문자열이 아니라 **화면**이라는 것뿐. 이 함수를 쓸 때는 **태그처럼** 쓴다.

```jsx
<MessageBox />
```

HTML 태그가 `button`, `table` 처럼 정해진 것만 있다면, **컴포넌트는 우리가 만드는 태그**다.

**컴포넌트 작성 규칙 세 가지**

| 규칙 | 이유 |
|---|---|
| **이름은 대문자로 시작한다** | 소문자로 시작하면 React 가 HTML 태그로 본다. `<messageBox />` 는 없는 태그를 찾다가 **아무것도 그리지 않는다** |
| **돌려주는 태그는 하나여야 한다** | 함수가 값을 하나만 돌려주는 것과 같다. 여러 개면 `<> </>` 로 감싼다 |
| **같은 입력이면 같은 화면이어야 한다** | 컴포넌트 함수는 여러 번 실행된다. 본문에서 바깥 값을 고치거나 서버를 부르면 실행할 때마다 결과가 달라진다 |

```jsx
// 오류 — 나란히 두 개
return (
  <h1>제목</h1>
  <p>본문</p>
);

// 빈 태그(프래그먼트)로 감싸면 된다. 실제 화면에는 아무것도 생기지 않는다
return (
  <>
    <h1>제목</h1>
    <p>본문</p>
  </>
);
```

⚠️ `Adjacent JSX elements must be wrapped in an enclosing tag` → 태그 여러 개를 나란히 돌려줬다. `<>` 와 `</>` 로 감싸라.

**컴포넌트 트리 — 화면을 나누는 기준**

```
App
|-- StudentForm  (학생 등록 · 수정 폼)
|    +-- MessageBox  (폼 아래 메시지 한 줄)
|
+-- StudentTable (학생 목록 표)
```

나누는 기준은 **"화면에서 하나의 덩어리로 보이는가"**와 **"따로 떼어 이해할 수 있는가"**다. 처음부터 잘게 나눌 필요는 없다. **한 파일이 길어져 읽기 힘들어지면 그때 나누어도 늦지 않다.**

🔗 4부에서 `ui/message.js`, `ui/studentForm.js`, `ui/studentTable.js` 로 나눴던 기준과 **똑같다.** 나누는 단위가 "모듈"에서 "컴포넌트"로 바뀌었을 뿐, 역할별로 쪼갠다는 생각은 그대로다.

### 코드 위치

`05_react/js/ch02_component.jsx` — 예제
`todolist_react/src/components/` — 실제로 만든 컴포넌트 6개

---

## 5-3. JSX 란 무엇인가

### 개념

React 는 화면을 JavaScript 객체로 표현한다. 그 객체를 만드는 함수가 `createElement` 다. **JSX 없이** 화면을 만들면 이렇게 된다.

```js
React.createElement(
  "tr",
  { key: student.id },
  React.createElement("td", null, student.name),
  React.createElement("td", null, student.studentNumber),
  React.createElement("td", null, student.detail?.address ?? "-")
);
```

같은 화면을 **JSX 로** 쓰면 이렇다.

```jsx
<tr key={student.id}>
  <td>{student.name}</td>
  <td>{student.studentNumber}</td>
  <td>{student.detail?.address ?? "-"}</td>
</tr>
```

**두 코드는 완전히 같은 일을 한다.** JSX 는 위 코드를 아래처럼 쓸 수 있게 해 주는 문법일 뿐이다.

| 왜 JSX 를 쓰는가 | 설명 |
|---|---|
| 화면 구조가 눈에 보인다 | 중첩된 `createElement` 는 몇 겹만 깊어져도 어디가 어디인지 알 수 없다. **JSX 는 들여쓰기가 곧 화면 구조다** |
| 화면과 로직이 함께 있다 | "이 값일 때 이 화면"이라는 관계를 한자리에서 본다 |
| 그래도 JavaScript 다 | 변수, 함수 호출, 삼항 연산자, `map` 을 그대로 쓴다. **새 템플릿 문법을 따로 안 배워도 된다** |
| 오류를 일찍 잡는다 | 닫는 태그를 빠뜨리면 빌드할 때 오류가 난다 |

**JSX 는 어떻게 JavaScript 가 되는가**

브라우저는 JSX 를 모른다. `<tr>` 을 그대로 보내면 문법 오류가 난다. 그래서 빌드 도구가 미리 바꿔 준다(4-2절).

```
내가 쓴 것            Vite(@vitejs/plugin-react)       브라우저가 받는 것
<td>{name}</td>   ──────────────────────────▶   jsx("td", { children: name })
```

**JSX 의 규칙 — HTML 과 다른 여섯 가지**

| HTML | JSX | 이유 |
|---|---|---|
| `class="card"` | `className="card"` | `class` 는 JavaScript 예약어 |
| `for="name"` | `htmlFor="name"` | `for` 도 예약어 |
| `colspan="7"` | `colSpan={7}` | 속성은 낙타 표기법 |
| `onclick="fn()"` | `onClick={fn}` | **문자열이 아니라 함수를 넘긴다** |
| `style="color:red"` | `style={{ color: "red" }}` | 문자열이 아니라 **객체** |
| `<br>` | `<br />` | 닫지 않는 태그도 반드시 닫는다 |

중괄호 안에는 **값을 내는 JavaScript 라면 무엇이든** 쓸 수 있다.

```jsx
<td>{name}</td>                             {/* 변수 */}
<td>{name.toUpperCase()}</td>               {/* 함수 호출 */}
<td>{student.detail?.address ?? "-"}</td>   {/* 4부 문법 그대로 */}
<td>{isEditing ? "수정" : "등록"}</td>       {/* 삼항 연산자 */}
<td colSpan={7}>...</td>                    {/* 숫자는 중괄호로 */}
```

⚠️ **반대로 중괄호 안에 쓸 수 없는 것은 `if` 문과 `for` 문이다.** 값이 아니라 **"문장"**이기 때문. 조건은 삼항 연산자나 `&&` 로, 반복은 `map` 으로 쓴다(5-7절).

⚠️ **`style` 의 중괄호가 두 겹인 이유** — 바깥 중괄호는 "여기부터 JavaScript", 안쪽 중괄호는 **객체**다. 속성 이름도 `background-color` 가 아니라 `backgroundColor`.

⚠️ `Objects are not valid as a React child` → 중괄호 안에 객체를 넣었다. `{student}` 가 아니라 `{student.name}` 처럼 값을 꺼내야 한다.

**.js · .jsx · .tsx — 확장자가 뜻하는 것**

"그냥 `.js` 로 쓰면 안 되나요?"라는 질문이 자주 나온다. 답은 이렇다. **JSX 는 JavaScript 문법이 아니기 때문에 누군가 반드시 번역해 주어야 한다. 그 번역기를 켜는 스위치가 확장자다.**

Vite 의 React 플러그인은 **`.jsx` 와 `.tsx` 파일만** 번역 대상으로 본다. `.js` 파일은 평범한 JavaScript 로 여기고 그냥 지나간다.

| 확장자 | 무엇이 들어 있나 | 언제 쓰나 |
|---|---|---|
| `.js` | JavaScript 만 | `config.js`, `studentApi.js`, `validation.js` 처럼 **화면이 없는 파일** |
| `.jsx` | JavaScript + JSX | `App.jsx`, `StudentTable.jsx` 처럼 **화면을 돌려주는 파일** |
| `.ts` | TypeScript 만 | 위 `.js` 와 같되 값의 타입을 함께 적는다 |
| `.tsx` | TypeScript + JSX | 위 `.jsx` 와 같되 props 의 타입을 함께 적는다 |

⚠️ `Failed to parse source for import analysis` / `Unexpected token <` → **`.js` 파일에 JSX 를 썼다.** 확장자를 `.jsx` 로 바꿔라.

🔗 `student_react_first` 를 보면 이 규칙이 그대로 적용돼 있다. `config.js` · `api/studentApi.js` · `lib/validation.js` 는 `.js` 이고, `App.jsx` · `main.jsx` 는 `.jsx` 다. **4부에서 그대로 복사해 온 세 파일이 전부 `.js` 인 것은 우연이 아니다. 화면을 안 만지는 파일이기 때문이다.**

---

## 5-4. Props 와 State

### 개념

| | **Props** | **State** |
|---|---|---|
| 어디서 오나 | 부모가 준다 | 컴포넌트가 스스로 가진다 |
| 바꿀 수 있나 | **없다 (읽기 전용)** | 있다 (setter 로) |
| 바뀌면 | 부모가 다시 그릴 때 새 값이 온다 | 그 컴포넌트가 다시 그려진다 |
| 비유 | 함수의 **매개변수** | 함수가 **기억하는 값** |
| 5부 예 | `students`, `onEdit`, `message` | `students`, `form`, `editingId` |

같은 `students` 가 양쪽에 나오는 것이 이상해 보일 수 있다. **App 에서는 자기가 가진 state 이고, StudentTable 에서는 부모가 준 props 다. 누구 입장에서 보느냐의 차이다.**

**Props — 부모가 주는 읽기 전용 값**

```jsx
// 주는 쪽 (부모)
<MessageBox message={message} />

// 받는 쪽 (자식)
function MessageBox(props) {
    return <span>{props.message.text}</span>;
}
```

보통은 **4부 구조 분해를 매개변수 자리에서 바로 쓴다.** React 코드가 거의 이 형태다.

```jsx
function StudentTable({ students, loading, error, onEdit, onDelete }) { ... }
```

🔗 **4-5절(구조 분해)을 익혀 두면 React 예제의 절반은 이미 읽을 수 있다**고 한 것이 이것이다.

**함수도 props 로 넘긴다. 자식이 부모에게 알리는 유일한 방법이다.**

```jsx
// 부모 — 무엇을 할지 정한다
<StudentTable students={students} onDelete={handleDelete} />

// 자식 — 언제 부를지만 정한다
<button onClick={() => onDelete(id)}>삭제</button>
```

⚠️ props 는 **읽기만** 한다. 자식이 `props.students.push(...)` 처럼 고치면 안 된다. 값을 바꾸는 것은 그 값을 가진 부모의 일이다.

> **이름 규칙** — 값이면 명사(`students`, `loading`), 함수면 `on` 으로 시작(`onEdit`, `onDelete`). 그 알림을 처리하는 쪽은 `handle` 로 시작(`handleEdit`). 그래서 `onEdit={handleEdit}` 이라고 쓰인 줄이 자연스럽게 읽힌다.

**무엇을 state 로 둘까 — 기준 두 가지**

| 질문 | 아니라면 |
|---|---|
| 화면에 영향을 주는가 | 아니면 그냥 변수나 `ref` 로 둔다 (5-11절) |
| **이미 있는 값으로 계산해 낼 수 있는가** | 계산할 수 있으면 state 로 두지 않고 그때그때 계산한다 |

두 번째 기준이 처음에는 잘 와닿지 않는다. 예를 보자.

```jsx
// ❌ 안 된다 — 같은 사실을 두 곳에 적어 두었다
const [editingId, setEditingId] = useState(null);
const [isEditing, setIsEditing] = useState(false);

function startEdit(id) {
    setEditingId(id);
    setIsEditing(true);     // 이 줄을 빠뜨리면 두 값이 어긋난다
}
function cancel() {
    setEditingId(null);
    setIsEditing(false);    // 여기서도 빠뜨리면 안 된다
}
```

`editingId` 에 값이 있다는 것과 수정 모드라는 것은 **같은 사실**이다. 두 곳에 따로 적어 두면 한쪽만 바꾸는 실수가 생긴다. `editingId` 는 3인데 `isEditing` 은 false 인 상태 — **화면은 등록 모드처럼 보이는데 제출하면 수정이 되는 셈이다.**

```jsx
// ✅ 이렇게 — 사실은 하나만 두고 나머지는 계산한다
const [editingId, setEditingId] = useState(null);
const isEditing = editingId !== null;   // 그냥 변수다. useState 가 아니다

function startEdit(id) {
    setEditingId(id);   // 빠뜨릴 줄이 없다
}
```

**실습 앱의 값들을 이 기준으로 가르면**

| 값 | state 인가 | 이유 |
|---|---|---|
| `students` | ✅ | 서버에서 받아 오는 값. 다른 값으로 계산할 수 없다 |
| `form` | ✅ | 사용자가 입력한 값. 계산할 수 없다 |
| `editingId` | ✅ | 어느 학생의 수정 버튼을 눌렀는가. 계산할 수 없다 |
| `isEditing` | ❌ | `editingId !== null` 로 계산된다 |
| `actionLabel` ("등록"/"수정") | ❌ | `isEditing` 으로 계산된다 |
| `containerClass` | ❌ | `isEditing` 으로 계산된다 |
| `formRef` | ❌ | 화면에 영향을 주지 않으므로 `ref` 로 둔다 |

> **정리하면 state 는 "이 값을 아무도 알려 주지 않으면 알아낼 방법이 없는 것"이다.** 서버가 준 값, 사용자가 누른 것, 사용자가 입력한 글자가 그렇다. 나머지는 그 값들로부터 계산해 낸다.
> 이 원칙의 이름이 **"하나의 사실은 한 곳에만(single source of truth)"**이다.

**State 를 어디에 둘 것인가 — 상태 끌어올리기**

두 컴포넌트가 같은 값을 필요로 하면, 그 값은 **둘의 공통 부모로 올라간다.** 이것을 **상태 끌어올리기(lifting state up)**라고 한다.

```
App  ← students 를 여기 둔다
|-- StudentForm   (등록하면 App 에 알린다)
+-- StudentTable  (students 를 받아 그린다)
```

| 장점 | 설명 |
|---|---|
| 어긋나지 않는다 | 같은 값을 두 컴포넌트가 따로 갖고 있지 않으므로 한쪽만 낡는 일이 없다 |
| 누가 바꿨는지 찾기 쉽다 | 값을 바꾸는 코드가 부모 한 곳에만 있다 |
| 자식이 단순해진다 | 자식은 받은 것을 그리기만 한다 |

| 단점 | 설명 |
|---|---|
| 부모가 비대해진다 | `App.jsx` 에 state 와 처리 함수가 모두 모인다 |
| **props 를 계단처럼 내려보낸다** | 손자에게 값을 주려면 중간 컴포넌트가 쓰지도 않는 props 를 받아 넘겨야 한다 |
| 필요 없는 다시 그리기 | 부모의 state 하나가 바뀌면 자식들이 함께 다시 실행된다 |

두 번째를 **props 내려꽂기(prop drilling)**라고 부른다.

```jsx
<Layout user={user}>          {/* Layout 은 user 를 쓰지 않는다 */}
  <Sidebar user={user}>       {/* Sidebar 도 쓰지 않는다 */}
    <Profile user={user} />   {/* 여기서야 쓴다 */}
  </Sidebar>
</Layout>
```

**부모가 무거워지면 — Context · Redux · Zustand**

| 이름 | 무엇인가 | 언제 고르나 |
|---|---|---|
| **Context API** | React 에 들어 있는 기능. 값을 트리 아래로 한 번에 내려 준다 | 테마·로그인 정보처럼 **자주 바뀌지 않는 값** |
| **Zustand** | 작은 외부 라이브러리. 훅처럼 쓴다 | 가볍게 시작하고 싶을 때 |
| **Redux (Toolkit)** | 규칙이 분명한 상태 관리 도구. 생태계가 크다 | 사람이 여럿이고 상태 변화가 복잡할 때 |

```jsx
// Zustand — 트리 바깥에 두고 필요한 곳에서 꺼낸다
const useStudentStore = create((set) => ({
    students: [],
    setStudents: (list) => set({ students: list }),
}));

function StudentTable() {
    // props 가 필요 없다. 이 컴포넌트가 직접 꺼내 쓴다.
    const students = useStudentStore((s) => s.students);
    return ...;
}
```

⚠️ **편해 보이지만 대가가 있다.** 값이 어디서 바뀌는지 한눈에 보이지 않게 된다. props 로 내려 줄 때는 부모만 보면 됐지만, store 는 **어느 컴포넌트든 고칠 수 있기** 때문이다. 그래서 Redux 는 "값을 바꾸려면 정해진 통로로만"이라는 규칙을 두고, **그 규칙 때문에 적을 코드가 늘어난다.**

> **■ 실습 앱에는 상태 관리 도구가 필요 없다.** 컴포넌트 트리가 두 단계뿐이고 `App.jsx` 도 100줄 남짓이기 때문이다.
> **도구는 불편해진 뒤에 넣는 것이지, 미리 갖춰 놓고 시작하는 것이 아니다.**

---

## 5-5. 이벤트 핸들링

### 개념

| | 설명 |
|---|---|
| 쓰는 법 | `addEventListener` 대신 JSX 속성 `onClick`, `onChange`, `onSubmit` |
| 이름 | 소문자 `onclick` 이 아니라 낙타 표기법 **`onClick`** |
| 값 | 문자열이 아니라 **함수**를 넘긴다 |
| 떼어내기 | 컴포넌트가 사라지면 **React 가 알아서 정리한다** |

React 는 각 요소에 직접 이벤트를 걸지 않고, **최상위에서 한 번 받아** 해당 컴포넌트로 전달한다. 그래서 넘어오는 이벤트 객체도 브라우저의 것이 아니라 React 가 감싼 **합성 이벤트(SyntheticEvent)**다. 다만 쓰는 방법은 브라우저 이벤트와 같다.

**함수를 넘긴다, 부르지 않는다 — 가장 중요한 규칙**

```jsx
// ✅ 맞다 — 함수 자체를 넘긴다
<button onClick={handleClick}>확인</button>

// ✅ 맞다 — 인자가 필요하면 감싼다
<button onClick={() => onDelete(id)}>삭제</button>

// ❌ 틀리다 — 그리는 순간 실행되고, 그 결과가 넘어간다
<button onClick={onDelete(id)}>삭제</button>
```

세 번째는 **버튼을 누르지 않았는데 함수가 실행된다.** 그 함수가 state 를 바꾸면 다시 그려지고, 또 실행되어 **무한히 반복된다.**

⚠️ `Too many re-renders. React limits the number of renders` → 그리는 도중에 `setState` 가 불렸다. `onClick={fn(id)}` 를 `onClick={() => fn(id)}` 로 고쳐라.

🔗 3-6절의 `addEventListener("click", loadStudents())` 실수와 **완전히 같은 실수**다. 다만 React 에서는 결과가 훨씬 심각하다(무한 루프).

**이벤트 객체**

```jsx
function handleSubmit(event) {
    event.preventDefault();     // 4부와 똑같이 필요하다
    ...
}
<form onSubmit={handleSubmit}> ... </form>

// 입력값은 event.target.value 로 읽는다
<input onChange={(event) => onChange(name, event.target.value)} />
```

⚠️ **React 를 쓴다고 폼의 기본 동작(제출하면 페이지 새로고침)이 사라지지 않는다.** `event.preventDefault()` 는 3부·4부와 똑같이 필요하다.

**이벤트 위임이 필요 없는 이유**

4부에서 `tbody` 한 곳에서 클릭을 받은 이유는, 표를 다시 그리면 버튼에 걸어 둔 이벤트가 **떨어져 나갔기 때문**이다. React 는 다시 그릴 때 `onClick` 도 함께 붙여 주므로 그 문제가 없다.

| 4부 (이벤트 위임) | React |
|---|---|
| `tbody.addEventListener("click", ...)` | 버튼마다 `onClick` |
| `event.target.closest("button[data-action]")` | 필요 없음 |
| `button.dataset.action = "edit"` | 필요 없음 |
| `Number(button.dataset.id)` | id 를 그대로 넘긴다 |

**열세 줄이 한 줄이 된다.**

```jsx
<button onClick={() => onEdit(id)}>수정</button>
```

---

## 5-6. ref — DOM 에 직접 접근하기

### 개념

React 의 목표는 **DOM 을 직접 만지지 않는 것**이다. 그런데 값으로 표현할 수 없는 동작이 있다.

| 하고 싶은 일 | 값으로 표현되나 |
|---|---|
| 이 요소로 스크롤 | **안 된다** — "스크롤됨"이라는 상태가 없다 |
| 입력칸에 커서 두기 | 안 된다 |
| 동영상 재생 / 정지 | 안 된다 |
| 요소의 실제 크기 재기 | 안 된다 |

이런 경우에**만** 실제 DOM 요소가 필요하고, 그때 쓰는 것이 `ref` 다.

```jsx
import { useRef } from "react";

function App() {
    const formRef = useRef(null);

    function handleEdit(id) {
        // 아직 화면에 그려지기 전이면 current 가 null 이다.
        if (formRef.current) {
            formRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }

    return <div ref={formRef}> ... </div>;
}
```

`useRef(null)` 이 만든 상자를 `ref` 속성에 주면, React 가 그 상자의 `current` 자리에 **실제 DOM 요소를 넣어 준다.** 4부의 `document.getElementById` 를 대신하는 방법이다.

**state 와 ref — 무엇을 언제 쓰나**

| | **state** | **ref** |
|---|---|---|
| 바뀌면 | 화면을 다시 그린다 | **다시 그리지 않는다** |
| 값 읽기 | 이번에 그려질 때의 값 | 언제나 최신 값 |
| 용도 | 화면에 보여야 하는 값 | DOM 요소, 타이머 id 처럼 보이지 않는 값 |
| 바꾸는 법 | `setValue(새 값)` | `ref.current = 새 값` (직접 고쳐도 된다) |

> **판단 기준은 하나다.** "이 값이 바뀌면 화면이 달라져야 하는가?" 그렇다면 state, 아니면 ref.

⚠️ `Cannot read properties of null (reading 'scrollIntoView')` → 아직 화면에 붙기 전이라 `current` 가 null 이다. `if (formRef.current)` 로 확인하거나 `formRef.current?.` 를 쓴다.

---

## 5-7. 컴포넌트 반복 — map()

### 개념

JSX 안에서는 `for` 문을 쓸 수 없다(5-3절). 대신 배열의 `map()` 으로 **"값의 배열"을 "화면의 배열"로** 바꾼다.

```jsx
<tbody>
  {students.map((student) => (
    <tr key={student.id}>
      <td>{student.name}</td>
      <td>{student.detail?.address ?? "-"}</td>
    </tr>
  ))}
</tbody>
```

⚠️ **화살표 함수의 몸통을 소괄호로 감싼 것에 주의.** 중괄호를 쓰면 `return` 을 적어야 하지만 소괄호는 그 값을 바로 돌려준다(4-4절). **React 코드에서 가장 자주 보는 형태다.**

**key — React 가 행을 알아보는 표시**

목록이 바뀌면 React 는 이전 목록과 새 목록을 견주어 무엇을 고칠지 정한다(5-1절의 재조정). 이때 **"어느 것이 어느 것인지" 알아보는 이름표**가 `key` 다.

| key 로 쓰는 값 | 결과 |
|---|---|
| `student.id` (권장) | 학생이 지워지거나 순서가 바뀌어도 정확히 따라간다 |
| 배열 순서(index) | **중간이 지워지면 엉뚱한 행이 남는다** |
| 없음 | 경고가 나고, 목록이 바뀔 때 예상 밖으로 동작할 수 있다 |

**index 를 key 로 쓰면 안 되는 이유**

학생 A, B, C 가 있고 key 가 0, 1, 2 라고 하자. 여기서 A 를 지우면 남은 것은 B, C 이고 key 는 다시 0, 1 이 된다.

```
지우기 전 :  key=0 → A    key=1 → B    key=2 → C
지운 뒤   :  key=0 → B    key=1 → C

React 가 보기에는 "0번의 내용이 A 에서 B 로 바뀌었다" 로 보인다.
실제로는 "A 가 사라졌다" 인데도.
```

내용만 바뀐 것이라면 결과가 같아 보이지만, **각 행이 입력칸이나 애니메이션 같은 자기 상태를 갖고 있으면 그 상태가 엉뚱한 행에 남는다.**

⚠️ `Warning: Each child in a list should have a unique "key" prop` → **`map` 이 만드는 가장 바깥 요소**에 key 를 넣어라. 안쪽 `<td>` 가 아니라 `<tr>` 에 넣어야 한다.

---

## 5-8. React Hooks

### 개념

**Hooks 이전 — 클래스 컴포넌트**

2019년 이전의 React 에서는 state 를 가지려면 **클래스**로 써야 했다.

```jsx
class StudentList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { students: [], loading: false };
        this.handleClick = this.handleClick.bind(this);   // this 묶기
    }
    componentDidMount()  { this.loadStudents(); }                 // 화면에 붙은 뒤
    componentDidUpdate(prevProps) {                                // 갱신된 뒤
        if (prevProps.id !== this.props.id) this.loadStudents();
    }
    componentWillUnmount() { clearTimeout(this.timer); }           // 사라지기 전
    render() { return <div>{this.state.students.length}명</div>; }
}
```

`componentDidMount` 처럼 정해진 이름의 메서드를 **생명주기 메서드(lifecycle method)**라고 한다.

> **중요** — 이 메서드들은 **우리가 부르는 것이 아니다.** React 가 정해진 시점에 대신 불러 준다. 우리는 "이 이름으로 메서드를 만들어 두면 React 가 그때 불러 준다"는 약속에 따라 **이름만 맞춰 두는 것**이다.

| 메서드 | React 가 부르는 시점 | 주로 하는 일 |
|---|---|---|
| `constructor` | 컴포넌트가 만들어질 때 한 번 | state 초기값, bind |
| `render` | 화면을 그려야 할 때마다 | JSX 를 돌려준다 |
| `componentDidMount` | **화면에 처음 붙은 직후 한 번** | 서버 요청, 타이머 시작 |
| `componentDidUpdate` | props 나 state 가 바뀌어 다시 그려진 직후 | 바뀐 값에 맞춰 다시 요청 |
| `componentWillUnmount` | **화면에서 사라지기 직전** | 타이머 정리, 이벤트 해제 |

⚠️ **이름을 한 글자라도 틀리면 React 가 찾지 못해 그냥 지나간다.** 오류도 나지 않고 아무 일도 일어나지 않는다. `componentDidmount` 처럼 대소문자를 틀리는 실수가 여기에 해당한다.

**이름 짓는 규칙** — `Will` 이 붙으면 그 일을 하기 **직전**, `Did` 가 붙으면 그 일을 마친 **직후**.

```
▶ ① Mounting — 화면에 처음 붙을 때 한 번
constructor  →  componentWillMount  →  render  →  componentDidMount

▶ ② Updating — props 가 바뀌었을 때
componentWillReceiveProps → shouldComponentUpdate ──false──▶ 멈춘다
                                     │ true
                            componentWillUpdate → render → componentDidUpdate

▶ ③ Updating — state 가 바뀌었을 때
shouldComponentUpdate ──false──▶ 멈춘다
         │ true
componentWillUpdate → render → componentDidUpdate

▶ ④ Unmounting — 화면에서 사라질 때
componentWillUnmount   (타이머 정리, 이벤트 해제)
```

②와 ③은 거의 같다. **다른 것은 맨 앞 하나뿐이다.** state 가 바뀐 경우에는 새 props 를 받은 것이 아니므로 `componentWillReceiveProps` 가 없다.

⚠️ `shouldComponentUpdate` 만 성격이 다르다. 나머지는 "이때 무언가 하라"는 자리인데 이것은 **참·거짓을 돌려주어 다시 그릴지 말지를 결정한다.** 함수 컴포넌트에서는 `memo` 가 이 일을 대신하는데, **돌려주는 값의 뜻이 반대다**(5-12절).

⚠️ **`Will` 이 붙은 셋은 지금은 쓰지 않는다.** React 16.3 부터 권장하지 않는 것으로 바뀌었고 이름 앞에 `UNSAFE_` 가 붙었다. 비동기 렌더링에서 여러 번 불릴 수 있어 안전하지 않기 때문. 대신 쓰는 것은 `getDerivedStateFromProps` 와 `getSnapshotBeforeUpdate`.

**클래스 컴포넌트의 문제 세 가지**

| 문제 | 설명 |
|---|---|
| **`this` 가 어렵다** | 메서드를 이벤트 핸들러로 넘기면 `this` 가 사라진다(4-5절). 생성자에서 `bind` 를 하거나 화살표 함수로 감싸야 했다 |
| **한 가지 일이 여러 메서드로 흩어진다** | 아래에서 자세히 |
| **재사용이 어렵다** | "상태를 가진 로직"을 나눠 쓰려면 HOC 나 render props 같은 복잡한 방법을 써야 했다 |

두 번째가 핵심이다. 한 화면에서 서로 관계없는 일 두 가지를 한다고 하자. **(A)** id 에 해당하는 목록 불러오기, **(B)** 1초마다 시계 갱신.

```jsx
componentDidMount() {
    this.loadStudents();                            // (A) 처음 불러오기
    this.timer = setInterval(this.tick, 1000);      // (B) 타이머 시작
}
componentDidUpdate(prevProps) {
    if (prevProps.id !== this.props.id) {
        this.loadStudents();                        // (A) id 가 바뀌면 다시
    }
}
componentWillUnmount() {
    clearInterval(this.timer);                      // (B) 타이머 정리
}
```

**(A)는 두 메서드에, (B)도 두 메서드에 걸쳐 있다.** (A)를 이해하려면 `componentDidMount` 와 `componentDidUpdate` 를 오가야 한다. 이것이 "흩어진다"의 뜻이다.

> **원인은 나누는 기준에 있다.** 클래스는 코드를 **"무슨 일인가"가 아니라 "언제 실행되는가"로** 나눈다. 그런데 우리는 "목록 불러오기", "타이머"처럼 **일 단위로** 생각한다. **생각의 단위와 코드의 단위가 어긋나 있는 것이다.**

`useEffect` 는 반대로 나눈다. **일 하나가 덩어리 하나**가 되고, 언제 실행할지는 그 덩어리 안에서 정한다.

```jsx
// (A) 목록 불러오기 — 처음과 id 가 바뀔 때
useEffect(() => {
    loadStudents();
}, [id]);

// (B) 타이머 — 시작과 정리가 한자리에 있다
useEffect(() => {
    const timer = setInterval(tick, 1000);
    return () => clearInterval(timer);
}, []);
```

**타이머를 지우고 싶으면 그 덩어리를 통째로 지우면 된다.** 클래스였다면 세 메서드를 뒤져 관련 줄만 골라내야 했다.

**Hooks 의 등장** — 2019년 2월 React 16.8. 함수 컴포넌트가 state 와 생명주기를 다룰 수 있게 해 주는 함수들이며, **이름이 모두 `use` 로 시작한다.**

**클래스형과 함수형 비교**

| | 클래스 컴포넌트 | 함수 컴포넌트 + Hooks |
|---|---|---|
| 정의 | `class X extends React.Component` | `function X() { }` |
| 화면 반환 | `render() { return ... }` | 함수가 바로 `return` |
| 상태 | `this.state` / `this.setState` | `const [v, setV] = useState()` |
| props 읽기 | `this.props.name` | 매개변수 `{ name }` |
| 첫 실행 | `componentDidMount` | `useEffect(fn, [])` |
| 갱신 시 | `componentDidUpdate` | `useEffect(fn, [의존값])` |
| 정리 | `componentWillUnmount` | `useEffect` 가 돌려주는 함수 |
| `this` | 신경 써야 한다 (bind) | **없다** |
| 로직 재사용 | HOC, render props | **커스텀 훅** |
| 오늘날 | 유지보수용 | **새 코드의 표준** |

**Hooks 의 규칙 두 가지**

| 규칙 | 이유 |
|---|---|
| **컴포넌트 맨 위에서 부른다** | 조건문·반복문·중첩 함수 안에서 부르면 안 된다. React 는 Hook 을 **"부른 순서"로 구분**하므로, 순서가 달라지면 값이 뒤섞인다 |
| **함수 컴포넌트나 커스텀 훅에서만 부른다** | 일반 함수나 클래스 안에서는 부를 수 없다 |

```jsx
// ❌ 안 된다 — 조건에 따라 부르는 횟수가 달라진다
if (isEditing) {
    const [value, setValue] = useState("");
}

// ✅ 맞다 — 언제나 부르고, 조건은 그 안에서
const [value, setValue] = useState("");
if (isEditing) { ... }
```

⚠️ `Rendered more hooks than during the previous render` → 조건문 안에서 Hook 을 불렀다.

---

## 5-9. useState

### 개념

```jsx
import { useState } from "react";

function Counter() {
    const [count, setCount] = useState(0);
    //     ↑ 지금 값  ↑ 바꾸는 함수       ↑ 처음 값

    return <button onClick={() => setCount(count + 1)}>{count}</button>;
}
```

`useState` 는 배열 두 칸을 돌려주고, 우리는 **4부의 배열 분해**로 받는다.

⚠️ **컴포넌트 함수는 다시 그릴 때마다 처음부터 다시 실행되고, 그 안의 `const` 도 매번 새로 만들어진다.** 그런데도 `count` 값이 유지되는 것은 **React 가 그 값을 컴포넌트 바깥에 기억해 두기 때문**이다. `useState` 는 "기억해 둔 값을 꺼내 오는" 함수다.

**state 는 setter 함수로만 바꾼다**

```jsx
// ❌ 안 된다 — React 는 값이 바뀐 줄 모른다
form.name = "홍길동";
students.push(newStudent);

// ✅ 새 객체 · 새 배열을 만들어 넘긴다 (4부 전개 구문)
setForm({ ...form, name: "홍길동" });
setStudents([...students, newStudent]);
```

⚠️ **값을 바꿨는데 화면이 그대로다** → state 를 직접 고쳤다. `push` · `splice` · `obj.x = ...` 대신 새 배열·새 객체를 만들어 넘겨라.

⚠️ **`setState` 는 즉시 반영되지 않는다.** `setEditingId(3)` 바로 다음 줄에서 `editingId` 를 읽으면 아직 예전 값이다. 다음번에 다시 그려질 때 새 값이 된다.

**state 가 여러 개일 때 — 따로 둘까, 객체로 묶을까**

```jsx
// ▶ 방법 1 — 값마다 useState 를 따로 둔다
const [age, setAge] = useState(19);
const [siblingsNum, setSiblingsNum] = useState(10);

const handleAge = () => setAge(age + 1);
const handleSiblingsNum = () => setSiblingsNum(siblingsNum + 1);
```

읽기 쉽다. 다만 값이 늘어날 때마다 `useState` 한 줄과 **그 값을 바꾸는 함수 하나가 함께 늘어난다.** 위 코드에서도 두 함수가 하는 일은 "1 더하기"로 똑같은데 따로 적혀 있다.

```jsx
// ▶ 방법 2 — 객체 하나로 묶는다
const [state, setState] = useState({ age: 19, siblingsNum: 4 });

const handleClick = (val) =>
    setState({
        ...state,          // 전개 구문      — 기존 값을 복사
        [val]: state[val] + 1,   // 계산된 속성명 — val 에 담긴 이름의 칸
    });

const { age, siblingsNum } = state;   // 구조 분해

<button onClick={() => handleClick("age")}>Get older!</button>
<button onClick={() => handleClick("siblingsNum")}>More siblings!</button>
```

**값이 몇 개든 `handleClick` 함수 하나면 된다.** 이 코드에 4부에서 배운 문법 **세 가지가 한꺼번에** 들어 있다.

⚠️ **`...state` 를 빠뜨리면 안 된다.** `setState` 는 넘긴 객체로 **통째로 바꾸기** 때문에, `{ [val]: state[val] + 1 }` 만 넘기면 나머지 값이 사라진다.

**두 방법 중 무엇을 고를까**

| 이럴 때 | 이렇게 |
|---|---|
| 값들이 서로 상관없다 | **따로 둔다.** `students`, `editingId`, `loading` 이 그렇다 |
| 값들이 늘 함께 움직인다 | **객체로 묶는다.** 폼 입력칸 여섯 개가 그렇다 |
| 값이 세 개를 넘고 다루는 방식이 같다 | 객체로 묶으면 함수 하나로 끝난다 |

🔗 **실습 앱이 바로 이 기준을 따랐다.** `students` · `editingId` · `loading` 은 서로 상관없어 따로 두었고, 입력칸 여섯 개는 늘 함께 움직이므로 `form` 객체 하나로 묶었다.

⚠️ `state.val` 이라고 썼는데 `undefined` 다 → 변수에 담긴 이름으로 꺼낼 때는 **대괄호**를 쓴다. `state[val]` 이다.

**(1.4 보강) setter 에 값 대신 함수를 넘기기 — 함수형 업데이트**

```jsx
setCount(count + 1);          // 값을 넘긴다 — 지금 이 함수가 기억하는 count 를 쓴다
setCount((c) => c + 1);       // 함수를 넘긴다 — React 가 "최신 값"을 c 로 넣어 준다
setTodos((prev) => prev.filter((t) => t.id !== id));
```

**보통은 어느 쪽이든 같다.** 차이가 드러나는 것은 **함수가 옛 값을 기억한 채 굳었을 때**다 — `useCallback(…, [])` 으로 고정한 함수 안에서 state 를 읽는 경우(부록 E-19 · E-20, 5-12-6 · 5-12-7절). 함수형 업데이트는 **state 를 읽지 않으므로** 언제 불려도 최신 값에 적용된다. Zustand 의 `set((state) => …)` 도 같은 모양이다(6-12절).

**(1.4 보강) `useState(초깃값)` 의 초깃값은 첫 렌더에서 한 번만 쓰인다.** 다시 그릴 때는 무시된다. 그래서 "이 컴포넌트가 붙을 때 한 번 정해지는 값"(예: 옮겨 오면서 받은 메시지)을 초깃값으로 넣으면 `useEffect` 로 옮겨 담을 필요가 없다(6-9절 목록 페이지).

---

## 5-10. useEffect

### 개념

컴포넌트 함수는 화면을 만들기 위한 것이고, **여러 번 실행된다**(5-1절). 그 안에서 서버를 부르거나 타이머를 걸면 실행할 때마다 반복된다.

```jsx
// ❌ 안 된다 — 그릴 때마다 서버를 부른다
function App() {
    fetchStudents().then(setStudents);
    ...
}

// ✅ 그린 뒤에 한 번
useEffect(() => {
    loadStudents();
}, []);
```

`useEffect` 는 **"화면을 다 그린 뒤에 이 일을 하라"**는 뜻이다. 서버 부르기, 타이머, 이벤트 구독처럼 화면 바깥과 주고받는 일(**부수 효과, side effect**)이 여기에 들어간다.

**함수 컴포넌트의 생명주기 — 세 단계**

```
① Render phase — 화면을 계산한다
   컴포넌트 함수가 실행된다        useMemo 로 계산을 아껴 둘 수 있다
          │
          ▼
   return ( JSX )                  아직 화면에 붙지 않았다

② Commit phase — 화면에 반영한다
   React 가 DOM 과 ref 를 고친다
          │
          ▼
   useEffect / useLayoutEffect 가 실행된다

③ Cleanup phase — 치운다
   useEffect 가 돌려준 함수가 실행된다
   (다음 실행 직전, 그리고 컴포넌트가 사라질 때)
```

| 단계 | 무엇을 하나 | 지켜야 할 것 |
|---|---|---|
| **Render** | 화면을 계산만 한다 | **순수해야 한다.** 서버를 부르거나 DOM 을 만지면 안 된다 |
| **Commit** | DOM 에 반영하고 효과를 실행 | DOM 을 만져도 되고 서버를 불러도 된다 |
| **Cleanup** | 켜 둔 것을 끈다 | 빠뜨리면 메모리 누수가 생긴다 |

⚠️ **Render phase 가 순수해야 하는 까닭** — React 는 이 단계를 **멈췄다가 다시 시작하거나 아예 버릴 수 있다.** 그 안에서 서버를 불렀다면 요청이 여러 번 나가게 된다.

**useEffect 하나가 세 자리를 대신한다**

| 이렇게 적으면 | 클래스라면 이 자리 |
|---|---|
| `useEffect(fn, [])` | `componentDidMount` |
| `useEffect(fn, [a, b])` | `componentDidMount` + `componentDidUpdate` (a 나 b 가 바뀔 때만) |
| `useEffect(fn)` — 배열 생략 | `componentDidMount` + `componentDidUpdate` (**다시 그릴 때마다 — 거의 언제나 실수다**) |
| `fn` 이 함수를 `return` | `componentWillUnmount` |

```jsx
useEffect(() => {
    // componentDidMount 자리 — 켠다
    const timer = setInterval(tick, 1000);

    return () => {
        // componentWillUnmount 자리 — 끈다
        clearInterval(timer);
    };
}, []);   // [] 이므로 처음 한 번만
```

**한 덩어리 안에 "켜는 일"과 "끄는 일"이 함께 있다.** 5-8절에서 본 "관련된 코드가 흩어진다"는 문제가 여기서 풀린다.

> **■ 대응표를 외우기보다 "화면을 그린 뒤에 할 일"이라고 생각하는 편이 낫다.** React 공식 문서도 `useEffect` 를 생명주기 메서드의 대체품이 아니라 **"화면 바깥과 값을 맞추는 수단"**으로 설명한다. 대응표는 옛 코드를 읽을 때 쓰는 번역표로 두자.

⚠️ **서버 요청이 끝없이 반복된다** → 두 번째 인자를 빠뜨렸다. 요청이 끝나면 `setState` 가 불리고, 그러면 다시 그려지고, 또 요청이 나가는 고리에 빠진 것.

**정리 함수 — componentWillUnmount 를 대신한다**

```jsx
useEffect(() => {
    const timer = setTimeout(() => setMessage(null), 3000);
    return () => clearTimeout(timer);    // 정리 함수
}, [message]);
```

🔗 **4부에서는 `messageTimer` 변수를 두고 새 메시지가 올 때마다 직접 `clearTimeout` 을 불러야 했다**(4-10절 `ui/message.js`). 정리 함수를 쓰면 **React 가 알아서 이전 예약을 취소한다.**

> **"켰으면 꺼야 하는 것"은 모두 정리 함수에서 끈다.** 타이머, 이벤트 구독, 웹소켓 연결이 대표적이다.

**StrictMode — 개발 중에 두 번 실행되는 이유**

개발 서버에서 요청이 두 번 나가는 것을 볼 수 있다. `main.jsx` 의 `StrictMode` 가 **일부러 두 번 실행해 "정리를 제대로 하는지" 확인하는 것**이다.

```jsx
createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
```

⚠️ **운영 빌드(`npm run build`)에서는 한 번만 실행된다.** 두 번 실행돼서 문제가 생긴다면 그것은 StrictMode 의 잘못이 아니라 **정리 함수가 빠졌다는 신호**다.

**`useLayoutEffect`** 는 `useEffect` 와 거의 같지만 브라우저가 화면을 그리기 **직전**에 실행된다. 깜빡임이 보이면 안 되는 경우에만 쓴다. 먼저 `useEffect` 를 쓰고, 깜빡임이 보일 때 바꾸면 된다.

---

## 5-11. useRef 와 그 밖의 Hooks

### 개념

**useRef — 다시 그리지 않는 기억 상자**

5-6절에서 DOM 요소를 잡는 데 썼지만, `useRef` 는 원래 **"다시 그려도 사라지지 않고, 바뀌어도 다시 그리지 않는 값"**을 담는 상자다.

```jsx
const timerRef = useRef(null);

function start() { timerRef.current = setInterval(tick, 1000); }  // 화면과 무관한 값
function stop()  { clearInterval(timerRef.current); }
```

**useCallback 과 useMemo — 아껴 두기**

컴포넌트 함수는 다시 그릴 때마다 처음부터 다시 실행된다. 그 말은 **안에서 만든 함수도, 계산한 값도 매번 새로 만들어진다**는 뜻이다.

```jsx
import { useCallback, useMemo } from "react";

// students 가 바뀔 때만 다시 정렬한다
const sorted = useMemo(() => students.slice().sort(byName), [students]);

// 이 함수는 한 번 만들고 계속 같은 것을 쓴다
const handleEdit = useCallback((id) => { setEditingId(id); }, []);
```

`useMemo` 는 **계산 결과**를, `useCallback` 은 **함수 자체**를 기억해 둔다. 둘 다 두 번째 인자로 받은 목록이 바뀌었을 때만 다시 만든다. `useEffect` 의 의존성 배열과 읽는 법이 같다.

**중요한 것은 "언제 쓰지 않아도 되는가"다.**

| 상황 | 필요한가 | 이유 |
|---|---|---|
| `App.jsx` 의 `handleEdit`, `handleDelete` | **처음에는 필요 없다** | 자식이 `React.memo` 가 아니면 어차피 다시 그린다. **표를 `memo` 로 감싸는 실습 5-13 부터 필요해진다** |
| `students.map` 으로 표를 그리기 | **필요 없다** | 목록 20개 정도는 매번 그려도 빠르다 |
| 수천 건을 정렬·필터링 | `useMemo` 가 도움 | 계산 자체가 무겁다 |
| `React.memo` 로 감싼 자식에게 함수를 내려 줌 | `useCallback` 이 필요 | 함수가 매번 새로 만들어지면 `memo` 가 소용없어진다 |

⚠️ **`student_react_first` 를 실습 5-12 까지 만드는 동안에는 `useCallback` 도 `useMemo` 도 나오지 않는다. 필요가 없기 때문이다.** "느려진 것을 확인한 뒤에 넣는다"가 순서다. **미리 붙여 두는 것은 최적화가 아니라 복잡도만 늘리는 일이다.** 실습 5-13 에서 목록이 길어졌을 때를 재 보고 나서 `StudentTable` 을 `memo` 로 감싸고, **그때 비로소** `handleEdit` · `handleDelete` 에 `useCallback` 을 붙인다 — 위 표의 마지막 줄에 해당하는 경우다(5-13-11절). `useCallback` 자체가 무엇인지는 부록 E-19 의 작은 예제(5-12-6절)가 가장 짧게 보여 준다.

> **실무 감각** — `useMemo` 가 정말 필요해지는 때는 둘이다. **계산이 눈에 띄게 비쌀 때**, 그리고 **객체나 배열을 `memo` 된 자식에게 props 로 넘길 때**. 뒤쪽이 실무에서 더 자주 나온다. `style={{ color: "red" }}` 처럼 매번 새 객체를 만들어 넘기면 `useCallback` 을 아무리 붙여도 `memo` 가 무력해진다.

> `useCallback(fn, deps)` 는 `useMemo(() => fn, deps)` 와 같은 뜻이다.

**커스텀 훅 — 반복되는 로직 묶기**

`use` 로 시작하는 함수를 직접 만들 수 있다. Hooks 가 해결한 세 번째 문제(로직 재사용)의 답이다.

```jsx
function useMessage(timeout = 3000) {
    const [message, setMessage] = useState(null);

    useEffect(() => {
        if (message?.type !== "success") return;
        const timer = setTimeout(() => setMessage(null), timeout);
        return () => clearTimeout(timer);
    }, [message, timeout]);

    return [message, setMessage];
}

// 쓰는 쪽 — 어느 컴포넌트에서든 한 줄
const [message, setMessage] = useMessage();
```

🔗 **4부에서 반복되는 fetch 를 `request()` 하나로 모았던 것과 같은 생각이다.** 반복되는 것이 **"상태와 효과"**라면 커스텀 훅으로 묶는다. ⚠️ 이름이 반드시 `use` 로 시작해야 규칙 검사가 제대로 동작한다.

**앞으로 만나게 될 것들**

| 이름 | 무엇을 해결하나 |
|---|---|
| `useContext` | props 를 여러 단계 넘기지 않고 값을 공유한다 |
| `useReducer` | 상태 변화가 복잡할 때 규칙을 한곳에 모은다 |
| React Router | 주소에 따라 다른 화면 보여 주기 |
| TanStack Query | 서버 데이터 캐시 — `loading` · `error` state 를 직접 만들지 않아도 된다 |
| TypeScript | props 와 서버 응답의 모양을 미리 정해 두기 |
| Next.js | 서버에서 미리 그려 보내기 |

---
---

## 5-12. 첫 React 앱 — TodoList (`todolist_react`)

> 개념만 읽고 바로 학생 관리 시스템으로 들어가면 벅차다. **서버 통신과 모듈 분리가 함께 들어오기 때문**이다.
> 이 절은 그 사이에 놓는 다리다. 서버도 없고 파일도 적은 아주 작은 앱을 처음부터 끝까지 만든다.

### 5-12-0. 만드는 순서

| 순서 | 무엇을 | 왜 |
|---|---|---|
| E-1 | Vite 프로젝트 만들기 | 작업할 자리를 만든다 |
| E-2 ~ E-6 | **같은 화면을 클래스형과 함수형 두 방식으로** | 5-8절에서 읽은 차이를 손으로 확인한다 |
| E-7 ~ E-18 | TodoList 만들기 + `memo` | 컴포넌트 · props · state 를 한 번에 겪는다 |
| E-19 ~ E-20 | **`useCallback`** — 작은 예제 → TodoList 에 적용 | `memo` 비교 함수가 만든 버그를 고친다 |

```
npm create vite@latest
  Project name: todolist_react
  Select a framework: React
  Select a variant: JavaScript
cd todolist_react && npm install && npm run dev
```

### 5-12-1. 몸풀기 — 같은 화면을 두 방식으로

**`components/MyComponent.jsx` — 클래스 컴포넌트**

```jsx
import React, { Component } from 'react';
import './MyComponent.css';

class MyComponent extends Component {
    // 상태객체
    state = {
        value: 0,
        message: '',
        username: '',
        isValid: false,
    };

    // event handler 함수 — 화살표 함수로 선언해 this 문제를 피한다
    handleChange = (e) => {
        this.setState({
            [e.target.name]: e.target.value      // 계산된 속성명
        });
    };

    handleEnter = (e) => {
        if (e.keyCode === 13) {
            this.setState({ isValid: true, message: '' });
            this.myUsername.focus();             // html dom 직접 접근
        }
    };

    // Component 메서드 재정의
    render() {
        const { name, age } = this.props;
        const { value, message, username, isValid } = this.state;
        const { handleChange, handleEnter } = this;

        return (
            <div>
                <h2>클래스 형태의 컴포넌트</h2>
                <h3>Hello! {name} / {age}</h3>
                <p>상태변수 value = {value}</p>
                <button onClick={() => this.setState({ value: value + 1 })}>증가</button>

                <p>상태변수 message = {message}</p>
                <input name="message" value={message} onChange={handleChange}
                    onKeyDown={handleEnter} />
                <br />
                <p>상태변수 username = {username}</p>
                <input name="username" value={username} onChange={handleChange}
                    className={isValid ? 'success' : 'failure'}
                    ref={(ref) => this.myUsername = ref} />
            </div>
        );
    }
}
export default MyComponent;
```

**`components/MyComponentFunc.jsx` — 같은 화면을 함수 컴포넌트로**

```jsx
import React, { useRef, useState } from 'react';
import './MyComponent.css';

const MyComponentFunc = ({ name, age, children }) => {
    const [value, setValue] = useState(0);

    // 입력칸 두 개는 늘 함께 움직이므로 객체 하나로 묶었다 (5-9절)
    const [inputs, setInputs] = useState({ message: '', username: '' });
    const [valid, setValid] = useState(false);

    // 비구조화 할당
    const { message, username } = inputs;

    // useRef 로 직접 접근할 엘리먼트의 이름 선언
    const myUsername = useRef(null);

    const handleChange = (e) => {
        setInputs({
            ...inputs,                        // ← 이걸 빠뜨리면 다른 칸이 사라진다
            [e.target.name]: e.target.value
        });
    }

    const handleEnter = (e) => {
        if (e.keyCode === 13) {
            setValid(true);
            setInputs({ ...inputs, message: '' });
            myUsername.current.focus();       // ← ref.current
        }
    }

    return (
        <div>
            <h2>함수 형태의 컴포넌트</h2>
            <h3>Hello! {name} / {age}</h3>
            {children}
            <p>상태변수 value = {value}</p>
            <button onClick={() => setValue(value + 1)}>증가</button>
            <p>상태변수 message = {message}</p>
            <input name="message" value={message}
                onChange={handleChange} onKeyDown={handleEnter} />
            <br />
            <p>상태변수 username = {username}</p>
            <input name="username" value={username}
                onChange={handleChange}
                className={valid ? 'success' : 'failure'}
                ref={myUsername} />
        </div>
    );
};
export default MyComponentFunc;
```

**두 방식 나란히 보기**

| 하는 일 | 클래스 컴포넌트 | 함수 컴포넌트 |
|---|---|---|
| 상태 선언 | `state = { value: 0, ... }` | `const [value, setValue] = useState(0)` |
| 상태 변경 | `this.setState({ value: v + 1 })` | `setValue(value + 1)` |
| 여러 상태 | 객체 하나에 모아 둔다 | `useState` 를 여러 번 부른다 |
| props 읽기 | `const { name } = this.props` | 매개변수에서 바로 꺼낸다 |
| props 기본값 | `MyComponent.defaultProps` | 매개변수 기본값 |
| DOM 참조 | `ref={(el) => this.x = el}` (콜백 ref) | `const x = useRef(null); ref={x}` |
| 이벤트 핸들러 | 화살표 함수 또는 `bind(this)` | 그냥 함수. **`this` 문제가 없다** |
| 화면 그리기 | `render()` 메서드 | 함수가 바로 JSX 를 돌려준다 |

⚠️ **가장 중요한 차이 하나** — 클래스의 `setState` 는 넘긴 객체를 **기존 state 와 합쳐 준다.** 함수의 `set` 함수는 **합치지 않고 통째로 바꾼다.** 그래서 `MyComponentFunc` 의 `handleChange` 가 `...inputs` 로 기존 값을 먼저 펼쳐 넣는 것이다. **이것을 빠뜨리면 다른 입력칸의 값이 사라진다.**

⚠️ **자주 나는 오류**
- 입력칸에 글자를 치면 다른 칸이 비워진다 → `setInputs` 에 `...inputs` 를 빠뜨렸다
- 클래스에서 `this.handleX is not a function` → 보통 함수로 선언하고 `bind(this)` 를 안 했다. 화살표 함수로 바꾸는 것이 간단하다
- `e.keyCode` 가 undefined → `onKeyDown` 이 아니라 `onChange` 에 붙였다. **키 관련 정보는 `onKeyDown` / `onKeyUp` 에서만 온다**

**`MyComponent.css`** — `className={valid ? "success" : "failure"}` 한 줄이 **4부의 `classList.toggle` 을 대신한다.**

```css
.success { background-color: aquamarine; }
.failure { background-color: yellow; }
```

`defaultProps` 와 `propTypes` 는 클래스 쪽에서만 쓴다. `npm install prop-types` 가 필요하고, **지금은 이 역할을 TypeScript 가 대신하는 경우가 많다.**

### 5-12-2. 컴포넌트 넷 — 화면만 먼저

```
src/components/
   TodoListTemplate.jsx   바깥 틀 — 제목 · 폼 자리 · 목록 자리
   Form.jsx               입력칸과 추가 버튼
   TodoItemList.jsx       할 일 목록 — TodoItem 을 여러 개 그린다
   TodoItem.jsx           할 일 한 줄
```

⚠️ **`TodoItemList` 만 스타일 파일이 없다.** 자기가 그리는 것이 없고 `TodoItem` 을 늘어놓기만 하기 때문.

> **■ 컴포넌트를 만들 때는 언제나 같은 순서다.** ① 생김새를 JSX 로 적고 ② 스타일을 CSS 로 적고 ③ 부모에서 불러 쓴다. **값을 주고받는 일(props · state)은 생김새가 다 나온 뒤에 붙인다.**

**`TodoListTemplate.jsx` — 자리만 내어 주는 컴포넌트**

```jsx
import "./TodoListTemplate.css";

const TodoListTemplate = ({ form, children }) => {
    return (
        <main className="todo-list-template">
            <div className="title">오늘의 할 일</div>
            <section className="form-wrapper">{form}</section>
            <section className="todos-wrapper">{children}</section>
        </main>
    );
};
export default TodoListTemplate;
```

| props | 무엇이 들어오나 |
|---|---|
| `form` | 폼 자리에 그릴 것. 부모가 `form={<Form />}` 처럼 넘긴다 |
| `children` | 목록 자리에 그릴 것. **여는 태그와 닫는 태그 사이에 넣은 것**이 여기로 온다 |

⚠️ **`children` 은 React 가 미리 정해 둔 특별한 props 다.** `form` 은 그냥 우리가 지은 이름이다. **자리를 두 곳 내어 주려니 `children` 하나로는 모자라서 하나를 더 만든 것.**

⚠️ `form={<Form />}` 이 낯설 수 있다. **props 로 값이 아니라 화면 조각을 넘기는 것인데, JSX 도 값이므로 이렇게 넘길 수 있다.**

**`Form.jsx` — 값을 갖지 않는 컴포넌트**

```jsx
import "./Form.css";

const Form = ({ myTodo, myChange, myCreate, myEnter }) => {
    return (
        <div className="form">
            <input value={myTodo} onChange={myChange} onKeyDown={myEnter} />
            <div className="create-button" onClick={myCreate}>추가</div>
        </div>
    );
};
export default Form;
```

`value` 와 `onChange` 를 **함께** 준 입력을 **제어 컴포넌트**라고 한다. 화면에 보이는 값이 **언제나 부모의 state 와 같다.**

**`TodoItem.jsx` — 가장 작지만 가장 많이 쓰이는 컴포넌트**

```jsx
import "./TodoItem.css";
import { memo } from "react";

const TodoItem = ({ text, checked, id, onToggle, onRemove }) => {
    return (
        <div className="todo-item" onClick={() => onToggle(id)}>
            <div
                className="remove"
                onClick={(e) => {
                    e.stopPropagation();   // onToggle 이 실행되지 않도록 함
                    onRemove(id);
                }}
            >
                &times;
            </div>
            <div className={`todo-text ${checked && "checked"}`}>
                <div>{text}</div>
            </div>
            {checked && (<div className="check-mark">✓</div>)}
        </div>
    );
};

// checked 가 바뀔 때만 다시 그린다
export default memo(
    TodoItem,
    (prevProps, nextProps) => prevProps.checked === nextProps.checked
);
```

**눈여겨볼 곳이 셋이다.**

1. **`e.stopPropagation()`** — `×` 는 줄 **안**에 있으므로 `×` 를 누르면 줄의 `onClick` 까지 함께 실행된다. **지우려고 눌렀는데 체크까지 되어 버린다.** 이 한 줄이 위로 올라가는 것을 막는다.
2. **백틱으로 만든 `className`** — `checked` 가 참이면 `"todo-text checked"` 가 된다.
   ⚠️ **개정된 교재는 이 줄을 `${checked ? "checked" : ""}` 로 바꿨다.** `${checked && "checked"}` 는 체크가 꺼져 있을 때 **`false` 라는 글자가 그대로 들어가 `class="todo-text false"` 가 된다.** 화면은 멀쩡하지만 F12 로 열어 보면 눈에 띈다. **`&&` 는 "무엇을 그릴지" 고를 때(아래 3번)는 편하지만, 글자를 만들 때는 삼항이 안전하다.**
   실물: `todolist_react` 는 아직 `&&` 그대로, `todolist_react_router` 부터는 삼항으로 바뀌어 있다.
3. **`{checked && (...)}`** — `checked` 가 참일 때만 체크마크를 그린다 (조건부 렌더링).

**`TodoItemList.jsx` — map 이 쓰이는 자리**

```jsx
import TodoItem from "./TodoItem";
import { memo } from "react";

const TodoItemList = ({ myTodos, myToggle, myRemove }) => {
    // 배열의 값 하나하나를 화면 조각으로 바꾼다
    const todoList = myTodos.map(
        ({ id, text, checked }) => (       // ← 매개변수 자리에서 구조 분해
            <TodoItem
                id={id}
                text={text}
                checked={checked}
                onToggle={myToggle}
                onRemove={myRemove}
                key={id}                   // ← index 가 아니라 id
            />
        )
    );

    return <div>{todoList}</div>;
};

// myTodos 가 바뀔 때만 다시 그린다
export default memo(
    TodoItemList,
    (prevProps, nextProps) => prevProps.myTodos === nextProps.myTodos
);
```

### 5-12-3. 값을 어디에 둘 것인가

값이 필요한 것은 `Form` 과 `TodoItemList` **둘**이다. 그런데 이 둘은 **형제**다. **형제끼리는 값을 주고받을 수 없다.**

```
App  ← 값은 여기 둔다 { todo, todos, nextId }
  │
  ┌───────┴───────┐
  │               │
Form          TodoItemList
입력칸의 글자    할 일 배열

값은 App 에서 아래로 내려간다 (props)
알림은 자식이 받은 함수를 불러 위로 올린다
Form 과 TodoItemList 는 서로 직접 이야기하지 않는다
```

**추가 버튼을 눌렀을 때 입력칸의 글자가 목록에 들어가야 하는데, 그 둘을 모두 아는 것은 `App` 뿐이다.** 그래서 값을 공통 부모로 올린다 — **상태 끌어올리기**(5-4절).

⚠️ 자식끼리 직접 값을 주고받는 방법이 **아예 없지는 않다.** `ref` 로 형제를 붙잡을 수는 있다. 하지만 **React 에서는 하지 않는 방식**이다. 누가 값을 바꿨는지 따라가기 어려워지기 때문. **자식은 부모를 통해서만 이야기한다** — 이것이 단방향 데이터 흐름이다.

### 5-12-4. `AppTodo.jsx` — 실물 완성 코드

⚠️ **파일 이름이 바뀌었다.** E-19 에서 `App.jsx` 에 다른 예제(`UseCallbackApp`)를 잠깐 그려 보느라, TodoList 본체는 **`AppTodo.jsx`** 로 옮기고 `App.jsx` 는 둘 중 하나를 골라 그리는 자리가 됐다. 안의 함수 이름은 여전히 `App` 이다(`export default App`).

```jsx
// src/App.jsx — 실물. 지금은 UseCallbackApp 을 그리는 중
import AppTodo from './AppTodo';
import UseCallbackApp from './components/UseCallbackApp';

const App = () => {
    return (
        <div>
            {/* <AppTodo /> */}
            <UseCallbackApp />
        </div>
    );
};
export default App;
```

```jsx
// src/main.jsx — 실물. E-19 확인 동안 StrictMode 를 꺼 둠
createRoot(document.getElementById('root')).render(
  // <StrictMode>
    <App />
  // </StrictMode>,
)
```

**`src/AppTodo.jsx` — TodoList 본체**

```jsx
import { useState } from 'react';
import './App.css'
import Form from './components/Form'
import TodoItemList from './components/TodoItemList'
import TodoListTemplate from './components/TodoListTemplate'

// E-18 성능 실험용 — 500개짜리 초기 목록
const initialTodos = new Array(500).fill(0).map(
    (item, idx) => ({ id: idx, text: `일정 ${idx}`, checked: true })
);

function App() {
  // 입력칸에 지금 들어 있는 글자
  const [todo, setTodo] = useState("");

  // 할 일 목록
  // const [todos, setTodos] = useState([
  //   { id: 0, text: "리액트 소개", checked: false },
  //   { id: 1, text: "리액트 구조", checked: true },
  //   { id: 2, text: "리액트 사용", checked: false },
  // ]);
  const [todos, setTodos] = useState(initialTodos);

  // 다음에 만들 할 일의 번호. 0,1,2 를 이미 썼으므로 3부터.
  const [nextId, setNextId] = useState(3);

  const handleChange = (e) => {
    setTodo(e.target.value);          // 입력칸의 다음 값
  };

  const handleCreate = () => {
    const newTodo = { id: nextId, text: todo, checked: false };

    // 기존 배열을 펼치고 뒤에 하나를 더한 "새 배열" 을 넣는다
    setTodos([...todos, newTodo]);
    setTodo("");                      // 입력칸 비우기
    setNextId(nextId + 1);            // 다음 번호 준비
  };

  const handleEnter = (e) => {
    // 눌려진 키가 Enter 이면 handleCreate 호출
    // if (e.keyCode === 13) {
    if (e.key === "Enter") {          // ← 최신 방식으로 바꿔 둔 것
      handleCreate();
    }
  };

  const handleToggle = (id) => {
    setTodos(
      todos.map((todo) =>
        todo.id === id ? { ...todo, checked: !todo.checked } : todo
      )
    );
  };

  const handleRemove = (id) => {
    setTodos(
      todos.filter((todo) => todo.id !== id)
    );
  };

  return (
    <>
      <TodoListTemplate form={
        <Form myTodo={todo}
          myChange={handleChange}
          myCreate={handleCreate}
          myEnter={handleEnter}
        />
      }>
        <TodoItemList myTodos={todos}
          myToggle={handleToggle}
          myRemove={handleRemove}
        />
      </TodoListTemplate>
    </>
  )
}
export default App
```

**세 핸들러가 전부 "새 배열을 만든다"**

| 하는 일 | 쓰는 메서드 | 왜 |
|---|---|---|
| **추가** | `setTodos([...todos, newTodo])` | `todos.push()` 로 기존 배열을 고치면 React 는 **같은 배열을 계속 보고 있어** 바뀐 줄 모른다 |
| **체크 토글** | `todos.map(t => t.id === id ? {...t, checked: !t.checked} : t)` | 고칠 것이 객체 하나뿐이어도 **배열 전체를 새로 만든다** |
| **삭제** | `todos.filter(t => t.id !== id)` | `filter` 는 원래 새 배열을 돌려주는 메서드다 |

⚠️ **"고칠 것이 배열 안의 객체 하나뿐인데 배열 전체를 새로 만드는 것이 낭비처럼 보인다"** — 하지만 **React 는 "배열이 바뀌었는가"를 `===` 로 판단한다.** 안을 고쳐 봐야 같은 배열이므로 바뀐 줄 모른다.

⚠️ **`nextId` 를 굳이 state 로 둔 이유** — 화면에 보이지 않는 값이니 그냥 변수로 두면 될 것 같지만, **함수 컴포넌트는 다시 그릴 때마다 함수 전체가 다시 실행된다.** 보통 변수로 두면 그때마다 3 으로 되돌아간다. `useRef` 로 두는 방법도 있다.

⚠️ **`e.keyCode` 는 오래된 방식이다.** 교재는 `e.keyCode === 13` 으로 썼지만 **실물 코드는 `e.key === "Enter"` 로 고쳐 두었다.** 읽기도 쉽고 `keyCode` 는 표준에서 물러난 속성이다.

### 5-12-5. 컴포넌트 최적화 — memo

지금 코드에는 낭비가 하나 있다. **입력칸에 글자 하나를 칠 때마다 `App` 이 다시 그려지고, 그러면 목록의 모든 줄이 함께 다시 그려진다.** 할 일이 셋일 때는 못 느끼지만 **500개라면 입력이 버벅거린다.**

그래서 `App.jsx` 에 `initialTodos` 로 500개를 만들어 두고 **직접 느껴 보게** 한 것이다. React DevTools 의 Profiler 로 보면 글자 하나마다 `TodoItem` 500개가 전부 다시 그려지고 있다.

고치는 방법은 **"받은 props 가 그대로면 다시 그리지 마라"**고 알려 주는 것. 함수 컴포넌트에서는 `memo` 가 그 일을 한다.

```jsx
import { memo } from "react";
// ... 컴포넌트 본문은 그대로 ...

export default memo(
    TodoItem,
    (prevProps, nextProps) => prevProps.checked === nextProps.checked
);
```

**`memo` 의 두 번째 인자는 "이전 props 와 다음 props 가 같은가"를 판단하는 함수다. 참을 돌려주면 다시 그리지 않는다.**

| | 클래스형 | 함수형 |
|---|---|---|
| 최적화 수단 | `shouldComponentUpdate` | `memo` |
| 적는 자리 | 클래스 안의 메서드 | **`export` 할 때 감싼다** |
| **참을 돌려주면** | **다시 그린다** | **다시 그리지 않는다** ← 반대! |
| 판단 기준 | `this.props` 와 `nextProps` | `prevProps` 와 `nextProps` |

⚠️ **`memo` 는 두 번째 인자를 주지 않아도 된다.** 그러면 props 를 하나씩 `===` 로 견주어 본다(얕은 비교). **여기서 굳이 비교 함수를 적은 것은, 그렇게 하지 않으면 `memo` 가 전혀 듣지 않기 때문이다.** 왜 그런지는 E-19(5-12-6절)에서, 이렇게 적었을 때 무엇이 망가지는지는 E-20(5-12-7절)에서 본다. **지금 적은 이 세 줄은 E-20 에서 도로 지우게 된다.**

⚠️ **최적화는 느려진 뒤에 하는 것이다.** 처음부터 모든 컴포넌트를 `memo` 로 감싸면 코드만 지저분해지고 **비교하는 비용이 더 들 수도 있다.**

> **■ 확인이 끝나면 `initialTodos` 를 지우고 원래 세 개로 되돌린다.** (실물 `AppTodo.jsx` 는 아직 500개짜리 상태로 남아 있다.)

### 5-12-6. E-19 — `useCallback` 이 무엇을 하나 (작은 예제)

**개념**

E-18 에서 `memo` 에 비교 함수를 직접 적었다. 그렇게 하지 않으면 `memo` 가 듣지 않기 때문인데, 왜 그런지 알려면 먼저 짚고 갈 것이 있다. **"컴포넌트가 다시 그려질 때 그 안에 적은 함수는 어떻게 되는가."** TodoList 로 바로 들어가면 볼 것이 많아 헷갈리므로 버튼 두 개짜리 예제로 먼저 본다.

**코드 위치** — `todolist_react/src/components/UseCallbackApp.jsx` (실물)

```jsx
import { useCallback, useState } from "react";

// Set 은 같은 값을 두 번 넣어도 하나로 친다.
// 컴포넌트 바깥에 두었으므로 다시 그려도 비워지지 않는다.
const set = new Set();

function UseCallbackApp() {
    const [count, setCount] = useState(0);

    const increase = () => setCount(count + 1);

    // (A) 그냥 적은 함수
    //const printLog = () => console.log("re-execute");

    // (B) useCallback 으로 감싼 함수 — (A) 를 지우고 아래 두 줄을 살린다.
    //     맨 위 import 에 useCallback 도 함께 더해야 한다.
    const printLog = useCallback(() => console.log("re-execute"), []);

    set.add(printLog);                  // 지금의 printLog 를 넣어 본다
    console.log("set.size :", set.size);
    console.log("set content :", set);  // ← 실물에만 있는 줄

    return (
        <div>
            <p>{count}</p>
            <button onClick={increase}>Add</button>
            <button onClick={printLog}>Print</button>
        </div>
    );
}

export default UseCallbackApp;
```

**해설 — Set 으로 "같은 함수인가"를 잰다**

그릴 때마다 `printLog` 를 Set 에 넣어 본다. **크기가 늘면 "매번 새 함수", 그대로면 "같은 함수"**다.

| | `printLog` 를 어떻게 적었나 | Add 를 누를 때마다 `set.size` |
|---|---|---|
| (A) | `const printLog = () => …` | 1 → 2 → 3 → 4 → 5 |
| (B) | `useCallback(() => …, [])` | **1 → 1 → 1 → 1 → 1** |

(A) 는 누를 때마다 새 함수가 하나씩 쌓인다. 하는 일은 언제나 같은데 몸만 새로 만들어지는 셈. (B) 는 처음 만든 하나를 계속 쓴다. **`useCallback` 이 하는 일은 이것 하나다 — 같은 함수를 계속 쓰게 해 주는 것.**

- ⚠️ **`const set` 을 컴포넌트 바깥에 둔 이유** — 안에 두면 다시 그릴 때마다 새 Set 이 만들어져 크기가 늘 1 이다. 측정 도구가 측정 대상과 함께 초기화되면 안 된다.
- ⚠️ **"함수 하나 새로 만드는 게 뭐가 그리 비싼가?"** — 비싸지 않다. **거의 공짜다.** 문제는 그 함수를 **`memo` 로 감싼 자식에게 내려 줄 때** 생긴다. `memo` 는 props 를 `===` 로 견주는데, 새로 만든 함수는 하는 일이 같아도 언제나 "다르다"가 나온다. **그래서 `useCallback` 은 혼자 쓰는 성능 도구가 아니라 `memo` 가 제대로 판단하게 해 주는 짝이다.**
- ⚠️ **StrictMode 가 켜져 있으면** 개발 모드에서 컴포넌트를 두 번 실행해 `set.size` 가 2씩 는다(2 → 4 → 6…). **확인하는 동안만 잠깐 꺼 둔다** — 실물 `main.jsx` 에서 `<StrictMode>` 가 주석 처리된 이유가 이것. `useCallback` 을 붙인 뒤에는 StrictMode 를 켜 두어도 1 에 머문다.
- 확인하는 동안만 `App.jsx` 에서 `<UseCallbackApp />` 을 그리고, **끝나면 TodoList 로 되돌린다.**

**감쌀 수 없는 함수도 있다 — 굳어 버린 값 (stale closure)**

같은 파일의 `increase` 를 감싸 보면 금방 알 수 있다. `printLog` 와 달리 이 함수는 **바깥의 `count` 를 읽는다.**

| `increase` 를 이렇게 적으면 | Add 를 네 번 눌렀을 때 화면의 count |
|---|---|
| `const increase = () => setCount(count + 1)` | 4 |
| `useCallback(() => setCount(count + 1), [])` | **1 — 여기서 멈춘다** |
| `useCallback(() => setCount((c) => c + 1), [])` | 4 |

**가운데가 왜 1 에서 멈추는가.** `[]` 는 "다시 만들지 마라"는 뜻인데, 그 함수는 **처음 만들어질 때의 `count` 값 0 을 기억한 채 굳는다.** 그래서 몇 번을 눌러도 `setCount(0 + 1)` 이다.

맨 아래처럼 **`setCount` 에 값 대신 함수를 넘기면 React 가 최신 값을 `c` 로 넣어 준다.** 이것을 **함수형 업데이트**라고 한다. 이렇게 하면 `count` 를 읽지 않게 되므로 `[]` 로 두어도 안전하다.

🔗 5-9절 "setter 에 함수를 넘기는 형태"가 여기서 **꼭 필요한 이유**를 얻는다. E-20 에서 TodoList 의 `handleToggle` · `handleRemove` 를 이 방법으로 고친다.

> **■ 확인하기** — Add 를 누를 때마다 `set.size` 가 1씩 는다 → (B) 로 바꾸면 1 에 머문다 → `increase` 를 `useCallback(..., [])` 로 감싸면 count 가 1 에서 멈춘다 → 함수형 업데이트로 바꾸면 다시 제대로 올라간다 → 확인이 끝나면 `App.jsx` 를 TodoList 로 되돌린다.

### 5-12-7. E-20 — 비교 함수를 걷어낸다 (`useCallback` 적용)

**개념 — 성능이 아니라 정확성의 문제다**

E-19 에서 본 것을 TodoList 에 적용한다. 비교 함수를 걷어내고 `useCallback` 으로 함수를 고정한다. **그래야 하는 이유는 성능이 아니라 정확성이다. 지금 코드에는 버그가 하나 들어 있다.**

**먼저 버그를 확인한다** (할 일은 처음 상태인 세 개로 두고 시작)

| | 조작 | 이렇게 되어야 | 그런데 |
|---|---|---|---|
| 1 | 처음 화면 | 3개 | 3개 |
| 2 | "새 할 일" 을 적고 추가 | 4개 | 4개 |
| 3 | 가운데 "리액트 구조" 의 × 를 누른다 | 3개 | **2개 — 방금 추가한 것이 함께 사라진다** |

지운 적이 없는 "새 할 일"이 함께 없어진다. **삭제 코드에는 잘못이 없다.** E-17 에서 적은 `filter` 한 줄은 지금도 맞다. **문제는 그 함수가 "언제 만들어진 것인가"에 있다.**

**왜 그런가 — 낡은 함수가 남는다**

App 은 다시 그려질 때마다 `handleToggle` 과 `handleRemove` 를 새로 만든다. 함수 안에서 `todos` 를 읽고 있으므로 **만들어진 시점의 `todos` 를 기억한 채 굳어진다.**

| | 무슨 일이 | 그래서 |
|---|---|---|
| ① | "새 할 일" 을 추가한다 | `todos` 가 4개짜리 새 배열이 된다 |
| ② | App 이 다시 그려진다 | `handleRemove` 가 새로 만들어진다. 이것은 4개짜리 배열을 기억한다 |
| ③ | TodoItemList 가 다시 그려진다 | 자식들에게 새 `handleRemove` 를 내려 준다 |
| ④ | **TodoItem 이 그것을 받지 않는다** | 비교 함수가 `checked` 만 보는데 그 줄의 `checked` 는 그대로다. "같다"고 판단해 다시 그리지 않는다 |
| ⑤ | 옛 함수가 그대로 남는다 | 그 줄은 **3개짜리 배열을 기억하는 옛 `handleRemove`** 를 들고 있다 |
| ⑥ | × 를 누른다 | 3개짜리 배열에서 하나를 뺀 것이 새 목록이 된다 → **"새 할 일" 증발** |

⚠️ **원인은 비교 함수가 "`checked` 만 보면 된다"고 단정한 것이다.** 화면에 보이는 글자만 생각하면 맞는 말이다. **그런데 props 에는 보이지 않는 것도 들어 있다 — `onToggle` 과 `onRemove` 라는 함수 둘.** 이 둘이 바뀌었는데도 "같다"고 답해 버린 것이다.

**고치는 순서 — 먼저 함수를 고정한다, 그 다음 비교 함수를 지운다**

비교 함수를 그냥 지우면 `memo` 는 아무 일도 하지 않게 된다(매번 새 함수 때문에 얕은 비교가 언제나 "달라졌다"). **그래서 순서가 있다.** 의존성을 `[]` 로 두려면 함수 안에서 `todos` 를 읽지 말아야 하고, 그러려면 **`setTodos` 에 값 대신 함수를 넘기면 된다**(함수형 업데이트).

**코드 위치** — 교재는 `todolist_react/src/App.jsx` 를 고치라고 하지만, ⚠️ **실물 `todolist_react` 에는 E-20 이 아직 적용되지 않았다**(`AppTodo.jsx` 에 `useCallback` 없음, 비교 함수 그대로). **E-20 이 반영된 실물은 6부 준비 운동의 `todolist_react_router/src/pages/TodoPage.jsx`** 다(6-5절).

```jsx
// todolist_react_router/src/pages/TodoPage.jsx — 실물 (E-20 적용)
import { useCallback, useState } from 'react';

// 옛 코드는 주석으로 남겨 두었다
// const handleToggle = (id) => {
//   setTodos(todos.map((todo) => todo.id === id ? { ...todo, checked: !todo.checked } : todo));
// };
// const handleRemove = (id) => {
//   setTodos(todos.filter((todo) => todo.id !== id));
// };

/* setTodos 에 값 대신 함수를 넘기면 React 가 최신 목록을
   prev 로 넣어 준다. todos 를 읽지 않으므로 의존성이 [] 가 되고,
   그래서 이 함수는 앱이 사는 동안 딱 한 번만 만들어진다. */
const handleToggle = useCallback((id) => {
  setTodos((prev) =>
    prev.map((todo) =>
      todo.id === id ? { ...todo, checked: !todo.checked } : todo
    )
  );
}, []);

const handleRemove = useCallback((id) => {
  setTodos((prev) => prev.filter((todo) => todo.id !== id));
}, []);
```

```jsx
// components/TodoItem.jsx — 비교 함수 세 줄을 지우고 이렇게 (실물: todolist_react_router)
/* 받은 props 가 그대로면 다시 그리지 않는다.
   비교 함수를 주지 않았으므로 props 다섯 개를 하나씩 === 로 견준다.
   onToggle 과 onRemove 가 useCallback 으로 고정돼 있어 이것만으로 충분하다. */
export default memo(TodoItem);

// components/TodoItemList.jsx 도 같다
// 받은 props 가 그대로면 다시 그리지 않는다. 비교 함수는 필요 없다.
export default memo(TodoItemList);
```

**무엇이 좋아졌나 — 성능은 그대로, 달라진 것은 정확성**

| 조작 | E-19 까지 (비교 함수) | E-20 (`useCallback`) |
|---|---|---|
| 입력칸에 글자 하나 | TodoItem 0번 다시 그림 | 0번 — 같다 |
| 체크 뒤집기 | 누른 줄만 1번 | 누른 줄만 1번 — 같다 |
| 추가한 뒤 다른 줄 삭제 | **추가한 것이 함께 사라진다** | **정상** |
| text 가 바뀌는 기능을 붙이면 | **글자가 안 바뀐다** | **정상** |

**어디에 쓰고 어디에 쓰지 않나 — 기준은 하나: "`memo` 로 감싼 자식에게 내려 주는 함수인가"**

| 무엇 | 감싸나 | 까닭 |
|---|---|---|
| `handleToggle` · `handleRemove` | **감싼다** | `memo` 된 TodoItemList · TodoItem 에게 내려 준다 |
| `handleChange` · `handleCreate` · `handleEnter` | 감싸지 않는다 | 받는 `Form` 이 `memo` 가 아니다. 감싸도 달라지는 것이 없다 |
| TodoItemList 의 `todoList` (map 결과) | `useMemo` 를 쓰지 않는다 | 이 컴포넌트는 `myTodos` 가 바뀔 때만 다시 그려진다. 그때는 map 을 다시 해야 하므로 아낄 것이 없다 |
| `form={<Form ... />}` | 지금은 쓰지 않는다 | `TodoListTemplate` 를 `memo` 로 감쌀 때 비로소 `useMemo` 가 필요해진다 |

- ⚠️ **`handleCreate` 는 여전히 `setTodos([...todos, newTodo])` 로 `todos` 를 읽는다.** 함수형 업데이트로 바꿔도 되지만 그대로 두었다. **`useCallback` 으로 감싸지 않는 함수라 바꿔서 얻는 것이 없기 때문.** 필요한 곳에만 쓰는 것이 읽기에도 낫다.
- ⚠️ **`useMemo` 는 이 예제에 넣을 자리가 없다.** 정말 필요해지는 때는 계산이 비쌀 때, 그리고 객체·배열을 `memo` 된 자식에게 넘길 때(5-11절).

> **■ 확인하기** — 할 일을 추가한 뒤 다른 줄을 지워도 추가한 것이 남아 있다 / `TodoItem.jsx` 와 `TodoItemList.jsx` 에 비교 함수가 없다 / 500개로 늘려도 입력이 부드럽다 / Profiler 에서 글자를 칠 때 TodoItem 이 다시 그려지지 않는다 / `useCallback` 이 두 번 나온다

**■ 자주 나는 오류**

| 증상 | 원인 |
|---|---|
| `useCallback` 을 붙였는데도 그대로다 | 의존성에 `[todos]` 를 적었다. 할 일이 바뀔 때마다 함수가 새로 만들어져 붙이나 마나 |
| 목록이 옛 값으로 되돌아간다 | `setTodos(todos.filter(...))` 처럼 state 를 직접 읽는 자리가 남아 있다. `setTodos((prev) => ...)` 로 |
| 비교 함수를 지웠더니 다시 느려졌다 | `useCallback` 을 **먼저** 붙여야 한다. 순서가 바뀌면 `memo` 가 듣지 않는다 |

### 5-12-8. 여기서 겪은 것

| 개념 | 이 앱의 어디에서 | 문서의 어디 |
|---|---|---|
| 컴포넌트로 나누기 | 컴포넌트 넷 | 5-2절 |
| JSX 와 조건부 렌더링 | `TodoItem` 의 `{checked && ...}` | 5-3절 |
| props 로 값 내려 주기 | App → Form · TodoItemList | 5-4절 |
| state 와 끌어올리기 | App 의 값 셋 | 5-4절 |
| 이벤트 처리 | `handleChange` · `handleCreate` | 5-5절 |
| map 과 key | `TodoItemList` | 5-7절 |
| useState | App 전체 | 5-9절 |
| **불변성 — 새 배열 · 새 객체** | 핸들러 세 개 전부 | 5-9절 |
| memo 로 최적화 | `TodoItem`, `TodoItemList` (E-18 · E-20) | 5-11절, 5-12-5절 |
| **useCallback 으로 함수 고정** | `UseCallbackApp` (E-19), `handleToggle` · `handleRemove` (E-20) | 5-12-6 · 5-12-7절 |
| **함수형 업데이트** | `setTodos((prev) => ...)` | 5-12-7절 |

> **다음 5부 실습에서도 똑같이 흘러간다.** `App` 이 값을 갖고, 자식이 알려 오면 `App` 이 처리한다. 거기서도 **실습 5-13 에 가서야** `memo` 와 `useCallback` 이 나온다. E-20 에서 본 대로 **넣어야 할 이유가 생긴 뒤에 넣는 것이 순서다.**

---

## 5-13. 실습 5-1 ~ 5-13 — 학생 관리 시스템을 React 로

> **기능은 하나도 바뀌지 않는다. 바뀌는 것은 단 하나, "화면을 바꾸는 방법"이다.**

### 5-13-0. 4부와 무엇이 달라지는가

| 주제 | 4부 (`student_ecma`) | 5부 (`student_react_first`) |
|---|---|---|
| 화면 그리기 | `createElement` + `appendChild` | **JSX** — HTML 처럼 써서 돌려준다 |
| 화면 갱신 | `renderStudentTable(students)` | **`setStudents(students)`** |
| 화면의 값 | DOM (`input.value`, `textContent`) | **state (`useState`)** |
| 폼 입력값 | `FormData` 로 긁어모음 | **제어 컴포넌트** (`value` + `onChange`) |
| 목록 첫 로드 | 파일 맨 아래 `loadStudents()` | **`useEffect(..., [])`** |
| 표의 버튼 | `data-action` + 이벤트 위임 | **`onClick={() => onEdit(id)}`** |
| 보이기 / 숨기기 | `style.display = "none"` | **조건부 렌더링** `{조건 && ...}` |
| 타이머 정리 | `messageTimer` + `clearTimeout` | **`useEffect` 의 정리 함수** |
| 화면 파일 | `ui/*.js` 3개 (DOM 조작) | `components/*.jsx` 3개 |
| **그대로 쓰는 것** | — | **`config` · `studentApi` · `validation`** |

### 5-13-1. 실습 5-1 — 무엇을 그대로 옮길 수 있는가 ✅

**기준은 하나다. 4부의 `src` 폴더를 열어 파일마다 `document` 라는 낱말이 있는지 찾아보라.**

| 4부 파일 | `document` 를 쓰는가 | 5부에서 |
|---|---|---|
| `config.js` | 안 씀 | ✅ **그대로 복사 — 고칠 것 없음** |
| `api/studentApi.js` | 안 씀 | ✅ **그대로 복사 — 고칠 것 없음** |
| `lib/validation.js` | 안 씀 | ✅ **그대로 복사 — 고칠 것 없음** |
| `ui/message.js` | 씀 | 컴포넌트로 다시 쓴다 (실습 5-7) |
| `ui/studentForm.js` | 씀 | 컴포넌트로 다시 쓴다 (실습 5-6) |
| `ui/studentTable.js` | 씀 | 컴포넌트로 다시 쓴다 (실습 5-5) |
| `main.js` | 씀 | `App.jsx` 로 다시 쓴다 (실습 5-10) |

> **■ 값만 다루는 세 파일은 React 든 Vue 든 상관없이 그대로 쓴다.**
> **프레임워크를 바꿔도 살아남는 코드가 어떤 것인지 이 표가 보여 준다.** 4부에서 "화면을 건드리는 코드"와 "값만 다루는 코드"를 나눠 둔 것이 여기서 값을 한다.

`.env.development` · `.env.production` · `style.css` 도 그대로 옮긴다. **`config.js` 가 `import.meta.env` 로 주소를 읽는 방식은 Vite 프로젝트라면 React 여도 똑같기 때문**이다.

```
npm create vite@latest
  Project name: student_react_first
  Select a framework: React
  Select a variant: JavaScript
  Which linter to use? ESLint
```

### 5-13-2. 실습 5-2 — 첫 화면 ✅

**4부의 `index.html` 은 화면 전체가 들어 있었다. 5부는 빈 상자 하나다.**

```html
<!doctype html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>학생 관리 시스템 (React + Vite)</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

**화면이 통째로 사라졌다. 이제 화면은 JavaScript 가 만들어 넣는다.**

```jsx
// src/main.jsx — 실물 코드
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```

⚠️ **이 파일이 5부에서 `document` 를 쓰는 유일한 곳이다.** "root 자리에 App 을 그려라" 한 번만 말하면, 그 뒤로는 React 가 알아서 한다.

### 5-13-3. 실습 5-3 · 5-4 — state 여섯 개와 useEffect ✅

**`student_react_first/src/App.jsx` — 실습 5-4 를 마친 시점의 모습** (1.3 에서 실물로 확인한 것. 최종 완성본은 5-13-11절)

```jsx
import { useEffect, useState } from 'react';
import { fetchStudents } from './api/studentApi';
import './style.css'

function App() {
  // 상태 변수 선언
  const [students, setStudents] = useState([]);          // 표에 그릴 학생 목록
  //const [form, setForm] = useState(EMPTY_FORM);        // 입력칸 여섯 개의 값
  const [editingId, setEditingId] = useState(null);      // null 이면 등록 모드
  const [loading, setLoading] = useState(false);         // "로딩 중..." 을 보일까
  const [listError, setListError] = useState(null);      // 표 자리에 낼 오류 문구
  // 메시지는 { text: "문구", type: "error" 또는 "success" } 모양으로 담는다.
  const [message, setMessage] = useState(null);          // 성공,오류 메시지

  // 수정 모드인지는 editingId 로 알 수 있으므로 따로 state 를 두지 않는다.
  const isEditing = editingId !== null;

  async function loadStudents() {
    setLoading(true);
    setListError(null);

    try {
      const data = await fetchStudents();
      console.log("Fetched students:", data);

      // 4부에서는 renderStudentTable(data) 를 불렀다.
      // 여기서는 값만 바꾸면 React 가 표를 다시 그린다.
      setStudents(data);
    } catch (error) {
      console.error("Error:", error);
      setMessage({ text: error.message, type: "error" });
      setListError("오류: 데이터를 불러올 수 없습니다.");
    } finally {
      // 성공하든 실패하든 로딩 표시는 반드시 끈다.
      setLoading(false);
    }
  }

  useEffect(() => {
    // 아래 주석은 ESLint 에게 "이 경고는 알고 있다"고 알려 주는 줄이다.
    // eslint-disable-next-line react-hooks/set-state-in-effect -- 처음 한 번 목록을 불러오는 것은 의도된 동작입니다
    loadStudents();
  },[]);

  return (
    <>
      <h1>학생 관리 시스템</h1>
    </>
  )
}
export default App
```

**이 파일에서 읽어야 할 것**

| state | 4부에서는 어디에 있었나 | 무엇을 정하는가 |
|---|---|---|
| `students` | DOM (`tbody` 의 `tr` 들) | 표에 무엇이 보이는가 |
| `form` | DOM (`input.value` 여섯 개) | 입력칸에 무엇이 들어 있는가 |
| `editingId` | **전역 변수 `editingStudentId`** | 등록 모드인가 수정 모드인가 |
| `loading` | DOM (`loadingMessage` 의 style) | "로딩 중..." 이 보이는가 |
| `listError` | DOM (표 안의 `error-row`) | 표 자리에 오류를 보일 것인가 |
| `message` | DOM (`formError` 의 글자와 색) | 폼 아래에 무엇을 보일 것인가 |

> **이 여섯 줄만 읽으면 화면이 어떤 모습인지 알 수 있다.** 4부에서 DOM 과 전역 변수에 흩어져 있던 것이 한자리에 모인 것이다.

- **`try / catch / finally` 구조는 4부와 글자 하나 다르지 않다.** 달라진 것은 `renderStudentTable(students)` 가 `setStudents(data)` 로 바뀐 것뿐.
- `form` 이 주석 처리된 것은 **실습 5-6 에서 살린다**는 뜻. 단계별로 만드는 중이라 그렇다.
- ⚠️ **`useEffect` 의 두 번째 인자 `[]` 가 핵심이다.** 이것이 없으면 그릴 때마다 실행되고, 실행하면 state 가 바뀌어 또 그리게 되어 **서버를 끝없이 부른다.**
- ⚠️ `eslint-disable-next-line` 주석은 **ESLint 규칙을 한 줄만 꺼 두는 것**이다. "이 경고는 알고 있다"고 표시하는 관용구. **이유를 `--` 뒤에 적어 두는 것이 예의다.**
- ⚠️ 실습 5-8 까지는 `npm run lint` 가 `setEditingId` 와 `setMessage` 를 아직 안 쓴다고 잡는다. **단계별로 만드는 중이라 그런 것이니 그냥 두면 된다.**

⚠️ **개발 모드에서 요청이 두 번 나간다** → `StrictMode` 가 일부러 두 번 실행해 문제를 찾아 주는 것. 운영 빌드에서는 한 번이다.

### 5-13-4. 실습 5-5 — 표를 컴포넌트로 ✅

**`components/StudentTable.jsx`** — 실물 대조 완료. 본문은 교재와 같고, 실물에는 ① 파일 맨 위 설명 주석, ② `import { memo } from "react";`, ③ 맨 아래 `export default memo(StudentTable);` (실습 5-13 에서 더한 것)이 붙어 있다.

```jsx
/* 실물 파일 머리 주석 — 4부 ui/studentTable.js 에서 사라진 것들
     createElement / appendChild   →  JSX 로 바로 쓴다
     innerHTML = ""                →  students 가 바뀌면 React 가 다시 그린다
     addCell / createActionButton  →  필요 없다
     data-action + 이벤트 위임     →  onClick 에 함수를 직접 넘긴다

   4부에서 이벤트 위임을 쓴 이유는 "행을 다시 그리면 이벤트가
   떨어져 나가기 때문" 이었습니다. React 는 그릴 때마다 onClick 을
   다시 붙여 주므로 그 문제가 없습니다. */
```

```jsx
// 표의 열 개수. colSpan 에 쓴다.
const COLUMN_COUNT = 7;

function StudentTable({ students, loading, error, onEdit, onDelete }) {
    /* tbody 안에 무엇을 그릴지 세 경우로 나눠서 정한다.
       JSX 안에 && 와 ? : 를 이어 쓰면 읽기 어려우므로,
       먼저 rows 에 담아 두고 아래 표 안에 끼워 넣는다. */
    let rows;

    if (error) {
        // (1) 목록을 못 불러왔다
        rows = (
            <tr><td colSpan={COLUMN_COUNT} className="error-row">{error}</td></tr>
        );
    } else if (students.length === 0 && !loading) {
        // (2) 목록이 비었다. 불러오는 중일 때는 안내를 내지 않는다.
        //     그래야 화면이 잠깐 깜빡이지 않는다.
        rows = (
            <tr><td colSpan={COLUMN_COUNT} className="empty-row">등록된 학생이 없습니다.</td></tr>
        );
    } else {
        // (3) 학생 한 명을 행 하나로 그린다.
        rows = students.map((student) => (
            <tr key={student.id}>
                <td>{student.name}</td>
                <td>{student.studentNumber}</td>
                <td>{student.detail?.address ?? "-"}</td>
                <td>{student.detail?.phoneNumber ?? "-"}</td>
                <td>{student.detail?.email ?? "-"}</td>
                <td>{student.detail?.dateOfBirth ?? "-"}</td>
                <td>
                    {/* data-id 도 Number(id) 도 필요 없다. id 를 그대로 넘긴다. */}
                    <button type="button" className="edit-btn"
                        onClick={() => onEdit(student.id)}>수정</button>
                    <button type="button" className="delete-btn"
                        onClick={() => onDelete(student.id)}>삭제</button>
                </td>
            </tr>
        ));
    }

    return (
        <div className="table-container">
            <h2>학생 목록</h2>
            {/* 4부 setLoading() 대신 조건부 렌더링을 쓴다. */}
            {loading && <div className="loading">로딩 중...</div>}
            <table>
                <thead>
                    <tr>
                        <th>이름</th><th>학번</th><th>주소</th><th>전화번호</th>
                        <th>이메일</th><th>생년월일</th><th>액션</th>
                    </tr>
                </thead>
                <tbody>{rows}</tbody>
            </table>
        </div>
    );
}
export default StudentTable;
```

**해설**
- **`state` 가 하나도 없다.** 받은 것을 그리기만 하는 **프레젠테이셔널 컴포넌트**다.
- `detail?.address ?? "-"` 는 4부에서 쓰던 **그대로**다. 옵셔널 체이닝과 널 병합은 React 문법이 아니라 ECMAScript 문법이므로 JSX 안에서도 똑같이 쓴다.
- ⚠️ **`students.length === 0 && !loading`** — 불러오는 중일 때는 "등록된 학생이 없습니다"를 내지 않는다. **그래야 화면이 잠깐 깜빡이지 않는다.** 작지만 실전 감각이 드러나는 조건.
- ⚠️ **`colspan` 이 아니라 `colSpan`** 이다. `class` 도 `className`.
- 🔗 4부 `ui/studentTable.js` 의 `createMessageRow` / `addCell` / `createActionButton` **세 헬퍼 함수가 통째로 사라졌다.**

**컴포넌트를 만든 단계에서 바로 App 에 붙여 확인한다**

```jsx
// App.jsx — 아직 handleEdit / handleDelete 는 빈 함수로 둔다
function handleEdit() { }
function handleDelete() { }

return (
  <>
    <h1>학생 관리 시스템</h1>
    <StudentTable
      students={students} loading={loading} error={listError}
      onEdit={handleEdit} onDelete={handleDelete}
    />
  </>
);
```

⚠️ **빈 함수에 매개변수를 적지 않은 까닭** — 쓰지도 않을 매개변수를 두면 ESLint 가 `'studentId' is defined but never used` 라고 잡는다. **`StudentTable` 이 `onEdit(student.id)` 로 불러도, 받지 않은 인자는 JavaScript 가 그냥 버린다.**

> **■ 하나 만들 때마다 화면에서 확인하고 넘어가는 편이 훨씬 빠르다.** 만들어 두기만 하고 여러 단계 뒤에 한꺼번에 연결하면, 고장 났을 때 어느 단계가 원인인지 가릴 수 없다.

### 5-13-5. 실습 5-6 — 폼을 제어 컴포넌트로 ✅

**`lib/studentData.js` — 값 변환만 남긴다** (아래는 교재 코드. 실물과 다른 두 줄은 코드 뒤 ⚠️ 참고)

```js
// 등록 모드의 빈 폼. 폼을 되돌릴 때도 이 값을 쓴다.
export const EMPTY_FORM = {
    name: "", studentNumber: "", address: "",
    phoneNumber: "", email: "", dateOfBirth: "",
};

// 폼 state 를 서버가 받는 구조로 바꾼다.
// 4부에서 FormData 로 하던 일인데, 이제 값이 form 객체에 이미 있다.
export function toRequest(form) {
    return {
        name: form.name.trim(),
        studentNumber: form.studentNumber.trim(),
        detailRequest: {
            address: form.address.trim(),
            phoneNumber: form.phoneNumber.trim(),
            // 빈 문자열("")도 걸러야 하므로 여기서는 ?? 가 아니라 || 를 쓴다.
            email: form.email.trim() || null,
            dateOfBirth: form.dateOfBirth || null,
        },
    };
}

// 서버에서 받은 학생 정보를 폼 state 모양으로 바꾼다. 수정 버튼을 눌렀을 때 쓴다.
export function toFormValues(student) {
    const detail = student.detail;
    // input 의 value 에 undefined 를 넣으면 React 가 경고를 낸다.
    // 그래서 값이 없을 때는 반드시 빈 문자열로 바꿔 준다.
    return {
        name: student.name ?? "",
        studentNumber: student.studentNumber ?? "",
        address: detail?.address ?? "",
        phoneNumber: detail?.phoneNumber ?? "",
        email: detail?.email ?? "",
        dateOfBirth: detail?.dateOfBirth ?? "",
    };
}
```

🔗 **4부 `collectStudentData()` 에서 `FormData` 를 걷어내면 남는 것이 `toRequest()` 다. `fillForm()` 에서 `input.value` 를 걷어내면 남는 것이 `toFormValues()` 다.** 이름만 바뀌고 하는 일은 똑같다 — 3-7절에서 예고한 그대로.

⚠️ **실물은 `toRequest` 의 두 줄이 교재와 다르다.** 주소와 전화번호에도 `|| null` 이 붙어 있다. 빈 칸이면 `""` 대신 `null` 을 보낸다는 뜻.

```js
// student_react_first/src/lib/studentData.js — 실물
// 백엔드 StudentDTO의 Request (요청) 객체
export function toRequest(form) {
    return {
        name: form.name.trim(),
        studentNumber: form.studentNumber.trim(),
        detailRequest: {
            address: form.address.trim() || null,          // ← 교재는 trim() 만
            phoneNumber: form.phoneNumber.trim() || null,  // ← 교재는 trim() 만
            // 빈 문자열("")도 걸러야 하므로 여기서는 ?? 가 아니라 || 를 쓴다.
            email: form.email.trim() || null,
            dateOfBirth: form.dateOfBirth || null,
        },
    };
}
// toFormValues 는 "백엔드 StudentDTO의 Response (응답) 객체" 라는 주석만 더해졌고 본문은 교재와 같다.
```

🔗 **`toRequest` = 백엔드 `StudentDTO.Request`, `toFormValues` 의 입력 = 백엔드 `StudentDTO.Response`.** 실물 주석이 백엔드 DTO 이름을 그대로 적어 두었다. 요청은 `detailRequest`, 응답은 `detail` — 3-7절에서 짚은 **요청/응답 필드명 차이**가 여기서도 그대로다.

⚠️ **실제로 영향이 있는 것은 주소 하나다.** `validateStudent` 는 전화번호를 `!phoneNumber` 로 검사하므로 `""` 든 `null` 이든 똑같이 걸린다. **주소는 검사 규칙에서 빠져 있어서(4-11절 대응표) 주소를 비우면 서버에 `""` 대신 `null` 이 간다.** 다만 폼의 `required` 속성이 브라우저에서 먼저 막으므로 평소에는 겪을 일이 드물다. `||` 와 `??` 의 차이(빈 문자열을 거르는가)는 3-4절.

**입력칸을 state 에 묶는다**

```jsx
// App.jsx
const [form, setForm] = useState(EMPTY_FORM);

function handleChange(event) {
    // event.target        방금 글자를 친 input
    // event.target.name   그 input 에 적어 둔 name
    const name = event.target.name;
    const value = event.target.value;

    const next = { ...form };   // 기존 값을 그대로 복사한 새 객체
    next[name] = value;         // 바뀐 칸 하나만 덮어쓴다
    setForm(next);
}
```

⚠️ 실물 `handleChange` 에는 `const next = { ...form };` 바로 아래 **`console.log(next)`** 한 줄이 더 있다(확인용). 글자를 칠 때마다 Console 에 **바뀌기 직전의** form 이 찍힌다.

**`components/StudentForm.jsx` — 실물 전체 구조**

```jsx
/* 이 컴포넌트는 값을 저장하지 않습니다. 그리기만 합니다.
     화면에 보이는 값 = props.form
     값이 바뀌면      = props.onChange 로 부모에게 알린다
   이런 입력을 제어 컴포넌트(controlled component)라고 합니다.
   입력칸 여섯 개가 생김새는 같지만 일부러 하나씩 펼쳐 적었습니다.
   위에서 아래로 한 번에 읽히는 것이 지금은 더 중요하기 때문입니다. */
import MessageBox from "./MessageBox.jsx";

function StudentForm({
    form,          // 화면에 보일 입력값 여섯 개
    isEditing,     // 수정 모드인가
    message,       // 폼 아래 보여 줄 메시지
    onChange,      // 입력칸이 바뀔 때 부를 함수
    onSubmit,      // 제출할 때 부를 함수
    onCancel,      // 취소를 누를 때 부를 함수
    containerRef,  // 수정 시 이 위치로 스크롤하기 위한 참조
}) {
    // 4부 setEditMode 가 classList.toggle 로 하던 일을 문자열로 표현한다.
    let containerClass = "form-container";
    if (isEditing) {
        containerClass = "form-container editing";
    }

    // 등록 모드와 수정 모드에서 글자만 달라진다.
    let actionLabel = "등록";
    if (isEditing) {
        actionLabel = "수정";
    }

    return (
        <div className={containerClass} ref={containerRef}>
            <h2>학생 {actionLabel}</h2>

            {/* onSubmit 안에서 event.preventDefault() 를 부르는 것은 4부와 같다. */}
            <form onSubmit={onSubmit}>
                <div className="form-grid">
                    {/* 입력칸 한 개는 언제나 이 세 가지가 짝이다.
                          value    = {form.어느칸}   보이는 값은 부모에게서 온다
                          onChange = {onChange}      바뀌면 부모에게 알린다
                          name     = "어느칸"        부모가 어느 칸인지 알아보는 이름 */}
                    <div className="form-group">
                        <label htmlFor="name">이름:</label>
                        <input id="name" name="name" type="text" required
                               value={form.name} onChange={onChange} />
                    </div>
                    {/* 학번(text) · 주소(text) · 전화번호(tel) · 이메일(email) 도
                        같은 모양으로 네 번 더. 모두 required */}
                    <div className="form-group">
                        <label htmlFor="dateOfBirth">생년월일:</label>
                        <input id="dateOfBirth" name="dateOfBirth" type="date"
                               value={form.dateOfBirth} onChange={onChange} />
                        {/* ↑ 생년월일만 required 가 없다 */}
                    </div>
                </div>

                <div className="button-group">
                    <button type="submit">학생 {actionLabel}</button>

                    {/* 4부에서는 style.display 를 바꿨지만, 여기서는 아예 그리지 않는다.
                        조건 && 화면 은 "조건이 참일 때만 그린다"는 뜻이다. */}
                    {isEditing && (
                        <button type="button" className="cancel-btn" onClick={onCancel}>
                            취소
                        </button>
                    )}

                    <MessageBox message={message} />
                </div>
            </form>
        </div>
    );
}

export default StudentForm;
```

**이 파일에서 읽어야 할 것**
- **`containerRef` 를 `ref={containerRef}` 로 넘긴다.** 부모(App)의 `formRef` 가 여기 붙는다. **`ref` 를 props 이름으로 쓰지 않고 `containerRef` 라고 지은 것**에 주목 — 함수 컴포넌트는 예전 버전의 React 에서 `ref` 라는 이름의 props 를 받을 수 없었다(React 19 부터는 가능). 다른 이름으로 받으면 버전과 상관없이 안전하다.
- **`MessageBox` 가 폼 안(`.button-group`)에 있다.** 메시지는 App 이 갖고 있지만, 그리는 자리는 폼 버튼 옆이다. 그래서 `message` 를 `StudentForm` 에 내려 주고 `StudentForm` 이 다시 `MessageBox` 에 내려 준다 — **두 단계 내려 주기**(🔗 6부 AppMessage 에서 이 구조가 바뀐다).
- `let` + `if` 로 `containerClass` · `actionLabel` 을 만든 것 — 삼항(`isEditing ? "수정" : "등록"`)으로도 되지만 **초보자에게 읽히기 쉬운 쪽을 골랐다.**
- 4부 `index.html` 의 폼 마크업이 **거의 그대로 JSX 로 옮겨 왔다.** 달라진 것은 `class`→`className`, `for`→`htmlFor`, 그리고 `value` + `onChange` 가 붙은 것뿐.

⚠️ **입력칸마다 적은 `name` 이 열쇠다.** 여섯 칸이 모두 같은 함수 하나를 부르는데, 어느 칸이 바뀌었는지는 `event.target.name` 이 알려 준다. **`name` 을 빠뜨린 칸은 글자를 쳐도 아무 일이 일어나지 않는다.**

🔗 **1-6절에서 "`id` 와 `name` 은 역할이 다르다"고 한 것이 5부까지 이어진다.** 4부에서는 `FormData` 가 `name` 을 봤고, 5부에서는 `handleChange` 가 `name` 을 본다.

⚠️ **`next[name] = value` 에서 대괄호를 쓴 이유** — 어느 칸이 바뀌었는지가 `name` **변수**에 담겨 있기 때문. `next.name` 이라고 쓰면 `"name"` 이라는 이름의 칸만 고치게 된다. **React 폼에서 가장 흔히 쓰는 모양이라, 다른 자료를 봐도 같은 코드를 만난다.**

⚠️ `A component is changing an uncontrolled input to be controlled` → 처음에 값이 `undefined` 였다가 생긴 것. **`EMPTY_FORM` 처럼 빈 문자열로 시작하라.** `toFormValues` 가 `?? ""` 로 빈 문자열을 보장하는 이유가 이것이다.

⚠️ **글자가 안 써진다** → `value` 만 주고 `onChange` 를 빠뜨렸다. 화면이 state 에 묶여 있으므로 state 를 바꿔 주지 않으면 값이 변하지 않는다.

### 5-13-6. 실습 5-7 — 메시지와 로딩 (조건부 렌더링) ✅

**`components/MessageBox.jsx` — 넷 중 가장 작은 컴포넌트**

```jsx
// 메시지 종류별 글자색 — 4부 COLORS 와 같다.
const COLORS = { error: "#dc3545", success: "#28a745" };

function MessageBox({ message }) {
    // null 을 돌려주면 아무것도 그리지 않는다.
    // 4부처럼 display: none 으로 숨길 필요가 없다.
    if (!message) {
        return null;
    }

    // style 에는 문자열이 아니라 객체를 넘긴다. 그래서 중괄호가 두 겹이다.
    const textStyle = { color: COLORS[message.type] ?? COLORS.error };

    return (
        <span className="error-message" style={textStyle}>
            {message.text}
        </span>
    );
}
export default MessageBox;
```

🔗 **4부의 `showError` · `showSuccess` · `clearMessages` 세 함수가 이 컴포넌트 하나가 됐다.** 지우는 함수가 없어진 것은 **`setMessage(null)` 을 부르면 이 컴포넌트가 `null` 을 돌려주며 저절로 사라지기 때문**이다.

**⚠️ `style.css` 에서 한 줄을 지워야 한다 — 놓치기 쉬운 함정**

```css
.error-message {
    color: #dc3545;
    font-size: 0.9em;
    margin-top: 5px;
    display: none;          /* ← 이 줄을 지운다 */
}
```

여기까지 적고 실행하면 **메시지가 화면에 나오지 않는다.** 코드는 맞다. F12 → Elements 로 보면 `span` 이 분명히 들어와 있다. 그런데 눈에는 안 보인다. **CSS 가 가리고 있기 때문이다.**

| | 4부 | 5부 |
|---|---|---|
| span 은 언제 있나 | **늘 있다.** `index.html` 에 자리를 잡고 있었다 | **그릴 것이 있을 때만 생긴다** |
| 숨기는 방법 | `display: none` 을 기본으로 두고 `showError()` 가 `block` 으로 바꾼다 | `MessageBox` 가 `null` 을 돌려준다 |
| 그래서 CSS 의 `display: none` 은 | **필요했다** | **방해만 된다** |

⚠️ **실물은 이 줄을 지우지 않고 `/* display: none; */` 으로 주석 처리해 두었다.** 효과는 같다. 나중에 왜 바꿨는지 보이게 남겨 둔 것.

> **■ 4부의 CSS 를 그대로 가져올 때는 이런 줄이 남아 있는지 살펴야 한다.** 보이고 숨기는 일을 CSS 가 아니라 조건부 렌더링이 맡게 되었으므로, **CSS 에 남은 `display: none` 은 이제 방해만 된다.**

⚠️ **`.loading` 에는 같은 줄이 없다.** 4부에서 그 요소를 CSS 가 아니라 `index.html` 의 `style="display: none"` 으로 숨겼기 때문. 그 인라인 style 은 5부에서 `index.html` 을 비우며 함께 사라졌다.

**조건부 렌더링 세 자리**

```jsx
// StudentTable.jsx — 로딩 표시
{loading && <div className="loading">로딩 중...</div>}

// StudentForm.jsx — 수정 모드일 때만 취소 버튼을 그린다
{isEditing && (
  <button type="button" className="cancel-btn" onClick={onCancel}>취소</button>
)}

// StudentForm.jsx — 4부 setEditMode 의 classList.toggle 을 문자열로
let containerClass = "form-container";
if (isEditing) {
    containerClass = "form-container editing";
}
```

🔗 **4부 `setEditMode` 의 세 줄이 여기서 각각 다른 자리로 흩어졌다.** 그런데 **빠뜨릴 수가 없다.** 셋 다 `isEditing` 하나만 보고 있기 때문. 5-1절에서 말한 선언형의 이득이 실제로 나타나는 자리다.

**3초 뒤 저절로 사라지게 — 정리 함수**

```jsx
// App 함수 바깥에 둔다 — 4부의 MESSAGE_TIMEOUT 과 같은 값이다.
const MESSAGE_TIMEOUT = 3000;

useEffect(() => {
    if (!message) {
        return;
    }
    // 오류 메시지는 사용자가 고칠 때까지 남겨 둔다.
    if (message.type !== "success") {
        return;
    }

    const timer = setTimeout(() => setMessage(null), MESSAGE_TIMEOUT);

    // 정리 함수 — 다음 번 실행 직전과 화면에서 사라질 때 불린다.
    return () => clearTimeout(timer);
}, [message]);
```

🔗 **4부에서 `messageTimer` 변수를 두고 `clearTimeout` 을 직접 부르던 일을 이 한 줄이 대신한다.** `return () => clearTimeout(timer)` 덕분에 **이전 예약이 새 메시지를 지워 버리는 일이 없다.**

⚠️ **앞의 `if` 두 개를 한 줄에 몰아 쓰지 않고 나눠 둔 것**에 주목. 무엇을 거르는지가 눈에 들어온다.

⚠️ **조건부 렌더링 자리에 `0` 이 보인다** → `{students.length && ...}` 처럼 숫자로 조건을 쓰면 **0 이 그대로 그려진다.** `students.length > 0` 처럼 참·거짓으로 바꿔라.

### 5-13-7. 실습 5-8 · 5-9 — 제출 · 수정 · 삭제 ✅

```jsx
async function handleSubmit(event) {
    event.preventDefault();   // 페이지 새로고침 막기
    setMessage(null);         // 앞선 메시지를 지운다

    const studentData = toRequest(form);

    // 검사에 걸리면 메시지만 보여 주고 끝낸다.
    const errorMessage = validateStudent(studentData);
    if (errorMessage) {
        setMessage({ text: errorMessage, type: "error" });
        return;
    }

    try {
        if (isEditing) {
            await updateStudent(editingId, studentData);
            setMessage({ text: "학생 정보가 성공적으로 수정되었습니다.", type: "success" });
        } else {
            await createStudent(studentData);
            setMessage({ text: "학생이 성공적으로 등록되었습니다.", type: "success" });
        }
        resetForm();
        await loadStudents();     // 목록 새로고침
    } catch (error) {
        console.error("Error:", error);
        setMessage({ text: error.message, type: "error" });
    }
}

// 폼을 비우고 등록 모드로 되돌린다 — 4부 resetForm()
function resetForm() {
    setForm(EMPTY_FORM);
    setEditingId(null);
}
```

⚠️ **`validateStudent` 는 4부에서 만든 그 함수 그대로다. 고치지 않았다.**

⚠️ **등록과 수정을 가르는 것은 `editingId` 하나다.** 4부의 `editingStudentId` 와 같은 역할인데, **이제 이 값이 바뀌면 버튼 글자와 취소 버튼까지 저절로 따라 바뀐다.** 4부에서 `setEditMode` 로 세 군데를 직접 고치던 일이 없어진다.

**수정 준비와 삭제 — 이벤트 위임이 사라진다**

```jsx
/* useRef 는 화면에 그려진 실제 요소를 붙잡아 두는 자리다.
   state 와 달리 값이 바뀌어도 화면을 다시 그리지 않는다. */
const formRef = useRef(null);

async function handleEdit(studentId) {
    setMessage(null);
    try {
        const student = await fetchStudent(studentId);

        // 4부에서는 fillForm 이 input.value 에 하나씩 넣었다.
        // 여기서는 state 만 바꾸면 입력칸이 따라서 바뀐다.
        setForm(toFormValues(student));
        setEditingId(studentId);

        // 아직 안 그려졌을 수도 있으므로 먼저 확인한다.
        if (formRef.current) {
            formRef.current.scrollIntoView({ behavior: "smooth" });
        }
    } catch (error) {
        setMessage({ text: error.message, type: "error" });
    }
}

async function handleDelete(studentId) {
    if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) return;

    try {
        await deleteStudent(studentId);
        setMessage({ text: "학생이 성공적으로 삭제되었습니다.", type: "success" });

        // 수정 중이던 학생을 삭제했다면 폼도 등록 모드로 되돌린다.
        if (editingId === studentId) {
            resetForm();
        }
        await loadStudents();
    } catch (error) {
        setMessage({ text: error.message, type: "error" });
    }
}
```

**🔗 4부의 이벤트 위임 열세 줄이 통째로 사라진다.**

```js
// 4부 main.js — 전부 없어진다
studentTableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const { action, id } = button.dataset;
    if (action === "edit")        await startEdit(Number(id));
    else if (action === "delete") await removeStudent(Number(id));
});
```

`fillForm(student)` → `setForm(toFormValues(student))`, `setEditMode(true)` → `setEditingId(studentId)` 로 바뀐 것 말고는 **4부의 `startEdit` · `removeStudent` 와 같다.**

### 5-13-8. 실습 5-10 — App.jsx 로 조립하기 ✅

```jsx
return (
  <>
    <h1>학생 관리 시스템 <span className={modeClass}>{APP_MODE}</span></h1>
    <StudentForm
      form={form}
      isEditing={isEditing}
      message={message}
      onChange={handleChange}
      onSubmit={handleSubmit}
      onCancel={resetForm}
      containerRef={formRef}
    />
    <StudentTable
      students={students}
      loading={loading}
      error={listError}
      onEdit={handleEdit}
      onDelete={handleDelete}
    />
  </>
);
```

**제목 옆 모드 배지 — 4부와 견주어 보기**

| 하는 일 | 4부 (`main.js`) | 5부 (`App.jsx`) |
|---|---|---|
| 자리 만들기 | `index.html` 에 빈 `span` 을 둔다 | **JSX 안에 바로 적는다** |
| 글자 넣기 | `badge.textContent = APP_MODE` | `{APP_MODE}` |
| 색 고르기 | `badge.classList.add("prod")` | `className={modeClass}` |

```jsx
import { APP_MODE } from "./config.js";

// 제목 옆에 붙일 배지의 class. 운영이면 빨강, 아니면 회색.
let modeClass = "app-mode test";
if (APP_MODE === "PROD") {
    modeClass = "app-mode prod";
}
```

> **■ 고친 파일이 `App.jsx` 하나뿐이라는 점을 짚고 넘어갈 것.** `.env` 도 `config.js` 도 `style.css` 도 4부 것을 그대로 쓴다.
> **"값만 다루는 파일은 프레임워크를 바꿔도 살아남는다"**는 실습 5-1 의 표가 여기서 또 확인된다.

**App 이 맡는 일**

| 맡는 일 | 무엇 |
|---|---|
| 값 보관 (state 6개) | `students`, `form`, `editingId`, `loading`, `listError`, `message` |
| 서버 호출 | `loadStudents`, `handleSubmit`, `handleEdit`, `handleDelete` |
| 자식에게 내려 주기 | `StudentForm` 에 7개, `StudentTable` 에 5개의 props |
| 실제 DOM 참조 | `formRef` — 수정할 때 폼으로 스크롤하는 데만 |
| **화면 그리기** | **`h1` 한 줄. 나머지는 전부 자식이 한다** |

🔗 **4부에서 `main.js` 가 조립을 맡고 `ui/*` 가 그리기만 하던 구조 그대로다. 구조는 그대로이고 도구만 바뀌었다.**

### 5-13-9. 실습 5-11 — 빌드하면 4부보다 커진다 ✅

| 항목 | 4부 (`student_ecma`) | 5부 (`student_react_first`) |
|---|---|---|
| js 크기 | 약 5 kB | **약 196 kB** (React 포함) |
| `index.html` | 2.74 kB (화면 전체) | **0.48 kB** (빈 상자 하나) |
| 읽는 `.env` | `.env.production` | 같음 |
| 모듈 수 | 7개 | 23개 (React 라이브러리 포함) |

⚠️ **결과물이 4부보다 훨씬 크다.** React 라이브러리가 함께 들어가기 때문. 화면이 이 정도로 단순하면 손해처럼 보이지만, **화면이 늘어날수록 직접 DOM 을 다루는 코드가 훨씬 빠르게 불어나므로 곧 상쇄된다.**

### 5-13-10. 실습 5-12 — 리팩토링: 반복되는 입력칸 묶기 ✅

**동작을 바꾸지 않고 코드의 모양만 고치는 일을 리팩토링(refactoring)이라고 한다. 화면도 기능도 그대로여야 한다.** 그래서 **다 만든 뒤에** 한다. 동작이 달라졌다면 그것은 리팩토링이 아니라 새 기능이거나 버그다.

여덟 줄짜리 덩어리가 여섯 번 반복되는데, 그 안에서 달라지는 것은 **네 가지뿐**이다.

| 달라지는 것 | 예 |
|---|---|
| `name` | name, studentNumber, address ... |
| `label` | 이름, 학번, 주소 ... |
| `type` | text, tel, email, date |
| `required` | **생년월일만 아니다** |

```jsx
function Field({ name, label, type, required, value, onChange }) {
    return (
        <div className="form-group">
            <label htmlFor={name}>{label}:</label>
            <input
                id={name} name={name} type={type}
                required={required} value={value} onChange={onChange}
            />
        </div>
    );
}
```

```jsx
<div className="form-grid">
    <Field name="name"          label="이름"     type="text"  required
           value={form.name}          onChange={onChange} />
    <Field name="studentNumber" label="학번"     type="text"  required
           value={form.studentNumber} onChange={onChange} />
    <Field name="address"       label="주소"     type="text"  required
           value={form.address}       onChange={onChange} />
    <Field name="phoneNumber"   label="전화번호" type="tel"   required
           value={form.phoneNumber}   onChange={onChange} />
    <Field name="email"         label="이메일"   type="email" required
           value={form.email}         onChange={onChange} />
    {/* 생년월일만 required 를 적지 않는다. 비워 두어도 된다. */}
    <Field name="dateOfBirth"   label="생년월일" type="date"
           value={form.dateOfBirth}   onChange={onChange} />
</div>
```

⚠️ **`required` 만 적고 값을 주지 않으면 `true` 와 같다.** 생년월일에는 아예 적지 않았으므로 `Field` 안에서 `required` 가 `undefined` 가 되고, **React 는 그 속성을 그리지 않는다.**

**새 파일로 두고 바꿔 끼우기** — 지금 있는 `StudentForm.jsx` 를 고치지 말고 `StudentFormField.jsx` 라는 새 파일을 만든다. 그러면 **두 벌이 나란히 남아 `App.jsx` 의 `import` 한 줄만 바꿔 가며 견주어 볼 수 있다.**

```jsx
// import StudentForm from "./components/StudentForm.jsx";       // 펼쳐 쓴 것
import StudentForm from "./components/StudentFormField.jsx";     // 묶은 것
```

**파일 이름은 달라도 내보내는 컴포넌트 이름은 둘 다 `StudentForm` 이다.** 그래서 `App.jsx` 의 나머지 코드는 한 글자도 고칠 것이 없다. **`export default` 의 편한 점이다.**

**어느 쪽이 나은가**

| | `StudentForm.jsx` (펼침) | `StudentFormField.jsx` (묶음) |
|---|---|---|
| 입력칸 부분 | 72줄 | **15줄** |
| 파일 전체 | 145줄 | 113줄 |
| 처음 읽을 때 | **위에서 아래로 한 번에 읽힌다** | `Field` 를 먼저 이해해야 한다 |
| 칸을 하나 더할 때 | 덩어리를 복사해 네 곳을 고친다 | **한 줄을 더한다** |
| 모든 칸의 모양을 바꿀 때 | 여섯 곳을 똑같이 고친다 | **`Field` 한 곳만 고친다** |
| 한 칸만 다르게 할 때 | **그 자리만 고치면 된다** | `Field` 에 분기가 생긴다 |

> **정답은 없다.** 반복이 셋을 넘고 모양이 정말 같다면 묶는 쪽이 낫다. **묶었다가 분기가 자꾸 늘어나면 다시 펼치는 것도 방법이다.**

⚠️ **화면에 입력칸이 하나도 안 보인다** → 컴포넌트 이름을 소문자 `field` 로 썼다. **React 가 HTML 태그로 보고 그리지 않는다. 오류도 나지 않아 찾기 어려운 실수다.**

**실물 확인** — `StudentFormField.jsx` 의 `Field` 와 입력칸 여섯 줄은 위 코드와 같다. `Field` 바깥의 `StudentForm` 본체(`containerClass` · `actionLabel` · 버튼 · `MessageBox`)는 `StudentForm.jsx` 와 **한 글자도 다르지 않다.** 그리고 실물 `App.jsx` 는 **묶은 쪽을 쓰고 있다.**

```jsx
// student_react_first/src/App.jsx — 실물
//import StudentForm from './components/StudentForm';
import StudentForm from './components/StudentFormField';
```

🔗 6부는 **묶은 쪽(`StudentFormField`)으로 이어 간다**(실습 6-1). 

### 5-13-11. 실습 5-13 — 성능 개선: memo 와 useCallback ✅ (신설)

**이번 단계 목표** — 폼에 글자를 칠 때마다 표 전체가 다시 그려지는 낭비를 없애고, **`memo` 와 `useCallback` 이 왜 짝으로 다녀야 하는지** 설명할 수 있다.

**1) 무엇이 낭비인가**

입력칸에 글자 하나를 치면 `form` state 가 바뀌고 → App 이 다시 그려지고 → **자식 둘도 함께 다시 그려진다.** `StudentForm` 은 그래야 맞다(값이 바뀌었으니). **그런데 `StudentTable` 은 그럴 까닭이 없다. `students` 는 한 글자도 바뀌지 않았기 때문이다.**

| 조작 (학생 50명) | StudentTable | 다시 만들어진 행 |
|---|---|---|
| 글자 5번 입력 | 5번 다시 그림 | **250개** |
| 그중 화면이 달라진 행 | — | **0개** |

> 학생이 스무 명 남짓이면 이 낭비가 느껴지지 않는다(5-11절 표 "목록 20개 정도는 매번 그려도 빠르다"). **이 실습은 지금 느려서 하는 것이 아니라, 언젠가 느려졌을 때 무엇을 해야 하는지를 미리 손으로 해 보는 것이다.** 순서는 언제나 **"재 보고 나서 넣는다"**.

**2) 먼저 재 본다 — Profiler**

크롬 확장 **React Developer Tools** 를 깔면 F12 에 **Profiler** 탭이 생긴다. 녹화를 켜고 이름 칸에 글자를 몇 개 친 뒤 멈추면, 글자마다 `StudentTable` 이 다시 그려진 것이 막대로 보인다. 막대를 눌러 **"Why did this render?"** 를 보면 이유까지 알려 준다.

**3) 표를 memo 로 감싼다** — `components/StudentTable.jsx` 실물 맨 아래

```jsx
import { memo } from "react";
// ... 컴포넌트 본문은 그대로 ...

/* 받은 props 가 그대로면 다시 그리지 않는다(실습 5-13).
   폼에 글자를 칠 때마다 App 이 다시 그려지는데, 그때 students 는
   바뀌지 않았으므로 이 표까지 다시 그릴 까닭이 없다.

   이것이 듣게 하려면 onEdit 과 onDelete 가 매번 새 함수여서는 안 된다.
   그래서 App 쪽에서 useCallback 으로 고정해 두었다. */
export default memo(StudentTable);
```

⚠️ **여기까지만 하면 아무것도 달라지지 않는다.** `memo` 는 props 를 하나씩 `===` 로 견주는데, `onEdit` 과 `onDelete` 가 App 이 다시 그려질 때마다 **새 함수로 만들어지기 때문**이다. 하는 일이 같은 함수여도 새로 만든 것은 `===` 로 보면 다른 것이다. 그래서 언제나 "달라졌다"가 나온다.

**4) 함수를 고정한다 — `useCallback`** — `App.jsx` 실물

```jsx
import { useCallback, useEffect, useRef, useState } from 'react';

//async function loadStudents() {                 ← 옛 모양을 주석으로 남겨 둠
const loadStudents = useCallback(async () => {
  setLoading(true);
  setListError(null);
  try {
    const data = await fetchStudents();
    console.log("Fetched students:", data);
    setStudents(data);
  } catch (error) {
    console.error("Error:", error);
    setMessage({ text: error.message, type: "error" });
    setListError("오류: 데이터를 불러올 수 없습니다.");
  } finally {
    setLoading(false);
  }
},[]);

//function resetForm() {
const resetForm = useCallback(() => {
  setForm(EMPTY_FORM);
  setEditingId(null);
},[])//resetForm

//async function handleEdit(studentId) {
const handleEdit = useCallback(async (studentId) => {
  setMessage(null);
  try {
    const student = await fetchStudent(studentId);
    setForm(toFormValues(student));
    setEditingId(studentId);
    if (formRef.current) {
      formRef.current.scrollIntoView({ behavior: "smooth" });
    }
  } catch (error) {
    console.error("Error:", error);
    setMessage({ text: error.message, type: "error" });
  }
},[]);//handleEdit

//async function handleDelete(studentId) {
const handleDelete = useCallback(async (studentId) => {
  if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) {
    return;
  }
  try {
    await deleteStudent(studentId);
    setMessage({ text: "학생이 성공적으로 삭제되었습니다.", type: "success" });
    // 수정 중이던 학생을 삭제했다면 폼도 등록 모드로 되돌린다.
    if (editingId === studentId) {
      resetForm();
    }
    await loadStudents();
  } catch (error) {
    console.error("Error:", error);
    setMessage({ text: error.message, type: "error" });
  }
}, [editingId, resetForm, loadStudents]);//handleDelete
```

**감쌀 것은 네 개다.** 표에 내려 주는 둘(`handleEdit` · `handleDelete`)과, **그 둘이 안에서 쓰는 둘(`loadStudents` · `resetForm`)**. **안쪽이 고정돼야 바깥쪽도 고정된다.**

**의존성 배열 — "함수 안에서 바깥의 무엇을 읽는가"로 정한다**

| 함수 | 의존성 | 까닭 |
|---|---|---|
| `loadStudents` | `[]` | set 함수와 import 한 `fetchStudents` 만 쓴다 |
| `resetForm` | `[]` | set 함수만 쓴다 |
| `handleEdit` | `[]` | set 함수 · `formRef` · import 만 쓴다. **ref 는 바뀌지 않는다** |
| `handleDelete` | **`[editingId, resetForm, loadStudents]`** | **안에서 `editingId` 를 읽는다** |

- ⚠️ **set 함수(`setForm`, `setEditingId` …)는 의존성에 적지 않아도 된다.** React 가 "이것은 바뀌지 않는다"고 보장하기 때문. `useRef` 로 만든 `formRef` 도 같다. 그래서 세 함수는 의존성이 비어 있다. import 한 함수(`fetchStudents` 등)도 컴포넌트 바깥 것이라 적지 않는다.
- ⚠️ **`handleDelete` 만 `[]` 가 아니다.** 안에서 `editingId === studentId` 를 견주기 때문. 그래서 수정 모드가 바뀔 때마다 이 함수는 새로 만들어지고, 그때 표도 한 번 다시 그려진다. **그래도 괜찮다. 없애려던 것은 "글자를 칠 때마다"였고, `editingId` 는 글자를 칠 때 바뀌지 않는다.**
- ⚠️ **`handleDelete` 의 의존성을 `[]` 로 두면** → 수정 중이던 학생을 삭제해도 폼이 등록 모드로 돌아오지 않는다. **낡은 `editingId`(처음 값 `null`)를 들고 있어 `editingId === studentId` 가 어긋나기 때문.** 화면은 멀쩡하고 오류도 나지 않아 찾기 어렵다 — 🔗 E-19 의 "count 가 1 에서 멈춘다"와 같은 원리(굳어 버린 값).
- ⚠️ **`resetForm` 과 `loadStudents` 를 `handleDelete` 의 의존성에 적는 이유** — 둘 다 컴포넌트 안에서 만든 함수라 "바깥의 값"이다. `useCallback([])` 으로 고정돼 있으니 실제로는 바뀌지 않지만, **ESLint(`react-hooks/exhaustive-deps`)가 적으라고 한다.** 적어도 손해가 없다.

**5) `useEffect` 의 `[]` 가 `[loadStudents]` 가 된다**

`loadStudents` 를 `useCallback` 으로 감싸는 순간, 실습 5-4 에서 적은 `useEffect` 가 ESLint 경고를 낸다 — **`useEffect has a missing dependency: 'loadStudents'`**.

```jsx
// 실물
useEffect(() => {
  // eslint-disable-next-line react-hooks/set-state-in-effect -- 처음 한 번 목록을 불러오는 것은 의도된 동작입니다
  loadStudents();
}, [loadStudents]);
```

**뜻은 그대로 "처음 한 번만"이다.** `loadStudents` 가 의존성 `[]` 로 고정돼 다시 만들어지지 않으므로 이 배열의 내용도 바뀌지 않는다. `npm run lint` 로 경고가 없는지 확인.

⚠️ **왜 5-4 때는 `[]` 로 괜찮았나** — 그때 `loadStudents` 는 평범한 `function` 선언이라 **매번 새로 만들어졌다.** `[loadStudents]` 로 적었다면 그릴 때마다 효과가 다시 실행돼 **서버를 끝없이 불렀을 것**이다. **`useCallback` 으로 고정한 뒤에야 의존성에 넣을 수 있다.**

⚠️ **`Cannot access 'loadStudents' before initialization`** → `function` 선언은 끌어올려지지만(호이스팅) **`const` 는 아니다**(4-3절 TDZ). `const loadStudents = useCallback(...)` 은 **그것을 쓰는 `useEffect` 와 `handleDelete` 보다 위에** 적어야 한다.

**6) 다시 재 본다**

| 조작 (학생 50명) | 5-12 까지 | 5-13 |
|---|---|---|
| 글자 5번 입력 — StudentTable | 5번 다시 그림 | **0번** |
| 글자 5번 입력 — 다시 만든 행 | 250개 | **0개** |
| 글자 5번 입력 — StudentForm | 5번 | 5번 (그래야 맞다) |
| 수정 · 삭제 · 등록 | 동작함 | 동작함 |

학생을 500명으로 늘려도 `StudentTable` 은 0번이다. **목록이 커질수록 이득이 커지는 것이 이 최적화의 성격이다.**

**7) 감싸지 않은 것들 — 기준은 하나: "`memo` 로 감싼 자식에게 내려 주는 값인가"**

| 무엇 | 어떻게 | 까닭 |
|---|---|---|
| `handleChange` · `handleSubmit` | 그대로 둔다 (`function`) | 받는 `StudentForm` 이 `memo` 가 아니다 |
| `StudentForm` 을 `memo` 로 | 감싸지 않는다 | `form` 이 글자마다 바뀌므로 어차피 다시 그려야 한다 |
| `MessageBox` 를 `memo` 로 | 감싸지 않는다 | `StudentForm` 안에 있고 매우 작다. **견주는 비용이 더 든다** |
| `useMemo` | 쓸 자리가 없다 | 무거운 계산도 없고, 객체·배열을 표에 넘기는 자리도 없다 |

- ⚠️ **`useMemo` 가 필요해지는 때** — 나중에 검색칸을 붙여 `students.filter(...)` 한 결과를 `StudentTable` 에 넘긴다고 하자. **`filter` 는 부를 때마다 새 배열을 돌려주므로** 검색어와 상관없이 매번 "달라졌다"가 되어 `memo` 가 다시 무력해진다. **그 배열을 `useMemo` 로 감싸야 비로소 듣는다.** 지금은 표에 state 값과 `useCallback` 함수만 내려 주므로 그런 자리가 없다.
- ⚠️ **이 단계를 5-12 뒤에 둔 까닭** — 최적화는 코드를 읽기 어렵게 만든다. `useCallback` 이 붙은 함수는 그냥 `function` 보다 눈에 잘 안 들어온다. **그래서 기능이 다 되고 구조가 정리된 뒤에 마지막으로 얹는다.** 실습 5-10 교재에도 "완성본을 열면 import 줄에 `useCallback` 이 더 붙어 있지만 5-13 에서 더하는 것이라 지금은 없는 것이 맞다"는 안내가 새로 들어갔다.

> **■ 확인하기** — Profiler 에서 글자를 칠 때 StudentTable 이 다시 그려지지 않는다 / 등록·수정·삭제가 그대로 동작한다 / **수정 중이던 학생을 삭제하면 폼이 등록 모드로 돌아온다** / 다른 학생을 삭제할 때는 수정 중인 폼이 그대로 남아 있다 / `npm run lint` 가 경고 없이 끝난다 / `StudentTable.jsx` 마지막 줄이 `export default memo(StudentTable);`

**■ 자주 나는 오류**

| 증상 | 원인과 해결 |
|---|---|
| `memo` 를 감쌌는데 그대로 다시 그려진다 | `useCallback` 을 아직 안 붙였다. **둘은 짝으로 다닌다** |
| `useEffect has a missing dependency` | 5) 의 `[loadStudents]` 로 바꾼다 |
| `Cannot access before initialization` | `function` 선언은 끌어올려지지만 `const` 는 아니다. 쓰는 자리보다 위에 적는다 |
| 수정 중인 학생을 지워도 폼이 안 비워진다 | `handleDelete` 의 의존성에 `editingId` 를 빠뜨렸다 |

**실습 5-1 ~ 5-13 에서 쓴 개념 한눈에**

| 개념 | 쓰인 곳 | 무엇을 해결했나 |
|---|---|---|
| JSX | 컴포넌트 전부 | `createElement` 를 안 써도 된다 |
| 컴포넌트 | App, StudentForm, StudentTable, MessageBox | 화면을 조각으로 나눈다 |
| props | form, students, onEdit, onDelete ... | 부모가 자식에게 값과 함수를 준다 |
| useState | App 의 값 여섯 개 | 값이 바뀌면 화면이 따라온다 |
| useEffect `[]` | 첫 목록 불러오기 | 처음 한 번만 실행 |
| useEffect 정리 함수 | 메시지 자동 소멸 | `clearTimeout` 을 대신한다 |
| useRef | 폼으로 스크롤 | 실제 DOM 요소가 필요할 때 |
| 조건부 렌더링 | 로딩, 메시지, 취소 버튼, 빈 목록 | display 조작을 없앤다 |
| map + key | 표의 행 | 배열을 화면으로 |
| 제어 컴포넌트 | 입력칸 여섯 개 | 입력값을 state 에 둔다 |
| 컴포넌트 재사용 | Field (실습 5-12) | 반복되는 화면 조각을 묶는다 |
| 전개 구문 | `const next = { ...form }` | state 를 새 객체로 바꾼다 |
| 옵셔널 체이닝 / `??` | `detail?.address ?? "-"` | 4부 문법 그대로 JSX 안에서 |
| **memo** | StudentTable (실습 5-13) | props 가 그대로면 다시 그리지 않는다 |
| **useCallback** | handleEdit · handleDelete (실습 5-13) | 함수를 고정해 memo 가 듣게 한다 |

---

## 5-14. student_ecma ↔ student_react_first 대응표

4부의 코드가 어디로 갔는지 찾을 때 쓴다.

| 4부 파일 / 함수 | 5부에서 | 달라진 점 |
|---|---|---|
| `config.js` | `src/config.js` | **그대로 (0줄 수정)** |
| `api/studentApi.js` | `src/api/studentApi.js` | **그대로 (0줄 수정)** |
| `lib/validation.js` | `src/lib/validation.js` | **그대로 (0줄 수정)** |
| `style.css` | `src/style.css` | 그대로 + `.error-message` 의 `display:none` 삭제 |
| `collectStudentData()` | `lib/studentData.js` `toRequest()` | `FormData` 대신 state 를 받는다 |
| `fillForm()` | `lib/studentData.js` `toFormValues()` | DOM 에 넣지 않고 값만 돌려준다 |
| `setEditMode()` | `isEditing` (계산된 값) | **함수가 사라지고 값 하나가 됐다** |
| `resetForm()` | `App.jsx` `resetForm()` | state 두 개를 되돌린다 |
| `scrollToForm()` | `useRef` + `scrollIntoView` | 요소 참조 방식만 다르다 |
| `ui/message.js` (3함수) | `components/MessageBox.jsx` | **`null` 을 돌려주면 사라진다** |
| `setLoading()` | `{loading && ...}` | 조건부 렌더링 |
| `ui/studentTable.js` | `components/StudentTable.jsx` | `createElement` 가 JSX 로 |
| `createStudentRow()` | `students.map(...)` | 반복문이 `map` 으로 |
| `renderTableError()` | `{error && <tr>...}` | 조건부 렌더링 |
| **이벤트 위임 (tbody)** | `onClick={() => onEdit(id)}` | **통째로 사라졌다** |
| `main.js loadStudents()` | `App.jsx loadStudents()` | `render` 대신 `setStudents` |
| `main.js startEdit()` | `App.jsx handleEdit()` | 이름만 다르다 |
| `main.js removeStudent()` | `App.jsx handleDelete()` | 이름만 다르다 |
| 맨 아래 `loadStudents()` | `useEffect(..., [])` | 처음 한 번만 |
| `messageTimer` / `clearTimeout` | `useEffect` 의 정리 함수 | **React 가 대신 취소** |

---

## 5-15. React 자주 나는 오류 모음

| 오류 메시지 / 증상 | 원인과 해결 |
|---|---|
| `Adjacent JSX elements must be wrapped` | 태그 여러 개를 나란히 돌려줬다. `<> </>` 로 감싸라 |
| `Each child in a list should have a unique "key"` | `map` 이 만드는 **가장 바깥** 요소에 `key={id}` 를 넣어라 |
| `Objects are not valid as a React child` | 중괄호 안에 객체를 넣었다. 값을 꺼내 쓰라 |
| `Too many re-renders` | `onClick={fn(id)}` → `onClick={() => fn(id)}` |
| `Rendered more hooks than during the previous render` | 조건문 안에서 Hook 을 불렀다 (5-8절) |
| `A component is changing an uncontrolled input...` | `value` 가 `undefined` 로 시작했다. 빈 문자열로 시작하라 |
| `Cannot read properties of null (reading ...)` | `ref.current` 가 아직 비어 있다. `?.` 를 붙여라 |
| `Unexpected token <` / import analysis 실패 | **`.js` 파일에 JSX 를 썼다.** `.jsx` 로 바꿔라 |
| **서버 요청이 끝없이 반복된다** | `useEffect` 의 두 번째 인자 `[]` 를 빠뜨렸다 |
| 개발 모드에서 요청이 두 번 나간다 | **`StrictMode` 의 정상 동작.** 빌드하면 한 번 |
| **값을 바꿨는데 화면이 그대로다** | state 를 직접 고쳤다. 새 객체·새 배열로 넘겨라 |
| `setState` 직후에 읽으면 이전 값이다 | `setState` 는 **다음에 그릴 때 쓸 값을 예약**한다. 방금 넣은 값이 필요하면 state 대신 그 값을 쓰라 |
| 글자가 안 써진다 | `value` 만 주고 `onChange` 를 빠뜨렸다 |
| 조건부 렌더링 자리에 `0` 이 보인다 | `{arr.length && ...}` → `{arr.length > 0 && ...}` |
| CSS 가 하나도 안 먹는다 | `class` 를 `className` 으로 바꿨는지, CSS 를 `import` 했는지 |
| 화면이 하얗다 | F12 Console 을 보라. 대개 **컴포넌트 이름을 소문자로 썼거나** import 경로가 틀렸다 |
| 메시지가 아예 안 보인다 | `style.css` 의 `.error-message` 에 `display: none` 이 남아 있다 |
| `blocked by CORS policy` | 서버의 CORS 허용 목록에 5173 을 추가하라 |
| **`memo` 로 감쌌는데 그대로 다시 그려진다** | 내려 주는 함수가 매번 새로 만들어진다. `useCallback` 을 붙여라 — **둘은 짝이다** |
| **추가한 할 일이 다른 줄을 지울 때 함께 사라진다** | `memo` 비교 함수가 함수 props 를 무시해 **낡은 함수**가 남았다. 비교 함수를 지우고 `useCallback` + 함수형 업데이트로 (E-20) |
| `useCallback` 을 붙였는데 값이 멈춘다 (count 가 1 에서 멈춤) | 의존성 `[]` 인데 안에서 state 를 읽었다. `setX((prev) => ...)` 함수형 업데이트로 |
| `useEffect has a missing dependency: 'loadStudents'` | `loadStudents` 를 `useCallback` 으로 고정한 뒤 `[loadStudents]` 로 적어라 |
| `Cannot access 'x' before initialization` | `const x = useCallback(...)` 을 쓰는 자리보다 아래에 적었다. 위로 올려라 (TDZ) |
| `class="todo-text false"` 가 보인다 | 백틱 안에 `${checked && "checked"}` — 글자를 만들 때는 삼항 `${checked ? "checked" : ""}` |
| 개발 모드에서 `set.size` 가 2씩 는다 | StrictMode 가 두 번 실행한 것. 확인하는 동안만 끄기 |

> **■ 4부에서 겪던 오류(경로 오타, 204 응답, CORS)는 React 를 써도 그대로 난다.** 서버를 부르고 모듈을 나누는 부분은 바뀌지 않았기 때문이다.
> **React 때문에 새로 생기는 오류는 대부분 두 가지다 — "state 를 직접 고쳤다"와 "함수를 넘겨야 할 자리에 호출 결과를 넘겼다".**

---

## 5-16. React 파트 자가 점검 질문

**개념**
1. React 가 **안** 해 주는 것 네 가지를 말하라. 그래서 React 를 무엇이라 부르나?
2. 명령형과 선언형의 차이를 4부 `setEditMode` 를 예로 설명하라.
3. 가상 DOM 의 3단계를 말하라. "다 다시 그리는데 왜 안 느린가"에 답하라.
4. 컴포넌트 함수가 여러 번 실행된다는 사실이 **어떤 코드를 어디에 두어야 하는지**를 결정하나?

**JSX · 컴포넌트**
5. 컴포넌트 이름을 대문자로 시작해야 하는 이유는? 소문자로 쓰면 어떤 일이 생기나?
6. JSX 중괄호 안에 쓸 수 **없는** 두 가지는? 대신 무엇을 쓰나?
7. `style={{ color: "red" }}` 의 중괄호가 두 겹인 이유는?
8. `.js` 에 JSX 를 쓰면 어떤 오류가 나나? `config.js` 는 왜 `.js` 여도 되나?

**props · state**
9. 같은 `students` 가 App 에서는 state, StudentTable 에서는 props 인 이유는?
10. `isEditing` 을 `useState` 로 두면 안 되는 이유는? 어떤 버그가 생기나?
11. 상태 끌어올리기가 무엇이며, 그 대가 세 가지는?
12. prop drilling 이 무엇이고 무엇으로 푸나?
13. state 를 직접 고치면 왜 화면이 안 바뀌나? React 는 무엇으로 판단하나?
14. state 를 따로 둘 때와 객체로 묶을 때의 기준은? 실습 앱은 어떻게 갈랐나?

**이벤트 · Hooks**
15. `onClick={onDelete(id)}` 와 `onClick={() => onDelete(id)}` 의 차이는? 전자는 어떤 오류로 이어지나?
16. React 에서 이벤트 위임이 필요 없는 이유는?
17. Hooks 규칙 두 가지와, 조건문 안에서 Hook 을 부르면 안 되는 **기술적** 이유는?
18. 클래스 컴포넌트의 문제 세 가지 중 Hooks 가 가장 크게 해결한 것은? 코드로 설명하라
19. `useEffect(fn, [])` / `[a]` / 배열 생략 — 각각 클래스의 어느 자리인가?
20. 정리 함수는 언제 불리나? 4부의 무엇을 대신하나?
21. StrictMode 때문에 요청이 두 번 나간다. 이게 문제인가 아닌가?
22. state 와 ref 중 어느 쪽을 쓸지 정하는 **한 문장짜리 기준**은?
23. `useMemo` 와 `useCallback` 을 **쓰지 않아도 되는** 경우 두 가지는?

**map · key**
24. `index` 를 `key` 로 쓰면 안 되는 이유를 A·B·C 예로 설명하라
25. `key` 는 `<tr>` 에 넣나 `<td>` 에 넣나?

**실습 (4부 → 5부)**
26. 4부 파일 중 **0줄 수정으로** 5부에 그대로 가는 것 셋은? 무엇이 기준인가?
27. 4부 `collectStudentData` 에서 무엇을 걷어내면 `toRequest` 가 되나?
28. `toFormValues` 가 `?? ""` 를 쓰는 이유는? 안 쓰면 어떤 경고가 나나?
29. `next[name] = value` 에서 대괄호를 쓰는 이유는?
30. 5부에서 `style.css` 의 `.error-message` 에서 한 줄을 지워야 한다. 어느 줄이고 왜인가?
31. 4부 이벤트 위임 열세 줄이 5부에서 몇 줄이 되나?
32. 5부 빌드 결과가 4부보다 40배 큰데도 React 를 쓰는 이유는?

**TodoList**
33. `TodoListTemplate` 이 `children` 말고 `form` 이라는 props 를 따로 둔 이유는?
34. `e.stopPropagation()` 을 빼면 무슨 일이 생기나?
35. `memo` 의 두 번째 인자가 참을 돌려주면? `shouldComponentUpdate` 와 어떻게 반대인가?
36. `nextId` 를 그냥 변수가 아니라 `useState` 로 둔 이유는?

**useCallback · 실습 5-13 (1.4 추가)**
37. E-19 에서 Set 을 **컴포넌트 바깥**에 둔 이유는? (A)와 (B)의 `set.size` 는 각각 어떻게 변하나?
38. `useCallback(() => setCount(count + 1), [])` 로 감싸면 count 가 1 에서 멈추는 이유는? 어떻게 고치나?
39. E-20 의 "새 할 일 증발" 버그를 ①~⑥ 순서로 설명하라. 삭제 코드(`filter`)는 왜 잘못이 없나?
40. E-20 에서 `useCallback` 을 먼저 붙이고 비교 함수를 **나중에** 지워야 하는 이유는?
41. 실습 5-13 에서 `useCallback` 으로 감싼 네 함수와 각각의 의존성 배열은? `handleDelete` 만 `[]` 가 아닌 이유는?
42. set 함수와 `formRef` 를 의존성에 적지 않아도 되는 이유는?
43. `useEffect(…, [])` 가 `[loadStudents]` 로 바뀌었는데도 "처음 한 번만"인 이유는? 5-4 때 `[loadStudents]` 로 적었다면?
44. `StudentForm` 과 `MessageBox` 는 왜 `memo` 로 감싸지 않았나? `useMemo` 가 필요해지는 상황의 예는?
