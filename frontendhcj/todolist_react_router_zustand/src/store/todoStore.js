/* ---------------------------------------------------------
   todoStore — 여러 곳이 함께 보는 값

   앞 예제(todolist_react_router)에는 문제가 하나 있었습니다.
   할 일을 추가한 뒤 "기초 컴포넌트" 로 갔다가 돌아오면
   추가한 것도, 체크한 것도 전부 사라졌습니다.

   목록이 TodoPage 안의 useState 에 있었기 때문입니다.
   페이지가 화면에서 내려가면(언마운트) useState 도 함께 사라집니다.

   store 는 컴포넌트 트리 밖에 있습니다. 페이지가 바뀌어도 살아 있고,
   어느 컴포넌트든 직접 꺼내 쓸 수 있습니다.
   --------------------------------------------------------- */

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
