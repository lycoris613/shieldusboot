# PART 4. 분석 ① — pandas

## 4-1. Series와 DataFrame

### 개념

- **pandas**: 데이터 처리·분석 라이브러리. R의 `data.frame`을 본떠 만든 **DataFrame**이 중심. 엑셀 표처럼 생겼고 SQL처럼 쿼리·조인을 할 수 있다. CSV·엑셀·DB 등 다양한 소스를 읽는다.
- NumPy 배열은 **전체가 한 타입**이어야 하지만, DataFrame은 **열마다 타입이 달라도** 된다(정수 열·문자열 열·날짜 열 공존) 🔗 5-1절.

| | Series | DataFrame |
|---|---|---|
| 차원 | 1차원 (열 하나) | 2차원 (표) |
| 만들기 | `pd.Series([4,7,-5,3], index=['d','b','a','c'])` | `pd.DataFrame(dict)` / `pd.DataFrame(딕셔너리들의 리스트)` / 2차원 배열 |
| 속성 | `.index` `.values` `.dtype` `.name` `.index.name` | `.index`(행 이름) `.columns`(열 이름) `.values` `.shape` `.dtypes` |

- 행 방향 이름표 = **인덱스**, 열 방향 이름표 = **컬럼**. 교차점에 값.
- `df['열']` → Series, `df[['열1','열2']]` → DataFrame(대괄호 두 겹).
- 딕셔너리로 만들 때 `columns=[...]`에 없는 이름을 넣으면 그 열은 **NaN**으로 생긴다(PDF 8.4의 `penalty` 열).

### 코드 위치

```python
# [프로젝트]09 CODE 44 — dict 로 생성 (키 = 열 이름, 값 = 열 데이터)
person = pd.DataFrame({"name": ["Selena", "Seungwoo", "Louis"],
                       "job":  ["Data Scientist", "Chef", "Home Protector"],
                       "grade": ["A", "B", "C"]})

# [기초]04 CODE 5 — 딕셔너리들의 리스트로 생성 (한 dict = 한 행)
df = pd.DataFrame([{'title': '파이썬 입문', 'price': 25000}, {...}])

# [프로젝트]03 CODE 8 — .values 는 numpy 배열
print(type(data.values))          # <class 'numpy.ndarray'>
```

---

## 4-2. 파일 읽고 쓰기

### 개념

| 읽기 | 쓰기 | 주요 옵션 |
|---|---|---|
| `pd.read_csv(path)` | `df.to_csv(path, index=False)` | `sep=','`, `index_col='열'`, `encoding='cp949'/'utf-8-sig'` |
| `pd.read_json(path)` | `df.to_json(path)` | |
| `pd.read_excel(path, sheet_name=, index_col=)` | `df.to_excel(path, sheet_name=)` | 엑셀 쓰기엔 `openpyxl` 또는 `xlsxwriter` |
| `pd.read_sql(sql, con)` / `read_sql_query(sql, con, params=)` | `df.to_sql(name, con, if_exists=)` | 🔗 PART 7 |

- `to_csv(..., encoding='utf-8-sig')` — **엑셀에서 열면 한글이 깨지지 않게** BOM을 붙인다 🔗 9-4절.
- `index=False` — 0,1,2… 인덱스를 파일에 쓰지 않는다. 안 쓰면 다시 읽을 때 `Unnamed: 0` 열이 생긴다(바로 아래 행정구역 CSV가 그 흔적).

### 코드 위치

**① 기본 읽기와 `Unnamed: 0` — `[프로젝트]03` CODE 3~7**

```python
data = pd.read_csv('data/data_draw_korea.csv')
print(data.shape)           # (229, 8)
data.columns                # ['Unnamed: 0', '인구수', 'shortName', 'x', 'y', '면적', '광역시도', '행정구역']
```
- `Unnamed: 0`은 누군가 `to_csv`를 `index=False` 없이 저장한 흔적이다. 3가지 처리법이 실습에 다 나온다: `rename(columns={'Unnamed: 0':'seq'})`(03 CODE 58) / `drop('Unnamed: 0', axis=1)`(`[스트림릿예제]05·06·07`) / 처음부터 `read_csv(..., index_col=0)`.

**② 인코딩·구분자 — `[프로젝트]11` CODE 2, `[판다스]groupby` CODE 1**

```python
df = pd.read_csv("data/kpopidolsv3.csv", encoding="utf-8-sig")        # 파일 앞에 BOM
primary = pd.read_csv("data/2016_presidential_election/primary_results.csv", sep=",")
```

**③ 엑셀 저장 + 천 단위 서식 — `[프로젝트]03` CODE 63 · 69 · 72**

```python
pop_mean_lt_df.to_excel('data/평균인구수미만행정구역.xlsx')
group_df.to_excel('data/광역시도별행정구역별인구수합계.xlsx', sheet_name='인구수합계')

# 숫자에 콤마 서식을 넣으려면 xlsxwriter 엔진 (%pip install xlsxwriter)
writer = pd.ExcelWriter('data/광역시도별인구수의합계1.xlsx', engine='xlsxwriter')
group_df.to_excel(writer, sheet_name='인구수합계')
workbook  = writer.book
worksheet = writer.sheets['인구수합계']
num_format = workbook.add_format({'num_format': '#,###'})
worksheet.set_column('C:C', None, num_format)     # C열 전체에 서식 (A·B 열은 MultiIndex 두 단)
writer.close()
```
- `to_excel`은 **값**만 저장한다. 콤마 같은 **셀 서식**은 엑셀 라이브러리(xlsxwriter)로 따로 입힌다. 값 자체를 문자열로 바꾸면(4-10절 `map('{:,}'.format)`) 엑셀에서 숫자 계산이 안 된다.

---

## 4-3. 데이터 훑어보기

### 개념 — 분석을 시작할 때 항상 확인하는 것들

| 코드 | 알려 주는 것 |
|---|---|
| `shape` · `columns` · `index` | 크기 · 열 이름 · 인덱스(`RangeIndex(start=0, stop=229)`) |
| `head(n)` / `tail(n)` / `sample(n)` | 앞 / 뒤 / **무작위** n행 |
| `info()` | 열별 **Non-Null 개수 + dtype** + 메모리 — 결측치를 한눈에 |
| `dtypes` | 열별 타입(Series) · `int64` `float64` `object`(문자열) `datetime64` `category` |
| `describe()` | 숫자 열 요약(count·mean·std·min·25%·50%·75%·max) |
| `describe(include=[object])` | **문자열 열** 요약(count·unique·top·freq) |
| `describe(include='all')` | 모든 열 (해당 없는 칸은 NaN) |
| `unique()` / `nunique()` | 고유값 배열 / 고유값 개수 |
| `value_counts()` | 값별 개수(내림차순). `normalize=True`면 **비율**(0~1) |

