# 使用多阶段构建减小镜像体积
# 使用ubuntu 构建temurin 21版本jdk
FROM eclipse-temurin:21-jdk-jammy AS builder
#构建工作目录
WORKDIR /app

# 使用Maven Wrapper 构建项目配置，解除对特定maven版本的依赖）
#复制项目中的mvn配置，包括阿里云镜像地址
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/
COPY .mvn/ .mvn
COPY mvnw ./
#给mvnw执行权限
RUN chmod +x mvnw

# copy阿里云镜像配置,包括settings文件
COPY .m2/settings.xml /app/.m2/settings.xml

# 复制pom
COPY pom.xml .
#开始下载依赖构建
RUN ./mvnw -s /app/.m2/settings.xml dependency:go-offline

#  拷贝源码
COPY src ./src
# 编译打包
RUN ./mvnw -s /app/.m2/settings.xml package -DskipTests

# 使用jre运行
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# 从构建结果中copy 可运行jar
COPY --from=builder /app/target/*.jar app.jar

# 创建个人用户
RUN useradd -m herozhao
USER herozhao
# 暴露端口
EXPOSE 8080
# 运行启动
ENTRYPOINT ["java", "-jar", "app.jar"]