# PART 2. CSS

## 2-0. 저장소 개요

```
02_css/                    ← 「CSS 기초」 12개 장. 장마다 HTML 1개 + CSS 1개
   ch01_three_ways   인라인 / 내부 / 외부 세 가지 방법
   ch02_selector     기본·관계·가상 클래스 선택자, 우선순위
   ch03_boxmodel     margin/border/padding/content, box-sizing
   ch04_display      block / inline / inline-block / none
   ch05_flexbox      justify-content, align-items, gap, direction, wrap
   ch06_grid         grid-template-columns, fr, auto-fit + minmax
   ch07_unit_color   px % rem em vw vh fr / 이름 16진수 rgb rgba
   ch08_text         font-family, line-height, 시각적 위계
   ch09_decoration   border-radius, box-shadow, overflow
   ch10_position     static relative absolute fixed sticky
   ch11_transition   transition 과 hover, transform
   ch12_responsive   viewport, 미디어 쿼리, auto-fit

02_css_display/            ← 「display 속성 완전 정복」 보조자료 7개 장 + 부록
   ch01_why_display  같은 코드인데 왜 다르게 보일까
   ch02_basic_four   기본 4형제와 inline 의 함정
   ch03_history      float 시절 → flex → grid
   ch04_flex         주축/교차축부터 실전 패턴 6가지까지
   ch05_grid         열/행 정의, fr, grid-template-areas
   ch06_flex_vs_grid 결정 흐름도
   ch07_related      box-sizing, overflow, position
   appendix          복사해서 바로 쓰는 레시피 · 점검표 (outline 디버깅 스위치 포함)

portfolio/css/style01.css  ← 1부 Portfolio 완성본 (실습 1-1 ~ 1-6)
student_js/css/form.css    ← 3부 학생 관리 화면 (실습 3-3)
```

- ⚠️ **같은 주제가 `02_css` 와 `02_css_display` 에 두 번 나온다.** 앞이 요약, 뒤가 심화다. Flex/Grid 가 흔들리면 뒤쪽을 본다.
- 이 파트에서 실제로 화면 하나를 만드는 데 쓰이는 속성은 **30개 남짓**이다. 부록 B에 전부 모아 두었다.

---

## 2-1. CSS를 붙이는 세 가지 방법

### 개념

| 방식 | 장점 | 단점 | 쓸 때 |
|---|---|---|---|
| **인라인** `style="..."` | 그 요소에만 확실히 적용 | 재사용 불가, 우선순위가 너무 높아 나중에 못 고침 | **JavaScript로 값을 바꿀 때만** |
| **내부** `<style>` | 파일 하나로 관리 | HTML이 길어지고 공유 불가 | 짧은 실습, 단일 페이지 |
| **외부** `<link>` | 여러 페이지가 공유, 역할 분리 | 파일이 늘어남 | **거의 모든 실무 상황** |

```
선택자 {
    속성: 값;      /* 세미콜론을 반드시 */
}
```

주석은 `/* 이렇게 */`. HTML의 `<!-- -->` 와 다르다.

### 코드 위치

`02_css/css/ch01_three_ways.css`

```css
/* 이 규칙은 외부 CSS 파일에서 적용됩니다. */
.external-demo {
    margin: 4px 0;
    color: #1f4e79;
    font-weight: bold;
}
```

**해설 — 세 방법이 실습에서 실제로 오가는 순서**
- 실습 3-2 : `<head>` 안 `<style>` 로 시작 (내부)
- 실습 3-3 : `css/form.css` 로 옮김 (외부) — **같은 화면을 두 방식으로 만들어 보며 왜 실무가 외부를 쓰는지 체감시키는 구성**
- 4부 실습 4-1 : `<link>` 조차 사라지고 `main.js` 안에서 `import "./style.css"` (Vite)
- 5부 : `App.jsx` 안에서 `import "./style.css"`

🔗 **인라인 방식은 완전히 사라지지 않는다.** JavaScript로 값을 바꾸는 자리에는 계속 남는다.
`cancelButton.style.display = "inline-block"` (3부) → `formError.style.color = COLORS[type]` (4부) → `style={{ color: COLORS[message.type] }}` (5부).

⚠️ **일부 스타일만 적용된다** → 세미콜론이나 중괄호를 하나 빠뜨리면 **그 지점부터 아래 규칙이 전부 무시된다.** 브라우저는 오류를 알려주지 않고 조용히 건너뛴다.

---

## 2-2. 선택자 — 요소를 골라내는 법

### 개념

| 선택자 | 고르는 대상 |
|---|---|
| `*` | 모든 요소 |
| `p` | 모든 p 태그 (타입 선택자) |
| `.card` | `class="card"` 인 모든 요소 |
| `#studentForm` | `id="studentForm"` 인 요소 하나 |
| `h1, h2` | h1 또는 h2 (그룹) |
| `.navbar h1` | `.navbar` 안의 **모든** h1 (자손) |
| `.navbar > h1` | `.navbar` 의 **바로 아래** 자식 h1 |

**가상 클래스** : `:hover` `:focus` `:active` `:first-child` `:last-child` `:nth-child(2n)`

**우선순위 — "CSS를 썼는데 왜 안 바뀌지?"**

| 순위 | 종류 | 예 |
|---|---|---|
| 1 (가장 셈) | `!important` | `color: red !important;` |
| 2 | 인라인 스타일 | `<p style="color:red">` |
| 3 | id 선택자 | `#studentForm` |
| 4 | class · 가상 클래스 · 속성 선택자 | `.card`, `:hover` |
| 5 (가장 약함) | 타입 선택자 | `p`, `div` |

**점수가 같으면 나중에 쓴 규칙이 이긴다.** 그래서 일반 규칙을 위에, 예외를 아래에 둔다.

### 코드 위치

`02_css/css/ch02_selector.css` — **공백 하나의 차이**를 실험으로 만들어 둔 파일

```css
/* --- 기본 선택자 : 우선순위 --- */
.sel-demo p   { color: #555; }      /* 타입 (가장 약함) */
.sel-demo .hi { color: #2e5496; }   /* 클래스 */
.sel-demo #only { color: #c0504d; } /* id (가장 셈) */

/* --- 관계 선택자 : 공백의 차이 ---
   공백은 자손 결합자다. 있고 없고가 대상을 바꾼다.
       .box-a:hover    공백 없음 → box-a "자신"이 hover
       .box-b :hover   공백 있음 → box-b "안의 자손"이 hover        */

/* .box-a 자신에게 마우스가 올라올 때 */
.box-a:hover { background-color: #e5f0f8; }

/* 공백에 주의. 실무에서는 .box-b .inner:hover 처럼 대상을 밝혀 적는다. */
.box-b :hover { background-color: #fceaea; }

/* --- 우선순위 실험 --- */
p.prio   { color: #2e5496; }   /* class */
#prio-id { color: #c0504d; }   /* id — 이쪽이 이깁니다 */
```

**해설**
- ⚠️ **`.card:hover` 와 `.card :hover` 를 헷갈리는 것이 가장 흔한 실수다.** 실습 2-7에서 "마우스를 올려도 아무 변화가 없다"는 문제의 단골 원인.
- `!important` 는 되도록 쓰지 마라. 한 번 쓰면 그 값을 이기기 위해 또 `!important` 를 쓰게 되고, 결국 **아무도 못 고치는 CSS**가 된다.

**✅ 디버깅 습관** — F12 → Elements 에서 요소를 클릭하면 오른쪽에 적용된 규칙 목록이 보인다. **진 규칙에는 취소선이 그어져 있다.** 특정 속성만 안 먹으면 취소선부터 찾는다.

---

## 2-3. 박스 모델 — 모든 것은 사각형이다

### 개념

```
+-- margin ------------------------+   <-- 다른 상자와의 거리 (투명)
|  +-- border --------------------+ |   <-- 테두리 선
|  |  +-- padding -------------+  | |   <-- 내용과 테두리 사이
|  |  |     content            |  | |   <-- 글자, 이미지
|  |  +------------------------+  | |
|  +------------------------------+ |
+----------------------------------+
```

| 층 | 역할 | 배경색이 칠해지나 |
|---|---|---|
| content | 실제 내용 | 칠해짐 |
| padding | 내용과 테두리 사이 | **칠해짐** |
| border | 테두리 선 | 선 자체의 색 |
| margin | 이웃과 벌리는 거리 | **칠해지지 않음 (투명)** |

