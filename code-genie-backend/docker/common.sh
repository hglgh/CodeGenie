#!/bin/bash

# 通用Docker操作函数库

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_debug() {
    if [ "${DEBUG:-false}" = "true" ]; then
        echo -e "${BLUE}[DEBUG]${NC} $1"
    fi
}

# 删除指定名称的历史容器和镜像
delete_container_and_image() {
    local CONTAINER_NAME=$1
    local IMAGE_NAME=$2

    log_info "正在删除历史容器 '$CONTAINER_NAME'..."
    docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || true
    docker rm "$CONTAINER_NAME" >/dev/null 2>&1 || true

    if [ -n "$IMAGE_NAME" ]; then
        log_info "正在删除历史镜像 '$IMAGE_NAME'..."
        docker rmi "$IMAGE_NAME" >/dev/null 2>&1 || true
    fi
}

# 构建 Docker 镜像
build_docker_image() {
    local IMAGE_NAME=$1
    local DOCKERFILE_PATH=${2:-"Dockerfile"}

    log_info "正在构建 Docker 镜像 '$IMAGE_NAME'..."
    if [ -f "$DOCKERFILE_PATH" ]; then
        docker build -t "$IMAGE_NAME" -f "$DOCKERFILE_PATH" .
        return $?
    else
        log_error "Dockerfile 不存在: $DOCKERFILE_PATH"
        return 1
    fi
}

# 智能解析内存单位
parse_memory() {
    local memory_str=$1
    local memory_value=$(echo "$memory_str" | grep -oE '[0-9]+' | head -1)
    local memory_unit=$(echo "$memory_str" | tr '[:upper:]' '[:lower:]' | sed 's/[0-9]//g' | head -c 1)

    if [ -z "$memory_value" ]; then
        log_error "无效的内存格式: $memory_str"
        return 1
    fi

    case "$memory_unit" in
        g|G)
            echo $((memory_value * 1024))
            ;;
        m|M|"")
            echo $memory_value
            ;;
        k|K)
            echo $((memory_value / 1024))
            ;;
        *)
            log_error "未知内存单位: $memory_unit"
            return 1
            ;;
    esac
}

# 计算JVM参数
calculate_jvm_params() {
    local max_memory=$1
    local jvm_ratio=${2:-0.7}      # JVM堆内存占容器内存比例
    local direct_ratio=${3:-0.15}  # Direct内存占容器内存比例
    local thread_stack=${4:-256k}  # 线程栈大小

    local total_memory_mb
    total_memory_mb=$(parse_memory "$max_memory")
    if [ $? -ne 0 ]; then
        return 1
    fi

    # 计算JVM堆内存
    local jvm_heap_mb
    jvm_heap_mb=$(echo "$total_memory_mb * $jvm_ratio" | bc 2>/dev/null)
    if [ $? -ne 0 ]; then
        log_error "计算JVM堆内存失败"
        return 1
    fi
    jvm_heap_mb=${jvm_heap_mb%.*}

    # 计算Direct内存
    local direct_memory_mb
    direct_memory_mb=$(echo "$total_memory_mb * $direct_ratio" | bc 2>/dev/null)
    if [ $? -ne 0 ]; then
        log_error "计算Direct内存失败"
        return 1
    fi
    direct_memory_mb=${direct_memory_mb%.*}

    # 构造JAVA_OPTS
    local java_opts="-Xmx${jvm_heap_mb}m"
    java_opts="$java_opts -XX:MaxDirectMemorySize=${direct_memory_mb}m"
    java_opts="$java_opts -Xss$thread_stack"
    java_opts="$java_opts -XX:NativeMemoryTracking=summary"
    java_opts="$java_opts -XX:+UseContainerSupport"

    echo "$java_opts"
}

