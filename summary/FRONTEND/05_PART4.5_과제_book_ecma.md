# PART 4.5. 과제 — 도서 관리 시스템 (book_ecma)

> 자료 : `연습문제_ECMAScript_실습_단계별_forBook.docx`
> 참고 : `05_ECMAScript_실습_단계별_forStudent.docx` (같은 번호의 실습을 펴 놓고 본다)

## 4.5-0. 이 과제는 무엇인가

수업에서는 **학생 관리 시스템**을 Vite 와 ES6 모듈로 다시 썼다. 이 과제는 **같은 일을 도서 관리 시스템으로 혼자** 해 보는 것이다.

**기능은 하나도 바꾸지 않는다.** 등록·조회·수정·삭제·**상세 보기**가 지금 그대로 동작하면 된다. 바뀌는 것은 **코드가 놓이는 자리**뿐이다.

⚠️ **수업 문서의 코드를 그대로 베끼면 학생 앱의 필드 이름(`name`, `studentNumber`)이 그대로 따라온다.** 도서는 입력칸이 **11개**이고, 서버로 보내는 모양도 이름이 다르다. 한 줄씩 무엇을 바꿔야 하는지 보면서 옮겨야 한다.

### 출발점 — 이미 가지고 있는 세 파일

| 파일 | 무엇이 들어 있나 | 크기 |
|---|---|---|
| `book/book-client-step10.html` | 화면 마크업 — 입력칸 11개, 표 7열 | 105줄 |
| `book/js/form10.js` | **전역 변수 5개와 함수 11개** | 305줄 |
| `book/css/form10.css` | CSS 규칙 27개 | 166줄 |

**이 과제는 `form10.js` 305줄을 역할별 모듈 여덟 개로 가르는 일이다.** CSS 는 자리만 옮기고 내용은 그대로 쓴다.

### form10.js 에 들어 있는 것

| 이름 | 하는 일 |
|---|---|
| `API_BASE_URL` | 서버 주소. **코드에 그대로 박혀 있다** |
| `editingBookId` | 수정 중인 도서 id. `null` 이면 등록 모드 |
| `bookForm` / `bookTableBody` / `submitButton` | 화면 요소 참조 |
| submit 핸들러 | 폼 값 수집 → 검사 → 등록 또는 수정 |
| `createBook(bookData)` | POST 로 등록 |
| `validateBook(book)` | 검사에 실패하면 **`alert` 을 띄우고 `false`** |
| `isValidUrl(string)` | 표지 이미지 주소가 올바른가 |
| `loadBooks()` | GET 으로 목록을 받아 표를 그린다 |
| `renderBookTable(books)` | `tbody` 를 비우고 다시 채운다 |
| `deleteBook(bookId)` | `confirm` 뒤 DELETE |
| `editBook(bookId)` | GET 한 뒤 폼을 채우고 수정 모드로 |
| `updateBook(bookId, bookData)` | PUT 으로 수정 |
| **`showBookDetail(bookId)`** | **GET 한 뒤 상세 정보를 `alert` 으로 — 학생 앱에는 없다** |
| `resetForm()` | 폼을 비우고 등록 모드로 |

### 만들 결과물 — book_ecma 폴더

```
book_ecma/
├── index.html            화면 마크업 (지금 html 에서 옮겨 온다)
├── package.json
├── .env.development      개발용 서버 주소
├── .env.production       배포용 서버 주소
└── src/
    ├── main.js           무엇을 할지 정한다 (조립)
    ├── style.css         지금 form10.css 를 그대로
    ├── config.js         서버 주소와 공통 헤더
    ├── api/
    │   └── bookApi.js    서버와 대화만 한다
    ├── lib/
    │   └── validation.js 값만 다룬다 (화면을 모른다)
    └── ui/
        ├── message.js    메시지와 로딩 표시
        ├── bookForm.js   폼 읽기 · 채우기 · 모드 전환
        ├── bookTable.js  표 그리기
        └── bookDetail.js 상세 정보 문자열 만들기
```

**파일 여덟 개를 새로 만든다. 나누는 기준은 하나다 — 화면을 만지는 파일(`ui/`)과 서버를 부르는 파일(`api/`)과 값만 다루는 파일(`lib/`)을 섞지 않는 것.**

---

## 4.5-1. 학생 앱과 다른 네 가지 — 여기서 대부분 걸린다

