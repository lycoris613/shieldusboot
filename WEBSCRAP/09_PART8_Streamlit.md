# PART 8. Streamlit — 분석 결과를 웹 앱으로

## 8-1. Streamlit이란

### 개념 (docx)

- **파이썬 코드만으로** 빠르게 웹 기반 데이터 앱을 만드는 오픈소스 프레임워크. HTML/CSS/JS를 몰라도 되고, **위→아래로 실행되는 스크립트가 그대로 웹 화면**이 된다.
- **목적**: 분석 결과를 웹으로 공유 · 대시보드 제작 · ML/AI 모델 시연 · 비개발자와 협업
- **주요 기능**: UI 컴포넌트(한 줄) · matplotlib/seaborn/plotly 연동 · 파일 업로드/다운로드 · 인터랙티브 · 간편 배포(Streamlit Community Cloud, Docker, Heroku) · ML 모델 연동
- **사용 예**: 크롤링 데이터 대시보드 / 입력에 따라 그래프 갱신 / CSV 업로드 분석 / 모델 결과 웹 인터페이스
- 비슷한 도구: **Gradio**(머신러닝 결과·LLM 챗봇 데모에 특화). 범용 웹 프레임워크는 Django·Flask.

**실행**

```bash
pip install streamlit
streamlit run streamlit_examples/01python_basic_streamlit.py
```
- 브라우저가 자동으로 열리고, **코드를 저장할 때마다** 화면이 갱신된다.
- ⚠️ Jupyter 셀에서는 `st.*`가 동작하지 않는다. 반드시 `.py` 파일 + `streamlit run`. (`streamlit_completever.ipynb`도 "복습용 정리"이지 실행용이 아니라고 밝혀 두었다.)

---

## 8-2. 핵심 동작 원리 — "위젯을 건드리면 스크립트 전체가 다시 실행된다"

이 한 문장이 Streamlit의 모든 것을 설명한다.

```
사용자가 슬라이더를 움직임
      ↓
스크립트 1번째 줄부터 끝까지 다시 실행
      ↓
위젯 함수는 '방금 사용자가 고른 값'을 반환 → 변수에 새 값 → 화면이 새 값으로 다시 그려짐
```

그래서 두 가지 문제가 생기고, 각각의 해결 도구가 있다.

| 문제 | 해결 도구 | 절 |
|---|---|---|
| 재실행마다 **변수가 초기화**된다 (카운터가 안 늘어남, 검색 결과가 사라짐) | `st.session_state` | 8-5 |
| 재실행마다 **느린 작업**(CSV 읽기, API 호출)을 반복한다 | `@st.cache_data` | 8-6 |

| | `session_state` | `cache_data` |
|---|---|---|
| 저장하는 것 | **값**(사용자 상태) | **함수 결과** |
| 범위 | **사용자(브라우저 세션)별** | 같은 인자면 **모든 사용자 공유** |
| 목적 | 재실행 간 기억 | 다시 계산/로딩 안 하기 |

---

## 8-3. 출력 API

| 함수 | 용도 |
|---|---|
| `st.set_page_config(page_title=, page_icon=, layout='wide')` (또는 `'centered'`) | 페이지 설정. ⚠️ **다른 st 명령보다 먼저** 호출 |
| `st.title` / `header` / `subheader` / `caption` | 큰 제목 / 중간 / 작은 / 작은 회색 글씨 |
| `st.write(아무거나)` | 만능 — 문자열·숫자·dict·DataFrame·그래프를 타입에 맞게 표시. 여러 인자 가능 |
| `st.markdown('**굵게** `코드`')` | 마크다운 |
| `st.divider()` | 가로 구분선 |
| `st.info` / `success` / `warning` / `error` | 알림 상자 4종 |
| `st.dataframe(df, use_container_width=True)` | **동적** 표(정렬·검색·스크롤), 컨테이너 폭에 맞춤 |
| `st.table(df)` | **정적** 표(전부 펼쳐서 고정) |
| `st.metric('평균 점수', 값, delta='2.3')` | 대시보드 숫자 카드(증감 화살표) |
| `st.balloons()` | 풍선 애니메이션 |
| `with st.spinner('검색 중...'):` | 블록 실행 중 로딩 표시 |

