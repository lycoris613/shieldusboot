# PART 2. 수집 ① — requests · BeautifulSoup · Open API

## 2-1. HTTP 클라이언트 3종 비교

### 개념

| | `urllib` (표준) | **`requests`** | `Selenium` |
|---|---|---|---|
| 설치 | 불필요(내장) | `pip install requests` | `pip install selenium` |
| 코드 | 장황함 | **간결** | 브라우저 조작 코드 |
| 세션·로그인 유지 | 번거로움 | 쉬움(`Session`) | 브라우저가 알아서 |
| JavaScript 실행 | ❌ | ❌ | ✅ (보이는 건 다 가져옴) |
| 속도·자원 | 빠름 | **빠름** | 느림 · 이미지/CSS/JS 전부 내려받음 |

- **원칙**: requests로 되면 requests(가장 효율적). JavaScript가 필요해 보여도 **JSON API를 찾으면 requests로 되는 경우가 많으니** 다각도로 검토한 뒤 Selenium을 쓴다(PDF 3.6).

### 코드 위치 — `urllib.request` 방식 (`[프로젝트]01` CODE 21~23, 네이버 캡차)

```python
import urllib.request
url = f"https://openapi.naver.com/v1/captcha/nkey?code={code}"
request = urllib.request.Request(url)                   # 요청 객체를 먼저 만들고
request.add_header("X-Naver-Client-Id", CLIENT_ID)      # 헤더를 하나씩 추가
request.add_header("X-Naver-Client-Secret", CLIENT_SECRET)
response = urllib.request.urlopen(request)              # 실제 전송
rescode = response.getcode()                            # 상태 코드
if rescode == 200:
    response_body = response.read()                     # bytes
    print(response_body.decode('utf-8'))
else:
    print("Error Code:" + rescode)                      # ⚠️ int + str → TypeError
```

**requests로 쓰면 같은 일이 이렇다**

```python
res = requests.get('https://openapi.naver.com/v1/captcha/nkey',
                   params={'code': code}, headers={'X-Naver-Client-Id': ..., 'X-Naver-Client-Secret': ...})
res.json()
```

- 네이버 개발자 문서의 예제 코드가 `urllib` 방식이라 그대로 가져온 것이다.
- ⚠️ `"Error Code:" + rescode`는 정수와 문자열을 더해서, **정작 오류가 났을 때 TypeError로 죽는다**. `f"Error Code: {rescode}"`로 써야 한다. (정상 응답만 받아서 드러나지 않았다.)
- 파파고 번역 예(PDF 7.3)도 같은 구조에 POST 데이터를 `urlopen(request, data=data.encode('utf-8'))`로 넘긴다. `urlopen`은 bytes만 받아서 `encode`가 필요하다 🔗 9-2절.

---

## 2-2. requests GET — 요청과 응답

### 개념 — 요청

| 인자 | 용도 | 예 |
|---|---|---|
| `headers={...}` | User-Agent, Referer, API 키 | 2-3·2-5절 |
| `params={...}` | 쿼리스트링 `?k=v&k2=v2` 자동 조립 + **URL 인코딩** | `res.url`로 실제 주소 확인 |
| `params=[('k','v1'), ('k','v2')]` | 튜플 리스트면 **같은 키를 여러 번** 보낼 수 있다(dict는 불가) | |
| `timeout=10` | 10초 넘으면 포기 | `[기초]05`, `[프로젝트]08.Image` |
| `verify=False` | SSL 인증서 검증 끄기(보안 위험, 기본 True) | |

- requests의 기본 User-Agent는 `python-requests/버전`이다. 서버가 이 값을 보고 거부할 수 있다.

### 개념 — 응답(`Response`)

