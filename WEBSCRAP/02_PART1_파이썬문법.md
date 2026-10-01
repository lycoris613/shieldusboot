# PART 1. 파이썬 문법 — 스크래핑 코드를 읽기 위한 만큼

## 1-1. 문자열 포맷팅

### 개념

| 방법 | 예 | 결과 |
|---|---|---|
| `str.format()` | `'{} {} 언어'.format(lang, value)` | `python 100 언어` |
| **f-string** (3.6+) | `f'{lang} {value} 언어'` | 같음. 실무 표준 |
| 딕셔너리 풀어 넣기 | `'.../section/{sid}'.format(**req_param)` | 🔗 `**` 는 1-5절 |
| `%` 포맷 | `'%1.1f%%'` | matplotlib `autopct`에서 아직 쓴다 |

**서식 지정자** (`:` 뒤)

| 지정자 | 뜻 | 예 → 결과 |
|---|---|---|
| `,` | 천 단위 콤마 | `f'{56784949494:,}'` → `56,784,949,494` |
| `.2f` | 소수점 둘째 자리 | `f'{24737.1234:.2f}'` → `24737.12` |
| `!r` | `repr()` 적용 | `f'{value!r}'` → 공백·개행이 보인다 🔗 9-4절 |

### 코드 위치와 해설

```python
# [프로젝트]01 CODE 6 — 딕셔너리를 ** 로 풀어 format 의 이름 자리에 넣기
req_param = {'sid': 105}
url = 'https://news.naver.com/section/{sid}'.format(**req_param)

# [프로젝트]03 CODE 25 — 통계값을 콤마 포맷으로
print(f"인구수 최대값 {data['인구수'].max():,}")

# [프로젝트]09 CODE 22 — format 의 순서 자리
print("{} null rate: {}%".format(i, round(missingValueRate, 2)))

# [프로젝트]09 CODE 63 — % 포맷: %0.f 는 정수처럼, %% 는 진짜 % 문자
plt.pie(type_counts, autopct='%0.f%%')
```

- `'{:,}'.format` 은 **함수 객체**라서 `Series.map('{:,}'.format)` 처럼 그대로 넘길 수 있다(4-10절).

---

## 1-2. 자료구조 — list · tuple · dict · set

### 개념

| 구분 | 문법 | 가변 | 순서 | 중복 | 인덱싱 | 대표 용도 |
|---|---|---|---|---|---|---|
| **list** | `[1, 2]` | ✅ | ✅ | ✅ | ✅ | 여러 건의 결과 목록 |
| **tuple** | `(1, 2)` | ❌ | ✅ | ✅ | ✅ | 함수 다중 반환값, 좌표 |
| **dict** | `{'k': 'v'}` | ✅ | ✅(3.7+) | 키 ❌ | ❌(키로) | **JSON 한 건** |
| **set** | `{1, 2}` | ✅ | ❌ | ❌ | ❌ | 중복 제거, 집합 연산 |

⚠️ `{}` 는 **빈 딕셔너리**. 빈 셋은 반드시 `set()`.
⚠️ 원소가 하나인 튜플은 `(1,)` — 콤마가 없으면 그냥 정수 1이다.

**list 메서드**

| 메서드 | 동작 | 주의 |
|---|---|---|
| `append(x)` | x **자체**를 요소 1개로 추가 | `['a'].append([1,3,5])` → `['a', [1,3,5]]` |
| `extend(iter)` | iter의 요소를 **각각** 추가 | `['a'].extend([1,3,5])` → `['a',1,3,5]` |
| `insert(i, x)` / `remove(x)` / `pop()` | 삽입 / 값으로 삭제(없으면 ValueError) / 꺼내며 삭제 | |
| `sort()` vs `sorted()` | **원본 정렬, None 반환** vs **새 리스트 반환** | 시험 단골 |
| 슬라이싱 `[1:3]`, `[:2]`, `[2:]`, `[::-1]` | 끝 인덱스 **미포함**, `-1`은 역순 | 🔗 `iloc`와 같은 규칙(4-4절) |

**dict**
- `d['k']`(없으면 KeyError) vs **`d.get('k', 기본값)`**(없으면 기본값) — API 응답처럼 키가 있을지 모를 때는 `.get`.
- `keys()/values()/items()`는 리스트가 아니라 **view 객체**(`dict_keys` 등). 진짜 리스트가 필요하면 `list(...)`.
- `'k' in d` 는 **키** 기준 검사.

