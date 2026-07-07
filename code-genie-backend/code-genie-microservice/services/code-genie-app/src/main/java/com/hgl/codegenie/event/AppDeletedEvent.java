package com.hgl.codegenie.event;

/**
 * 应用删除事件
 */
public record AppDeletedEvent(Long appId, String deployKey) {
}
