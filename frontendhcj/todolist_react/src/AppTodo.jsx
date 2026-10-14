import { useState } from 'react';
import './App.css'
import Form from './components/Form'
import TodoItemList from './components/TodoItemList'
import TodoListTemplate from './components/TodoListTemplate'

const initialTodos = new Array(500).fill(0).map(
    (item, idx) => ({ id: idx, text: `일정 ${idx}`, checked: true })
);

function App() {
  // 입력칸에 지금 들어 있는 글자
  const [todo, setTodo] = useState("");

  // 할 일 목록. 처음에 세 개를 넣어 둔다.
  // const [todos, setTodos] = useState([
  //   { id: 0, text: "리액트 소개", checked: false },
  //   { id: 1, text: "리액트 구조", checked: true },
  //   { id: 2, text: "리액트 사용", checked: false },
  // ]);
  const [todos, setTodos] = useState(initialTodos);
  
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
