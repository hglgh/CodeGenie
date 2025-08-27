package com.hgl.codegenie.model.request.user;

import com.hgl.codegenie.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: UserQueryRequest
 * Package: com.hgl.codegeniebackend.common.model.request.user
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 9:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}

