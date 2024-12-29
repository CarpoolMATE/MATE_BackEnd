# 베이스 이미지로 OpenJDK 사용
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일을 컨테이너에 복사
COPY ./build/libs/*.jar app.jar

# ENTRYPOINT 실행 명령어 설정
ENTRYPOINT ["java", "-jar", "app.jar"]