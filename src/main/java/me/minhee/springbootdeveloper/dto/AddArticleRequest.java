package me.minhee.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.minhee.springbootdeveloper.domain.Article;

/**
 * dto 는 계층끼리 데이터를 교환하기 위해 사용하는 객체
 * 데이터를 옮기기 위해 사용하는 전달자 역할
 * dao 는 데이터베이스와 연결되고 데이터를 조회하고 수정하는데 사용하는 객체
 * 데이터 수정과 관련된 로직 포함
 */

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    //dto 를 entity 로 만들어주는 메서드 : 블로그 글을 추가할 때 저장할 엔티티로 변환하는 용도
    public Article toEntity(String author){ //빌더 패턴으로 객체 생성
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }

}
