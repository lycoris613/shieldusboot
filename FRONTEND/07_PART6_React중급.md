# PART 6. React 중급 — Axios · React Router · Zustand

> **5부까지 만든 앱은 세 라이브러리 중 하나도 쓰지 않고 완성되었다.** 그런데도 쓰는 이유는, 앱이 커질 때 같은 코드를 반복해서 쓰게 되는 지점이 정해져 있기 때문이다.
> **라이브러리는 "없으면 못 하는 것"이 아니라 "있으면 덜 반복하는 것"이다.** 무엇이 불편해서 이것들이 생겼는지를 아는 것이 이 파트의 핵심이다.

## 6-0. 저장소 개요와 진도 현황

```
frontendhcj/
├─ todolist_react_router/            ← 준비 운동 1 — 라우터만 (서버 없음)
│  src/main.jsx                      BrowserRouter 로 감싼다        ← 라우터
│  src/App.jsx                       머리말(NavLink) + Routes         ← 라우터
│  src/App.css                       머리말 · 링크 · 페이지 바깥틀
│  src/pages/TodoPage.jsx            todolist_react 의 AppTodo (+ E-20 useCallback)
│  src/pages/BasicPage.jsx           MyComponent + MyComponentFunc 나란히
│  src/components/                   todolist_react 에서 옮겨 옴 (memo 비교 함수 제거)
│
├─ todolist_react_router_zustand/    ← 준비 운동 2 — store 만 (서버 없음)
│  src/store/todoStore.js            ★ todos · nextId · addTodo · toggleTodo · removeTodo
│  src/components/TodoCount.jsx      ★ 머리말의 "할 일 4 · 끝낸 것 2"
│  src/pages/TodoPage.jsx            useState 하나만 남음, useCallback 사라짐
│
├─ student_react_router_axios/       ← 6부 실습 6-1 ~ 6-10
│  src/api/client.js                 ★ axios.create + 응답 가로채기
│  src/api/studentApi.js             axios 로 다시 쓴 다섯 함수
│  src/api/studentApiFetch.js        5부 fetch 버전을 이름 바꿔 보관 (쓰지 않음)
│  src/AppOrg.jsx                    5부 App.jsx 를 이름 바꿔 보관 (쓰지 않음)
│  src/App.jsx                       머리말 + Routes 네 개
│  src/pages/StudentListPage.jsx     ★ 목록 + 삭제 (useLocation 으로 메시지 받기)
│  src/pages/StudentFormPage.jsx     ★ 등록 · 수정 (useParams · useNavigate)
│  src/pages/NotFoundPage.jsx        ★ 없는 주소 안내
│  src/components/StudentTable.jsx   수정 버튼이 <Link> 로
│  src/components/StudentFormField.jsx  containerRef 제거, 취소 버튼 항상 보임
│  src/store/studentStore.js         (다음 단계 파일이 미리 들어와 있음. 이 단계에선 안 씀)
│
└─ student_react_router_axios_zustand/  ← 6부 실습 6-11 ~ 6-14
   src/store/studentStore.js         ★ students · loading · listError · message · loaded
                                        + loadStudents · saveStudent · removeStudent
   src/components/AppMessage.jsx     ★ store 의 메시지를 머리말 아래 한 곳에서 그린다
   src/pages/StudentListPage.jsx     useState 0개 — 선택자로 꺼내 쓴다
   src/pages/StudentFormPage.jsx     form 만 useState, 저장은 store 에 맡김
   src/pages/StudentListPageRouter.jsx   앞 단계(라우터만) 버전 보관
   src/pages/StudentFormPageRouter.jsx   앞 단계(라우터만) 버전 보관
```

**라이브러리 버전 (실물 `package.json`)**

| 프로젝트 | 더해진 의존성 |
|---|---|
| `todolist_react_router` | `react-router-dom ^7.18.3` |
| `todolist_react_router_zustand` | `react-router-dom ^7.18.3` · `zustand ^5.0.15` |
| `student_react_router_axios` | `axios ^1.20.0` · `react-router-dom ^7.18.4` · `zustand ^5.0.15` ⚠️ |
| `student_react_router_axios_zustand` | `axios ^1.20.0` · `react-router-dom ^7.18.4` · `zustand ^5.0.15` |

⚠️ `student_react_router_axios` 에도 `zustand` 가 설치돼 있고 `store/studentStore.js` 까지 들어 있다. **다음 단계 파일이 앞 폴더에 미리 섞여 들어온 것**이다. 이 폴더의 페이지들은 store 를 import 하지 않으므로 동작에는 상관없다. 교재대로라면 이 단계의 `package.json` 에는 `axios` 와 `react-router-dom` 두 줄만 늘어야 한다.

**진도 현황**

| 자료 | 상태 |
|---|---|
| 「React 중급」 1~4장 + 부록 A·B | ✅ 전체 반영 (6-1 ~ 6-4절) |
| 준비 운동 1 · 2 | ✅ 실물 대조 (6-5, 6-12절) |
| 실습 6-1 ~ 6-10 (Axios + Router) | ✅ 실물 대조 (6-6 ~ 6-11절) |
| 실습 6-11 ~ 6-14 (Zustand) | ✅ 실물 대조 (6-13 ~ 6-16절) |

**5부 → 6부, 무엇이 달라지는가 (실습 교재 첫 표)**

| 주제 | 5부 (`student_react_first`) | 6부 (`student_react_router_axios` → `…_zustand`) |
|---|---|---|
| 서버 부르기 | `fetch` + `request()` 함수 | **axios 인스턴스** |
| 실패 판정 | `if (!response.ok)` 직접 | **axios 가 스스로 오류를 던진다** |
| 본문 꺼내기 | `await response.json()` | **`response.data`** |
| 오류 메시지 | `request()` 안에서 처리 | **가로채기(interceptor)가 한곳에서** |
| 화면 개수 | 하나 (목록과 폼이 함께) | **셋 (목록 · 등록 · 수정)** |
| 수정 중인 학생 | `editingId` state | **주소의 `:id` (`useParams`)** |
| 수정 버튼 | `onClick={() => onEdit(id)}` | **`<Link to={`/edit/${id}`}>`** |
| 등록 후 목록 갱신 | `loadStudents()` 를 직접 다시 호출 | **목록 페이지로 옮겨 가면 저절로** |
| 새로고침하면 | 등록 모드로 돌아간다 | **보고 있던 화면 그대로** |
| 그대로 쓰는 것 | — | `validation.js` · `studentData.js` · `MessageBox.jsx` |
| 상태를 두는 곳 | 페이지의 `useState` | **store** (여러 페이지가 함께 보는 값) |
| 메시지 나르기 | `navigate` 에 실어 보냄 | **store 에 넣기만 하면 됨** |
| 목록 다시 열기 | 서버를 다시 부름 | **갖고 있던 목록을 바로 보여 줌** |

**실습 목록**

| 실습 | 내용 | 주로 쓰는 것 |
|---|---|---|
| 6-1 | 프로젝트 복사하고 라이브러리 설치하기 | `npm install` |
| 6-2 | axios 인스턴스 만들기 | `axios.create`, `interceptors` |
| 6-3 | studentApi 를 axios 로 바꾸기 | `client.get / post / put / delete` |
| 6-4 | 스타일 먼저 다듬기 | CSS |
| 6-5 | 라우터 감싸기와 페이지 나누기 | `BrowserRouter`, `Routes`, `Route` |
| 6-6 | 목록 페이지 만들기 | `useEffect`, `useLocation` |
| 6-7 | 등록·수정 페이지 만들기 | `useParams`, `useNavigate` |
| 6-8 | 표의 수정 버튼을 링크로 | `Link` |
| 6-9 | 내비게이션과 없는 주소 | `NavLink`, `path="*"` |
| 6-10 | 확인과 배포 | `npm run build`, `preview` |
| 6-11 | Zustand 설치와 store 만들기 | `create`, `set`, `get` |
| 6-12 | 목록 페이지를 store 에 잇기 | 선택자(selector) |
| 6-13 | 메시지를 store 로 옮기기 | `AppMessage` |
| 6-14 | 폼 페이지를 store 에 잇기 | `saveStudent` |

> **■ Zustand 는 라우터로 페이지를 나눈 뒤에 넣는다(6-11 부터).** 페이지가 하나뿐일 때는 쓸 자리가 없기 때문이다. 6-10 까지 마치고 동작을 확인한 다음 넘어간다.

---

## 6-1. 무엇이 불편했나 — 세 라이브러리가 푸는 세 문제

### 개념

| 라이브러리 | 무엇이 불편했나 | 무엇을 대신해 주나 |
|---|---|---|
| **Axios** | `fetch` 는 **실패를 오류로 보지 않아** 매번 직접 확인해야 한다 | 실패하면 스스로 오류를 던지고, 설정을 한곳에 모아 준다 |
| **React Router** | 화면이 여러 개여도 **주소는 하나**여서 새로고침·뒤로 가기·공유가 안 된다 | 주소마다 다른 화면을 보여 주고, 주소에 값을 담을 수 있게 한다 |
| **Zustand** | 여러 컴포넌트가 같은 값을 보려면 **공통 부모까지 올렸다가 다시 내려야** 한다 | 값을 컴포넌트 트리 **밖**에 두고 필요한 곳이 직접 꺼내 쓰게 한다 |

**셋은 서로 독립적이다.** Axios 만 써도 되고 Router 만 써도 된다. 다만 6부 실습은 **Axios → Router → Zustand** 순서로 넣는다. **앞의 것이 뒤의 것을 부르는 순서이기 때문이다.**

⚠️ **Router 를 넣으면 화면이 여러 페이지로 나뉜다. 그러면 페이지끼리 같은 값을 봐야 하는 일이 생기고, 그때 Zustand 가 쓸모 있어진다.** Router 없이 Zustand 만 넣으면 과하다. 순서에 이유가 있다.

**라이브러리를 고르는 기준**

| 기준 | 무엇을 보나 |
|---|---|
| 지금 불편한가 | **아직 불편하지 않다면 넣지 않는 것이 맞다** |
| 배우는 데 드는 시간 | 팀 전체가 익혀야 하므로 간단할수록 좋다 |
| 관리되고 있는가 | 마지막 갱신 시점과 열려 있는 이슈 수를 본다 |
| 크기 | 브라우저가 내려받을 용량. 번들 크기로 확인한다 |
| 빠져나갈 수 있는가 | 나중에 걷어내기 쉬운가. **코드 곳곳에 스며드는 것은 위험하다** |

> **■ 이 교재의 앱에서 Axios 는 `api` 폴더 안에만, Router 는 `App.jsx` 와 `pages` 안에만, Zustand 는 `store` 파일 안에만 있다.** 그래서 하나를 걷어내도 나머지가 무너지지 않는다 — "빠져나갈 수 있는가"의 답.

🔗 5-11절 "useMemo 는 느려진 뒤에 넣는다", 5-12-7절 "넣어야 할 이유가 생긴 뒤에 넣는다"와 같은 태도다. **도구는 불편해진 뒤에 넣는다.**

---

## 6-2. Axios — 서버 부르기

### 개념

**fetch 의 불편한 점 세 가지**

```js
const response = await fetch(url, options);

// (1) 404 나 500 도 fetch 에게는 "성공" 이다. 직접 확인해야 한다
if (!response.ok) {
    throw new Error("실패했습니다");
}

// (2) 본문을 꺼내려면 한 번 더 기다려야 한다
const data = await response.json();

// (3) 보낼 때는 손으로 문자열로 바꾸고 헤더도 적어야 한다
await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(student),
});
```

**(1)이 특히 헷갈린다.** `fetch` 는 **"서버에 닿았는가"만 본다.** 서버가 404 를 주어도 요청 자체는 성공한 것으로 본다. 그래서 4·5부에서는 `request()` 라는 함수를 따로 만들어 이 셋을 한곳에 모아 두었다(🔗 4-10절). **Axios 는 그 함수를 대신해 준다.**

**axios 가 대신해 주는 것**

```js
// axios — 위 코드와 같은 일
const response = await axios.get(url);
const data = response.data;            // 이미 객체다

await axios.post(url, student);        // 객체를 그대로 넘긴다
```

| 하는 일 | fetch | axios |
|---|---|---|
| 실패 판정 | `response.ok` 를 직접 확인 | **스스로 오류를 던진다** |
| 본문 꺼내기 | `await response.json()` | **`response.data`** |
| 보낼 때 | `JSON.stringify(body)` | **객체를 그대로** |
| 헤더 | 요청마다 적는다 | **인스턴스에 한 번** |
| 주소 | 전체를 매번 적는다 | **`baseURL` 뒤의 경로만** |
| 설치 | 필요 없다 (브라우저 내장) | `npm install axios` |

⚠️ **axios 를 쓴다고 fetch 를 몰라도 되는 것은 아니다.** axios 안에서도 결국 브라우저의 요청 기능을 쓴다. **fetch 가 무엇을 하는지 알아야 axios 가 무엇을 줄여 주는지 보인다.**

**인스턴스 — 설정을 한 번만**

`axios.get` 을 그대로 써도 되지만, 보통은 **`create` 로 "내 설정이 담긴 axios"를 하나 만들어 둔다.** 주소와 헤더를 매번 적지 않아도 된다.

```js
import axios from "axios";

export const client = axios.create({
    baseURL: "http://localhost:8080",
    headers: { "Content-Type": "application/json" },
});

// 이제 뒷부분만 적으면 된다
client.get("/api/students");    // → http://localhost:8080/api/students
```

서버 주소가 바뀌면 이 파일 한 줄만 고치면 된다.

**가로채기(interceptor) — 오고 가는 길목에 세우는 검문소**

인터셉터는 **요청이 나가기 직전이나 응답이 돌아온 직후에 끼어드는 함수**다. 모든 요청에 공통으로 적용할 일이 있을 때 쓴다.

```js
// 응답 가로채기 — 오류 메시지를 우리 것으로 바꾼다
client.interceptors.response.use(
    (response) => response,                 // 성공은 그대로 통과
    (error) => {
        const status = error.response?.status;
        const message =
            error.response?.data?.message ??
            DEFAULT_MESSAGES[status] ??
            "서버와 통신하지 못했습니다.";
        return Promise.reject(new Error(message));
    }
);
```

**이 열 줄 덕분에 화면 쪽 코드는 `error.message` 만 읽으면 된다.** 상태 코드를 보고 문구를 고르는 일이 컴포넌트마다 반복되지 않는다.

| 자리 | 주로 하는 일 |
|---|---|
| 요청 가로채기 | **로그인 토큰을 헤더에 붙이기**, 요청을 기록에 남기기 |
| 응답 가로채기 (성공) | 응답 모양을 다듬기 |
| 응답 가로채기 (실패) | **오류 메시지 통일, 401 이면 로그인 화면으로 보내기** |

🔗 백엔드 PART 의 Spring Security / JWT 를 붙이면 **요청 가로채기에서 `Authorization: Bearer …` 헤더를 붙이는 것**이 전형적인 쓰임이다.

⚠️ **`error.response` 는 "서버가 응답을 주기는 했는데 실패였다"는 뜻이다.** 서버가 꺼져 있거나 주소가 틀리면 **응답 자체가 없어서 `error.response` 가 `undefined`** 다. 그래서 `?.` 를 붙여 두 경우를 함께 처리한다.

⚠️ **`Network Error` 만 뜨고 상태 코드가 없다** → 서버가 꺼져 있거나 주소가 틀렸다. `error.response` 가 없는 경우.

⚠️ **`blocked by CORS policy`** → **서버 설정 문제다. axios 로 바꾼다고 해결되지 않는다.** 개발 서버 주소(5173)가 서버의 허용 목록에 있어야 한다.

**한눈에 보는 대응표**

| 5부 (fetch) | 6부 (axios) | 설명 |
|---|---|---|
| `fetch(url)` | `client.get(path)` | 목록·단건 조회 |
| `fetch(url, { method: "POST", body })` | `client.post(path, obj)` | 등록 |
| `fetch(url, { method: "PUT", body })` | `client.put(path, obj)` | 수정 |
| `fetch(url, { method: "DELETE" })` | `client.delete(path)` | 삭제 |
| `await response.json()` | `response.data` | 본문 꺼내기 |
| `if (!response.ok) throw` | (자동) | 실패 판정 |
| `request()` 함수 | `create` + `interceptors` | 공통 처리 |

---

## 6-3. React Router — 화면 나누기

### 개념

**SPA 란 무엇이고, 왜 주소를 따로 다뤄야 하나**

SPA 는 **Single Page Application** — "한 장짜리 웹 애플리케이션"이다. 옛 방식과 견주면 이름이 붙은 이유가 보인다.

```
옛 방식 — 화면마다 HTML 파일이 있다
  /list     → 서버 → list.html   (화면 전체를 새로 받는다)
  /new      → 서버 → new.html    (또 전체를 새로 받는다)
  /edit/3   → 서버 → edit.html   (또 전체를 새로 받는다)

SPA — HTML 은 하나뿐이고 JavaScript 가 내용을 바꾼다
  처음 한 번   → 서버 → index.html + JavaScript
  그다음부터   → 서버 → JSON 데이터만
                 화면은 JavaScript 가 갈아 끼운다
```

**5부에서 만든 앱이 바로 SPA 다.** `index.html` 에는 `<div id="root"></div>` 빈 상자 하나뿐이었고, 그 안을 React 가 채웠다(🔗 5-13-2절).

| | 옛 방식 | SPA |
|---|---|---|
| HTML 파일 | 화면마다 하나씩 | **전체에 하나** |
| 화면을 바꿀 때 | 서버에서 HTML 을 새로 받는다 | JavaScript 가 내용을 갈아 끼운다 |
| 화면 깜빡임 | 있다 | 없다 |
| 서버와 주고받는 것 | HTML 전체 | 필요한 데이터(JSON)만 |
| 첫 로딩 | 빠르다 | **조금 느리다** (JavaScript 를 받아야 한다) |
| 이후 이동 | 매번 기다린다 | 빠르다 |

**빠르고 깜빡이지 않는 대신, 잃은 것이 하나 있다. 주소다.**

옛 방식에서 주소가 바뀐 것은 **브라우저가 한 일**이었다. 링크를 눌러 서버에 요청했으니 브라우저가 "지금 이 주소에 있다"고 기록해 둔 것. **SPA 는 서버에 요청하지 않고 JavaScript 가 화면만 바꾼다. 브라우저는 화면이 바뀐 줄도 모르므로 주소도 그대로다.**

```
5부 앱에서 실제로 일어난 일
  목록 화면          주소: http://localhost:5173/
  수정 버튼 클릭     주소: http://localhost:5173/   ← 그대로다
  취소 버튼 클릭     주소: http://localhost:5173/   ← 여전히 그대로
```

| 불편 | 무슨 일이 일어나나 |
|---|---|
| **새로고침하면 사라진다** | 주소가 `/` 이므로 새로고침하면 앱이 처음부터 다시 시작한다. 3번 학생을 수정하던 중이었어도 등록 모드로 돌아간다. **수정 중이라는 사실이 화면 안의 state 에만 있었기 때문** |
| **주소를 공유할 수 없다** | "3번 학생 수정 화면 좀 봐"라며 링크를 줄 수 없다. 즐겨찾기에 담아 둘 수도 없다 |
| **뒤로 가기가 안 된다** | 브라우저는 화면이 바뀐 것을 모르므로 되돌아갈 자리도 없다. 뒤로 가기를 누르면 **앱에 들어오기 전 페이지로 나가 버린다** |

**셋 다 원인이 같다 — "지금 어느 화면인가"가 주소에 담겨 있지 않다.**

**React Router 가 하는 일은 두 가지다**

| 하는 일 | 설명 |
|---|---|
| **주소를 바꿔 준다** | 서버에 요청하지 않고 주소창의 글자만 바꾼다. 브라우저가 원래 갖고 있는 기능(**History API**)을 쓴다. 그래서 깜빡임 없이 주소만 바뀐다 |
| **주소에 맞는 화면을 고른다** | 주소가 바뀌면 그것을 알아채고, 미리 짝지어 둔 컴포넌트를 그린다 |

브라우저가 하던 일을 JavaScript 가 대신하는 셈이다. 그래서 주소가 화면을 따라오게 되고, 앞의 세 가지 불편이 한꺼번에 사라진다.

⚠️ **SPA 라고 해서 화면이 하나뿐이라는 뜻은 아니다.** 사용자가 보는 화면은 여러 개일 수 있다. **"HTML 파일이 하나"라는 뜻이다.**

