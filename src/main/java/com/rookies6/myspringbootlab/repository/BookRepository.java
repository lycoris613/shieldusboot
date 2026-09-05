package com.rookies6.myspringbootlab.repository;

import com.rookies6.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
