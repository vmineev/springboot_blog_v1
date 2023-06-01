package me.minhee.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.domain.Article;
import me.minhee.springbootdeveloper.dto.AddArticleRequest;
import me.minhee.springbootdeveloper.dto.ArticleResponse;
import me.minhee.springbootdeveloper.dto.UpdateArticleRequest;
import me.minhee.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


/**
 * 컨트롤러 메서드에는 URL 매핑 애너테이션을 사용 : @GetMapping, @PostMapping etc.
 *  각 메서드는 HTTP 메서드에 대응
 */
@RequiredArgsConstructor
@RestController //HTTP Response Body 에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;

    //HTTP 메서드가 POST 일 때 전달받은 URL 와 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    //요청 본문 값 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal){
        //Principal : 현재 인증 정보를 가져오는 객체
        Article savedArticle = blogService.save(request, principal.getName());
        //요청한 자원이 성공적으로 생성되었으면 저장된 블로그 글 정보를 응답 객체에 담아 전송
        //201 Created : 요청이 성공적으로 수행되었고, 새로운 리소스가 생성되었음
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        //글 전체를 조회하는 findAll() 호출한 다음 응답용 객체인 ArticleResponse로 파싱해 body에 담아 클라이언트에게 전송
        //stream()으로 Article 객체를 ArticleResponse 객체로 변환
        List<ArticleResponse> articles = blogService.findAll()
                .stream() // 여러 데이터가 모여있는 컬렉션을 간편하게 처리하는 기능
                .map(ArticleResponse::new) //ArticleResponse 클래스의 생성자를 참조하는 메소드 참조
                .toList();
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}")
    //URL 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        blogService.delete(id);
    return ResponseEntity.ok().build();}

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updatedArticle);
    }



}