| 속성/메서드 | 타입 | 뜻 |
|---|---|---|
| `status_code` | int | 200, 404 ... |
| `ok` | bool | **200 이상 400 미만**이면 True |
| `headers` | `CaseInsensitiveDict` | 키의 대소문자 무시(`res.headers['content-type']`도 OK) |
| `content` | **bytes** | 서버가 보낸 **원본** — 이미지·mp3는 이것 |
| `text` | **str** | `content`를 `res.encoding`으로 디코딩한 것 |
| `json()` | dict/list | `json.loads(res.text)`와 같음(역직렬화) |
| `encoding` / `apparent_encoding` | str | 디코딩 규칙 / 내용으로 추정한 규칙 🔗 9-7절 |
| `url` | str | 실제 요청된 최종 URL(params 포함) |
| `raise_for_status()` | — | 4xx/5xx면 예외 발생 |

### 코드 위치 — 뉴스 제목 추출 (`[프로젝트]01` CODE 6)

```python
url = 'https://news.naver.com/section/{sid}'.format(**{'sid': 105})
req_header = {'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) ... Chrome/151.0.0.0 Safari/537.36'}

res = requests.get(url, headers=req_header)
print(res.status_code, res.ok)                  # 200 True
if res.ok:
    soup = BeautifulSoup(res.text, 'html.parser')
    a_tags = soup.select("div sa_text a[href*='https://n.news.naver.com/mnews']")
    for a_tag in a_tags:
        title = a_tag.tex.strip()
        link = a_tag['href']
```

**해설**
- MD 0: "user-agent 요청 헤더를 **반드시** 설정해야 한다." — 기본값 `python-requests/...`로 요청하면 봇으로 판단해 정상 페이지를 주지 않을 수 있기 때문이다.

⚠️ **이 셀의 출력이 `0`인 이유 — 선택자 오타**
```
"div sa_text a[...]"    ← div 안의 <sa_text> 라는 '태그'를 찾는다 → 그런 태그는 없다 → 0개
"div.sa_text a[...]"    ← class="sa_text" 인 div  ← 의도한 것
```
점(`.`) 하나 차이로 태그 선택자와 클래스 선택자가 갈린다. 🔗 2-6절 CSS 선택자 표.

⚠️ **선택자를 고치면 다음 줄에서 터진다** — `a_tag.tex`는 오타인데 에러가 나지 않는다. BeautifulSoup에서 `tag.이름`은 `tag.find('이름')`의 줄임이라 `<tex>`라는 자식 태그를 찾아 **None**을 돌려준다. 그 다음 `.strip()`에서 `AttributeError: 'NoneType' object has no attribute 'strip'`. 올바른 코드는 `a_tag.text.strip()` 또는 `a_tag.get_text(strip=True)`.

---

## 2-3. 바이너리 다운로드 — 이미지 · mp3

### 개념

1. `requests.get(파일URL)` → 2. `res.content`(bytes) → 3. `open(파일, 'wb')`로 저장.
- 텍스트가 아니므로 `.text`가 아니라 **`.content`**, 파일은 **`'wb'`**.
- **Referer 헤더**: "이 요청이 어느 페이지에서 왔는가". 이미지·파일 서버가 다른 사이트에서 직접 가져가는 것(핫링킹)을 막으려고 Referer가 없거나 다르면 거부한다.

### 코드 위치

**① 네이버 웹툰 — `[프로젝트]01` CODE 11 · 13 · 15, `02` CODE 1~5**

```python
webtoon_url = 'https://comic.naver.com/webtoon/detail?titleId=812354&no=208&week=sun'
req_header = {'referer': webtoon_url, 'user-agent': '...'}      # MD 10: referer 를 반드시 설정

res = requests.get(webtoon_url, headers=req_header)
soup = BeautifulSoup(res.text, 'html.parser')
print(len(soup.select("img[src$='.jpg']")))     # 263 — 페이지의 모든 jpg (광고·썸네일 포함)
print(len(soup.select("img[src*='IMAG01']")))   # 13  — 본문 컷만
img_url_list2 = [img_tag['src'] for img_tag in soup.select("img[src*='IMAG01']")]

for img_url in img_url_list2:
    res = requests.get(img_url, headers=req_header)            # 이미지 요청에 referer
    file_path = os.path.join('img', os.path.basename(img_url))
    with open(file_path, 'wb') as file:
        file.write(res.content)
```

