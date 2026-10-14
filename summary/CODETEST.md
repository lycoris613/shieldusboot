# 코딩테스트 본책 — 1단계 방어선

> **적용 대상**: 삼성 SW역량테스트 A형 / SK하이닉스 / 네이버 / 카카오 1차
> **검증 근거**: 카카오 2018~2026 기출, 삼성 A형 기출, 네이버·SK하이닉스 후기 교차 대조
> **핵심 결론**: 이 문서의 1단계 유형만 확실히 하면 삼성·SK하이닉스·네이버는 합격권,
> 카카오도 1차 컷(3.5~4솔)까지 커버된다. 2단계(DP·누적합·투포인터·다익스트라·유니온파인드)는
> 카카오 5~6번용 보너스이고, 3단계(비트마스킹DP·구간DP·세그먼트트리·KMP)는 공부 안 해도 무방.

---

# PART 1 — 문제 접근 전략

## 1.1 유형 판별 흐름도 (위에서부터 순서대로 체크)

**1단계: 입력 형태부터 본다**

- 그래프/격자/네트워크 정보(인접행렬, 간선 리스트, 격자)가 주어졌는가?
  - → **DFS/BFS 확정**
  - 간선 리스트로 주어짐 → 먼저 인접리스트로 변환 (1.3 참고)
  - "최단거리/최소 횟수" 필요 → **BFS**
  - 단순 "연결 여부/개수 세기" → DFS/BFS 아무거나 (결과 동일)

**2단계: "모든 경우의 수"를 다 봐야 하는가?**

- 순열/조합/부분집합처럼 선택을 나열 → **완전탐색**
  - 중간에 조건 깨지면 가지치기 필요 → 백트래킹 (또는 visited 배열 직접 구현)
  - 그냥 다 나열해서 확인 → itertools로 충분

**3단계: 답이 "값 하나"인가?**

- "정렬된 데이터에서 특정 값" 또는 "조건 만족하는 최대/최소값 자체" → **이분탐색**

**4단계: 이전 선택이 이후 선택에 영향을 주는가?**

- No, 매 순간 최선의 선택이 전체 최적해로 이어짐 → **그리디**
- Yes (부분 문제가 겹침) → DP 영역 (2단계, 본책 범위 밖)

**5단계: 위 어디에도 안 걸리면**

- "존재 여부/개수/빈도" 빠르게 확인 → **해시**
- "순서/크기 비교"가 핵심이거나 다른 알고리즘의 사전작업 → **정렬**
- "우선순위대로 꺼내기/K번째 값" → **힙**
- "괄호/최근 것부터(LIFO)" → **스택** / "순서대로(FIFO)" → **큐**

## 1.2 시간복잡도로 최종 확정

**핵심 기준: 연산 횟수가 10^8을 넘으면 위험 (1초 제한 기준).**

예시 (N = 100,000 기준):

- 완전탐색 O(N²) → 10^10 → **불가능**
- 정렬/투포인터 O(N log N) → 약 10^6 → 가능
- 해시/BFS-DFS O(N) → 10^5 → 가능

| N의 크기 | 허용 시간복잡도 | 대응 알고리즘 |
|---|---|---|
| N ≤ 11 | O(N!) | 완전탐색(순열) |
| N ≤ 20 | O(2^N) | 부분집합 완전탐색 |
| N ≤ 500 | O(N³) | 3중 for문 |
| N ≤ 2,000 | O(N²) | 2중 for문 |
| N ≤ 100,000 | O(N log N) | 정렬, 힙, 이분탐색 |
| N ≤ 10,000,000 | O(N) | 1중 for문 |

후보가 2개 남았을 때(예: 완전탐색 vs 다른 방법), N 크기로 역산해서 되는 쪽 선택.

## 1.3 인접리스트 변환 패턴 (간선 리스트 입력 시 필수)

시험장에서 새로 생각하지 말고 통째로 암기할 것.

```python
from collections import defaultdict

# 노드 번호가 순차적 정수일 때
graph = [[] for _ in range(n+1)]
for a, b in edges:
    graph[a].append(b)
    graph[b].append(a)   # 양방향이면 역방향도 추가

# 노드 번호가 문자열이거나 순차적이지 않을 때
graph = defaultdict(list)
for a, b in edges:
    graph[a].append(b)
    graph[b].append(a)
```

