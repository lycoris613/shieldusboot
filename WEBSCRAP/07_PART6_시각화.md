# PART 6. 시각화 — matplotlib · seaborn · WordCloud

## 6-1. matplotlib 구조와 환경 설정

### 개념

| 용어 | 뜻 |
|---|---|
| **Figure** | 그림 전체를 담는 **도화지** |
| **Axes** | 실제 그래프가 그려지는 **영역**(도화지를 나눈 칸 하나) |
| Axis | x축·y축 |

- 먼저 Figure를 만들고 `plt.subplots`로 나눠 각 Axes에 그린다.
- **Figure 크기 지정 3가지**: `plt.subplots(figsize=(10,5))` / `figure.set_size_inches(18, 12)` / `plt.rcParams['figure.figsize'] = (10, 7)`(전역).
- Jupyter 표시 옵션: `%matplotlib inline`(그림 고정, 기본) / `%matplotlib nbagg`(인터랙티브). inline이면 `plt.show()`를 안 불러도 셀 끝에서 그려지지만, 쓰는 것을 권장한다.

**두 가지 그리기 방식**

| | pyplot 방식 `plt.xxx()` | 객체 방식 `ax.xxx()` (권장) |
|---|---|---|
| 대상 | "현재" Figure/Axes | 내가 지정한 Axes |
| 제목·축 | `plt.title()` `plt.xlabel()` | `ax.set_title()` `ax.set_xlabel()` |
| 여러 칸 | 헷갈림 | `axes[0]`, `axes[1, 0]`로 명확 |

```python
fig, ax = plt.subplots(figsize=(6, 4))                  # 1칸
fig, (axes1, axes2) = plt.subplots(nrows=2, ncols=1)    # 2칸 — 튜플로 바로 풀기
fig, axes = plt.subplots(2, 2, figsize=(11, 8))         # 2×2 — axes[1, 0]
```

## 6-2. 한글 폰트 — 세 가지 방법

설정하지 않으면 한글이 □로 깨진다. 마이너스 기호가 깨지는 것도 함께 막는다.

```python
# ① rcParams (가장 짧음) — [기초]06, [프로젝트]10·11
plt.rcParams['font.family'] = 'Malgun Gothic'        # macOS: 'AppleGothic'
plt.rcParams['axes.unicode_minus'] = False

# ② rc 함수 — [프로젝트]03, [스트림릿기초]06, [스트림릿예제]04~07
matplotlib.rc('font', family='Malgun Gothic')        # rc = run command
plt.rc('font', family="Malgun Gothic")               # 같은 것

# ③ 폰트 파일에서 이름 알아내기 — [프로젝트]03 CODE 86~88, 05_02, 09
import matplotlib.font_manager as fm
[(f.name, f.fname) for f in fm.fontManager.ttflist if 'Mal' in f.name]   # 설치된 폰트 검색
font_name = fm.FontProperties(fname='C:\\Windows\\Fonts\\malgun.ttf').get_name()   # 'Malgun Gothic'
matplotlib.rc('font', family=font_name)
```
- 폰트 **이름**을 알면 ①②로 충분하다. ③은 이름을 모르는 폰트 파일을 쓸 때.
- seaborn은 테마를 바꾸면 폰트 설정이 초기화될 수 있어 `sns.set_theme(style="whitegrid", font="Malgun Gothic")`처럼 테마에 폰트를 같이 준다.

---

## 6-3. 그래프 종류와 옵션

| 종류 | 함수 | 언제 | 주요 옵션 | 실습 |
|---|---|---|---|---|
| 선 | `ax.plot(x, y)` | 시간에 따른 **추세** | `marker='o'`, `color`, `label` + `ax.legend()` | 연도별 추가 수, 연도별 데뷔 |
| 막대 | `ax.bar` / `ax.barh` | 항목 **비교** | `barh` + `invert_yaxis()`(큰 값을 위로) | 소속사별 인원 |
| 산점도 | `ax.scatter(x, y)` | 두 수치의 **관계** | `alpha`(투명도), `s`(크기) | 키 vs 몸무게 |
| 히스토그램 | `ax.hist(data, bins=20)` | **분포** | `bins`(구간 수), `edgecolor` + `ax.axvline(평균)` | 데뷔 나이 |
| 파이 | `ax.pie(sizes, labels=)` | **비율** | 아래 표 | Movie/TV, 선거구 |

**파이 차트 옵션** (`[프로젝트]09` CODE 63, `05_02` CODE 34)