### 코드 위치 — `[스트림릿기초]01_기본출력.py`, `[스트림릿예제]01python_basic_streamlit.py` 1~2절

```python
st.set_page_config(page_title='1기본', layout='wide')      # 가장 먼저
st.write({'딕셔너리도': '표로 보여준다', 'key': 'value'})
st.dataframe(df, use_container_width=True)                 # 정렬·검색 가능
st.table(df)                                               # 정적
col1, col2 = st.columns(2)
col1.metric('평균 점수', f"{df['점수'].mean():.1f}", delta='2.3')
```

---

## 8-4. 입력 위젯

### 개념 — 위젯 = "사용자가 고른 값을 **반환하는 함수**"

| 위젯 | 반환 | 예 (실습 코드) |
|---|---|---|
| `st.text_input(라벨, 기본값, type='password')` | str | 이름, 검색어, API 키(가려서) |
| `st.text_area` | str(여러 줄) | 자기소개, 의견 |
| `st.number_input(라벨, min, max, 기본, step)` | 숫자 | 나이, **최소 할인 금액** `(0, 100000, 20000, 1000)` |
| `st.slider(라벨, min, max, 기본)` | 숫자 | 나이, 결과 수 |
| `st.slider(..., (시작, 끝))` | **튜플** (범위) | 제작 연도 `(2015, 2021)` → `year_min, year_max = ...` |
| `st.selectbox(라벨, 목록)` | 선택 1개 | 도시, 광역시도 |
| `st.multiselect(라벨, 목록, default=)` | **리스트** | 취미, 유형 필터 |
| `st.radio(라벨, 목록, horizontal=True, index=0)` | 선택 1개 | 성별, 정렬 방식 |
| `st.checkbox(라벨)` | bool | 동의 |
| `st.button(라벨)` | bool (**눌린 그 순간만** True) | 제출, 검색 |
| `st.date_input` / `st.time_input` | date / time | `datetime.date.today()` 기본값 |
| `st.color_picker(라벨, '#00f900')` | 색 코드 str | |
| `st.file_uploader(라벨, type='csv')` | 업로드 파일 객체 또는 None | → `pd.read_csv(uploaded_file)` |

⚠️ **`st.button`은 누른 직후의 재실행 한 번만 True**이고 다음 재실행에서 다시 False다. 그래서
- 버튼으로 값을 **누적**하려면 `session_state`가 필요하고(8-5절),
- 버튼 **안에서만** 보여 준 결과는 다른 위젯을 건드리는 순간 사라진다(네이버 검색 앱이 결과를 `session_state`에 넣는 이유).

### 코드 위치

```python
# [스트림릿기초]02_위젯.py — 동의해야 제출 버튼이 의미 있음
if not agree:
    st.warning('약관에 동의해야 제출할 수 있습니다.')
elif st.button('제출'):
    st.success(f'{name}님 제출 완료!'); st.balloons()

# [스트림릿예제]03streamlit_survey_app.py — 입력 검증 후 요약
if st.button("제출하기"):
    if name == "":
        st.warning("이름을 입력해 주세요!")
    else:
        st.write(f"**관심 분야:** {', '.join(interests) if interests else '없음'}")   # multiselect 리스트 → 문자열

# [스트림릿예제]02streamlit_uicomponent.py — 위젯 전 종류 데모 (date_input, time_input, color_picker, file_uploader)

# [스트림릿예제]08 blog — 라디오 라벨에서 API 값 뽑기
sort_option = st.sidebar.radio("정렬", ["sim (정확도)", "date (날짜)"], index=0)
sort_value = "date" if sort_option.startswith("date") else "sim"
```