| | 학생 앱 (수업) | 도서 앱 (과제) |
|---|---|---|
| **1** | 표의 버튼이 수정·삭제 **둘** | **상세 보기가 하나 더 있어 셋** |
| **2** | 서버로 보내는 모양이 `detailRequest` | **`bookDetail`** — 이름이 다르다 |
| **3** | `index.html` 에 메시지·취소·로딩 자리가 **있다** | **step10 화면에는 없다. 직접 넣어야 한다** |
| **4** | 검사는 이름·학번·이메일 | **ISBN 형식과 표지 URL 검사가 더 있다** |

⚠️ **세 번째가 가장 자주 걸린다.** `form10.css` 에는 `.loading` · `.error-message` · `.cancel-btn` · `.button-group` 규칙이 **이미 들어 있지만**, `book-client-step10.html` 에는 **그 요소가 없다.** 스타일만 있고 붙일 자리가 없는 상태다. **과제 1에서 화면을 옮길 때 네 자리를 함께 넣을 것.**

### 서버가 받는 모양 — 학생 앱보다 한 겹 더 깊다

```js
{
    title, author, isbn, price, publishDate,      // 바깥이 다섯
    bookDetail: {                                  // ← detailRequest 가 아니다
        description, language, pageCount,
        publisher, coverImageUrl, edition
    }
}
```

학생 앱은 바깥 칸이 둘(`name`, `studentNumber`)이고 안쪽이 `detailRequest` 였다. **도서는 바깥이 다섯이고 안쪽 이름이 `bookDetail` 이다.** 이름을 잘못 쓰면 **서버가 상세 정보를 받지 못하고 조용히 빈 값으로 저장한다.**

⚠️ **비어 있는 값을 어떻게 보낼지 정해야 한다.** `price` 와 `pageCount` 는 숫자이고 `publishDate` 는 날짜이므로, 값이 없으면 빈 문자열 `""` 이 아니라 **`null`** 을 보내야 한다. 빈 문자열을 보내면 서버가 해석하지 못해 오류가 난다. **여기서도 `??` 가 아니라 `||` 를 쓴다.**

---

## 4.5-2. 과제 12개 — 수업 실습과 1:1로 대응한다

| 과제 | 무엇을 하나 | 만드는 파일 | 대응 실습 |
|---|---|---|---|
| 1 | Vite 프로젝트 만들기와 기존 코드 옮기기 | `index.html`, `src/style.css`, `src/main.js` | 4-1 |
| 2 | 서버 주소를 `.env` 로 분리 | `src/config.js`, `.env.*` | 4-2 |
| 3 | 서버 통신을 모듈로 분리 | `src/api/bookApi.js` | 4-3 |
| 4 | then 체인을 `async / await` 으로 | `src/main.js` | 4-4 |
| 5 | 반복되는 fetch 를 `request()` 하나로 | `src/api/bookApi.js` | 4-5 |
| 6 | 나머지 CRUD 를 화살표 함수로 | `src/api/bookApi.js` | 4-6 |
| 7 | 폼 값 수집을 `bookForm.js` 로 | `src/ui/bookForm.js` | 4-7 |
| 8 | 유효성 검사를 순수 함수로 | `src/lib/validation.js` | 4-8 |
| 9 | 메시지와 폼을 화면 모듈로 | `src/ui/message.js`, `bookForm.js` | 4-9 |
| 10 | 표 다시 그리기 — `onclick` 걷어내기 | `src/ui/bookTable.js`, `bookDetail.js` | 4-10 |
| 11 | `main.js` 로 조립 | `src/main.js` | 4-11 |
| 12 | 빌드와 배포 | `dist/` | 4-12 |

⚠️ **과제 3부터 10까지는 마지막 순서가 언제나 "`main.js` 정리"다.** 모듈로 옮긴 코드를 `main.js` 에서 지우고 그 자리에 `import` 를 넣는 단계다. **이 순서를 건너뛰면 같은 이름이 두 번 선언돼 오류가 난다.**

### 준비물

- Node.js 20 이상 (`node -v`)
- Spring Boot 서버가 `http://localhost:8080` 에서 실행 중일 것
- `book/book-client-step10.html`, `book/js/form10.js`, `book/css/form10.css`
- VS Code 와 터미널

---

