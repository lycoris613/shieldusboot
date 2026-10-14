# PART 1. HTML

## 1-0. 저장소 개요

```
frontendhcj/
├─ index.html             전체 예제 목차
├─ assets/                공용 자원
│  base.css               모든 예제 페이지의 공통 스타일
│  demo.js                out() / outClear() — 화면에 결과를 찍는 헬퍼
│  sample.svg             예제용 이미지
│
├─ 01_html/               ← PART 1. 8개 장
│  ch01_tag.html          태그·요소·속성·중첩
│  ch02_structure.html    문서 기본 구조 (+ ch02_minimal.html, css/ch02.css)
│  ch03_text.html         텍스트 태그, 블록 vs 인라인
│  ch04_list_table.html   ul/ol/dl, table
│  ch05_image_link.html   img, a
│  ch06_form.html         form, input, select, textarea, button
│  ch07_div_class.html    div/span, class/id
│  ch08_semantic.html     header/nav/main/section/article/aside/footer
│
├─ 02_css/ 02_css_display/    ← PART 2
├─ 03_js/                     ← PART 3
├─ 04_es6/                    ← PART 4
├─ 05_react/                  ← PART 5
├─ portfolio/                 1부 실습 완성본
├─ student_js/                3부 실습 완성본
├─ student_ecma/              4부 실습 완성본 (Vite)
├─ todolist_react/            5부 부록 E — 첫 React 앱
├─ student_react_first/       5부 실습 완성본
├─ todolist_react_router/          6부 준비 운동 1 (라우터만)
├─ todolist_react_router_zustand/  6부 준비 운동 2 (store 만)
├─ student_react_router_axios/     6부 실습 6-1 ~ 6-10
└─ student_react_router_axios_zustand/  6부 실습 6-11 ~ 6-14
```

- 각 장은 **설명이 붙은 `chNN_*.html`** 과 **코드만 남긴 `chNN_*_only.html`** 두 벌이다.
  설명본은 브라우저로 보고, `_only` 는 **코드를 읽을 때** 본다. 이 문서는 주로 `_only` 쪽을 인용한다.
- 빌드 도구 없음. **VS Code + Live Server 로 여는 것이 실행 방법의 전부다.**
- ⚠️ 파일을 더블클릭해 `file:///` 로 열면 3부의 `fetch` 와 4부의 `import` 가 브라우저에 막힌다. 반드시 `http://127.0.0.1:5500/...` 으로 열 것.

---

## 1-1. HTML이란 무엇인가 — 태그 · 요소 · 속성 · 중첩

### 개념

웹페이지 하나는 역할이 다른 세 언어로 만들어진다. 서로를 대신할 수 없다.

| 언어 | 역할 | 비유 |
|---|---|---|
| **HTML** | 구조 — 무엇이 어디에 있는가 | 건물의 뼈대와 벽 |
| **CSS** | 표현 — 어떻게 보이는가 | 페인트와 인테리어 |
| **JavaScript** | 동작 — 무엇이 움직이는가 | 전기 배선과 스위치 |

**CSS가 아무리 화려해도 HTML에 없는 내용은 화면에 나타날 수 없다.** 그래서 항상 HTML을 먼저 만든다.

**용어 4개**

```
<p class="intro">안녕하세요</p>
 |   |            |          |
 |   |            |          +-- 닫는 태그
 |   |            +------------- 내용(content)
 |   +-------------------------- 속성(attribute)  이름="값", 여는 태그 안에만
 +------------------------------ 여는 태그
```

- **요소(element)** = 여는 태그부터 닫는 태그까지 전체
- **빈 요소** = 내용이 없어 닫는 태그가 없는 태그 : `<br />` `<hr />` `<img>` `<input>` `<meta>` `<link>`
- 끝의 슬래시는 HTML5에서 없어도 된다. 교재는 붙이는 쪽으로 통일.

**중첩 규칙은 하나뿐이다 — 나중에 연 태그를 먼저 닫는다.**

```html
<p><strong>굵게</strong></p>   (O)
<p><strong>굵게</p></strong>   (X) 순서가 엇갈림
```

