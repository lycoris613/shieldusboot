/* ---------------------------------------------------------
   BookForm — 등록 · 수정 폼
   ui/bookForm.js 를 다시 씁니다. 이 컴포넌트도 입력값을 갖고 있지
   않습니다. 화면에 보이는 값은 부모(App)가 준 form 에서 오고,
   글자가 바뀌면 onChange 로 부모에게 알립니다.

   입력칸 11개는 생김새가 거의 같지만 일부러 하나씩 펼쳐 적습니다.
   위에서 아래로 한 번에 읽히기 때문입니다. 반복을 줄이는 것은
   과제 13(BookFormField.jsx)에서 합니다.
   --------------------------------------------------------- */

import MessageBox from "./MessageBox";

export default function BookForm({ form, isEditing, message, onChange, onSubmit, onCancel, containerRef }) {
    return (
        <div className={`form-container${isEditing ? " editing" : ""}`} ref={containerRef}>
            <h2>도서 등록</h2>
            <form onSubmit={onSubmit}>
                <div className="form-grid">
                    <div className="form-group">
                        <label htmlFor="title">제목:</label>
                        <input type="text" id="title" name="title" value={form.title} onChange={onChange} required />
                    </div>

                    <div className="form-group">
                        <label htmlFor="author">저자:</label>
                        <input type="text" id="author" name="author" value={form.author} onChange={onChange} required />
                    </div>

                    <div className="form-group">
                        <label htmlFor="isbn">ISBN:</label>
                        <input type="text" id="isbn" name="isbn" value={form.isbn} onChange={onChange} required />
                    </div>

                    <div className="form-group">
                        <label htmlFor="price">가격:</label>
                        <input type="number" id="price" name="price" value={form.price} onChange={onChange} />
                    </div>

                    <div className="form-group">
                        <label htmlFor="publishDate">출판일:</label>
                        <input type="date" id="publishDate" name="publishDate" value={form.publishDate} onChange={onChange} />
                    </div>

                    <div className="form-group">
                        <label htmlFor="language">언어:</label>
                        <input type="text" id="language" name="language" value={form.language} onChange={onChange} />
                    </div>

                    <div className="form-group">
                        <label htmlFor="pageCount">페이지 수:</label>
                        <input type="number" id="pageCount" name="pageCount" value={form.pageCount} onChange={onChange} />
                    </div>

                    <div className="form-group">
                        <label htmlFor="publisher">출판사:</label>
                        <input type="text" id="publisher" name="publisher" value={form.publisher} onChange={onChange} />
                    </div>

                    <div className="form-group">
                        <label htmlFor="edition">에디션:</label>
                        <input type="text" id="edition" name="edition" value={form.edition} onChange={onChange} />
                    </div>

                    <div className="form-group">
                        <label htmlFor="coverImageUrl">표지 이미지 URL:</label>
                        <input type="url" id="coverImageUrl" name="coverImageUrl" value={form.coverImageUrl} onChange={onChange} />
                    </div>
                </div>

                {/* 설명은 한 줄을 다 쓰도록 그리드 바깥에 둔다. */}
                <div className="form-group">
                    <label htmlFor="description">설명:</label>
                    <textarea id="description" name="description" rows="4" value={form.description} onChange={onChange} />
                </div>

                <div className="button-group">
                    <button type="submit">{isEditing ? "도서 수정" : "도서 등록"}</button>
                    {isEditing && (
                        <button type="button" className="cancel-btn" onClick={onCancel}>
                            취소
                        </button>
                    )}
                    <MessageBox message={message} />
                </div>
            </form>
        </div>
    );
}
