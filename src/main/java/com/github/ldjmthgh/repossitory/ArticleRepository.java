package com.github.ldjmthgh.repossitory;

import com.github.ldjmthgh.model.po.ArticlePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
@Repository
public interface ArticleRepository extends JpaRepository<ArticlePO, Long> {
}