들여쓰기는 브라우저가 무시한다. 그래도 하는 이유는 **닫는 태그를 빠뜨렸을 때 눈으로 찾기 위해서**다.

### 코드 위치

`01_html/ch01_tag_only.html`

```html
<!-- 태그의 생김새 -->
<p class="intro">안녕하세요</p>

<!-- 빈 요소 — 닫는 태그가 없는 태그 -->
첫 줄<br />
br 로 줄을 바꿨습니다.
<hr />

<!-- 중첩 — 태그 안에 태그 -->
<div class="card">     <!-- 부모 -->
  <h3>프로젝트 제목</h3>  <!-- 자식 -->
  <p>설명 문구입니다.</p> <!-- 자식 -->
</div>

<!-- 중첩 규칙 — 나중에 연 태그를 먼저 닫는다 -->
<p><strong>굵게</strong></p>
<!-- 아래처럼 순서가 엇갈리면 안 됩니다.
     <p><strong>굵게</p></strong> -->
```

**해설**
- `<div class="card">` 의 부모-자식 관계가 그대로 CSS 선택자(`.card h3`)와 JavaScript 탐색(`querySelector`)의 길이 된다. **중첩 구조를 정확히 잡는 것이 뒤의 모든 것을 결정한다.**
- 🔗 이 `card` 구조가 2부 실습(2-4)의 프로젝트 카드로, 3부의 `.form-container` 로 그대로 이어진다.

⚠️ **자주 나는 오류**
- 화면에 태그가 **글자 그대로** 보인다 → 꺾쇠를 전각으로 입력했거나 확장자가 `.txt` 다.
- 일부 내용이 **통째로 사라진다** → 닫는 태그를 빠뜨려 뒤 내용이 그 태그 안으로 빨려 들어갔다. F12 → Elements 에서 실제 구조를 볼 것.

---

## 1-2. 문서의 기본 구조 — doctype · head · body · 경로

### 개념

| 구성 | 역할 | 빠뜨리면 |
|---|---|---|
| `<!doctype html>` | 최신 HTML로 해석하라는 표시 | 브라우저가 옛날 방식으로 동작해 레이아웃이 어긋남 |
| `<html lang="ko">` | 문서 언어 | 화면 낭독기가 엉뚱한 발음으로 읽음 |
| `<head>` | 화면에 보이지 않는 정보 | — |
| `<meta charset="UTF-8">` | 글자 인코딩 | **한글이 ??? 로 깨짐** |
| `<meta name="viewport">` | 모바일에서 실제 크기로 표시 | 휴대폰에서 글자가 깨알만 해짐 |
| `<title>` | 탭 제목 | 탭에 파일 경로가 그대로 |
| `<link rel="stylesheet">` | 외부 CSS 연결 | 스타일이 하나도 안 먹음 |
| `<body>` | 실제로 보이는 내용 | 화면이 빔 |

**경로(상대 경로)** — 지금 이 HTML 파일 위치 기준으로 길을 알려준다.

```
css_basic/
|- portfolio01.html   <-- 지금 이 파일
|- css/
   |- style01.css

href="css/style01.css"     같은 폴더의 css 폴더 안으로
href="./css/style01.css"   위와 완전히 같은 뜻 (./ 는 현재 폴더)
href="../style.css"        한 단계 위 폴더로
href="/css/style01.css"    사이트 최상위부터 (실습에서는 잘 안 씀)
```

### 코드 위치

`01_html/ch02_structure_only.html` — 주석으로 각 줄의 이유를 달아 둔 뼈대

```html
<!doctype html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="description" content="HTML 문서의 기본 뼈대 예제" />
    <title>2장. 문서의 기본 구조</title>
    <link rel="stylesheet" href="css/ch02.css" />   <!-- 상대 경로 -->
  </head>
  <body>
    <h1>여기부터 화면에 보입니다</h1>

    <p><a href="ch03_text.html">./ch03_text.html — 같은 폴더</a></p>
    <p><a href="../index.html">../index.html — 한 단계 위 폴더</a></p>
    <p><img src="../assets/sample.svg" alt="../assets 폴더의 이미지" width="120" /></p>
  </body>
</html>
```

