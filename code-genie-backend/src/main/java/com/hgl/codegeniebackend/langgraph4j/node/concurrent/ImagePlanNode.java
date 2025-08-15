package com.hgl.codegeniebackend.langgraph4j.node.concurrent;

import com.hgl.codegeniebackend.common.utils.SpringContextUtil;
import com.hgl.codegeniebackend.langgraph4j.ai.ImageCollectionPlanService;
import com.hgl.codegeniebackend.langgraph4j.model.ImageCollectionPlan;
import com.hgl.codegeniebackend.langgraph4j.state.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @ClassName: ImagePlanNode
 * @Package: com.hgl.codegeniebackend.langgraph4j.node.concurrent
 * @Description: 图片计划节点：分析用户需求，生成图片收集计‌划，为并发执行做准备
 * @Author HGL
 * @Create: 2025/8/15 14:57
 */
@Slf4j
public class ImagePlanNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            String originalPrompt = context.getOriginalPrompt();
            try {
                // 获取图片收集计划服务
                ImageCollectionPlanService planService = SpringContextUtil.getBean(ImageCollectionPlanService.class);
                ImageCollectionPlan plan = planService.planImageCollection(originalPrompt);
                log.info("生成图片收集计划，准备启动并发分支");
                // 将计划存储到上下文中
                context.setImageCollectionPlan(plan);
                context.setCurrentStep("图片计划");
            } catch (Exception e) {
                log.error("图片计划生成失败: {}", e.getMessage(), e);
            }
            return WorkflowContext.saveContext(context);
        });
    }
}
