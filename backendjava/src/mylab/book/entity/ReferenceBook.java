package mylab.book.entity;

/**
 * 참고서. Publication 을 상속받아 field(전문 분야) 속성을 추가한다.
 */
public class ReferenceBook extends Publication {

    private String field; // 전문 분야 (예: "소프트웨어공학")

    public ReferenceBook() {
    }

    public ReferenceBook(String title, String publishDate, int page, int price, String field) {
        super(title, publishDate, page, price);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return super.toString() + " [참고서] 분야:" + field
                + ", " + getPage() + "쪽, " + getPrice() + "원, 출판일:" + getPublishDate();
    }
}
