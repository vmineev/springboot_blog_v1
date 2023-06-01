package me.minhee.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "author", nullable = false)
    private String author;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Builder //빌터 패턴으로 객체 생성, 객체 생성 코드에 가독성을 높인다
    public Article(String author,String title, String content){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // 특정 아이디의 글을 수정, 엔티티에 요청받은 내용으로 값을 수정하는 메서드
    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }



}
