package com.hgl.codegeniebackend.core.elegance.saverenhanced;

import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.MultiFileCodeResult;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.common.constant.CodeGenTypeConstant;
import org.springframework.stereotype.Component;

/**
 * ClassName: MultiFileCodeFileSaverTemplate
 * Package: com.hgl.codegeniebackend.core.saverenhanced
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 11:29
 */
@Component(CodeGenTypeConstant.MULTI_FILE+CodeGenTypeConstant.SAVER_SUFFIX)
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