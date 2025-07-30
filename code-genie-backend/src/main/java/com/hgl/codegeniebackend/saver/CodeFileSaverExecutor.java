package com.hgl.codegeniebackend.saver;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.HtmlCodeResult;
import com.hgl.codegeniebackend.ai.model.MultiFileCodeResult;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;

import java.io.File;

import static com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum.HTML;
import static com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum.MULTI_FILE;

/**
 * ClassName: CodeFileSaverExecutor
 * Package: com.hgl.codegeniebackend.saver
 * Description:  代码文件保存执行器  根据代码生成类型执行相应的保存逻辑
 *
 * @Author HGL
 * @Create: 2025/7/30 17:02
 */
public class CodeFileSaverExecutor {
    private static final HtmlCodeFileSaverTemplate HTML_CODE_FILE_SAVER_TEMPLATE = new HtmlCodeFileSaverTemplate();
    private static final MultiFileCodeFileSaverTemplate MULTI_FILE_CODE_FILE_SAVER_TEMPLATE = new MultiFileCodeFileSaverTemplate();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> HTML_CODE_FILE_SAVER_TEMPLATE.saveCode((HtmlCodeResult) codeResult);
            case MULTI_FILE -> MULTI_FILE_CODE_FILE_SAVER_TEMPLATE.saveCode((MultiFileCodeResult) codeResult);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        };
    }
}