## 4.5-3. 단계별 핵심만

### 과제 1 — Vite 프로젝트와 화면 옮기기

```
npm create vite@latest
  Project name: book_ecma
  Select a framework: Vanilla
  Select a variant: JavaScript
cd book_ecma
npm install
npm run dev
```

Vite 가 만들어 준 `counter.js` 와 `assets` 안의 그림 파일을 지운다. `main.js` 안도, `index.html` 의 `<body>` 안도 비운다.

**다섯 군데만 달라진다**

| | 지금 | `book_ecma/index.html` |
|---|---|---|
| 1 | `<link rel="stylesheet" href="css/form10.css">` | **지운다** — CSS 는 `main.js` 가 `import` 한다 |
| 2 | `<script src="js/form10.js"></script>` | `<script type="module" src="/src/main.js"></script>` |
| 3 | `<title>도서 관리 시스템</title>` | 그대로 둔다 |
| 4 | 등록 버튼 하나만 있다 | **`button-group` 으로 감싸고 취소 버튼과 메시지 자리를 넣는다** |
| 5 | 표 위에 아무것도 없다 | **로딩 표시 자리를 넣는다** |

```html
<div class="button-group">
    <button type="submit">도서 등록</button>
    <button type="button" class="cancel-btn" id="cancelButton"
            style="display: none">취소</button>
    <span class="error-message" id="formError"></span>
</div>

<div id="loadingMessage" class="loading" style="display: none">로딩 중...</div>
```

여기 쓰인 class 네 개는 **`form10.css` 에 이미 들어 있다.** 과제 9까지는 화면에 보이지 않고 자리만 잡고 있다.

옮기는 것 : `form10.css` 전부 → `src/style.css`, `form10.js` 전부 → `src/main.js`, 그리고 `main.js` 맨 위에 `import "./style.css";` 한 줄.

**지금은 한 글자도 고치지 말 것.** 과제 2부터 여기 붙여 넣은 코드를 조금씩 잘라내어 모듈로 옮기고 문법을 다듬는 과정이다.

⚠️ **절반만 동작한다.** 목록 조회와 도서 등록은 그대로 되지만, 표의 수정·삭제·상세 버튼을 누르면 `ReferenceError` 가 난다. 🔗 4-8절 ②번 — **모듈 안의 함수는 `window` 에 없다.** 과제 10까지 확인용으로 쓰려면 `main.js` 맨 아래에 세 줄을 임시로 둔다.

```js
// 임시 — 과제 10 에서 반드시 지운다
window.editBook = editBook;
window.deleteBook = deleteBook;
window.showBookDetail = showBookDetail;   // ← 학생 앱보다 한 줄 많다
```

⚠️ CSS 가 하나도 안 먹는다 → `main.js` 맨 위의 `import "./style.css";` 가 빠졌다.

### 과제 2 — .env 와 config.js

| export 하는 이름 | 무엇 |
|---|---|
| `API_BASE_URL` | `import.meta.env` 에서 읽고, 없으면 `localhost:8080` |
| **`BOOKS_URL`** | `API_BASE_URL` + **`/api/books`** |
| `JSON_HEADERS` | POST · PUT 공통 `Content-Type` 헤더 |

**`main.js` 는 아직 고치지 않는다.** `config.js` 는 아직 아무도 쓰지 않고, 과제 3부터 `bookApi.js` 가 import 해서 쓴다. **`main.js` 맨 위의 `const API_BASE_URL` 줄은 과제 6에서 지운다. 지금 지우면 아직 남아 있는 `fetch` 코드가 전부 깨진다.**

⚠️ `.env` 를 만든 뒤에는 **개발 서버(5173)를 다시 시작**해야 한다. `.gitignore` 에 `.env*` 를 넣는다.

### 과제 3 ~ 6 — bookApi.js

| export 할 이름 | 메서드 | 주소 |
|---|---|---|
| `fetchBooks()` | GET | `/api/books` |
| `fetchBook(id)` | GET | `/api/books/{id}` |
| `createBook(book)` | POST | `/api/books` |
| `updateBook(id, book)` | PUT | `/api/books/{id}` |
| `deleteBook(id)` | DELETE | `/api/books/{id}` |

`request(url, options = {})` 하나가 챙기는 네 가지는 수업과 같다. 다만 **기본 문구를 학생 앱에서 그대로 베끼면 안 된다.**