## 1.4 실전 시험 전략

- **"-1 목표"**: 만점 노리지 말고 마지막 1문제는 버려도 되는 구조로 접근. 카카오는 3~4솔이면 1차 통과권.
- **레벨4는 시간 상한(10~15분)** 정해두고 판단 안 서면 즉시 포기, 남은 시간을 앞 문제 검증에 투자.
- **시간초과 예상돼도 일단 정확성 위주로 시도** — 효율성 테스트는 틀려도 정확성 점수는 별도로 확보되는 구조가 많음.
- **코드 가독성·주석 신경쓰기** — 카카오 2차와 SK하이닉스는 코테 코드가 면접 코드리뷰 자료로 쓰임.
- **문제 순서는 쉬운 것부터 배치됨** (카카오·네이버 공통) — 앞 문제부터 확실히 잡기.

## 1.5 학습 전략

- **30분 룰**: 30분 이상 고민해도 안 풀리면 바로 해설 보고 패턴 이해 후 암기.
- **오판 원인 한 줄만 기록**: "그리디인 줄 알았는데 DP였음, 이유: 부분 선택이 이후 선택에 영향" 수준으로 충분.
- **판단 먼저, 코딩 나중**: 문제 읽고 바로 코드 치지 말고 "이건 X 패턴, 왜냐하면 Y" 판단 근거를 먼저 정리.
- **한 문제를 변형해가며 반복**: 물량도 중요하지만 걸린 문제는 변형해서 완전히 체화.

---

# PART 2 — 1단계 핵심 유형

## 2.1 해시 (출제 빈도: 매우 높음)

카카오 1~2번 단골. `Counter`, `dict`, `set` 셋 다 손에 익혀둘 것.

### 카운팅 패턴
```python
from collections import Counter

cnt = Counter(arr)
cnt.most_common()          # 빈도 내림차순 [(값, 개수), ...]
cnt.most_common(3)         # 상위 3개

# Counter 없이
cnt = {}
for x in arr:
    cnt[x] = cnt.get(x, 0) + 1
```

### Two Sum 패턴 — 해시로 O(N) 해결
```python
def two_sum(arr, target):
    seen = {}
    for i, x in enumerate(arr):
        if target - x in seen:
            return [seen[target-x], i]
        seen[x] = i
```

### 아나그램 / 그룹 매칭
```python
from collections import Counter, defaultdict

def is_anagram(s1, s2):
    return Counter(s1) == Counter(s2)

# 키 기준으로 묶기
groups = defaultdict(list)
for word in words:
    key = ''.join(sorted(word))
    groups[key].append(word)
```

### set으로 존재 확인 (O(1))
```python
seen = set()
seen.add(x)
if x in seen:      # 리스트 in 검사는 O(N), set은 O(1)
    pass
```

---

## 2.2 정렬 (출제 빈도: 높음 — 주로 다른 유형의 사전작업)

단독 출제보다 구현·그리디의 서브스텝으로 자주 등장.

### 다중 조건 정렬
```python
arr.sort(key=lambda x: (x[0], -x[1]))    # 1번 오름차순, 2번 내림차순
arr.sort(key=lambda x: (-x[1], x[0]))    # 우선순위 바꾸기

# 딕셔너리 값 기준 정렬
sorted(d.items(), key=lambda x: x[1], reverse=True)
```

### 커스텀 비교 ("가장 큰 수" 류)
```python
from functools import cmp_to_key

def compare(a, b):
    if a + b > b + a:
        return -1      # a가 앞
    else:
        return 1

arr.sort(key=cmp_to_key(compare))
```

### 정렬 활용 감각

- 그리디 → 거의 항상 정렬이 먼저
- 이분탐색 → 정렬이 전제조건
- 문자열 정렬해서 키로 쓰기 → 해시와 조합

---

## 2.3 스택 / 큐 (출제 빈도: 보통)

### 괄호 매칭
```python
def valid_parentheses(s):
    stack = []
    pairs = {')': '(', '}': '{', ']': '['}
    for ch in s:
        if ch in '({[':
            stack.append(ch)
        else:
            if not stack or stack.pop() != pairs[ch]:
                return False
    return not stack
```

