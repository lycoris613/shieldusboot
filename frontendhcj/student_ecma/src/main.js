import './style.css'
import {
    fetchStudents,
    fetchStudent,
    createStudent,
    updateStudent,
    deleteStudent,
} from './api/studentApi';

import {
    studentForm, collectStudentData, cancelButton,
    fillForm, setEditMode, resetForm, scrollToForm,
} from "./ui/studentForm.js";

import { validateStudent } from "./lib/validation.js";
import { showError, showSuccess, clearMessages, setLoading } from "./ui/message.js";
import { renderStudentTable, renderTableError, studentTableBody, } from "./ui/studentTable.js";
import { APP_MODE } from "./config.js";

// 현재 수정 중인 학생 ID
let editingStudentId = null;

/* ── 모드 표시 ──────────────────────────────────────────── */
 
// 제목 옆에 TEST 또는 PROD 를 적는다.
// 값은 .env 파일에서 오고, Vite 가 빌드할 때 넣어 준다.
const appModeBadge = document.getElementById("appMode");
appModeBadge.textContent = APP_MODE;
 
// 모드에 따라 색을 다르게 한다. classList.add 로 클래스를 하나 더 붙인다.
if (APP_MODE === "PROD") {
    appModeBadge.classList.add("prod");
} else {
    appModeBadge.classList.add("test");
}


// 폼 제출 이벤트 핸들러
// 핸들러 안에서 await 을 쓰려면 함수에 async 를 붙여야 한다.
studentForm.addEventListener("submit", async (event) => {
    event.preventDefault();          // 폼 제출로 페이지가 새로고침되는 것을 막는다
    clearMessages();
 
    const studentData = collectStudentData();
 
    // validateStudent 는 문제가 있으면 메시지를, 없으면 null 을 돌려준다.
    // 문제가 있으면 여기서 끝낸다(early return).
    const errorMessage = validateStudent(studentData);
    if (errorMessage) {
        showError(errorMessage);
        return;
    }
 
    try {
        // editingStudentId 에 값이 있으면 수정, 없으면 등록이다.
        if (editingStudentId) {
            await updateStudent(editingStudentId, studentData);
            showSuccess("학생 정보가 성공적으로 수정되었습니다.");
        } else {
            await createStudent(studentData);
            showSuccess("학생이 성공적으로 등록되었습니다.");
        }
 
        editingStudentId = null;
        resetForm();
        await loadStudents();         // 목록 새로고침
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);     // 서버가 보낸 실제 메시지
    }
});


cancelButton.addEventListener("click", () => {
    editingStudentId = null;
    resetForm();
    clearMessages();
});


async function loadStudents() {
    setLoading(true);

    // try 안에서 오류가 나면 곧바로 catch 로 넘어간다.
    // finally 는 성공하든 실패하든 마지막에 반드시 실행된다.
    try {
        // await 은 서버 응답이 올 때까지 기다린다.
        // 3부의 fetch().then().then() 사슬이 두 줄이 되었다.
        const students = await fetchStudents();
        renderStudentTable(students);
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
        renderTableError();
    } finally {
        // 여기에 두면 성공 경로와 실패 경로에 두 번 적지 않아도 된다.
        setLoading(false);
    }
}

/* 버튼마다 이벤트를 걸지 않는 이유는, 표를 다시 그릴 때마다
   버튼이 새로 만들어져 매번 다시 걸어야 하기 때문이다.
   사라지지 않는 부모인 tbody 에 한 번만 걸어 두면
   나중에 생기는 행의 버튼도 그대로 동작한다(이벤트 위임). */
studentTableBody.addEventListener("click", async (event) => {
    // tbody 안에서 일어난 클릭이 전부 여기로 들어온다.
    // 이름 칸을 눌렀는지 버튼을 눌렀는지 먼저 가려내야 한다.
    //
    //   event.target  이벤트를 건 tbody 가 아니라 실제로 눌린 가장 안쪽 요소
    //   closest(...)  자기 자신부터 부모 쪽으로 올라가며 조건에 맞는 첫 요소를 찾는다
    //                 끝까지 없으면 null 을 돌려준다
    //<button type="button" class="edit-btn" data-action="edit" data-id="1">수정</button>
    const button = event.target.closest("button[data-action]");
    if (!button) return;             // 버튼이 아닌 곳을 눌렀다
 
    // data-action="edit" 은 button.dataset.action 으로 읽는다.
    const { action, id } = button.dataset;
 
    // dataset 값은 언제나 문자열이다. data-id="3" 이면 "3" 이 온다.
    // 그래서 Number() 로 숫자로 바꿔서 넘긴다.
    if (action === "edit") {
        await startEdit(Number(id));
    } else if (action === "delete") {
        await removeStudent(Number(id));
    }
});

// 수정할 학생 정보를 불러와 폼에 채우고 수정 모드로 바꾼다.
async function startEdit(studentId) {
    clearMessages();
 
    try {
        const student = await fetchStudent(studentId);
 
        fillForm(student);
        editingStudentId = studentId;   // 이제 제출하면 등록이 아니라 수정이 된다
        setEditMode(true);
        scrollToForm();
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
    }
}
 
// 확인을 받은 뒤 학생을 삭제한다.
async function removeStudent(studentId) {
    if (!confirm("정말로 이 학생을 삭제하시겠습니까?")) {
        return;
    }
 
    try {
        await deleteStudent(studentId);
        showSuccess("학생이 성공적으로 삭제되었습니다.");
 
        // 수정 중이던 학생을 삭제했다면 폼도 등록 모드로 되돌린다.
        // 이걸 빠뜨리면 없는 학생을 수정하려다 404 가 난다.
        if (editingStudentId === studentId) {
            editingStudentId = null;
            resetForm();
        }
 
        await loadStudents();
    } catch (error) {
        console.error("Error:", error);
        showError(error.message);
    }
}


// Student load 함수 호출
loadStudents();