**값 쓰는 네 가지 방법**

```css
padding: 20px;                /* 상하좌우 모두 */
padding: 20px 50px;           /* 상하 20, 좌우 50 */
padding: 10px 20px 30px;      /* 위 10, 좌우 20, 아래 30 */
padding: 10px 20px 30px 40px; /* 위 → 오른쪽 → 아래 → 왼쪽 (시계 방향) */
margin: 0 auto;               /* 좌우 auto → 가로 중앙 정렬 */
```

⚠️ `margin: 0 auto` 는 **`width` 나 `max-width` 가 정해져 있어야** 효과가 있다.

**box-sizing — 반드시 알아야 할 한 줄**

기본 상태에서 `width: 200px` 은 **content만 200px**이다. 여기에 padding 20, border 1을 주면 실제 차지하는 너비는 200 + 40 + 2 = **242px**이 된다.

| 값 | `width: 200px` 의 뜻 | padding을 주면 |
|---|---|---|
| `content-box` (기본) | 내용만 200px | **전체 너비가 늘어남** |
| `border-box` | 테두리까지 합쳐 200px | 내용이 안으로 줄어들 뿐 전체는 그대로 |

**브라우저 기본값 지우기** — 브라우저는 `body` 에 8px, `h1`/`p` 에 위아래 여백, `ul` 에 왼쪽 여백을 기본으로 넣는다. 안 지우면 헤더가 화면 끝에 붙지 않고 흰 줄이 보인다(실습 1-2의 대표 오류).

### 코드 위치

`02_css/css/ch03_boxmodel.css` — 네 겹을 **눈으로 볼 수 있게** 색을 칠해 둔 예제

```css
/* --- 네 겹 구조를 눈으로 --- */
.bm-outer { background-color: #fdf0e3; display: inline-block; } /* margin 영역이 드러나도록 */
.bm-box {
    margin: 20px;                   /* 바깥 여백 (연한 주황 부분) */
    border: 6px solid #e8a33d;      /* 테두리 */
    padding: 24px;                  /* 안쪽 여백 (연두) */
    background-color: #ffffff;      /* content 배경 */
    box-shadow: inset 0 0 0 24px #eaf5e6;   /* padding 영역만 색칠 */
}

/* --- box-sizing 비교 : 같은 width 220px 인데 실제 너비가 다르다 --- */
.sizing { width: 220px; padding: 24px; border: 4px solid #2e5496; }
.sizing.content-box { box-sizing: content-box; }  /* 기본값 : width 는 내용만 */
.sizing.border-box  { box-sizing: border-box; }   /* 테두리까지 합쳐 220px */

/* --- margin: 0 auto --- */
.centered {
    box-sizing: border-box;
    max-width: 320px;
    margin: 0 auto;
}
```

`portfolio/css/style01.css` 첫 블록 — **실습 1-1의 첫 코드**

```css
/* [1단계] 공통 설정 및 초기화 */
* {
    /* 모든 요소의 기본 여백(margin, padding)을 0으로 초기화합니다. */
    margin: 0;
    padding: 0;
    /* 요소의 크기를 계산할 때 테두리와 안쪽 여백을 너비에 포함시킵니다.
       이걸 안 하면 패딩을 줄 때마다 박스가 커져서 레이아웃이 깨지기 쉬워요! */
    box-sizing: border-box;
}
```

**해설**
- 이 세 줄을 CSS 맨 위에 `*` 선택자로 **한 번만** 써 두면 이후 모든 계산이 직관적으로 맞아떨어진다. 실무 프로젝트도 거의 예외 없이 이 설정으로 시작한다.
- 🔗 Grid에서 `1fr` 을 쓸 때 특히 중요하다. 칸 크기를 브라우저가 계산해 주는데 padding 때문에 실제 크기가 달라지면 **3등분이 3등분이 아니게 된다.**

**✅ 확인하기** — F12 → Elements 오른쪽 아래의 박스 모델 그림에서 margin/border/padding/content 값이 각각 보인다. `padding` 을 늘려도 전체 너비가 변하지 않으면 `border-box` 가 적용된 상태다.

---

## 2-4. display — 모든 배치의 출발점

### 개념

**display 는 두 가지 일을 동시에 한다. 이 구분이 매우 중요하다.**

| 역할 | 설명 | 해당 값 |
|---|---|---|
| ① 자기 자신의 성질 | 나는 한 줄을 차지할 것인가, 글자처럼 옆에 붙을 것인가 | `block` `inline` `inline-block` `none` |
| ② 자식들의 배치 규칙 | 내 안의 자식들을 어떻게 늘어놓을 것인가 | `flex` `grid` |

```
display
  |
  |-- 자기 자신의 성질을 바꾸는 값
  |     |-- block          한 줄을 통째로 차지
  |     |-- inline         글자처럼 옆에 붙음
  |     |-- inline-block   옆에 붙되 크기는 지정 가능
  |     |-- none           화면에서 완전히 사라짐
  |
  |-- 자식들의 배치 규칙을 바꾸는 값
        |-- flex           한 방향으로 줄 세우기 (1차원)
        |-- grid           바둑판으로 배치 (2차원)
```

**"왜 flex를 자식이 아니라 부모에게 쓰나요?"** — 가장 많이 나오는 질문이다.
답은 이것이다. **flex는 "나를 어떻게 보이게 하라"는 명령이 아니라 "내 자식들을 이렇게 줄 세워라"는 명령이기 때문이다.** 반장이 반 아이들을 줄 세우는 것이지, 아이들이 스스로 줄을 서는 것이 아니다.

**inline이 겪는 문제 (반드시 알아야 할 함정)**

| 속성 | inline에서 | 이유 |
|---|---|---|
| width / height | **무시됨** | 글자는 폰트 크기가 크기를 결정하므로 |
| margin 위/아래 | **무시됨** | 줄 간격은 line-height가 담당하므로 |
| margin 좌/우 | 적용됨 | 글자 사이 간격은 벌릴 수 있으므로 |
| padding 위/아래 | **겉보기만 적용** | 배경은 커지지만 다른 요소를 밀어내지 못해 겹쳐 보임 |

**`display: none` vs `visibility: hidden`** — 면접에서도 자주 나온다.

| 구분 | `display: none` | `visibility: hidden` |
|---|---|---|
| 보이는가 | 안 보임 | 안 보임 |
| 자리를 차지하는가 | **차지하지 않음** | **자리는 그대로 남음** |
| 쓰는 경우 | 탭 전환, 모달, 취소 버튼 감추기 | 레이아웃을 유지한 채 잠시 감출 때 |

### 코드 위치

`02_css/css/ch04_display.css`

```css
.d-block, .d-inline, .d-ib {
    width: 120px;              /* 세 개 모두 같은 width 를 줬다 */
    padding: 8px;
    background-color: #dce6f1;
}

.d-block  { display: block; }        /* width 가 적용됨 */
.d-inline { display: inline; }       /* width 가 무시됨 */
.d-ib     { display: inline-block; } /* 둘의 장점을 합친 것 */

/* none 과 hidden */
.d-none    { display: none; }
.v-hidden  { visibility: hidden; }
```

**해설**
- 세 클래스에 **똑같은 `width: 120px`** 을 주고 `display` 만 바꿨다. 화면에서 `.d-inline` 만 글자 크기대로 줄어 있으면 이 장은 이해한 것이다.
- 🔗 실습 3-10에서 취소 버튼을 감출 때 `display: none` 을 쓰는 이유는 **버튼이 빠졌을 때 빈칸이 남지 않게** 하기 위해서다. 5부에서는 이 `style.display` 조차 사라지고 `{isEditing && <button>}` 라는 조건부 렌더링이 된다(5-7절).

⚠️ **`flex` 와 `grid` 는 반드시 부모에 선언한다.** 자식에게 주면 아무 일도 일어나지 않는다. 실습 1-5의 "상자들이 세로로 쌓여 있다"는 오류가 `display: grid` 를 자식(`.skill-item`)에 준 경우다.

---

## 2-5. Flexbox — 한 방향 배치

### 개념

