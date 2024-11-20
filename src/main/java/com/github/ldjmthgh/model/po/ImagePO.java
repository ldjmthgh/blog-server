package com.github.ldjmthgh.model.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "image_info")
public class ImagePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 图片名称 无要求 限长 50
     */
    @Column(name = "image_name", length = 50)
    private String imageName;

    /**
     * 图片类型 后缀
     */
    @Column(name = "image_type", length = 50)
    private String imageType;

    /**
     * 图片存储的绝对路径
     */
    @Column(name = "img_storage_url", length = 200)
    @JsonIgnore
    private String imageStorageUrl;

    /**
     * true ?  回收站 : 使用中  正数为true
     */
    @Column(name = "removed", length = 50)
    private Integer removed;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;
}
