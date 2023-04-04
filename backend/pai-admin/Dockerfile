FROM harbor.aigauss.com/base-images/openjdk:8 as app
ARG APP_ID=x-pai
ARG APOLLO_META=http://apollo-config-test.aigauss.com:8080
#ENV APP_ID=$APP_ID \
#    APOLLO_META=$APOLLO_META
ENV APOLLO_META=$APOLLO_META
ENV TRACING_ARGS=" -Xms512m -Xmx512m"

COPY target/*.jar /opt/app.jar

EXPOSE 8080