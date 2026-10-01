# PART 7. 저장 — PyMySQL · SQLAlchemy

## 7-1. 두 라이브러리의 역할

### 개념

| | **PyMySQL** | **SQLAlchemy** |
|---|---|---|
| 정체 | Pure Python **MySQL 클라이언트**(드라이버) | Python **SQL Toolkit + ORM** (Mike Bayer, 2005) |
| 방식 | 코드 안에 **SQL문을 직접** 써서 실행 | **Core**: SQL 실행 / **ORM**: SQL 없이 객체를 테이블에 매핑 |
| 매핑 | — | Class ⇔ Table, Object ⇔ Row, 변수 ⇔ Column |
| pandas 연동 | 없음(직접 fetch) | `df.to_sql()`, `pd.read_sql()` |
| 설치 | `pip install pymysql` | `pip install sqlalchemy` (`pip show sqlalchemy`) |

- SQLAlchemy는 특정 클래스를 상속하지 않아도 객체를 매핑할 수 있는 **데이터 매퍼 패턴** ORM으로 유명하다.
- 둘은 경쟁 관계가 아니라 **층이 다르다**. SQLAlchemy가 MySQL에 접속할 때 내부 드라이버로 PyMySQL을 쓴다(`mysql+pymysql://`).
- 실습 DB는 MariaDB/MySQL, 계정·DB 이름은 `python` / `python_db`.

---

## 7-2. PyMySQL — SQL을 직접 실행

### 개념 — MySQL 핸들링 7단계 (PDF 9.1)

| 단계 | 코드 |
|---|---|
| 1. 임포트 | `import pymysql` |
| 2. 연결 | `db = pymysql.connect(host=, port=, user=, password=, database=, charset=)` |
| 3. 커서 | `cursor = db.cursor()` |
| 4. SQL 전송 | `cursor.execute(sql, 파라미터)` |
| 5. 조회 결과 | `fetchone()`(한 행, 튜플) / `fetchall()`(전체, 튜플의 튜플) / `fetchmany(n)` |
| 6. DML 확정 | INSERT/UPDATE/DELETE 후 **`db.commit()`** (실패 시 `db.rollback()`) |
| 7. 닫기 | `db.close()` |

- `cursor.rowcount` — 영향받은 행 수, `cursor.lastrowid` — 마지막 INSERT의 AUTO_INCREMENT 값.
- 연결 인자 별칭: `database`=`db`, `password`=`passwd` (07 노트북에 두 표기가 섞여 있다).

### 코드 위치 — `[프로젝트]07.Pymysql과MariaDB연동`

**① 테이블 생성 + INSERT (CODE 2 · 7)**

```python
sql = """
CREATE TABLE product (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    model_num VARCHAR(10) NOT NULL,
    model_type VARCHAR(10) NOT NULL,
    PRIMARY KEY(id)
);"""
db = pymysql.connect(host='localhost', port=3307, database='python_db',
                     user='python', password='python', charset='utf8')
try:
    with db.cursor() as cursor:            # 블록이 끝나면 커서 자동 닫힘
        cursor.execute(sql)
        db.commit()
        for num in range(10, 20):
            name = 'S20' + str(num)
            ins_sql = 'insert into product (name,model_num,model_type) values (%s, %s, %s)'
            cursor.execute(ins_sql, (name, '7700', 'Phone'))    # 값은 튜플로 따로 넘김
        db.commit()                        # 10건을 한 번에 확정
        print(cursor.lastrowid)
except Exception as exp:
    print(exp)
    db.rollback()                          # 도중에 실패하면 모두 취소
finally:
    db.close()
```

**해설 — `%s` 자리표시자(파라미터 바인딩)**
- SQL 문자열에 값을 **직접 이어 붙이지 않고** `%s` 자리에 두고 값은 `execute`의 두 번째 인자로 넘긴다. 드라이버가 따옴표·이스케이프를 알아서 처리해 **SQL Injection을 막는다**.
- 주석으로 남은 옛 방식 `"... values('" + name + "','7700','Phone')"`은 `name`에 `'`가 들어가면 SQL이 깨지거나 공격에 노출된다.
- ⚠️ 파이썬 문자열 포맷의 `%s`와 달리 따옴표를 **붙이지 않는다**(`'%s'` ✗). 문자열·숫자 모두 `%s`.

**② SELECT · UPDATE · GROUP BY (CODE 8)**

```python
cursor.execute('select * from product where id=3')
result = cursor.fetchone()              # (3, 'S2012', '7700', '핸드폰') — 튜플, result[1] = 'S2012'

upd_sql = "update product set model_type='%s' where name between 'S2010' and 'S2015'" % '핸드폰'
cursor.execute(upd_sql); db.commit()
print(cursor.rowcount)                  # 0 ← 왜?

cursor.execute('select model_type, count(*) from product group by model_type')
for row in cursor.fetchall(): print(row)     # ('Phone', 4) ('핸드폰', 6)
```