`display: flex` 를 선언하는 순간 그 요소는 **컨테이너(부모)**가 되고, 안의 요소들은 **아이템(자식)**이 된다.

| 구분 | 누구에게 | 어떤 속성 |
|---|---|---|
| 배치 규칙 | **부모 (컨테이너)** | `display`, `flex-direction`, `justify-content`, `align-items`, `flex-wrap`, `gap` |
| 개별 조정 | **자식 (아이템)** | `flex-grow`, `flex-shrink`, `flex-basis`, `flex`, `align-self`, `order` |

⚠️ **속성이 안 먹는다면 90%는 "부모에게 써야 할 것을 자식에게 썼거나 그 반대"다.**

**가장 중요한 개념 : 주축과 교차축**

| 속성 | 값 | 주축 | 교차축 |
|---|---|---|---|
| `flex-direction` | `row` (기본값) | 가로 → | 세로 ↓ |
| | `column` | 세로 ↓ | 가로 → |

| 축 | 담당 속성 |
|---|---|
| **주축 (main axis)** — 아이템이 나열되는 방향 | `justify-content` |
| **교차축 (cross axis)** — 주축과 직각 | `align-items` |

> 많은 입문 자료가 "justify-content는 가로 정렬"이라고 설명하는데, **이것은 `flex-direction`이 `row`일 때만 맞다.**
> `column`으로 바꾸는 순간 `justify-content`가 세로를 담당한다.
> **"가로/세로"가 아니라 "주축/교차축"으로 외워야 한다.**

**justify-content (6가지) — 점(.)은 빈 공간**

```
flex-start (기본값)  [1][2][3].....................
flex-end             .....................[1][2][3]
center               ..........[1][2][3]..........
space-between        [1].........[2].........[3]     ← 양 끝에 딱 붙이고 사이를 최대로
space-around         ....[1]........[2]........[3]....
space-evenly         ......[1]......[2]......[3]......
```

| 값 | 언제 쓰나 |
|---|---|
| `space-between` | **로고는 왼쪽, 메뉴는 오른쪽인 네비게이션 바 (가장 많이 씀)** |
| `center` | 버튼 안의 글자, 로고 하나만 있는 상단 바 |
| `flex-end` | 오른쪽 하단의 확인/취소 버튼 묶음 |

**align-items (5가지)**

| 값 | 설명 |
|---|---|
| `stretch` (기본값) | 아이템이 교차축 방향으로 늘어나 부모를 꽉 채움. **아이템에 height가 있으면 적용 안 됨** |
| `center` | 교차축 정중앙. 가장 많이 사용 |
| `flex-start` / `flex-end` | 위쪽 / 아래쪽에 붙임 |
| `baseline` | 글자의 아랫선 기준. 글자 크기가 다른 요소를 나란히 놓을 때 |

**외워둘 세 줄 — 완전 중앙 정렬**

```css
display: flex;
justify-content: center;   /* 주축(가로) 중앙 */
align-items: center;       /* 교차축(세로) 중앙 */
```

교재에서만 히어로 영역(1-3), 네비게이션 바(2-2), 카드 이미지 영역(2-5) 세 번 나온다. **통째로 외워 두면 평생 쓴다.**
과거에는 세로 중앙 정렬을 위해 요소 높이를 계산해 `margin-top` 에 음수를 넣거나 테이블 태그를 흉내 내는 편법을 썼다. 지금은 이 세 줄이면 끝난다. **Flexbox의 가장 큰 선물이다.**

**나머지 자주 쓰는 속성**

| 속성 | 역할 |
|---|---|
| `gap: 20px` | 아이템 **사이**에만 간격. 바깥에는 여백이 안 생겨 margin 계산이 필요 없어짐 |
| `flex-direction: column` | 주축이 세로로. **두 정렬 속성의 역할도 뒤바뀜** |
| `flex-wrap: wrap` | 한 줄에 다 못 들어가면 다음 줄로. 기본값은 `nowrap` (억지로 찌그러뜨림) |
| `flex: 1` | (자식에 씀) 남는 공간을 나눠 가짐. `flex-grow:1 / shrink:1 / basis:0` 의 축약 |

```
부모 너비 600px, 아이템 3개

아무 설정 없음 (flex-grow: 0)
[1][2][3]..........................   ← 내용 크기만큼만

모두 flex: 1
[   1   ][   2   ][   3   ]           ← 남는 공간을 1:1:1 로

2번만 flex: 1
[1][     2     ][3]                   ← 남는 공간을 2번이 독차지

1번 flex:1, 2번 flex:2, 3번 flex:1
[  1  ][     2     ][  3  ]           ← 1:2:1 비율
```

```css
.sidebar { width: 200px; }  /* 고정 */
.content { flex: 1; }       /* 나머지 전부 */
```

**그 밖의 자식 속성**

| 속성 | 설명 |
|---|---|
| `align-self` | 이 아이템만 다른 교차축 정렬을 적용. 예: 나머지는 위쪽, 나만 아래쪽 |
| `order` | 숫자가 작을수록 앞. **HTML 순서를 바꾸지 않고 화면 순서만 바꿀 때** 사용 (기본값 0) |

```css
.item-special { align-self: flex-end; }  /* 이 아이템만 아래쪽에 붙는다 */
.item-first   { order: -1; }             /* HTML 상 마지막이어도 화면엔 맨 앞으로 */
```

### 코드 위치

`02_css/css/ch05_flexbox.css`

```css
/* --- 실습 1-2 헤더 --- */
.flex-header {
    display: flex;
    justify-content: space-between;   /* 로고 왼쪽, 메뉴 오른쪽 */
    align-items: center;              /* 세로 중앙 */
    padding: 14px 24px;
    background-color: #333333;
    color: #ffffff;
}

/* --- 정렬 실험용 상자 --- */
.fbox {
    display: flex;                 /* 이 줄을 지우면 전부 무너집니다 */
    gap: 6px;
}
.jc-start   { justify-content: flex-start; }
.jc-center  { justify-content: center; }
.jc-end     { justify-content: flex-end; }
.jc-between { justify-content: space-between; }
.jc-around  { justify-content: space-around; }
.jc-evenly  { justify-content: space-evenly; }

.ai-stretch { align-items: stretch; }
.ai-center  { align-items: center; }

/* 방향과 줄바꿈 */
.fbox.col  { flex-direction: column; align-items: flex-start; }
.fbox.wrap { flex-wrap: wrap; width: 300px; }

/* --- 정중앙 배치 (실습 1-3 히어로) --- */
.hero {
    display: flex;
    justify-content: center;      /* 가로 중앙 */
    align-items: center;          /* 세로 중앙 */
    height: 200px;
}
```

`portfolio/css/style01.css` — **실습 1-2와 1-3의 실제 코드**

```css
/* [2단계] 헤더(상단 바) 레이아웃 */
.header {
    display: flex;                    /* Flexbox 활성화 */
    justify-content: space-between;   /* 이름은 왼쪽, 메뉴는 오른쪽 */
    align-items: center;              /* 세로 정중앙 */
    padding: 20px 50px;
    background-color: #333;
    color: white;
}

/* [3단계] 히어로 섹션 (중앙 큰 문구 영역) */
.hero {
    height: 60vh;                     /* 화면 높이의 60% */
    display: flex;
    justify-content: center;          /* 가로 중앙 정렬 */
    align-items: center;              /* 세로 중앙 정렬 */
    background-color: #fff;
    text-align: center;
}

.profile-card {
    display: flex;
    flex-direction: row;
    gap: 20px;
    border-radius: 10px;
    padding: 30px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
```

`student_js/css/form.css` — **실습 3-3의 `.form-group`**

```css
.form-group {
    display: flex;
    flex-direction: column;   /* 라벨 위, 입력창 아래 */
    gap: 5px;
}
```

**실전 패턴 6가지 (이것만 외워도 충분하다)**