**set** — `a | b`(합집합) `a & b`(교집합) `a - b`(차집합) `a ^ b`(대칭차). `discard()`는 없어도 에러 없음, `remove()`는 KeyError. 원소는 불변(hashable)이어야 해서 `{[1,2]}`는 TypeError.

**중첩 구조 = JSON** — 실습의 모든 API 응답은 "딕셔너리 안에 리스트, 리스트 안에 딕셔너리"다.

```python
response = {'query': '파이썬', 'total': 3, 'items': [{'title': ...}, {...}]}
response['items'][0]['title']      # 리스트 인덱스 → 딕셔너리 키
```

### 코드 위치

**① `extend` 로 페이지별 결과 누적 — `[프로젝트]05_01` CODE 0**

```python
member_list_300 = []
for index in range(1, 31):
    res = requests.get(f'...findNaasThx01.json?pageIndex={index}', headers=headers)
    if res.ok:
        member_list_10 = []
        for member in res.json()['resultList']:
            member_list_10.append(member)        # 한 명(dict)씩 append
        member_list_300.extend(member_list_10)   # 10명 리스트의 '요소들'을 extend
```

**해설**
- 여기서 `extend` 대신 `append`를 쓰면 `member_list_300`은 **리스트 30개가 든 리스트**가 되어 `pd.DataFrame()`이 원하는 모양이 깨진다.
- 안쪽 루프는 사실 `member_list_300.extend(res.json()['resultList'])` 한 줄로 줄일 수 있다.

**② 한 건 = dict, 여러 건 = list — `[프로젝트]04` CODE 1**

```python
song_list = []                    # [{...}, {...}]
for idx, atag in enumerate(atag_list, 1):
    song_dict = {}                # 한 곡
    song_dict['title'] = atag.text
    song_dict['id'] = song_id
    song_dict['url'] = f'https://www.melon.com/song/detail.htm?songId={song_id}'
    song_list.append(song_dict)
```

**③ set으로 중복 제거 — `[프로젝트]08.음식점정보` `remove_duplicates()`**

```python
seen_names = set()
for restaurant in restaurants:
    name_normalized = re.sub(r'[^\w가-힣]', '', restaurant['name']).lower()
    if name_normalized not in seen_names:     # set 의 in 검사는 매우 빠르다
        seen_names.add(name_normalized)
        unique_restaurants.append(restaurant)
```

- `list(set(x))`는 **순서가 사라진다**. 위처럼 "본 적 있는 것"만 set에 두고 결과는 list에 담으면 **순서를 유지한 채** 중복을 뺄 수 있다.

---

## 1-3. 가변/불변 · 참조 복사 · 얕은/깊은 복사 · 기본값 함정

### 개념

| 불변(immutable) | 가변(mutable) |
|---|---|
| int, float, str, tuple, bool, frozenset | list, dict, set, 대부분의 사용자 객체 |

```python
x = [1, 2, 3]; y = x; y.append(4)      # x 도 [1,2,3,4] — 같은 객체를 가리킴(참조 복사)
a = "Hello"; b = a; b += " World"      # a 는 그대로 — 문자열은 불변이라 새 객체를 만듦
```

| | `y = x` | `x.copy()` / `x[:]` (얕은 복사) | `copy.deepcopy(x)` |
|---|---|---|---|
| 새 객체 | ❌ | 바깥 1단계만 | 내부까지 전부 |
| 중첩 리스트 | 공유 | **공유** (함정) | 독립 |

- 튜플은 불변이지만 **안에 든 리스트는 수정 가능**(`t = ([1,2], 3); t[0].append(9)` OK). 튜플이 막는 것은 "슬롯을 다른 객체로 바꾸는 것"뿐.
- `id(x) == id(y)` 가 True면 같은 객체.

**함수 기본값 함정**

```python
def add_item(item, basket=[]):   # ❌ 기본값 리스트는 '정의 시점에 한 번만' 만들어진다
    basket.append(item); return basket
add_item('사과'); add_item('바나나')   # ['사과', '바나나'] — 이전 호출 결과가 남아 있다

def add_item(item, basket=None):  # ✅ 정석
    if basket is None: basket = []
    ...
```

### 코드 위치 — pandas의 `.copy()` 가 같은 문제다

`[프로젝트]03` CODE 38 · 45 · 75 · 95

```python
area_1000 = data.loc[data['면적'] > 1000].copy()
area_1000.loc[:, '면적2'] = area_1000['면적'].map('{:,}'.format)
```

