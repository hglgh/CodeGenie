package com.hgl.codegeniebackend.service;

/**
 * ClassName: ScreenshotService
 *
 * @Package: com.hgl.codegeniebackend.service
 * @Description:
 * @Author HGL
 * @Create: 2025/8/7 16:59
 */
public interface ScreenshotService {

    /**
     * 生成并上传网页截图
     *
     * @param webUrl 网页URL
     * @return 截图文件路径
     */
    String generateAndUploadScreenshot(String webUrl);
}
