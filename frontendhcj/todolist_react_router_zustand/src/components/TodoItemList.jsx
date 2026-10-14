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
 
// 받은 props 가 그대로면 다시 그리지 않는다. 비교 함수는 필요 없다.
export default memo(TodoItemList);

