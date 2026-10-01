# PART 3. 수집 ② — Selenium 브라우저 자동화

## 3-1. Selenium이란

### 개념

- **웹 브라우저 자동화 도구**. 원래는 웹 애플리케이션 **테스트용** 프레임워크. Java·C#·Python·Ruby 등 여러 언어 지원.
- **WebDriver** API로 OS에 설치된 크롬 등을 조종해 클릭·키 입력 이벤트를 발생시킨다.
- 브라우저가 JavaScript까지 실행하므로 **나중에 로드되는 콘텐츠까지, 눈에 보이는 것은 모두** 가져올 수 있다. 반면 `requests.text`는 "페이지 소스 보기"처럼 **최초 HTML만** 본다.
- 대가: 이미지·CSS까지 다 내려받아 **느리고 자원을 많이 쓴다**. 그래서 0-4·2-1절 원칙대로 JSON API를 먼저 찾아본다.

| | Selenium | Requests · Scrapy |
|---|---|---|
| 방식 | 브라우저를 직접 띄움 | HTTP 요청만 |
| 동적 페이지 | ✅ | ❌ (사전 분석 필요) |
| 속도 | 느림 | 빠름 |

### 드라이버 설치의 변천 — 실습 코드에 세 세대가 섞여 있다

| 세대 | 코드 | 비고 |
|---|---|---|
| ① 수동 | chromedriver.exe를 직접 내려받아 경로 지정 | PDF 6.3. 크롬 버전과 맞춰야 해서 번거로움 |
| ② webdriver-manager | `webdriver.Chrome(service=Service(ChromeDriverManager().install()))` | `[프로젝트]08.GitHub로그인`, `08.Image`. `pip install webdriver-manager` |
| ③ **Selenium Manager (4.6+)** | `webdriver.Chrome()` / `webdriver.Chrome(options=options)` | `[기초]07`, `08.음식점정보`, `12selenium_infinite_scroll.py`. 추가 설치 없음 |

- 버전 확인: `import selenium; selenium.__version__` → 실습 환경 `'4.40.0'`. 4.6 이상이므로 ③만으로 충분하다.

---

## 3-2. 드라이버와 옵션

### 코드 위치 — `[프로젝트]08.음식점정보` `setup_selenium()` · `12selenium_infinite_scroll.py` `create_driver()`

```python
from selenium.webdriver.chrome.options import Options      # = webdriver.ChromeOptions
options = Options()
options.add_argument('--headless=new')          # 창 없이 실행
options.add_argument('--no-sandbox')            # 리눅스 서버·도커 환경 대응
options.add_argument('--disable-dev-shm-usage') # /dev/shm(공유 메모리) 부족 문제 회피
options.add_argument('--disable-gpu')
options.add_argument('--window-size=1920,1080') # headless 기본 창이 작아서 반응형 레이아웃이 바뀌는 것 방지
options.add_argument('--user-agent=Mozilla/5.0 ...')
driver = webdriver.Chrome(options=options)
```

| 옵션 | 설명 |
|---|---|
| `--headless` / `--headless=new` | 창 없이 실행. `=new`는 크롬의 새 headless 모드(실제 브라우저와 동작이 더 같음). PDF 6.6은 `'headless'`, `'window-size=1920x1080'`처럼 `--` 없이 쓴 옛 표기 |
| `--window-size` | headless에서 특히 중요 |
| `--user-agent` | headless 크롬은 UA에 `HeadlessChrome`이 들어가 차단되기 쉽다 |

---

## 3-3. 요소 찾기 — `find_element(By.XXX, 값)`

### 개념

| 메서드 | 결과 | 못 찾으면 |
|---|---|---|
| `find_element(By, 값)` | 첫 번째 **WebElement** | `NoSuchElementException` |
| `find_elements(By, 값)` | **리스트** | 빈 리스트 `[]` |

| By | 예 |
|---|---|
| `By.CSS_SELECTOR` (가장 많이 사용) | `'div#top ul.menu li'`, `'#query'`, `"li[data-id]"` |
| `By.ID` / `By.NAME` / `By.CLASS_NAME` | `"searchIframe"` / `'commit'` / `'quote'` |
| `By.TAG_NAME` | `'body'` |
| `By.XPATH` | `'//div[@class]'` |