### 코드 위치 — `[프로젝트]03` CODE 4~19, `09` CODE 5~10

```python
data.info()                          # 229 non-null — 행정구역 데이터는 결측치 없음
netflix.info()                       # director 6173 non-null / 8807 → 결측이 많다는 신호
data.describe(include=np.number)     # = describe() 기본
data.describe(include=[object])      # 광역시도: unique 17, top 경기도, freq 31
data['광역시도'].value_counts()       # 경기도 31, 서울특별시 25, ...
member_df['성별'].value_counts(normalize=True)   # 남 0.787, 여 0.213   ([프로젝트]05_02 CODE 14)
```

- `value_counts()` 결과는 **Series**다. 인덱스가 값, 값이 개수. `.index`로 "빈도순 정렬된 값 목록"을 얻어 그래프 순서에 쓴다(6-5절 `order=`).
- pandas 2.x에서 `value_counts()` 결과의 이름은 `count`(normalize면 `proportion`)다. 그래서 `reset_index()`하면 열이 `[원래이름, 'count']`가 된다(4-5절).

---

## 4-4. 선택 — `[]` · `loc` · `iloc` · 불리언 마스크

### 개념

| 문법 | 기준 | 슬라이스 끝 | 예 |
|---|---|---|---|
| `df['열']` / `df[['열1','열2']]` | 열 이름 | — | |
| `df[조건]` | True인 **행** | — | `df[df['price'] > 28000]` |
| **`df.loc[행, 열]`** | **라벨(이름)** | **포함** | `data.loc[20:25]` → 20~**25** 6행 |
| **`df.iloc[행, 열]`** | **정수 위치** | **미포함** | `data.iloc[20:25]` → 20~**24** 5행 |

- `loc[행선택]` — 모든 열 / `loc[:, 열선택]` — 모든 행 / `loc[조건, 열목록]` — 필터 + 열 선택 한 번에.
- `loc`는 **열 이름 범위**도 된다: `loc[:, '인구수':'행정구역']`(끝 포함). `iloc`는 정수만.
- 한 행만 고르면 `data.loc[0]` → **Series**(열 이름이 인덱스가 됨).

**불리언 마스크 결합** (🔗 원리는 5-1절 numpy)

| 연산 | 코드 |
|---|---|
| 그리고 / 또는 / 부정 | `(A) & (B)` / `(A) \| (B)` / `~A` — **각 조건 괄호 필수**, `and/or/not` 불가 |
| 여러 값 중 하나 | `df['열'].isin(['5선','6선'])` |
| 결측 여부 | `isnull()` = `isna()` / `notnull()` = `notna()` |
| 범위 | `df['release_year'].between(2015, 2021)` (양 끝 포함) |
| 문자열 포함 | `df['열'].str.contains('OST', na=False, case=False)` 🔗 4-9절 |

### 코드 위치

```python
# [프로젝트]03 CODE 17·18 — 조건 + 열 목록
data.loc[data['행정구역'] == '동구']                                    # 광주·대구·대전·부산·울산·인천 동구 6개
data.loc[data['광역시도'] == '경기도', ['행정구역','인구수','면적']]

# CODE 26~28 — 최대/최소인 행 찾기
data.loc[data['인구수'] == data['인구수'].max()]                         # 수원시
data.loc[data['면적'] == data['면적'].max()]                             # 홍천군

# CODE 48·49·51·52 — loc vs iloc
data.loc[20:25]                  # 6행 (라벨 20~25)
data.iloc[20:25]                 # 5행 (위치 20~24)
data.iloc[20:25, 1:8]            # 행 위치 20~24, 열 위치 1~7
data.loc[[40, 55, 60], ['인구수','광역시도','행정구역']]                 # 라벨 리스트
sido_df = df.loc[df['광역시도'] == sido_name, '인구수':'행정구역']       # CODE 45 — 열 이름 범위

# [프로젝트]05_02 CODE 12·13 — isin
member_df.loc[member_df['당선횟수'].isin(['5선', '6선']), ['이름', '정당']]

# [프로젝트]04 CODE 16, 09 CODE 60 — "이 열들만 빼고" 전부
song_df.loc[song_df['앨범'].str.contains('OST'), song_df.columns.drop(['detail_url', '가사'])]
```

**해설**
- `columns.drop([...])`은 **열 이름 목록(Index)에서** 몇 개를 뺀 새 목록을 만든다. 표에서 열을 지우는 `df.drop()`과 다르다. `[스트림릿예제]08 book`도 `df.columns.drop(['image','description','link'])`를 쓴다.
- ⚠️ `loc` 슬라이스는 **끝 포함**이라 `data.loc[0:10]`은 11행이다(03 CODE 24 출력).
- ⚠️ 인덱스가 0부터 연속된 정수일 때만 `loc[20:25]`와 `iloc[20:26]`이 같다. `reset_index` 전의 필터 결과처럼 인덱스가 듬성듬성하면 두 결과가 완전히 다르다.

---

## 4-5. 열 추가·수정·삭제 · 인덱스 · inplace

### 개념

| 작업 | 코드 |
|---|---|
| 새 열(단일값·리스트·연산) | `df['DataCategory'] = "Titanic"` / `df['Id'] = range(0, 891)` / `df['FamilySize'] = df['SibSp'] + df['Parch'] + 1` |
| 조건 결과를 열로 | `df['Fare_Cheap'] = df['Fare'] < 30` (True/False 열) |
| **조건부 대입** | `df.loc[조건, '열'] = 값` — 조건 밖의 행은 기존값 유지(새 열이면 **NaN**) |
| 열 이름 변경 | `df.rename(columns={'old':'new'})` / `df.columns = [새 이름 리스트]` |
| 열/행 삭제 | `df.drop('열', axis=1)` / `df.drop(인덱스, axis=0)` |
| 인덱스 ↔ 열 | `set_index('열')` / `reset_index()` |
| 인덱스 다시 매기기 | `reset_index(drop=True)` — 기존 인덱스를 **버리고** 0..n-1 |
| 인덱스 직접 지정 | `df.index = np.arange(1, len(df)+1)` / `df.index = df.index + 1` |
| 타입 변환 | `astype(int)` / `pd.to_numeric(s, errors='coerce')` / `astype('category')` |

**`inplace` — pandas 전반에 반복되는 개념**

| | `inplace=False` (기본) | `inplace=True` |
|---|---|---|
| 원본 | 그대로 | **바뀜** |
| 반환값 | 바뀐 **새 객체** | **None** |
| 쓰는 법 | `df = df.rename(...)` 처럼 다시 대입 | `df.rename(..., inplace=True)` 단독 |