**해설**
- `$=`(끝이 일치)로는 263개, `*=`(부분 일치)로 URL 속 `IMAG01`을 잡으니 13개. **URL 패턴을 관찰해서 선택자를 좁히는** 것이 핵심이다.
- `02` CODE 5 `download_image(title, url)`는 이 코드를 함수로 만들고 `img/웹툰제목/` 하위 폴더에 저장한다(그래서 `makedirs`).

⚠️ `01` CODE 15(리팩토링판): 페이지 요청은 `requests.get(url)`로 **헤더 없이** 보내고, 실패 시 `exit()`를 부른다. 노트북에서 `exit()`는 **커널을 종료**시킬 수 있다. 함수 안이라면 `return`, 셀이라면 `raise`나 `if/else`로 처리한다.

⚠️ `02` CODE 7(추천 웹툰 목록)은 출력이 `0 / []`다. `div.genreRecomInfo2`는 **옛 네이버 웹툰 HTML의 클래스명**이고 현재 웹툰 홈은 CSR로 바뀌어 이 요소가 HTML에 없다. PDF 4.7의 경고 그대로 — "선택자를 너무 타이트하게 잡으면 마크업이 조금만 바뀌어도 못 찾는다."

**② 팟빵 mp3 — `[프로젝트]06` CODE 4 · 5**

```python
url = f'https://app-api6.podbbang.com/channels/{channel_id}/episodes'
req_param = {"offset": 0, "limit": 20, "sort": "desc", "episode_id": 0, "focus_center": 0, "with": "image"}
res = requests.get(url, params=req_param)               # 목록은 JSON API (CSR)
for episode in res.json()['data']:
    title = episode['title']
    mp3_url = episode['media']['url']                   # 중첩 dict 탐색
    if mp3_url:
        res = requests.get(mp3_url, headers={'referer': f'https://www.podbbang.com/channels/{channel_id}', ...})
        with open(f"mp3/{channel_title}/{clean_text(title)}.mp3", 'wb') as f:
            f.write(res.content)
```

**해설**
- CODE 4는 URL 문자열에 `?offset=0&limit=20...`을 직접 붙였고, CODE 5는 같은 값을 `params=` 딕셔너리로 옮겼다. 결과는 같지만 `params`가 읽기 쉽고 한글·특수문자 인코딩도 알아서 해 준다 🔗 9-6절.
- 파일명은 URL의 `basename` 대신 **에피소드 제목**을 쓰고, 파일명에 못 쓰는 문자(`/ ? : * " < > |`)를 `clean_text()`로 지운다 🔗 1-9절 ③.
- CODE 3의 주석에 JSON 원문이 남아 있는데 `"title": "[유튜..."` 처럼 한글이 `\uXXXX`로 보인다. 서버가 `ensure_ascii=True` 방식으로 보낸 JSON이고, `res.json()`이 자동으로 한글로 되돌린다.

---

## 2-4. requests POST · 파일 업로드

### 개념

| 형태 | 코드 | Content-Type |
|---|---|---|
| 폼 데이터 | `requests.post(url, data={'k1':'v1'})` | `application/x-www-form-urlencoded` |
| 같은 키 여러 개 | `data=(('k1','v1'), ('k1','v3'))` | 〃 |
| JSON 본문 | `json={...}` (= `data=json.dumps(obj)` + 헤더) | `application/json` |
| **파일 업로드** | `files={'img1': open('a.jpg','rb')}` (+ `data=`로 추가 필드) | `multipart/form-data` |

### 코드 위치 — `[프로젝트]01` CODE 17 (httpbin 업로드)

