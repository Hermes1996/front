FROM java:8
ARG JAR_FILE
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone
ADD target/${JAR_FILE} app.jar
EXPOSE 20400 20401
ENTRYPOINT ["java", "-jar", "/app.jar"]