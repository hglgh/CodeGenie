package com.hgl.codegenie.event;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.hgl.codegenie.common.constant.AppConstant;
import com.hgl.codegenie.innerservice.InnerScreenshotService;
import com.hgl.codegenie.model.entity.App;
import com.hgl.codegenie.service.AppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class AppEventListener {

    @DubboReference
    private InnerScreenshotService screenshotService;

    @Resource
    private AppService appService;

    @Async
    @EventListener
    public void onAppDeployed(AppDeployedEvent event) {
        try {
            String screenshotUrl = screenshotService.generateAndUploadScreenshot(event.deployUrl());
            App updateApp = new App();
            updateApp.setId(event.appId());
            updateApp.setCover(screenshotUrl);
            appService.updateById(updateApp);
            log.info("应用截图生成完成，appId: {}", event.appId());
        } catch (Exception e) {
            log.error("应用截图生成失败，appId: {}", event.appId(), e);
        }
    }

    @EventListener
    public void onAppDeleted(AppDeletedEvent event) {
        if (event.deployKey() == null || event.deployKey().isEmpty()) {
            return;
        }
        String deployDir = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + event.deployKey();
        try {
            if (FileUtil.exist(deployDir)) {
                log.info("删除部署目录: {}", deployDir);
                boolean deleted = FileUtil.del(deployDir);
                if (!deleted) {
                    log.error("删除部署目录失败: {}", deployDir);
                }
            }
        } catch (IORuntimeException e) {
            log.error("删除部署目录时发生异常: {}", deployDir, e);
        }
    }
}
