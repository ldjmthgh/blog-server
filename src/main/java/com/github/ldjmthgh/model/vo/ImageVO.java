package com.github.ldjmthgh.model.vo;

import com.github.ldjmthgh.model.po.ImagePO;
import com.github.ldjmthgh.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ImageVO {
    private Long id;

    private String imageName;

    private String imageType;

    private String createTime;

    private String updateTime;


    public ImageVO(ImagePO po) {
        this.id = po.getId();
        this.imageName = po.getImageName();
        this.imageType = po.getImageType();

        this.createTime = DateUtil.simpleDateFormat(po.getCreateTime());
        this.updateTime = DateUtil.simpleDateFormat(po.getUpdateTime());
    }
}