```python
upload_files = {
    'img1': open('img/f1.jpg', 'rb'),
    'img2': open('img/f2.jpg', 'rb'),
}
res = requests.post('http://httpbin.org/post', files=upload_files)
print(res.json()['files']['img1'])     # 'data:application/octet-stream;base64,/9j/4AAQ...'
```

**해설**
- httpbin.org는 받은 요청을 **그대로 JSON으로 되돌려 주는** 연습용 서버다. 업로드한 바이너리를 JSON에 담아야 하니 **base64**로 바꿔 `data:` URI로 돌려준 것이다 🔗 9-5절.
- ⚠️ `open()`만 하고 닫지 않았다. `with open(...) as f1, open(...) as f2:` 블록 안에서 `files={'img1': f1, 'img2': f2}`로 보내는 것이 정석이다.

---

## 2-5. Open API — 네이버 개발자센터 · NAVER API HUB

### 개념

**웹 API(오픈 API)**: 사이트의 기능을 외부에 공개한 것. HTTP로 요청하고 주로 **JSON**(예전엔 XML)으로 응답한다. 정리된 데이터를 받을 수 있어 스크래핑보다 안정적이다. JSON은 데이터 교환의 표준 포맷이다.

```
XML:  <name>홍길동</name>
JSON: { "name": "홍길동" }
```

**네이버 검색 API 두 갈래** — 실습 코드에 둘 다 나온다

| | 네이버 개발자센터 | NAVER API HUB (네이버 클라우드) |
|---|---|---|
| 발급 | developers.naver.com 애플리케이션 등록 | 네이버 클라우드 콘솔 |
| 헤더 이름 | `X-Naver-Client-Id` / `X-Naver-Client-Secret` | `X-NCP-APIGW-API-KEY-ID` / `X-NCP-APIGW-API-KEY` |
| 블로그 검색 URL | `https://openapi.naver.com/v1/search/blog.json` | `https://naverapihub.apigw.ntruss.com/search/v1/blog` |
| 코드 | `[프로젝트]01` CODE 26, `[스트림릿예제]08 book` | `[프로젝트]01` CODE 29, `[스트림릿예제]08 blog`, `09 local` |

**검색 API 공통 파라미터**: `query`(검색어), `display`(개수), `sort`

| API | display | sort |
|---|---|---|
| 블로그·도서 | 최대 100 | `sim`(정확도) / `date`(날짜) |
| **지역(local)** | **1~5** | `random`(정확도+거리) / `comment`(리뷰순) — sim/date 없음 |

- 응답 `items`의 `title`·`description`에는 검색어가 `<b>파이썬</b>`처럼 **HTML 태그째** 들어 있다. 표에 보여 주려면 태그를 지운다(`[스트림릿예제]09`의 `clean_title()`).

### 코드 위치

**① 블로그 검색 → JSON 파일 → DataFrame (`[프로젝트]01` CODE 26~34)**

```python
headers = {'X-Naver-Client-Id': CLIENT_ID, 'X-Naver-Client-Secret': CLIENT_SECRET}
payload = {'query': '파이썬', 'display': 100, 'sort': 'sim'}
res = requests.get('https://openapi.naver.com/v1/search/blog.json', params=payload, headers=headers)
print(len(res.json()['items']))          # 100
pprint(res.json()['items'])              # 중첩 구조를 줄바꿈해서 보기 좋게 출력

items_data = res.json()['items']
with open('data/blog.json', 'w', encoding='utf-8') as file:
    json.dump(items_data, file)
blog_data = pd.read_json('data/blog.json')
blog_data['bloggername'].unique()
```

- `CLIENT_ID`/`CLIENT_SECRET`은 `.env`에서 읽는다(CODE 19) 🔗 9-8절.
- `pprint` 임포트 두 방식(CODE 25): `import pprint` → `pprint.pprint(x)` / `from pprint import pprint` → `pprint(x)`.

**② 캡차 API 3단계 (`[프로젝트]01` CODE 21~23)**