`〔정정〕` PDF 6.4의 `find_element_by_name()`, `find_elements_by_css_selector()` 등은 **Selenium 4에서 삭제**됐다. 실습 환경(4.40)에서 쓰면 AttributeError. 반드시 `find_element(By.CSS_SELECTOR, ...)` 형태로 쓴다.

- WebElement에서: `.text`(보이는 텍스트), `.get_attribute('href')`, `.is_displayed()`, `.is_enabled()`.
- 요소 **안에서** 다시 찾기: `card.find_element(By.CSS_SELECTOR, 'span.text')` — BS4의 `item.select_one()`과 같은 발상.

### 코드 위치 — `[프로젝트]08.GitHub로그인` CODE 7 (python.org → PyPI 클릭)

```python
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
driver.get("https://www.python.org/")
li_tags = driver.find_elements(By.CSS_SELECTOR, 'div#top ul.menu li')
pypi = None
for li_tag in li_tags:
    if li_tag.text == 'PyPI':          # 보이는 글자로 원하는 메뉴 찾기
        pypi = li_tag
time.sleep(3)
pypi.click()
time.sleep(5)
driver.quit()
```

---

## 3-4. 조작 — 클릭 · 입력 · 스크롤 · 프레임 · 스크린샷

### 개념

| 코드 | 동작 | 실습 |
|---|---|---|
| `driver.get(url)` / `driver.title` / `driver.current_url` | 이동 / 제목 / 현재 주소 | 전부 |
| `elem.click()` | 클릭 | PyPI 메뉴, 지식인 링크, 저장소 |
| `elem.send_keys('텍스트')` | 입력 | 검색어, 아이디·비밀번호 |
| `elem.send_keys(Keys.ENTER / RETURN / END)` | 키 입력 | 검색 실행, 로그인, 맨 아래로 스크롤 |
| `form.submit()` | 폼 제출 | GitHub 로그아웃 |
| `driver.execute_script("JS 코드", 인자)` | 브라우저에서 JavaScript 실행 | 무한 스크롤, 스크롤 박스 |
| `driver.switch_to.frame(el)` / `.default_content()` | iframe 안으로 / 밖으로 | 네이버 지도 |
| `driver.save_screenshot(p)` / `get_screenshot_as_file(p)` | 화면 저장 | `img/naver.png` |
| `driver.page_source` | **렌더링 후** HTML 문자열 | 3-6절 |
| `driver.quit()` | 브라우저 종료 (**반드시**) | 전부 |

### 코드 위치

**① 검색어 입력 → 엔터 → 결과 클릭 (`[프로젝트]08.GitHub로그인` CODE 12)**

```python
driver.get('https://www.naver.com/')
search_tag = driver.find_element(By.CSS_SELECTOR, '#query')
search_tag.send_keys('python')
search_tag.send_keys(Keys.ENTER)
a_tags = driver.find_elements(By.CSS_SELECTOR, 'ul.lst_type li a.lnk_head')
if a_tags:                     # find_elements 는 못 찾아도 에러가 없으니 비었는지 검사
    a_tags[0].click()
```

**② iframe 전환 + 스크롤 박스 (`[프로젝트]08.음식점정보` `crawl_naver_place`)**

```python
search_iframe = WebDriverWait(self.driver, 10).until(
    EC.presence_of_element_located((By.ID, "searchIframe")))
self.driver.switch_to.frame(search_iframe)          # 이후 find 는 iframe 안에서
...
self.driver.execute_script("arguments[0].scrollTop = arguments[0].scrollHeight",
                           self.driver.find_element(By.CSS_SELECTOR, ".scroll_box"))
...
self.driver.switch_to.default_content()             # 원래 문서로 복귀
```

