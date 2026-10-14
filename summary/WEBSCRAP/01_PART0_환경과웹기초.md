# PART 0. 환경과 웹의 기초

## 0-1. 데이터 수집·분석의 전체 과정

### 개념

| 단계 | 하는 일 | 이 문서의 파트 |
|---|---|---|
| **수집** | 오픈 API, 웹 스크래핑(크롤링) | PART 2 · 3 |
| **가공** | 데이터 랭글링(Data Wrangling), 데이터 클리닝 | PART 4 · 5 |
| **분석** | 통계 분석, 머신러닝(딥러닝) | PART 4 · 8-12(추천) |
| **저장** | 관계형 DB / NoSQL | PART 7 |
| **시각화·공유** | 그래프, 엑셀/CSV/JSON/YAML 파일, 메일·메신저·Slack, 웹 앱 | PART 6 · 8 |

- **데이터 랭글링**: 날것의 데이터를 분석 가능한 형태로 바꾸는 전 과정(형 변환, 결측 처리, 열 쪼개기 등). 이 과정이 실무 시간의 대부분이다.
- 강의 모듈 순서: 개발 환경 → 수집(Requests·BeautifulSoup·Selenium) → 분석·시각화(Pandas·Matplotlib·Seaborn) → 저장(SQLAlchemy·PyMySQL). 이 문서는 여기에 Streamlit(공유)과 인코딩(수집 내내 마주치는 문제)을 더했다.

---

## 0-2. 개발 환경 — Anaconda · Jupyter · VSCode · Colab

### 개념

| 도구 | 설명 |
|---|---|
| **Anaconda** | 과학 계산용 파이썬 배포판. NumPy·SciPy·matplotlib·pandas·IPython·Jupyter·scikit-learn을 **한 번에** 설치 |
| **Miniconda** | Anaconda의 핵심만 추린 것(conda + Python). 설치가 빠르고 가볍다 |
| **IPython** | 기본 파이썬 쉘(REPL)에 기능을 더한 대화형 쉘. Jupyter의 전신 |
| **Jupyter Notebook / Lab** | 웹 브라우저에서 셀 단위로 실행 + 마크다운 문서화. `jupyter notebook` / `jupyter lab` → `http://localhost:8888` |
| **Google Colab** | 클라우드 Jupyter. GPU/TPU 사용 가능. 구글 드라이브를 마운트해 파일을 읽는다 |

- `conda update conda` 로 conda 업데이트, `python --version` 으로 버전 확인.
- **`.py` vs `.ipynb`** — `main.py`는 `python main.py`로 **파일 전체**를 실행한다. `hello.ipynb`는 **셀(줄) 단위**로 실행할 수 있다. Streamlit은 `.py`만 된다(8-1절).
- **VSCode 확장**: Python(커널 자동 감지), Jupyter(.ipynb 실행·디버깅), Black Formatter.

**Jupyter 단축키** (docx + `[기초]00` 병합)

| 키 | 동작 |
|---|---|
| `Ctrl + Enter` | 현재 셀만 실행 |
| `Shift + Enter` | 실행 + 아래 셀로 이동(없으면 추가) |
| `Alt + Enter` | 실행 + **항상** 아래에 새 셀 추가 |
| `Esc → b` / `Esc → a` | 아래 / 위에 셀 추가 |
| `Esc → dd` | 셀 삭제 |
| `Esc → y` / `Esc → m` | 코드 셀 / 마크다운 셀로 변경 |
| `Ctrl + Shift + -` | 셀 나누기 |
| `Tab` / `Shift + Tab` | 자동완성 / 함수 간단 문서 보기 |

### 코드 위치

**① 마크다운 셀 문법 — `[기초]00python_basic.ipynb` MD 0~4**

```markdown
* ***저장***            ← 굵은 기울임
* ~~머신러닝~~          ← 취소선
> Text that is a quote  ← 인용
\begin{equation*} ... \end{equation*}   ← LaTeX 수식
| This | is | ...       ← 표
```

**② Colab에서 드라이브 파일 읽기 — `[판다스]Colab_Test.ipynb`**

```python
from google.colab import drive
drive.mount('/content/drive')                      # 구글 드라이브를 /content/drive 에 연결
data_path = '/content/drive/My Drive/Colab Notebooks/data'
data = pd.read_csv(os.path.join(data_path, 'data_draw_korea.csv'))
```

**해설**
- Colab은 내 PC가 아니라 **구글 서버의 가상 머신**에서 돈다. 그래서 `data/xxx.csv` 같은 로컬 상대경로가 없다. 드라이브를 마운트해서 경로를 만든다.
- 🔗 경로를 `os.path.join`으로 합치는 이유는 1-8절.

---

## 0-3. pip — 패키지 설치와 확인

### 개념

