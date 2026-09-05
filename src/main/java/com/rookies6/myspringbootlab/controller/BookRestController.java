package com.rookies6.myspringbootlab.controller;

import com.rookies6.myspringbootlab.entity.Book;
import com.rookies6.myspringbootlab.exception.BusinessException;
import com.rookies6.myspringbootlab.exception.ErrorCode;
import com.rookies6.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [SpringBoot 실습2-2] BookRestController
 *
 * BusinessException / ErrorObject / DefaultExceptionAdvice 는 그대로 재사용한다.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookRepository bookRepository;

    // POST /api/books : 새 도서 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(b -> {
            throw new BusinessException(ErrorCode.ISBN_DUPLICATE, book.getIsbn());
        });
        return bookRepository.save(book);
    }

    // GET /api/books : 모든 도서 조회
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // GET /api/books/{id} : ID로 특정 도서 조회
    // -> Optional 의 map() / orElse() 로 404 처리, 반환 타입 ResponseEntity<Book>
    @GetMapping("/{id}")
    public ResponseEntity<Book> getUserById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/books/isbn/{isbn} : ISBN으로 도서 조회
    // -> BusinessException + ErrorObject / DefaultExceptionAdvice 로 404 처리
    @GetMapping("/isbn/{isbn}")
    public Book getUserByIsbn(@PathVariable String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "Book", "isbn", isbn));
    }

    // PUT /api/books/{id} : 도서 정보 수정
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", String.valueOf(id)));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublishDate(request.getPublishDate());
        return bookRepository.save(book);
    }

    // DELETE /api/books/{id} : 도서 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", String.valueOf(id)));
        bookRepository.delete(book);
    }
}
