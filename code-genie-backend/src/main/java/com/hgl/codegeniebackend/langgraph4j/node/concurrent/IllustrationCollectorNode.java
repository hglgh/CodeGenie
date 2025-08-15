package com.hgl.codegeniebackend.langgraph4j.node.concurrent;

import com.hgl.codegeniebackend.common.utils.SpringContextUtil;
import com.hgl.codegeniebackend.langgraph4j.model.ImageCollectionPlan;
import com.hgl.codegeniebackend.langgraph4j.model.ImageResource;
import com.hgl.codegeniebackend.langgraph4j.state.WorkflowContext;
import com.hgl.codegeniebackend.langgraph4j.tools.UndrawIllustrationTool;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.ArrayList;
import java.util.List;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @ClassName: IllustrationCollectorNode
 * @Package: com.hgl.codegeniebackend.langgraph4j.node.concurrent
 * @Description: 插画图片收集节点
 * @Author HGL
 * @Create: 2025/8/15 14:59
 */
@Slf4j
public class IllustrationCollectorNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            List<ImageResource> illustrations = new ArrayList<>();
            try {
                ImageCollectionPlan plan = context.getImageCollectionPlan();
                if (plan != null && plan.getIllustrationTasks() != null) {
                    UndrawIllustrationTool illustrationTool = SpringContextUtil.getBean(UndrawIllustrationTool.class);
                    log.info("开始并发收集插画图片，任务数: {}", plan.getIllustrationTasks().size());
                    for (ImageCollectionPlan.IllustrationTask task : plan.getIllustrationTasks()) {
                        List<ImageResource> images = illustrationTool.searchIllustrations(task.query());
                        if (images != null) {
                            illustrations.addAll(images);
                        }
                    }
                    log.info("插画图片收集完成，共收集到 {} 张图片", illustrations.size());
                }
            } catch (Exception e) {
                log.error("插画图片收集失败: {}", e.getMessage(), e);
            }
            context.setIllustrations(illustrations);
            context.setCurrentStep("插画图片收集");
            return WorkflowContext.saveContext(context);
        });
    }
}

