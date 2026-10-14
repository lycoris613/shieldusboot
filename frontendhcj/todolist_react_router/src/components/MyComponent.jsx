import { Component } from 'react';
import './MyComponent.css';

class MyComponent extends Component {
    //상태객체
    state = {
        value: 0,
        message: '',
        username: '',
        isValid: false,
    };
    
    //event handler 함수
    handleChange = (e) => {
        this.setState({
            //message: e.target.value
            //username: e.target.value
            [e.target.name]: e.target.value
        });
    };

    handleEnter = (e) => {
        if(e.keyCode === 13){
            this.setState({
                isValid: true,
                message: ''
            });
            //html dom 직접 접근
            this.myUsername.focus();
        }//if
    };


    //Component 메서드 재정의
    render() {
        const { name, age } = this.props; 
        const { value, message, username, isValid } = this.state;
        const { handleChange, handleEnter } = this;

        return (
            <div>
                <h2>클래스 형태의 컴포넌트</h2>
                <h3>Hello! {name} / {age}</h3>
                <p>상태변수 value = {value}</p>
                <button onClick={() => this.setState({
                    value: value + 1
                })}>증가</button> 
                
                <p>상태변수 message = {message}</p>
                <input name="message" value={message} onChange={handleChange}
                    onKeyDown={handleEnter}
                 />
                <br />
                <p>상태변수 username = {username}</p>
                <input name="username" value={username} onChange={handleChange} 
                    className={isValid ? 'success':'failure'}
                    ref={(ref) => this.myUsername = ref}
                />
            </div>
        );
    }//render
}

export default MyComponent;