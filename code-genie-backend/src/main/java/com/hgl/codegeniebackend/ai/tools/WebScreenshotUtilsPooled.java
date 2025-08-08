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
import lombok.Getter;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用连接池模式管理WebDriver的网页截图工具类
 * 通过维护一个WebDriver池，按需分配和回收WebDriver实例
 *
 * @Author HGL
 * @Create 2025/8/8
 */
@Slf4j
public class WebScreenshotUtilsPooled {
    private static final int DEFAULT_WIDTH = 1600;
    private static final int DEFAULT_HEIGHT = 900;
    // 图片后缀
    private static final String IMAGE_SUFFIX = ".png";
    // 压缩图片后缀
    private static final String COMPRESSION_SUFFIX = "_compressed.jpg";

    // 连接池配置
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_POOL_SIZE = 2;
    // 5分钟
    private static final long KEEP_ALIVE_TIME = 300000L;

    // WebDriver连接池
    private static final BlockingQueue<WebDriverWrapper> WEB_DRIVER_POOL = new LinkedBlockingQueue<>(MAX_POOL_SIZE);

    // 当前池中WebDriver数量
    private static final AtomicInteger POOL_SIZE = new AtomicInteger(0);

    // 是否正在运行
    @Getter
    private static volatile boolean running = true;

    public static void setRunning(boolean running) {
        WebScreenshotUtilsPooled.running = running;
    }

    /**
     * WebDriver包装类，用于跟踪使用情况
     */
    private static class WebDriverWrapper {
        private final WebDriver webDriver;
        @Getter
        private final long createTime;
        @Getter
        private long lastUseTime;

        public WebDriverWrapper(WebDriver webDriver) {
            this.webDriver = webDriver;
            this.createTime = System.currentTimeMillis();
            this.lastUseTime = System.currentTimeMillis();
        }

        public WebDriver getWebDriver() {
            this.lastUseTime = System.currentTimeMillis();
            return webDriver;
        }

        public void quit() {
            try {
                webDriver.quit();
            } catch (Exception e) {
                log.warn("关闭WebDriver时发生异常", e);
            }
        }
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

        WebDriverWrapper driverWrapper = null;
        try {
            // 从连接池获取WebDriver
            driverWrapper = borrowWebDriver();

            // 创建临时目录
            String rootPath = AppConstant.SCREENSHOT_ROOT_DIR + File.separator + UUID.randomUUID().toString().substring(0, 8);
            FileUtil.mkdir(rootPath);

            // 原始截图文件路径
            String originalImagePath = rootPath + File.separator + String.format("%s_original", RandomUtil.randomNumbers(5)) + IMAGE_SUFFIX;

            // 访问网页
            driverWrapper.getWebDriver().get(webUrl);

            // 等待页面加载完成
            waitForPageLoad(driverWrapper.getWebDriver());

            // 截图
            byte[] screenshotBytes = ((TakesScreenshot) driverWrapper.getWebDriver()).getScreenshotAs(OutputType.BYTES);

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
        } finally {
            // 归还WebDriver到连接池
            if (driverWrapper != null) {
                returnWebDriver(driverWrapper);
            }
        }
    }

    /**
     * 从连接池获取WebDriver
     *
     * @return WebDriverWrapper
     * @throws Exception
     */
    private static WebDriverWrapper borrowWebDriver() throws Exception {
        WebDriverWrapper driverWrapper = WEB_DRIVER_POOL.poll();

        // 如果连接池为空且未达到最大连接数，则创建新的WebDriver
        if (driverWrapper == null && POOL_SIZE.get() < MAX_POOL_SIZE) {
            synchronized (WebScreenshotUtilsPooled.class) {
                if (POOL_SIZE.get() < MAX_POOL_SIZE) {
                    WebDriver webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                    driverWrapper = new WebDriverWrapper(webDriver);
                    POOL_SIZE.incrementAndGet();
                    log.debug("创建新的WebDriver实例，当前池大小: {}", POOL_SIZE.get());
                }
            }
        }

        // 如果仍然为空，则阻塞等待
        if (driverWrapper == null) {
            driverWrapper = WEB_DRIVER_POOL.take();
        }

        // 检查WebDriver是否过期
        long currentTime = System.currentTimeMillis();
        if (currentTime - driverWrapper.getLastUseTime() > KEEP_ALIVE_TIME) {
            // 过期则销毁并创建新的
            driverWrapper.quit();
            POOL_SIZE.decrementAndGet();
            WebDriver webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            driverWrapper = new WebDriverWrapper(webDriver);
            POOL_SIZE.incrementAndGet();
            log.debug("替换过期WebDriver实例");
        }

        return driverWrapper;
    }

    /**
     * 归还WebDriver到连接池
     *
     * @param driverWrapper WebDriver包装类
     */
    private static void returnWebDriver(WebDriverWrapper driverWrapper) {
        if (driverWrapper != null) {
            // 清理浏览器状态（如cookies等）
            try {
                driverWrapper.getWebDriver().manage().deleteAllCookies();
            } catch (Exception e) {
                log.warn("清理WebDriver状态时发生异常", e);
            }

            // 尝试归还到连接池
            if (!WEB_DRIVER_POOL.offer(driverWrapper)) {
                // 如果连接池已满，则销毁多余的WebDriver
                if (POOL_SIZE.get() > MIN_POOL_SIZE) {
                    driverWrapper.quit();
                    POOL_SIZE.decrementAndGet();
                    log.debug("销毁多余的WebDriver实例，当前池大小: {}", POOL_SIZE.get());
                } else {
                    // 否则尝试强制放入（可能会阻塞）
                    try {
                        WEB_DRIVER_POOL.offer(driverWrapper, 5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        driverWrapper.quit();
                        POOL_SIZE.decrementAndGet();
                        Thread.currentThread().interrupt();
                    }
                }
            }
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
     * 关闭连接池，释放所有资源
     */
    public static void shutdown() {
        running = false;
        int size = POOL_SIZE.get();
        for (int i = 0; i < size; i++) {
            WebDriverWrapper driverWrapper = WEB_DRIVER_POOL.poll();
            if (driverWrapper != null) {
                driverWrapper.quit();
            }
        }
        POOL_SIZE.set(0);
        log.info("WebDriver连接池已关闭");
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
        // 压缩图片质量（0.3 = 30% 质量）
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
        chromeOptions.addArguments(String.format("--window-size=%d,%d", width, height));
        // 禁用扩展
        chromeOptions.addArguments("--disable-extensions");
        // 设置用户代理
        chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        return chromeOptions;
    }
}