```css
/* ① 네비게이션 바 — 로고 왼쪽, 메뉴 오른쪽 */
.navbar { display: flex; justify-content: space-between; align-items: center; height: 70px; }

/* ② 완전 중앙 정렬 */
.hero { display: flex; justify-content: center; align-items: center; height: 400px; }

/* ③ 아이콘 + 텍스트 나란히 (세로 중앙 맞춤) */
.label { display: flex; align-items: center; gap: 8px; }

/* ④ 사이드바 + 본문 */
.layout { display: flex; }
.sidebar { width: 250px; }   /* 고정 */
.content { flex: 1; }        /* 나머지 전부 */

/* ⑤ 카드 목록 (줄바꿈 포함) */
.card-list { display: flex; flex-wrap: wrap; gap: 20px; }

/* ⑥ 세로 배치 — 라벨 위, 입력창 아래 */
.form-group { display: flex; flex-direction: column; gap: 5px; }

/* ⑦ 푸터를 항상 화면 맨 아래로 */
body { display: flex; flex-direction: column; min-height: 100vh; }
main { flex: 1; }
```

⚠️ **자주 하는 실수 5가지**

| 증상 | 원인 | 해결 |
|---|---|---|
| 가로로 안 늘어선다 | 자식에게 `display: flex` 를 씀 | 부모에게 선언 |
| `justify-content` 가 안 먹는다 | 부모에 `display: flex` 가 없음 | `display: flex` 먼저 |
| `align-items: center` 가 반응이 없다 | **부모에 높이가 없어 움직일 공간이 없음** | 부모에 `height` 또는 `padding` |
| column인데 `justify-content` 가 세로로 동작 | 정상 동작이다 | 주축/교차축 개념 다시 확인 |
| 아이템이 찌그러진다 | 기본값이 `nowrap` 이라 억지로 줄어듦 | `flex-wrap: wrap` 또는 `flex-shrink: 0` |

**🎮 연습** : Flexbox Froggy (24단계 게임) https://flexboxfroggy.com/#ko

---

## 2-6. CSS Grid — 격자 배치

### 개념

Flexbox가 "한 줄로 세우기"라면, Grid는 **"모눈종이에 칸을 나누고 그 안에 물건을 놓기"**다.

```
Flexbox (1차원)          Grid (2차원)
[1][2][3][4]             +----+----+----+
한 방향으로만 통제        | 1  | 2  | 3  |
                         +----+----+----+
                         | 4  | 5  | 6  |
                         +----+----+----+
                         행과 열을 동시에 통제
```

**Flex는 아이템의 크기가 배치를 결정하지만(콘텐츠 기준), Grid는 먼저 칸을 그려놓고 그 안에 아이템을 넣는다(레이아웃 기준).**

**fr 단위 — Grid의 핵심**

`fr` 은 fraction(비율)의 줄임말. `px` 처럼 절대적인 크기가 아니라 **"남은 공간의 몇 등분"**이다.

```css
grid-template-columns: 1fr 1fr 1fr;      /* 전체를 정확히 3등분 */
grid-template-columns: 2fr 1fr;          /* 왼쪽이 오른쪽의 2배 */
grid-template-columns: 200px 1fr 100px;  /* 고정 - 유연 - 고정 */
grid-template-columns: repeat(4, 1fr);   /* 1fr 1fr 1fr 1fr 과 같음 */
```

`200px 1fr 100px` 같은 조합이 특히 유용하다. 사이드바와 광고는 고정 폭, 본문만 늘어나게.

**auto-fit + minmax — 미디어 쿼리 없는 반응형**

```css
grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
```

| 부분 | 의미 |
|---|---|
| `auto-fit` | 열을 몇 개 만들지 브라우저가 알아서 결정 |
| `minmax(250px, 1fr)` | 각 열은 최소 250px, 최대는 남는 공간을 나눠 가짐 |
| 합친 뜻 | **"한 칸이 250px보다 작아지면 열 개수를 줄여라"** |

```
넓은 화면 (1200px)  [카드][카드][카드][카드]
중간 화면 (800px)   [ 카드 ][ 카드 ][ 카드 ]
좁은 화면 (500px)   [  카드  ][  카드  ]
모바일   (350px)    [     카드     ]
```

**미디어 쿼리를 한 줄도 안 썼는데 반응형이 된다.**

**grid-template-areas — 그림 그리듯 배치하기**

```css
.website {
    display: grid;
    grid-template-areas:
        "header  header   header"
        "sidebar content  content"
        "sidebar content  content"
        "footer  footer   footer";
    grid-template-columns: 200px 1fr 1fr;
    grid-template-rows: 80px 1fr 1fr 60px;
}
.header  { grid-area: header; }
.sidebar { grid-area: sidebar; }
```

규칙 세 가지 : ① 따옴표 한 줄이 한 행 ② 같은 이름을 여러 칸에 쓰면 합쳐짐 ③ 자식에 `grid-area: 이름` 으로 연결. 빈 칸은 마침표(`.`). **같은 이름은 반드시 직사각형이어야 하며, ㄱ자나 계단 모양은 안 된다.**

이 방식의 진짜 장점은 반응형이다. 모바일에서는 `areas` 만 다시 그리면 된다. **HTML을 한 글자도 안 고쳤는데 사이드바가 본문 아래로 내려간다.**

### 코드 위치

`02_css/css/ch06_grid.css`

```css
.g3, .g-half, .g-21, .g-fixed, .g-auto {
    display: grid;                 /* 부모에 선언합니다 */
    gap: 12px;
}

.g3      { grid-template-columns: repeat(3, 1fr); }
.g-half  { grid-template-columns: 1fr 1fr; }
.g-21    { grid-template-columns: 2fr 1fr; }
.g-fixed { grid-template-columns: 200px 1fr; }

/* 실습 3-3 의 반응형 폼과 같은 방식 */
.g-auto  { grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); }
```

`02_css_display/css/ch05_grid.css` — **areas 를 주석 그림과 함께 설명해 둔 파일**

```css
.website {
    display: grid;
    grid-template-areas:
        "header  header   ."
        "sidebar content  content"
        "sidebar content  content"
        "footer  footer   footer";
    /*
        이 표는 3열 × 4행 = 12칸
           200px      1fr        1fr
        +---------+----------+----------+
   60px |        header (3칸 합침)      |
        +---------+----------+----------+
    1fr | sidebar |      content        |   ← content 도
        +         +   (2열 × 2행 = 4칸) +      4칸을 합침
    1fr |  (2행)  |                     |
        +---------+----------+----------+
   50px |        footer (3칸 합침)      |
        +---------+----------+----------+
    */
    grid-template-columns: 200px 1fr 1fr;
    grid-template-rows: 60px 1fr 1fr 50px;
}
```

`portfolio/css/style01.css` — **실습 1-5 기술 스택**

```css
.grid-wrapper {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;
    margin-top: 20px;
}
.skill-item {
    background: #333;
    color: white;
    padding: 20px;
    text-align: center;
    border-radius: 5px;
}
```

`student_js/css/form.css` — **실습 3-3 반응형 폼 (미디어 쿼리 없음)**

```css
.form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 15px;
}
```

**해설**
- 자식 요소에는 CSS를 한 줄도 안 썼는데도 칸에 들어간다. **부모가 칸을 그려놓으면 자식들이 왼쪽 위부터 순서대로 채워 들어간다.** 이것이 Grid의 기본 동작이다.
- `grid-template-rows` 는 생략해도 된다. 그러면 행의 개수와 높이가 내용에 맞춰 자동으로 정해진다. **실무에서는 열만 정의하고 행은 자동에 맡기는 경우가 훨씬 많다.**
- 🔗 실습 2-3의 `.project-container` 가 `max-width: 1100px; margin: 60px auto;` 와 `repeat(3, 1fr)` 을 함께 쓴다. 2-3절의 `margin: 0 auto` 와 이 절의 `fr` 이 만나는 자리다.

⚠️ **자주 하는 실수**

| 증상 | 원인 | 해결 |
|---|---|---|
| 칸이 안 나뉜다 | 자식에게 `display: grid` 를 씀 | 부모에게 선언 |
| `grid-template-columns` 가 무시됨 | `display: grid` 가 없음 | 먼저 선언 |
| areas가 동작하지 않는다 | 이름이 직사각형이 아니거나 줄마다 칸 수가 다름 | 모든 줄의 칸 수를 같게 |
| 간격이 이상하다 | margin과 gap을 함께 씀 | **Grid에서는 gap만** |

**✅ 확인하기** — Chrome Elements 탭에서 `grid` 배지를 클릭하면 격자선이 화면에 표시된다.

**🎮 연습** : Grid Garden https://cssgridgarden.com/#ko

---