`01_html/ch02_minimal.html` + `01_html/css/ch02.css` — **실습 1-1과 똑같은 최소 구성**

```css
/* 실습 1-1 과 같은 최소 CSS */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}
body {
    padding: 40px;
    background-color: #f4f4f4;
    font-family: sans-serif;
    line-height: 1.7;
}
```

**해설**
- 이 세 줄짜리 `*` 규칙이 **실습 1-1의 첫 코드**이자 실무 프로젝트의 첫 코드다. 이유는 2-3절(박스 모델)에서 설명한다.
- 🔗 `<link>` 하나로 시작한 CSS 연결 방식이 **실습 3-2에서 내부 `<style>`** 로, **실습 3-3에서 다시 외부 파일**로 오가며 왜 외부 방식이 표준인지를 체감하게 한다. 그리고 4부 Vite 에서는 `<link>` 조차 사라지고 `import "./style.css"` 가 된다(4-1절).

⚠️ **자주 나는 오류**
- CSS가 전혀 적용되지 않는다 → **열에 아홉은 경로 문제.** F12 → Network 탭에서 `.css` 가 404 인지 확인.
- 폴더 이름 대소문자를 틀렸다 → 내 PC에서는 되는데 서버에 올리면 깨진다. `CSS` 와 `css` 를 다르게 취급하는 환경이 많다.

---

## 1-3. 텍스트를 다루는 태그 — 그리고 블록 vs 인라인

### 개념

`h1`~`h6` 은 **글자 크기를 정하는 태그가 아니라 문서의 목차 단계**를 나타내는 태그다. 크기는 CSS로, 태그는 의미로 고른다. 한 페이지에 `h1` 은 하나가 원칙이다.

HTML은 **여러 칸의 공백과 줄바꿈을 전부 한 칸 공백으로 합쳐 버린다.** 엔터를 아무리 눌러도 줄이 안 바뀐다 → `<br />` 을 쓴다.

| 태그 | 의미 | 기본 모양 |
|---|---|---|
| `<strong>` | 중요하다는 의미의 강조 | 굵게 |
| `<em>` | 말의 어조를 강조 | 기울임 |
| `<b>` / `<i>` | 의미 없이 굵게 / 기울임만 | 같음 |
| `<span>` | 의미 없음. CSS를 붙이기 위한 껍데기 | 변화 없음 |
| `<small>` | 부가 정보, 저작권 문구 | 작게 |
| `<pre>` | 공백과 줄바꿈을 그대로 | 고정폭 |

`<b>` 와 `<strong>` 은 똑같이 굵게 보이지만 **화면 낭독기는 `<strong>` 만 힘주어 읽는다.** 이유가 없으면 `<strong>`.

**블록 요소와 인라인 요소 — 꼭 이해할 것**

| 구분 | 블록(block) | 인라인(inline) |
|---|---|---|
| 줄 차지 | 가로 한 줄을 통째로 | 내용 크기만큼만 |
| 배치 | 위에서 아래로 쌓임 | 옆으로 나란히 흐름 |
| width/height | 지정할 수 있음 | **지정해도 무시됨** |
| 위아래 margin | 적용됨 | **적용되지 않음** |
| 대표 태그 | div, p, h1~h6, ul, li, section, header | span, a, strong, em, img, input, button |

### 코드 위치

`01_html/ch03_text_only.html`

```html
<p>엔터만 누른 경우 :
  여기서
  엔터를
  여러 번 눌렀지만 한 줄로 이어집니다.</p>

<p>br 을 쓴 경우 :<br />이렇게 줄이 바뀝니다.</p>

<p>
  이것은 <strong>strong (중요)</strong> 이고,
  이것은 <em>em (어조 강조)</em> 이며,
  이것은 <b>b (굵게만)</b> 이고,
  이것은 <i>i (기울임만)</i> 입니다.
</p>

<pre>pre 는 공백과 줄바꿈을
그대로     보여 줍니다.</pre>

<!-- 블록 : 세로로 쌓인다 -->
<div>div 1 — 블록</div>
<div>div 2 — 블록</div>
<div>div 3 — 블록</div>

<!-- 인라인 : 흐름을 끊지 않는다 -->
<p>
  문장 속의 <span>span</span> 은 흐름을 끊지 않습니다.
  <span>A</span><span>B</span>
</p>
```

