/* ---------------------------------------------------------
   TodoCount — 머리말에 "할 일 4 · 끝낸 것 2" 를 띄운다

   이 컴포넌트가 store 를 쓰는 진짜 이유를 보여 줍니다.

   머리말은 App.jsx 안, Routes 바깥에 있습니다. 할 일 목록은
   TodoPage 안에 있습니다. 둘은 부모·자식 사이가 아니므로
   props 로는 값을 건넬 길이 아예 없습니다.

     App
      ├── header → TodoCount     ← 여기서 목록을 알고 싶다
      └── Routes
           └── TodoPage          ← 목록은 여기에 있었다

   props 로 하려면 목록을 App 까지 끌어올려야 하는데, 그러면
   App 이 할 일 목록을 알아야 할 이유가 없는데도 떠안게 됩니다.
   store 는 트리 밖에 있어서 양쪽이 직접 꺼내 쓰면 됩니다.
   --------------------------------------------------------- */

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
