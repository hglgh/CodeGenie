package com.hgl.codegeniebackend.core.elegance.saverenhanced;

import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.HtmlCodeResult;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.common.constant.CodeGenTypeConstant;
import org.springframework.stereotype.Component;

/**
 * ClassName: HtmlCodeFileSaverTemplate
 * Package: com.hgl.codegeniebackend.core.saverenhanced
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 11:28
 */
@Component(CodeGenTypeConstant.HTML+CodeGenTypeConstant.SAVER_SUFFIX)
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