**해설**
- 네이버 지도의 검색 결과 목록은 **iframe(페이지 속 다른 페이지)** 안에 있다. 전환하지 않으면 `li[data-id]`를 절대 못 찾는다.
- 목록이 창 전체가 아니라 **목록 상자(`.scroll_box`)만** 스크롤되므로, `window.scrollTo`가 아니라 그 요소의 `scrollTop`을 바꾼다. `arguments[0]`은 `execute_script`의 두 번째 인자(WebElement)다.

**③ 폼 직접 제출로 로그아웃 (`08.GitHub로그인` CODE 16)**

```python
logout_forms = driver.find_elements(By.CSS_SELECTOR, "form[action='/logout']")
if logout_forms:
    logout_forms[0].submit()     # 드롭다운의 'Sign out' 은 링크가 아니라 form 안의 버튼
```

- 메뉴를 여러 번 클릭하는 대신 **URL로 직접 이동**(`driver.get(f"https://github.com/{GIT_USERNAME}?tab=repositories")`)하거나 **폼을 직접 제출**하면 UI 변경에 덜 취약하다.

---

## 3-5. 대기 — sleep · implicit · explicit

### 개념

페이지 요소는 **바로 나타나지 않는다**. 로딩 전에 찾으면 `NoSuchElementException`.

| 방식 | 코드 | 특징 |
|---|---|---|
| 고정 대기 | `time.sleep(3)` | 무조건 3초. 너무 짧으면 실패, 길면 낭비. **사람처럼 보이기**용으로도 씀 |
| **암묵적** | `driver.implicitly_wait(10)` | 한 번 설정하면 **모든** `find_element`가 최대 10초까지 기다림 |
| **명시적** (권장) | `WebDriverWait(driver, 10).until(EC.조건((By, 값)))` | **특정 조건**이 만족되는 즉시 진행, 10초 넘으면 `TimeoutException` |

| `expected_conditions` | 뜻 |
|---|---|
| `presence_of_element_located` | DOM에 **존재** |
| `visibility_of_element_located` | 존재 + **보임** |
| `element_to_be_clickable` | 보임 + **클릭 가능** |

⚠️ 조건에는 튜플 하나를 넘긴다: `EC.presence_of_element_located((By.CSS_SELECTOR, 'x'))` — 괄호가 **두 겹**.
⚠️ **암묵적 대기와 명시적 대기를 섞지 마라.** 두 대기가 겹쳐 대기 시간을 예측할 수 없게 된다.

### 코드 위치

**① 명시적 대기의 정석 — `[프로젝트]08.GitHub로그인` CODE 16**

```python
wait = WebDriverWait(driver, 10)
driver.get('https://github.com/login')
wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'input#login_field')))
...
first_repo = wait.until(EC.element_to_be_clickable(
    (By.CSS_SELECTOR, "#user-repositories-list h3 a, li.col-12 h3.wb-break-all a")))
first_repo.click()
...
except TimeoutException as texp:
    print("요소 대기 시간 초과 - 셀렉터를 현재 GitHub DOM 에 맞게 확인하세요:", texp)
```
- `STEP_PAUSE = 3`의 `time.sleep`은 로딩용이 아니라 **사람이 눈으로 확인하라고** 멈춘 것이다(주석에 명시).

**② ⚠️ 암묵적 대기가 성능 폭탄이 되는 경우 — `[프로젝트]08.음식점정보`**

```python
self.driver.implicitly_wait(10)                  # setup_selenium() 에서 설정
...
try:
    rating_elem = element.find_element(By.CSS_SELECTOR, ".rating_real")   # 평점이 없는 가게라면?
except:
    rating = "N/A"
```

- 평점·리뷰수·카테고리·주소처럼 **없을 수도 있는** 요소를 `find_element`로 찾는데 암묵적 대기가 10초로 걸려 있다. 없는 항목 하나마다 **10초씩** 기다린 뒤 예외가 난다. 가게 20곳 × 없는 항목 2개면 400초.
- 해결: 선택 항목은 `find_elements`(없으면 즉시 빈 리스트)로 찾고 `els[0].text if els else "N/A"`로 처리하거나, 암묵적 대기를 0으로 두고 필요한 곳만 명시적 대기.
- 이 클래스는 `WebDriverWait`(iframe)도 같이 써서 위의 "섞지 마라" 규칙도 어긴다.

