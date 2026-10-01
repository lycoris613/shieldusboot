# PART 9. 인코딩과 비밀정보

> 이 파트의 목표 — **"인코딩"이라는 한 단어가 서로 다른 두 가지 일을 가리킨다는 것을 구분하기.** 그리고 **인코딩은 암호화가 아니다.**

## 9-1. 컴퓨터에는 '글자'가 없다 — `str`과 `bytes`

### 개념

컴퓨터가 저장·전송하는 것은 **숫자(바이트)뿐**이다. "어떤 글자를 어떤 숫자로 볼지" 정한 **약속표**가 **문자 인코딩**(UTF-8, CP949/EUC-KR …)이다.

| 타입 | 정체 | 표기 |
|---|---|---|
| `str` | 사람이 읽는 **글자** | `'안녕'` (길이 2) |
| `bytes` | 컴퓨터가 다루는 **숫자 나열** | `b'\xec\x95\x88\xeb\x85\x95'` (길이 6) |

| | UTF-8 | CP949 (EUC-KR 확장) |
|---|---|---|
| `'안녕'` | `[236,149,136,235,133,149]` **6바이트** (한글 1자 = 3바이트) | `[190,200,179,231]` **4바이트** (1자 = 2바이트) |
| `'AB'` | `[65, 66]` | `[65, 66]` — 영문·숫자는 ASCII 호환이라 같다 |
| 쓰는 곳 | 요즘 대부분 · 웹 표준 · 파이썬 3 기본 | 국내 공공데이터·엑셀·옛 한국 사이트 |

- **영문만 다루면 인코딩 문제를 거의 안 겪는다. 한글을 다루는 순간부터** 약속표를 신경 써야 한다.
- `ord('A')`=65, `chr(65)`='A' — 글자 ↔ 유니코드 번호 🔗 1-6절.

---

## 9-2. encode / decode — 반드시 짝

| 용어 | 방향 | 한 문장 |
|---|---|---|
| **인코딩** `s.encode('utf-8')` | `str` → `bytes` | 약속표로 **숫자로 바꾸기** |
| **디코딩** `b.decode('utf-8')` | `bytes` → `str` | 같은 약속표로 **다시 글자로** |

- 인자를 생략하면 **UTF-8**(파이썬 3).
- **넣을 때 쓴 규칙과 꺼낼 때 쓴 규칙이 같아야만** 원본이 나온다.

**규칙이 어긋나면 — 글자 깨짐의 정체** (파일이 손상된 게 아니라 **읽는 약속표를 잘못 고른 것**)

| 유형 | 결과 | 위험도 |
|---|---|---|
| 해석 불가능한 바이트 | `UnicodeDecodeError` | 낮음 — 바로 알아챔 |
| 우연히 해석 가능한 바이트 | **조용히** 이상한 글자(`ìì`, `�ѱ�`) | **높음** — 모르고 지나감 |

| `errors=` | 동작 |
|---|---|
| `'strict'` (기본) | 에러 발생 |
| `'ignore'` | 못 읽는 바이트 **버림** |
| `'replace'` | `�`로 대체 |

⚠️ `ignore`/`replace`는 **데이터가 손실**된다. 원인을 모를 때 임시로만.

### 코드 위치

- 왕복 확인: `[기초]12` CODE 2~4 — `'파이썬'.encode('utf-8').decode('utf-8') == '파이썬'`
- 실전 decode: `[프로젝트]01` CODE 21 `response_body.decode('utf-8')` (urllib은 bytes를 준다)
- 실전 encode: PDF 7.3 `urlopen(request, data=data.encode("utf-8"))` (urllib은 bytes만 보낸다)

---

## 9-3. 인코딩의 두 층위

