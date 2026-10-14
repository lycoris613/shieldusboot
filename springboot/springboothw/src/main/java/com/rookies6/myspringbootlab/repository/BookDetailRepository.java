package com.rookies6.myspringbootlab.repository;

import com.rookies6.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//BookDetailRepository 인터페이스 [제출2-4]
@Repository
public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {

    // 특정 책의 상세 정보 조회
    Optional<BookDetail> findByBookId(Long bookId);

    // 상세 정보 + 책 정보 함께 로딩 (Fetch Join)
    @Query("SELECT bd FROM BookDetail bd JOIN FETCH bd.book WHERE bd.id = :id")
    Optional<BookDetail> findByIdWithBook(@Param("id") Long id);

    // 특정 출판사의 책 상세 정보 목록
    List<BookDetail> findByPublisher(String publisher);
}
