package com.github.ldjmthgh.model.vo;

import com.github.ldjmthgh.model.po.UserPO;
import com.github.ldjmthgh.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserVO {
    private Long id;
    /**
     * 用户名 唯一
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;


    public UserVO(UserPO po) {
        this.id = po.getId();
        this.userName = po.getUserName();
        this.nickName = po.getNickName();
        this.createTime = DateUtil.simpleDateFormat(po.getCreateTime());
        this.updateTime = DateUtil.simpleDateFormat(po.getUpdateTime());
    }
}