⚠️ **라우터가 주소를 바꾸는 것은 브라우저 안에서만 일어나는 일이다.** 서버는 `/edit/3` 이라는 주소를 모른다. 그래서 그 주소에서 새로고침하면 서버가 404 를 돌려준다(→ 뒤의 "배포할 때").

**BrowserRouter · Routes · Route — 세 겹**

**`BrowserRouter` 가 주소를 지켜보고, `Routes` 가 그중 맞는 것 하나를 고르고, `Route` 가 주소와 화면을 짝지어 둔다.**

```jsx
// main.jsx — 앱 전체를 감싼다
<BrowserRouter>
    <App />
</BrowserRouter>

// App.jsx — 주소와 화면을 짝짓는다
<Routes>
    <Route path="/"         element={<StudentListPage />} />
    <Route path="/new"      element={<StudentFormPage />} />
    <Route path="/edit/:id" element={<StudentFormPage />} />
    <Route path="*"         element={<NotFoundPage />} />
</Routes>
```

| path | 뜻 |
|---|---|
| `/` | 정확히 이 주소일 때 |
| `/new` | 정확히 이 주소일 때 |
| `:id` | **자리를 비워 둔다.** `/edit/3` 이면 `id` 가 `"3"` 이 된다 |
| `*` | 위 어느 것과도 맞지 않는 주소 |

- **`Routes` 바깥에 둔 것은 주소가 바뀌어도 그대로 남는다.** 머리말이나 바닥글처럼 모든 화면에 보여야 하는 것은 `Routes` 바깥에 둔다.
- `element={<StudentListPage />}` — **컴포넌트 이름이 아니라 JSX(그릴 결과)를 넘긴다.** `element={StudentListPage}` 가 아니다.
- 한 컴포넌트(`StudentFormPage`)가 두 주소(`/new`, `/edit/:id`)를 맡을 수 있다 — 등록과 수정은 화면이 거의 같기 때문.
- 실물 주석: **"위에서 아래로 훑어 맞는 것을 고르는 것이 아니라, React Router 가 가장 잘 맞는 하나를 골라 준다."** 그래서 `*` 를 맨 아래에 두지 않아도 동작은 하지만, 읽기 좋게 맨 아래 둔다.

⚠️ **`useNavigate() may be used only in the context of a <Router>`** → `BrowserRouter` 로 감싸지 않았다. `main.jsx` 를 확인.

**Link 와 NavLink — 화면 옮기기**

```jsx
// 안 된다 — 페이지를 통째로 새로 내려받는다
<a href="/new">학생 등록</a>

// 이렇게 — 주소만 바꾼다
<Link to="/new">학생 등록</Link>

// NavLink — 지금 보고 있는 주소면 알려 준다
<NavLink to="/" end
    className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}>
    학생 목록
</NavLink>
```

**`a` 태그를 그대로 쓰면 브라우저가 페이지 전체를 새로 내려받는다. 그러면 React 가 갖고 있던 state 도 모두 사라진다.** `Link` 는 주소만 바꾸고 화면은 React 가 갈아 끼운다.

⚠️ **`NavLink` 의 `end` 는 "주소가 정확히 이것일 때만"이라는 뜻이다.** 붙이지 않으면 `/` 는 `/new` 와 `/edit/3` 에서도 **"여기서 시작하는 주소"로 쳐서** 맞는 것으로 보아, 두 링크가 동시에 강조된다.

- `NavLink` 의 `className` 에는 **문자열 대신 함수를 넘길 수 있다.** React Router 가 `{ isActive }` 를 넣어 부른다. 🔗 4-5절 구조 분해가 매개변수 자리에서 쓰인다.
- `Link` 는 결국 `<a>` 태그로 그려진다 → 가운데 버튼 클릭으로 **새 탭에서 열기**도 된다. 링크이기 때문이다.

**useParams — 주소에서 값 읽기**

```jsx
// <Route path="/edit/:id" element={<StudentFormPage />} />
function StudentFormPage() {
    const { id } = useParams();       // "/edit/3" 이면 "3"
    const isEditing = id !== undefined;
    ...
}
```

⚠️ **`useParams` 가 돌려주는 값은 언제나 문자열이다.** 숫자로 써야 하면 `Number(id)` 로 바꾼다. 서버에 그대로 넘기는 경우에는 (URL 에 들어가므로) 문자열이어도 대개 문제가 없다. `/new` 처럼 그 자리가 없으면 `undefined`.

🔗 5-4절 **"계산해서 알 수 있는 값은 state 로 두지 않는다"** — `isEditing` 이 5부에서는 `editingId !== null`, 6부에서는 `id !== undefined`. 둘 다 계산값이다.

**useNavigate — 코드로 옮겨 가기 / useLocation — 실어 보낸 값 받기**

버튼을 눌러서가 아니라, **어떤 일이 끝난 뒤에** 화면을 옮겨야 할 때가 있다. 등록에 성공한 다음 목록으로 돌아가는 경우.

```jsx
const navigate = useNavigate();

await createStudent(studentData);

// 목록으로 옮겨 가면서 보여 줄 문구를 함께 실어 보낸다
navigate("/", { state: { message: "등록되었습니다." } });

// 받는 쪽
const location = useLocation();
const text = location.state?.message;
```

| 쓰는 법 | 무엇을 하나 |
|---|---|
| `navigate("/")` | 그 주소로 옮겨 간다 |
| `navigate(-1)` | **뒤로 한 번** 간다 |
| `navigate("/", { replace: true })` | 옮겨 가되 **뒤로 가기 기록을 남기지 않는다** (로그인 후 이동 등) |
| `navigate("/", { state })` | 값을 실어 보낸다. 받는 쪽은 `useLocation()` |

⚠️ **옮겨 간 페이지는 새로 그려진다(마운트).** 그래서 목록 페이지의 `useEffect` 가 다시 실행되고 목록을 **저절로 다시 불러온다.** 5부에서 `loadStudents()` 를 직접 다시 부르던 일이 없어진다.

⚠️ `location.state` 는 주소창에는 보이지 않는다. **브라우저 히스토리 항목에 붙어 다니는 값**이다. 그래서 `?.` 로 읽는다 — 다른 경로로 들어왔으면 `state` 가 `null` 이다.

**주소도 상태를 담는 자리다 — 이 장에서 가장 중요한 생각**

```jsx
// 5부 — 수정 중인 학생 번호를 state 에 둔다
const [editingId, setEditingId] = useState(null);

// 6부 — 주소에 둔다
const { id } = useParams();
```

**주소에 두면 새로고침해도 남고, 링크로 공유할 수 있고, 뒤로 가기도 동작한다. state 로 두면 셋 다 되지 않는다.** "이 값이 주소에 드러나도 괜찮은가"를 물어보고, 괜찮다면 주소에 두는 편이 낫다.

| 값 | 어디에 둘까 |
|---|---|
| 지금 보고 있는 화면 | **주소** |
| 몇 번 학생을 수정 중인가 | **주소** |
| 검색어, 정렬 순서, 페이지 번호 | **주소 (쿼리 문자열)** — `?q=홍&page=2` |
| 입력 중인 폼 내용 | state. 주소에 드러나면 곤란하다 |
| 로딩 중인가 | state. 잠깐 쓰고 버리는 값이다 |

🔗 백엔드의 `@PathVariable`(`/api/students/{id}`) · `@RequestParam`(`?page=2`) 과 짝을 이룬다. **프론트 주소도 같은 규칙으로 값을 싣는다.**

**배포할 때 — 새로고침하면 404 가 나는 이유와 해결법**

개발 서버에서는 잘 되는데 **배포하면 `/edit/3` 에서 새로고침할 때 404** 가 나는 일이 흔하다.

- 브라우저 안에서 `Link` 로 옮겨 갈 때는 **서버에 묻지 않는다.**
- **새로고침은 다르다.** 브라우저가 서버에 "`/edit/3` 을 주세요" 하고 묻고, 서버는 그런 이름의 파일이 없으니 404 를 돌려준다.

```
빌드 결과물(dist 폴더)에 있는 파일
  index.html
  assets/index-abc123.js
  assets/index-abc123.css
  edit 라는 폴더도, 3 이라는 파일도 없다
```

**고치는 방법은 하나다 — 서버에게 "없는 주소로 오더라도 404 대신 `index.html` 을 돌려줘라"고 알려 준다.** `index.html` 만 열리면 그 안의 JavaScript 가 주소를 읽고 알맞은 화면을 그린다. 이 설정을 **히스토리 폴백(history fallback)** 이라고 부른다.

① **Spring Boot** — 이 교재의 서버. 빌드 결과를 `src/main/resources/static` 에 넣어 함께 배포하는 경우

```java
@Controller
public class SpaForwardController {
    // 앱이 쓰는 주소를 그대로 적는다.
    // 여기 적힌 주소로 오면 index.html 을 대신 돌려준다.
    @GetMapping({ "/", "/new", "/edit/**" })
    public String forward() {
        return "forward:/index.html";
    }
}
```

⚠️ **주소를 하나하나 적는 것이 번거로워 보이지만 이 편이 안전하다.** `/api` 로 시작하는 주소와 `/assets` 안의 js·css 가 여기 걸리지 않기 때문. **패턴 하나로 모든 주소를 잡으면 API 응답까지 `index.html` 로 바뀌어 버린다.** 앱에 주소를 새로 더하면 이 목록에도 더한다. 🔗 백엔드 PART — `@Controller` 가 문자열을 돌려주면 뷰 이름으로, `forward:` 접두사는 서버 안에서 다른 경로로 넘기는 것.

② **Nginx** — `server` 블록 안

```nginx
server {
    listen 80;
    root /var/www/student-app;          # dist 폴더를 올려 둔 자리
    location / {
        # 요청한 파일이 있으면 그것을, 없으면 index.html 을 돌려준다
        try_files $uri $uri/ /index.html;
    }
}
```

`try_files` 는 **왼쪽부터 차례로 찾아보고 있는 것을 돌려준다.** `/assets/index-abc.js` 는 파일이 있으므로 그것을, `/edit/3` 은 없으므로 마지막의 `index.html` 을.

③ **Apache** — `dist` 폴더 안에 `.htaccess`

```apache
<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteBase /
  # 실제로 있는 파일이나 폴더면 그대로 둔다
  RewriteCond %{REQUEST_FILENAME} !-f
  RewriteCond %{REQUEST_FILENAME} !-d
  # 나머지는 전부 index.html 로
  RewriteRule . /index.html [L]
</IfModule>
```

④ **Netlify** — `dist` 폴더 안에 `_redirects` 파일, 한 줄: `/*  /index.html  200`

⑤ **Vercel** — 프로젝트 최상단 `vercel.json`

