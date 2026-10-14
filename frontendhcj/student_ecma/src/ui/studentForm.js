/* ---------------------------------------------------------
   폼 다루기 — 값 모으기 / 채우기 / 모드 전환
   3부 form.js 에서 폼과 관련된 코드는 제출 핸들러, editStudent,
   resetForm 세 군데에 흩어져 있었습니다. 여기로 모았습니다.

   폼 구조가 바뀌면 이 파일만 고치면 됩니다.
   --------------------------------------------------------- */

// export 를 붙이면 다른 파일에서 import 로 가져다 쓸 수 있다.
// 이 둘은 main.js 가 이벤트를 걸어야 하므로 밖으로 내보낸다.
export const studentForm = document.getElementById("studentForm");
export const cancelButton = document.getElementById("cancelButton");

// 이 둘은 이 파일 안에서만 쓰므로 export 하지 않는다.
const submitButton = studentForm.querySelector('button[type="submit"]');
// closest 는 자기 자신부터 부모 쪽으로 올라가며 맞는 요소를 찾는다.
// 폼을 감싸고 있는 <div class="form-container"> 를 찾아 둔다.
const formContainer = studentForm.closest(".form-container");

// 폼에 입력된 값을 서버가 받는 구조로 모은다.
export function collectStudentData() {
    // FormData 는 폼 안의 입력칸을 name 속성으로 꺼내 쓸 수 있게 모아 준다.
    // id 가 아니라 name 이 열쇠다.
    const formData = new FormData(studentForm);

    // 서버는 학생 기본 정보와 상세 정보를 나눠서 받는다.
    return {
        name: formData.get("name").trim(),
        studentNumber: formData.get("studentNumber").trim(),
        detailRequest: {
            address: formData.get("address").trim() || null,
            phoneNumber: formData.get("phoneNumber").trim(),
            // 여기서만 ?? 가 아니라 || 를 쓴다.
            // 아무것도 입력하지 않으면 빈 문자열("")이 오는데,
            // ?? 는 빈 문자열을 통과시켜 서버로 "" 이 나가 버린다.
            email: formData.get("email").trim(),
            dateOfBirth: formData.get("dateOfBirth") || null,
        },
    };
}

// 서버에서 받은 학생 정보로 폼을 채운다. 수정 버튼을 눌렀을 때 쓴다.
export function fillForm(student) {
    //Destructuring Assignment  비구조화 할당
    const { name, studentNumber, detail } = student;

    studentForm.name.value = name;
    studentForm.studentNumber.value = studentNumber;

    // 상세 정보를 등록하지 않은 학생은 detail 이 없다.
    //   detail?.address  detail 이 없으면 undefined 를 돌려주고 멈춘다
    //   ?? ""            그 undefined 를 빈 문자열로 바꾼다
    // 3부에서는 if (student.detail) { ... } 로 감싸야 했다.
    studentForm.address.value = detail?.address ?? "";
    studentForm.phoneNumber.value = detail?.phoneNumber ?? "";
    studentForm.email.value = detail?.email ?? "";
    studentForm.dateOfBirth.value = detail?.dateOfBirth ?? "";
}

// 등록 모드와 수정 모드를 전환한다. 인자를 생략하면 등록 모드다.
export function setEditMode(isEditing = false) {
    // 조건 ? 참일 때 값 : 거짓일 때 값  (삼항 연산자)
    submitButton.textContent = isEditing ? "학생 수정" : "학생 등록";
    cancelButton.style.display = isEditing ? "inline-block" : "none";

    // classList.toggle 의 두 번째 인자는 "뒤집기"가 아니라 "강제"다.
    //   true  면 무조건 넣고, false 면 무조건 뺀다.
    // 두 번째 인자가 없으면 부를 때마다 넣었다 뺐다 하므로,
    // setEditMode(true) 가 두 번 불리면 강조 표시가 사라져 버린다.
    // 이 클래스는 style.css 에서 폼 왼쪽에 녹색 띠를 그린다.
    formContainer.classList.toggle("editing", isEditing);
}

// 폼을 비우고 등록 모드로 되돌린다.
export function resetForm() {
    studentForm.reset();         // 입력칸을 모두 비운다
    setEditMode(false);
}

// 수정 버튼을 눌렀을 때 폼이 보이도록 부드럽게 스크롤한다.
export function scrollToForm() {
    studentForm.scrollIntoView({ behavior: "smooth" });
}