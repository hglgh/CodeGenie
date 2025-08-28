package com.hgl.codegenie.service.impl;

import com.hgl.codegenie.innerservice.InnerUserService;
import com.hgl.codegenie.model.entity.User;
import com.hgl.codegenie.model.vo.user.UserVO;
import com.hgl.codegenie.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName: InnerUserServiceImpl
 * @Package: com.hgl.codegenie.service.impl
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 17:28
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public List<User> listByIds(Collection<? extends Serializable> ids) {
        return userService.listByIds(ids);
    }

    @Override
    public User getById(Serializable id) {
        return userService.getById(id);
    }

    @Override
    public UserVO getUserVO(User user) {
        return userService.getUserVO(user);
    }
}

