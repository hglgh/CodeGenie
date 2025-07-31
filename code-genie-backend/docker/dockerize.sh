#!/bin/bash

# 通用Spring Boot项目Docker化脚本

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 加载通用函数库
if [ -f "$SCRIPT_DIR/common.sh" ]; then
    source "$SCRIPT_DIR/common.sh"
else
    echo "错误: 找不到通用函数库 common.sh"
    exit 1
fi

# 默认配置
DEFAULT_PORT=8080
DEFAULT_MEMORY="512m"
DEFAULT_VERSION="latest"

# 显示使用方法
show_usage() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -j, --jar <路径>        指定jar文件路径 (必需)"
    echo "  -n, --name <名称>       指定应用名称"
    echo "  -p, --port <端口>       指定端口映射 (默认: $DEFAULT_PORT)"
    echo "  -m, --memory <内存>     指定内存限制 (默认: $DEFAULT_MEMORY)"
    echo "  -v, --version <版本>    指定镜像版本 (默认: $DEFAULT_VERSION)"
    echo "  -e, --env <变量>        添加环境变量 (可多次使用)"
    echo "  --jmx                   启用JMX监控"
    echo "  --jmx-port <端口>       JMX端口 (默认: 9090)"
    echo "  --jmx-host <主机>       JMX主机名"
    echo "  --auto-port             自动寻找可用端口 (默认: false)"
    echo "  -h, --help              显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 -j ./app.jar"
    echo "  $0 -j ./app.jar -n my-app -p 9090 -m 1g"
    echo "  $0 -j ./app.jar --jmx --jmx-port 9999"
    echo "  $0 -j ./app.jar --auto-port"
}

# 检查端口是否可用
check_port_available() {
    local port=$1
    # 使用不同的方法检查端口可用性，适应不同系统
    if command -v netstat >/dev/null 2>&1; then
        if netstat -tuln | grep ":$port " >/dev/null 2>&1; then
            return 1  # 端口被占用
        else
            return 0  # 端口可用
        fi
    elif command -v ss >/dev/null 2>&1; then
        if ss -tuln | grep ":$port " >/dev/null 2>&1; then
            return 1  # 端口被占用
        else
            return 0  # 端口可用
        fi
    else
        # 如果没有netstat或ss，尝试创建监听套接字
        if timeout 1 bash -c "echo >/dev/tcp/127.0.0.1/$port" 2>/dev/null; then
            return 1  # 端口可达（可能被占用）
        else
            return 0  # 端口不可达（可能可用）
        fi
    fi
}

# 查找下一个可用端口
find_next_available_port() {
    local base_port=$1
    local port=$base_port

    while ! check_port_available $port; do
        log_warn "端口 $port 被占用，尝试端口 $((port + 1))"
        port=$((port + 1))
        # 防止无限循环，设置一个上限
        if [ $port -gt $((base_port + 1000)) ]; then
            log_error "无法找到可用端口，从 $base_port 开始的1000个端口都被占用"
            return 1
        fi
    done

    echo $port
    return 0
}

# 参数解析
JAR_FILE=""
APP_NAME=""
PORT="$DEFAULT_PORT"
MEMORY="$DEFAULT_MEMORY"
VERSION="$DEFAULT_VERSION"
ENV_VARS=""
ENABLE_JMX=false
JMX_PORT=9090
JMX_HOST="localhost"
ADDITIONAL_DOCKER_OPTIONS=""
AUTO_PORT=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -j|--jar)
            JAR_FILE="$2"
            shift 2
            ;;
        -n|--name)
            APP_NAME="$2"
            shift 2
            ;;
        -p|--port)
            PORT="$2"
            shift 2
            ;;
        -m|--memory)
            MEMORY="$2"
            shift 2
            ;;
        -v|--version)
            VERSION="$2"
            shift 2
            ;;
        -e|--env)
            if [ -n "$ENV_VARS" ]; then
                ENV_VARS="$ENV_VARS $2"
            else
                ENV_VARS="$2"
            fi
            shift 2
            ;;
        --jmx)
            ENABLE_JMX=true
            shift
            ;;
        --jmx-port)
            JMX_PORT="$2"
            shift 2
            ;;
        --jmx-host)
            JMX_HOST="$2"
            shift 2
            ;;
        --auto-port)
            AUTO_PORT=true
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            log_error "未知参数: $1"
            show_usage
            exit 1
            ;;
    esac
