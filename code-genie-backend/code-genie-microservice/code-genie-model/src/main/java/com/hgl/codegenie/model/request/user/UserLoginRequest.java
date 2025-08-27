package com.hgl.codegenie.model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: UserLoginRequest
 * Package: com.hgl.codegeniebackend.common.model.request.user
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/29 17:46
 */
@Data
public class UserLoginRequest implements Serializable {

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
}

