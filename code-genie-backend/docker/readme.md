
 ## 1.目录结构：
```   
docker-deploy/
   ├── common.sh            # 通用函数库
   ├── dockerize.sh         # 通用Docker化脚本
   ├── restart_all.sh       # 批量重启脚本
   └── projects/
       ├── code-genie/
       │   ├── code-genie-backend-0.0.1-SNAPSHOT.jar
       │   └── run.sh      # 通用项目运行脚本
       └── other-app/
           ├── other-app.jar
           └── run.sh
   ```
## 2.部署项目：
```bash
#进入项目目录并运行
cd projects/code-genie ./run.sh
#使用命令行参数指定配置
./run.sh -j ./app.jar -p 8080 --auto-port

```
## 3.批量重启:
```bash
   # 重启默认容器
   ./restart_all.sh
   
   # 重启指定容器
   ./restart_all.sh container1 container2 container3
```
## 4.直接使用dockerize.sh：
```bash
#简单部署
./dockerize.sh -j ./app.jar
#指定端口部署
./dockerize.sh -j ./app.jar -p 9090
#自动寻找可用端口
./dockerize.sh -j ./app.jar --auto-port
#带JMX监控部署
./dockerize.sh -j ./app.jar --jmx --jmx-port 9999
```
## 5. run.sh
```bash
# 使用默认端口8080
./run.sh -j ./app.jar

# 指定特定端口
./run.sh -j ./app.jar -p 9090

# 自动寻找可用端口
./run.sh -j ./app.jar --auto-port

# 启用JMX监控
./run.sh -j ./app.jar --jmx --jmx-port 9999

# 设置环境变量
./run.sh -j ./app.jar -e "SPRING_PROFILES_ACTIVE=prod" -e "SERVER_PORT=8080"
```

## 6. dockerize.sh
```bash
# 正常使用（如果端口被占用会报错）
./dockerize.sh -j ./app.jar

# 自动寻找可用端口
./dockerize.sh -j ./app.jar --auto-port

# 指定特定端口（如果被占用会报错）
./dockerize.sh -j ./app.jar -p 9090

```

## 7. run.sh命令行参数：
```bash
-j, --jar <路径> 指定jar文件路径 
-n, --name <名称> 指定应用名称 
-p, --port <端口> 指定主机端口 (默认: 8080) --container-port <端口> 容器端口 (默认: 8080) 
-m, --memory <内存> 内存限制 (默认: 512m) 
-v, --version <版本> 镜像版本 (默认: latest) 
-e, --env <变量> 环境变量 (可多次使用) 
--auto-port 自动寻找可用端口 
--jmx 启用JMX监控 
--jmx-port <端口> JMX端口 
--jmx-host <主机> JMX主机名 
-h, --help 显示帮助信息
```
