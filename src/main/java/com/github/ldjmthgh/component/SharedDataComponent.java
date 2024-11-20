package com.github.ldjmthgh.component;

import com.github.ldjmthgh.model.SharedDataInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
@Component
public class SharedDataComponent {
    @Bean
    public SharedDataInfo initCache() {
        return new SharedDataInfo();
    }
}
