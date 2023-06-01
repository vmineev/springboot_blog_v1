package me.minhee.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.minhee.springbootdeveloper.domain.Article;
import me.minhee.springbootdeveloper.dto.AddArticleRequest;
import me.minhee.springbootdeveloper.dto.UpdateArticleRequest;
import me.minhee.springbootdeveloper.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; // 자바 객체를 JSON 데이터로 변환하는 직렬화, JSON 데이터를 자바 객체로 변환하는 역직렬화를 위한 클래스


    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach //테스트 실행 전 실행하는 메서드
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception{
        /**
         * given : 준비과정, 블로그 글 추가에 필요한 요청 객체 만든다.
         * when : 블로그 글 추가 API 에 요청을 보낸다. 요청 타입 JSON, given 절에서 미리 만들어둔 객체를 요청 분문으로 함께 보낸다.
         * then : 응답 코드가 201 Created 인지 확인, 블로그를 전체 조회, 크기 1 인지 확인, 실제 저장 데이터와 요청값 비교
         */

        //given
        final String url ="/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title,content);

        //객체 JSON 으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);


        //when
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1) ; //블로그 글의 개수가 1 인지 검증
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);

    }

    /**
     * given : 블로그 글을 저장
     * when : 목록 조회 API 호출
     * then : 응답코드 200 OK, 반환받은 값 중에 0번째 요소의 content, title 이 저장된 값과 같은지 확인
     */

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception{
        //given
        final String url ="/api/articles";
        final String title = "title";
        final String content = "content";

        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    /**
     * give: 블로그 글 저장
     * when: 저장한 블로그 글의 id 값으로 API 호출
     * then: 응답 코드 200 ok, 반환받은 content, title 이 저장된 값과 같은지 확인
     */

    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception{
        //given
        final String url ="/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());
        //when
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));

    }

    /**
     * given : 블로그 글 저장
     * when : 저장한 블로그 글의 id 값으로 삭제 API 호출
     * then : 응답 코드 200 ok, 블로그 글 리스트를 전체 조회한 배열 크기가 0인지 확인
     */

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception{
        //given
        final String url ="/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when
        mockMvc.perform(delete(url,savedArticle.getId()))
                .andExpect(status().isOk());

        //then
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    /**
     * given: 블로그 글 저장, 수정에 필요한 요청 객체 만들기
     * when: update API로 수정 요청
     * then: 응답 코드 200 ok, 블로그 글 id로 조회한 후에 값이 수정되었는지 확인
     */

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception{
        //given
        final String url ="/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle,newContent);

        //when
        ResultActions result = mockMvc.perform(put(url,savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        //then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }
}