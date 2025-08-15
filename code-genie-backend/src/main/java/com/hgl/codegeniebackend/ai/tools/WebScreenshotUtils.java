package com.hgl.codegeniebackend.ai.tools;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.common.constant.AppConstant;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

/**
 * ClassName: WebScreenshotUtils
 *
 * @Package: com.hgl.codegeniebackend.ai.tools
 * @Description:
 * @Author HGL
 * @Create: 2025/8/7 15:00
 */
@Slf4j
public class WebScreenshotUtils {
    private static final WebDriver WEB_DRIVER;
    // 图片后缀
    private static final String IMAGE_SUFFIX = ".png";
    // 压缩图片后缀
    private static final String COMPRESSION_SUFFIX = "_compressed.jpg";

    /*
     * 静态初始化 Chrome 浏览器驱动
     */
    static {
        final int defaultWidth = 1600;
        final int defaultHeight = 900;
        WEB_DRIVER = initChromeDriver(defaultWidth, defaultHeight);
    }

    private WebScreenshotUtils() {
    }

    /**
     * 生成网页截图
     *
     * @param webUrl 网页URL
     * @return 压缩后的截图文件路径，失败返回null
     */
    public static String saveWebPageScreenshot(String webUrl) {
        if (StrUtil.isBlank(webUrl)) {
            log.error("网页URL不能为空");
            return null;
        }
        try {
            //创建临时目录
            String rootPath = AppConstant.SCREENSHOT_ROOT_DIR + File.separator + UUID.randomUUID().toString().substring(0, 8);
            FileUtil.mkdir(rootPath);
            // 原始截图文件路径
            String originalImagePath = rootPath + File.separator + String.format("%s_original", RandomUtil.randomNumbers(5)) + IMAGE_SUFFIX;
            // 访问网页
            WEB_DRIVER.get(webUrl);
            // 等待页面加载完成
            waitForPageLoad(WEB_DRIVER);
            // 截图
            byte[] screenshotBytes = ((TakesScreenshot) WEB_DRIVER).getScreenshotAs(OutputType.BYTES);
            // 保存原始图片
            saveImage(screenshotBytes, originalImagePath);
            log.info("原始截图保存成功: {}", originalImagePath);
            // 压缩图片
            String compressedImagePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + COMPRESSION_SUFFIX;
            compressImage(originalImagePath, compressedImagePath);
            log.info("压缩图片保存成功: {}", compressedImagePath);
            // 删除原始图片，只保留压缩图片
            FileUtil.del(originalImagePath);
            return compressedImagePath;
        } catch (Exception e) {
            log.error("网页截图失败: {}", webUrl, e);
            return null;
        }
    }

    /**
     * 清理临时文件
     */
    public static void cleanupTempFiles() {
        // 获取截图根目录
        File screenshotDir = new File(AppConstant.SCREENSHOT_ROOT_DIR);

        // 检查目录是否存在
        if (!screenshotDir.exists() || !screenshotDir.isDirectory()) {
            log.info("截图目录不存在: {}", screenshotDir.getAbsolutePath());
            return;
        }

        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        // 设置过期时间（24小时）
        long expireTime = 24 * 60 * 60 * 1000L;

        // 遍历目录中的所有子目录
        File[] subDirs = screenshotDir.listFiles(File::isDirectory);
        if (subDirs == null) {
            log.info("截图目录中没有子目录");
            return;
        }

        int deletedCount = 0;
        for (File subDir : subDirs) {
            // 检查目录的最后修改时间
            long lastModified = subDir.lastModified();
            if (currentTime - lastModified > expireTime) {
                // 删除过期的目录
                try {
                    boolean deleted = FileUtil.del(subDir);
                    if (deleted) {
                        deletedCount++;
                        log.info("已删除过期截图目录: {}", subDir.getAbsolutePath());
                    } else {
                        log.warn("删除过期截图目录失败: {}", subDir.getAbsolutePath());
                    }
                } catch (Exception e) {
                    log.error("删除过期截图目录时发生异常: {}", subDir.getAbsolutePath(), e);
                }
            }
        }

        log.info("清理完成，共删除 {} 个过期截图目录", deletedCount);
    }

    /**
     * 销毁 Chrome 浏览器驱动
     */
    @PreDestroy
    public void onApplicationShutdown() {
        if (WEB_DRIVER != null) {
            log.info("Shutting down Chrome driver...");
            WEB_DRIVER.quit();
        }
    }

    /**
     * 等待页面加载完成
     */
    private static void waitForPageLoad(WebDriver driver) {
        try {
            // 创建等待页面加载对象
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // 等待 document.readyState 为complete
            webDriverWait.until(webDriver ->
                    "complete".equals(
                            ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                    )
            );
            // 额外等待一段时间，确保动态内容加载完成
            Thread.sleep(2000);
            log.info("页面加载完成");
        } catch (Exception e) {
            log.error("等待页面加载时出现异常，继续执行截图", e);
        }
    }

    /**
     * 压缩图片
     *
     * @param originalImagePath   原始图片路径
     * @param compressedImagePath 压缩图片路径
     * @param compressionQuality  压缩质量
     */
    private static void compressImage(String originalImagePath, String compressedImagePath, float compressionQuality) {
        try {
            ImgUtil.compress(FileUtil.file(originalImagePath), FileUtil.file(compressedImagePath), compressionQuality);
        } catch (IORuntimeException e) {
            log.error("压缩图片失败: {} -> {}", originalImagePath, compressedImagePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩图片失败");
        }
    }

    /**
     * 压缩图片
     */
    private static void compressImage(String originalImagePath, String compressedImagePath) {
        // 压缩图片质量（0.1 = 10% 质量）
        final float compressionQuality = 0.3f;
        compressImage(originalImagePath, compressedImagePath, compressionQuality);
    }

    /**
     * 保存图片到文件
     */
    private static void saveImage(byte[] imageBytes, String imagePath) {
        try {
            FileUtil.writeBytes(imageBytes, imagePath);
        } catch (IORuntimeException e) {
            log.error("保存图片失败: {}", imagePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存图片失败");
        }
    }

    /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();
            // 配置 Chrome 选项
            ChromeOptions chromeOptions = getChromeOptions(width, height);
            // 创建驱动
            ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
            // 设置页面加载超时
            chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return chromeDriver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }

    }

    /**
     * 获取 Chrome 浏览器选项
     */
    private static ChromeOptions getChromeOptions(int width, int height) {
        ChromeOptions chromeOptions = new ChromeOptions();
        // 无头模式
        chromeOptions.addArguments("--headless");
        // 禁用GPU（在某些环境下避免问题）
        chromeOptions.addArguments("--disable-gpu");
        // 禁用沙盒模式（Docker环境需要）
        chromeOptions.addArguments("--no-sandbox");
        // 禁用开发者shm使用
        chromeOptions.addArguments("--disable-dev-shm-usage");
        // 设置窗口大小
        chromeOptions.addArguments(java.lang.String.format("--window-size=%d,%d", width, height));
        // 禁用扩展
        chromeOptions.addArguments("--disable-extensions");
        // 设置用户代理
        chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        return chromeOptions;
    }
}