| | **층위 1 — 문자 인코딩** | **층위 2 — 표현 변환** |
|---|---|---|
| 바꾸는 것 | **글자 ↔ 숫자** | **숫자 ↔ 숫자** |
| 타입 | `str` ↔ `bytes` | `bytes` ↔ `bytes` |
| 파이썬 | `.encode()` / `.decode()` | `base64.b64encode()` / `quote()` |
| 예 | UTF-8, CP949, EUC-KR | base64, URL 인코딩(%XX), hex |
| 왜 | 글자를 저장·전송하려고 | **특정 문자만 지나갈 수 있는 통로를 통과**시키려고 |

```python
s = '안녕'
step1 = s.encode('utf-8')          # 층위1 str → bytes
step2 = base64.b64encode(step1)    # 층위2 bytes → bytes   b'7JWI64WV'
step3 = step2.decode('utf-8')      # 층위1 bytes → str      '7JWI64WV'  (print 할 때 b'' 없이)
```
- ③의 `.decode()`가 필요한 이유: base64 결과는 내용상 알파벳·숫자뿐이지만 **타입은 여전히 bytes**라 그대로 출력하면 `b'...'`가 붙는다.
- 되돌리기는 정확히 **역순**: `base64.b64decode(step3).decode('utf-8')`.

---

## 9-4. `repr()`과 BOM — 눈에 안 보이는 문자

### 개념

| 함수 | 대상 | 목적 |
|---|---|---|
| `print()` / `str()` | 사람 | 보기 좋게 — 공백·개행·BOM이 **안 보인다** |
| `repr()` / f-string `{값!r}` | 개발자 | **정확하게** — 따옴표, `\n`, `\t`, `﻿`가 보인다 |

- 리스트·딕셔너리를 `print`하면 **안의 요소에는 자동으로 repr이 적용**된다. 통째로 찍어 보는 것만으로 숨은 문자를 발견할 수 있다.

**BOM (Byte Order Mark)** — 파일 맨 앞의 **보이지 않는 3바이트**(`\xef\xbb\xbf`, 글자로는 `﻿`). "이 파일은 UTF-8"이라고 알리는 표식으로, **엑셀이 CSV를 UTF-8로 저장할 때** 붙인다. 읽는 쪽이 모르면 첫 글자에 딸려 들어온다.

| 읽는 방법 | BOM 처리 |
|---|---|
| `pd.read_csv(..., encoding='utf-8')` | **pandas가 자동으로 걸러 줌** — 그래서 pandas만 쓰면 문제를 못 느낀다 |
| `pd.read_csv(..., encoding='utf-8-sig')` | 명시적으로 제거 |
| `open(...).read()` / `bytes.decode('utf-8')` | **남는다** → `'﻿이름'` |
| `csv.reader` | 첫 컬럼명이 `'﻿이름'` → `d['이름']`이 **None** |
| `json.loads(b.decode('utf-8'))` | `JSONDecodeError: Unexpected UTF-8 BOM (decode using utf-8-sig)` — 에러 메시지가 해결책까지 알려 준다 |

- **쓸 때**: `df.to_csv(..., encoding='utf-8-sig')` — 엑셀로 열 CSV에는 **일부러 BOM을 붙인다**. 엑셀은 BOM이 없으면 CP949로 읽어 한글이 깨지기 때문이다(`[기초]05`, `[프로젝트]08.음식점정보`, `12selenium_infinite_scroll.py`).
- ⚠️ `strip()`은 BOM을 **못 지운다**(공백이 아니라서). `k.lstrip('﻿')`로 직접 지정.

### 코드 위치 — `[기초]12` CODE (repr 진단) · CODE 25~26 (BOM 사고)

```python
data = {'﻿이름': '홍길동', '나이 ': 20}         # BOM 과 뒤 공백이 숨은 키
data.get('이름')                                      # None!
for k in data: print(f'{k!r} (길이 {len(k)})')      # '﻿이름' (길이 3), '나이 ' (길이 3)
cleaned = {k.lstrip('﻿').strip(): v for k, v in data.items()}    # 딕셔너리 컴프리헨션으로 정리
```
- `[프로젝트]11` CODE 2가 `kpopidolsv3.csv`를 `encoding="utf-8-sig"`로 읽은 이유가 이것이다(주석: "파일 앞에 BOM이 있어").