## 2-7. Flex와 Grid, 무엇을 쓸까

### 개념

```
배치할 것이 있다
   |
   +-- 가로 또는 세로 한 방향으로만 늘어놓으면 되나?
   |      |
   |      +-- 예  --> flex   (메뉴, 버튼 묶음, 아이콘+글자, 카드 내부)
   |      |
   |      +-- 아니오
   |            |
   +------------+-- 칸을 미리 나누고 그 안에 넣고 싶다
   |                  |
   |                  +-- 예 --> grid   (페이지 전체 구조, 카드 목록, 갤러리)
   |
   +-- 줄바꿈만 필요하다 --> flex + flex-wrap
```

| 기준 | Flexbox | Grid |
|---|---|---|
| 차원 | 1차원 (한 방향) | 2차원 (행 + 열) |
| 기준 | **콘텐츠 기준** (내용 크기가 배치를 결정) | **레이아웃 기준** (칸을 먼저 그리고 내용을 넣음) |
| 적합한 규모 | 작은 단위 — 버튼, 메뉴, 카드 안쪽 | 큰 단위 — 페이지 전체, 목록 전체 |
| 줄 맞춤 | 줄마다 아이템 수가 달라지면 세로줄이 안 맞음 | 항상 세로줄이 정확히 맞음 |
| 대표 코드 | `justify-content` / `align-items` / `flex: 1` | `grid-template-columns` / `gap` / `grid-template-areas` |

**둘은 경쟁 관계가 아니다. 실무에서는 함께 쓴다.**

```css
.page   { display: grid; grid-template-columns: 200px 1fr; }      /* 큰 구조 */
.header { display: flex; justify-content: space-between; }        /* 그 안의 배치 */
```

**Grid 아이템 안에 다시 `display: flex` 를 쓰는 것은 매우 흔한 패턴이다.**

### 코드 위치

`portfolio/portfolio.html` + `style01.css` 가 이 혼용의 실제 예다.

```
.header       → flex (로고 | 메뉴)
.hero         → flex (정중앙)
.profile-card → flex (사진 | 글)
.grid-wrapper → grid (기술 스택 4칸)
```

🔗 3부 `form.css` 도 같다. `.form-grid` 는 grid(입력칸 배치), 그 안의 `.form-group` 은 flex-column(라벨 위 입력창 아래), `.button-group` 은 flex(버튼 나열).

---

## 2-8. 배치 방법의 역사 — 왜 flex와 grid가 등장했나

### 개념

| 시대 | 방법 | 문제점 | 지금은 |
|---|---|---|---|
| ~2015 | `float`, `position` 을 이용한 편법 | 계산이 복잡하고 버그가 잦음, 반응형이 어려움 | 거의 안 씀 |
| 2015~ | **Flexbox** (1차원) | 한 방향만 제어 | 메뉴·버튼·카드 내부 배치의 표준 |
| 2017~ | **Grid** (2차원) | (구형 브라우저) | 페이지 전체 레이아웃의 표준 |

```css
/* 옛날 방식 - 참고만 하세요 */
.item {
    float: left;
    width: 33.333%;      /* gap을 넣으면 이 숫자를 다시 계산해야 함 */
    margin-right: 10px;  /* 마지막 요소만 빼야 하는데 방법이 복잡함 */
}
.container::after {
    content: "";
    display: block;
    clear: both;         /* 이 주문을 외우지 않으면 레이아웃이 무너짐 */
}
```

`float` 은 원래 **"이미지 옆에 글이 흐르게 하는"** 용도로 만들어진 속성이다. 배치 도구가 없어서 억지로 끌어다 쓴 것. `clear: both` 같은 주문을 외워야 했고, **세로 중앙 정렬은 사실상 불가능했다.**

> flex와 grid는 **"배치를 위해 처음부터 새로 설계된" 최초의 도구**다.
> 그래서 하고 싶은 말을 그대로 코드로 쓸 수 있다.
> "가운데로 모아라" = `justify-content: center` 한 줄, "3등분해라" = `repeat(3, 1fr)` 한 줄. **계산이 필요 없다.**

### 코드 위치

`02_css_display/ch03_history.html` + `css/ch03_history.css` — float 시절 코드를 실제로 실행해 볼 수 있게 남겨 둔 예제.

**해설** — 이 절은 시험에 나올 내용은 아니다. 다만 **오래된 프로젝트를 넘겨받거나 구글에서 검색한 예제가 `float: left` 로 되어 있을 때 "왜 이렇게 생겼는지"를 알아보기 위한 지식**이다.

---

## 2-9. 단위와 색

### 개념

| 단위 | 기준 | 언제 쓰나 |
|---|---|---|
| `px` | 화면의 점 하나 | 테두리, 그림자, 정확한 고정 크기 |
| `%` | **부모 요소**의 크기 | 너비를 부모에 맞출 때 |
| `rem` | **html의 글자 크기** (보통 16px) | 글자 크기와 여백 — **가장 권장** |
| `em` | **자기 자신**의 글자 크기 | 중첩되면 값이 누적되어 헷갈림 |
| `vw` / `vh` | 브라우저 화면 너비/높이의 1% | 화면을 꽉 채우는 영역 |
| `fr` | 격자에서 남는 공간의 비율 | **Grid 전용** |

⚠️ **`rem` 과 `em` 중에는 `rem` 을 권한다.** `em` 은 부모의 글자 크기 기준이라 중첩될수록 값이 곱해져 예측이 어렵다. `rem` 은 언제나 문서 최상단 기준이라 흔들리지 않는다.

**색 표기**

```css
color: red;                /* 이름 — 종류가 한정적 */
color: #333333;            /* 16진수 — 가장 많이 씀 */
color: #333;               /* 위와 같음 (각 자리를 두 번 반복) */
color: rgb(51, 51, 51);    /* 빨강·초록·파랑 각 0~255 */
color: rgba(0, 0, 0, 0.1); /* 마지막은 투명도 0(투명)~1(불투명) */
```

`rgba` 의 마지막 값은 **그림자에서 특히 자주 쓰인다.** `rgba(0,0,0,0.1)` = "검정을 10%만" → 아주 은은한 그림자. **실무의 그림자는 대부분 이렇게 매우 옅다.**

### 코드 위치

`02_css/css/ch07_unit_color.css` — **rem vs em 의 누적을 눈으로 보여주는 부분이 핵심**

```css
.u.px  { width: 200px; }
.u.pct { width: 60%; }
.u.rem { font-size: 1.4rem; }
.u.vw  { width: 40vw; }
.u.vh  { height: 15vh; }

/* --- rem vs em --- */
.rem-outer { font-size: 1.2rem; }
.rem-inner { font-size: 1.2rem; }   /* 항상 html 기준 */

.em-outer  { font-size: 1.2em; }
.em-inner  { font-size: 1.2em; }    /* 부모 기준 → 누적 */

/* --- 색 견본 --- */
.c1 { background-color: red; }
.c2 { background-color: #333333; }
.c3 { background-color: #333; }              /* c2 와 같은 색 */
.c4 { background-color: rgb(46, 84, 150); }
.c5 { background-color: rgba(0, 0, 0, 0.1); }
```

`portfolio/css/style01.css` — 실습 1-3

```css
.hero { height: 60vh; }   /* 화면 높이의 60% */
```

**해설**
- `height: 400px` 이라고 쓰면 큰 모니터에서는 작아 보이고 노트북에서는 커 보인다. `60vh` 로 쓰면 **어떤 화면에서도 같은 비율**을 차지한다.
- ⚠️ 글자가 안 보인다 → 배경색과 글자색이 같다. 흰 배경에 `color: white` 를 준 경우가 가장 흔하다.

---

## 2-10. 글꼴과 텍스트 — 시각적 위계

### 개념

| 속성 | 자주 쓰는 값 |
|---|---|
| `font-family` | `sans-serif`, `Arial`, `'Noto Sans KR'` |
| `font-size` | `16px`, `1.5rem` |
| `font-weight` | `normal(400)`, `bold(700)`, `500` |
| `line-height` | `1.6` — **단위 없이 배수로 쓰는 것을 권장** |
| `text-align` | `left`, `center`, `right` |
| `text-decoration` | `none` (링크 밑줄 제거) |
| `letter-spacing` | `2px` (로고에 자주) |

