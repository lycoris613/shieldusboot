package com.rookies6.myspringbootlab.repository;

import com.rookies6.myspringbootlab.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [SpringBoot 실습2-1] BookRepository 테스트
 *
 * @SpringBootTest 로 전체 컨텍스트를 띄우고, @Transactional 로 각 테스트가 끝나면
 * 롤백되도록 하여 unique(isbn) 제약이 테스트 간에 충돌하지 않게 한다.
 */
@SpringBootTest
@Transactional
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    // ===== 샘플 데이터 (문제 표) =====
    private Book springBootBook() {
        return Book.builder()
                .title("스프링 부트 입문")
                .author("홍길동")
                .isbn("9788956746425")
                .price(30000)
                .publishDate(LocalDate.of(2025, 5, 7))
                .build();
    }

    private Book jpaBook() {
        return Book.builder()
                .title("JPA 프로그래밍")
                .author("박둘리")
                .isbn("9788956746432")
                .price(35000)
                .publishDate(LocalDate.of(2025, 4, 30))
                .build();
    }

    @Test
    @DisplayName("도서 등록 테스트")
    void testCreateBook() {
        Book saved = bookRepository.save(springBootBook());

        assertThat(saved.getId()).isNotNull();

        Book found = bookRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("스프링 부트 입문");
        assertThat(found.getAuthor()).isEqualTo("홍길동");
        assertThat(found.getIsbn()).isEqualTo("9788956746425");
        assertThat(found.getPrice()).isEqualTo(30000);
        assertThat(found.getPublishDate()).isEqualTo(LocalDate.of(2025, 5, 7));
    }

    @Test
    @DisplayName("ISBN 으로 도서 조회 테스트")
    void testFindByIsbn() {
        bookRepository.save(springBootBook());
        bookRepository.save(jpaBook());

        Optional<Book> result = bookRepository.findByIsbn("9788956746432");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("JPA 프로그래밍");
        assertThat(result.get().getAuthor()).isEqualTo("박둘리");
    }

    @Test
    @DisplayName("저자명으로 도서 목록 조회 테스트")
    void testFindByAuthor() {
        bookRepository.save(springBootBook());
        bookRepository.save(jpaBook());
        bookRepository.save(Book.builder()
                .title("스프링 부트 심화")
                .author("홍길동")
                .isbn("9788956746999")
                .price(28000)
                .publishDate(LocalDate.of(2025, 6, 1))
                .build());

        List<Book> books = bookRepository.findByAuthor("홍길동");

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getAuthor).containsOnly("홍길동");
    }

    @Test
    @DisplayName("도서 정보 수정 테스트")
    void testUpdateBook() {
        Book saved = bookRepository.save(springBootBook());

        Book target = bookRepository.findById(saved.getId()).orElseThrow();
        target.setPrice(32000);
        target.setTitle("스프링 부트 입문 (개정판)");
        bookRepository.save(target);

        Book updated = bookRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPrice()).isEqualTo(32000);
        assertThat(updated.getTitle()).isEqualTo("스프링 부트 입문 (개정판)");
    }

    @Test
    @DisplayName("도서 삭제 테스트")
    void testDeleteBook() {
        Book saved = bookRepository.save(springBootBook());
        Long id = saved.getId();

        bookRepository.deleteById(id);

        assertThat(bookRepository.findById(id)).isEmpty();
        assertThat(bookRepository.count()).isZero();
    }
}