---

## 8-5. `st.session_state` — 재실행해도 값 유지

### 개념 — 초기화 공식

```python
if 'key' not in st.session_state:       # 최초 1회만
    st.session_state.key = 초깃값
```
- 딕셔너리처럼(`st.session_state['count']`)도, 속성처럼(`st.session_state.count`)도 쓸 수 있다.
- 이 `if`가 없으면 재실행마다 초깃값으로 덮어써서 의미가 없다.

### 코드 위치

**① 잘못된 카운터 vs 올바른 카운터 — `[스트림릿기초]03_session_state.py`**

```python
bad_count = 0
if st.button('❌ 잘못된 카운터'):
    bad_count += 1
st.write('잘못된 카운터:', bad_count)     # 아무리 눌러도 0 또는 1 — 재실행마다 0 으로 초기화

if 'count' not in st.session_state:
    st.session_state.count = 0
if col1.button('⭕ 올바른 카운터 +1'):
    st.session_state.count += 1
if col2.button('초기화'):
    st.session_state.count = 0

if 'history' not in st.session_state:     # 검색 기록 누적
    st.session_state.history = []
if st.button('검색') and keyword:
    st.session_state.history.append(keyword)

with st.expander('session_state 전체 보기'):
    st.write(dict(st.session_state))      # 디버깅
```

**② 검색 결과 보존 — `[스트림릿예제]08 book` · `08 blog` · `09 local`**

```python
if 'search_results' not in st.session_state:
    st.session_state.search_results = []
    st.session_state.books_df = pd.DataFrame()

if search_button and client_id and client_secret:
    with st.spinner('검색 중...'):
        st.session_state.search_results = search_naver_api('book', search_query, display_count)
        if st.session_state.search_results:
            st.session_state.books_df = pd.DataFrame(st.session_state.search_results)

with tab2:                                # 필터 값을 바꿔 재실행돼도 검색 결과는 살아 있다
    if not st.session_state.books_df.empty:
        filtered = filter_and_sort_books(st.session_state.books_df, min_discount)
```
- 검색(느리고 API 호출 횟수 제한이 있음)은 **버튼을 눌렀을 때만**, 필터(빠름)는 **매 재실행마다**. 이 분리가 session_state의 실전 용도다.

---

## 8-6. `@st.cache_data` · `st.stop` · `st.rerun`

### 개념

- 함수에 `@st.cache_data`를 붙이면 **같은 인자**로 호출될 때 함수 본문은 **최초 1회만** 실행되고, 이후엔 저장된 결과를 돌려준다(반환값의 복사본).
- `st.cache_data.clear()` — 캐시 전체 비우기 / `st.rerun()` — 스크립트 강제 재실행 / `st.stop()` — 여기서 실행 중단(데이터가 없을 때 아래 코드 보호).

### 코드 위치

```python
# [스트림릿기초]04_cache.py — 효과 측정
@st.cache_data
def load_data(path):
    time.sleep(2)                 # 느린 작업 흉내
    return pd.read_csv(path)
start = time.time(); df = load_data(CSV_PATH); elapsed = time.time() - start
st.metric('로딩 시간', f'{elapsed:.2f} 초')     # 처음 2초 이상 → 슬라이더를 움직이면 0초대
if st.button('캐시 지우기'):
    st.cache_data.clear(); st.rerun()

# [스트림릿예제]11 dashboard — 업로드 파일이 있으면 우선, 없으면 기본 파일, 둘 다 없으면 중단
@st.cache_data
def load_data(path_or_buffer): ...           # 전처리까지 캐시 (to_datetime, fillna)
if uploaded is not None:        df = load_data(uploaded)
elif os.path.exists(DATA_PATH): df = load_data(DATA_PATH)
else:
    st.error("기본 데이터 파일이 없습니다 ..."); st.stop()
```

