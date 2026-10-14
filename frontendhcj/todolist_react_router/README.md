# todolist_react_router — 라우터만 떼어서 보는 예제

`todolist_react` 에 **React Router 만** 얹은 것입니다. 서버도 axios 도 상태관리도 없습니다.
6부(`student_react_router_axios`)로 가기 전에, 라우터가 하는 일만 따로 보기 위한 자리입니다.

## 실행

```bash
npm install       # 처음 한 번
npm run dev       # http://localhost:5173
```

서버(Spring Boot)가 필요 없습니다. 브라우저만 있으면 됩니다.

## 주소 두 개

| 주소 | 보이는 것 | 파일 |
|---|---|---|
| `/` | 할 일 목록 (TodoList) | `pages/TodoPage.jsx` |
| `/basic` | 클래스형·함수형 컴포넌트 둘을 나란히 | `pages/BasicPage.jsx` |
| 그 밖 | "없는 주소입니다." | `App.jsx` 안에 한 줄 |

## 라우터가 하는 일은 두 가지뿐입니다

**(1) 주소를 보고 어느 페이지를 그릴지 고른다** — `App.jsx`

```jsx
<Routes>
    <Route path="/" element={<TodoPage />} />
    <Route path="/basic" element={<BasicPage />} />
    <Route path="*" element={<p>없는 주소입니다.</p>} />
</Routes>
```

**(2) 페이지를 새로 내려받지 않고 주소만 바꾼다** — `App.jsx`

```jsx
<NavLink to="/" end className={navClass}>할 일 목록</NavLink>
<NavLink to="/basic" className={navClass}>기초 컴포넌트</NavLink>
```

그리고 이 둘이 동작하려면 앱 전체를 한 번 감싸야 합니다 — `main.jsx`

```jsx
<BrowserRouter>
    <App />
</BrowserRouter>
```

**이 세 곳이 전부입니다.** 페이지 파일(`pages/*.jsx`)에는 라우터와 얽힌 코드가 한 줄도 없습니다.
페이지는 그냥 컴포넌트이고, 누가 언제 그릴지만 `App.jsx` 가 정합니다.

## 파일 구조

```
todolist_react_router/
├─ index.html
└─ src/
   ├─ main.jsx              BrowserRouter 로 감싼다        ← 라우터
   ├─ App.jsx               머리말 + Routes                ← 라우터
   ├─ App.css               머리말·링크·페이지 바깥틀
   ├─ index.css
   ├─ pages/
   │  ├─ TodoPage.jsx       todolist_react 의 AppTodo.jsx
   │  └─ BasicPage.jsx      MyComponent + MyComponentFunc
   └─ components/           todolist_react 에서 그대로 복사
      ├─ TodoListTemplate.jsx · Form.jsx · TodoItemList.jsx · TodoItem.jsx
      └─ MyComponent.jsx · MyComponentFunc.jsx
```

`components/` 는 `todolist_react` 의 것을 **한 글자도 고치지 않고** 옮겨 왔습니다.
`TodoPage.jsx` 만 세 군데가 다릅니다 — 이름(`App` → `TodoPage`), import 경로(한 단계 내려왔으므로
`./components` → `../components`), 그리고 `App.css` 를 `App.jsx` 가 가져간 것.

## 눈여겨볼 것 두 가지

**`end` 가 왜 필요한가** — `App.jsx` 의 첫 `NavLink`

```jsx
<NavLink to="/" end className={navClass}>
```

`end` 가 없으면 `/basic` 에 있을 때도 `/` 링크가 파랗게 표시됩니다.
`/` 가 모든 주소의 앞부분이라 "여기서 시작하는 주소" 로 쳐 주기 때문입니다.
`end` 는 "주소가 정확히 `/` 일 때만" 이라는 뜻입니다.

**`.nav-link` 에 `display: inline-block`** — `App.css`

`NavLink` 는 `<a>` 태그로 그려지는데, `a` 는 기본이 inline 이라 위아래 `padding` 이 제대로 먹지
않습니다. 버튼처럼 보이게 하려면 `inline-block` 으로 바꿔 줘야 합니다.

## 다음 단계

여기서 라우터가 익숙해지면 6부(`student_react_router_axios`)로 갑니다.
거기서는 같은 라우터 위에 이런 것들이 더해집니다.

- `/edit/:id` 처럼 주소에 값을 싣기 (`useParams`)
- 코드에서 주소를 바꾸기 (`useNavigate`)
- 옮겨 가면서 값을 실어 보내기 (`navigate(to, { state })`)
- axios 로 서버와 통신
