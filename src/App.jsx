/* ---------------------------------------------------------
   App — 이 앱의 유일한 컨테이너 컴포넌트
   값을 갖고, 서버를 부르고, 자식이 알려 온 일을 처리한다.
   정작 화면을 그리는 일은 거의 하지 않는다 — return 을 보면
   제목 한 줄과 자식 셋(BookForm · BookTable · BookDetail)뿐이다.
   --------------------------------------------------------- */

// 1) 가져오기 ------------------------------------------------
import { useEffect, useRef, useState } from "react";
import "./style.css";

import { fetchBooks, fetchBook, createBook, updateBook, deleteBook } from "./api/bookApi.js";
import { validateBook } from "./lib/validation.js";
import { EMPTY_FORM, toRequest, toFormValues } from "./lib/bookData.js";
import { APP_MODE } from "./config.js";

import BookForm from "./components/BookForm.jsx";
import BookTable from "./components/BookTable.jsx";
import BookDetail from "./components/BookDetail.jsx";

// ui/bookTable.js 의 renderTableError() 가 인자 없이 불렸을 때 쓰던 기본 문구.
// 표 자리에는 이 고정 문구만 보여 주고, 서버가 실제로 보낸 메시지는
// 폼 아래 메시지(showError)로 따로 보여 준다 — book_ecma 의 main.js 와 같다.
const DEFAULT_LIST_ERROR = "오류: 데이터를 불러올 수 없습니다.";

export default function App() {
    // 2) state 일곱 개 ----------------------------------------
    const [books, setBooks] = useState([]);
    const [form, setForm] = useState(EMPTY_FORM);
    const [editingId, setEditingId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [listError, setListError] = useState(null);
    const [message, setMessage] = useState(null);
    const [detailBook, setDetailBook] = useState(null);

    // 3) ref 하나 — 수정할 때 폼으로 스크롤하는 데 쓴다 --------
    const formContainerRef = useRef(null);

    const isEditing = editingId !== null;

    // 4) 목록 불러오기 -----------------------------------------
    async function loadBooks() {
        setLoading(true);
        setListError(null);

        try {
            const data = await fetchBooks();
            setBooks(data);
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" }); // bookApi 가 던진 실제 메시지
            setListError(DEFAULT_LIST_ERROR); // 표 자리에는 고정 문구만 보여 준다
        } finally {
            setLoading(false);
        }
    }

    // 5) 처음 한 번만 부르기 ------------------------------------
    useEffect(() => {
        loadBooks();
    }, []);

    // 6) 성공 메시지는 3초 뒤 저절로 사라진다 --------------------
    useEffect(() => {
        if (!message || message.type !== "success") return;

        const timer = setTimeout(() => setMessage(null), 3000);
        return () => clearTimeout(timer);
    }, [message]);

    // 7) 입력 처리 -----------------------------------------------
    function handleChange(event) {
        const { name, value } = event.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    }

    function resetForm() {
        setForm(EMPTY_FORM);
        setEditingId(null);
    }

    // 8) 폼 제출 — 등록과 수정 -----------------------------------
    async function handleSubmit(event) {
        event.preventDefault();
        setMessage(null);

        const bookData = toRequest(form);

        const errorMessage = validateBook(bookData);
        if (errorMessage) {
            setMessage({ text: errorMessage, type: "error" });
            return;
        }

        try {
            if (isEditing) {
                await updateBook(editingId, bookData);
                setMessage({ text: "도서 정보가 성공적으로 수정되었습니다.", type: "success" });
            } else {
                await createBook(bookData);
                setMessage({ text: "도서가 성공적으로 등록되었습니다.", type: "success" });
            }

            resetForm();
            await loadBooks();
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    function handleCancel() {
        resetForm();
        setMessage(null);
    }

    // 9) 표의 알림 처리 — 수정 준비 · 삭제 · 상세 -----------------
    async function handleEdit(bookId) {
        setMessage(null);

        try {
            const book = await fetchBook(bookId);
            setForm(toFormValues(book));
            setEditingId(bookId);
            formContainerRef.current?.scrollIntoView({ behavior: "smooth" });
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    async function handleDelete(bookId) {
        if (!window.confirm("정말로 이 도서를 삭제하시겠습니까?")) {
            return;
        }

        try {
            await deleteBook(bookId);
            setMessage({ text: "도서가 성공적으로 삭제되었습니다.", type: "success" });

            // 수정 중이던 도서를 삭제했다면 폼도 등록 모드로 되돌린다.
            if (editingId === bookId) {
                resetForm();
            }

            await loadBooks();
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    async function handleDetail(bookId) {
        setMessage(null);

        try {
            const book = await fetchBook(bookId);
            setDetailBook(book);
        } catch (error) {
            console.error("Error:", error);
            setMessage({ text: error.message, type: "error" });
        }
    }

    function handleCloseDetail() {
        setDetailBook(null);
    }

    // 10) 배지 계산 — APP_MODE 로 class 문자열 하나를 정한다 ------
    const appModeClass = `app-mode ${APP_MODE === "PROD" ? "prod" : "test"}`;

    // 11) return — 제목 · BookForm · BookTable · BookDetail ------
    return (
        <>
            <h1>
                도서 관리 시스템 <span className={appModeClass}>{APP_MODE}</span>
            </h1>

            <BookForm
                form={form}
                isEditing={isEditing}
                message={message}
                onChange={handleChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
                containerRef={formContainerRef}
            />

            <BookTable
                books={books}
                loading={loading}
                error={listError}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onDetail={handleDetail}
            />

            <BookDetail book={detailBook} onClose={handleCloseDetail} />
        </>
    );
}