---

## 9-5. base64 — 표현 변환, 암호화가 아니다

### 개념

- 메일 본문·JSON·URL·HTML 같은 통로는 **글자로 표현 가능한 것만** 지나간다. 이미지 같은 바이너리에는 글자로 못 쓰는 바이트가 섞여 있다.
- **어디서나 안전한 64개 문자**(`A-Z a-z 0-9 + /`, 끝에 `=` 채움)로 다시 표현하는 것이 base64.
- 장점: 어떤 통로든 통과. 대가: 길이가 약 **4/3배**.

| 쓰임 | 실습 |
|---|---|
| 이미지를 HTML에 직접: `<img src="data:image/png;base64,...">` (data URI) | `[기초]12`, `python_advanced` 4-6 |
| 업로드 파일을 JSON에 담아 되돌려 줌 | `[프로젝트]01` CODE 17 httpbin 응답 `data:application/octet-stream;base64,/9j/4AAQ...` 🔗 2-4절 |

⚠️ **base64는 암호화가 아니다.** 규칙이 공개돼 있고 **키가 없다** → 누구나 한 줄로 되돌린다. 비밀번호를 base64로 바꿔 노트북에 저장하면 **평문을 저장한 것과 같다**(`[프로젝트]08.GitHub로그인` CODE 21 시연). 🔗 9-8절.

| | 인코딩 | 암호화 | 해시 |
|---|---|---|---|
| 되돌리기 | **누구나** | **키가 있어야** | **불가능**(단방향) |
| 예 | base64, URL 인코딩 | AES, RSA | SHA-256 |

---

## 9-6. URL 인코딩 — 검색어를 주소에 넣기

### 개념

URL에는 **한글·공백·특수문자를 그대로 넣을 수 없다**(`?query=파이썬 기초`는 공백에서 주소가 끊긴다). 안전하지 않은 바이트를 **`%XX`(16진수)** 로 바꾸는 것이 URL 인코딩 — 층위 2.

```python
from urllib.parse import quote, unquote, urlencode
quote('파이썬 기초')              # '%ED%8C%8C%EC%9D%B4%EC%8D%AC%20%EA%B8%B0%EC%B4%88'  ← %XX 하나 = UTF-8 1바이트
unquote(...)                     # 되돌리기
urlencode({'query': '파이썬 기초', 'display': 10, 'sort': 'sim'})   # 여러 파라미터를 한 번에 (공백은 +)
requests.get(BASE, params=params)                                  # requests 가 내부에서 알아서 — 가장 편함
```

| 방법 | 언제 |
|---|---|
| 직접 조립 `f'{BASE}?query={quote("파이썬")}'` | URL **경로**에 검색어가 들어갈 때 |
| `urlencode(dict)` | 쿼리스트링을 문자열로 만들 때 |
| **`requests.get(url, params=dict)`** | requests를 쓸 때 (권장) |

### 코드 위치

```python
# [프로젝트]08.음식점정보 — URL '경로' 안에 검색어 → params 를 못 쓰니 quote
search_url = f"https://map.naver.com/p/search/{urllib.parse.quote(search_keyword)}"
search_url = f"https://map.kakao.com/?q={urllib.parse.quote(search_keyword)}"

# [프로젝트]08.Image — ⚠️ quote 없이 키워드를 그대로 붙임 (영문 'cat' 이라 동작했을 뿐)
search_url = f'https://www.google.com/search?tbm=isch&q={keyword}'
```
- Selenium의 `driver.get()`은 브라우저가 한글을 어느 정도 처리해 주지만, 공백·`&`·`#`이 든 키워드는 주소를 망가뜨린다. `quote()`를 습관으로.
- `[기초]05` CODE 2의 `res2.url`을 찍으면 `params`로 넘긴 `'파이썬'`이 `%ED%8C%8C...`로 바뀐 실제 URL이 보인다.