**해설**
- 마지막 두 덩어리가 이 장의 전부다. **div 세 개는 세로로, span 두 개는 옆으로 붙는다.** CSS를 한 줄도 안 썼는데 다르다는 것은, 태그마다 타고난 성질이 있다는 뜻이다.
- 🔗 실습 1-2에서 로고(`h1`)와 메뉴(`nav`)가 세로로 쌓여 있다가 `display: flex` 한 줄에 가로로 나란해지는 장면이 바로 이 성질을 뒤집는 것이다. → 2-4절.

⚠️ **"width를 줬는데 왜 안 먹히지?"의 90%가 인라인 요소에 준 경우다.** `<a>` 를 버튼처럼 만들려다 막히는 것이 대표적. `display: inline-block` 으로 바꾸면 된다(2-4절).

---

## 1-4. 목록과 표 — 3부에서 학생 목록을 만드는 구조

### 개념

- `ul` (순서 없음, 점) / `ol` (순서 있음, 번호) — **자식은 반드시 `li` 여야 한다.** `ul` 바로 안에 `div` 나 `p` 를 넣으면 문법 위반. 반대로 `li` 안에는 무엇이든 넣을 수 있다(중첩 목록).
- `dl` / `dt` / `dd` — 설명 목록(용어 + 뜻).

**표(table)** — 3부의 핵심 구조다.

| 태그 | 뜻 | 역할 |
|---|---|---|
| `table` | 표 전체 | 바깥 테두리 |
| `thead` | table head | 머리글 행 묶음. **내용이 바뀌지 않음** |
| `tbody` | table body | 데이터 행 묶음. **JavaScript가 여기만 갈아 끼움** |
| `tr` | table row | 가로 한 줄 |
| `th` | header cell | 머리글 칸. 굵게 가운데 정렬 |
| `td` | data cell | 데이터 칸 |
| `colspan` | — | 한 칸이 여러 칸을 차지 |

### 코드 위치

`01_html/ch04_list_table_only.html`

```html
<table border="1">
  <caption>학생 목록</caption>

  <thead>
    <tr><th>이름</th><th>학번</th><th>주소</th><th>액션</th></tr>
  </thead>

  <tbody>
    <tr>
      <td>홍길동</td><td>20241234</td><td>서울시 강남구</td>
      <td><button type="button">수정</button></td>
    </tr>

    <!-- colspan — 이 칸 하나가 가로 4칸을 차지한다 -->
    <tr>
      <td colspan="4">등록된 학생이 없습니다.</td>
    </tr>
  </tbody>
</table>
```

**해설**
- 실습 3-4에서 `tbody` 에만 `id="studentTableBody"` 를 붙이는 이유가 여기 있다. **머리글은 그대로 두고 데이터 행만 통째로 바꾸면 되기 때문**이다.
- `colspan` 은 실습 3-11의 "데이터를 불러올 수 없습니다" 안내를 표 전체 너비에 걸쳐 보여줄 때 쓴다.
- 🔗 4부 `studentTable.js` 의 `COLUMN_COUNT = 7` 과 `cell.colSpan = COLUMN_COUNT`, 5부 `StudentTable.jsx` 의 `<td colSpan={COLUMN_COUNT}>` 가 전부 이 속성이다. 열 개수를 상수로 뽑아 둔 이유는 **열이 늘어날 때 한 곳만 고치기 위해서**다.

⚠️ `colspan` 값이 실제 열 개수와 다르면 안내 문구가 잘리거나 빈 칸이 남는다.

---

## 1-5. 이미지와 링크

### 개념

```html
<img src="images/profile.jpg" alt="프로필 사진" />
```