| 옵션 | 뜻 |
|---|---|
| `autopct='%1.1f%%'` / `'%0.f%%'` | 조각 안 숫자 형식(`%%`가 진짜 %) |
| `startangle=90` | 시작 각도(90 = 12시) |
| `explode=[0.08, 0.08]` | 조각을 중심에서 띄우기 |
| `shadow=True` · `colors=[...]` · `textprops={'color':'white', 'fontsize':12}` | |
| `plt.axis('equal')` | 원이 찌그러지지 않게 |

- `plt.suptitle()` = **Figure 전체** 제목, `plt.title()`/`ax.set_title()` = **Axes** 제목(09 CODE 63은 둘을 같이 써서 큰 제목 + 부제).

---

## 6-4. 꾸미기 — 눈금 · 값 라벨 · 회전 · 저장

### 코드 위치 — `[프로젝트]03` CODE 100 (서울 구별 인구 막대)

```python
figure, ax1 = plt.subplots(nrows=1, ncols=1)
figure.set_size_inches(18, 12)
sns.barplot(data=seoul_df, x="행정구역", y="인구수", ax=ax1, hue='행정구역')

# y축 눈금에 천 단위 콤마 — FuncFormatter 는 (값, 위치) 두 인자 함수를 받는다
ax1.get_yaxis().set_major_formatter(plt.FuncFormatter(lambda x, loc: "{:,}".format(int(x))))

for item in ax1.get_xticklabels():           # x축 라벨 90도 회전 (구 이름이 겹치지 않게)
    item.set_rotation(90)

for i, v in enumerate(seoul_df["인구수"].items()):          # 막대 위에 값 쓰기
    ax1.text(i, v[1], "{:,}".format(v[1]), color='m', va='bottom', rotation=45)
plt.tight_layout()
```

**해설**
- `Series.items()`는 `(인덱스, 값)` 쌍을 준다(딕셔너리 `.items()`와 같은 이름). 거기에 `enumerate`를 씌워 `i`(0부터의 막대 위치), `v[1]`(값)을 얻는다. 막대의 x 위치는 0,1,2…라서 `ax.text(i, 높이, 글자)`로 막대 위에 숫자를 올린다.
- `va='bottom'` — 글자 아래쪽을 그 좌표에 맞춤(막대 **위에** 붙게).

| 기능 | 코드 |
|---|---|
| 회전(간단판) | `plt.xticks(rotation=45, ha='right')` (`[프로젝트]09`) |
| 축 범위 | `ax.set_xlim([50, 100])` (`[판다스]groupby` — 백인 비율 50~100%만) |
| 격자 | `ax.grid(axis='y', alpha=0.3)` |
| 간격 정리 | `plt.tight_layout()` |
| 저장 | `fig.savefig('a.png', dpi=100, bbox_inches='tight')` → `plt.close(fig)` / `plt.savefig('img/netflix_genre_distribution.png', dpi=300)` |

⚠️ `[프로젝트]03` CODE 90~97 출력에 `NameError: name 'seoul_df' is not defined`, `name 'plt' is not defined`가 찍혀 있다. **노트북은 셀 실행 순서에 의존**한다. 커널을 재시작한 뒤 위 셀(`seoul_df` 정의, import)을 건너뛰고 아래 셀부터 실행하면 이렇게 된다. 제출·공유 전에는 **Restart & Run All**로 위에서부터 한 번에 돌려 확인한다.

---

## 6-5. seaborn

### 개념

- matplotlib 기반. 색 테마·통계 그래프를 더한 라이브러리. matplotlib에 **의존**한다.
- **DataFrame과 열 이름**만 주면 된다: `sns.barplot(data=df, x='열', y='열')`.
- matplotlib에 없는 그래프(히트맵, 카운트플롯 등)를 제공.

**Axes 레벨 vs Figure 레벨**

| | Axes 레벨 | Figure 레벨 |
|---|---|---|
| 함수 | `barplot` `countplot` `histplot` `scatterplot` `lineplot` `boxplot` `heatmap` | `catplot` `displot` (`relplot`) |
| 그릴 곳 | `ax=`로 내가 만든 칸에 | **스스로 Figure를 만든다** (`ax=` 없음) |
| 크기 | `figsize` | `height=`, `aspect=` |
| 칸 나누기 | `plt.subplots` | `col=`/`row=`로 **조건별 자동 분할** |

