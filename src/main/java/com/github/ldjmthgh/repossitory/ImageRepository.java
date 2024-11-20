package com.github.ldjmthgh.repossitory;

import com.github.ldjmthgh.model.po.ImagePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
@Repository
public interface ImageRepository extends JpaRepository<ImagePO, Long> {
    ImagePO findByImageName(String imageName);

    Page<ImagePO> findByImageName(String imageName, Pageable pageable);
}