---

## 9-7. 실전 — CSV 인코딩 · requests의 `.text` vs `.content`

### CSV — `pd.read_csv()` 기본은 UTF-8

국내 공공데이터·엑셀에서 받은 CSV는 대부분 **CP949**다. 기본값으로 읽으면 `UnicodeDecodeError: ... invalid start byte`.

**인코딩을 모를 때 시도 순서**
1. `encoding='utf-8'` — 요즘 만든 파일 대부분
2. `encoding='cp949'` — 국내 공공데이터·엑셀 저장 파일
3. `encoding='utf-8-sig'` — 엑셀이 만든 UTF-8(BOM)
4. 그래도 안 되면 `encoding='latin-1'`로 **일단** 읽어 내용 확인(latin-1은 모든 바이트를 글자로 받아 주므로 에러는 안 나지만 한글은 깨진다)

### requests — `res.text = res.content.decode(res.encoding)`

| 속성 | 타입 | 설명 |
|---|---|---|
| `res.content` | bytes | 서버가 보낸 **원본** — 항상 그대로 |
| `res.text` | str | `res.encoding` 규칙으로 디코딩한 결과 — **encoding이 틀리면 깨짐** |

- 서버가 `Content-Type`에 charset을 안 알려 주면 requests가 **ISO-8859-1(latin-1)** 로 가정해 한글이 깨질 수 있다(PDF 3.8).

**대처 3가지**

```python
res.encoding = 'cp949'                                  # ① 직접 지정 (또는 'euc-kr')
res.encoding = res.apparent_encoding                    # ② 내용을 보고 추정
soup = BeautifulSoup(res.content, 'html.parser')        # ③ 원본 바이트를 파서에 — 파서가 <meta charset> 을 보고 판단 (가장 무난)
res.content.decode('euc-kr')                            # ④ 직접 디코딩
```

- `[기초]12` CODE 28은 네트워크 없이 `requests.models.Response()`를 만들어 `_content`에 CP949 바이트를 넣고, `res.encoding`을 `'utf-8'` ↔ `'cp949'`로 바꿔 가며 `res.text`가 깨졌다 돌아오는 것을 보여 준다. **content는 그대로, text만 바뀐다.**

---

## 9-8. 비밀정보 다루기 — `.env`에서 `SecretStr`까지

### 개념 — 단계별로 안전해진다

| 단계 | 방법 | 막는 것 | 한계 |
|---|---|---|---|
| 0 | 코드에 키를 직접 | — | Git에 올리는 순간 유출 |
| 1 | **`.env` + `python-dotenv`** | 코드와 비밀 분리 | `.env`는 **평문**. `.gitignore` 필수 |
| 2 | **`getpass`** | 입력값이 화면·노트북 출력에 안 남음 | 매번 입력 |
| 3 | **`keyring`** | OS 보안 저장소(Windows 자격 증명 관리자)에 **암호화 저장** 후 재사용 | |
| 4 | **PAT(Personal Access Token)** | 비밀번호 대신 — 권한 최소화·만료·폐기 가능 | |
| + | 출력 보호: `mask` / SHA-256 **지문** / **`SecretStr`** | 실수로 `print`해도 노트북에 안 남음 | |

### 코드 위치

**① `.env` — `[기초]03` CODE 12, `[프로젝트]01` CODE 19 · 28, `[스트림릿예제]08·09`**