| 단계 | URL | 응답 |
|---|---|---|
| 1. 키 발급 | `/v1/captcha/nkey?code=0` | `{"key":"oUdW..."}` |
| 2. 이미지 요청 | `/v1/captcha/ncaptcha.bin?key=...` | **이미지 bytes** → `'wb'`로 `captcha.jpg` 저장 |
| 3. 입력값 검증 | `/v1/captcha/nkey?code=1&key=...&value=사용자입력` | `{"result":true, ...}` |

- 한 API 안에서 JSON 응답(1·3단계)과 바이너리 응답(2단계)이 섞여 있다. 응답 종류에 따라 `.decode('utf-8')`로 읽을지 `'wb'`로 쓸지 고른다.

**③ 안전한 items 꺼내기 (`[스트림릿예제]08 book`)**

```python
return res.json().get('items', [])     # res.json()['items'] 보다 나은 방식 — 키가 없으면 빈 리스트
```

---

## 2-6. BeautifulSoup — HTML 파싱

### 개념

**HTML DOM** — HTML 요소(태그)들이 부모-자식 관계로 이어진 **트리**. `html > body > div`.

**문자열에서 정보를 꺼내는 두 방법** (PDF 4.1)
1. 정규 표현식 — 가장 빠르지만 규칙 만들기가 복잡
2. **HTML 파서** — DOM 트리를 탐색. 적용이 쉽다 (BeautifulSoup4, lxml)

**파서 종류**

| 파서 | 코드 | 특징 |
|---|---|---|
| `html.parser` | `BeautifulSoup(html, 'html.parser')` | 내장, 설치 불필요 |
| `lxml` | `BeautifulSoup(html, 'lxml')` | 외부 C 라이브러리, **더 빠르고 유연**. `pip install lxml` 필요 |

**태그를 찾는 두 방식**

| 방식 | 하나 | 여러 개(리스트) | 인자 |
|---|---|---|---|
| **find 계열** | `find()` | `find_all()` | 태그명, `id=`, `class_=`, `attrs={}` |
| **CSS 선택자** | `select_one()` | `select()` | CSS 선택자 문자열 |

`〔정정〕` PDF 4.3·4.4는 `find('.className')`으로 클래스를 찾는다고 적었지만, `find`의 첫 인자는 **태그 이름**이라 `'.className'`이라는 태그를 찾아 **None**이 된다. 클래스로 찾으려면 `find(class_='className')` 또는 `select_one('.className')`.

`〔정정〕` PDF 4.4의 "BeautifulSoup는 `:nth-child`를 지원하지 않는다"는 옛 버전 이야기다. 현재 bs4(내부 soupsieve 엔진)는 `:nth-child`, `:nth-of-type`을 지원한다. 실습 코드도 `li:nth-of-type(n+4)`를 쓴다(2-7절).

**꺼내는 방법**

| 코드 | 결과 |
|---|---|
| `tag.text` / `tag.get_text(strip=True)` | 안쪽 **모든** 텍스트 / 앞뒤 공백 제거 |
| `tag.string` | 텍스트 자식이 **딱 하나**일 때만 문자열, 아니면 None |
| `tag['href']` | 속성값(없으면 KeyError) |
| `tag.get('href')` | 속성값(없으면 None) |
| `tag.attrs` | 모든 속성을 dict로 |
| `tag.이름` | `tag.find('이름')`의 줄임 ⚠️ 2-2절 `a_tag.tex` 함정 |

- `select()`의 반환 타입은 `bs4.element.ResultSet`(리스트처럼 쓰는 객체), 원소는 `Tag`. `[프로젝트]04` CODE 3 주석: "`song_dd`는 ResultSet 타입, `song_dd[0]`는 Tag 타입".

**CSS 선택자 표** (PDF 4.7 + 실습 코드 사례)

