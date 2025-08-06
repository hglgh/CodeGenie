package com.hgl.codegeniebackend.core.elegance.parserenhanced;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.common.constant.CodeGenTypeConstant;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ClassName: CodeParserManager
 * Package: com.hgl.codegeniebackend.core.parser
 * Description: 代码解析器管理器，基于Spring IOC管理策略类
 *
 * @Author HGL
 * @Create: 2025/8/6 10:07
 */
@Slf4j
@Component
public class CodeParserManager {

    @Resource
    private Map<String, CodeParser<?>> parserMap;

    /**
     * 执行代码解析
     *
     * @param codeContent 代码内容
     * @param codeGenType 代码生成类型
     * @return 解析结果
     */
    public Object executeParser(String codeContent, CodeGenTypeEnum codeGenType) {
        // 构建Bean名称
        String beanName = CodeGenTypeConstant.buildParserBeanName(codeGenType.getValue());
        CodeParser<?> parser = parserMap.get(beanName);
        if (parser == null) {
            log.error("找不到代码生成类型 {} 对应的解析器，支持的解析器: {}", codeGenType, parserMap.keySet());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        }
        
        try {
            log.debug("开始解析 {} 类型的代码，内容长度: {}", codeGenType, codeContent.length());
            Object result = parser.parseCode(codeContent);
            log.debug("代码解析完成");
            return result;
        } catch (Exception e) {
            log.error("解析 {} 类型代码失败: {}", codeGenType, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码解析失败: " + e.getMessage());
        }
    }
}
