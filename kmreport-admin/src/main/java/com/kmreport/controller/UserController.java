package com.kmreport.controller;

import com.kmreport.common.result.Result;
import com.kmreport.dto.ModifyPasswordDTO;
import com.kmreport.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yanko24
 * @since 2022-07-01
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(tags = "User接口")
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "根据用户名修改用户密码")
    @PostMapping("/modifyPassword")
    public Result modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return userService.modifyPassword(modifyPasswordDTO.getUsername(), modifyPasswordDTO.getPassword(),
                modifyPasswordDTO.getNewPassword());
    }
}
