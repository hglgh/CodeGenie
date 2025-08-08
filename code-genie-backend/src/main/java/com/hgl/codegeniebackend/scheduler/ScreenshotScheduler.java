package com.hgl.codegeniebackend.scheduler;

import com.hgl.codegeniebackend.ai.tools.WebScreenshotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * ClassName: ScreenshotScheduler
 *
 * @Package: com.hgl.codegeniebackend.scheduler
 * @Description: Cron表达式由6或7个字段组成  (cron = "0 5 18 * * ?")   秒 分 时 日 月 周 [年]
 * Cron表达式中的特殊字符说明：
 * <ol>
 *     <li>*：表示匹配该域的任意值</li>
 *     <li>*：?：只能用在日和周两个域，表示不指定值，当其中一个被指定了值时，另一个需要用?占位</li>
 * </ol>
 * @Author HGL
 * @Create: 2025/8/7 17:54
 */
@Slf4j
@EnableScheduling
@Configuration
public class ScreenshotScheduler {
    /**
     * 每天凌晨2点清理过期的临时截图文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(cron = "0 5 18 * * ?")  //每天下午6点5分执行一次
    public void clearExpiredTempScreenshots() {
        try {
            log.info("开始清理过期的截图文件");
            WebScreenshotUtils.cleanupTempFiles();
            log.info("定时清理临时截图文件完成");
        } catch (Exception e) {
            log.error("定时清理临时截图文件失败", e);
        }
    }
}
