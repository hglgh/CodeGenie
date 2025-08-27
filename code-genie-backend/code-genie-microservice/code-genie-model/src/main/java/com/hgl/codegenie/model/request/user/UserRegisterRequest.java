package com.hgl.codegenie.model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: UserRegisterRequest
 * Package: com.hgl.codegeniebackend.common.model.request.user
 * Description: 用户注册请求
 *
 * @Author HGL
 * @Create: 2025/7/29 16:38
 */
@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}

