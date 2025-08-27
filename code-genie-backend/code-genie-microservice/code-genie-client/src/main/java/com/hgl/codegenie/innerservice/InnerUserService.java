package com.hgl.codegenie.innerservice;

import com.hgl.codegenie.common.exception.BusinessException;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.model.entity.User;
import com.hgl.codegenie.model.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.hgl.codegenie.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @ClassName: InnerUserService
 * @Package: com.hgl.codegenie.innerservice
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 14:25
 */
public interface InnerUserService {

    List<User> listByIds(Collection<? extends Serializable> ids);

    User getById(Serializable id);

    UserVO getUserVO(User user);

    // 静态方法，避免跨服务调用
    static User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
}

