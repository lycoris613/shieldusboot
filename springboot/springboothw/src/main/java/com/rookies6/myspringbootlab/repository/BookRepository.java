package com.rookies6.myspringbootlab.repository;

import com.rookies6.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//BookRepository 인터페이스
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // isbn 은 unique 이므로 단건 -> Optional
    Optional<Book> findByIsbn(String isbn);

    // 저자명으로 여러 권 -> List
    List<Book> findByAuthor(String author);

    // [제출2-4] 중복 ISBN 확인
    boolean existsByIsbn(String isbn);

    // [제출2-4] 저자/제목 부분 검색 (대소문자 무시)
    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    // [제출2-4] Book + BookDetail 을 한 번의 쿼리로 함께 로딩 (Fetch Join)
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.bookDetail WHERE b.id = :id")
    Optional<Book> findByIdWithBookDetail(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.bookDetail WHERE b.isbn = :isbn")
    Optional<Book> findByIsbnWithBookDetail(@Param("isbn") String isbn);
}
