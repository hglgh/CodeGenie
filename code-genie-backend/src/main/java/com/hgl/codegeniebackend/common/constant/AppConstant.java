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
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = String.format("%s/temp/code_output", System.getProperty("user.dir"));

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = String.format("%s/temp/code_deploy", System.getProperty("user.dir"));

    /**
     * 应用截图目录
     */
    String SCREENSHOT_ROOT_DIR = String.format("%s/temp/screenshot", System.getProperty("user.dir"));

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost";

}