```
# .env (프로젝트 루트, .gitignore 에 반드시 추가)
CLIENT_ID=발급받은_아이디
CLIENT_SECRET=발급받은_비밀키
GIT_USERNAME=...
```
```python
from dotenv import load_dotenv
import os
load_dotenv()                                   # 노트북: 현재 폴더의 .env
load_dotenv(BASE_DIR / '.env')                  # 스트림릿: 스크립트 기준 경로 🔗 8-9절
CLIENT_ID = os.getenv("CLIENT_ID")              # 없으면 None
print(CLIENT_ID[:4])                            # 확인은 '일부만'
```
- ⚠️ 수업 docx에는 **팀별 네이버 API 키가 평문으로** 적혀 있다(그래서 이 문서에는 옮기지 않았다). 공유 문서·노트북 출력·Git 어디에도 키 원문이 남지 않게 한다. `CLIENT_ID[:4]` 같은 부분 출력도 **노트북 파일에 저장**된다는 점을 기억.
- 스트림릿 앱은 `.env`가 없으면 `st.sidebar.text_input(..., type="password")`로 받는다 — 화면에 `●●●`로 가려진다.

**② getpass + keyring — `[프로젝트]08.GitHub로그인` CODE 18**

```python
def load_github_credentials(service="github-selenium-demo"):
    username = os.getenv("GIT_USERNAME") or input("GitHub 사용자명: ").strip()   # 아이디는 민감정보 아님
    secret = keyring.get_password(service, username) if HAS_KEYRING else None
    if not secret:
        secret = getpass.getpass(f"'{username}' 의 GitHub 비밀번호 또는 PAT 입력: ")   # 입력이 출력에 안 남음
        if HAS_KEYRING:
            keyring.set_password(service, username, secret)       # 다음부터는 입력 불필요
    return username, secret                                         # 튜플 → 언패킹 🔗 1-2절
GIT_USERNAME, GIT_PASSWORD = load_github_credentials()
```
- `try: import keyring ... except ImportError: HAS_KEYRING = False` — 라이브러리가 **없어도 동작하는** 선택적 의존성 패턴.
- 저장 확인: GUI `control /name Microsoft.CredentialManager` → Windows 자격 증명 / PowerShell `cmdkey /list` / 파이썬 `keyring.get_password(...)`(출력 주의). 삭제: `keyring.delete_password(SERVICE_NAME, GIT_USERNAME)`.

**③ 값을 확인하는 3가지 방법 — CODE 19~25**

| 방법 | 되돌리기 | 노트북에 저장돼도 안전? | 용도 |
|---|---|---|---|
| `base64` 인코딩 | 키 없이 누구나 | ❌ **평문과 동일** | 어깨너머 훔쳐보기 방어뿐 |
| **SHA-256 지문** | 불가능 | ✅ | "저장된 값이 입력한 값과 같은가" 확인 |
| **`SecretStr` 래퍼** | `.get()` 호출 시에만 | ✅ | **출력은 막고, 쓸 때는 원본** ★ |

```python
def mask(secret, keep=2):                        # 'gh****************** (len=20)'
    return f"{secret[:keep]}{'*' * (len(secret) - keep)} (len={len(secret)})" if secret else "(없음)"

def fingerprint(secret, n=8):                    # 원본 → 지문(가능), 지문 → 원본(불가능)
    return hashlib.sha256(secret.encode()).hexdigest()[:n]     # encode: 해시는 bytes 만 받는다 🔗 9-2절
fingerprint("hunter2_secret!")   # 'fd208923'
fingerprint("hunter2_secret?")   # '7f2b7564' — 한 글자만 달라도 완전히 다름(눈사태 효과)
```
- 지문은 `len()`으로 길이만 보는 것보다 정확하다(길이가 같아도 값은 다를 수 있지만, 지문이 같으면 값도 같다).

