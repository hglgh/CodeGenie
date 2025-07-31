#!/bin/bash

# 批量重启容器脚本

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 加载通用函数库
if [ -f "$SCRIPT_DIR/common.sh" ]; then
    source "$SCRIPT_DIR/common.sh"
else
    echo "错误: 找不到通用函数库 common.sh"
    exit 1
fi

# 默认容器列表（如果未提供参数）
DEFAULT_CONTAINERS=("emallplus-member"
                   "emallplus-basic"
                   "emallplus-datahub"
                   "emallplus-commodity"
                   "emallplus-ingateway")

# 使用命令行参数或默认容器列表
if [ $# -gt 0 ]; then
    CONTAINERS=("$@")
else
    CONTAINERS=("${DEFAULT_CONTAINERS[@]}")
fi

log_info "开始批量重启容器..."

# 循环遍历每个容器并尝试重启它们
for container in "${CONTAINERS[@]}"; do
    log_info "正在处理容器: $container"

    # 检查容器是否存在
    if docker ps -a --format '{{.Names}}' | grep -wq "$container"; then
        # 检查容器当前状态
        if docker ps --format '{{.Names}}' | grep -wq "$container"; then
            log_info "容器 '$container' 正在运行，准备重启..."
            if docker restart "$container"; then
                log_info "✅ 已成功重启容器: $container"
            else
                log_error "❌ 容器 '$container' 重启失败"
            fi
        else
            log_warn "容器 '$container' 存在但未运行，尝试启动..."
            if docker start "$container"; then
                log_info "✅ 已成功启动容器: $container"
            else
                log_error "❌ 容器 '$container' 启动失败"
            fi
        fi
    else
        log_warn "容器 '$container' 不存在"
    fi
done

log_info "所有指定容器处理完毕"
