package com.hgl.codegenie.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * ClassName: HtmlCodeResult
 * Package: com.hgl.codegeniebackend.ai.model
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 13:44
 */
@Description("生成 HTML 代码文件的结果")
@Data
public class HtmlCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("生成代码的描述")
    private String description;
}


