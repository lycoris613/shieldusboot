/* ---------------------------------------------------------
   App.jsx — 주소와 페이지를 이어 주는 곳

   라우터가 하는 일은 두 가지뿐입니다.

     (1) 주소를 보고 어느 페이지를 그릴지 고른다   → <Routes> <Route>
     (2) 페이지를 새로 내려받지 않고 주소만 바꾼다 → <Link> <NavLink>

   이 파일에 그 둘이 다 들어 있습니다. 서버도 axios 도 없습니다.
   --------------------------------------------------------- */

import { NavLink, Route, Routes } from "react-router-dom";

import TodoPage from "./pages/TodoPage.jsx";
import BasicPage from "./pages/BasicPage.jsx";
import TodoCount from "./components/TodoCount.jsx";

import "./App.css";

/* NavLink 의 className 에는 문자열 대신 함수를 넘길 수 있다.
   React Router 가 isActive 를 넣어 부르므로
   "지금 보고 있는 쪽" 만 다른 모양으로 만들 수 있다.

   같은 모양을 두 번 적지 않으려고 밖으로 빼 두었다. */
function navClass({ isActive }) {
    return isActive ? "nav-link active" : "nav-link";
}

function App() {
    return (
        <>
            {/* 머리말은 Routes 바깥에 있다. 그래서 주소가 바뀌어도 그대로 남는다. */}
            <header className="app-header">
                {/* 제목과 개수를 한 덩어리로 묶는다.
                    머리말이 space-between 이라 자식이 둘이어야 배치가 유지된다. */}
                <div className="app-brand">
                    <h1 className="app-title">React 연습</h1>

                    {/* 할 일 목록은 TodoPage 안에 있는데 여기서 개수를 보여 준다.
                        둘은 부모·자식 사이가 아니라 props 로는 못 하는 일이다.
                        store 가 트리 밖에 있어서 가능하다. */}
                    <TodoCount />
                </div>

                <nav className="app-nav">
                    {/* to 는 옮겨 갈 주소다. a 태그처럼 보이지만
                        페이지를 새로 내려받지 않고 주소만 바꾼다.

                        end 는 "주소가 정확히 / 일 때만 active" 라는 뜻이다.
                        이것이 없으면 /basic 에 있을 때도 / 가 active 가 된다.
                        / 가 모든 주소의 앞부분이기 때문이다. */}
                    <NavLink to="/" end className={navClass}>
                        할 일 목록
                    </NavLink>

                    <NavLink to="/basic" className={navClass}>
                        기초 컴포넌트
                    </NavLink>
                </nav>
            </header>

            {/* Routes 안에서 주소와 맞는 Route 하나만 그려진다.
                위에서 아래로 훑어 맞는 것을 고르는 것이 아니라,
                React Router 가 가장 잘 맞는 하나를 골라 준다. */}
            <main className="page">
                <Routes>
                    <Route path="/" element={<TodoPage />} />
                    <Route path="/basic" element={<BasicPage />} />

                    {/* * 는 "위의 어느 것과도 맞지 않는 주소" 다.
                        이것이 없으면 /abcd 같은 주소에서 빈 화면이 나온다. */}
                    <Route path="*" element={<p>없는 주소입니다.</p>} />
                </Routes>
            </main>
        </>
    );
}

export default App;
