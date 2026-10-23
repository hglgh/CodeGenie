package com.hgl.codegenie.innerservice;

import com.hgl.codegenie.common.exception.BusinessException;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import static com.hgl.codegenie.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户会话工具类，从 Session 中获取登录用户信息（本地调用，不走 RPC）
 *
 * @author hgl
 */
public class UserSessionUtils {

    private UserSessionUtils() {
    }

    /**
     * 获取当前登录用户
     *
     * @param request HTTP 请求
     * @return 当前登录用户
     */
    public static User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
}
