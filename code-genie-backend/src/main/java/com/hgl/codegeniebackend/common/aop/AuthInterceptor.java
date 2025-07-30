package com.hgl.codegeniebackend.common.aop;

import cn.hutool.core.util.ObjUtil;
import com.hgl.codegeniebackend.common.annotation.AuthCheck;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.common.model.entity.User;
import com.hgl.codegeniebackend.common.model.enums.UserRoleEnum;
import com.hgl.codegeniebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ClassName: AuthInterceptor
 * Package: com.hgl.codegeniebackend.common.aop
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 8:43
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 1.获取httpRequest 对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 2.获取所要的权限角色
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 3.判断权限
        // 不需要权限，放行
        if (ObjUtil.isEmpty(mustRoleEnum)) {
            return joinPoint.proceed();
        }

        // 需要权限，判断用户权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        // 用户权限不存在
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NO_AUTH_ERROR);

        // 用户权限小于目标权限
        if (!hasPermission(userRoleEnum, mustRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return joinPoint.proceed();
    }

    /**
     * 判断用户是否有权限访问目标资源
     *
     * @param userRole 用户角色
     * @param mustRole 所需角色
     * @return 是否有权限
     */
    private boolean hasPermission(UserRoleEnum userRole, UserRoleEnum mustRole) {
        // 管理员拥有所有权限
        if (UserRoleEnum.ADMIN.equals(userRole)) {
            return true;
        }

        // 用户只能访问用户级别的资源
        return UserRoleEnum.USER.equals(mustRole) && UserRoleEnum.USER.equals(userRole);
    }
}
