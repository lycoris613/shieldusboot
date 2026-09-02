package mylab.book.entity;

/**
 * 소설. Publication 을 상속받아 author(저자), genre(장르) 속성을 추가한다.
 */
public class Novel extends Publication {

    private String author; // 저자명
    private String genre;  // 장르

    public Novel() {
    }

    public Novel(String title, String publishDate, int page, int price, String author, String genre) {
        super(title, publishDate, page, price);
        this.author = author;
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return super.toString() + " [소설] 저자:" + author + ", 장르:" + genre
                + ", " + getPage() + "쪽, " + getPrice() + "원, 출판일:" + getPublishDate();
    }
}
