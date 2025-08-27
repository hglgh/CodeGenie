package com.hgl.codegenie.core.saver;

import cn.hutool.core.util.StrUtil;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.common.exception.ThrowUtils;
import com.hgl.codegenie.model.MultiFileCodeResult;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;


/**
 * ClassName: MultiFileCodeFileSaverTemplate
 * Package: com.hgl.codegeniebackend.saver
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 16:58
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        // 保存 HTML 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        // 保存 CSS 文件
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        // 保存 JavaScript 文件
        writeToFile(baseDirPath, "script.js", result.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        // 至少要有 HTML 代码，CSS 和 JS 可以为空
        ThrowUtils.throwIf(StrUtil.isBlank(result.getHtmlCode()), ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
    }
}