| 코드 | 학생 앱 | **도서 앱** |
|---|---|---|
| 409 | 이미 등록된 학번입니다 | **이미 등록된 ISBN 입니다** |
| 404 | 존재하지 않는 학생입니다 | **존재하지 않는 도서입니다** |

**`main.js` 정리 — 이름이 겹치므로 `as` 로 별칭을 붙인다.** 🔗 지금 `student_ecma/src/main.js` 가 정확히 이 상태다(4-0-1절).

```js
import {
    createBook as apiCreateBook,
    updateBook as apiUpdateBook,
    deleteBook as apiDeleteBook,
    fetchBook,
} from "./api/bookApi.js";
```

**별칭은 이름이 겹치는 동안만 필요하다. 과제 11에서 `main.js` 를 다시 쓰면 겹치는 이름이 없어지므로 별칭도 함께 사라진다.**

**■ 확인하기** — `main.js` 에 **`fetch` 라는 낱말이 하나도 남아 있지 않다.**

### 과제 7 — bookForm.js

지금은 제출 핸들러 한복판에서 `new FormData(bookForm)` 로 값을 긁어모은다. **입력칸이 11개라 그 코드만 열다섯 줄이다.**

| export 할 이름 | 무엇 |
|---|---|
| `bookForm` | `document.getElementById("bookForm")` |
| `collectBookData()` | 폼에서 값을 읽어 **서버가 받는 모양**으로 돌려준다 |

**■ 확인하기** — `main.js` 에 **`FormData` 라는 낱말이 없다.** 상세 정보(출판사·언어 등)가 서버에 저장된다.

### 과제 8 — validation.js

| export 할 이름 | 무엇 |
|---|---|
| `validateBook(book)` | 문제가 있으면 그 문구를, 없으면 **빈 문자열**을 돌려준다 |

`isValidUrl` 은 이 파일 안에서만 쓰이므로 **`export` 하지 않는다.** 이 파일은 `document` 도 `alert` 도 쓰지 않는다.

**도서에만 있는 검사 두 가지**

| 검사 | 기준 | 실패 문구 |
|---|---|---|
| ISBN 형식 | `/^[0-9X-]+$/` 에 맞는가 | 올바른 ISBN 형식이 아닙니다. (숫자와 X, - 만 허용) |
| 표지 이미지 URL | **`new URL()` 이 오류를 던지지 않는가** | 올바른 이미지 URL 형식이 아닙니다. |

**표지 이미지 URL 은 입력된 경우에만 검사한다.** 비워 두는 것은 잘못이 아니다. 가격과 페이지 수는 **0 이상**인지만 본다.

부르는 쪽:

```js
const message = validateBook(bookData);
if (message) {
    showError(message);   // 과제 9 에서 만든다. 지금은 alert 으로 둔다
    return;
}
```

### 과제 9 — message.js + bookForm.js

**왜 `alert` 을 걷어내나** — `alert` 은 화면을 막아 세우고, 모양을 손댈 수 없으며, 여러 개가 겹치면 순서대로 하나씩 눌러야 한다.

| export 할 이름 | 무엇 |
|---|---|
| `showError(message)` | `#formError` 에 빨간 글씨. **지우기 전까지 남는다** |
| `showSuccess(message)` | 초록 글씨. **3초 뒤 저절로 사라진다** |
| `clearMessages()` | 메시지를 지운다 |
| `setLoading(isLoading = true)` | `#loadingMessage` 를 켜고 끈다 |

`bookForm.js` 에 더할 것 : `submitButton` / `cancelButton`, `fillForm(book)` (**입력칸 11개**), `setEditMode(isEditing)`, `resetForm()`, `scrollToForm()`.

⚠️ **`fillForm` 을 쓸 때 `book.bookDetail` 이 없을 수 있다.** 지금 코드는 `if (book.bookDetail)` 로 **여섯 줄을 통째로 감싸** 건너뛴다. `book.bookDetail?.publisher ?? ""` 로 쓰면 한 줄씩 처리되어 `if` 가 없어진다. **`value` 에 `undefined` 를 넣으면 화면에 "undefined" 라는 글자가 그대로 나오므로 `??` 가 꼭 필요하다.**