⚠️ **`upd_sql`은 파이썬 `%` 포맷**으로 값을 끼워 넣었다. ①에서 배운 파라미터 바인딩이 아니다. `cursor.execute("update product set model_type=%s where name between %s and %s", ('핸드폰', 'S2010', 'S2015'))`로 써야 한다.
- `rowcount`가 **0**인 이유: 이 셀을 이미 한 번 실행해 값이 `'핸드폰'`으로 바뀐 상태라, MySQL은 **실제로 값이 바뀐 행만** 영향받은 행으로 센다(같은 값으로 UPDATE → 0).

**③ DELETE with IN (CODE 9)**

```python
sql = "delete from product where name in (%s,%s)"
cursor.execute(sql, ('S2012', 'S2013'))
con.commit()
print(cursor.rowcount)      # 0 — 이미 지워진 뒤 재실행
```
- 주석은 "`s2014`와 `s2015`를 삭제"라고 해 놓고 코드는 `S2012`, `S2013`을 지운다. 주석과 코드가 어긋나는 것도 흔한 버그 원천이다.

**④ 자주 보는 오류 — CODE 3 출력**

```
OperationalError: (1045, "Access denied for user 'python'@'localhost' (using password: YES)")
```
- 1045 = **계정/비밀번호가 틀렸거나 그 호스트에서의 접속 권한이 없다.** 같은 노트북 안에서도 `port=3306`(CODE 3·8·9)과 `port=3307`(CODE 7)이 섞여 있다. MySQL과 MariaDB를 한 PC에 같이 깔면 하나가 3306, 다른 하나가 3307을 쓰는 경우가 많다. **내 DB가 몇 번 포트인지** 확인하고 한 곳(설정 변수)에 모아 두는 것이 좋다. `04` 노트북도 3306(CODE 21·31)과 3307(CODE 33)이 섞여 있다.

---

## 7-3. SQLAlchemy — DataFrame ↔ 테이블

### 개념

**엔진과 연결 URL**

```
dialect+driver://username:password@host:port/database?charset=utf8mb4
mysql+pymysql://python:python@localhost:3306/python_db?charset=utf8mb4
sqlite:///data.db
```

| 코드 | 역할 |
|---|---|
| `engine = create_engine(URL)` | 접속 정보를 담은 엔진(연결 풀) |
| `conn = engine.connect()` | 연결을 직접 열어 SQL 실행할 때 |
| `conn.execute(text('SELECT ...'))` | SQLAlchemy 2.0 스타일 — SQL 문자열을 `text()`로 감쌈 |
| `df.to_sql(name, con=engine, if_exists=, index=, index_label=, dtype={...})` | **DataFrame → 테이블** |
| `pd.read_sql(sql, engine)` / `pd.read_sql_query(sql, con, params=)` | **쿼리 결과 → DataFrame** |
| `conn.close()` · `engine.dispose()` | 연결 반납 · 엔진(풀) 해제 — `finally`에서 |
| ORM: `declarative_base()`, `Session` | 테이블을 클래스로 정의하고 객체처럼 다루는 방식 (실습 코드엔 없음) |

| `if_exists` | 테이블이 이미 있으면 |
|---|---|
| `'fail'` (기본) | 에러 |
| `'replace'` | **지우고 새로 만듦** |
| `'append'` | 뒤에 **추가** |

`〔정정〕` PDF 9.3은 `create_engine(..., encoding='utf-8')`로 쓰지만 `encoding` 인자는 **SQLAlchemy 2.0에서 제거**됐다(실습 환경 2.0.34 → TypeError). 문자셋은 URL의 `?charset=utf8mb4`로 준다. 실습 코드가 `#, encoding='utf-8')`을 주석 처리해 둔 이유다.

`〔정정〕` `pymysql.install_as_MySQLdb()`는 PyMySQL을 **MySQLdb(mysqlclient)인 척** 등록하는 함수라, URL이 `mysql+mysqldb://`(PDF 방식)일 때 필요하다. 실습처럼 `mysql+pymysql://`로 드라이버를 **직접 지정**하면 없어도 된다(있어도 해는 없다).

- `utf8mb4` — MySQL의 `utf8`은 한 글자 최대 3바이트라 이모지(4바이트)를 못 담는다. 실제 UTF-8 전체를 쓰려면 `utf8mb4`. 🔗 9-1절.

### 코드 위치 — `[프로젝트]04.Melon100차트_데이터파싱_DB저장` CODE 21 · 31 · 33

**① 기본 저장 (CODE 21)**

