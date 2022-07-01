package com.kmreport.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ModifyPasswordDTO
 *
 * @author wenmo
 * @since 2022/2/22 23:27
 */
@Getter
@Setter
public class ModifyPasswordDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 新密码
     */
    private String newPassword;
}