### 큐 시뮬레이션 (다리를 지나는 트럭, 프로세스류)
```python
from collections import deque

q = deque(range(n))
while q:
    x = q.popleft()
    # 조건에 따라 다시 append 하거나 결과에 저장
    if 조건:
        q.append(x)
```

> **주의**: 리스트의 `pop(0)`은 O(N)이라 느림. 큐로 쓸 거면 반드시 `deque`.

---

## 2.4 힙 / 우선순위 큐 (출제 빈도: 보통)

### 최소힙 / 최대힙
```python
import heapq

heap = []
heapq.heappush(heap, 5)
heapq.heappop(heap)        # 최소값 반환하며 제거
heap[0]                    # 최소값 확인만 (제거 X)

# 최대힙 — 부호 반전 트릭
heapq.heappush(heap, -5)
-heapq.heappop(heap)

# 리스트를 힙으로
heapq.heapify(arr)
```

### 튜플로 우선순위 지정
```python
heap = []
for task in tasks:
    heapq.heappush(heap, (priority, task))    # 첫 원소 기준 정렬
while heap:
    priority, task = heapq.heappop(heap)
```

### K번째 값
```python
heapq.nsmallest(k, arr)[-1]
heapq.nlargest(k, arr)[-1]
```

---

## 2.5 구현 / 시뮬레이션 (출제 빈도: 최상 — 4사 전부 매년)

삼성 A형의 거의 전부이고, 카카오 1~2번·네이버 1번도 여기. **패턴 암기보다 물량으로 감 잡는 영역.**

### 방향벡터 — 4방향
```python
dx = [-1, 1, 0, 0]    # 상, 하
dy = [0, 0, -1, 1]    # 좌, 우

for i in range(n):
    for j in range(m):
        for d in range(4):
            nx, ny = i + dx[d], j + dy[d]
            if 0 <= nx < n and 0 <= ny < m:
                pass
```

### 방향벡터 — 8방향 (대각선 포함)
```python
dx = [-1, -1, -1, 0, 0, 1, 1, 1]
dy = [-1, 0, 1, -1, 1, -1, 0, 1]
```

### 2차원 배열 회전 (시계방향 90도)
```python
def rotate(board):
    n, m = len(board), len(board[0])
    new_board = [[0] * n for _ in range(m)]
    for i in range(n):
        for j in range(m):
            new_board[j][n - 1 - i] = board[i][j]
    return new_board

# 파이썬 한 줄 버전
rotated = [list(row) for row in zip(*board[::-1])]
```

### 격자 시뮬레이션 기본 틀
```python
for step in range(total_steps):
    new_grid = [[0] * m for _ in range(n)]   # 동시 갱신 필요시 새 배열
    for i in range(n):
        for j in range(m):
            # 규칙에 따라 new_grid[i][j] 계산
            pass
    grid = new_grid
```

### 구현 함정 케이스 (실수 빈발 지점)

1. **동시 갱신 vs 순차 갱신**
   - 모든 칸이 "동시에" 바뀌는 규칙이면 원본을 직접 수정하면 안 됨 → 새 배열에 기록 후 교체
2. **얕은 복사 함정**
   - `[[0]*m]*n` ❌ (모든 행이 같은 리스트 참조)
   - `[[0]*m for _ in range(n)]` ✅
   - `arr2 = arr1` ❌ / `arr2 = arr1[:]` 또는 `copy.deepcopy(arr1)` ✅
3. **인덱스 범위 체크 누락** — 경계 조건 `0 <= nx < n and 0 <= ny < m` 항상 먼저
4. **순회 중 삭제** — 리스트 순회하면서 `pop`/`remove` 하면 인덱스 밀림 → 역순회하거나 새 리스트 생성
5. **인덱스 간접참조로 그룹 관리**
   - "여러 셀이 하나로 병합되는" 류(카카오 표병합) → 실제 값 배열과 "이 칸이 가리키는 대표 칸" 배열을 분리
   ```python
   # merge[r][c] = 이 셀이 실제로 가리키는 대표 셀 좌표
   # content[r][c] = 대표 셀의 실제 값
   # 값 접근은 항상 merge를 거쳐서
   ```
