package com.hgl.codegeniebackend.core.parser;

/**
 * ClassName: CodeParser
 * Package: com.hgl.codegeniebackend.parser
 * Description: 代码解析器策略接口
 *
 * @Author HGL
 * @Create: 2025/7/30 16:15
 */
public interface CodeParser<T> {

    /**
     * 解析代码内容
     *
     * @param codeContent 原始代码内容
     * @return 解析后的结果对象
     */
    T parseCode(String codeContent);
}
