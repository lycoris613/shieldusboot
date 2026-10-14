# Yes24 데이터 수집 명세

## 1. 과업 목표
- Yes24 웹사이트에서 특정 카테고리의 도서 정보를 수집합니다.
- 수집된 데이터는 분석 및 시각화에 활용될 수 있도록 정제된 형태로 저장합니다.

## 2. 수집 관련 정보

### 네트워크 메뉴를 통해 실제 데이터를 가져오는 URL
Request URL
https://www.yes24.com/product/category/BestSellerContents?categoryNumber=001001003&sumGb=06&sex=A&age=255&goodsTp=0&addOptionTp=0&excludeTp=2&pageNumber=2&pageSize=24&goodsStatGb=06&eBookTp=0&bestType=YES24_BESTSELLER&type=&saleYear=0&saleMonth=0&weekNo=0&saleDts=&viewMode=&freeYn=
Request Method
GET
Status Code
200 OK
Remote Address
61.111.13.51:443
Referrer Policy
unsafe-url


### 해당 Request에 대한 Header 정보
host
www.yes24.com
referer
https://www.yes24.com/product/category/bestseller?categoryNumber=001001003&pageNumber=2&pageSize=24
rtt
50
sec-ch-ua
"Not;A=Brand";v="8", "Chromium";v="150", "Google Chrome";v="150"
sec-ch-ua-mobile
?0
sec-ch-ua-platform
"Windows"
sec-fetch-dest
empty
sec-fetch-mode
cors
sec-fetch-site
same-origin
user-agent
Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36
viewport-width
1148
x-requested-with
XMLHttpRequest

### Payload
categoryNumber=001001003&sumGb=06&sex=A&age=255&goodsTp=0&addOptionTp=0&excludeTp=2&pageNumber=2&pageSize=24&goodsStatGb=06&eBookTp=0&bestType=YES24_BESTSELLER&type=&saleYear=0&saleMonth=0&weekNo=0&saleDts=&viewMode=&freeYn=

