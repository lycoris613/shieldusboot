import { useCallback, useState } from "react";
 
// Set 은 같은 값을 두 번 넣어도 하나로 친다.
// 컴포넌트 바깥에 두었으므로 다시 그려도 비워지지 않는다.
const set = new Set();
 
function UseCallbackApp() {
    const [count, setCount] = useState(0);
 
    const increase = () => setCount(count + 1);
 
    // (A) 그냥 적은 함수
    //const printLog = () => console.log("re-execute");
 
    // (B) useCallback 으로 감싼 함수 — (A) 를 지우고 아래 두 줄을 살린다.
    //     맨 위 import 에 useCallback 도 함께 더해야 한다.
    const printLog = useCallback(() => console.log("re-execute"), []);
 
    set.add(printLog);                  // 지금의 printLog 를 넣어 본다
    console.log("set.size :", set.size);
    console.log("set content :", set);
 
    return (
        <div>
            <p>{count}</p>
            <button onClick={increase}>Add</button>
            <button onClick={printLog}>Print</button>
        </div>
    );
}
 
export default UseCallbackApp;
