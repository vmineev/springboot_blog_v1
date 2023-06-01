package me.minhee.springbootdeveloper.repository;

import me.minhee.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BlogRepository extends JpaRepository<Article,Long> {
}