6. **1-indexed vs 0-indexed** — 문제가 1번부터 세면 배열도 `n+1` 크기로 잡고 0번 버리기

---

## 2.6 완전탐색 (출제 빈도: 최상 — 카카오 3번 고정)

### itertools 활용
```python
from itertools import permutations, combinations, product, combinations_with_replacement

for p in permutations(arr, r):                     # 순서 O, 중복 X
    pass
for c in combinations(arr, r):                     # 순서 X, 중복 X
    pass
for p in product(arr, repeat=r):                   # 순서 O, 중복 O
    pass
for c in combinations_with_replacement(arr, r):    # 순서 X, 중복 O
    pass
```

### 부분집합 완전탐색 (비트마스킹, N ≤ 20)
```python
for state in range(1 << n):        # 0 ~ 2^n - 1
    subset = [arr[i] for i in range(n) if state & (1 << i)]
    # subset 처리
```

### 백트래킹 기본 틀
```python
def backtrack(path):
    if 종료조건(path):
        result.append(path[:])        # 얕은 복사 주의! 꼭 [:] 또는 list(path)
        return
    for choice in choices:
        if 유효하지_않으면(choice, path):
            continue                   # 가지치기
        path.append(choice)
        backtrack(path)
        path.pop()                     # 되돌리기 (핵심)
```

### 순열 직접 구현 (중간 가지치기 필요할 때)
`permutations()`는 완성된 순열만 주므로, **만드는 도중 조건이 깨지면 즉시 포기**해야 하는 경우엔 직접 구현 필수.

```python
def permute(arr, path, visited):
    if len(path) == len(arr):
        result.append(path[:])
        return
    for i in range(len(arr)):
        if visited[i]:
            continue
        if 중간_조건_위반(path, arr[i]):    # 여기서 가지치기
            continue
        visited[i] = True
        path.append(arr[i])
        permute(arr, path, visited)
        path.pop()
        visited[i] = False
```

### N-Queen류 — 대각선 충돌 판별
백트래킹 틀만 알아선 안 되고, **대각선 판별 로직을 따로 알아야 함**.

```python
def solve(row, cols, diag1, diag2, n):
    if row == n:
        return 1
    count = 0
    for col in range(n):
        # 핵심: 같은 대각선 판별
        #   ↘ 방향: row - col 이 같으면 같은 대각선
        #   ↙ 방향: row + col 이 같으면 같은 대각선
        if col in cols or (row - col) in diag1 or (row + col) in diag2:
            continue
        cols.add(col); diag1.add(row - col); diag2.add(row + col)
        count += solve(row + 1, cols, diag1, diag2, n)
        cols.remove(col); diag1.remove(row - col); diag2.remove(row + col)
    return count
```

---

## 2.7 DFS / BFS (출제 빈도: 최상 — 평균 점수 낮음 = 가장 중요)

### DFS — 재귀 방식
```python
import sys
sys.setrecursionlimit(10**6)

def dfs(graph, node, visited):
    visited[node] = True
    for next_node in graph[node]:
        if not visited[next_node]:
            dfs(graph, next_node, visited)
```

### DFS — 스택 방식 (그래프가 극단적으로 깊을 때)
`setrecursionlimit`을 늘려도 시스템 스택 한계로 강제 종료될 수 있음.
일렬로 길게 연결된 그래프(N=10만 이상)라면 스택 방식이 안전.

```python
def dfs_iter(graph, start, visited):
    stack = [start]
    while stack:
        node = stack.pop()
        if visited[node]:
            continue
        visited[node] = True
        for next_node in graph[node]:
            if not visited[next_node]:
                stack.append(next_node)
```

### BFS — deque 필수
```python
from collections import deque

def bfs(graph, start, visited):
    visited[start] = True
    q = deque([start])
    while q:
        node = q.popleft()
        for next_node in graph[node]:
            if not visited[next_node]:
                visited[next_node] = True
                q.append(next_node)
```