| 선택자 | 뜻 | 실습 사례 |
|---|---|---|
| `tag` / `#id` / `.class` / `*` | 태그 / 아이디 / 클래스 / 전부 | `select('li')` |
| `A B` | A의 **자손** B (직계 아니어도) | `div.wrap_song_info a` |
| `A > B` | A의 **직계 자식** B | `.placelist > .PlaceItem` |
| `A, B` | A 또는 B | `"#user-repositories-list h3 a, li.col-12 h3.wb-break-all a"` |
| `tag.c1.c2` | 클래스 c1과 c2를 **모두** 가진 태그 | |
| `tag.c1 .c2` | c1 태그의 자손 중 c2 (공백 주의!) | |
| `tag[attr]` | 속성이 있는 태그 | `li[data-id]` |
| `[attr="v"]` | 정확히 일치 | `input[type="submit"]`, `form[action='/logout']` |
| `[attr*="v"]` | **포함** | `img[src*='IMAG01']`, `a[href*='playSong']`, `a[href*='goArtistDetail']` |
| `[attr^="v"]` | **시작** | `img[src^="https://images.unsplash.com/photo"]` |
| `[attr$="v"]` | **끝** | `img[src$='.jpg']` |
| `:nth-of-type(n+4)` | 같은 태그 중 4번째부터 | `ul.list li:nth-of-type(n+4)` |

⚠️ 선택자를 너무 길고 구체적으로 잡으면 HTML이 조금만 바뀌어도 못 찾는다(PDF 4.7). `#tb_list tr .wrap_song_info a[href*=playSong]`보다 `div.wrap_song_info a[href*='playSong']`처럼 **의미 있는 클래스 + 속성 조건** 정도가 적당하다.

### 코드 위치

**① find 계열 vs select 계열 — Melon (PDF 4.6 ↔ 4.8 ↔ 실습)**

```python
# PDF 4.6 (find 방식)
for tr in soup.find(id='tb_list').find_all('tr'):
    tr.find(class_='wrap_song_info') ...

# PDF 4.8 / [프로젝트]04 CODE 1 (select 방식) — 한 줄
atag_list = soup.select("div.wrap_song_info a[href*='playSong']")    # 100
```

**② select_one — 곡 상세의 가수 (`[프로젝트]04` CODE 3)**

```python
singer_span = soup.select_one("a[href*='goArtistDetail'] span")
song_lyric_dict['가수'] = singer_span.text
```

**③ 연습용 HTML 파싱 → DataFrame (`[기초]05` CODE 8~11)**

```python
items = soup.select('li.book')
rows = [{'title': it.select_one('a').get_text(strip=True),
         'price': int(it.select_one('span.price').get_text(strip=True)),
         'link':  it.select_one('a')['href']} for it in items]
pd.DataFrame(rows).to_csv('parsed_books.csv', index=False, encoding='utf-8-sig')
```

---

## 2-7. SSR + CSR 혼합 수집 — 실전 파이프라인 두 개

### 개념

한 사이트 안에서도 **목록/본문은 HTML, 일부 숫자·목록은 Ajax JSON**인 경우가 흔하다. 두 수단을 섞는다.

```
목록 확보 ─┬─ HTML 파싱(select)      ─┐
           └─ JSON API(res.json())   ─┼─→ 한 건씩 상세 요청 → dict 로 합치기 → list
                                      │
상세 보강 ─┬─ 상세 HTML 파싱          ─┘
           └─ 별도 Ajax JSON (좋아요 등)
```

### 코드 위치 ① — Melon 100 (`[프로젝트]04` CODE 1 · 3)

```python
# 1단계: 차트 HTML(SSR) 에서 100곡의 제목·ID
atag_list = soup.select("div.wrap_song_info a[href*='playSong']")
# <a href="javascript:melon.play.playSong('1000002721',37928381);">LOVE ATTACK</a>
song_id = re.search(r'(\d+)\);', atag['href']).group(1)

# 2단계: 곡마다 상세 HTML(SSR)
res = requests.get(f'https://www.melon.com/song/detail.htm?songId={song_id}', headers=headers)
song_dd = soup.select('div.meta dd')                    # 앨범 / 발매일 / 장르
song_lyric_dict['앨범'] = song_dd[0].get_text(strip=True).replace('\xa0', ' ')
lyric_div = soup.select('div#d_video_summary')          # 가사

# 3단계: 좋아요 수는 Ajax JSON (CSR)
ajax_url = f'https://www.melon.com/commonlike/getSongLike.json?contsIds={song_id}'
song_lyric_dict['좋아요'] = requests.get(ajax_url, headers=headers).json()['contsLike'][0]['SUMMCNT']
```