---

## 3-6. Selenium + BeautifulSoup

### 개념

Selenium으로 **렌더링까지 끝난 HTML**을 얻고(`driver.page_source`), 파싱은 익숙한 BS4로 한다. 브라우저 조작은 Selenium, 데이터 추출은 BS4 — 역할 분담.

### 코드 위치 — `[프로젝트]08.Selenium사용_Image_download` CODE 2 · 4 · 5

```python
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)  # headless
driver.get('https://unsplash.com/s/photos/cat')
time.sleep(3)                                              # JS 로딩 대기
soup = BeautifulSoup(driver.page_source, 'html.parser')    # 렌더링된 HTML
img_tags = soup.select('img[src^="https://images.unsplash.com/photo"]')
img_urls = [tag['src'] for tag in img_tags]
for idx, img_url in enumerate(img_urls, 1):
    res = requests.get(img_url)                            # 다운로드는 다시 requests 로
    with open(os.path.join(save_dir, f'cat_{idx}.jpg'), 'wb') as f:
        f.write(res.content)
driver.quit()
```

**구글 이미지판(CODE 4)** 은 `body`에 `Keys.END`를 3번 보내 스크롤해서 이미지를 더 로드하고, `img.get('src')`가 `http`로 시작하는 것만 모은다. **CODE 5**는 `input()`으로 키워드·개수를 받아 목표 개수가 찰 때까지 스크롤한다.

**해설 — 출력이 모두 0개인 이유(추정)**
- Unsplash: 프리미엄 이미지는 `plus.unsplash.com`(주석에 남아 있음)이고, 일반 이미지는 지연 로딩(lazy loading)이라 3초 뒤 첫 화면에 `src`가 채워지지 않았을 수 있다.
- Google: headless 브라우저를 봇으로 판정하거나 동의(consent) 화면을 먼저 띄우는 경우가 많다. 이런 때는 `driver.save_screenshot()`으로 **headless 브라우저가 실제로 본 화면**을 찍어 확인하는 것이 첫 디버깅 단계다.

⚠️ CODE 5의 `while True:` 스크롤 루프는 **탈출 조건이 "이미지가 목표 개수 이상"뿐**이다. 검색 결과가 목표보다 적으면 영원히 돈다. 1-4절 팟빵과 같은 실수. 최대 스크롤 횟수나 "더 이상 늘지 않으면 중단"(3-7절) 조건이 필요하다.

---

## 3-7. 무한 스크롤 수집 — `12selenium_infinite_scroll.py`

### 개념

스크롤할 때마다 Ajax로 콘텐츠가 붙는 페이지는 requests로 **첫 화면만** 얻는다. Selenium으로 끝까지 스크롤한 뒤 한꺼번에 파싱한다. 끝에 도달했는지는 **페이지 높이가 더 이상 늘지 않는가**로 판단한다.

### 코드와 해설

```python
TARGET_URL = "https://quotes.toscrape.com/scroll"      # 스크래핑 연습용 사이트

def scroll_and_collect(driver, max_scrolls=15, pause=1.0):
    driver.get(TARGET_URL)
    time.sleep(pause)
    last_height = driver.execute_script("return document.body.scrollHeight")   # JS 결과를 파이썬으로 받음
    for i in range(max_scrolls):                        # ① 무한루프 방지 상한
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(pause)                               # Ajax 로딩 대기
        new_height = driver.execute_script("return document.body.scrollHeight")
        if new_height == last_height:                   # ② 높이가 그대로 = 끝
            break
        last_height = new_height
    quotes = []
    for card in driver.find_elements(By.CSS_SELECTOR, "div.quote"):
        quotes.append({"text":   card.find_element(By.CSS_SELECTOR, "span.text").text,
                       "author": card.find_element(By.CSS_SELECTOR, "small.author").text,
                       "tags":   ", ".join(t.text for t in card.find_elements(By.CSS_SELECTOR, "a.tag"))})
    return quotes

def main():
    driver = create_driver(headless=True)
    try:
        quotes = scroll_and_collect(driver)
    finally:
        driver.quit()                                   # 예외가 나도 브라우저 종료
    df = pd.DataFrame(quotes)
    df.to_csv("data/scraped_quotes.csv", index=False, encoding="utf-8-sig")
    print(df["author"].value_counts().head(5))
```