**해설**
- `data.loc[조건]`의 결과는 원본의 **일부를 보는 것일 수도, 복사본일 수도** 있다. 거기에 값을 대입하면 pandas가 `SettingWithCopyWarning`을 띄운다("원본을 바꾸려는 거야, 복사본을 바꾸려는 거야?").
- 걸러낸 표를 **따로 가공할 생각이면 `.copy()`로 명시적 복사**를 하는 것이 관례다. 파이썬 리스트의 얕은 복사와 같은 고민이 표 단위로 나타난 것이다.
- 🔗 4-5절 `inplace`, 4-8절 결측치 처리.

---

## 1-4. 제어문 · 반복 도구

### 개념

- `if/elif/else` — 들여쓰기(공백 4칸)로 블록 구분. **빈 리스트·빈 문자열·None·0은 False**.
- `for x in 반복대상` / `while 조건` / `break`(중단) / `continue`(다음 반복으로).
- `range(1, 4)` → 1,2,3 (끝 미포함). 결과는 리스트가 아니라 **range 객체**라 보려면 `list(range(10))`. `np.arange(10)`은 배열을 바로 준다.
- `enumerate(iter, start=1)` — (번호, 값). **`start`로 1부터 셀 수 있다.**
- `zip(a, b)` — 같은 자리끼리 튜플로. `dict(zip(keys, values))`로 딕셔너리 생성.
- `itertools.count(0)` — 0,1,2,… **끝없이** 세는 반복자. 반드시 탈출 조건이 필요.

### 코드 위치

**① `if res.ok:` / `if not res.ok:` — 전 노트북 공통**

```python
res = requests.get(url, headers=headers)
if res.ok:              # 200 이상 400 미만이면 True
    ...
else:
    print(f'Error Code = {res.status_code}')
```

**② `if song_dd:` / `if lyric_div:` — 빈 결과 방어 (`[프로젝트]04` CODE 3)**

```python
song_dd = soup.select('div.meta dd')      # 못 찾으면 빈 ResultSet
if song_dd:                               # 비어 있으면 False → 인덱싱 안 함
    song_lyric_dict['앨범'] = song_dd[0].get_text(strip=True).replace('\xa0', ' ')
```

- `'\xa0'`는 HTML의 `&nbsp;`(줄바꿈 없는 공백). `strip()`으로 안 지워지는 경우가 있어 `replace`로 보통 공백으로 바꾼다.

**③ `dict(zip(dt_list, dd_list))` — `[프로젝트]05_01` CODE 2**

```python
dt_list = [li_tag.find('dt').text for li_tag in li_tags]   # ['사무실 전화', '사무실 호실', ...]
dd_list = [...]                                            # ['02-784-...', '의원회관 ...', ...]
member_detail_dict = dict(zip(dt_list, dd_list))           # {'사무실 전화': '02-...', ...}
```

- HTML의 `<dl><dt>항목</dt><dd>값</dd></dl>` 구조는 **zip → dict 한 줄**로 딕셔너리가 된다. `[기초]00` CODE 35~37의 `dict(zip(['이름','취미','나이'], ['파이썬','독서','20']))`이 이 코드의 연습판이다.

**④ `enumerate(..., 1)` — `[프로젝트]02` CODE 3, `04` CODE 1, `05` CODE 2**

```python
for idx, img_url in enumerate(img_url_list, 1):
    print(f'---> 다운로드 번호 {idx}')
```

**⑤ `itertools.count` 무한 루프 — `[프로젝트]06` CODE 8 (팟빵 2단계)**

```python
from itertools import count
from time import sleep
for offset in count(0):
    req_param = {"offset": offset, "limit": 20, ...}
    res = requests.get(url, params=req_param)
    if res.ok:
        episode_list = res.json()['data']
        for episode in episode_list:
            ...
            sleep(0.5)          # 사람처럼 보이게
```

⚠️ **탈출 조건이 빠져 있다.** 마크다운(MD 7)에는 "`json_data['data']` 길이가 0이면 무한루프를 탈출한다"고 적혀 있는데 코드에는 그 줄이 없다. 실제 출력도 `KeyboardInterrupt`(사용자가 강제 중단)로 끝났다. 다음 두 줄이 있어야 한다.

```python
        if len(episode_list) == 0:
            break
```

⚠️ 또 `offset`을 1씩 올리는데 `limit=20`이다. API에서 `offset`이 "건너뛸 개수"라면 1씩 올리면 에피소드 20개 중 19개가 매번 중복 다운로드된다. 이런 API는 보통 `count(0, 20)`처럼 **limit만큼** 올린다. 브라우저 Network 탭에서 두 번째 페이지 요청의 offset 값을 보고 확인할 것.