⚠️ `df = df.dropna(inplace=True)` 처럼 **둘을 섞으면 df가 None**이 된다.
⚠️ `df['열'].fillna(값, inplace=True)` 같은 **열 선택 후 inplace**는 pandas 2에서 경고가 나고 원본이 안 바뀔 수도 있다. `df['열'] = df['열'].fillna(값)`으로 쓴다(`[프로젝트]09` CODE 27 주석 "pandas 2 버전에서는 warning").

**`reset_index()`의 두 번째 얼굴 — Series → DataFrame**
Series에 `reset_index()`(drop 없이)를 하면 인덱스가 **열**로 내려오면서 DataFrame이 된다. `value_counts()` 결과를 표로 만들 때 쓰는 핵심 패턴이다.

### 코드 위치

```python
# [프로젝트]03 CODE 57·58 — inplace 비교
data.rename(columns={'Unnamed: 0': 'seq'}, inplace=False)   # 바뀐 결과만 '출력', data 는 그대로
data.rename(columns={'Unnamed: 0': 'seq'}, inplace=True)    # data 자체가 바뀜, 출력 없음

# CODE 42·43 — 조건부 대입: 1500 미만 행은 NaN
area_1000.loc[area_1000['면적'] >= 1500, '면적3'] = area_1000['면적'].map('{:,}'.format)

# [프로젝트]04 CODE 24~27 — DB 에 넣기 전 정리
table_df = song_df.copy()
table_df.columns = ['title','singer','album','release_date','genre','url','likes','lyric']   # 영문 컬럼명
table_df.index = np.arange(1, len(table_df) + 1)       # 1부터 → 테이블 PK 로 쓸 예정
table_df.drop('url', axis=1, inplace=True)             # axis=1 은 열, axis=0 은 행

# [프로젝트]09 CODE 82·83 — Series → DataFrame
genre_counts = genre_series.value_counts().reset_index()   # 열: ['listed_in', 'count']
genre_counts.columns = ['genre', 'count']

# [프로젝트]11 CODE 3 — 컬럼명 일괄 정리 (Index 에도 .str 이 된다)
df.columns = df.columns.str.strip().str.lower().str.replace(" ", "_")
# 'Stage Name' → 'stage_name', 'Date of Birth' → 'date_of_birth'

# [프로젝트]11 CODE 5 — 숫자로 강제 변환 (변환 불가 → NaN)
df["height"] = pd.to_numeric(df["height"], errors="coerce")

# [스트림릿예제]08 book — API 응답의 discount 가 문자열이라 숫자로
if df['discount'].dtype == 'object':
    df['discount'] = pd.to_numeric(df['discount'], errors='coerce')
```

**PDF 8.9의 조건부 컬럼 예(타이타닉)** 는 위와 같은 문법의 연습판이다.

```python
train.loc[train["Embarked"] == "C", "Nationality"] = "France"
train.loc[train["Embarked"].isin(["S", "Q"]), "Nationality"] = "England"
train.loc[train["Age"].isnull(), "Age"] = train["Age"].mean()      # 결측을 평균으로
```

---

## 4-6. 통계 함수 · 상관계수 · 상위 N개

### 개념

| 함수 | 뜻 |
|---|---|
| `max` `min` `mean` `median` `std` `sum` `count` | 최대·최소·평균·중앙값·표준편차·합·개수(NaN 제외) |
| `quantile([0.25, 0.75])` | 사분위수 |
| `s1.corr(s2)` / `df.corr()` | 상관계수 / 상관계수 **행렬** 🔗 6-5절 heatmap |
| `nlargest(n, '열')` / `nsmallest(n, '열')` | 그 열 기준 상위/하위 n행 |
| `idxmax()` / `idxmin()` | 최대/최소값의 **인덱스(라벨)** — `loc`에 넣어 그 행을 얻는다 |

**상관계수(Correlation Coefficient)**
- **-1 ~ 1**. 0에 가까울수록 관련 약함, **양수**는 비례(한쪽이 크면 다른 쪽도 큼), **음수**는 반비례.
- 상관은 **인과가 아니다**.

### 코드 위치

**① 행정구역 인구수와 면적 — `[프로젝트]03` CODE 74~77**

```python
data['면적'].corr(data['인구수'])            # 전국   -0.346  넓은 군 지역은 인구가 적다
seoul_df['면적'].corr(seoul_df['인구수'])    # 서울    0.664  서울 안에서는 큰 구가 인구도 많다
gy_df['면적'].corr(gy_df['인구수'])          # 경기도 -0.216
# 세종특별자치시 → nan
```
- **같은 두 변수라도 어떤 집단에서 보느냐에 따라 부호까지 바뀐다.** 전국은 도시/농촌 차이가 지배하고, 서울 안에서는 구의 크기가 지배한다.
- 세종이 `nan`인 이유: 행정구역이 **1개**라 분산이 0 → 상관계수를 계산할 수 없다.

**② 누가 가장 ~한가 — `[프로젝트]11` CODE 21~23**

```python
oldest = df.loc[df["date_of_birth"].idxmin()]            # 생일이 가장 이른 = 가장 나이 많은 사람의 행
df.nlargest(5, "height")[["stage_name", "group", "height", "gender"]]
df.loc[valid].nsmallest(5, "debut_age")                  # 유효 범위 안에서 최연소 데뷔
```
- `sort_values(...).head(n)`과 같은 결과지만 `nlargest`가 의도가 분명하고 빠르다.

---

## 4-7. 정렬

```python
df.sort_values('price', ascending=False)                                      # 한 열 내림차순
pop_mean_lt_df.sort_values(by=['광역시도','인구수'], ascending=[True, False])   # [프로젝트]03 CODE 62 — 열마다 방향 지정
df.sort_index(axis=0, level=1)                                                # MultiIndex 의 두 번째 단계로 정렬 🔗 4-14
df['release_year'].value_counts().sort_index()                                # 개수를 '연도순'으로 → 선그래프용
```

- 필터 → 정렬 → 인덱스 재설정을 **메서드 체이닝**으로 잇고, 줄이 길면 `\`로 끊거나 전체를 `( )`로 감싼다.

```python
# [프로젝트]03 CODE 30
data.loc[data['인구수'] >= data['인구수'].mean(), ['광역시도','행정구역','인구수']]\
    .sort_values('인구수', ascending=False).reset_index(drop=True)
