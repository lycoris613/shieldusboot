/* ---------------------------------------------------------
   학생 목록 표 그리기
   3부 form.js 의 renderStudentTable 이 여기로 왔습니다.
   달라진 점 두 가지입니다.

   (1) onclick="deleteStudent(3)" 을 쓰지 않습니다.
       onclick 속성에 적은 이름은 브라우저가 전역(window)에서 찾는데,
       모듈 안의 함수는 전역에 없어 "is not defined" 가 납니다.
       대신 버튼에 data-action, data-id 를 심어 두고
       main.js 가 tbody 한 곳에서 클릭을 받습니다(이벤트 위임).

   (2) 값을 innerHTML 이 아니라 textContent 로 넣습니다.
       innerHTML 은 문자열을 HTML 로 해석하므로, 이름에 <img> 같은
       태그가 섞여 있으면 그대로 실행됩니다. textContent 는
       무엇이 들어와도 글자로만 보여 줍니다.
   --------------------------------------------------------- */

// main.js 가 여기에 클릭 이벤트를 건다. 그래서 export 한다.
export const studentTableBody = document.getElementById("studentTableBody");

// 표의 열 개수. 아래 colSpan 에 쓴다.
const COLUMN_COUNT = 7;

/* 표 전체를 가로지르는 한 줄짜리 행을 만든다.
   "등록된 학생이 없습니다" 안내와 오류 안내가 함께 쓴다.
   className 은 기본 매개변수라, 안내용으로 부를 때는 생략해도 된다. */
function createMessageRow(message, className = "empty-row") {
    const row = document.createElement("tr");
    const cell = document.createElement("td");

    cell.colSpan = COLUMN_COUNT;   // 한 칸이 7칸 너비를 차지하게 한다
    cell.className = className;
    cell.textContent = message;
    row.appendChild(cell);         // <tr> 안에 <td> 를 넣는다

    return row;
}

// <td>값</td> 하나를 만들어 행에 붙인다. 아래에서 여섯 번 부른다.
function addCell(row, value) {
    const cell = document.createElement("td");

    cell.textContent = value;      // 태그가 섞여 있어도 글자로만 보인다
    row.appendChild(cell);
}

/* 수정 또는 삭제 버튼 하나를 만든다.
   버튼에 "무슨 동작인지"와 "어느 학생인지"를 새겨 두면,
   나중에 클릭을 받은 main.js 가 그것만 보고 판단할 수 있다. */
function createActionButton(action, label, className, id) {
    const button = document.createElement("button");

    // type 을 안 주면 폼 안의 버튼은 submit 으로 동작한다.
    button.type = "button";
    button.className = className;
    button.textContent = label;

    // dataset.action 에 넣으면 HTML 에는 data-action="edit" 로 나온다.
    // data- 로 시작하는 속성은 우리가 마음대로 붙여 쓸 수 있다.
    button.dataset.action = action;
    button.dataset.id = id;

    return button;
}

// 학생 한 명을 표의 한 행(<tr>)으로 만든다.
function createStudentRow(student) {
    // 필요한 값 네 개만 이름 그대로 꺼낸다(구조 분해).
    const { id, name, studentNumber, detail } = student;
    const row = document.createElement("tr");

    addCell(row, name);
    addCell(row, studentNumber);

    // 상세 정보가 없는 학생은 detail 이 null 이다.
    //   detail?.address  detail 이 없으면 거기서 멈추고 undefined
    //   ?? "-"           그 undefined 를 "-" 로 바꾼다
    // 3부의 student.detail ? student.detail.address : "-" 와 같은 일이다.
    addCell(row, detail?.address ?? "-");
    addCell(row, detail?.phoneNumber ?? "-");
    addCell(row, detail?.email ?? "-");
    addCell(row, detail?.dateOfBirth ?? "-");

    // 마지막 칸에는 버튼 두 개를 넣는다.
    const actionCell = document.createElement("td");

    actionCell.appendChild(createActionButton("edit", "수정", "edit-btn", id));
    actionCell.appendChild(createActionButton("delete", "삭제", "delete-btn", id));

    row.appendChild(actionCell);
    return row;
}

// 학생 목록을 표에 그린다. 인자를 생략하면 빈 배열로 본다.
export function renderStudentTable(students = []) {
    
    // 먼저 비우지 않으면 목록을 새로고침할 때마다 같은 학생이 쌓인다.
    studentTableBody.innerHTML = "";

    if (students.length === 0) {
        studentTableBody.appendChild(createMessageRow("등록된 학생이 없습니다."));
        return;
    }

    // forEach 는 배열의 값을 하나씩 꺼내 함수에 넘겨 준다.
    students.forEach((student) => {
        studentTableBody.appendChild(createStudentRow(student));
    });
}

// 목록을 불러오지 못했을 때 표 자리에 오류를 표시한다.
export function renderTableError(message = "오류: 데이터를 불러올 수 없습니다.") {
    studentTableBody.innerHTML = "";
    studentTableBody.appendChild(createMessageRow(message, "error-row"));
}