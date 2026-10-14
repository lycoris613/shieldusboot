# PART 5. 분석 ② — numpy · datetime

## 5-1. numpy — pandas 밑에 깔린 계산 엔진

### 개념

| | 파이썬 list | numpy ndarray |
|---|---|---|
| 타입 | 섞어 담기 가능 | **한 타입만** |
| 계산 | 반복문 필요 | **전체를 한 번에**(벡터 연산) |
| 속성 | — | `shape` `dtype` `ndim` |

DataFrame의 각 열은 **ndarray에 인덱스·이름을 붙인 것**이다(`df['a'].to_numpy()` 또는 `.values`로 꺼냄). 그래서 아래 numpy 규칙이 pandas에서 그대로 통한다.

| 개념 | numpy | pandas에서 |
|---|---|---|
| **벡터 연산** | `prices * 1.1`, `prices * qty`(같은 자리끼리) | `df['price'] * 1.1`, `df['a'] / df['b']` |
| **브로드캐스팅** | `(2,3) + 스칼라`, `(2,3) + (3,)` → 모양 자동 맞춤 | 열 + 상수 |
| **불리언 마스크** | `arr[arr > 28000]`, `(A) & (B)` | `df[df['price'] > 28000]` |
| **조건 치환** | `np.where(조건, 참, 거짓)` | `df['grade'] = np.where(df['bmi'] >= 22, '높음', '보통')` |
| 결측 | `np.nan`, `np.isnan()`, `np.nanmean()` | `isna()`, `mean()`(기본이 NaN 무시) |

**axis — "그 축이 사라진다"**

| | 방향 | 결과 | 2차원 성적표 예 |
|---|---|---|---|
| `axis=0` | 세로(행을 따라 내려가며) | **열별** | 과목별 평균 |
| `axis=1` | 가로(열을 따라 옆으로) | **행별** | 학생별 평균 |

- pandas의 `drop('열', axis=1)`, `dropna(axis=0)`, `apply(axis=1)`, `concat(axis=1)`도 같은 규칙.
- `argmax()`는 최대**값**이 아니라 최대값의 **위치**.

⚠️ `np.nan == np.nan`은 **False**. 결측은 `==`로 못 찾는다 → `np.isnan()` / `isna()`.
⚠️ numpy의 `arr.mean()`은 NaN이 하나라도 있으면 **전체가 NaN**. pandas의 `Series.mean()`은 기본적으로 NaN을 **건너뛴다**(`skipna=True`). 같은 이름, 다른 기본값.

**생성 함수**: `np.array(list)`, `np.arange(0, 10, 2)`, `np.linspace(0, 1, 5)`(구간 등분), `np.zeros(3)`, `.reshape((4, 3))`, `np.random.seed(0)`(난수 고정), `np.random.normal(평균, 표준편차, 개수)`, `np.random.randn(n)`(표준정규).

### 코드 위치

```python
# [기초]09 CODE 20 — 인구현황에 벡터 연산 ('전국' 합계 행은 먼저 제외)
pop = pd.read_csv('../data/인구현황.csv')
pop = pop[pop['행정기관'] != '전국']
pop['남자비율'] = (pop['남자 인구수'] / pop['총인구수'] * 100).round(2)   # 반복문 없음

# [프로젝트]04 CODE 25 — 1부터 시작하는 인덱스
table_df.index = np.arange(1, len(table_df) + 1)

# [프로젝트]09 CODE 72 — 이미지를 배열로 (워드클라우드 마스크) 🔗 6-6절
mask = np.array(Image.open('data/netflix_logo.jpg'))       # (높이, 너비, RGB 3) 배열

# [판다스]merge CODE 31·45·51 — reshape, 브로드캐스팅
np.arange(12).reshape((4, 3));  5 + np.arange(4).reshape(2, 2)

# [기초]06 CODE 12 — 가상의 데뷔 나이 500개
np.random.seed(0); debut_ages = np.random.normal(19.6, 2.9, 500)
```

- `'전국'` 합계 행을 빼지 않으면 평균·합계에 **전체가 한 번 더** 더해진다. 행정·통계 데이터에서 매우 흔한 실수. 행정구역 CSV의 `describe()`를 볼 때도 합계 행이 있는지 먼저 확인한다.

---

## 5-2. 날짜와 시간 — datetime · to_datetime · .dt

### 개념

| 타입 | 뜻 |
|---|---|
| `date` | 날짜(연월일) |
| `datetime` | 날짜 + 시각 |
| `timedelta` | **기간** — 날짜끼리 빼면 나오고, 날짜에 더하면 새 날짜 |

```python
gap = date(2020,1,1) - date(2014,8,26);  gap.days        # 1954
date(2014,8,26) + timedelta(days=100);   d.weekday()     # 0=월요일
```

**문자열 ↔ 날짜** — 마지막 글자로 기억

| | 방향 | 예 |
|---|---|---|
| `strptime` (**p**arse) | 문자열 → 날짜 | `datetime.strptime('26/08/2014', '%d/%m/%Y')` |
| `strftime` (**f**ormat) | 날짜 → 문자열 | `d.strftime('%Y년 %m월 %d일')` |

`%Y`(4자리 연) `%y`(2자리) `%m` `%d` `%H:%M:%S`. **형식과 문자열 모양이 다르면 ValueError** — 스크래핑 데이터에서 매우 흔하다.

**pandas: 열 전체를 한 번에**