⚠️ `[프로젝트]05_01/05_02` CODE 2 — 바깥 루프 변수 `idx`를 안쪽에서 `idx = sosok.find(',')`로 **덮어쓴다**. 지금은 `idx`를 덮어쓰기 전에만 출력해서 티가 안 나지만, 루프 아래쪽에서 `idx`를 쓰면 엉뚱한 값이 나온다. 쉼표 앞 첫 위원회만 필요하면 `sosok.split(',')[0]` 한 줄이면 된다.

---

## 1-5. 컴프리헨션 · 함수 · lambda · `*` / `**`

### 개념

**컴프리헨션** — `[식 for 항목 in 대상 if 조건]`. 딕셔너리도 `{k: v for k, v in items}`.

**함수** — `def 이름(매개변수):` + `return`. 여러 값을 `return b, a` 하면 **튜플**로 묶여 나오고 `x, y = f()`로 언패킹한다. 첫 줄 `"""docstring"""`으로 용도를 적는다.
- **기본값 인자** `def greet(name, greeting='안녕하세요')` — 생략 가능. 위치 인자로 넘기면 기본값 대신 사용.
- **키워드 인자** `greet('민수', greeting='환영합니다')` — 이름으로 지정.

**lambda** — `lambda 인자: 식`. `return`을 쓰지 않는다. 정렬 key, 포맷 변환에 쓴다.

**`*` 와 `**` — 위치에 따라 정반대**

| 위치 | 역할 | 예 |
|---|---|---|
| 함수 **호출** `f(*리스트)` | **푼다** | `print(*[1,2,3])` = `print(1,2,3)` |
| **대입** `a, *b = ...` | **모은다**(리스트) | `first, *rest = [10,20,30]` |
| 함수 **정의** `def f(*args)` | **모은다**(튜플) | `args = (1,2,3)` |
| `**` 호출 / 정의 | dict ↔ 키워드 인자 | `f(**{'name':'홍길동'})`, `def f(**kwargs)` |

> "괄호를 벗긴다"고 기억한다. `f(*[1,2,3])`은 대괄호를 벗겨 `f(1,2,3)`.

**`zip(*pairs)` = 전치(transpose)**

```python
pairs = [('정부', 68), ('정책', 68), ('주택', 50)]
words, vals = zip(*pairs)          # ('정부','정책','주택'), (68,68,50)
words, vals = map(list, zip(*Counter(text.split()).most_common(3)))   # 리스트로 → plt.bar(words, vals)
```

### 코드 위치

**① 리스트 컴프리헨션 — `[프로젝트]01` CODE 13 (기존 방식과 나란히)**

```python
img_url_list = []                                        # 기존 방식
for img_tag in img_tags:
    img_url_list.append(img_tag['src'])

img_url_list2 = [img_tag['src'] for img_tag in img_tags]  # Pythonic 방식
```

`[기초]00` CODE 23 — 빈 줄 걸러내기: `[lyric for lyric in lyric_list if len(lyric) != 0]`
`[프로젝트]03` CODE 86 — 폰트 찾기: `[(f.name, f.fname) for f in fm.fontManager.ttflist if 'Mal' in f.name]`
`[스트림릿예제]08 blog` — 없는 컬럼 걸러내기: `[c for c in ['title','bloggername','postdate','link'] if c in result.columns]`

**② 함수로 묶기 — 같은 코드가 "셀 → 함수 → 인자"로 진화한다**

| 파일 | 셀 코드 | 함수화 |
|---|---|---|
| `[프로젝트]01` | CODE 6 뉴스 제목 | CODE 8 `print_news(section_name)` |
| `[프로젝트]02` | CODE 1~3 이미지 다운로드 | CODE 5 `download_image(title, url)` |
| `[프로젝트]03` | CODE 44 경기도 평균/편차 | CODE 45 `pop_mean_std(df, sido_name)` → CODE 47 모든 시도 순회 |
| `[프로젝트]03` | CODE 94 서울 그래프 | CODE 95 `show_pop_area(sido_name)` → `[스트림릿예제]06` 에서 재사용 |
| `[프로젝트]06` | CODE 4 팟빵 1채널 | CODE 5 `podbbang_download(channel_id, channel_title)` → CODE 8 전체 |
| `[프로젝트]04` | — | CODE 33 `search_album(keyword)` (DB 조회) |

