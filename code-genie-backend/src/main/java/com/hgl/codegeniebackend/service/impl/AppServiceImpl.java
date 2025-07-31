package com.hgl.codegeniebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.common.DeleteRequest;
import com.hgl.codegeniebackend.common.constant.AppConstant;
import com.hgl.codegeniebackend.common.constant.UserConstant;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.common.model.entity.User;
import com.hgl.codegeniebackend.common.model.request.app.AppAddRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppAdminUpdateRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppQueryRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppUpdateRequest;
import com.hgl.codegeniebackend.common.model.vo.app.AppVO;
import com.hgl.codegeniebackend.common.model.vo.user.UserVO;
import com.hgl.codegeniebackend.core.AiCodeGeneratorFacadeEnhanced;
import com.hgl.codegeniebackend.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.hgl.codegeniebackend.common.model.entity.App;
import com.hgl.codegeniebackend.mapper.AppMapper;
import com.hgl.codegeniebackend.service.AppService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/hglgh">hgl</a>
 * @since 2025-07-31
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacadeEnhanced aiCodeGeneratorFacadeEnhanced;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户输入不能为空");
        // 2. 查询应用信息
        App app = this.mapper.selectOneById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "用户没有权限");
        // 4. 获取应用的代码生成类型
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(app.getCodeGenType());
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        return aiCodeGeneratorFacadeEnhanced.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
    }

    @Override
    public long addApp(AppAddRequest appAddRequest, HttpServletRequest request) {
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化提示不能为空");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        App app = App.builder()
                .appName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)))
                .initPrompt(initPrompt)
                .codeGenType(CodeGenTypeEnum.MULTI_FILE.getValue())
                .priority(AppConstant.DEFAULT_APP_PRIORITY)
                .userId(loginUser.getId()).build();
        boolean save = this.save(app);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "添加应用失败");
        return app.getId();
    }

    @Override
    public boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        Long id = appUpdateRequest.getId();
        String appName = appUpdateRequest.getAppName();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(appName), ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        App oldApp = this.mapper.selectOneById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 应用创建者才能操作
        ThrowUtils.throwIf(!oldApp.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "用户没有权限");
        App app = App.builder()
                .id(id)
                .appName(appName)
                .editTime(LocalDateTime.now())
                .build();
        int update = this.mapper.update(app);
        ThrowUtils.throwIf(update <= 0, ErrorCode.OPERATION_ERROR, "更新应用失败");
        return true;
    }

    @Override
    public boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        App oldApp = this.mapper.selectOneById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 管理员或者应用创建者才能操作
        ThrowUtils.throwIf(!oldApp.getUserId().equals(loginUser.getId()) && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole()), ErrorCode.NO_AUTH_ERROR, "用户没有权限");
        int delete = this.mapper.deleteById(id);
        ThrowUtils.throwIf(delete <= 0, ErrorCode.OPERATION_ERROR, "删除应用失败");
        return false;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVoMap = userService.listByIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtil.copyProperties(app, appVO);
            UserVO userVO = userVoMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public Page<AppVO> listMyAppVoByPage(AppQueryRequest appQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        int pageNum = appQueryRequest.getPageNum();
        // 限制每页最多 20 个
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        Page<App> page = new Page<>(pageNum, pageSize);
        // 只查询当前用户的应用
        appQueryRequest.setUserId(loginUser.getId());
        return getAppVoPage(appQueryRequest, pageSize, pageNum, page);
    }

    @Override
    public Page<AppVO> listGoodAppVoByPage(AppQueryRequest appQueryRequest) {
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        int pageNum = appQueryRequest.getPageNum();
        Page<App> page = new Page<>(pageNum, pageSize);
        // 只查询精选的应用
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        return getAppVoPage(appQueryRequest, pageSize, pageNum, page);
    }

    @Override
    public Boolean updateAppByAdmin(AppAdminUpdateRequest appAdminUpdateRequest) {
        Long id = appAdminUpdateRequest.getId();
        String appName = appAdminUpdateRequest.getAppName();
        String cover = appAdminUpdateRequest.getCover();
        Integer priority = appAdminUpdateRequest.getPriority();

        App oldApp = this.mapper.selectOneById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        App app = App.builder()
                .id(id)
                .appName(appName)
                .cover(cover)
                .priority(priority)
                // 设置编辑时间
                .editTime(LocalDateTime.now())
                .build();
        int update = this.mapper.update(app);
        ThrowUtils.throwIf(update <= 0, ErrorCode.OPERATION_ERROR, "更新应用失败");
        return true;
    }

    @Override
    public Page<AppVO> listAppVoByPageByAdmin(AppQueryRequest appQueryRequest) {
        int pageSize = appQueryRequest.getPageSize();
        int pageNum = appQueryRequest.getPageNum();
        Page<App> page = new Page<>(pageNum, pageSize);
        return getAppVoPage(appQueryRequest, pageSize, pageNum, page);
    }

    /**
     * 获取应用分页
     *
     * @param appQueryRequest 应用查询请求
     * @param pageSize        每页大小
     * @param pageNum         页码
     * @param page            分页对象
     * @return 分页结果
     */
    private Page<AppVO> getAppVoPage(AppQueryRequest appQueryRequest, int pageSize, int pageNum, Page<App> page) {
        Page<App> appPage = this.mapper.paginate(page, getQueryWrapper(appQueryRequest));
        List<AppVO> appVOList = getAppVOList(appPage.getRecords());
        Page<AppVO> appVoPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        appVoPage.setRecords(appVOList);
        return appVoPage;
    }

}
