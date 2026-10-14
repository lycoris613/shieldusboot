import "./Form.css";
 
const Form = ({ myTodo, myChange, myCreate, myEnter }) => {
    return (
        <div className="form">
            <input
                value={myTodo}
                onChange={myChange}
                onKeyDown={myEnter}
            />
            <div className="create-button" onClick={myCreate}>
                추가
            </div>
        </div>
    );
};
 
export default Form;