| 함수 | x·y | 설명 |
|---|---|---|
| `barplot` | **둘 다** | 범주별 수치(기본은 평균) |
| `countplot` | **하나만** | 범주별 **개수**를 자동으로 셈 |
| `histplot` | 하나 | 구간별 개수(분포). `hue`별 `multiple='dodge'` 또는 `'stack'`, `shrink`, `kde=True` |
| `scatterplot` | 둘 | `hue`로 집단 색 구분 |
| `heatmap` | 표 | `annot=True`(숫자 표시), `cmap`, `cbar_kws` — `df.corr()`·pivot 결과 |
| `catplot(kind='count')` | | 다중 빈도 막대 |
| `displot(col=, kde=)` | | 조건별 분포를 칸 나눠서 |
| `palplot([색...])` | | 색 팔레트 미리보기 |
| `set_theme(style=, font=)` | | 전체 테마 |

**공통 옵션**: `hue='열'`(색으로 집단 구분), `palette='viridis'|'Blues_r'|{'남':'navy','여':'orange'}`, `order=목록`(막대 순서), `legend=False`.

⚠️ **seaborn 0.13 FutureWarning** — `palette`만 주고 `hue`를 안 주면 경고(`[프로젝트]10·11` 출력). `hue`에 x나 y와 같은 열을 주고 `legend=False`로 끈다.

```python
sns.barplot(x=top.values, y=top.index, palette="Reds_r")                            # ⚠️ 경고
sns.barplot(x=top.values, y=top.index, hue=top.index, palette="Reds_r", legend=False)   # ✅
```

### 코드 위치

**① 두 칸에 barplot + 순서 지정 — `[프로젝트]03` CODE 92~96**

```python
pop_order  = seoul_df.sort_values('인구수', ascending=False)['행정구역']
area_order = seoul_df.sort_values('면적', ascending=False)['행정구역']
figure, (axes1, axes2) = plt.subplots(nrows=2, ncols=1)
sns.barplot(data=seoul_df, x="행정구역", y="인구수", ax=axes1, hue="행정구역", order=pop_order)
sns.barplot(data=seoul_df, x="행정구역", y="면적",  ax=axes2, hue="행정구역", order=area_order)

def show_pop_area(sido_name): ...          # CODE 95 — 시도 이름을 받아 같은 그림 → [스트림릿예제]06
```
- `order=`를 주거나, **정렬된 DataFrame**을 `data=`로 넘기면 막대가 그 순서로 그려진다(두 방식 모두 나옴).

**② countplot 3칸 + 빈도순 — `[프로젝트]05_02` CODE 29~30**

```python
figure, (axes1, axes2, axes3) = plt.subplots(nrows=3, ncols=1)
sns.countplot(data=member_df, x='정당',   ax=axes1, order=member_df['정당'].value_counts().index, hue='정당')
sns.countplot(data=member_df, x='당선횟수', ax=axes2, order=member_df['당선횟수'].value_counts().index, hue='당선횟수')
sns.countplot(data=member_df, y='성별',   ax=axes3, order=member_df['성별'].value_counts().index, hue='성별')
```
- `value_counts().index` = 빈도순 목록 → `order`에 넣으면 많은 것부터.
- `y=`로 주면 가로 막대.

**③ histplot · catplot · displot — `[프로젝트]05_02` CODE 36 · 41 · 43**

```python
sns.histplot(data=member_df, x='정당', ax=axes1, hue='성별', multiple='dodge',
             palette={'남': 'blue', '여': 'red'}, shrink=0.8)            # 정당별 남녀 나란히
sns.catplot(data=member_df, y='선거구2', hue='성별', kind='count',
            palette={'남': 'navy', '여': 'orange'}, order=s_index, height=8)
sns.displot(data=member_df, x='당선횟수', hue='성별', col='성별',
            palette={'남': 'blue', '여': 'orange'}, kde=True, height=5, aspect=1.5)
```
- `_ = sns.catplot(...)` — Figure 레벨 함수는 `FacetGrid` 객체를 반환해서 셀 끝에 텍스트가 찍힌다. `_`에 받아 버리는 관용구.
- `kde=True` — 막대 위에 부드러운 분포 곡선(커널 밀도 추정).

**④ scatterplot · histplot(kde) — `[프로젝트]11` CODE 8 · 11**

```python
sns.histplot(df.loc[valid_age, "debut_age"], bins=20, kde=True, color="#FF4081", ax=ax)
ax.axvline(mean_age, color="red", linestyle="--", label=f"평균 {mean_age:.1f}세")
sns.scatterplot(data=hw, x="height", y="weight", hue="gender",
                palette={"M": "#2196F3", "F": "#FF4081"}, alpha=0.6, ax=ax)
```

**⑤ 장르 막대 저장 — `[프로젝트]09` CODE 85**

```python
sns.barplot(data=genre_counts, x='genre', y='count', hue='genre', palette='viridis', legend=False)
plt.xticks(rotation=45, ha='right'); plt.tight_layout()
plt.savefig('img/netflix_genre_distribution.png', dpi=300)
```

