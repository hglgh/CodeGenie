package com.hgl.codegeniebackend.common.constant;

/**
 * ClassName: CodeGenTypeConstant
 * Package: com.hgl.codegeniebackend.core.parserenhanced
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 13:05
 */
public interface CodeGenTypeConstant {
    String HTML = "html";
    String MULTI_FILE = "multi_file";
    
    // Parser Bean名称后缀
    String PARSER_SUFFIX = "-parser";
    
    // Saver Bean名称后缀
    String SAVER_SUFFIX = "-saver";
    
    // 构建Parser Bean名称的工具方法
    static String buildParserBeanName(String valueType) {
        return valueType + PARSER_SUFFIX;
    }
    
    // 构建Saver Bean名称的工具方法
    static String buildSaverBeanName(String valueType) {
        return valueType + SAVER_SUFFIX;
    }
}