### 응답 예시 (HTML, JSON 의 일부 정보)
<li class="" data-goods-no="194189421" data-iy-no="0" data-statgb="02" data-delvbadge="BOOK">
    <div class="itemUnit">
        <div class="item_img">
            <div class="img_canvas">
                
                    <div class="img_upper">
                            <em class="ico rank">27</em>
                        <span class="rank_info rank_dn">
                        </span>

                    </div>
                <span class="img_item">
                    <span class="img_grp">
                        
                                                                                                <a href="/product/goods/194189421" class="lnk_img" onclick="wiseLogV2('BS', '001_005_001', ''); ">
                            <em class="img_bdr">
                                <img class="lazy" data-original="https://image.yes24.com/goods/194189421/L" src="https://image.yes24.com/goods/194189421/L" border="0" alt="AI 대세전환 압도적 스킬로 바로 쓰는 클로드 코워크&amp;#215;AI 자동화" style="display: inline;">
                            </em>
                        </a>
                    </span>
                </span>
            </div>
            <div class="img_btn">
                
                        <a href="javascript:yes24GU.openPreviewCheck(194189421); wiseLogV2('BS', '001_005_011', '');" class="btnC btn_preview"><span class="bWrap"><em class="txt">미리보기</em></span></a>
                                            </div>
        </div>
        <div class="item_info">
            
                <div class="info_row info_keynote">




    <span id="spanGdKeynote" class="gd_keynote">
                

                                                                        <a href="javascript:void(0);" onclick="openUrl('/product/category/series/001001003?SeriesNumber=383894','Pcode','003_001')" class="lnk_series">AI 대세전환</a>
        <!-- 클래스24 상품일 경우 -->
    </span>
    <script type="text/javascript">
        if ($('#spanGdKeynote').children().length == 0) {
            $('#spanGdKeynote').remove();
        }
    </script>


                </div>
            <div class="info_row info_name">
                
                    <span class="gd_res">[도서]</span>
                                <a class="gd_name" href="/product/goods/194189421" onclick="wiseLogV2('BS', '001_005_001', ''); ">AI 대세전환 압도적 스킬로 바로 쓰는 클로드 코워크×AI 자동화</a>
                
                    <span class="gd_nameE">비개발자 N잡러를 위한 사무 자동화부터 콘텐츠 제작까지</span>
                                <a href="/product/goods/194189421" target="_blank" class="bgYUI ico_nWin" onclick="wiseLogV2('BS', '001_005_001', ''); ">AI 대세전환 압도적 스킬로 바로 쓰는 클로드 코워크×AI 자동화 새창이동</a>
            </div>
                <div class="info_row info_pubGrp">
                    
                            <span class="authPub info_auth" onclick="wiseLogV2('BS', '001_005_002', '');">
                                <a href="https://www.yes24.com/product/search?domain=ALL&amp;query=%25EA%25B0%2595%25EB%258B%25A4%25EC%2586%2594&amp;authorNo=478070&amp;author=강다솔" target="">강다솔</a>, <a href="https://www.yes24.com/product/search?domain=ALL&amp;query=%25EC%25A1%25B0%25EC%2583%2581%25EC%25B2%25A0&amp;authorNo=541410&amp;author=조상철" target="">조상철</a> 저
                            </span>
                    
                        <span class="authPub info_pub" onclick="wiseLogV2('BS', '001_005_003', '');"><a href="https://www.yes24.com/product/search?&amp;domain=ALL&amp;company=%ed%94%84%eb%a6%ac%eb%a0%89&amp;query=%25ED%2594%2584%25EB%25A6%25AC%25EB%25A0%2589">프리렉</a></span>
                                            <span class="authPub info_date">2026년 07월</span>
                </div>
                                                        <div class="info_row info_price">
                            <span class="txt_sale"><em class="num">10</em>%</span>
                        <strong class="txt_num"><em class="yes_b">25,200</em>원</strong>
                        <span class="txt_num dash"><em class="yes_m">28,000</em>원</span>
                                                    <span class="yPoint"><em class="bgYUI ico_point">포인트적립</em>1,400원</span>
                    </div>
                            <div class="info_row info_rating ">
                                <span class="saleNum">
                                    판매지수 2,574
                                </span>
                                            <span class="rating_rvCount">
                            <a href="https://www.yes24.com/product/goods/194189421?ReviewYn=Y" onclick="wiseLogV2('BS', '001_005_004', '');"><em class="bit">회원리뷰</em>(<em class="txC_blue">1</em>건)</a>
                        </span>
                        <span class="rating_grade">
                            <span class="bgYUI tRating tRating_10">리뷰 총점</span><em class="yes_b">10.0</em>
                            <span class="moreRatingArea">
                                <span class="moreRatingBtn">
                                        <a href="javascript:void(0);" onclick="toggleLiCont(this,$('.sGLi'),event);" class="bgYUI">정보 더 보기/감추기</a>
                                </span>
                                <span class="moreRatingLi">
                                    <span class="moreRatingLiRow">
                                        <ul class="yesAlertLi">
                                            <li><em class="bl_dot bgYUI">&nbsp;</em>종이책 리뷰 (1건)</li>
                                            <li><em class="bl_dot bgYUI">&nbsp;</em>eBook 리뷰 (0건)</li>
                                            <li><em class="bl_dot bgYUI">&nbsp;</em>종이책 한줄평 (0건)</li>
                                            <li><em class="bl_dot bgYUI">&nbsp;</em>eBook 한줄평 (0건)</li>
                                        </ul>
                                    </span>
                                </span>
                            </span>
                        </span>
                </div>

                    <div class="info_row info_deli" name="delvTextArea"><span class="deli_des">21시까지 주문하면 </span><span class="deli_date"><strong class="deli_act">내일 아침 7시 전 (8/11, 화)</strong> 도착예정</span></div>                    

            
                                                                                                
            
            

            
                                            </div>
            <div class="item_btnCol">
                
                


            <span class="btn_row">
                <span class="chkBox" style=""><label><input type="checkbox" name="ORD_GOODS_CHKBOX" id="ordChk_194189421" data-goodsno="194189421" class="basic" style=""><span class="bgYUI chk"></span></label></span>
                    <span class="numBox">
                        <span class="yesIpt ipt_wSizeF">
                            <input type="text" name="ORD_GOODS_CNT" id="ordCnt_194189421" style="ime-mode:disabled !important" title="수량설정" value="1" class="ac yes_m" onkeyup="return checkNumeric(this);" maxlength="3">
                        </span>
                        <button type="button" class="minus" onclick="order_payment.downOrderCount('194189421'); wiseLogV2('BS', '001_005_006', '');"><span class="bgYUI">수량감소</span></button>
                        <button type="button" class="plus" onclick="order_payment.upOrderCount('194189421'); wiseLogV2('BS', '001_005_006', '');"><span class="bgYUI">수량증가</span></button>
                    </span>
            </span>
                <a href="javascript:void(0);" onclick="order_payment.addCartV3('194189421', '', this, '', '', '', '', 'Search', true); wiseLogV2('BS', '001_005_007', '');" class="btnC btn_blue"><span class="bWrap"><em class="txt">카트에 넣기</em></span></a>
            <a href="javascript:void(0);" onclick="order_payment.orderDirectV3('194189421', '', this, '', '', '', '', 'Search'); wiseLogV2('BS', '001_005_008', '');" class="btnC btn_sBlue"><span class="bWrap"><em class="txt">바로구매</em></span></a>
        <a href="javascript:void(0);" class="btnC" name="btnList" onclick="order_payment.addMyListV3('194189421', '', '', true, 'whitemam'); wiseLogV2('BS', '001_005_010', '');;"><span class="bWrap"><em class="txt">리스트에 넣기</em></span></a>
