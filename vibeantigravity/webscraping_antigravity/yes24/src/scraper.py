"""
Yes24 베스트셀러 전수 자동 수집 및 데이터 병합 스크립트
작성일: 2026-08-19
주요 기능:
- 1페이지부터 마지막 페이지까지 자동으로 순회하며 전수 데이터 수집
- 각 페이지 요청마다 0.1~0.5초 사이의 무작위 딜레이 적용
- 개별 도서 파싱 오류 발생 시 예외 격리 및 로깅
- 기존 CSV 파일이 있을 경우 중복(goods_no)을 제거하고 최신 데이터로 병합하여 저장
"""

import logging
import os
import random
import re
import sys
import time
from typing import Any, Dict, List, Optional
from bs4 import BeautifulSoup
import pandas as pd
import requests

# Windows 콘솔 utf-8 인코딩 호환성 보장
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

# 로깅 기본 설정
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger("Yes24Scraper")

BASE_URL = "https://www.yes24.com"
AJAX_URL = "https://www.yes24.com/product/category/BestSellerContents"

DEFAULT_HEADERS = {
    "host": "www.yes24.com",
    "sec-ch-ua": '"Not;A=Brand";v="8", "Chromium";v="150", "Google Chrome";v="150"',
    "sec-ch-ua-mobile": "?0",
    "sec-ch-ua-platform": '"Windows"',
    "sec-fetch-dest": "empty",
    "sec-fetch-mode": "cors",
    "sec-fetch-site": "same-origin",
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36",
    "x-requested-with": "XMLHttpRequest",
}


def clean_text(text: Optional[str]) -> str:
    """텍스트의 불필요한 공백과 줄바꿈을 제거합니다."""
    if not text:
        return ""
    return re.sub(r"\s+", " ", text).strip()


def extract_number(text: Optional[str]) -> Optional[int]:
    """텍스트에서 숫자만 추출하여 정수로 반환합니다."""
    if not text:
        return None
    numbers = re.findall(r"\d+", text.replace(",", ""))
    if numbers:
        return int("".join(numbers))
    return None


def fetch_bestseller_page(
    category_number: str = "001001003",
    page_number: int = 1,
    page_size: int = 120,
    headers: Optional[Dict[str, str]] = None,
) -> str:
    """Yes24 베스트셀러 Ajax 엔드포인트에서 HTML 조각을 가져옵니다."""
    req_headers = DEFAULT_HEADERS.copy()
    if headers:
        req_headers.update(headers)
    req_headers["referer"] = (
        f"https://www.yes24.com/product/category/bestseller?categoryNumber={category_number}&pageNumber={page_number}&pageSize={page_size}"
    )

    params = {
        "categoryNumber": category_number,
        "sumGb": "06",
        "sex": "A",
        "age": "255",
        "goodsTp": "0",
        "addOptionTp": "0",
        "excludeTp": "2",
        "pageNumber": str(page_number),
        "pageSize": str(page_size),
        "goodsStatGb": "06",
        "eBookTp": "0",
        "bestType": "YES24_BESTSELLER",
        "type": "",
        "saleYear": "0",
        "saleMonth": "0",
        "weekNo": "0",
        "saleDts": "",
        "viewMode": "",
        "freeYn": "",
    }

    response = requests.get(AJAX_URL, headers=req_headers, params=params, timeout=15)
    response.raise_for_status()
    return response.text


