package com.hgl.codegeniebackend.config.expansion;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: ServeDeployService
 * Package: com.hgl.codegeniebackend.config.expansion
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 18:00
 */
@Service
public class ServeDeployService {

    private static final String CODE_BASE_DIR = "/temp/deploy";
    private static final int SERVE_PORT = 3000;
    private static Process serveProcess;

    /**
     * 启动 Serve 服务
     */
    public void startServeService() {
        try {
            if (serveProcess == null || !serveProcess.isAlive()) {
                ProcessBuilder pb = new ProcessBuilder(
                        "npx", "serve", CODE_BASE_DIR, "-p", String.valueOf(SERVE_PORT)
                );
                pb.redirectErrorStream(true);
                serveProcess = pb.start();
                System.out.println("Serve service started on port " + SERVE_PORT);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start serve service", e);
        }
    }

    /**
     * 关闭 Serve 服务
     */
    public void stopServeService() {
        if (serveProcess != null && serveProcess.isAlive()) {
            serveProcess.destroy();
            try {
                serveProcess.waitFor(5, TimeUnit.SECONDS);
                System.out.println("Serve service stopped");
            } catch (InterruptedException e) {
                serveProcess.destroyForcibly();
                System.out.println("Serve service force stopped");
            }
        }
    }
}