```python
engine = None; conn = None
try:
    engine = create_engine('mysql+pymysql://python:python@localhost:3306/python_db?charset=utf8mb4')
    conn = engine.connect()
    song_df.to_sql(name='songs', con=engine, if_exists='replace', index=False)
finally:
    if conn is not None:   conn.close()
    if engine is not None: engine.dispose()
```
- `engine = None`을 **try 밖에서 먼저** 선언한 이유: `create_engine`에서 실패하면 `engine` 변수가 아예 만들어지지 않아 `finally`의 `engine.dispose()`가 NameError를 낸다. `None`으로 두고 `is not None` 검사.
- 출력에 `Engine(mysql+pymysql://python:***@localhost...)` — SQLAlchemy가 **비밀번호를 가려서** 출력한다.

**② 타입을 지정해 PK까지 (CODE 22~31)**

```python
table_df = song_df.copy()
table_df.columns = ['title','singer','album','release_date','genre','url','likes','lyric']
table_df.index = np.arange(1, len(table_df) + 1)          # 1..100
table_df.drop('url', axis=1, inplace=True)

table_df.to_sql(name='songs100', con=engine, if_exists='replace',
                index=True, index_label='id',              # DataFrame 인덱스 → 'id' 열
                dtype={'id': sqlalchemy.types.INTEGER(),
                       'title': sqlalchemy.types.VARCHAR(200), 'singer': sqlalchemy.types.VARCHAR(200),
                       'album': sqlalchemy.types.VARCHAR(200), 'release_date': sqlalchemy.types.DATE,
                       'genre': sqlalchemy.types.VARCHAR(200), 'likes': sqlalchemy.types.BigInteger,
                       'lyric': sqlalchemy.types.VARCHAR(5000)})
```

**해설** (MD 22의 세 가지 목적)
1. **영문 컬럼명** — 한글 컬럼명은 SQL에서 매번 백틱으로 감싸야 하고 도구 호환성이 떨어진다.
2. **인덱스를 1부터 → PK** — `index=True, index_label='id'`로 DataFrame 인덱스가 `id` 열이 된다. (`to_sql`은 PRIMARY KEY 제약까지 만들어 주지는 않으므로 진짜 PK가 필요하면 `ALTER TABLE songs100 ADD PRIMARY KEY (id)`를 따로 실행한다.)
3. **타입 지정** — `dtype`을 안 주면 문자열은 `TEXT`, 날짜 문자열도 `TEXT`로 만들어진다. `release_date`를 `DATE`로 지정하면 날짜 조건 검색·정렬이 된다 🔗 5-2절 ③. 가사는 길어서 `VARCHAR(5000)`.

**③ 조회 함수 + 파라미터 바인딩 (CODE 33 · 34)**

```python
def search_album(keyword):
    sql = """select * from songs100 where album like %s;"""
    engine = None; conn = None
    try:
        engine = create_engine('mysql+pymysql://python:python@localhost:3307/python_db?charset=utf8mb4')
        conn = engine.connect()
        album_df = pd.read_sql_query(sql, con=conn, params=('%' + keyword + '%',))
        return album_df
    finally:
        print('finally')                      # return 이 있어도 finally 는 실행된다
        if conn is not None: conn.close()
        if engine is not None: engine.dispose()

search_album('OST')       # (6, 8) — 소나기, 사랑은 늘 도망가, 너의 모든 순간 ...
```

**해설**
- `LIKE`의 `%` 와일드카드는 **값 쪽에** 붙인다(`'%OST%'`). SQL 문자열에 `'%" + keyword + "%'`로 이어 붙이면 Injection 위험 🔗 7-2절.
- `params=('%OST%',)` — **요소 1개짜리 튜플은 콤마 필수** 🔗 1-2절.
- 출력 순서가 `(6, 8)` → `finally` → 표: `return` 값이 셀 결과로 표시되기 전에 `finally`가 먼저 실행된다.

---

## 7-4. PART 7 자가 점검 질문

1. PyMySQL과 SQLAlchemy는 어떤 관계인가? `mysql+pymysql://`에서 각 부분의 의미는?
2. PyMySQL 7단계를 순서대로 말하라. `commit()`이 필요한 문장은?
3. `%s` 파라미터 바인딩이 문자열 이어 붙이기보다 안전한 이유는?
4. `rowcount`가 0으로 나오는 두 가지 경우는?
5. `1045 Access denied` 오류를 보면 무엇을 확인하나?
6. `to_sql`의 `if_exists` 세 값의 차이는?
7. `index=True, index_label='id'`는 무슨 일을 하나? 그것만으로 PK 제약이 생기나?
8. `dtype`을 안 주면 날짜 문자열 열은 무슨 타입이 되나?
9. `create_engine(..., encoding='utf-8')`이 SQLAlchemy 2.0에서 실패하는 이유와 대안은?
10. `engine = None`을 try 밖에서 먼저 쓰는 이유는?
11. `LIKE` 검색에서 `%`를 어디에 붙이나? `params=('%OST%')`처럼 콤마를 빼면?