⚠️ **삭제 전에 묻는 `confirm` 과 상세 보기의 `alert` 은 남겨 둔다.** 확인을 받는 일과 알리는 일은 다르다.

### 과제 10 — onclick 걷어내기 (가장 큰 변화)

```html
<!-- 지금 -->
<button class="edit-btn"   onclick="editBook(${book.id})">수정</button>
<button class="delete-btn" onclick="deleteBook(${book.id})">삭제</button>
<button class="detail-btn" onclick="showBookDetail(${book.id})">상세</button>

<!-- 바꾼 뒤 — 버튼에는 표시만 남기고, 클릭은 한 곳에서 받는다 -->
<button type="button" class="edit-btn"   data-action="edit"   data-id="${book.id}">수정</button>
<button type="button" class="delete-btn" data-action="delete" data-id="${book.id}">삭제</button>
<button type="button" class="detail-btn" data-action="detail" data-id="${book.id}">상세</button>
```

**학생 앱은 `data-action` 이 `edit` 과 `delete` 둘이었다. 도서는 `detail` 이 하나 더 있어 셋이다. 클릭을 받는 쪽의 분기도 셋이 된다. 이것이 이 과제에서 수업 문서를 그대로 베끼면 안 되는 가장 눈에 띄는 자리다.**

```js
bookTableBody.addEventListener("click", (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    const id = Number(button.dataset.id);
    const action = button.dataset.action;

    if (action === "edit")   startEdit(id);
    if (action === "delete") removeBook(id);
    if (action === "detail") showDetail(id);
});
```

| export 할 이름 | 파일 | 무엇 |
|---|---|---|
| `bookTableBody` | `ui/bookTable.js` | 클릭을 한곳에서 받을 `tbody` |
| `renderBookTable(books)` | `ui/bookTable.js` | `tbody` 를 비우고 행을 다시 그린다 |
| `renderTableError(message)` | `ui/bookTable.js` | 목록을 못 불러왔을 때 안내 |
| **`formatBookDetail(book)`** | `ui/bookDetail.js` | 상세 정보를 **여러 줄 문자열로 만들어 돌려준다** |

- 값을 칸에 넣을 때는 `innerHTML` 대신 **`textContent`**. 도서 제목이나 설명에 꺾쇠(`<`)가 들어 있어도 안전하다.
- 가격은 `₩` 와 천 단위 구분, 값이 없으면 `"-"` 로 보이던 지금 방식을 그대로 옮긴다.
- `bookDetail.js` 는 **서버를 부르는 일은 `main.js` 가, 글자를 만드는 일은 이 파일이** 맡는다. 화면에 어떻게 보여 줄지(`alert` 이냐 모달이냐)는 부르는 쪽이 정한다. **나중에 모달로 바꾸더라도 이 파일은 고칠 것이 없다.**
- ⚠️ **지금 코드의 줄바꿈이 백슬래시 두 개(`\\n`)로 적혀 있다.** 그대로 옮기면 `alert` 에 줄바꿈 대신 `n` 이라는 글자가 찍힌다. **옮기면서 `\n` 하나로 고칠 것.**
- ⚠️ `dataset.id` 는 언제나 **문자열**이다. `Number(id)` 로 바꾸지 않으면 `editingBookId === bookId` 비교가 숫자와 문자열 비교가 되어 어긋난다.

**이제 `onclick` 없이도 버튼이 동작하므로, 과제 1에서 임시로 넣어 두었던 `window` 세 줄을 지운다. 이 세 줄을 지우는 것이 이번 과제의 마무리다.**

### 과제 11 — main.js 조립

| | 무엇 | 설명 |
|---|---|---|
| 1 | 가져오기 | `style.css` 부터 `ui` 까지 `import` 를 모은다 |
| 2 | 상태 | **`let editingBookId = null;` 한 줄만 남는다** |
| 3 | 목록 불러오기 | `loadBooks()` — `setLoading` 과 `renderTableError` 를 쓴다 |
| 4 | 폼 제출 | 등록과 수정을 `editingBookId` 하나로 가른다 |
| 5 | 표 클릭 받기 | 이벤트 위임 — `edit` · `delete` · `detail` 셋 |
| 6 | 수정 준비 · 삭제 · 상세 | `startEdit` / `removeBook` / `showDetail` |
| 7 | 취소 버튼과 시작 | `cancelButton` 연결, 마지막 줄에서 `loadBooks()` |

