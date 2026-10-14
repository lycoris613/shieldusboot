import TodoItem from "./TodoItem";
import { memo } from "react";

const TodoItemList = ({ myTodos, myToggle, myRemove }) => {
    // 배열의 값 하나하나를 화면 조각으로 바꾼다
    const todoList = myTodos.map(
        ({ id, text, checked }) => (
            <TodoItem
                id={id}
                text={text}
                checked={checked}
                onToggle={myToggle}
                onRemove={myRemove}
                key={id}
            />
        )
    );
    
    return (
        <div>
            {todoList}
        </div>
    );
};
 
//export default TodoItemList;
// myTodos 가 바뀔 때만 다시 그린다
export default memo(
    TodoItemList,
    (prevProps, nextProps) => prevProps.myTodos === nextProps.myTodos
);

