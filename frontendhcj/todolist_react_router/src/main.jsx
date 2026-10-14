/* ---------------------------------------------------------
   main.jsx — 앱을 화면에 붙이는 곳

   라우터를 쓰려면 앱 전체를 <BrowserRouter> 로 한 번 감싸야 합니다.
   이 감싸개가 "지금 주소가 무엇인가" 를 들고 있고,
   안쪽의 <Routes> 와 <Link> 들이 그것을 꺼내 씁니다.

   감싸는 일은 여기서 딱 한 번만 합니다.
   --------------------------------------------------------- */

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";

import "./index.css";
import App from "./App.jsx";

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <BrowserRouter>
            <App />
        </BrowserRouter>
    </StrictMode>
);
