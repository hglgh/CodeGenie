package com.hgl.codegeniebackend.common.model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: UserUpdateRequest
 * Package: com.hgl.codegeniebackend.common.model.request.user
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 9:06
 */
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}

