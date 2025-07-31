#!/bin/bash

# 通用项目运行脚本

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 默认配置
# shellcheck disable=SC2034
DEFAULT_JAR_FILE=""
# shellcheck disable=SC2034
DEFAULT_APP_NAME=""
DEFAULT_HOST_PORT=8080
DEFAULT_CONTAINER_PORT=8080
DEFAULT_MEMORY="512m"
DEFAULT_VERSION="latest"
DEFAULT_AUTO_PORT="false"

# 显示使用方法
show_usage() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -j, --jar <路径>        指定jar文件路径"
    echo "  -n, --name <名称>       指定应用名称"
    echo "  -p, --port <端口>       指定主机端口 (默认: $DEFAULT_HOST_PORT)"
    echo "  --container-port <端口> 容器端口 (默认: $DEFAULT_CONTAINER_PORT)"
    echo "  -m, --memory <内存>     内存限制 (默认: $DEFAULT_MEMORY)"
    echo "  -v, --version <版本>    镜像版本 (默认: $DEFAULT_VERSION)"
    echo "  -e, --env <变量>        环境变量 (可多次使用)"
    echo "  --auto-port             自动寻找可用端口"
    echo "  --jmx                   启用JMX监控"
    echo "  --jmx-port <端口>       JMX端口"
    echo "  --jmx-host <主机>       JMX主机名"
    echo "  -h, --help              显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 -j ./app.jar"
    echo "  $0 -j ./app.jar -p 9090"
    echo "  $0 -j ./app.jar --auto-port"
    echo "  $0 -j ./app.jar --jmx --jmx-port 9999"
}

# 查找项目jar文件
find_project_jar() {
    # 查找当前目录下的jar文件
    # shellcheck disable=SC2207
    # shellcheck disable=SC2035
    local jar_files=($(ls *.jar 2>/dev/null))

    if [ ${#jar_files[@]} -eq 1 ]; then
        echo "${jar_files[0]}"
        return 0
    elif [ ${#jar_files[@]} -gt 1 ]; then
        echo "警告: 当前目录找到多个jar文件，请使用 -j 参数指定:" >&2
        for jar in "${jar_files[@]}"; do
            echo "  - $jar" >&2
        done
        return 1
    else
        echo "错误: 当前目录未找到jar文件，请使用 -j 参数指定" >&2
        return 1
    fi
}

# 参数解析
JAR_FILE=""
APP_NAME=""
HOST_PORT="$DEFAULT_HOST_PORT"
CONTAINER_PORT="$DEFAULT_CONTAINER_PORT"
MEMORY="$DEFAULT_MEMORY"
VERSION="$DEFAULT_VERSION"
ENV_VARS=""
AUTO_PORT="$DEFAULT_AUTO_PORT"
JMX_OPTIONS=""

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
            HOST_PORT="$2"
            shift 2
            ;;
        --container-port)
            CONTAINER_PORT="$2"
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
        --auto-port)
            AUTO_PORT="true"
            shift
            ;;
        --jmx)
            JMX_OPTIONS="$JMX_OPTIONS --jmx"
            shift
            ;;
        --jmx-port)
            JMX_OPTIONS="$JMX_OPTIONS --jmx-port $2"
            shift 2
            ;;
        --jmx-host)
            JMX_OPTIONS="$JMX_OPTIONS --jmx-host $2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            echo "未知参数: $1" >&2
            show_usage
            exit 1
            ;;
    esac
done

# 如果没有指定jar文件，则自动查找
if [ -z "$JAR_FILE" ]; then
    JAR_FILE=$(find_project_jar)
    if [ $? -ne 0 ]; then
        exit 1
    fi
fi

# 构建dockerize.sh的参数
DOCKERIZE_ARGS="-j $SCRIPT_DIR/$JAR_FILE"

# 添加应用名称（如果指定）
if [ -n "$APP_NAME" ]; then
    DOCKERIZE_ARGS="$DOCKERIZE_ARGS -n $APP_NAME"
fi

# 添加端口配置
DOCKERIZE_ARGS="$DOCKERIZE_ARGS -p $HOST_PORT"
DOCKERIZE_ARGS="$DOCKERIZE_ARGS --container-port $CONTAINER_PORT"

# 添加内存配置
DOCKERIZE_ARGS="$DOCKERIZE_ARGS -m $MEMORY"

# 添加版本配置
DOCKERIZE_ARGS="$DOCKERIZE_ARGS -v $VERSION"

# 添加环境变量
if [ -n "$ENV_VARS" ]; then
    # 将环境变量拆分成多个-e参数
    IFS=' ' read -ra ENV_ARRAY <<< "$ENV_VARS"
    for env_var in "${ENV_ARRAY[@]}"; do
        if [ -n "$env_var" ]; then
            DOCKERIZE_ARGS="$DOCKERIZE_ARGS -e $env_var"
        fi
    done
fi

# 添加自动端口选项
if [ "$AUTO_PORT" = "true" ]; then
    DOCKERIZE_ARGS="$DOCKERIZE_ARGS --auto-port"
fi

# 添加JMX选项
if [ -n "$JMX_OPTIONS" ]; then
    DOCKERIZE_ARGS="$DOCKERIZE_ARGS $JMX_OPTIONS"
fi

# 调用通用Docker化脚本
../../dockerize.sh $DOCKERIZE_ARGS
