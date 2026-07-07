package com.hgl.codegeniebackend.common.constant;

/**
 * ClassName: AppConstant
 * Package: com.hgl.codegeniebackend.common.constant
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 16:24
 */
public interface AppConstant {

    /**
     * 精选应用的优先级
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 应用生成根路径
     */
    String ROOT_PATH = System.getProperty("user.dir");

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = String.format("%s/temp/code_output", ROOT_PATH);

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = String.format("%s/temp/code_deploy", ROOT_PATH);

    /**
     * 应用截图目录
     */
    String SCREENSHOT_ROOT_DIR = String.format("%s/temp/screenshot", ROOT_PATH);

    /**
     * ChromeDriver 驱动缓存目录
     */
    String CHROME_DRIVER_CACHE_DIR = String.format("%s/temp/chromedriver", ROOT_PATH);

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost";

}

