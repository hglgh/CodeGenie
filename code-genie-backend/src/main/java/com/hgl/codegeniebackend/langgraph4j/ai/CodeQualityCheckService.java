package com.hgl.codegeniebackend.langgraph4j.ai;

import com.hgl.codegeniebackend.langgraph4j.model.QualityResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @ClassName: CodeQualityCheckService
 * @Package: com.hgl.codegeniebackend.langgraph4j.ai
 * @Description:
 * @Author HGL
 * @Create: 2025/8/15 13:44
 */
public interface CodeQualityCheckService {

    /**
     * 检查代码质量
     * AI 会分析代码并返回质量检查结果
     */
    @SystemMessage(fromResource = "prompt/code-quality-check-system-prompt.txt")
    QualityResult checkCodeQuality(@UserMessage String codeContent);
}