- `src` — 파일 경로. 1-2절의 상대 경로 규칙 그대로.
- `alt` — **이미지를 못 볼 때 대신 읽히는 글자.** 생략하면 안 된다. 장식용이라 읽을 필요가 없으면 `alt=""` 로 **비워 둔다**(빼는 것이 아니다).

| 형태 | 동작 |
|---|---|
| `href="projects.html"` | 같은 폴더의 다른 페이지 |
| `href="https://..."` | 외부 사이트 |
| `href="#skills"` | 같은 페이지 안 `id="skills"` 로 스크롤 |
| `target="_blank"` | 새 탭 (`rel="noopener"` 를 함께) |

### 코드 위치

`01_html/ch05_image_link_only.html`

```html
<!-- src 가 틀리면 alt 글자가 대신 보입니다 -->
<img src="없는파일.svg" alt="이 글자가 alt 입니다 — 이미지가 없으면 이게 보입니다" />

<!-- 장식용이라 읽을 필요가 없으면 alt 를 빈 값으로 둡니다 -->
<img src="../assets/sample.svg" alt="" />

<p><a href="https://github.com/mysoyul/Html_CSS_JS_App" target="_blank" rel="noopener">
  외부 사이트를 새 탭에서 열기</a></p>

<p><a href="#bottom">같은 페이지 안의 맨 아래로 이동</a></p>
...
<h2 id="bottom">여기가 #bottom 입니다</h2>
```

`portfolio/portfolio.html` — 실습 1-6

```html
<nav>
  <span>Home</span>
  <span><a href="projects.html">MyProjects</a></span>
  <span>Contact</span>
</nav>
```

**해설**
- `a` 는 **인라인 요소**라 기본적으로 파란 글씨에 밑줄이 붙는다. 실습 1-6에서 `text-decoration: none` 으로 밑줄을 없애고 hover 를 주는 대상이 이것이다(2-11절).
- 실습에서 사진 대신 회색 상자(`div.photo`, `div.card-image`)를 쓴 이유는 실습 파일을 가볍게 유지하려는 것. 실제 사진을 넣으려면 그 `div` 를 `<img>` 로 바꾸고 `width:100%; height:100%; object-fit: cover;` 를 준다.

---

## 1-6. 폼(form)과 입력 — 3부의 핵심

### 개념

**여기가 PART 1에서 가장 중요한 절이다. 3부 전체가 폼 위에서 벌어진다.**

| type | 쓰임 | 얻는 것 |
|---|---|---|
| text | 이름, 학번 | 가장 기본 |
| email | 이메일 | 모바일에서 @ 키보드, 형식 검사 |
| date | 날짜 | 달력 UI. 값은 `2000-01-01` 형식 |
| number | 나이, 수량 | 위아래 화살표, 숫자만 |
| password | 비밀번호 | 점으로 가려짐 |
| checkbox | 여러 개 선택 | `checked` |
| radio | 여러 개 중 하나 | **같은 `name` 끼리 한 묶음** |
| hidden | 안 보이는 값 | id 등을 몰래 담아 보낼 때 |

**`id` 와 `name` — 둘 다 필요한 이유 (3부 최다 실수)**

| 속성 | 무엇을 위한 이름인가 | 누가 쓰는가 |
|---|---|---|
| `id="name"` | 문서 안에서 이 요소 하나를 특정 | CSS 선택자, `getElementById`, `label` 의 `for` |
| `name="name"` | **전송되는 데이터의 키(key)** | `FormData`, 서버 요청, `studentForm.name` |

⚠️ **`id` 만 넣고 `name` 을 빠뜨리면 화면은 멀쩡하다.** 그런데 실습 3-5에서 `FormData` 로 값을 모으는 순간 그 입력창만 통째로 빠지고, `formData.get("address")` 가 `null` 을 돌려주고, 이어지는 `.trim()` 에서 터진다. → `Cannot read properties of null (reading 'trim')`

**브라우저가 대신 해주는 검사**

