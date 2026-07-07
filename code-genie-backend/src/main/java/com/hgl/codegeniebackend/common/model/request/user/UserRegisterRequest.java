package com.hgl.codegeniebackend.common.model.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度应为 4-20 个字符")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度应为 8-32 个字符")
    private String userPassword;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Size(min = 8, max = 32, message = "确认密码长度应为 8-32 个字符")
    private String checkPassword;
}

