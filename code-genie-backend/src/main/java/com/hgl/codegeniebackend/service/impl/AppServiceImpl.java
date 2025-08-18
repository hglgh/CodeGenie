package com.hgl.codegeniebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.ai.AiCodeGenTypeRoutingService;
import com.hgl.codegeniebackend.ai.AiCodeGenTypeRoutingServiceFactory;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.common.DeleteRequest;
import com.hgl.codegeniebackend.common.constant.AppConstant;
import com.hgl.codegeniebackend.common.constant.UserConstant;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.common.model.entity.App;
import com.hgl.codegeniebackend.common.model.entity.User;
import com.hgl.codegeniebackend.common.model.enums.ChatHistoryMessageTypeEnum;
import com.hgl.codegeniebackend.common.model.request.app.AppAddRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppAdminUpdateRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppQueryRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppUpdateRequest;
import com.hgl.codegeniebackend.common.model.vo.app.AppVO;
import com.hgl.codegeniebackend.common.model.vo.user.UserVO;
import com.hgl.codegeniebackend.core.AiCodeGeneratorFacadeEnhanced;
import com.hgl.codegeniebackend.core.builder.VueProjectBuilder;
import com.hgl.codegeniebackend.core.handler.StreamHandlerExecutor;
import com.hgl.codegeniebackend.mapper.AppMapper;
import com.hgl.codegeniebackend.service.*;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
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
@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacadeEnhanced aiCodeGeneratorFacadeEnhanced;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Resource
    private ScreenshotService screenshotService;

    @Resource
    private ProjectDownloadService projectDownloadService;

    @Resource
    private AiCodeGenTypeRoutingServiceFactory aiCodeGenTypeRoutingServiceFactory;

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
        // 5. 通过校验后，添加用户消息到对话历史
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
        // 6. 调用 AI 生成代码（流式）
        Flux<String> contentFlux = aiCodeGeneratorFacadeEnhanced.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        // 7. 收集AI响应内容并在完成后记录到对话历史
        return streamHandlerExecutor.doExecute(contentFlux, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        App app = this.getById(appId);
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "用户没有权限");
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 7. Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDirPath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录: {}", distDir.getAbsolutePath());
        }
        // 8. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }
        // 9. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 10. 构建应用访问 URL
        String appDeployUrl = String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
        // 11. 异步生成截图并更新应用封面
        generateAppScreenshotAsync(appId, appDeployUrl);
        return appDeployUrl;

    }

    @Override
    public long addApp(AppAddRequest appAddRequest, HttpServletRequest request) {
        // 参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化提示不能为空");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 构造入库对象
        // 使用 AI 智能选择代码生成类型（多例模式）
        AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService = aiCodeGenTypeRoutingServiceFactory.createAiCodeGenTypeRoutingService();
        CodeGenTypeEnum selectedCodeGenType = aiCodeGenTypeRoutingService.routeCodeGenType(initPrompt);
        App app = App.builder()
                .appName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)))
                .initPrompt(initPrompt)
                .codeGenType(selectedCodeGenType.getValue())
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
        boolean result = removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
        // 删除部署目录
        if (oldApp.getDeployKey() != null && !oldApp.getDeployKey().isEmpty()) {
            deleteAppDeploymentDirectory(oldApp);
        }
        return true;
    }

    /**
     * 删除应用的部署目录
     *
     * @param app 应用对象
     */
    private void deleteAppDeploymentDirectory(App app) {
        String deployDir = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + app.getDeployKey();
        Thread.ofVirtual().name(String.format("delete-app-%s", System.currentTimeMillis())).start(() -> {
            try {
                boolean existed = FileUtil.exist(deployDir);
                if (existed) {
                    log.info("删除部署目录: {}", deployDir);
                    boolean delled = FileUtil.del(deployDir);
                    if (!delled) {
                        log.error("删除部署目录失败: {}", deployDir);
                    }
                }
            } catch (IORuntimeException e) {
                log.error("删除部署目录时发生异常: {}", deployDir, e);
            }
        });
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

    @Override
    public void downloadAppCode(Long appId, HttpServletRequest request, HttpServletResponse response) {
        // 2. 查询应用信息
        App app = this.mapper.selectOneById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 权限校验：只有应用创建者可以下载代码
        User loginUser = userService.getLoginUser(request);
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限下载该应用代码");
        }
        // 4. 构建应用代码目录路径（生成目录，非部署目录）
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 5. 检查代码目录是否存在
        ThrowUtils.throwIf(!FileUtil.exist(sourceDirPath) && !FileUtil.isDirectory(sourceDirPath),
                ErrorCode.NOT_FOUND_ERROR, "应用代码不存在，请先生成代码");
        // 6. 生成下载文件名（不建议添加中文内容）
        String downloadFileName = String.valueOf(appId);
        // 7. 调用通用下载服务
        projectDownloadService.downloadProjectAsZip(sourceDirPath, downloadFileName, response);
    }

    /**
     * 删除应用时关联删除对话历史(重写方法)
     *
     * @param id 应用ID
     * @return 是否成功
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        long appId = Long.parseLong(id.toString());
        if (appId <= 0) {
            return false;
        }
        try {
            chatHistoryService.deleteByAppId(appId);
            log.info("删除应用ID:{}关联的对话历史成功", appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }

    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    public void generateAppScreenshotAsync(Long appId, String appUrl) {
        // 使用虚拟线程异步执行
        Thread.startVirtualThread(() -> {
            // 调用截图服务生成截图并上传
            String screenshotUrl = screenshotService.generateAndUploadScreenshot(appUrl);
            // 更新应用封面字段
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(screenshotUrl);
            boolean updateResult = updateById(updateApp);
            ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用封面字段失败");
        });
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