| 속성 | 역할 |
|---|---|
| `placeholder` | 비어 있을 때 흐린 안내 문구. **실제 값이 아님** |
| `required` | 비어 있으면 제출을 막고 안내 풍선 |
| `pattern` | 정규식으로 형태 검사 |
| `readonly` | 수정은 막되 값은 전송됨 |
| `disabled` | 값이 **아예 전송되지 않음** |

⚠️ 이런 검사는 **보안 수단이 될 수 없다.** 개발자 도구로 속성을 지우면 그만이다. 그래서 실습 3-5에서 JavaScript로 한 번 더 검사하고, 최종 검증은 서버에서 다시 한다.
**"브라우저 검사는 사용자를 돕기 위한 것이지, 나쁜 요청을 막기 위한 것이 아니다."**

**`button` 의 type — 빠뜨리면 반드시 사고가 난다**

| type | 동작 | 언제 |
|---|---|---|
| `submit` | 폼을 제출 (**생략 시 기본값**) | 등록·저장 버튼 |
| `button` | 아무 동작도 하지 않음 | 취소 버튼, JS 전용 |
| `reset` | 입력값을 전부 지움 | 거의 안 씀 |

### 코드 위치

`01_html/ch06_form_only.html` — 모든 type을 한 화면에 모아 둔 견본

```html
<form id="studentForm">
  <div>
    <label for="name">이름:</label><br />
    <input type="text" name="name" id="name" required />
  </div>

  <!-- pattern — 이 형식이 아니면 브라우저가 막는다 -->
  <div>
    <label for="phoneNumber">전화번호:</label><br />
    <input type="tel" name="phoneNumber" id="phoneNumber"
           placeholder="010-1234-5678"
           pattern="[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}" />
  </div>

  <!-- radio — name 이 같은 것 중 하나만 골라진다 -->
  <label><input type="radio" name="type" value="online" /> 온라인</label>
  <label><input type="radio" name="type" value="offline" /> 오프라인</label>

  <div>
    <button type="submit">학생 등록</button>
    <button type="button">취소</button>
    <button type="reset">초기화</button>
  </div>
</form>
```

`student_js/student.html` — **실습 3-1~3-3의 완성본.** 위 견본이 실제 화면이 된 모습

```html
<form id="studentForm">
  <div class="form-grid">
    <div class="form-group">
      <label for="name">이름:</label>
      <input type="text" name="name" id="name" />
    </div>
    <div class="form-group">
      <label for="studentNumber">학번:</label>
      <input type="text" name="studentNumber" id="studentNumber" placeholder="CS001" />
    </div>
    <div class="form-group">
      <label for="phoneNumber">전화번호:</label>
      <input type="text" name="phoneNumber" id="phoneNumber" placeholder="00*-000*-0000"
             pattern="[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}" />
    </div>
    <div class="form-group">
      <label for="email">이메일:</label>
      <input type="email" name="email" id="email" placeholder="***@***.***" />
    </div>
    <div class="form-group">
      <label for="dateOfBirth">생년월일:</label>
      <input type="date" name="dateOfBirth" id="dateOfBirth" />
    </div>
  </div>

  <div class="button-group">
    <button type="submit">학생 등록</button>
    <button type="button" class="cancel-btn" id="cancelButton" style="display: none;">취소</button>
    <span class="error-message" id="formError"></span>
  </div>
</form>
```

**해설**
- 입력 6개가 전부 `name` 과 `id` 를 **같은 값으로** 갖고 있다. 같아도 되고 역할은 다르다는 것이 핵심.
- 취소 버튼에 `type="button"` 과 `style="display:none"` 이 둘 다 붙어 있다. `type` 을 빠뜨리면 **취소를 눌렀는데 폼이 제출된다.** `display:none` 은 실습 3-9에서 수정 모드로 들어갈 때 `inline-block` 으로 바뀐다.
- `<span class="error-message" id="formError">` 가 3부 내내 메시지가 뜨는 자리다. → 3-9절, 4-9절, 5-7절로 이어진다.
- 🔗 이 폼의 여섯 칸 이름이 **그대로** 4부 `collectStudentData()` 의 구조 분해 변수명, 5부 `StudentForm.jsx` 의 `FIELDS` 배열, 그리고 백엔드 `StudentRequest` DTO 의 필드명이 된다. **한 글자라도 다르면 서버에서 값이 전부 null 이 된다.**