⚠️ `[스트림릿예제]10 recommender`는 데이터 로딩만 캐시하고, **TF-IDF 행렬 계산**(8천여 건 전체)은 캐시하지 않아서 selectbox로 작품을 바꿀 때마다 다시 계산한다. 무거운 계산도 `@st.cache_data`(또는 모델 객체는 `@st.cache_resource`) 함수로 빼는 것이 좋다.

---

## 8-7. 레이아웃

| 컴포넌트 | 용도 | 두 가지 문법 |
|---|---|---|
| **사이드바** | 필터·옵션을 본문과 분리 | `st.sidebar.slider(...)` / `with st.sidebar: st.slider(...)` |
| **columns** | 가로 나란히 (metric 카드) | `c1, c2 = st.columns(2); c1.metric(...)` / `with col1: ...` |
| **tabs** | 같은 데이터를 여러 관점으로 | `tab1, tab2 = st.tabs(['표', '차트']); with tab1: ...` |
| **expander** | 기본은 접힘, 부가 정보 | `with st.expander('원본 데이터 정보'): ...` |

### 코드 위치 — `[스트림릿기초]05_레이아웃.py`

```python
with st.sidebar:
    kind = st.selectbox('종류', ['전체'] + df['type'].dropna().unique().tolist())
    year_min, year_max = st.slider('제작 연도', int(df['release_year'].min()),
                                   int(df['release_year'].max()), (2015, 2021))
filtered = df[df['release_year'].between(year_min, year_max)]
if kind != '전체':
    filtered = filtered[filtered['type'] == kind]

c1, c2, c3 = st.columns(3)
c1.metric('전체 작품', f'{len(df):,}'); c2.metric('필터 결과', f'{len(filtered):,}')
c3.metric('비율', f'{len(filtered) / len(df) * 100:.1f}%')

tab1, tab2, tab3 = st.tabs(['📋 목록', '📊 연도별', '🎬 장르'])
with tab3:
    st.bar_chart(filtered['listed_in'].str.split(', ').explode().value_counts().head(10))   # 🔗 4-9절
```
- `slider`에 `int(...)`로 감싼 이유: `min()`의 결과가 numpy 정수(`np.int64`)라 위젯이 타입을 거부할 수 있다.
- `['전체'] + ....tolist()` — numpy 배열을 파이썬 리스트로 바꿔야 `+`로 이어 붙일 수 있다.
- 이 파일의 `print(...)`는 **브라우저가 아니라 터미널**에 찍힌다(디버깅용). `[스트림릿예제]08 blog`의 `print(res.json())`도 마찬가지.

---

## 8-8. 차트 — 내장 차트 vs matplotlib

| | `st.bar_chart` · `st.line_chart` · `st.map` | `st.pyplot(fig)` |
|---|---|---|
| 코드 | 한 줄 (Series/DataFrame을 그대로) | Figure/Axes 직접 구성 |
| 커스터마이징 | 제한적 | 제목·색·격자·폰트 자유 |
| 인터랙션 | 확대·툴팁 기본 제공 | 정지 이미지 |
| 언제 | 빠르게 흐름 확인 | 발표·보고서용 |

- 내장 차트는 **인덱스가 x축**이 된다 → `st.bar_chart(df.set_index("company"))`.
- `st.map(df)` — `lat`, `lon`(또는 latitude/longitude) 열이 있는 DataFrame을 지도에 점으로.
- `st.pyplot(fig)` — `fig`를 **명시적으로** 넘긴다. matplotlib 한글 폰트 설정은 스트림릿에서도 필요.

```python
# [스트림릿기초]06_차트.py — 두 방식 비교
st.bar_chart(by_year)
fig, ax = plt.subplots(figsize=(10, 4))
ax.bar(by_year.index, by_year.values, color='#E50914'); ax.grid(axis='y', alpha=0.3)
st.pyplot(fig)

# [스트림릿예제]00streamlit_basic.py — seaborn 도 Axes 를 받아 그린 뒤 fig 를 넘기면 된다
fig, ax = plt.subplots()
sns.countplot(data=df, x='선거구', hue='성별', palette={'남': 'navy', '여': 'orange'}, ax=ax)
st.pyplot(fig)
```

