/* ---------------------------------------------------------
   BookTable — 도서 목록을 표로 그린다
   ui/bookTable.js 의 renderBookTable 을 다시 씁니다.
   서버를 부르지도, 값을 저장하지도 않습니다. 버튼이 눌리면
   "눌렸다" 고 부모(App)에게 알릴 뿐, 무엇을 할지는 App 이 정합니다.
   --------------------------------------------------------- */

// 표의 열 개수. colSpan 에 쓴다. 화면을 그릴 때마다 새로 만들 필요가 없으므로
// 컴포넌트 바깥, 파일 맨 위에 둔다.
const COLUMN_COUNT = 7;

// 가격을 "₩12,000" 모양으로 바꾼다. 값이 없으면 "-" 를 돌려준다.
// 이 함수도 컴포넌트 바깥에 둔다 — 화면을 그릴 때마다 새로 만들어지지 않게.
function formatPrice(price) {
    if (price == null) return "-";
    return `₩${price.toLocaleString()}`;
}

export default function BookTable({ books, loading, error, onEdit, onDelete, onDetail }) {
    return (
        <div className="table-container">
            <h2>도서 목록</h2>
            {loading && <div className="loading">로딩 중...</div>}
            <table>
                <thead>
                    <tr>
                        <th>제목</th>
                        <th>저자</th>
                        <th>ISBN</th>
                        <th>가격</th>
                        <th>출판일</th>
                        <th>출판사</th>
                        <th>액션</th>
                    </tr>
                </thead>
                <tbody>
                    {error ? (
                        <tr>
                            <td colSpan={COLUMN_COUNT} className="error-row">{error}</td>
                        </tr>
                    ) : books.length > 0 ? (
                        books.map((book) => (
                            <tr key={book.id}>
                                <td>{book.title}</td>
                                <td>{book.author}</td>
                                <td>{book.isbn}</td>
                                <td>{formatPrice(book.price)}</td>
                                <td>{book.publishDate ?? "-"}</td>
                                <td>{book.bookDetail?.publisher ?? "-"}</td>
                                <td>
                                    <button type="button" className="edit-btn" onClick={() => onEdit(book.id)}>
                                        수정
                                    </button>
                                    <button type="button" className="delete-btn" onClick={() => onDelete(book.id)}>
                                        삭제
                                    </button>
                                    <button type="button" className="detail-btn" onClick={() => onDetail(book.id)}>
                                        상세
                                    </button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={COLUMN_COUNT} className="empty-row">등록된 도서가 없습니다.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
}