### select 와 textarea — 본 실습에는 없지만 폼을 확장할 때 가장 먼저 쓰는 태그

```html
<select name="grade" id="grade">
  <option value="">선택하세요</option>
  <option value="1">1학년</option>
  <option value="2">2학년</option>
</select>

<textarea name="memo" id="memo" rows="4"></textarea>
```

| | 서버로 보내는 값 | `input` 과 다른 점 |
|---|---|---|
| `select` | 선택된 `option` 의 `value` | 여러 선택지 중 하나(또는 `multiple`로 여러 개) |
| `textarea` | 여는 태그와 닫는 태그 **사이**의 글자 | `value` 속성을 쓰지 않는다 |

⚠️ **`textarea`는 `<textarea value="...">` 처럼 쓰지 않는다.** `<textarea>여기에 값</textarea>` 처럼 **태그 사이**에 넣는다. `input`에 익숙해지면 자주 하는 실수.

---

## 1-7. div · span 과 class · id

### 개념

`div` 와 `span` 은 **그 자체로 아무 의미도, 아무 모양도 없다.** 오직 CSS를 붙이거나 여러 요소를 묶기 위해 존재한다.

| | class | id |
|---|---|---|
| 중복 | 같은 이름을 여러 요소가 함께 | **문서 전체에서 단 하나** |
| CSS 선택자 | `.card` (점) | `#studentForm` (샵) |
| 주 용도 | 같은 모양을 여러 개에 | JavaScript로 하나를 콕 집을 때 |

**이름 짓기** — 영문 소문자와 하이픈. "어떻게 보이는가"가 아니라 **"무엇인가"**로.

```
class="card-image"     (O) 무엇인지를 말함
class="blue-box"       (△) 색을 바꾸면 이름이 거짓말이 됨
class="카드"            (X) 한글
class="card image"     (X) 공백은 두 개의 클래스로 해석됨 → 선택자는 .card.image
```

### 코드 위치

`01_html/ch07_div_class_only.html` 마지막 블록이 **3부에서 쓰는 실제 구조를 미리 보여준다.**

```html
<div class="table-container">
  <h2>학생 목록</h2>
  <div id="loadingMessage" class="loading">로딩 중...</div>
  <table id="studentTable">
    <tbody id="studentTableBody"></tbody>
  </table>
</div>
```

**해설**
- 카드 3개가 모두 `class="card"` 인 것과, 폼에 `id="studentForm"` 을 하나만 붙이는 것이 각각의 전형적인 사용 예다.
- ⚠️ **같은 id를 두 곳에 쓰면 `getElementById` 는 첫 번째 것만 찾아내고, 두 번째 요소는 영원히 잡히지 않는다.**

---

## 1-8. 시맨틱 태그와 접근성

### 개념

`div` 만으로도 모든 화면을 만들 수 있다. 하지만 그러면 어디가 머리이고 어디가 본문인지 알 수 없다. 시맨틱 태그는 **모양은 `div` 와 똑같지만 이름에 의미가 담긴** 태그다.

| 태그 | 뜻 | 실습에서 |
|---|---|---|
| `header` | 머리 부분 | 1-2 상단 바 |
| `nav` | 내비게이션 묶음 | 1-2, 2-2 |
| `main` | 주요 내용 (**한 번만**) | 1-4, 2-3 |
| `section` | 주제가 있는 한 덩어리 | 1-3 히어로, 1-5 기술 스택 |
| `article` | 그 자체로 완결된 콘텐츠 | — |
| `aside` | 곁다리 정보 | — |
| `footer` | 바닥 | 1-6, 2-8 |

**두 코드는 화면상 완전히 같다. 차이는 사람과 기계가 읽을 때 생긴다.** 검색 엔진은 `main` 안을 본문으로 취급하고, 화면 낭독기 사용자는 `nav` 나 `main` 으로 바로 건너뛴다.

