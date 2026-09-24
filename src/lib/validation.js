/* ---------------------------------------------------------
   유효성 검사 — 화면도 서버도 모르는 순수 함수
   form10.js 의 validateBook, isValidUrl 이 여기로 왔습니다.

   달라진 점은 하나입니다. alert 을 띄우지 않고
   "무엇이 잘못됐는지" 메시지만 돌려줍니다.
   그것을 어떻게 보여줄지는 부르는 쪽이 정합니다.
   덕분에 나중에 alert 대신 화면 안 메시지로 바꿀 때
   이 파일은 한 줄도 고치지 않아도 됩니다.
   --------------------------------------------------------- */

// ISBN: 숫자와 X, 하이픈만 한 개 이상(+)
//   ^        문자열의 시작
//   [0-9X-]  숫자 또는 X 또는 하이픈
//            하이픈은 맨 뒤에 두어야 "범위"가 아니라 글자로 읽힌다
//   +        앞의 것이 한 번 이상
//   $        문자열의 끝
//   /i       대소문자를 가리지 않는다 (소문자 x 도 통과)
const ISBN_PATTERN = /^[0-9X-]+$/i;

/* URL 형식인지 확인한다. 정규식으로 URL 을 판별하기는 어려우므로
   브라우저가 가진 URL 객체를 대신 쓴다. 형식이 틀리면 오류를 던지므로
   그 오류를 잡아 false 를 돌려준다.

   이 함수는 이 파일 안에서만 쓰므로 export 하지 않는다.
   밖으로 내보내는 것은 validateBook 하나뿐이다. */
function isValidUrl(value) {
    try {
        new URL(value);
        return true;
    } catch {
        // catch (error) 에서 error 를 쓰지 않으면 괄호를 생략할 수 있다.
        // form10.js 의 catch (_) 가 이렇게 짧아졌다.
        return false;
    }
}

/* 문제가 없으면 null, 있으면 첫 번째 오류 메시지를 돌려준다.
   form10.js 에서는 여기서 alert 을 띄우고 true / false 를 돌려주었다. */
export function validateBook(book) {
    // 구조 분해 — 객체에서 필요한 값만 이름 그대로 꺼낸다.
    // const title = book.title; 을 네 번 쓰는 것과 같다.
    //
    // bookDetail = {} 는 기본값이다. bookDetail 이 없는 객체가
    // 들어와도 빈 객체로 대신해 "Cannot destructure property" 를 막는다.
    const { title, author, isbn, price, bookDetail = {} } = book;
    const { pageCount, coverImageUrl } = bookDetail;

    // 문제를 찾으면 그 자리에서 바로 돌려주고 끝낸다(early return).
    // 아래로 갈수록 조건이 중첩되지 않아 읽기 쉽다.
    if (!title) return "제목을 입력해주세요.";
    if (!author) return "저자를 입력해주세요.";

    // || 는 왼쪽이 거짓이면 오른쪽을 검사한다.
    // !isbn 이 먼저 걸러 주므로 안쪽 test() 가 안전하다.
    if (!isbn || !ISBN_PATTERN.test(isbn)) {
        return "ISBN 을 입력하지 않거나 올바른 형식이 아닙니다. (숫자와 X, - 만 허용)";
    }

    // 가격과 페이지 수는 선택 항목이라 비워 두면 null 이 온다.
    // null < 0 은 false 라 그냥 두어도 되지만, "비어 있음" 과 "잘못된 값" 을
    // 구분해 읽히도록 null 검사를 따로 적는다.
    if (price !== null && price < 0) {
        return "가격은 0 이상이어야 합니다.";
    }

    if (pageCount !== null && pageCount < 0) {
        return "페이지 수는 0 이상이어야 합니다.";
    }

    // 표지 이미지 URL 은 입력했을 때만 형식을 본다.
    if (coverImageUrl && !isValidUrl(coverImageUrl)) {
        return "올바른 이미지 URL 형식이 아닙니다. (예: https://example.com/cover.jpg)";
    }

    // 여기까지 왔으면 문제가 없다는 뜻이다.
    return null;
}
