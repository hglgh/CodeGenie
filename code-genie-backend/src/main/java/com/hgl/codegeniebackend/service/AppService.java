package com.hgl.codegeniebackend.service;

import com.hgl.codegeniebackend.common.DeleteRequest;
import com.hgl.codegeniebackend.common.model.entity.User;
import com.hgl.codegeniebackend.common.model.request.app.AppAddRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppAdminUpdateRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppQueryRequest;
import com.hgl.codegeniebackend.common.model.request.app.AppUpdateRequest;
import com.hgl.codegeniebackend.common.model.vo.app.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.hgl.codegeniebackend.common.model.entity.App;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/hglgh">hgl</a>
 * @since 2025-07-31
 */
public interface AppService extends IService<App> {

    /**
     * 应用聊天生成代码（流式 SSE）
     *
     * @param appId     应用ID
     * @param message   消息
     * @param loginUser 登录用户
     * @return 代码
     */
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 添加应用
     *
     * @param appAddRequest 请求参数
     * @param request       请求
     * @return 应用ID
     */
    long addApp(AppAddRequest appAddRequest, HttpServletRequest request);

    /**
     * 更新应用
     *
     * @param appUpdateRequest 请求参数
     * @param request          请求
     * @return 是否成功
     */
    boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    /**
     * 删除应用
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 是否成功
     */
    boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 获取应用列表
     *
     * @param appList 应用列表
     * @return 应用列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取脱敏后的应用信息
     *
     * @param app 应用
     * @return 应用信息
     */
    AppVO getAppVO(App app);

    /**
     * 获取当前用户的应用分页
     *
     * @param appQueryRequest 查询参数
     * @param request         请求
     * @return 应用分页
     */
    Page<AppVO> listMyAppVoByPage(AppQueryRequest appQueryRequest, HttpServletRequest request);

    /**
     * 分页获取精选应用列表
     *
     * @param appQueryRequest 查询参数
     * @return 应用分页
     */
    Page<AppVO> listGoodAppVoByPage(AppQueryRequest appQueryRequest);

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 删除请求
     * @return 是否成功
     */
    Boolean updateAppByAdmin(AppAdminUpdateRequest appAdminUpdateRequest);

    /**
     * 管理员获取应用分页
     *
     * @param appQueryRequest 删除请求
     * @return 应用分页
     */
    Page<AppVO> listAppVoByPageByAdmin(AppQueryRequest appQueryRequest);
}
