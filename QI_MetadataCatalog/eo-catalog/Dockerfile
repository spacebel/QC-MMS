FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV JAVA_OPTS=""

ENV RESOURCES_DIR=/usr/share/eo-catalog/resources
ENV PROPS_DIR=/usr/share/eo-catalog/resources/properties

ENV SERVER_URL=https://qcmms-cat.spacebel.be/eo-catalog
ENV SOLR_URL=http://qcmms-cat.spacebel.be:8983/solr

COPY resources /usr/share/eo-catalog/resources

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /usr/share/eo-catalog/eo-catalog.jar --server.port=9000

ARG JAR_FILE
ADD target/eo-catalog-1.0-SNAPSHOT.jar /usr/share/eo-catalog/eo-catalog.jar