---

## 8-9. 경로 문제 — 실행 위치(cwd) vs 스크립트 위치(`__file__`)

### 개념

- `streamlit run`의 **기준 폴더(cwd)는 명령을 실행한 폴더**다. 스크립트가 있는 폴더가 아니다.
- 그래서 `'data/x.csv'`, `'./../data'` 같은 상대 경로는 **어디서 실행하느냐에 따라** 깨진다.
- `Path(__file__).resolve()` — **이 스크립트 파일의 절대 경로**. 여기서 폴더를 거슬러 올라가면 실행 위치와 무관해진다.

| 코드 | 의미 |
|---|---|
| `Path(__file__).resolve()` | `.../project/streamlit_examples/04streamlit_korea.py` |
| `.parent` = `.parents[0]` | `.../project/streamlit_examples` |
| `.parent.parent` = `.parents[1]` | `.../project` (프로젝트 루트) |
| `BASE_DIR / 'data' / 'x.csv'` | `/` 연산자로 경로 결합(pathlib) |

### 코드 위치

```python
# [스트림릿예제]04~11 공통
BASE_DIR = Path(__file__).resolve().parent.parent       # 프로젝트 루트 (streamlit_examples 의 상위)
data = pd.read_csv(BASE_DIR / 'data' / 'data_draw_korea.csv')
load_dotenv(BASE_DIR / '.env')                          # .env 도 같은 방식으로

# [스트림릿기초]04~06
CSV_PATH = Path(__file__).resolve().parents[1] / 'data' / 'netflix_titles.csv'
```
- 두 표기는 같은 것이다(`parents[1]` = `parent.parent`). `[스트림릿기초]04_cache.py` 주석은 `parents[1]=python_basic`, `streamlit_completever`는 `parents[1]=프로젝트 루트`라고 적어 서로 다르다 — **`data/` 폴더가 실제로 어느 폴더 아래 있는지**에 맞춰 몇 단계 올라갈지 정하면 된다.
- 🔗 노트북의 `'../data/...'`(`[기초]08~10`)도 같은 문제다. 노트북은 **노트북 파일이 있는 폴더**가 cwd라서 상대 경로가 비교적 안정적이지만, 폴더를 옮기면 깨진다.

---

## 8-10. 파일 업로드·다운로드

```python
# 업로드 — [스트림릿예제]02, 11
uploaded = st.sidebar.file_uploader("CSV 업로드 (선택)", type=["csv"])
if uploaded is not None:
    df = pd.read_csv(uploaded)              # 업로드 객체를 파일 경로처럼 바로 넘김

# 다운로드 — [스트림릿예제]11
csv = df[show_cols].to_csv(index=False, encoding="utf-8-sig")     # 경로 없이 호출 → 문자열 반환
st.download_button("결과 CSV 다운로드", csv, "netflix_filtered.csv", "text/csv")
```
- `to_csv()`에 경로를 안 주면 파일을 쓰지 않고 **CSV 문자열을 반환**한다.
- ⚠️ 경로 없이 호출하면 결과가 파이썬 `str`이라 `encoding="utf-8-sig"`(BOM)는 적용되지 않는다. 엑셀에서 바로 열 CSV라면 `csv.encode("utf-8-sig")`로 **바이트**를 만들어 `download_button`에 넘겨야 BOM이 붙는다 🔗 9-4절.

---

## 8-11. 애드온 — streamlit-aggrid

`pip install streamlit-aggrid` (import 이름은 `st_aggrid`). 엑셀 같은 **편집·정렬·필터·페이지네이션** 표를 제공한다.