done

# 参数验证
if [ -z "$JAR_FILE" ]; then
    log_error "请指定jar文件路径"
    show_usage
    exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
    log_error "jar文件不存在: $JAR_FILE"
    exit 1
fi

# 检查并处理端口冲突
if [ "$AUTO_PORT" = true ]; then
    # 自动寻找可用端口
    NEW_PORT=$(find_next_available_port $PORT)
    if [ $? -ne 0 ]; then
        log_error "无法找到可用端口"
        exit 1
    fi
    if [ "$NEW_PORT" != "$PORT" ]; then
        log_info "自动切换到可用端口: $NEW_PORT"
    fi
    PORT=$NEW_PORT
else
    # 检查端口是否可用，如果不可用则报错
    if ! check_port_available $PORT; then
        log_error "端口 $PORT 被占用，请选择其他端口或使用 --auto-port 参数自动寻找可用端口"
        exit 1
    fi
fi

# 设置应用名称（如果没有指定）
if [ -z "$APP_NAME" ]; then
    APP_NAME=$(basename "$JAR_FILE" .jar)
fi

# 设置镜像名称
IMAGE_NAME="$APP_NAME:$VERSION"

# 检查Docker是否可用
if ! command -v docker &> /dev/null; then
    log_error "Docker未安装或不可用"
    exit 1
fi

# 创建临时工作目录
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
WORK_DIR="/tmp/${APP_NAME}_${TIMESTAMP}_$$"
log_info "创建工作目录: $WORK_DIR"
mkdir -p "$WORK_DIR"

# 复制jar文件
log_info "复制jar文件到工作目录"
cp "$JAR_FILE" "$WORK_DIR/app.jar"

# 切换到工作目录
cd "$WORK_DIR" || { log_error "无法切换到工作目录"; exit 1; }

# 创建Dockerfile
log_info "创建Dockerfile"
cat > Dockerfile << EOF
FROM openjdk:21-jdk-slim
LABEL maintainer="Generic Dockerize Script"
LABEL app_name="$APP_NAME"
LABEL version="$VERSION"

WORKDIR /app
COPY app.jar app.jar

EXPOSE $PORT

ENV SERVER_PORT=$PORT

ENTRYPOINT ["sh", "-c", "java \$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dserver.port=\$SERVER_PORT -jar app.jar"]
EOF

# 删除旧容器和镜像
delete_container_and_image "$APP_NAME" "$IMAGE_NAME"

# 构建镜像
build_docker_image "$IMAGE_NAME"
if [ $? -ne 0 ]; then
    log_error "镜像构建失败"
    cd /
    rm -rf "$WORK_DIR"
    exit 1
fi

# 启动容器
if [ "$ENABLE_JMX" = true ]; then
    start_container_with_jmx "$IMAGE_NAME" "$APP_NAME" "$PORT" "$PORT" "$MEMORY" "$ENV_VARS" "$JMX_PORT" "$JMX_HOST"
else
    start_container "$IMAGE_NAME" "$APP_NAME" "$PORT" "$PORT" "$MEMORY" "$ENV_VARS" "$ADDITIONAL_DOCKER_OPTIONS"
fi

if [ $? -ne 0 ]; then
    log_error "容器启动失败"
    cd /
    rm -rf "$WORK_DIR"
    exit 1
fi

# 等待容器启动
wait_for_container "$APP_NAME" 30

# 检查容器状态
check_container_status "$APP_NAME"

# 显示成功信息
log_info "应用部署成功！"
echo "--------------------------------------------------"
echo "  应用名称: $APP_NAME"
echo "  访问地址: http://localhost:$PORT"
echo "  镜像版本: $VERSION"
echo "  内存限制: $MEMORY"
if [ "$ENABLE_JMX" = true ]; then
    echo "  JMX端口: $JMX_PORT"
    echo "  JMX主机: $JMX_HOST"
fi
echo "  查看日志: docker logs -f $APP_NAME"
echo "--------------------------------------------------"

# 清理工作目录
log_info "清理临时文件"
cd /
rm -rf "$WORK_DIR"

log_info "部署完成！"