```

---

## 4-8. 결측치 처리

### 개념

| 표기 | 뜻 |
|---|---|
| NA | Not Available — 유효하지 않은 값 |
| **NaN** | Not a Number — pandas/numpy의 빈 값 (`np.nan`) |
| NaT | Not a Time — 날짜형의 빈 값 |
| Null | 아무것도 없음 (`isnull()` = `isna()`, 완전히 같은 함수) |

**처리 방법 3가지**
1. **제거** — 결측 있는 행/열 삭제 (`dropna`). 표본이 줄어든다.
2. **대체** — 평균·중앙값·최빈값·"No Data" 등으로 치환 (`fillna`, `replace(np.nan, 값)`, `interpolate`). 도메인 지식이 있으면 효율적.
3. **모델 기반** — 예측 모델로 결측값을 추정.

**결측 비율에 따른 판단 기준** (`[프로젝트]09` MD 14, 이론적 가이드라인일 뿐 절대 규칙 아님)

| 비율 | 권장 |
|---|---|
| **5% 미만** | 행 **삭제** — 손실이 적어 신뢰성에 영향 작음 |
| **5~20%** | **대체** — 평균·중앙값·최빈값 또는 모델 |
| **20% 이상** | 열 제거를 고려하되, 변수가 중요하면 대체·모델 보완. 분석 목적·데이터량을 종합 판단 |

| 함수 | 동작 |
|---|---|
| `isna().sum()` | 열별 결측 **개수** |
| `isna().mean() * 100` | 열별 결측 **비율(%)** — True=1이라 평균이 곧 비율 |
| `dropna(axis=0)` / `dropna(axis=1)` | 결측 있는 **행** / **열** 삭제 |
| `dropna(subset=['a','b'])` | 지정 열에 결측 있는 행만 삭제 |
| `fillna(값)` | 대체 |
| `replace(np.nan, 값)` | 대체도 **가능**(본래 용도는 일반 값 치환) |

`〔정정〕` 2단계 정리의 "`replace()`는 결측치 처리 용도가 아님"은 정확히는 "본래 목적이 일반 값 치환이라 결측 **전용**인 `fillna`가 더 표준적"이라는 뜻이다. `replace(np.nan, 'No Data')`도 실제로 동작한다(`[프로젝트]09` CODE 30). 반대로 `-999` 같은 **가짜 결측값을 진짜 NaN으로** 바꿀 때는 `replace(-999, np.nan)`을 쓴다(`[판다스]merge` CODE 71).

### 코드 위치 — 넷플릭스 결측 처리 전 과정 (`[프로젝트]09` CODE 20~39)

```python
netflix.isna().sum()          # director 2634, cast 825, country 831, date_added 10, rating 4, duration 3

for i in netflix.columns:                                   # 결측 비율
    missingValueRate = netflix[i].isna().sum() / len(netflix) * 100
    if missingValueRate > 0:
        print("{} null rate: {}%".format(i, round(missingValueRate, 2)))
# 한 줄 버전 ([프로젝트]10·11 CODE 2·4)
missing = df.isna().mean().mul(100).round(1).sort_values(ascending=False)
missing[missing > 0]           # director 29.9 / cast 9.4 / country 9.4 / date_added 0.1

# 판단: director(29.9%)·country·cast(9.4%) → 대체 / date_added·rating·duration(<1%) → 행 삭제
netflix['country']  = netflix['country'].fillna('No Data')
netflix['director'] = netflix['director'].replace(np.nan, 'No Data')
netflix['cast']     = netflix['cast'].replace(np.nan, 'No Data')
netflix.dropna(axis=0, inplace=True)          # (8807, 12) → (8790, 12) — 17행 삭제
netflix.isna().sum()                          # 전부 0 확인
```

**해설**
- `director`는 30% 가까이 비었지만 "감독 정보 없음" 자체가 의미 있는 범주이고 다른 열(제목·장르)은 멀쩡하므로, 열을 버리지 않고 `'No Data'`로 대체했다.
- 대체한 뒤에는 `value_counts()`에서 `'No Data'`가 1등으로 나온다(2634). 배우 순위를 낼 때는 **이 값을 빼고** 세야 한다(`cast_series[cast_series != 'No Data']`, 09 CODE 90).
- `dropna` 후 인덱스는 0~8806 중 17개가 빠진 채로 남는다(`Index: 8790 entries, 0 to 8806`). 위치 기반 작업을 하려면 `reset_index(drop=True)`.

**그 밖의 코드 위치**
- 부분 결측 행 제거: `df.dropna(subset=["year_added"])`(`[프로젝트]10` CODE 5), `hw = df.dropna(subset=["height", "weight"]).copy()`(`11` CODE 11)
- 집계 결과의 NaN → 0: `groupby(...).agg([...]).fillna(0)`(`03` CODE 79 — 표본 1개인 세종의 std가 NaN)
- 평균으로 채우기: `df2.fillna(df2['height'].mean())`(`[기초]04`)
- 부분 결측 확인: `(cast_series == 'No Data').sum()` — True를 세면 개수

---

## 4-9. 문자열 열 가공 — `.str` 접근자 · `explode`

### 개념

문자열 열에 `.str`을 붙이면 파이썬 문자열 메서드가 **모든 행에 한 번에** 적용된다. `type(df['앨범'].str)` → `pandas.core.strings.accessor.StringMethods`.

| 메서드 | 예 | 결과 |
|---|---|---|
| `.str.strip()` | `'  아이유  '` | `'아이유'` |
| `.str.replace(a, b, regex=False)` | `'25,000원'` → `.replace(',','').replace('원','')` → `.astype(int)` | `25000` |
| `.str.contains(s, case=False, na=False)` | 포함 여부 | True/False Series |
| `.str.split(', ')` | `'Drama, Comedy'` | `['Drama','Comedy']` (리스트가 든 Series) |
| `.str.split(', ', expand=True)` | 〃 | **여러 열**로 펼친 DataFrame |
| `.str.split(', ').str[0]` | 〃 | 첫 요소만 |
| `.str[:2]` | `'경기 안양시만안구'` | `'경기'` (슬라이싱) |
| `.str.lower()` | | |

- `contains`의 `case=False`는 대소문자 무시, **`na=False`는 결측을 False로** — 안 쓰면 NaN이 섞인 불리언이 되어 필터에 쓸 때 에러.
- `.str.replace`에 `<b>`처럼 정규식 특수문자가 든 값을 넣을 때는 `regex=False`로 **글자 그대로** 치환한다.

**`explode()` — 한 칸에 여러 값**

```
listed_in (원본)           split(', ')                    explode()
0  "Comedy, Drama"    →    0 ["Comedy","Drama"]      →    0 Comedy
1  "Horror, Thriller" →    1 ["Horror","Thriller"]   →    0 Drama
                                                         1 Horror
                                                         1 Thriller