```python
# [스트림릿예제]07streamlit_korea_aggrid.py
from st_aggrid import AgGrid, GridOptionsBuilder, ColumnsAutoSizeMode, GridUpdateMode

gb = GridOptionsBuilder.from_dataframe(sido_df)
gb.configure_default_column(editable=True, groupable=True, value=True, enableRowGroup=True)   # 셀 편집 허용
gb.configure_pagination(paginationAutoPageSize=False, paginationPageSize=10)
gb.configure_side_bar()                 # 오른쪽 필터·열 패널
gb.configure_selection('single')        # 행 하나 선택
grid_response = AgGrid(sido_df, gridOptions=gb.build(), height=300,
                       update_mode=GridUpdateMode.MODEL_CHANGED,          # 값이 바뀌면 앱 재실행
                       columns_auto_size_mode=ColumnsAutoSizeMode.FIT_CONTENTS, theme='material')
updated_df = pd.DataFrame(grid_response['data'])     # 사용자가 표에서 고친 값
# → 아래 두 그래프가 updated_df 로 그려진다: 표에서 인구수를 고치면 그래프가 즉시 바뀐다
```
- 이 파일은 `load_data()`에 `@st.cache_data`를 붙였다(04~06은 안 붙임). 표를 편집할 때마다 재실행되기 때문에 캐시가 특히 중요하다.

---

## 8-12. 실습 앱 해부

### A. 행정구역 앱의 4단 진화 — `[스트림릿예제]04 → 05 → 06 → 07`

`[프로젝트]03`의 pandas·seaborn 분석을 웹으로 옮기며 **입력 방식만** 바뀐다. (FRONTEND 정리의 "기능은 같고 화면을 바꾸는 방식만 바뀐다"와 같은 구조)

| 파일 | 시도 선택 | 새로 배우는 것 |
|---|---|---|
| `04streamlit_korea.py` | **없음**(서울 고정) | `st.dataframe` + `st.pyplot` 로 노트북 그래프 옮기기 |
| `05streamlit_korea_combo.py` | **selectbox**(`data['광역시도'].unique()`) | 선택 → 필터 → `if sido_df.empty: st.error` |
| `06streamlit_korea_func.py` | **text_input + button** | `[프로젝트]03`의 `show_pop_area()` 함수를 그대로 가져와 `st.pyplot` 추가, 잘못된 이름 입력 시 `return` |
| `07streamlit_korea_aggrid.py` | selectbox | 캐시 + AgGrid 편집 + `st.columns(2)`에 그래프 두 개 |

- 05·07은 목록에서 고르니 **오타가 날 수 없고**, 06은 직접 입력이라 오타 방어(`empty` 검사)가 필수다. 입력 위젯 선택이 에러 처리량을 결정한다.
- 06에서 `button_clicked`가 True인 동안만 그래프가 보인다 — 다른 곳을 건드리면 사라진다(8-4절 ⚠️).

### B. 네이버 검색 앱 3종 — 공통 뼈대

```
.env 에서 키 로드 ──(없으면)──→ 사이드바 password 입력으로 대체
      ↓
사이드바: 검색어 / 결과 수 / 정렬 / [검색하기] [결과 저장하기] / 필터 입력
      ↓
[검색하기] → search_naver_api() → session_state 에 결과 list + DataFrame 저장
[결과 저장하기] → save_json() → data/{검색어}_xxx.json
      ↓
tab1 전체 결과 │ tab2 필터 ① │ tab3 필터 ②    ← 필터 함수는 빈 DataFrame 이면 빈 DataFrame 반환
```

| | `08streamlit_book_search_nhndev` | `08streamlit_blog_search_nhncloud` | `09streamlit_local_search_nhncloud` |
|---|---|---|---|
| API | 개발자센터 `openapi.naver.com/v1/search/book.json` | API HUB `/search/v1/blog` | API HUB `/search/v1/local` |
| 결과 수 | 10~100 | 10~100 + 정렬(sim/date) | **1~5** + 정렬(random/comment) |
| 필터 ① | 할인가 ≥ 금액 → 정렬 (`pd.to_numeric`) | 작성일 ≥ YYYYMMDD → 최신순 | 카테고리 포함 |
| 필터 ② | 출판사 포함(`columns.drop`) | 블로거명 포함 | 도로명/지번 주소 포함(`mask = road \| addr`) |
| 특이점 | — | 오류 응답 본문까지 표시 | `<b>` 태그 제거, **`st.map`**(mapx/mapy ÷ 1e7 → 경위도) |