```json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

| 서버 | 어디에 적나 | 무엇을 적나 |
|---|---|---|
| Spring Boot | `@Controller` 클래스 | `forward:/index.html` |
| Nginx | `server` 블록 | `try_files $uri $uri/ /index.html;` |
| Apache | `dist/.htaccess` | `RewriteRule . /index.html [L]` |
| Netlify | `dist/_redirects` | `/* /index.html 200` |
| Vercel | `vercel.json` | `rewrites` 설정 |
| `npm run preview` | 적을 것 없음 | **이미 되어 있다** |

**설정을 바꿀 수 없을 때 — `HashRouter`**

```jsx
import { HashRouter } from "react-router-dom";

<HashRouter>
    <App />
</HashRouter>

// 주소가 이렇게 바뀐다
// http://example.com/#/edit/3
```

**`#` 뒤의 글자는 서버에 전달되지 않는다.** 서버는 언제나 `/` 만 받으므로 404 가 날 일이 없다. 다만 **주소가 지저분해지고 검색 엔진이 잘 읽지 못하므로**, 설정을 바꿀 수 있다면 `BrowserRouter` 가 낫다.

🔗 1-5절 `<a href="#bottom">` 의 `#` 이 **같은 페이지 안의 위치**를 가리키던 것과 같은 원리다 — `#` 뒤는 브라우저만의 몫.

⚠️ **개발 중에는 되는데 배포하면 새로고침할 때 404** → 히스토리 폴백 설정이 빠졌다. **Router 코드를 잘못 쓴 것이 아니므로 소스는 고칠 필요가 없다.**

⚠️ 설정을 넣은 뒤 **`/api/students` 를 직접 열어 본다.** JSON 이 아니라 `index.html` 이 보인다면 폴백 규칙이 API 주소까지 잡아먹고 있는 것이다. Spring Boot 예제에서 `/api` 를 빼 둔 이유가 그것.

> **■ 확인하기** — `npm run build` 뒤 `npm run preview` 로 열어 `/edit/3` 에서 새로고침이 된다 / 실제 서버에 올린 뒤에도 같은 자리에서 새로고침이 된다 / F12 → Network 에서 `/assets` 의 js·css 가 200 으로 내려온다

---

## 6-4. Zustand — 상태를 트리 밖에 두기

### 개념

**상태 관리 라이브러리는 왜 필요한가**

먼저 짚어 둘 것 — **상태 관리 라이브러리는 React 가 못 하는 일을 해 주는 것이 아니다.** `useState` 와 props 만으로도 모든 앱을 만들 수 있다. 다만 앱이 커지면 같은 불편이 되풀이되고, 그 불편을 덜어 주는 것이 이 라이브러리들이다.

**① 중간 컴포넌트가 쓰지도 않는 값을 나른다 (prop drilling)**

```jsx
// user 를 쓰는 것은 Profile 하나뿐이다
<Layout user={user}>
    <Sidebar user={user}>
        <Profile user={user} />
    </Sidebar>
</Layout>
```

**② 형제끼리 값을 나눌 방법이 없다.** 라우터를 넣으면 이 문제가 또렷해진다. **목록 페이지와 등록 페이지는 형제여서 서로를 모른다.** 둘이 같은 값을 보려면 App 까지 올렸다가 다시 내려야 한다.

**③ 페이지를 옮기면 값이 사라진다.** state 는 그 컴포넌트가 화면에서 사라질 때(언마운트) 함께 없어진다. 그래서 목록 페이지를 떠났다 돌아올 때마다 서버를 다시 부르게 된다.

**④ 값을 바꾸는 코드가 화면 코드에 섞인다.** 목록을 불러오고, 실패를 처리하고, 삭제한 뒤 다시 불러오는 일이 모두 페이지 컴포넌트 안에 있다.

| 불편 | 언제 겪나 | 라이브러리가 해 주는 일 |
|---|---|---|
| props 내려꽂기 | 트리가 세 단계를 넘을 때 | 중간을 건너뛰고 필요한 곳이 직접 꺼낸다 |
| 형제끼리 공유 | 페이지가 여럿일 때 | 공통 부모가 없어도 같은 값을 본다 |
| 페이지를 옮기면 사라짐 | 라우터를 쓸 때 | 트리 밖에 있어 살아남는다 |
| 화면 코드와 섞임 | 데이터 다루는 일이 늘 때 | 값과 그 값을 바꾸는 함수를 한곳에 모은다 |

**5부 앱에서는 넷 중 하나도 겪지 않았다.** 컴포넌트가 두 단계뿐이고 화면도 하나였기 때문. **6부에서 라우터로 페이지를 나눈 뒤에야 ②와 ③이 나타난다.** 그래서 이 순서로 배운다.

> **■ "큰 앱이니까 상태 관리 라이브러리를 쓴다"가 아니라 "이 네 가지 중 하나를 겪고 있으니 쓴다"가 맞다.** 겪지 않았다면 아직 넣을 때가 아니다.

🔗 5-4절 "상태 끌어올리기의 대가"와 "prop drilling → Context · Zustand · Redux"에서 예고한 것이 여기서 풀린다.

**store 만들기 — `create`, `set`, `get`**

Zustand 는 **값을 컴포넌트 트리 밖에 둔다.** `create` 로 상자를 하나 만들고, **그 안에 값과 값을 바꾸는 함수를 함께 넣는다.**

```js
import { create } from "zustand";

export const useStudentStore = create((set, get) => ({
    // ── 값 ──
    students: [],
    loading: false,
    message: null,
    loaded: false,            // 한 번이라도 불러왔는가

    // ── 값을 바꾸는 함수 ──
    showSuccess: (text) => set({ message: { text: text, type: "success" } }),
    clearMessage: () => set({ message: null }),

    loadStudents: async () => {
        set({ loading: true });
        try {
            const data = await fetchStudents();
            set({ students: data, loaded: true });
        } finally {
            set({ loading: false });
        }
    },
}));
```

| 인자 | 하는 일 |
|---|---|
| **`set`** | 값을 바꾼다. `useState` 의 setter 와 성격이 같다 |
| **`get`** | **지금 값을 읽는다.** 함수 안에서 store 의 다른 함수를 부를 때 쓴다 — `get().showSuccess(...)`, `get().loadStudents()` |

- 이름이 **`use` 로 시작하는 이유는 훅처럼 쓰기 때문이다.** 🔗 5-11절 커스텀 훅 규칙 — `use` 로 시작해야 Hooks 규칙 검사가 동작한다.
- ⚠️ **Zustand 의 `set` 은 클래스 컴포넌트의 `setState` 처럼 기존 값과 합쳐 준다(병합).** `useState` 의 setter 가 **통째로 바꾸는 것**과 다르다. 그래서 `...state` 를 펼쳐 넣지 않아도 된다. `set({ loading: true })` 를 해도 `students` · `message` 는 그대로 남는다. 🔗 5-12-1절 "클래스 `setState` 는 합쳐 주고 / 함수의 set 함수는 통째로 바꾼다"
- ⚠️ **다만 합쳐 주는 것은 맨 바깥 한 층뿐이다.** `students` 배열 **안**을 고칠 때는 여전히 새 배열을 만들어 넘겨야 한다(불변성).
- `set` 에 **함수를 넘기면 지금 값을 `state` 로 받는다** — `set((state) => ({ todos: [...state.todos, x] }))`. `useState` 의 함수형 업데이트와 같은 모양(🔗 5-12-6절).

**필요한 조각만 꺼내 쓰기 — 선택자(selector)**

```jsx
function StudentTable() {
    // props 가 필요 없다. 이 컴포넌트가 직접 꺼낸다.
    const students = useStudentStore((s) => s.students);
    return <table>...</table>;
}
```

괄호 안의 함수를 **선택자(selector)** 라고 한다. **"이 상자에서 `students` 만 보겠다"**는 뜻이다. **선택자를 쓰면 그 값이 바뀔 때만 이 컴포넌트가 다시 그려진다.**

```jsx
// 좋지 않다 — loading 이 바뀌어도 표가 다시 그려진다
const store = useStudentStore();

// 이렇게 — students 가 바뀔 때만 다시 그려진다
const students = useStudentStore((s) => s.students);
```

⚠️ **값을 바꿨는데 화면이 안 바뀐다** → store 바깥에서 배열을 직접 고쳤다. `push` 대신 새 배열을 만들어 `set` 에 넘긴다. **불변성 규칙은 `useState` 와 같다.**

**Redux 와 견주기 — 왜 Zustand 를 골랐나**

Redux 는 오랫동안 React 상태 관리의 표준이었고, 지금도 큰 프로젝트에서 많이 쓴다. 이 교재가 Zustand 를 고른 이유는 하나 — **훨씬 간단하기 때문이다.**

```
Redux                                          Zustand
 화면 ─▶ Action Creator ─▶ Dispatcher           화면 ◀──▶ store
          ("무엇을 하겠다" 쪽지)                  (store 안의 함수를 바로 부른다)
             ─▶ Reducer ─▶ State ─▶ 화면
 (값을 바꾸려면 정해진 통로를 거친다)           (화면과 store 사이에 아무것도 없다)
```

| 단계 (Redux) | 하는 일 |
|---|---|
| **Action Creator** | "무엇을 하겠다"는 쪽지를 만든다. 예: `{ type: "student/added" }` |
| **Dispatcher** | 그 쪽지를 store 로 보낸다 |
| **Reducer** | 쪽지를 보고 새 state 를 만들어 돌려준다 |
| **State** | 바뀐 값이 화면으로 흘러간다 |

**"학생 목록을 store 에 넣는다" 하나를 하는 데 필요한 코드 — 세 단계 비교**

① 상자 만들기

```js
// Redux Toolkit — store/studentSlice.js
const studentSlice = createSlice({
    name: "students",
    initialState: { list: [] },
    reducers: {
        setStudents: (state, action) => {
            state.list = action.payload;
        },
    },
});
export const { setStudents } = studentSlice.actions;

// store/index.js
export const store = configureStore({
    reducer: { students: studentSlice.reducer },
});
```

```js
// Zustand — store/studentStore.js
export const useStudentStore = create((set) => ({
    students: [],
    setStudents: (list) => set({ students: list }),
}));
```

Redux 는 파일 둘에 걸쳐 `createSlice` · `configureStore` · `actions` 세 가지가 나온다. **Zustand 는 `create` 하나다.** 값과 그 값을 바꾸는 함수가 한 객체에 나란히 있다.

⚠️ Redux Toolkit 의 reducer 안에서 `state.list = action.payload` 처럼 **직접 고치는 것처럼 쓸 수 있는 것은 안에 immer 가 들어 있기 때문**이다. 겉보기와 달리 불변성은 지켜진다.

② 앱 감싸기

```jsx
// Redux Toolkit — main.jsx
import { Provider } from "react-redux";
import { store } from "./store";

<Provider store={store}>
    <App />
</Provider>

// Zustand — main.jsx 고칠 것이 없다
<App />
```

**Redux 는 store 를 앱 전체에 내려 주려고 `Provider` 로 감싸야 한다. Zustand 의 store 는 컴포넌트 트리 밖에 있으므로 감쌀 것이 없다.**

③ 컴포넌트에서 쓰기

```jsx
// Redux Toolkit
const list = useSelector((s) => s.students.list);
const dispatch = useDispatch();
dispatch(setStudents(data));          // 바꿀 때는 "쪽지"를 만들어 보낸다

// Zustand
const students = useStudentStore((s) => s.students);
const setStudents = useStudentStore((s) => s.setStudents);
setStudents(data);                    // 바꿀 때는 함수를 바로 부른다
```

**읽는 방식은 비슷하다**(둘 다 선택자). **다른 것은 바꿀 때다.** Redux 는 `dispatch` 를 거치고, Zustand 는 store 에 담아 둔 함수를 그대로 부른다.

| 항목 | Redux Toolkit | Zustand |
|---|---|---|
| 상자 만들기 | slice + `configureStore` | `create` 한 번 |
| 앱 감싸기 | `<Provider>` 로 감싸야 한다 | 필요 없다 |
| 값 읽기 | `useSelector` | 훅에 선택자를 넘긴다 |
| 값 바꾸기 | `dispatch(action)` 으로 간접 | store 의 함수를 직접 부른다 |
| 새 개념 | action · reducer · dispatch · slice | create · set · get |
| 비동기 처리 | `createAsyncThunk` 같은 도구 | **그냥 async 함수** |
| 기록 도구 | Redux DevTools (매우 강력) | middleware 로 붙일 수 있다 |
| 배우는 시간 | 길다 | 짧다 |

⚠️ **Redux 가 한 단계를 더 거치는 것은 낭비가 아니다.** 값을 바꾸는 길이 `dispatch` 하나뿐이라 **"언제 누가 무엇을 바꿨는가"가 전부 기록으로 남는다.** Redux DevTools 가 강력한 이유. 사람이 여럿이고 상태 변화가 얽혀 있을 때는 이 기록이 큰 도움이 된다. **다만 그만큼 적을 코드가 늘어난다.** 우리 앱은 값이 다섯 개고 바꾸는 곳도 몇 군데 되지 않는다 — 그 규모에서는 action 과 reducer 를 만드는 일이 이득보다 부담이 크다.

**크기도 재 보았다 — 이 교재의 앱에 zustand 를 넣기 전과 뒤**

| | 넣기 전 (Axios + Router) | 넣은 뒤 (+ Zustand) |
|---|---|---|
| 번들 크기 | 316.76 kB | 318.11 kB |
| gzip 압축 후 | 102.98 kB | 103.45 kB |
| 늘어난 양 | — | **1.35 kB (gzip 0.47 kB)** |

🔗 5-13-9절 — 5부 빌드는 약 196 kB 였다. Axios + Router 를 더해 약 317 kB.

⚠️ **Zustand 본체는 2 kB 가 채 되지 않는다.** 그래서 "일단 넣어 두고 안 쓰면 그만"이라고 생각하기 쉽지만, **크기보다 중요한 것은 코드가 늘어나는 정도다.** 필요해질 때 넣는다.

**Zustand 의 장점 여덟 가지 (소개 글들이 흔히 꼽는 것)**

| 장점 | 무슨 뜻인가 |
|---|---|
| 단순한 API | 배울 것이 `create` · `set` · `get` 셋뿐이다 |
| Provider 가 필요 없다 | 앱을 감싸는 코드가 없다. 설치하고 바로 쓴다 |
| 꼭 필요한 곳만 다시 그린다 | 선택자로 꺼낸 값이 바뀔 때만 그 컴포넌트가 다시 그려진다 |
| **비동기를 그냥 쓴다** | async 함수를 그대로 store 에 넣는다. 별도 도구가 필요 없다 |
| 가볍다 | 본체가 1 kB 남짓이다 |
| 틀에 박힌 코드가 없다 | action · reducer · type 을 만들지 않는다 |
| **React 밖에서도 쓴다** | 컴포넌트가 아닌 곳에서도 store 를 읽고 쓸 수 있다 |
| middleware 로 늘린다 | 기록 남기기, 브라우저에 저장하기, DevTools 붙이기 |

- **넷째 — Redux 와 견주면 차이가 크다.** Redux 에서 서버를 부르려면 **Redux Thunk 나 Redux Saga** 같은 도구를 따로 붙여야 했다. Zustand 는 store 안에 async 함수를 그냥 적으면 된다(실습 앱의 `loadStudents`).
- **일곱째 — store 는 컴포넌트 밖에 있으므로 React 와 상관없는 코드에서도 쓸 수 있다.** 훅이 아니라 **함수로** 부르면 된다.

```js
// 컴포넌트 안 — 훅으로 쓴다. 값이 바뀌면 다시 그려진다
const students = useStudentStore((s) => s.students);

// 컴포넌트 밖 — 함수로 쓴다. 다시 그리는 일과 무관하다
const students = useStudentStore.getState().students;
useStudentStore.getState().showError("문제가 생겼습니다");
```

🔗 예: axios 응답 가로채기(컴포넌트가 아님) 안에서 `useStudentStore.getState().showError(...)` 를 부를 수 있다. **Hooks 규칙(5-8절) 때문에 컴포넌트 밖에서는 `useStudentStore(...)` 를 부를 수 없지만 `getState()` 는 된다.**

> **middleware** 는 store 에 기능을 덧입히는 장치. 자주 쓰는 셋 — **`persist`**(값을 브라우저에 저장해 새로고침해도 남게), **`devtools`**(Redux DevTools 에 연결해 값이 바뀐 기록을 보여 줌), **`immer`**(중첩된 객체를 편하게 고치게).

**Context API 와 견주기**

Context 는 **React 에 들어 있는 기능이라 설치가 필요 없다.** 값을 트리 아래로 한 번에 내려 줄 수 있어서 props 내려꽂기를 없애 준다.

**다만 성질이 다르다. Context 는 "값을 나르는 통로"일 뿐 값을 담아 두는 상자가 아니다.** 값은 여전히 어느 컴포넌트의 `useState` 에 있어야 하고, 그 값이 바뀌면 **Context 를 쓰는 컴포넌트가 모두 다시 그려진다.** 선택자로 필요한 조각만 고를 수 없다.

| | Context API | Zustand |
|---|---|---|
| 설치 | 필요 없다 | `npm install zustand` |
| 값이 사는 곳 | 어느 컴포넌트의 state | **컴포넌트 트리 밖** |
| 일부만 꺼내기 | 안 된다 | **선택자로 된다** |
| 값이 바뀌면 | 쓰는 컴포넌트가 **모두** 다시 그려진다 | **그 값을 꺼낸 곳만** 다시 그려진다 |
| 어울리는 값 | 테마·로그인 정보처럼 **거의 안 바뀌는 것** | **자주 바뀌는 것** |

이 셋 말고도 **Recoil, Jotai, MobX** 처럼 같은 일을 하는 것이 여럿 있다.

> **■ 고르는 기준은 간단하다 — 거의 안 바뀌는 값이면 Context, 자주 바뀌는데 규모가 작으면 Zustand, 사람이 여럿이고 흐름이 복잡하면 Redux.** 확신이 서지 않으면 가장 간단한 것부터 시작한다. 걷어내기도 그쪽이 쉽다.

**언제 쓰고 언제 쓰지 않나**

| 상황 | 판단 |
|---|---|
| 컴포넌트 두 단계, 값 대여섯 개 | **필요 없다.** props 로 충분하다 |
| 페이지가 여럿이고 같은 목록을 본다 | 쓸 만하다 |
| 중간 컴포넌트가 쓰지도 않는 props 를 넘긴다 | 쓸 때가 되었다 |
| 로그인 정보처럼 앱 전체가 본다 | 쓴다. Context 도 후보다 |

**무엇을 store 에 두고 무엇을 두지 않을까 — 기준: "이 값을 두 군데 이상에서 보는가"**

| 값 | 어디에 | 이유 |
|---|---|---|
| `students` | **store** | 목록 페이지와 폼 페이지가 함께 본다 |
| `loading` · `listError` | **store** | 목록을 불러오는 일과 한 몸이다 |
| `message` | **store** | 어느 페이지에서 넣든 한곳에서 보여 준다 |
| `form` (입력 중인 값) | 페이지의 `useState` | 그 화면에서만 쓰고 버린다 |
| 폼의 불러오기 표시 | 페이지의 `useState` | 목록의 로딩과 다른 값이다 |

⚠️ **`form` 을 store 에 넣지 않은 이유** — 입력 중인 값은 그 화면에서만 쓰고 버린다. **store 에 넣으면 폼을 떠났다 돌아왔을 때 지난번 입력이 남아 있어 오히려 헷갈린다.**

⚠️ **"전역 상태"라는 말 때문에 모든 것을 store 에 넣어야 할 것 같지만 그렇지 않다. `useState` 는 없어지지 않는다.** 한 화면에서만 쓰는 값은 그 화면에 두는 편이 낫다.

**Zustand 를 넣으면 무엇이 없어지나 — 두 가지**

첫째, **페이지 사이로 값을 나르던 코드가 없어진다.**

```jsx
// Zustand 이전 — 보내는 쪽
navigate("/", { state: { message: "등록되었습니다." } });
// Zustand 이전 — 받는 쪽
const location = useLocation();
const [message, setMessage] = useState(
    location.state?.message ? { text: location.state.message, type: "success" } : null
);

// Zustand 이후 — 넣는 쪽
showSuccess("등록되었습니다.");
// Zustand 이후 — 보여 주는 쪽
const message = useStudentStore((s) => s.message);
```

둘째, **페이지를 옮길 때마다 서버를 다시 부르던 일이 없어진다.** state 는 페이지가 바뀌면 사라지지만 store 는 남아 있기 때문.

```jsx
// 한 번도 안 불러왔을 때만 부른다
useEffect(() => {
    if (!loaded) {
        loadStudents();
    }
}, [loaded, loadStudents]);
```

| | 라우터만 | 라우터 + Zustand |
|---|---|---|
| 메시지 나르기 | `navigate` 에 실어 보낸다 | store 에 넣는다 |
| 메시지 그리는 곳 | 페이지마다 | **App 에서 한 번** |
| 목록 페이지의 `useState` | 네 개 | **없음** |
| 페이지를 다시 열면 | 서버를 다시 부른다 | **갖고 있던 목록을 바로 보여 준다** |

⚠️ **값이 store 에 남아 있다는 것은 양날이다.** 화면이 빨라지지만, 다른 사람이 그 사이에 데이터를 바꿨다면 **낡은 목록**을 보게 된다. 그래서 실습 앱은 **등록·수정·삭제에 성공하면 store 안에서 목록을 다시 불러 둔다.**

**실습 앱의 전체 구조**

```
                       브라우저 주소
                            |
                   React Router 가 읽는다
                            |
       +--------------------+--------------------+
       |                    |                    |
      "/"                 "/new"            "/edit/:id"
       |                    |                    |
 StudentListPage      StudentFormPage      StudentFormPage
       |                    |                    |
       | students           | saveStudent        | fetchStudent
       | loading            | showError          | (한 명만 직접)
       | removeStudent      |                    |
       +----------+---------+--------------------+
                  |
                  v
     +---------------------------+
     |  store/studentStore.js    |  ← Zustand
     |  students   loading       |
     |  listError  message       |
     |  loadStudents()           |
     |  saveStudent()            |
     |  removeStudent()          |
     +-------------+-------------+
                   |
                   v
     +---------------------------+
     |  api/studentApi.js        |
     |  api/client.js            |  ← Axios
     +-------------+-------------+
                   |
                   v
             Spring Boot 서버
```

**값이 흐르는 방향은 언제나 같다.** 화면이 store 의 함수를 부르고 → store 가 Axios 로 서버를 부르고 → 돌아온 값을 store 에 넣으면 → 그 값을 꺼내 쓰던 화면이 다시 그려진다.

```
① 사용자가 삭제 버튼을 누른다
② StudentListPage 가 removeStudent(3) 를 부른다
③ store 가 api 의 deleteStudent(3) 를 부른다
④ Axios 가 서버에 DELETE 를 보낸다
⑤ store 가 showSuccess(...) 와 loadStudents() 를 부른다
⑥ students 와 message 가 바뀐다
⑦ 그 값을 꺼내 쓰던 표와 AppMessage 가 다시 그려진다
```

**화면 컴포넌트 어디에도 `fetch` 나 `axios` 가 없다. 반대로 store 와 api 어디에도 JSX 가 없다. 이 구분이 이 구조의 핵심이다.**

| 자리 | 파일 | 맡는 일 |
|---|---|---|
| 화면 | `pages/` · `components/` | 받은 값을 그리고, 사용자의 동작을 알린다 |
| 상태 | `store/studentStore.js` | 값을 갖고, 값을 바꾸는 순서를 정한다 |
| 통신 | `api/client.js` · `studentApi.js` | 서버와 주고받고, 오류를 우리 메시지로 바꾼다 |
| 규칙 | `lib/validation.js` | 값이 올바른지 검사한다 |
| 변환 | `lib/studentData.js` | 폼 값과 서버 구조 사이를 오간다 |
| 주소 | `App.jsx` 의 `Routes` | 어느 주소에 어느 화면인지 정한다 |

⚠️ **폼 페이지가 `fetchStudent` 한 명만은 store 를 거치지 않고 api 를 직접 부른다.** 그 학생 한 명은 폼 화면에서만 쓰고 버리므로 store 에 둘 이유가 없기 때문 — "두 군데 이상에서 보는가" 기준의 적용.

⚠️ **세 라이브러리가 서로를 모른다는 점에 주목.** Axios 는 라우터를 모르고, 라우터는 store 를 모른다. 그래서 **하나를 걷어내도 나머지가 무너지지 않는다.** 예를 들어 Axios 를 fetch 로 되돌려도 고칠 곳은 `api` 폴더뿐이다(실물에 `studentApiFetch.js` 가 그대로 남아 있어 직접 확인할 수 있다).

🔗 **백엔드의 Controller → Service → Repository 계층 구조와 같은 생각이다.** 화면(pages) ≈ Controller, store ≈ Service(순서·규칙), api ≈ Repository(외부와 주고받기). 계층이 서로의 속을 모르므로 하나를 바꿔도 나머지가 버틴다.

**세 라이브러리 한눈에 (부록 A)**

| | Axios | React Router | Zustand |
|---|---|---|---|
| 푸는 문제 | 서버 통신의 반복 | 화면이 하나뿐 | 값을 멀리 나르기 |
| 설치 | `npm i axios` | `npm i react-router-dom` | `npm i zustand` |
| 핵심 | `create` · `interceptors` | `Routes` · `Link` · `useParams` | `create` · selector |
| 닿는 파일 | `api/` 폴더 | `App.jsx` · `pages/` | store 파일 |
| 없으면 | fetch 로 가능 | 화면 하나로 가능 | props 로 가능 |
| 언제 넣나 | 요청이 서너 개 넘을 때 | 화면이 둘 이상일 때 | 트리가 깊어질 때 |

> **셋 다 "없으면 못 하는 것"이 아니라 "있으면 덜 반복하는 것"이다. 넣기 전에 지금 무엇이 반복되고 있는지를 먼저 보라.**

---

## 6-5. 준비 운동 1 — 라우터만 먼저 보기 (`todolist_react_router`)

### 개념

6부는 세 가지를 한꺼번에 다룬다. 그중 라우터가 가장 낯선데, 정작 라우터를 넣는 실습 6-5 는 이미 Axios 로 바꾼 코드 위에서 시작한다. 그래서 **"어디까지가 라우터가 하는 일인가"가 잘 보이지 않는다.** 그래서 **서버도 Axios 도 상태 관리도 없는 아주 작은 프로젝트**로 라우터만 떼어서 먼저 본다. 부록 E 의 TodoList 에 주소 두 개를 붙인 것이 전부다.

| 주소 | 보이는 것 | 파일 |
|---|---|---|
| `/` | 할 일 목록 (TodoList) | `pages/TodoPage.jsx` |
| `/basic` | 클래스형·함수형 컴포넌트를 나란히 | `pages/BasicPage.jsx` |
| 그 밖 | "없는 주소입니다." | `App.jsx` 안에 한 줄 |

**라우터는 세 곳에만 있다** — ① `main.jsx` 의 `BrowserRouter`, ② `App.jsx` 의 `Routes`, ③ `App.jsx` 의 `NavLink`. **`pages/` 아래의 두 파일에는 라우터와 얽힌 코드가 한 줄도 없다. 페이지는 그냥 컴포넌트이고, 누가 언제 그릴지만 `App.jsx` 가 정한다.**

### 코드 위치
`todolist_react_router/src/main.jsx` · `App.jsx` · `App.css` · `pages/TodoPage.jsx` · `pages/BasicPage.jsx`

### 코드와 해설

**`src/main.jsx` — 실물**

```jsx
/* 라우터를 쓰려면 앱 전체를 <BrowserRouter> 로 한 번 감싸야 합니다.
   이 감싸개가 "지금 주소가 무엇인가" 를 들고 있고,
   안쪽의 <Routes> 와 <Link> 들이 그것을 꺼내 씁니다.
   감싸는 일은 여기서 딱 한 번만 합니다. */
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";

import "./index.css";
import App from "./App.jsx";

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <BrowserRouter>
            <App />
        </BrowserRouter>
    </StrictMode>
);
```

**`src/App.jsx` — 실물**

```jsx
/* 라우터가 하는 일은 두 가지뿐입니다.
     (1) 주소를 보고 어느 페이지를 그릴지 고른다   → <Routes> <Route>
     (2) 페이지를 새로 내려받지 않고 주소만 바꾼다 → <Link> <NavLink>
   이 파일에 그 둘이 다 들어 있습니다. 서버도 axios 도 없습니다. */
import { NavLink, Route, Routes } from "react-router-dom";

import TodoPage from "./pages/TodoPage.jsx";
import BasicPage from "./pages/BasicPage.jsx";

import "./App.css";

/* NavLink 의 className 에는 문자열 대신 함수를 넘길 수 있다.
   React Router 가 isActive 를 넣어 부르므로
   "지금 보고 있는 쪽" 만 다른 모양으로 만들 수 있다.
   같은 모양을 두 번 적지 않으려고 밖으로 빼 두었다. */
function navClass({ isActive }) {
    return isActive ? "nav-link active" : "nav-link";
}

function App() {
    return (
        <>
            {/* 머리말은 Routes 바깥에 있다. 그래서 주소가 바뀌어도 그대로 남는다. */}
            <header className="app-header">
                <h1 className="app-title">React 연습</h1>

                <nav className="app-nav">
                    {/* end 는 "주소가 정확히 / 일 때만 active" 라는 뜻이다.
                        이것이 없으면 /basic 에 있을 때도 / 가 active 가 된다.
                        / 가 모든 주소의 앞부분이기 때문이다. */}
                    <NavLink to="/" end className={navClass}>
                        할 일 목록
                    </NavLink>

                    <NavLink to="/basic" className={navClass}>
                        기초 컴포넌트
                    </NavLink>
                </nav>
            </header>

            {/* Routes 안에서 주소와 맞는 Route 하나만 그려진다.
                위에서 아래로 훑어 맞는 것을 고르는 것이 아니라,
                React Router 가 가장 잘 맞는 하나를 골라 준다. */}
            <main className="page">
                <Routes>
                    <Route path="/" element={<TodoPage />} />
                    <Route path="/basic" element={<BasicPage />} />

                    {/* * 는 "위의 어느 것과도 맞지 않는 주소" 다.
                        이것이 없으면 /abcd 같은 주소에서 빈 화면이 나온다. */}
                    <Route path="*" element={<p>없는 주소입니다.</p>} />
                </Routes>
            </main>
        </>
    );
}

