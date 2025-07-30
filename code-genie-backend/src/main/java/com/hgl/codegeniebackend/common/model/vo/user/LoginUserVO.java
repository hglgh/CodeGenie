package com.hgl.codegeniebackend.common.model.vo.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.common.model.entity.User;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: LoginUserVO
 * Package: com.hgl.codegeniebackend.common.model.vo.user
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/29 17:36
 */
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;

}

