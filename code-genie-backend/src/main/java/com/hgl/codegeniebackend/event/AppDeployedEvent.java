package com.hgl.codegeniebackend.event;

/**
 * 应用部署完成事件
 */
public record AppDeployedEvent(Long appId, String deployUrl) {
}