`font-family` 는 여러 개를 쉼표로 나열한다. 앞에서부터 찾아보고 없으면 다음 것. **맨 끝에는 `sans-serif` 같은 큰 분류를 두어 어떤 환경에서도 글꼴이 있게 한다.**

**시각적 위계 — 크기와 색만으로**

정보의 중요도를 표현하는 데 화려한 장식은 필요 없다. **크기와 색만으로 충분하다.**

### 코드 위치

`02_css/css/ch08_text.css` — **위계가 잘 잡힌 카드와 그렇지 않은 카드를 나란히 둔 예제**

```css
/* 위계가 잘 잡힌 경우 */
.card-demo.good h3 { color: #222222; font-size: 1.2rem; font-weight: bold; }
.card-demo.good p  { color: #777777; font-size: 0.95rem; }   /* 한 톤 물러남 */

/* 위계가 없는 경우 — 크기와 색이 거의 같음 */
.card-demo.bad h3 { color: #333333; font-size: 1rem; font-weight: normal; }
.card-demo.bad p  { color: #333333; font-size: 1rem; }
```

**해설**
- 실습 2-6이 이 주제다. **본문의 `color` 를 `#777` 에서 `#333` 으로 바꿔 보면** 제목과 본문의 구분이 얼마나 흐려지는지 알 수 있다. "색으로 위계를 만든다"는 말의 뜻이 그때 이해된다.
- `.card-demo .tag` 의 `border-radius: 999px` 가 **알약(칩) 모양**을 만드는 관용구다. 실습 2-7의 태그 칩이 이것.

---

## 2-11. 배경 · 테두리 · 그림자, 그리고 transition

### 개념

| 속성 | 예 |
|---|---|
| `background-color` | `#f4f4f4` |
| `background-image` | `url('bg.jpg')` |
| `border` | `1px solid #ddd` |
| `border-bottom` | `2px solid #007bff` (아래쪽만) |
| `border-radius` | `10px`, `50%`(원), `999px`(알약) |
| `box-shadow` | `0 2px 8px rgba(0,0,0,0.1)` |
| `overflow` | 넘치는 내용을 어떻게 할지 (아래 표) |
| `opacity` | `0.5` — 요소 전체의 투명도 |

**overflow — 내용이 상자를 넘칠 때**

| 값 | 동작 | 주로 쓰는 곳 |
|---|---|---|
| `visible` (기본값) | 넘치는 내용을 그대로 보여준다. 상자 밖으로 삐져나옴 | 의도치 않으면 레이아웃이 깨져 보인다 |
| `hidden` | 넘치는 부분을 잘라내어 감춘다 | 둥근 모서리 안에 이미지 맞추기 |
| `scroll` | 스크롤바를 항상 표시 | 내용이 적어도 스크롤바가 보여 잘 안 씀 |
| `auto` | 넘칠 때만 스크롤바 표시 | 채팅창, 모달, 사이드바 — **가장 많이 씀** |

가로·세로를 따로 지정할 수도 있다.

```css
overflow-x: auto;    /* 가로만 스크롤 (넓은 표에 유용) */
overflow-y: hidden;  /* 세로는 잘라냄 */
```

⚠️ `overflow: hidden` 을 부모에 줬더니 드롭다운 메뉴나 툴팁이 잘려서 안 보인다 → 자식이 부모 밖으로 나가야 하는 요소라면 `hidden` 을 쓰면 안 된다. 부모의 `overflow` 를 `visible` 로 두고 다른 방법을 찾아야 한다.

**box-shadow 읽는 법**

```
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            | |   |   |
            | |   |   +-- 색 (검정을 10% 투명도로)
            | |   +------ 흐림 정도 (클수록 은은하고 부드러움)
            | +---------- 세로 위치 (아래로 2px)
            +------------ 가로 위치 (0이면 좌우로 안 치우침)
```

**실무의 그림자는 대부분 아래로 살짝, 아주 흐리게, 아주 옅게 준다.** 진한 그림자는 촌스러워 보이기 쉽다.

**transition 과 hover**

```css
nav a        { color: white; transition: color 0.3s; }  /* 평소 상태에 씁니다 */
nav a:hover  { color: #00adb5; }
```

⚠️ **`transition` 은 변화가 일어나는 `:hover` 쪽이 아니라 평소 상태에 쓴다.** 그래야 올릴 때와 뗄 때 모두 부드럽다. `:hover` 에만 쓰면 올릴 때는 부드럽고 **뗄 때는 툭 끊긴다.**

실제 웹사이트는 대부분 **0.2 ~ 0.4초**를 쓴다. 더 짧으면 변화를 못 느끼고, 더 길면 답답하다.

### 코드 위치

`02_css/css/ch11_transition.css` — **맞는 방식과 틀린 방식을 나란히 둔 예제**

```css
/* 올바른 방식 : 평소 상태에 transition */
.tr-nav a.good { transition: background-color 0.3s, color 0.3s; }
.tr-nav a.good:hover { background-color: #00adb5; }

/* 흔한 실수 : :hover 에만 transition → 뗄 때 끊김 */
.tr-nav a.bad:hover {
    background-color: #00adb5;
    transition: background-color 0.3s;
}

/* --- 카드 hover (실습 2-7) --- */
.hcard {
    overflow: hidden;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;      /* 평소 상태에 선언 */
}
.hcard:hover {
    transform: translateY(-10px);               /* 위로 떠오르는 효과 */
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15); /* 그림자도 함께 진하게 */
}
```

`02_css/css/ch09_decoration.css` — **overflow 의 대표 용도**

```css
.card-x { border-radius: 12px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
.card-x .head { background-color: #333333; }  /* 직사각형 자식이 모서리를 덮는다 */
.card-x.clipped { overflow: hidden; }         /* 이 한 줄이 모서리를 잘라 줍니다 */
```

`portfolio/css/style01.css` — 실습 1-6

```css
nav a {
    color: white;
    text-decoration: none;
    transition: color 0.3s;
}
nav span { margin-left: 25px; }
nav a:hover { color: #00adb5; }
```

**해설**
- `.hcard:hover` 에서 **`transform: translateY(-10px)` 와 그림자 강화를 동시에** 주면 "떠오르는" 느낌이 난다. 실습 2-7의 핵심.
- ⚠️ 둥근 모서리 밖으로 자식이 튀어나온다 → 부모에 `overflow: hidden`. 다만 **드롭다운이나 툴팁처럼 자식이 부모 밖으로 나가야 하는 요소가 있으면 `hidden` 을 쓰면 안 된다.**

---

## 2-11-1. 실습 2 — Projects Gallery 페이지 (8단계)

> ⚠️ **이 실습의 코드 파일은 저장소(zip)에 없다.** `css_basic/projects01.html` ~ `projects08.css` 가 커밋되지 않은 상태로 보인다. 그래서 이 절은 다른 절과 달리 "코드 위치"를 가리키지 못하고, **`03-1_HTML_CSS_JS_단계별` 문서에 적힌 코드를 그대로 옮겨 적은 것**이다. 실습 파일이 나중에 올라오면 이 절을 실물 코드로 교체할 것.

지금까지의 1부(portfolio)가 "정적인 한 페이지"였다면, 실습 2는 **"카드가 여러 장 나열된 목록 페이지"**를 만든다. Flexbox와 Grid를 **한 페이지 안에서 같이** 쓰는 첫 연습이다.

### 2-1단계 — 페이지 뼈대와 전체 초기화

```html
<!doctype html>
<html>
<head>
  <title>My Projects</title>
  <link rel="stylesheet" href="css/projects01.css" />
</head>
<body>
  <nav class="navbar">Project Gallery</nav>
  <main class="project-container"></main>
</body>
</html>
```

```css
/* [1단계] 전체 초기화 */
* { margin: 0; padding: 0; box-sizing: border-box; }
body {
    background-color: #f8f9fa;
    font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
    color: #333;
    line-height: 1.6;
}
```

`main` 은 지금은 비어 있다. **2-4단계에서 이 안에 카드들을 채워 넣는다.** 클래스 이름 `project-container` 는 "프로젝트 카드들을 담는 큰 바구니"라는 뜻.

### 2-2단계 — 상단 네비게이션 바 (Flexbox로 가운데 정렬)