### 격자 BFS — 레벨 단위 (몇 턴 걸리는지)
```python
from collections import deque

def bfs_levels(grid):
    n, m = len(grid), len(grid[0])
    dx, dy = [-1,1,0,0], [0,0,-1,1]
    q = deque(시작점들)
    dist = 0
    while q:
        for _ in range(len(q)):        # 현재 레벨 전부 처리
            x, y = q.popleft()
            for d in range(4):
                nx, ny = x+dx[d], y+dy[d]
                if 0<=nx<n and 0<=ny<m and 조건:
                    q.append((nx, ny))
        dist += 1
    return dist
```

### 격자 BFS — 거리 배열 (각 칸까지 최단거리)
레벨 단위 버전과 **용도가 다름**. 각 지점별 거리가 다 필요할 때.

```python
from collections import deque

def bfs_dist(grid, start):
    n, m = len(grid), len(grid[0])
    dist = [[-1]*m for _ in range(n)]
    dist[start[0]][start[1]] = 0
    q = deque([start])
    dx, dy = [-1,1,0,0], [0,0,-1,1]

    while q:
        x, y = q.popleft()
        for d in range(4):
            nx, ny = x+dx[d], y+dy[d]
            if 0<=nx<n and 0<=ny<m and grid[nx][ny] != 1 and dist[nx][ny] == -1:
                dist[nx][ny] = dist[x][y] + 1
                q.append((nx, ny))
    return dist      # 도달 불가는 -1로 남음
```

### DFS vs BFS 선택 기준

- **최단거리/최소 횟수** → 반드시 BFS
- **연결 여부/개수만** → 아무거나 (결과 동일)
- **모든 경로 탐색/백트래킹** → DFS
- **그래프가 극단적으로 깊음** → DFS 스택 방식 또는 BFS

---

## 2.8 그리디 (출제 빈도: 높음 — 카카오 2023 이후 급증)

**코드는 짧지만 "이게 그리디로 풀리는 게 맞는지" 판단이 진짜 어려움.**

### 정렬 후 그리디 (기본 패턴)
```python
arr.sort()                      # 또는 sort(key=..., reverse=...)
result = 0
for x in arr:
    if 조건:
        result += x
```

### 구간 스케줄링 (끝나는 시간 기준 — 매우 빈출)
```python
intervals.sort(key=lambda x: x[1])      # 끝나는 시간 기준
count = 0
last_end = -float('inf')
for start, end in intervals:
    if start >= last_end:
        count += 1
        last_end = end
```

### 동전 거스름돈류 (큰 단위부터)
```python
coins = sorted(coins, reverse=True)
count = 0
for coin in coins:
    count += target // coin
    target %= coin
```

### 선택을 미루는 그리디 (힙 조합)
"지금 당장 최선"을 모를 때, 선택지를 모아뒀다가 더 미룰 수 없을 때 최선을 고르는 방식.

```python
import heapq

candidates = []
for item in items:
    heapq.heappush(candidates, -item.value)    # 후보 축적
    if 더_이상_미룰_수_없으면:
        best = -heapq.heappop(candidates)      # 그때 최선 선택
```

### 반례 의심 습관 (필수)
그리디로 풀었으면 **반드시 반례를 한 번 만들어볼 것**:

- 정렬 기준을 바꾸면 답이 달라지는가?
- "지금 최선"이 나중에 손해가 되는 케이스가 있는가?
- 있다면 그리디가 아니라 DP 영역 (2단계)

---

## 2.9 이분탐색 (출제 빈도: 높음 — 카카오·네이버)

### bisect 모듈 (직접 구현 불필요)
```python
from bisect import bisect_left, bisect_right

idx = bisect_left(arr, x)      # x가 들어갈 왼쪽 위치 (lower_bound)
idx = bisect_right(arr, x)     # 오른쪽 위치 (upper_bound)

# 존재 여부 확인
def exists(arr, target):
    idx = bisect_left(arr, target)
    return idx < len(arr) and arr[idx] == target

# 범위 내 개수
count = bisect_right(arr, hi) - bisect_left(arr, lo)
```

### 파라메트릭 서치 (값 자체를 이분탐색)
"최대 몇 개까지 가능한가", "최소 몇 시간이면 되는가" 류.

```python
def can_achieve(x):
    # x라는 값이 조건을 만족하는지 판정
    return True or False

left, right = 최소가능값, 최대가능값
answer = 0
while left <= right:
    mid = (left + right) // 2
    if can_achieve(mid):
        answer = mid           # 일단 저장하고 더 큰 값 탐색
        left = mid + 1
    else:
        right = mid - 1
```

