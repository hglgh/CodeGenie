package com.hgl.codegeniebackend.core.elegance.saverenhanced;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.common.constant.AppConstant;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * ClassName: CodeFileSaverTemplate
 * Package: com.hgl.codegeniebackend.saver
 * Description: 抽象代码文件保存器 - 模板方法模式
 *
 * @Author HGL
 * @Create: 2025/7/30 16:33
 */
public abstract class CodeFileSaverTemplate<T> {
    // 文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * 模板方法：保存代码的标准流程
     *
     * @param result 代码结果对象
     * @return 保存的目录
     */
    public final File saveCode(T result, Long appId) {
        // 1.验证输入参数
        validateInput(result);
        // 2. 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件（具体实现由子类提供）
        saveFiles(result, baseDirPath);
        // 4. 返回目录文件对象
        return new File(baseDirPath);
    }

    /**
     * 获取代码类型（由子类实现）
     *
     * @return 代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件的具体实现（由子类实现）
     *
     * @param result      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);


    /**
     * 验证输入参数（可由子类覆盖）
     *
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.PARAMS_ERROR, "代码结果不能为空");
    }

    /**
     * 构建唯一目录路径
     *
     * @return 目录路径
     */
    protected final String buildUniqueDir(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR, "appId不能为空");
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = String.format("%s/%s", FILE_SAVE_ROOT_DIR, uniqueDirName);
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件的工具方法
     *
     * @param dirPath  目录路径
     * @param filename 文件名
     * @param content  文件内容
     */
    protected final void writeToFile(String dirPath, String filename, String content) {
        if (StrUtil.isNotBlank(content)) {
//            String filePath = dirPath + File.separator + filename;
            String filePath = String.format("%s/%s", dirPath, filename);
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }
}