| 명령어 | 역할 |
|---|---|
| `pip install 패키지` / `==1.2.0` / `--upgrade` | 설치 / 특정 버전 / 업그레이드 |
| `pip uninstall 패키지` | 삭제 |
| `pip list` | **설치된 전체 목록** |
| `pip show 패키지` | **패키지 하나**의 상세 정보(버전·위치·의존성) |
| `pip freeze` → `pip install -r requirements.txt` | `이름==버전` 목록 저장 → 한 번에 설치 |

⚠️ **`pip list`(전체 목록) vs `pip show`(개별 상세)** — 시험에서 뒤집어 내기 좋은 포인트.
⚠️ `pip` 자체는 파이썬에 딸려 오는 관리 도구지만, `requests`·`pandas`·`bs4` 등은 **서드파티**라 따로 설치해야 한다.

### 코드 위치 — 노트북 안에서 `!pip` 와 `%pip`

`[프로젝트]01` CODE 1~2, `04` CODE 18~19, `08.GitHub로그인` CODE 1~4, `09` CODE 66~67

```python
! pip show requests          # '!' = 셸 명령을 그대로 실행
%pip install xlsxwriter      # '%' = IPython 매직 명령
%pip install -q wordcloud    # -q(quiet): 설치 로그를 출력하지 않음
%pip show -q selenium webdriver-manager
```

**해설**
- `!pip`은 운영체제 셸의 `pip`을 부른다. PC에 파이썬이 여러 개면 **노트북 커널과 다른 파이썬**에 설치될 수 있다.
- `%pip`은 **지금 돌고 있는 커널의 파이썬**에 설치한다. 그래서 노트북 안에서는 `%pip`이 더 안전하다.
- 설치 직후 `Note: you may need to restart the kernel` 메시지가 뜨면 커널을 재시작해야 새 패키지가 import 된다(`03` CODE 71 출력).
- `pip show` 결과의 `Requires`(내가 의존하는 것) / `Required-by`(나에게 의존하는 것)도 읽을 줄 알아야 한다. 예: `requests`는 `certifi, charset-normalizer, idna, urllib3`를 필요로 한다(`01` CODE 1 출력).

---

## 0-4. 웹의 이해 — HTTP · SSR/CSR · Ajax

### 개념

**웹 페이지·서버·클라이언트**
- 웹 페이지는 대부분 **HTML**로 된 문서다. 클라이언트(브라우저)가 서버에 요청하면 서버가 콘텐츠를 응답하고, 브라우저가 HTML을 해석해 화면에 그린다.

**HTTP (Hyper Text Transfer Protocol)**
- 서버-클라이언트 사이의 통신 규약. 메시지는 **시작줄 · 헤더(Header) · 본문(Body)** 으로 구성.
- 메서드는 9개지만 주로 **GET(조회)** 과 **POST(등록)** 만 쓴다.

| 상태 코드 | 뜻 | 실습에서 본 곳 |
|---|---|---|
| 200 OK | 성공 | 거의 모든 셀 |
| 403 Forbidden | 거부됨 — **IP 차단·봇 판정** 가능성 | 0-5절 체크리스트 |
| 404 Not Found | 없는 주소 | `[기초]05` `httpbin.org/status/404` |
| 500 Internal Server Error | 서버 내부 오류 | — |

**렌더링 방식 — 스크래핑 도구 선택을 결정하는 개념** (docx)

| | SSR (Server Side Rendering) | CSR (Client Side Rendering) |
|---|---|---|
| 서버가 보내는 것 | **데이터가 이미 들어간 HTML** | **데이터만**(JSON/XML) + 빈 HTML 1개 |
| 화면을 그리는 곳 | 서버 | 브라우저의 JavaScript |
| 예 | JSP·Thymeleaf(Java), PHP, Next.js·Nuxt.js | React·Vue·Svelte (SPA: index.html 1개) |
| SEO(검색엔진 최적화) | 유리 | 불리 |
| 스크래핑 | `requests + BS4`로 HTML 파싱 | **JSON API를 직접 호출**하거나 Selenium |

- **Ajax (Asynchronous JavaScript and XML, 2005)**: "서버는 데이터만 보내라(→ REST API), 화면은 클라이언트가 그린다." 비동기 통신을 담당하는 JS 객체가 **XMLHttpRequest(XHR)**.
- **동기(Blocking) vs 비동기(Non-Blocking)**: 동기는 응답이 올 때까지 기다리고, 비동기는 요청을 보내 놓고 다른 일을 한다.
- React는 CSR만, Next.js는 CSR+SSR 둘 다 제공.

### 코드 위치 — 같은 사이트 안에 SSR과 CSR이 섞여 있다

| 실습 | SSR 부분 (HTML 파싱) | CSR/Ajax 부분 (JSON 직접 호출) |
|---|---|---|
| `[프로젝트]04` Melon 100 | 차트 페이지·곡 상세 페이지 | **좋아요 수** `getSongLike.json?contsIds=...` |
| `[프로젝트]05_01/05_02` 국회의원 | 의원 **상세 페이지**(사무실·보좌관) | **의원 목록** `findNaasThx01.json?pageIndex=` |
| `[프로젝트]06` 팟빵 | — | 에피소드 목록 `app-api6.podbbang.com/.../episodes` |

