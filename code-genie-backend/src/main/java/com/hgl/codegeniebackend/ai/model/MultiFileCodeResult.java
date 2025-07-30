package com.hgl.codegeniebackend.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * ClassName: MultiFileCodeResult
 * Package: com.hgl.codegeniebackend.ai.model
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 13:45
 */
@Description("生成多个代码文件的结果")
@Data
public class MultiFileCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("CSS代码")
    private String cssCode;

    @Description("JS代码")
    private String jsCode;

    @Description("生成代码的描述")
    private String description;
}