```css
.navbar {
    background-color: #ffffff;
    height: 80px;
    display: flex;                /* 1. 배치 모드 활성화 */
    justify-content: center;      /* 2. 가로 방향 중앙 정렬 */
    align-items: center;          /* 3. 세로 방향 중앙 정렬 */
    border-bottom: 1px solid #ddd;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}
```

**부모(Flex Container)** = `display: flex` 를 선언한 `.navbar`. **자식(Flex Items)** = 그 안의 "Project Gallery" 텍스트. 부모에게 "이제부터 Flex 모드야"라고 명령하면, 그 안의 자식들을 자유자재로 배치할 수 있는 권한이 생긴다.

⚠️ `display: flex` 를 지우면? `align-items: center` 와 `justify-content: center` 가 **한꺼번에 무시**되어 글자가 왼쪽 위 구석에 붙는다. Flex 없이 중앙 정렬을 하려면 글자 크기를 계산해 `padding-top` 을 직접 넣어야 하고, 높이가 바뀔 때마다 숫자를 다시 고쳐야 한다.

### 2-3단계 — 카드를 담을 바구니 (CSS Grid로 3열)

```css
.project-container {
    max-width: 1100px;
    margin: 60px auto;
    padding: 0 20px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 40px;
}
```

**왜 이걸 Grid로 만드나 (Flex와의 차이)** — 이 카드들을 flex로 만들었다면 카드가 많아질 때 줄바꿈(`flex-wrap`)을 따로 설정하고 각 카드 너비를 `%` 로 계산해서 넣어야 한다. Grid는 세 가지가 다르다.

1. **계산이 필요 없다** — `1fr` 이 알아서 남은 공간을 계산해 준다.
2. **구조가 한눈에 보인다** — CSS만 봐도 "3열짜리 레이아웃"임을 바로 안다.
3. **반응형에 강하다** — 모바일에서 1열로 바꾸려면 `repeat(1, 1fr)` 로 숫자만 바꾸면 된다.

⚠️ 지금은 `main` 안이 비어 있어 화면에 눈에 띄는 변화가 없다. **정상이다.** F12 → Elements 에서 `main` 을 클릭하면 `display: grid` 가 적용된 것만 확인된다.

### 2-4단계 — 카드 만들기 (HTML 구조 + 기본 스타일)

```html
<main class="project-container">
  <div class="card">
    <div class="card-image">Project 01</div>
    <div class="card-content">
      <h3>자기소개 웹사이트</h3>
      <p>HTML과 CSS의 기초를 활용하여 제작한 첫 번째 포트폴리오 페이지입니다.</p>
      <div class="tags"><span>#HTML</span><span>#CSS</span><span>#Flexbox</span></div>
    </div>
  </div>
  <!-- 카드 2, 3 은 구조가 완전히 같다. 복사해서 내용만 바꾼다 -->
</main>
```

> **■ 알아두기** 세 카드의 구조가 완전히 같다. 이렇게 **같은 구조가 반복되는 것을 컴포넌트**라고 부르며, PART 5에서 배울 React 같은 도구는 이 반복을 없애 주는 것이 목적이다.

```css
.card {
    background: white;
    border-radius: 15px;
    overflow: hidden;                          /* 둥근 모서리 밖으로 튀어나온 자식을 자름 */
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);  /* 배경과 카드색이 비슷해 구분이 안 되므로 */
    transition: all 0.3s ease;                  /* 2-7단계 hover 를 위한 준비 */
}
```

⚠️ `overflow: hidden` 이 없으면 `border-radius: 15px` 로 카드 모서리를 둥글게 깎아도 **안의 직각 이미지가 그 모서리를 덮어버려** 다시 뾰족해 보인다.

### 2-5단계 — 카드 이미지 영역

```css
.card-image {
    height: 200px;
    background-color: #444;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: bold;
    font-size: 1.2rem;
}
```

🔗 **1부 히어로 영역(1-3), 2-2단계 네비게이션 바에 이어 세 번째로 나오는 같은 조합이다.** `display: flex` + `justify-content: center` + `align-items: center` — **이 세 줄은 통째로 외워둘 것.**

### 2-6단계 — 카드 텍스트 (여백과 시각적 위계)

```css
.card-content { padding: 25px; }
.card-content h3 { margin-bottom: 12px; font-size: 1.25rem; }
.card-content p  { color: #777; font-size: 0.95rem; margin-bottom: 20px; }
```

| 속성 | 비유 | 효과 |
|---|---|---|
| `padding` | 방 안의 가구를 벽에서 떼어놓기 | 답답함 해소, 여유로운 느낌 |
| `margin-bottom` | 줄 간격 띄우기 | 제목·본문·태그 각 정보의 구분 |
| `color` / `font-size` | 굵은 펜과 얇은 펜 쓰기 | 읽는 순서와 중요도 정하기 |

**제목은 진한 검은색, 본문은 연한 회색(`#777`)** — 사용자가 제목을 먼저 읽고 본문을 읽도록 자연스럽게 유도하는 디자인 기법. 2-10절에서 다룬 시각적 위계가 여기서 실전으로 쓰인다.

### 2-7단계 — hover 효과와 태그 칩

```css
/* 마우스 오버 효과 */
.card:hover {
    transform: translateY(-15px);
    box-shadow: 0 12px 25px rgba(0, 0, 0, 0.2);
}

/* 태그 바구니 */
.tags { display: flex; gap: 8px; flex-wrap: wrap; }
.tags span {
    background: #f0f0f0;
    padding: 4px 12px;
    border-radius: 50px;    /* 상자 높이보다 충분히 크면 알약 모양 */
    font-size: 0.8rem;
    color: #555;
    font-weight: 500;
}
```

`transform: translateY(-15px)` — Y축은 세로 방향, 컴퓨터 그래픽은 아래로 갈수록 Y가 커지므로 **마이너스 값이 위쪽**을 뜻한다. 카드가 공중에 뜨는 느낌.

🔗 **2-4단계에서 미리 넣어둔 `transition: all 0.3s ease` 덕분에 이 변화가 툭 끊기지 않고 부드럽게 이어진다.** transition 줄을 잠시 주석 처리하고 마우스를 올려보면 차이가 확실히 느껴진다 — 2-11절에서 말한 "평소 상태에 transition을 쓰는 이유"가 여기서 실전으로 확인된다.

⚠️ **가장 흔한 실수** — 마우스를 올려도 아무 변화가 없다 → `.card :hover` 처럼 공백을 넣었는지 확인. `.card:hover` 와 `.card :hover` 는 완전히 다른 선택자다(2-2절).

### 2-8단계 — 푸터로 페이지 마무리

```html
<footer class="footer">
  <p>&copy; 2026 My Portfolio. 계속해서 학습 중입니다!</p>
</footer>
```

`&copy;` 는 저작권 기호 `©` 를 나타내는 **HTML 엔티티(entity)**다. `&nbsp;`(공백), `&lt;`(`<`), `&gt;`(`>`) 등도 자주 쓰인다.

```css
.footer { text-align: center; /* ... */ }
```

**이 실습에서 반복해서 확인할 것** — 지금까지 배운 개념 중 어느 것 하나 새로 나온 게 없다. `display: flex` 세 줄 조합(2-2, 2-5), `grid-template-columns: repeat()`(2-3), `overflow: hidden` + `border-radius`(2-4), `transition` + `:hover`(2-7)가 **이미 배운 것을 조합해 하나의 실제 페이지를 완성**하는 데 쓰인다.

---

## 2-12. position — 흐름에서 꺼내기

### 개념

지금까지의 배치는 모두 "위에서 아래로 흐르는" 문서 흐름 안에서 이뤄졌다. `position` 은 **그 흐름에서 요소를 꺼내 원하는 자리에 고정**하는 속성이다.

| 값 | 동작 | 쓰임 |
|---|---|---|
| `static` | 기본값. 문서 흐름 그대로 | — |
| `relative` | 원래 자리를 유지한 채 조금 밀어냄 | **absolute 자식의 기준점 역할** |
| `absolute` | 흐름에서 빠져 **가장 가까운 relative 부모** 기준으로 배치 | 배지, 닫기 버튼, 툴팁 |
| `fixed` | **화면(viewport) 기준**으로 고정. 스크롤해도 그대로 | 상단 고정 헤더, 채팅 버튼 |
| `sticky` | 스크롤하다 특정 위치에 닿으면 고정 | 스크롤을 따라오는 목차, 표 머리글 |