⚠️ `[프로젝트]01` CODE 8 `print_news` — `sid = section_dict.get(section_name, '사회')`. 없는 섹션을 넣으면 기본값으로 **숫자 102가 아니라 문자열 `'사회'`** 가 들어가 URL이 `.../section/사회`가 된다. `section_dict.get(section_name, 102)`가 맞다.

**③ lambda — `[기초]00` CODE 31~34 ↔ `[프로젝트]03` CODE 100**

```python
def my_format(val): return "{:,}".format(int(val))     # 일반 함수
result = lambda val: "{:,}".format(int(val))            # 같은 것
ax1.get_yaxis().set_major_formatter(plt.FuncFormatter(lambda x, loc: "{:,}".format(int(x))))
```
- `FuncFormatter`는 **인자 2개(값, 위치)** 를 받는 함수를 요구해서 `lambda x, loc:` 가 된다. 🔗 6-4절.

**④ `sorted(key=lambda)` — `[기초]02` CODE 14**

```python
books_sorted = sorted(books, key=lambda b: b['price'], reverse=True)
```

---

## 1-6. 내장 함수 모음

| 함수 | 설명 | 실습에서 |
|---|---|---|
| `len` · `type` · `id` · `isinstance` · `dir` | 길이 / 타입 / 객체 주소 / 타입 검사(상속 고려) / 속성 목록 | `print(type(a_tags))` → `bs4.element.ResultSet` |
| `chr(65)`→`'A'` / `ord('A')`→65 | 코드 ↔ 문자 | 🔗 9-1절 |
| `range` · `enumerate` · `zip` · `map` · `filter` | 결과가 **이터레이터** → 보려면 `list()` | |
| `sorted` · `sum` · `min` · `max` · `abs` · `round(x, n)` | | `round(missingValueRate, 2)` |
| `int` · `float` · `str` · `list` · `tuple` · `dict` · `set` | 형 변환 | `int(input(...))` |
| `input()` | 항상 **문자열** 반환 | `[프로젝트]08.Image` CODE 5 `int(input('다운로드할 이미지 개수'))` |
| `print(sep=, end=)` | | |
| `repr()` | 개발자용 정확한 표현 | 🔗 9-4절 |

---

## 1-7. 예외 처리

### 개념

```python
try:
    위험한 코드
except 특정예외 as e:     # 해당 예외만 처리, e 에 오류 객체
    ...
except (A, B) as e:       # 여러 예외 묶기
    ...
finally:
    항상 실행             # 자원 정리 위치 (driver.quit(), conn.close())
```

- 구체적인 예외를 먼저, 넓은 예외(`Exception`)를 나중에.
- ⚠️ 이유 없이 **맨 `except:`** 를 쓰면 오타(`NameError`)나 Ctrl+C(`KeyboardInterrupt`)까지 삼켜서 디버깅이 불가능해진다.

### 코드 위치

| 상황 | 코드 | 파일 |
|---|---|---|
| requests 실패 | `except requests.exceptions.RequestException as e:` + `raise_for_status()` | `[기초]05` `fetch()`, `[스트림릿예제]08·09` |
| 오류 응답 본문까지 보기 | `if e.response is not None: e.response.json()` | `[스트림릿예제]08 blog`, `09 local` |
| 날짜 형식 불일치 | `except ValueError as exp:` | `[기초]10` strptime |
| 디코딩 실패 | `except UnicodeDecodeError as exp: exp.reason` | `[기초]12` |
| Selenium 요소 없음/시간 초과 | `except NoSuchElementException` / `TimeoutException` | `[프로젝트]08.GitHub로그인` |
| DB 오류 → 되돌리기 | `except Exception as exp: db.rollback()` + `finally: db.close()` | `[프로젝트]07` |
| 자원 정리만 보장 | `try: ... finally: conn.close(); engine.dispose()` (except 없음) | `[프로젝트]04` CODE 21·31·33 |

**`[프로젝트]08.GitHub로그인` CODE 16 — "예외가 났다 = 성공"인 역발상**

```python
try:
    div_error = driver.find_element(By.CSS_SELECTOR, 'div.js-flash-alert')   # 실패 배너
    if div_error:
        login_ok = False
except NoSuchElementException:
    login_ok = True          # 실패 배너가 '없다' = 로그인 성공
```

⚠️ `[프로젝트]08.음식점정보`는 `try: ... except: rating = "N/A"` 처럼 맨 `except:`를 10여 곳에서 쓴다. 선택 항목이 없을 때 넘어가려는 의도지만, `except NoSuchElementException:`으로 좁혀야 다른 오류를 숨기지 않는다. 이 코드에는 더 큰 문제(대기 시간 폭증)도 있다 → 3-5절.

