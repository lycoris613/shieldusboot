/* ---------------------------------------------------------
   과제 13 — 리팩토링: 반복되는 입력칸을 공통 컴포넌트로

   BookForm.jsx 의 입력칸 열 개를 나란히 놓고 보면 달라지는 것은
   다섯 가지(label · name · type · required · value)뿐이다. 그
   다섯 가지를 props 로 받는 작은 컴포넌트(Field)로 묶었다.

   선택 — 설명(textarea)은 묶지 않고 그대로 둔다. input 열 개는
   생김새가 완전히 같아 배열 하나로 표현할 수 있지만, textarea 는
   value/onChange 는 같아도 태그 자체가 달라 조건을 넣어야 한다.
   조건 하나를 넣어 "폼이 완전히 한 모양"이 되는 이득보다, 가장
   낯선 자리(textarea)를 코드에서도 눈에 띄게 남겨 두는 쪽을
   골랐다.

   동작은 BookForm.jsx 와 똑같다. App.jsx 에서 가져오는 줄만
     import BookForm from "./components/BookForm.jsx";
   ↕
     import BookForm from "./components/BookFormField.jsx";
   로 바꾸면 두 버전을 번갈아 보며 견줄 수 있다.
   --------------------------------------------------------- */

import MessageBox from "./MessageBox";

// 달라지는 다섯 가지만 여기 적는다. 순서는 BookForm.jsx 의 순서를 따른다.
const FIELDS = [
    { name: "title", label: "제목:", type: "text", required: true },
    { name: "author", label: "저자:", type: "text", required: true },
    { name: "isbn", label: "ISBN:", type: "text", required: true },
    { name: "price", label: "가격:", type: "number", required: false },
    { name: "publishDate", label: "출판일:", type: "date", required: false },
    { name: "language", label: "언어:", type: "text", required: false },
    { name: "pageCount", label: "페이지 수:", type: "number", required: false },
    { name: "publisher", label: "출판사:", type: "text", required: false },
    { name: "edition", label: "에디션:", type: "text", required: false },
    { name: "coverImageUrl", label: "표지 이미지 URL:", type: "url", required: false },
];

// input 열 개가 공통으로 쓰는 모양. 이 파일 안에서만 쓰므로 export 하지 않는다.
function Field({ name, label, type, required, value, onChange }) {
    return (
        <div className="form-group">
            <label htmlFor={name}>{label}</label>
            <input type={type} id={name} name={name} value={value} onChange={onChange} required={required} />
        </div>
    );
}

export default function BookForm({ form, isEditing, message, onChange, onSubmit, onCancel, containerRef }) {
    return (
        <div className={`form-container${isEditing ? " editing" : ""}`} ref={containerRef}>
            <h2>도서 등록</h2>
            <form onSubmit={onSubmit}>
                <div className="form-grid">
                    {FIELDS.map((field) => (
                        <Field key={field.name} {...field} value={form[field.name]} onChange={onChange} />
                    ))}
                </div>

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
