package com.rookies6.myspringbootlab.service;

import com.rookies6.myspringbootlab.controller.dto.BookDTO;
import com.rookies6.myspringbootlab.entity.Book;
import com.rookies6.myspringbootlab.entity.BookDetail;
import com.rookies6.myspringbootlab.exception.BusinessException;
import com.rookies6.myspringbootlab.exception.ErrorCode;
import com.rookies6.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [제출2-4] 도서 비즈니스 로직 계층 (Book ↔ BookDetail 1:1).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDTO.Response> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    public BookDTO.Response getBookById(Long id) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", id));
        return BookDTO.Response.fromEntity(book);
    }

    public BookDTO.Response getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "isbn", isbn));
        return BookDTO.Response.fromEntity(book);
    }

    public List<BookDTO.Response> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    public List<BookDTO.Response> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    @Transactional
    public BookDTO.Response createBook(BookDTO.Request request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.ISBN_DUPLICATE, request.getIsbn());
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publishDate(request.getPublishDate())
                .build();

        if (request.getDetailRequest() != null) {
            book.setBookDetail(toDetailEntity(request.getDetailRequest()));
        }

        return BookDTO.Response.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", id));

        // ISBN 을 바꾸는 경우에만 중복 검사
        if (!book.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.ISBN_DUPLICATE, request.getIsbn());
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublishDate(request.getPublishDate());

        // 상세 정보: 있으면 갱신, 없으면 새로 연결
        BookDTO.BookDetailDTO detailRequest = request.getDetailRequest();
        if (detailRequest != null) {
            BookDetail detail = book.getBookDetail();
            if (detail == null) {
                book.setBookDetail(toDetailEntity(detailRequest));
            } else {
                detail.setDescription(detailRequest.getDescription());
                detail.setLanguage(detailRequest.getLanguage());
                detail.setPageCount(detailRequest.getPageCount());
                detail.setPublisher(detailRequest.getPublisher());
                detail.setCoverImageUrl(detailRequest.getCoverImageUrl());
                detail.setEdition(detailRequest.getEdition());
            }
        }

        return BookDTO.Response.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", id);
        }
        // Book 삭제 시 CascadeType.ALL 로 BookDetail 도 함께 삭제
        bookRepository.deleteById(id);
    }

    private BookDetail toDetailEntity(BookDTO.BookDetailDTO dto) {
        return BookDetail.builder()
                .description(dto.getDescription())
                .language(dto.getLanguage())
                .pageCount(dto.getPageCount())
                .publisher(dto.getPublisher())
                .coverImageUrl(dto.getCoverImageUrl())
                .edition(dto.getEdition())
                .build();
    }
}