---

## 1-8. 파일 · JSON · 경로 (os, pathlib)

### 개념

**`with open(경로, 모드, encoding=)`** — 블록이 끝나면 자동으로 닫힌다.

| 모드 | 뜻 | 실습 |
|---|---|---|
| `'r'` / `'w'` | 텍스트 읽기 / 쓰기(덮어씀) | `yesterday.txt`, `sample.txt` |
| `'rb'` / `'wb'` | **바이너리** 읽기 / 쓰기 | 이미지·mp3 저장, 파일 업로드 |

- 텍스트 모드는 `encoding='utf-8'`을 꼭 적는다(Windows 기본은 cp949). 바이너리 모드에는 encoding이 없다.

**JSON**

| 함수 | 방향 |
|---|---|
| `json.dump(obj, f)` / `json.load(f)` | 객체 ↔ **파일** |
| `json.dumps(obj)` / `json.loads(s)` | 객체 ↔ **문자열** (`res.json()`은 `json.loads(res.text)`와 같다) |

- `ensure_ascii=False` — 한글을 그대로 저장(기본 True면 `유튜...`처럼 저장). `indent=2` — 사람이 읽기 좋게.

**경로**

| 함수 | 동작 | 주의 |
|---|---|---|
| `os.mkdir(p)` | 폴더 **하나** 생성 | 중간 폴더가 없으면 에러, 이미 있어도 에러 |
| `os.makedirs(p, exist_ok=True)` | `a/b/c`처럼 **중간 폴더까지** 생성 | `exist_ok=True`면 있어도 OK |
| `os.path.isdir(p)` / `exists(p)` | 폴더인가 / 존재하는가 | |
| `os.path.join('img', name)` | OS에 맞는 구분자로 합치기 | Windows는 `img\a.jpg` |
| `os.path.basename(url)` | 마지막 조각(파일명) | URL에도 쓸 수 있다 |
| `os.path.dirname(p)` | 폴더 부분 | |
| `urllib.parse.urljoin(base, sub)` | 상대 URL → 절대 URL | |
| `Path(__file__).resolve().parent` | **스크립트 파일 위치** 기준 경로 | 🔗 8-9절 |

### 코드 위치

**① 바이너리 저장 — `[프로젝트]01` CODE 11**

```python
res = requests.get(img_url, headers=req_header)
img_data = res.content                       # bytes
file_name = os.path.basename(img_url)        # '..._IMAG01_1.jpg'
with open(file_name, 'wb') as file:
    file.write(img_data)
```

**② 폴더 준비 세 가지 방식**

```python
if not os.path.isdir('img'): os.mkdir('img')          # [프로젝트]01 CODE 13, 02 CODE 2
os.makedirs(imgdir_name, exist_ok=True)               # [프로젝트]01 CODE 15 (리팩토링판)
if not os.path.exists(save_dir): os.mkdir(save_dir)   # [프로젝트]08.Image
os.makedirs(os.path.dirname(filepath), exist_ok=True) # [스트림릿예제]08·09 save_json
```

- `[프로젝트]02` MD 4가 정리한 대로 `mkdir`은 한 폴더만, `makedirs`는 `./a/b/c` 같은 하위 폴더까지 만든다. 팟빵 코드(`06` CODE 4)는 `mp3`를 `mkdir`로 만든 뒤 `mp3/반란의영문법`을 `makedirs`로 또 만드는데, `makedirs` 하나면 둘 다 만들어진다.

**③ 상대 URL → 절대 URL — `[프로젝트]02` CODE 7, `05_01` CODE 2**

```python
from urllib.parse import urljoin
img_url = urljoin(detail_url, member['mThumbImgUrl'])
# 'https://www.assembly.go.kr/members/22nd/...' + '/static/portal/img/...'
# → 'https://www.assembly.go.kr/static/portal/img/...'
```

- `/`로 시작하는 경로는 도메인 루트 기준으로 붙는다. 문자열 `+`로 붙이면 `//`가 겹치거나 경로가 꼬인다.

**④ JSON 저장 → DataFrame — `[프로젝트]04` CODE 7~9, `05_02` CODE 7~8, `01` CODE 30~31**

```python
with open('data/songs100.json', 'w', encoding='utf-8') as file:
    json.dump(song_lyric_list, file)        # ⚠️ ensure_ascii 없음 → 파일 안 한글이 \uXXXX
song_df = pd.read_json('data/songs100.json')   # 읽을 때는 정상 복원된다
```