**해설**
- 브라우저에서 좋아요 숫자가 **보이는데** `requests`로 받은 HTML에는 **없다** → CSR이라는 신호다. 크롬 개발자 도구 **Network 탭 → Fetch/XHR** 필터에서 그 숫자를 담은 JSON 요청을 찾아 그 URL을 직접 부르면 된다(docx에 적힌 `getSongLike.json` 주소가 그렇게 찾은 것).
- 🔗 자세한 코드는 2-7절, "JSON API로도 안 되면 Selenium"은 PART 3.

---

## 0-5. 웹 데이터 수집의 방법과 주의사항

### 개념

**수집 3단계**: ① 대상 선정(목적 데이터·사이트 정하기) → ② 수집 → ③ 정리.

| 용어 | 뜻 |
|---|---|
| **스크래핑(Scraping)** | 각각의 페이지에서 정보를 **추출하는 행위** |
| **크롤러(Crawler)** | 추출을 **자동으로 반복**하는 프로그램 |
| 반자동화 | 페이지 지정(수동) → 내려받아 추출(프로그램) → 저장(수동/프로그램) |
| 완전 자동화 | 전 과정을 프로그램으로 + 스케줄링. 단, **사이트 구조 변화에 취약** |

**주의사항**
1. **저작권** — 웹의 정보는 저작물이다. 읽을 수 있다고 마음대로 쓸 수 있는 것이 아니다(저작권법 제30조의 '정보 해석 목적 복제·변환'도 범위가 있다).
2. **리소스 압박·업무 방해** — 서버 자원을 독점하면 다른 사용자가 못 쓴다. 무한 크롤러는 업무방해 혐의가 적용될 수 있다.
3. **API 먼저 확인** — 공식 API가 있으면 그것을 쓴다.

**사람처럼 보이기 체크리스트** (PDF 5.3)

| 항목 | 이유 | 실습 코드 |
|---|---|---|
| **User-Agent** 헤더 설정 | 기본값 `python-requests/2.x`·`Python-urllib/3.x`는 봇으로 판정돼 거부될 수 있다 | 뉴스·Melon·국회의원 전부 |
| **Referer** 헤더 설정 | 이미지·파일 서버가 "어느 페이지에서 왔는지" 검사 | 웹툰·팟빵 |
| 이동 속도 조절(지연) | 너무 빠른 연속 요청은 기계 패턴 | 팟빵 `sleep(0.5)`, Selenium `time.sleep` |
| 403 → IP 차단 의심 | 다른 IP·장소에서 재시도 | — |
| JS 실행 전 페이지는 비어 있을 수 있음 | CSR | 0-4절 |
| POST 시 **서버가 기대하는 모든 값** 전송 | hidden 필드·쿠키 누락 시 실패. Network 탭으로 확인 | — |
| 헤더를 주기적으로 변경 | — | — |

---

## 0-6. 라이브러리 지도

| 단계 | 라이브러리 | 한 줄 설명 | 확인 명령 |
|---|---|---|---|
| 수집 | **requests** | HTTP 통신. 표준 `urllib`와 같은 역할을 더 간결하게 | `pip show requests` |
| 수집 | **beautifulsoup4** | HTML/XML 파서(pulling data) | `pip show beautifulsoup4` |
| 수집 | **selenium** | 브라우저 자동화. requests+BS4 역할을 다 하지만 **느리다**. 브라우저별 WebDriver 사용 | `pip show selenium` |
| 분석 | **pandas** · numpy | 표 데이터 분석 / 수치 계산 | `pip show pandas` |
| 시각화 | **matplotlib** · **seaborn** · wordcloud | 그래프 / 통계 그래프 / 단어 구름 | |
| 저장 | **pymysql** | Pure Python MySQL 클라이언트. **코드 안에 SQL문을 넣어** DB 연동 | |
| 저장 | **sqlalchemy** | ORM. **SQL 없이** 객체를 테이블에 매핑 (Class⇔Table, Object⇔Row, 변수⇔Column) | `pip show sqlalchemy` |
| 편집기 | jupyter | 노트북 | `pip show jupyter` |
| 웹 | Django · Flask · **Streamlit** · Gradio | 웹 프레임워크 / 데이터 앱 / ML·LLM 챗봇 데모 | `pip show streamlit` |
| 기타 | python-dotenv · keyring · xlsxwriter · streamlit-aggrid · scikit-learn · Pillow | `.env` 읽기 / OS 보안 저장소 / 엑셀 서식 / 표 위젯 / TF-IDF / 이미지 | |

**참고 학습 자료** (docx): 점프 투 파이썬(wikidocs.net/book/1), 생활코딩 파이썬 입문, 가천대 파이썬 강의(TEAMLAB), pandas 공식 "10 Minutes to pandas"(번역본 dataitgirls2.github.io/10minutes2pandas), Streamlit 30일 챌린지(wikidocs.net/book/14530).