⚠️ **`absolute` 를 쓸 때 가장 흔한 실수는 부모에 `position: relative` 를 빠뜨리는 것.** 기준점을 못 찾아 페이지 전체를 기준으로 엉뚱한 곳에 붙는다. **"absolute 자식에는 relative 부모"를 한 쌍으로 기억한다.**

겹치는 순서는 `z-index` 로 정한다. 숫자가 클수록 위. 다만 **`z-index` 는 `position` 이 `static` 이 아닌 요소에만 적용된다.**

### 코드 위치

`02_css/css/ch10_position.css` — **기준점이 있는 경우와 없는 경우를 나란히**

```css
.pcard {
    position: relative;           /* 기준점 — 이 줄이 핵심입니다 */
}
.pcard.no-rel {
    position: static;             /* 기준점이 없는 경우 */
}
.pcard .badge {
    position: absolute;
    top: 8px;
    right: 8px;
    border-radius: 999px;
}

/* --- sticky --- */
.scroll-area { height: 200px; overflow-y: auto; }
.sticky-head { position: sticky; top: 0; }
```

**해설**
- 본 교재 실습에는 `position` 이 나오지 않는다. **하지만 실무에서 헤더 고정·모달·툴팁을 만들려면 반드시 필요하다.**
- **레이아웃의 기본은 `display`(flex, grid)로 잡고, `position` 은 "그 위에 겹쳐 놓아야 하는 것"에만 쓴다.** `position` 만으로 전체 레이아웃을 만들려는 것이 2-8절에서 말한 옛날 방식의 어려움이었다.

---

## 2-13. 반응형과 미디어 쿼리

### 개념

**반응형의 출발점은 CSS가 아니라 이 메타 태그다.**

```html
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
```

이 한 줄이 `<head>` 에 없으면 모바일 브라우저가 페이지를 **강제로 축소**해 보여준다. CSS를 아무리 잘 짜도 반응형이 동작하지 않는다.

```css
/* 기본 : 넓은 화면 */
.project-container { grid-template-columns: repeat(3, 1fr); }

/* 화면 폭이 768px 이하일 때만 적용 */
@media (max-width: 768px) {
    .project-container { grid-template-columns: 1fr; }
}
```

| 조건 | 흔한 용도 |
|---|---|
| `(max-width: 768px)` | 태블릿·모바일 |
| `(min-width: 1024px)` | 큰 화면 전용 |
| `(max-width: 480px)` | 작은 휴대폰 |

**미디어 쿼리가 필요 없을 때도 많다.** 2-6절의 `auto-fit` + `minmax`, 그리고 `max-width` 와 `%` 를 잘 쓰면 미디어 쿼리 없이도 상당한 수준의 반응형이 된다. 실습 3-3이 그 예다. **미디어 쿼리는 "자동으로는 도저히 안 되는 지점"에만 쓰는 것이 좋다.**

### 코드 위치

`02_css/css/ch12_responsive.css` — **두 방식을 한 페이지에서 비교**

```css
/* 기본(넓은 화면) : 3열 */
.resp { grid-template-columns: repeat(3, 1fr); }
.width-label::before { content: "[넓은 화면 · 3열] "; }

/* 미디어 쿼리 없이도 반응형 (실습 3-3 방식) */
.resp-auto { grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); }

/* --- 태블릿 --- */
@media (max-width: 768px) {
    .resp { grid-template-columns: repeat(2, 1fr); }
    .width-label::before { content: "[중간 화면 · 2열] "; }
}

/* --- 모바일 --- */
@media (max-width: 480px) {
    .resp { grid-template-columns: 1fr; }
    .width-label::before { content: "[좁은 화면 · 1열] "; }
}
```

**해설**
- `::before` 의 `content` 로 현재 어떤 미디어 쿼리가 적용 중인지 화면에 글자로 표시한다. 창 너비를 줄이면 라벨이 바뀐다.
- ⚠️ **미디어 쿼리가 안 먹는다** → `@media` 블록을 해당 규칙보다 **위에** 썼다. 점수가 같으면 나중에 쓴 쪽이 이기므로 **아래에 둔다.**
- ⚠️ **가로 스크롤이 생긴다** → 어딘가에 화면보다 넓은 고정 px 값이 있다. `width` 대신 `max-width` 를 쓴다.

**✅ 확인하기** — F12 → 왼쪽 위 기기 아이콘(Ctrl+Shift+M)으로 모바일 화면을 흉내 낼 수 있다.

---

## 2-14. 배치가 안 될 때 점검표 (부록 B 발췌)

생각대로 배치되지 않을 때 **위에서부터 순서대로** 확인한다. 대부분 3번 안에서 해결된다.

1. `display` 를 자식이 아니라 **부모**에게 썼는가? (flex와 grid는 항상 부모)
2. 오타는 없는가? 클래스 이름, 콜론, 세미콜론, 하이픈(`justify-content` 는 하이픈 하나)
3. F12 → Elements 에서 그 요소를 클릭했을 때 내가 쓴 CSS가 **실제로 보이는가**? 취소선이 그어져 있다면 다른 규칙에 밀린 것이다.
4. `align-items` 가 안 먹는다면 **부모에 높이가 있는가**?
5. `width` 가 안 먹는다면 그 요소가 **inline** 은 아닌가? (`a`, `span` 등)
6. padding 때문에 크기가 커졌다면 `box-sizing: border-box` 를 넣었는가?
7. 요소가 잘려 보인다면 부모에 `overflow: hidden` 이 있지 않은가?

**디버깅 요령 : 모든 상자에 테두리 그리기**

```css
* {
    outline: 1px solid red;   /* border가 아니라 outline이라 크기에 영향 없음 */
}
```

`border` 대신 `outline` 을 쓰는 이유는, **`border` 는 요소의 크기를 바꿔서 오히려 레이아웃을 더 헷갈리게 만들기 때문**이다.

`02_css_display/css/appendix.css` 에는 이 스위치를 체크박스로 켜고 끌 수 있게 해 뒀다.

```css
/* :has() 로 체크 상태를 읽어 페이지 전체에 outline 을 그립니다 */
body:has(#ot:checked) * {
    outline: 1px solid red;
}
```

---

## 2-15. CSS 파트 자가 점검 질문

**선택자·우선순위**
1. `.card:hover` 와 `.card :hover` 는 각각 무엇을 고르나? → `ch02_selector.css`
2. 특정 속성만 안 먹을 때 F12에서 무엇을 먼저 찾나?
3. `!important` 를 쓰면 안 되는 이유는?

**박스 모델**
4. `width: 200px; padding: 20px; border: 1px` 의 실제 너비는? `border-box` 를 주면?
5. `margin: 0 auto` 가 동작하지 않는다. 무엇이 빠졌나?
6. `padding` 과 `margin` 중 배경색이 칠해지는 쪽은?

**배치**
7. `display` 가 하는 **두 가지** 일을 말하라. → `02_css_display` 1장
8. flex를 자식에 주면 왜 아무 일도 안 일어나나?
9. `flex-direction: column` 일 때 `justify-content` 는 어느 방향을 담당하나?
10. `align-items: center` 를 줬는데 반응이 없다. 가장 흔한 원인은?
11. `flex: 1` 은 무엇의 축약이며 어떤 레이아웃에서 쓰나?
12. `repeat(auto-fit, minmax(250px, 1fr))` 를 한국어 한 문장으로 풀어 말하라.
13. flex와 grid 중 "세로줄이 항상 정확히 맞는" 쪽은? 왜인가?

**그 밖**
14. `rem` 을 `em` 보다 권하는 이유는?
15. `transition` 을 `:hover` 가 아니라 평소 상태에 쓰는 이유는? → `ch11_transition.css`
16. 둥근 카드 안의 직사각형 이미지가 모서리를 덮는다. 해결책은?
17. `absolute` 자식이 엉뚱한 곳에 붙는다. 무엇을 빠뜨렸나?
18. 미디어 쿼리 없이 반응형을 만드는 두 가지 방법은?
19. 배치가 이상할 때 `* { outline: 1px solid red }` 를 쓰는데, `border` 를 안 쓰는 이유는?