export default App;
```

- **`navClass` 를 컴포넌트 바깥 함수로 뺐다.** 학생 앱(6-8절)은 같은 화살표 함수를 두 번 적었다. 같은 일, 다른 모양.
- **`element={<p>없는 주소입니다.</p>}`** — 페이지 컴포넌트를 따로 만들지 않고 JSX 한 줄을 바로 넣었다. `element` 에는 **아무 JSX 나** 들어갈 수 있다.
- `<main className="page">` 로 `Routes` 를 감쌌다 → 모든 페이지가 같은 바깥틀 안에 그려진다.

**`src/App.css` — 라우터를 붙이면서 새로 생긴 것만 (실물 요지)**

```css
/* 어느 페이지에서나 보이는 머리말 */
.app-header {
    display: flex;
    justify-content: space-between;   /* 제목은 왼쪽, 링크는 오른쪽 */
    align-items: center;
    max-width: 900px;
    margin: 0 auto 20px;
    padding: 16px 20px;
    background-color: white;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* NavLink 는 a 태그로 그려진다. a 는 기본이 inline 이라
   padding 이 제대로 먹지 않으므로 inline-block 으로 바꾼다. */
.nav-link {
    display: inline-block;
    padding: 8px 14px;
    border-radius: 5px;
    color: #555;
    text-decoration: none;
}
/* 지금 보고 있는 쪽. NavLink 가 isActive 일 때만 붙여 준다. */
.nav-link.active {
    background-color: #007bff;
    color: white;
}

/* 화면이 넓으면 둘을 나란히, 좁으면 위아래로 놓는다. — /basic 페이지 */
.basic-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 16px;
}
```

🔗 **2부 CSS 가 그대로 쓰인다.** `justify-content: space-between`(2-5절 Flexbox), `repeat(auto-fit, minmax(320px, 1fr))`(2-6절 Grid 반응형), **인라인 요소에 세로 padding 이 제대로 안 먹는 문제**(2-4절 display, 1-3절 블록 vs 인라인).
⚠️ **`.nav-link` 에 `display: inline-block` 을 준 까닭** — `NavLink` 는 `a` 태그로 그려지는데 `a` 는 기본이 inline 이라 위아래 padding 이 제대로 먹지 않는다. 실습 6-9 의 `a.edit-btn` 과 같은 이야기.

**`src/pages/TodoPage.jsx` — 부록 E 의 `AppTodo.jsx` 를 옮겨 온 것 (실물)**

교재가 말하는 차이는 **세 가지뿐**이다 — ① 이름(`App` → `TodoPage`), ② import 경로(한 단계 내려왔으므로 `./components` → `../components`), ③ `App.css` 를 `App.jsx` 가 가져갔다.

⚠️ **실물은 그 셋에 더해 E-20 이 반영돼 있다** — 500개 `initialTodos` 는 주석으로 빠지고 원래 세 개로 돌아왔으며, `handleToggle` · `handleRemove` 가 `useCallback` + 함수형 업데이트로 바뀌었다(코드는 5-12-7절). `components/` 도 `TodoItem` · `TodoItemList` 의 **비교 함수가 지워지고 `memo(TodoItem)` 만 남았고**, `className` 이 삼항으로 바뀌었고, `MyComponent*.jsx` 의 `import React, { … }` 에서 쓰지 않는 `React` 가 빠졌다. **"한 글자도 고치지 않고 옮겨 왔다"는 README 설명과는 이만큼 다르다.**

```jsx
/* E-18 · E-20 에서 성능을 재 볼 때만 살리는 코드.
   아래 useState 자리의 주석도 함께 바꿔 끼운다.
const initialTodos = new Array(500).fill(0).map(
    (item, idx) => ({ id: idx, text: `일정 ${idx}`, checked: true })
);
*/
function TodoPage() {
  const [todo, setTodo] = useState("");
  const [todos, setTodos] = useState([
    { id: 0, text: "리액트 소개", checked: false },
    { id: 1, text: "리액트 구조", checked: true },
    { id: 2, text: "리액트 사용", checked: false },
  ]);
  // 성능을 재 볼 때: 위를 주석 처리하고 아래를 살린다(맨 위 initialTodos 도 함께).
  // const [todos, setTodos] = useState(initialTodos);
  const [nextId, setNextId] = useState(3);
  // ... handleChange · handleCreate · handleEnter 는 부록 E 그대로
  // ... handleToggle · handleRemove 는 useCallback (5-12-7절)
  // ... return 은 부록 E 그대로
}
export default TodoPage
```

**`src/pages/BasicPage.jsx` — 실물**

```jsx
/* 같은 일을 하는 컴포넌트 둘을 한 화면에 나란히 놓습니다.
     MyComponent      클래스 컴포넌트 — state, this.setState, 메서드
     MyComponentFunc  함수 컴포넌트   — useState, useRef */
import MyComponent from "../components/MyComponent.jsx";
import MyComponentFunc from "../components/MyComponentFunc.jsx";

function BasicPage() {
    return (
        <div>
            <p className="basic-hint">
                두 컴포넌트는 화면도 기능도 같습니다. 적는 방법만 다릅니다.
                양쪽의 입력칸에 글자를 쳐 보고, 증가 버튼도 눌러 보세요.
            </p>

            {/* 두 컴포넌트가 받는 props 는 이름과 나이로 같다. 같은 값을 주어야 견주기 좋다. */}
            <div className="basic-grid">
                <section className="basic-card">
                    <MyComponent name="홍길동" age={20} />
                </section>

                <section className="basic-card">
                    {/* 함수 쪽은 children 도 받는다.
                        여는 태그와 닫는 태그 사이에 적은 것이 children 으로 들어간다. */}
                    <MyComponentFunc name="홍길동" age={20}>
                        <p>이 문장은 children 으로 들어온 것입니다.</p>
                    </MyComponentFunc>
                </section>
            </div>
        </div>
    );
}
export default BasicPage;
```

🔗 5-12-1절(부록 E-2 ~ E-6)에서 만든 몸풀기 두 컴포넌트를 한 화면에 나란히 놓은 것. `children` 은 5-4절.

> **■ 확인하기** — `npm install` 뒤 `npm run dev` 로 열면 할 일 목록이 보인다 / "기초 컴포넌트"를 누르면 주소가 `/basic` 으로 바뀌고 화면이 바뀐다 / **그때 브라우저가 페이지를 새로 내려받지 않는다 (F12 → Network 에 문서 요청이 없다)** / 지금 보고 있는 쪽 링크만 파란 배경이다 / 주소창에 `/abcd` 를 치면 "없는 주소입니다." / 뒤로 가기 버튼이 제대로 동작한다

**6부에서 더해지는 것**

| 더해지는 것 | 무엇을 할 수 있게 되나 |
|---|---|
| `useParams` | 주소에 값을 싣는다 — `/edit/3` 의 3 을 꺼내 쓴다 |
| `useNavigate` | 코드에서 주소를 바꾼다 — 등록에 성공하면 목록으로 |
| `useLocation` | 옮겨 가면서 실어 보낸 값을 받는다 |
| Axios · Zustand | 서버 통신과, 페이지들이 함께 보는 값 |

> 이 준비 운동은 건너뛰어도 6부를 따라가는 데 지장이 없다. 다만 실습 6-5 에서 `BrowserRouter` · `Routes` · `Route` 가 한꺼번에 나올 때 "이게 뭐였지" 싶으면 여기로 돌아온다. **서버를 켜지 않아도 되므로 언제든 다시 열어 볼 수 있다.**

---

## 6-6. 실습 6-1 ~ 6-3 — Axios 로 바꾸기

### 실습 6-1 — 프로젝트 복사하고 라이브러리 설치하기

```bash
# student_react_first 가 있는 폴더에서
cp -r student_react_first student_react_router_axios
cd student_react_router_axios
rm -rf node_modules dist          # 용량만 크고 npm install 로 다시 만들어진다

npm install
npm install axios react-router-dom
```

- `package.json` 의 `name` 을 폴더 이름과 맞춰 둔다(동작에는 영향 없음).
- 설치 후 `dependencies` 에 `"axios": "^1.20.0"`, `"react-router-dom": "^7.18.3"` 두 줄이 는다(실물은 `^7.18.4`).
- ⚠️ **5부를 실습 5-12 까지 마쳤다면 폼이 두 벌 있다.** 교재는 "복사한 폴더에서 `StudentFormField.jsx` 의 내용을 `StudentForm.jsx` 에 옮겨 넣고 `StudentFormField.jsx` 는 지우라"고 한다. **⚠️ 실물은 그렇게 하지 않았다.** 두 파일이 그대로 남아 있고, 폼 페이지가 **`StudentFormField.jsx` 를 직접 import** 한다(6-10절). 그리고 `StudentFormField.jsx` 쪽만 6부에 맞게 고쳤다.
- 실물에는 5부 `App.jsx` 를 **`AppOrg.jsx`** 로 이름만 바꿔 남겨 두었다. 어디서도 import 하지 않는 보관용 파일이다.

### 실습 6-2 — axios 인스턴스 만들기

**`src/config.js` — 5부에서는 주소 전체를 만들어 두었다. axios 는 앞부분(`baseURL`)을 기억하므로 뒷부분 경로만 남긴다.**

```js
// 교재 — 6부 config.js
export const API_BASE_URL = import.meta.env?.VITE_API_BASE_URL ?? "http://localhost:8080";
// 학생 API 의 경로. axios 가 baseURL 뒤에 이어 붙인다.
export const STUDENTS_PATH = "/api/students";
// (STUDENTS_URL 과 JSON_HEADERS 는 사라진다 — 헤더는 client.js 에서 한 번만 적는다)
```

⚠️ **실물 `config.js` 는 `STUDENTS_URL` 과 `JSON_HEADERS` 를 지우지 않고 남겨 두었다.** 보관용 `studentApiFetch.js`(5부 fetch 버전)가 아직 그 둘을 import 하기 때문이다. **5부 `config.js` 에 `STUDENTS_PATH` 한 줄만 더한 모양**이고, `console.log('API_BASE_URL =' …)` 확인용 줄은 4부 때부터 있던 것이다.

```js
// student_react_router_axios/src/config.js — 실물
export const API_BASE_URL = import.meta.env?.VITE_API_BASE_URL ?? "http://localhost:8080";
console.log('API_BASE_URL =' + API_BASE_URL );

// 학생 API 의 경로. axios 가 baseURL 뒤에 이어 붙인다.
export const STUDENTS_PATH = "/api/students";

export const STUDENTS_URL = `${API_BASE_URL}/api/students`;   // ← (문서 설명) studentApiFetch.js 가 쓴다
export const JSON_HEADERS = { "Content-Type": "application/json" };   // ← (문서 설명) 실물은 여러 줄 객체

export const APP_MODE = import.meta.env?.VITE_MODE ?? "TEST";
```

`.env.development`(`VITE_MODE=TEST`) · `.env.production`(`VITE_MODE=PROD`) 은 5부 그대로(🔗 4-9절).

**`src/api/client.js` — 새 파일. 모든 요청이 지나가는 통로 (실물 = 교재)**

```js
import axios from "axios";
import { API_BASE_URL } from "../config.js";

// 서버가 message 를 주지 않을 때 대신 쓸 문구 — 5부와 같다.
const DEFAULT_MESSAGES = {
    400: "입력한 값이 올바르지 않습니다.",
    404: "존재하지 않는 학생입니다.",
    409: "이미 등록된 학번입니다.",
    500: "서버에서 오류가 발생했습니다.",
};

/* create 로 "내 설정이 담긴 axios" 를 하나 만들어 둔다.
   baseURL 을 적어 두면 요청할 때는 뒷부분만 적으면 된다.
     client.get("/api/students")
       → http://localhost:8080/api/students */
export const client = axios.create({
    baseURL: API_BASE_URL,
    headers: { "Content-Type": "application/json" },
});

/* 가로채기(interceptor) — 응답이 돌아오는 길목에 세워 두는 검문소다.
   성공은 그대로 통과시키고, 실패는 우리 메시지로 바꿔서 넘긴다.
   이 다섯 줄 덕분에 화면 쪽 코드는 error.message 만 읽으면 된다. */
client.interceptors.response.use(
    (response) => response,
    (error) => {
        // 서버가 응답을 준 경우에만 status 와 data 가 있다.
        // 서버가 꺼져 있으면 error.response 자체가 없다.
        const status = error.response?.status;

        const message =
            error.response?.data?.message ??
            DEFAULT_MESSAGES[status] ??
            "서버와 통신하지 못했습니다. 서버가 켜져 있는지 확인하세요.";

        // 5부의 throw new Error(message) 와 같은 뜻이다.
        return Promise.reject(new Error(message));
    }
);
```

**해설**
- **`??` 세 단 폴백은 4부 `request()` 와 똑같은 모양이다.** 서버 메시지 → 상태 코드별 기본 문구 → 마지막 안전망. 🔗 4-10절. **달라진 것은 마지막 문구뿐** — 4부는 `` `요청에 실패했습니다. (${response.status})` `` 였지만, 여기서는 `status` 자체가 없을 수 있으므로(서버 꺼짐) "서버가 켜져 있는지 확인하세요"로 바꿨다.
- **`error.response?.data?.message`** — 백엔드 PART 의 `@RestControllerAdvice` 가 돌려주는 오류 JSON 의 `message` 필드. 🔗 409 는 학번 중복(`unique` 제약).
- **`return Promise.reject(new Error(message))`** — 인터셉터 안에서 "실패로 계속 흘려보내라"는 뜻. **`throw new Error(message)` 라고 써도 같다.** 이것이 `studentApi.js` 를 부른 쪽의 `try / catch` 로 전달된다. 🔗 4-7절 Promise.
- ⚠️ `return error` 라고 쓰면 **성공으로 바뀌어 버린다** — 부른 쪽이 `catch` 로 가지 않는다.

> **■ 확인하기** — `src/api/` 폴더에 `client.js` 와 `studentApi.js` 두 파일이 있다 / 아직 `studentApi.js` 는 fetch 를 쓰고 있어 화면은 그대로 동작한다

### 실습 6-3 — studentApi 를 axios 로 바꾸기

**5부의 `request()` 함수가 통째로 없어진다.** `DEFAULT_MESSAGES` 와 오류 처리는 `client.js` 로 옮겨 갔다.

```js
// student_react_router_axios/src/api/studentApi.js — 실물
/* 하는 일은 5부와 같습니다. 달라진 것은 fetch 대신 axios 를
   쓴다는 것뿐이고, 그래서 함수마다 두 줄이면 끝납니다.
   오류 처리는 client.js 의 가로채기가 맡습니다.
   여기에는 try / catch 가 한 줄도 없습니다.
   화면에 무엇을 보여줄지는 페이지 컴포넌트가 정합니다. */
import { client } from "./client.js";
import { STUDENTS_PATH } from "../config.js";

/* axios 는 서버가 준 본문을 response.data 에 담아 준다.
   5부에서 await response.json() 을 부르던 일이 없어졌다. */
export const fetchStudents = async () => {
    console.log('API fetchStudents');            // ← 실물에만 있는 확인용 줄
    const response = await client.get(STUDENTS_PATH);
    return response.data;
};

export const fetchStudent = async (id) => {
    const response = await client.get(`${STUDENTS_PATH}/${id}`);
    return response.data;
};

// 두 번째 인자가 본문이다. JSON.stringify 를 부를 필요가 없다.
export const createStudent = async (student) => {
    const response = await client.post(STUDENTS_PATH, student);
    return response.data;
};

export const updateStudent = async (id, student) => {
    const response = await client.put(`${STUDENTS_PATH}/${id}`, student);
    return response.data;
};

// 삭제는 서버가 204 를 주고 본문이 없다. 돌려줄 것이 없으므로 null 이다.
export const deleteStudent = async (id) => {
    await client.delete(`${STUDENTS_PATH}/${id}`);
    return null;
};
```

**해설**
- **함수마다 두 줄. `try / catch` 가 한 줄도 없는 것에 주목.** 오류는 `client.js` 의 가로채기가 맡고, 화면에 무엇을 보여줄지는 페이지가 정한다.
- **함수 이름과 인자는 5부와 한 글자도 다르지 않다.** 그래서 이 파일을 부르는 쪽(페이지)은 **고칠 것이 없다.** 🔗 4부에서 `api/` 를 분리해 둔 보상.
- ⚠️ **삭제만 `await` 뒤에 `null` 을 돌려준다.** 서버가 204 를 주어 본문이 없기 때문. 5부에서 `response.status === 204` 를 확인하던 자리. **axios 는 본문이 없어도 `.json()` 처럼 터지지 않는다**(`response.data` 가 빈 값이 될 뿐). 그래도 "돌려줄 것이 없다"는 뜻을 분명히 하려고 `null` 을 직접 돌려준다.
- 실물 `studentApiFetch.js` = **5부 `studentApi.js` 를 이름만 바꿔 남긴 것**(`request()` + `STUDENTS_URL` + `JSON_HEADERS`). import 하는 곳이 없다. 🔗 "Axios 를 fetch 로 되돌려도 고칠 곳은 api 폴더뿐" — import 한 줄만 바꾸면 되돌아간다.

> **■ 확인하기** — 등록·조회·수정·삭제가 5부와 똑같이 동작한다 / F12 → Network 에서 요청 주소가 `http://localhost:8080/api/students` 로 나간다 / 같은 학번으로 등록하면 "이미 등록된 학번입니다"가 뜬다

---

## 6-7. 실습 6-4 — 스타일 먼저 다듬기

**앞으로 만들 요소들의 스타일을 미리 적어 둔다.** 여기 적는 이름들은 실습 6-5 부터 화면에 하나씩 나타난다. **먼저 적는 이유** — 6-5 에서 화면을 열었을 때 머리말이 곧바로 제 모양으로 보이고, **"링크를 누르면 화면이 바뀌는가" 하나에만 집중할 수 있기 때문.**

**`src/style.css` — 맨 아래에 추가 (실물 = 교재)**

```css
/* ===========================================================
   라우터를 추가 이후에 — 페이지가 여러 개가 되면서 필요해진 스타일
   =========================================================== */

/* 어느 페이지에서나 보이는 머리말 */
.app-header {
    display: flex;
    justify-content: space-between;   /* 제목은 왼쪽, 버튼은 오른쪽 */
    align-items: center;              /* 세로 가운데 맞춤 */
    max-width: 1200px;
    margin: 0 auto 20px;
    padding: 16px 20px;
    border-bottom: 2px solid #dee2e6;
}

/* 머리말의 제목 — Link 이지만 제목처럼 보이게 한다 */
.app-title {
    font-size: 1.5em;
    font-weight: bold;
    color: #343a40;
    text-decoration: none;            /* 링크 밑줄 제거 */
}
.app-title:hover { color: #0056b3; }

/* 머리말의 내비게이션 — 링크 두 개를 나란히 */
.app-nav {
    display: flex;
    gap: 8px;                          /* 링크 사이 간격 */
}

/* 내비게이션 링크 공통 */
.nav-link {
    padding: 8px 16px;
    border-radius: 4px;
    color: #495057;
    text-decoration: none;
    font-size: 0.95em;
    transition: background-color 0.15s;
}
.nav-link:hover { background-color: #e9ecef; }

/* 지금 보고 있는 페이지의 링크 — NavLink 가 active 클래스를 붙여 준다 */
.nav-link.active {
    background-color: #007bff;
    color: white;
}
.nav-link.active:hover { background-color: #0056b3; }

/* 각 페이지의 본문을 감싸는 상자 */
.page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 40px;
}

/* 목록 페이지에서는 메시지가 표 위에 홀로 놓인다.
   MessageBox 가 돌려주는 것은 span 이라 인라인이고,
   인라인 요소에는 세로 margin 이 먹지 않는다. 그래서 블록으로 바꾼다.
   폼 안(.button-group)의 메시지는 flex 의 gap 이 간격을 주므로 건드리지 않는다. */
.page > .error-message {
    display: block;
    margin-bottom: 16px;
}

/* 수정 버튼이 button 에서 Link 로 바뀌었다.
   a 태그는 기본이 inline 이라 버튼처럼 보이도록 맞춰 준다. */
a.edit-btn {
    display: inline-block;
    text-decoration: none;
    line-height: 1.2;
}

/* 없는 주소 안내 */
.not-found {
    max-width: 600px;
    margin: 60px auto;
    padding: 40px 20px;
    text-align: center;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.not-found h2 { color: #dc3545; }

/* 폼 페이지 위쪽의 "목록으로 돌아가기" 링크 */
.back-link {
    display: inline-block;
    margin-bottom: 12px;
    color: #007bff;
    text-decoration: none;
    font-size: 0.9em;
}
.back-link:hover { text-decoration: underline; }

/* 제목과 모드 배지를 한 덩어리로 묶는다.
   머리말이 space-between 이라 자식이 둘이어야 배치가 유지된다. */
.app-brand {
    display: flex;
    align-items: center;
    gap: 10px;
}

/* 머리말 안에서는 부모 글자가 작아 0.5em 이 너무 작다. 크기만 따로 준다. */
.app-brand .app-mode {
    font-size: 0.8rem;
}
```