> **신호**: "~하려면 최소/최대 얼마?" + N이 커서 완전탐색 불가 → 파라메트릭 서치 의심

---

# PART 3 — 파이썬 문법 최소셋

## 3.1 입출력
```python
# 프로그래머스는 solution 함수 형태라 입력 처리 불필요
# 백준·삼성 등에서 필요한 경우:
import sys
input = sys.stdin.readline        # 빠른 입력

n = int(input())
a, b = map(int, input().split())
arr = list(map(int, input().split()))
grid = [list(map(int, input().split())) for _ in range(n)]

print(' '.join(map(str, arr)))    # 리스트 한 번에 출력 (반복 print보다 빠름)
print(f"{3.14159:.2f}")           # 3.14
```

## 3.2 리스트
```python
arr.append(x); arr.pop(); arr.pop(0)
arr.insert(0, x); arr.remove(val)
arr.sort(); arr.sort(reverse=True); arr.reverse()
arr.index(val); arr.count(val)

# 슬라이싱
arr[1:3]; arr[:3]; arr[2:]; arr[::-1]; arr[::2]

# 컴프리헨션
[x**2 for x in range(10)]
[x for x in arr if x % 2 == 0]
[[0] * m for _ in range(n)]        # 2차원 초기화 (반드시 이 형태)

# 동시 순회
for i, v in enumerate(arr): pass
for a, b in zip(arr1, arr2): pass
```

## 3.3 딕셔너리 / 세트
```python
d.get(key)              # 없으면 None
d.get(key, 0)           # 없으면 기본값 (카운팅에 유용)
key in d                # 존재 확인
for k, v in d.items(): pass

s = set()               # 빈 세트 ({}는 딕셔너리라 주의)
s.add(x); s.discard(x)  # discard는 없어도 에러 X
a & b; a | b; a - b     # 교/합/차집합
x in s                  # O(1)
```

## 3.4 문자열
```python
s.split(); s.split(',')
','.join(lst)
s.strip(); s.replace(a, b)
s.upper(); s.lower()
s.isdigit(); s.isalpha()
s[::-1]                 # 뒤집기
ord('a'); chr(97)       # 문자 <-> 아스키
```

## 3.5 필수 모듈
```python
from collections import Counter, deque, defaultdict
import heapq
from itertools import permutations, combinations, product
from bisect import bisect_left, bisect_right
from functools import cmp_to_key
import sys
sys.setrecursionlimit(10**6)
```

## 3.6 자주 하는 실수 7가지

1. `arr2 = arr1` → 참조 복사 (같이 바뀜). 진짜 복사는 `arr1[:]` / `arr1.copy()` / `deepcopy`
2. `[[0]*m]*n` → 모든 행이 같은 리스트 참조. `[[0]*m for _ in range(n)]` 써야 함
3. 리스트 순회 중 `pop`/`remove` → 인덱스 밀림. 역순회하거나 새 리스트 생성
4. `set()`은 순서 보장 X → 순서 중요하면 리스트로 별도 관리
5. `input().split()`은 문자열 리스트 → 숫자면 반드시 `map(int, ...)`
6. 재귀 깊이 초과 → `sys.setrecursionlimit(10**6)` (그래도 안 되면 스택 방식 DFS)
7. `pop(0)`은 O(N) → 큐로 쓸 거면 `deque`

---

# 부록 예고 (2단계 — 필요할 때 별도 제작)

카카오 5~6번·네이버 3번 대비용. 1단계가 안정권에 들어선 뒤에 착수할 것.

- DP (1차원/2차원, 완전탐색+메모이제이션)
- 누적합 / 투포인터 / 슬라이딩 윈도우
- 다익스트라
- 유니온파인드 (카카오 준반복 유형)

**3단계(비트마스킹DP·구간DP·세그먼트트리·KMP·플로이드워셜·크루스칼·위상정렬)는 공부하지 않아도 4사 합격에 지장 없음.** 카카오 최후반부 1문제나 삼성 B형에만 등장하는 영역이므로, 여기 쓸 시간에 1·2단계 문제를 더 푸는 것이 기대 합격률이 높다.
