FROM maven:3.6-jdk-8-slim
WORKDIR /app
COPY src /app/src
COPY pom.xml /app
https://alainelkhoury.hashnode.dev/spring-security-oauth-20-opaque-token-authentication
https://www.linkedin.com/pulse/running-test-automation-project-docker-container-nitin-goyal/

docker build -t samplemaven:latest .
docker run -it --name samplecontainer samplemaven:latest /bin/bash