**해설 — 2부 CSS 가 어디서 다시 나오나**

| 규칙 | 왜 | 🔗 |
|---|---|---|
| `.page > .error-message { display: block }` | `span` 은 인라인 → **세로 margin 이 먹지 않는다** | 1-3절 블록 vs 인라인, 2-4절 display |
| `a.edit-btn { display: inline-block }` | `a` 는 인라인 → **padding 이 제대로 먹지 않는다** | 2-3절 박스 모델, 2-4절 display |
| `.page > .error-message` 의 `>` | **바로 아래 자식만.** 폼 안(`.button-group` 안)의 메시지는 걸리지 않는다 | 2-2절 자식 선택자 |
| `.nav-link.active` | 두 클래스를 **모두** 가진 요소 | 2-2절 복합 선택자 |
| `.app-brand .app-mode` | 후손 선택자 — 머리말 안의 배지만 작게 | 2-2절 |
| `transition: background-color 0.15s` | **평소 상태(`.nav-link`)에 둔다.** `:hover` 에 두면 돌아올 때 끊긴다 | 2-11절 transition |
| `rem` vs `em` | 배지는 부모가 작아 `0.5em` 이 너무 작다 → **뿌리 기준 `rem`** | 2-9절 단위 |

⚠️ **`.app-brand` 가 필요한 까닭** — 머리말은 `justify-content: space-between` 이라, **자식이 셋이면 양쪽 끝과 가운데로 벌려 놓는다.** 제목과 배지를 `div` 하나로 묶어 **header 의 자식을 둘로 유지한다.** 배지를 `Link` 안에 넣지 않은 것은, **안에 넣으면 배지를 눌러도 목록으로 옮겨 가 버리기 때문.**

⚠️ 실물은 `.app-brand` · `.app-brand .app-mode` 두 규칙이 **Zustand 단계의 `style.css` 에 한 번 더 중복**돼 있다(6-15절). 같은 규칙이 두 번 있어도 뒤의 것이 이기므로 결과는 같다.

---

## 6-8. 실습 6-5 — 라우터 감싸기와 페이지 나누기

**1) 어떤 주소를 쓸 것인가**

| 주소 | 화면 | 맡는 파일 |
|---|---|---|
| `/` | 학생 목록 | `pages/StudentListPage.jsx` |
| `/new` | 학생 등록 | `pages/StudentFormPage.jsx` |
| `/edit/:id` | 학생 수정 | `pages/StudentFormPage.jsx` |
| 그 밖 | 없는 주소 안내 | `pages/NotFoundPage.jsx` |

**등록과 수정은 화면이 거의 같으므로 한 컴포넌트가 두 주소를 맡는다.**

**2) `main.jsx` — `BrowserRouter` 로 감싸기 (실물)**

```jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>,
)
```

주소를 지켜보는 `BrowserRouter` 를 앱 바깥에 한 겹 더 씌운다. **이것이 없으면 `useParams` 나 `Link` 가 동작하지 않는다.**

**3) 세 개의 스텁(stub) 페이지 먼저 만들기**

`App.jsx` 가 페이지 셋을 import 할 것이므로 그 파일들이 **먼저** 있어야 한다. 없으면 저장하는 순간 **`Failed to resolve import`** 가 셋 난다.

```jsx
// src/pages/StudentListPage.jsx
function StudentListPage() {
    return <h2>학생 목록 페이지</h2>;
}
export default StudentListPage;

// src/pages/StudentFormPage.jsx
function StudentFormPage() {
    return <h2>학생 등록 · 수정 페이지</h2>;
}
export default StudentFormPage;

// src/pages/NotFoundPage.jsx
function NotFoundPage() {
    return <h2>없는 주소입니다</h2>;
}
export default NotFoundPage;
```

⚠️ **스텁(stub)** — 여러 조각이 맞물려 있을 때, 한 조각을 제대로 만드는 동안 나머지는 **"있기만 한"** 상태로 두는 것. 덕분에 **단계마다 앱이 돌아가는 상태를 유지할 수 있다.** 🔗 5-13-4절 "빈 함수 `handleEdit() {}` 로 먼저 연결"과 같은 생각. 지금 중요한 것은 내용이 아니라 **"주소가 바뀌면 화면이 바뀌는가"** 다.

**4) `App.jsx` — 주소와 화면 짝짓기 (실물)**

5부의 `App.jsx` 는 state 를 모두 갖고 화면도 직접 그렸다. **그 일이 페이지로 내려가고, App 은 "주소 → 페이지"를 이어 주는 일만 한다.** 통째로 바꾼다.

```jsx
/* 5부에서는 App 이 상태를 모두 갖고 화면도 직접 그렸습니다.
   6부에서는 그 일이 페이지 컴포넌트로 내려가고, App 은
   "주소 → 페이지" 를 이어 주는 일만 합니다.
     주소            보여 줄 페이지
     /               학생 목록
     /new            학생 등록 폼
     /edit/3         3번 학생 수정 폼
     그 밖의 주소     없는 주소 안내
   5부의 editingId state 가 사라진 것에 주목하세요.
   "지금 몇 번 학생을 수정 중인가" 를 주소가 알고 있기 때문입니다. */
import { Link, NavLink, Route, Routes } from "react-router-dom";

import StudentListPage from "./pages/StudentListPage.jsx";
import StudentFormPage from "./pages/StudentFormPage.jsx";
import NotFoundPage from "./pages/NotFoundPage.jsx";

// 지금 어느 모드로 도는지 (TEST / PROD)
import { APP_MODE } from "./config.js";

import "./style.css";

function App() {
    // 제목 옆에 붙일 배지의 class. 운영이면 빨강, 아니면 회색.
    let modeClass = "app-mode test";
    if (APP_MODE === "PROD") {
        modeClass = "app-mode prod";
    }

    return (
        <>
            {/* 어느 페이지에서나 보이는 머리말. Routes 바깥에 있어서 바뀌지 않는다. */}
            <header className="app-header">
                {/* Link 는 <a> 처럼 보이지만 페이지를 새로 내려받지 않는다.
                    주소만 바꾸고 React 가 화면을 갈아 끼운다. */}
                <div className="app-brand">
                    <Link to="/" className="app-title">학생 관리 시스템</Link>
                    <span className={modeClass}>{APP_MODE}</span>
                </div>

                <nav className="app-nav">
                    {/* NavLink 는 Link 와 같지만, 지금 보고 있는 주소와 맞으면
                        className 에 isActive 가 true 로 들어온다. */}
                    <NavLink
                        to="/"
                        end
                        className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}
                    >
                        학생 목록
                    </NavLink>

                    <NavLink
                        to="/new"
                        className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}
                    >
                        학생 등록
                    </NavLink>
                </nav>
            </header>

            {/* Routes 안에서 주소와 맞는 Route 하나만 그려진다. */}
            <Routes>
                <Route path="/" element={<StudentListPage />} />
                <Route path="/new" element={<StudentFormPage />} />

                {/* :id 는 자리를 비워 둔다는 뜻이다. /edit/3 이면 id 가 "3" 이 된다. */}
                <Route path="/edit/:id" element={<StudentFormPage />} />

                {/* * 는 위 어느 것과도 맞지 않는 주소다. */}
                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </>
    );
}

export default App;
```

**머리말에 쓴 CSS 클래스 — 5부에는 없던 이름들**

| class | 붙는 곳 | 무엇을 맡나 |
|---|---|---|
| `app-header` | 머리말 전체 | 제목과 내비를 한 줄에 놓고 양 끝으로 벌린다 |
| `app-brand` | 제목 + 배지 묶음 | 둘을 붙여 한 덩어리로 만든다 |
| `app-title` | 제목 `Link` | 링크 밑줄을 없애고 제목처럼 크게 보이게 한다 |
| `app-nav` | 링크 두 개 묶음 | 나란히 놓고 사이 간격을 준다 |
| `nav-link` | 링크 하나하나 | 버튼처럼 보이게 한다 |
| `active` | 지금 보고 있는 링크 | **NavLink 가 자동으로 붙인다.** 파랗게 강조 |

**해설**
- 🔗 **APP_MODE 배지 코드(`let modeClass` + `if`)는 5부 App 에서 그대로 옮겨 왔다.** 배지는 모든 페이지에 보여야 하므로 **Routes 바깥 머리말**로 갔다.
- **5부 App 의 state 6개 · 핸들러 · `useEffect` 가 전부 사라졌다.** 이 파일에는 **Hook 이 하나도 없다.** App 은 이제 "틀"이다.
- **제목도 `Link`** — 제목을 누르면 목록으로 간다. 흔한 관례.

> **■ 먼저 동작하게 만들고 모양은 나중에 손보는 순서다.** 지금 단계에서 봐야 할 것은 "링크를 누르면 화면이 바뀌는가" 하나뿐. 스타일을 함께 넣으면 무엇 때문에 동작하고 무엇 때문에 안 되는지 가리기 어려워진다(그래서 6-4 에서 CSS 를 **미리** 적어 두었다).

> **■ 확인하기** — 머리말과 "학생 목록 페이지"가 보인다 / "학생 등록"을 누르면 주소가 `/new` 로 바뀌고 화면이 바뀐다 / `/nosuchpage` 를 넣으면 "없는 주소입니다" / 어느 화면에서나 머리말은 그대로 / 뒤로 가기가 동작한다

---

## 6-9. 실습 6-6 — 목록 페이지 만들기

5부 `App.jsx` 가 하던 일 중 **"목록 불러오기"와 "삭제"가 여기로 온다.** 등록·수정 폼은 다른 페이지로 떨어져 나가므로 `form` 과 `editingId` 는 가져오지 않는다.

**`src/pages/StudentListPage.jsx` — 실물**

```jsx
/* 5부 App.jsx 가 하던 일 중 "목록 불러오기" 와 "삭제" 가 여기로 왔습니다.
   등록·수정 폼은 다른 페이지로 떨어져 나갔으므로
   이 페이지는 form · editingId 같은 값을 갖지 않습니다. */
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

import { fetchStudents, deleteStudent } from "../api/studentApi.js";
import StudentTable from "../components/StudentTable.jsx";
import MessageBox from "../components/MessageBox.jsx";

// 성공 메시지가 저절로 사라지기까지의 시간(ms)
const MESSAGE_TIMEOUT = 3000;

function StudentListPage() {
    /* useLocation 은 지금 주소에 딸린 정보를 돌려준다.
       등록·수정 페이지가 navigate("/", { state: { message } }) 로
       실어 보낸 문구가 location.state.message 에 들어 있다. */
    const location = useLocation();

    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [listError, setListError] = useState(null);

    /* useState 에 넘긴 값은 첫 렌더에서 한 번만 쓰인다.
       그래서 "옮겨 오면서 받은 메시지" 를 처음 값으로 두면 딱 맞는다.
       useEffect 로 옮겨 담을 필요가 없다. */
    const [message, setMessage] = useState(
        location.state?.message ?
        { text: location.state.message, type: "success" } : null
    );

    async function loadStudents() {
        setLoading(true);
        setListError(null);

        try {
            const data = await fetchStudents();
            setStudents(data);
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
            setListError("오류: 데이터를 불러올 수 없습니다.");
        } finally {
            setLoading(false);
        }
    }

    /* 이 페이지가 화면에 붙을 때 한 번 목록을 불러온다.
       5부에서는 등록에 성공한 뒤 loadStudents() 를 직접 다시 불렀지만,
       이제는 폼 페이지에서 "/" 로 옮겨 오면 이 페이지가 새로 붙으므로
       저절로 다시 불러옵니다. */
    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect -- 처음 한 번 목록을 불러오는 것은 의도된 동작입니다
        loadStudents();
    }, []);

    // 성공 메시지는 3초 뒤에 저절로 사라진다.
    useEffect(() => {
        if (!message) {
            return;
        }
        if (message.type !== "success") {
            return;
        }

        const timer = setTimeout(() => setMessage(null), MESSAGE_TIMEOUT);
        return () => clearTimeout(timer);
    }, [message]);

    async function handleDelete(studentId) {
        if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) {
            return;
        }

        try {
            await deleteStudent(studentId);
            setMessage({ text: "학생이 성공적으로 삭제되었습니다.", type: "success" });
            await loadStudents();
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    return (
        <div className="page">
            <MessageBox message={message} />

            <StudentTable
                students={students}
                loading={loading}
                error={listError}
                onDelete={handleDelete}
            />
        </div>
    );
}

export default StudentListPage;
```

**해설**

**1) 메시지를 다른 페이지에서 받는 방법 — `useState` 의 초깃값**

```jsx
// 보내는 쪽 — StudentFormPage
navigate("/", { state: { message: "등록되었습니다." } });

// 받는 쪽 — StudentListPage
const location = useLocation();
const [message, setMessage] = useState(
    location.state?.message ? { text: location.state.message, type: "success" } : null
);
```

⚠️ **`useEffect` 로 옮겨 담지 않은 이유** — **`useState` 에 넘긴 값은 첫 렌더에서 한 번만 쓰인다.** 목록 페이지는 옮겨 올 때마다 **새로 붙으므로(마운트)** 그 성질이 딱 맞다. `useEffect(() => setMessage(...), [location])` 로 쓰면 한 번 더 그리게 되고 ESLint 경고(`set-state-in-effect`)도 난다. 🔗 5-9절 (1.4 보강) "useState 초깃값은 첫 렌더에서 한 번만".

**2) 목록이 저절로 갱신되는 이유** — 5부에서는 등록에 성공한 뒤 `loadStudents()` 를 직접 다시 불렀다. **이제는 폼 페이지에서 `"/"` 로 옮겨 오면 이 페이지가 새로 붙고, `useEffect(…, [])` 가 실행되어 저절로 다시 불러온다.** `[]` 는 "이 컴포넌트가 붙을 때 한 번"이지 "앱 전체에서 한 번"이 아니다.

**5부 App 과 견주기**

| 5부 App | 6부 StudentListPage |
|---|---|
| state 6개 | **4개** (`students` · `loading` · `listError` · `message`) — `form` · `editingId` 는 폼 페이지로 |
| `loadStudents` 를 `useCallback` 으로 (5-13) | **다시 평범한 `async function`** — 표에 넘기지 않으므로 고정할 필요 없음 |
| `useEffect(…, [loadStudents])` | `useEffect(…, [])` + `eslint-disable` 주석 (5-4 시절 모양으로 돌아감) |
| `handleDelete` 안의 `editingId === studentId` 확인 | **없어졌다** — 이 페이지는 수정 중인 학생을 모른다 |
| `MessageBox` 가 폼 안에 | **표 위에 홀로** — 그래서 `.page > .error-message { display: block }` 이 필요 |

⚠️ **`handleDelete` 가 `useCallback` 이 아닌데 `StudentTable` 은 여전히 `memo`** — 이 페이지에는 폼 입력이 없어서 "글자마다 다시 그리는" 상황 자체가 없다. `memo` 가 있어도 해가 없고, 없어도 손해가 없다.

> **■ 확인하기** — 목록이 보이고 삭제가 동작한다 / 교재는 "수정 버튼을 누르면 아직 껍데기인 폼 페이지로 옮겨 간다"고 적었다.

⚠️ **순서대로 따라 하면 이 시점의 수정 버튼은 아직 5부의 `onClick={() => onEdit(student.id)}` 버튼이고, 목록 페이지는 `onEdit` 을 넘기지 않는다.** 누르면 **`onEdit is not a function`** 오류가 난다. 실습 6-8 에서 `Link` 로 바꾸면 해결된다 — 걱정할 오류가 아니다.

---

## 6-10. 실습 6-7 — 등록·수정 페이지 만들기

**1) `editingId` 가 사라진다 — 이번 실습에서 가장 중요한 변화**

```jsx
// 5부
const [editingId, setEditingId] = useState(null);
const isEditing = editingId !== null;

// 6부
const { id } = useParams();         // "/edit/3" 이면 "3"
const isEditing = id !== undefined;
```

**주소에 두면 새로고침해도 남는다.** 5부에서는 수정하던 중 F5 를 누르면 등록 모드로 돌아갔지만, 이제는 그 학생의 수정 화면이 그대로 뜬다.

**2) `src/pages/StudentFormPage.jsx` — 실물**

```jsx
/* 한 컴포넌트가 두 주소를 맡습니다. 등록인지 수정인지는
   주소에 id 가 붙어 있는지로 가릅니다.
     /new       →  id 가 없다   →  등록 모드
     /edit/3    →  id 가 "3"   →  수정 모드
   5부에서 useState 로 들고 있던 editingId 가 사라졌습니다.
   그 값이 주소로 옮겨갔기 때문입니다. 덕분에 수정 화면에서
   새로고침을 해도 수정 모드가 그대로 유지됩니다. */
import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import { createStudent, fetchStudent, updateStudent } from "../api/studentApi.js";
import { validateStudent } from "../lib/validation.js";
import { EMPTY_FORM, toRequest, toFormValues } from "../lib/studentData.js";
import StudentForm from "../components/StudentFormField.jsx";   // ⚠️ 교재는 StudentForm.jsx

function StudentFormPage() {
    /* useParams 는 주소의 edit/:id 자리에 있던 값을 돌려준다.
       언제나 문자열이고, /new 처럼 그 자리가 없으면 undefined 다. */
    const { id } = useParams();
    //id 값이 있다면 수정모드
    const isEditing = id !== undefined;

    // useNavigate 는 "다른 주소로 옮겨 가는 함수" 를 돌려준다.
    const navigate = useNavigate();

    const [form, setForm] = useState(EMPTY_FORM);
    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);

    /* 수정 모드면 서버에서 그 학생을 불러와 폼을 채운다.
       의존성 배열에 id 가 있으므로, 주소가 /edit/3 에서 /edit/7 로
       바뀌면 이 효과가 다시 실행된다. */
    useEffect(() => {
        //등록모드이면 처리 안됨
        if (!isEditing) {
            return;
        }

        let cancelled = false;

        async function loadStudent() {
            setLoading(true);
            try {
                const student = await fetchStudent(id);
                // 불러오는 도중에 다른 페이지로 떠났으면 state 를 건드리지 않는다.
                if (!cancelled) {
                    setForm(toFormValues(student));
                }
            } catch (error) {
                console.error("Error:", error);
                if (!cancelled) {
                    setMessage({ text: error.message, type: "error" });
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        }

        loadStudent();

        // 정리 함수 — 이 페이지를 떠날 때 위 플래그를 올린다.
        return () => {
            cancelled = true;
        };
    }, [id, isEditing]);

    function handleChange(event) {
        // 입력칸 여섯 개가 모두 이 함수 하나를 부른다.
        // 어느 칸인지는 input 에 적어 둔 name 이 알려 준다.
        const name = event.target.name;
        const value = event.target.value;

        const next = { ...form };
        next[name] = value;
        setForm(next);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setMessage(null);

        const studentData = toRequest(form);

        const errorMessage = validateStudent(studentData);
        if (errorMessage) {
            setMessage({ text: errorMessage, type: "error" });
            return;
        }

        try {
            //수정
            if (isEditing) {
                await updateStudent(id, studentData);
            } else {
                await createStudent(studentData);
            }

            /* 목록으로 돌아가면서 보여 줄 메시지를 함께 실어 보낸다.
               받는 쪽은 StudentListPage 의 useLocation() 이다. */
            const text = isEditing
                ? "학생 정보가 성공적으로 수정되었습니다."
                : "학생이 성공적으로 등록되었습니다.";

            // navigate("/") 목록 페이지로 포워딩 해라
            navigate("/", { state: { message: text } });
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    // 취소하면 목록으로 돌아간다.
    function handleCancel() {
        navigate("/");
    }

    return (
        <div className="page">
            {/* 폼만 있는 페이지이므로 목록으로 돌아갈 길을 위쪽에 둔다.
                머리말의 내비게이션과 겹치지만, 보고 있던 자리에서 가까운 편이 낫다. */}
            <Link to="/" className="back-link">&larr; 학생 목록으로</Link>

            {loading && <div className="loading">불러오는 중...</div>}

            <StudentForm
                form={form}
                isEditing={isEditing}
                message={message}
                onChange={handleChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
            />
        </div>
    );
}

export default StudentFormPage;
```

