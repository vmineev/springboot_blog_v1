package me.minhee.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.domain.Article;
import me.minhee.springbootdeveloper.dto.ArticleListViewResponse;
import me.minhee.springbootdeveloper.dto.ArticleViewResponse;
import me.minhee.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles); //articles 키에 글 리스트 저장
        return "articleList";
    }

    //블로그 글 반환하는 메서드
    @GetMapping("/article/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));
        return "article";
    }


    @GetMapping("/new-article")
    //id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑 (id는 없을 수도 있음)
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if (id == null) { //id가 없으면 새글 생성
            model.addAttribute("article", new ArticleViewResponse());
        } else {  //있으면 수정
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";

    }
}
