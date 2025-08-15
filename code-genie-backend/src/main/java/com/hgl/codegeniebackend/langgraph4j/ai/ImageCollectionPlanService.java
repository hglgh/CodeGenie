package com.hgl.codegeniebackend.langgraph4j.ai;

import com.hgl.codegeniebackend.langgraph4j.model.ImageCollectionPlan;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @ClassName: ImageCollectionPlanService
 * @Package: com.hgl.codegeniebackend.langgraph4j.ai
 * @Description: 图片收集计划服务
 * @Author HGL
 * @Create: 2025/8/15 14:35
 */
public interface ImageCollectionPlanService {

    /**
     * 根据用户提示词分析需要收集的图片类型和参数
     */
    @SystemMessage(fromResource = "prompt/image-collection-plan-system-prompt.txt")
    ImageCollectionPlan planImageCollection(@UserMessage String userPrompt);
}