**해설**

**`useEffect` 의 `cancelled` 플래그 — "늦게 도착한 응답" 막기**

```
/edit/3 으로 들어감 → fetchStudent(3) 요청 출발
   (응답이 오기 전에) 사용자가 목록으로 떠남 → 이 페이지가 사라짐 → 정리 함수: cancelled = true
   ...뒤늦게 응답 도착 → if (!cancelled) 에 걸려 setForm 을 부르지 않는다
```

- **정리 함수에서 플래그를 올려 두면 뒤늦게 도착한 응답이 사라진 화면을 건드리지 않는다.** 🔗 5-10절 정리 함수 — 5부에서는 `clearTimeout` 에 썼다. **"다음 실행 직전과 화면에서 사라질 때 불린다"**는 규칙 그대로.
- **`/edit/3` → `/edit/7` 로 바로 옮겨 가도 같다.** `id` 가 바뀌어 효과가 다시 실행되기 **직전에** 정리 함수가 불려 이전 요청(3번)의 응답을 무시한다. 이것이 없으면 **3번 응답이 늦게 와서 7번 화면을 3번 값으로 덮어쓰는** 경쟁 상태(race condition)가 생긴다.
- `let cancelled` 가 **효과 함수 안에서** 만들어지므로, 실행마다 자기만의 플래그를 갖는다(클로저 🔗 5-12-6절).
- **`async function loadStudent()` 를 효과 안에서 따로 만들어 부른 이유** — `useEffect(async () => {...})` 처럼 효과 함수 자체를 `async` 로 만들면 **Promise 를 돌려주게 되어 정리 함수 자리를 차지한다.** React 가 경고한다. 그래서 안에서 async 함수를 만들어 부른다.
- **의존성 `[id, isEditing]`** — 효과 안에서 읽는 바깥 값 둘. `isEditing` 은 `id` 에서 계산되지만 ESLint 가 적으라고 한다.

**`loading` 이 두 개가 된다** — 목록 페이지의 `loading`(목록 불러오기) 과 폼 페이지의 `loading`(한 명 불러오기, "불러오는 중..."). **서로 다른 값이다.** 🔗 6-4절 "폼의 불러오기 표시는 페이지의 useState"(Zustand 단계에서도 store 로 가지 않는다).

**`handleSubmit` 의 변화**

| 5부 | 6부 |
|---|---|
| 성공 → `setMessage(success)` → `resetForm()` → `await loadStudents()` | 성공 → **`navigate("/", { state: { message } })`** 한 줄 |
| `updateStudent(editingId, …)` | `updateStudent(id, …)` — **`id` 는 문자열 `"3"`** 이지만 URL 에 들어가므로 그대로 써도 된다 |
| 실패 → `setMessage(error)` | 같다 — **폼에 머무르면서** 빨간 메시지 |

`resetForm()` 이 필요 없어졌다 — **목록으로 떠나면 이 페이지가 사라지고, 다음에 `/new` 로 오면 `useState(EMPTY_FORM)` 부터 새로 시작하기 때문.**

**3) `StudentForm` 에서 `containerRef` 걷어내기** — 폼이 아예 다른 페이지이므로 스크롤이 필요 없다. **취소 버튼은 등록 모드에서도 목록으로 돌아갈 길이 되므로 언제나 보여 준다.**

```jsx
// 고칠 곳 세 군데 — 실물은 StudentFormField.jsx 에 적용
// 1. 매개변수에서 containerRef 를 뺀다
function StudentForm({ form, isEditing, message, onChange, onSubmit, onCancel }) {

// 2. ref 속성을 지운다
<div className={containerClass}>

// 3. 취소 버튼을 조건 없이 보여 준다 ({isEditing && ...} 를 벗긴다)
<button type="button" className="cancel-btn" onClick={onCancel}>
    취소
</button>
```

⚠️ **실물은 이 세 군데를 `StudentFormField.jsx` 에만 고쳤다.** `StudentForm.jsx`(펼친 쪽)는 5부 그대로 남아 있다(`containerRef` · `isEditing &&` 그대로). 폼 페이지가 `StudentFormField.jsx` 를 import 하므로 이 단계에서는 문제없다 — 🔗 **그런데 Zustand 단계에서 폼 페이지가 `StudentForm.jsx` 쪽으로 import 를 바꾸면서 이 차이가 드러난다(6-16절).**

⚠️ **`useParams` 가 빈 객체다** → `App.jsx` 의 `Route path` 에 `:id` 를 적었는지 확인.

> **■ 확인하기** — 수정 버튼을 누르면 `/edit/3` 으로 주소가 바뀌고 폼에 값이 채워진다 / **그 화면에서 새로고침해도 수정 화면이 그대로다** / 등록에 성공하면 목록으로 돌아가면서 초록 메시지가 뜬다

---

## 6-11. 실습 6-8 ~ 6-10 — 수정 버튼을 링크로 · 없는 주소 · 확인과 배포

### 실습 6-8 — 표의 수정 버튼을 링크로

**수정은 다른 화면으로 옮겨 가는 일이므로 `Link` 가 맞다. 삭제는 옮겨 가지 않고 그 자리에서 하는 일이므로 버튼 그대로 둔다.** — 화면을 옮기는 일과 그 자리에서 하는 일을 가려 쓴다.

```jsx
// student_react_router_axios/src/components/StudentTable.jsx — 실물 (바뀐 곳만)
import { memo } from "react";
import { Link } from "react-router-dom";

// onEdit props 가 없어졌다. 링크가 그 일을 대신한다.
function StudentTable({ students, loading, error, onDelete }) {
    // ...
        rows = students.map((student) => (
            <tr key={student.id}>
                <td>{student.name}</td>
                {/* ... 학번 · 주소 · 전화 · 이메일 · 생년월일 그대로 ... */}
                <td>
                    {/* <button type="button" className="edit-btn"
                            onClick={() => onEdit(student.id)}>수정</button> */}

                    {/* to 에 넣은 주소로 옮겨 간다. /edit/3 처럼 만들어진다.
                        주소가 바뀌면 StudentFormPage 가 useParams 로 3 을 읽는다. */}
                    <Link to={`/edit/${student.id}`} className="edit-btn">수정</Link>

                    <button type="button" className="delete-btn"
                            onClick={() => onDelete(student.id)}>삭제</button>
                </td>
            </tr>
        ));
    // ...
}
export default memo(StudentTable);   // 5-13 에서 붙인 memo 는 그대로
```

- **`to={`/edit/${student.id}`}`** — 템플릿 리터럴(🔗 4-4절)로 주소를 만든다.
- **App 이 `handleEdit` 을 만들어 내려 줄 일이 사라졌다.** 5-13 에서 `useCallback` 으로 고정하던 `handleEdit` 자체가 없다. 🔗 "이벤트 위임 열세 줄이 사라졌다"(5-13-7) 다음으로 또 하나가 사라진 셈.
- ⚠️ **`Link` 는 `a` 태그로 그려진다.** CSS 에서 button 에 주던 스타일이 그대로 먹지 않으므로 `display` 와 `text-decoration` 을 맞춰 줘야 한다 → 6-4 에서 미리 적은 **`a.edit-btn`**.

> **■ 확인하기** — 수정 버튼에 마우스를 올리면 브라우저 아래에 `/edit/3` 이 보인다 / **가운데 버튼으로 누르면 새 탭에서 열린다. 링크이기 때문이다.**

### 실습 6-9 — 내비게이션과 없는 주소

**`src/pages/NotFoundPage.jsx` — 실물 = 교재**

```jsx
import { Link } from "react-router-dom";

function NotFoundPage() {
    return (
        <div className="page">
            <div className="not-found">
                <h2>없는 주소입니다</h2>
                <p>주소를 다시 확인해 주세요.</p>
                <Link to="/">학생 목록으로 돌아가기</Link>
            </div>
        </div>
    );
}

export default NotFoundPage;
```

`App.jsx` 의 `Route` 중 어느 것과도 맞지 않으면 여기가 그려진다. **`/edit` 처럼 `id` 를 빠뜨린 주소나 오타가 여기로 온다.**

폼 페이지 `return` 맨 앞의 `<Link to="/" className="back-link">&larr; 학생 목록으로</Link>` 도 이 실습의 일 — 머리말에 내비게이션이 있지만, **보고 있던 자리에서 가까운 링크가 하나 더 있으면 편하다.** (`&larr;` = ← HTML 엔티티, 🔗 2-11-1절 `&copy;`)

> **■ 확인하기** — `/nosuchpage` 에서 안내 페이지 / 지금 보고 있는 쪽 링크가 파란 배경 / **`/edit/3` 에서는 둘 다 `active` 가 붙지 않는다** (`/` 는 `end` 때문에, `/new` 는 주소가 달라서)

⚠️ **두 링크가 함께 강조된다** → `NavLink` 의 `/` 에 `end` 를 붙이지 않았다.

### 실습 6-10 — 확인과 배포

> **■ 확인하기** — 목록·등록·수정·삭제가 5부와 똑같다 / `/edit/3` 에서 새로고침해도 그 화면 / 뒤로 가기가 앱 안에서 이전 화면으로 / **주소를 복사해 새 탭에 붙여 넣으면 같은 화면** / Console 에 오류가 없다

```bash
npm run build
npm run preview      # 열어서 /edit/3 에서 새로고침 → 정상
```

| 서버 | 설정 |
|---|---|
| `npm run preview` | 이미 되어 있다 |
| Nginx | `try_files $uri /index.html;` |
| Apache | `.htaccess` 에 rewrite 규칙 |
| Netlify / Vercel | 기본으로 되어 있다 (교재 실습 문서 기준 — 개념 교재는 파일을 두라고 한다. 6-3절 표 참고) |

⚠️ **배포 후 `/edit/3` 에서 새로고침하면 404** → 서버 설정이 빠졌다. 라우터를 잘못 쓴 것이 아니다(6-3절 히스토리 폴백).

> **■ 여기까지가 1단계다.** 지금은 목록 페이지가 `students` 를 갖고 있는데, **페이지를 옮길 때마다 서버를 다시 부르고 있다.** 그 지점이 Zustand 를 쓸 자리다.

---

## 6-12. 준비 운동 2 — store 만 먼저 보기 (`todolist_react_router_zustand`)

### 개념

라우터 때와 같은 이유로 **store 만 떼어서 먼저 본다.** 준비 운동 1 의 `todolist_react_router` 에 store 를 얹은 것이라 새로 배울 것이 적다.

**1) 먼저 앞 예제의 문제를 본다** — `todolist_react_router` 를 열고

| | 무엇을 하나 | 어떻게 되나 |
|---|---|---|
| 1 | 할 일을 하나 추가하고, 아무 줄이나 눌러 체크한다 | 잘 된다 |
| 2 | 머리말의 "기초 컴포넌트" 를 누른다 | `/basic` 으로 옮겨 간다 |
| 3 | 다시 "할 일 목록" 으로 돌아온다 | **추가한 것도 체크한 것도 사라졌다** |

**처음 세 개로 돌아가 있다.** 목록이 `TodoPage` 안의 `useState` 에 있었기 때문. **페이지가 화면에서 내려가면(언마운트) `useState` 도 함께 사라진다.** 🔗 6-4절 불편 ③. 실습 6-11 에서 학생 목록을 store 로 옮기는 첫 번째 이유가 이것이다.

### 코드 위치
`todolist_react_router_zustand/src/store/todoStore.js` · `components/TodoCount.jsx` · `pages/TodoPage.jsx` · `App.jsx`

### 코드와 해설

**`src/store/todoStore.js` — 실물**

```js
/* store 는 컴포넌트 트리 밖에 있습니다. 페이지가 바뀌어도 살아 있고,
   어느 컴포넌트든 직접 꺼내 쓸 수 있습니다. */
import { create } from "zustand";

/* create 에 넘긴 함수가 store 의 내용을 돌려준다.
     set  값을 바꾼다. useState 의 setter 와 성격이 같다.
   set 은 넘긴 것을 기존 값과 합쳐 준다. useState 의 setter 가 통째로
   바꾸는 것과 다르므로 ...state 를 펼쳐 넣지 않아도 된다.
   create 는 두 번째 인자로 get 도 넘겨 준다. store 안의 함수가 다른
   함수를 부를 때 쓰는데(7부 studentStore.js 의 get().loadStudents()),
   여기서는 쓸 일이 없어 받지 않았다. */
export const useTodoStore = create((set) => ({
    // ── 값 ──────────────────────────────────────────────
    todos: [
        { id: 0, text: "리액트 소개", checked: false },
        { id: 1, text: "리액트 구조", checked: true },
        { id: 2, text: "리액트 사용", checked: false },
    ],

    // 다음에 만들 할 일의 번호. 0,1,2 를 이미 썼으므로 3부터.
    nextId: 3,

    // ── 값을 바꾸는 함수 ────────────────────────────────
    /* set 에 값 대신 함수를 넘기면 지금 값을 state 로 받는다.
       useState 의 함수형 업데이트와 같은 모양이다.
       이렇게 하면 바깥의 값을 읽지 않으므로 언제 불려도 안전하다. */
    addTodo: (text) =>
        set((state) => ({
            todos: [...state.todos, { id: state.nextId, text: text, checked: false }],
            nextId: state.nextId + 1,
        })),

    toggleTodo: (id) =>
        set((state) => ({
            todos: state.todos.map((todo) =>
                todo.id === id ? { ...todo, checked: !todo.checked } : todo
            ),
        })),

    removeTodo: (id) =>
        set((state) => ({
            todos: state.todos.filter((todo) => todo.id !== id),
        })),
}));
```

**해설**
- **부록 E 의 핸들러 세 개(`handleCreate` · `handleToggle` · `handleRemove`)가 거의 그대로 store 로 들어왔다.** 달라진 것은 `setTodos(…)` 가 `set((state) => ({ todos: … }))` 가 된 것뿐. 🔗 5-12-4절 "세 핸들러가 전부 새 배열을 만든다" — **불변성 규칙은 store 에서도 같다.**
- **`toggleTodo` 는 `todos` 만 돌려준다. `nextId` 를 적지 않아도 그대로 남는다** — `set` 이 합쳐 주기 때문(병합). `useState` 였다면 `nextId` 가 사라졌을 것.
- **`addTodo` 는 `todos` 와 `nextId` 를 한 번에 바꾼다.** 5부에서는 `setTodos` · `setNextId` 두 번 불렀다.
- 주석의 "7부 studentStore.js" = 이 문서의 실습 6-11(6-13절). 부 번호가 자료마다 다르다(문서 맨 앞 ⚠️).

**`src/pages/TodoPage.jsx` — 실물**

```jsx
/* 앞 예제와 견주어 보면 사라진 것이 많습니다.
     useState 세 개 중 둘이 사라졌다   todos · nextId 는 store 로 갔다
     handleToggle / handleRemove 가 사라졌다
     useCallback 두 개가 사라졌다
   store 의 함수는 create() 안에서 딱 한 번 만들어지고 다시 만들어지지
   않습니다. 그래서 memo 로 감싼 TodoItem 에 그대로 내려 주어도
   "달라졌다" 가 나오지 않습니다. useCallback 이 필요 없어진 까닭입니다. */
import { useState } from "react";

import Form from "../components/Form";
import TodoItemList from "../components/TodoItemList";
import TodoListTemplate from "../components/TodoListTemplate";

import { useTodoStore } from "../store/todoStore.js";

function TodoPage() {
    /* 입력칸의 글자는 store 에 넣지 않는다.
       이 페이지에서만 쓰는 값이고 다른 곳이 볼 이유가 없다.
       "함께 보는 값만 store 에 둔다" 가 기준이다. */
    const [todo, setTodo] = useState("");

    /* 괄호 안의 함수를 선택자(selector)라고 한다.
       "이 store 에서 todos 만 보겠다" 는 뜻이고,
       todos 가 바뀔 때만 이 컴포넌트가 다시 그려진다. */
    const todos = useTodoStore((s) => s.todos);

    const addTodo = useTodoStore((s) => s.addTodo);
    const toggleTodo = useTodoStore((s) => s.toggleTodo);
    const removeTodo = useTodoStore((s) => s.removeTodo);

    const handleChange = (e) => {
        setTodo(e.target.value);
    };

    const handleCreate = () => {
        // 빈 칸을 더하지 않는다.
        if (!todo.trim()) {
            return;
        }

        addTodo(todo.trim());   // 번호를 매기는 일은 store 가 한다
        setTodo("");            // 입력칸 비우기
    };

    const handleEnter = (e) => {
        if (e.key === "Enter") {
            handleCreate();
        }
    };

    return (
        <TodoListTemplate
            form={
                <Form
                    myTodo={todo}
                    myChange={handleChange}
                    myCreate={handleCreate}
                    myEnter={handleEnter}
                />
            }
        >
            {/* store 에서 꺼낸 함수를 그대로 내려 준다.
                감싸지 않아도 언제나 같은 함수다. */}
            <TodoItemList
                myTodos={todos}
                myToggle={toggleTodo}
                myRemove={removeTodo}
            />
        </TodoListTemplate>
    );
}

export default TodoPage;
```

**해설**
- ⚠️ **`useCallback` 이 왜 필요 없어지는가** — **store 의 함수는 `create()` 안에서 딱 한 번 만들어지고 다시 만들어지지 않는다.** 그래서 `memo` 로 감싼 TodoItem 에 그대로 내려 주어도 "달라졌다"가 나오지 않는다. **5권 부록 E-19 · E-20 에서 애써 고쳤던 문제가 store 를 쓰면 저절로 풀린다.**
- **실물에는 빈 칸 막기(`if (!todo.trim()) return;`)와 `trim()` 이 새로 들어갔다.** 부록 E 에는 없던 것 — 빈 할 일이 추가되지 않는다.
- ⚠️ **store 를 통째로 꺼내지 마라.** `const store = useTodoStore();` 라고 쓰면 store 안의 **아무 값이 바뀌어도** 그 컴포넌트가 다시 그려진다. 필요한 것만 하나씩 꺼낸다.
- `return` 에서 바깥 `<> </>` 가 없어졌다 — `TodoListTemplate` 하나를 돌려주므로 감쌀 필요가 없다.

**4) props 로는 못 하는 일 — `TodoCount` (store 를 쓰는 진짜 이유)**

```
App
 ├── header → TodoCount     ← 여기서 목록을 알고 싶다
 └── Routes
      └── TodoPage          ← 목록은 여기에 있었다
```

**둘은 부모·자식 사이가 아니다. props 로는 값을 건넬 길이 아예 없다.** props 로 하려면 목록을 App 까지 끌어올려야 하는데, 그러면 App 이 할 일 목록을 알아야 할 이유가 없는데도 떠안게 된다.

```jsx
// src/components/TodoCount.jsx — 실물
import { useTodoStore } from "../store/todoStore.js";

function TodoCount() {
    // 목록이 바뀔 때만 이 컴포넌트가 다시 그려진다.
    const todos = useTodoStore((s) => s.todos);

    // 끝낸 개수는 목록에서 계산한다. 따로 저장해 두지 않는다.
    const done = todos.filter((todo) => todo.checked).length;

    return (
        <span className="todo-count">
            할 일 {todos.length} · 끝낸 것 {done}
        </span>
    );
}

export default TodoCount;
```

```jsx
// src/App.jsx — 실물 (바뀐 곳)
import TodoCount from "./components/TodoCount.jsx";
// ...
<header className="app-header">
    {/* 제목과 개수를 한 덩어리로 묶는다.
        머리말이 space-between 이라 자식이 둘이어야 배치가 유지된다. */}
    <div className="app-brand">
        <h1 className="app-title">React 연습</h1>

        {/* 할 일 목록은 TodoPage 안에 있는데 여기서 개수를 보여 준다.
            둘은 부모·자식 사이가 아니라 props 로는 못 하는 일이다.
            store 가 트리 밖에 있어서 가능하다. */}
        <TodoCount />
    </div>
    <nav className="app-nav">...</nav>
</header>
```

