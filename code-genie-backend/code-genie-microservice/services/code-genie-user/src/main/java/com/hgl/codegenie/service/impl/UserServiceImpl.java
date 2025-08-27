package com.hgl.codegenie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.hgl.codegenie.common.exception.BusinessException;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.common.exception.ThrowUtils;
import com.hgl.codegenie.model.entity.User;
import com.hgl.codegenie.model.enums.UserRoleEnum;
import com.hgl.codegenie.model.request.user.UserAddRequest;
import com.hgl.codegenie.model.request.user.UserLoginRequest;
import com.hgl.codegenie.model.request.user.UserQueryRequest;
import com.hgl.codegenie.model.request.user.UserRegisterRequest;
import com.hgl.codegenie.model.vo.user.LoginUserVO;
import com.hgl.codegenie.model.vo.user.UserVO;
import com.hgl.codegenie.mapper.UserMapper;
import com.hgl.codegenie.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryWrapperAdapter;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hgl.codegenie.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/hglgh">hgl</a>
 * @since 2025-07-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        //1.校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号过短");
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码过短");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        //2.检查账号是否重复
        long account = this.mapper.selectCountByQuery(new QueryWrapperAdapter<>().eq("userAccount", userAccount));
        ThrowUtils.throwIf(account > 0, ErrorCode.PARAMS_ERROR, "账号重复");
        //3.加密
        String encryptPassword = getEncryptPassword(userPassword);
        //4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("用户");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1.校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号错误");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码错误");
        // 2.密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        User user = this.mapper.selectOneByQuery(new QueryWrapperAdapter<>().eq("userAccount", userAccount).eq("userPassword", encryptPassword));
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR, "用户不存在或密码错误");
        // 3.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 4.返回用户脱敏信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (ObjUtil.isNull(user)) {
            return null;
        }
        return BeanUtil.copyProperties(user, LoginUserVO.class);
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String defaultPassword = "12345678";
        String encryptPassword = this.getEncryptPassword(defaultPassword);
        user.setUserPassword(encryptPassword);
        int insert = this.mapper.insert(user);
        ThrowUtils.throwIf(insert <= 0, ErrorCode.OPERATION_ERROR);
        return user.getId();
    }


    private String getEncryptPassword(String userPassword) {
        final String salt = "hgl";
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
    }
}