```
- 리스트의 각 요소를 **별도 행**으로 펼친다. **원래 인덱스가 복제**된다(0,0,1,1) — 어느 행에서 왔는지 표시. 새로 매기려면 `reset_index(drop=True)`.
- 펼친 뒤에야 `value_counts()`로 **장르별** 개수를 셀 수 있다. 펼치기 전 `value_counts()`는 "Dramas, International Movies" 같은 **조합**을 센다(09 CODE 78).

### 코드 위치

```python
# [프로젝트]09 CODE 80·81 — 장르 집계
genre_series = netflix['listed_in'].str.split(', ').explode()    # 8790행 → 19294행
genre_series.value_counts()        # International Movies 2752, Dramas 2426, Comedies 1674 ...

# [프로젝트]10 CODE 6 — 국가도 다중값, 펼친 뒤 공백 정리 + 'Unknown' 제외
country_series = df["country"].str.split(", ").explode().str.strip()
top_countries = country_series[country_series != "Unknown"].value_counts().head(10)

# [프로젝트]09 CODE 88~95 — 배우 TOP 10
cast_series = netflix['cast'].str.split(', ').explode()
cast_series_data = cast_series[cast_series != 'No Data']
top_actors = cast_series_data.value_counts().head(10).reset_index()
top_actors.columns = ['actor', 'count']       # Anupam Kher 43, Shah Rukh Khan 35 ...

# [프로젝트]09 CODE 98 — 특정 배우 출연작 (함수)
def get_actor_titles(actor_name):
    return netflix[netflix['cast'].str.contains(actor_name, na=False)][['title','type','release_year','listed_in']]

# [프로젝트]05_02 CODE 16 — 앞 두 글자 = 시도
member_df['선거구2'] = member_df['선거구'].str[:2]     # '대구 동구군위군을' → '대구', '비례대표' → '비례'

# [프로젝트]09 CODE 58·59 — 대소문자 무시 검색
netflix[netflix['title'].str.contains('Squid Game', na=False, case=False)]

# [스트림릿예제]09 local — <b> 태그 제거
df['title'] = df['title'].str.replace('<b>', '', regex=False).str.replace('</b>', '', regex=False)
```

⚠️ `[프로젝트]09` CODE 56 `print(type(netflix['title']).str)` — 괄호 위치가 틀렸다. `type(...)`을 먼저 해서 **Series 클래스**의 `.str`을 출력한 것이다. 의도는 `type(netflix['title'].str)`. 출력이 우연히 같게 나와 눈치채기 어렵다.

⚠️ `str.contains('Lee Jung-jae')`는 부분 일치라 이름이 다른 이름에 포함되면 같이 걸린다(예: `'Kim'`으로 검색). 정확히 한 사람만 찾으려면 explode 후 `==` 비교가 안전하다.

---

## 4-10. `map` · `apply` · 피처 엔지니어링

### 개념

| 코드 | 대상 | 넘기는 것 |
|---|---|---|
| `Series.map(dict)` | 각 **값** | 값 → 값 **대응표**. 표에 없는 값은 **NaN** |
| `Series.map(함수)` / `Series.map('{:,}'.format)` | 각 **값** | 함수 |
| `Series.apply(함수)` | 각 **값** | 함수 (map과 거의 같음) |
| `DataFrame.apply(함수, axis=1)` | 각 **행**(Series) | 여러 열을 함께 봐야 할 때 `row['a'] / row['b']` |
| `np.where(조건, 참값, 거짓값)` | 전체(벡터) | 두 갈래 분기는 이게 가장 빠르다 🔗 5-1절 |

⚠️ 내장 함수 `map(함수, iterable)`(1-6절)과 pandas의 `Series.map()`은 **이름만 같은 다른 것**이다.

**피처 엔지니어링(Feature Engineering)** — 기존 변수로 **새 변수(열)를 만드는** 과정. 코드 → 이해하기 쉬운 범주, 날짜 → 나이, 텍스트 → 숫자 등.

**`map` 포맷 vs `style.format`**

| | `df['a'].map('{:.2f}'.format)` | `df.style.format('{0:.2f}')` |
|---|---|---|
| 값 | **문자열로 바뀜** — 계산·정렬 불가 | 그대로(숫자) — **보이는 모양만** |
| 반환 | Series | `Styler` 객체(주피터에서 표로 렌더링) |

### 코드 위치

**① 딕셔너리 매핑 — 넷플릭스 시청 등급 → 연령대 (`[프로젝트]09` CODE 42~52)**

```python
grade_dic = {"A": 100, "B": 90, "C": 80}
person['grade'] = person['grade'].map(grade_dic)       # 연습: 등급 → 점수

age_group_dic = {'G': 'All', 'TV-G': 'All', 'TV-Y': 'All',
                 'PG': 'Older Kids', 'TV-Y7': 'Older Kids', 'TV-Y7-FV': 'Older Kids', 'TV-PG': 'Older Kids',
                 'PG-13': 'Teens', 'TV-14': 'Young Adults',
                 'NC-17': 'Adults', 'NR': 'Adults', 'UR': 'Adults', 'R': 'Adults', 'TV-MA': 'Adults'}