| 옵션 | 역할 |
|---|---|
| `format='%d/%m/%Y'` | 형식 명시(가장 빠르고 안전) |
| `dayfirst=True` | `dd/mm/yyyy`임을 알림 — **안 쓰면 월·일이 뒤바뀔 수 있다** |
| `errors='coerce'` | 변환 실패 값을 **NaT**로 → 이후 `isna().sum()`으로 실패 건수 확인 필수 |

- 날짜형(`datetime64`)이 되어야 **비교·정렬·뺄셈**이 제대로 된다. 문자열이면 `'31/10/2015' < '26/08/2014'`처럼 글자 순서로 비교된다.
- **`.dt` 접근자**: `.dt.year` `.month` `.day` `.hour` `.dayofweek` `.day_name()` `.quarter` `.strftime('%Y-%m')` `.days`(timedelta 열).
- **기간 집계**: `groupby(df['date'].dt.year)` — 데이터가 있는 기간만 / `set_index('date').resample('ME').sum()` — **빈 달도 0으로** 채움. `'ME'`=Month End(pandas 2.2+, 예전 `'M'`은 최신판에서 에러).

**나이 계산** — 연도만 빼면 틀린다(생일 전이면 1살 적음). 튜플 비교로 "생일이 지났나"를 True/False(1/0)로 만들어 뺀다.

```python
def calc_age(birth, today=None):
    today = today or date.today()
    not_yet = (today.month, today.day) < (birth.month, birth.day)   # 튜플은 앞 요소부터 비교
    return today.year - birth.year - not_yet                         # True=1, False=0
```

### 코드 위치

**① 넷플릭스 추가일 — `[프로젝트]10` CODE 3, `[스트림릿예제]11`**

```python
df["date_added"] = pd.to_datetime(df["date_added"].str.strip(), errors="coerce")   # 'September 25, 2021'
df["year_added"] = df["date_added"].dt.year           # 결측 때문에 2021.0 처럼 float
yearly = df.dropna(subset=["year_added"]).groupby("year_added").size()
```
- `date_added` 값 중 **앞에 공백이 붙은 것**이 있어 `strip()` 없이 변환하면 실패(NaT)가 생긴다. 스트림릿판은 `astype(str).str.strip()`으로 NaN까지 문자열로 만든 뒤 처리.
- `year_added`가 `2021.0`인 이유: NaN이 섞인 정수 열은 float가 된다(4-13절과 같은 이유).

**② K-Pop 데뷔 나이 — `[프로젝트]11` CODE 5 · 7, `[기초]10` CODE 24~27**

```python
df["date_of_birth"] = pd.to_datetime(df["date_of_birth"], dayfirst=True, errors="coerce")   # '10/09/1997'
df["debut"]         = pd.to_datetime(df["debut"], dayfirst=True, errors="coerce")
df["debut_age"] = (df["debut"] - df["date_of_birth"]).dt.days / 365.25     # timedelta → 일수 → 연
valid_age = df["debut_age"].between(10, 40)                                # 비정상 값 제거
df.loc[valid_age, "debut_age"].mean()                                      # 19.6세
df["age"] = (pd.Timestamp.today().normalize() - df["date_of_birth"]).dt.days / 365.25   # 현재 나이
```

**해설**
- `'10/09/1997'`은 1997년 9월 10일이다. `dayfirst=True`가 없으면 10월 9일로 읽힐 수 있다.
- `365.25`는 윤년을 평균 낸 1년 일수.
- `[기초]10`판은 범위 필터 없이 `describe()`를 찍어서 **min이 -2.8세**(데뷔일이 생일보다 빠름 — 입력 오류)가 나왔다. `[프로젝트]11`판이 `between(10, 40)`을 넣은 이유가 이것이다. 계산한 파생 변수는 **최소·최대를 먼저 보고 말이 되는지 검사**한다.
- `pd.Timestamp.today().normalize()` — 오늘 날짜의 **자정**(시각을 0으로)으로 맞춰 날짜만 비교.

**③ 발매일 문자열 — `[프로젝트]04`**
Melon의 발매일은 `'2024.11.22'` 문자열로 수집됐고, DB 저장 시 `dtype={'release_date': sqlalchemy.types.DATE}`로 넘겨 **MySQL이 날짜로 해석**했다(조회 결과 `2024-04-08`). pandas 단계에서 미리 `pd.to_datetime(table_df['release_date'], format='%Y.%m.%d')`로 바꿔 두면 DB 종류와 무관하게 안전하다.

---

## 5-3. PART 5 자가 점검 질문

1. `arr.mean(axis=0)`은 2차원 배열에서 무엇을 계산하나?
2. `np.nan == np.nan`의 결과와, 결측을 찾는 올바른 함수는?
3. numpy `mean()`과 pandas `mean()`의 NaN 처리 기본값 차이는?
4. 인구현황 분석 전 `'전국'` 행을 빼야 하는 이유는?
5. `strptime`과 `strftime`의 방향은?
6. `pd.to_datetime(..., errors='coerce')` 후 반드시 해야 할 확인은?
7. `dayfirst=True`를 빼면 `'10/09/1997'`은 어떻게 될 수 있나?
8. 데뷔 나이 최소값이 -2.8세로 나왔다. 무엇을 의미하고 어떻게 처리했나?
9. `groupby(dt.year)`와 `resample('ME')`의 차이는?
10. 튜플 비교로 만 나이를 계산하는 원리를 설명하라.