**⑥ 브랜드 팔레트 — `[프로젝트]09` CODE 54**

```python
sns.palplot(['#221f1f', '#b20710', '#e50914', '#f5f5f1'])     # 넷플릭스 색 확인
plt.title('Netflix brand palette', loc='center', fontfamily='serif', fontsize=15, y=1.2)
```

---

## 6-6. WordCloud

### 개념

- 텍스트에서 단어 **빈도**를 세어 **많이 나올수록 크고 진하게** 그리는 시각화. `pip install wordcloud`.
- **Pillow(PIL)** 로 이미지를 열고 numpy 배열로 바꿔 **마스크**(글자를 채울 모양 틀)로 쓴다. 흰색(#FFFFFF) 영역은 비워 두는 부분으로 간주된다.

### 코드 위치 — `[프로젝트]09` CODE 68~76

```python
from wordcloud import WordCloud
from PIL import Image

text = str(list(netflix['description']))        # 열 → list → 하나의 큰 문자열(약 130만 글자)
mask = np.array(Image.open('data/netflix_logo.jpg'))
cmap = plt.matplotlib.colors.LinearSegmentedColormap.from_list('', ['#221f1f', '#b20710'])   # 두 색 사이 그라데이션

wordcloud = WordCloud(background_color='white', width=1400, height=1400,
                      max_words=170, mask=mask, colormap=cmap).generate(text)
plt.imshow(wordcloud)       # 배열(이미지)을 그림으로
plt.axis('off')             # 축 숨기기
plt.show()
```
- `str(list(...))`는 `"['문장1', '문장2', ...]"` 모양의 문자열이다(CODE 75에서 2개로 줄여 확인). 따옴표·쉼표가 섞이지만 WordCloud가 단어만 세므로 문제없다. 깔끔하게는 `' '.join(netflix['description'])`.
- 기본 불용어(the, a, of …)는 WordCloud가 알아서 빼 준다. **한글** 텍스트는 한글 폰트 경로(`font_path=`)를 주지 않으면 □로 나온다.

---

## 6-7. 분석 결과를 해석할 때 — 실습 노트북의 인사이트 주의점

`[프로젝트]11`의 💡 인사이트 셀들은 **숫자를 말로 옮길 때의 함정**을 정리해 두었다.

| 상황 | 함정 | 실습 근거 |
|---|---|---|
| 결측이 많은 열 | 순위·평균은 **기록된 사람들 안에서**의 결과다. 모집단 일반화 주의 | 출생지 결측 53.1%, 몸무게 68.2% → 키·몸무게 분석은 556명만 |
| 특수 집단 | 일반 인구가 아니다 | 아이돌 체형(평균 164.4cm/46.3kg) |
| 마지막 기간 급감 | 인기 하락이 아니라 **수집 시점이 그 해 중간**(우측 절단, right-censored) | 2023년 데뷔 40명 |
| 평균 ≈ 중앙값 | 좌우 대칭에 가까운 분포 | 데뷔 나이 19.6 / 19.4 |
| 상관 | 강한 양의 상관(0.89)도 인과는 아니다 | 키-몸무게 |
| 롱테일 | 상위 몇 개가 커 보여도 전체의 일부 | 대형 기획사 5곳 vs 1,778명 |
| 비정상 값 | 계산한 변수는 범위부터 확인 | 데뷔 나이 -2.8세 → 10~40세만 사용 |

---

## 6-8. PART 6 자가 점검 질문

1. Figure와 Axes의 차이는? `plt.title()`과 `ax.set_title()`은?
2. 한글 폰트 설정 코드 두 줄과 `axes.unicode_minus`의 의미는?
3. 선·막대·산점도·히스토그램·파이는 각각 무엇을 보여 줄 때 쓰나?
4. `autopct='%1.1f%%'`에서 `%%`의 의미는?
5. `FuncFormatter(lambda x, loc: ...)`의 두 인자는?
6. `barplot`과 `countplot`의 x/y 지정 차이는?
7. `catplot`·`displot`에 `ax=`를 줄 수 없는 이유는? 대신 무엇으로 칸을 나누나?
8. seaborn 0.13에서 `palette`만 줬을 때 나오는 경고를 없애는 법은?
9. 막대 순서를 빈도순으로 하는 두 가지 방법은?
10. 워드클라우드에서 마스크 이미지는 어떻게 만들고, 흰색 영역은 어떻게 처리되나?
11. 노트북에서 `NameError: seoul_df`가 났는데 코드는 멀쩡하다. 원인과 예방법은?
12. 2023년 데뷔 인원 급감을 "인기 하락"으로 해석하면 안 되는 이유는?