**해설**
- 작성일 필터 `result['postdate'] >= start_date` — `postdate`가 `'20260714'`처럼 **자릿수가 고정된 문자열**이라 문자열 비교로도 날짜 순서가 맞다. 형식이 `'2026-7-14'`처럼 들쭉날쭉하면 5-2절처럼 날짜형으로 바꿔야 한다.
- 지역 검색의 `mapx`, `mapy`는 경도·위도에 10⁷을 곱한 정수(코드 주석 기준)라 `/ 1e7`로 되돌려 `lat`, `lon` 열을 만들고 `st.map`에 넘긴다.
- `filter_by_address`의 `road | addr` — 열이 없을 때 `False`를 대신 넣어서 `|` 연산이 깨지지 않게 했다.

⚠️ **`08streamlit_book_search_nhndev.py`의 키 버그** — `headers` 딕셔너리를 **파일 맨 위에서** 환경변수 값으로 만들어 둔다. `.env`가 없어서 사이드바에서 키를 입력해도 `client_id` 변수만 바뀌고 이미 만들어진 `headers`에는 여전히 `None`이 들어 있다 → 401 오류. blog·local 판은 `get_headers()` **함수**가 호출될 때마다 현재 `client_id`로 헤더를 만들어 이 문제가 없다. 같은 수정이 필요하다.

⚠️ `filter_and_sort_books`는 넘겨받은 `df`(= `st.session_state.books_df`)의 `discount` 열을 **그 자리에서** 바꾼다. 결과적으로 문제는 없지만, 함수가 인자를 몰래 바꾸는 것은 1-3절의 참조 공유와 같은 함정이라 `df = df.copy()`로 시작하는 것이 안전하다(blog판 `filter_and_sort_by_date`는 `result = df.copy()`로 시작한다).

### C. 넷플릭스 앱 3종

**① 배우 검색 — `10streamlit_netflix_actor_search.py`**

```python
@st.cache_data
def load_data():
    df = pd.read_csv(BASE_DIR / 'data' / 'netflix_titles.csv')
    df['cast'] = df['cast'].fillna('No Data')
    return df
results = df[df['cast'].str.contains(actor_name, case=False, na=False)]
st.dataframe(results[['title','type','release_year','listed_in']].sort_values(by='release_year', ascending=False).reset_index(drop=True))
```
- `[프로젝트]09`의 `get_actor_titles()`를 입력창 하나로 웹에 올린 것.

**② 출연작 기반 추천 — `10streamlit_netflix_actor_recommender.py`**

```python
df['content'] = df['type'] + " " + df['listed_in'] + " " + df['description']   # 비교할 텍스트 한 덩어리
tfidf = TfidfVectorizer(stop_words='english')
tfidf_matrix = tfidf.fit_transform(df['content'])                   # 작품 × 단어 행렬
target_idx = df[df['title'] == selected_title].index[0]
cosine_sim = cosine_similarity(tfidf_matrix[target_idx], tfidf_matrix).flatten()   # 선택 작품 vs 전체
similar_indices = cosine_sim.argsort()[::-1][1:6]                   # 유사도 내림차순, 0번(자기 자신) 제외 5개
recommendations = df.iloc[similar_indices][['title', 'type', 'release_year', 'listed_in']]
```

