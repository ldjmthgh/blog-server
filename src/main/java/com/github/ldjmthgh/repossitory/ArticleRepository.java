package com.github.ldjmthgh.repossitory;

import com.github.ldjmthgh.model.po.ArticlePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
@Repository
public interface ArticleRepository extends JpaRepository<ArticlePO, Long> {
    ArticlePO findByArticleName(String articleName);

    Page<ArticlePO> findByArticleName(String articleName, Pageable pageable);

    Page<ArticlePO> findByPublished(Integer published, Pageable pageable);

    Page<ArticlePO> findByArticleNameAndPublished(String articleName, Integer published, Pageable pageable);
}
