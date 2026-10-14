/* ---------------------------------------------------------
   BasicPage — 주소 "/basic" 일 때 보이는 페이지

   같은 일을 하는 컴포넌트 둘을 한 화면에 나란히 놓습니다.

     MyComponent      클래스 컴포넌트 — state, this.setState, 메서드
     MyComponentFunc  함수 컴포넌트   — useState, useRef

   둘을 동시에 만져 보면 무엇이 같고 무엇이 다른지 금방 보입니다.
   --------------------------------------------------------- */

import MyComponent from "../components/MyComponent.jsx";
import MyComponentFunc from "../components/MyComponentFunc.jsx";

function BasicPage() {
    /* <> </> 는 프래그먼트다. 태그 하나로 묶어 돌려주어야 하는데
       감쌀 만한 마땅한 이름이 없을 때 쓰는 빈 껍데기다.
       화면에는 아무것도 그려지지 않는다.

       바깥틀은 App.jsx 의 <main className="page"> 가 이미 맡고 있으므로
       여기서 div 를 한 겹 더 두면 스타일 없는 껍데기만 늘어난다. */
    return (
        <>
            <p className="basic-hint">
                두 컴포넌트는 화면도 기능도 같습니다. 적는 방법만 다릅니다.
                양쪽의 입력칸에 글자를 쳐 보고, 증가 버튼도 눌러 보세요.
            </p>

            {/* 두 컴포넌트가 받는 props 는 이름과 나이로 같다.
                같은 값을 주어야 견주기 좋다. */}
            <div className="basic-grid">
                <section className="basic-card">
                    <MyComponent name="홍길동" age={20} />
                </section>

                <section className="basic-card">
                    {/* 함수 쪽은 children 도 받는다.
                        여는 태그와 닫는 태그 사이에 적은 것이 children 으로 들어간다. */}
                    <MyComponentFunc name="홍길동" age={20}>
                        <p>이 문장은 children 으로 들어온 것입니다.</p>
                    </MyComponentFunc>
                </section>
            </div>
        </>
    );
}

export default BasicPage;
