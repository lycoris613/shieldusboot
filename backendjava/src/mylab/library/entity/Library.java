package mylab.library.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Book 객체들을 관리하는 도서관 클래스.
 */
public class Library {

    private String name;        // 도서관 이름
    private List<Book> books;    // 도서 목록

    public Library(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** 도서 추가 */
    public void addBook(Book book) {
        books.add(book);
        System.out.println("도서가 추가되었습니다: " + book.getTitle());
    }

    /** 제목으로 도서 검색 (첫 번째 일치 도서 반환) */
    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    /** 저자로 도서 검색 (일치하는 모든 도서 반환) */
    public List<Book> findBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equals(author)) {
                result.add(book);
            }
        }
        return result;
    }

    /** ISBN 으로 도서 검색 */
    public Book findBookByISBN(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    /** ISBN 으로 도서 대출 */
    public boolean checkOutBook(String isbn) {
        Book book = findBookByISBN(isbn);
        if (book == null) {
            return false;
        }
        return book.checkOut();
    }

    /** ISBN 으로 도서 반납 */
    public boolean returnBook(String isbn) {
        Book book = findBookByISBN(isbn);
        if (book == null) {
            return false;
        }
        return book.returnBook();
    }

    /** 대출 가능한 도서 목록 */
    public List<Book> getAvailableBooks() {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                result.add(book);
            }
        }
        return result;
    }

    /** 전체 도서 목록 */
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /** 전체 도서 갯수 */
    public int getTotalBooks() {
        return books.size();
    }

    /** 대출 가능한 도서 갯수 */
    public int getAvailableBooksCount() {
        return getAvailableBooks().size();
    }

    /** 대출 중인 도서 갯수 */
    public int getBorrowedBooksCount() {
        return getTotalBooks() - getAvailableBooksCount();
    }
}