# 启动容器并设置环境变量和内存限制
start_container() {
    local IMAGE_NAME=$1
    local CONTAINER_NAME=$2
    local HOST_PORT=$3
    local CONTAINER_PORT=$4
    local MAX_MEMORY=$5
    local ENV_VARS=${6:-""}
    local ADDITIONAL_OPTIONS=${7:-""}

    # 计算JVM参数
    local JAVA_OPTS
    JAVA_OPTS=$(calculate_jvm_params "$MAX_MEMORY")
    if [ $? -ne 0 ]; then
        log_error "JVM参数计算失败"
        return 1
    fi

    # 启动容器并设置内存限制和 JVM 参数
    log_info "正在启动容器 '$CONTAINER_NAME'，内存限制: $MAX_MEMORY，JVM 参数: $JAVA_OPTS"

    local docker_cmd="docker run -d"
    docker_cmd="$docker_cmd --name $CONTAINER_NAME"
    docker_cmd="$docker_cmd -p $HOST_PORT:$CONTAINER_PORT"
    docker_cmd="$docker_cmd --memory $MAX_MEMORY"
    docker_cmd="$docker_cmd --env JAVA_OPTS=$JAVA_OPTS"

    # 添加额外的环境变量
    if [ -n "$ENV_VARS" ]; then
        IFS=' ' read -ra ENV_ARRAY <<< "$ENV_VARS"
        for env_var in "${ENV_ARRAY[@]}"; do
            if [ -n "$env_var" ]; then
                docker_cmd="$docker_cmd --env $env_var"
            fi
        done
    fi

    # 添加额外的Docker选项
    if [ -n "$ADDITIONAL_OPTIONS" ]; then
        docker_cmd="$docker_cmd $ADDITIONAL_OPTIONS"
    fi

    docker_cmd="$docker_cmd $IMAGE_NAME"

    log_debug "执行命令: $docker_cmd"
    eval $docker_cmd
}

# 带JMX监控的容器启动函数
start_container_with_jmx() {
    local IMAGE_NAME=$1
    local CONTAINER_NAME=$2
    local HOST_PORT=$3
    local CONTAINER_PORT=$4
    local MAX_MEMORY=$5
    local ENV_VARS=${6:-""}
    local JMX_PORT=${7:-9090}
    local JMX_HOST=${8:-"localhost"}

    # 计算JVM参数（包含JMX配置）
    local JAVA_OPTS
    JAVA_OPTS=$(calculate_jvm_params "$MAX_MEMORY")
    if [ $? -ne 0 ]; then
        log_error "JVM参数计算失败"
        return 1
    fi

    # 添加JMX相关参数
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
    JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=$JMX_HOST"

    # 启动容器
    log_info "正在启动带JMX监控的容器 '$CONTAINER_NAME'，内存限制: $MAX_MEMORY，JVM 参数: $JAVA_OPTS"

    local docker_cmd="docker run -d"
    docker_cmd="$docker_cmd --name $CONTAINER_NAME"
    docker_cmd="$docker_cmd -p $HOST_PORT:$CONTAINER_PORT"
    docker_cmd="$docker_cmd -p $JMX_PORT:$JMX_PORT"
    docker_cmd="$docker_cmd --memory $MAX_MEMORY"
    docker_cmd="$docker_cmd --env JAVA_OPTS=$JAVA_OPTS"

    # 添加额外的环境变量
    if [ -n "$ENV_VARS" ]; then
        IFS=' ' read -ra ENV_ARRAY <<< "$ENV_VARS"
        for env_var in "${ENV_ARRAY[@]}"; do
            if [ -n "$env_var" ]; then
                docker_cmd="$docker_cmd --env $env_var"
            fi
        done
    fi

    docker_cmd="$docker_cmd $IMAGE_NAME"

    log_debug "执行命令: $docker_cmd"
    eval $docker_cmd
}

# 检查容器状态
check_container_status() {
    local CONTAINER_NAME=$1
    if docker ps --filter "name=$CONTAINER_NAME" --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
        log_info "✅ 容器 '$CONTAINER_NAME' 启动成功。"
        return 0
    else
        log_error "❌ 容器 '$CONTAINER_NAME' 启动失败，请检查日志。"
        return 1
    fi
}

# 显示容器日志
show_container_logs() {
    local CONTAINER_NAME=$1
    local LINES=${2:-100}
    log_info "查看日志: docker logs -f -t --tail=$LINES '$CONTAINER_NAME'"
    echo "----------------------------------------"
    docker logs -t --tail="$LINES" "$CONTAINER_NAME"
    echo "----------------------------------------"
}

# 等待容器启动
wait_for_container() {
    local CONTAINER_NAME=$1
    local TIMEOUT=${2:-30}
    local COUNT=0

    log_info "等待容器 '$CONTAINER_NAME' 启动..."

    while [ $COUNT -lt $TIMEOUT ]; do
        if docker ps --filter "name=$CONTAINER_NAME" --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
            # 检查容器是否真的在运行（而不仅仅是存在）
            if [ "$(docker inspect -f '{{.State.Running}}' "$CONTAINER_NAME" 2>/dev/null)" = "true" ]; then
                log_info "容器 '$CONTAINER_NAME' 已启动"
                return 0
            fi
        fi
        sleep 1
        COUNT=$((COUNT + 1))
    done

    log_error "容器 '$CONTAINER_NAME' 启动超时"
    return 1
}
