package com.hgl.codegenie.controller;

import com.hgl.codegenie.common.BaseResponse;
import com.hgl.codegenie.common.ResultUtils;
import com.hgl.codegenie.common.annotation.AuthCheck;
import com.hgl.codegenie.common.constant.UserConstant;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.common.exception.ThrowUtils;
import com.hgl.codegenie.innerservice.InnerUserService;
import com.hgl.codegenie.model.entity.ChatHistory;
import com.hgl.codegenie.model.entity.User;
import com.hgl.codegenie.model.request.chathistory.ChatHistoryQueryRequest;
import com.hgl.codegenie.service.ChatHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 对话历史 控制层。
 *
 * @author <a href="https://github.com/hglgh">hgl</a>
 * @since 2025-08-01
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;

    @DubboReference
    private InnerUserService userService;

    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param request        请求
     * @return 对话历史分页
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(
            @PathVariable Long appId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) LocalDateTime lastCreateTime,
            HttpServletRequest request
    ) {
        User loginUser = InnerUserService.getLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询所有对话历史
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 对话历史分页
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryByPageForAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = chatHistoryQueryRequest.getPageNum();
        long pageSize = chatHistoryQueryRequest.getPageSize();
        // 查询数据
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> result = chatHistoryService.page(Page.of(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(result);
    }

    /**
     * 删除对话历史
     *
     * @param id 对话历史ID
     * @return 删除结果
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteChatHistory(@RequestBody Long id) {
        return ResultUtils.success(chatHistoryService.removeById(id));
    }
}
