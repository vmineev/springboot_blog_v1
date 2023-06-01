package me.minhee.springbootdeveloper.dto;

import lombok.Getter;
import me.minhee.springbootdeveloper.domain.Article;

/**
 * 전체 글 목록을 조회하고 응답하는 dto
 */
@Getter
public class ArticleResponse {

    private final String title;
    private final String content;

    public ArticleResponse(Article article){ //엔티티를 파라미터로 받는 생성자
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
