package com.kmreport.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kmreport.common.Asserts;
import com.kmreport.common.result.Result;
import com.kmreport.mapper.UserMapper;
import com.kmreport.model.User;
import com.kmreport.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yanko24
 * @since 2022-07-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result modifyPassword(String username, String password, String newPassword) {
        User user = getUserByUsername(username);
        if (Asserts.isNull(user)) {
            return Result.failed("该账号不存在");
        }
        if (!Asserts.isEquals(SaSecureUtil.md5(password), user.getPassword())) {
            return Result.failed("原密码错误");
        }
        user.setPassword(SaSecureUtil.md5(newPassword));
        if (updateById(user)) {
            return Result.succeed("密码修改成功");
        }
        return Result.failed("密码修改失败");
    }

    @Override
    public User getUserByUsername(String username) {
        User user = getOne(new QueryWrapper<User>().eq("username", username).eq("is_delete", 0));
        if (Asserts.isNotNull(user)) {
            user.setIsAdmin(Asserts.isEqualsIgnoreCase(username, "admin"));
        }
        return user;
    }
}