**이름이 바뀌는 함수 셋**

| `form10.js` | `book_ecma` 의 `main.js` | 까닭 |
|---|---|---|
| `editBook(id)` | `startEdit(id)` | api 의 `fetchBook` 과 하는 일이 다르다 |
| `deleteBook(id)` | `removeBook(id)` | api 의 `deleteBook` 과 이름이 겹친다 |
| `showBookDetail(id)` | `showDetail(id)` | ui 의 `formatBookDetail` 과 구분한다 |

**사라지는 함수도 있다.** `createBook` 과 `updateBook` 은 **제출 핸들러 안으로 들어가 함수 자체가 없어진다.** 등록인지 수정인지는 `editingBookId` 가 `null` 인가로 가른다.

⚠️ **`DOMContentLoaded` 는 필요 없다.** `type="module"` 로 불러온 파일은 HTML 을 다 읽은 뒤에 실행되므로 이 감싸개가 필요 없다. `loadBooks()` 를 `main.js` **마지막 줄에서 그냥** 부르면 된다. → 4-8절 ③번

**■ 자주 나는 오류**
- submit 을 눌렀는데 페이지가 새로고침된다 → `event.preventDefault()` 가 빠졌다
- 수정 후에도 계속 수정 모드다 → `editingBookId = null` 을 빠뜨렸다
- `blocked by CORS policy` → **서버의 CORS 허용 목록에 5173 이 있어야 한다**

### 과제 12 — 빌드와 배포

```bash
npm run build
npm run preview
```

모듈 여덟 개가 js 파일 하나로 합쳐진다. CSS 도 하나로 합쳐지고, **파일 이름 뒤에 붙는 문자열은 내용이 바뀌면 함께 바뀌어 브라우저가 옛 파일을 계속 쓰는 것을 막아 준다.**

⚠️ `.env` 파일의 값은 **빌드 결과에 그대로 들어간다.** 비밀번호나 API 키는 절대 넣지 말 것.

---

## 4.5-4. form10.js 가 어디로 가는가 (부록 A)

| `form10.js` | `book_ecma` | 달라지는 점 |
|---|---|---|
| `API_BASE_URL` | `src/config.js` | `.env` 에서 읽는다 |
| `editingBookId` | `src/main.js` | 그대로 |
| `bookForm`, `submitButton` | `src/ui/bookForm.js` | 그대로 |
| `bookTableBody` | `src/ui/bookTable.js` | 그대로 |
| 제출 핸들러의 `FormData` | `ui/bookForm.js` `collectBookData()` | 값만 돌려준다 |
| `validateBook` | `lib/validation.js` | **`alert` 대신 문구를 돌려준다** |
| `isValidUrl` | `lib/validation.js` | **`export` 하지 않는다** |
| `loadBooks` 의 fetch | `api/bookApi.js` `fetchBooks()` | `request()` 를 쓴다 |
| `createBook` 의 fetch | `api/bookApi.js` `createBook()` | `request()` 를 쓴다 |
| `updateBook` 의 fetch | `api/bookApi.js` `updateBook()` | `request()` 를 쓴다 |
| `deleteBook` 의 fetch | `api/bookApi.js` `deleteBook()` | `request()` 를 쓴다 |
| `editBook` 의 fetch | `api/bookApi.js` `fetchBook()` | `request()` 를 쓴다 |
| `renderBookTable` | `ui/bookTable.js` | **`onclick` → `data-action`** |
| `editBook` 의 폼 채우기 | `ui/bookForm.js` `fillForm()` | `?.` 와 `??` 로 줄인다 |
| `submitButton.textContent` | `ui/bookForm.js` `setEditMode()` | 취소 버튼도 함께 다룬다 |
| `resetForm` | `ui/bookForm.js` `resetForm()` | 그대로 |
| `showBookDetail` 의 문자열 | `ui/bookDetail.js` `formatBookDetail()` | 값만 돌려준다 |
| **`alert(...)` 전부** | `ui/message.js` | 화면 안 메시지로 |
| `DOMContentLoaded` | **없어진다** | module 은 늦게 실행된다 |

## 4.5-5. 학생 앱 ↔ 도서 앱 대응표 (부록 B)

수업 문서를 보며 옮길 때 **바꿔 써야 하는 이름**을 모은 표다. **이 표를 옆에 두고 옮긴다.**

