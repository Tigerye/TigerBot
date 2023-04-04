FROM harbor.aigauss.com/base-images/openjdk:8 as app

ARG APP_ID=x-pai
ARG APOLLO_META=http://apollo-config-test.aigauss.com:8080
ENV APOLLO_META=$APOLLO_META
ENV TRACING_ARGS=" -Xms2g -Xmx2g"

COPY target/*.jar /opt/app.jar
#CMD ["-Xms128m","-Xmx512m"]
EXPOSE 8080