def parse_single_item(elem: BeautifulSoup, current_page: int) -> Optional[Dict[str, Any]]:
    """단일 li 요소를 파싱하여 도서 정보를 추출합니다. 오류 발생 시 None 반환."""
    goods_no = elem.get("data-goods-no", "").strip()
    if not goods_no:
        return None

    try:
        # 순위
        rank_elem = elem.select_one("em.ico.rank")
        rank = int(rank_elem.get_text(strip=True)) if rank_elem and rank_elem.get_text(strip=True).isdigit() else None

        # 도서명 및 부제목
        title_elem = elem.select_one(".info_row.info_name a.gd_name")
        title = clean_text(title_elem.get_text(strip=True)) if title_elem else ""

        subtitle_elem = elem.select_one(".info_row.info_name span.gd_nameE")
        subtitle = clean_text(subtitle_elem.get_text(strip=True)) if subtitle_elem else ""

        # 저자 파싱
        auth_elem = elem.select_one(".info_row.info_pubGrp span.info_auth")
        authors = ""
        if auth_elem:
            author_links = [clean_text(a.get_text(strip=True)) for a in auth_elem.find_all("a")]
            raw_auth_text = clean_text(auth_elem.get_text(strip=True))
            if author_links:
                suffix_match = re.search(r"(저|역|글|그림|공저|편저|감수)$", raw_auth_text)
                suffix = f" {suffix_match.group(1)}" if suffix_match else ""
                authors = ", ".join(author_links) + suffix
            else:
                authors = raw_auth_text

        # 출판사, 출간일
        pub_elem = elem.select_one(".info_row.info_pubGrp span.info_pub")
        publisher = clean_text(pub_elem.get_text(strip=True)) if pub_elem else ""

        date_elem = elem.select_one(".info_row.info_pubGrp span.info_date")
        pub_date = clean_text(date_elem.get_text(strip=True)) if date_elem else ""

        # 가격 정보
        sale_price_elem = elem.select_one(".info_row.info_price strong.txt_num em.yes_b")
        sale_price = extract_number(sale_price_elem.get_text(strip=True)) if sale_price_elem else None

        orig_price_elem = elem.select_one(".info_row.info_price span.txt_num.dash em.yes_m")
        original_price = extract_number(orig_price_elem.get_text(strip=True)) if orig_price_elem else None

        discount_elem = elem.select_one(".info_row.info_price span.txt_sale em.num")
        discount_rate = f"{discount_elem.get_text(strip=True)}%" if discount_elem else ""

        point_elem = elem.select_one(".info_row.info_price span.yPoint")
        point = clean_text(point_elem.get_text(strip=True)) if point_elem else ""

        # 판매지수, 평점, 리뷰 수
        sales_index_elem = elem.select_one(".info_row.info_rating span.saleNum")
        sales_index = extract_number(sales_index_elem.get_text(strip=True)) if sales_index_elem else None

        review_elem = elem.select_one(".info_row.info_rating span.rating_rvCount em.txC_blue")
        review_count = extract_number(review_elem.get_text(strip=True)) if review_elem else 0

        rating_elem = elem.select_one(".info_row.info_rating span.rating_grade em.yes_b")
        try:
            rating = float(rating_elem.get_text(strip=True)) if rating_elem else None
        except ValueError:
            rating = None

        # 배송 안내
        deli_elem = elem.select_one(".info_row.info_deli")
        delivery_info = clean_text(deli_elem.get_text(strip=True)) if deli_elem else ""

        # 이미지 URL
        img_elem = elem.select_one(".item_img img")
        img_url = ""
        if img_elem:
            img_url = img_elem.get("data-original") or img_elem.get("src") or ""

        # 상세 URL
        detail_url = f"{BASE_URL}/product/goods/{goods_no}"

        return {
            "page": current_page,
            "goods_no": str(goods_no),
            "rank": rank,
            "title": title,
            "subtitle": subtitle,
            "authors": authors,
            "publisher": publisher,
            "pub_date": pub_date,
            "sale_price": sale_price,
            "original_price": original_price,
            "discount_rate": discount_rate,
            "point": point,
            "sales_index": sales_index,
            "rating": rating,
            "review_count": review_count,
            "delivery_info": delivery_info,
            "image_url": img_url,
            "detail_url": detail_url,
        }
    except Exception as e:
        logger.warning(f"[페이지 {current_page}] 상품(goods_no: {goods_no}) 파싱 중 오류 발생 (건너뜀): {e}")
        return None


def parse_bestseller_items(html_content: str, current_page: int) -> List[Dict[str, Any]]:
    """HTML 조각 내의 모든 도서 항목을 파싱합니다."""
    soup = BeautifulSoup(html_content, "html.parser")
    items = []

    item_elements = soup.select("li[data-goods-no]")
    for elem in item_elements:
        item = parse_single_item(elem, current_page)
        if item:
            items.append(item)

    return items


