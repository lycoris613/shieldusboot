/* ---------------------------------------------------------
   MessageBox — 폼 아래 메시지 한 줄
   ui/message.js 의 showMessage 가 하던 일을 컴포넌트로 옮겼습니다.
   그릴 것이 없으면 null 을 돌려주는 것만으로 "숨기기"가 됩니다.
   --------------------------------------------------------- */

const COLORS = {
    error: "#f44336",
    success: "#4CAF50",
};

export default function MessageBox({ message }) {
    if (!message) return null;

    return (
        <span className="error-message" style={{ color: COLORS[message.type] ?? COLORS.error }}>
            {message.text}
        </span>
    );
}
