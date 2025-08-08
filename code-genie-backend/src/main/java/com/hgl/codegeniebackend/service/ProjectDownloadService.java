package com.hgl.codegeniebackend.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * ClassName: ProjectDownloadService
 *
 * @Package: com.hgl.codegeniebackend.service
 * @Description:
 * @Author HGL
 * @Create: 2025/8/8 8:59
 */
public interface ProjectDownloadService {
    /**
     * 下载zip项目
     *
     * @param projectPath      项目路径
     * @param downloadFileName 下载文件名
     * @param response         响应
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
