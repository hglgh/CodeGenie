package com.hgl.codegeniebackend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.hgl.codegeniebackend.common.model.entity.App;
import com.hgl.codegeniebackend.mapper.AppMapper;
import com.hgl.codegeniebackend.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/hglgh">hgl</a>
 * @since 2025-07-31
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
