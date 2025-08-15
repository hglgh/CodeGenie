package com.hgl.codegeniebackend.langgraph4j.node.concurrent;

import com.hgl.codegeniebackend.common.utils.SpringContextUtil;
import com.hgl.codegeniebackend.langgraph4j.model.ImageCollectionPlan;
import com.hgl.codegeniebackend.langgraph4j.model.ImageResource;
import com.hgl.codegeniebackend.langgraph4j.state.WorkflowContext;
import com.hgl.codegeniebackend.langgraph4j.tools.LogoGeneratorTool;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.ArrayList;
import java.util.List;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @ClassName: LogoCollectorNode
 * @Package: com.hgl.codegeniebackend.langgraph4j.node.concurrent
 * @Description: Logo 生成节点
 * @Author HGL
 * @Create: 2025/8/15 15:00
 */
@Slf4j
public class LogoCollectorNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            List<ImageResource> logos = new ArrayList<>();
            try {
                ImageCollectionPlan plan = context.getImageCollectionPlan();
                if (plan != null && plan.getLogoTasks() != null) {
                    LogoGeneratorTool logoTool = SpringContextUtil.getBean(LogoGeneratorTool.class);
                    log.info("开始并发生成Logo，任务数: {}", plan.getLogoTasks().size());
                    for (ImageCollectionPlan.LogoTask task : plan.getLogoTasks()) {
                        List<ImageResource> images = logoTool.generateLogos(task.description());
                        if (images != null) {
                            logos.addAll(images);
                        }
                    }
                    log.info("Logo生成完成，共生成 {} 张图片", logos.size());
                }
            } catch (Exception e) {
                log.error("Logo生成失败: {}", e.getMessage(), e);
            }
            context.setLogos(logos);
            context.setCurrentStep("Logo生成");
            return WorkflowContext.saveContext(context);
        });
    }
}