**해설**
- `href` 자체가 JavaScript 호출이라 URL이 아니다. 여기서 ID만 정규식으로 뽑아 상세 URL을 **직접 조립**한다.
- 좋아요 JSON 구조(CODE 3 주석): `{"contsLike": [{"CONTSID":..., "LIKEYN":"N", "SUMMCNT":109704}], ...}` → 딕셔너리 → 리스트 → 딕셔너리.
- 100곡 × (상세 1 + 좋아요 1) = 200번 요청. Melon은 User-Agent가 없는 요청에 정상 페이지를 주지 않으므로(406 Not Acceptable 등) 모든 요청에 같은 `headers`를 넣었다.

⚠️ `print(f'==> {idx} {song['title']}')` — f-문자열 안에서 **바깥과 같은 따옴표**를 썼다. 파이썬 **3.12부터** 허용된 문법이라 3.11 이하에서는 SyntaxError. `{song["title"]}`처럼 다른 따옴표를 쓰면 어느 버전에서나 된다.

### 코드 위치 ② — 국회의원 300명 (`[프로젝트]05_01` · `05_02` CODE 0 · 3)

```python
# 1단계: 목록 = JSON API, 1~30 페이지 (페이지당 10명)
for index in range(1, 31):
    url = f'https://www.assembly.go.kr/portal/cnts/cntsNaas/findNaasThx01.json?pageIndex={index}'
    member_list_300.extend(requests.get(url, headers=headers).json()['resultList'])
# 한 명 = {'hgNm':'강경숙', 'polyNm':'조국혁신당', 'origNm':'비례대표', 'cmitNm':'교육위원회, 예산...',
#          'reeleGbnNm':'초선', 'sexGbnNm':'여', 'mLinkUrl':'/members/22nd/KANGKYUNGSOOK', 'mThumbImgUrl':...}

# 2단계: 상세 = HTML
for idx, member in enumerate(member_list_300, 1):
    detail_url = f"https://www.assembly.go.kr{member['mLinkUrl']}"
    soup = BeautifulSoup(requests.get(detail_url, headers=headers).text, 'html.parser')
    li_tags = soup.select('ul.list li:nth-of-type(n+4)')            # 앞 3개(이름 등)는 JSON 에 있으니 건너뜀
    dt_list = [li.find('dt').text for li in li_tags]
    dd_list = [re.compile(r'[\n\r\t]').sub('', li.find('dd').text.strip()).replace(" ", "") for li in li_tags]
    member_detail_dict = dict(zip(dt_list, dd_list))                # HTML 부분
    member_detail_dict['이름'] = member['hgNm']                     # JSON 부분을 덧붙임
    member_detail_dict['정당'] = member['polyNm']
    ...
    member_detail_dict['이미지'] = urljoin(detail_url, member['mThumbImgUrl'])
    member_detail_list.append(member_detail_dict)
```

**해설** (05_02 CODE 3 주석의 12단계를 압축)
- 목록 JSON에 이미 있는 값(이름·정당·성별·선거구·당선횟수)은 JSON에서, JSON에 **없는 값**(사무실 전화·호실·홈페이지·이메일·보좌관·비서관)은 상세 HTML에서 가져와 **한 dict로 합친다**.
- `dd` 텍스트는 줄바꿈·탭·공백이 섞여 있어 정규식 + `replace(" ", "")`로 정리한다.
- 결과 행 수는 296(출력 `(296, 16)`)으로 300이 아니다. 수집 시점의 현직 의원 수(공석)나 일부 상세 요청 실패 때문일 수 있다 — 어느 쪽이든 **수집 후 반드시 개수를 확인**하고, 실패한 요청은 `else: print(res.status_code)`로 남겨 두는 습관이 필요하다.

