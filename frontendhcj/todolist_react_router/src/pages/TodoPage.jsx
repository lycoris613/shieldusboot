/* ---------------------------------------------------------
   TodoPage — 주소 "/" 일 때 보이는 페이지
   todolist_react 의 AppTodo.jsx 를 그대로 옮겨 왔습니다.
   고친 것은 세 가지뿐입니다.

     이름        App        →  TodoPage
     import 경로 ./components →  ../components  (한 단계 내려왔다)
     App.css     App.jsx 가 가져갔다

   라우터와 얽힌 코드는 한 줄도 없습니다. 페이지는 그냥 컴포넌트입니다.
   --------------------------------------------------------- */

import { useCallback, useState } from 'react';
import Form from '../components/Form'
import TodoItemList from '../components/TodoItemList'
import TodoListTemplate from '../components/TodoListTemplate'

/* E-18 · E-20 에서 성능을 재 볼 때만 살리는 코드.
   아래 useState 자리의 주석도 함께 바꿔 끼운다.

const initialTodos = new Array(500).fill(0).map(
    (item, idx) => ({ id: idx, text: `일정 ${idx}`, checked: true })
);
*/

function TodoPage() {
  // 입력칸에 지금 들어 있는 글자
  const [todo, setTodo] = useState("");

  // 할 일 목록. 처음에 세 개를 넣어 둔다.
  const [todos, setTodos] = useState([
    { id: 0, text: "리액트 소개", checked: false },
    { id: 1, text: "리액트 구조", checked: true },
    { id: 2, text: "리액트 사용", checked: false },
  ]);
  // 성능을 재 볼 때: 위를 주석 처리하고 아래를 살린다(맨 위 initialTodos 도 함께).
  // const [todos, setTodos] = useState(initialTodos);
  
  // 다음에 만들 할 일의 번호. 0,1,2 를 이미 썼으므로 3부터.
  const [nextId, setNextId] = useState(3);

  const handleChange = (e) => {
    setTodo(e.target.value); // 입력칸의 다음 값
  };

  const handleCreate = () => {
    const newTodo = {
      id: nextId,
      text: todo,
      checked: false,
    };

    // 기존 배열을 펼치고 뒤에 하나를 더한 "새 배열" 을 넣는다
    setTodos([...todos, newTodo]);
    setTodo("");                // 입력칸 비우기
    setNextId(nextId + 1);      // 다음 번호 준비
  };

  const handleEnter = (e) => {
    // 눌려진 키가 Enter 이면 handleCreate 호출
    // if (e.keyCode === 13) {
    if (e.key === "Enter") {
      handleCreate();
    }
  };


  // const handleToggle = (id) => {
  //   setTodos(
  //     todos.map((todo) =>
  //       todo.id === id ? { ...todo, checked: !todo.checked } : todo
  //     )
  //   );
  // };
  // const handleRemove = (id) => {
  //   setTodos(
  //     todos.filter((todo) => todo.id !== id)
  //   );
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

export default TodoPage