**접근성 — 최소한 이것만은 (추가 비용이 거의 없다)**
1. 모든 `img` 에 `alt`. 장식용이면 `alt=""`.
2. 모든 `input` 에 짝이 되는 `label`.
3. `html` 태그에 `lang="ko"`.
4. 제목은 `h1 → h2 → h3` 순서로. 건너뛰지 않는다.
5. **색만으로 정보를 전달하지 않는다.** 빨간 글씨에는 문구도 함께.

### 코드 위치

`01_html/ch08_semantic_only.html`

```html
<header>
  header — 머리 부분
  <nav>
    <ul>
      <li><a href="#intro">소개</a></li>
      <li><a href="#projects">프로젝트</a></li>
    </ul>
  </nav>
</header>

<main>                      <!-- 한 문서에 한 번만 -->
  <section id="intro">
    <h2>section — 주제가 있는 덩어리</h2>
  </section>
  <section id="projects">
    <article>
      <h3>article — 그것만 떼어내도 말이 되는 조각</h3>
    </article>
  </section>
  <aside>aside — 곁다리 정보</aside>
</main>

<footer>footer — 바닥. 저작권, 연락처 등</footer>

<!-- label 이 연결된 경우 — 글자를 클릭해도 커서가 간다 -->
<label for="good">이름 (label 연결됨):</label>
<input type="text" id="good" name="good" />

<!-- label 로 감싸는 방법도 있다. 이때는 for 가 없어도 된다 -->
<label>이메일: <input type="email" name="email" /></label>
```

`portfolio/portfolio.html` — 실습 1부 완성본의 뼈대

```html
<header class="header"> ... </header>
<section class="hero"> ... </section>
<main class="container"> ... </main>
<section class="skills"> ... </section>
<footer class="footer"> ... </footer>
```

**해설**
- `label` 을 다는 방법이 두 가지다. `for` + `id` 짝을 맞추거나, `label` 로 `input` 을 **감싸거나**. 후자는 `for` 가 필요 없다.
- ⚠️ 라벨을 눌러도 커서가 안 간다 → `for` 와 `id` 값이 **한 글자라도** 다르다.

---

## 1-9. HTML 파트 자가 점검 질문

각 질문에 코드로 답할 수 있으면 이 파트는 정리된 것이다.

**구조**
1. `<meta charset="UTF-8">` 을 빼면 무슨 일이 일어나나? → `ch02_structure_only.html`
2. `href="css/a.css"` 와 `href="../css/a.css"` 의 차이는? 기준점은 무엇인가?
3. `head` 에 글자를 쓰면 왜 화면에 안 보이나?

**텍스트·배치**
4. `<p>` 안에서 엔터를 세 번 쳤는데 화면이 그대로다. 왜인가?
5. `<span>` 에 `width: 200px` 을 줬는데 안 먹는다. 이유와 두 가지 해결책은? → `ch03_text_only.html`
6. `<b>` 대신 `<strong>` 을 쓰라는 이유는?

**표·폼 (3부 직결)**
7. 데이터를 `table` 이 아니라 `tbody` 에 그리는 이유는? → `ch04_list_table_only.html`
8. `id` 와 `name` 은 각각 누가 쓰는가? 하나를 빼면 어느 시점에 어떤 오류가 나는가?
9. 취소 버튼에 `type="button"` 을 안 붙이면 무슨 일이 생기나?
10. 라디오 버튼 세 개 중 하나만 선택되게 하려면 무엇을 같게 해야 하나?
11. `required` 와 `pattern` 만으로 검증을 끝내면 안 되는 이유는?
12. `disabled` 와 `readonly` 중 값이 서버로 전송되는 쪽은?

**class·시맨틱**
13. `class="card image"` 로 쓰고 `.card image` 로 선택했다. 무엇이 잘못됐나?
14. 같은 `id` 를 두 요소에 붙이면 `getElementById` 는 어떻게 동작하나?
15. `<div class="header">` 와 `<header>` 는 화면이 같은데 왜 후자를 쓰나?
