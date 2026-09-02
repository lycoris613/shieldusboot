package mylab.book.control;

import mylab.book.entity.Magazine;
import mylab.book.entity.Novel;
import mylab.book.entity.Publication;
import mylab.book.entity.ReferenceBook;

/**
 * [실습4] 출판물 관리 시스템의 주요 실행 클래스.
 * 다형성을 활용해 다양한 출판물을 생성/출력하고, 가격을 조정하며, 통계 분석을 수행한다.
 */
public class ManageBook {

    public static void main(String[] args) {
        // 다형성: Publication 배열에 하위 클래스 객체들을 저장
        Publication[] publications = {
                new Magazine("마이크로소프트", "2007-10-01", 328, 9900, "매월"),
                new Magazine("경영과컴퓨터", "2007-10-03", 316, 9000, "매월"),
                new Novel("빠삐용", "2007-07-01", 396, 9800, "베르나르베르베르", "현대소설"),
                new Novel("남한산성", "2007-04-14", 383, 11000, "김훈", "대하소설"),
                new ReferenceBook("실용주의프로그래머", "2007-01-14", 496, 25000, "소프트웨어공학"),
                new Novel("소년이온다", "2014-05-01", 216, 15000, "한강", "장편소설"),
                new Novel("작별하지않는다", "2021-09-09", 332, 15120, "한강", "장편소설")
        };

        // 출판물 정보 출력 (1부터 시작하는 일련번호)
        System.out.println("==== 도서 정보 출력 ====");
        for (int i = 0; i < publications.length; i++) {
            System.out.println((i + 1) + ". " + publications[i]);
        }

        // 가격 변경 (마지막 도서)
        System.out.println("==== 가격 변경 ====");
        Publication target = publications[publications.length - 1];
        int priceBefore = target.getPrice();
        System.out.println(target.getTitle() + " 변경 전 가격: " + priceBefore + "원");

        modifyPrice(target);

        int priceAfter = target.getPrice();
        System.out.println(target.getTitle() + " 변경 후 가격: " + priceAfter + "원");
        System.out.println("차액: " + (priceBefore - priceAfter) + "원");

        // 통계 분석 실행
        StatisticsAnalyzer analyzer = new StatisticsAnalyzer();
        analyzer.printStatistics(publications);
    }

    /**
     * 출판물의 실제 타입에 따라 다른 할인율을 적용한다.
     *   Magazine      : 40% 할인 (원가의 60%)
     *   Novel         : 20% 할인 (원가의 80%)
     *   ReferenceBook : 10% 할인 (원가의 90%)
     */
    public static void modifyPrice(Publication publication) {
        int currentPrice = publication.getPrice();
        if (publication instanceof Magazine) {
            publication.setPrice((int) (currentPrice * 0.6));
        } else if (publication instanceof Novel) {
            publication.setPrice((int) (currentPrice * 0.8));
        } else if (publication instanceof ReferenceBook) {
            publication.setPrice((int) (currentPrice * 0.9));
        }
    }
}