```python
class SecretStr:
    __slots__ = ("_value",)                      # 속성을 _value 하나로 제한
    def __init__(self, value): self._value = value or ""
    def get(self): return self._value            # 원본은 여기서만
    def __repr__(self): return f"SecretStr(***, len={len(self._value)})"
    __str__ = __repr__                           # print·f-string 도 가려지게 — 이 줄이 없으면 구멍
    def __len__(self): return len(self._value)
    def __bool__(self): return bool(self._value)
    def fingerprint(self, n=8): ...

GIT_PASSWORD = SecretStr(keyring.get_password(SERVICE_NAME, GIT_USERNAME))   # 꺼내는 즉시 감싼다
print(GIT_PASSWORD)          # SecretStr(***, len=15) — [pwd], {"pw": pwd} 안에서도 가려짐
driver.find_element(By.CSS_SELECTOR, 'input#password').send_keys(GIT_PASSWORD.get())   # 쓰는 순간에만
```
- `mask`·`fingerprint`는 **내가 조심해서 호출할 때만** 안전하다. `SecretStr`은 실수로 `print(GIT_PASSWORD)`를 쳐도 새지 않는다. 코드에서 `.get()`만 검색하면 노출 지점을 전부 찾을 수 있다. (pydantic 라이브러리의 `SecretStr`이 같은 방식.)
- 🔗 특수 메서드는 1-10절.

---

## 9-9. 증상별 진단표

| 증상 | 원인 | 해결 | 절 |
|---|---|---|---|
| `UnicodeDecodeError: invalid start byte` | 읽는 인코딩이 틀림(대개 CP949 파일을 UTF-8로) | `encoding='cp949'` / `'utf-8-sig'` | 9-7 |
| 글자가 `ìì`, `�ѱ�`처럼 깨짐 | 조용히 잘못 디코딩 | `res.encoding` 지정 / `BeautifulSoup(res.content)` | 9-7 |
| 키 이름이 같아 보이는데 조회가 None | 앞에 BOM(`﻿`)이나 공백 | `repr()`로 확인 → `utf-8-sig` / `lstrip('﻿')` | 9-4 |
| `json` — `Unexpected UTF-8 BOM` | BOM | `.decode('utf-8-sig')` 후 파싱 | 9-4 |
| 엑셀에서 연 CSV 한글 깨짐 | BOM 없는 UTF-8 | `to_csv(..., encoding='utf-8-sig')` | 9-4 |
| URL 요청 실패·주소 잘림 | 한글·공백 미인코딩 | `quote()` / `params=` | 9-6 |
| 그래프 한글이 □ | 한글 폰트 미설정 | `rcParams['font.family']` | 6-2 |
| JSON 파일 안 한글이 `\uXXXX` | `ensure_ascii=True`(기본) | `json.dump(..., ensure_ascii=False)` | 1-8 |
| MySQL에 이모지 저장 실패 | `utf8`(3바이트) | `utf8mb4` | 7-3 |
| 정규식 `SyntaxWarning: invalid escape` | raw 문자열 아님 | `r'...'` | 1-9 |

---

## 9-10. PART 9 자가 점검 질문

1. `'안녕'`을 UTF-8과 CP949로 인코딩했을 때 바이트 수는? 영문 `'AB'`는?
2. `encode()`와 `decode()`의 방향은? 둘이 "짝"이어야 한다는 말의 뜻은?
3. 깨짐의 두 유형 중 더 위험한 쪽과 그 이유는?
4. 인코딩의 두 층위를 타입(`str`/`bytes`)으로 설명하라.
5. `base64.b64encode('안녕'.encode())` 결과를 print하면 `b'...'`가 붙는 이유는?
6. BOM이 pandas에서는 문제없는데 `csv`·`json` 모듈에서 사고를 내는 이유는?
7. 엑셀용 CSV를 저장할 때 일부러 `utf-8-sig`를 쓰는 이유는?
8. `res.text`와 `res.content`의 관계를 식으로 쓰라. 한글이 깨질 때 가장 무난한 해결은?
9. `requests.get(url, params=...)`와 `quote()`는 각각 언제 쓰나?
10. 인코딩·암호화·해시를 "되돌리기" 기준으로 구분하라.
11. `SecretStr`에서 `__str__ = __repr__` 한 줄이 없으면 어떤 구멍이 생기나?
12. `.env`를 쓸 때 꼭 함께 해야 할 Git 설정은?
