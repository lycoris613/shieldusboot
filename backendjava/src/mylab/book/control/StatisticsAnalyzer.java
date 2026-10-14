package mylab.book.control;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import mylab.book.entity.Magazine;
import mylab.book.entity.Novel;
import mylab.book.entity.Publication;
import mylab.book.entity.ReferenceBook;

/**
 * 출판물 배열을 분석하여 통계 정보를 계산/출력한다.
 */
public class StatisticsAnalyzer {

    /** 출판물 타입별 평균 가격을 계산한다. */
    public Map<String, Double> calculateAveragePriceByType(Publication[] publications) {
        Map<String, Double> totalByType = new HashMap<>();
        Map<String, Integer> countByType = new HashMap<>();

        for (Publication pub : publications) {
            String type = getPublicationType(pub);
            totalByType.put(type, totalByType.getOrDefault(type, 0.0) + pub.getPrice());
            countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        }

        Map<String, Double> averageByType = new HashMap<>();
        for (String type : totalByType.keySet()) {
            averageByType.put(type, totalByType.get(type) / countByType.get(type));
        }
        return averageByType;
    }

    /** 출판물 유형별 비율(백분율)을 계산한다. */
    public Map<String, Double> calculatePublicationDistribution(Publication[] publications) {
        Map<String, Integer> countByType = new HashMap<>();
        for (Publication pub : publications) {
            String type = getPublicationType(pub);
            countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        }

        Map<String, Double> distribution = new HashMap<>();
        int total = publications.length;
        for (String type : countByType.keySet()) {
            distribution.put(type, countByType.get(type) * 100.0 / total);
        }
        return distribution;
    }

    /** 지정한 연도에 출판된 출판물의 비율(백분율)을 계산한다. 출판일자 형식은 "yyyy-mm-dd" 로 가정한다. */
    public double calculatePublicationRatioByYear(Publication[] publications, String year) {
        int count = 0;
        for (Publication pub : publications) {
            String date = pub.getPublishDate();
            if (date != null && date.length() >= 4 && date.substring(0, 4).equals(year)) {
                count++;
            }
        }
        return count * 100.0 / publications.length;
    }

    /** 출판물 객체의 실제 타입을 확인하여 한글 타입명을 반환한다. */
    private String getPublicationType(Publication pub) {
        if (pub instanceof Novel) {
            return "소설";
        } else if (pub instanceof Magazine) {
            return "잡지";
        } else if (pub instanceof ReferenceBook) {
            return "참고서";
        } else {
            return "기타";
        }
    }

    /** 종합 통계 정보를 출력한다. */
    public void printStatistics(Publication[] publications) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        String[] order = {"소설", "참고서", "잡지"};

        System.out.println("===== 출판물 통계 분석 =====");

        Map<String, Double> averageByType = calculateAveragePriceByType(publications);
        System.out.println("1. 타입별 평균 가격:");
        for (String type : order) {
            if (averageByType.containsKey(type)) {
                System.out.println("   - " + type + ": " + df.format(averageByType.get(type)) + "원");
            }
        }

        Map<String, Double> distribution = calculatePublicationDistribution(publications);
        System.out.println("2. 출판물 유형 분포:");
        for (String type : order) {
            if (distribution.containsKey(type)) {
                System.out.println("   - " + type + ": " + df.format(distribution.get(type)) + "%");
            }
        }

        double ratio2007 = calculatePublicationRatioByYear(publications, "2007");
        System.out.println("3. 2007년에 출판된 출판물 비율: " + df.format(ratio2007) + "%");
    }
}
