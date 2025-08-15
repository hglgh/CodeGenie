package com.hgl.codegeniebackend.langgraph4j.node;

import com.hgl.codegeniebackend.langgraph4j.ai.ImageCollectionService;
import com.hgl.codegeniebackend.langgraph4j.state.WorkflowContext;
import com.hgl.codegeniebackend.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @ClassName: ImageCollectorNode
 * @Package: com.hgl.codegeniebackend.langgraph4j.node
 * @Description: 图片收集节点
 * @Author HGL
 * @Create: 2025/8/14 15:08
 */
@Slf4j
public class ImageCollectorNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: 图片收集");
            String originalPrompt = context.getOriginalPrompt();
            String imageListStr = "";
            try {
                // 获取AI图片收集服务
                ImageCollectionService imageCollectionService = SpringContextUtil.getBean(ImageCollectionService.class);
                // 使用 AI 服务进行智能图片收集
                imageListStr = imageCollectionService.collectImages(originalPrompt);
            } catch (Exception e) {
                log.error("图片收集失败: {}", e.getMessage(), e);
            }

            // 更新状态
            context.setCurrentStep("图片收集");
            context.setImageListStr(imageListStr);
            log.info("图片收集完成，图片列表: {}", imageListStr);
            return WorkflowContext.saveContext(context);
        });
    }
}

