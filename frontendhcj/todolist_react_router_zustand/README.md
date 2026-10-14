# todolist_react_router_zustand — store 만 떼어서 보는 예제

`todolist_react_router` 에 **Zustand 만** 얹은 것입니다.
7부(`student_react_router_axios_zustand`)로 가기 전에, store 가 하는 일만 따로 보기 위한 자리입니다.

## 실행

```bash
npm install
npm run dev       # http://localhost:5173
```

## 먼저 앞 예제의 문제를 확인하세요

`todolist_react_router` 를 열고 이렇게 해 보세요.

1. 할 일을 하나 추가하고, 아무 줄이나 눌러 체크한다
2. 머리말의 **기초 컴포넌트** 를 누른다
3. 다시 **할 일 목록** 으로 돌아온다

**추가한 것도 체크한 것도 전부 사라집니다.** 처음 세 개로 돌아가 있습니다.

목록이 `TodoPage` 안의 `useState` 에 있었기 때문입니다. 페이지가 화면에서 내려가면(언마운트)
`useState` 도 함께 사라집니다. 이 예제는 그것을 고칩니다.

## 무엇이 달라졌나

| | 앞 예제 | 이 예제 |
|---|---|---|
| 할 일 목록이 있는 곳 | `TodoPage` 의 `useState` | `store/todoStore.js` |
| 페이지를 떠났다 오면 | 사라진다 | 그대로 있다 |
| 머리말에 개수 표시 | 할 수 없다 | `TodoCount` 가 직접 꺼내 쓴다 |
| `handleToggle` · `handleRemove` | 컴포넌트에서 만든다 | store 가 갖고 있다 |
| `useCallback` | 두 개 필요했다 | 필요 없다 |

## store 는 왜 트리 밖에 있어야 하나

머리말의 개수 표시가 그 이유를 가장 잘 보여 줍니다.

```
App
 ├── header → TodoCount     ← 여기서 목록을 알고 싶다
 └── Routes
      └── TodoPage          ← 목록은 여기에 있었다
```

둘은 부모·자식 사이가 아닙니다. props 로는 값을 건넬 길이 **아예 없습니다.**
props 로 하려면 목록을 `App` 까지 끌어올려야 하는데, 그러면 `App` 이 할 일 목록을 알아야 할
이유가 없는데도 떠안게 됩니다.

store 는 컴포넌트 트리 밖에 있어서 `TodoCount` 와 `TodoPage` 가 각자 직접 꺼내 씁니다.

## store 읽고 쓰기

**읽을 때는 선택자(selector)를 씁니다.**

```jsx
const todos = useTodoStore((s) => s.todos);
```

괄호 안의 함수가 선택자입니다. "이 store 에서 `todos` 만 보겠다" 는 뜻이고,
`todos` 가 바뀔 때만 그 컴포넌트가 다시 그려집니다.
store 를 통째로 꺼내면 아무 값이나 바뀔 때마다 다시 그려지므로 하나씩 꺼내는 편이 낫습니다.

**쓸 때는 store 안의 함수를 부릅니다.**

```jsx
const addTodo = useTodoStore((s) => s.addTodo);
addTodo("장보기");
```

## useCallback 이 필요 없어진 까닭

앞 예제의 `TodoPage` 에는 `useCallback` 이 두 개 있었습니다.
`TodoItem` 이 `memo` 로 감싸여 있어서, 매번 새로 만들어지는 함수를 내려 주면
memo 가 듣지 않기 때문이었습니다.

store 의 함수는 `create()` 안에서 **딱 한 번** 만들어지고 다시 만들어지지 않습니다.
그래서 그대로 내려 주어도 memo 가 "달라졌다" 고 하지 않습니다.

입력칸에 글자를 두 번 쳐 보고 React DevTools 의 Profiler 로 확인해 보세요.
`TodoItem` 은 한 번도 다시 그려지지 않습니다.

## 무엇을 store 에 넣고 무엇을 안 넣나

기준은 하나입니다. **여러 곳이 함께 보는 값인가.**

| 값 | 어디에 | 왜 |
|---|---|---|
| `todos` · `nextId` | store | `TodoPage` 와 `TodoCount` 가 함께 본다 |
| 입력칸의 글자(`todo`) | `TodoPage` 의 `useState` | 그 페이지에서만 쓴다 |
| 끝낸 개수 | 어디에도 저장하지 않는다 | `todos` 에서 세면 된다 |

마지막 줄이 중요합니다. 끝낸 개수를 따로 저장해 두면 `todos` 와 어긋날 수 있습니다.
**계산해서 알 수 있는 값은 state 로 두지 않습니다.**

## 파일 구조

```
todolist_react_router_zustand/
└─ src/
   ├─ store/
   │  └─ todoStore.js        ★ 새 파일 — 값과 그 값을 바꾸는 함수
   ├─ components/
   │  └─ TodoCount.jsx       ★ 새 파일 — 머리말의 개수 표시
   ├─ pages/
   │  ├─ TodoPage.jsx        store 에 이었다 (useState 둘이 사라졌다)
   │  └─ BasicPage.jsx       그대로
   ├─ App.jsx                머리말에 TodoCount 를 놓았다
   └─ (나머지 그대로)
```

## 다음 단계

7부(`student_react_router_axios_zustand`)의 `studentStore.js` 와 견주어 보세요. 구조가 같습니다.

| 이 예제 | 7부 |
|---|---|
| `todos` | `students` |
| `addTodo` · `toggleTodo` · `removeTodo` | `saveStudent` · `removeStudent` |
| `TodoCount` (머리말의 개수) | `AppMessage` (머리말의 메시지) |
| — | `loading` · `listError` · `loaded` (서버를 부르니까 생긴다) |
| — | `get()` — store 안의 함수가 다른 함수를 부를 때 |

7부에서 더해지는 것은 **서버 통신** 하나뿐입니다.
store 를 만들고 선택자로 꺼내 쓰는 방식은 여기서 본 그대로입니다.
