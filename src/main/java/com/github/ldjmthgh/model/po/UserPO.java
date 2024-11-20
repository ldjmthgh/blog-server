package com.github.ldjmthgh.model.po;

import com.github.ldjmthgh.model.dto.UserDTO;
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
@Table(name = "user_info")
public class UserPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户名 唯一
     */
    @Column(name = "user_name", length = 50)
    private String userName;
    /**
     * 密码  不对称加密后入库
     */
    @Column(name = "user_password", length = 50)
    private String userPassword;
    /**
     * 头像存储地址
     */
    @Column(name = "avatar_storage_url", length = 50)
    private String avatarStorageUrl;
    /**
     * 用户昵称
     */
    @Column(name = "nick_name", length = 50)
    private String nickName;
    /**
     * 用户 email 可用于召回密码
     */
    @Column(name = "email", length = 50)
    private String email;
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

    public UserPO(UserDTO dto) {
        this.userName = dto.getUserName();
        this.userPassword = dto.getUserPassword();
        this.nickName = dto.getNickName();
        this.email = dto.getEmail();
    }
}