netflix['age_group'] = netflix['rating'].map(age_group_dic)
netflix['age_group'].value_counts()     # Adults 4089, Young Adults 2157, Older Kids 1487, All 567, Teens 490
```
- 14종 코드가 5개 범주로 줄어 해석·시각화가 쉬워진다. 사전에 빠진 코드가 있으면 NaN이 생기므로 매핑 후 `isna().sum()` 확인.

**② 함수/포맷 매핑 — `[프로젝트]03` CODE 35 · 37 · 39**

```python
gy_df['인구수2'] = gy_df['인구수'].map(lambda val: f"{val:,}")     # 1125461 → '1,125,461'
gy_df['면적2']  = gy_df['면적'].map(lambda val: f'{val:.1f}')
area_1000.loc[:, '면적2'] = area_1000['면적'].map('{:,}'.format)
```

**③ ⚠️ map 포맷의 함정 — `[프로젝트]03` CODE 81 · 82**

```python
group_agg_df.style.format('{0:.2f}')                           # 보기만 소수 둘째 자리 (값은 숫자)
group_agg_df['mean'] = group_agg_df['mean'].map('{:.2f}'.format)   # 주석: "(오류)"
```
- 두 번째 줄은 **처음 한 번은** 된다. 하지만 `mean`이 문자열로 바뀌었으므로 셀을 **다시 실행**하면 `ValueError: Unknown format code 'f' for object of type 'str'`. 또 이후 `sort_values('mean')`은 문자열 정렬이 된다. 보기 좋게 하려면 `style.format` 또는 `round(2)`.

**④ apply — 사전 조회에 전처리 끼우기 (`[판다스]merge` CODE 67~68)**

```python
meat_to_animal = {'bacon': 'pig', 'pulled pork': 'pig', 'pastrami': 'cow', ...}
df2["animal"] = df2["food"].apply(lambda x: meat_to_animal[x.lower()])   # 'Bacon', 'Pastrami' 대소문자 통일 후 조회
```
- `map(meat_to_animal)`만 쓰면 `'Bacon'`이 사전에 없어 NaN이 된다. 값을 **가공한 뒤** 조회해야 해서 lambda.

**⑤ apply(axis=1) · 사용자 함수 (`[기초]08`)**

```python
books['grade'] = books['price'].apply(price_grade)                     # 값 하나 → 등급
books['price_per_page'] = books.apply(lambda row: round(row['price'] / row['pages'], 1), axis=1)   # 행 전체
```
- 사실 `books['price'] / books['pages']`로 **벡터 연산**하면 apply 없이 더 빠르다. apply(axis=1)는 벡터 연산으로 표현하기 어려운 규칙에 쓴다.

**⑥ 문자열에서 숫자 뽑는 함수 적용 (`[프로젝트]08.음식점정보`)**

```python
df['rating_numeric'] = df['rating'].apply(extract_rating)      # '평점 4.5' → 4.5, 'N/A' → None
df.groupby('source')['rating_numeric'].agg(['count', 'mean']).round(2)
```

---

## 4-11. groupby — 그룹별 집계

### 개념

**split → apply → combine** (PDF 8.5)
1. **split** — 그룹 열의 값으로 데이터를 나눈다
2. **apply** — 각 그룹에 집계 함수(sum, mean …)를 적용
3. **combine** — 결과를 하나로 합쳐 보여 준다

| 형태 | 코드 | 인자 | 결과 객체 |
|---|---|---|---|
| **Series** groupby | `data['인구수'].groupby(data['광역시도'])` | 그룹 기준 **값(Series)** | `SeriesGroupBy` |
| **DataFrame** groupby | `data.groupby('광역시도')` | 그룹 기준 **열 이름** | `DataFrameGroupBy` |
| 열 선택 후 | `data.groupby('광역시도')['인구수']` | | `SeriesGroupBy` |

| 집계 | 예 |
|---|---|
| 단일 | `.sum()` `.mean()` `.count()` `.max()` `.size()`(행 수, NaN 포함) |
| 숫자 열만 | `.sum(numeric_only=True)` — 문자열 열이 섞인 DataFrame 전체 집계 시 |
| 여러 개 | `.agg(['max','mean','std'])` → 열이 3개인 DataFrame |
| **이름 붙여** | `.agg(평균데뷔나이="mean", 인원="count")` (named aggregation) |
| 여러 키 | `groupby(['광역시도','행정구역'])` → 결과 인덱스가 **MultiIndex** 🔗 4-14절 |
| 날짜로 | `groupby(df['date'].dt.year)` 🔗 5-2절 |
| 그룹 순회 | `for name, group in df.groupby('key1'):` |

### 코드 위치

**① 기본 3형태 — `[프로젝트]03` CODE 65~68**

```python
data['인구수'].groupby(data['광역시도']).sum()        # Series 방식 — 값을 넘김
data.groupby('광역시도').sum(numeric_only=True)       # DataFrame 전체 — 숫자 열만 (seq·x·y 까지 합쳐짐)
data.groupby('광역시도')['인구수'].sum()              # 가장 흔한 형태
data.groupby(['광역시도','행정구역'])['인구수'].sum()  # 2단 인덱스
```

**② 여러 통계를 한 번에 — `[프로젝트]03` CODE 79 · 80**

```python
group_agg_df = data.groupby('광역시도')['인구수'].agg(['max','mean','std']).fillna(0)
group_agg_df.sort_values(by='std', ascending=False)   # 편차가 큰 순 — 경기도(대도시+군 지역 혼재)
```

**③ 이름 붙인 집계 + 표본 수 필터 — `[프로젝트]11` CODE 24**

```python
company_age = df[valid].groupby("company")["debut_age"].agg(평균데뷔나이="mean", 인원="count")
company_age = company_age[company_age["인원"] >= 20].sort_values("평균데뷔나이")   # DSP 17.3, Pledis 17.8 ...
```
- 인원이 2~3명인 소속사의 평균은 우연에 크게 흔들린다. **표본 수를 같이 집계해 기준 이하는 걸러내는** 습관이 해석의 신뢰도를 지킨다.

**④ 2016 미국 대선 — `[판다스]pandas_groupby_2016USElection_Dataset`**

```python
# 후보별 총 득표 → 가로 막대
candidate_to_votes_s = primary.groupby("candidate")["votes"].sum().sort_values()
candidate_to_votes_s.plot(kind="barh", fontsize=8)

# 주별·정당별 득표 비율
state_party_to_votes_s = primary.groupby(["state", "party"])["votes"].sum()   # MultiIndex Series
state_to_votes_s       = primary.groupby("state")["votes"].sum()              # 단일 Index Series
state_party_to_vote_pcts_s = state_party_to_votes_s / state_to_votes_s        # ← 자동 정렬 나눗셈
state_party_to_vote_pcts_df = state_party_to_vote_pcts_s.unstack()            # party 를 열로
state_party_to_vote_pcts_df.plot(kind="barh", stacked=True, ax=axes1)         # 누적 막대
```

**해설**
- `(state, party)` 인덱스 Series를 `state` 인덱스 Series로 나누면, pandas가 **공통 이름(state) 단계를 맞춰서** 각 주의 합계로 나눈다. 반복문 없이 "주 안에서의 비율"이 나온다.
- `unstack()` — 인덱스의 **가장 안쪽 단계**(party)를 열로 올린다. 반대는 `stack()` 🔗 4-12절.
- Colorado는 Democrat 1.0 / Republican NaN — 데이터에 공화당 결과가 없는 주다. 비율 계산 후 NaN이 생기는 이유를 데이터에서 찾아야 한다.

**⑤ 그룹마다 "1등 행" 뽑기 — 같은 노트북 CODE 39~46**

```python
func = lambda agg_df: agg_df.sort_values("votes", ascending=False).iloc[0]
winners = primary.groupby("fips").agg(func)             # county(fips)별 1위 후보 행
winners_county_races = pd.merge(winners, counties[["fips", "RHI825214"]],
                                left_index=True, right_on="fips", how="left")
winners_county_races.rename(columns={"RHI825214": "white_pcts"}) \
    .groupby(["party", "candidate"])["white_pcts"].mean()
