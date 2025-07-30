package com.hgl.codegeniebackend.service;

import com.hgl.codegeniebackend.common.model.request.user.UserAddRequest;
import com.hgl.codegeniebackend.common.model.request.user.UserLoginRequest;
import com.hgl.codegeniebackend.common.model.request.user.UserQueryRequest;
import com.hgl.codegeniebackend.common.model.request.user.UserRegisterRequest;
import com.hgl.codegeniebackend.common.model.vo.user.LoginUserVO;
import com.hgl.codegeniebackend.common.model.vo.user.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.hgl.codegeniebackend.common.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/hglgh">hgl</a>
 * @since 2025-07-29
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册信息
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录信息
     * @param request          请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询条件
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取登录用户视图
     *
     * @param user 用户
     * @return 登录用户视图
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户 VO 列表
     *
     * @param userList 用户列表
     * @return 用户 VO 列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加请求
     * @return 用户 ID
     */
    Long addUser(UserAddRequest userAddRequest);
}
