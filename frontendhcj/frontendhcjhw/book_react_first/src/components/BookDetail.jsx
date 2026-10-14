/* ---------------------------------------------------------
   BookDetail — 상세 보기
   ui/bookDetail.js 의 formatBookDetail 은 여러 줄짜리 "문자열"을
   만들었고, main.js 가 그것을 alert 으로 띄웠습니다. React 에서
   화면에 낼 것은 문자열이 아니라 JSX 이므로, 이 파일은 그대로
   옮기지 않고 다시 씁니다.

   book 이 null 이면 null 을 돌려주어 화면에서 사라집니다.
   alert 과 달리 화면을 멈춰 세우지 않으므로 떠 있는 동안에도
   표를 스크롤할 수 있습니다.
   --------------------------------------------------------- */

const EMPTY = "-";

// bookTable.js 의 것과 같은 규칙. 컴포넌트 바깥에 둔다.
function formatPrice(price) {
    if (price == null) return EMPTY;
    return `₩${price.toLocaleString()}`;
}

export default function BookDetail({ book, onClose }) {
    if (!book) return null;

    const { title, author, isbn, price, publishDate, bookDetail } = book;

    return (
        <div className="detail-overlay" onClick={onClose}>
            {/* 카드 안 클릭이 바깥(overlay)까지 번지면 곧바로 닫혀 버리므로 막는다. */}
            <div className="detail-card" onClick={(event) => event.stopPropagation()}>
                <button type="button" className="detail-close-btn" onClick={onClose}>
                    닫기
                </button>

                <h2>{title}</h2>
                <p>저자: {author}</p>
                <p>ISBN: {isbn}</p>
                <p>가격: {formatPrice(price)}</p>
                <p>출판일: {publishDate ?? EMPTY}</p>

                {/* 상세 정보가 없는 도서는 bookDetail 이 null 이다. 그때는 기본 정보만 보여 준다. */}
                {bookDetail && (
                    <>
                        <hr />
                        <p>설명: {bookDetail.description || EMPTY}</p>
                        <p>언어: {bookDetail.language || EMPTY}</p>
                        <p>페이지 수: {bookDetail.pageCount ?? EMPTY}</p>
                        <p>출판사: {bookDetail.publisher || EMPTY}</p>
                        <p>에디션: {bookDetail.edition || EMPTY}</p>
                        <p>표지 이미지: {bookDetail.coverImageUrl || EMPTY}</p>
                    </>
                )}
            </div>
        </div>
    );
}