| student_ecma (수업) | book_ecma (과제) | 비고 |
|---|---|---|
| `api/studentApi.js` | `api/bookApi.js` | |
| `ui/studentForm.js` | `ui/bookForm.js` | |
| `ui/studentTable.js` | `ui/bookTable.js` | |
| — | **`ui/bookDetail.js`** | **도서에만 있다** |
| `STUDENTS_URL` | `BOOKS_URL` | `/api/books` |
| `fetchStudents` / `fetchStudent` | `fetchBooks` / `fetchBook` | |
| `createStudent` / `updateStudent` | `createBook` / `updateBook` | |
| `deleteStudent` | `deleteBook` | |
| `validateStudent` | `validateBook` | |
| `collectStudentData` | `collectBookData` | |
| `renderStudentTable` | `renderBookTable` | |
| `studentTableBody` | `bookTableBody` | |
| `editingStudentId` | `editingBookId` | |
| **`detailRequest`** | **`bookDetail`** | **이름이 다르다** |
| 입력칸 6개 | 입력칸 11개 | |
| `data-action`: edit, delete | `data-action`: edit, delete, **detail** | **하나 더 있다** |
| 409 이미 등록된 학번입니다 | 409 이미 등록된 ISBN 입니다 | |
| 404 존재하지 않는 학생입니다 | 404 존재하지 않는 도서입니다 | |

## 4.5-6. 제출 전 점검표 (부록 C)

| | 확인할 것 | 관련 과제 |
|---|---|---|
| ☐ | `npm run dev` 로 열어 등록·조회·수정·삭제·상세가 모두 된다 | 전체 |
| ☐ | F12 → Console 에 오류가 하나도 없다 | 전체 |
| ☐ | `src` 아래에 파일 여덟 개가 모두 있다 | 전체 |
| ☐ | 코드에 `http://localhost:8080` 이 `config.js` 말고는 없다 | 2 |
| ☐ | `.env*` 가 `.gitignore` 에 들어 있다 | 2 |
| ☐ | **`main.js` 에 `fetch` 가 하나도 없다** | 6 |
| ☐ | **`main.js` 에 `FormData` 가 없다** | 7 |
| ☐ | **`validation.js` 에 `alert` 과 `document` 가 없다** | 8 |
| ☐ | **`bookApi.js` 에 `alert` 과 `innerHTML` 이 없다** | 3~6 |
| ☐ | 등록·수정·삭제에서 `alert` 창이 뜨지 않는다 | 9 |
| ☐ | 성공 메시지가 3초 뒤 사라지고 오류 메시지는 남는다 | 9 |
| ☐ | 취소 버튼이 수정 모드에서만 보인다 | 9 |
| ☐ | **`window.editBook` 세 줄을 지웠다** | 10 |
| ☐ | 버튼에 `onclick` 이 아니라 `data-action` 이 붙어 있다 | 10 |
| ☐ | 상세 보기의 줄바꿈이 제대로 나온다 | 10 |
| ☐ | **`main.js` 에 `document.getElementById` 가 없다** | 11 |
| ☐ | `npm run build` 가 오류 없이 끝나고 `preview` 로 동작한다 | 12 |

## 4.5-7. 마무리 — React 로 가는 다리

기능은 지금과 똑같은데 코드는 완전히 달라졌다. **305줄짜리 파일 하나가 역할이 또렷한 파일 여덟 개가 되었다.**

여기서 익힌 세 가지가 React 로 넘어가는 다리가 된다.

1. **파일을 역할별로 나누고 `export` / `import` 로 잇는 것**
2. **화면을 그리는 코드와 데이터를 가져오는 코드를 섞지 않는 것**
3. **상태(`editingBookId`)가 바뀌면 화면을 다시 그리는 것**

**React 는 이 마지막 하나를 자동으로 해 준다.** 그것이 PART 5의 전부라고 해도 과언이 아니다.

> 막히면 수업 문서의 같은 번호 실습을 볼 것. 다만 **그대로 베끼지는 말 것.** 부록 B를 옆에 두고 이름을 하나씩 바꿔 가며 옮기면 된다.
> **베껴서 동작하게 만드는 것보다, 왜 이 파일에 이 코드가 있는지 말할 수 있는 것이 이 과제의 목표다.**