- 저장한 파일을 **사람이 열어 볼 일**이 있으면 `ensure_ascii=False, indent=2`를 붙인다(`[기초]03` CODE 7, `[스트림릿예제]08`의 `save_json`은 붙였다).
- `json.dump` → `pd.read_json`으로 한 번 파일을 거치면, 수집(오래 걸림)과 분석(여러 번 반복)을 **분리**할 수 있다. 커널이 죽어도 다시 수집하지 않아도 된다.

---

## 1-9. 정규표현식 — `re` 모듈

### 개념

HTML 파서(BS4)는 **태그 단위**로 찾고, 정규식은 **문자열 패턴**으로 찾는다. PDF 4.1은 정규식을 "가장 빠르지만 규칙 만들기가 번거로운 방법"으로 소개한다. 실습에서는 **BS4로 태그를 찾은 뒤 그 안의 지저분한 문자열을 정리할 때** 정규식을 쓴다.

| 함수 | 역할 |
|---|---|
| `re.compile(p)` | 패턴 객체를 미리 만들어 재사용 |
| `p.sub(바꿀값, 문자열)` / `re.sub(p, 바꿀값, 문자열)` | 매칭된 부분 치환 |
| `re.search(p, s)` | 처음 매칭 1개 → Match 객체 또는 None |
| `m.group(0)` / `m.group(1)` | 매칭 전체 / 첫 번째 `( )` 그룹 |
| `re.findall(p, s)` | 매칭 전부를 리스트로 |

| 패턴 | 뜻 |
|---|---|
| `\d` · `\d+` | 숫자 · 숫자 1개 이상 |
| `\w` | 문자·숫자·밑줄(유니코드 한글 포함) |
| `[...]` / `[^...]` | 괄호 안 중 하나 / 괄호 안 **제외** |
| `\.?` | 점이 있어도 되고 없어도 됨 |
| `( )` | 그룹 — 나중에 `group(n)`으로 꺼냄 |

⚠️ 패턴은 **raw 문자열 `r'...'`** 로 쓴다. 일반 문자열에서 `\d`, `\{`는 파이썬이 먼저 이스케이프로 해석하려 한다.

### 코드 위치

**① `search + group` — Melon 곡 ID 뽑기 (`[프로젝트]04` CODE 1)**

```python
# href = "javascript:melon.play.playSong('1000002721',37928381);"
matched = re.search(r'(\d+)\);', href)       # '숫자들);' 을 찾되 숫자만 그룹으로
if matched:
    song_id = matched.group(1)               # '37928381'   (group(0) = '37928381);')
```

⚠️ `if matched:`가 거짓이면 `song_id`는 **이전 곡의 값**이 그대로 남는다. `else: song_id = None` 또는 `continue`가 있어야 안전하다.

**② `compile + sub` — 개행·탭 제거 (`[프로젝트]04` CODE 3, `05_01` CODE 2)**

```python
pattern = re.compile(r'[\n\r\t]')
song_lyric_dict['가사'] = pattern.sub('', lyric)

pattern = re.compile(f'[\n\r\t]')       # 05_01 은 f-문자열 — 동작은 하지만 r'' 이 의도에 맞다
dd_text = pattern.sub('', dd_tag.text.strip()).replace(" ", "")
```

- `f'[\n\r\t]'`는 파이썬이 `\n`을 **진짜 개행 문자**로 바꾼 뒤 정규식에 넘긴다. 문자 클래스 안에 실제 개행이 들어가도 결과는 같아서 동작하지만, 우연히 맞은 것이다.

**③ 특수문자 제거로 파일명 만들기 (`[프로젝트]06` CODE 1)**

```python
cleaned_text = re.sub('[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]', '', text)
```

⚠️ 출력에 `SyntaxWarning: invalid escape sequence '\{'`가 찍혀 있다. raw 문자열이 아니어서 파이썬이 `\{`를 해석하려다 경고한 것이다. `r'[...]'`로 바꾸면 사라진다. 또 "허용할 문자만 남긴다"는 반대 방향이 더 짧고 안전하다: `re.sub(r'[^\w가-힣]', '', text)` (음식점 크롤러가 이 방식).

**④ `findall` — 평점 문자열에서 숫자만 (`[프로젝트]08.음식점정보` `analyze_ratings`)**

