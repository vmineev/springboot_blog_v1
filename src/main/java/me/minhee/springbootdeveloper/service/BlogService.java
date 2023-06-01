package me.minhee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.domain.Article;
import me.minhee.springbootdeveloper.dto.AddArticleRequest;
import me.minhee.springbootdeveloper.dto.UpdateArticleRequest;
import me.minhee.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor //빈을 생성자로 생성 , final 이 붙거나 @NotNull 이 붙은 필드의 생성자를 만든다
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드 : AddArticleRequest class 에 저장된 값들을 article 데이터베이스에 저장
    public Article save(AddArticleRequest request, String userName){
        return blogRepository.save(request.toEntity(userName));
    }

    //블로그 글 전체 목록 조회 메서드 : 데이터베이스에 저장되어 있는 글을 모두 가져오기
    public List<Article> findAll(){return blogRepository.findAll();}

    //블로그 글 하나 조회
    public Article findById(long id){
        return blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found: " + id));
    }

    //글 삭제하기
    public  void delete(long id){
        Article article = blogRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }


    //글 수정하기
    @Transactional //매칭한 메서드를 하나의 트랜잭션으로 묶음, 트랜잭션은 데이터를 바꾸기 위해 묶은 작업의 단위
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    //게시글을 작성한 유저인지 확인하기
    private static  void authorizeArticleAuthor(Article article){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!article.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }
}
