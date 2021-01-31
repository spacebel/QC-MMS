FROM solr:7.2.1

RUN mkdir -p /opt/solr/server/solr/cat
RUN mkdir -p /opt/solr/server/solr/dataset

COPY cat /opt/solr/server/solr/cat
COPY dataset /opt/solr/server/solr/dataset
COPY jts-1.13.jar /opt/solr/server/lib

USER root

RUN chown -R solr:solr /opt/solr/server/solr/cat
RUN chown -R solr:solr /opt/solr/server/solr/dataset

USER solr