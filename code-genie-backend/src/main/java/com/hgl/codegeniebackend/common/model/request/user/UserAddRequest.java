package com.hgl.codegeniebackend.common.model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: UserAddRequest
 * Package: com.hgl.codegeniebackend.common.model.request.user
 * Description: 用户创建请求(仅管理员可操作)
 *
 * @Author HGL
 * @Create: 2025/7/30 9:05
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}

