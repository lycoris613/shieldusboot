package com.rookies6.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * [제출2-4] 도서 상세 정보. 1:1 연관관계의 주인(외래 키 소유).
 */
@Entity
@Table(name = "book_details")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_detail_id")
    private Long id;

    @Column(length = 2000)
    private String description;

    private String language;

    private Integer pageCount;

    private String publisher;

    private String coverImageUrl;

    private String edition;

    // 관계의 주인 - 외래 키(book_id) + 유니크 제약. 지연 로딩.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true)
    private Book book;
}
