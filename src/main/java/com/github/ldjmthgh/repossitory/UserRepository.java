package com.github.ldjmthgh.repossitory;

import com.github.ldjmthgh.model.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
@Repository
public interface UserRepository extends JpaRepository<UserPO, Long> {
//    @Query("select u from User u where u.userName = ?1")
    UserPO findByUserName(String userName);

//    @Query("select u from User u where u.email = ?1")
    UserPO findByEmail(String email);

//    @Query("select u from User u where u.userName = ?1 and u.userPassword = ?2")
    UserPO findByUserNameAndUserPassword(String userName, String pwd);
}
