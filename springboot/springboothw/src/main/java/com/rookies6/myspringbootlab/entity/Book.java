package com.rookies6.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

//Book
@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    private Integer price;

    // [제출2-4] 1:1 - Book 은 관계의 주인이 아님(mappedBy). 저장/삭제 시 BookDetail 도 함께 처리(Cascade).
    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BookDetail bookDetail;

    /** 양방향 연관관계 편의 메서드 - 양쪽 참조를 함께 맞춘다 */
    public void setBookDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
        if (bookDetail != null) {
            bookDetail.setBook(this);
        }
    }
}