**개념 — 콘텐츠 기반 추천**
- 머신러닝 모델은 텍스트를 직접 이해하지 못하므로 **숫자 벡터**로 바꾼다.
- **TF-IDF** — TF(한 문서에 자주 나오는 단어) × IDF(모든 문서에 흔한 단어는 감점). "이 작품을 특징짓는 단어"일수록 큰 값. `stop_words='english'`는 the·a·of 같은 불용어 제거. (scikit-learn, `pip install scikit-learn`)
- **코사인 유사도** — 두 벡터의 **방향이 얼마나 같은가**(0~1). 문서 길이와 무관하게 비슷한 단어 구성을 가진 작품일수록 1에 가깝다.
- `argsort()[::-1]` — 🔗 5-1절 `argmax`처럼 **값이 아니라 위치**를 정렬한다. 역순으로 뒤집어 큰 것부터. `[1:6]`은 1등(자기 자신, 유사도 1.0)을 건너뛴 2~6등.
- `df.iloc[위치]` — `argsort`가 돌려주는 것이 **위치**이므로 `loc`이 아니라 `iloc` 🔗 4-4절. (`read_csv` 직후라 인덱스 = 위치여서 `target_idx`의 `index[0]`과도 일치한다.)
- ⚠️ 매 재실행마다 TF-IDF를 다시 계산한다 🔗 8-6절.

**③ 대시보드 — `11streamlit_netflix_dashboard.py`**

```
데이터 소스: 업로드 CSV > 기본 CSV > 없으면 st.stop()
필터(누적 적용): 유형 multiselect → isin / 추가 연도 slider(range) → between(*year_range) / 국가 text → str.contains
요약: st.columns(3) × st.metric  (전체 / 영화 (df['type']=='Movie').sum() / TV)
탭: 연도별 추이 st.line_chart(groupby.size()) │ 인기 장르 st.bar_chart(split→explode→value_counts) │ 원본 + download_button
```
- `between(*year_range)` — 슬라이더가 돌려준 튜플 `(2015, 2021)`을 **풀어서** 두 인자로 🔗 1-5절 `*`.
- `if "type" in df.columns:` 식의 방어 코드가 곳곳에 있다. **다른 CSV를 업로드해도** 없는 열 때문에 앱이 죽지 않게 하려는 것.
- `[프로젝트]10`의 전처리(`to_datetime(str.strip())`, `fillna("Unknown")`)를 `load_data` 안에 넣어 캐시했다.

### D. 입문 모음 — `01python_basic_streamlit.py`

8-3 ~ 8-8절을 한 파일에 1~6절로 요약한 파일이다: 텍스트 출력 → 입력 위젯 → 버튼 → `dataframe`/`bar_chart` → 사이드바 필터 + `st.table` → `columns`/`tabs` → `cache_data` + `session_state` 카운터. 처음 복습할 때 이 파일 하나를 실행하며 위에서부터 읽으면 된다.

---

## 8-13. PART 8 자가 점검 질문

1. Streamlit의 재실행 모델을 한 문장으로 말하라. 그로 인해 생기는 문제 두 가지와 해결 도구는?
2. `session_state`와 `cache_data`의 차이를 "무엇을 / 누구와 / 왜" 기준으로 비교하라.
3. `st.button` 안에서만 그린 결과가 사라지는 이유는?
4. `st.set_page_config`의 위치 규칙은?
5. `st.dataframe`과 `st.table`의 차이는?
6. `st.slider`에 기본값으로 튜플을 주면 반환값은?
7. `streamlit run`에서 `'data/x.csv'`가 실행 위치에 따라 깨지는 이유와 해결 코드는?
8. 도서 검색 앱에서 사이드바에 키를 넣어도 401이 나는 이유는?
9. 지역 검색 결과를 지도에 띄우기 위해 한 변환은?
10. TF-IDF와 코사인 유사도를 각각 한 문장으로. `argsort()[::-1][1:6]`에서 `[1:6]`의 의미는?
11. 대시보드에서 `st.stop()`은 언제 불리고 왜 필요한가?
12. `download_button`으로 받은 CSV의 한글이 엑셀에서 깨졌다. 원인과 해결은?