# 각 후보가 이긴 county 의 평균 백인 비율 — Kasich 89.2, Trump 83.2, Clinton 56.9 ...
```

⚠️ `groupby().agg(함수)`는 **열(Series) 단위로** 함수를 부르는 것이 원칙이다. 옛 pandas는 실패하면 그룹 DataFrame 전체로 다시 시도해 줬지만, 최신 pandas에서는 `Series.sort_values("votes")`가 되어 **TypeError**가 난다. 최신판에서 같은 결과를 얻는 방법:

```python
winners = primary.loc[primary.groupby("fips")["votes"].idxmax()].set_index("fips")   # 그룹별 최대 득표 행
# 또는 primary.sort_values("votes", ascending=False).drop_duplicates("fips")
```

---

## 4-12. pivot_table · stack/unstack · pandas 내장 plot

### 개념

`groupby`가 **한 방향**(세로로 긴) 결과라면, `pivot_table`은 **행 × 열 교차표**다.

```python
df.pivot_table(index='행기준', columns='열기준', values='집계할열', aggfunc='sum', fill_value=0)
```

| 인자 | 뜻 |
|---|---|
| `aggfunc` | `'sum'` `'mean'`(기본) `'count'` `'max'` … |
| `fill_value=0` | 조합이 없는 칸(NaN)을 0으로 |

- `groupby([A, B])[v].sum().unstack()` ≈ `pivot_table(index=A, columns=B, values=v, aggfunc='sum')`.
- `stack()` — 열을 인덱스 안쪽으로 내림(표 → 긴 Series). `unstack()` — 반대.
- 결과 표는 그대로 `pt.plot(kind='bar')` · `sns.heatmap(pt)`에 넣을 수 있다.

### 코드 위치

```python
# [판다스]groupby CODE 48~53 — 같은 집계를 groupby 와 pivot_table 로
primary.groupby(["state","candidate"])["votes"].sum()                       # 세로로 김
total_votes = primary.pivot_table(values="votes", index="state", columns="candidate",
                                  aggfunc="sum", fill_value=0)              # 주 × 후보 표
mean_frac_votes = primary.pivot_table(values="fraction_votes", index="state_abbreviation",
                                      columns="party", aggfunc="mean")

# [프로젝트]10 CODE 8 — 연도 × 유형 → 히트맵
pivot = df.dropna(subset=["year_added"]).pivot_table(index="type", columns="year_added",
                                                     values="show_id", aggfunc="count", fill_value=0)
sns.heatmap(pivot, cmap="Reds", ax=ax, cbar_kws={"label": "콘텐츠 수"})

# [프로젝트]11 CODE 17 — 데뷔 연도 × 성별 → 선그래프 두 줄
yearly_gender = df[df["debut_year"] >= 2000].pivot_table(index="debut_year", columns="gender",
                                                         values="stage_name", aggfunc="count", fill_value=0)
ax.plot(yearly_gender.index, yearly_gender["M"], label="남성")

# [판다스]merge CODE 100~106 — stack / unstack 왕복
df5 = df4.stack()      # (city, number) MultiIndex Series
df6 = df5.unstack()    # 다시 표
```

- `aggfunc="count"`의 `values`는 **결측이 없는 아무 열**(ID 등)이면 된다. 행 수를 세는 용도이기 때문이다.
- **pandas 내장 plot**: `Series/DataFrame.plot(kind="barh"|"bar"|"line", stacked=True, fontsize=, ax=)` — 내부적으로 matplotlib을 부른다. 반환값이 Axes라 `ax.set_xlim([50, 100])`처럼 이어서 꾸민다(`[판다스]groupby` CODE 47).

---

## 4-13. merge · concat

### 개념

| | `pd.merge(A, B)` | `pd.concat([A, B])` |
|---|---|---|
| 방향 | **옆으로**(열이 늘어남) — SQL JOIN, 엑셀 VLOOKUP | **아래로**(행이 늘어남), `axis=1`이면 옆으로 |
| 기준 | **key 열의 값** | **인덱스 / 열 이름** 위치 맞춤 |
| 쓰는 곳 | 다른 표의 정보 붙이기 | 페이지별 결과 이어 붙이기 |

**merge의 `how`**

| how | 남는 행 |
|---|---|
| `'inner'` (기본) | 양쪽에 **모두** 있는 key |
| `'left'` | 왼쪽 **전부** + 짝 없으면 NaN — 데이터를 잃지 않아 실무에서 가장 많이 씀 |
| `'right'` | 오른쪽 전부 |
| `'outer'` | 양쪽 전부 |

| key 지정 | 언제 |
|---|---|
| `on='key'` | 두 표의 key 열 이름이 같을 때 |
| `left_on='lkey', right_on='rkey'` | 이름이 다를 때 (두 열 모두 결과에 남음) |
| `left_on='key', right_index=True` | 오른쪽은 **인덱스**가 key |
| `left_index=True, right_index=True` | 양쪽 다 인덱스 |

- key 말고도 **이름이 같은 열**이 양쪽에 있으면 `_x`, `_y`가 붙는다(`suffixes=`로 변경).
- 합친 뒤 **`isna().sum()`으로 짝을 못 찾은 행 확인**하는 습관.
- 한쪽 key가 중복이면 행이 **곱해진다**(1:N 조인). `primary`는 한 county(fips)에 후보 수만큼 행이 있어서, `counties`의 county 정보가 후보 행마다 복제된다.
- 대선 실습에서 inner 결과는 13211행(노트북 주석), outer는 14359행이다. 늘어난 1천여 행은 **`counties`에만 있는 fips** — 예비선거 결과가 데이터에 없는 주의 county, 국가·주 합계 행(fips 0, 1000 …) — 가 `party`=NaN으로 붙은 것이다.

**concat 옵션**: `ignore_index=True`(인덱스 0..n-1 새로), `axis=1`(옆으로), `keys=[...]`(원본 구분 이름 → 열 이름 또는 바깥 인덱스). 열 이름이 서로 다르면 없는 칸은 NaN.

### 코드 위치

**① how 4종 — `[판다스]pandas_merge` CODE 1~8**

```python
df1 = pd.DataFrame({"key": list("bbacaab"), "data1": range(7)})
df2 = pd.DataFrame({"key": list("abd"),     "data2": range(3)})
pd.merge(df1, df2, on="key")                  # inner: b,a 만 (c·d 사라짐) — 6행
pd.merge(df1, df2, on="key", how="outer")     # c 의 data2, d 의 data1 이 NaN — 8행
pd.merge(df1, df2, on="key", how="left")      # df1 7행 전부
pd.merge(df1, df2, on="key", how="right")     # df2 기준

