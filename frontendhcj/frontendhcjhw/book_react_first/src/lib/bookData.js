/* ---------------------------------------------------------
   폼 값 다루기 — 값을 옮기는 두 함수
   ui/bookForm.js 에서 document 를 쓰지 않는 부분만 골라 옮겼습니다.
   FormData 대신 form 객체(state)를 주고받는다는 점만 다릅니다.
   --------------------------------------------------------- */

// 입력칸 11개의 빈 값. 폼을 처음 열 때도, 되돌릴 때도 이 값을 쓴다.
export const EMPTY_FORM = {
    title: "",
    author: "",
    isbn: "",
    price: "",
    publishDate: "",
    language: "",
    pageCount: "",
    publisher: "",
    coverImageUrl: "",
    edition: "",
    description: "",
};

// 숫자 칸(가격, 페이지 수)의 값을 숫자로 바꾼다. 비어 있으면 null 을 돌려준다.
function toNumberOrNull(value) {
    if (!value || !value.trim()) return null;
    return Number(value);
}

// form(state) 을 서버가 받는 구조로 바꾼다.
// 빈 문자열은 서버가 해석하지 못하므로 ?? 가 아니라 || 로 null 로 바꿔 보낸다.
export function toRequest(form) {
    return {
        title: form.title.trim(),
        author: form.author.trim(),
        isbn: form.isbn.trim(),
        price: toNumberOrNull(form.price),
        publishDate: form.publishDate || null,
        bookDetail: {
            description: form.description.trim(),
            language: form.language.trim(),
            pageCount: toNumberOrNull(form.pageCount),
            publisher: form.publisher.trim(),
            coverImageUrl: form.coverImageUrl.trim(),
            edition: form.edition.trim(),
        },
    };
}

// 서버에서 받은 도서 정보를 폼(state)에 넣을 수 있는 모양으로 바꾼다.
// input 의 value 에 undefined 나 null 을 넣으면 React 가 경고하므로
// 값이 없으면 반드시 빈 문자열로 바꿔 돌려준다.
export function toFormValues(book) {
    const { title, author, isbn, price, publishDate, bookDetail } = book;

    return {
        title,
        author,
        isbn,
        price: price ?? "",
        publishDate: publishDate ?? "",
        description: bookDetail?.description ?? "",
        language: bookDetail?.language ?? "",
        pageCount: bookDetail?.pageCount ?? "",
        publisher: bookDetail?.publisher ?? "",
        coverImageUrl: bookDetail?.coverImageUrl ?? "",
        edition: bookDetail?.edition ?? "",
    };
}
