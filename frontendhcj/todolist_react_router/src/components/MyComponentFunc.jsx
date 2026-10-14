import { useRef, useState } from 'react';
import './MyComponent.css';

const MyComponentFunc = ({ name, age, children }) => {
    const [value, setValue] = useState(0);
    // const [message, setMessage] = useState("");
    // const [username, setUsername] = useState("");
    const [inputs, setInputs] = useState({
        message: '', username: ''
    });
    const [valid, setValid] = useState(false);

    //비구조화 할당
    const { message, username } = inputs;

    //useRef로 직접 접근할 엘리먼트의 이름 선언
    const myUsername = useRef(null);

    //event handler 함수
    const handleChange = (e) => {
        setInputs({
            ...inputs,
            [e.target.name]: e.target.value
        });
    }
    const handleEnter = (e) => {
        if(e.keyCode === 13){
            setValid(true);
            setInputs({
                ...inputs,
                message: ''
            });
            myUsername.current.focus();
        }
    }
    return (
        <div>
            <h2>함수 형태의 컴포넌트</h2>
            <h3>Hello! {name} / {age}</h3>
            {children}
            <p>상태변수 value = {value}</p>
            <button onClick={() => setValue(value + 1)}>증가</button>
            <p>상태변수 message = {message}</p>
            <input name="message" value={message} 
                onChange={handleChange} 
                onKeyDown={handleEnter}
            />
            <br />
            <p>상태변수 username = {username}</p>
            <input name="username" value={username} 
            onChange={handleChange} 
            className={valid ? 'success':'failure'}
            ref={myUsername}
            />
        </div>
    );
};

export default MyComponentFunc;