/* ---------------------------------------------------------
   TodoPage — 주소 "/" 일 때 보이는 페이지

   앞 예제와 견주어 보면 사라진 것이 많습니다.

     useState 세 개 중 둘이 사라졌다   todos · nextId 는 store 로 갔다
     handleToggle / handleRemove 가 사라졌다
     useCallback 두 개가 사라졌다

   store 의 함수는 create() 안에서 딱 한 번 만들어지고 다시 만들어지지
   않습니다. 그래서 memo 로 감싼 TodoItem 에 그대로 내려 주어도
   "달라졌다" 가 나오지 않습니다. useCallback 이 필요 없어진 까닭입니다.
   --------------------------------------------------------- */

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
