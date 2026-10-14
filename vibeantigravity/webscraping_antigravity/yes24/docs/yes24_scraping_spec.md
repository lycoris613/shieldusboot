# Yes24 베스트셀러 대량 데이터 수집 명세서 및 가이드

## 1. 개요
Yes24의 카테고리별 베스트셀러 목록(Ajax 엔드포인트: `/product/category/BestSellerContents`)으로부터 도서 메타데이터를 수집하여 정제된 Pandas DataFrame으로 가공하고 CSV 데이터셋으로 저장하는 모듈입니다.

---

## 2. 프로젝트 디렉토리 구조
```
yes24/
├── data/
│   └── yes24_bestseller.csv       # 1~10페이지(최대 1,000위) 수집 결과 데이터 (UTF-8-SIG)
├── docs/
│   └── yes24_scraping_spec.md     # 본 수집 명세 및 설명 문서
├── images/                        # 시각화/분석 이미지 저장 디렉토리
└── src/
    └── scraper.py                 # 다중 페이지 대량 수집 및 로깅/가공 실행 코드
```

---

## 3. 수집 요구사항 및 기능 명세

### 3-1. 수집 파라미터
- **엔드포인트 URL**: `https://www.yes24.com/product/category/BestSellerContents`
- **카테고리**: `001001003` (IT 모바일 등)
- **페이지 범위**: 1페이지 ~ 10페이지
- **페이지당 수집 수**: 120개 (`pageSize=120`)
- **실제 유효 데이터**: Yes24 베스트셀러는 카테고리당 최대 1,000위까지 제공 (9페이지 40건 수집 후 1,000건 달성 시 종료)

### 3-2. 안정성 및 부하 방지 정책
- **요청 딜레이**: 각 페이지 요청 사이 `random.uniform(0.1, 0.5)`초의 무작위 대기 시간 적용
- **예외 격리**: 개별 도서 항목 파싱 시 `try-except`를 적용하여 특정 도서 정보 파싱 실패 시에도 전체 프로세스가 중단되지 않고 로그 출력 후 다음 도서로 진행
- **실시간 로깅**: `logging` 모듈을 통해 각 페이지 요청, 수집 건수, 누적 현황 및 오류 발생 여부를 실시간 출력

---

## 4. 수집 항목(Fields) 정의

| 컬럼명 | 타입 | 설명 | 예시 |
|---|---|---|---|
| `page` | integer | 수집된 페이지 번호 | `1` |
| `goods_no` | string | 도서 고유 번호 | `194483933` |
| `rank` | integer | 베스트셀러 순위 | `1` |
| `title` | string | 도서명 | `클로드 코드 제대로 시작하기` |
| `subtitle` | string | 부제목 / 도서 한줄소개 | `최신 클로드 코드 완벽 반영! ...` |
| `authors` | string | 저자 정보 | `주홍철, 황진성 저` |
| `publisher` | string | 출판사명 | `길벗` |
| `pub_date` | string | 출간 연월 | `2026년 08월` |
| `sale_price` | integer | 판매가 (원) | `28800` |
| `original_price` | integer | 정가 (원) | `32000` |
| `discount_rate` | string | 할인율 | `10%` |
| `point` | string | 적립 포인트 안내 | `포인트적립1,600원` |
| `sales_index` | integer | 판매지수 | `14070` |
| `rating` | float | 평점 (10점 만점) | `10.0` |
| `review_count` | integer | 회원 리뷰 건수 | `5` |
| `delivery_info` | string | 배송 예정 정보 | - |
| `image_url` | string | 표지 이미지 링크 | `https://image.yes24.com/goods/...` |
| `detail_url` | string | 상품 상세 페이지 URL | `https://www.yes24.com/product/goods/...` |

---

## 5. 실행 방법

```bash
# 워크스페이스 루트에서 uv를 통해 실행
uv run python yes24/src/scraper.py
```
