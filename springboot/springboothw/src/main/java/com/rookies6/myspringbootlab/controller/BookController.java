package com.rookies6.myspringbootlab.controller;

import com.rookies6.myspringbootlab.controller.dto.BookDTO;
import com.rookies6.myspringbootlab.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [제출2-4] Book ↔ BookDetail (1:1) REST 컨트롤러.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // GET /api/books : 모든 책 조회
    @GetMapping
    public ResponseEntity<List<BookDTO.Response>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // GET /api/books/{id} : ID로 책 조회
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO.Response> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // GET /api/books/isbn/{isbn} : ISBN으로 책 조회
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.Response> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    // GET /api/books/search/author?author={author} : 저자로 책 검색
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO.Response>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(author));
    }

    // GET /api/books/search/title?title={title} : 제목으로 책 검색
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO.Response>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    // POST /api/books : 책 생성 (상세 정보 포함 가능)
    @PostMapping
    public ResponseEntity<BookDTO.Response> createBook(@Valid @RequestBody BookDTO.Request request) {
        return new ResponseEntity<>(bookService.createBook(request), HttpStatus.CREATED);
    }

    // PUT /api/books/{id} : 책 수정 (전체 수정)
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO.Response> updateBook(@PathVariable Long id,
                                                      @Valid @RequestBody BookDTO.Request request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    // DELETE /api/books/{id} : 책 삭제 (BookDetail 도 Cascade 삭제)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