merge_df.loc[:, ['data1','data2']].fillna(0).astype(int)   # NaN 때문에 float 가 된 열을 다시 int 로
```
- NaN은 float 타입이라, 정수 열에 NaN이 하나라도 생기면 **열 전체가 float**(`0.0`, `1.0`)가 된다. `fillna(0).astype(int)`로 되돌린다.

**② 키 이름이 다를 때 / 인덱스 기준 — 같은 노트북 CODE 12 · 16 · 20**

```python
pd.merge(df3, df4, left_on="lkey", right_on="rkey")
pd.merge(left1, right1, left_on="key", right_index=True)       # right1 의 인덱스 a,b 가 key
pd.merge(left2, right2, how="outer", left_index=True, right_index=True)
```

**③ 실데이터 — `[판다스]groupby` CODE 19~28**

```python
pd.merge(primary, counties, on="fips")                          # state_abbreviation_x / _y 생김
pd.merge(primary, counties[['fips','RHI425214']], on="fips")    # 필요한 열만 골라 붙이면 충돌 없음
pd.merge(primary[['fips','party']], counties[['fips','RHI425214']], on="fips", how='outer')   # 14359행
counties2 = counties.set_index("fips")
pd.merge(primary, counties2[['area_name','PST045214']], left_on="fips", right_index=True)
```

**④ merge로 설명 붙이기 — `[기초]08` CODE 31**

```python
joined = pd.merge(genre_count.head(6), genre_info, on='genre', how='left')   # 설명 없는 장르는 NaN
```

**⑤ concat — `[판다스]merge` CODE 25~30 · 54 · 58**

```python
pd.concat([s1, s2, s3])                               # 아래로
pd.concat([s1, s2, s3], axis=1)                       # 옆으로 — 인덱스 합집합, 없는 칸 NaN
pd.concat([s1, s2, s3], axis=1, keys=["one", "two", "three"])   # 열 이름
pd.concat([df1, df2], axis=1)                         # 인덱스 a,b,c 기준으로 맞춰 옆으로
pd.concat([df3, df4], ignore_index=True)              # 열 순서가 달라도 이름으로 맞춤, 없는 c 는 NaN
```
- 스크래핑 실전 패턴: **페이지별 DataFrame을 리스트에 모아 마지막에 한 번 `pd.concat(frames, ignore_index=True)`** 🔗 2-8절.

---

## 4-14. MultiIndex(계층적 인덱스) · 중복 제거 · category

### 개념

**MultiIndex** — 인덱스(또는 컬럼)가 **여러 단계**. `groupby` 여러 키, `set_index([...])`, `pivot_table`에서 자연스럽게 생긴다.

| 코드 | 동작 |
|---|---|
| `set_index(["state","county"])` | 두 열을 인덱스로 (`drop=False`면 열도 유지) |
| `df.index.names = ["key1","key2"]` | 단계 이름 |
| `df.loc["a"]` | 바깥 단계가 a인 부분 |
| `df["Seoul"]` / `df[("Seoul","Green")]` | 컬럼 바깥 단계 / 정확한 한 열 |
| `df.loc["a", ("Seoul","Red")]` | 행·열 동시 |
| `sort_index(axis=0, level=1)` | 두 번째 단계 기준 정렬 |
| `unstack()` / `stack()` | 안쪽 인덱스 단계 ↔ 열 |

| 기타 | 코드 |
|---|---|
| 중복 행 제거 | `drop_duplicates(["k1"])` / `drop_duplicates(["k1","k2"], keep="last")` |
| 범주형 | `astype("category")` → `.cat.categories`, `.cat.rename_categories([...])`, `.cat.set_categories([...])` |

- **category 타입**: 값 종류가 적은 문자열 열을 코드 번호로 저장해 메모리를 아끼고, `set_categories`로 **정렬 순서**를 정할 수 있다(`sort_values(by="grade")`가 알파벳순이 아니라 지정 순서 very bad → good → very good).

### 코드 위치 — `[판다스]pandas_merge` CODE 31~48 · 60~92

```python
df = pd.DataFrame(np.arange(12).reshape((4, 3)),
                  index=[["a","a","b","b"], [1,2,1,2]],
                  columns=[["Seoul","Seoul","Busan"], ["Green","Red","Green"]])
df.index.names = ["key1", "key2"]; df.columns.names = ["city", "color"]
df.loc["a", ("Seoul", "Red")]         # key2 1 → 1, 2 → 4

df3["grade"] = df3["raw_grade"].astype("category")
df3["grade"].cat.categories = ["very good", "good", "very bad"]     # ⚠️
df3["grade"] = df3["grade"].cat.set_categories(["very bad", "bad", "medium", "good", "very good"])
```

⚠️ `cat.categories = [...]`처럼 **속성에 대입하는** 이름 바꾸기는 pandas 2.0에서 제거됐다(`AttributeError: ... has no setter`). 지금은 `df3["grade"] = df3["grade"].cat.rename_categories(["very good", "good", "very bad"])`.

---

## 4-15. PART 4 자가 점검 질문

1. `data.loc[20:25]`와 `data.iloc[20:25]`의 행 수는 각각? 왜 다른가?
2. `df[df['a'] > 1 and df['b'] < 5]`가 에러인 이유와 올바른 코드는?
3. `rename(..., inplace=False)`를 단독으로 쓰면 원본은? `df = df.rename(..., inplace=True)`를 하면 df는?
4. `value_counts().reset_index()`의 열 이름은? 왜 이 패턴을 쓰나?
5. 넷플릭스에서 `director`(29.9%)를 삭제하지 않고 대체한 근거는? `date_added`(0.1%)는?
6. `str.contains()`에 `na=False`를 빼면 어떤 문제가 생기나?
7. `split(', ')` 후 `explode()` 없이 `value_counts()`를 하면 무엇을 세게 되나?
8. `map('{:.2f}'.format)` 셀을 두 번 실행하면 왜 에러가 나나? 대안 두 가지는?
9. 넷플릭스 `rating`을 `age_group`으로 바꾼 것처럼, 딕셔너리 매핑 후 꼭 확인할 것은?
10. Series groupby와 DataFrame groupby의 인자 차이는?
11. `state_party_votes / state_votes` 나눗셈이 반복문 없이 되는 이유는?
12. 소속사별 평균 데뷔 나이에서 `인원 >= 20` 필터를 넣은 이유는?
13. `groupby().agg(lambda df: df.sort_values(...).iloc[0])`가 최신 pandas에서 실패하는 이유와 대안은?
14. 대선 데이터 merge에서 inner(13211행)보다 outer(14359행)가 많은 이유는? 늘어난 행의 `party`는 무슨 값인가?
15. 정수 열이 merge 후 `1.0, 2.0`이 된 이유와 되돌리는 법은?
16. 전국 면적-인구 상관은 음수인데 서울은 양수인 이유를 설명하라. 세종이 nan인 이유는?