def collect_all_bestsellers(
    category_number: str = "001001003",
    start_page: int = 1,
    page_size: int = 120,
    min_delay: float = 0.1,
    max_delay: float = 0.5,
    max_pages: int = 100,
) -> List[Dict[str, Any]]:
    """1페이지부터 마지막 페이지(빈 응답)까지 자동으로 탐색하며 수집합니다."""
    all_items: List[Dict[str, Any]] = []
    current_page = start_page

    logger.info(f"전수 자동 수집 시작: 카테고리={category_number}, 시작페이지={start_page}, 페이지당={page_size}개")

    while current_page <= max_pages:
        try:
            logger.info(f"[페이지 {current_page}] 요청 중...")
            html_content = fetch_bestseller_page(
                category_number=category_number,
                page_number=current_page,
                page_size=page_size,
            )

            page_items = parse_bestseller_items(html_content, current_page=current_page)

            # 더 이상 데이터가 없는 경우 마지막 페이지 도달로 판단
            if not page_items:
                logger.info(f"[페이지 {current_page}] 더 이상 도서 데이터가 없습니다. (수집 종료)")
                break

            all_items.extend(page_items)
            logger.info(
                f"[페이지 {current_page}] 수집 완료: {len(page_items)}건 (현재 누적 {len(all_items)}건)"
            )

            # 요청 항목 수가 pageSize보다 작으면 마지막 페이지로 간주
            if len(page_items) < page_size:
                logger.info(f"[페이지 {current_page}] 마지막 페이지 도달 ({len(page_items)}건 < {page_size}건). 수집을 완료합니다.")
                break

        except Exception as e:
            logger.error(f"[페이지 {current_page}] 요청 실패: {e}")
            break

        # 0.1~0.5초 무작위 딜레이 적용
        sleep_sec = random.uniform(min_delay, max_delay)
        time.sleep(sleep_sec)
        current_page += 1

    logger.info(f"수집 루프 종료! 신규 수집 총 건수: {len(all_items)}건")
    return all_items


def merge_and_save_data(new_items: List[Dict[str, Any]], filepath: str) -> pd.DataFrame:
    """새로 수집된 데이터를 기존 CSV 파일과 병합(중복 제거)하고 정렬 후 저장합니다."""
    new_df = pd.DataFrame(new_items)

    if os.path.exists(filepath):
        try:
            existing_df = pd.read_csv(filepath, dtype={"goods_no": str})
            logger.info(f"기존 CSV 파일 발견: {len(existing_df):,}건 로드됨.")
            
            # 신규 데이터와 기존 데이터 병합 (신규 데이터를 우선 반영하기 위해 new_df -> existing_df 순서로 결합)
            combined_df = pd.concat([new_df, existing_df], ignore_index=True)
            # goods_no 기준 중복 제거 (첫 번째 발견된 최신 레코드 유지)
            merged_df = combined_df.drop_duplicates(subset=["goods_no"], keep="first").copy()
            logger.info(f"병합 완료: 기존 {len(existing_df):,}건 + 신규 {len(new_df):,}건 -> 중복 제거 후 총 {len(merged_df):,}건")
        except Exception as e:
            logger.warning(f"기존 CSV 파일 로드 중 오류 발생 ({e}). 신규 데이터로 대체합니다.")
            merged_df = new_df
    else:
        merged_df = new_df

    if merged_df.empty:
        logger.warning("저장할 데이터가 없습니다.")
        return merged_df

    # 순위 기준 정렬 (rank가 있는 경우)
    if "rank" in merged_df.columns:
        merged_df["rank"] = pd.to_numeric(merged_df["rank"], errors="coerce")
        merged_df = merged_df.sort_values(by="rank", ascending=True).reset_index(drop=True)

    # 데이터 통계 요약
    logger.info("=" * 60)
    logger.info("[최종 데이터프레임 요약 정보]")
    logger.info(f"- 총 저장 도서 수: {len(merged_df):,}권")
    logger.info(f"- 평균 판매가: {merged_df['sale_price'].mean():,.0f}원")
    logger.info(f"- 평균 평점: {merged_df['rating'].mean():.2f}점")
    top_pubs = merged_df["publisher"].value_counts().head(5).to_dict()
    logger.info(f"- 상위 출판사 TOP 5: {top_pubs}")
    logger.info("=" * 60)

    # CSV 파일 저장 (utf-8-sig)
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    merged_df.to_csv(filepath, index=False, encoding="utf-8-sig")
    logger.info(f"[저장 완료] CSV 파일: {filepath}")

    return merged_df


def main():
    category_number = "001001003"  # IT 모바일 카테고리
    page_size = 120

    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    output_csv = os.path.join(project_root, "data", "yes24_bestseller.csv")

    # 1페이지부터 마지막 페이지까지 자동 수집
    new_items = collect_all_bestsellers(
        category_number=category_number,
        start_page=1,
        page_size=page_size,
        min_delay=0.1,
        max_delay=0.5,
    )

    # 기존 CSV와 병합 후 저장
    df = merge_and_save_data(new_items, output_csv)


if __name__ == "__main__":
    main()
