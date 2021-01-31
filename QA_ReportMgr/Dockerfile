FROM tomee:8-jre-7.1.0-plus

# Maintainer
MAINTAINER Minh Nguyen Quang Spacebel

RUN rm -rf /usr/local/tomee/webapps/docs
RUN rm -rf /usr/local/tomee/webapps/host-manager
RUN rm -rf /usr/local/tomee/webapps/manager

ENV IS_DOCKER_DEPLOYMENT=true

## Copy all configuration files to the docker image
COPY opensearch-web-client/config /config

# Copy the web app to the autodeploy directory
COPY opensearch-web-client/target/OpenSearchWebClient.war /usr/local/tomee/webapps/client.war