---

## 2-8. 수집 결과를 DataFrame으로 — 세 가지 방법

### 개념

| 방법 | 코드 | 평가 |
|---|---|---|
| **A. 리스트를 통째로** | `pd.DataFrame(member_detail_list)` | 가장 간단하고 빠름 ✅ |
| B. 파일 경유 | `json.dump(list, f)` → `pd.read_json(path)` | 수집과 분석을 분리할 때 ✅ |
| C. 빈 표 + 한 행씩 concat | 아래 코드 | 수업 코드 방식. 느리고 경고 가능 |

### 코드 위치 — 방법 C (`[프로젝트]04` CODE 5, `05_02` CODE 5)

```python
member_df = pd.DataFrame(columns=['이름','정당','선거구','당선횟수', ... ,'의원실 안내'])   # 컬럼만 있는 빈 표
for member_detail in member_detail_list:
    df_new_row = pd.DataFrame.from_records([member_detail])     # dict 1개 → 1행짜리 DataFrame
    member_df = pd.concat([member_df, df_new_row])              # 이어붙이기
    # (예전 방식) member_df = member_df.append(pd.Series(member_detail), ignore_index=True)
member_df.reset_index(drop=True, inplace=True)                  # 인덱스가 전부 0 → 0..n-1
member_df.index = member_df.index + 1                           # 1부터 시작
```

**해설**
- `DataFrame.append()`는 pandas 2.0에서 **삭제**됐다. 주석으로 남은 옛 코드가 그 흔적이고, 지금은 `concat`을 쓴다.
- `from_records([dict])` — **리스트로 감싸야** 1행이 된다.
- 한 행씩 `concat`하면 매번 표 전체를 새로 복사해서 행 수가 많을수록 느려진다. 또 pandas 2.1~2.2는 빈 DataFrame과 concat할 때 FutureWarning을 낼 수 있다. **리스트를 다 모은 뒤 한 번에 `pd.DataFrame(list)`** (또는 `pd.concat(frames)` 한 번)이 정석이다. `[기초]08` CODE의 "반복문으로 리스트에 모으고 마지막에 한 번만 concat" 패턴이 그 원리다.
- 방법 A에서 컬럼 순서를 지정하고 싶으면 `pd.DataFrame(list, columns=[...])`.
- 🔗 `concat`·`reset_index`는 4-5·4-13절.

---

## 2-9. PART 2 자가 점검 질문

1. `res.ok`가 True인 상태 코드 범위는?
2. `res.content`와 `res.text`의 타입과 관계는? 이미지 저장에는 어느 쪽?
3. `params`를 딕셔너리 대신 튜플 리스트로 주는 이유는?
4. 웹툰 이미지 다운로드에서 Referer를 빼면 어떻게 되나? 왜?
5. `"div sa_text a"`와 `"div.sa_text a"`의 차이는?
6. `a_tag.tex.strip()`은 어떤 에러가, 왜, 어느 줄에서 나는가?
7. `find('.title')`이 None을 반환하는 이유와 올바른 두 가지 방법은?
8. `[attr*=v]`, `[attr^=v]`, `[attr$=v]`를 실습 사례와 함께 말하라.
9. 브라우저에는 보이는데 `res.text`에는 없는 숫자를 가져오려면? → 0-4, 2-7절
10. 국회의원 수집에서 JSON에서 가져온 항목과 HTML에서 가져온 항목을 나눠 말하라.
11. `DataFrame.append()` 대신 무엇을 쓰나? 그보다 더 나은 방법은?
12. 네이버 개발자센터와 API HUB의 헤더 이름 차이는?
13. 지역 검색 API의 `display` 최대값과 `sort` 값은?
