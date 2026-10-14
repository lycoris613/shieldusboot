/* ---------------------------------------------------------
   학생 목록 페이지 — 주소 "/"
   5부 App.jsx 가 하던 일 중 "목록 불러오기" 와 "삭제" 가
   여기로 왔습니다.

   등록·수정 폼은 다른 페이지로 떨어져 나갔으므로
   이 페이지는 form · editingId 같은 값을 갖지 않습니다.
   --------------------------------------------------------- */

import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

import { fetchStudents, deleteStudent } from "../api/studentApi.js";
import StudentTable from "../components/StudentTable.jsx";
import MessageBox from "../components/MessageBox.jsx";

// 성공 메시지가 저절로 사라지기까지의 시간(ms)
const MESSAGE_TIMEOUT = 3000;

function StudentListPage() {
    /* useLocation 은 지금 주소에 딸린 정보를 돌려준다.
       등록·수정 페이지가 navigate("/", { state: { message } }) 로
       실어 보낸 문구가 location.state.message 에 들어 있다. */
    const location = useLocation();

    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [listError, setListError] = useState(null);

    /* useState 에 넘긴 값은 첫 렌더에서 한 번만 쓰인다.
       그래서 "옮겨 오면서 받은 메시지" 를 처음 값으로 두면 딱 맞는다.
       useEffect 로 옮겨 담을 필요가 없다. */
    const [message, setMessage] = useState(
        location.state?.message ? 
        { text: location.state.message, type: "success" } : null
    );

    async function loadStudents() {
        setLoading(true);
        setListError(null);

        try {
            const data = await fetchStudents();
            setStudents(data);
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
            setListError("오류: 데이터를 불러올 수 없습니다.");
        } finally {
            setLoading(false);
        }
    }

    /* 이 페이지가 화면에 붙을 때 한 번 목록을 불러온다.
       5부에서는 등록에 성공한 뒤 loadStudents() 를 직접 다시 불렀지만,
       이제는 폼 페이지에서 "/" 로 옮겨 오면 이 페이지가 새로 붙으므로
       저절로 다시 불러옵니다. */
    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect -- 처음 한 번 목록을 불러오는 것은 의도된 동작입니다
        loadStudents();
    }, []);

    // 성공 메시지는 3초 뒤에 저절로 사라진다.
    useEffect(() => {
        if (!message) {
            return;
        }
        if (message.type !== "success") {
            return;
        }

        const timer = setTimeout(() => setMessage(null), MESSAGE_TIMEOUT);
        return () => clearTimeout(timer);
    }, [message]);

    async function handleDelete(studentId) {
        if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) {
            return;
        }

        try {
            await deleteStudent(studentId);
            setMessage({ text: "학생이 성공적으로 삭제되었습니다.", type: "success" });
            await loadStudents();
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    return (
        <div className="page">
            <MessageBox message={message} />

            <StudentTable
                students={students}
                loading={loading}
                error={listError}
                onDelete={handleDelete}
            />
        </div>
    );
}

export default StudentListPage;
