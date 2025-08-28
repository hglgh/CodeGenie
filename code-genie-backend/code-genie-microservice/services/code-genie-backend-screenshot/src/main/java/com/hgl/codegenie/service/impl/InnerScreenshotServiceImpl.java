package com.hgl.codegenie.service.impl;

import com.hgl.codegenie.innerservice.InnerScreenshotService;
import com.hgl.codegenie.service.ScreenshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName: InnerScreenshotServiceImpl
 * @Package: com.hgl.codegenie.service.impl
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 17:35
 */
@DubboService
@Slf4j
public class InnerScreenshotServiceImpl implements InnerScreenshotService {

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        return screenshotService.generateAndUploadScreenshot(webUrl);
    }
}
