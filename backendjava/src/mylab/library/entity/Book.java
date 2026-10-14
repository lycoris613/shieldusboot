package mylab.library.entity;

/**
 * 도서 한 권의 정보를 담는 클래스.
 * 모든 멤버 변수는 private, 접근은 getter/setter 로만(캡슐화).
 */
public class Book {

    private String title;       // 제목
    private String author;      // 저자
    private String isbn;        // ISBN
    private int publishYear;    // 출판년도
    private boolean isAvailable; // 대출 가능 여부 (true: 대출 가능)

    /** 기본 생성자: 생성 시 대출 가능 상태로 설정한다. */
    public Book() {
        this.isAvailable = true;
    }

    /** 모든 필드를 초기화하는 생성자. 대출 가능 상태는 true 로 설정한다. */
    public Book(String title, String author, String isbn, int publishYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishYear = publishYear;
        this.isAvailable = true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    /**
     * 도서 대출 처리.
     * 대출 가능하면 상태를 대출 중으로 바꾸고 true 를, 이미 대출 중이면 false 를 반환한다.
     */
    public boolean checkOut() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }

    /** 도서 반납 처리. 대출 가능 상태로 되돌린다. */
    public boolean returnBook() {
        isAvailable = true;
        return true;
    }

    @Override
    public String toString() {
        return "책 제목: " + title + "\n"
                + "저자: " + author + "\n"
                + "ISBN: " + isbn + "\n"
                + "출판년도: " + publishYear + "\n"
                + "대출 가능 여부: " + (isAvailable ? "가능" : "대출 중");
    }
}
