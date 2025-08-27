package com.hgl.codegenie.core.saver;

import cn.hutool.core.util.StrUtil;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.common.exception.ThrowUtils;
import com.hgl.codegenie.model.HtmlCodeResult;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;


/**
 * ClassName: HtmlCodeFileSaverTemplate
 * Package: com.hgl.codegeniebackend.saver
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 16:48
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        // 保存 HTML 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        ThrowUtils.throwIf(StrUtil.isBlank(result.getHtmlCode()), ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
    }
}
