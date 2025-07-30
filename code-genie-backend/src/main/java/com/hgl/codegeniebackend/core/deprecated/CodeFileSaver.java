package com.hgl.codegeniebackend.core.deprecated;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.HtmlCodeResult;
import com.hgl.codegeniebackend.ai.model.MultiFileCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * ClassName: CodeFileSaver
 * Package: com.hgl.codegeniebackend.core
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 14:07
 */
@Deprecated
public class CodeFileSaver {

    // 文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = String.format("%s/temp/code_output", System.getProperty("user.dir"));


    /**
     * 保存 HTML 代码结果
     *
     * @param result HTML 代码结果
     * @return 保存后的文件  File 对象只是一个文件的引用或路径表示，它只包含文件的元信息（如文件路径、名称等），不包含文件的实际内容。
     * 如果需要获取文件内容，需要通过 FileInputStream、BufferedReader 等 IO 流来读取文件内容。File 对象本身只是指向磁盘上文件的一个"指针"或"地址"。
     */
    public static File saveHtmlCodeResult(HtmlCodeResult result) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult
     *
     * @param result 多文件代码生成结果
     * @return 保存后的文件目录
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult result) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     *
     * @param bizType 业务类型
     * @return 唯一目录路径
     */
    private static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = String.format("%s/%s", FILE_SAVE_ROOT_DIR, uniqueDirName);
        FileUtil.mkdir(dirPath);
        return dirPath;
    }


    /**
     * 写入单个文件
     *
     * @param dirPath  目录路径
     * @param filename 文件名
     * @param content  文件内容
     */
    private static void writeToFile(String dirPath, String filename, String content) {
//        String filePath = dirPath + File.separator + filename;
        String filePath = String.format("%s/%s", dirPath, filename);
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