```python
numbers = re.findall(r'\d+\.?\d*', str(rating_str))    # '평점 4.5 (리뷰 12)' → ['4.5', '12']
return float(numbers[0]) if numbers else None
```

---

## 1-10. 클래스와 스크립트 진입점

이론 자료에는 없고 실습 코드에만 나오는 문법이라 필요한 만큼만 정리한다.

### 개념

```python
class 이름:
    def __init__(self, 인자):     # 생성자: 객체가 만들어질 때 실행
        self.속성 = 값             # self = 만들어지는 그 객체 자신
    def 메서드(self, ...):         # 첫 인자는 항상 self
        ...
obj = 이름(인자)
obj.메서드()
```

| 특수 메서드 | 불리는 때 |
|---|---|
| `__init__` | `이름(...)`으로 객체 생성 시 |
| `__repr__` | 셀 마지막 줄 출력, **리스트·딕셔너리 안에** 담겨 출력될 때 |
| `__str__` | `print(obj)`, `f"{obj}"` |
| `__len__` / `__bool__` | `len(obj)` / `if obj:` |
| `__slots__` | 인스턴스가 가질 수 있는 속성 이름을 제한 |

**`if __name__ == "__main__":`** — 이 파일을 **직접 실행**할 때만 아래 코드를 실행한다. 다른 파일에서 `import`할 때는 실행되지 않는다.

### 코드 위치

**① 크롤러 클래스 — `[프로젝트]08.음식점정보`**

```python
class RestaurantRatingCrawler:
    def __init__(self, use_selenium=True):
        self.session = requests.Session()                       # 헤더를 유지하는 세션
        self.session.headers.update({'User-Agent': '...'})
        self.driver = None
        if use_selenium:
            self.setup_selenium()
    def crawl_naver_place(self, search_keyword, max_results=50): ...
    def crawl_kakao_map(...), crawl_siksinhot(...)
    def crawl_all_sources(...)   # 세 사이트 결과를 extend 로 합침
    def remove_duplicates(...), save_to_csv(...), save_to_json(...), analyze_ratings(...)
    def close(self):
        if self.driver: self.driver.quit()

def main():
    crawler = RestaurantRatingCrawler()
    try: ...
    finally:
        crawler.close()          # 무슨 일이 있어도 브라우저 종료

if __name__ == "__main__":
    main()
```

**해설**
- 드라이버·세션 같은 **공유 자원을 `self`에 한 번 만들어 두고** 여러 메서드가 같이 쓴다. 함수로만 짜면 매 함수에 `driver`를 인자로 넘겨야 한다.
- 사이트마다 `crawl_xxx` + `extract_xxx_info` 한 쌍씩 → 사이트가 늘어도 같은 모양으로 붙일 수 있다.

**② `SecretStr` — 출력하면 가려지는 비밀번호 객체 (`[프로젝트]08.GitHub로그인` CODE 24)** → 9-8절에서 자세히.

**③ `12selenium_infinite_scroll.py` · `[스트림릿예제]` — 스크립트 파일은 `main()` + `if __name__` 구조**를 쓴다(노트북은 셀이 진입점이라 필요 없다).

---

## 1-11. PART 1 자가 점검 질문

1. `member_list_300.append(member_list_10)`으로 바꾸면 `pd.DataFrame(member_list_300)`은 어떻게 되나? → 1-2절
2. `sort()`와 `sorted()`의 반환값 차이는?
3. `data.loc[조건]`에 바로 새 열을 대입하면 왜 경고가 뜨고, `.copy()`는 무엇을 해결하나? → 1-3절
4. `def f(x, basket=[])`가 위험한 이유를 "정의 시점"이라는 말로 설명하라.
5. `enumerate(lst, 1)`의 `1`은 무엇인가?
6. 팟빵 전체 다운로드 코드가 끝나지 않은 이유 두 가지는? → 1-4절 ⑤
7. `zip(*pairs)`는 왜 전치(transpose)가 되나? `zip(pairs)`와의 차이는?
8. `print_news('스포츠')`를 호출하면 어떤 URL이 만들어지나? → 1-5절 ②
9. 맨 `except:`가 위험한 이유는?
10. `os.mkdir('mp3/반란의영문법')`은 `mp3` 폴더가 없을 때 어떻게 되나?
11. `re.search(r'(\d+)\);', s).group(0)`과 `.group(1)`의 차이는?
12. 정규식 문자열 앞에 `r`을 붙이지 않으면 어떤 경고가 나오나?
13. `if __name__ == "__main__":` 아래 코드는 언제 실행되지 않나?