- **탈출 조건이 두 개**(최대 횟수 + 높이 변화 없음)라서 3-6절·1-4절의 무한 루프 문제가 없다. 스크롤 코드를 짤 때의 모범 답안.
- `execute_script("return ...")`처럼 `return`을 붙이면 JS 값이 파이썬 변수로 돌아온다.
- 태그 목록은 `", ".join(...)`으로 **한 칸에 쉼표로 묶어** 저장했다. 나중에 분석할 때는 `.str.split(', ').explode()`로 다시 펼친다 🔗 4-9절.

---

## 3-8. 구조화된 크롤러 — 음식점 평점 크롤러 전체 흐름

`[프로젝트]08.Selenium사용_음식점정보`는 지금까지의 요소를 한 클래스로 묶은 **종합 예제**다.

```
main()
 ├─ RestaurantRatingCrawler()          requests.Session + headless Chrome
 ├─ crawl_all_sources("강남 맛집", 20)
 │    ├─ crawl_naver_place   iframe 전환 → li[data-id] → 스크롤 박스 반복 (max_scroll=10)
 │    ├─ crawl_kakao_map     .placelist > .PlaceItem → 다음 페이지 버튼 (최대 5페이지)
 │    └─ crawl_siksinhot     .item_list li → 더보기 버튼(.btn_more) 반복
 │       (각각 extract_xxx_info 로 name/rating/category/address/source dict 생성)
 ├─ remove_duplicates()      정규식으로 이름 정규화 + set        🔗 1-2절 ③
 ├─ analyze_ratings()        df['rating'].apply(extract_rating) → groupby('source').agg(['count','mean'])
 ├─ save_to_csv / save_to_json   utf-8-sig / ensure_ascii=False, indent=2
 └─ finally: close()         driver.quit()
```

**해설**
- 사이트마다 "더 불러오기" 방식이 다르다: **스크롤**(네이버) / **페이지 번호**(카카오, `By.ID, "info\\.search\\.page\\.next"` — id에 점이 있어 CSS에서 `\\.`로 이스케이프) / **더보기 버튼**(식신). 세 패턴을 한 번에 보여 준다.
- 모든 루프에 **상한**(`max_scroll`, `page <= 5`, `collected < max_results`)이 있다.
- 결과를 `source` 열로 구분해 두었기 때문에 마지막에 `groupby('source')`로 사이트별 평균 평점을 비교할 수 있다. 수집 단계에서 "나중에 무엇으로 묶을지"를 미리 넣어 두는 설계.
- ⚠️ 이 클래스의 약점: 맨 `except:`(1-7절), 암묵적 대기로 인한 지연(3-5절 ②). 또 세 사이트의 CSS 클래스명은 수시로 바뀌므로 실행 시점에 개발자 도구로 다시 확인해야 한다.

---

## 3-9. PART 3 자가 점검 질문

1. requests로 충분한데 Selenium을 쓰면 무엇이 손해인가?
2. `webdriver.Chrome()`만으로 드라이버가 준비되는 것은 Selenium 몇 버전부터인가? 그 전에는 무엇을 썼나?
3. `find_element_by_css_selector()`를 Selenium 4에서 쓰면? 대체 코드는?
4. `find_element`와 `find_elements`의 "못 찾았을 때" 차이는?
5. 명시적 대기에서 `EC.presence_of_element_located` 뒤 괄호가 두 겹인 이유는?
6. 음식점 크롤러가 느린 진짜 이유는? → 3-5절 ②
7. 네이버 지도 목록을 찾으려면 먼저 무엇을 해야 하나? 끝나면?
8. 무한 스크롤 수집의 두 가지 탈출 조건은?
9. `driver.page_source`와 `requests.get(url).text`의 차이는?
10. `driver.quit()`을 `finally`에 두는 이유는?
11. headless에서 요소를 못 찾을 때 가장 먼저 해 볼 디버깅은?