```css
/* App.css 에 더한 것 */
.app-brand {
    display: flex;
    align-items: baseline;    /* 큰 제목과 작은 글자의 밑줄을 맞춘다 */
    gap: 12px;
}
.todo-count {
    font-size: 0.85rem;
    color: #666;
    white-space: nowrap;      /* 줄바꿈 금지 */
}
```

🔗 **실습 6-13 의 `AppMessage` 가 이것과 똑같은 구조다.** 머리말(Routes 바깥)에 두고 store 를 직접 꺼내 쓴다. `align-items: baseline` 은 2-5절 Flexbox 교차축 정렬.

실물 `BasicPage.jsx` 는 바깥 `<div>` 를 **프래그먼트 `<> </>`** 로 바꿨다 — "바깥틀은 App.jsx 의 `<main className="page">` 가 이미 맡고 있으므로 여기서 div 를 한 겹 더 두면 스타일 없는 껍데기만 늘어난다"는 주석이 붙었다. 🔗 5-2절 프래그먼트(컴포넌트 규칙 "돌려주는 태그는 하나").

**5) 무엇을 store 에 넣고 무엇을 안 넣나 — 기준: 여러 곳이 함께 보는 값인가**

| 값 | 어디에 | 왜 |
|---|---|---|
| `todos` · `nextId` | **store** | TodoPage 와 TodoCount 가 함께 본다 |
| 입력칸의 글자 | TodoPage 의 `useState` | 그 페이지에서만 쓴다 |
| **끝낸 개수** | **어디에도 저장하지 않는다** | `todos` 에서 세면 된다 |

⚠️ **마지막 줄이 중요하다. 끝낸 개수를 따로 저장해 두면 `todos` 와 어긋날 수 있다. 계산해서 알 수 있는 값은 state 로 두지 않는다.** 🔗 5부에서 `isEditing` 을 state 로 두지 않고 `editingId !== null` 로 계산한 것과 같은 이야기(5-4절 single source of truth).

**앞 예제와 이 예제 (README)**

| | `todolist_react_router` | `todolist_react_router_zustand` |
|---|---|---|
| 할 일 목록이 있는 곳 | `TodoPage` 의 `useState` | `store/todoStore.js` |
| 페이지를 떠났다 오면 | 사라진다 | **그대로 있다** |
| 머리말에 개수 표시 | 할 수 없다 | `TodoCount` 가 직접 꺼내 쓴다 |
| `handleToggle` · `handleRemove` | 컴포넌트에서 만든다 | store 가 갖고 있다 |
| `useCallback` | 두 개 필요했다 | **필요 없다** |

> **■ 확인하기** — 할 일을 추가하고 체크한 뒤 `/basic` 에 갔다 돌아와도 그대로 / 머리말의 개수가 목록과 같이 움직인다 / `/basic` 에 있을 때도 개수가 보인다 / 입력칸에 글자를 쳐도 Profiler 에서 TodoItem 이 다시 그려지지 않는다 / `TodoPage.jsx` 에 `useCallback` 이 하나도 없다

**실습 6-11 에서 더해지는 것**

| 더해지는 것 | 왜 생기나 |
|---|---|
| `loading` · `listError` · `loaded` | 서버를 부르니까 생긴다 |
| `get()` | store 안의 함수가 다른 함수를 부를 때 |
| `async` 함수 | 서버 응답을 기다려야 하므로 |

| 이 예제 | 학생 앱 (실습 6-11 ~ 6-14) |
|---|---|
| `todos` | `students` |
| `addTodo` · `toggleTodo` · `removeTodo` | `saveStudent` · `removeStudent` |
| `TodoCount` (머리말의 개수) | `AppMessage` (머리말 아래의 메시지) |
| — | `loading` · `listError` · `loaded` |
| — | `get()` |

**store 를 만들고 선택자로 꺼내 쓰는 방식은 여기서 본 그대로다. 실습 6-11 부터는 그 위에 서버 통신이 얹힐 뿐이다.**

---

## 6-13. 실습 6-11 — Zustand 설치와 store 만들기

**1) 폴더 복사와 설치** — 앞 단계를 그대로 두고 새 폴더에서 이어 간다. **두 단계를 견주어 볼 수 있어야 하기 때문이다.**

```bash
rm -rf node_modules
cd ..
cp -r student_react_router_axios student_react_router_axios_zustand
cd student_react_router_axios_zustand
npm install
npm install zustand
```

⚠️ 실물은 복사하면서 앞 단계의 두 페이지를 **`StudentListPageRouter.jsx` · `StudentFormPageRouter.jsx`** 로 이름만 바꿔 남겨 두었다(내용은 6-9 · 6-10절과 한 글자도 같음, import 하는 곳 없음). **`AppOrg.jsx` · `studentApiFetch.js` 와 같은 "보관용" 습관.**

**2) 무엇이 불편했는지 먼저 본다** — 지금 목록 페이지는 네 개의 값을 `useState` 로 들고 있다.

| 불편한 점 | 왜 그런가 |
|---|---|
| 페이지를 떠나면 값이 사라진다 | 목록으로 돌아올 때마다 서버를 다시 부른다 |
| 메시지를 나르기 번거롭다 | 등록 성공 문구를 주소에 실어 보내야 한다 |

**3) `src/store/studentStore.js` — 실물**

```js
/* 6부까지는 이 값들이 StudentListPage 안의 useState 에 있었습니다.
   그래서 두 가지가 불편했습니다.
     (1) 목록 페이지를 떠났다 돌아오면 값이 사라져 서버를 다시 불렀다
     (2) 등록 성공 문구를 페이지 밖으로 나르려고
         navigate("/", { state: { message } }) 를 써야 했다
   store 는 컴포넌트 트리 밖에 있습니다. 페이지가 바뀌어도 살아 있고,
   어느 컴포넌트든 직접 꺼내 쓸 수 있습니다. 위 두 가지가 사라집니다. */
import { create } from "zustand";

import {
    fetchStudents,
    createStudent,
    updateStudent,
    deleteStudent,
} from "../api/studentApi.js";

/* create 에 넘긴 함수가 store 의 내용을 돌려줍니다.
     set  값을 바꾼다. useState 의 setter 와 성격이 같다.
     get  지금 값을 읽는다. 함수 안에서 다른 값이 필요할 때 쓴다.
   set 은 넘긴 것을 기존 값과 합쳐 준다. useState 의 setter 가 통째로
   바꾸는 것과 다르므로 ...state 를 펼쳐 넣지 않아도 된다. */
export const useStudentStore = create((set, get) => ({
    // ── 값(상태변수)을 변경하려면 인자로 받은 set() 함수를 호출해야 함
    students: [],
    loading: false,
    listError: null,
    message: null,          // { text, type } 또는 null
    loaded: false,          // 한 번이라도 목록을 불러왔는가

    // ── 아래에 정의된 변경된 값(상태변수) ──────────────
    showError: (text) => set({ message: { text: text, type: "error" } }),
    showSuccess: (text) => set({ message: { text: text, type: "success" } }),
    clearMessage: () => set({ message: null }),

    // ── 목록 불러오기 ───────────────────────────────────
    loadStudents: async () => {
        set({ loading: true, listError: null });

        try {
            const data = await fetchStudents();
            set({ students: data, loaded: true });
        } catch (error) {
            console.error("Error:", error);
            set({
                message: { text: error.message, type: "error" },
                listError: "오류: 데이터를 불러올 수 없습니다.",
            });
        } finally {
            set({ loading: false });
        }
    },

    /* 등록과 수정을 한 함수로 받는다. id 가 있으면 수정이다.
       성공하면 true, 실패하면 false 를 돌려준다.
       페이지는 그 값을 보고 목록으로 옮겨 갈지 정한다. */
    saveStudent: async (id, studentData) => {
        try {
            if (id) {
                await updateStudent(id, studentData);
                get().showSuccess("학생 정보가 성공적으로 수정되었습니다.");
            } else {
                await createStudent(studentData);
                get().showSuccess("학생이 성공적으로 등록되었습니다.");
            }

            // 서버 값이 바뀌었으므로 목록을 다시 불러 둔다.
            await get().loadStudents();
            return true;
        } catch (error) {
            console.error("Error:", error);
            get().showError(error.message);
            return false;
        }
    },

    // ── 삭제 ────────────────────────────────────────────
    removeStudent: async (id) => {
        try {
            await deleteStudent(id);
            get().showSuccess("학생이 성공적으로 삭제되었습니다.");
            await get().loadStudents();
        } catch (error) {
            console.error("Error:", error);
            get().showError(error.message);
        }
    },
}));
```

**해설**

- **5부 App 의 `loadStudents` · `handleSubmit` 의 저장 부분 · `handleDelete` 가 거의 글자 그대로 store 로 들어왔다.** `setLoading(true)` → `set({ loading: true })`, `setMessage({...})` → `get().showSuccess(...)` 로 바뀐 것뿐. 🔗 **`try / catch / finally` 구조는 4부부터 한 번도 바뀌지 않았다**(3부는 `.then/.catch`).
- **`set({ loading: true, listError: null })`** — `useState` 였다면 `setLoading` · `setListError` 두 번이었다. **한 번에 여러 칸을 바꿀 수 있다.**
- ⚠️ **`get()` 은 함수 안에서 store 의 다른 함수를 부를 때 쓴다.** `saveStudent` 가 끝난 뒤 `get().loadStudents()` 로 목록을 다시 불러 두는 자리. **`this.loadStudents()` 가 아니다** — 화살표 함수라 `this` 가 store 가 아니다(🔗 4-4절 화살표 함수의 `this`).
- **`saveStudent(id, studentData)` 가 등록·수정을 한 함수로 받는다.** `id` 가 있으면(truthy) 수정. 5부 `handleSubmit` 의 `if (isEditing)` 분기가 여기로 왔다.
- ⚠️ **성공하면 `true`, 실패하면 `false` 를 돌려준다.** 오류를 store 안에서 이미 처리(`showError`)했으므로 **다시 던지지 않는다.** 그래서 페이지는 `try/catch` 없이 **돌려받은 값만 보고** 목록으로 옮겨 갈지 정한다(6-16절).
- **`loaded` — "한 번이라도 목록을 불러왔는가."** 목록 페이지가 이 값을 보고 서버를 부를지 정한다(6-14절).
- ⚠️ **등록·수정·삭제 뒤에 `loadStudents()` 로 목록을 다시 불러 둔다** — store 에 남은 목록이 낡지 않게. 6-4절 "양날".
- **이 파일에 JSX 가 한 줄도 없다. 반대로 페이지에는 `axios` 가 한 줄도 없게 된다.**

---

## 6-14. 실습 6-12 — 목록 페이지를 store 에 잇기

파일 전체를 바꾼다. **`useState` 가 한 줄도 없어진 것에 주목.**

```jsx
// student_react_router_axios_zustand/src/pages/StudentListPage.jsx — 실물
/* 6부에서는 이 페이지가 students · loading · listError · message 를
   직접 갖고 있었습니다. 그 넷이 모두 store 로 옮겨갔습니다.
   그래서 이 파일에 useState 가 한 줄도 없습니다. 하는 일은 둘뿐입니다.
     (1) 아직 안 불러왔으면 목록을 불러오라고 시킨다
     (2) store 에서 꺼낸 값을 표에 넘긴다 */
import { useEffect } from "react";

import { useStudentStore } from "../store/studentStore.js";
import StudentTable from "../components/StudentTable.jsx";

function StudentListPage() {
    /* 필요한 것만 하나씩 꺼낸다. store 전체를 꺼내면
       상관없는 값이 바뀔 때도 이 페이지가 다시 그려진다. */
    const students = useStudentStore((s) => s.students);
    const loading = useStudentStore((s) => s.loading);
    const listError = useStudentStore((s) => s.listError);
    const loaded = useStudentStore((s) => s.loaded);

    const loadStudents = useStudentStore((s) => s.loadStudents);
    const removeStudent = useStudentStore((s) => s.removeStudent);

    /* 한 번도 안 불러왔을 때만 부른다.
       6부에서는 이 페이지로 돌아올 때마다 서버를 다시 불렀습니다.
       store 는 페이지가 바뀌어도 살아 있으므로 그럴 필요가 없습니다. */
    useEffect(() => {
        if (!loaded) {
            loadStudents();
        }
    }, [loaded, loadStudents]);

    async function handleDelete(studentId) {
        if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) {
            return;
        }
        // 지우고 목록을 다시 불러오는 일은 store 가 맡는다.
        await removeStudent(studentId);
    }

    return (
        <div className="page">
            <StudentTable
                students={students}
                loading={loading}
                error={listError}
                onDelete={handleDelete}
            />
        </div>
    );
}

export default StudentListPage;
```

**해설**

- **선택자 여섯 줄** — 값 넷 + 함수 둘. **함수도 선택자로 꺼낸다.** 함수는 바뀌지 않으므로 이 선택자 때문에 다시 그려질 일은 없다.
- ⚠️ **`[loaded, loadStudents]`** — `loadStudents` 는 store 의 함수라 **언제나 같은 함수**다(🔗 6-12절 "useCallback 이 필요 없어진 까닭"). 그래서 의존성에 넣어도 5-13 처럼 안전하다. **`eslint-disable` 주석도 사라졌다** — 이 효과 안에서 `setState` 를 직접 부르지 않기 때문(store 의 함수를 부를 뿐).
- **`loaded` 를 두는 이유** — 앞 단계에서는 이 페이지에 올 때마다 서버를 불렀다. 이제 store 에 목록이 남아 있으므로 **한 번도 안 불러왔을 때만** 부른다. ⚠️ **`students.length === 0` 으로 판단하면 안 된다** — 학생이 정말 0명이면 올 때마다 서버를 부르게 된다. "불러왔는가"와 "비었는가"는 다른 질문이라 값을 따로 둔다.
- `handleDelete` 는 **`confirm` 만 남았다.** "정말 지울까?"는 **화면의 일**이라 페이지에, "지우고 다시 불러오기"는 **데이터의 일**이라 store 에. 🔗 6-4절 "화면 코드와 섞임"의 해소.
- **`MessageBox` import 도, 메시지 타이머 `useEffect` 도 사라졌다** → 다음 실습의 `AppMessage` 로.
- ⚠️ **실습 6-12 만 한 시점에는 메시지가 안 보인다.** 목록 페이지에서 `MessageBox` 를 뺐고 `AppMessage` 는 아직 없기 때문. 6-13 까지 해야 다시 보인다.

**선택자를 쓰는 이유 (다시)**

```jsx
// 좋지 않다 — loading 이 바뀌어도 이 페이지가 다시 그려진다
const store = useStudentStore();
const students = store.students;

// 이렇게 — students 가 바뀔 때만 다시 그려진다
const students = useStudentStore((s) => s.students);
```

> **■ 확인하기** — 목록이 5부와 똑같이 보인다 / **수정 페이지에 갔다가 돌아오면 목록이 깜빡이지 않고 바로 보인다** / **F12 → Network 에서 돌아올 때 요청이 나가지 않는다**

---

## 6-15. 실습 6-13 — 메시지를 store 로 옮기기 (`AppMessage`)

**1) 없어지는 코드** — 앞 단계에서 등록 성공 문구를 목록 페이지로 보내려고 주소에 실어 보냈던 것.

```jsx
// StudentFormPage — 보내는 쪽
navigate("/", { state: { message: text } });

// StudentListPage — 받는 쪽
const location = useLocation();
const [message, setMessage] = useState(
    location.state?.message ? { text: location.state.message, type: "success" } : null
);
```

**2) `src/components/AppMessage.jsx` — 실물 (새 파일)**

```jsx
/* 6부까지는 메시지가 페이지마다 따로 있었습니다. 그래서 등록 성공
   문구를 목록 페이지로 나르려고 navigate 에 실어 보내야 했습니다.
   이제 메시지는 store 에 있습니다. 어느 페이지에서 넣든 여기 한 곳이
   그려 주므로, 페이지 사이로 값을 나를 일이 없습니다.
   성공 메시지를 3초 뒤에 지우는 일도 여기서 한 번만 합니다. */
import { useEffect } from "react";

import { useStudentStore } from "../store/studentStore.js";
import MessageBox from "./MessageBox.jsx";

// 성공 메시지가 저절로 사라지기까지의 시간(ms)
const MESSAGE_TIMEOUT = 3000;

function AppMessage() {
    /* 괄호 안의 함수를 선택자(selector)라고 한다.
       "이 store 에서 message 만 보겠다" 는 뜻이고,
       message 가 바뀔 때만 이 컴포넌트가 다시 그려진다. */
    const message = useStudentStore((s) => s.message);
    const clearMessage = useStudentStore((s) => s.clearMessage);

    // 성공 메시지는 3초 뒤에 저절로 사라진다.
    useEffect(() => {
        if (!message) {
            return;
        }

        // 오류 메시지는 사용자가 고칠 때까지 남겨 둔다.
        if (message.type !== "success") {
            return;
        }

        const timer = setTimeout(clearMessage, MESSAGE_TIMEOUT);

        // 정리 함수 — 다음 실행 직전과 화면에서 사라질 때 불린다.
        return () => clearTimeout(timer);
    }, [message, clearMessage]);

    return (
        <div className="app-message">
            <MessageBox message={message} />
        </div>
    );
}

export default AppMessage;
```

**해설**
- 🔗 **타이머 `useEffect` 는 5부 App(5-13-6절)의 것과 글자 하나 다르지 않다.** 다른 것은 `setMessage(null)` 대신 store 의 **`clearMessage`** 를 부르는 것뿐. `setTimeout(clearMessage, …)` — **함수를 부르지 않고 넘긴다**(괄호 없음, 🔗 3-10절).
- **`MessageBox.jsx` 는 한 글자도 고치지 않았다.** `message` 가 props 로 오든 store 에서 오든, `MessageBox` 는 받은 것을 그릴 뿐 — **프레젠테이셔널 컴포넌트**의 이득.
- `[message, clearMessage]` — `clearMessage` 는 store 함수라 바뀌지 않지만 ESLint 를 위해 적는다.
- ⚠️ **이 컴포넌트는 `useStudentStore` 에서 `message` 를 꺼낸다 — props 가 하나도 없다.** App 은 그냥 `<AppMessage />` 를 놓기만 한다. 🔗 6-12절 `TodoCount` 와 같은 구조.

**3) `App.jsx` 에 자리 만들기 — `Routes` 바깥에 둬야 페이지가 바뀌어도 사라지지 않는다**

```jsx
// student_react_router_axios_zustand/src/App.jsx — 실물 (바뀐 곳 두 군데)
import AppMessage from "./components/AppMessage.jsx";
// ...
            </header>

            {/* store 의 메시지를 그리는 자리. 어느 페이지에서든 여기 뜬다. */}
            <AppMessage />

            <Routes>
                ...
            </Routes>
```

**4) `StudentForm` 에서 `message` 걷어내기 (교재)** — 메시지를 위쪽에서 한 번만 그리므로 폼은 더 이상 메시지를 받지 않는다.

```jsx
// 1. MessageBox import 를 지운다
// 2. 매개변수에서 message 를 뺀다
function StudentForm({ form, isEditing, onChange, onSubmit, onCancel }) {
// 3. 버튼 옆의 <MessageBox message={message} /> 줄을 지운다
```

⚠️ **실물은 이 단계를 하지 않았다.** `StudentForm.jsx` 와 `StudentFormField.jsx` 모두 여전히 `message` 를 받고 `<MessageBox message={message} />` 를 그린다. **그래도 문제가 없는 이유** — 폼 페이지가 `message` props 를 **넘기지 않으므로** `message` 가 `undefined` 이고, `MessageBox` 는 `if (!message) return null;` 로 아무것도 그리지 않는다. **동작은 교재와 같고, 쓰지 않는 코드가 남아 있을 뿐이다.**

**5) `src/style.css` — 맨 아래에 추가 (실물)**

```css
/* Zustand 적용 이후에 더한 것 — 화면 위쪽(AppMessage)의 메시지 자리 */
.app-message {
    max-width: 1200px;
    margin: 0 auto 16px;
    /* 아래 여백 16px — 이것이 없으면 메시지가 목록 제목에 딱 붙는다 */
    padding: 0 20px;
    min-height: 24px;
    /* 메시지가 없을 때도 자리를 비워 두어 화면이 덜컥이지 않게 한다 */
}
/* .app-brand · .app-brand .app-mode — 6-4 에서 적은 것이 한 번 더 들어 있다 (중복) */
```

