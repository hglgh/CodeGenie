package com.hgl.codegenie.innerservice;

import com.hgl.codegenie.model.entity.User;
import com.hgl.codegenie.model.vo.user.UserVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
}