<input type="hidden" name="ORD_GOODS_OPT" id="ordOpt_194189421" value="{&quot;goods_no&quot;:194189421,&quot;goods_seq&quot;:1,&quot;order_limit_yn&quot;:&quot;N&quot;,&quot;order_remain_count&quot;:0,&quot;event_no&quot;:0,&quot;add_cart_yn&quot;:&quot;Y&quot;,&quot;goods_state&quot;:&quot;02&quot;,&quot;order_limit_count&quot;:0,&quot;resource_key&quot;:&quot;01&quot;,&quot;limit_age_yn&quot;:&quot;N&quot;,&quot;limit_age&quot;:0,&quot;member_age&quot;:54,&quot;goods_name&quot;:&quot;AI 대세전환 압도적 스킬로 바로 쓰는 클로드 코워크&amp;#215;AI 자동화&quot;,&quot;noint_quotamonth&quot;:0,&quot;min_cnt&quot;:0,&quot;max_cnt&quot;:0,&quot;opt_salepr&quot;:0,&quot;opt_yn&quot;:&quot;N&quot;,&quot;opt_inst_yn&quot;:&quot;N&quot;,&quot;flat_rate_yn&quot;:null,&quot;rent_goods_yn&quot;:&quot;N&quot;,&quot;bookclue_yn&quot;:&quot;N&quot;,&quot;goods_gb&quot;:&quot;01&quot;,&quot;goodsSortNo&quot;:&quot;001002&quot;,&quot;goodsSortNm&quot;:&quot;IT 모바일&quot;,&quot;goodsAuth&quot;:&quot;&lt;강다솔&gt;,&lt;조상철&gt; 저&quot;,&quot;shopPrice&quot;:28000.00,&quot;salePrice&quot;:25200.00,&quot;discountShopPrice&quot;:2800.00}">

            </div>
    </div>
</li>

### 한 페이지가 성공적으로 수집되는지 확인하고 csv 파일로 저장할 것

## 3. 데이터 수집 프로세스
* 파이썬과 관련 라이브러리를 활용하여 1페이지부터 10페이지까지 수집하고 페이지당 120개씩 수집할 수 
있도록 하고 수집한 결과의 일부를 데이터프레임 형태로 가공하고 수집 결과는 csv파일로 저장할 것
* 수집 과정을 로그로 출력하여 수집 상태를 확인할 수 있게 할 것 

## 4. 데이터 저장 형식
- **파일 형식:** CSV (Comma-Separated Values)


## 5. 추가 요구사항
- 데이터 수집 시 웹사이트에 과도한 부하를 주지 않도록 요청 간에 적절한 시간 간격(예:  0.1~0.5초 사이)을 둡니다.
- 오류 발생 시(예: 특정 항목을 찾을 수 없는 경우), 해당 항목은 건너뛰고 로그를 남긴 후 다음 항목 수집을 계속 진행합니다.