⚠️ **`min-height: 24px` — 메시지가 없을 때도 자리를 비워 둔다.** 없으면 메시지가 나타나고 사라질 때마다 **아래 화면 전체가 위아래로 덜컥인다**(레이아웃 이동). 🔗 2-3절 박스 모델. `max-width` + `margin: 0 auto` 로 머리말·페이지와 같은 폭에 가운데 정렬.

⚠️ **`.page > .error-message { display: block }`(6-4)은 이제 쓰이지 않는다.** 메시지가 `.page` 안이 아니라 `.app-message` 안에 그려지기 때문. 남아 있어도 해는 없다.

> **■ 확인하기** — 어느 페이지에서나 메시지가 화면 위쪽 같은 자리에 뜬다 / 성공 메시지는 3초 뒤 사라지고 오류 메시지는 남는다 / 메시지와 그 아래 "학생 목록" 제목 사이에 여백이 있다

---

## 6-16. 실습 6-14 — 폼 페이지를 store 에 잇기

```jsx
// student_react_router_axios_zustand/src/pages/StudentFormPage.jsx — 실물
/* 등록인지 수정인지는 주소에 id 가 붙어 있는지로 가릅니다.
   6부와 달라진 곳은 둘입니다.
     (1) 서버에 보내는 일을 store 의 saveStudent 가 맡는다
     (2) 메시지를 navigate 에 실어 보내지 않는다.
         store 에 넣으면 목록 페이지에서 저절로 보인다.
   form 은 그대로 이 페이지의 useState 에 둡니다. 입력 중인 값은
   이 화면에서만 쓰고 버리는 것이라 store 에 둘 이유가 없습니다. */
import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import { fetchStudent } from "../api/studentApi.js";
import { validateStudent } from "../lib/validation.js";
import { EMPTY_FORM, toRequest, toFormValues } from "../lib/studentData.js";
import { useStudentStore } from "../store/studentStore.js";
import StudentForm from "../components/StudentForm.jsx";      // ⚠️ 펼친 쪽으로 바뀜

function StudentFormPage() {
    const { id } = useParams();
    const isEditing = id !== undefined;
    const navigate = useNavigate();

    // 입력 중인 값은 이 화면만의 것이다.
    const [form, setForm] = useState(EMPTY_FORM);
    const [loading, setLoading] = useState(false);

    // 서버에 보내는 일과 메시지는 store 가 맡는다.
    const saveStudent = useStudentStore((s) => s.saveStudent);
    const showError = useStudentStore((s) => s.showError);
    const clearMessage = useStudentStore((s) => s.clearMessage);

    //const loading = useStudentStore((s) => s.loading);

    // 수정 모드면 서버에서 그 학생을 불러와 폼을 채운다.
    useEffect(() => {
        if (!isEditing) {
            return;
        }

        let cancelled = false;

        async function loadStudent() {
            setLoading(true);
            try {
                const student = await fetchStudent(id);
                if (!cancelled) {
                    setForm(toFormValues(student));
                }
            } catch (error) {
                console.error("Error:", error);
                if (!cancelled) {
                    showError(error.message);
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        }

        loadStudent();

        // 정리 함수 — 이 페이지를 떠날 때 위 플래그를 올린다.
        return () => {
            cancelled = true;
        };
    }, [id, isEditing, showError]);

    function handleChange(event) {
        const name = event.target.name;
        const value = event.target.value;

        const next = { ...form };
        next[name] = value;
        setForm(next);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        clearMessage();

        const studentData = toRequest(form);

        const errorMessage = validateStudent(studentData);
        if (errorMessage) {
            showError(errorMessage);
            return;
        }

        /* 성공하면 true 가 돌아온다. 성공 문구는 store 가 이미 넣어 두었으므로
           여기서는 옮겨 가기만 하면 된다. navigate 에 값을 실을 필요가 없다. */
        const saved = await saveStudent(isEditing ? id : null, studentData);

        if (saved) {
            navigate("/");
        }
    }

    function handleCancel() {
        clearMessage();
        navigate("/");
    }

    return (
        <div className="page">
            <Link to="/" className="back-link">&larr; 학생 목록으로</Link>

            {loading && <div className="loading">불러오는 중...</div>}

            <StudentForm
                form={form}
                isEditing={isEditing}
                onChange={handleChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
            />
        </div>
    );
}

export default StudentFormPage;
```

**해설**

- **`try / catch` 가 `handleSubmit` 에서 사라졌다.** `saveStudent` 가 오류를 안에서 처리하고 `true / false` 만 돌려주기 때문. **`if (saved) navigate("/")`** — 실패하면 폼에 머무르고, 빨간 메시지는 store 가 이미 넣어 두었다.
- **`isEditing ? id : null`** — store 의 `saveStudent` 는 `if (id)` 로 등록·수정을 가른다. 등록이면 `null` 을 넘긴다(`undefined` 여도 되지만 뜻을 분명히).
- **`setMessage` 자리가 전부 `showError` · `clearMessage` 로 바뀌었다.** 폼 페이지의 `message` state 가 없어졌다.
- **`fetchStudent` 는 여전히 api 를 직접 부른다.** 한 명의 정보는 이 화면에서만 쓰고 버리므로 store 에 두지 않는다(6-4절).
- ⚠️ **`const [loading, setLoading] = useState(false)` 는 남았다.** 바로 아래 **`//const loading = useStudentStore((s) => s.loading);`** 이 주석으로 남아 있는 것에 주목 — **store 의 `loading` 은 "목록을 불러오는 중"이고, 이 페이지의 `loading` 은 "학생 한 명을 불러오는 중"이다. 다른 값이다.** store 것을 쓰면 저장 후 `loadStudents()` 가 도는 동안 폼에 "불러오는 중..."이 뜨는 엉뚱한 일이 생긴다.
- **`handleCancel` 에 `clearMessage()`** — 폼에서 난 오류 메시지를 들고 목록으로 가지 않게.
- `useEffect` 의존성에 **`showError`** 가 더해졌다(효과 안에서 부르므로). store 함수라 바뀌지 않아 효과가 괜히 다시 돌지 않는다.

**`form` 은 왜 store 에 두지 않는가**

| 값 | 어디에 | 이유 |
|---|---|---|
| `students` | store | 목록 페이지와 폼 페이지가 함께 본다 |
| `message` | store | 어느 페이지에서 넣든 한곳에서 보여 준다 |
| `form` | 페이지의 `useState` | 이 화면에서만 쓰고 버린다 |
| 폼의 불러오기 표시 | 페이지의 `useState` | 목록의 로딩과 다른 값이다 |

⚠️ **실물이 교재와 다른 곳 — `import StudentForm from "../components/StudentForm.jsx"`**

앞 단계(6-10절)는 `StudentFormField.jsx` 를 import 했는데, 이 단계는 교재 코드를 따라 **`StudentForm.jsx`** 로 바꿨다. 그런데 **실물 `StudentForm.jsx` 는 5부에서 한 번도 고치지 않은 파일**이다(6-10 의 세 군데 수정은 `StudentFormField.jsx` 에만 했다).

| 5부 그대로인 `StudentForm.jsx` 의 모습 | 그래서 실물에서 생기는 일 |
|---|---|
| `containerRef` 를 받아 `ref={containerRef}` | 넘기지 않으므로 `undefined` — **해 없음** |
| `message` 를 받아 `<MessageBox>` | 넘기지 않으므로 `null` 반환 — **해 없음** |
| **`{isEditing && (<button ... 취소>)}`** | ⚠️ **`/new`(등록 화면)에서 취소 버튼이 안 보인다.** 교재는 "취소 버튼을 언제나 보여 준다" 였다 |

**교재대로 되려면 6-10절의 세 군데 수정(와 6-15절의 `message` 걷어내기)을 `StudentForm.jsx` 에도 해 주거나, import 를 `StudentFormField.jsx` 로 되돌리면 된다.** 등록 화면에서도 위쪽 "← 학생 목록으로" 링크와 머리말 내비가 있으니 기능상 막히지는 않는다.

> **■ 확인하기** — 등록·수정·삭제가 앞 단계와 똑같다 / 등록에 성공하면 목록으로 돌아가면서 초록 메시지 / **같은 학번으로 등록하면 폼에 머무르면서 빨간 메시지** / Console 에 오류가 없다

---

## 6-17. 5부 ↔ 6부 대응표 (부록 A)

5부의 코드가 어디로 갔는지 찾을 때 쓴다.

| 5부 | 6부 | 달라진 점 |
|---|---|---|
| `config.js` `STUDENTS_URL` | `config.js` `STUDENTS_PATH` | 앞부분은 axios 가 기억한다 (실물은 둘 다 남음) |
| `api/studentApi.js` `request()` | `api/client.js` | `create` + 가로채기로 대체 |
| `fetch(url)` | `client.get(path)` | 본문은 `response.data` |
| `App.jsx` 의 state 여섯 개 | 페이지별로 나뉨 | 목록은 목록 페이지, 폼은 폼 페이지 |
| `editingId` state | `useParams` 의 `id` | **주소가 상태를 담는다** |
| `App.jsx` 의 화면 | `App.jsx` 의 `Routes` | 주소와 화면을 짝짓는 일만 한다 |
| `loadStudents()` 다시 호출 | 페이지 이동 | 옮겨 가면 저절로 다시 부른다 |
| `onEdit` props | `Link to` | 화면 이동은 링크가 맡는다 |
| `scrollToForm` (`useRef`) | 없어짐 | 폼이 다른 페이지라 필요 없다 |
| `resetForm()` | 없어짐 | 페이지를 떠나면 폼 state 도 사라진다 |
| `useCallback` 네 개 (5-13) | 없어짐 | `handleEdit` 은 Link 로, 나머지는 memo 자식에 안 넘김 / store 함수는 원래 고정 |
| `lib/validation.js` | 그대로 | **0줄 수정** |
| `lib/studentData.js` | 그대로 | **0줄 수정** |
| `components/MessageBox.jsx` | 그대로 | **0줄 수정** |
| StudentListPage 의 `useState` 넷 | `store/studentStore.js` | 페이지가 바뀌어도 남는다 |
| `navigate` 에 메시지 싣기 | store 의 `message` | 나를 필요가 없어졌다 |
| 페이지마다 `MessageBox` | `AppMessage` 하나 | App 에서 한 번만 그린다 |

> **■ 값만 다루는 파일 셋은 통신 방법과 화면 구조가 모두 바뀌었는데도 한 줄도 고치지 않았다.** 4부에서 "화면을 건드리는 코드"와 "값만 다루는 코드"를 나눠 둔 보상이 여기서도 돌아온다. 🔗 5-13-1절 "프레임워크를 바꿔도 살아남는 코드".

**학생 앱 5단계 — 같은 기능, 다른 구조**

| 단계 | 폴더 | 서버 부르기 | 화면 | 값이 사는 곳 |
|---|---|---|---|---|
| 3부 | `student_js` | `fetch` + `.then` | DOM 직접 | DOM · 전역 변수 |
| 4부 | `student_ecma` | `fetch` + `request()` | `ui/*.js` 가 DOM | DOM · 모듈 변수 |
| 5부 | `student_react_first` | `fetch` + `request()` | 컴포넌트 | App 의 `useState` |
| 6부 ① | `student_react_router_axios` | **axios** | **페이지 셋** | 페이지의 `useState` + **주소** |
| 6부 ② | `…_zustand` | axios | 페이지 셋 + `AppMessage` | **store** + 주소 + 폼만 `useState` |

---

## 6-18. React 중급 자주 나는 오류 모음 (부록 B 두 벌 합침)

| 오류 메시지 / 증상 | 원인과 해결 |
|---|---|
| `Cannot find module 'axios'` | 프로젝트 폴더 안에서 `npm install` 했는지 확인 |
| `Network Error` (상태 코드 없음) | 서버가 꺼져 있거나 주소가 틀렸다. `error.response` 가 없는 경우 |
| `error.response` 가 `undefined` | 응답 자체가 없는 경우. `?.` 로 방어 |
| 요청 주소가 이상하다 (`/api/students/api/students` 등) | `baseURL` 과 `STUDENTS_PATH` 가 겹치거나 빠졌다 |
| `blocked by CORS policy` | **서버 설정 문제.** axios 로 바꿔도 해결되지 않는다 |
| 인터셉터를 달았는데 오류가 `catch` 로 안 간다 | 실패 쪽에서 `return error` 를 했다. `return Promise.reject(...)` 또는 `throw` |
| `useNavigate() may be used only in the context of a <Router>` | `BrowserRouter` 로 감싸지 않았다 |
| `Failed to resolve import "./pages/…"` | 페이지 파일을 만들기 전에 import 했다. **스텁을 먼저** |
| `useParams` 가 빈 객체다 | `Route` 의 `path` 에 `:id` 를 적었는지 확인 |
| 링크를 누르면 화면이 깜빡인다 / state 가 날아간다 | `a` 태그를 썼다. `Link` 로 |
| `NavLink` 두 개가 함께 강조된다 | `/` 에 `end` 를 붙이지 않았다 |
| 수정 버튼 모양이 깨진다 | `a.edit-btn` 에 `display: inline-block` 이 빠졌다 |
| `onEdit is not a function` | 페이지가 `onEdit` 을 안 넘기는데 표가 아직 `onClick={() => onEdit(…)}` 이다. 실습 6-8 로 `Link` 로 바꾼다 |
| 배포 후 새로고침하면 404 | 서버가 `index.html` 을 돌려주도록(히스토리 폴백) |
| 폴백 설정 뒤 `/api/students` 가 HTML 로 온다 | 폴백 규칙이 API 주소까지 잡아먹었다. `/api` 를 빼라 |
| 수정 후 목록이 갱신되지 않는다 | `navigate("/")` 로 옮겨 가는지 / (store 단계) `saveStudent` 안에서 `loadStudents()` 를 부르는지 |
| 늦게 온 응답이 다른 학생 값으로 폼을 덮는다 | `useEffect` 정리 함수의 `cancelled` 플래그가 없다 |
| `useEffect must not return anything besides a function` | `useEffect(async () => …)` — 효과 함수를 async 로 만들었다. 안에서 async 함수를 만들어 부른다 |
| store 값을 바꿨는데 화면 그대로 | 배열·객체를 직접 고쳤다. 새로 만들어 `set` 에 넘긴다 |
| 상관없는 값이 바뀌어도 다시 그려진다 | 선택자 없이 store 전체를 꺼냈다 |
| store 에 넣었는데 다른 칸이 사라졌다 | (없다 — `set` 은 병합한다.) 사라졌다면 `useState` setter 와 헷갈려 통째로 넘긴 코드를 다시 보라 |
| 목록 페이지에 돌아올 때마다 요청이 나간다 (store 단계) | `loaded` 확인 없이 `useEffect(…, [])` 에서 매번 `loadStudents()` |
| 메시지가 페이지를 옮기면 사라진다 / 두 번 뜬다 | `AppMessage` 를 `Routes` 안에 두었거나, 페이지에도 `MessageBox` 가 남았다 |
| `/new` 에 취소 버튼이 없다 (실물 zustand 단계) | 폼 페이지가 5부 그대로의 `StudentForm.jsx` 를 import 한다(6-16절) |

---

## 6-19. React 중급 파트 자가 점검 질문

**개념 — 세 라이브러리**
1. 세 라이브러리가 각각 푸는 문제를 한 줄씩 말하라. 6부에서 Axios → Router → Zustand 순서로 넣는 이유는?
2. "라이브러리를 고르는 기준" 다섯 가지 중 "빠져나갈 수 있는가"가 이 앱에서 어떻게 지켜졌나?

**Axios**
3. `fetch` 가 404 를 "성공"으로 보는 이유는? axios 는 어떻게 다른가?
4. `axios.create` 로 인스턴스를 만들면 무엇이 좋아지나? `baseURL` 과 `STUDENTS_PATH` 의 관계는?
5. 응답 인터셉터의 실패 쪽 함수가 하는 일을 `??` 세 단 폴백으로 설명하라. 4부 `request()` 와 무엇이 같고 무엇이 다른가?
6. `error.response` 가 `undefined` 인 경우는 언제인가?
7. 인터셉터에서 `return Promise.reject(new Error(message))` 대신 `return error` 라고 쓰면?
8. axios 로 바꾸면 CORS 오류가 해결되나?
9. `studentApi.js` 에 `try / catch` 가 없는데 오류는 어디서 잡히나?

**React Router**
10. SPA 가 "잃은 것"은 무엇이며, 그 때문에 생기는 불편 세 가지는?
11. `BrowserRouter` · `Routes` · `Route` 가 각각 맡는 일은? `Routes` 바깥에 두는 것은 무엇인가?
12. `<a href>` 대신 `<Link to>` 를 쓰는 이유는? `NavLink` 의 `end` 가 없으면?
13. `useParams` 가 돌려주는 값의 타입은? `/new` 에서는 무엇이 나오나?
14. `navigate(-1)`, `navigate("/", { replace: true })`, `navigate("/", { state })` 의 차이는?
15. "주소도 상태를 담는 자리다" — 주소에 둘 값과 state 에 둘 값을 두 개씩 예로 들라.
16. 배포 후 `/edit/3` 에서 새로고침하면 404 가 나는 이유와 해결법(히스토리 폴백)은? Spring Boot 에서 주소를 하나하나 적는 이유는?
17. `HashRouter` 는 왜 서버 설정 없이 동작하나? 단점은?

**Zustand · 상태 관리**
18. 상태 관리 라이브러리가 덜어 주는 불편 네 가지는? 5부 앱은 왜 하나도 겪지 않았나?
19. `create((set, get) => …)` 에서 `set` 과 `get` 의 역할은? `this.loadStudents()` 가 아닌 이유는?
20. Zustand `set` 과 `useState` setter 의 차이(병합 vs 교체)를 `toggleTodo` 로 설명하라.
21. 선택자를 쓰는 이유는? `const store = useStudentStore()` 의 문제는?
22. Redux 가 값을 바꾸는 네 단계는? Redux 가 한 단계 더 거치는 것이 "낭비가 아닌" 이유는?
23. Redux Toolkit 과 Zustand 를 상자 만들기 · 앱 감싸기 · 값 바꾸기 세 단계로 비교하라.
24. Context API 와 Zustand 의 차이 — "통로"와 "상자"의 뜻은? 각각 어울리는 값은?
25. 컴포넌트 밖에서 store 를 쓰는 방법은? 왜 `useStudentStore(...)` 는 안 되나?
26. 무엇을 store 에 두고 무엇을 두지 않나? `form` 과 폼의 `loading` 이 store 에 가지 않은 이유는?
27. Zustand 를 넣으면 없어지는 코드 두 가지는? 값이 store 에 남는 것이 "양날"인 이유는?

**실습 코드**
28. 준비 운동 1 에서 "라우터는 세 곳에만 있다" — 어디인가? 페이지 파일에 라우터 코드가 없는 이유는?
29. 준비 운동 2 의 `TodoPage` 에서 `useCallback` 이 사라진 이유는?
30. `TodoCount` 가 props 로는 불가능한 이유를 트리 그림으로 설명하라. "끝낸 개수"를 저장하지 않는 이유는?
31. 스텁 페이지를 먼저 만드는 이유는?
32. 목록 페이지가 `useLocation` 으로 받은 메시지를 `useEffect` 가 아니라 `useState` 초깃값으로 넣은 이유는?
33. 폼 페이지 `useEffect` 의 `cancelled` 플래그가 막는 두 가지 상황은? `useEffect(async () => …)` 로 쓰면 안 되는 이유는?
34. 수정은 `Link`, 삭제는 `button` 인 이유는?
35. `.app-brand` 로 제목과 배지를 묶은 이유는? `a.edit-btn` 에 `inline-block` 이 필요한 이유는?
36. `saveStudent` 가 `true / false` 를 돌려주는 설계의 장점은? 폼 페이지의 `handleSubmit` 이 어떻게 짧아졌나?
37. 목록 페이지에서 `students.length === 0` 대신 `loaded` 로 판단하는 이유는?
38. `AppMessage` 의 `min-height: 24px` 은 무엇을 막나?
39. 5부 → 6부에서 **0줄 수정**으로 넘어온 파일 셋은? 이것이 말해 주는 것은?
40. (실물) Zustand 단계 폼 페이지가 `StudentForm.jsx` 를 import 하면서 생긴 차이는 무엇이고, 어떻게 고치나?
