import "./TodoItem.css";
import { memo } from "react";
 
const TodoItem = ({ text, checked, id, onToggle, onRemove }) => {
    return (
        <div className="todo-item" onClick={() => onToggle(id)}>
            <div
                className="remove"
                onClick={(e) => {
                    e.stopPropagation(); // onToggle 이 실행되지 않도록 함
                    onRemove(id);
                }}
            >
                &times;
            </div>
            <div className={`todo-text ${checked ? "checked" : ""}`}>
                <div>{text}</div>
            </div>
            {checked && (<div className="check-mark">✓</div>)}
        </div>
    );
};
 
/* 받은 props 가 그대로면 다시 그리지 않는다.
   비교 함수를 주지 않았으므로 props 다섯 개를 하나씩 === 로 견준다.
   onToggle 과 onRemove 가 useCallback 으로 고정돼 있어 이것만으로 충분하다. */
export default memo(TodoItem);
