package com.kmreport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kmreport.common.result.Result;
import com.kmreport.model.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Yanko24
 * @since 2022-07-01
 */
public interface UserService extends IService<User> {
    Result modifyPassword(String username, String password, String newPassword);

    User getUserByUsername(String username);
}
