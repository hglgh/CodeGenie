package com.hgl.codegeniebackend.core.elegance.saverenhanced;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.common.constant.CodeGenTypeConstant;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

/**
 * ClassName: CodeFileSaverManager
 * Package: com.hgl.codegeniebackend.core.saverenhanced
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 11:27
 */
@Slf4j
@Component
public class CodeFileSaverManager {

    @Resource
    private Map<String, CodeFileSaverTemplate<?>> saverMap;

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @param appId       应用ID
     * @return 保存的目录
     */
    public File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        // 构建Bean名称
        String beanName = CodeGenTypeConstant.buildSaverBeanName(codeGenType.getValue());
        CodeFileSaverTemplate<?> saver = saverMap.get(beanName);
        if (saver == null) {
            log.error("找不到代码生成类型 {} 对应的保存器，支持的保存器: {}", codeGenType, saverMap.keySet());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        }

        // 使用类型安全的调用方式
        try {
            log.debug("开始保存 {} 类型的代码，应用ID: {}", codeGenType, appId);
            File savedDir = saveCode(saver, codeResult, appId);
            log.info("代码保存成功，路径为：{}", savedDir.getAbsolutePath());
            return savedDir;
        } catch (Exception e) {
            log.error("保存 {} 类型代码失败: {}", codeGenType, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码保存失败: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> File saveCode(CodeFileSaverTemplate<T> saver, Object codeResult, Long appId) {
        return saver.saveCode((T) codeResult, appId);
    }
}
