package mylab.book.entity;

/**
 * 모든 출판물의 기본 클래스.
 * title, publishDate, page, price 속성을 가진다.
 */
public class Publication {

    private String title;       // 출판물 제목
    private String publishDate; // 출간일 (yyyy-MM-dd)
    private int page;           // 페이지 수
    private int price;          // 가격

    /** 기본 생성자: 빈 객체 생성 */
    public Publication() {
    }

    /** 매개변수 생성자: 모든 필드 초기화 */
    public Publication(String title, String publishDate, int page, int price) {
        this.title = title;
        this.publishDate = publishDate;
        this.page = page;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    /** 요구사항에 맞게 title 만 반환한다. */
    @Override
    public String toString() {
        return title;
    }
}
